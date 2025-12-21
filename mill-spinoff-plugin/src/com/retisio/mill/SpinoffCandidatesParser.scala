package com.retisio.mill

import os.Path

/**
 * Parser for SPINOFF-CANDIDATES.md files.
 *
 * Extracts spinoff candidate information:
 * - Service name
 * - Status (Not Ready, Monitoring, Triggered, Complete)
 * - Trigger conditions
 * - Aggregates count
 * - Readiness percentage
 *
 * @param candidatesPath Path to SPINOFF-CANDIDATES.md
 */
class SpinoffCandidatesParser(candidatesPath: Path) {

  /**
   * Parse SPINOFF-CANDIDATES.md and extract candidate services.
   *
   * @return List of spinoff candidates
   */
  def parse(): List[SpinoffCandidate] = {
    if (!os.exists(candidatesPath)) {
      return List.empty
    }

    val content = os.read(candidatesPath)
    val lines = content.split("\n")

    var candidates = List.empty[SpinoffCandidate]
    var currentCandidate: Option[CandidateBuilder] = None

    for (line <- lines) {
      // Detect service header: ### 1. Invoice Service
      if (line.startsWith("###") && line.contains(".")) {
        // Save previous candidate
        currentCandidate.foreach(c => candidates = candidates :+ c.build())
        
        // Start new candidate
        val name = line.split("\\.").last.trim.replace(" Service", "")
        currentCandidate = Some(CandidateBuilder(name))
      } else if (currentCandidate.isDefined) {
        val builder = currentCandidate.get
        
        // Parse status
        if (line.startsWith("- **Status**:")) {
          val status = extractStatus(line)
          currentCandidate = Some(builder.copy(status = status))
        }
        // Parse trigger conditions
        else if (line.startsWith("- **Trigger Conditions**:")) {
          val triggers = line.split(":", 2).last.trim
          currentCandidate = Some(builder.copy(triggerConditions = triggers))
        }
        // Parse aggregates
        else if (line.startsWith("- **Aggregates**:")) {
          val aggregates = extractNumber(line)
          currentCandidate = Some(builder.copy(aggregateCount = aggregates))
        }
        // Parse readiness
        else if (line.startsWith("- **Readiness**:")) {
          val readiness = extractPercentage(line)
          currentCandidate = Some(builder.copy(readinessPercentage = readiness))
        }
      }
    }

    // Save last candidate
    currentCandidate.foreach(c => candidates = candidates :+ c.build())

    candidates
  }

  /** Extract status from line */
  private def extractStatus(line: String): String = {
    if (line.contains("🟢")) "Not Ready"
    else if (line.contains("🟡")) "Monitoring"
    else if (line.contains("🔴")) "Triggered"
    else if (line.contains("✅")) "Complete"
    else "Unknown"
  }

  /** Extract number from line */
  private def extractNumber(line: String): Int = {
    val pattern = "(\\d+)".r
    pattern.findFirstIn(line).map(_.toInt).getOrElse(0)
  }

  /** Extract percentage from line */
  private def extractPercentage(line: String): String = {
    val pattern = "(\\d+)%".r
    pattern.findFirstIn(line).getOrElse("0%")
  }
}

/** Builder for spinoff candidates */
private case class CandidateBuilder(
  name: String,
  status: String = "Unknown",
  triggerConditions: String = "",
  aggregateCount: Int = 0,
  readinessPercentage: String = "0%"
) {
  def build(): SpinoffCandidate = SpinoffCandidate(
    name = name,
    status = status,
    triggerConditions = triggerConditions,
    aggregateCount = aggregateCount,
    readinessPercentage = readinessPercentage
  )
}

/** Spinoff candidate service */
case class SpinoffCandidate(
  name: String,
  status: String,
  triggerConditions: String,
  aggregateCount: Int,
  readinessPercentage: String
)
