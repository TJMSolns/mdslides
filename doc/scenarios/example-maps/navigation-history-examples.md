# Example Mapping: Previous/Next Navigation

**Date**: 2025-12-29
**Feature**: Previous/Next Navigation with History Stack (v3.0.0 - Feature 4 of 10)
**Participants**: Product Owner, Bench Developer, Architect
**Story**: As a presenter, I want to navigate backward through my viewed slides (not linearly) using the P key so I can return to previously discussed content, and use the N key for forward/next navigation with redo capability.

---

## Business Rules (Yellow Cards)

### Rule 1: P Key Navigation (History-Based Previous)
**Statement**: P key pops from visit stack (LIFO) and navigates to most recently viewed slide. Current slide pushed to forward stack.

**Examples** (Green Cards ✅):
- **Ex 1.1**: Navigated 0 → 5 → 12 → 8, press P → Navigate to slide 12, forward stack contains [8]
- **Ex 1.2**: On slide 12, press P again → Navigate to slide 5, forward stack contains [8, 12]
- **Ex 1.3**: On slide 5, press P again → Navigate to slide 0, forward stack contains [8, 12, 5]
- **Ex 1.4**: On slide 0, press P again → Stay on slide 0, forward stack contains [8, 12, 5]

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 1.5**: Press P navigates to slide 7 (linear previous) → Wrong, P uses visit stack not linear order
- ❌ **Ex 1.6**: Press P clears visit stack → Wrong, P pops from stack, doesn't clear it

**BDD Traceability**:
- `navigation-history.feature`: "Navigate previous with populated history"
- `navigation-history.feature`: "Navigate previous multiple times"

**Questions Resolved**:
- Q: Should P key use history stack or linear order? → History stack (LIFO)
- Q: What happens to current slide when pressing P? → Pushed to forward stack

---

### Rule 2: N Key Navigation (Hybrid Redo/Next)
**Statement**: N key pops from forward stack (redo) if it exists, else advances linearly to next slide. Visit stack updated accordingly.

**Examples** (Green Cards ✅):
- **Ex 2.1**: Forward stack [12, 8], press N → Navigate to slide 12 (popped), visit stack updated
- **Ex 2.2**: Forward stack empty, on slide 5, press N → Navigate to slide 6 (linear next)
- **Ex 2.3**: Forward stack [12, 8], press N twice → Navigate to 12, then to 8, then forward stack empty
- **Ex 2.4**: On slide 19 (last), forward stack empty, press N → Stay on slide 19 (no slide 20)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 2.5**: N always advances linearly → Wrong, N redos if forward history exists
- ❌ **Ex 2.6**: N at last slide with empty forward stack advances to slide 20 → Wrong, no slide 20 exists

**BDD Traceability**:
- `navigation-history.feature`: "Navigate next with forward history (redo)"
- `navigation-history.feature`: "Navigate next without forward history (linear advance)"
- `navigation-history.feature`: "Navigate next at last slide with no forward history"

**Invariant**: `N key behavior = if forwardStack.nonEmpty then redo else linearNext`

---

### Rule 3: Visit Stack Management (Chronological Order)
**Statement**: Visit stack preserves chronological order of all slide visits. Each navigation (except P/N) pushes current slide to visit stack.

**Examples** (Green Cards ✅):
- **Ex 3.1**: Navigate 0 → 1 → 5 → 3 → 8 → Visit stack contains [0, 1, 5, 3]
- **Ex 3.2**: Navigate 0 → 5 → 10 → 5 → 15 → Visit stack contains [0, 5, 10, 5] (duplicates preserved)
- **Ex 3.3**: Press right arrow from slide 5 to 6 → Visit stack appends [5]

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 3.4**: Visit stack removes duplicates → Wrong, temporal sequence preserved
- ❌ **Ex 3.5**: Visit stack sorted by slide index → Wrong, chronological order only

**BDD Traceability**:
- `navigation-history.feature`: "Visit stack preserves chronological order"
- `navigation-history.feature`: "Visiting the same slide multiple times preserves temporal sequence"

**Design Decision**: LIFO stack (last in, first out) for P key navigation.

---

### Rule 4: Forward Stack Management (Redo Capability)
**Statement**: Forward stack enables redo after using P key. Populated when P key pressed, consumed when N key pressed.

**Examples** (Green Cards ✅):
- **Ex 4.1**: On slide 12, press P → Current slide (12) pushed to forward stack
- **Ex 4.2**: Forward stack [12, 8], press N → Pop 12, navigate to 12, forward stack becomes [8]
- **Ex 4.3**: Press P twice, then N twice → Redo both previous navigations

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 4.4**: Forward stack populated on right arrow press → Wrong, only P key populates it
- ❌ **Ex 4.5**: N key pops from visit stack → Wrong, N pops from forward stack (if exists)

