# Acceptance Criteria: Presentation Timer

**Date**: 2025-12-29
**Reviewer**: Architect
**Participants**: Product Owner, Bench Developer
**Story**: As a presenter, I want to see elapsed presentation time so I can manage my presentation duration.
**Status**: ✅ APPROVED (with decisions documented)

---

## Review Summary

**Example Mapping**: [presentation-timer-examples.md](../example-maps/presentation-timer-examples.md)
**BDD Scenarios**: [presentation-timer.feature](../../../features/presentation-timer.feature)
**Domain Model**: [presentation-timer-aggregate.md](../../domain-models/aggregates/presentation-timer-aggregate.md)

**Rules Reviewed**: 10/10
**Examples Validated**: 34/34
**Invariants Verified**: 6/6
**Questions Resolved**: 6/6

---

## Acceptance Criteria (Final)

### AC1: Timer Auto-Start
**Given** a presentation is loaded in the browser
**When** the presentation finishes loading
**Then** the timer state should be Running
**And** the elapsed time should be "00:00:00"
**And** the timer should be displayed in the footer bottom-left corner

**Validation**:
- ✅ Aligns with domain model: Timer starts in NotStarted, transitions to Running
- ✅ Respects invariant: State transitions (NotStarted → Running)
- ✅ Maps to BDD: Scenario 1

---

### AC2: Timer Format Display
**Given** the timer is in Running state
**And** N seconds have elapsed
**When** I view the timer display
**Then** the formatted time should be in "hh:mm:ss" format with zero-padding

**Examples**:
| Seconds | Formatted |
|---------|-----------|
| 0       | 00:00:00  |
| 59      | 00:00:59  |
| 60      | 00:01:00  |
| 3600    | 01:00:00  |
| 86399   | 23:59:59  |
| 86400   | 24:00:00  |
| 359999  | 99:59:59  |

**Validation**:
- ✅ Aligns with domain model: `formattedTime()` method specification
- ✅ Maximum duration: 99:59:59 (99 hours 59 minutes 59 seconds)
- ✅ Maps to BDD: Scenarios 3, 17

---

### AC3: Timer Increments
**Given** the timer is in Running state
**When** time passes
**Then** the timer should increment by 1 second every second
**And** the display should update every second

**Validation**:
- ✅ Aligns with domain model: `TimerUpdated` event every 1 second
- ✅ Respects invariant: Monotonic time (never decreases)
- ✅ Maps to BDD: Scenario 2

---

### AC4: Pause on Break Mode
**Given** the timer is in Running state
**And** the elapsed time is "00:05:30"
**When** the presenter presses the 'B' key
**Then** the timer state should be Paused
**And** the elapsed time should remain "00:05:30"
**And** the timer display should stop updating

**Validation**:
- ✅ Aligns with domain model: `pause()` command specification
- ✅ Respects invariant: Cannot pause when not running
- ✅ Maps to BDD: Scenario 4

---

### AC5: Resume After Break
**Given** the timer is in Paused state
**And** the elapsed time is "00:10:00"
**And** the timer was paused for 120 seconds
**When** the presenter presses the 'B' key again
**Then** the timer state should be Running
**And** the elapsed time should be "00:10:00"
**And** the timer display should resume updating every second

**Validation**:
- ✅ Aligns with domain model: `resume()` command specification
- ✅ Respects invariant: Cannot resume when not paused
- ✅ Maps to BDD: Scenario 5

---

### AC6: Paused Duration Excluded
**Given** the timer is in Running state
**And** 300 seconds have elapsed
**When** the presenter pauses the timer
**And** 120 seconds pass while paused
**And** the presenter resumes the timer
**And** 300 seconds more pass
**Then** the elapsed time should be "00:10:00"
**And** the total paused duration should be 120 seconds

**Validation**:
- ✅ Aligns with domain model: `elapsedSeconds()` calculation excludes `totalPausedDuration`
- ✅ Respects invariant: Paused duration ≤ total runtime
- ✅ Maps to BDD: Scenarios 6, 7

---

### AC7: Cross-Window Synchronization
**Given** the timer is in Running state
**And** the elapsed time is "00:05:00"
**When** the presenter opens the speaker view
**Then** the speaker view timer should display "00:05:00"
**And** the speaker view timer state should be Running

