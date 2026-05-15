# Example Mapping: Report Command

**Date**: 2025-12-29
**Feature**: Report Command (v3.0.0 - Feature 8 of 10)
**Participants**: Product Owner, Bench Developer, Architect
**Story**: As a presenter, I want to run `mdslides report <deck-name>` to view a formatted report of my presentation session so I can analyze slide timing, review navigation patterns, and improve future presentations.

---

## Business Rules (Yellow Cards)

### Rule 1: Command Invocation (Deck Name Required)
**Statement**: Report command requires deck name argument. Invoked as `mdslides report <deck-name>`. No deck name → usage error.

**Examples** (Green Cards ✅):
- **Ex 1.1**: `mdslides report my-talk` → Report displays for my-talk.log
- **Ex 1.2**: `mdslides report talks/conference` → Report displays for talks/conference/conference.log
- **Ex 1.3**: `mdslides report` (no deck name) → Error: "Usage: mdslides report <deck-name>"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 1.4**: Report command works without deck name → Wrong, deck name required
- ❌ **Ex 1.5**: Report lists all available logs → Wrong, requires specific deck name

**BDD Traceability**:
- `report-command.feature`: "Successful report command invocation"
- `report-command.feature`: "Report command requires deck name argument"

**Usage**: `java -jar ../mdslides.jar report <deck-name>`

---

### Rule 2: Log File Location Resolution
**Statement**: Log file resolved to `<output-dir>/<deck-name>.log`. If not found, display actionable error.

**Examples** (Green Cards ✅):
- **Ex 2.1**: `report my-talk` → Looks for `my-talk/my-talk.log`
- **Ex 2.2**: `report talks/conference` → Looks for `talks/conference/conference.log`
- **Ex 2.3**: Log file exists → Report generated

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 2.4**: Log not found, command succeeds → Wrong, must error
- ❌ **Ex 2.5**: Wrong log location checked → Wrong, must follow `<output-dir>/<deck-name>.log` pattern

**BDD Traceability**:
- `report-command.feature`: "Error when log file doesn't exist"

**Error Message**:
```
✗ No log file found for presentation: my-talk

  Expected location: my-talk/my-talk.log

  To create a log file, present the deck using:
    java -jar ../mdslides.jar display my-talk

  Note: Opening index.html directly in browser does NOT create logs.
```

---

### Rule 3: JSON Parsing (Valid JSON Required)
**Statement**: Log file parsed as JSON. Invalid JSON → parse error with line number and actionable message.

**Examples** (Green Cards ✅):
- **Ex 3.1**: Valid JSON log → Successfully parsed, report generated
- **Ex 3.2**: Empty log file → Parse error (not valid JSON)
- **Ex 3.3**: Log contains valid session, slideVisits, events → All parsed

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 3.4**: Invalid JSON at line 23 → Error: "Parse error at line 23: Unexpected end of JSON input"
- ❌ **Ex 3.5**: Corrupted log → Parse error with recovery suggestions

**BDD Traceability**:
- `report-command.feature`: "Successful JSON log file parsing"
- `report-command.feature`: "Corrupted log file (invalid JSON)"

**Error Message**:
```
✗ Failed to parse log file: my-talk/my-talk.log

  Parse error at line 23: Unexpected end of JSON input

  The log file may be corrupted. To regenerate:
  1. Delete or rename the corrupted log file
  2. Present the deck again using: java -jar ../mdslides.jar display my-talk
```

---

### Rule 4: Report Header Section
**Statement**: Header displays session information: presentation name, start time, duration, theme, total slides, slides viewed.

**Examples** (Green Cards ✅):
- **Ex 4.1**: Session from 14:23:45 to 15:09:17 → Duration: "00:45:32"
- **Ex 4.2**: 42 total slides, 38 viewed → "Slides Viewed: 38/42 (90%)"
- **Ex 4.3**: Dark theme → "Theme: dark"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 4.4**: Duration calculated incorrectly → Wrong, must be endTime - startTime
- ❌ **Ex 4.5**: Percentage not rounded → Wrong, round to nearest integer

**BDD Traceability**:
- `report-command.feature`: "Report header displays session information"

**Header Format**:
```
═══════════════════════════════════════════════════════════════
  Presentation Report: mdslides-tutorial
═══════════════════════════════════════════════════════════════

Session Information:
  Started:        2025-12-29 14:23:45
  Duration:       00:45:32
  Theme:          dark
  Total Slides:   42
  Slides Viewed:  38/42 (90%)
```

---

