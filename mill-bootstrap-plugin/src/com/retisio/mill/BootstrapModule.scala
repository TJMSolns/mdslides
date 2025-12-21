package com.retisio.mill

import mill._
import mill.api.Result
import os.Path

/**
 * Mill module for bootstrapping new projects with ceremony-based SDLC framework.
 *
 * Usage:
 * {{{
 * object bootstrap extends BootstrapModule {
 *   def targetOrg = T { "RETISIO" }
 * }
 * }}}
 *
 * Commands:
 * - `mill bootstrap.validate <project-name>` - Validate prerequisites
 * - `mill bootstrap.execute <project-name> <description>` - Bootstrap new project
 * - `mill bootstrap.list` - List recently bootstrapped projects
 */
trait BootstrapModule extends Module {

  /**
   * Target GitHub organization (default: "RETISIO").
   * Override in build.sc to change default.
   */
  def targetOrg: T[String] = T { "RETISIO" }

  /**
   * GitHub Personal Access Token from environment.
   * Must have `repo` (full) and `admin:org` (read/write) scopes.
   */
  def githubToken: T[String] = T {
    sys.env.get("GITHUB_TOKEN") match {
      case Some(token) if token.nonEmpty => token
      case _ => throw new Exception(
        "GITHUB_TOKEN environment variable not set. " +
        "Create token at https://github.com/settings/tokens/new with scopes: repo (full), admin:org (read/write)"
      )
    }
  }

  /**
   * Path to source repository (copilot-training).
   * Default: current working directory.
   */
  def sourceRepoPath: T[Path] = T { os.pwd }

  /**
   * Validate prerequisites for bootstrapping.
   *
   * @param projectName Name of project (kebab-case, 3-50 characters)
   * @param args Optional: --org <organization>
   * @return Result indicating success (exit 0) or failure (exit 1)
   */
  def bootstrapValidate(projectName: String, args: String*): Command[Unit] = T.command {
    val org = parseOrg(args)
    val token = githubToken()
    val sourcePath = sourceRepoPath()

    println(s"✅ Bootstrap Validation: $projectName\n")
    
    val validator = new BootstrapValidator(token, org, sourcePath)
    val results = validator.runAllChecks(projectName)
    
    println("Validation Results:")
    results.foreach { case (check, result) =>
      val icon = if (result.passed) "✅" else "❌"
      println(s"$icon Check ${check.number}: ${check.description}")
      if (!result.passed) {
        println(s"   Error: ${result.error.getOrElse("Unknown error")}")
      }
    }
    
    val allPassed = results.values.forall(_.passed)
    val passedCount = results.values.count(_.passed)
    val totalCount = results.size
    
    println(s"\nResult: ${if (allPassed) "PASS" else "FAIL"} ($passedCount/$totalCount checks passed)")
    
    if (allPassed) {
      println("Ready to bootstrap!")
      Result.Success(())
    } else {
      println("Cannot proceed with bootstrap.")
      Result.Failure("Validation failed")
    }
  }

