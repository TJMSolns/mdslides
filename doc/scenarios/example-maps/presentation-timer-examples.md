# Example Mapping: Presentation Timer

**Date**: 2025-12-29
**Facilitator**: Product Owner
**Participants**: Architect, Bench Developer
**Story**: As a presenter, I want to see elapsed presentation time so I can manage my presentation duration.
**Feature File**: [presentation-timer.feature](../../../features/presentation-timer.feature)
**Domain Model**: [presentation-timer-aggregate.md](../../domain-models/aggregates/presentation-timer-aggregate.md)

---

## Story Card (Yellow)

**User Story**:
> As a presenter
> I want to see elapsed presentation time
> So that I can manage my presentation duration

**Acceptance Criteria**:
- Timer displays in footer (bottom-left corner)
- Timer shows format hh:mm:ss (e.g., "00:15:30")
- Timer automatically starts when presentation loads
- Timer can be paused/resumed with 'B' key
- Paused time does NOT count toward elapsed time
- Timer syncs between main presentation and speaker view

---

## Business Rules (Blue Cards)

### Rule 1: Timer Lifecycle
**Statement**: Timer starts automatically when presentation loads and runs continuously until presentation closes.

**Examples** (Green Cards):
- ✅ Presentation loads → Timer starts at 00:00:00
- ✅ Timer running for 5 minutes → Display shows "00:05:00"
- ✅ Timer running for 1 hour 23 minutes 45 seconds → Display shows "01:23:45"
- ✅ Presentation closed after 45 minutes → Final elapsed time is 00:45:00

**Counter-Examples** (Red Cards):
- ❌ Timer does not start until user presses a key
- ❌ Timer resets to 00:00:00 during navigation
- ❌ Timer stops when switching slides

**BDD Scenarios**:
- Scenario 1: Timer starts when presentation loads
- Scenario 16: Timer ends when presentation closes

---

### Rule 2: Timer Display Format
**Statement**: Timer always displays elapsed time in hh:mm:ss format with zero-padding.

**Examples** (Green Cards):
- ✅ 0 seconds → "00:00:00"
- ✅ 59 seconds → "00:00:59"
- ✅ 60 seconds → "00:01:00"
- ✅ 61 seconds → "00:01:01"
- ✅ 3599 seconds → "00:59:59"
- ✅ 3600 seconds → "01:00:00"
- ✅ 86399 seconds (23h 59m 59s) → "23:59:59"
- ✅ 86400 seconds (24 hours) → "24:00:00"
- ✅ 359999 seconds (99h 59m 59s) → "99:59:59" (maximum supported)

**Counter-Examples** (Red Cards):
- ❌ "5:30" (missing hour component)
- ❌ "1:2:3" (missing zero-padding)
- ❌ "00:05" (missing seconds component)
- ❌ "100:00:00" (exceeds maximum 99:59:59)

**BDD Scenarios**:
- Scenario 3: Timer shows correct format (Scenario Outline with 8 examples)
- Scenario 17: Timer display format edge cases (Scenario Outline with 9 examples)

---

### Rule 3: Timer Increments
**Statement**: Timer increments by 1 second every second while in Running state.

**Examples** (Green Cards):
- ✅ Timer at 00:00:00 → Wait 5 seconds → Timer at 00:00:05
- ✅ Timer at 00:05:30 → Wait 10 seconds → Timer at 00:05:40
- ✅ Timer at 00:59:59 → Wait 1 second → Timer at 01:00:00

**Counter-Examples** (Red Cards):
- ❌ Timer increments by 2 seconds after 2 seconds (drift)
- ❌ Timer increments while paused
- ❌ Timer stops incrementing during slide navigation

**BDD Scenarios**:
- Scenario 2: Timer increments while running
- Scenario 14: Timer continues running during slide navigation

---

### Rule 4: Pause Excludes Duration
**Statement**: When timer is paused, the paused duration does NOT count toward elapsed time.

**Examples** (Green Cards):
- ✅ Run 5 min → Pause 2 min → Resume → Elapsed shows 00:05:00 (not 00:07:00)
- ✅ Run 5 min → Pause 2 min → Resume → Run 10 min → Elapsed shows 00:15:00 (5+10, not 5+2+10)
- ✅ Run 5 min → Pause 1 min → Resume → Run 5 min → Pause 2 min → Resume → Run 5 min → Elapsed shows 00:15:00

**Counter-Examples** (Red Cards):
- ❌ Run 5 min → Pause 2 min → Elapsed shows 00:07:00 (includes pause)
- ❌ Paused duration partially counted

**BDD Scenarios**:
- Scenario 5: Resume timer when break mode deactivated
- Scenario 6: Paused time does not count toward elapsed time
- Scenario 7: Handle multiple pause/resume cycles

---

### Rule 5: Pause Precondition
**Statement**: Timer can only be paused when currently in Running state.

