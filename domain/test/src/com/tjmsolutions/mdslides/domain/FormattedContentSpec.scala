package com.tjmsolutions.mdslides.domain

import munit.FunSuite

/**
 * Tests for FormattedContent value object.
 *
 * FormattedContent represents parsed markdown with structure:
 * - Text spans with inline formatting (bold, italic, code)
 * - Hyperlinks
 * - Raw text extraction (for validation)
 *
 * Related Governance:
 * - US-003: Full Markdown Rendering
 * - PDR-001: Density Validation Limits
 * - POL-004: Property-Based Testing Requirements
 */
class FormattedContentSpec extends FunSuite:

  test("FormattedContent.plainText - single text span without formatting"):
    val span = TextSpan("Hello world", bold = false, italic = false, code = false)
    val content = FormattedContent(List(span), List.empty)

    assertEquals(content.plainText, "Hello world")

  test("FormattedContent.plainText - text span with bold formatting"):
    val span = TextSpan("important", bold = true, italic = false, code = false)
    val content = FormattedContent(List(span), List.empty)

    // Plain text strips formatting
    assertEquals(content.plainText, "important")

  test("FormattedContent.plainText - text span with italic formatting"):
    val span = TextSpan("emphasis", bold = false, italic = true, code = false)
    val content = FormattedContent(List(span), List.empty)

    assertEquals(content.plainText, "emphasis")

  test("FormattedContent.plainText - text span with inline code"):
    val span = TextSpan("println", bold = false, italic = false, code = true)
    val content = FormattedContent(List(span), List.empty)

    assertEquals(content.plainText, "println")

  test("FormattedContent.plainText - multiple text spans"):
    val spans = List(
      TextSpan("MDSlides is ", bold = false, italic = false, code = false),
      TextSpan("domain-driven", bold = true, italic = false, code = false),
      TextSpan(" and ", bold = false, italic = false, code = false),
      TextSpan("well-tested", bold = false, italic = true, code = false),
      TextSpan(".", bold = false, italic = false, code = false)
    )
    val content = FormattedContent(spans, List.empty)

    assertEquals(content.plainText, "MDSlides is domain-driven and well-tested.")

  test("FormattedContent.plainText - with hyperlink"):
    val spans = List(
      TextSpan("Learn more at ", bold = false, italic = false, code = false)
    )
    val links = List(
      Link("our docs", "https://example.com")
    )
    val content = FormattedContent(spans, links)

    // Plain text includes link text, not URL
    assert(content.plainText.contains("our docs"))
    assert(!content.plainText.contains("https://example.com"))

  test("FormattedContent.wordCount - counts plain text words only"):
    val spans = List(
      TextSpan("MDSlides is ", bold = false, italic = false, code = false),
      TextSpan("domain-driven", bold = true, italic = false, code = false),
      TextSpan(" and ", bold = false, italic = false, code = false),
      TextSpan("well-tested", bold = false, italic = true, code = false),
      TextSpan(".", bold = false, italic = false, code = false)
    )
    val content = FormattedContent(spans, List.empty)

    // "MDSlides is domain-driven and well-tested." = 5 words
    assertEquals(content.wordCount, 5)

  test("FormattedContent.wordCount - includes link text"):
    val spans = List(
      TextSpan("Learn more at ", bold = false, italic = false, code = false)
    )
    val links = List(
      Link("our docs", "https://example.com")
    )
    val content = FormattedContent(spans, links)

    // "Learn more at our docs" = 5 words
    assertEquals(content.wordCount, 5)

  test("FormattedContent.lineCount - counts newlines in plain text"):
    val spans = List(
      TextSpan("Line one\n", bold = false, italic = false, code = false),
      TextSpan("Line two", bold = false, italic = false, code = false)
    )
    val content = FormattedContent(spans, List.empty)

    assertEquals(content.lineCount, 2)

  test("FormattedContent.lineCount - single line with no newline"):
    val spans = List(
      TextSpan("Single line", bold = false, italic = false, code = false)
    )
    val content = FormattedContent(spans, List.empty)

    assertEquals(content.lineCount, 1)

  test("FormattedContent.empty - creates empty content"):
    val content = FormattedContent.empty

    assertEquals(content.plainText, "")
    assertEquals(content.wordCount, 0)
    assertEquals(content.lineCount, 0)
