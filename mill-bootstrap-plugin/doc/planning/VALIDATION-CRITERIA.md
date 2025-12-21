# mill-bootstrap-plugin: Validation Criteria

**Version**: 1.0.0  
**Date**: December 16, 2025  
**Plugin**: mill-bootstrap-plugin

---

## Overview

The bootstrap plugin performs **7 prerequisite checks** before creating a new project repository. Unlike mill-deploy-plugin (which has environment-specific validation), all bootstrap checks are **Blocking** since project setup has no environment variance.

---

## Validation Checks

### Check 1: GitHub Token Valid ✋ BLOCKING

**Purpose**: Verify GitHub Personal Access Token has required scopes

**Category**: Authentication  
**Severity**: Blocking  
**Ceremony Source**: N/A (Infrastructure prerequisite)

**Implementation**:
```scala
def check1GitHubTokenValid(): ValidationResult = {
  val token = sys.env.get("GITHUB_TOKEN").getOrElse("")
  
  if (token.isEmpty) {
    return ValidationResult(
      checkNumber = 1,
      name = "GitHub token valid",
      passed = false,
      message = "GITHUB_TOKEN environment variable not set",
      details = Some("Export GITHUB_TOKEN with 'repo' and 'admin:org' scopes")
    )
  }
  
  // Verify token with GitHub API
  val response = requests.get(
    "https://api.github.com/user",
    headers = Map("Authorization" -> s"Bearer $token")
  )
  
  if (response.statusCode != 200) {
    return ValidationResult(
      checkNumber = 1,
      name = "GitHub token valid",
      passed = false,
      message = s"GitHub API rejected token (HTTP ${response.statusCode})",
      details = Some("Verify token is valid: https://github.com/settings/tokens")
    )
  }
  
  // Check scopes
  val scopes = response.headers.get("x-oauth-scopes")
    .map(_.split(",").map(_.trim).toSet)
    .getOrElse(Set.empty)
  
  val requiredScopes = Set("repo", "admin:org")
  val missingScopes = requiredScopes -- scopes
  
  if (missingScopes.nonEmpty) {
    ValidationResult(
      checkNumber = 1,
      name = "GitHub token valid",
      passed = false,
      message = s"Missing required scopes: ${missingScopes.mkString(", ")}",
      details = Some("Token needs: repo (full), admin:org (read/write)")
    )
  } else {
    ValidationResult(
      checkNumber = 1,
      name = "GitHub token valid",
      passed = true,
      message = s"Token valid with required scopes (user: ${ujson.read(response.text())("login").str})"
    )
  }
}
```

**Pass Criteria**:
- ✅ GITHUB_TOKEN environment variable set
- ✅ Token authenticated successfully with GitHub API
- ✅ Token has `repo` scope (full repository access)
- ✅ Token has `admin:org` scope (manage organization repositories)

**Failure Examples**:
- ❌ "GITHUB_TOKEN environment variable not set"
- ❌ "GitHub API rejected token (HTTP 401)"
- ❌ "Missing required scopes: admin:org"

---

### Check 2: Target Organization Exists ✋ BLOCKING

**Purpose**: Verify GitHub organization exists before attempting repository creation

**Category**: GitHub  
**Severity**: Blocking  
**Ceremony Source**: N/A (Infrastructure prerequisite)

**Implementation**:
```scala
def check2OrganizationExists(org: String, token: String): ValidationResult = {
  val response = requests.get(
    s"https://api.github.com/orgs/$org",
    headers = Map("Authorization" -> s"Bearer $token"),
    check = false
  )
  
  if (response.statusCode == 404) {
    ValidationResult(
      checkNumber = 2,
      name = "Target organization exists",
      passed = false,
      message = s"Organization '$org' not found on GitHub",
      details = Some("Verify organization name or check token has access")
    )
  } else if (response.statusCode == 200) {
    val orgData = ujson.read(response.text())
    ValidationResult(
      checkNumber = 2,
      name = "Target organization exists",
      passed = true,
      message = s"Organization '$org' found (${orgData("public_repos").num.toInt} public repos)"
    )
  } else {
    ValidationResult(
      checkNumber = 2,
      name = "Target organization exists",
      passed = false,
      message = s"Failed to verify organization (HTTP ${response.statusCode})",
      details = Some(response.text().take(200))
    )
  }
}
```

**Pass Criteria**:
- ✅ Organization exists on GitHub
- ✅ Token has access to organization

**Failure Examples**:
- ❌ "Organization 'NonExistent' not found on GitHub"
- ❌ "Failed to verify organization (HTTP 403)" - token lacks access