**Examples** (Green Cards):
- ✅ Timer Running → Pause command → Timer Paused (success)
- ✅ Timer Running for 5 min → Pause → State changes to Paused, time frozen at 00:05:00

**Counter-Examples** (Red Cards):
- ❌ Timer NotStarted → Pause command → Error: "Cannot pause when not running"
- ❌ Timer Paused → Pause command again → Error: "Cannot pause when not running"

**BDD Scenarios**:
- Scenario 4: Pause timer when break mode activated
- Scenario 11: Reject pause command when timer not running

---

### Rule 6: Resume Precondition
**Statement**: Timer can only be resumed when currently in Paused state.

**Examples** (Green Cards):
- ✅ Timer Paused → Resume command → Timer Running (success)
- ✅ Timer Paused at 00:10:00 → Resume → Timer continues from 00:10:00

**Counter-Examples** (Red Cards):
- ❌ Timer Running → Resume command → Error: "Cannot resume when not paused"
- ❌ Timer NotStarted → Resume command → Error: "Cannot resume when not paused"

**BDD Scenarios**:
- Scenario 5: Resume timer when break mode deactivated
- Scenario 12: Reject resume command when timer not paused

---

### Rule 7: Cross-Window Synchronization
**Statement**: Timer state (elapsed time, running/paused) synchronizes in real-time between main presentation and speaker view.

**Examples** (Green Cards):
- ✅ Main timer at 00:05:00 → Open speaker view → Speaker view shows 00:05:00
- ✅ Main timer paused at 00:08:30 → Speaker view also shows Paused at 00:08:30
- ✅ Main timer resumed from 00:12:00 → Speaker view also resumes from 00:12:00
- ✅ Both timers increment in sync every second

**Counter-Examples** (Red Cards):
- ❌ Speaker view shows different elapsed time than main presentation
- ❌ Main timer paused but speaker view continues running
- ❌ Sync delayed by more than 1 second

**BDD Scenarios**:
- Scenario 8: Timer synchronizes with speaker view
- Scenario 9: Pause state synchronizes with speaker view
- Scenario 10: Resume state synchronizes with speaker view

---

### Rule 8: No Reset Capability
**Statement**: Timer cannot be reset to 00:00:00 during a presentation session.

**Examples** (Green Cards):
- ✅ Timer at 00:15:30 → Reset command not available
- ✅ Timer at 00:45:00 → No keyboard shortcut or UI button for reset

**Counter-Examples** (Red Cards):
- ❌ Timer at 00:15:30 → Reset to 00:00:00 (not allowed)

**Rationale**: Timer represents actual session duration for history logging (v3.1.0)

**BDD Scenarios**:
- Scenario 13: Timer has no reset capability

---

### Rule 9: Navigation Persistence
**Statement**: Timer state (running/paused and elapsed time) persists unchanged during slide navigation.

**Examples** (Green Cards):
- ✅ Timer Running at 00:03:00 on slide 5 → Navigate to slide 10 → Timer still Running, continues from 00:03:00+
- ✅ Timer Paused at 00:07:45 on slide 3 → Navigate to slide 8 → Timer still Paused at 00:07:45

**Counter-Examples** (Red Cards):
- ❌ Timer resets when navigating slides
- ❌ Timer pauses during navigation
- ❌ Timer state lost during navigation

**BDD Scenarios**:
- Scenario 14: Timer continues running during slide navigation
- Scenario 15: Paused timer remains paused during slide navigation

---

### Rule 10: Display Location
**Statement**: Timer displays in the footer at bottom-left corner of presentation.

**Examples** (Green Cards):
- ✅ Timer visible at bottom-left of every slide
- ✅ Timer does not overlap slide content
- ✅ Timer readable on all themes (light, dark, high-contrast)

**Counter-Examples** (Red Cards):
- ❌ Timer displays at top of slide
- ❌ Timer displays at center
- ❌ Timer hidden on some slides

**BDD Scenarios**:
- Scenario 1: Timer starts when presentation loads (verifies display location)

---

## Questions (Pink Cards)

### Question 1: Timer Display During Break Mode
**Question**: Should the timer display change appearance when paused (e.g., different color, "PAUSED" label)?

**Current Assumption**: Timer shows elapsed time in same format whether running or paused. Footer element may have different CSS class for styling.

**Impact**: UI/UX design, accessibility (screen reader announcements)

**Needs**: Product Owner decision

---

### Question 2: Timer Accuracy Requirements
**Question**: What is acceptable timer drift over long presentations (e.g., 2-hour presentation)?

**Current Assumption**: Using `setInterval(1000)` may drift due to JavaScript event loop. Acceptable drift is ±1 second per hour.

**Impact**: Implementation choice (setInterval vs. periodic recalculation from epoch timestamps)

**Needs**: Product Owner acceptance of drift tolerance

---

### Question 3: Speaker Notes Integration
**Question**: Should speaker notes show time spent on current slide (lap time) in addition to total elapsed time?

