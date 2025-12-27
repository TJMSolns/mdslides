# Example Mapping: US-034 - Speaker Notes Rendering

**Date:** 2024-12-27
**User Story:** US-034 - Speaker Notes Rendering / Speaker View
**Status:** Ready for TDD Implementation

## Story

**As a** presentation author
**I want** to view my speaker notes in a separate pane during presentation
**So that** I can reference talking points without the audience seeing them

## Rules

### R1: Single Speaker View Window
Only one speaker view window per presentation session.

### R2: Bidirectional Sync
Navigation in either window syncs to the other immediately (<100ms).

### R3: Graceful Degradation
If one window closes, the other continues functioning.

### R4: Optional Notes
Slides without notes display "No notes for this slide".

### R5: Timer Auto-Start
Timer starts on first navigation action (not on page load).

### R6: Next Slide Preview
Preview shows heading/title of next slide, or "End of presentation" if on last slide.

## Examples

### Example 1: Opening Speaker View
**Rule:** R1 (Single Speaker View)

**Scenario 1.1: First time opening speaker view**
```gherkin
Given: Presentation loaded in browser (slide 1 of 3)
When: User presses 'S' key
Then: New window opens with speaker view
  And: Speaker view shows "Slide 1 / 3"
  And: Notes area shows "No notes for this slide" (slide 1 has no notes)
  And: Preview shows heading of slide 2
  And: Timer shows "00:00" (not started)
```

**Scenario 1.2: Speaker view already open**
```gherkin
Given: Speaker view is already open in separate window
When: User presses 'S' in main window
Then: Existing speaker view window gains focus
  And: No new window is created
```

**Scenario 1.3: Browser blocks popups**
```gherkin
Given: Browser popup blocker is enabled
When: User presses 'S' key
Then: Main window displays message: "Please allow popups to open speaker view"
  And: No speaker view opens
```

### Example 2: Notes Display
**Rule:** R4 (Optional Notes)

**Scenario 2.1: Slide with simple string notes**
```markdown
Input markdown:
---
template: content
notes: "Remember to emphasize the key point"
---
## Main Concept
Content here

Speaker view displays:
┌─────────────────────────────────────┐
│ Speaker Notes:                      │
│ Remember to emphasize the key point │
└─────────────────────────────────────┘
```

**Scenario 2.2: Slide with array notes (multi-line)**
```markdown
Input markdown:
---
template: content
notes:
  - "First talking point"
  - "Second talking point"
  - "Third talking point"
---
## Agenda
Content here

Speaker view displays:
┌─────────────────────────────┐
│ Speaker Notes:              │
│ First talking point         │
│ Second talking point        │
│ Third talking point         │
└─────────────────────────────┘

(Each point on separate line, newlines preserved)
```

**Scenario 2.3: Slide without notes field**
```markdown
Input markdown:
---
template: content
---
## No Notes Slide
Content here

Speaker view displays:
┌─────────────────────────────┐
│ Speaker Notes:              │
│ No notes for this slide     │
└─────────────────────────────┘
```

**Scenario 2.4: Slide with empty notes string**
```markdown
Input markdown:
---
template: content
notes: ""
---
## Empty Notes
Content here

Speaker view displays:
┌─────────────────────────────┐
│ Speaker Notes:              │
│                             │
│ (blank, not "No notes")     │
└─────────────────────────────┘
```

**Scenario 2.5: Notes with special characters**
```markdown
Input markdown:
notes: "Use <strong> tags & check \"quotes\""

Speaker view displays (HTML-escaped):
Use &lt;strong&gt; tags &amp; check &quot;quotes&quot;

(Prevents XSS, displays as plain text)
```

### Example 3: Next Slide Preview
**Rule:** R6 (Next Slide Preview)

**Scenario 3.1: Preview on middle slide**
```gherkin
Given: Presentation with 5 slides
  And: Currently on slide 2
When: Speaker view is displayed
Then: Preview area shows:
  "Next: Introduction to Concepts" (heading of slide 3)
```

**Scenario 3.2: Preview on last slide**
```gherkin
Given: Presentation with 5 slides
  And: Currently on slide 5 (last)
When: Speaker view is displayed
Then: Preview area shows:
  "Next: End of presentation"
```