---

### Check 3: Repository Name Available ✋ BLOCKING

**Purpose**: Verify repository name is available in target organization

**Category**: GitHub  
**Severity**: Blocking  
**Ceremony Source**: N/A (Infrastructure prerequisite)

**Implementation**:
```scala
def check3RepositoryAvailable(org: String, repoName: String, token: String): ValidationResult = {
  val response = requests.get(
    s"https://api.github.com/repos/$org/$repoName",
    headers = Map("Authorization" -> s"Bearer $token"),
    check = false
  )
  
  if (response.statusCode == 404) {
    ValidationResult(
      checkNumber = 3,
      name = "Repository name available",
      passed = true,
      message = s"Repository name '$repoName' is available in '$org'"
    )
  } else if (response.statusCode == 200) {
    val repoData = ujson.read(response.text())
    val repoUrl = repoData("html_url").str
    ValidationResult(
      checkNumber = 3,
      name = "Repository name available",
      passed = false,
      message = s"Repository '$org/$repoName' already exists",
      details = Some(s"URL: $repoUrl")
    )
  } else {
    ValidationResult(
      checkNumber = 3,
      name = "Repository name available",
      passed = false,
      message = s"Failed to check repository (HTTP ${response.statusCode})",
      details = Some(response.text().take(200))
    )
  }
}
```

**Pass Criteria**:
- ✅ Repository does not exist in organization (HTTP 404)

**Failure Examples**:
- ❌ "Repository 'Acme/existing-project' already exists"
- ❌ "Failed to check repository (HTTP 500)"

---

### Check 4: Project Name Follows Conventions ✋ BLOCKING

**Purpose**: Validate project name matches kebab-case convention

**Category**: Naming  
**Severity**: Blocking  
**Ceremony Source**: Naming conventions from HOW-WE-WORK.md

**Implementation**:
```scala
def check4ProjectNameConventions(projectName: String): ValidationResult = {
  // Kebab-case pattern: lowercase letters, numbers, hyphens
  // Must start with letter, end with letter/number
  val kebabCasePattern = """^[a-z][a-z0-9]*(-[a-z0-9]+)*$""".r
  
  if (!kebabCasePattern.matches(projectName)) {
    val issues = List(
      if (!projectName.head.isLower) Some("Must start with lowercase letter") else None,
      if (projectName.contains("_")) Some("Use hyphens (-), not underscores (_)") else None,
      if (projectName.exists(_.isUpper)) Some("Must be lowercase only") else None,
      if (projectName.contains("--")) Some("No consecutive hyphens") else None,
      if (projectName.endsWith("-")) Some("Cannot end with hyphen") else None
    ).flatten
    
    ValidationResult(
      checkNumber = 4,
      name = "Project name follows conventions",
      passed = false,
      message = "Project name must be kebab-case",
      details = Some(issues.mkString("; "))
    )
  } else if (projectName.length < 3) {
    ValidationResult(
      checkNumber = 4,
      name = "Project name follows conventions",
      passed = false,
      message = "Project name too short (minimum 3 characters)"
    )
  } else if (projectName.length > 50) {
    ValidationResult(
      checkNumber = 4,
      name = "Project name follows conventions",
      passed = false,
      message = "Project name too long (maximum 50 characters)"
    )
  } else {
    ValidationResult(
      checkNumber = 4,
      name = "Project name follows conventions",
      passed = true,
      message = s"Project name '$projectName' follows kebab-case convention"
    )
  }
}
```

**Pass Criteria**:
- ✅ Kebab-case format: `^[a-z][a-z0-9]*(-[a-z0-9]+)*$`
- ✅ Starts with lowercase letter
- ✅ Contains only lowercase letters, numbers, hyphens
- ✅ No consecutive hyphens
- ✅ Does not end with hyphen
- ✅ Length: 3-50 characters

**Valid Examples**:
- ✅ `ecommerce-platform`
- ✅ `client-portal`
- ✅ `api-gateway-v2`

**Invalid Examples**:
- ❌ `EcommercePlatform` (PascalCase)
- ❌ `ecommerce_platform` (snake_case)
- ❌ `ecommerce--platform` (consecutive hyphens)
- ❌ `-ecommerce-platform` (starts with hyphen)
- ❌ `ecommerce-platform-` (ends with hyphen)
- ❌ `e` (too short)

---

### Check 5: Git Configured Locally ✋ BLOCKING

**Purpose**: Verify git is installed and configured with user identity

