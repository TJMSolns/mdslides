# Three Amigos: US-034 - Speaker Notes Rendering

**Date:** 2024-12-27
**Participants:** Product Owner, Developer, QA Tester
**User Story:** US-034 - Speaker Notes Rendering / Speaker View

## User Story Recap

**As a** presentation author
**I want** to view my speaker notes in a separate pane during presentation
**So that** I can reference talking points without the audience seeing them

**Priority:** MUST for v1.1
**Dependencies:** US-004 (Speaker Notes Parsing) - ✅ Complete in v1.0

## Acceptance Criteria

### AC1: Opening Speaker View
**Given** I have a presentation open in my browser
**When** I press the 'S' key
**Then** a new browser window/tab opens with the speaker view interface
**And** the speaker view shows the current slide's notes
**And** the speaker view shows a preview of the next slide
**And** the speaker view shows elapsed time starting at 00:00

**Edge Cases:**
- If speaker view is already open, pressing 'S' should focus existing window (not open duplicate)
- If presentation has no slides, speaker view should show "No slides available"
- If browser blocks popup, display message: "Please allow popups for speaker view"

### AC2: Notes Display
**Given** the speaker view is open
**When** I navigate to a slide with notes
**Then** the notes area displays the speaker notes for that slide
**And** newlines in the notes are preserved (multi-line display)

**When** I navigate to a slide without notes
**Then** the notes area displays "No notes for this slide"

**Edge Cases:**
- Empty notes string (`notes: ""`) should display as blank (not "No notes")
- Very long notes should scroll within notes pane (no vertical overflow)
- Notes with special characters (quotes, HTML) should display correctly (escaped)

### AC3: Next Slide Preview
**Given** the speaker view is open
**When** I am on slide N (where N < total slides)
**Then** the preview pane shows slide N+1
**And** the preview shows the heading or title of the next slide

**When** I am on the last slide
**Then** the preview pane displays "End of presentation"

**Edge Cases:**
- Single-slide presentation should show "End of presentation" immediately
- Preview should scale to fit pane (CSS transform or max-width/height)
- Background images on next slide should not be shown in preview (performance)

### AC4: Bidirectional Synchronization
**Given** both main window and speaker view are open
**When** I press arrow keys in the main window
**Then** the speaker view updates to show notes/preview for the new slide
**And** the sync occurs within 100ms

**When** I press arrow keys in the speaker view
**Then** the main window updates to show the new slide
**And** the sync occurs within 100ms

**Edge Cases:**
- Rapid navigation (key held down) should not cause sync lag or desync
- If one window is closed, the other continues to function normally
- If browser tab is unfocused, sync should still occur when refocused

### AC5: Timer Display
**Given** the speaker view is open
**When** the presentation loads
**Then** the timer displays "00:00" and is not running

**When** I navigate to any slide (first navigation action)
**Then** the timer starts counting up from 00:00
**And** the timer updates every second in format "MM:SS"

**Examples:**
- After 5 seconds: "00:05"
- After 1 minute 30 seconds: "01:30"
- After 59 minutes 59 seconds: "59:59"
- After 1 hour: "60:00" (continues past 1 hour)

**Edge Cases:**
- Closing and reopening speaker view resets timer to 00:00
- Timer continues running even if presentation window loses focus
- Timer does not reset when navigating between slides

### AC6: Graceful Degradation
**Given** the speaker view is open
**When** I close the main presentation window
**Then** the speaker view continues to function (can still navigate)
**And** a warning message displays: "Main window closed - speaker view only"

**Given** the speaker view is open
**When** I close the speaker view window
**Then** the main presentation continues to function normally
**And** pressing 'S' again reopens a new speaker view

**Edge Cases:**
- Browser crash recovery: both windows resume independently
- Network storage failure (localStorage unavailable): fallback to postMessage

### AC7: Keyboard Shortcuts
**Given** the speaker view is open
**When** I press the following keys in speaker view
**Then** the following actions occur:

| Key | Action |
|-----|--------|
| → (Right arrow) | Next slide |
| ← (Left arrow) | Previous slide |
| Space | Next slide |
| Home | First slide |
| End | Last slide |
| Esc | Close speaker view |

**Edge Cases:**
- Shortcuts work even when notes pane is focused (not just on body)
- No conflicts with browser shortcuts (F11 fullscreen, etc.)

## Examples

