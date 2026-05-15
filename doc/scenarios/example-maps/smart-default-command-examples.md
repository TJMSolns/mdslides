# Example Mapping: Smart Default Command

**Date**: 2025-12-29
**Feature**: Smart Default Command (Workflow Orchestration) (v3.0.0 - Feature 10 of 10)
**Participants**: Product Owner, Bench Developer, Architect
**Story**: As a presenter, I want to run a single command `mdslides <deck-name>` that intelligently decides whether to report, render, and/or display based on file state so I can minimize repetitive CLI invocations and follow a smart workflow.

---

## Business Rules (Yellow Cards)

### Rule 1: Command Invocation (No Subcommand)
**Statement**: Smart command invoked without subcommand: `mdslides <deck-name>`. Deck name required, no subcommand.

**Examples** (Green Cards ✅):
- **Ex 1.1**: `mdslides my-talk` → Smart workflow starts
- **Ex 1.2**: `mdslides talks/conference` → Smart workflow for talks/conference
- **Ex 1.3**: `mdslides` (no deck name) → Error: "Usage: mdslides <deck-name> [options]"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 1.4**: Smart command works without deck name → Wrong, deck name required
- ❌ **Ex 1.5**: `mdslides my-talk other-talk` → Error: "Smart command accepts only one deck name"

**BDD Traceability**:
- `smart-default-command.feature`: "Smart command invoked without subcommand"
- `smart-default-command.feature`: "Smart command requires deck name"

**Usage**: `java -jar ../mdslides.jar <deck-name>`

---

### Rule 2: Workflow Decision Tree (Log → Render → Display)
**Statement**: Decision tree checks: (1) Does log exist? → shouldReport. (2) Is output up-to-date? → shouldRender. (3) Always display.

**Examples** (Green Cards ✅):
- **Ex 2.1**: Log exists, output up-to-date → Report + Display (skip render)
- **Ex 2.2**: No log, output missing → Render + Display (skip report)
- **Ex 2.3**: Log exists, source modified → Render + Display (skip report, output stale)
- **Ex 2.4**: No log, output up-to-date → Display only (skip report and render)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 2.5**: Display not always executed → Wrong, display always runs
- ❌ **Ex 2.6**: Report shown when output is stale → Wrong, report skipped (data is outdated)

**BDD Traceability**:
- `smart-default-command.feature`: "Decision logic - Check log file first"
- `smart-default-command.feature`: "Decision logic - Check render status"
- `smart-default-command.feature`: "Decision logic - Always display"

**Invariant**: `shouldDisplay = always true`

---

