# Three Amigos Session #016
## Render SlideDeck to HTML

---

```yaml
# MACHINE-READABLE METADATA
session:
  id: 3A-016-HTML-RENDERING
  date: 2024-12-20
  user_story_id: US-016
  participants:
    - Tony Moores (Business/Dev/QA)
  duration_minutes: 60-90
  status: complete

story:
  title: Render SlideDeck to HTML
  type: Feature
  priority: P0 (Blocker)
  epic: HTML Rendering
```

---

## 📋 User Story

**As a** slide deck author
**I want to** generate HTML from my slide deck
**So that** I can view my presentation in a browser

**Business Value**: Minimal render target for MVP - proves the entire pipeline works (parse → validate → theme → render)

**Technical Scope**: Transform validated SlideDeck into standalone HTML file with theme CSS, keyboard navigation, and slide numbers

---

## 🎭 Three Perspectives

### 👔 Business Perspective (Product Owner)

**What success looks like**:
- Author runs MDSlides CLI → HTML file generated
- HTML opens in any modern browser (Chrome, Firefox, Safari, Edge)
- Slides display with theme styling (colors, fonts, layout)
- Keyboard navigation works (arrow keys, space, home, end)
- Slide numbers displayed (e.g., "3 / 10")
- No external dependencies (single standalone HTML file)
- Presentation works offline

**v1.0 Capabilities**:
- ✅ Render title slide template
- ✅ Render content slide template
- ✅ Apply theme CSS (colors, fonts, layout)
- ✅ Keyboard navigation (→, ←, Space, Home, End)
- ✅ Slide numbers (current / total)
- ✅ Standalone HTML (CSS + JS inlined)

**v1.0 Limitations** (deferred):
- ❌ Speaker notes rendering (v1.1 - US-034)
- ❌ PDF export (v2.0)
- ❌ Print styling (v1.1)
- ❌ Two-screen presenter mode (v1.1)

**Acceptance criteria**:
- ✅ Render title slide with title, subtitle, author
- ✅ Render content slide with heading, body
- ✅ Theme CSS inlined into `<style>` tag
- ✅ Theme layout applied (slide dimensions, padding)
- ✅ Keyboard navigation functional
- ✅ Slide counter displayed
- ✅ Markdown formatted (bold, italics, code, lists)
- ✅ HTML is valid (passes W3C validator)
- ✅ No external dependencies (works offline)

---

### 💻 Development Perspective (Technical)

**Implementation approach**:
1. **Technology Stack**:
   - **Scalatags**: Type-safe HTML generation
   - **Flexmark**: Markdown → HTML conversion
   - **Theme CSS**: Generated from Theme object

2. **HTML Structure**:
   ```html
   <!DOCTYPE html>
   <html lang="en">
   <head>
     <meta charset="UTF-8">
     <meta name="viewport" content="width=device-width, initial-scale=1.0">
     <title>Slide Deck Title</title>
     <style>
       /* Theme CSS inlined here */
       body { ... }
       .slide { ... }
       .slide.active { display: block; }
       .slide.inactive { display: none; }
     </style>
   </head>
   <body>
     <div class="slide-container">
       <div class="slide active" id="slide-1">
         <!-- Slide 1 content -->
       </div>
       <div class="slide inactive" id="slide-2">
         <!-- Slide 2 content -->
       </div>
       ...
     </div>
     <div class="slide-counter">1 / 10</div>
     <script>
       /* Navigation JS inlined here */
       let currentSlide = 1;
       function nextSlide() { ... }
       function prevSlide() { ... }
       document.addEventListener('keydown', ...);
     </script>
   </body>
   </html>
   ```

3. **Rendering Pipeline**:
   ```scala
   def renderSlideDeck(deck: SlideDeck, theme: Theme): Html = {
     val slideHtml = deck.slides.map(slide => renderSlide(slide, theme))
     val themeCSS = generateThemeCSS(theme)
     val navigationJS = generateNavigationJS(deck.slides.length)

     html(
       head(
         meta(charset := "UTF-8"),
         titleTag(deck.title.getOrElse("Slide Deck")),
         style(themeCSS)
       ),
       body(
         div(cls := "slide-container")(slideHtml),
         div(cls := "slide-counter")(s"1 / ${deck.slides.length}"),
         script(navigationJS)
       )
     )
   }

   def renderSlide(slide: Slide, theme: Theme): Html = {
     slide.template match {
       case "title" => renderTitleSlide(slide, theme)
       case "content" => renderContentSlide(slide, theme)
       case _ => renderContentSlide(slide, theme) // fallback
     }
   }
   ```

