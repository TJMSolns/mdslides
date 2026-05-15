# Acceptance Criteria: Display Command

**Feature**: Display Command (v3.0.0 - Feature 9 of 10)
**Date**: 2025-12-29
**Status**: Pending Approval

---

## User Story

**As a** presenter
**I want to** run `mdslides display <deck-name>` to open my rendered presentation in a browser with session logging enabled
**So that** I can deliver my talk and capture metrics for later analysis

---

## Acceptance Criteria

### AC-1: Command Invocation (Deck Name Required)
**Usage**: `mdslides display <deck-name>`

**Given** no deck name
**Then** error: "Usage: mdslides display <deck-name> [--browser <name>]"

**BDD Scenarios**: display-command.feature (2 scenarios)

---

### AC-2: Presentation Not Rendered (Error)
**Given** `my-talk/index.html` does NOT exist
**Then** error: "Presentation not rendered: my-talk"
**And** suggests running render or smart command

**BDD Scenarios**: display-command.feature (2 scenarios)

---

### AC-3: Log File Initialization
**When** display command runs at 14:23:45
**Then** log created at `my-talk/my-talk.log` with:
- sessionId (UUID v4)
- presentationName
- startTime: "2025-12-29T14:23:45Z"
- theme
- totalSlides

**BDD Scenarios**: display-command.feature (1 scenario)

---

### AC-4: Log File Overwrites Previous Session
**Given** log exists from yesterday
**When** I run display today
**Then** old log truncated, new session starts

**BDD Scenarios**: display-command.feature (1 scenario)

---

### AC-5: Log Creation Failure (Non-Fatal)
**Given** output directory is read-only
**When** I run display
**Then** warning displayed
**But** browser opens (exit code 0)

**BDD Scenarios**: display-command.feature (2 scenarios)

---

### AC-6: Browser Resolution (4-Layer Precedence)
**Precedence**: CLI arg > project config > global config > system default

**System defaults**:
- Linux: xdg-open
- macOS: open
- Windows: cmd /c start

**BDD Scenarios**: display-command.feature (4 scenarios)

---

### AC-7: Supported Browsers
**Supported**: default, firefox, chromium, google-chrome, brave, <absolute-path>

**BDD Scenarios**: display-command.feature (2 scenarios)

---

### AC-8: Browser Not Found (Error)
**Given** safari not in PATH
**Then** error: "Failed to launch browser: safari"
**And** suggests alternatives
**And** exit code 2

**BDD Scenarios**: display-command.feature (1 scenario)

---

### AC-9: Absolute File URLs
**URL format**: `file:///absolute/path/to/output/index.html`

**Windows**: Backslashes converted to forward slashes

**BDD Scenarios**: display-command.feature (2 scenarios)

---

### AC-10: Non-Blocking Browser Launch
**Then** browser starts in background
**And** command returns to shell immediately

**BDD Scenarios**: display-command.feature (2 scenarios)

---

### AC-11: Success Message
**Output**:
```
✓ Opened presentation in Firefox
  URL: file:///home/user/mdslides/my-talk/index.html
```

**BDD Scenarios**: display-command.feature (1 scenario)

---

### AC-12: Multiple Display Invocations
**When** I run display twice
**Then** two browser windows/tabs open
**And** second invocation overwrites log

**BDD Scenarios**: display-command.feature (1 scenario)

---

## Exit Codes

| Scenario | Code |
|----------|------|
| Success | 0 |
| Not rendered | 1 |
| Browser not found | 2 |
| Log permission denied | 0 (warning) |

---

## Definition of Done

- [ ] All 12 acceptance criteria implemented
- [ ] 21 BDD scenarios passing
- [ ] Domain model: DisplaySession aggregate
- [ ] Browser launcher working (all platforms)
- [ ] Documentation updated

**Approval**:
- Product Owner: ________________ Date: ________
