package com.tjmsolutions.mdslides.domain

/**
 * Formatted content represents parsed markdown with structure.
 *
 * Contains:
 * - Text spans with inline formatting (bold, italic, code)
 * - Hyperlinks
 *
 * Validation uses plain text (formatting stripped) for density checks.
 *
 * Related Governance:
 * - US-003: Full Markdown Rendering
 * - PDR-001: Density Validation Limits
 * - POL-003: Pure Functional Domain
 *
 * @param textSpans Ordered list of text segments with formatting
 * @param links Hyperlinks embedded in content
 */
case class FormattedContent(
  textSpans: List[TextSpan],
  links: List[Link]
):
  /**
   * Extract plain text (strip formatting).
   *
   * Used for density validation (word count, line count).
   * Includes link text but not URLs.
   */
  def plainText: String =
    val spanText = textSpans.map(_.text).mkString
    val linkText = links.map(_.text).mkString(" ")
    if linkText.isEmpty then spanText
    else s"$spanText $linkText"

  /**
   * Count words in plain text.
   *
   * Counts visible words only (no markdown syntax, no URLs).
   */
  def wordCount: Int =
    val text = plainText.trim
    if text.isEmpty then 0
    else text.split("\\s+").length

  /**
   * Count lines in plain text.
   *
   * Counts newline-separated lines.
   */
  def lineCount: Int =
    val text = plainText
    if text.isEmpty then 0
    else text.split("\n").length

object FormattedContent:
  /**
   * Empty formatted content (no text, no links).
   */
  val empty: FormattedContent = FormattedContent(List.empty, List.empty)

/**
 * Text span with optional inline formatting.
 *
 * Represents a segment of text that may be bold, italic, or inline code.
 *
 * @param text The raw text content
 * @param bold True if text is bold (**text** or __text__)
 * @param italic True if text is italic (*text* or _text_)
 * @param code True if text is inline code (`text`)
 */
case class TextSpan(
  text: String,
  bold: Boolean,
  italic: Boolean,
  code: Boolean
)

/**
 * Hyperlink with text and URL.
 *
 * Represents markdown link: [text](url)
 *
 * @param text Link text (visible to user)
 * @param url Link target (href)
 */
case class Link(
  text: String,
  url: String
)
