package com.tjmsolutions.mdslides.domain

/**
 * Formatted content represents parsed markdown with structure.
 *
 * Contains:
 * - Text spans with inline formatting (bold, italic, code)
 * - Hyperlinks
 * - Code blocks (fenced code with optional language)
 * - Content images (informational images with alt text)
 * - Unordered lists (bullet points)
 * - Ordered lists (numbered items)
 *
 * Validation uses plain text (formatting stripped) for density checks.
 * Code blocks and images are excluded from body line/word counts (PDR-006, PDR-008).
 *
 * Related Governance:
 * - US-003: Full Markdown Rendering (inline formatting, lists)
 * - US-004: Code Block Support (fenced code blocks)
 * - US-005: Image Embedding (content images)
 * - PDR-001: Density Validation Limits
 * - PDR-006: Code Block Rendering Limits
 * - PDR-008: Image Policy
 * - POL-003: Pure Functional Domain
 *
 * @param textSpans Ordered list of text segments with formatting
 * @param links Hyperlinks embedded in content
 * @param codeBlocks Fenced code blocks with optional language hints
 * @param contentImages Content images with alt text (for accessibility)
 * @param unorderedLists Bullet point lists
 * @param orderedLists Numbered lists
 */
case class FormattedContent(
  textSpans: List[TextSpan],
  links: List[Link],
  codeBlocks: List[CodeBlock] = List.empty,
  contentImages: List[ContentImage] = List.empty,
  unorderedLists: List[UnorderedList] = List.empty,
  orderedLists: List[OrderedList] = List.empty
):
  /**
   * Extract plain text (strip formatting).
   *
   * Used for density validation (word count, line count).
   * Includes link text but not URLs.
   * Includes list item text.
   */
  def plainText: String =
    val spanText = textSpans.map(_.text).mkString
    val linkText = links.map(_.text).mkString(" ")
    val unorderedListText = unorderedLists.flatMap(_.items.map(_.plainText)).mkString(" ")
    val orderedListText = orderedLists.flatMap(_.items.map(_.plainText)).mkString(" ")

    List(spanText, linkText, unorderedListText, orderedListText)
      .filter(_.nonEmpty)
      .mkString(" ")

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

  /**
   * Check visual density for content images (PDR-008).
   *
   * Returns warning message if too many images on one slide:
   * - 1-2 images: No warning
   * - 3-4 images: Warning (high density)
   * - 5+ images: Warning (excessive density)
   *
   * @return Optional warning message (to be wrapped by validation layer)
   */
  def visualDensityWarning: Option[String] =
    if contentImages.length >= 5 then
      Some(s"Excessive visual density (${contentImages.length} images). Consider splitting across slides.")
    else if contentImages.length >= 3 then
      Some(s"High visual density (${contentImages.length} images).")
    else
      None

object FormattedContent:
  /**
   * Empty formatted content (no text, no links, no code blocks, no images, no lists).
   */
  val empty: FormattedContent = FormattedContent(
    List.empty,
    List.empty,
    List.empty,
    List.empty,
    List.empty,
    List.empty
  )

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

/**
 * List item containing formatted text spans.
 *
 * Represents a single item in an ordered or unordered list.
 * List items can contain inline formatting (bold, italic, code).
 *
 * @param textSpans Formatted text content of the list item
 */
case class ListItem(
  textSpans: List[TextSpan]
):
  /**
   * Extract plain text from list item.
   */
  def plainText: String = textSpans.map(_.text).mkString

/**
 * Unordered list (bullet points).
 *
 * Represents markdown unordered list:
 * - Item one
 * - Item two
 *
 * @param items List items in order
 */
case class UnorderedList(
  items: List[ListItem]
)

/**
 * Ordered list (numbered items).
 *
 * Represents markdown ordered list:
 * 1. First item
 * 2. Second item
 *
 * @param items List items in order
 */
case class OrderedList(
  items: List[ListItem]
)
