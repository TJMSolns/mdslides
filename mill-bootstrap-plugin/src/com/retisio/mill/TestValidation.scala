package com.retisio.mill

import os.Path

/**
 * Standalone test validation runner.
 * Tests project name validation without requiring full bootstrap.
 */
object TestValidation {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      println("Usage: TestValidation <project-name>")
      sys.exit(1)
    }
    
    val projectName = args(0)
    val token = sys.env.getOrElse("GITHUB_TOKEN", "fake-token-for-testing")
    val org = "RETISIO"
    val sourcePath = Path("/home/tjm/Cloud/GitHub/copilot-training")
    
    val validator = new BootstrapValidator(token, org, sourcePath)
    
    println(s"🔍 Validating project name: $projectName")
    println()
    
    // Run only Check 4 (project name conventions) for quick testing
    val results = validator.runAllChecks(projectName)
    val check4Result = results(Check4_ProjectNameConventions)
    
    if (check4Result.passed) {
      println(s"✅ PASS: '$projectName' is a valid project name")
      println("   Format: kebab-case (lowercase letters, numbers, hyphens)")
      println("   Length: 3-50 characters")
    } else {
      println(s"❌ FAIL: '$projectName' is not a valid project name")
      println(s"   Error: ${check4Result.error.get}")
      println()
      println("Valid format:")
      println("  - kebab-case (lowercase letters, numbers, hyphens)")
      println("  - Start with letter")
      println("  - 3-50 characters")
      println("  - No consecutive hyphens (--)")
      println("  - No trailing/leading hyphens")
      println()
      println("Examples:")
      println("  ✅ ecommerce-platform")
      println("  ✅ api-gateway-v2")
      println("  ✅ order-processing")
      println("  ❌ Ecommerce-Platform (uppercase)")
      println("  ❌ ecommerce_platform (underscore)")
      println("  ❌ ecommerce--platform (consecutive hyphens)")
      sys.exit(1)
    }
  }
}
