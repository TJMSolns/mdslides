package com.retisio.mill

import os.Path

/**
 * Copies framework files from source repository to target repository.
 */
class FrameworkCopier(
  sourceRepoPath: Path,
  targetRepoPath: Path
) {

  /**
   * Copy all framework files from source to target.
   *
   * @return Number of files copied
   */
  def copyFrameworkFiles(): Int = {
    var filesCopied = 0

    // Copy HOW-WE-WORK.md
    copyFile(sourceRepoPath / "HOW-WE-WORK.md", targetRepoPath / "HOW-WE-WORK.md")
    filesCopied += 1

    // Copy .github/copilot-instructions*.md (14 files)
    val copilotInstructions = os.list(sourceRepoPath / ".github")
      .filter(_.last.startsWith("copilot-instructions"))
      .filter(_.last.endsWith(".md"))
    
    os.makeDir.all(targetRepoPath / ".github")
    copilotInstructions.foreach { file =>
      copyFile(file, targetRepoPath / ".github" / file.last)
      filesCopied += 1
    }

    // Copy doc/reference/SBPF/ (22 files)
    filesCopied += copyDirectory(
      sourceRepoPath / "doc" / "reference" / "SBPF",
      targetRepoPath / "doc" / "reference" / "SBPF"
    )

    // Copy doc/reference/templates/ (34 files)
    filesCopied += copyDirectory(
      sourceRepoPath / "doc" / "reference" / "templates",
      targetRepoPath / "doc" / "reference" / "templates"
    )

    // Copy doc/reference/validation/ (5 files)
    filesCopied += copyDirectory(
      sourceRepoPath / "doc" / "reference" / "validation",
      targetRepoPath / "doc" / "reference" / "validation"
    )

    // Copy mill-spinoff-plugin/ (15 files - source code for self-sufficiency)
    filesCopied += copyDirectory(
      sourceRepoPath / "mill-spinoff-plugin",
      targetRepoPath / "mill-spinoff-plugin",
      excludePatterns = Seq("out", "target", ".git")
    )

    // Copy mill-deploy-plugin/ (10 files - source code for self-sufficiency)
    filesCopied += copyDirectory(
      sourceRepoPath / "mill-deploy-plugin",
      targetRepoPath / "mill-deploy-plugin",
      excludePatterns = Seq("out", "target", ".git")
    )

    // Copy mill-bootstrap-plugin/ (this plugin - for self-sufficiency)
    filesCopied += copyDirectory(
      sourceRepoPath / "mill-bootstrap-plugin",
      targetRepoPath / "mill-bootstrap-plugin",
      excludePatterns = Seq("out", "target", ".git")
    )

    // Copy select framework ADRs and POLs
    val frameworkADRs = Seq(
      "ADR-001-use-pekko-instead-of-akka.md",
      "ADR-002-reject-spring-boot-framework.md",
      "ADR-003-reactive-postgres-over-jdbc.md",
      "ADR-056-mill-for-jvm-builds.md",
      "ADR-060-spinoff-via-mill-plugin.md"
    )
    
    os.makeDir.all(targetRepoPath / "doc" / "governance" / "ADR")
    frameworkADRs.foreach { adr =>
      val sourcePath = sourceRepoPath / "doc" / "governance" / "ADR" / adr
      if (os.exists(sourcePath)) {
        copyFile(sourcePath, targetRepoPath / "doc" / "governance" / "ADR" / adr)
        filesCopied += 1
      }
    }

    val frameworkPOLs = Seq(
      "POL-001-non-blocking-io-mandate.md",
      "POL-006-documentation-as-code.md",
      "POL-007-ubiquitous-language-enforcement.md",
      // Newly added policy to support sprint retrospective action item limits (POL-036)
      "POL-036-sprint-retrospective-action-item-limit.md"
    )
    
    os.makeDir.all(targetRepoPath / "doc" / "governance" / "POL")
    frameworkPOLs.foreach { pol =>
      val sourcePath = sourceRepoPath / "doc" / "governance" / "POL" / pol
      if (os.exists(sourcePath)) {
        copyFile(sourcePath, targetRepoPath / "doc" / "governance" / "POL" / pol)
        filesCopied += 1
      }
    }

    // Create empty directories
    os.makeDir.all(targetRepoPath / "doc" / "domain-models")
    os.makeDir.all(targetRepoPath / "doc" / "governance" / "PDR")
    os.makeDir.all(targetRepoPath / "doc" / "planning")
    // Ensure presentations area exists for Marp decks
    os.makeDir.all(targetRepoPath / "doc" / "presentations")
    os.makeDir.all(targetRepoPath / "doc" / "scenarios")
    os.makeDir.all(targetRepoPath / "services")

    filesCopied
  }

  /**
   * Update file references from "copilot-training" to project name.
   *
   * @param projectName New project name
   * @return Number of files updated
   */
  def updateFileReferences(projectName: String): Int = {
    var filesUpdated = 0

    // Files to update references in
    val filesToUpdate = Seq(
      targetRepoPath / "HOW-WE-WORK.md",
      targetRepoPath / "ARCHITECTURE.md",
      targetRepoPath / "README.md",
      targetRepoPath / "build.sc"
    ) ++ os.walk(targetRepoPath / ".github")
      .filter(_.last.endsWith(".md"))
      .filter(os.isFile)

    filesToUpdate.foreach { file =>
      if (os.exists(file)) {
        val content = os.read(file)
        val updated = content
          .replace("copilot-training", projectName)
          .replace("RETISIO/copilot-training", s"RETISIO/$projectName") // Preserve org in some contexts
        
        if (content != updated) {
          os.write.over(file, updated)
          filesUpdated += 1
        }
      }
    }

    filesUpdated
  }

  // Helper: Copy a single file
  private def copyFile(source: Path, target: Path): Unit = {
    os.makeDir.all(target / os.up)
    os.copy(source, target, replaceExisting = true)
  }

  // Helper: Copy directory recursively
  private def copyDirectory(
    source: Path,
    target: Path,
    excludePatterns: Seq[String] = Seq.empty
  ): Int = {
    var filesCopied = 0

    if (os.exists(source) && os.isDir(source)) {
      os.makeDir.all(target)
      
      os.walk(source).foreach { file =>
        val relativePath = file.relativeTo(source)
        
        // Skip excluded patterns
        val shouldExclude = excludePatterns.exists(pattern =>
          relativePath.segments.exists(_.contains(pattern))
        )
        
        if (!shouldExclude) {
          val targetFile = target / relativePath
          
          if (os.isFile(file)) {
            copyFile(file, targetFile)
            filesCopied += 1
          } else if (os.isDir(file)) {
            os.makeDir.all(targetFile)
          }
        }
      }
    }

    filesCopied
  }
}
