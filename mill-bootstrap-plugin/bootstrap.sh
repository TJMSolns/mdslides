#!/usr/bin/env bash
set -e

# Manual bootstrap wrapper
# Direct invocation of bootstrap plugin without build.sc integration

PLUGIN_DIR="/home/tjm/Cloud/GitHub/copilot-training/mill-bootstrap-plugin"
SOURCE_REPO="/home/tjm/Cloud/GitHub/copilot-training"

if [ $# -lt 1 ]; then
    echo "Usage: $0 <command> [args...]"
    echo ""
    echo "Commands:"
    echo "  validate <project-name> [--org ORG]"
    echo "  execute <project-name> <description> [--org ORG]"
    echo "  list [--org ORG] [--limit N]"
    echo ""
    echo "Examples:"
    echo "  $0 validate ecommerce-platform"
    echo "  $0 execute ecommerce-platform \"Multi-tenant ecommerce\" --org Acme"
    echo "  $0 list --limit 20"
    exit 1
fi

COMMAND=$1
shift

# Create temporary Scala script
TEMP_SCRIPT=$(mktemp /tmp/bootstrap-XXXXXX.sc)
trap "rm -f $TEMP_SCRIPT" EXIT

cat > "$TEMP_SCRIPT" << 'EOF'
import $file.`mill-bootstrap-plugin`.src.com.retisio.mill.{
  BootstrapModule,
  BootstrapValidator,
  GitHubClient,
  GitOperations,
  FrameworkCopier,
  ProjectStubGenerator,
  Check1_GitHubTokenValid,
  Check2_OrganizationExists,
  Check3_RepositoryNameAvailable,
  Check4_ProjectNameConventions,
  Check5_GitConfigured,
  Check6_MillVersionValid,
  Check7_SourceRepoValid,
  ValidationResult,
  BootstrappedProject
}

import os.Path

val token = sys.env.get("GITHUB_TOKEN") match {
  case Some(t) if t.nonEmpty => t
  case _ => 
    println("Error: GITHUB_TOKEN not set")
    sys.exit(1)
}

val args = interp.watchedFiles.tail.toList.map(_.toString)
val command = args.head
val commandArgs = args.tail

command match {
  case "validate" =>
    if (commandArgs.isEmpty) {
      println("Error: project-name required")
      sys.exit(1)
    }
    val projectName = commandArgs.head
    val org = if (commandArgs.contains("--org")) {
      commandArgs(commandArgs.indexOf("--org") + 1)
    } else "RETISIO"
    
    val validator = new BootstrapValidator(token, org, Path("SOURCE_REPO_PLACEHOLDER"))
    val results = validator.runAllChecks(projectName)
    
    println(s"✅ Bootstrap Validation: $projectName\n")
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
    } else {
      println("Cannot proceed with bootstrap.")
      sys.exit(1)
    }
    
  case "list" =>
    val org = if (commandArgs.contains("--org")) {
      commandArgs(commandArgs.indexOf("--org") + 1)
    } else "RETISIO"
    
    val limit = if (commandArgs.contains("--limit")) {
      commandArgs(commandArgs.indexOf("--limit") + 1).toInt
    } else 10
    
    val client = new GitHubClient(token)
    val projects = client.listBootstrappedProjects(org, limit)
    
    println(s"📋 Recently Bootstrapped Projects ($org)\n")
    
    if (projects.isEmpty) {
      println("No bootstrapped projects found.")
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
    }
    
  case "execute" =>
    println("Execute command not yet implemented in wrapper script")
    println("Use direct Scala API or wait for build.sc integration")
    sys.exit(1)
    
  case _ =>
    println(s"Unknown command: $command")
    sys.exit(1)
}
EOF

# Replace placeholder with actual path
sed -i "s|SOURCE_REPO_PLACEHOLDER|$SOURCE_REPO|g" "$TEMP_SCRIPT"

# Run with Ammonite
cd "$PLUGIN_DIR"
amm "$TEMP_SCRIPT" "$COMMAND" "$@"
