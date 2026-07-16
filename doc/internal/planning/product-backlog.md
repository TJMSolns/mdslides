# MDSlides Product Backlog

**Last Updated**: 2024-12-26 (Post List Rendering Fix)
**Status**: Active Development
**Current Version**: v0.4.0 (95% complete - bug fixes in progress)

## Prioritization Framework

**MoSCoW Method:**
- **MUST**: Critical for release, blocks other features
- **SHOULD**: Important, high value, defer only if necessary
- **COULD**: Nice-to-have, include if capacity allows
- **WON'T**: Out of scope for this release, document for future

---

## v0.3.0 Sprint (COMPLETED ✅)

### Completed Features
- ✅ **US-006**: Image Asset Copying (completed 2024-12-26)
  - 192 tests passing (21 new)
  - Default copy-on-render with `--no-copy-images` opt-out
  - Size warnings (>5MB per image, >20MB total)

---

## v0.4.0 Sprint (Current - 95% Complete)

**Focus:** Directory-Based Themes + Background Images + List Rendering

**Status:** ✅ Core features implemented, bug fixes in progress
**Completed:** 2024-12-26
**Next Step:** Reprioritize backlog for v1.0 MVP

**Related Governance:**
- [v0.4.0 Decisions Summary](v0.4.0-decisions-summary.md)
- [v0.4.0 Progress Report](v0.4.0-progress-report.md)
- [PDR-011: Background Image Architecture](../governance/pdr/PDR-011-background-image-architecture.md)
- [PDR-013: Directory-Based Theme Architecture](../governance/pdr/PDR-013-directory-based-theme-architecture.md)
- [ADR-015: Directory-Based Themes](../governance/ADR/ADR-015-directory-based-themes.md)

---

### Completed Features (v0.4.0)

#### US-016: Directory-Based Themes [MUST] ✅ COMPLETED
**As a** theme designer
**I want** themes to be directories containing theme.json and assets
**So that** themes are self-contained and easy to share

**Acceptance Criteria:**
1. ✅ Theme structure: `themes/retisio/theme.json` + assets in subdirectories
2. ✅ Paths in theme.json are relative to theme directory
3. ✅ CLI discovers themes in configured theme directory
4. ✅ Theme images copied to output directory (preserving structure)
5. ✅ Clear error messages when theme not found

**Related Governance:**
- PDR-013: Directory-Based Theme Architecture
- ADR-015: Directory-Based Themes
- v0.4.0 Decisions Summary §1

**Technical Implementation:**
- Infrastructure: ThemeLoader resolves theme-relative paths
- Anticorruption: ThemeJsonAdapter path resolution
- CLI: Main.scala copies theme background images

**Priority:** MUST (foundation for US-011, US-012)
**Complexity:** Medium
**Status:** ✅ COMPLETED (6 tests passing)

---

#### US-011: Per-Slide Background Images [SHOULD] ✅ COMPLETED
**As a** presentation author
**I want** to override the theme background on specific slides
**So that** I can use different visuals for title vs content slides

**Acceptance Criteria:**
1. ✅ Slide frontmatter accepts `background` field (string)
2. ✅ Simple form: `background: images/custom-bg.png`
3. ⚠️ Extended form: NOT IMPLEMENTED (opacity, color overlay)
4. ✅ Slide background overrides theme/template background
5. ✅ Background images copied to output directory
6. ⚠️ Validation warns if background image missing (graceful degradation)

**Related Governance:**
- PDR-011: Background Image Architecture
- v0.4.0 Decisions Summary §4

**Technical Implementation:**
- Domain: Slide frontmatter supports `background` field
- Parser: FrontmatterAdapter parses background field
- Renderer: Fallback chain: slide → template → theme → color
- CLI: Main.scala copies all background images

**Priority:** SHOULD
**Complexity:** Medium
**Status:** ✅ PARTIALLY COMPLETED (simple form only, extended form deferred)

---

#### US-012: Template-Specific Background Defaults [SHOULD] ✅ COMPLETED
**As a** theme designer
**I want** to specify different default backgrounds per template type
**So that** title slides automatically get one background, content slides get another