**BDD Traceability**:
- `navigation-history.feature`: "Navigate next with forward history (redo)"

**Invariant**: `forwardStack populated only by P key, consumed only by N key`

---

### Rule 5: Forward Stack Clearing (New Decision Path)
**Statement**: Forward stack cleared on non-P/N navigation (goto, linear next, left/right arrow). P/N preserve forward stack.

**Examples** (Green Cards ✅):
- **Ex 5.1**: Forward stack [12], press right arrow → Forward stack cleared
- **Ex 5.2**: Forward stack [12], press G and goto slide 20 → Forward stack cleared
- **Ex 5.3**: Forward stack [12], press P → Forward stack preserved, becomes [12, newSlide]
- **Ex 5.4**: Forward stack [12], press N → Forward stack becomes [] after consuming

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 5.5**: Forward stack preserved on goto → Wrong, goto clears it (new decision path)
- ❌ **Ex 5.6**: P key clears forward stack → Wrong, P key appends to it

**BDD Traceability**:
- `navigation-history.feature`: "Forward stack clears on non-P/N navigation"
- `navigation-history.feature`: "Goto navigation clears forward stack"

**Design Decision**: Non-P/N navigation represents new decision path, invalidating redo.

---

### Rule 6: Duplicate Slide Tracking (Temporal Sequence)
**Statement**: Same slide can appear multiple times in visit stack. Each visit is a distinct entry in temporal order.

**Examples** (Green Cards ✅):
- **Ex 6.1**: Navigate 0 → 5 → 10 → 5 → Visit stack contains [0, 5, 10] (two 5s)
- **Ex 6.2**: On slide 15, press P four times → Navigate to 5, then 10, then 5 (earlier visit), then 0
- **Ex 6.3**: Visit slide 8 three times → Visit stack contains [8, 8, 8] in sequence

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 6.4**: Visit stack deduplicates slide 5 → Wrong, preserves all visits
- ❌ **Ex 6.5**: P key skips duplicate visits → Wrong, follows temporal order

**BDD Traceability**:
- `navigation-history.feature`: "Visiting the same slide multiple times preserves temporal sequence"
- `navigation-history.feature`: "Previous navigation to duplicate slide"

**Rationale**: History reflects actual navigation flow, not unique slides.

---

### Rule 7: Unbounded History Stack
**Statement**: Visit stack grows without limit during session. No maximum size constraint.

**Examples** (Green Cards ✅):
- **Ex 7.1**: Navigate through 100 slides → Visit stack contains all 100 entries
- **Ex 7.2**: Navigate 100 slides, press P 99 times → Successfully navigate back to slide 0
- **Ex 7.3**: Long presentation with 500+ navigations → All visits tracked

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 7.4**: Visit stack limited to 50 entries → Wrong, no limit
- ❌ **Ex 7.5**: Oldest entries dropped after 100 navigations → Wrong, all preserved

**BDD Traceability**:
- `navigation-history.feature`: "History stack grows without limit during session"

**Design Decision**: Memory is cheap, long presentations benefit from full history.

---

### Rule 8: Timer Integration (No Pause on P/N)
**Statement**: P and N keys do NOT pause PresentationTimer. Timer continues running during P/N navigation.

**Examples** (Green Cards ✅):
- **Ex 8.1**: Timer at 00:05:00, press P → Timer continues to 00:05:01, 00:05:02 (not paused)
- **Ex 8.2**: Timer at 00:10:30, press N → Timer continues running
- **Ex 8.3**: Press P ten times rapidly → Timer never pauses

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 8.4**: P key pauses timer → Wrong, only B and G keys pause timer
- ❌ **Ex 8.5**: Timer pauses during P/N navigation → Wrong, timer always runs

**BDD Traceability**:
- `navigation-history.feature`: "Previous navigation does not pause timer"
- `navigation-history.feature`: "Next navigation does not pause timer"

**Contrast with Goto**: G key pauses timer (user enters popup), P/N are instant (no pause).

---

### Rule 9: Cross-Window Synchronization
**Statement**: P and N navigation syncs to speaker view via BroadcastChannel. Both windows display same slide.

**Examples** (Green Cards ✅):
- **Ex 9.1**: Main view presses P → Speaker view immediately updates to same slide
- **Ex 9.2**: Main view presses N → Speaker view immediately updates to same slide
- **Ex 9.3**: Speaker view timer stays in sync with main view timer

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 9.4**: Speaker view out of sync after P navigation → Wrong, must sync via BroadcastChannel
- ❌ **Ex 9.5**: Speaker view shows different slide → Wrong, both windows must match

