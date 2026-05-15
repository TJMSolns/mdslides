# Example Mapping: History Logging

**Date**: 2025-12-29
**Feature**: History Logging (Session Log) (v3.0.0 - Feature 3 of 10)
**Participants**: Product Owner, Bench Developer, Architect
**Story**: As a presenter, I want my presentation session to be automatically logged to a JSON file so I can analyze slide timing, review navigation patterns, and improve future presentations.

---

## Business Rules (Yellow Cards)

### Rule 1: Log File Creation (Display Command Only)
**Statement**: Log file created ONLY when display command used. Direct HTML open does NOT create log. Location: `<output-dir>/<deck-name>.log`

**Examples** (Green Cards ✅):
- **Ex 1.1**: Run `mdslides display my-talk` → Log file created at `my-talk/my-talk.log`
- **Ex 1.2**: Run `mdslides display talks/conference` → Log file created at `talks/conference/conference.log`
- **Ex 1.3**: Open `my-talk/index.html` directly in browser → No log file created

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 1.4**: Direct HTML open creates log → Wrong, only display command creates log
- ❌ **Ex 1.5**: Log file created during render → Wrong, created during display

**BDD Traceability**:
- `history-logging.feature`: "Display command creates log file"
- `display-command.feature`: "Log file initialized on display"

**Design Decision**: Logging requires intent (display command), not accidental (direct HTML open).

---

### Rule 2: Log File Overwrites Previous Session
**Statement**: Each display command invocation creates new session, overwriting previous log. Old session data is lost.

**Examples** (Green Cards ✅):
- **Ex 2.1**: Log exists from yesterday, run display today → Old log truncated, new session starts
- **Ex 2.2**: Run display twice in one day → Second invocation overwrites first log
- **Ex 2.3**: Log contains 100 slide visits from old session → Truncated to new session metadata

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 2.4**: New session appends to existing log → Wrong, file is truncated
- ❌ **Ex 2.5**: Old session preserved in separate file → Wrong, single log file only

**BDD Traceability**:
- `history-logging.feature`: "Log file overwrites previous session"
- `display-command.feature`: "Log file overwrites previous session"

**Rationale**: Session-scoped logging, not multi-session archive.

---

### Rule 3: Session Metadata Initialization
**Statement**: Log file initialized with session metadata on display command. Includes sessionId (UUID), presentationName, startTime (ISO 8601), theme, totalSlides.

**Examples** (Green Cards ✅):
- **Ex 3.1**: Display at 14:23:45 → startTime: "2025-12-29T14:23:45Z"
- **Ex 3.2**: Session ID is UUID v4 → sessionId: "a3f2e9d7-4b5c-6a1d-8e9f-0a1b2c3d4e5f"
- **Ex 3.3**: Dark theme rendered → theme: "dark"
- **Ex 3.4**: 42 slides in deck → totalSlides: 42

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 3.5**: startTime is epoch milliseconds → Wrong, ISO 8601 format required
- ❌ **Ex 3.6**: sessionId is timestamp → Wrong, UUID v4 required

**BDD Traceability**:
- `history-logging.feature`: "Session metadata structure"
- `display-command.feature`: "Log file contains session metadata"

**JSON Structure**:
```json
{
  "session": {
    "sessionId": "a3f2e9d7-4b5c-6a1d-8e9f-0a1b2c3d4e5f",
    "presentationName": "my-talk",
    "startTime": "2025-12-29T14:23:45Z",
    "theme": "dark",
    "totalSlides": 42
  },
  "slideVisits": [],
  "events": []
}
```

---

### Rule 4: Slide Visit Recording
**Statement**: Each slide navigation logged with slideIndex, entryTime (ISO 8601), navigationMethod, and optional exitTime.

**Examples** (Green Cards ✅):
- **Ex 4.1**: Navigate to slide 5 → `{"slideIndex": 5, "entryTime": "2025-12-29T14:24:00Z", "navigationMethod": "Linear"}`
- **Ex 4.2**: Press P to slide 10 → navigationMethod: "Previous"
- **Ex 4.3**: Press G, goto slide 25 → navigationMethod: "Goto"
- **Ex 4.4**: Navigate to slide 10 → entryTime recorded, exitTime: null (still on slide)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 4.5**: Slide visit not logged → Wrong, all navigations logged
- ❌ **Ex 4.6**: navigationMethod not recorded → Wrong, required field

**BDD Traceability**:
- `history-logging.feature`: "Slide visit structure"
- `history-logging.feature`: "Navigation method recorded"

**Navigation Methods**: Linear, Previous, Next, Goto

---