4. **Markdown Rendering**:
   - Use Flexmark to convert markdown → HTML
   - Preserve inline formatting (bold, italics, code)
   - Render lists (bullet, numbered)
   - Render code blocks with syntax highlighting (if language specified)

5. **Theme CSS Generation**:
   ```scala
   def generateThemeCSS(theme: Theme): String = {
     s"""
     body {
       background-color: ${theme.colors.background};
       color: ${theme.colors.foreground};
       font-family: ${theme.fonts.family};
       font-size: ${theme.fonts.bodySize}px;
     }
     .slide {
       width: ${theme.layout.slideWidth}px;
       height: ${theme.layout.slideHeight}px;
       padding: ${theme.layout.slidePadding}px;
     }
     h1, h2 {
       color: ${theme.colors.heading};
       font-size: ${theme.fonts.headingSize}px;
     }
     ...
     """
   }
   ```

6. **Navigation JavaScript**:
   ```javascript
   let currentSlide = 1;
   const totalSlides = <TOTAL>;

   function showSlide(n) {
     document.querySelectorAll('.slide').forEach(s => s.classList.remove('active'));
     document.querySelector(`#slide-${n}`).classList.add('active');
     document.querySelector('.slide-counter').textContent = `${n} / ${totalSlides}`;
     currentSlide = n;
   }

   function nextSlide() {
     if (currentSlide < totalSlides) showSlide(currentSlide + 1);
   }

   function prevSlide() {
     if (currentSlide > 1) showSlide(currentSlide - 1);
   }

   document.addEventListener('keydown', (e) => {
     if (e.key === 'ArrowRight' || e.key === ' ') nextSlide();
     if (e.key === 'ArrowLeft') prevSlide();
     if (e.key === 'Home') showSlide(1);
     if (e.key === 'End') showSlide(totalSlides);
   });
   ```

**Technical risks**:
- **Markdown rendering**: Flexmark may produce unexpected HTML for edge cases
- **Browser compatibility**: Test on Chrome, Firefox, Safari, Edge
- **File size**: Inlining CSS/JS increases HTML file size
  - **Mitigation**: Minify CSS/JS before inlining
- **Scalatags learning curve**: Team must learn Scalatags DSL

**Dependencies**:
- Scalatags (HTML generation)
- Flexmark (Markdown rendering)
- Theme (US-008)
- Validated SlideDeck (US-011, 012, 013, 015)

---

### 🧪 Testing Perspective (Quality/Edge Cases)

**Happy path scenarios**:
1. Single title slide → HTML with 1 slide
2. 5-slide deck (1 title + 4 content) → HTML with 5 slides
3. Markdown formatting (bold, italics, code) → rendered correctly
4. Theme colors/fonts applied → visible in HTML
5. Keyboard navigation (→, ←) → changes slides
6. Slide counter → displays "1 / 5", updates on navigation

**Edge cases**:
1. Markdown with special characters (<, >, &) → escaped correctly
2. Long slide content → doesn't overflow slide dimensions
3. Empty slide (no content) → renders blank slide
4. 200-slide deck → HTML file size reasonable (<5 MB)
5. Theme with custom fonts → font family applied

**Boundary cases**:
1. Deck with 1 slide → navigation disabled (no next/prev)
2. Deck with exactly 200 slides → all slides rendered
3. Markdown with nested lists → indentation preserved

**Browser Compatibility**:
- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

**Non-functional requirements**:
- Rendering < 500ms for 50-slide deck
- HTML file size < 100 KB per slide (average)
- Valid HTML5 (passes W3C validator)
- Works offline (no CDN dependencies)

---

## 🗂️ Example Mapping

### Rule 1: Render title slide template

**Slot Mapping**:
- `title` → `<h1>`
- `subtitle` → `<h2>`
- `author` → `<p class="author">`

**Examples**:
- ✅ **Valid**: Title slide with all 3 slots → HTML with h1, h2, p.author

**Questions**:
- Q1: What if subtitle is missing?
  - **Decision**: Render only h1 and p.author (subtitle is optional).

---

### Rule 2: Render content slide template

**Slot Mapping**:
- `heading` → `<h2>`
- `body` → `<div class="body">` with markdown rendered

**Examples**:
- ✅ **Valid**: Content slide with heading + body → HTML with h2 + div.body

**Questions**:
- Q2: How to render markdown in body?
  - **Decision**: Use Flexmark to convert markdown → HTML, inject into div.body.

---

### Rule 3: Theme CSS inlined into `<style>` tag

**Examples**:
- ✅ **Valid**: Theme with colors/fonts → `<style>` tag with CSS rules

**Questions**:
- Q3: Minify CSS?
  - **Decision**: Not in v1.0. Minification deferred to v1.1 (optimization).

---

### Rule 4: Keyboard navigation functional

**Keyboard Shortcuts**:
- `→` or `Space`: Next slide
- `←`: Previous slide
- `Home`: First slide
- `End`: Last slide

**Examples**:
- ✅ **Valid**: Press `→` → currentSlide increments, display updates

**Questions**:
- Q4: What if on last slide and press `→`?
  - **Decision**: No-op (stay on last slide).

---

### Rule 5: Slide counter displayed

**Format**: `<current> / <total>` (e.g., "3 / 10")

**Examples**:
- ✅ **Valid**: 10-slide deck on slide 3 → counter shows "3 / 10"

**Questions**:
- Q5: Update counter on navigation?
  - **Decision**: Yes. JavaScript updates counter text on slide change.

---

### Rule 6: Markdown formatted

**Supported Markdown**:
- **Bold**: `**text**` → `<strong>`
- **Italics**: `*text*` → `<em>`
- **Code**: `` `code` `` → `<code>`
- **Lists**: `- item` → `<ul><li>`
- **Code Blocks**: ` ```lang ` → `<pre><code class="language-lang">`

