# Example Mapping: Goto Function

**Date**: 2025-12-29
**Feature**: Goto Function with Popup Validation (v3.0.0 - Feature 5 of 10)
**Participants**: Product Owner, Bench Developer, Architect
**Story**: As a presenter, I want to press G to open a goto popup and jump to a specific slide number so I can quickly navigate to any slide during Q&A or non-linear presentations.

---

## Business Rules (Yellow Cards)

### Rule 1: Goto Popup Activation
**Statement**: G key opens goto popup with input field. Popup displays when not already open and not during break mode.

**Examples** (Green Cards ✅):
- **Ex 1.1**: Press G on slide 10 → Popup opens with empty input field, focus on input
- **Ex 1.2**: Press G on slide 0 → Popup opens normally
- **Ex 1.3**: Press 'g' (lowercase) → Popup opens (case-insensitive)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 1.4**: Press G during break mode → Popup does NOT open, error message shown
- ❌ **Ex 1.5**: Popup already open, press G → No second popup (duplicate prevented)

**BDD Traceability**:
- `goto-function.feature`: "Activate goto popup with G key"
- `goto-function.feature`: "G key is case-insensitive"

**Questions Resolved**:
- Q: Should G be case-sensitive? → No, both 'g' and 'G' work

---

### Rule 2: Input Validation (Real-Time)
**Statement**: Input validated in real-time as user types. Valid range: 1 to totalSlides (1-indexed in UI). Errors displayed below input.

**Examples** (Green Cards ✅):
- **Ex 2.1**: Type "25", total slides = 42 → Valid, no error message
- **Ex 2.2**: Type "43", total slides = 42 → Error: "Slide number must be between 1 and 42"
- **Ex 2.3**: Type "0" → Error: "Slide number must be between 1 and 42"
- **Ex 2.4**: Type "abc" → Error: "Please enter a valid number"
- **Ex 2.5**: Type "-5" → Error: "Slide number must be between 1 and 42"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 2.6**: Type "43", no error shown → Wrong, out-of-range must show error
- ❌ **Ex 2.7**: Error shown after submit (not real-time) → Wrong, validate as user types

**BDD Traceability**:
- `goto-function.feature`: "Valid slide number (in range)"
- `goto-function.feature`: "Invalid slide number (out of range)"
- `goto-function.feature`: "Invalid input (non-numeric)"

**Invariant**: `Valid input ⟺ input is integer AND 1 ≤ input ≤ totalSlides`

---

### Rule 3: Navigation on Enter
**Statement**: Pressing Enter with valid input navigates to target slide (1-indexed UI to 0-indexed internal). Popup closes, timer resumes.

**Examples** (Green Cards ✅):
- **Ex 3.1**: Type "25", press Enter → Navigate to slide 24 (0-indexed), popup closes
- **Ex 3.2**: Type "1", press Enter → Navigate to slide 0
- **Ex 3.3**: Type "42", press Enter → Navigate to slide 41 (last slide)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 3.4**: Type "25", press Enter → Navigate to slide 25 (wrong, should be 24)
- ❌ **Ex 3.5**: Press Enter with invalid input → Navigation occurs (wrong, should prevent)

**BDD Traceability**:
- `goto-function.feature`: "Navigate to target slide with Enter key"
- `goto-function.feature`: "1-indexed slide numbers (UI) to 0-indexed (internal)"

**Mapping**: `UI index = internal index + 1` or `internal = UI - 1`

---

### Rule 4: Popup Dismissal (Escape Key)
**Statement**: Pressing Escape closes popup without navigating. Returns to previous slide, timer resumes.

**Examples** (Green Cards ✅):
- **Ex 4.1**: Type "25", press Escape → Popup closes, stay on current slide (no navigation)
- **Ex 4.2**: Press G, press Escape immediately → Popup closes, no side effects
- **Ex 4.3**: Popup open for 30 seconds, press Escape → Popup closes

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 4.4**: Press Escape navigates to typed slide → Wrong, Escape cancels navigation
- ❌ **Ex 4.5**: Escape does not close popup → Wrong, Escape always dismisses

**BDD Traceability**:
- `goto-function.feature`: "Dismiss popup with Escape key"

