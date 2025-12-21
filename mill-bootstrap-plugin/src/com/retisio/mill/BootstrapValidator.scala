package com.retisio.mill

import os.Path
import requests.Response

import scala.util.{Try, Success, Failure}
import scala.util.matching.Regex

/**
 * Validator for bootstrap prerequisites.
 * Runs 7 checks before allowing bootstrap to proceed.
 */
class BootstrapValidator(
  githubToken: String,
  targetOrg: String,
  sourceRepoPath: Path
) {

  private val githubClient = new GitHubClient(githubToken)

  /**
   * Run all 7 validation checks.
   *
   * @param projectName Name of project to bootstrap
   * @return Map of validation checks to results
   */
  def runAllChecks(projectName: String): Map[ValidationCheck, ValidationResult] = {
    Map(
      Check1_GitHubTokenValid -> validateGitHubToken(),
      Check2_OrganizationExists -> validateOrganization(),
      Check3_RepositoryNameAvailable -> validateRepositoryName(projectName),
      Check4_ProjectNameConventions -> validateProjectName(projectName),
      Check5_GitConfigured -> validateGitConfig(),
      Check6_MillVersionValid -> validateMillVersion(),
      Check7_SourceRepoValid -> validateSourceRepo()
    )
  }

  // Check 1: GitHub token valid
  private def validateGitHubToken(): ValidationResult = {
    Try {
      val response = requests.get(
        "https://api.github.com/user",
        headers = Map(
          "Authorization" -> s"Bearer $githubToken",
          "Accept" -> "application/vnd.github+json"
        )
      )
      
      if (response.statusCode == 200) {
        // Check scopes in X-OAuth-Scopes header
        val scopes = response.headers.get("x-oauth-scopes")
          .map(_.mkString(",").split(",").map(_.trim).toSet)
          .getOrElse(Set.empty)
        
        val hasRepo = scopes.exists(s => s == "repo" || s.startsWith("repo:"))
        val hasAdminOrg = scopes.exists(s => s.contains("admin:org") || s.contains("read:org"))
        
        if (hasRepo && hasAdminOrg) {
          ValidationResult(passed = true, error = None)
        } else {
          ValidationResult(
            passed = false,
            error = Some(s"Token missing required scopes. Has: ${scopes.mkString(", ")}. Need: repo (full), admin:org (read/write)")
          )
        }
      } else {
        ValidationResult(passed = false, error = Some(s"GitHub API returned ${response.statusCode}"))
      }
    } match {
      case Success(result) => result
      case Failure(e) => ValidationResult(passed = false, error = Some(s"Failed to validate token: ${e.getMessage}"))
    }
  }

  // Check 2: Target organization exists
  private def validateOrganization(): ValidationResult = {
    Try {
      val response = requests.get(
        s"https://api.github.com/orgs/$targetOrg",
        headers = Map(
          "Authorization" -> s"Bearer $githubToken",
          "Accept" -> "application/vnd.github+json"
        )
      )
      
      if (response.statusCode == 200) {
        ValidationResult(passed = true, error = None)
      } else if (response.statusCode == 404) {
        ValidationResult(
          passed = false,
          error = Some(s"Organization '$targetOrg' does not exist or is not accessible")
        )
      } else {
        ValidationResult(passed = false, error = Some(s"GitHub API returned ${response.statusCode}"))
      }
    } match {
      case Success(result) => result
      case Failure(e) => ValidationResult(passed = false, error = Some(s"Failed to validate organization: ${e.getMessage}"))
    }
  }

  // Check 3: Repository name available
  private def validateRepositoryName(projectName: String): ValidationResult = {
    Try {
      val response = requests.get(
        s"https://api.github.com/repos/$targetOrg/$projectName",
        headers = Map(
          "Authorization" -> s"Bearer $githubToken",
          "Accept" -> "application/vnd.github+json"
        ),
        check = false // Don't throw on 404
      )
      
      if (response.statusCode == 404) {
        // 404 means repository doesn't exist - available!
        ValidationResult(passed = true, error = None)
      } else if (response.statusCode == 200) {
        ValidationResult(
          passed = false,
          error = Some(s"Repository '$targetOrg/$projectName' already exists")
        )
      } else {
        ValidationResult(passed = false, error = Some(s"GitHub API returned ${response.statusCode}"))
      }
    } match {
      case Success(result) => result
      case Failure(e) => ValidationResult(passed = false, error = Some(s"Failed to check repository: ${e.getMessage}"))
    }
  }

  // Check 4: Project name conventions
  private def validateProjectName(projectName: String): ValidationResult = {
    val pattern: Regex = "^[a-z][a-z0-9]*(-[a-z0-9]+)*$".r
    val length = projectName.length
    
    if (length < 3) {
      ValidationResult(passed = false, error = Some(s"Project name too short ($length characters, minimum 3)"))
    } else if (length > 50) {
      ValidationResult(passed = false, error = Some(s"Project name too long ($length characters, maximum 50)"))
    } else if (projectName.contains("--")) {
      ValidationResult(passed = false, error = Some("Project name contains consecutive hyphens"))
    } else if (!pattern.matches(projectName)) {
      ValidationResult(
        passed = false,
        error = Some(s"Project name '$projectName' violates naming conventions. Use kebab-case: lowercase letters, numbers, hyphens only, start with letter")
      )
    } else {
      ValidationResult(passed = true, error = None)
    }
  }

  // Check 5: Git configured locally
  private def validateGitConfig(): ValidationResult = {
    Try {
      val userName = os.proc("git", "config", "user.name").call().out.trim()
      val userEmail = os.proc("git", "config", "user.email").call().out.trim()
      
      if (userName.isEmpty) {
        ValidationResult(passed = false, error = Some("Git user.name not configured"))
      } else if (userEmail.isEmpty) {
        ValidationResult(passed = false, error = Some("Git user.email not configured"))
      } else {
        ValidationResult(passed = true, error = None)
      }
    } match {
      case Success(result) => result
      case Failure(e) => ValidationResult(
        passed = false,
        error = Some(s"Failed to check git config: ${e.getMessage}. Run: git config --global user.name 'Your Name' && git config --global user.email 'you@example.com'")
      )
    }
  }

  // Check 6: Mill version valid (0.11.6+)
  private def validateMillVersion(): ValidationResult = {
    Try {
      val versionOutput = os.proc("mill", "--version").call().out.trim()
      
      // Parse version (e.g., "Mill Build Tool version 0.11.6")
      val versionPattern = """(\d+)\.(\d+)\.(\d+)""".r
      versionPattern.findFirstMatchIn(versionOutput) match {
        case Some(m) =>
          val major = m.group(1).toInt
          val minor = m.group(2).toInt
          val patch = m.group(3).toInt
          
          // Check if >= 0.11.6
          if (major > 0 || (major == 0 && minor > 11) || (major == 0 && minor == 11 && patch >= 6)) {
            ValidationResult(passed = true, error = None)
          } else {
            ValidationResult(
              passed = false,
              error = Some(s"Mill $major.$minor.$patch installed, 0.11.6+ required. Upgrade: cs install mill")
            )
          }
        case None =>
          ValidationResult(passed = false, error = Some(s"Could not parse Mill version from: $versionOutput"))
      }
    } match {
      case Success(result) => result
      case Failure(e) => ValidationResult(
        passed = false,
        error = Some(s"Mill not found or failed to run: ${e.getMessage}. Install: cs install mill")
      )
    }
  }

  // Check 7: Source repository valid
  private def validateSourceRepo(): ValidationResult = {
    val requiredFiles = Seq(
      sourceRepoPath / "HOW-WE-WORK.md",
      sourceRepoPath / ".github",
      sourceRepoPath / "doc" / "reference" / "SBPF",
      sourceRepoPath / "doc" / "reference" / "templates",
      sourceRepoPath / "doc" / "reference" / "validation",
      sourceRepoPath / "mill-spinoff-plugin",
      sourceRepoPath / "mill-deploy-plugin"
    )
    
    val missingFiles = requiredFiles.filterNot(os.exists)
    
    if (missingFiles.isEmpty) {
      ValidationResult(passed = true, error = None)
    } else {
      ValidationResult(
        passed = false,
        error = Some(s"Source repository missing required files: ${missingFiles.map(_.last).mkString(", ")}. Path: $sourceRepoPath")
      )
    }
  }
}