### Rule 5: Auto-Exit Previous Slide
**Statement**: Navigating to new slide automatically records exitTime for previous slide. Duration = exitTime - entryTime.

**Examples** (Green Cards ✅):
- **Ex 5.1**: On slide 5 from 14:24:00, navigate to slide 10 at 14:25:30 → Slide 5 exitTime: "2025-12-29T14:25:30Z", duration: 90 seconds
- **Ex 5.2**: On slide 10 for 2 minutes, navigate away → exitTime updated, duration: 120 seconds
- **Ex 5.3**: Rapid navigation (1 second on slide) → Duration: 1 second

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 5.4**: Previous slide exitTime not updated → Wrong, must update on navigation
- ❌ **Ex 5.5**: Duration calculated on report read (not stored) → Wrong, stored in log

**BDD Traceability**:
- `history-logging.feature`: "Auto-exit previous slide on navigation"

**Calculation**: `duration = exitTime - entryTime` (in seconds)

---

### Rule 6: Event Logging (B, S, G Keys)
**Statement**: B, S, G key presses logged as events with timestamp, key, and action. P/N keys excluded (too noisy).

**Examples** (Green Cards ✅):
- **Ex 6.1**: Press B at 14:26:15 → `{"timestamp": "2025-12-29T14:26:15Z", "key": "B", "action": "break_mode_enabled"}`
- **Ex 6.2**: Press S at 14:27:00 → `{"timestamp": "2025-12-29T14:27:00Z", "key": "S", "action": "speaker_view_opened"}`
- **Ex 6.3**: Press G at 14:28:10 → `{"timestamp": "2025-12-29T14:28:10Z", "key": "G", "action": "goto_popup_opened"}`

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 6.4**: Press P, event logged → Wrong, P/N excluded from events array
- ❌ **Ex 6.5**: Press N, event logged → Wrong, P/N excluded from events array

**BDD Traceability**:
- `history-logging.feature`: "Key press events logged"
- `history-logging.feature`: "P/N keys excluded from events"

**Event Actions**: break_mode_enabled, break_mode_disabled, speaker_view_opened, goto_popup_opened, goto_popup_dismissed, goto_navigation

---

### Rule 7: Session End Time Recording
**Statement**: endTime recorded when browser closes or session ends. If browser crashed, endTime is null (incomplete session).

**Examples** (Green Cards ✅):
- **Ex 7.1**: Close browser at 15:09:17 → endTime: "2025-12-29T15:09:17Z"
- **Ex 7.2**: Session duration: startTime 14:23:45, endTime 15:09:17 → 45 minutes 32 seconds
- **Ex 7.3**: Browser crashes → endTime: null (incomplete session)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 7.4**: endTime always populated → Wrong, crash/force-quit leaves null
- ❌ **Ex 7.5**: endTime set on navigation → Wrong, set only on session end

**BDD Traceability**:
- `history-logging.feature`: "Session end time recorded"

**Incomplete Session**: Report command detects endTime: null, shows "IN PROGRESS" or "INCOMPLETE"

---

### Rule 8: JSON Format Validation
**Statement**: Log file is valid JSON. Top-level object with session, slideVisits, events arrays.

**Examples** (Green Cards ✅):
- **Ex 8.1**: Log can be parsed with `JSON.parse()` → Valid JSON
- **Ex 8.2**: Session is object, slideVisits is array, events is array → Correct structure
- **Ex 8.3**: ISO 8601 timestamps are strings → `"2025-12-29T14:23:45Z"`

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 8.4**: Log contains JavaScript comments → Invalid JSON
- ❌ **Ex 8.5**: Timestamps are epoch numbers → Wrong, ISO 8601 strings required

**BDD Traceability**:
- `history-logging.feature`: "Log file is valid JSON"
- `report-command.feature`: "Log file is valid JSON after initialization"

**Validation**: Report command must successfully parse log file.

---

### Rule 9: Log Creation Failure (Non-Fatal)
**Statement**: If log file cannot be created (permissions, disk full), display warning but continue opening presentation. Presentation works without logging.

**Examples** (Green Cards ✅):
- **Ex 9.1**: Output directory read-only → Warning displayed, browser opens, exit code 0
- **Ex 9.2**: Disk full → Warning displayed, presentation opens without logging
- **Ex 9.3**: Permission denied → Warning shown, presentation functional

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 9.4**: Log creation failure halts display command → Wrong, non-fatal
- ❌ **Ex 9.5**: Display command exits with code 1 → Wrong, exit code 0 (warning only)

**BDD Traceability**:
- `display-command.feature`: "Warning when log file cannot be created"
- `history-logging.feature`: "Log creation failure is non-fatal"