**Design Decision**: Escape is cancel (no navigation), Enter is confirm (navigate).

---

### Rule 5: Timer Pauses During Goto Popup
**Statement**: PresentationTimer pauses when popup opens, resumes when popup closes (Enter or Escape).

**Examples** (Green Cards ✅):
- **Ex 5.1**: Timer at 00:05:30, press G → Timer pauses at 00:05:30
- **Ex 5.2**: Popup open for 10 seconds, press Enter → Timer resumes from 00:05:30 (not 00:05:40)
- **Ex 5.3**: Popup open for 5 seconds, press Escape → Timer resumes from 00:05:30

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 5.4**: Timer continues during popup → Wrong, must pause
- ❌ **Ex 5.5**: Popup time included in elapsed → Wrong, excluded (timer paused)

**BDD Traceability**:
- `goto-function.feature`: "Timer pauses when popup opens"
- `goto-function.feature`: "Timer resumes when popup closes"

**Integration**: Direct timer.pause() on popup open, timer.resume() on close.

---

### Rule 6: Enter Key Blocked for Invalid Input
**Statement**: Pressing Enter with invalid input shows error message, does NOT navigate or close popup.

**Examples** (Green Cards ✅):
- **Ex 6.1**: Type "abc", press Enter → Error shown, popup stays open
- **Ex 6.2**: Type "0", press Enter → Error shown, popup stays open
- **Ex 6.3**: Type "100" (out of range), press Enter → Error shown, popup stays open

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 6.4**: Type "abc", press Enter → Popup closes (wrong, should stay open)
- ❌ **Ex 6.5**: Invalid input, press Enter → Navigates to slide 0 (wrong, no navigation)

**BDD Traceability**:
- `goto-function.feature`: "Enter key blocked for invalid input"

**Design Decision**: Enter only effective when input is valid.

---

### Rule 7: Forward History Cleared on Goto
**Statement**: Goto navigation clears forward stack (same as other non-P/N navigation). Visit stack updated with previous slide.

**Examples** (Green Cards ✅):
- **Ex 7.1**: On slide 10, forward stack [15, 20], goto slide 25 → Forward stack cleared
- **Ex 7.2**: On slide 5, visit stack [0, 1, 2], goto slide 30 → Visit stack becomes [0, 1, 2, 5]
- **Ex 7.3**: After goto, press N → Linear next (not redo, forward stack was cleared)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 7.4**: Goto preserves forward stack → Wrong, goto clears it (new decision path)
- ❌ **Ex 7.5**: Goto does not update visit stack → Wrong, previous slide added to stack

**BDD Traceability**:
- `goto-function.feature`: "Goto clears forward navigation history"

**Integration**: `NavigationHistory.pushToHistory(targetIndex)` clears forward stack.

---

### Rule 8: Session Logging Integration
**Statement**: Goto navigation logged with NavigationMethod.Goto and target slide. G key press logged as event with timestamp.

**Examples** (Green Cards ✅):
- **Ex 8.1**: Press G at 14:30:15 → Event logged: `{"timestamp": "2025-12-29T14:30:15Z", "key": "G", "action": "goto_popup_opened"}`
- **Ex 8.2**: Type "25", press Enter → Slide visit logged with method "Goto", targetSlide = 25
- **Ex 8.3**: Press Escape at 14:30:45 → Event logged: `{"timestamp": "2025-12-29T14:30:45Z", "key": "Escape", "action": "goto_popup_dismissed"}`

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 8.4**: Direct HTML open (no display command) → No logging (expected)
- ❌ **Ex 8.5**: Goto not logged → Wrong, all navigation methods logged

**BDD Traceability**:
- `goto-function.feature`: "Goto popup events logged to session history"
- `history-logging.feature`: "Goto navigation is logged"

**Event Types**: goto_popup_opened, goto_popup_dismissed, goto_navigation

---

### Rule 9: Cross-Window Synchronization
**Statement**: Goto navigation syncs to speaker view via BroadcastChannel. Popup state does NOT sync (main view only).

