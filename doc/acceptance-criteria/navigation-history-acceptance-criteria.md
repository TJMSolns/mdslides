# Acceptance Criteria: Previous/Next Navigation

**Feature**: Previous/Next Navigation with History Stack (v3.0.0 - Feature 4 of 10)
**Date**: 2025-12-29
**Status**: Pending Approval
**Stakeholders**: Product Owner, Bench Developer, Architect

---

## User Story

**As a** presenter
**I want to** navigate backward through my viewed slides using the P key and forward using the N key
**So that** I can return to previously discussed content and provide a non-linear presentation flow

---

## Acceptance Criteria

### AC-1: P Key Navigation (History-Based Previous)
**Given** I have navigated through slides: 0 → 5 → 12 → 8
**And** I am currently on slide 8
**When** I press the 'P' key
**Then** I navigate to slide 12 (most recently viewed)
**And** slide 8 is pushed to the forward stack

**When** I press 'P' again
**Then** I navigate to slide 5
**And** slide 12 is pushed to the forward stack

**Invariant**: P key pops from visit stack (LIFO), NOT linear previous.

**Edge Cases**:
- P key is case-insensitive ('p' and 'P' both work)
- Multiple rapid P presses: Each pops from stack in order

**BDD Scenarios**: navigation-history.feature (4 scenarios)

---

### AC-2: Empty History Fallback
**Given** I am on slide 10
**And** I navigated directly via URL (no history)
**When** I press the 'P' key
**Then** I navigate to slide 0 (first slide by default)
**And** slide 10 is pushed to the forward stack

**Given** I am on slide 0
**And** the navigation history is empty
**When** I press the 'P' key
**Then** I remain on slide 0
**And** no error occurs

**Design Decision**: "Previous" without history means "beginning of presentation".

**BDD Scenarios**: navigation-history.feature (2 scenarios)

---

### AC-3: N Key Navigation (Hybrid Redo/Next)
**Given** I have navigated: 0 → 5 → 12 → 8
**And** I pressed 'P' to go back to 12
**And** the forward stack contains [8]
**When** I press the 'N' key
**Then** I navigate to slide 8 (popped from forward stack)
**And** the forward stack is empty

**Given** I am on slide 5
**And** the forward stack is empty
**When** I press the 'N' key
**Then** I navigate to slide 6 (linear next)
**And** the visit stack is updated with [5]

**Invariant**: `N key = if forwardStack.nonEmpty then redo else linearNext`

**BDD Scenarios**: navigation-history.feature (4 scenarios)

---

### AC-4: Last Slide Boundary
**Given** I am on slide 19 (last of 20 slides)
**And** the forward stack is empty
**When** I press the 'N' key
**Then** I remain on slide 19
**And** no error occurs

**Invariant**: `currentSlideIndex always in range [0, totalSlides - 1]`

**BDD Scenarios**: navigation-history.feature (1 scenario)

---

### AC-5: Visit Stack Management (Chronological Order)
**Given** I navigate to slides in order: 0 → 1 → 5 → 3 → 8
**Then** the visit stack contains [0, 1, 5, 3]
**And** the order is preserved (FIFO for append, LIFO for P key pop)

**Edge Cases**:
- Each navigation (except P/N) pushes current slide to visit stack
- Visit stack grows unbounded (no size limit)

**BDD Scenarios**: navigation-history.feature (2 scenarios)

---

### AC-6: Forward Stack Management (Redo Capability)
**Given** I am on slide 12
**When** I press the 'P' key
**Then** slide 12 is pushed to the forward stack

**Given** the forward stack contains [12, 8]
**When** I press the 'N' key
**Then** slide 12 is popped from the forward stack
**And** I navigate to slide 12

**Invariant**: Forward stack populated only by P key, consumed only by N key.

**BDD Scenarios**: navigation-history.feature (2 scenarios)

---

### AC-7: Forward Stack Clearing (New Decision Path)
**Given** I have forward history [12]
**When** I press the right arrow key (linear next)
**Then** the forward stack is cleared

**Given** I have forward history [12]
**When** I press 'G' and navigate to slide 20 (goto)
**Then** the forward stack is cleared

**Design Decision**: Non-P/N navigation represents new decision path, invalidating redo.

**BDD Scenarios**: navigation-history.feature (2 scenarios)

---

### AC-8: Duplicate Slide Tracking (Temporal Sequence)
**Given** I navigate: 0 → 5 → 10 → 5 → 15
**Then** the visit stack contains [0, 5, 10, 5] (duplicates preserved)