**Category**: Git  
**Severity**: Blocking  
**Ceremony Source**: N/A (Infrastructure prerequisite)

**Implementation**:
```scala
def check5GitConfigured(): ValidationResult = {
  // Check git installed
  val gitVersion = os.proc("git", "--version").call(check = false)
  if (gitVersion.exitCode != 0) {
    return ValidationResult(
      checkNumber = 5,
      name = "Git configured locally",
      passed = false,
      message = "Git not installed or not in PATH",
      details = Some("Install git: https://git-scm.com/downloads")
    )
  }
  
  // Check user.name configured
  val userName = os.proc("git", "config", "user.name").call(check = false)
  if (userName.exitCode != 0 || userName.out.text().trim.isEmpty) {
    return ValidationResult(
      checkNumber = 5,
      name = "Git configured locally",
      passed = false,
      message = "Git user.name not configured",
      details = Some("Run: git config --global user.name \"Your Name\"")
    )
  }
  
  // Check user.email configured
  val userEmail = os.proc("git", "config", "user.email").call(check = false)
  if (userEmail.exitCode != 0 || userEmail.out.text().trim.isEmpty) {
    return ValidationResult(
      checkNumber = 5,
      name = "Git configured locally",
      passed = false,
      message = "Git user.email not configured",
      details = Some("Run: git config --global user.email \"you@example.com\"")
    )
  }
  
  ValidationResult(
    checkNumber = 5,
    name = "Git configured locally",
    passed = true,
    message = s"Git configured (user: ${userName.out.text().trim} <${userEmail.out.text().trim}>)"
  )
}
```

**Pass Criteria**:
- ✅ Git installed and in PATH
- ✅ `user.name` configured (git config user.name)
- ✅ `user.email` configured (git config user.email)

**Failure Examples**:
- ❌ "Git not installed or not in PATH"
- ❌ "Git user.name not configured"
- ❌ "Git user.email not configured"

---

### Check 6: Mill 0.11.6+ Installed ✋ BLOCKING

**Purpose**: Verify Mill build tool is installed with minimum version

**Category**: Build Tool  
**Severity**: Blocking  
**Ceremony Source**: ADR-056 (Mill for JVM Builds)

**Implementation**:
```scala
def check6MillInstalled(): ValidationResult = {
  val millVersion = os.proc("mill", "--version").call(check = false)
  
  if (millVersion.exitCode != 0) {
    return ValidationResult(
      checkNumber = 6,
      name = "Mill 0.11.6+ installed",
      passed = false,
      message = "Mill not installed or not in PATH",
      details = Some("Install mill: https://mill-build.com/mill/Intro_to_Mill.html#_installation")
    )
  }
  
  val version = millVersion.out.text().trim.split(" ").last
  val versionParts = version.split("\\.").map(_.toInt)
  
  val requiredMajor = 0
  val requiredMinor = 11
  val requiredPatch = 6
  
  val meetsRequirement = (
    versionParts(0) > requiredMajor ||
    (versionParts(0) == requiredMajor && versionParts(1) > requiredMinor) ||
    (versionParts(0) == requiredMajor && versionParts(1) == requiredMinor && versionParts(2) >= requiredPatch)
  )
  
  if (meetsRequirement) {
    ValidationResult(
      checkNumber = 6,
      name = "Mill 0.11.6+ installed",
      passed = true,
      message = s"Mill $version installed (meets requirement >=0.11.6)"
    )
  } else {
    ValidationResult(
      checkNumber = 6,
      name = "Mill 0.11.6+ installed",
      passed = false,
      message = s"Mill $version installed (requires >=0.11.6)",
      details = Some("Upgrade mill: https://mill-build.com/mill/Intro_to_Mill.html#_installation")
    )
  }
}
```

**Pass Criteria**:
- ✅ Mill installed and in PATH
- ✅ Version >= 0.11.6

**Failure Examples**:
- ❌ "Mill not installed or not in PATH"
- ❌ "Mill 0.10.5 installed (requires >=0.11.6)"

---

### Check 7: Source Repo Path Valid ✋ BLOCKING

**Purpose**: Verify copilot-training repository exists at specified path

**Category**: Source  
**Severity**: Blocking  
**Ceremony Source**: N/A (Infrastructure prerequisite)

