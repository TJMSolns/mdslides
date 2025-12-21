package com.tjmsolutions.mdslides.infrastructure.parser

import com.tjmsolutions.mdslides.domain.{Slide, SlideDeck, SlideId}
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits.*

/**
 * Markdown parser for MDSlides format.
 *
 * Parses markdown files with slide delimiters (---) and YAML-like frontmatter
 * to extract slide structure and content.
 *
 * Format:
 * ```markdown
 * ---
 * template: title
 * ---
 * # My Title
 * ## Subtitle
 * Author Name
 *
 * ---
 * template: content
 * ---
 * ## Heading
 * Body content here
 * ```
 *
 * Current Implementation (MVP):
 * - Parses slide delimiters (---)
 * - Extracts template type from frontmatter
 * - Extracts slot content based on template type
 * - Does NOT render markdown formatting (preserved as-is)
 *
 * Future Enhancements (US-003):
 * - Full markdown rendering (bold, italic, links, etc.)
 * - Code blocks
 * - Images
 * - Lists
 *
 * Related Governance:
 * - ADR-006: Rendering Architecture
 * - ADR-007: Anticorruption Layer for Flexmark
 */
object MarkdownParser:

  /**
   * Parse markdown content into a SlideDeck.
   *
   * @param markdown The markdown content
   * @return Right(deck) if parsing succeeds, Left(error) otherwise
   */
  def parse(markdown: String): Either[String, SlideDeck] =
    // Split by double --- delimiter (slide separators)
    // Format: ---\nfrontmatter\n---\ncontent\n\n---\nfrontmatter\n---\ncontent
    val rawSlides = splitSlides(markdown)

    if rawSlides.isEmpty then
      return Left("No slides found in markdown (use --- to separate slides)")

    // Parse each slide
    val parsedSlides = rawSlides.zipWithIndex.map { case (rawSlide, index) =>
      parseSlide(rawSlide, index + 1)
    }

    // Collect errors or build deck
    parsedSlides.toList.sequence match
      case Left(error) => Left(error)
      case Right(slides) =>
        NonEmptyList.fromList(slides) match
          case None => Left("No valid slides found")
          case Some(nel) => Right(SlideDeck(nel))

  /**
   * Split markdown into raw slide chunks.
   *
   * Each slide has format:
   * ---
   * frontmatter
   * ---
   * content
   */
  private def splitSlides(markdown: String): List[String] =
    // Split by --- on its own line
    val parts = markdown.split("(?m)^---$").map(_.trim).filter(_.nonEmpty).toList

    // Group parts into slides: each slide is (frontmatter, content) pairs
    // Parts alternate: frontmatter1, content1, frontmatter2, content2, ...
    parts.grouped(2).map {
      case List(frontmatter, content) => s"$frontmatter\n---\n$content"
      case List(single) => single  // Last slide might only have frontmatter or content
      case Nil => ""  // Empty list (shouldn't happen but need for exhaustiveness)
      case _ => ""    // Catch-all for unexpected cases
    }.filter(_.nonEmpty).toList

  /**
   * Parse a single raw slide into a Slide.
   *
   * @param raw The raw slide content
   * @param slideNumber The slide number (1-based)
   * @return Right(slide) if parsing succeeds, Left(error) otherwise
   */
  private def parseSlide(raw: String, slideNumber: Int): Either[String, Slide] =
    // Extract frontmatter and content
    val (frontmatter, content) = extractFrontmatter(raw)

    // Get template name from frontmatter
    val templateName = frontmatter.getOrElse("template", "content")

    // Parse content based on template
    val slots = templateName match
      case "title" => parseTitleSlide(content)
      case "content" => parseContentSlide(content)
      case _ => return Left(s"Slide $slideNumber: Unknown template '$templateName'")

    // Create slide ID
    SlideId(slideNumber) match
      case Left(error) => Left(s"Slide $slideNumber: $error")
      case Right(id) => Right(Slide(id, templateName, slots))

  /**
   * Extract frontmatter from raw slide content.
   *
   * Frontmatter format:
   * ```
   * template: title
   * key: value
   * ---
   * content here
   * ```
   *
   * @param raw The raw slide content
   * @return (frontmatter map, remaining content)
   */
  private def extractFrontmatter(raw: String): (Map[String, String], String) =
    val lines = raw.split("\n")
    // Extract frontmatter (lines with : before the --- delimiter)
    val frontmatterLines = lines.takeWhile(line =>
      line.trim.nonEmpty && line.contains(":") && line.trim != "---"
    )
    // Skip frontmatter, --- delimiter, and empty lines to get content
    val contentLines = lines
      .dropWhile(line => line.trim.isEmpty || line.contains(":") || line.trim == "---")

    val frontmatter = frontmatterLines.map { line =>
      val parts = line.split(":", 2)
      if parts.length == 2 then
        Some(parts(0).trim -> parts(1).trim)
      else None
    }.flatten.toMap

    (frontmatter, contentLines.mkString("\n"))

  /**
   * Parse title slide content.
   *
   * Expected format:
   * ```
   * # Title (required)
   * ## Subtitle (optional)
   * Author (optional, plain text)
   * ```
   */
  private def parseTitleSlide(content: String): Map[String, String] =
    val lines = content.split("\n").map(_.trim).filter(_.nonEmpty)
    val slots = Map.newBuilder[String, String]

    // Extract title (# heading)
    lines.find(_.startsWith("# ")).foreach { line =>
      slots += ("title" -> line.drop(2).trim)
    }

    // Extract subtitle (## heading)
    lines.find(_.startsWith("## ")).foreach { line =>
      slots += ("subtitle" -> line.drop(3).trim)
    }

    // Extract author (any remaining non-heading line)
    lines.find(line => !line.startsWith("#") && line.nonEmpty).foreach { line =>
      slots += ("author" -> line.trim)
    }

    slots.result()

  /**
   * Parse content slide content.
   *
   * Expected format:
   * ```
   * ## Heading (required)
   * Body content (required, everything after heading)
   * ```
   */
  private def parseContentSlide(content: String): Map[String, String] =
    val lines = content.split("\n")
    val slots = Map.newBuilder[String, String]

    // Find heading (## line)
    val headingIndex = lines.indexWhere(_.trim.startsWith("## "))
    if headingIndex >= 0 then
      val heading = lines(headingIndex).trim.drop(3).trim
      slots += ("heading" -> heading)

      // Everything after heading is body
      val bodyLines = lines.drop(headingIndex + 1).dropWhile(_.trim.isEmpty)
      if bodyLines.nonEmpty then
        slots += ("body" -> bodyLines.mkString("\n"))

    slots.result()

end MarkdownParser
