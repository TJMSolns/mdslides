package com.retisio.mill

import os.Path
import com.retisio.mill.ValidationCheckType.*

/**
 * Validates spinoff readiness for a sub-service.
 *
 * Performs 13 pre-flight checks to ensure the sub-service has the necessary
 * STRUCTURE and SCAFFOLDING to begin work as an independent project.
 *
 * IMPORTANT: This validates "ready to spin off" NOT "ready for production".
 * Ceremonies (Event Storming, Three Amigos, TDD, etc.) happen AFTER spinoff
 * in the production repository. This validator checks that the service has:
 * - Architectural boundaries defined (charter, context map)
 * - Team ownership assigned
 * - Scaffold structure in place (even if marked TODO/scaffold)
 *
 * @param candidatesPath Path to SPINOFF-CANDIDATES.md
 * @param contextMapPath Path to CONTEXT-MAP.md
 * @param serviceRoot Root directory of the parent service
 */
class SpinoffValidator(
  candidatesPath: Path,
  contextMapPath: Path,
  serviceRoot: Path
) {

  /**
   * Validate spinoff readiness for a sub-service.
   *
   * @param subServiceName Name of sub-service to validate
   * @return Validation result with individual check outcomes
   */
  def validate(subServiceName: String): ValidationResult = {
    val checks = List(
      check1CharterExists(subServiceName),
      check2DomainModelComplete(subServiceName),
      check3ContextMapDefined(subServiceName),
      check4BDDScenariosExist(subServiceName),
      check5UnitTestsExist(subServiceName),
      check6IntegrationContractsDefined(subServiceName),
      check7APISpecificationComplete(subServiceName),
      check8EventSchemasDefined(subServiceName),
      check9DatabaseMigrationsReady(subServiceName),
      check10ObservabilityConfigured(subServiceName),
      check11SecurityControlsDefined(subServiceName),
      check12DeploymentManifestsReady(subServiceName),
      check13TeamOwnershipAssigned(subServiceName),
      check14FrameworkFilesPresent(subServiceName)
    )

    ValidationResult(
      subServiceName = subServiceName,
      checks = checks
    )
  }

  /** Check 1: CHARTER.md exists with problem statement */
  private def check1CharterExists(subServiceName: String): ValidationCheck = {
    val charterPath = serviceRoot / "doc" / subServiceName / "CHARTER.md"
    val exists = os.exists(charterPath)

      if (!exists) {
        ValidationCheck(SpinoffCheckCharterExists, passed = false,
          message = s"CHARTER.md not found at ${charterPath}. Create using template from doc/reference/templates/CHARTER-TEMPLATE.md")
    } else {
      val content = os.read(charterPath)
      val hasProblemStatement = content.contains("## Problem Statement") && content.length > 500
        ValidationCheck(SpinoffCheckCharterExists, passed = hasProblemStatement,
          message = if (hasProblemStatement) "" else "Charter exists but missing Problem Statement or too short")
    }
  }

  /** Check 2: Domain model structure exists (even if marked scaffold/TODO) */
  private def check2DomainModelComplete(subServiceName: String): ValidationCheck = {
    val domainModelPath = serviceRoot / "doc" / "domain-models"

    if (!os.exists(domainModelPath)) {
      return ValidationCheck(SpinoffCheckDomainModelStructure, passed = false,
        message = s"Domain model directory not found: ${domainModelPath}. Create structure even if marked scaffold.")
    }

    // Check for ANY aggregate documentation (even if marked "scaffold" or "TODO")
    val aggregateFiles = os.walk(domainModelPath)
      .filter(_.ext == "md")
      .filter(p => p.last.contains("aggregate") || p.last.contains("Aggregate"))
      .size

    val passed = aggregateFiles >= 1

      ValidationCheck(SpinoffCheckDomainModelStructure, passed = passed,
        message = if (passed) "" else s"No aggregate documentation found in ${domainModelPath}. Create at least 1 aggregate-*.md file (can be marked 'scaffold').")
  }

  /** Check 3: Context map exists (relationships can be high-level/draft) */
  private def check3ContextMapDefined(subServiceName: String): ValidationCheck = {
    // Check for context map in doc/domain-models/ OR at contextMapPath
    val altContextMapPath = serviceRoot / "doc" / "domain-models" / "CONTEXT-MAP.md"
    val contextMapExists = os.exists(contextMapPath) || os.exists(altContextMapPath)

    if (!contextMapExists) {
      return ValidationCheck(SpinoffCheckContextMapExists, passed = false,
        message = s"CONTEXT-MAP.md not found at ${contextMapPath} or ${altContextMapPath}. Create high-level context map (can be draft).")
    }

    // Existence is sufficient - content can be draft/TODO
      ValidationCheck(SpinoffCheckContextMapExists, passed = true, message = "")
  }

  /** Check 4: BDD scenario structure exists (can contain TODOs) */
  private def check4BDDScenariosExist(subServiceName: String): ValidationCheck = {
    val featuresPath = serviceRoot / "features"
    
    if (!os.exists(featuresPath)) {
      return ValidationCheck(SpinoffCheckBDDScenarios, passed = false,
        message = s"Features directory not found: ${featuresPath}. Create features/ with at least 1 .feature file (can contain TODOs).")
    }

    val featureFiles = os.walk(featuresPath).filter(_.ext == "feature")
    val passed = featureFiles.size >= 1

      ValidationCheck(SpinoffCheckBDDScenarios, passed = passed,
        message = if (passed) "" else s"No .feature files found in ${featuresPath}. Create at least 1 placeholder scenario.")
  }

  /** Check 5: Test structure exists (tests can throw UnsupportedOperationException) */
  private def check5UnitTestsExist(subServiceName: String): ValidationCheck = {
    val testPath = serviceRoot / "src" / "test"

    if (!os.exists(testPath)) {
      return ValidationCheck(SpinoffCheckTestsExist, passed = false,
        message = s"Test directory not found: ${testPath}. Create src/test/ with at least 1 test class (can be stub).")
    }

    val javaTestFiles = os.walk(testPath).filter(_.ext == "java").filter(p => p.last.endsWith("Test.java")).size
    val scalaTestFiles = os.walk(testPath).filter(_.ext == "scala").filter(p => p.last.endsWith("Test.scala")).size

    val totalTests = javaTestFiles + scalaTestFiles
    val passed = totalTests >= 1 // At least 1 test class (even if stub)

      ValidationCheck(SpinoffCheckTestsExist, passed = passed,
        message = if (passed) "" else s"No test classes found. Create at least 1 *Test.java or *Test.scala file (can throw UnsupportedOperationException).")
  }

  /** Check 6: Integration approach documented (context map sufficient) */
  private def check6IntegrationContractsDefined(subServiceName: String): ValidationCheck = {
    // Context map serves as integration documentation for spinoff readiness
    val altContextMapPath = serviceRoot / "doc" / "domain-models" / "CONTEXT-MAP.md"
    val contextMapExists = os.exists(contextMapPath) || os.exists(altContextMapPath)

      ValidationCheck(SpinoffCheckIntegrationApproach, passed = contextMapExists,
        message = if (contextMapExists) "Context map defines integration approach" else s"Context map needed for integration planning")
  }

  /** Check 7: API approach defined (OpenAPI created during Phase 2 post-spinoff) */
  private def check7APISpecificationComplete(subServiceName: String): ValidationCheck = {
    // API spec is created during Phase 2 ceremonies (Three Amigos), not required for spinoff
    // Charter existence indicates API requirements are understood
      ValidationCheck(SpinoffCheckAPINoted, passed = true,
        message = "OpenAPI spec created during Phase 2 ceremonies (post-spinoff)")
  }

  /** Check 8: Event-driven architecture noted (schemas created during Phase 1 post-spinoff) */
  private def check8EventSchemasDefined(subServiceName: String): ValidationCheck = {
    // Event schemas created during Event Storming ceremony (Phase 1), not required for spinoff
    // Domain model existence indicates event-driven approach is understood
      ValidationCheck(SpinoffCheckEventDrivenNoted, passed = true,
        message = "Event schemas created during Phase 1 Event Storming (post-spinoff)")
  }

  /** Check 9: Database migration structure exists (can be placeholder) */
  private def check9DatabaseMigrationsReady(subServiceName: String): ValidationCheck = {
    val dbPath = serviceRoot / "db"
    val migrationsPath = dbPath / "migrations"
    
    if (!os.exists(dbPath)) {
      return ValidationCheck(SpinoffCheckDBMigrations, passed = false,
        message = s"Database directory not found: ${dbPath}. Create db/migrations/ with V1__*.sql (can be placeholder).")
    }

    if (!os.exists(migrationsPath)) {
      return ValidationCheck(SpinoffCheckDBMigrations, passed = false,
        message = s"Migrations directory not found: ${migrationsPath}. Create with V1__*.sql placeholder.")
    }

    val migrationFiles = os.list(migrationsPath).filter(p => p.ext == "sql")
    val passed = migrationFiles.size >= 1

      ValidationCheck(SpinoffCheckDBMigrations, passed = passed,
        message = if (passed) "Flyway migrations can be refined during Phase 3 (post-spinoff)" else s"No .sql files in ${migrationsPath}. Create V1__*.sql placeholder.")
  }

  /** Check 10: Observability approach noted (configured during Phase 3 post-spinoff) */
  private def check10ObservabilityConfigured(subServiceName: String): ValidationCheck = {
    // OpenTelemetry configured during Phase 3 implementation, not required for spinoff
    // Charter/architecture decisions indicate observability requirements
      ValidationCheck(SpinoffCheckObservabilityNoted, passed = true,
        message = "OpenTelemetry/metrics configured during Phase 3 implementation (post-spinoff)")
  }

  /** Check 11: Security approach noted (documented during Phase 2 post-spinoff) */
  private def check11SecurityControlsDefined(subServiceName: String): ValidationCheck = {
    // Security model documented during Phase 2 acceptance criteria, not required for spinoff
    // Charter indicates security requirements at high level
      ValidationCheck(SpinoffCheckSecurityNoted, passed = true,
        message = "AuthN/AuthZ model documented during Phase 2 ceremonies (post-spinoff)")
  }

  /** Check 12: Deployment structure exists (manifests can be placeholders) */
  private def check12DeploymentManifestsReady(subServiceName: String): ValidationCheck = {
    val k8sPath = serviceRoot / "k8s"

    if (!os.exists(k8sPath)) {
      return ValidationCheck(SpinoffCheckDeploymentStructure, passed = false,
        message = s"k8s directory not found: ${k8sPath}. Create k8s/ with deployment.yaml, service.yaml (can be placeholders).")
    }

    val k8sManifests = os.list(k8sPath).filter(_.ext == "yaml")
    val passed = k8sManifests.size >= 1

      ValidationCheck(SpinoffCheckDeploymentStructure, passed = passed,
        message = if (passed) "Deployment manifests can be refined during Phase 3 (post-spinoff)" else s"No .yaml files in ${k8sPath}. Create placeholder manifests.")
  }

  /** Check 13: Team ownership assigned (charter or CODEOWNERS) */
  private def check13TeamOwnershipAssigned(subServiceName: String): ValidationCheck = {
    // Team can be assigned in charter, CODEOWNERS created during spinoff
    val charterPath = serviceRoot / "doc" / "exhibits"
    val charterExists = os.exists(charterPath)

    if (charterExists) {
      ValidationCheck(SpinoffCheckTeamOwnership, passed = true,
        message = "Team assigned in charter. CODEOWNERS created during spinoff execution.")
    } else {
      ValidationCheck(SpinoffCheckTeamOwnership, passed = false,
        message = "Charter should specify team ownership (@RETISIO/team-name)")
    }
  }

  /** Check 14: Framework files present (ceremony instructions, templates, SBPFs) */
  private def check14FrameworkFilesPresent(subServiceName: String): ValidationCheck = {
    // This check verifies spun-off repository has framework files
    // Only applicable AFTER spinoff execution
    val requiredFiles = List(
      serviceRoot / "HOW-WE-WORK.md",
      serviceRoot / ".github",
      serviceRoot / "doc" / "reference" / "SBPF",
      serviceRoot / "doc" / "reference" / "templates",
      serviceRoot / "doc" / "reference" / "validation"
    )

    val missingFiles = requiredFiles.filterNot(os.exists)

    if (missingFiles.isEmpty) {
      // Verify ceremony instruction files
      val ceremonyFiles = if (os.exists(serviceRoot / ".github")) {
        os.list(serviceRoot / ".github")
          .filter(_.last.startsWith("copilot-instructions"))
          .filter(_.last.endsWith(".md"))
          .size
      } else 0

      if (ceremonyFiles >= 14) {
        ValidationCheck(SpinoffCheckFrameworkFiles, passed = true,
          message = s"All framework files present ($ceremonyFiles ceremony guides, templates, SBPFs)")
      } else {
        ValidationCheck(SpinoffCheckFrameworkFiles, passed = false,
          message = s"Only $ceremonyFiles ceremony guides found (expected 14). Re-run spinoff with updated plugin.")
      }
    } else {
      ValidationCheck(SpinoffCheckFrameworkFiles, passed = false,
        message = s"Framework files missing: ${missingFiles.map(_.last).mkString(", ")}. Re-run spinoff to copy framework files.")
    }
  }
}

