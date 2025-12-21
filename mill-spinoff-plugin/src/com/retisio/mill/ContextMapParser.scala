package com.retisio.mill

import os.Path

/**
 * Parser for CONTEXT-MAP.md files.
 *
 * Extracts bounded context relationships:
 * - Upstream/downstream contexts
 * - Integration patterns (ACL, Conformist, OHS, PL, SK)
 * - Shared kernels
 * - Published languages
 *
 * @param contextMapPath Path to CONTEXT-MAP.md
 */
class ContextMapParser(contextMapPath: Path) {

  /**
   * Parse CONTEXT-MAP.md and extract relationships for a bounded context.
   *
   * @param contextName Name of bounded context (e.g., "Invoice")
   * @return Context relationships
   */
  def parseRelationships(contextName: String): ContextRelationships = {
    if (!os.exists(contextMapPath)) {
      return ContextRelationships(
        contextName = contextName,
        upstreamContexts = List.empty,
        downstreamContexts = List.empty,
        sharedKernels = List.empty
      )
    }

    val content = os.read(contextMapPath)
    val lines = content.split("\n")

    var upstreams = List.empty[Relationship]
    var downstreams = List.empty[Relationship]
    var sharedKernels = List.empty[String]

    var inContextSection = false
    var currentContext = ""

    for (line <- lines) {
      // Detect context section: ### 1. Billing Context
      if (line.startsWith("###") && line.toLowerCase.contains(contextName.toLowerCase)) {
        inContextSection = true
        currentContext = contextName
      } else if (line.startsWith("###")) {
        inContextSection = false
      }

      if (inContextSection) {
        // Parse upstream relationships
        if (line.contains("⬆️") || line.toLowerCase.contains("upstream")) {
          val upstream = extractRelationship(line, isUpstream = true)
          upstream.foreach(r => upstreams = upstreams :+ r)
        }
        // Parse downstream relationships
        else if (line.contains("⬇️") || line.toLowerCase.contains("downstream")) {
          val downstream = extractRelationship(line, isUpstream = false)
          downstream.foreach(r => downstreams = downstreams :+ r)
        }
        // Parse shared kernels
        else if (line.contains("🔗") || line.toLowerCase.contains("shared kernel")) {
          val sharedKernel = extractContextName(line)
          sharedKernel.foreach(sk => sharedKernels = sharedKernels :+ sk)
        }
      }
    }

    ContextRelationships(
      contextName = contextName,
      upstreamContexts = upstreams,
      downstreamContexts = downstreams,
      sharedKernels = sharedKernels
    )
  }

  /** Extract relationship from line */
  private def extractRelationship(line: String, isUpstream: Boolean): Option[Relationship] = {
    val pattern = "([A-Za-z]+)\\s+\\(([^)]+)\\)".r
    pattern.findFirstMatchIn(line).map { m =>
      val contextName = m.group(1)
      val pattern = m.group(2)
      Relationship(
        contextName = contextName,
        pattern = pattern,
        isUpstream = isUpstream
      )
    }
  }

  /** Extract context name from line */
  private def extractContextName(line: String): Option[String] = {
    val pattern = "\\*\\*([A-Za-z]+)\\*\\*".r
    pattern.findFirstMatchIn(line).map(_.group(1))
  }
}

/** Context relationships */
case class ContextRelationships(
  contextName: String,
  upstreamContexts: List[Relationship],
  downstreamContexts: List[Relationship],
  sharedKernels: List[String]
)

/** Relationship between contexts */
case class Relationship(
  contextName: String,
  pattern: String, // ACL, Conformist, OHS, PL, SK
  isUpstream: Boolean
)
