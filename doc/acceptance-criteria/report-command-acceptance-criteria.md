# Acceptance Criteria: Report Command

**Feature**: Report Command (v3.0.0 - Feature 8 of 10)
**Date**: 2025-12-29
**Status**: Pending Approval

---

## User Story

**As a** presenter
**I want to** run `mdslides report <deck-name>` to view a formatted report of my presentation session
**So that** I can analyze slide timing, review navigation patterns, and improve future presentations

---

## Acceptance Criteria

### AC-1: Command Invocation (Deck Name Required)
**Usage**: `mdslides report <deck-name>`

**Given** no deck name provided
**Then** error: "Usage: mdslides report <deck-name>"

**BDD Scenarios**: report-command.feature (2 scenarios)

---

### AC-2: Log File Location Resolution
**Given** I run `mdslides report my-talk`
**Then** looks for `my-talk/my-talk.log`

**If not found**:
**Then** error with suggestion to run display command

**BDD Scenarios**: report-command.feature (2 scenarios)

---

### AC-3: JSON Parsing (Valid JSON Required)
**Given** valid JSON log
**Then** successfully parsed, report generated

**If invalid JSON at line 23**:
**Then** error: "Parse error at line 23: Unexpected end of JSON input"

**BDD Scenarios**: report-command.feature (2 scenarios)

---

### AC-4: Report Header Section
**Displays**:
- Presentation name
- Start time (local time)
- Duration (hh:mm:ss)
- Theme
- Total slides
- Slides viewed: 38/42 (90%)

**BDD Scenarios**: report-command.feature (1 scenario)

---

### AC-5: Slide Timing Table (Unicode Box-Drawing)
**Table format**:
```
┌─────┬──────────────────────────────────────┬──────────┐
│ No. │ Title                                │ Duration │
├─────┼──────────────────────────────────────┼──────────┤
│  1  │ MDSlides v3.0.0                      │ 00:00:27 │
```

**Slide numbers**: 1-indexed
**Title truncation**: 38 chars + "..."

**BDD Scenarios**: report-command.feature (2 scenarios)

---

### AC-6: Top 5 Longest Slides Section
**Displays**: Top 5 slides by duration (descending)

**If < 5 slides viewed**:
**Then** dynamic title: "Top N Longest Slides"

**BDD Scenarios**: report-command.feature (2 scenarios)

---

### AC-7: Events Log Section
**Displays**: Chronological B, S, G key press events

**Format**:
```
14:26:15  [B] Break mode enabled
14:29:10  [S] Speaker view opened
```

**If no events**:
**Then** "No events recorded during this session."

**BDD Scenarios**: report-command.feature (2 scenarios)

---

### AC-8: Navigation Statistics Section
**Displays**:
- Forward navigations
- Backward navigations (P)
- Goto jumps
- Slides revisited

**BDD Scenarios**: report-command.feature (2 scenarios)

---

### AC-9: Duration Formatting (hh:mm:ss)
**Examples**:
- 27 seconds → "00:00:27"
- 185 seconds → "00:03:05"
- 3625 seconds → "01:00:25"

**BDD Scenarios**: report-command.feature (4 scenarios)

---

### AC-10: Ongoing Session Support
**If endTime is null**:
**Then** duration calculated from current time
**And** displays "00:18:30 (IN PROGRESS)"

**BDD Scenarios**: report-command.feature (1 scenario)

---

### AC-11: Unicode Box-Drawing Characters
**Uses**: ┌ ┬ ┐ ├ ┼ ┤ └ ┴ ┘ ─ │

**Not ASCII**: +, -, |

**BDD Scenarios**: report-command.feature (1 scenario)

---

### AC-12: Console Output Only (No File Write)
**Then** report printed to stdout
**And** user can redirect: `mdslides report my-talk > report.txt`

**BDD Scenarios**: report-command.feature (1 scenario)

---

## Exit Codes

| Scenario | Code |
|----------|------|
| Success | 0 |
| Log not found | 1 |
| Parse error | 2 |

---

## Definition of Done

- [ ] All 12 acceptance criteria implemented
- [ ] 20 BDD scenarios passing
- [ ] Domain model: PresentationReport aggregate
- [ ] Unicode table rendering working
- [ ] Documentation updated

**Approval**:
- Product Owner: ________________ Date: ________
