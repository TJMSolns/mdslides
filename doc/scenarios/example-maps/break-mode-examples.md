# Example Mapping: Break Mode

**Date**: 2025-12-29
**Feature**: Break Mode (v3.0.0 - Feature 2 of 10)
**Participants**: Product Owner, Bench Developer, Architect
**Story**: As a presentation author, I want to activate break mode during my presentation so I can take a break without showing slide content to the audience, and the timer pauses to exclude break time from elapsed presentation time.

---

## Business Rules (Yellow Cards)

### Rule 1: Break Mode Activation/Deactivation
**Statement**: B key toggles break mode on/off. When activated, timer pauses and break screen displays.

**Examples** (Green Cards ✅):
- **Ex 1.1**: Press B on slide 15 → Break mode activates, timer pauses, break screen shows
- **Ex 1.2**: Press B during break mode → Break mode deactivates, timer resumes, slide 15 shows
- **Ex 1.3**: Press B twice rapidly → First activates, second deactivates

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 1.4**: Press P during break mode → Should NOT activate break mode (P is for previous)
- ❌ **Ex 1.5**: Press B when already active → Should NOT cause error (toggle to deactivate)

**BDD Traceability**:
- `break-mode.feature`: "Activate break mode with B key"
- `break-mode.feature`: "Deactivate break mode with B key"

**Questions Resolved**:
- Q: Should B key be case-sensitive? → No, both 'b' and 'B' work

---

### Rule 2: Timer Pause/Resume Integration
**Statement**: Break mode directly commands timer to pause on activation and resume on deactivation. Break time excluded from elapsed time.

**Examples** (Green Cards ✅):
- **Ex 2.1**: Timer at 00:15:30, activate break → Timer pauses at 00:15:30
- **Ex 2.2**: Break for 2 minutes, deactivate → Timer resumes from 00:15:30 (not 00:17:30)
- **Ex 2.3**: Total presentation: 20 min running + 5 min break = elapsed shows 00:20:00

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 2.4**: Break time included in elapsed → Wrong, breaks excluded
- ❌ **Ex 2.5**: Timer continues during break → Wrong, timer must pause

**BDD Traceability**:
- `break-mode.feature`: "Timer pauses when break mode activates"
- `break-mode.feature`: "Timer resumes when break mode deactivates"

**Invariant**: `totalElapsed = totalRuntime - totalPausedDuration`

---

### Rule 3: Break Screen Configuration (4-Layer Precedence)
**Statement**: Break screen resolved via CLI arg > project config > global config > default (black screen). File validated on activation.

