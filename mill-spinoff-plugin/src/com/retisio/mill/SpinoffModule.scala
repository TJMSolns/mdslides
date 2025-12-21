package com.retisio.mill

import mill._
import mill.define.{Command, Task}
import mill.scalalib._
import os.Path

/**
 * Mill module trait for spinning off bounded context services from a master training
 * repository to production repositories.
 *
 * Services mix in this trait and configure spinoff candidates via SPINOFF-CANDIDATES.md
 * and context relationships via CONTEXT-MAP.md.
 *
 * Example usage in service build.sc:
 * {{{
 * import $ivy.`com.retisio::mill-spinoff:1.0.0`
 * import com.retisio.mill.SpinoffModule
 *
 * object billingService extends JavaModule with SpinoffModule {
 *   def spinoffCandidatesPath = T { millSourcePath / "doc" / "SPINOFF-CANDIDATES.md" }
 *   def contextMapPath = T { millSourcePath / "doc" / "domain-models" / "CONTEXT-MAP.md" }
 *   def targetOrg = T { "RETISIO" }
 * }
 * }}}
 *
 * Commands:
 * - `mill service.spinoffList` - List all spinoff candidates
 * - `mill service.spinoffValidate <subServiceName>` - Validate readiness for spinoff
 * - `mill service.spinoffExecute <subServiceName>` - Execute spinoff to new repository
 */
trait SpinoffModule extends Module {

  /**
   * Path to SPINOFF-CANDIDATES.md documenting services ready for spinoff.
   * Default: `millSourcePath / "doc" / "SPINOFF-CANDIDATES.md"`
   */
  def spinoffCandidatesPath: Task[Path] = T {
    millSourcePath / "doc" / "SPINOFF-CANDIDATES.md"
  }

  /**
   * Path to CONTEXT-MAP.md documenting bounded context relationships.
   * Default: `millSourcePath / "doc" / "domain-models" / "CONTEXT-MAP.md"`
   */
  def contextMapPath: Task[Path] = T {
    millSourcePath / "doc" / "domain-models" / "CONTEXT-MAP.md"
  }

  /**
   * Target GitHub organization for spun-off repositories.
   * Default: `"RETISIO"`
   */
  def targetOrg: Task[String] = T { "RETISIO" }

  /**
   * GitHub personal access token for API authentication.
   * Reads from environment variable `GITHUB_TOKEN`.
   */
  def githubToken: Task[String] = T.input {
    sys.env.getOrElse("GITHUB_TOKEN", throw new Exception(
      "GITHUB_TOKEN environment variable not set. Required for GitHub API operations."
    ))
  }

  /**
   * List all spinoff candidates from SPINOFF-CANDIDATES.md.
   *
   * Usage: `mill service.spinoffList`
   *
   * Output: Table of candidates with status, trigger conditions, and readiness.
   */
  def spinoffList(): Command[Unit] = T.command {
    val candidatesPath = spinoffCandidatesPath()
    val parser = new SpinoffCandidatesParser(candidatesPath)
    val candidates = parser.parse()

    if (candidates.isEmpty) {
      println(s"No spinoff candidates found in ${candidatesPath}")
      println(s"Create SPINOFF-CANDIDATES.md using the template from mill-spinoff-plugin")
    } else {
      println(s"Found ${candidates.size} spinoff candidates:\n")
      println(f"${"Service"}%-30s ${"Status"}%-15s ${"Aggregates"}%-10s ${"Readiness"}%-10s")
      println("-" * 75)
      candidates.foreach { candidate =>
        val statusIcon = candidate.status match {
          case "Not Ready" => "🟢"
          case "Monitoring" => "🟡"
          case "Triggered" => "🔴"
          case "Complete" => "✅"
          case _ => "❓"
        }
        println(f"${candidate.name}%-30s ${statusIcon} ${candidate.status}%-12s ${candidate.aggregateCount}%-10d ${candidate.readinessPercentage}%-10s")
      }
      println()
    }
  }

