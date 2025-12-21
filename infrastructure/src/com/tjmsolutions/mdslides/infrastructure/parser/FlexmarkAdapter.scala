package com.tjmsolutions.mdslides.infrastructure.parser

import com.tjmsolutions.mdslides.domain.{FormattedContent, TextSpan, Link}
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
 * Related Governance:
 * - ADR-010: Markdown Library Selection (Flexmark)
 * - ADR-007: Anticorruption Layer
 * - US-003: Full Markdown Rendering
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
   * Walks the AST and builds TextSpan and Link objects.
   */
  private def extractFormattedContent(node: Node): FormattedContent =
    val textSpans = scala.collection.mutable.ListBuffer.empty[TextSpan]
    val links = scala.collection.mutable.ListBuffer.empty[Link]

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

        case _ =>
          // For other nodes, recursively visit children
          n.getChildren.asScala.foreach(visitNode)

    visitNode(node)

    // Merge consecutive text spans with same formatting
    val mergedSpans = mergeConsecutiveSpans(textSpans.toList)

    FormattedContent(mergedSpans, links.toList)

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

end FlexmarkAdapter