**Current Scope**: v3.0.0 only shows total elapsed time

**Future Enhancement**: v3.1.0 "Lap Times" feature mentioned in aggregate doc

**Needs**: Confirmation this is out of scope for v3.0.0

---

### Question 4: Screen Reader Behavior
**Question**: Should screen readers announce timer updates every second, or should timer have `aria-live="off"`?

**Current Assumption**: `aria-live="off"` to prevent announcing every second (would be extremely disruptive)

**Impact**: Accessibility compliance, user experience for screen reader users

**Needs**: Accessibility review

**Documented in Aggregate**: Yes - aggregate doc specifies `aria-live="off"`

---

### Question 5: Maximum Duration Handling
**Question**: What should happen if presentation exceeds 99:59:59 (current maximum)?

**Current Assumption**: Timer continues incrementing but display may overflow (e.g., "100:00:00" with 3-digit hour)

**Impact**: Edge case handling, display width constraints

**Likelihood**: Extremely rare (100-hour presentation)

**Needs**: Product Owner decision on priority

---

### Question 6: LocalStorage Persistence
**Question**: Should timer state persist across page refreshes?

**Current Decision**: No persistence in v3.0.0 (from Hotspot 2 in event storming doc)

**Future Enhancement**: v3.1.0 may add LocalStorage persistence if requested

**Needs**: Confirmation this is deferred to v3.1.0

---

## Mapping Summary

| Rule | Examples Count | BDD Scenarios | Questions |
|------|----------------|---------------|-----------|
| Timer Lifecycle | 4 | 2 | Q3 (lap times) |
| Display Format | 9 | 2 | Q5 (max duration) |
| Timer Increments | 3 | 2 | Q2 (accuracy) |
| Pause Excludes Duration | 3 | 3 | - |
| Pause Precondition | 2 | 2 | - |
| Resume Precondition | 2 | 2 | - |
| Cross-Window Sync | 4 | 3 | - |
| No Reset Capability | 2 | 1 | - |
| Navigation Persistence | 2 | 2 | - |
| Display Location | 3 | 1 | Q1 (visual state), Q4 (screen reader) |

**Total**: 10 rules, 34 examples, 17 BDD scenarios, 6 questions

---

## Decisions Made

### ✅ Decision 1: Use BroadcastChannel for Sync
**Rule**: Cross-Window Synchronization
**Decision**: Use BroadcastChannel API (same as slide sync)
**Rationale**: Already in use, minimal overhead, same-origin compatible
**Source**: Event Storming Hotspot 1

---

### ✅ Decision 2: No Persistence (v3.0.0)
**Rule**: Timer Lifecycle
**Decision**: No LocalStorage persistence in v3.0.0
**Rationale**: Refreshing during presentation is rare, simpler implementation
**Future**: Consider v3.1.0 if user feedback requests it
**Source**: Event Storming Hotspot 2

---

### ✅ Decision 3: No Reset Capability
**Rule**: No Reset Capability
**Decision**: Timer cannot be reset during session
**Rationale**: Represents true session duration for history logging
**Source**: Event Storming Hotspot 3, Design Specifications

---

### ✅ Decision 4: Independent Timers Per Window
**Rule**: Cross-Window Synchronization
**Decision**: Each browser window has independent timer (BroadcastChannel scoped to window)
**Rationale**: Unusual use case, not worth added complexity
**Source**: Event Storming Hotspot 4

---

### ✅ Decision 5: Screen Reader Silence
**Rule**: Display Location
**Decision**: Timer has `aria-live="off"` to prevent announcing every second
**Rationale**: Announcing every second would be extremely disruptive
**Source**: Aggregate doc accessibility considerations

---

## Next Steps

1. **Phase 2.3: Acceptance Criteria Review**
   - Architect validates examples against domain model invariants
   - Product Owner confirms decisions and answers pink card questions
   - Document final acceptance criteria

2. **Phase 3.1: Test-First Pairing**
   - Convert green card examples to property-based tests
   - Write failing unit tests for each business rule
   - Begin Red-Green-Refactor cycle

3. **Questions Requiring Product Owner Input**:
   - Q1: Timer display appearance when paused
   - Q2: Acceptable timer drift tolerance
   - Q3: Confirm lap times deferred to v3.1.0
   - Q4: Confirm `aria-live="off"` decision
   - Q5: Maximum duration edge case handling
   - Q6: Confirm LocalStorage persistence deferred to v3.1.0

---

**Session Notes**:
- All 10 business rules have concrete examples
- All rules map to BDD scenarios (100% coverage)
- 6 questions identified for Product Owner review
- 5 design decisions documented from prior event storming
- Ready for Acceptance Criteria Review (Phase 2.3)

**Facilitator Sign-off**: Ready for Product Owner and Architect validation

---

**Last Updated**: 2025-12-29
**Next Review**: After Phase 2.3 (Acceptance Criteria Review)