**Examples**:
- ✅ **Valid**: Body with `**bold**` → HTML with `<strong>bold</strong>`

**Questions**:
- Q6: Syntax highlighting for code blocks?
  - **Decision**: v1.0 uses `<pre><code class="language-X">` (no highlighting library). Syntax highlighting deferred to v1.1.

---

### Rule 7: HTML is valid

**Validation**: Passes W3C HTML5 validator

**Examples**:
- ✅ **Valid**: Generated HTML passes validation

**Questions**:
- Q7: How to ensure validity?
  - **Decision**: Scalatags generates valid HTML by construction (type-safe). Test with W3C validator.

---

### Rule 8: No external dependencies

**Examples**:
- ✅ **Valid**: HTML works offline (no CDN links)

**Questions**:
- Q8: Custom fonts from Google Fonts?
  - **Decision**: Not in v1.0. Only system fonts. Web fonts deferred to v1.1.

---

## 📝 Concrete Examples (Given/When/Then)

### Example 1: Render Title Slide

```gherkin
Feature: HTML Rendering

  Scenario: Render title slide with all slots
    Given I have a validated SlideDeck with 1 title slide:
      """
      ---
      template: title
      ---
      # My Presentation
      ## A Great Talk
      By: Tony Moores
      """
    And I have a theme (default)
    When I render the SlideDeck to HTML
    Then HTML contains:
      """
      <div class="slide active" id="slide-1">
        <h1>My Presentation</h1>
        <h2>A Great Talk</h2>
        <p class="author">By: Tony Moores</p>
      </div>
      """
```

---

### Example 2: Render Content Slide

```gherkin
  Scenario: Render content slide with markdown body
    Given I have a validated SlideDeck with 1 content slide:
      """
      ---
      template: content
      ---
      ## Key Takeaways

      Functional programming **reduces bugs** and *improves maintainability*.
      """
    When I render the SlideDeck to HTML
    Then HTML contains:
      """
      <div class="slide active" id="slide-1">
        <h2>Key Takeaways</h2>
        <div class="body">
          <p>Functional programming <strong>reduces bugs</strong> and <em>improves maintainability</em>.</p>
        </div>
      </div>
      """
```

---

### Example 3: Theme CSS Inlined

```gherkin
  Scenario: Theme CSS inlined into HTML
    Given I have a theme with:
      - background: "#FFFFFF"
      - foreground: "#000000"
      - font family: "Arial, sans-serif"
    When I render the SlideDeck to HTML
    Then HTML contains:
      """
      <style>
        body {
          background-color: #FFFFFF;
          color: #000000;
          font-family: Arial, sans-serif;
        }
        ...
      </style>
      """
```

---

### Example 4: Keyboard Navigation

```gherkin
  Scenario: Navigate slides with arrow keys
    Given I have a 5-slide deck rendered to HTML
    And I open the HTML in a browser
    When I press the right arrow key
    Then slide 2 is displayed
    And slide counter shows "2 / 5"
```

---

### Example 5: Slide Counter Updates

