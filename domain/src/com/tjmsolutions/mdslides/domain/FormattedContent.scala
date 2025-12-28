package com.tjmsolutions.mdslides.domain

/**
 * Sealed trait representing a list element (ordered or unordered).
 * Used to preserve source order of mixed list types (BUG-001 fix).
 */
sealed trait ListElement

/**
 * Wrapper for an unordered list in source-ordered list sequence.
 */
case class UnorderedListElement(list: UnorderedList) extends ListElement

/**
 * Wrapper for an ordered list in source-ordered list sequence.
 */
case class OrderedListElement(list: OrderedList) extends ListElement

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
 * @param unorderedLists Bullet point lists (deprecated - use lists field)
 * @param orderedLists Numbered lists (deprecated - use lists field)
 * @param lists All lists in source order (BUG-001 fix - preserves mixed list ordering)
 */
case class FormattedContent(
  textSpans: List[TextSpan],
  links: List[Link],
  codeBlocks: List[CodeBlock] = List.empty,
  contentImages: List[ContentImage] = List.empty,
  unorderedLists: List[UnorderedList] = List.empty,
  orderedLists: List[OrderedList] = List.empty,
  lists: List[ListElement] = List.empty
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

  /**
   * Calculate the maximum nesting depth across all lists (US-003.3).
   *
   * Returns the maximum nesting depth found in any unordered or ordered list.
   * Returns 0 if no lists are present.
   */
  def maxNestingDepth: Int =
    val unorderedDepths = unorderedLists.map(_.maxNestingDepth)
    val orderedDepths = orderedLists.map(_.maxNestingDepth)
    (unorderedDepths ++ orderedDepths).maxOption.getOrElse(0)

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
 * List item containing formatted text spans and optional nested lists.
 *
 * Represents a single item in an ordered or unordered list.
 * List items can contain inline formatting (bold, italic, code).
 * List items can contain nested unordered and/or ordered lists (US-003.3).
 *
 * @param textSpans Formatted text content of the list item
 * @param nestedUnorderedLists Nested unordered lists (default empty)
 * @param nestedOrderedLists Nested ordered lists (default empty)
 */
case class ListItem(
  textSpans: List[TextSpan],
  nestedUnorderedLists: List[UnorderedList] = List.empty,
  nestedOrderedLists: List[OrderedList] = List.empty
):
  /**
   * Extract plain text from list item (including nested lists).
   */
  def plainText: String =
    val spanText = textSpans.map(_.text).mkString
    val nestedUnorderedText = nestedUnorderedLists.flatMap(_.items.map(_.plainText)).mkString(" ")
    val nestedOrderedText = nestedOrderedLists.flatMap(_.items.map(_.plainText)).mkString(" ")

    List(spanText, nestedUnorderedText, nestedOrderedText)
      .filter(_.nonEmpty)
      .mkString(" ")

  /**
   * Calculate the maximum nesting depth of this list item.
   *
   * Returns 1 for leaf items (no nested lists).
   * Returns 1 + max(child depths) for items with nested lists.
   */
  def maxNestingDepth: Int =
    val unorderedDepths = nestedUnorderedLists.map(_.maxNestingDepth)
    val orderedDepths = nestedOrderedLists.map(_.maxNestingDepth)
    val maxChildDepth = (unorderedDepths ++ orderedDepths).maxOption.getOrElse(0)

    if maxChildDepth == 0 then 1 else 1 + maxChildDepth

/**
 * Unordered list (bullet points).
 *
 * Represents markdown unordered list:
 * - Item one
 * - Item two
 *
 * Can be nested inside other lists (US-003.3).
 *
 * @param items List items in order
 */
case class UnorderedList(
  items: List[ListItem]
):
  /**
   * Calculate the maximum nesting depth of this list.
   *
   * Returns the maximum depth across all items.
   */
  def maxNestingDepth: Int =
    items.map(_.maxNestingDepth).maxOption.getOrElse(1)

/**
 * Ordered list (numbered items).
 *
 * Represents markdown ordered list:
 * 1. First item
 * 2. Second item
 *
 * Can be nested inside other lists (US-003.3).
 *
 * @param items List items in order
 */
case class OrderedList(
  items: List[ListItem]
):
  /**
   * Calculate the maximum nesting depth of this list.
   *
   * Returns the maximum depth across all items.
   */
  def maxNestingDepth: Int =
    items.map(_.maxNestingDepth).maxOption.getOrElse(1)
