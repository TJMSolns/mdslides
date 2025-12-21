package com.retisio.mill

import os.Path
import java.time.Instant
import scala.util.matching.Regex

/**
 * Parses DEPLOY-TARGETS.md file to extract deployment configuration.
 * 
 * Expected format:
 * - Markdown file with sections per environment
 * - YAML-like key-value pairs for configuration
 * - Deployment history table
 * 
 * See: doc/reference/templates/DEPLOY-TARGETS-TEMPLATE.md
 */
class DeployTargetsParser(deployTargetsFile: Path) {
  
  private val content = os.read(deployTargetsFile)
  
  /**
   * Parse all deployment targets from file.
   */
  def parseTargets(): List[DeployTarget] = {
    List(
      parseTarget("local", "Local"),
      parseTarget("dev", "Dev"),
      parseTarget("staging", "Staging"),
      parseTarget("production", "Production")
    ).flatten
  }
  
  /**
   * Find specific target by name (case-insensitive).
   */
  def findTarget(name: String): Option[DeployTarget] = {
    parseTargets().find(_.name.equalsIgnoreCase(name))
  }
  
  /**
   * Parse a single deployment target section.
   */
  private def parseTarget(env: String, sectionName: String): Option[DeployTarget] = {
    // Find section header
    val sectionPattern = s"### $sectionName.*?\\n".r
    val sectionStart = sectionPattern.findFirstMatchIn(content).map(_.start)
    
    sectionStart.map { start =>
      // Extract section content (until next ### or end)
      val nextSection = content.indexOf("\n### ", start + 1)
      val sectionEnd = if (nextSection > 0) nextSection else content.length
      val section = content.substring(start, sectionEnd)
      
      DeployTarget(
        name = sectionName,
        environment = env,
        cluster = extractField(section, "Cluster"),
        namespace = extractField(section, "Namespace"),
        currentVersion = extractField(section, "Current Version"),
        desiredVersion = extractField(section, "Desired Version"),
        status = extractField(section, "Status").replaceAll("[^a-zA-Z]", "").toLowerCase,
        lastDeployed = parseLastDeployed(section),
        replicas = extractField(section, "Replicas").toIntOption.getOrElse(1),
        validationLevel = parseValidationLevel(section)
      )
    }
  }
  
  /**
   * Extract field value from section.
   * Example: "**Current Version**: `v1.2.3`" → "v1.2.3"
   */
  private def extractField(section: String, fieldName: String): String = {
    val pattern = s"\\*\\*$fieldName\\*\\*:\\s*`?([^`\\n]+)`?".r
    pattern.findFirstMatchIn(section)
      .map(_.group(1).trim)
      .getOrElse("")
  }
  
  /**
   * Parse last deployed timestamp.
   */
  private def parseLastDeployed(section: String): Option[Instant] = {
    val lastDeployedStr = extractField(section, "Last Deployed")
    if (lastDeployedStr.isEmpty || lastDeployedStr == "N/A") {
      None
    } else {
      try {
        Some(Instant.parse(lastDeployedStr))
      } catch {
        case _: Exception => None
      }
    }
  }
  
  /**
   * Parse validation level from section.
   */
  private def parseValidationLevel(section: String): ValidationLevel = {
    val levelStr = extractField(section, "Validation Level")
    ValidationLevel.fromString(levelStr)
  }
  
  /**
   * Parse deployment history table.
   */
  def parseDeploymentHistory(): List[DeploymentEvent] = {
    val tablePattern = """(?s)\| Version \| Environment.*?\n((?:\|.*?\n)+)""".r
    val table = tablePattern.findFirstMatchIn(content).map(_.group(1))
    
    table.map { rows =>
      rows.split("\n").filter(_.startsWith("|")).filterNot(_.contains("---")).flatMap { row =>
        val cols = row.split("\\|").map(_.trim).filter(_.nonEmpty)
        if (cols.length >= 6) {
          try {
            Some(DeploymentEvent(
              version = cols(0).replaceAll("`", ""),
              environment = cols(1),
              timestamp = Instant.parse(cols(2)),
              deployedBy = cols(3),
              status = cols(4).replaceAll("[^a-z_]", ""),
              duration = parseDuration(cols(5)),
              approvals = if (cols.length > 6) parseApprovals(cols(6)) else List.empty
            ))
          } catch {
            case _: Exception => None
          }
        } else {
          None
        }
      }.toList
    }.getOrElse(List.empty)
  }
  
  /**
   * Get previous successful version for environment.
   */
  def getPreviousSuccessfulVersion(environment: String): Option[String] = {
    val history = parseDeploymentHistory()
      .filter(e => e.environment == environment && e.status == "success")
      .sortBy(_.timestamp.toEpochMilli)(Ordering[Long].reverse)
    
    // Return second-to-last (skip current)
    history.drop(1).headOption.map(_.version)
  }
  
  /**
   * Parse duration string to seconds.
   * Example: "90s" → 90, "2m 30s" → 150
   */
  private def parseDuration(durationStr: String): Long = {
    val secondsPattern = """(\d+)s""".r
    val minutesPattern = """(\d+)m""".r
    
    val seconds = secondsPattern.findFirstMatchIn(durationStr).map(_.group(1).toLong).getOrElse(0L)
    val minutes = minutesPattern.findFirstMatchIn(durationStr).map(_.group(1).toLong * 60).getOrElse(0L)
    
    seconds + minutes
  }
  
  /**
   * Parse approval links.
   * Example: "[#456](https://...)" → List("https://...")
   */
  private def parseApprovals(approvalsStr: String): List[String] = {
    val linkPattern = """\[.*?\]\((.*?)\)""".r
    linkPattern.findAllMatchIn(approvalsStr).map(_.group(1)).toList
  }
}
