# Event Storming: US-034 - Speaker Notes Rendering

**Date:** 2024-12-27
**Participants:** Development Team
**User Story:** US-034 - Speaker Notes Rendering / Speaker View

## User Story

**As a** presentation author
**I want** to view my speaker notes in a separate pane during presentation
**So that** I can reference talking points without the audience seeing them

## Domain Events (Time-ordered)

### 1. Presentation Opened
**When:** User opens the presentation HTML file
**What:** Browser loads index.html
**Triggers:** Initial slide display
**Data:** Presentation metadata, slide count, theme

### 2. Speaker View Requested
**When:** User presses 'S' key or clicks "Speaker View" button
**What:** Opens new browser window/tab with speaker interface
**Triggers:** Window synchronization setup
**Data:** Current slide index, window reference

### 3. Windows Synchronized
**When:** Speaker view window opens
**What:** Both windows establish communication channel
**Triggers:** Initial state sync
**Data:** Window IDs, communication protocol (localStorage/postMessage)

### 4. Current Slide Displayed (Main Window)
**When:** Slide navigation occurs in main window
**What:** Updates visible slide in presentation view
**Triggers:** Sync to speaker view
**Data:** Slide index, slide content

### 5. Speaker Notes Displayed (Speaker Window)
**When:** Slide changes or speaker view opens
**What:** Shows notes for current slide
**Triggers:** Next slide preview update
**Data:** Notes text, current slide index

### 6. Next Slide Previewed (Speaker Window)
**When:** Current slide changes
**What:** Shows preview of next slide
**Triggers:** None
**Data:** Next slide index, next slide content

### 7. Timer Started
**When:** First navigation action in presentation
**What:** Starts elapsed time counter
**Triggers:** Timer display updates
**Data:** Start timestamp

### 8. Timer Updated
**When:** Every second while presenting
**What:** Updates elapsed time display
**Triggers:** Visual time update
**Data:** Elapsed seconds, formatted time

### 9. Slide Navigation (Keyboard)
**When:** User presses arrow keys / space / home / end
**What:** Changes current slide index
**Triggers:** Slide display update, sync event
**Data:** New slide index, navigation direction

### 10. Windows Desynchronized
**When:** User closes one window or loses connection
**What:** Falls back to standalone mode
**Triggers:** Warning message (optional)
**Data:** Desync reason

## Commands (User Actions)

### Open Speaker View
**Actor:** Presenter
**Trigger:** Press 'S' key or click button
**Precondition:** Presentation is loaded
**Postcondition:** Speaker view window opens
**Business Rule:** Only one speaker view per presentation

### Navigate Slides (Main)
**Actor:** Presenter
**Trigger:** Keyboard navigation
**Precondition:** Focus on main window
**Postcondition:** Slide changes, speaker view syncs
**Business Rule:** Sync must be near-instant (<100ms)

### Navigate Slides (Speaker View)
**Actor:** Presenter
**Trigger:** Keyboard navigation in speaker window
**Precondition:** Focus on speaker window
**Postcondition:** Main window syncs to match
**Business Rule:** Bidirectional sync

### Close Speaker View
**Actor:** Presenter
**Trigger:** Close window or press 'Esc'
**Precondition:** Speaker view is open
**Postcondition:** Returns to standalone mode
**Business Rule:** Main window continues functioning

### Reset Timer
**Actor:** Presenter
**Trigger:** Press 'R' key (optional feature)
**Precondition:** Presentation active
**Postcondition:** Timer resets to 00:00
**Business Rule:** Warns before reset

## Aggregates

### SlideDeck (Existing)
**Responsibilities:**
- Contains all slides
- Provides slide lookup by index
- Validates slide count

**Already Implemented:** Yes (v1.0)

### Slide (Enhanced)
**Responsibilities:**
- Contains slide content (slots)
- Contains speaker notes (added in v1.0)
- Provides note access

**Changes:** None needed (notes field exists)

### PresentationState (New)
**Responsibilities:**
- Current slide index
- Total slide count
- Timer state (running, paused, elapsed)
- Window sync state

