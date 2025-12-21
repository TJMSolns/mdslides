package com.retisio.mill

import mill._
import mill.scalalib._
import os.Path

/**
 * Mill module trait that provides deployment automation commands.
 * 
 * Usage in build.sc:
 * {{{
 * import com.retisio.mill.DeployModule
 * 
 * object myService extends DeployModule {
 *   def scalaVersion = "2.13.12"
 * }
 * }}}
 * 
 * Provides three commands following the spinoff pattern:
 * - deployList: Show all deployment targets and their status
 * - deployValidate <target>: Validate readiness for deployment
 * - deployExecute <target>: Execute deployment with monitoring
 */
trait DeployModule extends ScalaModule {
  
  /**
   * Path to DEPLOY-TARGETS.md configuration file.
   * Default: services/<module-name>/DEPLOY-TARGETS.md
   */
  def deployTargetsFile: T[Path] = T {
    millSourcePath / "DEPLOY-TARGETS.md"
  }
  
  /**
   * Path to Kubernetes manifests directory.
   * Default: services/<module-name>/k8s
   */
  def kubernetesManifestsDir: T[Path] = T {
    millSourcePath / "k8s"
  }
  
  /**
   * Path to database migrations directory.
   * Default: services/<module-name>/db/migrations
   */
  def migrationsDir: T[Path] = T {
    millSourcePath / "db" / "migrations"
  }
  
  /**
   * Service name (derived from module name).
   */
  def serviceName: T[String] = T {
    millModuleSegments.parts.last
  }
  
  /**
   * List all deployment targets and their current status.
   * 
   * Example output:
   * {{{
   * ╔════════════════════════════════════════════════════════╗
   * ║           DEPLOYMENT TARGETS: my-service               ║
   * ╠════════════════════════════════════════════════════════╣
   * ║ Environment │ Current  │ Desired  │ Status   │ Age    ║
   * ╠═════════════╪══════════╪══════════╪══════════╪════════╣
   * ║ dev         │ v1.2.3   │ v1.2.4   │ 🟡 Update│ 2h ago ║
   * ║ staging     │ v1.2.2   │ v1.2.3   │ 🟡 Update│ 1d ago ║
   * ║ production  │ v1.2.1   │ v1.2.2   │ 🟢 Healthy│ 5d ago║
   * ╚═════════════╧══════════╧══════════╧══════════╧════════╝
   * }}}
   */
  def deployList(): Command[Unit] = T.command {
    val targetsFile = deployTargetsFile()
    
    if (!os.exists(targetsFile)) {
      println(s"❌ DEPLOY-TARGETS.md not found: $targetsFile")
      println(s"Create it from template: cp doc/reference/templates/DEPLOY-TARGETS-TEMPLATE.md $targetsFile")
      sys.error("DEPLOY-TARGETS.md missing")
    }
    
    val parser = new DeployTargetsParser(targetsFile)
    val targets = parser.parseTargets()
    
    DeploymentReporter.displayTargetList(targets)
  }
  
  /**
   * Validate deployment readiness for a target environment.
   * 
   * Runs 4-15 validation checks depending on environment:
   * - Local: 4 checks (tests, docker build)
   * - Dev: 10 checks (+ security, manifests, secrets)
   * - Staging/Production: 15 checks (+ contracts, load tests, approvals)
   * 
   * @param targetName Environment name from DEPLOY-TARGETS.md (e.g., "Dev", "Staging", "Production")
   * 
   * Example:
   * {{{
   * mill myService.deployValidate Staging
   * }}}
   */
  def deployValidate(targetName: String): Command[Unit] = T.command {
    val targetsFile = deployTargetsFile()
    val service = serviceName()
    val k8sDir = kubernetesManifestsDir()
    val dbMigrations = migrationsDir()
    
    // Parse target configuration
    val parser = new DeployTargetsParser(targetsFile)
    val target = parser.findTarget(targetName).getOrElse {
      println(s"❌ Target not found: $targetName")
      println(s"Available targets: ${parser.parseTargets().map(_.name).mkString(", ")}")
      sys.error(s"Invalid target: $targetName")
    }
    
    // Display target details
    DeploymentReporter.displayTarget(target)
    
    // Run validation checks
    val validator = new DeployValidator(
      service = service,
      target = target,
      millSourcePath = T.workspace,
      k8sDir = k8sDir,
      migrationsDir = dbMigrations
    )
    
    val results = validator.runAllChecks()
    
    // Display results with reporter
    DeploymentReporter.displayValidationResults(results, target)
    
    // Fail if any blocking checks failed
    val failures = results.filter(r => r.severity == Severity.Blocking && !r.passed)
    if (failures.nonEmpty) {
      sys.error(s"Validation failed: ${failures.size} blocking issue(s)")
    }
  }
  