**And Given** the speaker view is open
**When** the presenter pauses the timer in the main window
**Then** the speaker view timer should also pause
**And** both timers should display the same elapsed time

**Validation**:
- ✅ Aligns with domain model: `TimerStateSynced` event specification
- ✅ Integration point: BroadcastChannel API (same as slide sync)
- ✅ Maps to BDD: Scenarios 8, 9, 10

---

### AC8: Reject Invalid State Transitions
**Given** the timer is in NotStarted state
**When** the presenter attempts to pause the timer
**Then** the pause command should be rejected
**And** the timer state should remain NotStarted
**And** an error message should indicate "Cannot pause when not running"

**And Given** the timer is in Running state
**When** the presenter attempts to resume the timer
**Then** the resume command should be rejected
**And** the timer state should remain Running
**And** an error message should indicate "Cannot resume when not paused"

**Validation**:
- ✅ Aligns with domain model: State transition invariants
- ✅ Respects invariant: Pause precondition, Resume precondition
- ✅ Maps to BDD: Scenarios 11, 12

---

### AC9: No Reset Capability
**Given** the timer is in Running state
**And** the elapsed time is "00:15:30"
**When** the presenter attempts to reset the timer
**Then** the reset command should not be available
**And** the elapsed time should remain "00:15:30"

**Validation**:
- ✅ Aligns with domain model: Invariant "Start Once" (no reset)
- ✅ Business rationale: Timer represents true session duration for history logging
- ✅ Maps to BDD: Scenario 13

---

### AC10: Navigation Persistence
**Given** the timer is in Running state
**And** the elapsed time is "00:03:00"
**And** the presentation is on slide 5
**When** the presenter navigates to slide 10
**Then** the timer state should remain Running
**And** the timer should continue incrementing
**And** the elapsed time should be greater than "00:03:00"

**And Given** the timer is in Paused state
**And** the elapsed time is "00:07:45"
**When** the presenter navigates between slides
**Then** the timer state should remain Paused
**And** the elapsed time should remain "00:07:45"

**Validation**:
- ✅ Aligns with domain model: Timer state independent of slide navigation
- ✅ Integration point: Timer does not listen to slide change events
- ✅ Maps to BDD: Scenarios 14, 15

---

### AC11: Presentation End
**Given** the timer is in Running state
**And** the elapsed time is "00:45:00"
**When** the presenter closes the presentation window
**Then** a PresentationEnded event should be emitted
**And** the final elapsed time should be recorded as "00:45:00"

**Validation**:
- ✅ Aligns with domain model: `PresentationEnded` event specification
- ✅ Future integration: History logging (v3.1.0) will consume this event
- ✅ Maps to BDD: Scenario 16

---

## Invariant Validation Matrix

| Invariant | Acceptance Criteria | BDD Scenarios | Verified |
|-----------|---------------------|---------------|----------|
| State Exclusivity | AC1, AC4, AC5, AC8 | 1, 4, 5, 11, 12 | ✅ |
| Monotonic Time | AC3, AC6, AC10 | 2, 6, 7, 14 | ✅ |
| Pause Precondition | AC4, AC8 | 4, 11 | ✅ |
| Resume Precondition | AC5, AC8 | 5, 12 | ✅ |
| Paused Duration Bound | AC6 | 6, 7 | ✅ |
| Start Once (No Reset) | AC9 | 13 | ✅ |

**Result**: All 6 domain invariants have corresponding acceptance criteria and BDD scenarios.

---

## Questions Resolved

### Q1: Timer Display Appearance When Paused
**Question**: Should the timer display change appearance when paused?

**Decision**: ✅ YES - Add visual indicator
- Timer text color changes when paused (CSS class: `.timer-paused`)
- Display shows small "⏸" pause icon next to time
- Maintains same "hh:mm:ss" format
- CSS styling applied via theme files (light.css, dark.css, high-contrast.css)

**Rationale**: Improves usability - presenter can quickly see if timer is paused without relying solely on break screen

**Impact**: UI implementation, CSS theme updates

---

### Q2: Timer Accuracy Requirements
**Question**: What is acceptable timer drift over long presentations?

**Decision**: ✅ Acceptable drift: ±2 seconds per hour
- Use `setInterval(1000)` with periodic drift correction
- Every 60 seconds, recalculate elapsed time from epoch timestamps
- Drift correction adjusts display without resetting timer

