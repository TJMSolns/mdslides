# Example Mapping: Display Command

**Date**: 2025-12-29
**Feature**: Display Command (v3.0.0 - Feature 9 of 10)
**Participants**: Product Owner, Bench Developer, Architect
**Story**: As a presenter, I want to run `mdslides display <deck-name>` to open my rendered presentation in a browser with session logging enabled so I can deliver my talk and capture metrics for later analysis.

---

## Business Rules (Yellow Cards)

### Rule 1: Command Invocation (Deck Name Required)
**Statement**: Display command requires deck name argument. Invoked as `mdslides display <deck-name>`. No deck name → usage error.

**Examples** (Green Cards ✅):
- **Ex 1.1**: `mdslides display my-talk` → Opens my-talk/index.html in browser
- **Ex 1.2**: `mdslides display talks/conference` → Opens talks/conference/index.html
- **Ex 1.3**: `mdslides display` (no deck name) → Error: "Usage: mdslides display <deck-name> [--browser <name>]"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 1.4**: Display works without deck name → Wrong, deck name required
- ❌ **Ex 1.5**: Display lists available presentations → Wrong, requires specific deck

**BDD Traceability**:
- `display-command.feature`: "Successful display command invocation"
- `display-command.feature`: "Display command requires deck name argument"

**Usage**: `java -jar ../mdslides.jar display <deck-name>`

---

### Rule 2: Presentation Not Rendered (Error)
**Statement**: If index.html does not exist, display actionable error suggesting render or smart command.

**Examples** (Green Cards ✅):
- **Ex 2.1**: `my-talk/index.html` missing → Error with suggestion to run render
- **Ex 2.2**: Output directory exists but index.html missing → Error: "Presentation not rendered"
- **Ex 2.3**: Output directory does not exist → Error: "Output directory not found"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 2.4**: Missing index.html opens blank browser → Wrong, must error
- ❌ **Ex 2.5**: No actionable suggestion in error → Wrong, suggest render command

**BDD Traceability**:
- `display-command.feature`: "Error when index.html doesn't exist"
- `display-command.feature`: "Error when output directory doesn't exist"

**Error Message**:
```
✗ Presentation not rendered: my-talk

  Expected file: my-talk/index.html (NOT FOUND)

  To render the presentation first, run:
    java -jar ../mdslides.jar render my-talk

  Or use the smart default command to auto-render:
    java -jar ../mdslides.jar my-talk
```

---

### Rule 3: Log File Initialization
**Statement**: Log file created at `<output-dir>/<deck-name>.log` with session metadata (sessionId, presentationName, startTime, theme, totalSlides).

**Examples** (Green Cards ✅):
- **Ex 3.1**: Display at 14:23:45 → Log created with startTime "2025-12-29T14:23:45Z"
- **Ex 3.2**: Session ID is UUID v4 → sessionId: "a3f2e9d7-4b5c-6a1d-8e9f-0a1b2c3d4e5f"
- **Ex 3.3**: 42 slides → totalSlides: 42
- **Ex 3.4**: Dark theme → theme: "dark"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 3.5**: Log not created → Wrong, always attempt creation
- ❌ **Ex 3.6**: Session ID is timestamp → Wrong, UUID v4 required

**BDD Traceability**:
- `display-command.feature`: "Log file created and initialized on display"

**Log Metadata**:
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

### Rule 4: Log File Overwrites Previous Session
**Statement**: Existing log file truncated and replaced with new session. Old session data is lost.

**Examples** (Green Cards ✅):
- **Ex 4.1**: Log exists from yesterday → Truncated, new session starts
- **Ex 4.2**: Run display twice → Second invocation overwrites first
- **Ex 4.3**: Log has 100 entries → Replaced with new session metadata

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 4.4**: New session appends to log → Wrong, file truncated
- ❌ **Ex 4.5**: Old session preserved → Wrong, overwrite is intentional

**BDD Traceability**:
- `display-command.feature`: "Log file overwrites previous session"

**Design Decision**: Session-scoped logging (single session per log file).

---

### Rule 5: Log File Creation Failure (Non-Fatal)
**Statement**: If log cannot be created (permissions, disk full), display warning but continue. Browser opens, exit code 0.

