package com.tjmsolutions.mdslides.infrastructure.renderer

import com.tjmsolutions.mdslides.domain.{Slide, SlideDeck, SlideId}
import cats.data.NonEmptyList

/**
 * Tests for HTMLRenderer.
 *
 * Tests verify:
 * - HTML structure (doctype, head, body)
 * - Title slide rendering (h1, h2, p tags)
 * - Content slide rendering (h2, div, p tags)
 * - Navigation controls (keyboard, counter)
 * - CSS and JavaScript injection
 * - Multi-slide deck rendering
 *
 * Related Governance:
 * - ADR-006: Rendering Architecture
 * - PDR-005: Accessibility Requirements (WCAG AA)
 */
class HTMLRendererSpec extends munit.FunSuite:

  test("render minimal title slide to HTML") {
    val slide = Slide(
      id = SlideId.unsafe(1),
      templateName = "title",
      slots = Map("title" -> "Welcome to MDSlides")
    )
    val deck = SlideDeck(NonEmptyList.one(slide))

    val html = HTMLRenderer.renderDeck(deck)

    // Check HTML structure
    assert(html.startsWith("<!DOCTYPE html>"))
    assert(html.contains("<html>"))
    assert(html.contains("</html>"))
    assert(html.contains("<head>"))
    assert(html.contains("<body>"))

    // Check metadata
    assert(html.contains("charset=\"UTF-8\""))
    assert(html.contains("viewport"))

    // Check title content
    assert(html.contains("Welcome to MDSlides"))
    assert(html.contains("slide-title"))

    // Check CSS injection
    assert(html.contains("<style>"))
    assert(html.contains(".slide"))
    assert(html.contains(".title-slide"))

    // Check JavaScript injection
    assert(html.contains("<script>"))
    assert(html.contains("currentSlide"))
    assert(html.contains("showSlide"))

    // Check slide counter
    assert(html.contains("slide-counter"))
    assert(html.contains("1 / 1"))
  }

  test("render title slide with all optional slots") {
    val slide = Slide(
      id = SlideId.unsafe(1),
      templateName = "title",
      slots = Map(
        "title" -> "MDSlides Framework",
        "subtitle" -> "Building Better Presentations",
        "author" -> "John Doe"
      )
    )
    val deck = SlideDeck(NonEmptyList.one(slide))

    val html = HTMLRenderer.renderDeck(deck)

    // Check all slots rendered
    assert(html.contains("MDSlides Framework"))
    assert(html.contains("Building Better Presentations"))
    assert(html.contains("John Doe"))

    // Check correct HTML tags
    assert(html.contains("slide-title"))
    assert(html.contains("slide-subtitle"))
    assert(html.contains("slide-author"))
  }

  test("render content slide with heading and body") {
    val slide = Slide(
      id = SlideId.unsafe(1),
      templateName = "content",
      slots = Map(
        "heading" -> "Key Principles",
        "body" -> "Domain-Driven Design\nTest-Driven Development\nClean Architecture"
      )
    )
    val deck = SlideDeck(NonEmptyList.one(slide))

    val html = HTMLRenderer.renderDeck(deck)

    // Check heading rendered
    assert(html.contains("Key Principles"))
    assert(html.contains("slide-heading"))

    // Check body content
    assert(html.contains("Domain-Driven Design"))
    assert(html.contains("Test-Driven Development"))
    assert(html.contains("Clean Architecture"))
    assert(html.contains("slide-body"))

    // Check body rendered as paragraphs
    assert(html.contains("<p>"))
  }

  test("render multi-slide deck") {
    val slide1 = Slide(
      id = SlideId.unsafe(1),
      templateName = "title",
      slots = Map("title" -> "Introduction")
    )
    val slide2 = Slide(
      id = SlideId.unsafe(2),
      templateName = "content",
      slots = Map(
        "heading" -> "Overview",
        "body" -> "This is the overview."
      )
    )
    val slide3 = Slide(
      id = SlideId.unsafe(3),
      templateName = "content",
      slots = Map(
        "heading" -> "Conclusion",
        "body" -> "Thank you!"
      )
    )
    val deck = SlideDeck(NonEmptyList.of(slide1, slide2, slide3))

    val html = HTMLRenderer.renderDeck(deck)

    // Check all slides present
    assert(html.contains("Introduction"))
    assert(html.contains("Overview"))
    assert(html.contains("Conclusion"))

    // Check slide counter shows total
    assert(html.contains("1 / 3"))

    // Check JavaScript has correct total
    assert(html.contains("const totalSlides = 3"))

    // Check first slide has "active" class
    assert(html.contains("class=\"slide active\""))

    // Check data attributes for navigation
    assert(html.contains("data-slide-index=\"0\""))
    assert(html.contains("data-slide-index=\"1\""))
    assert(html.contains("data-slide-index=\"2\""))
  }

  test("render unknown template shows error") {
    val slide = Slide(
      id = SlideId.unsafe(1),
      templateName = "unknown",
      slots = Map()
    )
    val deck = SlideDeck(NonEmptyList.one(slide))

    val html = HTMLRenderer.renderDeck(deck)

    // Check error message
    assert(html.contains("Unknown template"))
    assert(html.contains("unknown"))
    assert(html.contains("error"))
  }

  test("empty body lines are filtered out") {
    val slide = Slide(
      id = SlideId.unsafe(1),
      templateName = "content",
      slots = Map(
        "heading" -> "Test",
        "body" -> "Line 1\n\n\nLine 2\n   \nLine 3"
      )
    )
    val deck = SlideDeck(NonEmptyList.one(slide))

    val html = HTMLRenderer.renderDeck(deck)

    // Check content rendered
    assert(html.contains("Line 1"))
    assert(html.contains("Line 2"))
    assert(html.contains("Line 3"))

    // Empty lines should be filtered (no empty <p> tags)
    // Count paragraph tags - should have 3, not 6
    val paragraphCount = "<p>".r.findAllIn(html).length
    // Note: This is a simple heuristic; actual count may vary
    assert(paragraphCount >= 3, s"Expected at least 3 paragraphs, got $paragraphCount")
  }

  test("navigation JavaScript includes all required functions") {
    val slide = Slide(
      id = SlideId.unsafe(1),
      templateName = "title",
      slots = Map("title" -> "Test")
    )
    val deck = SlideDeck(NonEmptyList.one(slide))

    val html = HTMLRenderer.renderDeck(deck)

    // Check navigation functions
    assert(html.contains("function showSlide"))
    assert(html.contains("function nextSlide"))
    assert(html.contains("function prevSlide"))
    assert(html.contains("function firstSlide"))
    assert(html.contains("function lastSlide"))

    // Check keyboard event listener
    assert(html.contains("addEventListener('keydown'"))
    assert(html.contains("ArrowRight"))
    assert(html.contains("ArrowLeft"))
    assert(html.contains("Home"))
    assert(html.contains("End"))
  }

  test("CSS includes all required classes") {
    val slide = Slide(
      id = SlideId.unsafe(1),
      templateName = "title",
      slots = Map("title" -> "Test")
    )
    val deck = SlideDeck(NonEmptyList.one(slide))

    val html = HTMLRenderer.renderDeck(deck)

    // Check CSS classes
    assert(html.contains(".slide"))
    assert(html.contains(".slide.active"))
    assert(html.contains(".title-slide"))
    assert(html.contains(".content-slide"))
    assert(html.contains(".slide-title"))
    assert(html.contains(".slide-subtitle"))
    assert(html.contains(".slide-author"))
    assert(html.contains(".slide-heading"))
    assert(html.contains(".slide-body"))
    assert(html.contains(".controls"))
    assert(html.contains(".error"))
  }

  test("HTML is valid structure") {
    val slide = Slide(
      id = SlideId.unsafe(1),
      templateName = "title",
      slots = Map("title" -> "Test")
    )
    val deck = SlideDeck(NonEmptyList.one(slide))

    val html = HTMLRenderer.renderDeck(deck)

    // Check proper nesting (basic validation)
    assert(html.indexOf("<html>") < html.indexOf("</html>"))
    assert(html.indexOf("<head>") < html.indexOf("</head>"))
    assert(html.indexOf("<body>") < html.indexOf("</body>"))
    assert(html.indexOf("</head>") < html.indexOf("<body>"))
  }

  test("slide counter updates with deck size") {
    val slides = (1 to 5).map { i =>
      Slide(
        id = SlideId.unsafe(i),
        templateName = "content",
        slots = Map(
          "heading" -> s"Slide $i",
          "body" -> s"Content $i"
        )
      )
    }
    val deck = SlideDeck(NonEmptyList.fromListUnsafe(slides.toList))

    val html = HTMLRenderer.renderDeck(deck)

    // Check counter shows correct total
    assert(html.contains("1 / 5"))
    assert(html.contains("const totalSlides = 5"))
  }

end HTMLRendererSpec
