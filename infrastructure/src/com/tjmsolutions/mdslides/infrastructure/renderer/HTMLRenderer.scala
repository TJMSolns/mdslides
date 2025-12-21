package com.tjmsolutions.mdslides.infrastructure.renderer

import com.tjmsolutions.mdslides.domain.{Slide, SlideDeck}
import scalatags.Text.all.*

/**
 * HTML renderer for slides using Scalatags.
 *
 * Renders a SlideDeck to standalone HTML with:
 * - Inline CSS for styling
 * - Inline JavaScript for navigation
 * - Keyboard navigation (arrow keys, space, home, end)
 * - Responsive design
 *
 * Related Governance:
 * - ADR-006: Rendering Architecture
 * - PDR-005: Accessibility Requirements (WCAG AA)
 */
object HTMLRenderer:

  /**
   * Render a complete SlideDeck to standalone HTML.
   *
   * @param deck The slide deck to render
   * @return Complete HTML document as string
   */
  def renderDeck(deck: SlideDeck): String =
    val slideHtml = deck.toList.zipWithIndex.map { case (slide, index) =>
      renderSlide(slide, index, deck.slideCount)
    }

    val document = html(
      head(
        meta(charset := "UTF-8"),
        meta(name := "viewport", content := "width=device-width, initial-scale=1.0"),
        tag("title")("MDSlides Presentation"),
        tag("style")(raw(defaultCSS))
      ),
      body(
        div(cls := "slides")(slideHtml),
        div(cls := "controls")(
          span(cls := "slide-counter", id := "slide-counter")("1 / " + deck.slideCount)
        ),
        tag("script")(raw(navigationJS(deck.slideCount)))
      )
    )

    "<!DOCTYPE html>\n" + document.render

  /**
   * Render a single slide.
   *
   * @param slide The slide to render
   * @param index The slide index (0-based)
   * @param totalSlides Total number of slides in deck
   * @return HTML div element for the slide
   */
  private def renderSlide(slide: Slide, index: Int, totalSlides: Int): Frag =
    val slideClass = if index == 0 then "slide active" else "slide"

    div(cls := slideClass, attr("data-slide-index") := index.toString)(
      renderSlideContent(slide)
    )

  /**
   * Render slide content based on template type.
   *
   * @param slide The slide to render
   * @return HTML content for the slide
   */
  private def renderSlideContent(slide: Slide): Frag =
    slide.templateName match
      case "title" => renderTitleSlide(slide)
      case "content" => renderContentSlide(slide)
      case _ => div(cls := "error")("Unknown template: " + slide.templateName)

  /**
   * Render a title slide.
   */
  private def renderTitleSlide(slide: Slide): Frag =
    div(cls := "title-slide")(
      slide.getSlot("title").map(title =>
        h1(cls := "slide-title")(title)
      ),
      slide.getSlot("subtitle").map(subtitle =>
        h2(cls := "slide-subtitle")(subtitle)
      ),
      slide.getSlot("author").map(author =>
        p(cls := "slide-author")(author)
      )
    )

  /**
   * Render a content slide.
   */
  private def renderContentSlide(slide: Slide): Frag =
    div(cls := "content-slide")(
      slide.getSlot("heading").map(heading =>
        h2(cls := "slide-heading")(heading)
      ),
      slide.getSlot("body").map(body =>
        div(cls := "slide-body")(
          // For now, render body as paragraphs (markdown parsing comes later)
          body.split("\n").filter(_.trim.nonEmpty).map(line =>
            p(line)
          )
        )
      )
    )

  /**
   * Default CSS styles.
   */
  private def defaultCSS: String = """
    * {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
    }

    body {
      font-family: Arial, sans-serif;
      background-color: #f5f5f5;
      overflow: hidden;
    }

    .slides {
      width: 100vw;
      height: 100vh;
    }

    .slide {
      display: none;
      width: 100%;
      height: 100%;
      padding: 60px;
      background-color: #ffffff;
      justify-content: center;
      align-items: center;
      flex-direction: column;
    }

    .slide.active {
      display: flex;
    }

    .title-slide {
      text-align: center;
      max-width: 800px;
    }

    .slide-title {
      font-size: 48px;
      color: #333333;
      margin-bottom: 30px;
      line-height: 1.2;
    }

    .slide-subtitle {
      font-size: 36px;
      color: #666666;
      margin-bottom: 40px;
      font-weight: normal;
    }

    .slide-author {
      font-size: 24px;
      color: #999999;
    }

    .content-slide {
      max-width: 900px;
      width: 100%;
    }

    .slide-heading {
      font-size: 36px;
      color: #333333;
      margin-bottom: 30px;
    }

    .slide-body {
      font-size: 24px;
      color: #333333;
      line-height: 1.6;
    }

    .slide-body p {
      margin-bottom: 15px;
    }

    .controls {
      position: fixed;
      bottom: 20px;
      right: 20px;
      background-color: rgba(0, 0, 0, 0.7);
      color: white;
      padding: 10px 20px;
      border-radius: 5px;
      font-size: 18px;
    }

    .error {
      color: red;
      font-size: 24px;
      padding: 40px;
    }
  """

  /**
   * Navigation JavaScript.
   *
   * @param totalSlides Total number of slides
   * @return JavaScript code for keyboard navigation
   */
  private def navigationJS(totalSlides: Int): String = s"""
    let currentSlide = 0;
    const totalSlides = $totalSlides;

    function showSlide(index) {
      const slides = document.querySelectorAll('.slide');
      slides.forEach((slide, i) => {
        slide.classList.toggle('active', i === index);
      });

      const counter = document.getElementById('slide-counter');
      counter.textContent = (index + 1) + ' / ' + totalSlides;
    }

    function nextSlide() {
      if (currentSlide < totalSlides - 1) {
        currentSlide++;
        showSlide(currentSlide);
      }
    }

    function prevSlide() {
      if (currentSlide > 0) {
        currentSlide--;
        showSlide(currentSlide);
      }
    }

    function firstSlide() {
      currentSlide = 0;
      showSlide(currentSlide);
    }

    function lastSlide() {
      currentSlide = totalSlides - 1;
      showSlide(currentSlide);
    }

    document.addEventListener('keydown', (e) => {
      switch(e.key) {
        case 'ArrowRight':
        case ' ':
          e.preventDefault();
          nextSlide();
          break;
        case 'ArrowLeft':
          e.preventDefault();
          prevSlide();
          break;
        case 'Home':
          e.preventDefault();
          firstSlide();
          break;
        case 'End':
          e.preventDefault();
          lastSlide();
          break;
      }
    });
  """

end HTMLRenderer
