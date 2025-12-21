# mill-bootstrap-plugin: Workflow

**Version**: 1.0.0  
**Date**: December 16, 2025  
**Plugin**: mill-bootstrap-plugin

---

## Command Overview

The bootstrap plugin provides 3 commands following the same pattern as mill-spinoff-plugin and mill-deploy-plugin:

| Command | Purpose | Prerequisites |
|---------|---------|---------------|
| `bootstrapList()` | Show recently bootstrapped projects | GitHub token |
| `bootstrapValidate(name)` | Run 7 prerequisite checks | None |
| `bootstrapExecute(name, desc)` | Create new project repository | Validation passed |

---

## Workflow 1: bootstrapValidate

### Purpose
Verify all prerequisites before attempting to create a new project repository.

### Command
```bash
mill bootstrap.validate ecommerce-platform
```

### Process Flow

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Parse project name                                       │
│    - Convert to kebab-case                                  │
│    - Derive repository name (same as project name)          │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. Run 7 validation checks                                  │
│    ├── Check 1: GitHub token valid                          │
│    ├── Check 2: Target organization exists                  │
│    ├── Check 3: Repository name available                   │
│    ├── Check 4: Project name follows conventions            │
│    ├── Check 5: Git configured locally                      │
│    ├── Check 6: Mill 0.11.6+ installed                      │
│    └── Check 7: Source repo path valid                      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. Display results                                          │
│    - Pass: ✓ Green checkmark with details                   │
│    - Fail: ✗ Red X with error message and remediation       │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. Exit with status code                                    │
│    - All passed: Exit 0                                     │
│    - Any failed: Exit 1                                     │
└─────────────────────────────────────────────────────────────┘
```

### Output Example

```
Validating bootstrap for: ecommerce-platform
================================================================================

✓ Check 1/7: GitHub token valid (repo, admin:org scopes)
  User: developer
  
✓ Check 2/7: Organization "Acme" exists
  Public repos: 42
  
✓ Check 3/7: Repository "ecommerce-platform" available
  Name is available in Acme
  
✓ Check 4/7: Project name follows conventions (kebab-case)
  Name: ecommerce-platform ✓
  
✓ Check 5/7: Git configured (user: Developer <developer@acme.com>)

✓ Check 6/7: Mill 0.11.6 installed
  Version: 0.11.6 (meets requirement >=0.11.6)
  
✓ Check 7/7: Source repo path valid
  Found: 22 SBPFs, 34 templates, 14 copilot files

================================================================================
✅ ALL CHECKS PASSED - Ready to bootstrap
================================================================================

Next step: mill bootstrap.execute ecommerce-platform "Multi-tenant ecommerce platform"
```

### Failure Example

```
Validating bootstrap for: EcommercePlatform
================================================================================

✗ Check 1/7: GitHub token valid
  ❌ GITHUB_TOKEN environment variable not set
  Remediation: Export GITHUB_TOKEN with 'repo' and 'admin:org' scopes
  
✓ Check 2/7: Organization "Acme" exists
  
✗ Check 3/7: Repository name available
  (Skipped - token validation failed)
  
✗ Check 4/7: Project name follows conventions
  ❌ Project name must be kebab-case
  Issues: Must be lowercase only; Use hyphens (-), not underscores (_)
  
✓ Check 5/7: Git configured
✓ Check 6/7: Mill installed
✓ Check 7/7: Source repo valid

================================================================================
❌ VALIDATION FAILED - 3 blocking issue(s)
================================================================================

Fix the issues above before running bootstrapExecute.
```

---

## Workflow 2: bootstrapExecute

### Purpose
Create a new project repository with complete ceremony framework.

### Command
```bash
export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx
mill bootstrap.execute ecommerce-platform "Multi-tenant ecommerce platform"

