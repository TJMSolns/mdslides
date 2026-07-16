# Event Storming: US-012 Template-Specific Background Defaults

**Date:** 2024-12-26
**User Story:** US-012 - Template-Specific Background Defaults
**Related:** PDR-011, US-016, US-011, v0.4.0-decisions-summary.md

---

## User Story

**As a** theme designer
**I want** to specify different default backgrounds per template type
**So that** title slides automatically get one background, content slides get another

---

## Domain Events (Orange Stickies)

Events that occur in the system, past tense:

1. **ThemeJsonParsed** - Theme JSON successfully parsed (from US-016)
2. **TemplateBackgroundsExtracted** - templateBackgrounds map parsed from theme
3. **TemplateBackgroundMapped** - Specific template name linked to background image path
4. **TemplateBackgroundResolved** - Background path resolved (relative to theme directory)
5. **TemplateBackgroundApplied** - Slide uses template-specific background (no slide override)
6. **TemplateNotFoundWarning** - Template name in templateBackgrounds doesn't match any known template
7. **TemplateBackgroundImageMissing** - Referenced template background image not found

---

## Commands (Blue Stickies)

Actions triggered by actors:

1. **ParseTemplateBackgrounds** - Extract templateBackgrounds map from theme JSON
2. **ValidateTemplateNames** - Check template names against known templates (warn if unknown)
3. **ResolveTemplateBackgroundPaths** - Convert relative paths to absolute (relative to theme dir)
4. **DetermineEffectiveBackground** - Apply fallback chain: slide → template → theme
5. **ExtractTemplateBackgroundImages** - List all images referenced in templateBackgrounds (for copying)

---

## Aggregates (Yellow Stickies)

Domain entities that process commands and emit events:

### Theme (existing - enhanced)
- **Responsibility:** Represents presentation styling
- **Properties:** name, version, background, colors, fonts, spacing, syntax, slideCounter, **templateBackgrounds** (NEW)
- **Invariants:**
  - templateBackgrounds is a Map[String, String] (template name → image path)
  - Template names are strings (no validation - warnings only)
  - Image paths are strings (validation at render time)
  - templateBackgrounds can be empty map (no template-specific backgrounds)

### TemplateBackgrounds (conceptual - not separate aggregate)
- **Representation:** `Map[String, String]` within Theme
- **Example:** `{"title": "backgrounds/title.png", "content": "backgrounds/content.png"}`
- **Semantics:** Template name (key) → Background image path (value)

---

## Read Models (Green Stickies)

Data projections for queries:

1. **EffectiveSlideBackground** - Computed background after fallback chain (slide → template → theme)
2. **TemplateBackgroundList** - All template-specific backgrounds defined in theme
3. **UnusedTemplateBackgrounds** - Template backgrounds with no corresponding slides (informational)

---

## Policies (Purple Stickies)

Business rules connecting events:

1. **When ThemeJsonParsed → ParseTemplateBackgrounds**
   - Extract templateBackgrounds field (if present)
   - Default to empty map if field missing

