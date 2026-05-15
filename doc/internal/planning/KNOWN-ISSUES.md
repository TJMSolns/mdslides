# Known Issues & Bug Tracking

**Last Updated**: 2025-12-29
**Maintainer**: Tony Moores, TJM Solutions

---

## Critical Bugs (P0)

### BUG-001: List Rendering Order Incorrect
**Discovered**: 2025-12-27
**Status**: ✅ FIXED (v1.3.1)
**Priority**: P0 - CRITICAL
**Affects**: v1.2.0, v1.3.0
**Fixed In**: v1.3.1 (2025-12-27)

**Description**:
When a slide contains both ordered and unordered lists, they render in the wrong order. Unordered lists appear first, followed by ordered lists, regardless of their order in the source Markdown.

**Expected Behavior**:
```markdown
**Ordered Lists:**
1. First
2. Second

**Unordered Lists:**
- Item A
- Item B
```

Should render as:
```
Ordered Lists:
1. First
2. Second

Unordered Lists:
- Item A
- Item B
```

**Actual Behavior**:
Renders as:
```
Ordered Lists:

Unordered Lists:
- Item A
- Item B
1. First
2. Second
```

**Root Cause**:
`FormattedContent` domain model had separate `unorderedLists` and `orderedLists` fields which were always rendered in that fixed order (unordered first, ordered second) regardless of source markdown order.

**Fix Implemented**:
1. Added new `ListElement` sealed trait with `OrderedListElement` and `UnorderedListElement` cases
2. Added `lists: List[ListElement]` field to `FormattedContent` to preserve source order
3. Modified `FlexmarkAdapter` to track lists in insertion order via new `lists` buffer
4. Updated `HTMLRenderer` to render from `lists` field (with backward compatibility fallback)
5. Deprecated old `unorderedLists` and `orderedLists` fields (retained for backward compatibility)
6. Added regression test verifying source order preservation

**Verification**:
- Unit tests: FlexmarkAdapterSpec:393 (Scenario 8), FlexmarkAdapterSpec:425 (Scenario 9)
- Integration tests: HTMLRendererSpec:894 (Scenario 8), HTMLRendererSpec:921 (Scenario 9)
- HTML source verification: Confirmed `<ol>` appears before `<ul>` in generated HTML for test file /tmp/bugfix-test.md
- Full test suite: 328 tests passing (324 existing + 4 new Example Mapping scenario tests)
- See [v1.3.1-TESTING-ARTIFACTS.md](v1.3.1-TESTING-ARTIFACTS.md) for complete test evidence

**Test Case**:
```scala
test("render ordered list before unordered list preserves order"):
  val markdown = """
  |**Ordered Lists:**
  |1. First
  |2. Second
  |
  |**Unordered Lists:**
  |- Item A
  |- Item B
  |""".stripMargin

  val content = FlexmarkAdapter.parseInlineFormatting(markdown)
  val html = HTMLRenderer.renderFormattedContent(content)

  val olIndex = html.indexOf("<ol>")
  val ulIndex = html.indexOf("<ul>")
  assert(olIndex < ulIndex, "Ordered list should render before unordered list")
```

---

### BUG-002: 'S' Key Does Not Open Speaker View
**Discovered**: 2025-12-27
**Status**: ✅ FIXED (v1.3.1)
**Priority**: P0 - CRITICAL
**Affects**: v1.0.0, v1.1.0, v1.2.0, v1.3.0
**Fixed In**: v1.3.1 (2025-12-27)

**Description**:
The documentation states that pressing 'S' during a presentation opens the speaker view, but this functionality is not implemented. The keyboard handler does not include a listener for the 'S' key.

**Expected Behavior**:
- User presses 'S' or 's' during presentation
- Speaker view window opens via `window.open('speaker.html', ...)`
- Main presentation and speaker view sync

**Actual Behavior**:
- Pressing 'S' does nothing
- No keyboard handler for 'S' key exists in navigation JavaScript

**Root Cause**:
The 'S' key handler was documented but never implemented in the navigation JavaScript. The speaker view functionality exists (speaker.html, sync.js), but the main presentation lacked the keyboard shortcut to open it.