**Acceptance Criteria:**
1. ✅ Theme JSON accepts `templateBackgrounds` map (template name → image path)
2. ✅ Background selection priority: slide override > template default > theme default
3. ✅ Example: `{"title": "backgrounds/title.png", "content": "backgrounds/content.png"}`
4. ✅ Template backgrounds are copied to output directory
5. ✅ Invalid template names in `templateBackgrounds` gracefully handled

**Related Governance:**
- PDR-011: Background Image Architecture
- v0.4.0 Decisions Summary §5

**Technical Implementation:**
- Domain: Theme.templateBackgrounds field (Map[String, String])
- Infrastructure: ThemeJsonAdapter custom decoder
- Renderer: Fallback chain implementation
- the prior organization theme demonstrates feature with 5 template backgrounds

**Priority:** SHOULD
**Complexity:** Medium
**Status:** ✅ COMPLETED (7 tests passing)

---

#### US-003.2: List Support (Ordered & Unordered) ✅ COMPLETED
**As a** presentation author
**I want** to render ordered and unordered lists correctly
**So that** my content structure is preserved as HTML `<ul>` and `<ol>` elements

**Acceptance Criteria:**
1. ✅ Unordered lists (`- item`) render as `<ul><li>` elements
2. ✅ Ordered lists (`1. item`) render as `<ol><li>` elements
3. ✅ List items support inline formatting (bold, italic, code)
4. ✅ Multiple lists per slide render correctly
5. ⚠️ Nested lists NOT IMPLEMENTED (FlexmarkAdapter limitation)

**Technical Implementation:**
- Domain: UnorderedList, OrderedList, ListItem types
- Infrastructure: FlexmarkAdapter handles BulletList and OrderedList nodes
- Renderer: renderUnorderedList and renderOrderedList functions
- Tests: 4 new tests in FormattedContentSpec

**Priority:** MUST (core markdown feature)
**Complexity:** Medium
**Status:** ✅ COMPLETED (2024-12-26, 4 tests passing)

---

### Deferred Features (v0.4.0 → Future)

#### US-017: Configuration Management CLI [DEFERRED to v0.5.0+]
**As a** MDSlides user
**I want** to configure defaults via CLI
**So that** I don't have to specify the same flags repeatedly

**Status:** 🔵 DEFERRED

**Rationale:**
- v0.4.0 focused on theme system and list rendering
- Configuration can be added later without breaking changes
- Current CLI flags sufficient for v1.0 MVP
- Avoid feature creep in v0.4.0

**Related Governance:**
- PDR-014: Configuration Management Strategy
- v0.4.0 Decisions Summary §3

**Acceptance Criteria (for future):**
1. `mdslides config set theme-dir /path` sets configuration
2. Three-tier precedence: project → user → system
3. Configuration keys: `theme_dir`, `default_theme`, `copy_images`

---

#### US-010: Theme Logo Support [DEFERRED to v0.5.0+]
**Status:** 🔵 Deferred

**Rationale:**
- Focus v0.4.0 on background images
- Logo positioning needs more design work
- Interim solution: use content images
- Avoid feature creep

**Related Governance:**
- PDR-012: Logo Positioning Architecture (Deferred)
- v0.4.0 Decisions Summary §10

---

### Layout Enhancement Track (Future Releases)

#### US-013: Two-Column Layout Template [COULD]
**As a** presentation author
**I want** a two-column slide template
**So that** I can display side-by-side comparisons or contrasts

**Acceptance Criteria:**
1. New template: `two-column`
2. Required slots: `heading`, `left`, `right`
3. Left and right columns rendered side-by-side with equal width
4. Responsive: columns stack vertically on narrow screens
5. Markdown rendering works in both columns (formatting, lists, images)

**Related Governance:**
- retisio-theme-analysis.md §4: Two-Column Layouts
- PowerPoint example: "Realities of the Past" vs "Realities of the Present"

**Technical Notes:**
- Domain: Add `TwoColumn` template to Template.scala
- Renderer: CSS flexbox for `.two-column-slide` class
- Parser: No changes needed (frontmatter already supports arbitrary templates)

**Estimated Complexity**: Low-Medium

---

#### US-014: Multi-Level Bullet Styling [COULD]
**As a** presentation author
**I want** nested bullet lists to have distinct visual hierarchy
**So that** my content structure is clear