/**
 * Validation check definition.
 */
sealed trait ValidationCheck {
  def number: Int
  def description: String
  def category: String
  def severity: String
}

case object Check1_GitHubTokenValid extends ValidationCheck {
  val number = 1
  val description = "GitHub token valid (scopes: repo, admin:org)"
  val category = "Authentication"
  val severity = "Blocking"
}

case object Check2_OrganizationExists extends ValidationCheck {
  val number = 2
  val description = s"Target organization exists"
  val category = "GitHub"
  val severity = "Blocking"
}

case object Check3_RepositoryNameAvailable extends ValidationCheck {
  val number = 3
  val description = "Repository name available"
  val category = "GitHub"
  val severity = "Blocking"
}

case object Check4_ProjectNameConventions extends ValidationCheck {
  val number = 4
  val description = "Project name follows conventions (kebab-case)"
  val category = "Naming"
  val severity = "Blocking"
}

case object Check5_GitConfigured extends ValidationCheck {
  val number = 5
  val description = "Git configured locally (user.name, user.email)"
  val category = "Git"
  val severity = "Blocking"
}

case object Check6_MillVersionValid extends ValidationCheck {
  val number = 6
  val description = "Mill 0.11.6+ installed"
  val category = "Build Tool"
  val severity = "Blocking"
}

case object Check7_SourceRepoValid extends ValidationCheck {
  val number = 7
  val description = "Source repo valid (copilot-training with framework files)"
  val category = "Source"
  val severity = "Blocking"
}

/**
 * Result of a validation check.
 */
case class ValidationResult(
  passed: Boolean,
  error: Option[String] = None
)
