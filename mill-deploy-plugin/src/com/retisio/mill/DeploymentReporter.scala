package com.retisio.mill

/**
 * Formats and displays deployment validation and execution results.
 * 
 * Provides colorized console output for validation checks and deployment progress.
 */
object DeploymentReporter {
  
  // ANSI color codes
  private val RESET = "\u001b[0m"
  private val BOLD = "\u001b[1m"
  private val RED = "\u001b[31m"
  private val GREEN = "\u001b[32m"
  private val YELLOW = "\u001b[33m"
  private val BLUE = "\u001b[34m"
  private val CYAN = "\u001b[36m"
  private val GRAY = "\u001b[90m"
  
  /**
   * Display deployment target details
   */
  def displayTarget(target: DeployTarget): Unit = {
    println(s"\n${BOLD}${CYAN}Deployment Target: ${target.name}${RESET}")
    println(s"  ${GRAY}Environment:${RESET}     ${target.environment}")
    println(s"  ${GRAY}Cluster:${RESET}         ${target.cluster}")
    println(s"  ${GRAY}Namespace:${RESET}       ${target.namespace}")
    println(s"  ${GRAY}Current Version:${RESET} ${target.currentVersion}")
    println(s"  ${GRAY}Desired Version:${RESET} ${target.desiredVersion}")
    println(s"  ${GRAY}Status:${RESET}          ${colorizeStatus(target.status)}")
    println(s"  ${GRAY}Replicas:${RESET}        ${target.replicas}")
    println(s"  ${GRAY}Validation:${RESET}      ${colorizeValidationLevel(target.validationLevel)}")
    println()
  }
  
  /**
   * Display validation results in tabular format
   */
  def displayValidationResults(results: List[ValidationResult], target: DeployTarget): Unit = {
    val totalChecks = results.length
    val passed = results.count(_.passed)
    val failed = results.filterNot(_.passed).filterNot(_.severity == Severity.Skipped)
    val skipped = results.count(_.severity == Severity.Skipped)
    val blocking = results.filterNot(_.passed).count(_.severity == Severity.Blocking)
    
    println(s"\n${BOLD}${CYAN}Validation Results for ${target.name}${RESET}")
    println(s"${GRAY}${"=" * 80}${RESET}\n")
    
    // Group by category
    val grouped = results.groupBy(_.category)
    
    List(
      Category.Testing,
      Category.Security,
      Category.Infrastructure,
      Category.Integration,
      Category.Governance
    ).foreach { category =>
      grouped.get(category).foreach { checks =>
        displayCategoryResults(category, checks)
      }
    }
    
    // Summary
    println(s"\n${BOLD}Summary${RESET}")
    println(s"${GRAY}${"─" * 80}${RESET}")
    println(s"  Total Checks:    $totalChecks")
    println(s"  ${GREEN}✓ Passed:${RESET}        $passed")
    println(s"  ${RED}✗ Failed:${RESET}        ${failed.length}")
    println(s"  ${YELLOW}⊘ Skipped:${RESET}       $skipped")
    
    if (blocking > 0) {
      println(s"\n  ${RED}${BOLD}⚠ $blocking BLOCKING issue(s) - deployment cannot proceed${RESET}")
    }
    
    println()
    
    // Display overall status
    if (blocking > 0) {
      println(s"${RED}${BOLD}❌ VALIDATION FAILED${RESET}")
      println(s"${GRAY}Fix blocking issues above before deploying.${RESET}\n")
    } else {
      println(s"${GREEN}${BOLD}✅ VALIDATION PASSED${RESET}")
      println(s"${GRAY}Ready to deploy with: mill ${target.name}.deployExecute${RESET}\n")
    }
  }
  
  /**
   * Display validation results for a single category
   */
  private def displayCategoryResults(category: Category, checks: List[ValidationResult]): Unit = {
    val categoryIcon = category match {
      case Category.Testing => "🧪"
      case Category.Security => "🔒"
      case Category.Infrastructure => "⚙️"
      case Category.Integration => "🔗"
      case Category.Governance => "📋"
    }
    
    println(s"\n${BOLD}$categoryIcon ${category.name}${RESET}")
    println(s"${GRAY}${"─" * 80}${RESET}")
    
    checks.sortBy(_.checkNumber).foreach { result =>
      val icon = if (result.passed) s"${GREEN}✓${RESET}" else s"${RED}✗${RESET}"
      val severityBadge = colorizeSeverity(result.severity)
      
      println(s"  [$icon] Check ${result.checkNumber}: ${result.name}")
      println(s"      $severityBadge ${result.message}")
      
      result.details.foreach { details =>
        val truncated = if (details.length > 200) details.take(200) + "..." else details
        println(s"      ${GRAY}Details: $truncated${RESET}")
      }
    }
  }
  
  /**
   * Display deployment progress header
   */
  def displayDeploymentStart(service: String, target: DeployTarget, version: String, user: String): Unit = {
    println(s"\n${BOLD}${BLUE}${"=" * 80}${RESET}")
    println(s"${BOLD}${BLUE}   DEPLOYMENT STARTED${RESET}")
    println(s"${BOLD}${BLUE}${"=" * 80}${RESET}\n")
    println(s"  ${GRAY}Service:${RESET}      $service")
    println(s"  ${GRAY}Target:${RESET}       ${target.name} (${target.environment})")
    println(s"  ${GRAY}Version:${RESET}      ${target.currentVersion} → $version")
    println(s"  ${GRAY}Cluster:${RESET}      ${target.cluster}")
    println(s"  ${GRAY}Namespace:${RESET}    ${target.namespace}")
    println(s"  ${GRAY}Deployed By:${RESET}  $user")
    println(s"  ${GRAY}Timestamp:${RESET}    ${java.time.Instant.now()}")
    println()
  }
  
