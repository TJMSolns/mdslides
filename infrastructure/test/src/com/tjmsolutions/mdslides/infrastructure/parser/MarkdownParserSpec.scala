package com.tjmsolutions.mdslides.infrastructure.parser

import com.tjmsolutions.mdslides.domain.{Slide, SlideDeck}

/**
 * Tests for MarkdownParser.
 *
 * Tests verify:
 * - Parsing title slides (with/without optional slots)
 * - Parsing content slides
 * - Multi-slide decks
 * - Error handling (empty input, unknown templates)
 * - Frontmatter extraction
 * - Markdown structure preservation (not rendering yet)
 *
 * Related Governance:
 * - ADR-006: Rendering Architecture
 * - ADR-007: Anticorruption Layer for Flexmark
 */
class MarkdownParserSpec extends munit.FunSuite:

  test("parse minimal title slide") {
    val markdown = """---
                     |template: title
                     |---
                     |# Welcome to MDSlides""".stripMargin

    val result = MarkdownParser.parse(markdown)

    result match
      case Right(deck) =>
        assertEquals(deck.slideCount, 1)
        val slide = deck.getSlide(0).get
        assertEquals(slide.templateName, "title")
        assertEquals(slide.getSlot("title"), Some("Welcome to MDSlides"))
        assertEquals(slide.getSlot("subtitle"), None)
        assertEquals(slide.getSlot("author"), None)

      case Left(error) =>
        fail(s"Expected successful parse, got error: $error")
  }

  test("parse title slide with all optional slots") {
    val markdown = """---
                     |template: title
                     |---
                     |# MDSlides Framework
                     |## Building Better Presentations
                     |John Doe""".stripMargin

    val result = MarkdownParser.parse(markdown)

    result match
      case Right(deck) =>
        val slide = deck.getSlide(0).get
        assertEquals(slide.getSlot("title"), Some("MDSlides Framework"))
        assertEquals(slide.getSlot("subtitle"), Some("Building Better Presentations"))
        assertEquals(slide.getSlot("author"), Some("John Doe"))

      case Left(error) =>
        fail(s"Expected successful parse, got error: $error")
  }

  test("parse content slide") {
    val markdown = """---
                     |template: content
                     |---
                     |## Key Principles
                     |Domain-Driven Design helps us build better software.
                     |Test-Driven Development ensures quality.""".stripMargin

    val result = MarkdownParser.parse(markdown)

    result match
      case Right(deck) =>
        val slide = deck.getSlide(0).get
        assertEquals(slide.templateName, "content")
        assertEquals(slide.getSlot("heading"), Some("Key Principles"))
        assert(slide.getSlot("body").get.contains("Domain-Driven Design"))
        assert(slide.getSlot("body").get.contains("Test-Driven Development"))

      case Left(error) =>
        fail(s"Expected successful parse, got error: $error")
  }

  test("parse multi-slide deck") {
    val markdown = """---
                     |template: title
                     |---
                     |# Introduction
                     |
                     |---
                     |template: content
                     |---
                     |## Overview
                     |This is the overview.
                     |
                     |---
                     |template: content
                     |---
                     |## Conclusion
                     |Thank you!""".stripMargin

    val result = MarkdownParser.parse(markdown)

    result match
      case Right(deck) =>
        assertEquals(deck.slideCount, 3)

        // Check first slide (title)
        val slide1 = deck.getSlide(0).get
        assertEquals(slide1.templateName, "title")
        assertEquals(slide1.getSlot("title"), Some("Introduction"))

        // Check second slide (content)
        val slide2 = deck.getSlide(1).get
        assertEquals(slide2.templateName, "content")
        assertEquals(slide2.getSlot("heading"), Some("Overview"))
        assertEquals(slide2.getSlot("body"), Some("This is the overview."))

        // Check third slide (content)
        val slide3 = deck.getSlide(2).get
        assertEquals(slide3.templateName, "content")
        assertEquals(slide3.getSlot("heading"), Some("Conclusion"))
        assertEquals(slide3.getSlot("body"), Some("Thank you!"))

      case Left(error) =>
        fail(s"Expected successful parse, got error: $error")
  }

  test("default template is content when not specified") {
    val markdown = """---
                     |---
                     |## Default Content
                     |This uses default template.""".stripMargin

    val result = MarkdownParser.parse(markdown)

    result match
      case Right(deck) =>
        val slide = deck.getSlide(0).get
        assertEquals(slide.templateName, "content")
        assertEquals(slide.getSlot("heading"), Some("Default Content"))

      case Left(error) =>
        fail(s"Expected successful parse, got error: $error")
  }

  test("parse empty markdown fails") {
    val result = MarkdownParser.parse("")

    result match
      case Left(error) =>
        assert(error.contains("No slides found"))

      case Right(_) =>
        fail("Expected parsing to fail for empty markdown")
  }

  test("parse unknown template fails") {
    val markdown = """---
                     |template: unknown
                     |---
                     |Some content""".stripMargin

    val result = MarkdownParser.parse(markdown)

    result match
      case Left(error) =>
        assert(error.contains("Unknown template"))
        assert(error.contains("unknown"))

      case Right(_) =>
        fail("Expected parsing to fail for unknown template")
  }

  test("markdown formatting is preserved (not rendered)") {
    val markdown = """---
                     |template: content
                     |---
                     |## Formatted Content
                     |This has **bold** and *italic* text.
                     |Also a [link](https://example.com).""".stripMargin

    val result = MarkdownParser.parse(markdown)

    result match
      case Right(deck) =>
        val slide = deck.getSlide(0).get
        val body = slide.getSlot("body").get

        // Markdown should be preserved as-is (not rendered to HTML)
        assert(body.contains("**bold**"))
        assert(body.contains("*italic*"))
        assert(body.contains("[link](https://example.com)"))

      case Left(error) =>
        fail(s"Expected successful parse, got error: $error")
  }

  test("extra whitespace is handled") {
    val markdown = """---
                     |template: title
                     |---
                     |
                     |
                     |# Title with Extra Whitespace
                     |
                     |
                     |## Subtitle
                     |
                     |""".stripMargin

    val result = MarkdownParser.parse(markdown)

    result match
      case Right(deck) =>
        val slide = deck.getSlide(0).get
        assertEquals(slide.getSlot("title"), Some("Title with Extra Whitespace"))
        assertEquals(slide.getSlot("subtitle"), Some("Subtitle"))

      case Left(error) =>
        fail(s"Expected successful parse, got error: $error")
  }

  test("content slide body preserves newlines") {
    val markdown = """---
                     |template: content
                     |---
                     |## Multi-line Body
                     |Line 1
                     |Line 2
                     |Line 3""".stripMargin

    val result = MarkdownParser.parse(markdown)

    result match
      case Right(deck) =>
        val slide = deck.getSlide(0).get
        val body = slide.getSlot("body").get

        // Newlines should be preserved
        assert(body.contains("\n"))
        assert(body.contains("Line 1"))
        assert(body.contains("Line 2"))
        assert(body.contains("Line 3"))

      case Left(error) =>
        fail(s"Expected successful parse, got error: $error")
  }

end MarkdownParserSpec