  /**
   * Validate readiness for spinning off a sub-service.
   *
   * Performs 13 pre-flight checks:
   * 1. CHARTER.md exists with problem statement
   * 2. Domain model complete (aggregates, entities, value objects)
   * 3. Context map defines relationships
   * 4. BDD scenarios exist (features/)
   * 5. Unit tests exist with >80% coverage
   * 6. Integration contracts defined
   * 7. API specification complete (OpenAPI)
   * 8. Event schema defined (Avro/JSON Schema)
   * 9. Database migrations ready
   * 10. Observability configured (OpenTelemetry)
   * 11. Security controls defined (AuthN/AuthZ)
   * 12. Deployment manifests ready (Dockerfile, k8s/)
   * 13. Team ownership assigned (CODEOWNERS)
   *
   * Usage: `mill service.spinoffValidate Invoice`
   *
   * @param subServiceName Name of sub-service to validate (e.g., "Invoice")
   */
  def spinoffValidate(subServiceName: String): Command[Unit] = T.command {
    println(s"Validating spinoff readiness for: $subServiceName\n")

    val candidatesPath = spinoffCandidatesPath()
    val contextPath = contextMapPath()
    val serviceRoot = millSourcePath

    val validator = new SpinoffValidator(
      candidatesPath = candidatesPath,
      contextMapPath = contextPath,
      serviceRoot = serviceRoot
    )

    val result = validator.validate(subServiceName)

    println(s"Validation Results for ${subServiceName}:")
    println("=" * 80)
    println()

    result.checks.foreach { check =>
      val icon = if (check.passed) "✅" else "❌"
      println(f"${icon} ${check.check.label}%-50s ${if (check.passed) "PASS" else "FAIL"}")
      if (!check.passed && check.message.nonEmpty) {
        println(s"   └─ ${check.message}")
      }
    }

    println()
    println(s"Total: ${result.passedCount}/${result.totalCount} checks passed")
    println()

    if (result.isReady) {
      println(s"✅ ${subServiceName} is READY for spinoff")
      println()
      println("Next steps:")
      println(s"  1. Review validation results")
      println(s"  2. Execute spinoff: mill ${this.getClass.getSimpleName}.spinoffExecute ${subServiceName}")
    } else {
      println(s"❌ ${subServiceName} is NOT READY for spinoff")
      println()
      println("Required actions:")
      result.checks.filter(!_.passed).foreach { check =>
        println(s"  • ${check.check.label}: ${check.message}")
      }
    }
  }