**Implementation**:
```scala
def check7SourceRepoValid(sourcePath: os.Path): ValidationResult = {
  if (!os.exists(sourcePath)) {
    return ValidationResult(
      checkNumber = 7,
      name = "Source repo path valid",
      passed = false,
      message = s"Source path does not exist: $sourcePath",
      details = Some("Specify correct path to copilot-training repository")
    )
  }
  
  // Check for key framework files
  val requiredFiles = List(
    sourcePath / "HOW-WE-WORK.md",
    sourcePath / ".github" / "copilot-instructions.md",
    sourcePath / "doc" / "reference" / "SBPF",
    sourcePath / "doc" / "reference" / "templates",
    sourcePath / "mill-spinoff-plugin",
    sourcePath / "mill-deploy-plugin"
  )
  
  val missingFiles = requiredFiles.filterNot(os.exists)
  
  if (missingFiles.nonEmpty) {
    ValidationResult(
      checkNumber = 7,
      name = "Source repo path valid",
      passed = false,
      message = "Source path missing required framework files",
      details = Some(s"Missing: ${missingFiles.map(_.relativeTo(sourcePath)).mkString(\", \")}")
    )
  } else {
    // Count files to copy
    val sbpfCount = os.list(sourcePath / "doc" / "reference" / "SBPF").size
    val templatesCount = os.list(sourcePath / "doc" / "reference" / "templates").size
    val instructionsCount = os.list(sourcePath / ".github")
      .filter(_.last.startsWith("copilot-instructions"))
      .size
    
    ValidationResult(
      checkNumber = 7,
      name = "Source repo path valid",
      passed = true,
      message = s"Source repo valid ($sbpfCount SBPFs, $templatesCount templates, $instructionsCount copilot files)"
    )
  }
}
```

**Pass Criteria**:
- ✅ Source path exists
- ✅ Contains HOW-WE-WORK.md
- ✅ Contains .github/copilot-instructions*.md
- ✅ Contains doc/reference/SBPF/
- ✅ Contains doc/reference/templates/
- ✅ Contains mill-spinoff-plugin/
- ✅ Contains mill-deploy-plugin/

**Failure Examples**:
- ❌ "Source path does not exist: /path/to/copilot-training"
- ❌ "Source path missing required framework files (Missing: HOW-WE-WORK.md, doc/reference/SBPF)"

---

## Validation Summary

| # | Check | Severity | Pass Rate Target |
|---|-------|----------|------------------|
| 1 | GitHub token valid | Blocking | 100% |
| 2 | Target organization exists | Blocking | 100% |
| 3 | Repository name available | Blocking | 100% |
| 4 | Project name follows conventions | Blocking | 100% |
| 5 | Git configured locally | Blocking | 100% |
| 6 | Mill 0.11.6+ installed | Blocking | 100% |
| 7 | Source repo path valid | Blocking | 100% |

**Total Checks**: 7  
**All Blocking**: Yes (no environment variance)  
**Target Pass Rate**: 100% before bootstrap execution

---

## Error Handling

### Validation Failure
If **any** check fails:
1. Display all validation results (pass/fail)
2. Highlight failing checks with error details
3. Exit with code 1
4. Do **not** proceed to bootstrap execution

### GitHub API Errors
- **401 Unauthorized**: Token invalid or expired
- **403 Forbidden**: Token lacks required scopes
- **404 Not Found**: Organization or repository doesn't exist
- **422 Unprocessable Entity**: Invalid input (e.g., bad repo name)
- **Rate Limit**: Display retry-after header, suggest waiting

---

## Testing Strategy

### Unit Tests
```scala
class BootstrapValidatorTest extends AnyFunSuite {
  test("check1: Reject empty GitHub token") { ... }
  test("check1: Reject token without required scopes") { ... }
  test("check2: Pass when organization exists") { ... }
  test("check3: Pass when repository name available") { ... }
  test("check4: Reject non-kebab-case names") { ... }
  test("check4: Accept valid kebab-case names") { ... }
  test("check5: Reject when git not configured") { ... }
  test("check6: Reject Mill version < 0.11.6") { ... }
  test("check7: Reject when source path missing files") { ... }
}
```

### Integration Tests
```scala
class BootstrapIntegrationTest extends AnyFunSuite {
  test("validation: All checks pass with valid configuration") { ... }
  test("validation: Fail fast on first blocking issue") { ... }
  test("GitHub API: Handle rate limiting gracefully") { ... }
}
```

---

## References

- **mill-deploy-plugin**: Similar validation pattern (15 checks with severity levels)
- **mill-spinoff-plugin**: Similar GitHub API validation (13 checks)
- **ADR-056**: Mill for JVM Builds (Check 6)
- **HOW-WE-WORK.md**: Naming conventions (Check 4)