### Rule 5: Slide Timing Table (Unicode Box-Drawing)
**Statement**: Table displays slide number (1-indexed), title (truncated at 38 chars), and duration (hh:mm:ss).

**Examples** (Green Cards ✅):
- **Ex 5.1**: Slide 0 → Number displays as "1" (1-indexed)
- **Ex 5.2**: Title "MDSlides v3.0.0" (17 chars) → Displayed fully
- **Ex 5.3**: Title 50 chars → Truncated to 38 chars + "..."
- **Ex 5.4**: Duration 185 seconds → Displayed as "00:03:05"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 5.5**: Slide number 0-indexed → Wrong, 1-indexed for users
- ❌ **Ex 5.6**: Title not truncated → Wrong, max 38 chars

**BDD Traceability**:
- `report-command.feature`: "Slide timing summary table"
- `report-command.feature`: "Slide title truncation"

**Table Format**:
```
Slide Timing Summary:
  ┌─────┬──────────────────────────────────────┬──────────┐
  │ No. │ Title                                │ Duration │
  ├─────┼──────────────────────────────────────┼──────────┤
  │  1  │ MDSlides v3.0.0                      │ 00:00:27 │
  │  2  │ About This Tutorial                  │ 00:00:51 │
  │  3  │ Configuration Precedence             │ 00:01:15 │
  │ ... │ ...                                  │ ...      │
  │ 42  │ Thank You!                           │ 00:00:18 │
  └─────┴──────────────────────────────────────┴──────────┘
```

---

### Rule 6: Top 5 Longest Slides Section
**Statement**: Displays top 5 slides by duration in descending order. Shows title, slide number, and duration (mm:ss).

**Examples** (Green Cards ✅):
- **Ex 6.1**: Slide 12 took 205 seconds → "Mermaid Diagrams (Slide 12)  03:25"
- **Ex 6.2**: Only 3 slides viewed → "Top 3 Longest Slides" (dynamic title)
- **Ex 6.3**: Sorted by duration descending → Longest first

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 6.4**: Always shows "Top 5" even with 3 slides → Wrong, dynamic title
- ❌ **Ex 6.5**: Duration in hh:mm:ss format → Wrong, mm:ss for top slides

**BDD Traceability**:
- `report-command.feature`: "Top 5 longest slides section"
- `report-command.feature`: "Fewer than 5 slides viewed"

**Section Format**:
```
Top 5 Longest Slides:
  1. Mermaid Diagrams (Slide 12)           03:25
  2. Two-Column Layout (Slide 18)          02:47
  3. Accessibility Features (Slide 28)     02:15
  4. Configuration System (Slide 3)        01:15
  5. Code Highlighting (Slide 23)          01:02
```

---

### Rule 7: Events Log Section
**Statement**: Displays chronological log of B, S, G key press events with timestamp and action.

**Examples** (Green Cards ✅):
- **Ex 7.1**: Break enabled at 14:26:15 → "14:26:15  [B] Break mode enabled"
- **Ex 7.2**: Speaker view at 14:29:10 → "14:29:10  [S] Speaker view opened"
- **Ex 7.3**: Goto at 14:35:42 → "14:35:42  [G] Goto slide 25"
- **Ex 7.4**: No events → "No events recorded during this session."

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 7.5**: P/N keys in events log → Wrong, P/N excluded (too noisy)
- ❌ **Ex 7.6**: Events not chronological → Wrong, sorted by timestamp

**BDD Traceability**:
- `report-command.feature`: "Events log displays chronological key presses"
- `report-command.feature`: "No events logged (empty events array)"

**Section Format**:
```
Events Log:
  14:26:15  [B] Break mode enabled
  14:28:22  [B] Break mode disabled
  14:29:10  [S] Speaker view opened
  14:35:42  [G] Goto slide 25
```

---

### Rule 8: Navigation Statistics Section
**Statement**: Displays counts for forward navigations, backward (P), goto, and revisited slides.

**Examples** (Green Cards ✅):
- **Ex 8.1**: 35 forward, 8 backward, 3 goto → All displayed
- **Ex 8.2**: Visited slides [0, 5, 10, 5] → "Slides revisited: 1" (slide 5)
- **Ex 8.3**: 45 total views, 38 unique → Calculated from slideVisits array

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 8.4**: Revisited count includes all visits → Wrong, count unique slides revisited
- ❌ **Ex 8.5**: Navigation counts not accurate → Wrong, must match log data

**BDD Traceability**:
- `report-command.feature`: "Navigation statistics display"
- `report-command.feature`: "Navigation statistics calculation"

