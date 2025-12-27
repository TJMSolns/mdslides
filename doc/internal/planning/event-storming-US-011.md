# Event Storming: US-011 Per-Slide Background Images

**Date:** 2024-12-26
**User Story:** US-011 - Per-Slide Background Images
**Related:** PDR-011, US-016, v0.4.0-decisions-summary.md

---

## User Story

**As a** presentation author
**I want** to override the theme background on specific slides
**So that** I can use different visuals for title vs content slides

---

## Domain Events (Orange Stickies)

Events that occur in the system, past tense:

1. **SlideFrontmatterParsed** - Frontmatter extracted from markdown slide
2. **SlideBackgroundSpecified** - Author specified background in frontmatter
3. **BackgroundSyntaxValidated** - Background value validated (string OR object)
4. **SlideBackgroundResolved** - Slide background path resolved (relative to slide deck)
5. **SlideBackgroundOverridden** - Slide background takes precedence over theme/template
6. **InvalidBackgroundSyntaxDetected** - Background syntax invalid (neither string nor object)
7. **BackgroundImageNotFound** - Referenced background image missing from filesystem

---

## Commands (Blue Stickies)

Actions triggered by actors:

1. **ParseSlideFrontmatter** - Extract background field from YAML frontmatter
2. **ValidateBackgroundSyntax** - Check if background is valid string OR object
3. **ResolveSlideBackgroundPath** - Convert relative path to absolute (relative to slide deck directory)
4. **ApplyBackgroundFallbackChain** - Determine effective background (slide → template → theme)
5. **RenderSlideWithBackground** - Generate HTML with appropriate background CSS

---

## Aggregates (Yellow Stickies)

Domain entities that process commands and emit events:

### Slide (existing - enhanced)
- **Responsibility:** Represents a single presentation slide
- **Properties:** id, templateName, slots, **backgroundImage** (NEW)
- **Invariants:**
  - backgroundImage is optional (None = use template/theme default)
  - backgroundImage must be valid SlideBackground (String | BackgroundConfig)
  - templateName must reference valid template

### SlideBackground (new value object)
- **Responsibility:** Represents per-slide background override
- **Union Type:** `String | BackgroundConfig`
- **Simple form:** `"images/custom-bg.png"` (just path)
- **Extended form:** `BackgroundConfig(image: String, opacity: Option[Double])`
- **Invariants:**
  - image path must be non-empty string
  - opacity (if present) must be 0.0 to 1.0

### BackgroundConfig (new value object)
- **Responsibility:** Extended background configuration
- **Properties:** image (String), opacity (Option[Double])
- **Invariants:**
  - image must be non-empty
  - opacity must be in range [0.0, 1.0]
  - Future: position, blendMode, etc.

---

## Read Models (Green Stickies)

Data projections for queries:

1. **EffectiveSlideBackground** - Computed background after fallback chain (slide → template → theme)
2. **SlideImageReferences** - All images referenced by slide (content + background)
3. **BackgroundValidationReport** - List of slides with missing background images

---

## Policies (Purple Stickies)

Business rules connecting events:

1. **When SlideFrontmatterParsed → ValidateBackgroundSyntax**
   - Check if background field is string OR object
   - Emit InvalidBackgroundSyntaxDetected if neither

2. **When BackgroundSyntaxValidated → ResolveSlideBackgroundPath**
   - Extract image path (from string or object.image)
   - Resolve relative to slide deck directory

