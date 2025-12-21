package com.retisio.mill

import com.retisio.mill.templates._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

/**
 * Tests for template generators.
 * 
 * Validates that all templates generate valid output with expected structure.
 * 
 * This validates: Template generator code quality and correctness
 */
class TemplateGeneratorsTest extends AnyFunSuite with Matchers {
  
  val serviceName = "tenant-management"
  val packageName = "com.retisio.tenant"
  val targetOrg = "RETISIO"
  
  test("STEP 5: BuildScTemplate generates valid Mill build config") {
    val output = BuildScTemplate.generate(serviceName, packageName)
    
    output should include("import mill._")
    output should include("object tenantManagement extends ScalaModule")
    output should include("def scalaVersion")
    output should include("pekko")
    
    println("✅ BuildScTemplate generates valid output")
  }
  
  test("STEP 5: CIWorkflowTemplate generates valid GitHub Actions YAML") {
    val output = CIWorkflowTemplate.generate(serviceName)
    
    output should include("name: CI")
    output should include("on:")
    output should include("push:")
    output should include("pull_request:")
    output should include("jobs:")
    output should include("runs-on:")
    output should include("mill test")
    
    println("✅ CIWorkflowTemplate generates valid output")
  }
  
  test("STEP 5: DockerfileTemplate generates valid Dockerfile") {
    val output = DockerfileTemplate.generate()
    
    output should include("FROM eclipse-temurin:21")
    output should include("WORKDIR /app")
    output should include("COPY")
    output should include("EXPOSE")
    output should include("ENTRYPOINT")
    
    println("✅ DockerfileTemplate generates valid output")
  }
  
  test("STEP 5: CDStagingTemplate generates valid CD workflow") {
    val output = CDStagingTemplate.generate(serviceName, targetOrg)
    
    output should include("name: CD - Staging")
    output should include("environment: staging")
    output should include("kubectl apply")
    output should include(serviceName)
    
    println("✅ CDStagingTemplate generates valid output")
  }
  
  test("STEP 5: CDProductionTemplate generates valid CD workflow") {
    val output = CDProductionTemplate.generate(serviceName, targetOrg)
    
    output should include("name: CD - Production")
    output should include("environment: production")
    output should include("kubectl apply")
    output should include("needs:")
    
    println("✅ CDProductionTemplate generates valid output")
  }
  
  test("STEP 5: K8sManifestsTemplate generates valid Kubernetes YAML") {
    val output = K8sManifestsTemplate.generate(serviceName, packageName)
    
    output should include("apiVersion:")
    output should include("kind: Deployment")
    output should include("kind: Service")
    output should include("kind: HorizontalPodAutoscaler")
    output should include(s"app: $serviceName")
    
    println("✅ K8sManifestsTemplate generates valid output")
  }
  
  test("STEP 5: READMETemplate generates valid README") {
    val output = READMETemplate.generate(
      serviceName, 
      packageName,
      "Tenant Management service"
    )
    
    output should include("# Tenant Management")
    output should include("## Overview")
    output should include("## Architecture")
    output should include("## Development")
    output should include("mill compile")
    output should include("mill test")
    
    println("✅ READMETemplate generates valid output")
  }
  
  test("STEP 5: SpinoffADRTemplate generates valid ADR") {
    val output = SpinoffADRTemplate.generate(serviceName, "2025-12-15")
    
    output should include("# ADR")
    output should include("Spinoff")
    output should include("## Status")
    output should include("## Context")
    output should include("## Decision")
    output should include("## Consequences")
    
    println("✅ SpinoffADRTemplate generates valid output")
  }
  
  test("STEP 5: All templates produce non-empty output") {
    val templates = Map(
      "BuildSc" -> BuildScTemplate.generate(serviceName, packageName),
      "CIWorkflow" -> CIWorkflowTemplate.generate(serviceName),
      "Dockerfile" -> DockerfileTemplate.generate(),
      "CDStaging" -> CDStagingTemplate.generate(serviceName, targetOrg),
      "CDProduction" -> CDProductionTemplate.generate(serviceName, targetOrg),
      "K8sManifests" -> K8sManifestsTemplate.generate(serviceName, packageName),
      "README" -> READMETemplate.generate(serviceName, packageName, "Test service"),
      "SpinoffADR" -> SpinoffADRTemplate.generate(serviceName, "2025-12-15")
    )
    
    templates.foreach { case (name, output) =>
      output should not be empty
      output.length should be > 100
      println(s"✅ $name: ${output.length} chars")
    }
  }
}