**Rationale**: Balance between implementation simplicity and accuracy. 2-second drift over 1 hour is imperceptible for presentation timing.

**Impact**: Implementation uses hybrid approach (setInterval + periodic recalculation)

---

### Q3: Lap Times (Time Per Slide)
**Question**: Should speaker notes show time spent on current slide?

**Decision**: ✅ DEFERRED to v3.1.0
- v3.0.0 shows only total elapsed time
- v3.1.0 "History Logging" feature will track per-slide lap times
- Future enhancement documented in aggregate doc

**Rationale**: Keeps v3.0.0 scope focused on core timer functionality

**Impact**: No impact on v3.0.0 implementation

---

### Q4: Screen Reader Behavior
**Question**: Should screen readers announce timer updates?

**Decision**: ✅ `aria-live="off"` (no announcements)
- Timer display has `aria-live="off"` attribute
- Timer does not announce every second (would be disruptive)
- Screen reader users can manually navigate to timer element to query current time

**Rationale**: Announcing every second would make presentation content unreadable for screen reader users

**Impact**: Accessibility implementation, HTML attributes

**Validation**: Complies with WCAG 2.1 Level AA (avoid disruptive live regions)

---

### Q5: Maximum Duration Handling
**Question**: What happens if presentation exceeds 99:59:59?

**Decision**: ✅ Continue incrementing with 3-digit hours
- Timer supports hours ≥100 with 3+ digit display (e.g., "100:00:00", "123:45:67")
- Display width adjusts dynamically (CSS: `min-width: fit-content`)
- Edge case test: 100 hours = 360000 seconds

**Rationale**: Extremely rare edge case (100+ hour presentation), but graceful degradation is better than overflow error

**Impact**: CSS styling (dynamic width), format function allows hours >99

---

### Q6: LocalStorage Persistence
**Question**: Should timer state persist across page refreshes?

**Decision**: ✅ NO PERSISTENCE in v3.0.0
- Timer does not save state to LocalStorage
- Page refresh resets timer to 00:00:00
- Future enhancement: v3.1.0 may add persistence if requested by users

**Rationale**: Refreshing during active presentation is rare. Simpler implementation without persistence.

**Impact**: No LocalStorage integration in v3.0.0

---

## Design Decisions Summary

| Decision | Outcome | Impact on Implementation |
|----------|---------|--------------------------|
| Sync Mechanism | BroadcastChannel API | Reuse existing sync infrastructure |
| Persistence | None (v3.0.0) | No LocalStorage code needed |
| Reset Capability | Not available | No reset command/button |
| Multi-Window | Independent timers per window | BroadcastChannel scoped to window |
| Screen Reader | `aria-live="off"` | Add ARIA attribute to timer element |
| Pause Visual | Color change + pause icon | CSS classes, theme updates |
| Accuracy | ±2 sec/hour drift tolerance | Hybrid setInterval + recalculation |
| Max Duration | Support 3+ digit hours | Dynamic CSS width, unbounded hours |
| Lap Times | Deferred to v3.1.0 | Not in v3.0.0 scope |

---

## Implementation Checklist

### Domain Layer (Scala)
- [ ] Create `TimerState` enum (NotStarted, Running, Paused)
- [ ] Create `PresentationTimer` case class with 4 fields
- [ ] Implement `start()` command with NotStarted precondition
- [ ] Implement `pause()` command with Running precondition
- [ ] Implement `resume()` command with Paused precondition
- [ ] Implement `elapsedSeconds()` query with pause duration exclusion
- [ ] Implement `formattedTime()` query with hh:mm:ss format
- [ ] Define `TimerError` enum for state transition errors
- [ ] Define `TimerStarted`, `TimerPaused`, `TimerResumed`, `TimerUpdated`, `PresentationEnded` events

### Infrastructure Layer (JavaScript)
- [ ] Create `presentation-timer.js` module
- [ ] Initialize PresentationTimer on DOMContentLoaded
- [ ] Set up `setInterval(1000)` with periodic drift correction (every 60s)
- [ ] Wire 'B' key handler to pause/resume toggle
- [ ] Update footer display element every second while running
- [ ] Add CSS classes for paused state (`.timer-paused`)
- [ ] Add pause icon element (⏸) with conditional visibility
- [ ] Set up BroadcastChannel sync for `TimerStateSynced` events
- [ ] Emit `PresentationEnded` event on window unload
- [ ] Add `aria-live="off"` attribute to timer display element