**Examples** (Green Cards ✅):
- **Ex 5.1**: Output directory read-only → Warning, browser opens, exit 0
- **Ex 5.2**: Disk full → Warning, presentation opens without logging
- **Ex 5.3**: Permission denied → Warning message with troubleshooting

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 5.4**: Log failure halts display → Wrong, non-fatal (presentation must work)
- ❌ **Ex 5.5**: Exit code 1 on log failure → Wrong, exit code 0 (warning only)

**BDD Traceability**:
- `display-command.feature`: "Warning when log file cannot be created"
- `display-command.feature`: "Disk full during log initialization"

**Warning Message**:
```
⚠ Warning: Unable to create log file: my-talk/my-talk.log
  Reason: Permission denied

  Presentation will open without session logging.
  To enable logging, fix file permissions or run with appropriate privileges.
```

---

### Rule 6: Browser Resolution (4-Layer Precedence)
**Statement**: Browser resolved via CLI arg > project config > global config > system default. CLI overrides all.

**Examples** (Green Cards ✅):
- **Ex 6.1**: CLI `--browser firefox`, project has chromium → Firefox launched (CLI wins)
- **Ex 6.2**: No CLI, project has chromium, global has brave → Chromium launched (project wins)
- **Ex 6.3**: No CLI/project, global has brave → Brave launched (global wins)
- **Ex 6.4**: No config at all, Linux → xdg-open used (system default)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 6.5**: Project config overrides CLI → Wrong, CLI highest precedence
- ❌ **Ex 6.6**: No fallback to system default → Wrong, must have default

**BDD Traceability**:
- `display-command.feature`: "Browser from CLI argument (highest precedence)"
- `display-command.feature`: "Browser from project config"
- `display-command.feature`: "Browser from global config"
- `display-command.feature`: "System default browser (lowest precedence)"

**Precedence**: CLI > project > global > system default

---

### Rule 7: Supported Browsers
**Statement**: Supported browsers: default, firefox, chromium, google-chrome, brave. Also supports custom paths.

**Examples** (Green Cards ✅):
- **Ex 7.1**: `--browser firefox` → Browser command: "firefox"
- **Ex 7.2**: `--browser chromium` → Browser command: "chromium"
- **Ex 7.3**: `--browser /usr/bin/custom-browser` → Custom path used
- **Ex 7.4**: `--browser default` on Linux → Browser command: "xdg-open"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 7.5**: `--browser safari` not found → Error: "Failed to launch browser: safari"
- ❌ **Ex 7.6**: Invalid browser name → Error with suggestions

**BDD Traceability**:
- `display-command.feature`: "Launching different browsers"
- `display-command.feature`: "Custom browser path"

**Supported Values**: default, firefox, chromium, google-chrome, brave, <absolute-path>

---

### Rule 8: Browser Not Found (Error)
**Statement**: If browser command not found in PATH, display error with troubleshooting steps and alternatives.

**Examples** (Green Cards ✅):
- **Ex 8.1**: Safari not in PATH → Error: "Failed to launch browser: safari"
- **Ex 8.2**: Error suggests alternatives: firefox, chromium, google-chrome, brave
- **Ex 8.3**: Exit code 2 (browser launch failure)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 8.4**: Browser not found, command succeeds → Wrong, must error
- ❌ **Ex 8.5**: Generic error with no suggestions → Wrong, actionable troubleshooting required

**BDD Traceability**:
- `display-command.feature`: "Configured browser is not installed"

**Error Message**:
```
✗ Failed to launch browser: safari

  Error: Command not found: safari

  Troubleshooting:
  1. Check if Safari is installed: which safari
  2. Specify a different browser: --browser chromium
  3. Use system default: --browser default

  Available browsers to try:
  - firefox, chromium, google-chrome, brave
  - Or provide full path: --browser /usr/bin/custom-browser
```

---

### Rule 9: Absolute File URLs
**Statement**: Browser launched with absolute file URL: `file:///absolute/path/to/output/index.html`.

**Examples** (Green Cards ✅):
- **Ex 9.1**: Output at `/home/user/mdslides/my-talk/` → URL: "file:///home/user/mdslides/my-talk/index.html"
- **Ex 9.2**: Windows `C:\Users\user\mdslides\my-talk\` → URL: "file:///C:/Users/user/mdslides/my-talk/index.html"
- **Ex 9.3**: URL is absolute (not relative)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 9.4**: Relative URL `my-talk/index.html` → Wrong, must be absolute
- ❌ **Ex 9.5**: Windows backslashes not converted → Wrong, use forward slashes

**BDD Traceability**:
- `display-command.feature`: "Browser launched with absolute file URL"
- `display-command.feature`: "File URL on Windows"

**URL Format**: `file:///<absolute-path>/index.html` (forward slashes, even on Windows)