**Examples** (Green Cards ✅):
- **Ex 9.1**: Main view opens popup → Speaker view does NOT show popup
- **Ex 9.2**: Main view gotos slide 25 → Speaker view immediately navigates to slide 25
- **Ex 9.3**: Main view timer pauses → Speaker view timer pauses in sync

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 9.4**: Speaker view shows goto popup → Wrong, popup is main view only
- ❌ **Ex 9.5**: Speaker view out of sync after goto → Wrong, must sync via BroadcastChannel

**BDD Traceability**:
- `goto-function.feature`: "Goto navigation syncs to speaker view"

**Design Decision**: Popup UI is local, navigation result is synced.

---

### Rule 10: Goto Disabled During Break Mode
**Statement**: G key shows error message during break mode, popup does NOT open.

**Examples** (Green Cards ✅):
- **Ex 10.1**: Break active, press G → Message: "Goto is disabled during break mode"
- **Ex 10.2**: Popup open, press B → Popup closes, break activates
- **Ex 10.3**: Deactivate break, press G → Popup opens normally

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 10.4**: Goto opens during break → Wrong, should be disabled
- ❌ **Ex 10.5**: No error message when G pressed during break → Wrong, user needs feedback

**BDD Traceability**:
- `goto-function.feature`: "Goto popup is disabled during break mode"
- `break-mode.feature`: "Goto popup disabled during break"

**Design Decision**: Simplifies state management, prevents confusing interactions.

---

### Rule 11: Focus Management
**Statement**: Input field receives focus when popup opens. Focus returns to main presentation when popup closes.

**Examples** (Green Cards ✅):
- **Ex 11.1**: Press G → Input field has focus, user can type immediately
- **Ex 11.2**: Type "25", press Enter → Focus returns to main presentation
- **Ex 11.3**: Press Escape → Focus returns to main presentation

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 11.4**: Input field does not have focus → Wrong, user must be able to type
- ❌ **Ex 11.5**: Focus remains on popup after Enter → Wrong, focus should return

**BDD Traceability**:
- `goto-function.feature`: "Input field receives focus on popup open"

**Accessibility**: Proper focus management for keyboard navigation.

---

### Rule 12: ARIA Attributes for Accessibility
**Statement**: Popup has role="dialog", aria-modal="true", aria-labelledby, and aria-describedby for screen readers.

**Examples** (Green Cards ✅):
- **Ex 12.1**: Popup has `role="dialog"` → Screen reader announces "dialog"
- **Ex 12.2**: Popup has `aria-modal="true"` → Screen reader indicates modal state
- **Ex 12.3**: Input has `aria-label="Enter slide number"` → Screen reader reads label

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 12.4**: No ARIA attributes → Wrong, accessibility requirement
- ❌ **Ex 12.5**: aria-modal="false" → Wrong, popup is modal (blocks main view)

**BDD Traceability**:
- `goto-function.feature`: "Goto popup has proper ARIA attributes"

**WCAG Requirement**: Modal dialogs must have proper ARIA roles and attributes.

---

## Questions (Pink Cards)

### Q1: Should goto support slide titles instead of numbers?
**Status**: DEFERRED to v3.1.0
**Decision**: Numbers only for v3.0.0. Title-based goto adds autocomplete complexity.
**Workaround**: Use numbers for now, add title search in future release.

### Q2: Should goto popup show slide preview?
**Status**: DEFERRED to v3.1.0
**Decision**: Text input only for v3.0.0. Preview adds rendering complexity.
**Rationale**: Simple implementation, preview is nice-to-have.

### Q3: Should goto support relative navigation (e.g., "+5" or "-3")?
**Status**: DEFERRED to v3.1.0
**Decision**: Absolute slide numbers only (1-42).
**Rationale**: Simple validation, relative navigation less common use case.

### Q4: Should pressing G twice close the popup?
**Status**: RESOLVED - No
**Decision**: G key while popup open is no-op (Escape to close).
**Rationale**: Prevents accidental closure, Escape is standard cancel key.

### Q5: Should invalid input clear on re-opening popup?
**Status**: RESOLVED - Yes
**Decision**: Popup opens with empty input field each time.
**Rationale**: Clean slate for each goto invocation.

