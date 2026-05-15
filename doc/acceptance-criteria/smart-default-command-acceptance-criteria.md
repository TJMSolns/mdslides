# Acceptance Criteria: Smart Default Command

**Feature**: Smart Default Command (Workflow Orchestration) (v3.0.0 - Feature 10 of 10)
**Date**: 2025-12-29
**Status**: Pending Approval

---

## User Story

**As a** presenter
**I want to** run `mdslides <deck-name>` (single command, no subcommand)
**So that** the system intelligently decides whether to report, render, and/or display based on file state

---

## Acceptance Criteria

### AC-1: Command Invocation (No Subcommand)
**Usage**: `mdslides <deck-name>`

**Given** I run `mdslides my-talk`
**Then** smart workflow starts

**Given** no deck name
**Then** error: "Usage: mdslides <deck-name> [options]"

**BDD Scenarios**: smart-default-command.feature (2 scenarios)

---

### AC-2: Workflow Decision Tree
**Decision checks**:
1. Does log exist? → shouldReport
2. Is output up-to-date? → shouldRender
3. Always display

**BDD Scenarios**: smart-default-command.feature (3 scenarios)

---

### AC-3: First-Time Presenter (No Log, No Output)
**Given** `my-talk.md` exists, `my-talk/` does NOT exist
**Then** workflow: shouldReport=false, shouldRender=true, shouldDisplay=true
**And** render reason: OutputNotFound

**BDD Scenarios**: smart-default-command.feature (1 scenario)

---

### AC-4: After Giving Presentation (Log Exists, Output Up-to-Date)
**Given** log exists, output up-to-date
**Then** workflow: shouldReport=true, shouldRender=false, shouldDisplay=true
**And** report displayed first
**And** blocking prompt: "Press Enter to continue..."
**And** browser opens after Enter

**BDD Scenarios**: smart-default-command.feature (2 scenarios)

---

### AC-5: Modified Source File (Log Exists, Output Outdated)
**Given** source modified at 15:00, output rendered at 14:00
**Then** workflow: shouldReport=false, shouldRender=true, shouldDisplay=true
**And** render reason: OutputOutdated
**And** report skipped (output is stale)

**BDD Scenarios**: smart-default-command.feature (2 scenarios)

---

### AC-6: Timestamp Comparison (Second-Level Precision)
**Given** source 14:30:45.123, output 14:30:45.789
**When** timestamps compared
**Then** both truncate to 14:30:45
**And** output is up-to-date

**BDD Scenarios**: smart-default-command.feature (2 scenarios)

---

### AC-7: Source File Not Found (Error)
**Given** `my-talk.md` does NOT exist
**Then** error: "Source file not found: my-talk.md"
**And** suggests using display command if only showing existing presentation

**BDD Scenarios**: smart-default-command.feature (1 scenario)

---

### AC-8: Render Failure Handling (Halt Workflow)
**Given** render phase fails
**Then** error displayed
**And** workflow halts (display does NOT execute)
**And** exit code 1

**BDD Scenarios**: smart-default-command.feature (2 scenarios)

---

### AC-9: Config Inheritance (Render and Display)
**Given** I run `mdslides my-talk --theme dark --browser firefox`
**Then** render uses theme "dark"
**And** display launches Firefox

**BDD Scenarios**: smart-default-command.feature (2 scenarios)

---

### AC-10: Blocking Prompt After Report
**Given** report is displayed
**When** prompt "Press Enter to continue..." shown
**Then** command waits for user input
**And** shell is blocked
**And** pressing Enter continues workflow

**BDD Scenarios**: smart-default-command.feature (2 scenarios)

---

### AC-11: Report Skip Optimization
**Given** log exists, source modified
**Then** report skipped (output is stale)
**And** render + display executed

**Rationale**: Stale report is useless (old data)

**BDD Scenarios**: smart-default-command.feature (2 scenarios)

---

### AC-12: Console Output Clarity
**Output messages**:
- Output missing: "Rendering presentation: my-talk"
- Source modified: "Rendering presentation: my-talk (source modified)"
- Output up-to-date: "Presentation up-to-date: my-talk"
- No log: "No log file found (presentation not given yet)."

**BDD Scenarios**: smart-default-command.feature (4 scenarios)

---

## Workflow Paths

| Log | Output | Source Newer | Report | Render | Display |
|-----|--------|--------------|--------|--------|---------|
| No | No | n/a | No | Yes | Yes |
| No | Yes | No | No | No | Yes |
| No | Yes | Yes | No | Yes | Yes |
| Yes | No | n/a | Yes | Yes | Yes |
| Yes | Yes | No | Yes | No | Yes |
| Yes | Yes | Yes | No | Yes | Yes |

---

## Exit Codes

| Scenario | Code |
|----------|------|
| Success | 0 |
| Source not found | 1 |
| Render failed | 1 |
| Display failed | 2 |

---

## Definition of Done

- [ ] All 12 acceptance criteria implemented
- [ ] 25 BDD scenarios passing
- [ ] Domain model: SmartWorkflow aggregate
- [ ] File state analysis working
- [ ] Workflow orchestration working
- [ ] Documentation updated

**Approval**:
- Product Owner: ________________ Date: ________
