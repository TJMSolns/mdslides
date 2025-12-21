package com.retisio.mill

import os.Path

/**
 * Updates SPINOFF-CANDIDATES.md to mark services as complete.
 *
 * @param candidatesPath Path to SPINOFF-CANDIDATES.md
 */
class SpinoffCandidatesUpdater(candidatesPath: Path) {

  /**
   * Mark a service as complete in SPINOFF-CANDIDATES.md.
   *
   * Updates:
   * - Status: 🔴 Triggered → ✅ Complete
   * - Adds repository URL
   * - Adds completion timestamp
   *
   * @param subServiceName Name of completed service
   * @param repoUrl Repository URL
   */
  def markComplete(subServiceName: String, repoUrl: String): Unit = {
    if (!os.exists(candidatesPath)) {
      throw new Exception(s"SPINOFF-CANDIDATES.md not found: ${candidatesPath}")
    }

    val content = os.read(candidatesPath)
    val lines = content.split("\n").toList

    val updatedLines = updateServiceStatus(lines, subServiceName, repoUrl)
    val updatedContent = updatedLines.mkString("\n")

    os.write.over(candidatesPath, updatedContent)
  }

  /** Update service status in lines */
  private def updateServiceStatus(lines: List[String], serviceName: String, repoUrl: String): List[String] = {
    var inServiceSection = false
    var updated = false

    lines.map { line =>
      // Detect service section
      if (line.startsWith("###") && line.toLowerCase.contains(serviceName.toLowerCase)) {
        inServiceSection = true
        line
      } else if (line.startsWith("###")) {
        inServiceSection = false
        line
      } else if (inServiceSection && line.startsWith("- **Status**:") && !updated) {
        updated = true
        s"- **Status**: ✅ Complete"
      } else if (inServiceSection && line.startsWith("- **Repository**:") && !updated) {
        s"- **Repository**: [${serviceName}](${repoUrl})"
      } else if (inServiceSection && line.startsWith("- **Spun Off**:") && !updated) {
        val timestamp = java.time.LocalDateTime.now().toString
        s"- **Spun Off**: ${timestamp}"
      } else {
        line
      }
    }
  }
}