**When** I press 'P' four times from slide 15
**Then** I navigate to: 15 → 5 (recent) → 10 → 5 (earlier) → 0

**Rationale**: History reflects temporal sequence, not unique slides.

**BDD Scenarios**: navigation-history.feature (2 scenarios)

---

### AC-9: Unbounded History Stack
**Given** I navigate through 100 slides in sequence
**When** I press 'P' 99 times
**Then** I successfully navigate back to slide 0
**And** all 100 slides are preserved in the history stack

**Design Decision**: No limit on visit stack size (unbounded).

**BDD Scenarios**: navigation-history.feature (1 scenario)

---

### AC-10: Timer Does NOT Pause on P/N
**Given** the timer is running at 00:05:00
**When** I press the 'P' key
**Then** the timer continues running (state remains Running)
**And** the timer is NOT paused

**Contrast**: G key (goto popup) and B key (break mode) DO pause timer.

**Rationale**: P/N are instant navigation (no dialog), not interruptions.

**BDD Scenarios**: navigation-history.feature (2 scenarios)

---

### AC-11: Cross-Window Synchronization
**Given** the speaker view is open
**When** I press 'P' in the main presentation
**Then** the speaker view immediately navigates to the same slide

**Implementation**: Uses BroadcastChannel API (same as timer and break mode sync).

**BDD Scenarios**: navigation-history.feature (2 scenarios)

---

### AC-12: Session Logging Integration
**Given** session logging is enabled
**When** I press 'P' at timestamp "2025-12-29T14:30:15Z"
**Then** the slide visit is logged with navigationMethod: "Previous"

**When** I press 'N' (redo) at timestamp "2025-12-29T14:31:22Z"
**Then** the slide visit is logged with navigationMethod: "Next"

**Important**: P/N key presses NOT logged in events array (too noisy). Only slide visits logged.

**BDD Scenarios**: navigation-history.feature (3 scenarios)

---

### AC-13: P Key Excluded from Events Array
**Given** session logging is enabled
**When** I press the 'P' key
**Then** the session log records the slide visit
**But** the session log does NOT record a "P" key press event
**And** the events log does not contain an entry for key "P"

**Rationale**: P/N too frequent (noisy), only meaningful events logged (B, S, G).

**BDD Scenarios**: navigation-history.feature (2 scenarios)

---

### AC-14: Break Mode Interaction
**Given** break mode is active
**And** I am on slide 10 with history [0, 5]
**When** I press the 'P' key
**Then** I navigate to slide 5
**And** the speaker view shows slide 5 content
**And** the main view still shows the break screen
**And** the timer remains paused

**Design Decision**: Navigation works during break (same as other navigation keys).

**BDD Scenarios**: navigation-history.feature (2 scenarios)

---

## Non-Functional Requirements

### Performance
- P/N navigation: < 50ms response time
- Visit stack operations: O(1) for push/pop
- No memory leaks with unbounded stack

### Accessibility
- P/N keys announced by screen reader: "Navigated to previous slide" / "Navigated to next slide"
- Keyboard-only operation (no mouse required)

---

## Integration Requirements

### Depends On
- **Slide Renderer**: Displays slide at new index

### Integrates With
- **Presentation Timer**: P/N do NOT pause timer
- **Session Logging**: Visits logged with navigation method
- **Goto Function**: Goto clears forward stack
- **Break Mode**: P/N work during break mode

---

## Testing Requirements

### Unit Tests (Domain Layer)
- NavigationHistory aggregate: navigatePrevious, navigateNext, pushToHistory
- Visit stack: LIFO operations
- Forward stack: clearing on non-P/N navigation

### Integration Tests
- BroadcastChannel sync to speaker view
- Session logging (navigation method recorded)

### BDD Tests
- 27 scenarios in navigation-history.feature
- All 14 business rules from Example Mapping

---

## Definition of Done

- [ ] All 14 acceptance criteria implemented and tested
- [ ] 27 BDD scenarios passing
- [ ] Domain model implemented (NavigationHistory aggregate)
- [ ] Infrastructure implemented (BroadcastChannel sync, session logging)
- [ ] UI implemented (transparent keyboard shortcuts, no UI changes)
- [ ] Documentation updated
- [ ] Code reviewed and approved
- [ ] No regressions in existing features

---

**Approval**:
- Product Owner: ________________ Date: ________
- Architect: ________________ Date: ________
- QA Lead: ________________ Date: ________