# Override organization (default from BootstrapModule.targetOrg)
mill bootstrap.execute my-project "Description" --org CustomOrg
```

### Prerequisites
- All 7 validation checks must pass
- GITHUB_TOKEN environment variable set

### Process Flow

```
┌─────────────────────────────────────────────────────────────┐
│ Step 1/9: Validate prerequisites                            │
│ - Run all 7 validation checks                               │
│ - Exit if any check fails                                   │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 2/9: Create GitHub repository                          │
│ - POST /orgs/:org/repos                                     │
│ - Set description, visibility (private by default)          │
│ - Return repository URL                                     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 3/9: Clone empty repository locally                    │
│ - Create temp directory: /tmp/bootstrap-<project>-<uuid>    │
│ - git clone <repo-url> <temp-dir>                           │
│ - cd <temp-dir>                                             │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 4/9: Copy framework files                              │
│ - Copy HOW-WE-WORK.md                                       │
│ - Copy .github/copilot-instructions*.md (14 files)          │
│ - Copy doc/reference/SBPF/* (recursive)                     │
│ - Copy doc/reference/templates/* (recursive)                │
│ - Copy doc/reference/validation/* (recursive)               │
│ - Copy mill-spinoff-plugin/* (entire plugin)                │
│ - Copy mill-deploy-plugin/* (entire plugin)                 │
│ - Create doc/domain-models/ (empty)                         │
│ - Create doc/governance/ADR/ (with framework ADRs)          │
│ - Create doc/governance/PDR/ (empty)                        │
│ - Create doc/governance/POL/ (with framework POLs)          │
│ - Create doc/planning/ (empty)                              │
│ - Create services/ (empty with .gitkeep)                    │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 5/9: Generate project-specific stubs                   │
│ - Generate CHARTER.md (from CharterTemplate)                │
│ - Generate ARCHITECTURE.md (from ArchitectureTemplate)      │
│ - Generate README.md (from ReadmeTemplate)                  │
│ - Generate build.sc (from RootBuildScTemplate)              │
│ - Generate .gitignore (from GitignoreTemplate)              │
│ - Generate GOVERNANCE-BACKLOG.md (empty framework)          │
│ - Generate STATUS.md (Phase 0 status)                       │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 6/9: Post-copy file updates                            │
│ - Replace "copilot-training" → project name in copied files │
│ - Update plugin build.sc files (organization references)    │
│ - Update copilot instructions (project-specific context)    │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 7/9: Create initial commit                             │
│ - git add -A                                                │
│ - git commit -m "Initial project bootstrap from RETISIO/... │
│ - Include commit metadata (bootstrap plugin version)        │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 8/9: Push to GitHub                                    │
│ - git push origin main                                      │
│ - Verify push succeeded                                     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 9/9: Configure repository settings                     │
│ - Enable branch protection (main)                           │
│   * Require pull request reviews (1 approval)               │
│   * Dismiss stale reviews                                   │
│   * Require status checks                                   │
│ - Set repository topics                                     │
│   * ddd, bdd, tdd, ceremony-based, mill-build               │
│ - Disable wiki (docs in repo only)                          │
│ - Disable projects (use GitHub issues)                      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Cleanup & Success                                           │
│ - Remove temp directory                                     │
│ - Display success message with next steps                   │
│ - Exit with code 0                                          │
└─────────────────────────────────────────────────────────────┘
```

### Output Example

```
Bootstrapping new project: ecommerce-platform
================================================================================

Step 1/9: Validating prerequisites...
  ✓ All 7 checks passed

Step 2/9: Creating GitHub repository...
  ✓ Repository created: Acme/ecommerce-platform
  ✓ URL: https://github.com/Acme/ecommerce-platform

Step 3/9: Cloning repository locally...
  ✓ Cloned to: /tmp/bootstrap-ecommerce-platform-abc123

Step 4/9: Copying framework files...
  ✓ Copied HOW-WE-WORK.md
  ✓ Copied .github/copilot-instructions.md
  ✓ Copied .github/copilot-instructions-phase0-*.md (1 file)
  ✓ Copied .github/copilot-instructions-phase1-*.md (4 files)
  ✓ Copied .github/copilot-instructions-phase2-*.md (3 files)
  ✓ Copied .github/copilot-instructions-phase3-*.md (3 files)
  ✓ Copied .github/copilot-instructions-phase4-*.md (2 files)
  ✓ Copied doc/reference/SBPF/ (22 files)
  ✓ Copied doc/reference/templates/ (34 files)
  ✓ Copied doc/reference/validation/ (5 files)
  ✓ Copied mill-spinoff-plugin/ (15 files)
  ✓ Copied mill-deploy-plugin/ (10 files)
  ✓ Created doc/domain-models/ (empty, ready for event storming)
  ✓ Created doc/governance/ADR/ (5 framework ADRs)
  ✓ Created doc/governance/PDR/ (empty)
  ✓ Created doc/governance/POL/ (3 framework POLs)
  ✓ Created doc/planning/ (empty)
  ✓ Created services/ (empty with .gitkeep)

Step 5/9: Generating project stubs...
  ✓ Generated CHARTER.md (project charter template)
  ✓ Generated ARCHITECTURE.md (system architecture template)
  ✓ Generated README.md (project overview)
  ✓ Generated build.sc (root build configuration)
  ✓ Generated .gitignore (Mill/JVM patterns)
  ✓ Generated GOVERNANCE-BACKLOG.md (tracking document)
  ✓ Generated STATUS.md (Phase 0 status)

Step 6/9: Updating file references...
  ✓ Updated 47 references: copilot-training → ecommerce-platform

Step 7/9: Creating initial commit...
  ✓ Committed: "Initial project bootstrap from RETISIO/copilot-training v1.0.0"
  ✓ Commit SHA: a1b2c3d

Step 8/9: Pushing to GitHub...
  ✓ Pushed to main branch

Step 9/9: Configuring repository...
  ✓ Branch protection enabled (main)
  ✓ Topics set: ddd, bdd, tdd, ceremony-based, mill-build
  ✓ Wiki disabled
  ✓ Projects disabled

Cleaning up...
  ✓ Removed temp directory

================================================================================
✅ BOOTSTRAP SUCCESSFUL
================================================================================

Project:    Acme/ecommerce-platform
URL:        https://github.com/Acme/ecommerce-platform
Duration:   45 seconds

Next Steps:
1. Clone repository:
   git clone https://github.com/Acme/ecommerce-platform.git
   
2. Complete CHARTER.md:
   - Define project goals and scope
   - Identify stakeholders
   - List bounded contexts
   
3. Start Phase 1 (Discovery):
   - Event Storming: Identify domain events and commands
   - Ubiquitous Language: Define shared vocabulary
   - Domain Modeling: Model aggregates and entities
   - Context Mapping: Define relationships between contexts
   
4. GitHub Copilot ready:
   - All ceremony instructions included in .github/copilot-instructions*.md
   - Use @workspace to reference templates and SBPFs

For complete process: See HOW-WE-WORK.md in the new repository
```

### Failure Example

```
Bootstrapping new project: ecommerce-platform
================================================================================

Step 1/9: Validating prerequisites...
  ✗ Validation failed: 1 blocking issue(s)
  
  ✗ Check 1/7: GitHub token valid
    ❌ GITHUB_TOKEN environment variable not set
    
Bootstrap aborted. Fix issues above and try again.
```

---

## Workflow 3: bootstrapList

### Purpose
Show recently created projects from GitHub (filtered by bootstrap plugin topic).

### Command
```bash
mill bootstrap.list
```

### Process Flow

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Get GitHub token from environment                        │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. Query GitHub API                                         │
│ - GET /orgs/:org/repos?sort=created&direction=desc          │
│ - Filter by topic: "ceremony-based"                         │
│ - Limit: 20 most recent                                     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. Format and display results                               │
│ - Table: Name | Organization | Created | Topics             │
│ - Sort by creation date (newest first)                      │
└─────────────────────────────────────────────────────────────┘
```

### Output Example

```
Recently Bootstrapped Projects
================================================================================

Name                      Organization  Created              Topics
ecommerce-platform        Acme          2025-12-16 10:30 UTC ceremony-based, ddd, bdd
client-portal             RETISIO       2025-12-15 14:22 UTC ceremony-based, nextjs
internal-tools            RETISIO       2025-12-10 09:15 UTC ceremony-based, scala
supply-chain              Acme          2025-12-08 16:45 UTC ceremony-based, ddd
analytics-platform        RETISIO       2025-12-01 11:30 UTC ceremony-based, kafka

Found 5 bootstrapped projects in Acme organization

Note: Only showing repositories with 'ceremony-based' topic
```

---

## Error Handling

### GitHub API Errors

| Error | Status Code | Handling |
|-------|-------------|----------|
| Unauthorized | 401 | Display "GitHub token invalid or expired" + remediation |
| Forbidden | 403 | Display "Token lacks required scopes" + required scopes |
| Not Found | 404 | Display "Organization/repository not found" |
| Unprocessable Entity | 422 | Display specific validation errors from API |
| Rate Limit Exceeded | 429 | Display retry-after header + suggest waiting |
| Server Error | 500 | Display "GitHub API unavailable, try again later" |

### Git Operation Errors

| Error | Handling |
|-------|----------|
| Clone failed | Display error + verify network/credentials |
| Commit failed | Display error + check git configuration |
| Push failed | Display error + verify permissions |

### File Operation Errors

| Error | Handling |
|-------|----------|
| Source file missing | Display "Framework file missing" + verify source repo |
| Copy failed | Display "Failed to copy file" + check permissions |
| Generate failed | Display "Template generation failed" + check syntax |

---

## Configuration

### Default Configuration (in build.sc)

```scala
object bootstrap extends BootstrapModule {
  // GitHub organization (can override per execution)
  def targetOrg = T { "RETISIO" }
  
  // Path to copilot-training source repository
  def sourceRepoPath = T { os.pwd }  // Current directory
  
  // GitHub token from environment
  def githubToken = T {
    sys.env.get("GITHUB_TOKEN").getOrElse {
      sys.error("GITHUB_TOKEN environment variable not set")
    }
  }
}
```

### Override Configuration

```bash
# Use different organization
mill bootstrap.execute my-project "Description" --org CustomOrg

# Use different source repo
mill --define bootstrap.sourceRepoPath=/path/to/copilot-training \
  bootstrap.execute my-project "Description"
```

---

## Success Metrics

| Metric | Target | Measurement |
|--------|--------|-------------|
| Bootstrap time | <60 seconds | Time from execute to success message |
| Framework completeness | 100% | All required files copied |
| GitHub integration | 100% | Repository created and configured |
| Validation accuracy | 100% | All checks detect issues correctly |
| Self-sufficiency | 100% | Bootstrapped repo works independently |

---

## Testing

### Manual Testing Checklist

**Validation**:
- [ ] Missing GitHub token fails Check 1
- [ ] Invalid organization fails Check 2
- [ ] Existing repository fails Check 3
- [ ] Invalid project name fails Check 4
- [ ] Unconfigured git fails Check 5
- [ ] Old Mill version fails Check 6
- [ ] Missing source repo fails Check 7

**Execution**:
- [ ] Creates GitHub repository
- [ ] Copies all framework files (HOW-WE-WORK.md, SBPFs, templates, copilot instructions)
- [ ] Copies plugin sources (mill-spinoff-plugin, mill-deploy-plugin)
- [ ] Generates all stub files (CHARTER.md, ARCHITECTURE.md, README.md, build.sc, .gitignore)
- [ ] Pushes to GitHub successfully
- [ ] Configures branch protection
- [ ] Sets repository topics

**Post-Bootstrap Verification**:
- [ ] Clone bootstrapped repo
- [ ] Verify all files present
- [ ] Verify mill compiles (mill __.compile when services added)
- [ ] Verify copilot instructions work (@workspace references)
- [ ] Verify plugins compile (mill mill-spinoff-plugin.compile)

---

## References

- **mill-spinoff-plugin**: Similar workflow pattern (list/validate/execute)
- **mill-deploy-plugin**: Similar validation and reporting approach
- **DESIGN.md**: Complete plugin architecture
- **VALIDATION-CRITERIA.md**: Detailed validation check specifications