**Acceptance Criteria:**
1. CSS defines distinct bullet styles for 3 levels: filled circle, hollow circle, square
2. Progressive indentation (0px, 20px, 40px)
3. Font size slightly smaller for nested levels (optional)
4. Works with both ordered (`<ol>`) and unordered (`<ul>`) lists

**Related Governance:**
- retisio-theme-analysis.md §5: Multi-Level Bullet Lists

**Technical Notes:**
- Infrastructure: HTMLRenderer CSS enhancement only
- No domain or parser changes needed
- Theme system could later add `bulletStyles` customization

**Estimated Complexity**: Very Low (CSS-only change)

---

### Font Enhancement Track

#### US-015: Google Fonts Support [SHOULD]
**As a** theme designer
**I want** to use Google Fonts in my theme
**So that** presentations have professional typography matching corporate branding

**Acceptance Criteria:**
1. Theme JSON accepts `googleFonts: ["Font Name"]` array
2. HTMLRenderer injects `<link>` tag for each Google Font
3. Fonts referenced in `fonts.title`, `fonts.body`, `fonts.code` are loaded
4. Fallback fonts still work if Google Fonts fail to load
5. No runtime errors if Google Fonts API is unreachable

**Related Governance:**
- retisio-theme-analysis.md §1: Google Fonts Support
- the prior organization theme uses "Varela Round" (currently falling back to Arial)

**Technical Notes:**
- Domain: Add `googleFonts: List[String]` to Theme aggregate
- Infrastructure: ThemeJsonAdapter parses field, HTMLRenderer generates `<link>` tags
- URL encoding: `"Varela Round"` → `https://fonts.googleapis.com/css2?family=Varela+Round&display=swap`

**Estimated Complexity**: Low
**Impact**: High (enables the prior organization theme completion)

---

## v0.4.0 Sprint Summary

**Scope:** 4 user stories planned → 4 completed (3 fully, 1 partially)

### Completed ✅
- **US-016**: Directory-Based Themes (foundation) - COMPLETE
- **US-011**: Per-Slide Background Images - PARTIAL (simple form only)
- **US-012**: Template-Specific Background Defaults - COMPLETE
- **US-003.2**: List Support (bonus feature added during sprint)

### Deferred to v0.5.0+
- **US-017**: Configuration Management CLI
- **US-010**: Theme Logo Support

**Final Statistics:**
- 223+ tests passing (all tests)
- 4 new list tests added
- 13 theme-related tests from earlier
- the prior organization theme with 5 background images
- Tutorial presentation with 15 slides

**v0.4.0 Status:** ✅ 95% COMPLETE (ready for v1.0 planning)

---

## v1.0 MVP Sprint ✅ COMPLETED (2024-12-27)

**Goal:** Complete minimum viable product with speaker notes support and improved CLI
**Status:** ✅ RELEASED as v1.0.0
**Tag:** v1.0.0
**Commit:** 97998c3

**Implemented Features:**
- US-019: Improved CLI UX (Breaking Change)
- US-004: Speaker Notes Parsing

**Test Coverage:** 261 tests passing (127 domain, 110 infrastructure, 24 CLI)

**Related Documentation:**
- [CHANGELOG.md](../../../CHANGELOG.md) - v1.0.0 release notes
- [INSTALL.md](../../../INSTALL.md) - Installation and usage guide
- [examples/mdslides-tutorial.md](../../../examples/mdslides-tutorial.md) - Updated tutorial

---

### v1.0 Implemented Features

#### US-019: Improved CLI UX [MUST] ⚠️ BREAKING CHANGE ✅ COMPLETED
**As a** presentation author
**I want** a simpler CLI that infers output from input name
**So that** I don't have to specify redundant paths and avoid directory pollution

**Status:** ✅ IMPLEMENTED (v1.0.0)
**Priority:** MUST (breaking change acceptable pre-v1.0, hard to change after)

**Acceptance Criteria:**
1. Simple form: `mdslides render DECK_NAME [--theme THEME]`
   - Reads `DECK_NAME.md` (or `.markdown`)
   - Creates `DECK_NAME/` directory
   - Writes `DECK_NAME/index.html`
   - Copies assets to `DECK_NAME/images/`, `DECK_NAME/backgrounds/`
2. Explicit form: `mdslides render --input FILE --output DIR [--theme THEME]`
   - Full control over input/output paths
