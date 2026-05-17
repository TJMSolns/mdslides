import mill._
import mill.scalalib._
import mill.scalalib.publish._

/**
 * MDSlides: Ceremony-Based Framework Test Project
 *
 * A standalone CLI application for generating slide decks from Markdown DSL.
 * Demonstrates DDD + BDD + TDD ceremony-based SDLC in a non-service context.
 *
 * Status: Phase 3 - TDD Implementation
 * Build System: Mill 0.11.6
 * Scala Version: 3.3.1 LTS
 * Architecture: 3-module design (domain, infrastructure, cli)
 *
 * Module Structure:
 * - domain: Pure functional domain logic (no I/O, no side effects)
 * - infrastructure: I/O adapters (parsers, file I/O, renderers)
 * - cli: Command-line interface (wires modules together)
 *
 * Related Governance:
 * - ADR-001: Technology Stack Selection
 * - ADR-007: Pure Functional Domain Model
 * - POL-004: Property-Based Testing Requirements
 * - ADR-009: Property-Based Testing Strategy
 */

val mdSlidesVersion = "1.0.6"

// Common configuration shared across all modules
trait MDSlidesModule extends ScalaModule {
  def scalaVersion = "3.3.1"

  override def scalacOptions = Seq(
    "-encoding", "UTF-8",
    "-feature",
    "-deprecation",
    "-unchecked",
    "-Xfatal-warnings"
  )
}

/**
 * Domain Module: Pure functional domain logic
 *
 * Contains:
 * - Aggregates (Slide, SlideDeck, Template, Theme)
 * - Value objects (SlideId, ValidationError, etc.)
 * - Domain functions (validation, transformations)
 *
 * Dependencies: Cats Core only (NO Cats Effect, NO I/O)
 *
 * Related Governance:
 * - ADR-007: Pure Functional Domain Model
 * - POL-003: Pure Functional Domain
 */
object domain extends MDSlidesModule {
  override def ivyDeps = Agg(
    // Functional programming primitives (Either, NonEmptyList, etc.)
    ivy"org.typelevel::cats-core:2.10.0"
  )

  object test extends ScalaTests with TestModule.Munit {
    override def ivyDeps = Agg(
      // Test framework
      ivy"org.scalameta::munit:0.7.29",

      // Property-based testing
      ivy"org.scalameta::munit-scalacheck:0.7.29",
      ivy"org.scalacheck::scalacheck:1.17.0"
    )
  }
}

/**
 * Infrastructure Module: I/O adapters and parsers
 *
 * Contains:
 * - Markdown parser (Flexmark adapter)
 * - Theme loader (Circe JSON parser)
 * - HTML renderer (Scalatags adapter)
 * - File I/O operations
 *
 * Dependencies: Cats Effect, Flexmark, Circe, Scalatags, os-lib
 *
 * Related Governance:
 * - ADR-001: Technology Stack Selection
 * - ADR-006: Rendering Architecture
 */
object infrastructure extends MDSlidesModule {
  override def moduleDeps = Seq(domain)

  override def ivyDeps = Agg(
    // Effect system for I/O
    ivy"org.typelevel::cats-effect:3.5.4",

    // File I/O
    ivy"com.lihaoyi::os-lib:0.9.3",

    // HTML generation
    ivy"com.lihaoyi::scalatags:0.12.0",

    // Markdown parsing
    ivy"com.vladsch.flexmark:flexmark-all:0.64.8",

    // JSON parsing (themes)
    ivy"io.circe::circe-core:0.14.6",
    ivy"io.circe::circe-generic:0.14.6",
    ivy"io.circe::circe-parser:0.14.6"
  )

  object test extends ScalaTests with TestModule.Munit {
    override def ivyDeps = Agg(
      // Test framework
      ivy"org.scalameta::munit:0.7.29",

      // Test utilities for Cats Effect
      ivy"org.typelevel::munit-cats-effect:2.0.0"
    )
  }
}

/**
 * MCP Module: Model Context Protocol server (Tier 1)
 *
 * Exposes mdslides rendering pipeline as MCP tools:
 * - render_deck(input_path, output_dir, theme?, no_copy_images?) → RenderResult
 * - validate_deck(input_path) → ValidationResult
 *
 * Protocol: JSON-RPC 2.0 over stdio (standard MCP transport)
 * Architecture: file-in/file-out (stateless); see ADR-013
 *
 * Related Governance:
 * - ADR-013: MCP Server Architecture
 * - MS-012: MCP Server Tier 1 implementation
 */
object mcp extends MDSlidesModule {
  override def moduleDeps = Seq(infrastructure)

  override def ivyDeps = Agg(
    // Effect system
    ivy"org.typelevel::cats-effect:3.5.4",

    // JSON parsing/encoding for JSON-RPC 2.0
    ivy"io.circe::circe-core:0.14.6",
    ivy"io.circe::circe-generic:0.14.6",
    ivy"io.circe::circe-parser:0.14.6"
  )

  def mainClass = T { Some("com.tjmsolutions.mdslides.mcp.Main") }

  def assembly = T { super.assembly() }

  object test extends ScalaTests with TestModule.Munit {
    override def ivyDeps = Agg(
      ivy"org.scalameta::munit:0.7.29",
      ivy"org.typelevel::munit-cats-effect:2.0.0"
    )
  }
}

/**
 * CLI Module: Command-line interface
 *
 * Contains:
 * - Main entry point
 * - CLI argument parsing (Decline)
 * - Module wiring (glues domain + infrastructure)
 *
 * Dependencies: Decline, Cats Effect
 *
 * Related Governance:
 * - ADR-001: Technology Stack Selection
 */
object cli extends MDSlidesModule {
  override def moduleDeps = Seq(domain, infrastructure)

  override def ivyDeps = Agg(
    // CLI parsing
    ivy"com.monovore::decline:2.4.1",

    // Effect system
    ivy"org.typelevel::cats-effect:3.5.4"
  )

  override def generatedSources = T {
    val dir = T.dest / "generated"
    os.makeDir.all(dir)
    os.write(
      dir / "BuildInfo.scala",
      s"""package com.tjmsolutions.mdslides.cli
         |object BuildInfo { val version = "$mdSlidesVersion" }
         |""".stripMargin
    )
    Seq(PathRef(dir))
  }

  // Main entry point
  def mainClass = T { Some("com.tjmsolutions.mdslides.cli.Main") }

  // Create executable assembly (fat JAR)
  def assembly = T {
    super.assembly()
  }

  object test extends ScalaTests with TestModule.Munit {
    override def ivyDeps = Agg(
      // Test framework
      ivy"org.scalameta::munit:0.7.29",

      // Test utilities for Cats Effect
      ivy"org.typelevel::munit-cats-effect:2.0.0"
    )
  }
}