### Example 1: Basic Speaker View Usage
```markdown
Given: Presentation with 3 slides, only slide 2 has notes
1. User opens presentation → sees slide 1
2. User presses 'S' → speaker view opens
3. Speaker view shows:
   - Current: Slide 1 / 3
   - Notes: "No notes for this slide"
   - Next: Preview of slide 2 heading
   - Timer: 00:00 (not started)
4. User presses → in main window
5. Both windows update:
   - Main: Shows slide 2
   - Speaker: Shows notes "Remember to emphasize key points", preview of slide 3, timer starts 00:00
6. User waits 30 seconds
7. Speaker view timer shows: 00:30
```

### Example 2: Array Notes Display
```markdown
Given: Slide with array notes:
notes:
  - "Point one: Introduction"
  - "Point two: Main concept"
  - "Point three: Call to action"

Speaker view displays:
┌─────────────────────────────┐
│ Notes:                      │
│ Point one: Introduction     │
│ Point two: Main concept     │
│ Point three: Call to action │
└─────────────────────────────┘

(Each line on separate line, newlines preserved)
```

### Example 3: Last Slide Behavior
```markdown
Given: Presentation with 5 slides
1. User navigates to slide 4
2. Speaker view shows:
   - Current: Slide 4 / 5
   - Next: Preview of slide 5
3. User presses →
4. Speaker view shows:
   - Current: Slide 5 / 5
   - Next: "End of presentation"
   - Timer: 02:15 (continued counting)
5. User presses → again
6. Nothing happens (already at end)
```

### Example 4: Desync Recovery
```markdown
Given: Both windows open and synced
1. User navigates to slide 3
2. User accidentally closes main window
3. Speaker view displays warning: "Main window closed - speaker view only"
4. Speaker view continues to show:
   - Slide content (current slide rendered in speaker view)
   - Notes for slide 3
   - Preview of slide 4
   - Timer continues
5. User can still navigate in speaker view
6. User reopens presentation in new tab
7. New tab starts at slide 1 (independent session)
```

### Example 5: Special Characters in Notes
```markdown
Given: Slide with notes containing HTML/special chars:
notes: "Use <strong> tags for emphasis & check \"quotes\""

Speaker view displays (escaped):
Use &lt;strong&gt; tags for emphasis &amp; check &quot;quotes&quot;

(Raw text, not rendered as HTML)
```

## Non-Functional Requirements

### Performance
- **Sync latency:** <100ms from navigation to sync completion
- **Timer accuracy:** ±1 second per hour
- **Memory usage:** <50MB additional for speaker view window
- **Browser support:** Chrome 90+, Firefox 88+, Safari 14+, Edge 90+

### Usability
- **Font size:** Notes text minimum 14px (readable from distance)
- **Preview size:** Next slide preview fills available space (responsive)
- **Timer position:** Always visible (fixed/sticky positioning)
- **Color contrast:** WCAG AA compliance for notes text

### Reliability
- **Sync failure rate:** <1% under normal network conditions
- **Recovery time:** <5 seconds to resume after tab refocus
- **Data loss:** Zero notes lost if sync fails (read-only display)

## Technical Constraints

### Browser APIs
- **localStorage:** Used for cross-window sync (localStorage events)
- **window.open():** Required for speaker view popup (user must allow popups)
- **setInterval():** Used for timer updates (1-second interval)
- **postMessage():** Fallback if localStorage unavailable