3. Backward compatibility NOT required (pre-v1.0)
4. Clear error messages for missing input files
5. Output directory created automatically if missing

**Current Problems (v0.4.0):**
- `mdslides slides.md output.html` creates:
  - `output.html` (single file)
  - `images/` in parent directory (pollutes `/tmp/` if output is `/tmp/output.html`)
- Redundant: user specifies both input and output names
- Misleading: `.html` extension but output should be directory

**Technical Notes:**
- CLI: Argument parsing in Main.scala
- Path resolution: `DECK_NAME` → `DECK_NAME.md` + `DECK_NAME/`
- Output structure: `DECK_NAME/index.html` with relative asset paths
- Asset copying: images to `DECK_NAME/images/`, backgrounds to `DECK_NAME/backgrounds/`

**Related Governance:**
- US-034 (v1.1): Speaker view will need multiple HTML files → directory output required
- Future: Multi-page decks, handouts, etc. all need directory structure

**Estimated Complexity:** Low-Medium
**Depends On:** None (independent)
**Blocks:** US-034 (speaker view needs directory output)

---

#### US-004: Speaker Notes Parsing [MUST] ⚠️ CRITICAL GAP
**As a** presentation author
**I want** to add speaker notes to slides
**So that** I can prepare talking points without displaying them on slides

**Status:** ❌ NOT IMPLEMENTED (v1.0 MVP requirement)
**Priority:** MUST (critical v1.0 gap per IMPLEMENTATION-STATUS.md)

**Acceptance Criteria:**
1. Slide frontmatter accepts `notes:` field (string or array)
2. Simple form: `notes: "Remember to mention the key point about..."`
3. Multi-line form: `notes: ["Point 1", "Point 2", "Point 3"]`
4. Notes stored in Slide aggregate
5. Note density validation (max lines, max words)
6. **Parsing ONLY** - rendering deferred to v1.1

**Technical Notes:**
- Domain: Add `notes: Option[String]` to Slide aggregate
- Parser: FrontmatterAdapter parses `notes` field
- Validation: Add note density checks (similar to slide content)
- **DO NOT render notes in HTML yet** (v1.1 feature)

**Related Governance:**
- Original v1.0 backlog item
- Rendering: US-034 (v1.1)

**Estimated Complexity:** Low-Medium
**Depends On:** None (independent)

---

#### US-003.3: Nested List Support [SHOULD]
**As a** presentation author
**I want** to use nested lists in slides
**So that** I can show hierarchical content structure

**Status:** ❌ NOT IMPLEMENTED (FlexmarkAdapter limitation)
**Priority:** SHOULD (nice-to-have for v1.0)

**Acceptance Criteria:**
1. Nested unordered lists render correctly (`<ul><li><ul>`)
2. Nested ordered lists render correctly (`<ol><li><ol>`)
3. Mixed nesting (ordered within unordered) supported
4. CSS styling shows visual hierarchy (indentation, bullet style)
5. Max 3 levels of nesting (prevent overly complex slides)

**Technical Notes:**
- Current: FlexmarkAdapter.extractListItemContent doesn't handle nested BulletList/OrderedList children
- Solution: Recursive list extraction in FlexmarkAdapter
- Domain: No changes needed (ListItem can already contain TextSpan list)
- Renderer: CSS for multi-level bullet styles

**Workaround:** Use flat lists only
**Estimated Complexity:** Medium

---

#### US-022: Mermaid Diagram Support [COULD]
**As a** presentation author
**I want** to embed Mermaid diagrams inline
**So that** I can generate diagrams from markdown syntax

**Status:** ❌ NOT IMPLEMENTED (mentioned in docs but no code exists)
**Priority:** COULD (deferred to v2.0 per IMPLEMENTATION-STATUS.md)

**Acceptance Criteria:**
1. Code blocks with `mermaid` language hint are recognized
2. Mermaid.js library included in HTML output
3. Diagrams render client-side in browser
4. Common diagram types supported (flowchart, sequence, class)
5. Theme colors applied to diagrams

**Technical Notes:**
- Infrastructure: HTMLRenderer includes mermaid.min.js CDN link
- Renderer: Code blocks with lang="mermaid" use `<div class="mermaid">` instead of `<pre>`
- Client-side rendering (mermaid.initialize() in JavaScript)

