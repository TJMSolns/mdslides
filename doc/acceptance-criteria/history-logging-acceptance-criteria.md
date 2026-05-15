# Acceptance Criteria: History Logging

**Feature**: History Logging (Session Log) (v3.0.0 - Feature 3 of 10)
**Date**: 2025-12-29
**Status**: Pending Approval

---

## User Story

**As a** presenter
**I want to** my presentation session automatically logged to a JSON file
**So that** I can analyze slide timing, review navigation patterns, and improve future presentations

---

## Acceptance Criteria

### AC-1: Log File Creation (Display Command Only)
**Given** I run `mdslides display my-talk`
**Then** log file created at `my-talk/my-talk.log`

**Given** I open `my-talk/index.html` directly in browser
**Then** NO log file is created

**BDD Scenarios**: history-logging.feature (3 scenarios)

---

### AC-2: Log File Overwrites Previous Session
**Given** a log file exists from yesterday
**When** I run `mdslides display my-talk`
**Then** the old log is truncated
**And** new session data replaces it

**BDD Scenarios**: history-logging.feature (1 scenario)

---

### AC-3: Session Metadata Initialization
**Then** log contains:
```json
{
  "session": {
    "sessionId": "<UUID v4>",
    "presentationName": "my-talk",
    "startTime": "2025-12-29T14:23:45Z",
    "theme": "dark",
    "totalSlides": 42
  }
}
```

**BDD Scenarios**: history-logging.feature (6 scenarios)

---

### AC-4: Slide Visit Recording
**When** I navigate to slide 5
**Then** logged:
```json
{
  "slideIndex": 5,
  "entryTime": "2025-12-29T14:24:00Z",
  "navigationMethod": "Linear",
  "exitTime": null
}
```

**Navigation Methods**: Linear, Previous, Next, Goto

**BDD Scenarios**: history-logging.feature (5 scenarios)

---

### AC-5: Auto-Exit Previous Slide
**Given** I'm on slide 5 from 14:24:00
**When** I navigate to slide 10 at 14:25:30
**Then** slide 5 exitTime updated to "2025-12-29T14:25:30Z"
**And** duration = 90 seconds

**BDD Scenarios**: history-logging.feature (3 scenarios)

---

### AC-6: Event Logging (B, S, G Keys Only)
**When** I press 'B' at 14:26:15
**Then** event logged:
```json
{
  "timestamp": "2025-12-29T14:26:15Z",
  "key": "B",
  "action": "break_mode_enabled"
}
```

**Excluded**: P/N keys (too noisy)

**BDD Scenarios**: history-logging.feature (4 scenarios)

---

### AC-7: Session End Time Recording
**When** I close browser at 15:09:17
**Then** endTime: "2025-12-29T15:09:17Z"

**If browser crashes**:
**Then** endTime: null (incomplete session)

**BDD Scenarios**: history-logging.feature (2 scenarios)

---

### AC-8: JSON Format Validation
**Then** log file is valid JSON
**And** can be parsed with JSON.parse()
**And** timestamps are ISO 8601 strings

**BDD Scenarios**: history-logging.feature (2 scenarios)

---

### AC-9: Log Creation Failure (Non-Fatal)
**Given** output directory is read-only
**When** I run display command
**Then** warning displayed
**But** browser opens successfully
**And** exit code 0

**BDD Scenarios**: history-logging.feature (3 scenarios)

---

### AC-10: Concurrent Session Handling
**When** I display talk-1 and talk-2
**Then** `talk-1/talk-1.log` and `talk-2/talk-2.log` created
**And** both sessions are independent

**BDD Scenarios**: history-logging.feature (2 scenarios)

---

### AC-11: Navigation Method Classification
| Key/Action | Navigation Method |
|------------|-------------------|
| Right arrow/Space | Linear |
| P key | Previous |
| N key | Next |
| G key (goto) | Goto |

**BDD Scenarios**: history-logging.feature (6 scenarios)

---

### AC-12: Last Slide Exit Time
**Given** last slide visited at 15:09:00
**When** session ends at 15:09:17
**Then** exitTime: "2025-12-29T15:09:17Z"

**BDD Scenarios**: history-logging.feature (2 scenarios)

---

## Definition of Done

- [ ] All 12 acceptance criteria implemented
- [ ] 39 BDD scenarios passing
- [ ] Domain model: SessionLog aggregate
- [ ] JSON file writer working
- [ ] Documentation updated

**Approval**:
- Product Owner: ________________ Date: ________
