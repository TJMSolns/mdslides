package com.retisio.mill

import org.scalatest.funsuite.AnyFunSuite
import os.Path

/**
 * Integration test for framework file copying during spinoff.
 * 
 * Verifies that RepositoryGenerator copies all 110+ framework files
 * from copilot-training to spun-off repositories.
 */
class FrameworkFilesIntegrationTest extends AnyFunSuite {
  
  test("spinoff copies all framework files") {
    // Setup: Create mock source and target repositories
    val sourceRepo = createMockSourceRepo()
    val targetRepo = os.temp.dir()
    
    try {
      // Execute: Run RepositoryGenerator
      val generator = new RepositoryGenerator(
        targetPath = targetRepo,
        subServiceName = "TestService",
        sourceServiceName = "ParentService",
        contextMapPath = sourceRepo / "CONTEXT-MAP.md",
        sourceRepoPath = sourceRepo
      )
      generator.generate()
      
      // Assert: Framework files copied
      assertFrameworkFilesCopied(targetRepo)
      
    } finally {
      // Cleanup
      os.remove.all(sourceRepo)
      os.remove.all(targetRepo)
    }
  }
  
  test("spinoff counts framework files correctly") {
    // Setup
    val sourceRepo = createMockSourceRepo()
    val targetRepo = os.temp.dir()
    
    try {
      // Execute
      val copier = new FrameworkCopier(sourceRepo, targetRepo)
      val filesCopied = copier.copyFrameworkFiles()
      
      // Assert: Minimum expected file count
      // 1 HOW-WE-WORK.md + 14 ceremony instructions + 5 framework ADRs + 3 framework POLs
      // + templates + SBPFs + validation checklists
      assert(filesCopied >= 23, s"Expected >= 23 files in mock, got $filesCopied")
      
    } finally {
      os.remove.all(sourceRepo)
      os.remove.all(targetRepo)
    }
  }
  
  test("spinoff creates correct directory structure") {
    // Setup
    val sourceRepo = createMockSourceRepo()
    val targetRepo = os.temp.dir()
    
    try {
      // Execute
      val copier = new FrameworkCopier(sourceRepo, targetRepo)
      copier.copyFrameworkFiles()
      
      // Assert: Directories exist
      assert(os.exists(targetRepo / "doc" / "reference" / "SBPF"), "SBPF directory should exist")
      assert(os.exists(targetRepo / "doc" / "reference" / "templates"), "templates directory should exist")
      assert(os.exists(targetRepo / "doc" / "reference" / "validation"), "validation directory should exist")
      assert(os.exists(targetRepo / "doc" / "governance" / "ADR"), "ADR directory should exist")
      assert(os.exists(targetRepo / "doc" / "governance" / "POL"), "POL directory should exist")
      assert(os.exists(targetRepo / "doc" / "domain-models"), "domain-models directory should exist")
      assert(os.exists(targetRepo / "doc" / "scenarios"), "scenarios directory should exist")
      
    } finally {
      os.remove.all(sourceRepo)
      os.remove.all(targetRepo)
    }
  }
  
  // Helper: Create mock source repository with framework files
  private def createMockSourceRepo(): Path = {
    val sourceRepo = os.temp.dir()
    
    // Create HOW-WE-WORK.md
    os.write(sourceRepo / "HOW-WE-WORK.md", mockHowWeWorkContent)
    
    // Create .github with ceremony instructions
    os.makeDir.all(sourceRepo / ".github")
    ceremonyFiles.foreach { file =>
      os.write(sourceRepo / ".github" / file, s"# $file\n\nCeremony guide content...")
    }
    
    // Create doc/reference/SBPF/ with sample files
    os.makeDir.all(sourceRepo / "doc" / "reference" / "SBPF")
    Seq("SBPF-001-example.md", "SBPF-002-example.md").foreach { file =>
      os.write(sourceRepo / "doc" / "reference" / "SBPF" / file, s"# $file\n\nShared best practice...")
    }
    
    // Create doc/reference/templates/ with sample files
    os.makeDir.all(sourceRepo / "doc" / "reference" / "templates")
    Seq("AGGREGATE-TEMPLATE.md", "EVENT-STORMING-TEMPLATE.md", "FEATURE-TEMPLATE.feature").foreach { file =>
      os.write(sourceRepo / "doc" / "reference" / "templates" / file, s"# $file\n\nTemplate content...")
    }
    
    // Create doc/reference/validation/ with sample files
    os.makeDir.all(sourceRepo / "doc" / "reference" / "validation")
    Seq("PHASE1-VALIDATION.md", "PHASE2-VALIDATION.md").foreach { file =>
      os.write(sourceRepo / "doc" / "reference" / "validation" / file, s"# $file\n\nValidation checklist...")
    }
    
    // Create doc/governance/ADR with framework ADRs
    os.makeDir.all(sourceRepo / "doc" / "governance" / "ADR")
    frameworkADRs.foreach { adr =>
      os.write(sourceRepo / "doc" / "governance" / "ADR" / adr, s"# $adr\n\nArchitecture decision...")
    }
    
    // Create doc/governance/POL with framework POLs
    os.makeDir.all(sourceRepo / "doc" / "governance" / "POL")
    frameworkPOLs.foreach { pol =>
      os.write(sourceRepo / "doc" / "governance" / "POL" / pol, s"# $pol\n\nPolicy document...")
    }
    
    // Create mill plugins (required by FrameworkCopier)
    os.makeDir.all(sourceRepo / "mill-spinoff-plugin" / "src")
    os.write(sourceRepo / "mill-spinoff-plugin" / "build.sc", "// Build config")
    
    os.makeDir.all(sourceRepo / "mill-deploy-plugin" / "src")
    os.write(sourceRepo / "mill-deploy-plugin" / "build.sc", "// Build config")
    
    os.makeDir.all(sourceRepo / "mill-bootstrap-plugin" / "src")
    os.write(sourceRepo / "mill-bootstrap-plugin" / "build.sc", "// Build config")
    
    sourceRepo
  }
  
