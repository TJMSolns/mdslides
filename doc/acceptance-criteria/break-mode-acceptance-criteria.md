# Acceptance Criteria: Break Mode

**Feature**: Break Mode (v3.0.0 - Feature 2 of 10)
**Date**: 2025-12-29
**Status**: Pending Approval
**Stakeholders**: Product Owner, Bench Developer, Architect

---

## User Story

**As a** presentation author
**I want to** activate break mode during my presentation
**So that** I can take a break without showing slide content to the audience, and the timer pauses to exclude break time from elapsed presentation time

---

## Acceptance Criteria

### AC-1: B Key Toggle Activation/Deactivation
**Given** I am presenting slides
**When** I press the 'B' key
**Then** break mode is activated
**And** the timer pauses
**And** the main view displays the break screen
**And** the speaker view shows the current slide with "BREAK MODE ACTIVE" indicator

**Given** break mode is active
**When** I press the 'B' key again
**Then** break mode is deactivated
**And** the timer resumes
**And** the main view displays the current slide
**And** the speaker view removes the break indicator

**Edge Cases**:
- Pressing B twice rapidly: First activates, second deactivates
- B key is case-insensitive ('b' and 'B' both work)
- Break mode state syncs to speaker view via BroadcastChannel

**BDD Scenarios**: break-mode.feature (2 scenarios)

---

### AC-2: Timer Pause/Resume Integration
**Given** the timer is running at 00:15:30
**When** I activate break mode
**Then** the timer pauses at 00:15:30

**Given** break mode is active for 2 minutes
**When** I deactivate break mode
**Then** the timer resumes from 00:15:30 (not 00:17:30)
**And** break time is excluded from elapsed presentation time

**Invariant**: `totalElapsed = totalRuntime - totalPausedDuration`

**Edge Cases**:
- Multiple break sessions: Total paused duration is cumulative
- Break during break: Toggle deactivates (no nested breaks)

**BDD Scenarios**: break-mode.feature (3 scenarios)

---

### AC-3: Break Screen Configuration (4-Layer Precedence)
**Given** I have configured a custom break screen
**When** break mode is activated
**Then** the break screen is resolved via CLI arg > project config > global config > default (black screen)

**CLI Argument** (highest precedence):
```bash
java -jar ../mdslides.jar display my-talk --break-screen images/break.png
```

**Project Config**:
```json
{
  "breakScreen": "custom.jpg"
}
```

**Global Config**:
```json
{
  "defaults": {
    "breakScreen": "~/break.png"
  }
}
```

