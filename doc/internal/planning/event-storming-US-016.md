# Event Storming: US-016 Directory-Based Themes

**Date:** 2024-12-26
**User Story:** US-016 - Directory-Based Themes
**Related:** PDR-013, v0.4.0-decisions-summary.md

---

## User Story

**As a** theme designer
**I want** themes to be directories containing theme.json and assets
**So that** themes are self-contained and easy to share

---

## Domain Events (Orange Stickies)

Events that occur in the system, past tense:

1. **ThemeDirectoryLocated** - CLI found theme directory at configured path
2. **ThemeJsonRead** - theme.json file contents loaded from disk
3. **ThemeJsonParsed** - JSON successfully parsed into domain objects
4. **ThemeImagePathResolved** - Image path resolved relative to theme directory
5. **ThemeImageCopied** - Theme image copied to output directory
6. **ThemeLoadFailed** - Theme could not be loaded (missing directory, invalid JSON, etc.)
7. **ThemeNotFound** - Requested theme name not found in configured directory

---

## Commands (Blue Stickies)

Actions triggered by actors:

1. **LoadTheme** - Load theme by name from configured directory
2. **ResolveThemeImagePath** - Convert relative path to absolute path based on theme directory
3. **CopyThemeImages** - Copy all theme images to output directory
4. **ValidateThemeStructure** - Check theme directory has required files

---

## Aggregates (Yellow Stickies)

Domain entities that process commands and emit events:

### Theme (existing)
- **Responsibility:** Represents presentation styling
- **Properties:** name, version, background, colors, fonts, spacing, syntax, slideCounter, templateBackgrounds
- **Invariants:**
  - name must be non-empty
  - version must follow semver
  - All image paths are strings (validation happens at infrastructure layer)

### ThemeDirectory (new infrastructure concept - NOT domain)
- **Responsibility:** File system representation of theme
- **Properties:** directoryPath, themeJsonPath
- **Note:** This is an infrastructure concern, not a domain aggregate

---

## Read Models (Green Stickies)

Data projections for queries:

1. **AvailableThemes** - List of theme names in configured directory
2. **ThemeMetadata** - Theme name, version, description without loading full theme
3. **ThemeImagePaths** - All image paths referenced by theme (for copying)

---

## Policies (Purple Stickies)

Business rules connecting events:

1. **When ThemeDirectoryLocated → ValidateThemeStructure**
   - Ensure theme.json exists
   - Ensure it's readable

2. **When ThemeJsonParsed → ResolveThemeImagePaths**
   - Extract all image paths from theme
   - Resolve relative to theme directory

3. **When CopyImages command received → CopyThemeImages**
   - Only if copyImages flag is true
   - Copy theme images alongside content images

4. **When ThemeLoadFailed → ShowClearError**
   - Display helpful error message
   - Suggest available themes
   - Show configuration command

---

## External Systems (Pink Stickies)

External dependencies:

1. **File System** - Read theme.json, check directory existence
2. **Configuration System** - Get theme directory path from config
3. **Image Copier** - Copy theme images to output (existing US-006)

---

## Timeline (Left to Right Flow)

```
User Action: mdslides render slides.md output/ --theme retisio

↓

[LoadTheme Command]
  ↓
[Read Configuration: theme_dir]
  ↓
[ThemeDirectoryLocated Event]
  theme_dir/retisio/ found
  ↓
[ValidateThemeStructure Command]
  ↓
IF theme.json exists:
  [ThemeJsonRead Event]
    ↓
  [ThemeJsonParsed Event]
    ↓
  [ResolveThemeImagePath Command] (for each image in theme)
    background.image: "backgrounds/default.png"
    → themes/retisio/backgrounds/default.png
    ↓
  [ThemeImagePathResolved Event]
    ↓
  [Theme aggregate created]
    ↓
  Continue with rendering...
    ↓
  [CopyThemeImages Command] (during image copying phase)
    ↓
  [ThemeImageCopied Event] (for each image)

ELSE theme.json missing:
  [ThemeLoadFailed Event]
    ↓
  [ShowClearError Policy]
    ↓
  Display: "Theme 'retisio' not found in ./themes/
           Searched for: ./themes/retisio/theme.json
           Available themes: minimal, tjm-solutions
           Use 'mdslides config set theme-dir /path' to change."
  ↓
  Exit with error
```

---

## Hotspots (Red Stickies)

Areas of complexity or risk:

1. **🔥 Path Resolution Complexity**
   - **Issue:** Theme images referenced as relative paths in JSON
   - **Risk:** Incorrect resolution leads to broken images
   - **Mitigation:** Centralize path resolution logic in ThemeJsonAdapter
   - **Test:** Verify paths with `.`, `..`, absolute paths, URLs

2. **🔥 Backward Compatibility**
   - **Issue:** Breaking change from single-file themes
   - **Risk:** Users' existing themes break
   - **Mitigation:** Clear migration guide, helpful error messages
   - **Decision:** Clean break (no dual support) per PDR-013

3. **🔥 Theme Discovery**
   - **Issue:** Finding all available themes in directory
   - **Risk:** Non-theme directories mistaken for themes
   - **Mitigation:** Only directories with theme.json are valid
   - **Test:** Handle directories without theme.json gracefully