**Scenario 3.3: Single-slide presentation**
```gherkin
Given: Presentation with 1 slide
  And: Currently on slide 1
When: Speaker view is displayed
Then: Preview area shows:
  "Next: End of presentation"
```

**Scenario 3.4: Preview updates on navigation**
```gherkin
Given: Speaker view open on slide 2 of 5
  And: Preview shows "Next: Slide 3 Heading"
When: User navigates to slide 3
Then: Preview updates to show:
  "Next: Slide 4 Heading"
```

### Example 4: Bidirectional Sync
**Rule:** R2 (Bidirectional Sync)

**Scenario 4.1: Navigate in main window**
```gherkin
Given: Both windows open, showing slide 1
When: User presses → in main window
Then: Main window shows slide 2
  And: Speaker view syncs to slide 2 within 100ms
  And: Speaker view shows notes for slide 2
  And: Speaker view preview updates to slide 3
```

**Scenario 4.2: Navigate in speaker window**
```gherkin
Given: Both windows open, showing slide 1
When: User presses → in speaker window
Then: Speaker view shows slide 2
  And: Main window syncs to slide 2 within 100ms
  And: Speaker view shows notes for slide 2
  And: Speaker view preview updates to slide 3
```

**Scenario 4.3: Rapid navigation**
```gherkin
Given: Both windows open on slide 1 of 10
When: User presses → five times rapidly (within 2 seconds)
Then: Both windows show slide 6
  And: No intermediate states are visible
  And: Speaker view shows correct notes for slide 6
  And: Preview shows slide 7
  And: No sync lag or desync occurs
```

**Scenario 4.4: Home/End navigation**
```gherkin
Given: Both windows open on slide 5 of 10
When: User presses Home in speaker window
Then: Both windows jump to slide 1
  And: Sync occurs within 100ms

Given: Both windows on slide 1
When: User presses End in main window
Then: Both windows jump to slide 10
  And: Speaker preview shows "End of presentation"
```

### Example 5: Timer Behavior
**Rule:** R5 (Timer Auto-Start)

**Scenario 5.1: Timer on initial load**
```gherkin
Given: Speaker view just opened
When: No navigation has occurred yet
Then: Timer displays "00:00"
  And: Timer is not running (not incrementing)
```

**Scenario 5.2: Timer starts on first navigation**
```gherkin
Given: Speaker view open, timer shows "00:00" (not started)
When: User navigates to slide 2 (first navigation action)
Then: Timer starts counting
  And: Timer updates every second
  And: After 5 seconds, timer shows "00:05"
```

**Scenario 5.3: Timer continues across slides**
```gherkin
Given: Timer started at 00:00, now shows "02:30"
When: User navigates to slide 5
Then: Timer continues from "02:30" (does not reset)
  And: Timer continues incrementing every second
```

**Scenario 5.4: Timer format examples**
```gherkin
After 0 seconds:   00:00
After 5 seconds:   00:05
After 59 seconds:  00:59
After 60 seconds:  01:00
After 90 seconds:  01:30
After 599 seconds: 09:59
After 600 seconds: 10:00
After 3599 seconds: 59:59
After 3600 seconds: 60:00  (continues past 1 hour)
After 7200 seconds: 120:00
```

**Scenario 5.5: Timer persists across tab unfocus**
```gherkin
Given: Timer running, shows "05:00"
When: User switches to different browser tab for 30 seconds
  And: User switches back to speaker view
Then: Timer shows "05:30" (continued counting in background)
```

**Scenario 5.6: Timer resets on speaker view close/reopen**
```gherkin
Given: Timer running, shows "10:00"
When: User closes speaker view window
  And: User presses 'S' to reopen speaker view
Then: Timer resets to "00:00" (not started)
  And: Timer starts on next navigation
```

### Example 6: Graceful Degradation
**Rule:** R3 (Graceful Degradation)

**Scenario 6.1: Close main window**
```gherkin
Given: Both windows open, synced on slide 3
When: User closes main presentation window
Then: Speaker view continues functioning
  And: Speaker view displays warning: "Main window closed"
  And: Speaker view still shows current slide info
  And: User can still navigate in speaker view
  And: Timer continues running
```