**BDD Traceability**:
- `navigation-history.feature`: "Previous navigation syncs to speaker view"
- `navigation-history.feature`: "Next navigation syncs to speaker view"

**Design Decision**: Uses BroadcastChannel API (same as timer and break mode sync).

---

### Rule 10: Session Logging Integration
**Statement**: P and N navigation logged with NavigationMethod.Previous or NavigationMethod.Next. Slide visit recorded, but key press NOT logged as event (too noisy).

**Examples** (Green Cards ✅):
- **Ex 10.1**: Press P at 14:30:15 → Slide visit logged with method "Previous", timestamp "2025-12-29T14:30:15Z"
- **Ex 10.2**: Press N (redo) at 14:31:22 → Slide visit logged with method "Next"
- **Ex 10.3**: Press N (linear) at 14:32:10 → Slide visit logged with method "Next" (same as redo)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 10.4**: P key press logged in events array → Wrong, P/N excluded from events (too noisy)
- ❌ **Ex 10.5**: Session log contains {"key": "P", "action": "..."} → Wrong, only slide visits logged

**BDD Traceability**:
- `navigation-history.feature`: "Previous navigation is logged with NavigationMethod.Previous"
- `navigation-history.feature`: "Next navigation (redo) is logged with NavigationMethod.Next"
- `navigation-history.feature`: "P key press is not logged as an event (too noisy)"

**Design Decision**: Slide visits logged, but key presses excluded (P/N too frequent for events array).

---

### Rule 11: Empty History Fallback
**Statement**: P key on empty visit stack navigates to slide 0 (first slide). No error occurs.

**Examples** (Green Cards ✅):
- **Ex 11.1**: Start on slide 0, visit stack empty, press P → Stay on slide 0
- **Ex 11.2**: Direct URL to slide 10 (no history), press P → Navigate to slide 0
- **Ex 11.3**: On slide 0, press P multiple times → Always stay on slide 0

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 11.4**: P on empty history shows error → Wrong, graceful fallback to slide 0
- ❌ **Ex 11.5**: P on empty history is no-op (stays on current slide) → Wrong, navigates to slide 0

**BDD Traceability**:
- `navigation-history.feature`: "Navigate previous with empty history (default to slide 0)"
- `navigation-history.feature`: "Navigate previous from middle slide with empty history"

**Design Decision**: "Previous" without history means "beginning of presentation".

---

### Rule 12: Last Slide Boundary
**Statement**: N key at last slide with empty forward stack stays on last slide. No error occurs.

**Examples** (Green Cards ✅):
- **Ex 12.1**: On slide 19 (last of 20), forward stack empty, press N → Stay on slide 19
- **Ex 12.2**: Press N multiple times at last slide → Always stay on last slide
- **Ex 12.3**: Forward stack empty throughout → N advances linearly until last slide, then stops

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 12.4**: N at last slide wraps to slide 0 → Wrong, stays on last slide
- ❌ **Ex 12.5**: N at last slide shows error → Wrong, graceful no-op

**BDD Traceability**:
- `navigation-history.feature`: "Navigate next at last slide with no forward history"

**Invariant**: `currentSlideIndex always in range [0, totalSlides - 1]`

---

## Questions (Pink Cards)

### Q1: Should we limit history stack size for performance?
**Status**: RESOLVED - No limit
**Decision**: Unbounded history stack (store entire session)
**Rationale**: Modern browsers handle thousands of entries easily. Long presentations benefit from full history.

### Q2: Should N key be pure redo or hybrid redo/next?
**Status**: RESOLVED - Hybrid
**Decision**: N pops from forward stack if exists, else advances linearly
**Rationale**: Provides intuitive undo/redo with P/N while maintaining "next slide" convenience.

### Q3: Should P/N pause the timer?
**Status**: RESOLVED - No
**Decision**: P and N do NOT pause timer (unlike G and B keys)
**Rationale**: P/N are instant navigation (no dialog), not interruptions.

### Q4: Should duplicate slides in history be deduplicated?
**Status**: RESOLVED - No
**Decision**: Preserve every visit in temporal sequence
**Rationale**: History reflects actual navigation flow, not unique slides visited.

### Q5: Should left/right arrow keys use history stack?
**Status**: RESOLVED - No
**Decision**: Left arrow is linear previous (slide n-1), right arrow is linear next (slide n+1)
**Rationale**: P/N for history-based, arrows for linear. Provides both navigation modes.