**Fix Implemented**:
1. Added `case 's':` and `case 'S':` handlers to navigation JavaScript in `HTMLRenderer.navigationJS()`
2. Handler calls `window.open('speaker.html', 'speaker-view', 'width=1024,height=768')`
3. Prevents default browser behavior with `e.preventDefault()`
4. Added regression test verifying handler presence in generated HTML

**Verification**:
- Unit test: HTMLRendererSpec:876 (verifies S key handler presence)
- HTML JavaScript verification: Confirmed `case 's':` and `case 'S':` handlers in generated navigation.js
- HTML verification: Confirmed `window.open('speaker.html', ...)` call present
- Speaker view file: Confirmed speaker.html generated alongside index.html
- Full test suite: 328 tests passing
- See [v1.3.1-TESTING-ARTIFACTS.md](v1.3.1-TESTING-ARTIFACTS.md) for complete test evidence

**Test Case**:
```scala
test("navigation JavaScript includes S key handler for speaker view"):
  val deck = SlideDeck(NonEmptyList.one(testSlide))
  val html = HTMLRenderer.renderDeck(deck)

  assert(html.contains("e.key === 's'") || html.contains("e.key === 'S'"))
  assert(html.contains("window.open"))
  assert(html.contains("speaker.html"))
```

---

## High Priority Bugs (P1)

### BUG-003: Markdown Tables Not Rendering in HTML
**Discovered**: 2025-12-29
**Status**: ✅ FIXED (v1.4.1)
**Priority**: P1 - HIGH
**Affects**: v1.4.0 and earlier
**Fixed In**: v1.4.1 (2026-04-21)

**Description**:
Markdown tables in slide content do not render correctly as HTML tables. The table syntax is preserved as plain text instead of being converted to `<table>` elements with proper structure.

**Affected Slides** (examples/how-we-work.md):
- Slide 3: "Team Roles" table
- Slide 28: "Available Mill Plugins" table
- Slide 37: "Charter vs Canvas" table

**Expected Behavior**:
```markdown
| Role | Primary Responsibility |
|------|----------------------|
| **Program Manager** | Coordination, dependencies |
| **Product Owner** | Business value, requirements |
```

Should render as:
```html
<table>
  <thead>
    <tr>
      <th>Role</th>
      <th>Primary Responsibility</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Program Manager</strong></td>
      <td>Coordination, dependencies</td>
    </tr>
    <tr>
      <td><strong>Product Owner</strong></td>
      <td>Business value, requirements</td>
    </tr>
  </tbody>
</table>
```

**Actual Behavior**:
Table syntax appears as plain text in the rendered HTML, or is not parsed/rendered at all.

**Root Cause**:
The full implementation (domain `Table`/`TableElement`, `FlexmarkAdapter` with `TablesExtension`, `HTMLRenderer.renderTable`, CSS) was already present but entirely untested. Zero MUnit tests existed to verify or document the behaviour.

**Fix Implemented**:
1. Confirmed `TablesExtension` registered in `FlexmarkAdapter` parser builder
2. Confirmed `extractTableContent` correctly extracts headers, rows, and alignment
3. Confirmed `TableElement` dispatch in `renderFormattedContentWithParagraphs`
4. Confirmed `renderTable` produces `<table><thead><tbody>` with alignment styles
5. Confirmed `.slide-body table` CSS present in `HTMLRenderer`
6. Added 8 parser tests → `FlexmarkAdapterTableSpec` (AC1–AC4, AC6)
7. Added 10 renderer tests → `HTMLRendererTableSpec` (AC1–AC2, AC4, AC6)

**Ceremony Artifacts**:
- `doc/internal/planning/example-mapping-BUG-003.md`
- `doc/internal/planning/three-amigos-BUG-003.md`

**Test Results**: 18/18 new tests pass. No regressions in domain or infrastructure suites.

---

### BUG-004: Slides Overflow Vertical Limits
**Discovered**: 2025-12-29
**Status**: ✅ FIXED (v1.4.1)
**Priority**: P1 - HIGH
**Affects**: v1.4.0 and earlier
**Fixed In**: v1.4.1 (2026-04-21)