**Examples** (Green Cards ✅):
- **Ex 3.1**: CLI `--break-screen images/break.png` + exists → Use images/break.png
- **Ex 3.2**: Project config `"breakScreen": "custom.jpg"` + exists → Use custom.jpg
- **Ex 3.3**: Global config `"~/break.png"` + exists → Use ~/break.png
- **Ex 3.4**: No config → Use default black screen (background: #000000)
- **Ex 3.5**: CLI specifies, project specifies → CLI wins (highest precedence)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 3.6**: File specified but doesn't exist → Fallback to black screen with warning
- ❌ **Ex 3.7**: Invalid image format (text file) → Fallback to black screen

**BDD Traceability**:
- `break-mode.feature`: "Custom break screen via CLI argument"
- `break-mode.feature`: "Default break screen (no custom configuration)"

**Questions Resolved**:
- Q: What if break screen path is invalid? → Fallback to default, log warning

---

### Rule 4: Main View vs. Speaker View Behavior
**Statement**: Main view shows break screen when active. Speaker view shows current slide + notes + break indicator.

**Examples** (Green Cards ✅):
- **Ex 4.1**: Activate on slide 20 → Main: break screen, Speaker: slide 20 content + "BREAK MODE ACTIVE"
- **Ex 4.2**: Navigate to slide 25 during break → Main: still break screen, Speaker: slide 25 content
- **Ex 4.3**: Deactivate on slide 25 → Main: slide 25, Speaker: slide 25 (no indicator)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 4.4**: Speaker view shows break screen → Wrong, speaker needs to see current slide
- ❌ **Ex 4.5**: Main view shows slide during break → Wrong, audience shouldn't see content

**BDD Traceability**:
- `break-mode.feature`: "Speaker view shows current slide during break"
- `break-mode.feature`: "Navigate slides while in break mode"

**Design Decision**: Speaker view remains functional during break for presenter preparation.

---

### Rule 5: Multiple Break Sessions
**Statement**: Break count increments with each activation/deactivation cycle. Sessions tracked cumulatively per presentation.

**Examples** (Green Cards ✅):
- **Ex 5.1**: First break → breakCount = 1
- **Ex 5.2**: Second break → breakCount = 2
- **Ex 5.3**: Third break → breakCount = 3
- **Ex 5.4**: New presentation session → breakCount resets to 0

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 5.5**: Break count persists across sessions → Wrong, session-scoped only

**BDD Traceability**:
- `break-mode.feature`: "Break count increments with each break session"
- `break-mode.feature`: "Break count is session-scoped"

**Questions Resolved**:
- Q: Should we track break count? → Yes, useful for analytics
- Q: Cumulative across sessions? → No, per-session only

---

### Rule 6: Goto Popup Disabled During Break
**Statement**: Goto popup cannot open during break mode. G key shows error message.

**Examples** (Green Cards ✅):
- **Ex 6.1**: Break active, press G → Popup does NOT open, message: "Goto is disabled during break mode"
- **Ex 6.2**: Goto open, press B → Goto closes, break activates
- **Ex 6.3**: Deactivate break, press G → Goto opens normally

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 6.4**: Goto opens during break → Wrong, should be disabled
- ❌ **Ex 6.5**: No error message when G pressed during break → Wrong, user needs feedback

**BDD Traceability**:
- `break-mode.feature`: "Goto popup is disabled during break mode"
- `goto-function.feature`: "Goto popup cannot open during break mode"

**Design Decision**: Simplifies state management, prevents confusing interactions.

---

### Rule 7: Navigation During Break Mode
**Statement**: Slide navigation works during break mode. Main view stays on break screen, speaker view updates.

**Examples** (Green Cards ✅):
- **Ex 7.1**: Break on slide 10, press right arrow → Main: break screen, Speaker: slide 11
- **Ex 7.2**: Break on slide 15, press P (previous) → Main: break screen, Speaker: previous slide
- **Ex 7.3**: Break on slide 20, navigate to slide 30 → Main: break screen, Speaker: slide 30

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 7.4**: Navigation disabled during break → Wrong, presenter needs to prepare next slides

**BDD Traceability**:
- `break-mode.feature`: "Navigate slides while in break mode"
- `navigation-history.feature`: "Previous navigation works during break mode"

**Design Decision**: Allows presenter to review upcoming slides during break.

---

### Rule 8: Session Logging Integration
**Statement**: Break activation and deactivation events logged with timestamps when session logging enabled.

**Examples** (Green Cards ✅):
- **Ex 8.1**: Activate at 14:26:15 → Log: `{"timestamp": "2025-12-29T14:26:15Z", "key": "B", "action": "break_mode_enabled"}`
- **Ex 8.2**: Deactivate at 14:28:22 → Log: `{"timestamp": "2025-12-29T14:28:22Z", "key": "B", "action": "break_mode_disabled"}`
- **Ex 8.3**: Multiple breaks → All activation/deactivation pairs logged

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 8.4**: Direct HTML open (no display command) → No logging occurs (expected)

**BDD Traceability**:
- `break-mode.feature`: "Break mode events are logged to session history"
- `history-logging.feature`: "Break mode activation is logged"

**Questions Resolved**:
- Q: Log every second during break? → No, only activation/deactivation events

---

### Rule 9: Cross-Window Synchronization
**Statement**: Break mode state syncs between main presentation and speaker view via BroadcastChannel.

**Examples** (Green Cards ✅):
- **Ex 9.1**: Activate in main → Speaker view immediately shows break indicator
- **Ex 9.2**: Deactivate in main → Speaker view immediately removes indicator
- **Ex 9.3**: Speaker view timer pauses in sync with main view timer

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 9.4**: Speaker view out of sync → Wrong, must sync via BroadcastChannel

**BDD Traceability**:
- `break-mode.feature`: "Break mode syncs to speaker view"

**Design Decision**: Uses BroadcastChannel API (same as timer sync).

---

### Rule 10: Break Screen Validation at Activation
**Statement**: Break screen file existence checked when break mode activates. Missing file triggers fallback to default.

**Examples** (Green Cards ✅):
- **Ex 10.1**: Config specifies `images/break.png`, file exists → Use file
- **Ex 10.2**: Config specifies `missing.png`, file missing → Fallback to black screen, log warning
- **Ex 10.3**: File deleted after render but before break → Fallback on activation

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 10.4**: Fail to activate if file missing → Wrong, fallback ensures activation always works
- ❌ **Ex 10.5**: Validate at render time only → Wrong, file might be deleted before activation

**BDD Traceability**:
- `break-mode.feature`: "Break screen file not found (fallback to default)"

**Design Decision**: Validate on activation (fail-late) to ensure resilience.

---

## Questions (Pink Cards)

### Q1: Should break screen support HTML slides instead of images?
**Status**: DEFERRED to v3.1.0
**Decision**: Images only for v3.0.0. HTML slides add complexity (CSS, scripts, security).
**Workaround**: Use image with text overlay created externally.

### Q2: Should we display break duration on break screen?
**Status**: RESOLVED - No
**Decision**: Break screen is minimal (black or image). Duration visible in speaker view timer.
**Rationale**: Audience doesn't need to see break duration, keeps break screen simple.

### Q3: Should presenter be able to activate break from speaker view?
**Status**: RESOLVED - No for v3.0.0
**Decision**: Break activation only from main presentation view (B key).
**Rationale**: Main view is presenter's control surface. Simplifies implementation.
**Future**: Add speaker view controls in v3.1.0 if requested.

### Q4: How to handle break mode if speaker view not open?
**Status**: RESOLVED - Works independently
**Decision**: Break mode works with or without speaker view open.
**Behavior**: Main view shows break screen, timer pauses. Speaker view (if open) syncs.

### Q5: Should break count be displayed anywhere?
**Status**: RESOLVED - No UI display
**Decision**: Break count tracked in domain model and logged, but not displayed in UI.
**Rationale**: Primarily for post-presentation analytics (report command).

### Q6: What if user closes browser during break?
**Status**: RESOLVED - Session ends normally
**Decision**: Session log records break activation but no deactivation (incomplete).
**Report command**: Shows break as still active at session end.

---

## Design Decisions from Event Storming

1. **Break Screen Resolution**: Fail early (render time) vs. Fail late (activation time)
   - **Decision**: Fail late (on activation) with fallback
   - **Rationale**: File might be deleted between render and activation, fallback ensures resilience

2. **Timer Integration**: Event-driven vs. Direct command
   - **Decision**: Direct command (break mode directly calls timer.pause())
   - **Rationale**: Tighter coupling acceptable for simple pause/resume, reduces event complexity

3. **Speaker View During Break**: Show break screen vs. Show current slide
   - **Decision**: Show current slide + break indicator
   - **Rationale**: Presenter needs to see content during break for preparation

4. **Multiple Break Sessions**: Track count vs. Don't track
   - **Decision**: Track cumulatively per session
   - **Rationale**: Useful for analytics, minimal overhead

5. **Break Screen Fallback**: Error vs. Black screen
   - **Decision**: Black screen fallback
   - **Rationale**: Presentation must continue even if custom break screen missing

---

## Traceability Matrix

| Rule | BDD Scenarios | Event Storming Events | Acceptance Criteria |
|------|---------------|----------------------|---------------------|
| Rule 1 | break-mode.feature (2) | BreakModeActivated, BreakModeDeactivated | AC-1, AC-2 |
| Rule 2 | break-mode.feature (3) | TimerPaused, TimerResumed | AC-3 |
| Rule 3 | break-mode.feature (5) | BreakModeActivated | AC-4 |
| Rule 4 | break-mode.feature (2) | BreakModeActivated | AC-5 |
| Rule 5 | break-mode.feature (2) | BreakModeActivated, BreakModeDeactivated | AC-6 |
| Rule 6 | break-mode.feature (2) | GotoPopupBlocked | AC-7 |
| Rule 7 | break-mode.feature (2) | NavigatedToSlide | AC-8 |
| Rule 8 | break-mode.feature (1) | BreakEventLogged | AC-9 |
| Rule 9 | break-mode.feature (2) | BreakModeStateSynced | AC-10 |
| Rule 10 | break-mode.feature (1) | BreakScreenValidated | AC-11 |

**Total Coverage**: 23 BDD scenarios across 10 business rules

---

## Implementation Notes

### Domain Model Requirements
- `BreakMode` aggregate with state (active/inactive), breakCount, breakStartTimestamp
- `ActivateBreakMode` command validates screen path, pauses timer
- `DeactivateBreakMode` command resumes timer, increments count
- `BreakModeError` enum for validation errors

### Infrastructure Requirements
- Break screen renderer (image display, fallback to black screen)
- BroadcastChannel sync for speaker view
- Keyboard handler for B key
- Integration with PresentationTimer (pause/resume)

### UI Requirements
- Break screen overlay (CSS: position: fixed, z-index: 200)
- Speaker view break indicator (e.g., orange banner "BREAK MODE ACTIVE")
- Main view: full-screen break screen

---

**Example Mapping Complete**: 2025-12-29
**Next Step**: Acceptance Criteria Review
**Ready for Implementation**: Pending AC approval
