# Known Issues & Bug Tracking

**Last Updated**: 2025-12-27
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
Manual testing confirms ordered and unordered lists now render in correct source order.

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
Manual testing confirms pressing 'S' or 's' now opens speaker view in new window.

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

_None currently tracked_

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
1. **BUG-001**: Integration test for list rendering order
2. **BUG-002**: Test for 'S' key handler in navigation JS

### Code Review Checklist
- [ ] All keyboard handlers documented match implementation
- [ ] List rendering preserves source order
- [ ] Integration tests cover multi-element interactions
- [ ] Manual testing with actual rendered HTML

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

**Next Review**: 2025-12-28 (daily until hotfix released)