**Description**:
Some slides with dense content overflow the vertical viewport height, making content unreadable or requiring scrolling. This violates the fundamental presentation constraint that all content should be visible within the slide viewport.

**Affected Slides** (examples/how-we-work.md):
- Slide 38: "Template Categories" - Too many items in lists
- Slide 40: "New Project Setup" - Dense procedural content

**Expected Behavior**:
All slide content should fit within the viewport height (~100vh) with appropriate margins. If content exceeds viewport, the rendering system should:
1. Warn during validation (density check)
2. Apply CSS constraints to prevent overflow
3. Optionally truncate or paginate content

**Actual Behavior**:
Content extends beyond viewport height, creating vertical scroll within slide or content being cut off.

**Root Cause**:
Missing `min-height: 0` on `.slide-content-wrapper`. Without it, a flex child with `overflow: hidden` cannot shrink below its natural content height — a standard CSS flexbox constraint requirement. Additionally, `.slide-body` had no `overflow-y` property, so overflowing content was silently clipped by the parent.

**Fix Implemented**:
1. Added `min-height: 0` to `.slide-content-wrapper` CSS — enables correct flex clipping
2. Added `overflow-y: auto` to `.slide-body` CSS — overflowing content is scrollable (visible) rather than silently hidden

**Ceremony Artifacts**:
- `doc/internal/planning/example-mapping-BUG-004.md`
- `doc/internal/planning/three-amigos-BUG-004.md`

**Test Results**: 5/5 new tests pass (`HTMLRendererOverflowSpec`). No regressions in infrastructure suite.

---

## Medium Priority Bugs (P2)

_None currently tracked_

---

## Low Priority Bugs (P3)

_None currently tracked_

---

## Bug Lifecycle

### States
- 🔴 **OPEN**: Bug confirmed, not yet assigned
- 🟡 **IN PROGRESS**: Actively being worked on
- 🟢 **FIXED**: Fix implemented, awaiting release
- ✅ **CLOSED**: Released and verified

### Priority Levels
- **P0 - CRITICAL**: Blocks core functionality, affects all users
- **P1 - HIGH**: Significant impact, affects many users
- **P2 - MEDIUM**: Moderate impact, workaround available
- **P3 - LOW**: Minor issue, cosmetic or edge case

### Fix Timeline
- **P0**: Hotfix release (v1.x.y) within 1 week
- **P1**: Next minor release (v1.x.0) within 1 month
- **P2**: Next minor/major release as capacity allows
- **P3**: Backlog, fix when convenient

---

## Regression Prevention

### New Tests Required
1. **BUG-001**: ✅ Integration test for list rendering order (IMPLEMENTED)
2. **BUG-002**: ✅ Test for 'S' key handler in navigation JS (IMPLEMENTED)
3. **BUG-003**: ✅ 18 table tests implemented (FlexmarkAdapterTableSpec, HTMLRendererTableSpec)
4. **BUG-004**: ✅ 5 overflow tests implemented (HTMLRendererOverflowSpec)

### Code Review Checklist
- [x] All keyboard handlers documented match implementation
- [x] List rendering preserves source order
- [x] Markdown tables render as HTML `<table>` elements (BUG-003 ✅)
- [x] Slide content fits within viewport height constraints (BUG-004 ✅)
- [ ] Density validation calculates actual height, not just line count
- [x] Integration tests cover multi-element interactions
- [x] Manual testing with actual rendered HTML

---

## Release Impact

### v1.3.1 (Hotfix - Target: 2025-12-30)
**Scope**: Fix BUG-001 and BUG-002
**Changes**:
- Fix list rendering order in FlexmarkAdapter or HTMLRenderer
- Add 'S' key handler to navigation JavaScript
- Add 2 regression tests
- Update CHANGELOG with hotfix notes

**Risk**: Low - localized changes, well-tested
**Test Count**: 324+ (322 existing + 2 new)

---

**Next Review**: 2025-12-30

**Notes**:
- v1.3.1 hotfix (BUG-001, BUG-002) completed and released
- BUG-003 and BUG-004 discovered during how-we-work.md presentation testing
- Target fix for BUG-003 and BUG-004: v3.0.0 or before
- Next priority: Table rendering support (BUG-003) and viewport overflow handling (BUG-004)
