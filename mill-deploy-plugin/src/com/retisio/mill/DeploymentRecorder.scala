package com.retisio.mill

import os.Path
import java.time.{Instant, ZonedDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

/**
 * Records deployment events to DEPLOY-TARGETS.md audit trail.
 * 
 * Updates deployment history table and rollback events in tracking document.
 */
class DeploymentRecorder(deployTargetsFile: Path) {
  
  private val isoFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
  
  /**
   * Record successful deployment
   */
  def recordDeployment(
    environment: String,
    version: String,
    deployedBy: String,
    durationSeconds: Long,
    approvals: List[String] = List.empty
  ): Unit = {
    if (!os.exists(deployTargetsFile)) {
      println(s"Warning: Deploy targets file not found: $deployTargetsFile")
      return
    }
    
    val content = os.read(deployTargetsFile)
    val timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(isoFormatter)
    val duration = formatDuration(durationSeconds)
    val approvalsStr = if (approvals.nonEmpty) {
      approvals.map(url => s"[✓]($url)").mkString(", ")
    } else {
      "N/A"
    }
    
    // Find deployment history section for environment
    val sectionPattern = s"(?s)### $environment.*?#### Deployment History.*?\\| Version.*?\\n(.*?)(?=###|$$)".r
    
    sectionPattern.findFirstMatchIn(content) match {
      case Some(m) =>
        val tableContent = m.group(1)
        
        // Create new row
        val newRow = s"| `$version` | $timestamp | $deployedBy | ✅ Success | $duration | N/A | $approvalsStr |"
        
        // Insert after header row (2nd line)
        val lines = tableContent.split("\n")
        val headerRow = lines.headOption.getOrElse("")
        val separatorRow = lines.drop(1).headOption.getOrElse("")
        val dataRows = lines.drop(2)
        
        val updatedTable = (List(headerRow, separatorRow, newRow) ++ dataRows).mkString("\n")
        
        val updated = content.replace(tableContent, updatedTable)
        os.write.over(deployTargetsFile, updated)
        
        println(s"✅ Recorded deployment: $environment@$version")
        
      case None =>
        println(s"Warning: Could not find deployment history section for $environment")
    }
  }
  
  /**
   * Record failed deployment
   */
  def recordFailure(
    environment: String,
    version: String,
    deployedBy: String,
    durationSeconds: Long,
    reason: String
  ): Unit = {
    if (!os.exists(deployTargetsFile)) {
      println(s"Warning: Deploy targets file not found: $deployTargetsFile")
      return
    }
    
    val content = os.read(deployTargetsFile)
    val timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(isoFormatter)
    val duration = formatDuration(durationSeconds)
    val truncatedReason = if (reason.length > 100) reason.take(100) + "..." else reason
    
    // Find deployment history section for environment
    val sectionPattern = s"(?s)### $environment.*?#### Deployment History.*?\\| Version.*?\\n(.*?)(?=###|$$)".r
    
    sectionPattern.findFirstMatchIn(content) match {
      case Some(m) =>
        val tableContent = m.group(1)
        
        // Create new row
        val newRow = s"| `$version` | $timestamp | $deployedBy | ❌ Failed | $duration | $truncatedReason | N/A |"
        
        // Insert after header row
        val lines = tableContent.split("\n")
        val headerRow = lines.headOption.getOrElse("")
        val separatorRow = lines.drop(1).headOption.getOrElse("")
        val dataRows = lines.drop(2)
        
        val updatedTable = (List(headerRow, separatorRow, newRow) ++ dataRows).mkString("\n")
        
        val updated = content.replace(tableContent, updatedTable)
        os.write.over(deployTargetsFile, updated)
        
        println(s"❌ Recorded failure: $environment@$version - $truncatedReason")
        
      case None =>
        println(s"Warning: Could not find deployment history section for $environment")
    }
  }
  
  /**
   * Record rollback event
   */
  def recordRollback(
    environment: String,
    failedVersion: String,
    rolledBackTo: String,
    durationSeconds: Long,
    reason: String
  ): Unit = {
    if (!os.exists(deployTargetsFile)) {
      println(s"Warning: Deploy targets file not found: $deployTargetsFile")
      return
    }
    
    val content = os.read(deployTargetsFile)
    val timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(isoFormatter)
    val duration = formatDuration(durationSeconds)
    val truncatedReason = if (reason.length > 100) reason.take(100) + "..." else reason
    
    // Find rollback events section for environment
    val sectionPattern = s"(?s)### $environment.*?#### Rollback Events.*?\\| Failed Version.*?\\n(.*?)(?=###|$$)".r
    
    sectionPattern.findFirstMatchIn(content) match {
      case Some(m) =>
        val tableContent = m.group(1)
        
        // Create new row
        val newRow = s"| $timestamp | `$failedVersion` | `$rolledBackTo` | $duration | $truncatedReason |"
        
        // Insert after header row
        val lines = tableContent.split("\n")
        val headerRow = lines.headOption.getOrElse("")
        val separatorRow = lines.drop(1).headOption.getOrElse("")
        val dataRows = lines.drop(2)
        
        val updatedTable = (List(headerRow, separatorRow, newRow) ++ dataRows).mkString("\n")
        
        val updated = content.replace(tableContent, updatedTable)
        os.write.over(deployTargetsFile, updated)
        
        println(s"⚠️  Recorded rollback: $environment $failedVersion → $rolledBackTo")
        
      case None =>
        println(s"Warning: Could not find rollback events section for $environment")
    }
  }
  
  /**
   * Update current version in target configuration
   */
  def updateCurrentVersion(environment: String, version: String): Unit = {
    if (!os.exists(deployTargetsFile)) {
      println(s"Warning: Deploy targets file not found: $deployTargetsFile")
      return
    }
    
    val content = os.read(deployTargetsFile)
    
    // Find the environment section and update Current Version field
    val currentVersionPattern = s"(?s)(### $environment.*?\\*\\*Current Version\\*\\*: )`[^`]+`".r
    
    val updated = currentVersionPattern.replaceFirstIn(content, s"$$1`$version`")
    
    if (updated != content) {
      os.write.over(deployTargetsFile, updated)
      println(s"📝 Updated current version: $environment → $version")
    } else {
      println(s"Warning: Could not find current version field for $environment")
    }
  }
  
  /**
   * Update deployment status (Pending/In Progress/Deployed)
   */
  def updateStatus(environment: String, status: String): Unit = {
    if (!os.exists(deployTargetsFile)) {
      println(s"Warning: Deploy targets file not found: $deployTargetsFile")
      return
    }
    
    val content = os.read(deployTargetsFile)
    
    // Find the environment section and update Status field
    val statusPattern = s"(?s)(### $environment.*?\\*\\*Status\\*\\*: )([^\\n]+)".r
    
    val statusEmoji = status match {
      case "Pending" => "🟡"
      case "In Progress" => "🔵"
      case "Deployed" => "🟢"
      case "Failed" => "🔴"
      case _ => "⚪"
    }
    
    val updated = statusPattern.replaceFirstIn(content, s"$$1$statusEmoji $status")
    
    if (updated != content) {
      os.write.over(deployTargetsFile, updated)
      println(s"📝 Updated status: $environment → $status")
    } else {
      println(s"Warning: Could not find status field for $environment")
    }
  }
  
  /**
   * Update last deployed timestamp
   */
  def updateLastDeployed(environment: String): Unit = {
    if (!os.exists(deployTargetsFile)) {
      println(s"Warning: Deploy targets file not found: $deployTargetsFile")
      return
    }
    
    val content = os.read(deployTargetsFile)
    val timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(isoFormatter)
    
    // Find the environment section and update Last Deployed field
    val lastDeployedPattern = s"(?s)(### $environment.*?\\*\\*Last Deployed\\*\\*: )[^\\n]+".r
    
    val updated = lastDeployedPattern.replaceFirstIn(content, s"$$1$timestamp")
    
    if (updated != content) {
      os.write.over(deployTargetsFile, updated)
      println(s"📝 Updated last deployed: $environment → $timestamp")
    } else {
      println(s"Warning: Could not find last deployed field for $environment")
    }
  }
  
  /**
   * Complete deployment record (updates version, status, timestamp, adds history)
   */
  def recordCompleteDeployment(
    environment: String,
    version: String,
    deployedBy: String,
    durationSeconds: Long,
    approvals: List[String] = List.empty
  ): Unit = {
    updateCurrentVersion(environment, version)
    updateStatus(environment, "Deployed")
    updateLastDeployed(environment)
    recordDeployment(environment, version, deployedBy, durationSeconds, approvals)
  }
  
  /**
   * Complete failure record (updates status, adds history)
   */
  def recordCompleteFailure(
    environment: String,
    version: String,
    deployedBy: String,
    durationSeconds: Long,
    reason: String
  ): Unit = {
    updateStatus(environment, "Failed")
    recordFailure(environment, version, deployedBy, durationSeconds, reason)
  }
  
  /**
   * Complete rollback record (updates version, status, adds rollback event)
   */
  def recordCompleteRollback(
    environment: String,
    failedVersion: String,
    rolledBackTo: String,
    durationSeconds: Long,
    reason: String
  ): Unit = {
    updateCurrentVersion(environment, rolledBackTo)
    updateStatus(environment, "Deployed")
    updateLastDeployed(environment)
    recordRollback(environment, failedVersion, rolledBackTo, durationSeconds, reason)
  }
  
  // ==================== HELPER METHODS ====================
  
  /**
   * Format duration in seconds to human-readable string
   */
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
  
  /**
   * Get deployment statistics for environment
   */
  def getStats(environment: String): DeploymentStats = {
    if (!os.exists(deployTargetsFile)) {
      return DeploymentStats(0, 0, 0, 0.0)
    }
    
    val content = os.read(deployTargetsFile)
    
    // Parse deployment history
    val sectionPattern = s"(?s)### $environment.*?#### Deployment History.*?\\| Version.*?\\n(.*?)(?=###|$$)".r
    
    sectionPattern.findFirstMatchIn(content) match {
      case Some(m) =>
        val tableContent = m.group(1)
        val rows = tableContent.split("\n").drop(2).filter(_.trim.startsWith("|"))
        
        val total = rows.length
        val successful = rows.count(_.contains("✅ Success"))
        val failed = rows.count(_.contains("❌ Failed"))
        val successRate = if (total > 0) (successful.toDouble / total) * 100 else 0.0
        
        DeploymentStats(total, successful, failed, successRate)
        
      case None =>
        DeploymentStats(0, 0, 0, 0.0)
    }
  }
}

/**
 * Deployment statistics for an environment
 */
case class DeploymentStats(
  total: Int,
  successful: Int,
  failed: Int,
  successRate: Double
)