**Scenario 6.2: Close speaker window**
```gherkin
Given: Both windows open, synced on slide 3
When: User closes speaker view window
Then: Main window continues functioning normally
  And: User can still navigate in main window
  And: User can press 'S' to reopen speaker view
  And: Reopened speaker view syncs to current slide
```

**Scenario 6.3: localStorage unavailable (fallback)**
```gherkin
Given: Browser has localStorage disabled
When: User presses 'S' to open speaker view
Then: Speaker view opens using postMessage fallback
  And: Sync still works bidirectionally
  And: User sees no difference in behavior
```

### Example 7: Keyboard Shortcuts in Speaker View
**Rule:** R2 (Bidirectional Sync)

**Scenario 7.1: Arrow keys**
```gherkin
Given: Speaker view focused, on slide 2 of 5
When: User presses →
Then: Advances to slide 3 in both windows

When: User presses ←
Then: Goes back to slide 2 in both windows
```

**Scenario 7.2: Space key**
```gherkin
Given: Speaker view focused, on slide 2 of 5
When: User presses Space
Then: Advances to slide 3 in both windows
```

**Scenario 7.3: Home/End keys**
```gherkin
Given: Speaker view focused, on slide 3 of 5
When: User presses Home
Then: Jumps to slide 1 in both windows

When: User presses End
Then: Jumps to slide 5 in both windows
```

**Scenario 7.4: Escape key**
```gherkin
Given: Speaker view is open
When: User presses Esc in speaker view
Then: Speaker view window closes
  And: Main window continues functioning
```

### Example 8: Edge Cases - Boundary Conditions

**Scenario 8.1: Navigate before first slide**
```gherkin
Given: Currently on slide 1 (first)
When: User presses ← in speaker view
Then: Remains on slide 1 (no action)
  And: No sync event fired
```

**Scenario 8.2: Navigate after last slide**
```gherkin
Given: Currently on slide 5 (last of 5)
When: User presses → in main window
Then: Remains on slide 5 (no action)
  And: Preview still shows "End of presentation"
```

**Scenario 8.3: Very long notes (>500 words)**
```gherkin
Given: Slide with notes containing 1000 words
When: Speaker view displays the slide
Then: Notes area shows all text
  And: Notes area has vertical scrollbar
  And: Text does not overflow container
  And: Font size remains readable (min 14px)
```

**Scenario 8.4: Notes with many newlines**
```gherkin
Given: Slide with notes:
notes:
  - "Point 1"
  - ""
  - "Point 2"
  - ""
  - ""
  - "Point 3"

When: Speaker view displays the slide
Then: Notes area shows:
Point 1

Point 2


Point 3

(Blank lines preserved exactly as specified)
```

## Test Implementation Plan

### Phase 1: Domain Model (Scala)
**Module:** `domain/src/.../domain/PresentationState.scala` (new)

**Tests to write:**
1. Create PresentationState with initial values
2. Update current slide index
3. Start timer (records start time)
4. Calculate elapsed time from start
5. Validate slide index bounds (0 to slideCount-1)

**TDD Cycle:** 5 tests

---

### Phase 2: Sync Module (JavaScript)
**Module:** `infrastructure/resources/sync.js` (new)

**Tests to write:**
1. Send slide change event via localStorage
2. Receive slide change event via localStorage
3. Fallback to postMessage if localStorage unavailable
4. Prevent sync loops (ignore own events)
5. Handle malformed sync messages

**TDD Cycle:** 5 tests (JavaScript tests using Jest or similar)

---

### Phase 3: Speaker View Renderer (Scala)
**Module:** `infrastructure/src/.../infrastructure/rendering/SpeakerViewRenderer.scala` (new)

**Tests to write:**
1. Render speaker view HTML structure
2. Embed slide data as JSON
3. Include sync.js script reference
4. Render notes area with placeholder
5. Render preview area
6. Render timer area
7. Escape HTML in notes content
8. Handle missing notes (display "No notes for this slide")
9. Handle empty notes (display blank)
10. Format timer display (MM:SS)

**TDD Cycle:** 10 tests

---

### Phase 4: CLI Integration
**Module:** `cli/src/.../cli/Main.scala` (modify)