**Default**: Black screen (background: #000000)

**Validation**:
- File validated on activation (not render time)
- Missing file → Fallback to black screen with warning logged
- Invalid image format → Fallback to black screen

**Edge Cases**:
- CLI specifies, project specifies → CLI wins
- File deleted between render and activation → Fallback on activation
- Relative path resolution from presentation directory

**BDD Scenarios**: break-mode.feature (5 scenarios)

---

### AC-4: Main View vs. Speaker View Behavior
**Given** break mode is activated on slide 20
**When** I view the main presentation
**Then** the main view displays the break screen (image or black)

**When** I view the speaker view
**Then** the speaker view displays slide 20 content + notes + "BREAK MODE ACTIVE" indicator

**Given** break mode is active
**When** I navigate to slide 25
**Then** the main view continues showing the break screen
**And** the speaker view updates to show slide 25 content

**Design Decision**: Speaker view remains functional during break for presenter preparation.

**Edge Cases**:
- Break indicator is prominent (e.g., orange banner at top)
- Speaker notes remain accessible during break
- Timer display shows paused state

**BDD Scenarios**: break-mode.feature (2 scenarios)

---

### AC-5: Multiple Break Sessions
**Given** I am presenting slides
**When** I activate break mode for the first time
**Then** breakCount = 1

**When** I activate break mode for the second time
**Then** breakCount = 2

**Given** I close the presentation and start a new session
**When** I activate break mode
**Then** breakCount = 1 (reset for new session)

**Tracking**:
- Break count is session-scoped (not persistent across sessions)
- Each activation/deactivation cycle increments count
- Useful for post-presentation analytics

**Edge Cases**:
- Break count tracks total break sessions, not total break duration
- Closing browser during break: Session log shows incomplete break (no deactivation event)

**BDD Scenarios**: break-mode.feature (2 scenarios)

---

### AC-6: Goto Popup Disabled During Break
**Given** break mode is active
**When** I press the 'G' key
**Then** the goto popup does NOT open
**And** an error message is displayed: "Goto is disabled during break mode"

**Given** the goto popup is open
**When** I press the 'B' key
**Then** the goto popup closes
**And** break mode activates

**Design Decision**: Simplifies state management, prevents confusing interactions.

**Edge Cases**:
- Error message is visible but non-intrusive (e.g., toast notification)
- G key during break is no-op (does not queue for after break)

**BDD Scenarios**: break-mode.feature (2 scenarios)

---

### AC-7: Navigation During Break Mode
**Given** break mode is active on slide 10
**When** I press the right arrow key
**Then** the main view continues showing the break screen
**And** the speaker view navigates to slide 11

**Given** break mode is active on slide 15
**When** I press the 'P' key (previous)
**Then** the main view continues showing the break screen
**And** the speaker view navigates to the previous slide in history

**Design Decision**: Allows presenter to review upcoming slides during break.

**Edge Cases**:
- All navigation keys work during break (arrow, P, N, goto is disabled)
- Navigation history is updated normally during break
- Main view always shows break screen regardless of navigation

**BDD Scenarios**: break-mode.feature (2 scenarios)

---

### AC-8: Session Logging Integration
**Given** session logging is enabled
**When** I activate break mode at timestamp "2025-12-29T14:26:15Z"
**Then** the session log records:
```json
{
  "timestamp": "2025-12-29T14:26:15Z",
  "key": "B",
  "action": "break_mode_enabled"
}
```

**When** I deactivate break mode at timestamp "2025-12-29T14:28:22Z"
**Then** the session log records:
```json
{
  "timestamp": "2025-12-29T14:28:22Z",
  "key": "B",
  "action": "break_mode_disabled"
}
```

**Edge Cases**:
- Direct HTML open (no display command) → No logging occurs (expected)
- Multiple breaks → All activation/deactivation pairs logged

**BDD Scenarios**: break-mode.feature (1 scenario)

---

### AC-9: Cross-Window Synchronization
**Given** the speaker view is open
**When** I activate break mode in the main presentation
**Then** the speaker view immediately shows the break indicator

**When** I deactivate break mode in the main presentation
**Then** the speaker view immediately removes the break indicator

**Implementation**: Uses BroadcastChannel API (same as timer sync).

**Edge Cases**:
- Speaker view timer pauses in sync with main view timer
- Multiple browser windows: All sync via BroadcastChannel
- No speaker view open: Break mode works independently

**BDD Scenarios**: break-mode.feature (2 scenarios)

---

### AC-10: Break Screen Validation at Activation
**Given** the config specifies `breakScreen: "images/break.png"`
**And** the file exists
**When** break mode is activated
**Then** the break screen displays the image from "images/break.png"

**Given** the config specifies `breakScreen: "missing.png"`
**And** the file does NOT exist
**When** break mode is activated
**Then** the break screen displays the default black screen
**And** a warning is logged: "Break screen file not found: missing.png, using default"

**Design Decision**: Validate on activation (fail-late) to ensure resilience.

**Edge Cases**:
- File deleted after render but before break → Fallback on activation
- Invalid image format (e.g., text file) → Fallback to black screen
- Network drive or slow file system → Timeout and fallback

**BDD Scenarios**: break-mode.feature (1 scenario)

---

## Non-Functional Requirements

### Performance
- Break mode activation: < 100ms
- Timer pause: immediate (no lag)
- Speaker view sync: < 50ms latency

### Accessibility
- Break screen has aria-label="Break mode active"
- Screen reader announces "Break mode activated" / "Break mode deactivated"
- Keyboard-only operation (no mouse required)

### Error Handling
- Break screen file not found → Fallback to black screen (non-fatal)
- Invalid image format → Fallback to black screen (non-fatal)
- Speaker view not open → Break mode works in main view only

---

## Integration Requirements

### Depends On
- **Presentation Timer** (v3.0.0 Feature 1): Direct pause/resume integration

### Integrates With
- **Goto Function** (v3.0.0 Feature 5): Goto disabled during break
- **Navigation History** (v3.0.0 Feature 4): Navigation works during break
- **Session Logging** (v3.0.0 Feature 3): Break events logged

---

## Testing Requirements

### Unit Tests (Domain Layer)
- BreakMode aggregate: activate, deactivate, toggle
- BreakScreen resolution: 4-layer precedence
- Error handling: file not found, invalid format

### Integration Tests (Infrastructure Layer)
- Timer pause/resume integration
- BroadcastChannel sync
- Session logging

### BDD Tests (Acceptance Layer)
- 23 scenarios in break-mode.feature
- All 10 business rules from Example Mapping

---

## Definition of Done

- [ ] All 10 acceptance criteria implemented and tested
- [ ] 23 BDD scenarios passing
- [ ] Domain model implemented (BreakMode aggregate)
- [ ] Infrastructure implemented (timer integration, BroadcastChannel sync)
- [ ] UI implemented (break screen overlay, speaker view indicator)
- [ ] Documentation updated (user guide, API docs)
- [ ] Code reviewed and approved
- [ ] No regressions in existing features

---

**Approval**:
- Product Owner: ________________ Date: ________
- Architect: ________________ Date: ________
- QA Lead: ________________ Date: ________