  // Helper: Assert framework files copied correctly
  private def assertFrameworkFilesCopied(targetRepo: Path): Unit = {
    // Check HOW-WE-WORK.md
    assert(os.exists(targetRepo / "HOW-WE-WORK.md"), "HOW-WE-WORK.md should be copied")
    
    // Check ceremony instruction files
    ceremonyFiles.foreach { file =>
      assert(
        os.exists(targetRepo / ".github" / file),
        s"Ceremony file should be copied: $file"
      )
    }
    
    // Check SBPF files
    assert(os.exists(targetRepo / "doc" / "reference" / "SBPF"), "SBPF directory should exist")
    val sbpfFiles = os.list(targetRepo / "doc" / "reference" / "SBPF")
    assert(sbpfFiles.nonEmpty, "SBPF directory should contain files")
    
    // Check template files
    assert(os.exists(targetRepo / "doc" / "reference" / "templates"), "templates directory should exist")
    val templateFiles = os.list(targetRepo / "doc" / "reference" / "templates")
    assert(templateFiles.nonEmpty, "templates directory should contain files")
    
    // Check validation files
    assert(os.exists(targetRepo / "doc" / "reference" / "validation"), "validation directory should exist")
    val validationFiles = os.list(targetRepo / "doc" / "reference" / "validation")
    assert(validationFiles.nonEmpty, "validation directory should contain files")
    
    // Check framework ADRs
    frameworkADRs.foreach { adr =>
      assert(
        os.exists(targetRepo / "doc" / "governance" / "ADR" / adr),
        s"Framework ADR should be copied: $adr"
      )
    }
    
    // Check framework POLs
    frameworkPOLs.foreach { pol =>
      assert(
        os.exists(targetRepo / "doc" / "governance" / "POL" / pol),
        s"Framework POL should be copied: $pol"
      )
    }
    
    // Check empty directories created
    assert(os.exists(targetRepo / "doc" / "domain-models"), "domain-models directory should exist")
    assert(os.exists(targetRepo / "doc" / "governance" / "PDR"), "PDR directory should exist")
    assert(os.exists(targetRepo / "doc" / "scenarios"), "scenarios directory should exist")
  }
  
  // Test data: Ceremony instruction files (14 files)
  private val ceremonyFiles = Seq(
    "copilot-instructions-phase0-program-initiation-program-manager.md",
    "copilot-instructions-phase1-event-storming-architect.md",
    "copilot-instructions-phase1-ubiquitous-language-architect.md",
    "copilot-instructions-phase1-domain-modeling-architect.md",
    "copilot-instructions-phase1-context-mapping-architect.md",
    "copilot-instructions-phase2-three-amigos-product-owner.md",
    "copilot-instructions-phase2-example-mapping-product-owner.md",
    "copilot-instructions-phase2-acceptance-criteria-review-architect.md",
    "copilot-instructions-phase3-test-first-pairing-bench-developer.md",
    "copilot-instructions-phase3-red-green-refactor-bench-developer.md",
    "copilot-instructions-phase3-property-based-testing-bench-developer.md",
    "copilot-instructions-phase4-scenario-to-test-decomposition-architect.md",
    "copilot-instructions-phase4-domain-model-retrospective-architect.md",
    "copilot-instructions-phase4-living-documentation-sync-program-manager.md"
  )
  
  // Test data: Framework ADRs (5 files)
  private val frameworkADRs = Seq(
    "ADR-001-use-pekko-instead-of-akka.md",
    "ADR-002-reject-spring-boot-framework.md",
    "ADR-003-reactive-postgres-over-jdbc.md",
    "ADR-056-mill-for-jvm-builds.md",
    "ADR-060-spinoff-via-mill-plugin.md"
  )
  
  // Test data: Framework POLs (3 files)
  private val frameworkPOLs = Seq(
    "POL-001-non-blocking-io-mandate.md",
    "POL-006-documentation-as-code.md",
    "POL-007-ubiquitous-language-enforcement.md"
  )
  
  // Mock HOW-WE-WORK.md content
  private val mockHowWeWorkContent = 
    """# HOW WE WORK
      |
      |## Ceremony-Based SDLC
      |
      |This document describes our development process...
      |
      |### Phase 1: Event Storming & Domain Modeling
      |1. Event Storming (4-8 hours)
      |2. Ubiquitous Language (2-3 hours)
      |3. Domain Modeling (4-6 hours)
      |4. Context Mapping (2-4 hours)
      |
      |### Phase 2: BDD Scenario Definition
      |1. Three Amigos (2-3 hours)
      |2. Example Mapping (1-2 hours)
      |3. Acceptance Criteria Review (1-2 hours)
      |
      |### Phase 3: Test-First Development
      |1. Test-First Pairing (ongoing)
      |2. Red-Green-Refactor (ongoing)
      |3. Property-Based Testing (ongoing)
      |
      |### Phase 4: Retrospectives & Documentation
      |1. Scenario-to-Test Decomposition (1-2 hours/sprint)
      |2. Domain Model Retrospective (1-2 hours/sprint)
      |3. Living Documentation Sync (1 hour/sprint)
      |""".stripMargin
}