4. **🔥 Configuration Integration**
   - **Issue:** Theme directory comes from configuration system
   - **Risk:** Configuration not yet implemented (US-017)
   - **Mitigation:** Hardcode default for v0.4.0, integrate config later
   - **Decision:** US-016 doesn't depend on US-017 (use defaults)

---

## Questions & Decisions

### Q1: Where does theme directory path come from?
**A:** Three sources (precedence order):
1. CLI flag: `--theme-dir /path` (future, not v0.4.0)
2. Configuration file (US-017, implemented later)
3. Default: `./themes/` (hardcoded for v0.4.0)

**Decision:** Start with hardcoded default, make configurable in US-017

### Q2: How do we handle theme.json parse errors?
**A:** Emit ThemeLoadFailed event with descriptive error message

**Examples:**
- Invalid JSON syntax → "Theme 'retisio' has invalid JSON: Unexpected token..."
- Missing required fields → "Theme 'retisio' missing required field: name"

### Q3: Should we validate theme directory structure at load time?
**A:** Yes, fail fast with helpful errors

**Validation:**
- Directory exists: ✓
- theme.json exists: ✓
- theme.json is valid JSON: ✓
- theme.json has required fields: ✓
- Referenced images exist: ⚠ Warn only (graceful degradation per PDR-011)

### Q4: How do theme images integrate with existing image copying (US-006)?
**A:** Theme images extracted and added to image list before copying

**Code integration point:**
```scala
// Main.scala
def extractAllImages(deck: SlideDeck, theme: Theme, themeDir: Path): List[ImageSource] =
  val contentImages = extractContentImages(deck)
  val themeImages = extractThemeImages(theme, themeDir)  // NEW
  (contentImages ++ themeImages).distinct
```

---

## Anti-Corruption Layer

**Infrastructure → Domain boundary:**

```scala
// Infrastructure: ThemeLoader
object ThemeLoader:
  def loadTheme(themeName: String): IO[Theme] =
    for
      themeDir <- locateThemeDirectory(themeName)  // File system operation
      themeJson <- readThemeJson(themeDir)         // File system operation
      theme <- ThemeJsonAdapter.parse(themeJson, themeDir)  // Domain parsing
    yield theme

// Domain: Theme (unchanged)
case class Theme(
  name: String,
  version: String,
  background: Background,
  colors: ColorScheme,
  fonts: FontScheme,
  spacing: Spacing,
  syntax: SyntaxColors,
  slideCounter: SlideCounter,
  templateBackgrounds: Map[String, String] = Map.empty
)
```

**Key insight:** Theme aggregate doesn't know it came from a directory. Theme directory is purely an infrastructure concern.

---

## Acceptance Tests (BDD Scenarios)

### Scenario 1: Load theme from directory
```gherkin
Given a theme directory "themes/retisio/" exists
And "themes/retisio/theme.json" contains valid theme JSON
When I run "mdslides render slides.md output/ --theme retisio"
Then the theme should load successfully
And theme images should be resolved relative to "themes/retisio/"
```

### Scenario 2: Theme directory not found
```gherkin
Given no theme directory "themes/nonexistent/" exists
When I run "mdslides render slides.md output/ --theme nonexistent"
Then the command should fail with error
And the error should list available themes
And the error should suggest using "mdslides config set theme-dir"
```

### Scenario 3: Missing theme.json
```gherkin
Given a directory "themes/incomplete/" exists
But "themes/incomplete/theme.json" does not exist
When I run "mdslides render slides.md output/ --theme incomplete"
Then the command should fail with error "theme.json not found"
```

### Scenario 4: Theme image path resolution
```gherkin
Given a theme "themes/retisio/theme.json" with background image "backgrounds/title.png"
When the theme is loaded
Then the image path should resolve to "themes/retisio/backgrounds/title.png"
```

### Scenario 5: Theme images copied to output
```gherkin
Given a theme with background image "backgrounds/title.png"
And the theme directory is "themes/retisio/"
When rendering with default copy-images behavior
Then "themes/retisio/backgrounds/title.png" should be copied to "output/backgrounds/title.png"
```

---

## Implementation Tasks (TDD Order)

### Phase 1: Infrastructure - Theme Discovery
1. **Test:** ThemeLoader finds theme directory in default location
2. **Test:** ThemeLoader returns error when theme not found
3. **Test:** ThemeLoader lists available themes

### Phase 2: Infrastructure - Theme Loading
4. **Test:** ThemeJsonAdapter parses theme.json from directory
5. **Test:** ThemeJsonAdapter resolves relative image paths
6. **Test:** ThemeJsonAdapter handles missing theme.json
7. **Test:** ThemeJsonAdapter handles invalid JSON

### Phase 3: Domain - Theme Aggregate (minimal changes)
8. **Test:** Theme accepts templateBackgrounds field (already in PDR-011)

### Phase 4: Integration - Image Copying
9. **Test:** Theme images extracted and added to copy list
10. **Test:** Theme images preserve directory structure in output
11. **Test:** Theme images respect --no-copy-images flag

### Phase 5: CLI - Error Messages
12. **Test:** Clear error when theme not found
13. **Test:** Error message lists available themes
14. **Test:** Error message suggests configuration command

---

## Success Metrics

- ✅ All themes migrate to directory structure
- ✅ Theme loading time < 50ms (not significantly slower than file-based)
- ✅ Zero path resolution bugs in production
- ✅ Error messages resolve 95% of user issues without support

---

**Event Storming Complete**
**Next Step:** Implement TDD tests for Phase 1