  /**
   * Display rollback progress header
   */
  def displayRollbackStart(service: String, target: DeployTarget, fromVersion: String, toVersion: String, reason: String): Unit = {
    println(s"\n${BOLD}${YELLOW}${"=" * 80}${RESET}")
    println(s"${BOLD}${YELLOW}   ROLLBACK INITIATED${RESET}")
    println(s"${BOLD}${YELLOW}${"=" * 80}${RESET}\n")
    println(s"  ${GRAY}Service:${RESET}      $service")
    println(s"  ${GRAY}Target:${RESET}       ${target.name} (${target.environment})")
    println(s"  ${GRAY}From:${RESET}         $fromVersion")
    println(s"  ${GRAY}To:${RESET}           $toVersion")
    println(s"  ${GRAY}Reason:${RESET}       $reason")
    println(s"  ${GRAY}Timestamp:${RESET}    ${java.time.Instant.now()}")
    println()
  }
  
  /**
   * Display deployment completion
   */
  def displayDeploymentSuccess(duration: Long, stats: DeploymentStats): Unit = {
    println(s"\n${BOLD}${GREEN}${"=" * 80}${RESET}")
    println(s"${BOLD}${GREEN}   ✅ DEPLOYMENT SUCCESSFUL${RESET}")
    println(s"${BOLD}${GREEN}${"=" * 80}${RESET}\n")
    println(s"  ${GRAY}Duration:${RESET}        ${formatDuration(duration)}")
    println(s"  ${GRAY}Total Deploys:${RESET}   ${stats.total}")
    println(s"  ${GRAY}Success Rate:${RESET}    ${colorizeSuccessRate(stats.successRate)}")
    println()
  }
  
  /**
   * Display deployment failure
   */
  def displayDeploymentFailure(duration: Long, error: String): Unit = {
    println(s"\n${BOLD}${RED}${"=" * 80}${RESET}")
    println(s"${BOLD}${RED}   ❌ DEPLOYMENT FAILED${RESET}")
    println(s"${BOLD}${RED}${"=" * 80}${RESET}\n")
    println(s"  ${GRAY}Duration:${RESET}  ${formatDuration(duration)}")
    println(s"  ${GRAY}Error:${RESET}     $error")
    println()
  }
  
  /**
   * Display rollback completion
   */
  def displayRollbackSuccess(duration: Long): Unit = {
    println(s"\n${BOLD}${GREEN}${"=" * 80}${RESET}")
    println(s"${BOLD}${GREEN}   ✅ ROLLBACK SUCCESSFUL${RESET}")
    println(s"${BOLD}${GREEN}${"=" * 80}${RESET}\n")
    println(s"  ${GRAY}Duration:${RESET}       ${formatDuration(duration)}")
    println(s"  ${GRAY}MTTR Target:${RESET}    <2 minutes ${if (duration < 120) s"${GREEN}✓${RESET}" else s"${RED}✗${RESET}"}")
    println()
  }
  
  /**
   * Display available deployment targets
   */
  def displayTargetList(targets: List[DeployTarget]): Unit = {
    println(s"\n${BOLD}${CYAN}Available Deployment Targets${RESET}")
    println(s"${GRAY}${"=" * 120}${RESET}\n")
    
    // Header
    println(f"  ${BOLD}Name%-20s Environment%-15s Cluster%-20s Current%-15s Desired%-15s Status%-15s${RESET}")
    println(s"  ${GRAY}${"─" * 118}${RESET}")
    
    // Targets
    targets.foreach { target =>
      val statusColored = colorizeStatus(target.status)
      println(f"  ${target.name}%-20s ${target.environment}%-15s ${target.cluster}%-20s ${target.currentVersion}%-15s ${target.desiredVersion}%-15s $statusColored")
    }
    
    println()
  }
  
  // ==================== HELPER METHODS ====================
  
  private def colorizeStatus(status: String): String = status match {
    case s if s.contains("Deployed") => s"${GREEN}$s${RESET}"
    case s if s.contains("Failed") => s"${RED}$s${RESET}"
    case s if s.contains("Pending") => s"${YELLOW}$s${RESET}"
    case s if s.contains("In Progress") => s"${BLUE}$s${RESET}"
    case s => s
  }
  
  private def colorizeValidationLevel(level: ValidationLevel): String = level match {
    case ValidationLevel.Minimal => s"${YELLOW}Minimal (4 checks)${RESET}"
    case ValidationLevel.Standard => s"${BLUE}Standard (10 checks)${RESET}"
    case ValidationLevel.Strict => s"${GREEN}Strict (15 checks)${RESET}"
  }
  
  private def colorizeSeverity(severity: Severity): String = severity match {
    case Severity.Blocking => s"${RED}[BLOCKING]${RESET}"
    case Severity.Warning => s"${YELLOW}[WARNING]${RESET}"
    case Severity.Skipped => s"${GRAY}[SKIPPED]${RESET}"
  }
  
  private def colorizeSuccessRate(rate: Double): String = {
    val formatted = f"$rate%.1f%%"
    if (rate >= 95.0) s"${GREEN}$formatted${RESET}"
    else if (rate >= 80.0) s"${YELLOW}$formatted${RESET}"
    else s"${RED}$formatted${RESET}"
  }
  
  private def formatDuration(seconds: Long): String = {
    if (seconds < 60) {
      s"${seconds}s"
    } else if (seconds < 3600) {
      val minutes = seconds / 60
      val secs = seconds % 60
      s"${minutes}m ${secs}s"
    } else {
      val hours = seconds / 3600
      val minutes = (seconds % 3600) / 60
      s"${hours}h ${minutes}m"
    }
  }
}