2. **When TemplateBackgroundsExtracted → ValidateTemplateNames**
   - Check each template name against known templates
   - Emit TemplateNotFoundWarning if unknown (don't fail)

3. **When TemplateBackgroundsExtracted → ResolveTemplateBackgroundPaths**
   - For each image path in map, resolve relative to theme directory
   - Store resolved paths for rendering

4. **When Rendering Slide → DetermineEffectiveBackground**
   - Priority: slide.backgroundImage > theme.templateBackgrounds[slide.templateName] > theme.background.image
   - Use first non-None value

5. **When Template Background Selected → Check Image Exists**
   - Verify template background image exists
   - If missing: Emit TemplateBackgroundImageMissing (warn, don't fail)
   - Fall back to theme default background

---

## External Systems (Pink Stickies)

External dependencies:

1. **File System** - Verify template background images exist in theme directory
2. **Theme Loader** - Load theme JSON and parse templateBackgrounds field
3. **Image Copier** - Copy template background images to output (US-006 integration)
4. **Template Registry** - Known template names for validation (optional)

---

## Timeline (Left to Right Flow)

```
Theme Designer: Creates theme with template-specific backgrounds

themes/retisio/theme.json:
{
  "name": "the prior organization",
  "templateBackgrounds": {
    "title": "backgrounds/title-page.png",
    "content": "backgrounds/content-page.png",
    "diagram": "backgrounds/diagram-page.png"
  }
}

↓

[LoadTheme Command] (from US-016)
  ↓
[ThemeJsonRead Event]
  ↓
[ThemeJsonParsed Event]
  ↓
[ParseTemplateBackgrounds Command]
  ↓
[TemplateBackgroundsExtracted Event]
  templateBackgrounds: {
    "title": "backgrounds/title-page.png",
    "content": "backgrounds/content-page.png",
    "diagram": "backgrounds/diagram-page.png"
  }
  ↓
[ValidateTemplateNames Command]
  knownTemplates: ["title", "content", "section-title", "closing", ...]
  ↓
IF all templates known:
  ✓ All template names valid
  ↓
ELSE unknown templates:
  [TemplateNotFoundWarning Event]
    ⚠ Warning: Template 'diagram' not recognized
       Template backgrounds defined for: title, content, diagram
       Known templates: title, content, section-title, closing
  ↓
[ResolveTemplateBackgroundPaths Command]
  themeDir: themes/retisio/
  "backgrounds/title-page.png" → themes/retisio/backgrounds/title-page.png
  "backgrounds/content-page.png" → themes/retisio/backgrounds/content-page.png
  ↓
[TemplateBackgroundResolved Event] (for each)
  ↓
[Theme aggregate created with templateBackgrounds]
  ↓
[Author creates slide with template "title"...]
  ---
  template: title
  ---
  # Welcome
  ↓
[ParseSlide Command]
  templateName: "title"
  backgroundImage: None  (no slide-level override)
  ↓
[During Rendering...]
  ↓
[DetermineEffectiveBackground Command]
  slide.backgroundImage: None
  theme.templateBackgrounds["title"]: Some("backgrounds/title-page.png")  ← USE THIS
  theme.background.image: Some("backgrounds/default.png")  ← IGNORED
  ↓
[TemplateBackgroundApplied Event]
  effectiveBackground: "backgrounds/title-page.png"
  source: template
  ↓
[Check Image Exists]
  ↓
IF themes/retisio/backgrounds/title-page.png exists:
  [RenderSlideWithBackground Command]
    CSS: background-image: url('backgrounds/title-page.png');
    ↓
  [During Image Copying Phase...]
    ↓
  Copy: themes/retisio/backgrounds/title-page.png → output/backgrounds/title-page.png

ELSE image not found:
  [TemplateBackgroundImageMissing Event]
    ↓
  ⚠ Warning: Template background image not found: backgrounds/title-page.png
  ↓
  [Fall back to theme default background]
    theme.background.image: "backgrounds/default.png"
```

---

## Fallback Chain Examples

### Example 1: Slide with no override, template has background
```
Slide:
  template: "title"
  backgroundImage: None

Theme:
  templateBackgrounds: {"title": "backgrounds/title.png"}
  background.image: "backgrounds/default.png"

Result: "backgrounds/title.png" (template background)
```

### Example 2: Slide overrides template background
```
Slide:
  template: "title"
  backgroundImage: Some("images/custom.png")

Theme:
  templateBackgrounds: {"title": "backgrounds/title.png"}
  background.image: "backgrounds/default.png"

Result: "images/custom.png" (slide background overrides)
```

### Example 3: Template not in templateBackgrounds, falls back to theme
```
Slide:
  template: "section-title"
  backgroundImage: None

Theme:
  templateBackgrounds: {"title": "backgrounds/title.png"}
  background.image: "backgrounds/default.png"

Result: "backgrounds/default.png" (theme default, no template mapping)
```

### Example 4: All three levels specified
```
Slide:
  template: "title"
  backgroundImage: Some("images/hero.png")

Theme:
  templateBackgrounds: {"title": "backgrounds/title.png"}
  background.image: "backgrounds/default.png"

Result: "images/hero.png" (slide wins, template and theme ignored)
```

---

## Hotspots (Red Stickies)

Areas of complexity or risk:

1. **🔥 Unknown Template Names**
   - **Issue:** User specifies template background for template that doesn't exist
   - **Risk:** Silent failure or confusing behavior
   - **Mitigation:** Warn at theme load time (informational, don't fail)
   - **Decision:** Allow unknown template names (theme may be used with custom templates)
   - **Test:** Theme with "custom-template" background → warning shown but accepted

2. **🔥 Fallback Chain Correctness**
   - **Issue:** Three-level fallback (slide → template → theme) must be unambiguous
   - **Risk:** Wrong background selected due to precedence bug
   - **Mitigation:** Single canonical function implementing chain
   - **Test:** Exhaustive test matrix (all combinations of slide/template/theme present/absent)

3. **🔥 Empty templateBackgrounds**
   - **Issue:** What if templateBackgrounds is empty map or missing from JSON?
   - **Risk:** Null pointer or missing field errors
   - **Mitigation:** Default to empty map, treat as "no template-specific backgrounds"
   - **Test:** Theme with no templateBackgrounds field → defaults to empty map

4. **🔥 Theme Migration**
   - **Issue:** Existing themes (v0.3.0) don't have templateBackgrounds field
   - **Risk:** Themes break when loaded in v0.4.0
   - **Mitigation:** Make templateBackgrounds optional, default to empty map
   - **Test:** Load v0.3.0 theme (no templateBackgrounds) → loads successfully

5. **🔥 Image Copying Integration**
   - **Issue:** Template backgrounds must be discovered and copied
   - **Risk:** Template background referenced but not copied to output
   - **Mitigation:** Extract all templateBackgrounds values during image discovery (US-006)
   - **Test:** Theme with templateBackgrounds → all images copied to output

---

## Questions & Decisions

### Q1: Should we validate template names against known templates?
**A:** Warn, but don't fail (allow extensibility)

**Rationale:**
- Themes may define backgrounds for custom templates (future extensibility)
- Better to warn (informational) than reject valid use case
- Theme designer can ignore warning if intentional

**Example:**
```
⚠ Warning: Template 'custom-diagram' in templateBackgrounds is not a built-in template.
   This is OK if you're using custom templates.
   Built-in templates: title, content, section-title, closing
```

### Q2: Should templateBackgrounds be required in theme.json?
**A:** No, optional field (defaults to empty map)

**Rationale:**
- Backward compatibility with v0.3.0 themes
- Not all themes need template-specific backgrounds
- Minimal theme can be just colors/fonts

### Q3: How do we handle missing template background images?
**A:** Warn and fall back to theme default background

**Behavior:**
```
⚠ Warning: Template background image not found: backgrounds/title.png
   Falling back to theme default background
```

**Rationale:**
- Graceful degradation (consistent with PDR-011)
- Build continues, author can fix
- Better than cryptic rendering error

### Q4: Should we support nested maps (e.g., per-template opacity)?
**A:** No, keep simple for v0.4.0 (just path strings)

**Current design:**
```json
{
  "templateBackgrounds": {
    "title": "backgrounds/title.png",
    "content": "backgrounds/content.png"
  }
}
```

**Future extension (v0.5.0+):**
```json
{
  "templateBackgrounds": {
    "title": {
      "image": "backgrounds/title.png",
      "opacity": 0.8
    }
  }
}
```

**Rationale:**
- YAGNI - no user request for per-template opacity yet
- String values simpler to parse and understand
- Can extend later without breaking changes (detect string vs object)

---

## Anti-Corruption Layer

**Infrastructure → Domain boundary:**

```scala
// Infrastructure: ThemeJsonAdapter
object ThemeJsonAdapter:
  def parse(json: String, themeDir: Path): Either[String, Theme] =
    val parsed = parseJson(json)

    // ... existing fields ...

    // NEW: Parse templateBackgrounds (optional field)
    val templateBackgrounds = parsed.get("templateBackgrounds") match
      case Some(YamlMapping(entries)) =>
        entries.collect {
          case (templateName: String, YamlScalar(imagePath: String)) =>
            templateName -> imagePath
        }.toMap

      case Some(_) =>
        return Left("templateBackgrounds must be an object (map)")

      case None =>
        Map.empty[String, String]  // Default to empty map

    Right(Theme(
      // ... existing fields ...
      templateBackgrounds = templateBackgrounds
    ))

// Domain: Theme (enhanced)
case class Theme(
  name: String,
  version: String,
  background: Background,
  colors: ColorScheme,
  fonts: FontScheme,
  spacing: Spacing,
  syntax: SyntaxColors,
  slideCounter: SlideCounter,
  templateBackgrounds: Map[String, String] = Map.empty  // NEW
)
```

**Rendering logic:**

```scala
// Renderer: Determine effective background
def effectiveBackground(slide: Slide, theme: Theme): Option[String] =
  slide.backgroundImage
    .map(extractImagePath)  // Slide override (highest priority)
    .orElse(
      theme.templateBackgrounds.get(slide.templateName)  // Template background
    )
    .orElse(
      theme.background.image  // Theme default (lowest priority)
    )

private def extractImagePath(bg: SlideBackground): String =
  bg match
    case path: String => path
    case config: BackgroundConfig => config.image
```

**Key insight:** Theme aggregate stores templateBackgrounds as simple map. Rendering logic consults map during background resolution.

---

## Acceptance Tests (BDD Scenarios)

### Scenario 1: Template background used when slide has no override
```gherkin
Given a theme with templateBackgrounds:
  """
  {
    "title": "backgrounds/title-page.png",
    "content": "backgrounds/content-page.png"
  }
  """
And a slide with template "title" and no background override
When the presentation is rendered
Then the slide should use background "backgrounds/title-page.png"
```

### Scenario 2: Slide override takes precedence over template background
```gherkin
Given a theme with templateBackgrounds:
  """
  {"title": "backgrounds/title-page.png"}
  """
And a slide with template "title" and background "images/custom.png"
When the presentation is rendered
Then the slide should use background "images/custom.png"
And the template background "backgrounds/title-page.png" should NOT be used
```

### Scenario 3: Theme default used when template not in templateBackgrounds
```gherkin
Given a theme with templateBackgrounds:
  """
  {"title": "backgrounds/title-page.png"}
  """
And a theme default background "backgrounds/default.png"
And a slide with template "content" (not in templateBackgrounds)
When the presentation is rendered
Then the slide should use background "backgrounds/default.png"
```

### Scenario 4: Template backgrounds are copied to output
```gherkin
Given a theme with templateBackgrounds:
  """
  {
    "title": "backgrounds/title.png",
    "content": "backgrounds/content.png"
  }
  """
And the theme directory is "themes/retisio/"
When rendering with default copy-images behavior
Then "themes/retisio/backgrounds/title.png" should be copied to "output/backgrounds/title.png"
And "themes/retisio/backgrounds/content.png" should be copied to "output/backgrounds/content.png"
```

### Scenario 5: Unknown template name shows warning
```gherkin
Given a theme with templateBackgrounds:
  """
  {"custom-template": "backgrounds/custom.png"}
  """
When the theme is loaded
Then a warning should be displayed: "Template 'custom-template' in templateBackgrounds is not a built-in template"
And the theme should load successfully
```

### Scenario 6: Missing template background image shows warning
```gherkin
Given a theme with templateBackgrounds:
  """
  {"title": "backgrounds/missing.png"}
  """
And the file "themes/retisio/backgrounds/missing.png" does NOT exist
And a slide with template "title"
When the presentation is rendered
Then a warning should be displayed: "Template background image not found: backgrounds/missing.png"
And the slide should fall back to theme default background
```

### Scenario 7: Empty templateBackgrounds is valid
```gherkin
Given a theme with no templateBackgrounds field
When the theme is loaded
Then the theme should have templateBackgrounds = {} (empty map)
And slides should use theme default background
```

### Scenario 8: Multiple slides with same template use same background
```gherkin
Given a theme with templateBackgrounds:
  """
  {"content": "backgrounds/content.png"}
  """
And 5 slides with template "content"
When the presentation is rendered
Then all 5 slides should use background "backgrounds/content.png"
And "backgrounds/content.png" should be copied once (no duplicates)
```

---

## Implementation Tasks (TDD Order)

### Phase 1: Domain - Theme Aggregate Enhancement
1. **Test:** Theme accepts templateBackgrounds field (Map[String, String])
2. **Test:** Theme with empty templateBackgrounds defaults to Map.empty
3. **Test:** Theme with no templateBackgrounds field defaults to Map.empty

### Phase 2: Infrastructure - Theme JSON Parsing
4. **Test:** Parse templateBackgrounds object from JSON
5. **Test:** Parse empty templateBackgrounds object → Map.empty
6. **Test:** Missing templateBackgrounds field → Map.empty (no error)
7. **Test:** Invalid templateBackgrounds (not object) returns error
8. **Test:** Invalid templateBackgrounds values (not strings) returns error

### Phase 3: Rendering - Fallback Chain
9. **Test:** Slide with no background → uses template background
10. **Test:** Slide with background → ignores template background
11. **Test:** Template not in map → falls back to theme default
12. **Test:** Template in map + theme default → uses template background
13. **Test:** Empty templateBackgrounds → all slides use theme default

### Phase 4: Validation - Template Names
14. **Test:** Known template name → no warning
15. **Test:** Unknown template name → warning shown
16. **Test:** Multiple unknown template names → all warnings shown
17. **Test:** Unknown template name → theme still loads successfully

### Phase 5: Integration - Image Copying
18. **Test:** Template background images extracted from theme
19. **Test:** Template background images copied to output
20. **Test:** Template background images preserve directory structure
21. **Test:** Unused template backgrounds still copied (all templateBackgrounds values)
22. **Test:** Template backgrounds respect --no-copy-images flag

### Phase 6: Edge Cases
23. **Test:** Missing template background image → warning shown
24. **Test:** Missing template background → falls back to theme default
25. **Test:** Theme with templateBackgrounds but no default background → slides without template mapping have no background
26. **Test:** Multiple slides same template → background copied once (deduplication)

---

## Success Metrics

- ✅ Fallback chain works correctly in all combinations (slide/template/theme)
- ✅ Template backgrounds from retisio.json example work perfectly
- ✅ Unknown template names warned (informational, not error)
- ✅ Backward compatible: v0.3.0 themes load without templateBackgrounds field
- ✅ Image copying integrates seamlessly (no duplicate images copied)
- ✅ Zero rendering bugs related to template background precedence

---

**Event Storming Complete**
**Next Step:** Implement TDD tests for Phase 1
