package com.retisio.mill

import upickle.default._

/**
 * GitHub API client for repository operations.
 * Handles repository creation, configuration, and listing.
 */
class GitHubClient(token: String) {

  private val baseUrl = "https://api.github.com"
  private val headers = Map(
    "Authorization" -> s"Bearer $token",
    "Accept" -> "application/vnd.github+json",
    "X-GitHub-Api-Version" -> "2022-11-28"
  )

  /**
   * Create a new repository in an organization.
   *
   * @param org GitHub organization name
   * @param name Repository name
   * @param description Repository description
   * @param isPrivate Whether repository should be private
   * @return Repository URL
   */
  def createRepository(
    org: String,
    name: String,
    description: String,
    isPrivate: Boolean
  ): String = {
    val payload = ujson.Obj(
      "name" -> name,
      "description" -> description,
      "private" -> isPrivate,
      "has_issues" -> true,
      "has_projects" -> false,
      "has_wiki" -> false,
      "auto_init" -> false
    )

    val response = requests.post(
      s"$baseUrl/orgs/$org/repos",
      headers = headers,
      data = write(payload)
    )

    if (response.statusCode == 201) {
      val json = ujson.read(response.text())
      json("html_url").str
    } else {
      throw new Exception(s"Failed to create repository: ${response.statusCode} - ${response.text()}")
    }
  }

  /**
   * Configure branch protection on main branch.
   *
   * @param org GitHub organization name
   * @param repo Repository name
   * @param branch Branch name (typically "main")
   */
  def configureBranchProtection(org: String, repo: String, branch: String): Unit = {
    val payload = ujson.Obj(
      "required_status_checks" -> ujson.Null,
      "enforce_admins" -> false,
      "required_pull_request_reviews" -> ujson.Obj(
        "dismiss_stale_reviews" -> false,
        "require_code_owner_reviews" -> false,
        "required_approving_review_count" -> 1
      ),
      "restrictions" -> ujson.Null,
      "required_linear_history" -> false,
      "allow_force_pushes" -> false,
      "allow_deletions" -> false
    )

    val response = requests.put(
      s"$baseUrl/repos/$org/$repo/branches/$branch/protection",
      headers = headers,
      data = write(payload),
      check = false
    )

    if (response.statusCode != 200) {
      // Branch protection might fail if branch doesn't exist yet (no commits)
      // This is acceptable - will be enabled after first push
      println(s"    Warning: Could not enable branch protection (${response.statusCode}). Will be enabled after first push.")
    }
  }

  /**
   * Set repository topics.
   *
   * @param org GitHub organization name
   * @param repo Repository name
   * @param topics List of topics (tags)
   */
  def setTopics(org: String, repo: String, topics: Seq[String]): Unit = {
    val payload = ujson.Obj(
      "names" -> ujson.Arr(topics.map(ujson.Str): _*)
    )

    val response = requests.put(
      s"$baseUrl/repos/$org/$repo/topics",
      headers = headers + ("Accept" -> "application/vnd.github.mercy-preview+json"),
      data = write(payload)
    )

    if (response.statusCode != 200) {
      throw new Exception(s"Failed to set topics: ${response.statusCode} - ${response.text()}")
    }
  }

  /**
   * Disable wiki and projects for repository.
   *
   * @param org GitHub organization name
   * @param repo Repository name
   */
  def disableWiki(org: String, repo: String): Unit = {
    val payload = ujson.Obj(
      "has_wiki" -> false,
      "has_projects" -> false
    )

    val response = requests.patch(
      s"$baseUrl/repos/$org/$repo",
      headers = headers,
      data = write(payload)
    )

    if (response.statusCode != 200) {
      throw new Exception(s"Failed to disable wiki: ${response.statusCode} - ${response.text()}")
    }
  }

  /**
   * List recently bootstrapped projects (filtered by topic: ceremony-based).
   *
   * @param org GitHub organization name
   * @param limit Maximum number of projects to return
   * @return List of bootstrapped projects
   */
  def listBootstrappedProjects(org: String, limit: Int): Seq[BootstrappedProject] = {
    val response = requests.get(
      s"$baseUrl/orgs/$org/repos",
      headers = headers,
      params = Map(
        "type" -> "all",
        "sort" -> "created",
        "direction" -> "desc",
        "per_page" -> limit.toString
      )
    )

    if (response.statusCode == 200) {
      val json = ujson.read(response.text())
      json.arr
        .filter { repo =>
          // Filter by ceremony-based topic
          val topics = repo("topics").arr.map(_.str)
          topics.contains("ceremony-based")
        }
        .map { repo =>
          BootstrappedProject(
            fullName = repo("full_name").str,
            description = repo("description").strOpt.getOrElse("No description"),
            createdAt = repo("created_at").str.take(19).replace("T", " ")
          )
        }
        .take(limit)
        .toSeq
    } else {
      throw new Exception(s"Failed to list repositories: ${response.statusCode} - ${response.text()}")
    }
  }
}
