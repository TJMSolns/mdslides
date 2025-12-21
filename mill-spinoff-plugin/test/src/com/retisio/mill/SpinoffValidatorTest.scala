package com.retisio.mill

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import java.nio.file.Paths

/**
 * Tests for SpinoffValidator.
 * 
 * Validates that all 13 pre-flight checks execute correctly.
 * Expects 1/13 checks to pass for Tenant Management scaffold.
 * 
 * This simulates: mill tenantManagement.spinoffValidate TenantManagement
 */
class SpinoffValidatorTest extends AnyFunSuite with Matchers {
  
  val servicePath = Paths.get("../services/tenant-management")
  val charterPath = Paths.get("../doc/exhibits/CHARTER-01-TENANT-MANAGEMENT.md")
  
  test("STEP 4: Validator runs all 13 checks") {
    val validator = new SpinoffValidator(
      servicePath = servicePath.toString,
      serviceName = "TenantManagement",
      charterPath = Some(charterPath.toString)
    )
    
    val results = validator.validate()
    
    results should have size 13
    println(s"✅ All 13 validation checks executed")
    
    results.foreach { r =>
      val status = if (r.passed) "✅ PASS" else "❌ FAIL"
      println(s"$status: ${r.checkName}")
      if (!r.passed && r.message.isDefined) {
        println(s"        ${r.message.get}")
      }
    }
  }
  
  test("STEP 4: Validator shows 1/13 checks passed (charter exists)") {
    val validator = new SpinoffValidator(
      servicePath = servicePath.toString,
      serviceName = "TenantManagement", 
      charterPath = Some(charterPath.toString)
    )
    
    val results = validator.validate()
    val passedCount = results.count(_.passed)
    
    println(s"Validation result: $passedCount/13 checks passed")
    
    passedCount shouldBe 1
    
    // Charter check should pass
    val charterCheck = results.find(_.checkName contains "Charter")
    charterCheck shouldBe defined
    charterCheck.get.passed shouldBe true
    
    println("✅ STEP 4 PASSED: 1/13 checks passed (expected for scaffold)")
  }
  
  test("STEP 4: Validator correctly identifies blocking issues") {
    val validator = new SpinoffValidator(
      servicePath = servicePath.toString,
      serviceName = "TenantManagement",
      charterPath = Some(charterPath.toString)
    )
    
    val results = validator.validate()
    val failedChecks = results.filter(!_.passed)
    
    failedChecks should have size 12
    
    // Expected failures for ceremony-driven scaffold
    val expectedFailures = Set(
      "domain model", "unit tests", "BDD scenarios", 
      "aggregates", "event schemas", "database migrations",
      "integration contracts", "deployment manifests",
      "security", "observability", "property tests", "CI/CD"
    )
    
    failedChecks.foreach { check =>
      val matchesExpected = expectedFailures.exists(check.checkName.toLowerCase contains _)
      matchesExpected shouldBe true
    }
    
    println("✅ All expected checks failed (ceremony work required)")
  }
}