### Security
- **Same-origin policy:** Both windows must be same origin (file:// or same domain)
- **XSS prevention:** All notes content must be escaped before display
- **Popup blocking:** Graceful handling with user-facing error message

### Output Structure
- **Main presentation:** `OUTPUT_DIR/index.html` (unchanged from v1.0)
- **Speaker view:** `OUTPUT_DIR/speaker.html` (new file)
- **Sync module:** `OUTPUT_DIR/sync.js` (new file)
- **Shared assets:** Both HTML files reference same images/backgrounds

### Rendering Architecture
- **Main presentation:** Existing HTMLRenderer (no changes)
- **Speaker view:** New SpeakerViewRenderer
- **Notes rendering:** Plain text with `<pre>` or `<div>` with `white-space: pre-wrap`
- **Preview rendering:** Scaled-down version of slide HTML

## Open Questions & Decisions

### Q1: Sync mechanism?
**Options:**
- localStorage events (broad compatibility)
- postMessage (modern, explicit)
- BroadcastChannel API (cleanest but limited support)

**Decision:** localStorage events for MVP (v1.1), BroadcastChannel in v1.2
**Rationale:** Widest browser support, proven pattern

### Q2: Timer features?
**Options:**
- Just elapsed time (simple)
- Countdown + warnings (complex)
- Target time with color coding (medium)

**Decision:** Just elapsed time for v1.1, countdown in v1.2
**Rationale:** Meets MVP requirement, avoids complexity

### Q3: Notes rendering?
**Options:**
- Plain text (`<pre>`)
- Markdown rendering
- Rich text (HTML subset)

**Decision:** Plain text with newline preservation for v1.1, markdown in v1.2
**Rationale:** Aligns with US-004 parsing (newline-separated), safe (no XSS)

### Q4: Preview rendering?
**Options:**
- Full slide HTML (scaled)
- Heading/title only (text)
- Screenshot/thumbnail (complex)

**Decision:** Heading/title only for v1.1, full slide in v1.2
**Rationale:** Simpler, faster, meets core need

### Q5: Speaker view layout?
**Options:**
- Fixed split (3 panes: notes, preview, timer)
- Resizable panes (draggable dividers)
- Configurable (user preferences)

**Decision:** Fixed split for v1.1, resizable in v1.2
**Rationale:** Faster to implement, most users won't need customization

## Test Scenarios for Example Mapping

### Scenario 1: Happy Path
- Open presentation → press 'S' → navigate slides → verify sync
- **Variations:** 2 slides, 10 slides, 50 slides
- **Verifications:** Timer, notes display, preview, sync latency

### Scenario 2: Edge Cases - Notes
- Slide with no notes
- Slide with empty notes (`notes: ""`)
- Slide with very long notes (>500 words)
- Slide with special characters in notes

### Scenario 3: Edge Cases - Navigation
- First slide (no previous)
- Last slide (no next)
- Single-slide presentation
- Rapid navigation (key held down)

### Scenario 4: Edge Cases - Windows
- Close main window (speaker continues)
- Close speaker window (main continues)
- Open multiple speaker views (should focus existing)
- Browser blocks popups (error message)

### Scenario 5: Edge Cases - Sync
- Navigate in main window (speaker syncs)
- Navigate in speaker window (main syncs)
- Lose focus on tab (sync when refocused)
- localStorage disabled (fallback to postMessage)

### Scenario 6: Timer
- Timer starts on first navigation
- Timer continues across slides
- Timer survives tab unfocus/refocus
- Timer resets on speaker view close/reopen

### Scenario 7: Browser Compatibility
- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)
- Mobile browsers (informational - out of scope for v1.1)

## Out of Scope for v1.1

The following are explicitly deferred to v1.2 or later:

1. **Markdown rendering in notes** (v1.2)
2. **Full slide preview** (scaled HTML) (v1.2)
3. **Countdown timer** with warnings (v1.2)
4. **Resizable panes** in speaker view (v1.2)
5. **Current time clock** (wall clock display) (v1.2)
6. **Slide thumbnails** overview (v1.2)
7. **Mobile speaker view** (responsive layout) (v1.3)
8. **Remote control** (phone as remote) (v2.0)
9. **Presenter metrics** (slide duration analytics) (v2.0)
10. **Multi-presenter sync** (>2 windows) (v2.0)

## Success Criteria Summary

**US-034 is complete when:**
1. ✅ Pressing 'S' opens speaker view in new window
2. ✅ Speaker view displays current slide's notes (or "No notes for this slide")
3. ✅ Speaker view displays next slide heading/title preview
4. ✅ Speaker view displays elapsed time timer (starts on first navigation)
5. ✅ Navigation in either window syncs to the other within 100ms
6. ✅ Keyboard shortcuts work in both windows
7. ✅ Closing either window leaves the other functioning
8. ✅ All manual test scenarios pass on Chrome, Firefox, Safari, Edge
9. ✅ Example presentation (mdslides-tutorial) renders with speaker view
10. ✅ Documentation updated (INSTALL.md, CHANGELOG.md, tutorial)

## Next Steps

1. **Example Mapping** - Create detailed test scenarios
2. **TDD Implementation** - Red → Green → Refactor
3. **Manual Testing** - Browser compatibility verification
4. **Documentation** - Update user guide with speaker view instructions

---

**Status:** ✅ Complete - Ready for Example Mapping
**Next Step:** Create example-mapping-US-034.md with concrete test cases