**Section Format**:
```
Navigation Statistics:
  Forward navigations:      35
  Backward navigations:     8
  Goto jumps:              3
  Slides revisited:        7
```

---

### Rule 9: Duration Formatting (hh:mm:ss)
**Statement**: All durations formatted as hh:mm:ss with zero-padding. 27 seconds → "00:00:27", 185 seconds → "00:03:05".

**Examples** (Green Cards ✅):
- **Ex 9.1**: 27 seconds → "00:00:27"
- **Ex 9.2**: 185 seconds → "00:03:05"
- **Ex 9.3**: 3625 seconds (1h 25s) → "01:00:25"
- **Ex 9.4**: 7325 seconds (2h 2m 5s) → "02:02:05"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 9.5**: 27 seconds → "27s" → Wrong, must be hh:mm:ss
- ❌ **Ex 9.6**: No zero-padding → "0:5:30" → Wrong, must be "00:05:30"

**BDD Traceability**:
- `report-command.feature`: "Consistent hh:mm:ss duration format"

**Formatting Logic**:
```scala
def formatDuration(seconds: Long): String =
  val hours = seconds / 3600
  val minutes = (seconds % 3600) / 60
  val secs = seconds % 60
  f"$hours%02d:$minutes%02d:$secs%02d"
```

---

### Rule 10: Ongoing Session Support
**Statement**: If session has no endTime (in progress or crashed), display "IN PROGRESS" and calculate duration from current time.

**Examples** (Green Cards ✅):
- **Ex 10.1**: endTime is null, current time 14:42:15, startTime 14:23:45 → Duration: "00:18:30 (IN PROGRESS)"
- **Ex 10.2**: Browser crashed → endTime null, show as incomplete
- **Ex 10.3**: Note displayed: "Session is currently in progress. Report shows data up to now."

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 10.4**: endTime null → Error or no report → Wrong, gracefully handle
- ❌ **Ex 10.5**: Duration not calculated for ongoing session → Wrong, use current time

**BDD Traceability**:
- `report-command.feature`: "Report for in-progress presentation"

**Calculation**: `duration = currentTime - startTime` if endTime is null

---

### Rule 11: Unicode Box-Drawing Characters
**Statement**: Tables use Unicode box-drawing characters for borders and lines.

**Examples** (Green Cards ✅):
- **Ex 11.1**: Top-left corner: ┌
- **Ex 11.2**: Top-right corner: ┐
- **Ex 11.3**: Horizontal line: ─
- **Ex 11.4**: Vertical line: │
- **Ex 11.5**: Cross intersection: ┼

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 11.6**: ASCII characters (+, -, |) → Wrong, Unicode required
- ❌ **Ex 11.7**: Mixed ASCII and Unicode → Wrong, consistent Unicode only

**BDD Traceability**:
- `report-command.feature`: "Report uses Unicode box-drawing characters"

**Character Set**:
```
┌ ┬ ┐  (top)
├ ┼ ┤  (middle)
└ ┴ ┘  (bottom)
─ │    (lines)
```

---

### Rule 12: Console Output Only (No File Write)
**Statement**: Report displayed to stdout, not saved to file. User can redirect output manually.

**Examples** (Green Cards ✅):
- **Ex 12.1**: Report printed to console
- **Ex 12.2**: No file created in output directory
- **Ex 12.3**: User can redirect: `mdslides report my-talk > report.txt`

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 12.4**: Report saved to `my-talk-report.txt` → Wrong, stdout only
- ❌ **Ex 12.5**: Report cannot be redirected → Wrong, stdout supports redirection

**BDD Traceability**:
- `report-command.feature`: "Report is displayed to stdout, not saved"

**Output**: All report content to System.out

---

## Questions (Pink Cards)

### Q1: Should we support report export formats (PDF, HTML, CSV)?
**Status**: DEFERRED to v3.1.0
**Decision**: Console output only for v3.0.0
**Rationale**: Simple implementation, stdout can be redirected. Export formats in future.

### Q2: Should we support color output (ANSI codes)?
**Status**: DEFERRED to v3.1.0
**Decision**: Plain text with Unicode box-drawing for v3.0.0
**Rationale**: Portable, works in all terminals. Color in future with --color flag.

### Q3: Should report support verbose mode (all slide visits)?
**Status**: DEFERRED to v3.1.0
**Decision**: Summary only for v3.0.0
**Rationale**: Most useful data in summary. Verbose mode in future with --verbose flag.

### Q4: Should report fit 80-column terminal?
**Status**: RESOLVED - Yes
**Decision**: Report width ≤ 80 characters
**Rationale**: Standard terminal width, ensures compatibility.