### Rule 3: First-Time Presenter (No Log, No Output)
**Statement**: First invocation with no output directory → Render + Display. No report (log doesn't exist).

**Examples** (Green Cards ✅):
- **Ex 3.1**: `my-talk.md` exists, `my-talk/` does not exist → Render + Display
- **Ex 3.2**: Console: "No log file found (presentation not given yet)."
- **Ex 3.3**: Console: "Rendering presentation: my-talk"
- **Ex 3.4**: Workflow: shouldReport=false, shouldRender=true, shouldDisplay=true

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 3.5**: Report attempted when no log → Wrong, report skipped
- ❌ **Ex 3.6**: Output not rendered → Wrong, must render (output missing)

**BDD Traceability**:
- `smart-default-command.feature`: "First time running smart command (render + display)"

**Render Reason**: OutputNotFound

---

### Rule 4: After Giving Presentation (Log Exists, Output Up-to-Date)
**Statement**: Log exists, output up-to-date → Report + Display (skip render). Blocking prompt after report.

**Examples** (Green Cards ✅):
- **Ex 4.1**: Log from yesterday, output up-to-date → Report displayed, then "Press Enter to continue...", then display
- **Ex 4.2**: Workflow: shouldReport=true, shouldRender=false, shouldDisplay=true
- **Ex 4.3**: User reviews report, presses Enter, browser opens

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 4.4**: Display before report → Wrong, report first
- ❌ **Ex 4.5**: No blocking prompt → Wrong, user must press Enter

**BDD Traceability**:
- `smart-default-command.feature`: "Smart command after presentation session (report + display)"
- `smart-default-command.feature`: "User presses Enter after viewing report"

**UX**: Report → Wait for Enter → Display

---

### Rule 5: Modified Source File (Log Exists, Output Outdated)
**Statement**: Source modified after render → Re-render + Display (skip report, output is stale).

**Examples** (Green Cards ✅):
- **Ex 5.1**: Source modified at 15:00:00, output rendered at 14:00:00 → Re-render (source newer)
- **Ex 5.2**: Workflow: shouldReport=false, shouldRender=true, shouldDisplay=true
- **Ex 5.3**: Console: "Rendering presentation: my-talk (source modified)"
- **Ex 5.4**: Report not displayed (output stale, report would show old data)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 5.5**: Report shown when output is stale → Wrong, report skipped
- ❌ **Ex 5.6**: Render not triggered → Wrong, source is newer than output

**BDD Traceability**:
- `smart-default-command.feature`: "Source modified after render (re-render + display)"
- `smart-default-command.feature`: "Skip report when render is needed"

**Render Reason**: OutputOutdated

---

### Rule 6: Timestamp Comparison (Second-Level Precision)
**Statement**: Source vs. output timestamps compared at second-level precision (truncate milliseconds). If source ≤ output, up-to-date.

**Examples** (Green Cards ✅):
- **Ex 6.1**: Source 14:30:00, output 14:00:00 → Source newer, re-render
- **Ex 6.2**: Source 14:30:45.123, output 14:30:45.789 → Both truncate to 14:30:45, up-to-date
- **Ex 6.3**: Source 14:30:44, output 14:30:45 → Output newer, up-to-date

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 6.4**: Millisecond precision used → Wrong, second-level only
- ❌ **Ex 6.5**: Source 14:30:45.999 > output 14:30:45.001 → Wrong, both 14:30:45 (equal)

**BDD Traceability**:
- `smart-default-command.feature`: "Source timestamp vs. output timestamp"
- `smart-default-command.feature`: "Second-level precision for timestamp comparison"

**Comparison**: `sourceSec ≤ outputSec → up-to-date`

---

### Rule 7: Source File Not Found (Error)
**Statement**: If source file (`<deck-name>.md`) does not exist, display error with actionable suggestions.

**Examples** (Green Cards ✅):
- **Ex 7.1**: `my-talk.md` missing → Error: "Source file not found: my-talk.md"
- **Ex 7.2**: Suggestion to use display command if only showing existing presentation
- **Ex 7.3**: Exit code 1

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 7.4**: Command succeeds without source → Wrong, source required for smart workflow
- ❌ **Ex 7.5**: No actionable suggestion → Wrong, suggest display command

**BDD Traceability**:
- `smart-default-command.feature`: "Error when source file doesn't exist"

**Error Message**:
```
✗ Source file not found: my-talk.md

  Smart command requires source file to determine render status.

  If you only want to display the existing rendered presentation:
    java -jar ../mdslides.jar display my-talk

  If you renamed or moved the source file, update the deck name.
```

---

### Rule 8: Render Failure Handling (Halt Workflow)
**Statement**: If render phase fails, halt workflow. Display phase does NOT execute. Exit code 1.

**Examples** (Green Cards ✅):
- **Ex 8.1**: Render fails (invalid markdown) → Error displayed, workflow halts
- **Ex 8.2**: Display phase does not execute after render failure
- **Ex 8.3**: Exit code 1, user must fix error and re-run

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 8.4**: Display executed after render failure → Wrong, workflow halts
- ❌ **Ex 8.5**: Render retried automatically → Wrong, no retry (user must fix)

**BDD Traceability**:
- `smart-default-command.feature`: "Render fails during smart workflow"
- `smart-default-command.feature`: "No retry on render failure"

**Error Message**:
```
✗ Render failed for: my-talk

  [Render error details...]

  Workflow halted. Fix the error and try again.
```

---

### Rule 9: Config Inheritance (Render and Display)
**Statement**: CLI options apply to both render and display phases. 4-layer precedence: CLI > project > global > default.

**Examples** (Green Cards ✅):
- **Ex 9.1**: `mdslides my-talk --theme dark` → Render uses theme "dark"
- **Ex 9.2**: `mdslides my-talk --browser firefox` → Display launches Firefox
- **Ex 9.3**: Project config theme "dark", CLI `--theme light` → CLI wins (theme "light")

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 9.4**: CLI options not passed to render → Wrong, must inherit
- ❌ **Ex 9.5**: Display ignores browser option → Wrong, must respect config

**BDD Traceability**:
- `smart-default-command.feature`: "Smart command respects render config"
- `smart-default-command.feature`: "Smart command respects display config"

**Inheritance**: All CLI options apply to render and display phases.

---

### Rule 10: Blocking Prompt After Report
**Statement**: If report is displayed, command blocks with prompt "Press Enter to continue..." before display phase.

**Examples** (Green Cards ✅):
- **Ex 10.1**: Report shown → Prompt displayed, shell blocked, user presses Enter → Display phase begins
- **Ex 10.2**: User can review report for any duration → Command waits patiently
- **Ex 10.3**: After Enter, browser opens presentation

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 10.4**: Display starts before Enter → Wrong, blocking prompt required
- ❌ **Ex 10.5**: No prompt after report → Wrong, user must acknowledge

**BDD Traceability**:
- `smart-default-command.feature`: "Prompt blocks until user presses Enter"
- `smart-default-command.feature`: "User can review report before continuing"

**UX**: Report → Blocking prompt → Display (user-paced workflow)

---

### Rule 11: Report Skip Optimization
**Statement**: Report skipped when render is needed (output is stale, report would show outdated data).

**Examples** (Green Cards ✅):
- **Ex 11.1**: Log exists, source modified → Report skipped, render + display executed
- **Ex 11.2**: Console: "Rendering presentation: my-talk (source modified)" (no report)
- **Ex 11.3**: Workflow optimized (skip report for stale data)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 11.4**: Report shown when output is stale → Wrong, report is outdated (useless)
- ❌ **Ex 11.5**: Report and render both executed → Wrong, report skipped if render needed

**BDD Traceability**:
- `smart-default-command.feature`: "Skip report when render is needed"

**Optimization**: Skip report if `shouldRender = true` (output is stale or missing).

---

### Rule 12: Console Output Clarity
**Statement**: Clear status messages at each phase. Render reason shown in message if applicable.

**Examples** (Green Cards ✅):
- **Ex 12.1**: Output missing → "Rendering presentation: my-talk"
- **Ex 12.2**: Source modified → "Rendering presentation: my-talk (source modified)"
- **Ex 12.3**: Output up-to-date, skip render → "Presentation up-to-date: my-talk"
- **Ex 12.4**: No log → "No log file found (presentation not given yet)."

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 12.5**: No status messages → Wrong, user needs feedback
- ❌ **Ex 12.6**: Render reason not shown → Wrong, helps user understand workflow

**BDD Traceability**:
- `smart-default-command.feature`: "Status message for render (output missing)"
- `smart-default-command.feature`: "Status message for render (source modified)"
- `smart-default-command.feature`: "Status message for display (output up-to-date)"

**Messages**:
- Output missing: "Rendering presentation: my-talk"
- Source modified: "Rendering presentation: my-talk (source modified)"
- Output up-to-date: "Presentation up-to-date: my-talk"

---

## Questions (Pink Cards)

### Q1: Should smart command support multi-deck batch processing?
**Status**: DEFERRED to v3.1.0
**Decision**: Single deck only for v3.0.0
**Rationale**: Simplifies implementation. Batch processing in future with `--batch` flag.

### Q2: Should smart command support watch mode (auto-reload)?
**Status**: DEFERRED to v3.1.0
**Decision**: No watch mode in v3.0.0
**Rationale**: Single invocation workflow. Watch mode in future with `--watch` flag.

### Q3: Should report timeout after N seconds (auto-continue)?
**Status**: RESOLVED - No
**Decision**: Blocking prompt waits indefinitely for Enter
**Rationale**: User should control when to continue, no forced timeout.

### Q4: Should smart command support --skip-report flag?
**Status**: DEFERRED to v3.1.0
**Decision**: No skip flags in v3.0.0
**Rationale**: Workflow is smart (auto-skips when appropriate). Manual override in future.

### Q5: Should workflow decisions be logged (verbose mode)?
**Status**: RESOLVED - Yes (--verbose flag)
**Decision**: --verbose shows decision process
**Rationale**: Useful for debugging workflow, understanding file state checks.

### Q6: Should smart command support multiple deck names?
**Status**: RESOLVED - No
**Decision**: Single deck per invocation
**Rationale**: Simplifies implementation, clear workflow. Use shell loop for multiple decks.

---

## Design Decisions from Event Storming

1. **Workflow Decision Tree**:
   - **Decision**: Log → Render → Display (sequential checks)
   - **Rationale**: Log existence determines report, output status determines render, display always runs

2. **Report Skip on Stale Output**:
   - **Decision**: Skip report if render is needed
   - **Rationale**: Stale report is useless, confusing (timing data from old session)

3. **Blocking Prompt After Report**:
   - **Decision**: Wait for user Enter before display
   - **Rationale**: User should review report before presentation opens

4. **Second-Level Timestamp Precision**:
   - **Decision**: Truncate milliseconds, compare seconds
   - **Rationale**: File systems have varying timestamp precision, second-level is reliable

5. **Display Always Runs**:
   - **Decision**: Display phase always executes (if workflow succeeds)
   - **Rationale**: Primary use case is opening presentation

6. **Source File Required**:
   - **Decision**: Error if source file missing
   - **Rationale**: Smart command needs source to determine render status

7. **No Auto-Retry**:
   - **Decision**: Render failure halts workflow, no retry
   - **Rationale**: User must fix issue, retry would repeat same error

---

## Traceability Matrix

| Rule | BDD Scenarios | Event Storming Events | Acceptance Criteria |
|------|---------------|----------------------|---------------------|
| Rule 1 | smart-default-command.feature (2) | SmartWorkflowStarted | AC-1 |
| Rule 2 | smart-default-command.feature (3) | WorkflowDecisionsMade | AC-2 |
| Rule 3 | smart-default-command.feature (1) | RenderPhaseExecuted, DisplayPhaseExecuted | AC-3 |
| Rule 4 | smart-default-command.feature (2) | ReportPhaseExecuted, DisplayPhaseExecuted | AC-4 |
| Rule 5 | smart-default-command.feature (2) | RenderPhaseExecuted, ReportSkipped | AC-5 |
| Rule 6 | smart-default-command.feature (2) | TimestampsCompared | AC-6 |
| Rule 7 | smart-default-command.feature (1) | SourceFileNotFound | AC-7 |
| Rule 8 | smart-default-command.feature (2) | RenderPhaseFailed, WorkflowHalted | AC-8 |
| Rule 9 | smart-default-command.feature (2) | ConfigInherited | AC-9 |
| Rule 10 | smart-default-command.feature (2) | ReportPromptDisplayed | AC-10 |
| Rule 11 | smart-default-command.feature (2) | ReportSkipped | AC-11 |
| Rule 12 | smart-default-command.feature (4) | StatusMessageDisplayed | AC-12 |

**Total Coverage**: 25 BDD scenarios across 12 business rules

---

## Implementation Notes

### Domain Model Requirements
- `SmartWorkflow` aggregate with state (fileState, decisions, phases)
- `AnalyzeFileState` command checks log, output, source timestamps
- `MakeWorkflowDecisions` command determines shouldReport, shouldRender, shouldDisplay
- `ExecuteWorkflowPhase` command runs report/render/display
- `CompareTimestamps` value object for second-level precision
- `WorkflowError` enum for source not found, render failed

### Infrastructure Requirements
- File system checks (log exists, output exists, source exists)
- Timestamp comparison (second-level precision)
- Render command integration
- Display command integration
- Report command integration
- Blocking stdin reader (for Enter prompt)

### UI Requirements
- Console output for status messages
- Blocking prompt: "Press Enter to continue..."
- Render reason suffix: "(source modified)"
- Clear phase transitions in output

---

## Workflow Decision Logic

```scala
case class FileState(
  sourceModifiedTime: Option[Instant],
  outputModifiedTime: Option[Instant],
  logExists: Boolean
)

case class WorkflowDecisions(
  shouldReport: Boolean,
  shouldRender: Boolean,
  shouldDisplay: Boolean,
  renderReason: Option[RenderReason]
)

enum RenderReason:
  case OutputNotFound, OutputOutdated

def makeDecisions(fileState: FileState): WorkflowDecisions =
  val shouldReport = fileState.logExists

  val (shouldRender, renderReason) = fileState.outputModifiedTime match
    case None =>
      // Output doesn't exist
      (true, Some(RenderReason.OutputNotFound))
    case Some(outputTime) =>
      fileState.sourceModifiedTime match
        case Some(sourceTime) if sourceTime.truncatedTo(ChronoUnit.SECONDS)
          .isAfter(outputTime.truncatedTo(ChronoUnit.SECONDS)) =>
          // Source newer than output
          (true, Some(RenderReason.OutputOutdated))
        case _ =>
          // Output up-to-date
          (false, None)

  // Skip report if render is needed (output is stale)
  val finalShouldReport = shouldReport && !shouldRender

  WorkflowDecisions(
    shouldReport = finalShouldReport,
    shouldRender = shouldRender,
    shouldDisplay = true,  // Always display
    renderReason = renderReason
  )
```

---

## Workflow Paths Table

| Log Exists | Output Exists | Source Newer | Report | Render | Display | Notes |
|------------|---------------|--------------|--------|--------|---------|-------|
| No | No | n/a | No | Yes | Yes | First time: render + display |
| No | Yes | No | No | No | Yes | Display only (output up-to-date) |
| No | Yes | Yes | No | Yes | Yes | Re-render + display (source modified) |
| Yes | No | n/a | Yes | Yes | Yes | Report + render + display (output missing) |
| Yes | Yes | No | Yes | No | Yes | Report + display (output up-to-date) |
| Yes | Yes | Yes | No | Yes | Yes | Re-render + display (skip report, stale) |

---

## Exit Codes

| Scenario | Exit Code |
|----------|-----------|
| All phases successful | 0 |
| Source file not found | 1 |
| Render failed | 1 |
| Display failed (browser) | 2 |
| Log file parse error (during report) | 0 (non-fatal, workflow continues) |

---

## Verbose Mode Output Example

```
Analyzing workflow for: my-talk
Checking log file: my-talk/my-talk.log
Log file: FOUND
Decision: shouldReport = true

Checking output status: my-talk/index.html
Output exists: YES
Source modified time: 2025-12-29T14:00:00Z
Output modified time: 2025-12-29T14:00:00Z
Decision: shouldRender = false (up-to-date)

Workflow plan:
1. Display report
2. Skip render (output up-to-date)
3. Display presentation

Executing workflow...
```

---

**Example Mapping Complete**: 2025-12-29
**Next Step**: Acceptance Criteria Review
**Ready for Implementation**: Pending AC approval