### UI Layer (HTML/CSS)
- [ ] Add timer display element to footer template (bottom-left)
- [ ] Style timer for light theme (light.css)
- [ ] Style timer for dark theme (dark.css)
- [ ] Style timer for high-contrast theme (high-contrast.css)
- [ ] Add `.timer-paused` CSS class (color change)
- [ ] Add pause icon styling (⏸)
- [ ] Test responsive layout (timer should not overlap content)
- [ ] Test all themes for readability

### Testing (ScalaTest + Property-Based)
- [ ] Unit test: `start()` transitions NotStarted → Running
- [ ] Unit test: `pause()` transitions Running → Paused
- [ ] Unit test: `resume()` transitions Paused → Running
- [ ] Unit test: `pause()` rejects when NotStarted
- [ ] Unit test: `resume()` rejects when Running
- [ ] Unit test: `elapsedSeconds()` excludes paused duration
- [ ] Unit test: `formattedTime()` formats all examples correctly (9 cases)
- [ ] Property test: Monotonic time (elapsed never decreases)
- [ ] Property test: Paused duration ≤ total runtime
- [ ] Integration test: Cross-window sync via BroadcastChannel
- [ ] Integration test: Timer persists during slide navigation
- [ ] Integration test: Multiple pause/resume cycles

### Documentation
- [ ] Update user guide with 'B' key for pause/resume
- [ ] Update speaker notes template with timer usage
- [ ] Update accessibility documentation with `aria-live="off"` rationale
- [ ] Update CHANGELOG with v3.0.0 timer feature

---

## Acceptance Criteria Sign-Off

**Architect Review**: ✅ APPROVED
- All acceptance criteria align with domain model
- All invariants have corresponding tests
- All state transitions validated
- All integration points identified

**Product Owner Review**: ✅ APPROVED
- All questions resolved with clear decisions
- Scope clearly defined (v3.0.0 vs. v3.1.0)
- Acceptance criteria testable and unambiguous
- Visual design decisions documented (pause indicator)

**Ready for Phase 3 (TDD Implementation)**: ✅ YES

---

## Traceability Matrix

| User Story | Acceptance Criteria | BDD Scenarios | Domain Invariants | Implementation Checklist |
|------------|---------------------|---------------|-------------------|-------------------------|
| Timer auto-start | AC1 | Scenario 1 | State Exclusivity | Domain: start(), UI: DOMContentLoaded |
| Display format | AC2 | Scenarios 3, 17 | - | Domain: formattedTime(), UI: display update |
| Timer increments | AC3 | Scenario 2 | Monotonic Time | Infrastructure: setInterval |
| Pause on break | AC4 | Scenario 4 | Pause Precondition | Domain: pause(), UI: B key handler |
| Resume after break | AC5 | Scenario 5 | Resume Precondition | Domain: resume(), UI: B key handler |
| Exclude paused time | AC6 | Scenarios 6, 7 | Paused Duration Bound | Domain: elapsedSeconds() calculation |
| Cross-window sync | AC7 | Scenarios 8, 9, 10 | - | Infrastructure: BroadcastChannel |
| Reject invalid transitions | AC8 | Scenarios 11, 12 | Pause/Resume Preconditions | Domain: Either[TimerError, ...] |
| No reset | AC9 | Scenario 13 | Start Once | Domain: no reset() method |
| Navigation persistence | AC10 | Scenarios 14, 15 | - | Infrastructure: timer independent of slides |
| Presentation end | AC11 | Scenario 16 | - | Infrastructure: window unload event |

**Coverage**: 11 acceptance criteria, 17 BDD scenarios, 6 domain invariants, 37 implementation tasks

---

**Last Updated**: 2025-12-29
**Next Phase**: Phase 3.1 - Test-First Pairing (TDD Implementation)

**Sign-off**:
- ✅ Architect: All acceptance criteria validated against domain model
- ✅ Product Owner: All decisions documented, scope approved
- ✅ Bench Developer: Implementation checklist ready

**Status**: 🟢 READY FOR IMPLEMENTATION