**Workaround:** Use SVG/PNG images instead
**Backlogged For:** v2.0
**Estimated Complexity:** Medium

---

### Medium Priority Features (v1.0 Nice-to-Have)

#### US-014: Accessibility Validation [SHOULD]
**As a** presentation author
**I want** MDSlides to validate accessibility issues
**So that** my presentations are WCAG AA/AAA compliant

**Status:** ❌ NOT IMPLEMENTED
**Priority:** SHOULD (important for professional use)

**Acceptance Criteria:**
1. Contrast ratio validation (text vs background ≥4.5:1)
2. Font size validation (body ≥24px, headings larger)
3. Alt text validation (all images have alt text)
4. Heading hierarchy validation (no skipped levels)
5. Warnings (not errors) for accessibility issues

**Technical Notes:**
- Validation: New AccessibilityValidator in domain layer
- Theme: Validate theme colors against WCAG standards
- Images: Check ContentImage for alt text presence
- Report: Collect all warnings, display at end of validation

**Backlogged For:** v1.1 per IMPLEMENTATION-STATUS.md
**Estimated Complexity:** Medium-High

---

### Low Priority Features (v1.0 Stretch Goals)

#### Syntax Highlighting for Code Blocks [COULD]
**As a** presentation author
**I want** syntax-highlighted code blocks
**So that** code is easier to read

**Status:** ❌ NOT IMPLEMENTED (currently monochrome)
**Priority:** COULD

**Acceptance Criteria:**
1. Code blocks use syntax highlighting library (e.g., highlight.js)
2. Language detection from code block hint
3. Theme can specify syntax colors (already in theme.json)
4. Fallback to monochrome if language not recognized

**Technical Notes:**
- Infrastructure: HTMLRenderer includes highlight.js CDN
- Renderer: Apply `hljs` CSS classes to code blocks
- Theme: Use existing `syntax.*` colors from theme.json

**Estimated Complexity:** Low
**Backlogged For:** v1.1

---

## v1.0 MVP Sprint Summary

**Scope (Revised):** 2 MUST + 2 SHOULD + 2 COULD = 6 user stories

### Must Have (Block v1.0 release)
- **US-019**: Improved CLI UX ⚠️ BREAKING CHANGE (pre-v1.0 only)
- **US-004**: Speaker Notes Parsing

### Should Have (Defer only if necessary)
- **US-003.3**: Nested List Support
- **Syntax Highlighting**: Code block colors (low effort, high impact)

### Could Have (Include if capacity allows)
- **US-014**: Accessibility Validation
- **Enhanced Tutorial**: Document all v1.0 features

### Won't Have (Explicitly deferred to v2.0+)
- **US-022**: Mermaid Diagrams → v2.0
- **US-005-007**: Additional Templates (two-column, image, code) → v1.1
- **US-034**: Speaker View / Notes Rendering → v1.1

**Recommendation:** Implement both MUST features (US-019 + US-004) as minimal v1.0 MVP. CLI change is breaking but essential before public release.

---

## Ceremony Process for Backlog Items

To promote a backlog item to a sprint:

1. ✅ **Stakeholder Discussion**: Clarify open questions (COMPLETED for v0.4.0)
2. ✅ **Write PDRs**: Product decisions (COMPLETED: PDR-011, PDR-013, PDR-014)
3. 🟡 **Event Storming**: Identify domain events, commands, aggregates (NEXT)
4. **Create Ceremony Document**: `doc/internal/ceremonies/v0.4.0.md`
5. **Write ADRs**: Architecture decisions (if needed during implementation)
6. **TDD Implementation**: Red → Green → Refactor

---

## Next Steps (Post v0.4.0 Completion)

### Immediate Actions

1. ✅ **v0.4.0 Completion:**
   - US-016: Directory-Based Themes ✅
   - US-012: Template-Specific Background Defaults ✅
   - US-011: Per-Slide Background Images ✅ (partial)
   - US-003.2: List Support ✅
   - Total: 223+ tests passing

2. ✅ **Status Documentation:**
   - IMPLEMENTATION-STATUS.md created
   - Product backlog updated
   - Critical gaps identified