**Warning Message**:
```
⚠ Warning: Unable to create log file: my-talk/my-talk.log
  Reason: Permission denied

  Presentation will open without session logging.
```

---

### Rule 10: Concurrent Session Handling
**Statement**: Two different presentations can have active sessions concurrently. Separate log files, independent logging.

**Examples** (Green Cards ✅):
- **Ex 10.1**: Display talk-1 and talk-2 → `talk-1/talk-1.log` and `talk-2/talk-2.log` created
- **Ex 10.2**: Navigate in talk-1 → Only talk-1.log updated
- **Ex 10.3**: Navigate in talk-2 → Only talk-2.log updated

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 10.4**: Single shared log file → Wrong, per-presentation logs
- ❌ **Ex 10.5**: Cross-session interference → Wrong, independent sessions

**BDD Traceability**:
- `display-command.feature`: "Concurrent display sessions"

**Isolation**: Each presentation has separate log file and session state.

---

### Rule 11: Navigation Method Classification
**Statement**: Navigation method determined by key/action used. Linear (arrow/space), Previous (P), Next (N), Goto (G).

**Examples** (Green Cards ✅):
- **Ex 11.1**: Press right arrow → navigationMethod: "Linear"
- **Ex 11.2**: Press space → navigationMethod: "Linear"
- **Ex 11.3**: Press P → navigationMethod: "Previous"
- **Ex 11.4**: Press N (redo) → navigationMethod: "Next"
- **Ex 11.5**: Press N (linear, no forward history) → navigationMethod: "Next"
- **Ex 11.6**: Press G, goto slide 25 → navigationMethod: "Goto"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 11.7**: All methods logged as "Linear" → Wrong, must distinguish
- ❌ **Ex 11.8**: N (redo) vs N (linear) have different methods → Wrong, both "Next"

**BDD Traceability**:
- `history-logging.feature`: "Navigation method classification"

**Enum**: NavigationMethod { Linear, Previous, Next, Goto }

---

### Rule 12: Last Slide Exit Time (Session End)
**Statement**: Last slide in session has exitTime set to session endTime. If session incomplete, exitTime is null.

**Examples** (Green Cards ✅):
- **Ex 12.1**: Last slide visited at 15:09:00, session ends at 15:09:17 → exitTime: "2025-12-29T15:09:17Z"
- **Ex 12.2**: Browser crashes on last slide → exitTime: null (session incomplete)
- **Ex 12.3**: Duration of last slide = endTime - entryTime

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 12.4**: Last slide exitTime always null → Wrong, set to endTime on session close
- ❌ **Ex 12.5**: Last slide duration not calculated → Wrong, calculated if endTime exists

**BDD Traceability**:
- `history-logging.feature`: "Last slide exit time equals session end time"

**Special Case**: Graceful session close sets exitTime for last slide.

---

## Questions (Pink Cards)

### Q1: Should we support multi-session log files?
**Status**: DEFERRED to v4.0.0
**Decision**: Single session per log file (overwrite on each display)
**Rationale**: Simpler implementation, most common use case is single session review.
**Future**: Multi-session archive in separate feature.

### Q2: Should log file creation support custom paths?
**Status**: DEFERRED to v3.1.0
**Decision**: Fixed location: `<output-dir>/<deck-name>.log`
**Rationale**: Predictable location for report command, consistent with output structure.

### Q3: Should P/N keys be logged in events array?
**Status**: RESOLVED - No
**Decision**: Slide visits logged (with navigation method), but P/N key presses excluded from events
**Rationale**: P/N too frequent (noisy), slide visits provide sufficient data.

### Q4: Should we log slide content or just metadata?
**Status**: RESOLVED - Metadata only
**Decision**: Log slideIndex, not slide content (title, text)
**Rationale**: Slide content in source file, log focuses on timing/navigation.
**Limitation**: If slide content changes, old logs reference old indices.

### Q5: Should log file support compression?
**Status**: DEFERRED to v3.1.0
**Decision**: Plain JSON (no compression)
**Rationale**: Human-readable logs prioritized over size. Compression adds complexity.

### Q6: Should we log browser/OS information?
**Status**: DEFERRED to v3.1.0
**Decision**: Not in v3.0.0
**Rationale**: Session timing is primary use case, browser info is secondary.

---

## Design Decisions from Event Storming

1. **Display Command Only**:
   - **Decision**: Log creation only via `mdslides display`, not direct HTML open
   - **Rationale**: Requires user intent, prevents accidental logging