3. **When SlideBackgroundResolved → Check Image Exists**
   - Verify image file exists
   - If missing: Emit BackgroundImageNotFound (warn, don't fail)

4. **When Rendering Slide → ApplyBackgroundFallbackChain**
   - Priority: slide.backgroundImage > theme.templateBackgrounds[templateName] > theme.background.image
   - Use first non-None value

5. **When SlideBackgroundSpecified → SlideBackgroundOverridden**
   - Slide-level background takes precedence
   - Template and theme backgrounds ignored for this slide

---

## External Systems (Pink Stickies)

External dependencies:

1. **File System** - Read slide deck markdown files, verify background images exist
2. **Markdown Parser** - Extract YAML frontmatter from slides
3. **Image Copier** - Copy slide background images to output (US-006 integration)
4. **Theme System** - Fallback to theme/template backgrounds when slide has none

---

## Timeline (Left to Right Flow)

```
User Action: Author writes slide with custom background

---
template: title
background: images/hero-image.png
---

# Welcome to MDSlides

↓

[ParseSlideFrontmatter Command]
  ↓
[SlideFrontmatterParsed Event]
  templateName: "title"
  background: "images/hero-image.png"
  ↓
[ValidateBackgroundSyntax Command]
  ↓
IF valid string:
  [BackgroundSyntaxValidated Event]
    type: String
    value: "images/hero-image.png"
    ↓
  [ResolveSlideBackgroundPath Command]
    relativePath: "images/hero-image.png"
    slideDir: "/path/to/slides/"
    → absolutePath: "/path/to/slides/images/hero-image.png"
    ↓
  [SlideBackgroundResolved Event]
    ↓
  [Check Image Exists]
    ↓
  IF exists:
    [Create Slide Aggregate]
      backgroundImage: Some("images/hero-image.png")
      ↓
    [During Rendering...]
      ↓
    [ApplyBackgroundFallbackChain Command]
      slide.backgroundImage: Some("images/hero-image.png")  ← USE THIS
      theme.templateBackgrounds["title"]: Some("backgrounds/title.png")  ← IGNORED
      theme.background.image: Some("backgrounds/default.png")  ← IGNORED
      ↓
    [SlideBackgroundOverridden Event]
      effectiveBackground: "images/hero-image.png"
      ↓
    [RenderSlideWithBackground Command]
      CSS: background-image: url('images/hero-image.png');
      ↓
    [During Image Copying Phase...]
      ↓
    Copy: slides/images/hero-image.png → output/images/hero-image.png

  ELSE image not found:
    [BackgroundImageNotFound Event]
      ↓
    [Validation Warning Policy]
      ⚠ Warning: Background image not found: images/hero-image.png
      ↓
    [Create Slide Aggregate Anyway]
      backgroundImage: Some("images/hero-image.png")  (store anyway)
      ↓
    [Continue rendering with fallback...]

ELSE invalid syntax:
  [InvalidBackgroundSyntaxDetected Event]
    ↓
  Error: Invalid background syntax in slide
         Expected: string OR {image: "path", opacity: 0.5}
         Got: [1, 2, 3]
```

---

## Extended Syntax Example (Object Form)

```yaml
---
template: title
background:
  image: images/hero-image.png
  opacity: 0.7
---

# Welcome to MDSlides
```

**Flow Difference:**
```
[ValidateBackgroundSyntax Command]
  ↓
[BackgroundSyntaxValidated Event]
  type: BackgroundConfig
  image: "images/hero-image.png"
  opacity: 0.7
  ↓
[ResolveSlideBackgroundPath Command]
  (same as string form, extracts image field)
  ↓
[RenderSlideWithBackground Command]
  CSS: background-image: url('images/hero-image.png');
       opacity: 0.7;  ← ADDITIONAL
```

---

## Hotspots (Red Stickies)

Areas of complexity or risk:

1. **🔥 Union Type Parsing (String | Object)**
   - **Issue:** YAML frontmatter can be string OR object for background field
   - **Risk:** Parser must handle both forms correctly
   - **Mitigation:** Pattern match on parsed YAML node type
   - **Test:** Verify both `background: "path"` and `background: {image: "path"}` work

2. **🔥 Path Resolution Context**
   - **Issue:** Slide backgrounds relative to slide deck, theme backgrounds relative to theme dir
   - **Risk:** Mixing up resolution contexts leads to broken images
   - **Mitigation:** Explicitly track source directory per image (ImageSource case class)
   - **Test:** Slide with background + theme with background → both resolve correctly

3. **🔥 Fallback Chain Complexity**
   - **Issue:** Three-level fallback: slide → template → theme
   - **Risk:** Precedence bugs (wrong background shown)
   - **Mitigation:** Single function implementing chain with clear priority
   - **Test:** Verify all combinations (slide only, template only, theme only, slide+template, etc.)

4. **🔥 Graceful Degradation**
   - **Issue:** What happens when slide background image missing?
   - **Risk:** Build fails or silent failure
   - **Mitigation:** Warn but don't fail, fall back to template/theme background
   - **Decision:** Per PDR-011, warn during validation but continue rendering
   - **Test:** Missing slide background → warning shown, fallback used

5. **🔥 Image Copying Integration**
   - **Issue:** Slide backgrounds must be copied alongside content images
   - **Risk:** Slide background referenced but not copied to output
   - **Mitigation:** Extract slide backgrounds during image discovery phase (US-006)
   - **Test:** Slide with background → image copied to output/images/

---

## Questions & Decisions

### Q1: Should invalid background syntax fail the build?
**A:** No, emit validation error and skip background (fall back to template/theme)

**Rationale:**
- Consistent with graceful degradation philosophy (PDR-011)
- Better UX than cryptic build failure
- Author can fix based on clear error message

### Q2: How do we parse union type (String | BackgroundConfig)?
**A:** Pattern match on YAML node type

**Implementation:**
```scala
def parseBackground(yamlNode: YamlNode): Option[SlideBackground] =
  yamlNode match
    case YamlScalar(value) =>
      // Simple string form
      Some(value)  // String is valid SlideBackground

    case YamlMapping(fields) =>
      // Object form
      fields.get("image") match
        case Some(imagePath) =>
          val opacity = fields.get("opacity").flatMap(_.toDoubleOption)
          Some(BackgroundConfig(imagePath, opacity))
        case None =>
          None  // Invalid: object without 'image' field

    case _ =>
      None  // Invalid: neither string nor object
```

### Q3: Should we validate opacity range at parse time or render time?
**A:** Parse time (fail fast)

**Validation:**
```scala
case class BackgroundConfig(
  image: String,
  opacity: Option[Double] = None
):
  require(image.nonEmpty, "Background image path cannot be empty")
  opacity.foreach { o =>
    require(o >= 0.0 && o <= 1.0, s"Opacity must be 0.0-1.0, got $o")
  }
```

### Q4: Should slide backgrounds be copied during US-011 or rely on US-006?
**A:** Integrate with existing US-006 image copying

**Integration Point:**
```scala
// ImageAssetCopier (from US-006)
def extractAllImages(
  deck: SlideDeck,
  theme: Theme,
  themeDir: Path,
  slideDir: Path
): List[ImageSource] =

  val contentImages = deck.slides.flatMap(extractContentImages)
    .map(path => ImageSource(path, slideDir))

  val slideBackgrounds = deck.slides.flatMap(extractSlideBackground)  // NEW
    .map(path => ImageSource(path, slideDir))

  val themeImages = extractThemeImages(theme)
    .map(path => ImageSource(path, themeDir))

  (contentImages ++ slideBackgrounds ++ themeImages).distinct

def extractSlideBackground(slide: Slide): Option[String] =
  slide.backgroundImage.map {
    case path: String => path
    case config: BackgroundConfig => config.image
  }
```

---

## Anti-Corruption Layer

**Markdown → Domain boundary:**

```scala
// Infrastructure: SlideParser
object SlideParser:
  def parseSlide(markdown: String): Either[String, Slide] =
    for
      frontmatter <- extractFrontmatter(markdown)
      templateName <- frontmatter.get("template").toRight("Missing template")
      backgroundImage <- parseBackgroundField(frontmatter)  // NEW
      slots <- parseSlots(markdown)
    yield Slide(
      id = SlideId.generate(),
      templateName = templateName,
      slots = slots,
      backgroundImage = backgroundImage
    )

  private def parseBackgroundField(frontmatter: Map[String, YamlNode]): Either[String, Option[SlideBackground]] =
    frontmatter.get("background") match
      case None =>
        Right(None)  // No background specified

      case Some(YamlScalar(path)) =>
        Right(Some(path))  // String form

      case Some(YamlMapping(fields)) =>
        fields.get("image") match
          case Some(imagePath) =>
            val opacity = fields.get("opacity").flatMap(_.toDoubleOption)

            // Validate opacity range
            opacity match
              case Some(o) if o < 0.0 || o > 1.0 =>
                Left(s"Invalid opacity: $o (must be 0.0-1.0)")
              case _ =>
                Right(Some(BackgroundConfig(imagePath, opacity)))

          case None =>
            Left("Background object missing required 'image' field")

      case Some(_) =>
        Left("Invalid background syntax (expected string or object)")

// Domain: Slide (unchanged)
case class Slide(
  id: SlideId,
  templateName: String,
  slots: Map[String, String],
  backgroundImage: Option[SlideBackground] = None
)
```

**Key insight:** Slide aggregate doesn't know background came from frontmatter. Frontmatter parsing is purely an infrastructure concern.

---

## Acceptance Tests (BDD Scenarios)

### Scenario 1: Slide with simple string background
```gherkin
Given a slide with frontmatter:
  """
  ---
  template: title
  background: images/hero.png
  ---
  """
And the file "slides/images/hero.png" exists
When the presentation is rendered
Then the slide should use background "images/hero.png"
And "images/hero.png" should be copied to "output/images/hero.png"
```

### Scenario 2: Slide with extended object background
```gherkin
Given a slide with frontmatter:
  """
  ---
  template: title
  background:
    image: images/hero.png
    opacity: 0.7
  ---
  """
And the file "slides/images/hero.png" exists
When the presentation is rendered
Then the slide should use background "images/hero.png" with opacity 0.7
And "images/hero.png" should be copied to "output/images/hero.png"
```

### Scenario 3: Slide background overrides theme background
```gherkin
Given a theme with default background "backgrounds/default.png"
And a slide with frontmatter:
  """
  ---
  template: content
  background: images/custom.png
  ---
  """
When the presentation is rendered
Then the slide should use background "images/custom.png"
And the theme background "backgrounds/default.png" should NOT be used for this slide
```

### Scenario 4: Slide background overrides template background
```gherkin
Given a theme with template background for "title": "backgrounds/title.png"
And a slide with frontmatter:
  """
  ---
  template: title
  background: images/custom-title.png
  ---
  """
When the presentation is rendered
Then the slide should use background "images/custom-title.png"
And the template background "backgrounds/title.png" should NOT be used for this slide
```

### Scenario 5: Missing slide background falls back gracefully
```gherkin
Given a slide with frontmatter:
  """
  ---
  template: content
  background: images/nonexistent.png
  ---
  """
And the file "slides/images/nonexistent.png" does NOT exist
And the theme has template background for "content": "backgrounds/content.png"
When the presentation is rendered
Then a warning should be displayed: "Background image not found: images/nonexistent.png"
And the slide should fall back to template background "backgrounds/content.png"
And the build should NOT fail
```

### Scenario 6: Invalid background syntax shows clear error
```gherkin
Given a slide with frontmatter:
  """
  ---
  template: content
  background: [1, 2, 3]
  ---
  """
When the presentation is parsed
Then an error should be displayed: "Invalid background syntax (expected string or object)"
And the slide should be created without background (use template/theme default)
```

### Scenario 7: Invalid opacity range rejected
```gherkin
Given a slide with frontmatter:
  """
  ---
  template: content
  background:
    image: images/bg.png
    opacity: 1.5
  ---
  """
When the presentation is parsed
Then an error should be displayed: "Invalid opacity: 1.5 (must be 0.0-1.0)"
And the slide should NOT be created
```

---

## Implementation Tasks (TDD Order)

### Phase 1: Domain - SlideBackground Value Object
1. **Test:** SlideBackground accepts simple string
2. **Test:** BackgroundConfig validates image non-empty
3. **Test:** BackgroundConfig validates opacity range (0.0-1.0)
4. **Test:** BackgroundConfig with no opacity uses default (1.0)

### Phase 2: Domain - Slide Aggregate Enhancement
5. **Test:** Slide accepts backgroundImage field (Option[SlideBackground])
6. **Test:** Slide with no backgroundImage defaults to None
7. **Test:** Slide with string backgroundImage stores correctly
8. **Test:** Slide with BackgroundConfig backgroundImage stores correctly

### Phase 3: Infrastructure - Frontmatter Parsing
9. **Test:** Parse string background: `background: "path"`
10. **Test:** Parse object background: `background: {image: "path"}`
11. **Test:** Parse object with opacity: `background: {image: "path", opacity: 0.7}`
12. **Test:** Invalid background syntax returns error
13. **Test:** Missing 'image' field in object returns error
14. **Test:** Invalid opacity value returns error

### Phase 4: Rendering - Fallback Chain
15. **Test:** Slide background takes precedence over template background
16. **Test:** Slide background takes precedence over theme background
17. **Test:** Template background used when slide has no background
18. **Test:** Theme background used when slide and template have no background
19. **Test:** No background when slide/template/theme all lack backgrounds

### Phase 5: Integration - Image Copying
20. **Test:** Slide background image extracted and added to copy list
21. **Test:** Slide background copied to output preserving directory structure
22. **Test:** Slide background respects --no-copy-images flag
23. **Test:** Slide background + content images + theme images all copied

### Phase 6: Validation - Graceful Degradation
24. **Test:** Missing slide background image shows warning
25. **Test:** Missing slide background falls back to template background
26. **Test:** Missing slide background falls back to theme background
27. **Test:** Build continues despite missing slide background

---

## Success Metrics

- ✅ 100% of frontmatter background syntax variants work (string, object, opacity)
- ✅ Fallback chain behaves correctly in all 8 combinations (slide/template/theme present/absent)
- ✅ Zero rendering bugs related to background precedence
- ✅ Graceful degradation: warnings shown, build doesn't fail
- ✅ Image copying integrates seamlessly (no duplicate code)

---

**Event Storming Complete**
**Next Step:** Implement TDD tests for Phase 1
