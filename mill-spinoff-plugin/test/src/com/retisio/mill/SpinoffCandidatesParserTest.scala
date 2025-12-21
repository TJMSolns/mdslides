package com.retisio.mill

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import java.nio.file.{Files, Paths}

/**
 * Tests for SpinoffCandidatesParser.
 * 
 * Validates that the parser correctly reads SPINOFF-CANDIDATES.md
 * and extracts service information.
 * 
 * This simulates: mill tenantManagement.spinoffList
 */
class SpinoffCandidatesParserTest extends AnyFunSuite with Matchers {
  
  val testCandidatesPath = Paths.get("../doc/reference/templates/SPINOFF-CANDIDATES.md")
  
  test("STEP 3: Parser reads SPINOFF-CANDIDATES.md") {
    assert(Files.exists(testCandidatesPath), 
      s"SPINOFF-CANDIDATES.md not found at $testCandidatesPath")
    
    val parser = new SpinoffCandidatesParser(testCandidatesPath.toString)
    val candidates = parser.parse()
    
    candidates should not be empty
    println(s"✅ Parsed ${candidates.length} candidates")
  }
  
  test("STEP 3: Parser finds Tenant Management candidate") {
    val parser = new SpinoffCandidatesParser(testCandidatesPath.toString)
    val candidates = parser.parse()
    
    val tenantMgmt = candidates.find(_.name == "Tenant Management")
    
    tenantMgmt shouldBe defined
    tenantMgmt.foreach { tm =>
      println(s"✅ Found Tenant Management:")
      println(s"   - Status: ${tm.status}")
      println(s"   - Readiness: ${tm.readinessPercent}%")
      println(s"   - Charter: ${tm.charterPath.getOrElse("MISSING")}")
      
      tm.charterPath shouldBe defined
    }
  }
  
  test("STEP 3: Parser extracts all candidate fields correctly") {
    val parser = new SpinoffCandidatesParser(testCandidatesPath.toString)
    val candidates = parser.parse()
    
    candidates.foreach { c =>
      c.name should not be empty
      c.status should not be empty
      c.readinessPercent should be >= 0
      c.readinessPercent should be <= 100
      c.ceremonyPhase should not be empty
      c.contextBoundary should not be empty
      
      println(s"✅ ${c.name}: ${c.readinessPercent}% ready, ${c.status}")
    }
  }
}