### Q6: Should P/N key presses be logged in events array?
**Status**: RESOLVED - No
**Decision**: Slide visits logged, but P/N key presses excluded from events array
**Rationale**: P/N too frequent (noisy), only meaningful events logged (B, S, G).

---

## Design Decisions from Event Storming

1. **History Stack Implementation**: LIFO stack (List[Int])
   - **Decision**: Use immutable List with prepend operation
   - **Rationale**: Efficient for LIFO access, functional style

2. **Forward Stack Clearing**: On non-P/N navigation
   - **Decision**: Goto, linear next, arrow keys clear forward stack
   - **Rationale**: Represents new decision path, invalidates redo

3. **N Key Behavior**: Hybrid redo/next
   - **Decision**: Check forward stack first, fallback to linear
   - **Rationale**: Matches user mental model of P/N as bidirectional

4. **Empty History Fallback**: Go to slide 0
   - **Decision**: P on empty history navigates to slide 0
   - **Rationale**: Consistent "go back to start" behavior

5. **Duplicate Tracking**: Preserve all visits
   - **Decision**: Visit stack: [0, 5, 10, 5] if user visited 5 twice
   - **Rationale**: Temporal sequence more useful than unique slides

---

## Traceability Matrix

| Rule | BDD Scenarios | Event Storming Events | Acceptance Criteria |
|------|---------------|----------------------|---------------------|
| Rule 1 | navigation-history.feature (4) | NavigatedToPrevious, HistoryStackPopped | AC-1, AC-2 |
| Rule 2 | navigation-history.feature (4) | NavigatedToNext | AC-3, AC-4 |
| Rule 3 | navigation-history.feature (2) | SlideAddedToHistory | AC-5 |
| Rule 4 | navigation-history.feature (2) | HistoryStackPopped | AC-6 |
| Rule 5 | navigation-history.feature (2) | HistoryStackCleared | AC-7 |
| Rule 6 | navigation-history.feature (2) | SlideAddedToHistory | AC-8 |
| Rule 7 | navigation-history.feature (1) | SlideAddedToHistory | AC-9 |
| Rule 8 | navigation-history.feature (2) | NavigatedToPrevious, NavigatedToNext | AC-10 |
| Rule 9 | navigation-history.feature (2) | NavigatedToPrevious, NavigatedToNext | AC-11 |
| Rule 10 | navigation-history.feature (3) | SlideAddedToHistory | AC-12 |
| Rule 11 | navigation-history.feature (2) | NavigatedToPrevious | AC-13 |
| Rule 12 | navigation-history.feature (1) | NavigatedToNext | AC-14 |

**Total Coverage**: 27 BDD scenarios across 12 business rules

---

## Implementation Notes

### Domain Model Requirements
- `NavigationHistory` aggregate with state (backwardStack, forwardStack, currentSlideIndex, totalSlides)
- `NavigatePrevious` command pops from backward stack, pushes to forward stack
- `NavigateNext` command checks forward stack first, fallback to linear
- `PushToHistory` command appends to backward stack, clears forward stack
- `NavigationError` enum for validation errors

### Infrastructure Requirements
- Keyboard handler for P and N keys
- BroadcastChannel sync for speaker view
- Integration with slide renderer (display target slide)
- Integration with session logging (record navigation method)

### UI Requirements
- No UI changes (P/N are transparent keyboard shortcuts)
- Speaker view updates immediately on P/N navigation
- Main view displays target slide

---

## Complex Navigation Flows

### Flow 1: Zig-Zag Pattern
```
Navigate: 0 → 10 → 5 → 15 → 2
Press P four times: 2 → 15 → 5 → 10 → 0
Press N four times: 0 → 10 → 5 → 15 → 2
Forward stack now empty
Visit stack ends with: [5, 15, 2]
```

### Flow 2: Goto Interrupts P/N Sequence
```
Navigate: 0 → 5 → 10
Press P (back to 5), forward stack: [10]
Press G, goto slide 20, forward stack: [] (cleared)
Press N: Navigate to slide 21 (linear, not redo to slide 10)
```

### Flow 3: Rapid P/N Toggling
```
On slide 10, visit stack: [0, 5]
Press P: Navigate to 5, forward stack: [10]
Press N: Navigate to 10, forward stack: []
Press P: Navigate to 5, forward stack: [10]
Press N: Navigate to 10, forward stack: []
```

---

**Example Mapping Complete**: 2025-12-29
**Next Step**: Acceptance Criteria Review
**Ready for Implementation**: Pending AC approval
