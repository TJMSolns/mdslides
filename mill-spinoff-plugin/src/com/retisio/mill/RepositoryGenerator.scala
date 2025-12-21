package com.retisio.mill

import os.Path
import com.retisio.mill.templates._

/**
 * Generates service repository structure.
 *
 * Creates:
 * - Framework files (HOW-WE-WORK.md, ceremony instructions, templates, SBPFs)
 * - build.sc (Mill build configuration)
 * - .github/workflows/ (CI/CD pipelines)
 * - Dockerfile (multi-stage build)
 * - k8s/ (Kubernetes manifests)
 * - README.md (service overview)
 * - CODEOWNERS (team ownership)
 *
 * @param targetPath Path to extracted code
 * @param subServiceName Name of sub-service
 * @param sourceServiceName Name of parent service
 * @param contextMapPath Path to CONTEXT-MAP.md
 * @param sourceRepoPath Path to source repository (copilot-training)
 */
class RepositoryGenerator(
  targetPath: Path,
  subServiceName: String,
  sourceServiceName: String,
  contextMapPath: Path,
  sourceRepoPath: Path
) {

  /**
   * Generate repository structure.
   */
  def generate(): Unit = {
    // Step 1: Copy framework files (ceremony instructions, templates, SBPFs)
    println("  📋 Copying framework files...")
    val copier = new com.retisio.mill.FrameworkCopier(sourceRepoPath, targetPath)
    val filesCopied = copier.copyFrameworkFiles()
    println(s"    ✅ Copied $filesCopied framework files")
    
    // Step 2: Generate service-specific files
    println("  🏗️  Generating service scaffolds...")
    generateBuildSc()
    generateCIWorkflow()
    generateCDWorkflows()
    generateDockerfile()
    generateK8sManifests()
    generateREADME()
    generateCODEOWNERS()
    generateGitIgnore()
    println("    ✅ Generated service scaffolds")
  }

  /** Generate build.sc */
  private def generateBuildSc(): Unit = {
    val template = new BuildScTemplate(
      serviceName = subServiceName,
      contextMapPath = contextMapPath
    )
    val buildScPath = targetPath / "build.sc"
    os.write(buildScPath, template.generate(), createFolders = true)
  }

  /** Generate CI workflow */
  private def generateCIWorkflow(): Unit = {
    val template = new CIWorkflowTemplate(
      serviceName = subServiceName
    )
    val ciWorkflowPath = targetPath / ".github" / "workflows" / "ci.yml"
    os.write(ciWorkflowPath, template.generate(), createFolders = true)
  }

  /** Generate CD workflows (staging + production) */
  private def generateCDWorkflows(): Unit = {
    val stagingTemplate = new CDStagingTemplate(
      serviceName = subServiceName
    )
    val stagingWorkflowPath = targetPath / ".github" / "workflows" / "cd-staging.yml"
    os.write(stagingWorkflowPath, stagingTemplate.generate(), createFolders = true)

    val productionTemplate = new CDProductionTemplate(
      serviceName = subServiceName
    )
    val productionWorkflowPath = targetPath / ".github" / "workflows" / "cd-production.yml"
    os.write(productionWorkflowPath, productionTemplate.generate(), createFolders = true)
  }

  /** Generate Dockerfile */
  private def generateDockerfile(): Unit = {
    val template = new DockerfileTemplate(
      serviceName = subServiceName
    )
    val dockerfilePath = targetPath / "Dockerfile"
    os.write(dockerfilePath, template.generate(), createFolders = true)
  }

  /** Generate Kubernetes manifests */
  private def generateK8sManifests(): Unit = {
    val template = new K8sManifestsTemplate(
      serviceName = subServiceName
    )
    
    val deploymentPath = targetPath / "k8s" / "deployment.yaml"
    os.write(deploymentPath, template.generateDeployment(), createFolders = true)

    val servicePath = targetPath / "k8s" / "service.yaml"
    os.write(servicePath, template.generateService(), createFolders = true)

    val configMapPath = targetPath / "k8s" / "configmap.yaml"
    os.write(configMapPath, template.generateConfigMap(), createFolders = true)

    val hpaPath = targetPath / "k8s" / "hpa.yaml"
    os.write(hpaPath, template.generateHPA(), createFolders = true)
  }

  /** Generate README.md */
  private def generateREADME(): Unit = {
    val template = new READMETemplate(
      serviceName = subServiceName,
      sourceServiceName = sourceServiceName
    )
    val readmePath = targetPath / "README.md"
    os.write(readmePath, template.generate(), createFolders = true)
  }

  /** Generate CODEOWNERS */
  private def generateCODEOWNERS(): Unit = {
    val codeownersPath = targetPath / ".github" / "CODEOWNERS"
    val content = s"""# Code ownership for ${subServiceName} Service
                     |# See: https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners
                     |
                     |# Default owners for everything in the repository
                     |* @RETISIO/development
                     |
                     |# Domain model owned by architects
                     |/src/main/java/com/retisio/${subServiceName.toLowerCase}/domain/ @RETISIO/architects
                     |/src/main/scala/com/retisio/${subServiceName.toLowerCase}/domain/ @RETISIO/architects
                     |
                     |# Security-sensitive files owned by security team
                     |/doc/security/ @RETISIO/security
                     |/k8s/ @RETISIO/security @RETISIO/devops
                     |
                     |# CI/CD pipelines owned by DevOps
                     |/.github/workflows/ @RETISIO/devops
                     |/Dockerfile @RETISIO/devops
                     |""".stripMargin
    os.write(codeownersPath, content, createFolders = true)
  }

  /** Generate .gitignore */
  private def generateGitIgnore(): Unit = {
    val gitignorePath = targetPath / ".gitignore"
    val content = """# Mill
                    |out/
                    |.mill-version
                    |
                    |# IntelliJ IDEA
                    |.idea/
                    |*.iml
                    |*.ipr
                    |*.iws
                    |
                    |# Eclipse
                    |.classpath
                    |.project
                    |.settings/
                    |
                    |# Metals (Scala language server)
                    |.metals/
                    |.bloop/
                    |.bsp/
                    |
                    |# VS Code
                    |.vscode/
                    |
                    |# macOS
                    |.DS_Store
                    |
                    |# Build artifacts
                    |target/
                    |*.jar
                    |*.class
                    |
                    |# Logs
                    |*.log
                    |
                    |# Temp files
                    |*.swp
                    |*.swo
                    |*~
                    |""".stripMargin
    os.write(gitignorePath, content, createFolders = true)
  }
}
