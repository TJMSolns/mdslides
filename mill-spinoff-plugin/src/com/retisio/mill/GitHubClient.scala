package com.retisio.mill

import os.Path

/**
 * GitHub API client for repository operations.
 *
 * Handles:
 * - Repository creation
 * - Branch protection
 * - Team permissions
 * - Initial code push
 *
 * Requires: GITHUB_TOKEN environment variable with `repo` and `admin:org` scopes
 *
 * @param token GitHub personal access token
 */
class GitHubClient(token: String) {

  import ujson._
  import requests._

  private val apiBase = "https://api.github.com"
  private val headers = Map(
    "Authorization" -> s"Bearer ${token}",
    "Accept" -> "application/vnd.github+json",
    "X-GitHub-Api-Version" -> "2022-11-28"
  )

  /**
   * Create a new repository in the target organization.
   *
   * @param org GitHub organization (e.g., "RETISIO")
   * @param name Repository name (e.g., "invoice-service")
   * @param description Repository description
   * @param isPrivate Whether repository is private (default: true)
   * @return Repository clone URL (HTTPS)
   */
  def createRepository(
    org: String,
    name: String,
    description: String,
    isPrivate: Boolean = true
  ): String = {
    val url = s"${apiBase}/orgs/${org}/repos"
    
    val payload = ujson.Obj(
      "name" -> name,
      "description" -> description,
      "private" -> isPrivate,
      "has_issues" -> true,
      "has_projects" -> false,
      "has_wiki" -> false,
      "auto_init" -> false, // We'll push initial code ourselves
      "allow_squash_merge" -> true,
      "allow_merge_commit" -> false,
      "allow_rebase_merge" -> false,
      "delete_branch_on_merge" -> true
    )

    try {
      val response = post(
        url,
        headers = headers,
        data = payload.toString()
      )

      if (response.statusCode == 201) {
        val json = ujson.read(response.text())
        json("clone_url").str
      } else {
        throw new Exception(s"Failed to create repository: ${response.statusCode} ${response.text()}")
      }
    } catch {
      case e: requests.RequestFailedException =>
        if (e.response.statusCode == 422) {
          throw new Exception(s"Repository ${org}/${name} already exists or name is invalid")
        } else {
          throw new Exception(s"GitHub API error: ${e.response.statusCode} ${e.response.text()}")
        }
    }
  }

  /**
   * Configure repository settings: branch protection, team permissions.
   *
   * @param org GitHub organization
   * @param repoName Repository name
   */
  def configureRepository(org: String, repoName: String): Unit = {
    // Configure branch protection for main branch
    configureBranchProtection(org, repoName, "main")
    
    // Add development team with write access (if team exists)
    addTeamPermission(org, repoName, "development", "push")
    
    // Add security team with admin access (if team exists)
    addTeamPermission(org, repoName, "security", "admin")
  }

  /**
   * Configure branch protection rules for main branch.
   *
   * Rules:
   * - Require pull request reviews (1 approval)
   * - Require status checks to pass
   * - Require branches to be up to date
   * - No force pushes
   * - No deletions
   *
   * @param org GitHub organization
   * @param repoName Repository name
   * @param branch Branch name (default: "main")
   */
  private def configureBranchProtection(org: String, repoName: String, branch: String): Unit = {
    val url = s"${apiBase}/repos/${org}/${repoName}/branches/${branch}/protection"
    
    val payload = ujson.Obj(
      "required_status_checks" -> ujson.Obj(
        "strict" -> true,
        "contexts" -> ujson.Arr("build", "test", "lint")
      ),
      "enforce_admins" -> false,
      "required_pull_request_reviews" -> ujson.Obj(
        "dismiss_stale_reviews" -> true,
        "require_code_owner_reviews" -> true,
        "required_approving_review_count" -> 1
      ),
      "restrictions" -> ujson.Null,
      "required_linear_history" -> true,
      "allow_force_pushes" -> false,
      "allow_deletions" -> false
    )

    try {
      val response = put(
        url,
        headers = headers,
        data = payload.toString()
      )

      if (response.statusCode != 200) {
        println(s"Warning: Failed to configure branch protection: ${response.statusCode} ${response.text()}")
      }
    } catch {
      case e: Exception =>
        println(s"Warning: Failed to configure branch protection: ${e.getMessage}")
    }
  }

  /**
   * Add team permission to repository.
   *
   * @param org GitHub organization
   * @param repoName Repository name
   * @param teamSlug Team slug (e.g., "development")
   * @param permission Permission level: "pull", "push", "admin"
   */
  private def addTeamPermission(org: String, repoName: String, teamSlug: String, permission: String): Unit = {
    val url = s"${apiBase}/orgs/${org}/teams/${teamSlug}/repos/${org}/${repoName}"
    
    val payload = ujson.Obj(
      "permission" -> permission
    )

    try {
      val response = put(
        url,
        headers = headers,
        data = payload.toString()
      )

      if (response.statusCode != 204) {
        println(s"Warning: Failed to add team ${teamSlug} permission: ${response.statusCode}")
      }
    } catch {
      case e: Exception =>
        println(s"Warning: Failed to add team ${teamSlug} permission: ${e.getMessage}")
    }
  }

  /**
   * Push initial code to new repository.
   *
   * Steps:
   * 1. Initialize git repository
   * 2. Add all files
   * 3. Create initial commit
   * 4. Add remote origin
   * 5. Push to main branch
   *
   * @param localPath Path to local code to push
   * @param repoUrl Repository clone URL (HTTPS)
   */
  def pushInitialCode(localPath: Path, repoUrl: String): Unit = {
    import sys.process._

    val repoUrlWithAuth = repoUrl.replace("https://", s"https://${token}@")

    // Initialize git repository
    Process(Seq("git", "init"), localPath.toIO).!!
    
    // Add all files
    Process(Seq("git", "add", "."), localPath.toIO).!!
    
    // Configure git user (required for commit)
    Process(Seq("git", "config", "user.name", "Mill Spinoff Plugin"), localPath.toIO).!!
    Process(Seq("git", "config", "user.email", "noreply@retisio.com"), localPath.toIO).!!
    
    // Create initial commit
    Process(Seq("git", "commit", "-m", "Initial spinoff from training repository"), localPath.toIO).!!
    
    // Rename default branch to main (if not already)
    try {
      Process(Seq("git", "branch", "-M", "main"), localPath.toIO).!!
    } catch {
      case _: Exception => // Branch already named main
    }
    
    // Add remote origin
    Process(Seq("git", "remote", "add", "origin", repoUrlWithAuth), localPath.toIO).!!
    
    // Push to main branch
    Process(Seq("git", "push", "-u", "origin", "main"), localPath.toIO).!!
  }
}
