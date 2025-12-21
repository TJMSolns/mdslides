package com.retisio.mill

import munit.FunSuite

class BootstrapValidatorTest extends FunSuite {

  test("project name validation - valid kebab-case") {
    val validator = new BootstrapValidator("fake-token", "RETISIO", os.pwd)
    val result = validator.runAllChecks("ecommerce-platform")
    
    // Check 4 should pass (project name conventions)
    assert(result(Check4_ProjectNameConventions).passed, "Valid kebab-case name should pass")
  }

  test("project name validation - too short") {
    val validator = new BootstrapValidator("fake-token", "RETISIO", os.pwd)
    val result = validator.runAllChecks("ab")
    
    assert(!result(Check4_ProjectNameConventions).passed, "Name too short should fail")
    assert(result(Check4_ProjectNameConventions).error.get.contains("too short"))
  }

  test("project name validation - too long") {
    val validator = new BootstrapValidator("fake-token", "RETISIO", os.pwd)
    val result = validator.runAllChecks("a" * 51)
    
    assert(!result(Check4_ProjectNameConventions).passed, "Name too long should fail")
    assert(result(Check4_ProjectNameConventions).error.get.contains("too long"))
  }

  test("project name validation - uppercase letters") {
    val validator = new BootstrapValidator("fake-token", "RETISIO", os.pwd)
    val result = validator.runAllChecks("Ecommerce-Platform")
    
    assert(!result(Check4_ProjectNameConventions).passed, "Uppercase letters should fail")
  }

  test("project name validation - underscore") {
    val validator = new BootstrapValidator("fake-token", "RETISIO", os.pwd)
    val result = validator.runAllChecks("ecommerce_platform")
    
    assert(!result(Check4_ProjectNameConventions).passed, "Underscore should fail")
  }

  test("project name validation - consecutive hyphens") {
    val validator = new BootstrapValidator("fake-token", "RETISIO", os.pwd)
    val result = validator.runAllChecks("ecommerce--platform")
    
    assert(!result(Check4_ProjectNameConventions).passed, "Consecutive hyphens should fail")
  }

  test("project name validation - starts with number") {
    val validator = new BootstrapValidator("fake-token", "RETISIO", os.pwd)
    val result = validator.runAllChecks("123ecommerce")
    
    assert(!result(Check4_ProjectNameConventions).passed, "Starting with number should fail")
  }

  test("project name validation - trailing hyphen") {
    val validator = new BootstrapValidator("fake-token", "RETISIO", os.pwd)
    val result = validator.runAllChecks("ecommerce-")
    
    assert(!result(Check4_ProjectNameConventions).passed, "Trailing hyphen should fail")
  }
}
