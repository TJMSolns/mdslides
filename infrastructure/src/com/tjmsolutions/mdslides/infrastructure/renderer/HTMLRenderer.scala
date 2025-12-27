package com.tjmsolutions.mdslides.infrastructure.renderer

import com.tjmsolutions.mdslides.domain.{Slide, SlideDeck, FormattedContent, TextSpan, Link, CodeBlock, ContentImage, Theme}
import com.tjmsolutions.mdslides.infrastructure.parser.FlexmarkAdapter
import scalatags.Text.all.*

/**
 * HTML renderer for slides using Scalatags.
 *
 * Renders a SlideDeck to standalone HTML with:
 * - Inline CSS for styling (theme-based)
 * - Inline JavaScript for navigation
 * - Keyboard navigation (arrow keys, space, home, end)
 * - Responsive design
 * - Background images (theme-configured)
 *
 * Related Governance:
 * - ADR-006: Rendering Architecture
 * - PDR-005: Accessibility Requirements (WCAG AA)
 * - US-008: Theme System
 * - US-009: Built-in Themes
 */
object HTMLRenderer:

  /**
   * Render a complete SlideDeck to standalone HTML with theme.
   *
   * @param deck The slide deck to render
   * @param theme The theme to apply (default: Theme.light)
   * @return Complete HTML document as string
   */
  def renderDeck(deck: SlideDeck, theme: Theme = Theme.light): String =
    val slideHtml = deck.toList.zipWithIndex.map { case (slide, index) =>
      renderSlide(slide, index, deck.slideCount, theme)
    }

    val document = html(
      head(
        meta(charset := "UTF-8"),
        meta(name := "viewport", content := "width=device-width, initial-scale=1.0"),
        tag("title")("MDSlides Presentation"),
        tag("style")(raw(generateCSS(theme)))
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
   * Render FormattedContent to HTML (for testing).
   *
   * Public method to test formatted content rendering in isolation.
   * Used by HTMLRendererSpec tests.
   *
   * @param content The formatted content to render
   * @return HTML string
   */
  def renderFormattedContent(content: FormattedContent): String =
    val frag = renderFormattedContentWithParagraphs(content)
    frag.render

  /**
   * Resolve background image using fallback chain (US-011, PDR-011).
   *
   * Priority:
   * 1. Slide.backgroundImage (highest - per-slide override)
   * 2. Theme.templateBackgrounds[Slide.templateName] (template-specific)
   * 3. Theme.background.image (theme default)
   * 4. None (use theme background color only)
   *
   * @param slide The slide to resolve background for
   * @param theme The theme containing template and default backgrounds
   * @return Some(imagePath) if background found, None otherwise
   */
  private def resolveBackgroundImage(slide: Slide, theme: Theme): Option[String] =
    import com.tjmsolutions.mdslides.domain.{SlideBackground, BackgroundConfig}

    // 1. Check for per-slide background override (highest priority)
    slide.backgroundImage match
      case Some(bg: String) => Some(bg)
      case Some(bg: BackgroundConfig) => Some(bg.imagePath)
      case None =>
        // 2. Check for template-specific background
        theme.templateBackgrounds.get(slide.templateName) match
          case Some(templateBg) => Some(templateBg)
          case None =>
            // 3. Fall back to theme default background
            theme.background.image

  /**
   * Generate CSS from theme.
   *
   * Converts Theme aggregate to inline CSS.
   * Handles background images, colors, fonts, spacing.
   *
   * @param theme The theme to generate CSS from
   * @return CSS string
   */
  private def generateCSS(theme: Theme): String =
    val backgroundImage = theme.background.image match
      case Some(img) =>
        val position = theme.background.position.getOrElse("center")
        val size = theme.background.size.getOrElse("cover")
        val opacity = theme.background.opacity
        s"""
        background-image: url('$img');
        background-position: $position;
        background-size: $size;
        background-repeat: no-repeat;
        opacity: $opacity;
        """
      case None => ""

    s"""
    * {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
    }

    body {
      font-family: ${theme.fonts.body};
      background-color: ${theme.background.color};$backgroundImage
      color: ${theme.colors.text};
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
      padding: ${theme.spacing.slideMargin};
      background-color: ${theme.background.color};
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
      justify-content: space-between;
      padding-top: 60px;
      padding-bottom: 60px;
    }

    .title-slide-header {
      margin-bottom: auto;
    }

    .title-slide-footer {
      margin-top: auto;
    }

    .slide-title {
      font-size: 48px;
      font-family: ${theme.fonts.heading};
      color: ${theme.colors.heading};
      margin: ${theme.spacing.headingMargin};
      line-height: ${theme.spacing.lineHeight};
    }

    .slide-subtitle {
      font-size: 36px;
      font-family: ${theme.fonts.heading};
      color: ${theme.colors.heading};
      margin-bottom: 20px;
      font-weight: normal;
    }

    .slide-author {
      font-size: 24px;
      color: ${theme.colors.text};
      margin-top: 20px;
    }

    .content-slide {
      max-width: 900px;
      width: 100%;
    }

    .slide-heading {
      font-size: 36px;
      font-family: ${theme.fonts.heading};
      color: ${theme.colors.heading};
      margin: ${theme.spacing.headingMargin};
    }

    .slide-body {
      font-size: 24px;
      color: ${theme.colors.text};
      line-height: ${theme.spacing.lineHeight};
    }

    .slide-body p {
      margin: ${theme.spacing.paragraphMargin};
    }

    /* Inline formatting (US-003) */
    strong {
      font-weight: bold;
    }

    em {
      font-style: italic;
    }

    code {
      background-color: ${theme.colors.codeBackground};
      color: ${theme.colors.codeText};
      border: 1px solid #ddd;
      border-radius: 3px;
      padding: 2px 6px;
      font-family: ${theme.fonts.code};
      font-size: 0.9em;
    }

    /* Code blocks (US-004) */
    pre {
      background-color: ${theme.colors.codeBackground};
      border: 1px solid #ddd;
      border-radius: 5px;
      padding: 15px;
      margin: ${theme.spacing.paragraphMargin};
      overflow-x: auto;
      font-size: 18px;
    }

    pre code {
      background-color: transparent;
      border: none;
      padding: 0;
      font-family: ${theme.fonts.code};
      color: ${theme.colors.codeText};
      font-size: inherit;
    }

    /* Syntax highlighting using theme colors */
    .syntax-keyword { color: ${theme.syntax.keyword}; }
    .syntax-string { color: ${theme.syntax.string}; }
    .syntax-comment { color: ${theme.syntax.comment}; }
    .syntax-function { color: ${theme.syntax.function}; }
    .syntax-number { color: ${theme.syntax.number}; }
    .syntax-operator { color: ${theme.syntax.operator}; }

    /* Content images (US-005) */
    .content-image {
      max-width: 100%;
      height: auto;
      margin: ${theme.spacing.paragraphMargin};
      display: block;
      border-radius: 5px;
    }

    a {
      color: ${theme.colors.link};
      text-decoration: none;
    }

    a:hover {
      color: ${theme.colors.linkHover};
      text-decoration: underline;
    }

    /* Nested list support (US-003.3) */
    .slide-body ul,
    .slide-body ol {
      margin: ${theme.spacing.paragraphMargin};
      padding-left: 2em;
    }

    /* Unordered list bullet hierarchy */
    .slide-body ul {
      list-style-type: disc;  /* Level 1: disc (•) */
    }

    .slide-body ul ul {
      list-style-type: circle;  /* Level 2: circle (◦) */
      margin-top: 0.5em;
    }

    .slide-body ul ul ul {
      list-style-type: square;  /* Level 3: square (▪) */
    }

    /* Ordered list numbering hierarchy */
    .slide-body ol {
      list-style-type: decimal;  /* Level 1: decimal (1, 2, 3...) */
    }

    .slide-body ol ol {
      list-style-type: lower-alpha;  /* Level 2: lower-alpha (a, b, c...) */
      margin-top: 0.5em;
    }

    .slide-body ol ol ol {
      list-style-type: lower-roman;  /* Level 3: lower-roman (i, ii, iii...) */
    }

    /* Mixed nesting support */
    .slide-body ul ol,
    .slide-body ol ul {
      margin-top: 0.5em;
    }

    /* List item spacing */
    .slide-body li {
      margin: 0.3em 0;
      line-height: ${theme.spacing.lineHeight};
    }

    .controls {
      position: fixed;
      bottom: 20px;
      right: 20px;
      background-color: ${theme.slideCounter.background};
      color: ${theme.slideCounter.color};
      padding: 10px 20px;
      border-radius: 5px;
      font-size: ${theme.slideCounter.fontSize};
    }

    .error {
      color: red;
      font-size: 24px;
      padding: 40px;
    }
    """

  /**
   * Render a single slide.
   *
   * @param slide The slide to render
   * @param index The slide index (0-based)
   * @param totalSlides Total number of slides in deck
   * @param theme The theme (for background resolution)
   * @return HTML div element for the slide
   */
  private def renderSlide(slide: Slide, index: Int, totalSlides: Int, theme: Theme): Frag =
    val slideClass = if index == 0 then "slide active" else "slide"

    // Resolve background using fallback chain (US-011)
    val backgroundModifiers = resolveBackgroundImage(slide, theme) match
      case Some(bgPath) =>
        Seq(attr("style") := s"background-image: url('$bgPath'); background-size: cover; background-position: center;")
      case None =>
        Seq()  // No inline background, use CSS default

    div(cls := slideClass, attr("data-slide-index") := index.toString, backgroundModifiers)(
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
   *
   * Title/subtitle are top-justified, author is bottom-justified.
   * This matches standard presentation design patterns.
   */
  private def renderTitleSlide(slide: Slide): Frag =
    div(cls := "title-slide")(
      // Header section (top-justified)
      div(cls := "title-slide-header")(
        slide.getSlot("title").map(title =>
          h1(cls := "slide-title")(renderFormattedText(title))
        ),
        slide.getSlot("subtitle").map(subtitle =>
          h2(cls := "slide-subtitle")(renderFormattedText(subtitle))
        )
      ),
      // Footer section (bottom-justified)
      slide.getSlot("author").map(author =>
        div(cls := "title-slide-footer")(
          p(cls := "slide-author")(renderFormattedText(author))
        )
      )
    )

  /**
   * Render a content slide.
   */
  private def renderContentSlide(slide: Slide): Frag =
    div(cls := "content-slide")(
      slide.getSlot("heading").map(heading =>
        h2(cls := "slide-heading")(renderFormattedText(heading))
      ),
      slide.getSlot("body").map(body =>
        div(cls := "slide-body")(
          renderFormattedText(body)
        )
      )
    )

  /**
   * Render formatted text with inline markdown.
   *
   * Parses markdown once and renders all content including links.
   *
   * @param text Plain text or markdown text
   * @return HTML fragments with formatting
   */
  private def renderFormattedText(text: String): Frag =
    val formatted = FlexmarkAdapter.parseInlineFormatting(text)
    renderFormattedContentWithParagraphs(formatted)

  /**
   * Render FormattedContent to HTML with paragraph structure, code blocks, and images.
   *
   * Converts TextSpan, Link, CodeBlock, and ContentImage domain objects to HTML.
   * Splits text into paragraphs and renders code blocks and images separately.
   *
   * @param content Formatted content from domain
   * @return HTML fragments
   */
  private def renderFormattedContentWithParagraphs(content: FormattedContent): Frag =
    // Build a map of link text → URL for easy lookup
    val linkMap = content.links.map(link => link.text -> link.url).toMap

    // Split text spans by newlines to create paragraphs
    val spans = content.textSpans
    val paragraphs = scala.collection.mutable.ListBuffer.empty[List[TextSpan]]
    val currentParagraph = scala.collection.mutable.ListBuffer.empty[TextSpan]

    spans.foreach { span =>
      if span.text.contains("\n") then
        // Split on newlines
        val lines = span.text.split("\n", -1)
        lines.zipWithIndex.foreach { case (line, idx) =>
          if line.nonEmpty then
            currentParagraph += TextSpan(line, span.bold, span.italic, span.code)

          if idx < lines.length - 1 then
            // Newline encountered - finish current paragraph
            if currentParagraph.nonEmpty then
              paragraphs += currentParagraph.toList
              currentParagraph.clear()
        }
      else
        currentParagraph += span
    }

    // Add final paragraph
    if currentParagraph.nonEmpty then
      paragraphs += currentParagraph.toList

    // Render paragraphs
    val paragraphFrags = if paragraphs.isEmpty then
      Seq.empty[Frag]
    else
      paragraphs.toList.map { paraSpans =>
        p(paraSpans.map(span => renderTextSpanWithLinks(span, linkMap)))
      }

    // Render code blocks
    val codeBlockFrags = content.codeBlocks.map(renderCodeBlock)

    // Render content images
    val imageFrags = content.contentImages.map(renderContentImage)

    // Render unordered lists
    val unorderedListFrags = content.unorderedLists.map(renderUnorderedList(_, linkMap))

    // Render ordered lists
    val orderedListFrags = content.orderedLists.map(renderOrderedList(_, linkMap))

    // Combine paragraphs, code blocks, images, and lists
    Seq(paragraphFrags, codeBlockFrags, imageFrags, unorderedListFrags, orderedListFrags)

  /**
   * Render a TextSpan, converting to link if text matches a link.
   */
  private def renderTextSpanWithLinks(span: TextSpan, linkMap: Map[String, String]): Frag =
    linkMap.get(span.text) match
      case Some(url) =>
        // This text is a link
        a(href := url)(span.text)
      case None =>
        // Regular text span
        renderTextSpan(span)

  /**
   * Render a single TextSpan to HTML.
   *
   * Applies bold, italic, and code formatting.
   */
  private def renderTextSpan(span: TextSpan): Frag =
    val text: Frag = span.text

    if span.code then
      tag("code")(text)
    else if span.bold && span.italic then
      tag("strong")(tag("em")(text))
    else if span.bold then
      tag("strong")(text)
    else if span.italic then
      tag("em")(text)
    else
      text

  /**
   * Render a code block to HTML.
   *
   * Creates <pre><code> tags with optional language class.
   * For MVP, we're not doing syntax highlighting yet (no highlight.js).
   * Theme colors are applied via CSS classes.
   *
   * @param codeBlock The code block to render
   * @return HTML <pre><code> element
   */
  private def renderCodeBlock(codeBlock: CodeBlock): Frag =
    val languageClass = codeBlock.language.map(lang => s"language-$lang").getOrElse("")
    val codeTag = if languageClass.nonEmpty then
      tag("code")(cls := languageClass)(codeBlock.code)
    else
      tag("code")(codeBlock.code)

    tag("pre")(codeTag)

  /**
   * Render content image to HTML (US-005).
   *
   * Creates <img> tag with alt text for accessibility (PDR-005, PDR-008).
   *
   * @param image Content image from domain
   * @return HTML <img> element
   */
  private def renderContentImage(image: ContentImage): Frag =
    img(
      cls := "content-image",
      src := image.url,
      alt := image.altText
    )

  /**
   * Render an unordered list (bullet points).
   *
   * @param list The UnorderedList to render
   * @param linkMap Map of link text to URLs for rendering links
   * @return HTML <ul> fragment
   */
  private def renderUnorderedList(list: com.tjmsolutions.mdslides.domain.UnorderedList, linkMap: Map[String, String]): Frag =
    ul(
      list.items.map { item =>
        // Build the fragments for this list item (US-003.3)
        val textFrags = item.textSpans.map(span => renderTextSpanWithLinks(span, linkMap))
        val nestedUnorderedFrags = item.nestedUnorderedLists.map(nestedList => renderUnorderedList(nestedList, linkMap))
        val nestedOrderedFrags = item.nestedOrderedLists.map(nestedList => renderOrderedList(nestedList, linkMap))

        // Concatenate all fragments and pass to li()
        li((textFrags ++ nestedUnorderedFrags ++ nestedOrderedFrags): _*)
      }
    )

  /**
   * Render an ordered list (numbered items).
   *
   * @param list The OrderedList to render
   * @param linkMap Map of link text to URLs for rendering links
   * @return HTML <ol> fragment
   */
  private def renderOrderedList(list: com.tjmsolutions.mdslides.domain.OrderedList, linkMap: Map[String, String]): Frag =
    ol(
      list.items.map { item =>
        // Build the fragments for this list item (US-003.3)
        val textFrags = item.textSpans.map(span => renderTextSpanWithLinks(span, linkMap))
        val nestedUnorderedFrags = item.nestedUnorderedLists.map(nestedList => renderUnorderedList(nestedList, linkMap))
        val nestedOrderedFrags = item.nestedOrderedLists.map(nestedList => renderOrderedList(nestedList, linkMap))

        // Concatenate all fragments and pass to li()
        li((textFrags ++ nestedUnorderedFrags ++ nestedOrderedFrags): _*)
      }
    )

  /**
   * Navigation JavaScript with code block auto-scaling.
   *
   * @param totalSlides Total number of slides
   * @return JavaScript code for keyboard navigation and code scaling
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

    // Auto-scale code blocks that exceed 20 lines (PDR-006)
    function scaleCodeBlocks() {
      document.querySelectorAll('pre code').forEach(codeBlock => {
        const lines = codeBlock.textContent.split('\\n').length;
        if (lines > 20) {
          // Calculate scale factor: 20 lines = 1.0, 40 lines = 0.7, 60+ lines = 0.5
          const scaleFactor = Math.max(0.5, 1.0 - ((lines - 20) * 0.015));
          codeBlock.parentElement.style.fontSize = (18 * scaleFactor) + 'px';
        }
      });
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

    // Run auto-scaling on page load
    window.addEventListener('DOMContentLoaded', scaleCodeBlocks);
  """

end HTMLRenderer