```gherkin
  Scenario: Slide counter updates on navigation
    Given I have a 10-slide deck on slide 1
    When I press right arrow 2 times
    Then slide counter shows "3 / 10"
```

---

### Example 6: Markdown Special Characters Escaped

```gherkin
  Scenario: Markdown with HTML special characters
    Given I have a slide with body:
      """
      This is <not> an HTML tag & should be escaped.
      """
    When I render to HTML
    Then HTML contains:
      """
      <p>This is &lt;not&gt; an HTML tag &amp; should be escaped.</p>
      """
```

---

### Example 7: Multi-Slide Deck

```gherkin
  Scenario: Render 5-slide deck
    Given I have a deck with:
      - Slide 1: title
      - Slides 2-5: content
    When I render to HTML
    Then HTML contains 5 div.slide elements
    And only slide 1 has class "active"
    And slides 2-5 have class "inactive"
```

---

### Example 8: Single-Slide Deck (No Navigation)

```gherkin
  Scenario: Deck with 1 slide
    Given I have a 1-slide deck
    When I render to HTML
    And I press right arrow key
    Then slide remains at 1
    And counter shows "1 / 1"
```

---

## 🚧 Open Questions

| ID | Question | Status | Decision |
|----|----------|--------|----------|
| Q1 | Missing subtitle slot? | ✅ Resolved | Render only h1 + author (subtitle optional) |
| Q2 | Render markdown in body? | ✅ Resolved | Use Flexmark |
| Q3 | Minify CSS? | ✅ Resolved | Not in v1.0 (deferred to v1.1) |
| Q4 | Navigate past last slide? | ✅ Resolved | No-op (stay on last slide) |
| Q5 | Update counter on navigation? | ✅ Resolved | Yes, JavaScript updates |
| Q6 | Syntax highlighting? | ✅ Resolved | v1.0 no highlighting (deferred to v1.1) |
| Q7 | Ensure valid HTML? | ✅ Resolved | Scalatags + W3C validator |
| Q8 | Custom web fonts? | ✅ Resolved | Not in v1.0 (system fonts only) |

---

## ✅ Acceptance Criteria (Definition of Done)

### Functional Criteria

1. ✅ **AC1**: Render title slide with title, subtitle, author
2. ✅ **AC2**: Render content slide with heading, body
3. ✅ **AC3**: Theme CSS inlined into `<style>` tag
4. ✅ **AC4**: Theme layout applied (slide dimensions, padding)
5. ✅ **AC5**: Keyboard navigation (→, ←, Space, Home, End)
6. ✅ **AC6**: Slide counter displayed ("current / total")
7. ✅ **AC7**: Markdown formatted (bold, italics, code, lists)
8. ✅ **AC8**: HTML is valid (W3C validator)
9. ✅ **AC9**: No external dependencies (works offline)

### Technical Criteria

10. ✅ **AC10**: Scalatags used for HTML generation
11. ✅ **AC11**: Flexmark used for markdown rendering
12. ✅ **AC12**: CSS/JS inlined (single HTML file)
13. ✅ **AC13**: Markdown special characters escaped
14. ✅ **AC14**: All domain terms from ubiquitous language used

### Non-Functional Criteria

15. ✅ **AC15**: Rendering < 500ms for 50-slide deck
16. ✅ **AC16**: HTML file size < 100 KB per slide
17. ✅ **AC17**: Browser compatibility (Chrome, Firefox, Safari, Edge)
18. ✅ **AC18**: Pure functional rendering (no side effects)

**Scenarios**: 8 concrete examples documented
- 8 success paths

**Dependencies**:
- Scalatags library
- Flexmark library
- Theme (US-008)
- Validated SlideDeck (validation framework)

---

## 📚 Related Artifacts

- **User Story Tracker**: [BACKLOG-V3.md](../../BACKLOG-V3.md)
- **Domain Model**: [slide-deck-aggregate.md](../domain-models/aggregates/slide-deck-aggregate.md)
- **Ubiquitous Language**: [ubiquitous-language.md](../domain-models/ubiquitous-language.md)
- **Related Stories**: US-008 Apply Theme, US-019 CLI Interface

---

## 🎯 Next Steps

1. **Create Example Mapping visual** (Ceremony 2.2)
2. **Document formal acceptance criteria** in BACKLOG-V3.md
3. **Proceed to Ceremony 2.2**: Example Mapping Workshop (refine scenarios)

---

**Session Type**: Ceremony 2.1 - Three Amigos Session
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Next Review**: After Example Mapping Workshop (Ceremony 2.2)