---

### Rule 10: Non-Blocking Browser Launch
**Statement**: Browser process starts in background, command returns to shell immediately (non-blocking).

**Examples** (Green Cards ✅):
- **Ex 10.1**: Browser starts, command exits immediately
- **Ex 10.2**: Terminal available for new commands
- **Ex 10.3**: Closing terminal does not close browser (browser independent)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 10.4**: Command blocks until browser closes → Wrong, non-blocking
- ❌ **Ex 10.5**: Browser process attached to terminal → Wrong, detached process

**BDD Traceability**:
- `display-command.feature`: "Command returns to shell after launching browser"
- `display-command.feature`: "Browser process runs independently"

**Implementation**: Launch browser in separate process, do not wait for exit.

---

### Rule 11: Success Message
**Statement**: Display success message with browser name and file URL.

**Examples** (Green Cards ✅):
- **Ex 11.1**: Firefox → "✓ Opened presentation in Firefox"
- **Ex 11.2**: System default → "✓ Opened presentation in system default browser"
- **Ex 11.3**: URL displayed: "  URL: file:///home/user/mdslides/my-talk/index.html"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 11.4**: No success message → Wrong, user needs confirmation
- ❌ **Ex 11.5**: URL not displayed → Wrong, useful for debugging

**BDD Traceability**:
- `display-command.feature`: "Success message displayed"

**Message Format**:
```
✓ Opened presentation in Firefox
  URL: file:///home/user/mdslides/my-talk/index.html
```

---

### Rule 12: Multiple Display Invocations
**Statement**: Running display command multiple times opens multiple browser tabs/windows. Each overwrites log file.

**Examples** (Green Cards ✅):
- **Ex 12.1**: Run display twice → Two browser windows/tabs open
- **Ex 12.2**: Second invocation overwrites log → Only most recent session logged
- **Ex 12.3**: Both browser windows function independently

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 12.4**: Second invocation fails → Wrong, should succeed
- ❌ **Ex 12.5**: Second log appends to first → Wrong, overwrites

**BDD Traceability**:
- `display-command.feature`: "Running display command multiple times"

**Behavior**: Each invocation is independent, log is session-scoped.

---

## Questions (Pink Cards)

### Q1: Should display support custom browser arguments?
**Status**: DEFERRED to v3.1.0
**Decision**: No custom args for v3.0.0
**Rationale**: Browser launches in default mode. Custom args (--private-window, etc.) in future.

### Q2: Should display support browser profiles?
**Status**: DEFERRED to v3.1.0
**Decision**: Default profile only
**Rationale**: Simplifies implementation, most users use default profile.

### Q3: Should browser launch be optional (e.g., --no-launch)?
**Status**: DEFERRED to v3.1.0
**Decision**: Always launch browser
**Rationale**: Display command purpose is to launch. Future: --no-launch for log init only.

### Q4: Should display support environment variable MDSLIDES_BROWSER?
**Status**: DEFERRED to v3.1.0
**Decision**: Not in v3.0.0
**Rationale**: Config layers sufficient. Environment variable in future if requested.

### Q5: Should display check if browser is already running?
**Status**: RESOLVED - No
**Decision**: Always launch new window/tab, no process check
**Rationale**: Browser handles multiple instances. Checking process is complex, not reliable.

### Q6: Should display wait for browser to be ready?
**Status**: RESOLVED - No
**Decision**: Launch and exit immediately (non-blocking)
**Rationale**: Browser startup is browser's responsibility. Command completes quickly.

---

## Design Decisions from Event Storming

1. **Log Creation on Display Only**:
   - **Decision**: Log file only via display command (not direct HTML open)
   - **Rationale**: Requires user intent, prevents accidental logging

2. **Non-Fatal Log Failure**:
   - **Decision**: Warning on log failure, presentation continues
   - **Rationale**: Presentation delivery is primary, logging is secondary

3. **4-Layer Browser Precedence**:
   - **Decision**: CLI > project > global > system default
   - **Rationale**: Consistent with render config precedence