**Tests to write:**
1. Render both index.html and speaker.html
2. Both files share same images/ directory
3. Both files share same sync.js file
4. Verify speaker.html references sync.js correctly
5. Verify index.html references sync.js correctly

**TDD Cycle:** 5 tests (integration level)

---

### Phase 5: Manual Testing (Browser)
**No automated tests - manual verification required**

**Test cases:**
1. Chrome: Open presentation, press 'S', verify sync
2. Firefox: Same as Chrome
3. Safari: Same as Chrome
4. Edge: Same as Chrome
5. Popup blocker: Verify error message
6. localStorage disabled: Verify postMessage fallback
7. Rapid navigation: Verify no desync
8. Close windows: Verify graceful degradation
9. Timer accuracy: Verify ±1 second over 10 minutes
10. Example presentation: Verify mdslides-tutorial works with speaker view

---

## Total Test Count Estimate

| Phase | Test Type | Count |
|-------|-----------|-------|
| Phase 1 | Scala unit tests | 5 |
| Phase 2 | JavaScript unit tests | 5 |
| Phase 3 | Scala unit tests | 10 |
| Phase 4 | Scala integration tests | 5 |
| Phase 5 | Manual browser tests | 10 |
| **TOTAL** | | **35 tests** |

**Automated tests:** 25
**Manual tests:** 10

## Implementation Order

1. ✅ Event Storming (completed)
2. ✅ Three Amigos (completed)
3. ✅ Example Mapping (completed)
4. **→ TDD Phase 1:** PresentationState domain model
5. **→ TDD Phase 2:** sync.js module
6. **→ TDD Phase 3:** SpeakerViewRenderer
7. **→ TDD Phase 4:** CLI integration
8. **→ Phase 5:** Manual browser testing
9. **→ Documentation:** Update INSTALL.md, CHANGELOG.md, tutorial
10. **→ Release:** v1.1.0 packaging

## Questions for TDD

### Q1: Where to put JavaScript tests?
**Options:**
- Skip JS tests, rely on manual testing
- Use Node.js + Jest in build pipeline
- Use browser-based test runner (Jasmine)

**Decision:** Skip automated JS tests for MVP, rely on manual testing
**Rationale:** Reduces tooling complexity, JS code is simple enough for manual verification

### Q2: How to represent timer in PresentationState?
**Options:**
- Store start timestamp, calculate elapsed on query
- Store elapsed seconds, update every second
- Store both start time and current elapsed

**Decision:** Store start timestamp (Option[Long]), calculate elapsed on query
**Rationale:** Simpler state model, no need to update every second, more accurate

### Q3: How to test sync.js without browser?
**Options:**
- Node.js with jsdom (simulates browser APIs)
- Manual testing only
- Browser automation (Selenium/Puppeteer)

**Decision:** Manual testing only for v1.1
**Rationale:** Fastest path to MVP, defer automated browser testing to v1.2

### Q4: Should PresentationState be in domain or infrastructure?
**Options:**
- Domain (pure business logic)
- Infrastructure (tied to rendering)

**Decision:** Domain module
**Rationale:** Represents presentation state aggregate, independent of rendering concerns

### Q5: How to embed slide data in speaker.html?
**Options:**
- Inline JSON in `<script>` tag
- Separate JSON file loaded via fetch
- LocalStorage pre-populated by main window

**Decision:** Inline JSON in `<script>` tag
**Rationale:** Self-contained file, works offline, no async loading needed

## Success Criteria

**Example Mapping is complete when:**
1. ✅ All rules identified (6 rules: R1-R6)
2. ✅ All example scenarios written (8 examples, 30+ scenarios)
3. ✅ Edge cases explicitly covered (boundary conditions, error cases)
4. ✅ Test implementation plan created (5 phases, 35 tests)
5. ✅ Implementation order defined
6. ✅ Open questions resolved (5 decisions documented)

**Ready for TDD when:**
- Test plan is clear and unambiguous
- Each phase has measurable completion criteria
- All dependencies are identified
- Questions are resolved or explicitly deferred

---

**Status:** ✅ Complete - Ready for TDD Implementation
**Next Step:** TDD Phase 1 - PresentationState domain model (5 tests)