### Q6: Should goto pause session logging?
**Status**: RESOLVED - No
**Decision**: Session logging continues, goto events logged normally.
**Rationale**: Only timer pauses, logging continues to record all events.

---

## Design Decisions from Event Storming

1. **1-Indexed UI to 0-Indexed Internal**:
   - **Decision**: Users see 1-42, code uses 0-41
   - **Rationale**: User-friendly (slide 1 is first), developer-friendly (0-based arrays)

2. **Real-Time Validation**:
   - **Decision**: Validate on every keystroke (input event)
   - **Rationale**: Immediate feedback, prevents submission of invalid input

3. **Timer Pause Integration**:
   - **Decision**: Direct timer.pause() on popup open
   - **Rationale**: Goto is interruption (user enters dialog, selects slide)

4. **Enter Key Blocking**:
   - **Decision**: Enter only navigates if input is valid
   - **Rationale**: Prevents accidental navigation to invalid slides

5. **Goto Disabled During Break**:
   - **Decision**: Show error message, do not open popup
   - **Rationale**: Simplifies state management (no two-popup scenario)

---

## Traceability Matrix

| Rule | BDD Scenarios | Event Storming Events | Acceptance Criteria |
|------|---------------|----------------------|---------------------|
| Rule 1 | goto-function.feature (3) | GotoPopupOpened | AC-1 |
| Rule 2 | goto-function.feature (5) | GotoInputValidated | AC-2, AC-3 |
| Rule 3 | goto-function.feature (3) | NavigatedToTargetSlide | AC-4 |
| Rule 4 | goto-function.feature (1) | GotoPopupDismissed | AC-5 |
| Rule 5 | goto-function.feature (2) | TimerPaused, TimerResumed | AC-6 |
| Rule 6 | goto-function.feature (1) | GotoInputValidated | AC-7 |
| Rule 7 | goto-function.feature (1) | HistoryStackCleared | AC-8 |
| Rule 8 | goto-function.feature (3) | GotoEventLogged | AC-9 |
| Rule 9 | goto-function.feature (2) | GotoNavigationSynced | AC-10 |
| Rule 10 | goto-function.feature (2) | GotoPopupBlocked | AC-11 |
| Rule 11 | goto-function.feature (1) | GotoPopupOpened | AC-12 |
| Rule 12 | goto-function.feature (1) | GotoPopupOpened | AC-13 |

**Total Coverage**: 25 BDD scenarios across 12 business rules

---

## Implementation Notes

### Domain Model Requirements
- `GotoPopup` aggregate with state (isOpen, inputValue, validationError)
- `OpenGotoPopup` command checks break mode, pauses timer
- `ValidateGotoInput` command validates range and numeric format
- `NavigateToTargetSlide` command converts 1-indexed to 0-indexed, clears forward history
- `DismissGotoPopup` command resumes timer, returns focus
- `GotoError` enum for validation errors

### Infrastructure Requirements
- Keyboard handler for G and Escape keys
- Input validation (numeric, range check)
- Timer integration (pause on open, resume on close)
- BroadcastChannel sync for navigation result
- Focus management (input field → main presentation)

### UI Requirements
- Modal popup overlay (CSS: position: fixed, z-index: 100)
- Input field with autofocus
- Error message area below input
- ARIA attributes: role="dialog", aria-modal="true", aria-labelledby
- Styling: centered, semi-transparent backdrop

---

## Validation Logic

### Input Validation State Machine
```
Empty → Typing → Valid → Enter → Navigate
              ↓
           Invalid → Error Shown → Typing (continue editing)
                                ↓
                              Escape → Dismiss (no navigation)
```

### Validation Rules
```scala
def validateGotoInput(input: String, totalSlides: Int): Either[GotoError, Int] =
  input.toIntOption match
    case None => Left(GotoError.NonNumeric("Please enter a valid number"))
    case Some(n) if n < 1 || n > totalSlides =>
      Left(GotoError.OutOfRange(s"Slide number must be between 1 and $totalSlides"))
    case Some(n) => Right(n - 1) // Convert to 0-indexed
```

---

**Example Mapping Complete**: 2025-12-29
**Next Step**: Acceptance Criteria Review
**Ready for Implementation**: Pending AC approval