3. 🟡 **v1.0 MVP Planning** (NEXT STEP):
   - Prioritize US-004: Speaker Notes Parsing (MUST)
   - Consider US-003.3: Nested List Support (SHOULD)
   - Defer US-022: Mermaid Diagrams to v2.0
   - Create PDR for speaker notes (if needed)
   - Event Storming for US-004
   - Three Amigos session
   - Example Mapping
   - TDD implementation

### v1.0 Implementation Plan (APPROVED)

**User Decision:** Proceed with **Minimal v1.0 MVP** + CLI UX improvement

**Sprint Scope:**
1. **US-019**: Improved CLI UX (MUST) - Breaking change, essential pre-v1.0
2. **US-004**: Speaker Notes Parsing (MUST) - Critical gap from original MVP

**Implementation Order:**
1. US-019 first (CLI foundation for better UX)
2. US-004 second (speaker notes on top of improved CLI)

**Next Steps:**
1. Create PDR-015: CLI UX Design (if needed for open questions)
2. Event Storming for US-019 and US-004
3. Three Amigos sessions
4. Example Mapping
5. TDD Implementation

**Out of Scope for v1.0:**
- Nested lists → v1.1
- Syntax highlighting → v1.1
- Accessibility validation → v1.1
- Mermaid diagrams → v2.0

---

## v1.1 Sprint ✅ COMPLETED (2024-12-27)

**Goal:** Speaker notes rendering and enhanced markdown support
**Status:** ✅ RELEASED as v1.1.0
**Release Date:** 2024-12-27

**Implemented Features:**

### ✅ MUST HAVE - Completed
- **US-034**: Speaker Notes Rendering / Speaker View
  - Dual-pane layout (notes + preview + timer)
  - Bidirectional keyboard synchronization via localStorage
  - Presenter timer with MM:SS format (auto-start on first navigation)
  - Progress indicators (slide counter)
  - Output files: `speaker.html` + `sync.js`
  - **Implementation:**
    - PresentationState domain model (17 tests)
    - SpeakerViewRenderer (14 tests)
    - sync.js module (manual testing)
  - **Depends on:** US-004 (completed in v1.0) ✅

**Test Coverage:** 292 tests passing (144 domain, 124 infrastructure, 24 CLI)

**Related Documentation:**
- [CHANGELOG.md](../../../CHANGELOG.md) - v1.1.0 release notes
- [INSTALL.md](../../../INSTALL.md) - Updated with speaker view guide
- [examples/mdslides-tutorial.md](../../../examples/mdslides-tutorial.md) - Updated tutorial
- Event Storming: `event-storming-US-034.md`
- Three Amigos: `three-amigos-US-034.md`
- Example Mapping: `example-mapping-US-034.md`

---

## v1.2 Sprint ✅ COMPLETED (2024-12-27)

**Goal:** Enhanced markdown support with nested lists
**Status:** ✅ RELEASED as v1.2.0
**Release Date:** 2024-12-27

**Implemented Features:**

### ✅ MUST HAVE - Completed
- **US-003.3**: Nested List Support
  - Recursive list parsing in FlexmarkAdapter
  - Multi-level bullet styling (disc → circle → square, 3 levels max)
  - Ordered list numbering hierarchy (decimal → lower-alpha → lower-roman)
  - Mixed nesting (ordered within unordered and vice versa)
  - CSS styling with 2em indentation per level
  - **Implementation:**
    - Domain model enhancements (3 tests)
    - Recursive parser logic (10 tests)
    - HTML renderer with Scalatags fix (9 tests)
  - **Bug Fix:** Fixed Scalatags fragment combining issue discovered during integration testing
- **BUG FIX**: the prior organization Theme Template References
  - Removed `futureTemplates` section from theme.json
  - Cleaned up references to non-existent templates (deferred to v2.0)
- **Tutorial Update**: Comprehensive Feature Demonstration
  - Added nested list demonstration slide
  - Updated tutorial to 17 slides total
  - Documented visual hierarchy and nesting limits

**Test Coverage:** 290 tests passing (150 domain, 140 infrastructure)

**Related Documentation:**
- [CHANGELOG.md](../../../CHANGELOG.md) - v1.2.0 release notes
- Event Storming: `event-storming-US-003.3.md`
- Three Amigos: `three-amigos-US-003.3.md`
- Example Mapping: `example-mapping-US-003.3.md`

---

## v1.3.1 Sprint 🔴 IN PROGRESS (2025-12-27 to 2025-12-30)