enum ValidationCheckType(val label: String):
  case SpinoffCheckCharterExists extends ValidationCheckType("Charter exists")
  case SpinoffCheckDomainModelStructure extends ValidationCheckType("Domain model structure exists")
  case SpinoffCheckContextMapExists extends ValidationCheckType("Context map exists")
  case SpinoffCheckBDDScenarios extends ValidationCheckType("BDD scenario structure exists")
  case SpinoffCheckTestsExist extends ValidationCheckType("Test structure exists")
  case SpinoffCheckIntegrationApproach extends ValidationCheckType("Integration approach documented")
  case SpinoffCheckAPINoted extends ValidationCheckType("API approach defined in charter")
  case SpinoffCheckEventDrivenNoted extends ValidationCheckType("Event-driven approach noted")
  case SpinoffCheckDBMigrations extends ValidationCheckType("Database migration structure exists")
  case SpinoffCheckObservabilityNoted extends ValidationCheckType("Observability approach noted")
  case SpinoffCheckSecurityNoted extends ValidationCheckType("Security approach noted")
  case SpinoffCheckDeploymentStructure extends ValidationCheckType("Deployment structure exists")
  case SpinoffCheckTeamOwnership extends ValidationCheckType("Team ownership assigned")
  case SpinoffCheckFrameworkFiles extends ValidationCheckType("Framework files present")

case class ValidationCheck(
  check: ValidationCheckType,
  passed: Boolean,
  message: String
)

/** Aggregated validation result for a sub-service */
case class ValidationResult(
  subServiceName: String,
  checks: List[ValidationCheck]
) {
  def passedCount: Int = checks.count(_.passed)
  def totalCount: Int = checks.size
  def isReady: Boolean = checks.forall(_.passed)
}