**Fields:**
- currentSlideIndex: Int
- totalSlides: Int
- timerStartTime: Option[Timestamp]
- timerElapsed: Seconds
- speakerViewOpen: Boolean

### WindowSync (New - JavaScript only)
**Responsibilities:**
- Cross-window communication
- State synchronization
- Desync detection

**Methods:**
- sendSlideChange(index)
- receiveSlideChange(index)
- heartbeat()

## Read Models (Queries)

### Get Current Slide
**Query:** getCurrentSlide(index: Int)
**Returns:** Slide with slots and notes
**Used By:** Both main and speaker view

### Get Next Slide Preview
**Query:** getNextSlide(currentIndex: Int)
**Returns:** Next slide content (heading/title only)
**Used By:** Speaker view

### Get Notes for Slide
**Query:** getNotesForSlide(index: Int)
**Returns:** Option[String] (speaker notes)
**Used By:** Speaker view

### Get Timer State
**Query:** getElapsedTime()
**Returns:** Formatted time string (MM:SS)
**Used By:** Speaker view

## Policies (Business Rules)

### P1: Single Speaker View
**Policy:** Only one speaker view window allowed per presentation
**Rationale:** Prevents sync confusion
**Implementation:** Check for existing window before opening

### P2: Bidirectional Sync
**Policy:** Navigation in either window syncs to the other
**Rationale:** Presenter may navigate from either window
**Implementation:** Event listeners on both windows

### P3: Graceful Degradation
**Policy:** If speaker view closes, main window continues functioning
**Rationale:** Presentation must not fail if sync breaks
**Implementation:** Try-catch around sync operations

### P4: Notes Are Optional
**Policy:** Slides without notes display "No notes for this slide"
**Rationale:** Not all slides need notes
**Implementation:** Check notes.isEmpty in speaker view

### P5: Timer Auto-Start
**Policy:** Timer starts on first slide navigation (not on load)
**Rationale:** Prevents timer running during setup
**Implementation:** Start timer on first navigation event

## External Systems

### Browser Storage API
**Purpose:** Cross-window communication (localStorage events)
**Alternatives:** postMessage API, BroadcastChannel API
**Dependency:** Modern browser support

### Browser Window API
**Purpose:** Open/close speaker view window
**Dependency:** window.open() permissions

## Open Questions

1. **Sync mechanism:** localStorage events vs postMessage vs BroadcastChannel?
   - **Decision:** localStorage events (broadest compatibility)

2. **Speaker view layout:** Fixed split vs resizable panes?
   - **Decision:** Fixed split (simpler MVP, resizable in v1.2)

3. **Timer features:** Just elapsed time or countdown + warnings?
   - **Decision:** Just elapsed time (MVP), countdown in v1.2

4. **Keyboard shortcuts:** Reuse existing or add speaker-specific?
   - **Decision:** Reuse existing (arrow keys, space, home, end, add 'S' for speaker view)

5. **Notes rendering:** Plain text vs markdown?
   - **Decision:** Plain text with newline preservation (MVP), markdown in v1.2

## Implementation Notes

### Rendering Strategy
- **Main presentation:** Existing HTMLRenderer (no changes)
- **Speaker view:** New HTML template with dual-pane layout
- **Sync:** JavaScript module for window communication

### File Output
- `index.html` - Main presentation (existing)
- `speaker.html` - Speaker view (new)
- `sync.js` - Window synchronization module (new)

Both HTML files embedded in same output directory, share same JavaScript sync code.

## Success Metrics

1. **Sync latency:** <100ms from navigation to sync
2. **Browser support:** Chrome, Firefox, Safari, Edge
3. **Reliability:** No desync under normal navigation
4. **Usability:** Presenter can navigate from either window

## Related Documentation

- US-004: Speaker Notes Parsing (v1.0 - completed)
- PDR-015: CLI UX Design (directory output enables speaker.html)
- ADR-006: Rendering Architecture

---

**Status:** ✅ Complete - Ready for Three Amigos
**Next Step:** Three Amigos session to refine acceptance criteria