**Goal:** Critical bugfix hotfix for v1.3.0
**Status:** 🔴 IN PROGRESS
**Target Release:** 2025-12-30 (3 days)
**Priority:** P0 - CRITICAL

**Critical Bugs to Fix:**

### ✅ MUST FIX - In Progress
- **BUG-001**: List Rendering Order Incorrect
  - **Severity**: P0 - CRITICAL
  - **Impact**: All presentations with mixed ordered/unordered lists render incorrectly
  - **Symptoms**: Unordered lists appear before ordered lists regardless of source order
  - **Root Cause**: TBD (investigate FlexmarkAdapter or HTMLRenderer)
  - **Fix**: Preserve source order when collecting/rendering lists
  - **Test**: Add regression test for list ordering
  - **Estimated Effort**: 1 day investigation + 1 day fix + testing

- **BUG-002**: 'S' Key Does Not Open Speaker View
  - **Severity**: P0 - CRITICAL
  - **Impact**: Documented feature completely non-functional
  - **Symptoms**: Pressing 'S' during presentation does nothing
  - **Root Cause**: Navigation JS missing 'S' key handler
  - **Fix**: Add keyboard handler: `if (e.key === 's' || e.key === 'S') window.open('speaker.html', ...)`
  - **Test**: Verify 'S' key handler present in rendered HTML
  - **Estimated Effort**: 0.5 day implementation + testing

**Test Coverage:** 324+ tests (322 existing + 2 new regression tests)

**Success Criteria:**
- Both bugs verified fixed with integration/unit tests
- No regressions in existing 322 tests
- Manual testing confirms correct behavior
- CHANGELOG updated with hotfix notes
- Release within 3 days of bug discovery

**Related Documentation:**
- [KNOWN-ISSUES.md](KNOWN-ISSUES.md) - Detailed bug tracking
- [ROADMAP.md](ROADMAP.md) - Release timeline impact

---

## v1.3 Sprint ✅ COMPLETED (2025-12-27)

**Goal:** Syntax highlighting for code blocks
**Status:** ✅ RELEASED as v1.3.0 (with known bugs)
**Release Date:** 2025-12-27

**Implemented Features:**

### ✅ SHOULD HAVE - Completed
- **US-019**: Syntax Highlighting for Code Blocks
  - CDN-based highlight.js v11.9.0 integration
  - 190+ languages supported (Scala, Java, Python, JavaScript, TypeScript, SQL, Bash, JSON, YAML, XML, etc.)
  - Theme-aware highlighting:
    - Light themes (light, corporate, retisio) → GitHub theme
    - Dark theme → Monokai Sublime theme
  - Automatic highlighting on page load via `hljs.highlightAll()`
  - Graceful degradation if CDN unavailable
  - Preserves existing auto-scaling for long code blocks (PDR-006)
  - **Implementation:**
    - HTMLRenderer: Added CSS link, script tag, initialization script (8 tests)
    - Theme mapping helper function: `highlightJsTheme(theme: Theme): String`
    - No domain changes (CodeBlock already supported language hints)
    - Infrastructure-only changes (CDN inclusion, theme mapping)

**Test Coverage:** 322 tests passing (158 domain, 164 infrastructure)

**Related Documentation:**
- [CHANGELOG.md](../../../CHANGELOG.md) - v1.3.0 release notes
- Event Storming: `event-storming-syntax-highlighting.md`
- Three Amigos: `three-amigos-syntax-highlighting.md`
- Example Mapping: `example-mapping-syntax-highlighting.md`

### Could Have
- **US-014**: Accessibility Validation
  - WCAG 2.1 AA compliance checking
  - Contrast ratio validation
  - Alt text validation for images
  - Warning-based (non-blocking)
- **Google Fonts Support** (US-020)
  - Load web fonts from Google Fonts API
  - Fallback to system fonts
  - Theme configuration for font families

### Won't Have (Deferred to v2.0+)
- **US-022**: Mermaid Diagram Support → v2.0
- **US-005-007**: Additional Templates (diagram, closing, section-title) → v2.0
- **Configuration Management** → v2.0

---

**End of Backlog**
**Last Updated:** 2025-12-27 (Post v1.3.0 Release)
**Maintainer:** Tony Moores, TJM Solutions