  /**
   * Execute bootstrap - create new service with complete framework.
   *
   * Default: Creates service in services/ subdirectory of current project
   * Custom path: --path /path/to/location
   * GitHub repo: --create-repo (creates new) or --repo <url> (uses existing)
   *
   * Examples:
   *   mill bootstrap.execute my-service "My Service"
   *   mill bootstrap.execute my-service "..." --path /home/dev/my-service
   *   mill bootstrap.execute my-service "..." --create-repo --org RETISIO
   *   mill bootstrap.execute my-service "..." --repo https://github.com/org/repo
   *
   * @param projectName Name of project (kebab-case, 3-50 characters)
   * @param description Short project description
   * @param args Optional: --path, --create-repo, --repo, --org
   * @return Result indicating success or failure
   */
  def bootstrapExecute(projectName: String, description: String, args: String*): Command[Unit] = T.command {
    val customPath = parseArgValue(args, "--path")
    val createRepo = args.contains("--create-repo")
    val existingRepo = parseArgValue(args, "--repo")
    val org = parseOrg(args)
    val sourcePath = sourceRepoPath()

    println(s"🚀 Bootstrap Execute: $projectName\n")
    
    val startTime = System.currentTimeMillis()
    
    // Determine target path
    val targetPath = customPath match {
      case Some(path) => 
        println(s"Target: Custom path $path")
        os.Path(path, os.pwd)
      case None =>
        val servicesPath = os.pwd / "services" / projectName
        println(s"Target: ${servicesPath.relativeTo(os.pwd)}")
        servicesPath
    }
    
    if (os.exists(targetPath)) {
      println(s"  ❌ Path already exists: $targetPath")
      return Result.Failure("Target path already exists")
    }
    
    try {
      // Step 1: Determine if we need GitHub operations
      val needsGitHub = createRepo || existingRepo.isDefined
      val token = if (needsGitHub) {
        try { githubToken() } catch {
          case e: Exception => 
            println(s"  ⚠️  Warning: ${e.getMessage}")
            println("  Continuing without GitHub integration...")
            ""
        }
      } else ""
      
      var repoUrl: Option[String] = None
      
      if (createRepo && token.nonEmpty) {
        // Step 2a: Create GitHub repository
        println(s"\n[1/7] Creating GitHub repository $org/$projectName...")
        val githubClient = new GitHubClient(token)
        repoUrl = Some(githubClient.createRepository(org, projectName, description, isPrivate = true))
        println(s"  ✅ Repository created: ${repoUrl.get}")
      } else if (existingRepo.isDefined) {
        // Step 2b: Use existing repository
        println(s"\n[1/7] Using existing repository: ${existingRepo.get}")
        repoUrl = existingRepo
      } else {
        println("\n[1/7] Creating local service (no GitHub repo)")
      }
      
      // Step 2: Create target directory
      println(s"\n[2/7] Creating directory structure...")
      os.makeDir.all(targetPath)
      println(s"  ✅ Created ${targetPath}")
      
      // Step 3: Copy framework files
      println("\n[3/7] Copying framework files (110 files)...")
      val copier = new FrameworkCopier(sourcePath, targetPath)
      val filesCopied = copier.copyFrameworkFiles()
      println(s"  ✅ $filesCopied files copied successfully")
      
      // Step 4: Generate project stubs
      println("\n[4/7] Generating project stubs (7 files)...")
      val generator = new ProjectStubGenerator(projectName, org, description, targetPath)
      generator.generateAllStubs()
      println("  ✅ Stubs generated")
      
      // Step 5: Vendor Mill plugins if not in training repo
      val isInTrainingRepo = os.exists(sourcePath / "mill-bootstrap-plugin")
      if (!isInTrainingRepo) {
        println("\n[5/7] Vendoring Mill plugins (self-contained setup)...")
        vendorPlugins(sourcePath, targetPath)
        println("  ✅ Plugins vendored to mill-plugins/")
      } else {
        println("\n[5/7] Skipping plugin vendoring (training repo, use $file imports)")
      }
      
      // Step 6: Initialize git if GitHub repo specified
      if (repoUrl.isDefined) {
        println("\n[6/7] Initializing git repository...")
        os.proc("git", "init").call(cwd = targetPath)
        os.proc("git", "remote", "add", "origin", repoUrl.get).call(cwd = targetPath)
        println("  ✅ Git initialized")
      } else {
        println("\n[6/7] Skipping git initialization (no GitHub repo)")
      }
      
      // Step 7: Update file references (if framework files were copied)
      println(s"\n[7/7] Updating file references (copilot-training → $projectName)...")
      if (filesCopied > 0) {
        val filesUpdated = copier.updateFileReferences(projectName)
        println(s"  ✅ $filesUpdated files updated")
      } else {
        println(s"  ⏭️  Skipped (no framework files)")
      }
      
      // Optional: Create initial commit and push
      if (repoUrl.isDefined) {
        println("\n[8/8] Creating initial commit...")
        val frameworkVersion = "v1.0.0"
        GitOperations.commit(targetPath, s"Initial bootstrap from copilot-training $frameworkVersion")
        println(s"  ✅ Committed")
        
        if (createRepo && token.nonEmpty) {
          println("\n[9/9] Pushing to GitHub and configuring...")
          GitOperations.push(targetPath)
          val githubClient = new GitHubClient(token)
          githubClient.setTopics(org, projectName, Seq("ddd", "bdd", "tdd", "ceremony-based", "mill-build"))
          println(s"  ✅ Pushed and configured")
        }
      }
      
      // Success summary
      val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
      println(s"\n✅ Bootstrap Complete!\n")
      println(s"Service created: ${targetPath}")
      repoUrl.foreach(url => println(s"GitHub: $url"))
      println("\nNext Steps:")
      if (repoUrl.isEmpty) {
        println(s"  1. Complete CHARTER.md with your service requirements")
        println(s"  2. Run Phase 1 ceremonies (Event Storming → Domain Modeling)")
        if (!isInTrainingRepo) {
          println(s"  3. Optionally create GitHub repo: cd ${targetPath.relativeTo(os.pwd)} && gh repo create")
        }
      } else {
        println(s"  1. Complete CHARTER.md (Phase 0: Program Initiation)")
        println(s"  2. Run Phase 1: Event Storming")
      }
      println(s"\nTime: $elapsedSeconds seconds")
      
      Result.Success(())
      
    } catch {
      case e: Exception =>
        println(s"\n❌ Bootstrap failed: ${e.getMessage}")
        e.printStackTrace()
        Result.Failure(s"Bootstrap failed: ${e.getMessage}")
    }
  }

