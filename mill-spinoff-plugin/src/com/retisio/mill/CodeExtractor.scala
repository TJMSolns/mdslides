package com.retisio.mill

import os.Path

/**
 * Extracts bounded context code from parent service.
 *
 * Extracts:
 * - Domain layer (aggregates, entities, value objects, domain events)
 * - Application layer (use cases, commands, queries)
 * - Infrastructure layer (repositories, adapters, messaging)
 * - Tests (unit, integration, BDD scenarios)
 * - Documentation (charter, domain models, API specs)
 *
 * @param serviceRoot Root directory of parent service
 * @param subServiceName Name of sub-service to extract
 */
class CodeExtractor(serviceRoot: Path, subServiceName: String) {

  /**
   * Extract bounded context code to temporary directory.
   *
   * @return Path to extracted code
   */
  def extract(): Path = {
    val targetPath = os.temp.dir(prefix = s"spinoff-${subServiceName.toLowerCase}-")

    // Extract source code
    extractSourceCode(targetPath)

    // Extract tests
    extractTests(targetPath)

    // Extract documentation
    extractDocumentation(targetPath)

    // Extract features (BDD scenarios)
    extractFeatures(targetPath)

    // Extract resources (config, migrations)
    extractResources(targetPath)

    // Vendor Mill plugins (copy for self-contained project)
    vendorMillPlugins(targetPath)

    targetPath
  }

  /** Extract source code (domain, application, infrastructure) */
  private def extractSourceCode(targetPath: Path): Unit = {
    val sourcePath = serviceRoot / "src" / "main"
    val subServicePath = sourcePath / "java" / "com" / "retisio" / subServiceName.toLowerCase

    if (os.exists(subServicePath)) {
      val targetSourcePath = targetPath / "src" / "main" / "java" / "com" / "retisio" / subServiceName.toLowerCase
      os.copy(subServicePath, targetSourcePath, createFolders = true, replaceExisting = true)
    }

    // Also check Scala sources
    val scalaSubServicePath = sourcePath / "scala" / "com" / "retisio" / subServiceName.toLowerCase
    if (os.exists(scalaSubServicePath)) {
      val targetSourcePath = targetPath / "src" / "main" / "scala" / "com" / "retisio" / subServiceName.toLowerCase
      os.copy(scalaSubServicePath, targetSourcePath, createFolders = true, replaceExisting = true)
    }
  }

  /** Extract tests */
  private def extractTests(targetPath: Path): Unit = {
    val testPath = serviceRoot / "src" / "test"
    val subServiceTestPath = testPath / "java" / "com" / "retisio" / subServiceName.toLowerCase

    if (os.exists(subServiceTestPath)) {
      val targetTestPath = targetPath / "src" / "test" / "java" / "com" / "retisio" / subServiceName.toLowerCase
      os.copy(subServiceTestPath, targetTestPath, createFolders = true, replaceExisting = true)
    }

    // Also check Scala tests
    val scalaSubServiceTestPath = testPath / "scala" / "com" / "retisio" / subServiceName.toLowerCase
    if (os.exists(scalaSubServiceTestPath)) {
      val targetTestPath = targetPath / "src" / "test" / "scala" / "com" / "retisio" / subServiceName.toLowerCase
      os.copy(scalaSubServiceTestPath, targetTestPath, createFolders = true, replaceExisting = true)
    }
  }

  /** Extract documentation */
  private def extractDocumentation(targetPath: Path): Unit = {
    val docPath = serviceRoot / "doc" / subServiceName
    if (os.exists(docPath)) {
      val targetDocPath = targetPath / "doc"
      os.copy(docPath, targetDocPath, createFolders = true, replaceExisting = true)
    }
  }

  /** Extract BDD features */
  private def extractFeatures(targetPath: Path): Unit = {
    val featuresPath = serviceRoot / "features" / subServiceName.toLowerCase
    if (os.exists(featuresPath)) {
      val targetFeaturesPath = targetPath / "features"
      os.copy(featuresPath, targetFeaturesPath, createFolders = true, replaceExisting = true)
    }
  }

  /** Extract resources (config, migrations) */
  private def extractResources(targetPath: Path): Unit = {
    val resourcesPath = serviceRoot / "src" / "main" / "resources"
    
    // Copy application.conf (if exists)
    val appConfPath = resourcesPath / "application.conf"
    if (os.exists(appConfPath)) {
      val targetAppConfPath = targetPath / "src" / "main" / "resources" / "application.conf"
      os.copy(appConfPath, targetAppConfPath, createFolders = true, replaceExisting = true)
    }

    // Copy database migrations
    val migrationsPath = resourcesPath / "db" / "migration" / subServiceName.toLowerCase
    if (os.exists(migrationsPath)) {
      val targetMigrationsPath = targetPath / "src" / "main" / "resources" / "db" / "migration"
      os.copy(migrationsPath, targetMigrationsPath, createFolders = true, replaceExisting = true)
    }

    // Copy logback.xml (if exists)
    val logbackPath = resourcesPath / "logback.xml"
    if (os.exists(logbackPath)) {
      val targetLogbackPath = targetPath / "src" / "main" / "resources" / "logback.xml"
      os.copy(logbackPath, targetLogbackPath, createFolders = true, replaceExisting = true)
    }
  }

  /**
   * Vendor Mill plugins by copying them into the spun-off project.
   * 
   * This makes the project self-contained with no external dependencies.
   * Each project gets its own copy of the plugins for:
   * - Zero authentication complexity (no GitHub Packages setup)
   * - Offline development (no network required)
   * - Version locking (each project locked to tested plugin versions)
   * - Zero IP exposure (all stays in private repos)
   */
  private def vendorMillPlugins(targetPath: Path): Unit = {
    // Determine training repo root (parent of serviceRoot)
    val trainingRoot = serviceRoot / os.up / os.up

    // List of Mill plugins to vendor
    val pluginsToVendor = Seq(
      "mill-bootstrap-plugin",
      "mill-testing-plugin",
      "mill-specification-plugin",
      "mill-domain-plugin",
      "mill-quality-plugin",
      "mill-observability-plugin",
      "mill-release-plugin"
    )

    val targetPluginsDir = targetPath / "mill-plugins"
    os.makeDir.all(targetPluginsDir)

    var copiedCount = 0
    pluginsToVendor.foreach { pluginName =>
      val pluginSource = trainingRoot / pluginName
      if (os.exists(pluginSource)) {
        val pluginTarget = targetPluginsDir / pluginName
        os.copy(pluginSource, pluginTarget, createFolders = true, replaceExisting = true)
        copiedCount += 1
        
        // Clean out/ directory from vendored plugins (don't copy build artifacts)
        val outDir = pluginTarget / "out"
        if (os.exists(outDir)) {
          os.remove.all(outDir)
        }
      }
    }

    if (copiedCount > 0) {
      println(s"   ✅ Vendored $copiedCount Mill plugins to mill-plugins/")
    } else {
      println(s"   ⚠️  No Mill plugins found to vendor (checked ${trainingRoot})")
    }
  }
}