4. **Absolute File URLs**:
   - **Decision**: file:/// with absolute path
   - **Rationale**: Works on all platforms, no relative path issues

5. **Non-Blocking Launch**:
   - **Decision**: Detached browser process, command exits immediately
   - **Rationale**: User can continue using terminal

6. **System Default Browsers**:
   - **Decision**: xdg-open (Linux), open (macOS), cmd /c start (Windows)
   - **Rationale**: Platform-specific, respects user's default browser

7. **Exit Code for Browser Failure**:
   - **Decision**: Exit code 2 (browser launch failure)
   - **Rationale**: Distinguish from other errors (1 = not rendered)

---

## Traceability Matrix

| Rule | BDD Scenarios | Event Storming Events | Acceptance Criteria |
|------|---------------|----------------------|---------------------|
| Rule 1 | display-command.feature (2) | DisplayCommandInvoked | AC-1 |
| Rule 2 | display-command.feature (2) | PresentationNotRendered | AC-2 |
| Rule 3 | display-command.feature (1) | LogFileInitialized | AC-3 |
| Rule 4 | display-command.feature (1) | LogFileOverwritten | AC-4 |
| Rule 5 | display-command.feature (2) | LogCreationFailed | AC-5 |
| Rule 6 | display-command.feature (4) | BrowserResolved | AC-6 |
| Rule 7 | display-command.feature (2) | BrowserLaunched | AC-7 |
| Rule 8 | display-command.feature (1) | BrowserNotFound | AC-8 |
| Rule 9 | display-command.feature (2) | BrowserLaunched | AC-9 |
| Rule 10 | display-command.feature (2) | BrowserLaunched | AC-10 |
| Rule 11 | display-command.feature (1) | DisplaySuccessful | AC-11 |
| Rule 12 | display-command.feature (1) | DisplayCommandInvoked | AC-12 |

**Total Coverage**: 21 BDD scenarios across 12 business rules

---

## Implementation Notes

### Domain Model Requirements
- `DisplaySession` aggregate with state (deckName, outputPath, browserCommand, logPath)
- `InitializeLogFile` command creates log with session metadata
- `ResolveBrowser` command applies 4-layer precedence
- `LaunchBrowser` command starts browser process with file URL
- `ValidatePresentationRendered` command checks index.html existence
- `DisplayError` enum for not rendered, browser not found, log creation failed

### Infrastructure Requirements
- File path resolver (output directory, log file location)
- Browser launcher (platform-specific: xdg-open, open, cmd /c start)
- Process spawner (detached browser process)
- JSON log file writer
- UUID generator (sessionId)
- Absolute path resolver (file URL conversion)

### UI Requirements
- No UI (CLI only)
- Console output for success/error messages
- Exit codes: 0 (success), 1 (not rendered), 2 (browser launch failed)

---

## Browser Launch Commands

| Platform | Default Command | Notes |
|----------|----------------|-------|
| Linux | xdg-open | Respects user's default browser |
| macOS | open | Respects user's default browser |
| Windows | cmd /c start | Respects user's default browser |

| Named Browser | Command |
|---------------|---------|
| firefox | firefox |
| chromium | chromium |
| google-chrome | google-chrome |
| brave | brave |

**Custom Path**: Absolute path to browser executable (e.g., /usr/bin/custom-browser)

---

## Exit Codes

| Scenario | Exit Code |
|----------|-----------|
| Presentation opened successfully | 0 |
| Presentation not rendered (index.html missing) | 1 |
| Browser launch failed (browser not found) | 2 |
| Log creation failed (permission denied) | 0 (non-fatal warning) |

---

## Cross-Platform File URL Examples

### Linux
```
Output: /home/user/projects/mdslides/my-talk/
URL:    file:///home/user/projects/mdslides/my-talk/index.html
```

### macOS
```
Output: /Users/user/projects/mdslides/my-talk/
URL:    file:///Users/user/projects/mdslides/my-talk/index.html
```

### Windows
```
Output: C:\Users\user\mdslides\my-talk\
URL:    file:///C:/Users/user/mdslides/my-talk/index.html
```

**Note**: Windows backslashes converted to forward slashes in URL.

---

**Example Mapping Complete**: 2025-12-29
**Next Step**: Acceptance Criteria Review
**Ready for Implementation**: Pending AC approval