### Q5: Should timezone be configurable for timestamps?
**Status**: RESOLVED - Local time
**Decision**: Display timestamps in local time (no timezone suffix)
**Rationale**: User-friendly, log has UTC, report converts to local.

### Q6: Should report handle multi-session logs?
**Status**: NOT APPLICABLE
**Decision**: Single session per log (overwrite on each display)
**Rationale**: Log structure is single-session, multi-session in v4.0.0.

---

## Design Decisions from Event Storming

1. **Unicode Box-Drawing**:
   - **Decision**: Use Unicode characters (┌─┬─┐ │ ├─┼─┤ └─┴─┘)
   - **Rationale**: Professional appearance, widely supported in modern terminals

2. **Console Output Only**:
   - **Decision**: Stdout, no file write
   - **Rationale**: Unix philosophy (text streams), user controls redirection

3. **1-Indexed Slide Numbers**:
   - **Decision**: Display slide 0 as "1" in report
   - **Rationale**: User-facing numbers, matches goto popup

4. **Duration Format**:
   - **Decision**: hh:mm:ss for all durations
   - **Rationale**: Consistent, zero-padded, sortable

5. **Ongoing Session Handling**:
   - **Decision**: Calculate duration from current time if endTime null
   - **Rationale**: Useful for reviewing in-progress sessions

6. **Title Truncation**:
   - **Decision**: 38 chars + "..." for table column
   - **Rationale**: Fits 80-column terminal, readable

7. **Top N Slides Dynamic**:
   - **Decision**: "Top N" where N = min(5, slides viewed)
   - **Rationale**: Handles edge case where < 5 slides viewed

---

## Traceability Matrix

| Rule | BDD Scenarios | Event Storming Events | Acceptance Criteria |
|------|---------------|----------------------|---------------------|
| Rule 1 | report-command.feature (2) | ReportCommandInvoked | AC-1 |
| Rule 2 | report-command.feature (2) | LogFileResolved | AC-2 |
| Rule 3 | report-command.feature (2) | LogFileParsed | AC-3 |
| Rule 4 | report-command.feature (1) | SessionInfoDisplayed | AC-4 |
| Rule 5 | report-command.feature (2) | SlideTimingTableDisplayed | AC-5 |
| Rule 6 | report-command.feature (2) | TopSlidesDisplayed | AC-6 |
| Rule 7 | report-command.feature (2) | EventsLogDisplayed | AC-7 |
| Rule 8 | report-command.feature (2) | NavigationStatsDisplayed | AC-8 |
| Rule 9 | report-command.feature (4) | DurationFormatted | AC-9 |
| Rule 10 | report-command.feature (1) | OngoingSessionDetected | AC-10 |
| Rule 11 | report-command.feature (1) | UnicodeBoxDrawing | AC-11 |
| Rule 12 | report-command.feature (1) | ReportOutputToStdout | AC-12 |

**Total Coverage**: 20 BDD scenarios across 12 business rules

---

## Implementation Notes

### Domain Model Requirements
- `PresentationReport` aggregate with parsed session log
- `GenerateReport` command reads log, parses, formats
- `FormatDuration` value object for hh:mm:ss formatting
- `CalculateNavigationStats` command analyzes slideVisits
- `RenderReportTable` command generates Unicode tables
- `ReportError` enum for log not found, parse errors

### Infrastructure Requirements
- JSON parser (parse log file)
- Duration formatter (hh:mm:ss)
- Unicode table renderer (box-drawing characters)
- Stdout writer (System.out)
- Timestamp formatter (ISO 8601 → local time)

### UI Requirements
- No UI (CLI only)
- Console output formatting
- Table alignment and padding

---

## Report Width Calculation

**Target**: 80 characters
**Table Structure**:
- Border characters: 2 (left |, right |)
- Column separators: 2 (│ between columns)
- Padding: 6 (2 per column)
- Slide number column: 5 chars ("  42 ")
- Title column: 38 chars (truncated)
- Duration column: 10 chars (" 00:00:00 ")

**Total**: 2 + 2 + 6 + 5 + 38 + 10 = 63 chars (fits 80-column terminal)

---

## Exit Codes

| Scenario | Exit Code |
|----------|-----------|
| Report generated successfully | 0 |
| Log file not found | 1 |
| Log file parse error (corrupted) | 2 |

---

**Example Mapping Complete**: 2025-12-29
**Next Step**: Acceptance Criteria Review
**Ready for Implementation**: Pending AC approval