2. **JSON Format**:
   - **Decision**: Structured JSON with session, slideVisits, events
   - **Rationale**: Machine-readable, parseable by report command and external tools

3. **ISO 8601 Timestamps**:
   - **Decision**: All timestamps in ISO 8601 UTC format
   - **Rationale**: Standard format, timezone-aware, sortable

4. **Auto-Exit Previous Slide**:
   - **Decision**: Navigation to new slide updates exitTime of previous slide
   - **Rationale**: No manual "exit" action, duration calculated automatically

5. **P/N Exclusion from Events**:
   - **Decision**: P/N keys excluded from events array (too noisy)
   - **Rationale**: Slide visits with navigation method provide sufficient data

6. **Single Session Per Log**:
   - **Decision**: Each display command overwrites previous log
   - **Rationale**: Session-scoped logging, simpler implementation

7. **Non-Fatal Log Creation**:
   - **Decision**: Log creation failure is warning, not error
   - **Rationale**: Presentation must work without logging

---

## Traceability Matrix

| Rule | BDD Scenarios | Event Storming Events | Acceptance Criteria |
|------|---------------|----------------------|---------------------|
| Rule 1 | history-logging.feature (3) | SessionStarted | AC-1 |
| Rule 2 | history-logging.feature (1) | SessionStarted | AC-2 |
| Rule 3 | history-logging.feature (6) | SessionStarted | AC-3 |
| Rule 4 | history-logging.feature (5) | SlideVisitRecorded | AC-4 |
| Rule 5 | history-logging.feature (3) | SlideExitRecorded | AC-5 |
| Rule 6 | history-logging.feature (4) | EventLogged | AC-6 |
| Rule 7 | history-logging.feature (2) | SessionEnded | AC-7 |
| Rule 8 | history-logging.feature (2) | SessionStarted | AC-8 |
| Rule 9 | history-logging.feature (3) | LogCreationFailed | AC-9 |
| Rule 10 | history-logging.feature (2) | SessionStarted | AC-10 |
| Rule 11 | history-logging.feature (6) | SlideVisitRecorded | AC-11 |
| Rule 12 | history-logging.feature (2) | SessionEnded | AC-12 |

**Total Coverage**: 39 BDD scenarios across 12 business rules

---

## Implementation Notes

### Domain Model Requirements
- `SessionLog` aggregate with state (sessionId, presentationName, startTime, endTime, slideVisits, events)
- `StartSession` command initializes log file with metadata
- `RecordSlideVisit` command logs navigation with method and timestamp
- `RecordEvent` command logs B/S/G key presses
- `EndSession` command sets endTime, exits last slide
- `LoggingError` enum for log creation failures

### Infrastructure Requirements
- JSON file writer (structured JSON output)
- UUID generator for sessionId
- ISO 8601 timestamp formatter
- Log file path resolver (`<output-dir>/<deck-name>.log`)
- Browser close/unload event handler (set endTime)

### UI Requirements
- No UI for logging (transparent background process)
- Warning message if log creation fails (console output)

---

## JSON Schema Example

```json
{
  "session": {
    "sessionId": "a3f2e9d7-4b5c-6a1d-8e9f-0a1b2c3d4e5f",
    "presentationName": "mdslides-tutorial",
    "startTime": "2025-12-29T14:23:45Z",
    "endTime": "2025-12-29T15:09:17Z",
    "theme": "dark",
    "totalSlides": 42
  },
  "slideVisits": [
    {
      "slideIndex": 0,
      "entryTime": "2025-12-29T14:23:45Z",
      "exitTime": "2025-12-29T14:24:12Z",
      "navigationMethod": "Linear"
    },
    {
      "slideIndex": 1,
      "entryTime": "2025-12-29T14:24:12Z",
      "exitTime": "2025-12-29T14:25:03Z",
      "navigationMethod": "Linear"
    },
    {
      "slideIndex": 12,
      "entryTime": "2025-12-29T14:30:00Z",
      "exitTime": "2025-12-29T14:33:25Z",
      "navigationMethod": "Goto"
    }
  ],
  "events": [
    {
      "timestamp": "2025-12-29T14:26:15Z",
      "key": "B",
      "action": "break_mode_enabled"
    },
    {
      "timestamp": "2025-12-29T14:28:22Z",
      "key": "B",
      "action": "break_mode_disabled"
    },
    {
      "timestamp": "2025-12-29T14:29:10Z",
      "key": "S",
      "action": "speaker_view_opened"
    }
  ]
}
```

---

**Example Mapping Complete**: 2025-12-29
**Next Step**: Acceptance Criteria Review
**Ready for Implementation**: Pending AC approval
