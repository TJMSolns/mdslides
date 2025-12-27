package com.tjmsolutions.mdslides.infrastructure.parser

import com.tjmsolutions.mdslides.domain.{FormattedContent, TextSpan, Link, CodeBlock}
import munit.FunSuite

/**
 * Tests for FlexmarkAdapter (Anticorruption Layer).
 *
 * Verifies that Flexmark AST is correctly translated to domain types.
 *
 * Related Governance:
 * - ADR-010: Markdown Library Selection
 * - ADR-007: Anticorruption Layer
 * - US-003: Full Markdown Rendering
 * - US-004: Code Block Support
 */
class FlexmarkAdapterSpec extends FunSuite:

  test("parse plain text without formatting"):
    val markdown = "Hello world"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assertEquals(result.textSpans.length, 1)
    assertEquals(result.textSpans.head.text, "Hello world")
    assertEquals(result.textSpans.head.bold, false)
    assertEquals(result.textSpans.head.italic, false)
    assertEquals(result.textSpans.head.code, false)
    assertEquals(result.links, List.empty)

  test("parse bold text with **"):
    val markdown = "This is **bold** text"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assert(result.textSpans.exists(span => span.text == "bold" && span.bold))
    assertEquals(result.plainText.trim, "This is bold text")

  test("parse bold text with __"):
    val markdown = "This is __bold__ text"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assert(result.textSpans.exists(span => span.text == "bold" && span.bold))

  test("parse italic text with *"):
    val markdown = "This is *italic* text"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assert(result.textSpans.exists(span => span.text == "italic" && span.italic))
    assertEquals(result.plainText.trim, "This is italic text")

  test("parse italic text with _"):
    val markdown = "This is _italic_ text"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assert(result.textSpans.exists(span => span.text == "italic" && span.italic))

  test("parse inline code with backticks"):
    val markdown = "Use `println` to print"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assert(result.textSpans.exists(span => span.text == "println" && span.code))
    assertEquals(result.plainText.trim, "Use println to print")

  test("parse link"):
    val markdown = "Visit [our docs](https://example.com) for more"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assertEquals(result.links.length, 1)
    assertEquals(result.links.head.text, "our docs")
    assertEquals(result.links.head.url, "https://example.com")

    // Link text should appear in plain text
    assert(result.plainText.contains("our docs"))
    assert(!result.plainText.contains("https://example.com"))

  test("parse combined bold and italic"):
    val markdown = "This is ***bold and italic*** text"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assert(result.textSpans.exists(span =>
      span.text == "bold and italic" && span.bold && span.italic
    ))

  test("parse multiple formatting in same line"):
    val markdown = "MDSlides is **domain-driven** and *well-tested*"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assert(result.textSpans.exists(span => span.text == "domain-driven" && span.bold))
    assert(result.textSpans.exists(span => span.text == "well-tested" && span.italic))
    assertEquals(result.plainText.trim, "MDSlides is domain-driven and well-tested")

  test("parse markdown with link and formatting"):
    val markdown = "**MDSlides** is documented at [our site](https://example.com)"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assert(result.textSpans.exists(span => span.text == "MDSlides" && span.bold))
    assertEquals(result.links.length, 1)
    assertEquals(result.links.head.text, "our site")

  test("parse empty string"):
    val markdown = ""
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assertEquals(result, FormattedContent.empty)

  test("parse multiline text"):
    val markdown = "Line one\nLine two"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assert(result.plainText.contains("Line one"))
    assert(result.plainText.contains("Line two"))

  test("word count strips markdown syntax"):
    val markdown = "MDSlides is **domain-driven** and *well-tested*"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    // "MDSlides is domain-driven and well-tested" = 5 words
    assertEquals(result.wordCount, 5)

  test("plain text includes link text but not URL"):
    val markdown = "Learn more at [our docs](https://example.com)"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assert(result.plainText.contains("our docs"))
    assert(!result.plainText.contains("https://example.com"))

  test("handles nested formatting correctly"):
    val markdown = "This is **bold with *italic inside* it**"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    // Should have both bold and italic text
    assert(result.textSpans.exists(span => span.bold))
    assert(result.textSpans.exists(span => span.italic))

  test("parse fenced code block without language"):
    val markdown = "```\ndef hello():\n    print('Hello')\n```"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assertEquals(result.codeBlocks.length, 1)
    val codeBlock = result.codeBlocks.head
    assertEquals(codeBlock.code, "def hello():\n    print('Hello')\n")
    assertEquals(codeBlock.language, None)

  test("parse fenced code block with language"):
    val markdown = "```scala\ndef add(a: Int, b: Int): Int =\n  a + b\n```"
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assertEquals(result.codeBlocks.length, 1)
    val codeBlock = result.codeBlocks.head
    assert(codeBlock.code.contains("def add"))
    assertEquals(codeBlock.language, Some("scala"))

  test("parse multiple code blocks"):
    val markdown = """
      |Some text
      |```python
      |print('hello')
      |```
      |More text
      |```javascript
      |console.log('world');
      |```
      |""".stripMargin
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    assertEquals(result.codeBlocks.length, 2)
    assertEquals(result.codeBlocks(0).language, Some("python"))
    assertEquals(result.codeBlocks(1).language, Some("javascript"))

  test("parse text with inline code and code block"):
    val markdown = """
      |Use `println` for output.
      |
      |Example:
      |```scala
      |println("Hello")
      |```
      |""".stripMargin
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    // Should have inline code span
    assert(result.textSpans.exists(span => span.text == "println" && span.code))
    // Should have code block
    assertEquals(result.codeBlocks.length, 1)
    assert(result.codeBlocks.head.code.contains("println"))

  test("code blocks don't appear in plain text"):
    val markdown = """
      |Some text
      |```
      |code here
      |```
      |""".stripMargin
    val result = FlexmarkAdapter.parseInlineFormatting(markdown)

    // Plain text should not contain code block content (per PDR-006)
    assert(!result.plainText.contains("code here"))
    assert(result.plainText.contains("Some text"))

end FlexmarkAdapterSpec
