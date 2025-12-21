package com.retisio.mill

import java.time.Instant

/**
 * Represents a deployment target environment.
 */
case class DeployTarget(
  name: String,              // "Dev", "Staging", "Production"
  environment: String,       // "dev", "staging", "production"
  cluster: String,           // Kubernetes cluster URL
  namespace: String,         // Kubernetes namespace
  currentVersion: String,    // Currently deployed version
  desiredVersion: String,    // Version to deploy
  status: String,            // "healthy", "degraded", "failed"
  lastDeployed: Option[Instant],
  replicas: Int,
  validationLevel: ValidationLevel
)

/**
 * Validation strictness level per environment.
 */
sealed trait ValidationLevel {
  def checkCount: Int
}
object ValidationLevel {
  case object Minimal extends ValidationLevel { val checkCount = 4 }   // Local
  case object Standard extends ValidationLevel { val checkCount = 10 }  // Dev
  case object Strict extends ValidationLevel { val checkCount = 15 }    // Staging/Production
  
  def fromString(s: String): ValidationLevel = s.toLowerCase match {
    case "minimal" => Minimal
    case "standard" => Standard
    case "strict" => Strict
    case _ => Standard
  }
}

/**
 * Result of a validation check.
 */
case class ValidationResult(
  checkNumber: Int,
  category: Category,
  name: String,
  passed: Boolean,
  severity: Severity,
  message: String,
  details: Option[String] = None
)

/**
 * Validation check category.
 */
sealed trait Category {
  def displayName: String
}
object Category {
  case object Testing extends Category { val displayName = "Testing" }
  case object Security extends Category { val displayName = "Security" }
  case object Infrastructure extends Category { val displayName = "Infrastructure" }
  case object Integration extends Category { val displayName = "Integration" }
  case object Governance extends Category { val displayName = "Governance" }
}

/**
 * Check severity level.
 */
sealed trait Severity
object Severity {
  case object Blocking extends Severity  // Deployment fails if check fails
  case object Warning extends Severity   // Deployment proceeds with warning
  case object Skipped extends Severity   // Check not applicable to environment
}

/**
 * Deployment event record.
 */
case class DeploymentEvent(
  version: String,
  environment: String,
  timestamp: Instant,
  deployedBy: String,
  status: String,        // "success", "failed", "rolled_back"
  duration: Long,        // Seconds
  reason: Option[String] = None,
  approvals: List[String] = List.empty
)

/**
 * Rollback event record.
 */
case class RollbackEvent(
  timestamp: Instant,
  environment: String,
  failedVersion: String,
  rolledBackTo: String,
  reason: String,
  duration: Long
)