  /**
   * Execute spinoff of sub-service to new GitHub repository.
   *
   * Process:
   * 1. Validate spinoff readiness (all 13 checks must pass)
   * 2. Create new repository in target organization (RETISIO/*)
   * 3. Extract bounded context code (domain, application, infrastructure)
   * 4. Generate service repository structure (build.sc, Dockerfile, k8s/, CI/CD)
   * 5. Create spinoff ADR documenting decision
   * 6. Push initial code to new repository
   * 7. Configure branch protection, permissions, secrets
   * 8. Update parent repository SPINOFF-CANDIDATES.md (mark as Complete)
   *
   * Usage: `mill service.spinoffExecute Invoice`
   *
   * Requires: GITHUB_TOKEN environment variable with repo and admin:org scopes
   *
   * @param subServiceName Name of sub-service to spin off (e.g., "Invoice")
   */
  def spinoffExecute(subServiceName: String): Command[Unit] = T.command {
    println(s"Executing spinoff for: $subServiceName")
    println("=" * 80)
    println()

    // Step 1: Validate readiness
    println("Step 1/8: Validating spinoff readiness...")
    val candidatesPath = spinoffCandidatesPath()
    val contextPath = contextMapPath()
    val serviceRoot = millSourcePath

    val validator = new SpinoffValidator(
      candidatesPath = candidatesPath,
      contextMapPath = contextPath,
      serviceRoot = serviceRoot
    )

    val validationResult = validator.validate(subServiceName)

    if (!validationResult.isReady) {
      println(s"❌ Validation FAILED. ${subServiceName} is not ready for spinoff.")
      println()
      println("Failed checks:")
      validationResult.checks.filter(!_.passed).foreach { check =>
        println(s"  • ${check.name}: ${check.message}")
      }
      throw new Exception(s"Spinoff aborted due to validation failures")
    }

    println(s"✅ Validation passed (${validationResult.passedCount}/${validationResult.totalCount} checks)")
    println()

    // Step 2: Create GitHub repository
    println("Step 2/8: Creating GitHub repository...")
    val org = targetOrg()
    val token = githubToken()
    val repoName = s"${subServiceName.toLowerCase}-service"
    val githubClient = new GitHubClient(token)

    val repoUrl = githubClient.createRepository(
      org = org,
      name = repoName,
      description = s"${subServiceName} Service - Spun off from training repository",
      isPrivate = true
    )
    println(s"✅ Created repository: ${repoUrl}")
    println()

    // Step 3: Extract bounded context code
    println("Step 3/8: Extracting bounded context code...")
    val extractor = new CodeExtractor(
      serviceRoot = serviceRoot,
      subServiceName = subServiceName
    )
    val extractedPath = extractor.extract()
    println(s"✅ Extracted code to: ${extractedPath}")
    println()

    // Step 4: Generate repository structure
    println("Step 4/8: Generating service repository structure...")
    val generator = new RepositoryGenerator(
      targetPath = extractedPath,
      subServiceName = subServiceName,
      sourceServiceName = this.getClass.getSimpleName,
      contextMapPath = contextPath,
      sourceRepoPath = millSourcePath
    )
    generator.generate()
    println(s"✅ Generated repository structure")
    println()

    // Step 5: Create spinoff ADR
    println("Step 5/8: Creating spinoff ADR...")
    val adrGenerator = new templates.SpinoffADRTemplate(
      subServiceName = subServiceName,
      sourceServiceName = this.getClass.getSimpleName,
      targetOrg = org,
      repoName = repoName
    )
    val adrPath = extractedPath / "doc" / "governance" / "ADR" / s"ADR-001-spinoff-from-training-repository.md"
    os.write(adrPath, adrGenerator.generate(), createFolders = true)
    println(s"✅ Created spinoff ADR: ${adrPath}")
    println()

    // Step 6: Push initial code
    println("Step 6/8: Pushing initial code to GitHub...")
    githubClient.pushInitialCode(
      localPath = extractedPath,
      repoUrl = repoUrl
    )
    println(s"✅ Pushed initial code to ${repoUrl}")
    println()

    // Step 7: Configure repository
    println("Step 7/8: Configuring repository settings...")
    githubClient.configureRepository(
      org = org,
      repoName = repoName
    )
    println(s"✅ Configured branch protection and permissions")
    println()

    // Step 8: Update SPINOFF-CANDIDATES.md
    println("Step 8/8: Updating SPINOFF-CANDIDATES.md...")
    val updater = new SpinoffCandidatesUpdater(candidatesPath)
    updater.markComplete(subServiceName, repoUrl)
    println(s"✅ Updated SPINOFF-CANDIDATES.md (marked ${subServiceName} as Complete)")
    println()

    println("=" * 80)
    println(s"✅ Spinoff COMPLETE for ${subServiceName}")
    println()
    println(s"Repository: ${repoUrl}")
    println()
    println("🚨 CRITICAL: Post-Spinoff Cleanup Required")
    println("=" * 80)
    println()
    println("The source directory MUST be deleted from the training repository")
    println("within 24 hours to avoid dual source of truth.")
    println()
    println("Run these commands:")
    println()
    println(s"  # 1. Verify production repo exists")
    println(s"  curl -H \"Authorization: token $$GITHUB_TOKEN\" \\")
    println(s"    https://api.github.com/repos/${org}/${repoName}")
    println()
    println(s"  # 2. Delete source from training repo")
    val sourceDir = if (serviceRoot.toString.contains("/services/")) {
      s"services/${subServiceName.toLowerCase}-management/"
    } else {
      s"bounded-contexts/${subServiceName.toLowerCase}/"
    }
    println(s"  rm -rf ${sourceDir}")
    println(s"  git add -A")
    println(s"  git commit -m \"Remove ${subServiceName} after spinoff to ${org}/${repoName}\"")
    println(s"  git push origin main")
    println()
    println("See: POL-029-mill-spinoff-plugin-usage.md - Section 5: Post-Spinoff Cleanup")
    println()
    println("Future Enhancement: Use --cleanup flag to automate deletion")
    println(s"  mill ${this.getClass.getSimpleName}.spinoffExecute ${subServiceName} --cleanup")
    println()
    println("=" * 80)
    println()
    println("Next steps:")
    println(s"  1. DELETE source directory (see above) - MANDATORY")
    println(s"  2. Review repository: ${repoUrl}")
    println(s"  3. Configure CI/CD secrets (GitHub Actions)")
    println(s"  4. Deploy to staging environment")
    println(s"  5. Update cross-boundary integration tests")
    println(s"  6. Document spinoff in Ceremony 4.3 (Living Documentation Sync)")
  }

  // TODO: Future enhancement - Add --cleanup flag support
  // def spinoffExecute(subServiceName: String, cleanup: Boolean = false): Command[Unit] = T.command {
  //   // ... existing spinoff logic ...
  //   
  //   if (cleanup) {
  //     println("Step 9/9: Cleaning up source directory...")
  //     val sourceDir = detectSourceDirectory(subServiceName)
  //     os.remove.all(sourceDir)
  //     println(s"✅ Deleted source directory: ${sourceDir}")
  //   }
  // }
}