  /**
   * Execute deployment to target environment.
   * 
   * Workflow:
   * 1. Validate deployment readiness (all checks)
   * 2. Build Docker image
   * 3. Push image to registry
   * 4. Run database migrations (if any)
   * 5. Build Kubernetes manifests (Kustomize)
   * 6. Apply manifests to cluster
   * 7. Wait for rollout completion
   * 8. Run smoke tests
   * 9. Monitor for 5 minutes (automatic rollback on failure)
   * 10. Record deployment in DEPLOY-TARGETS.md
   * 
   * @param targetName Environment name from DEPLOY-TARGETS.md
   * @param version Optional version override (default: git tag or commit SHA)
   * 
   * Example:
   * {{{
   * mill myService.deployExecute Staging
   * mill myService.deployExecute Production --version v1.2.3
   * }}}
   */
  def deployExecute(targetName: String, version: Option[String] = None): Command[Unit] = T.command {
    val targetsFile = deployTargetsFile()
    val service = serviceName()
    val k8sDir = kubernetesManifestsDir()
    val dbMigrations = migrationsDir()
    val user = getCurrentUser()
    
    // Parse target configuration
    val parser = new DeployTargetsParser(targetsFile)
    val target = parser.findTarget(targetName).getOrElse {
      sys.error(s"Target not found: $targetName")
    }
    
    // Determine version
    val deployVersion = version.getOrElse(determineVersion())
    
    // Display deployment start
    DeploymentReporter.displayDeploymentStart(service, target, deployVersion, user)
    
    // Get kubeconfig path
    val kubeconfig = os.home / ".kube" / s"config-${target.environment}"
    
    // Execute deployment
    val executor = new DeployExecutor(
      service = service,
      target = target,
      version = deployVersion,
      deployedBy = user,
      millSourcePath = T.workspace,
      k8sDir = k8sDir,
      migrationsDir = dbMigrations,
      kubeconfig = kubeconfig
    )
    
    val recorder = new DeploymentRecorder(targetsFile)
    
    val (success, duration, error) = executor.execute()
    
    if (success) {
      // Record successful deployment
      recorder.recordCompleteDeployment(
        environment = target.environment,
        version = deployVersion,
        deployedBy = user,
        durationSeconds = duration
      )
      
      // Display success
      val stats = recorder.getStats(target.environment)
      DeploymentReporter.displayDeploymentSuccess(duration, stats)
    } else {
      // Record failure
      recorder.recordCompleteFailure(
        environment = target.environment,
        version = deployVersion,
        deployedBy = user,
        durationSeconds = duration,
        reason = error.getOrElse("Unknown error")
      )
      
      // Display failure
      DeploymentReporter.displayDeploymentFailure(duration, error.getOrElse("Unknown error"))
      
      sys.error("Deployment failed")
    }
  }
  
  /**
   * Rollback to previous successful version.
   * 
   * @param targetName Environment name
   * @param toVersion Optional specific version to rollback to
   * 
   * Example:
   * {{{
   * mill myService.deployRollback Staging
   * mill myService.deployRollback Production --version v1.2.0
   * }}}
   */
  def deployRollback(targetName: String, toVersion: Option[String] = None): Command[Unit] = T.command {
    val targetsFile = deployTargetsFile()
    val service = serviceName()
    val k8sDir = kubernetesManifestsDir()
    val dbMigrations = migrationsDir()
    
    // Parse target
    val parser = new DeployTargetsParser(targetsFile)
    val target = parser.findTarget(targetName).getOrElse {
      sys.error(s"Target not found: $targetName")
    }
    
    // Determine rollback version
    val rollbackVersion = toVersion.getOrElse {
      parser.getPreviousSuccessfulVersion(target.environment).getOrElse {
        sys.error("No previous successful deployment found")
      }
    }
    
    // Display rollback start
    DeploymentReporter.displayRollbackStart(
      service = service,
      target = target,
      fromVersion = target.currentVersion,
      toVersion = rollbackVersion,
      reason = "Manual rollback by user"
    )
    
    // Get kubeconfig path
    val kubeconfig = os.home / ".kube" / s"config-${target.environment}"
    
    // Execute rollback
    val executor = new DeployExecutor(
      service = service,
      target = target,
      version = target.currentVersion,
      deployedBy = getCurrentUser(),
      millSourcePath = T.workspace,
      k8sDir = k8sDir,
      migrationsDir = dbMigrations,
      kubeconfig = kubeconfig
    )
    
    val (success, duration, error) = executor.rollback(rollbackVersion, "Manual rollback by user")
    
    val recorder = new DeploymentRecorder(targetsFile)
    
    if (success) {
      // Record rollback
      recorder.recordCompleteRollback(
        environment = target.environment,
        failedVersion = target.currentVersion,
        rolledBackTo = rollbackVersion,
        durationSeconds = duration,
        reason = "Manual rollback by user"
      )
      
      // Display success
      DeploymentReporter.displayRollbackSuccess(duration)
    } else {
      println(s"\n❌ ROLLBACK FAILED: ${error.getOrElse("Unknown error")}")
      sys.error("Rollback failed")
    }
  }
  
  // Helper methods
  
  private def determineVersion(): String = {
    // Try git tag first
    val gitTag = os.proc("git", "describe", "--tags", "--exact-match")
      .call(cwd = T.workspace, check = false)
      .out.trim()
    
    if (gitTag.nonEmpty) {
      gitTag
    } else {
      // Fall back to commit SHA
      val sha = os.proc("git", "rev-parse", "--short", "HEAD")
        .call(cwd = T.workspace)
        .out.trim()
      s"sha-$sha"
    }
  }
  
  private def getCurrentUser(): String = {
    os.proc("git", "config", "user.email")
      .call(cwd = T.workspace)
      .out.trim()
  }
}
