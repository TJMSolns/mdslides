package com.tjmsolutions.mdslides.infrastructure.parser

import com.tjmsolutions.mdslides.domain.{FormattedContent, TextSpan, Link, CodeBlock, ContentImage, UnorderedList, OrderedList, ListItem}
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.{Node, NodeVisitor, VisitHandler}
import com.vladsch.flexmark.ast.*
import scala.jdk.CollectionConverters.*

/**
 * Anticorruption Layer for Flexmark markdown parser.
 *
 * Translates Flexmark AST to domain FormattedContent.
 * Isolates domain from external library dependencies.
 *
 * Supported inline formatting:
 * - Bold: **text** or __text__
 * - Italic: *text* or _text_
 * - Inline code: `text`
 * - Links: [text](url)
 *
 * Supported block elements:
 * - Fenced code blocks: ```language\ncode\n```
 * - Images: ![alt text](url)
 *
 * Related Governance:
 * - ADR-010: Markdown Library Selection (Flexmark)
 * - ADR-007: Anticorruption Layer
 * - US-003: Full Markdown Rendering
 * - US-004: Code Block Support
 * - US-005: Image Embedding
 * - PDR-008: Image Policy
 * - POL-003: Pure Functional Domain (this adapter keeps domain pure)
 */
object FlexmarkAdapter:

  private val parser: Parser = Parser.builder().build()

  /**
   * Parse markdown text into FormattedContent.
   *
   * Extracts inline formatting (bold, italic, code, links) and converts
   * to domain types (TextSpan, Link).
   *
   * @param markdown Raw markdown text
   * @return FormattedContent with parsed structure
   */
  def parseInlineFormatting(markdown: String): FormattedContent =
    if markdown.trim.isEmpty then
      return FormattedContent.empty

    val document = parser.parse(markdown)
    extractFormattedContent(document)

  /**
   * Extract FormattedContent from Flexmark document AST.
   *
   * Walks the AST and builds TextSpan, Link, CodeBlock, ContentImage, and List objects.
   */
  private def extractFormattedContent(node: Node): FormattedContent =
    val textSpans = scala.collection.mutable.ListBuffer.empty[TextSpan]
    val links = scala.collection.mutable.ListBuffer.empty[Link]
    val codeBlocks = scala.collection.mutable.ListBuffer.empty[CodeBlock]
    val contentImages = scala.collection.mutable.ListBuffer.empty[ContentImage]
    val unorderedLists = scala.collection.mutable.ListBuffer.empty[UnorderedList]
    val orderedLists = scala.collection.mutable.ListBuffer.empty[OrderedList]

    // Track current formatting state as we traverse
    var currentBold = false
    var currentItalic = false
    var currentCode = false

    def visitNode(n: Node): Unit =
      n match
        case text: Text =>
          // Plain text or text within formatting
          val span = TextSpan(
            text.getChars.toString,
            bold = currentBold,
            italic = currentItalic,
            code = currentCode
          )
          textSpans += span

        case strong: StrongEmphasis =>
          // Bold text: **text** or __text__
          val prevBold = currentBold
          currentBold = true
          n.getChildren.asScala.foreach(visitNode)
          currentBold = prevBold

        case emphasis: Emphasis =>
          // Italic text: *text* or _text_
          val prevItalic = currentItalic
          currentItalic = true
          n.getChildren.asScala.foreach(visitNode)
          currentItalic = prevItalic

        case code: Code =>
          // Inline code: `text`
          val span = TextSpan(
            code.getChars.toString.stripPrefix("`").stripSuffix("`"),
            bold = false,
            italic = false,
            code = true
          )
          textSpans += span

        case link: com.vladsch.flexmark.ast.Link =>
          // Link: [text](url)
          val linkText = link.getText.toString
          val linkUrl = link.getUrl.toString
          links += Link(linkText, linkUrl)

          // Also add link text as a text span for plain text extraction
          val span = TextSpan(
            linkText,
            bold = currentBold,
            italic = currentItalic,
            code = false
          )
          textSpans += span
          // Note: Don't visit children - we've already extracted the text

        case _: SoftLineBreak =>
          // Soft line break (single newline in source) -> space in output
          textSpans += TextSpan(" ", bold = false, italic = false, code = false)

        case _: HardLineBreak =>
          // Hard line break (two spaces + newline, or backslash + newline)
          textSpans += TextSpan("\n", bold = false, italic = false, code = false)

        case para: Paragraph =>
          // Paragraph - visit children
          n.getChildren.asScala.foreach(visitNode)
          // Add newline after paragraph (unless it's the last node)
          if n.getNext != null then
            textSpans += TextSpan("\n", bold = false, italic = false, code = false)

        case fencedCode: FencedCodeBlock =>
          // Fenced code block: ```language\ncode\n```
          val code = fencedCode.getContentChars.toString
          val language = Option(fencedCode.getInfo.toString).filter(_.nonEmpty)
          codeBlocks += CodeBlock(code, language)
          // Don't visit children - we've extracted the code

        case image: Image =>
          // Content image: ![alt text](url)
          val url = image.getUrl.toString
          val altText = image.getText.toString
          // Use validated constructor to ensure alt text is non-empty
          ContentImage.validated(url, altText) match
            case Right(img) =>
              contentImages += img
            case Left(error) =>
              // Invalid image (empty alt text) - skip it
              // Validation layer will catch this
              ()
          // Don't visit children - we've extracted the image data

        case bulletList: BulletList =>
          // Unordered list: - Item one\n- Item two
          val items = extractListItems(bulletList)
          if items.nonEmpty then
            unorderedLists += UnorderedList(items)
          // Don't visit children - we've extracted the list

        case orderedList: com.vladsch.flexmark.ast.OrderedList =>
          // Ordered list: 1. Item one\n2. Item two
          val items = extractListItems(orderedList)
          if items.nonEmpty then
            orderedLists += com.tjmsolutions.mdslides.domain.OrderedList(items)
          // Don't visit children - we've extracted the list

        case _: BulletListItem | _: OrderedListItem =>
          // List items are handled by extractListItems, skip here
          ()

        case _ =>
          // For other nodes, recursively visit children
          n.getChildren.asScala.foreach(visitNode)

    visitNode(node)

    // Merge consecutive text spans with same formatting
    val mergedSpans = mergeConsecutiveSpans(textSpans.toList)

    FormattedContent(
      mergedSpans,
      links.toList,
      codeBlocks.toList,
      contentImages.toList,
      unorderedLists.toList,
      orderedLists.toList
    )

  /**
   * Merge consecutive TextSpans with identical formatting.
   *
   * Optimization: reduces number of spans for rendering.
   * Example: [TextSpan("Hello", bold=true), TextSpan(" ", bold=true), TextSpan("world", bold=true)]
   *          -> [TextSpan("Hello world", bold=true)]
   */
  private def mergeConsecutiveSpans(spans: List[TextSpan]): List[TextSpan] =
    spans.foldLeft(List.empty[TextSpan]) { (acc, span) =>
      acc match
        case Nil => List(span)
        case prev :: rest if prev.bold == span.bold &&
                            prev.italic == span.italic &&
                            prev.code == span.code =>
          // Same formatting - merge text
          TextSpan(prev.text + span.text, prev.bold, prev.italic, prev.code) :: rest
        case _ =>
          // Different formatting - append new span
          span :: acc
    }.reverse

  /**
   * Extract list items from a BulletList or OrderedList node.
   *
   * Processes each list item and extracts its text spans with formatting.
   *
   * @param listNode The BulletList or OrderedList node
   * @return List of ListItem objects with formatted text
   */
  private def extractListItems(listNode: Node): List[ListItem] =
    listNode.getChildren.asScala.toList.flatMap {
      case item: BulletListItem =>
        Some(extractListItemContent(item))
      case item: OrderedListItem =>
        Some(extractListItemContent(item))
      case _ =>
        None
    }

  /**
   * Extract formatted content from a single list item.
   *
   * Recursively processes the list item's children to extract text spans
   * with inline formatting (bold, italic, code).
   *
   * @param itemNode The list item node (BulletListItem or OrderedListItem)
   * @return ListItem with formatted text spans
   */
  private def extractListItemContent(itemNode: Node): ListItem =
    val itemSpans = scala.collection.mutable.ListBuffer.empty[TextSpan]
    var currentBold = false
    var currentItalic = false
    var currentCode = false

    def visitItemNode(n: Node): Unit =
      n match
        case text: Text =>
          val span = TextSpan(
            text.getChars.toString,
            bold = currentBold,
            italic = currentItalic,
            code = currentCode
          )
          itemSpans += span

        case strong: StrongEmphasis =>
          val prevBold = currentBold
          currentBold = true
          n.getChildren.asScala.foreach(visitItemNode)
          currentBold = prevBold

        case emphasis: Emphasis =>
          val prevItalic = currentItalic
          currentItalic = true
          n.getChildren.asScala.foreach(visitItemNode)
          currentItalic = prevItalic

        case code: Code =>
          val span = TextSpan(
            code.getChars.toString.stripPrefix("`").stripSuffix("`"),
            bold = false,
            italic = false,
            code = true
          )
          itemSpans += span

        case _: SoftLineBreak =>
          itemSpans += TextSpan(" ", bold = false, italic = false, code = false)

        case _ =>
          // Recursively visit children
          n.getChildren.asScala.foreach(visitItemNode)

    visitItemNode(itemNode)

    val mergedSpans = mergeConsecutiveSpans(itemSpans.toList)
    ListItem(mergedSpans)

end FlexmarkAdapter
