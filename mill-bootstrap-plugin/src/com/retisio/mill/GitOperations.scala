package com.retisio.mill

import os.Path

/**
 * Git operations for repository management.
 */
object GitOperations {

  /**
   * Clone a repository.
   *
   * @param repoUrl Repository URL (https)
   * @param targetPath Local path to clone into
   */
  def clone(repoUrl: String, targetPath: Path): Unit = {
    // Convert https URL to git@ format for SSH
    val sshUrl = repoUrl
      .replace("https://github.com/", "git@github.com:")
      .replace("http://github.com/", "git@github.com:")
    
    os.proc("git", "clone", sshUrl, targetPath.toString).call()
  }

  /**
   * Create initial commit with all files.
   *
   * @param repoPath Path to repository
   * @param message Commit message
   */
  def commit(repoPath: Path, message: String): Unit = {
    // Configure git to use main as default branch
    os.proc("git", "config", "init.defaultBranch", "main")
      .call(cwd = repoPath)
    
    // Initialize if needed (for empty clones)
    if (!os.exists(repoPath / ".git")) {
      os.proc("git", "init").call(cwd = repoPath)
    }
    
    // Add all files
    os.proc("git", "add", "-A").call(cwd = repoPath)
    
    // Create commit
    os.proc("git", "commit", "-m", message).call(cwd = repoPath)
  }

  /**
   * Push commits to remote.
   *
   * @param repoPath Path to repository
   * @param branch Branch name (default: main)
   */
  def push(repoPath: Path, branch: String = "main"): Unit = {
    // Set upstream and push
    os.proc("git", "push", "-u", "origin", branch).call(cwd = repoPath)
  }
}