  /**
   * List recently bootstrapped projects in organization.
   *
   * @param args Optional: --org <organization>, --limit <count>
   * @return Result with list of projects
   */
  def bootstrapList(args: String*): Command[Unit] = T.command {
    val org = parseOrg(args)
    val limit = parseLimit(args)
    val token = githubToken()

    println(s"📋 Recently Bootstrapped Projects ($org)\n")
    
    try {
      val githubClient = new GitHubClient(token)
      val projects = githubClient.listBootstrappedProjects(org, limit)
      
      if (projects.isEmpty) {
        println("No bootstrapped projects found.")
        println(s"Filter: topic:ceremony-based")
      } else {
        println("Repository                           Created              Description")
        println("-----------------------------------  -------------------  ---------------------------------")
        projects.foreach { project =>
          val repo = f"${project.fullName}%-35s"
          val created = f"${project.createdAt}%-19s"
          val desc = project.description.take(35)
          println(s"$repo  $created  $desc")
        }
        println(s"\nTotal: ${projects.size} projects")
        println("Filter: topic:ceremony-based")
      }
      
      Result.Success(())
      
    } catch {
      case e: Exception =>
        println(s"❌ Failed to list projects: ${e.getMessage}")
        Result.Failure(s"Failed to list projects: ${e.getMessage}")
    }
  }

  // Helper methods
  private def parseOrg(args: Seq[String]): String = {
    val orgIndex = args.indexOf("--org")
    if (orgIndex >= 0 && orgIndex + 1 < args.length) {
      args(orgIndex + 1)
    } else {
      targetOrg()
    }
  }

  private def parseLimit(args: Seq[String]): Int = {
    val limitIndex = args.indexOf("--limit")
    if (limitIndex >= 0 && limitIndex + 1 < args.length) {
      args(limitIndex + 1).toInt
    } else {
      10
    }
  }

  private def parseArgValue(args: Seq[String], flag: String): Option[String] = {
    val index = args.indexOf(flag)
    if (index >= 0 && index + 1 < args.length) {
      Some(args(index + 1))
    } else {
      None
    }
  }

  private def vendorPlugins(sourcePath: Path, targetPath: Path): Unit = {
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
      val pluginSource = sourcePath / pluginName
      if (os.exists(pluginSource)) {
        val pluginTarget = targetPluginsDir / pluginName
        os.copy(pluginSource, pluginTarget, createFolders = true, replaceExisting = true)
        
        // Clean out/ directory
        val outDir = pluginTarget / "out"
        if (os.exists(outDir)) {
          os.remove.all(outDir)
        }
        copiedCount += 1
      }
    }
    
    if (copiedCount == 0) {
      println("     ⚠️  No plugins found to vendor")
    } else {
      println(s"     Copied $copiedCount plugins")
    }
  }
}

/**
 * Case class representing a bootstrapped project.
 */
case class BootstrappedProject(
  fullName: String,
  description: String,
  createdAt: String
)
