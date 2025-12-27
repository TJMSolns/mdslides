# Event Storming: US-017 Configuration Management CLI

**Date:** 2024-12-26
**User Story:** US-017 - Configuration Management CLI
**Related:** PDR-014, v0.4.0-decisions-summary.md

---

## User Story

**As a** MDSlides user
**I want** to configure defaults via CLI
**So that** I don't have to specify the same flags repeatedly

---

## Domain Events (Orange Stickies)

Events that occur in the system, past tense:

1. **ConfigFileLocated** - Configuration file found at expected path (project/user/system)
2. **ConfigFileRead** - Configuration file contents loaded from disk
3. **ConfigFileParsed** - Key-value pairs extracted from config file
4. **ConfigurationMerged** - Multiple config sources merged with precedence
5. **ConfigKeySet** - User set configuration value via CLI
6. **ConfigKeyUnset** - User removed configuration value
7. **ConfigKeyRetrieved** - User queried configuration value
8. **ConfigFileNotFound** - Configuration file missing (not an error, use defaults)
9. **ConfigParseError** - Invalid configuration file syntax
10. **ConfigValidationWarning** - Configuration value suspicious but allowed
11. **InteractiveConfigStarted** - User launched interactive configuration wizard
12. **InteractiveConfigCompleted** - User finished configuration wizard

---

## Commands (Blue Stickies)

Actions triggered by actors:

1. **LoadConfiguration** - Load and merge config from all sources (project/user/system)
2. **SetConfigValue** - Write key-value pair to config file
3. **UnsetConfigValue** - Remove key from config file
4. **GetConfigValue** - Retrieve single configuration value
5. **ListAllConfig** - Show all configuration with sources
6. **ValidateConfigValue** - Check if value is valid for key
7. **RunInteractiveWizard** - Launch interactive configuration flow
8. **CreateConfigFile** - Initialize new config file with defaults

---

## Aggregates (Yellow Stickies)

Domain entities that process commands and emit events:

### Config (new aggregate)
- **Responsibility:** Represents merged configuration settings
- **Properties:** themeDir, defaultTheme, copyImages, defaultOutputDir
- **Invariants:**
  - themeDir must be valid Path
  - defaultTheme is optional (None = use built-in default)
  - copyImages must be boolean
  - defaultOutputDir must be valid Path

### ConfigSource (new value object)
- **Responsibility:** Tracks where config value came from
- **Values:** Project, User, System, Default
- **Precedence:** Project > User > System > Default

### ConfigFile (infrastructure concept - not domain)
- **Responsibility:** File system representation of configuration
- **Properties:** path, entries (Map[String, String])
- **Note:** This is an infrastructure concern, not a domain aggregate

---

## Read Models (Green Stickies)

Data projections for queries:

1. **EffectiveConfiguration** - Merged config from all sources with precedence applied
2. **ConfigurationWithSources** - Each setting annotated with source (project/user/system/default)
3. **ConfigurationDiff** - Comparison of values across scopes

---

## Policies (Purple Stickies)

Business rules connecting events:

1. **When LoadConfiguration → Check All Scopes**
   - Read project config (./.mdslides/config)
   - Read user config (~/.mdslides/config)
   - Read system config (/usr/local/etc/mdslides/config)
   - Merge with precedence: project > user > system > defaults

2. **When ConfigFileNotFound → Use Defaults**
   - Missing config file is NOT an error
   - Continue with built-in defaults or lower-precedence configs

3. **When SetConfigValue → Validate First**
   - Check key is recognized
   - Check value is valid for key type
   - Write to specified scope (default: user)

4. **When ConfigParseError → Show Clear Error**
   - Display line number and parsing issue
   - Suggest fix (e.g., "Expected: key=value")
   - Don't silently ignore

5. **When InteractiveConfigStarted → Load Current Values**
   - Show current effective values
   - Show source of each value (project/user/default)
   - Allow user to keep or change

---

## External Systems (Pink Stickies)

External dependencies:

1. **File System** - Read/write config files at three locations
2. **YAML/Properties Parser** - Parse key=value format
3. **Theme System** - List available themes for interactive selection
4. **User Input** - Terminal input for interactive wizard

---

## Timeline (Left to Right Flow)

### Flow 1: Non-Interactive Set

```
User Command: mdslides config set theme-dir ~/.mdslides/themes

↓

[SetConfigValue Command]
  key: "theme-dir"
  value: "~/.mdslides/themes"
  scope: User (default)
  ↓
[ValidateConfigValue Command]
  key: "theme-dir" → recognized ✓
  value: "~/.mdslides/themes" → valid path ✓
  ↓
[ConfigKeySet Event]
  ↓
[Load Existing User Config]
  path: ~/.mdslides/config
  ↓
IF file exists:
  [ConfigFileRead Event]
  [ConfigFileParsed Event]
    existing entries: {default_theme: "minimal"}
    ↓
ELSE file missing:
  [ConfigFileNotFound Event]
    existing entries: {}
  ↓
[Merge New Value]
  entries: {default_theme: "minimal", theme_dir: "~/.mdslides/themes"}
  ↓
[Write Config File]
  path: ~/.mdslides/config
  content:
    default_theme=minimal
    theme_dir=~/.mdslides/themes
  ↓
[Success Message]
  ✓ Set theme_dir=~/.mdslides/themes (saved to ~/.mdslides/config)
```

### Flow 2: Non-Interactive Get

```
User Command: mdslides config get theme-dir

↓

[LoadConfiguration Command]
  ↓
[Check Project Config]
  path: ./.mdslides/config
  ↓
  [ConfigFileNotFound Event]  (no project config)
  ↓
[Check User Config]
  path: ~/.mdslides/config
  ↓
  [ConfigFileLocated Event]
  [ConfigFileRead Event]
  [ConfigFileParsed Event]
    entries: {theme_dir: "~/.mdslides/themes"}
  ↓
[Check System Config]
  path: /usr/local/etc/mdslides/config
  ↓
  [ConfigFileNotFound Event]  (no system config)
  ↓
[ConfigurationMerged Event]
  effectiveConfig: {theme_dir: "~/.mdslides/themes"} (from user)
  ↓
[GetConfigValue Command]
  key: "theme-dir"
  ↓
[ConfigKeyRetrieved Event]
  value: "~/.mdslides/themes"
  source: User
  ↓
[Output]
  ~/.mdslides/themes
```

### Flow 3: Interactive Wizard

```
User Command: mdslides config

↓

[InteractiveConfigStarted Event]
  ↓
[LoadConfiguration Command]
  (load current effective configuration)
  ↓
[Show Welcome]
  MDSlides Configuration
  ↓
[Prompt: Theme Directory]
  Where should themes be located?
  Current: ./themes (default)

  1. Keep current value (./themes)
  2. Use user themes (~/.mdslides/themes)
  3. Enter custom path
  4. Skip

  Choice [1-4]: 2
  ↓
[User selects option 2]
  ↓
[SetConfigValue Command]
  key: "theme-dir"
  value: "~/.mdslides/themes"
  scope: User
  ↓
[ConfigKeySet Event]
  ✓ Set theme_dir=~/.mdslides/themes (saved to ~/.mdslides/config)
  ↓
[Prompt: Default Theme]
  What is your default theme?
  Current: minimal (default)

  Available themes:
    - minimal
    - retisio
    - tjm-solutions

  1. Keep current value (minimal)
  2. Select from available themes
  3. Skip

  Choice [1-3]: 2
  ↓
[User selects option 2]
  ↓
[Show Theme Selection]
  Select theme:
    1. minimal
    2. retisio
    3. tjm-solutions

  Choice [1-3]: 2
  ↓
[User selects retisio]
  ↓
[SetConfigValue Command]
  key: "default-theme"
  value: "retisio"
  scope: User
  ↓
[ConfigKeySet Event]
  ✓ Set default_theme=retisio (saved to ~/.mdslides/config)
  ↓
[... more prompts ...]
  ↓
[InteractiveConfigCompleted Event]
  ↓
[Show Summary]
  Configuration complete!

  Summary:
    theme_dir=~/.mdslides/themes (user)
    default_theme=retisio (user)
    copy_images=true (default)

  View all settings: mdslides config list
```

### Flow 4: List All Config

```
User Command: mdslides config list

↓

[LoadConfiguration Command]
  ↓
[Check All Scopes]
  Project: {default_theme: "corporate"}
  User: {theme_dir: "~/.mdslides/themes"}
  System: (none)
  Defaults: {theme_dir: "./themes", copy_images: "true"}
  ↓
[ConfigurationMerged Event]
  effectiveConfig:
    theme_dir: "~/.mdslides/themes" (user)
    default_theme: "corporate" (project)
    copy_images: "true" (default)
  ↓
[Output]
  theme_dir=~/.mdslides/themes (user)
  default_theme=corporate (project)
  copy_images=true (default)
  default_output_dir=./output (default)
```

---

## Hotspots (Red Stickies)

Areas of complexity or risk:

1. **🔥 Path Expansion**
   - **Issue:** User enters "~/.mdslides/themes" - must expand tilde
   - **Risk:** Tilde not expanded → invalid path
   - **Mitigation:** Expand ~ to home directory when reading config
   - **Test:** Set theme-dir to ~/themes → expands correctly

2. **🔥 Config File Corruption**
   - **Issue:** User manually edits config file with invalid syntax
   - **Risk:** Parser fails, config becomes unusable
   - **Mitigation:** Clear parse error with line number, suggest fix
   - **Test:** Invalid config line → helpful error message

3. **🔥 Concurrent Modification**
   - **Issue:** Two CLI processes modify same config file simultaneously
   - **Risk:** Lost writes or corrupted file
   - **Mitigation:** Read-modify-write cycle (acceptable for CLI tool)
   - **Decision:** Not using file locking (YAGNI for single-user CLI)

4. **🔥 Configuration Precedence**
   - **Issue:** Three-tier precedence (project/user/system) must be correct
   - **Risk:** Wrong config value used (precedence bug)
   - **Mitigation:** Single canonical merge function with clear precedence
   - **Test:** Project value overrides user value, user overrides system, etc.

5. **🔥 Unknown Configuration Keys**
   - **Issue:** User sets key that doesn't exist (typo or future version)
   - **Risk:** Silent failure or confusing error
   - **Mitigation:** Warn about unknown keys, suggest valid keys
   - **Test:** `mdslides config set typo-key value` → error with suggestions

6. **🔥 Type Validation**
   - **Issue:** User sets boolean key to non-boolean value
   - **Risk:** Parse error at runtime
   - **Mitigation:** Validate at set time, show expected type
   - **Test:** `mdslides config set copy-images maybe` → error "Expected: true or false"

---

## Questions & Decisions

### Q1: Should we validate config values at set time or load time?
**A:** Set time (fail fast)

**Rationale:**
- Immediate feedback to user
- Prevents invalid config persisting to file
- Better UX than cryptic error later

**Example:**
```bash
$ mdslides config set copy-images maybe
Error: Invalid value for 'copy_images': maybe
Expected: true or false
```

### Q2: What happens if config file has unknown keys?
**A:** Warn but don't fail (forward compatibility)

**Rationale:**
- Config file may be from newer version
- Allow graceful degradation
- Don't break existing config

**Example:**
```
⚠ Warning: Unknown configuration key: future_feature
   This key will be ignored.
```

### Q3: Should interactive wizard modify user or project config?
**A:** User config (default scope)

**Rationale:**
- Most users want personal config
- Project config requires explicit opt-in (--scope project)
- Safer default (doesn't affect team)

### Q4: Should we support environment variables (e.g., MDSLIDES_THEME_DIR)?
**A:** Not in v0.4.0 (defer to v0.5.0+)

**Rationale:**
- YAGNI - no user request yet
- Config files sufficient for current use cases
- Can add later without breaking changes

**Precedence if added later:**
```
CLI flags > Env vars > Project config > User config > System config > Defaults
```

### Q5: How do we handle missing parent directories?
**A:** Create automatically when writing config

**Example:**
```bash
$ mdslides config set theme-dir ~/themes
# Creates ~/.mdslides/ directory if needed
✓ Set theme_dir=~/themes (saved to ~/.mdslides/config)
```

---

## Anti-Corruption Layer

**Infrastructure → Domain boundary:**

```scala
// Infrastructure: ConfigFile
object ConfigFile:
  def read(path: Path): IO[Map[String, String]] =
    if Files.exists(path) then
      IO(Files.readAllLines(path))
        .map(parseLines)
    else
      IO.pure(Map.empty)  // Missing file → empty map (not error)

  def write(path: Path, entries: Map[String, String]): IO[Unit] =
    val content = entries.map { case (k, v) => s"$k=$v" }.mkString("\n")
    IO(Files.createDirectories(path.getParent)) *>  // Create parent dirs
      IO(Files.writeString(path, content))

  private def parseLines(lines: java.util.List[String]): Map[String, String] =
    lines.asScala
      .map(_.trim)
      .filterNot(line => line.isEmpty || line.startsWith("#"))
      .flatMap { line =>
        line.split("=", 2) match
          case Array(key, value) => Some(key.trim -> value.trim)
          case _ => None  // Invalid line - skip silently
      }
      .toMap

// Domain: Config
case class Config(
  themeDir: Path,
  defaultTheme: Option[String],
  copyImages: Boolean,
  defaultOutputDir: Path
)

object Config:
  val defaults: Config = Config(
    themeDir = Paths.get("./themes"),
    defaultTheme = Some("minimal"),
    copyImages = true,
    defaultOutputDir = Paths.get("./output")
  )

  def load(): IO[Config] =
    for
      system  <- loadSystemConfig()
      user    <- loadUserConfig()
      project <- loadProjectConfig()
    yield merge(defaults, system, user, project)

  private def loadSystemConfig(): IO[Map[String, String]] =
    ConfigFile.read(Paths.get("/usr/local/etc/mdslides/config"))

  private def loadUserConfig(): IO[Map[String, String]] =
    val homeDir = System.getProperty("user.home")
    ConfigFile.read(Paths.get(homeDir, ".mdslides", "config"))

  private def loadProjectConfig(): IO[Map[String, String]] =
    ConfigFile.read(Paths.get(".mdslides", "config"))

  private def merge(
    defaults: Config,
    system: Map[String, String],
    user: Map[String, String],
    project: Map[String, String]
  ): Config =
    // Project overrides user overrides system overrides defaults
    val allEntries = system ++ user ++ project

    Config(
      themeDir = allEntries.get("theme_dir")
        .map(expandPath)
        .getOrElse(defaults.themeDir),

      defaultTheme = allEntries.get("default_theme")
        .orElse(defaults.defaultTheme),

      copyImages = allEntries.get("copy_images")
        .flatMap(_.toBooleanOption)
        .getOrElse(defaults.copyImages),

      defaultOutputDir = allEntries.get("default_output_dir")
        .map(expandPath)
        .getOrElse(defaults.defaultOutputDir)
    )

  private def expandPath(pathStr: String): Path =
    if pathStr.startsWith("~") then
      val homeDir = System.getProperty("user.home")
      Paths.get(homeDir, pathStr.drop(1))  // Remove ~
    else
      Paths.get(pathStr)
```

**Key insight:** Config aggregate is built from merged key-value maps. Configuration file format is purely an infrastructure concern.

---

## Acceptance Tests (BDD Scenarios)

### Scenario 1: Set configuration value (non-interactive)
```gherkin
Given no user configuration file exists
When I run "mdslides config set theme-dir ~/.mdslides/themes"
Then the file "~/.mdslides/config" should be created
And it should contain "theme_dir=~/.mdslides/themes"
And I should see "✓ Set theme_dir=~/.mdslides/themes (saved to ~/.mdslides/config)"
```

### Scenario 2: Get configuration value (non-interactive)
```gherkin
Given a user configuration file with "theme_dir=~/.mdslides/themes"
When I run "mdslides config get theme-dir"
Then I should see "~/.mdslides/themes"
And the exit code should be 0
```

### Scenario 3: Get non-existent key shows error
```gherkin
Given no configuration files exist
When I run "mdslides config get theme-dir"
Then I should see "Error: Configuration key not set: theme-dir"
And I should see "Using default value: ./themes"
And the exit code should be 1
```

### Scenario 4: List all configuration with sources
```gherkin
Given a user config with "theme_dir=~/.mdslides/themes"
And a project config with "default_theme=corporate"
When I run "mdslides config list"
Then I should see:
  """
  theme_dir=~/.mdslides/themes (user)
  default_theme=corporate (project)
  copy_images=true (default)
  default_output_dir=./output (default)
  """
```

### Scenario 5: Project config overrides user config
```gherkin
Given a user config with "default_theme=minimal"
And a project config with "default_theme=corporate"
When I load configuration
Then the effective default_theme should be "corporate"
```

### Scenario 6: Unset configuration value
```gherkin
Given a user config with "default_theme=retisio"
When I run "mdslides config unset default-theme"
Then the file "~/.mdslides/config" should NOT contain "default_theme"
And I should see "✓ Unset default_theme"
```

### Scenario 7: Set with specific scope
```gherkin
Given I am in a project directory
When I run "mdslides config set --scope project default-theme corporate"
Then the file "./.mdslides/config" should contain "default_theme=corporate"
And the user config should NOT be modified
```

### Scenario 8: Invalid configuration value rejected
```gherkin
When I run "mdslides config set copy-images maybe"
Then I should see:
  """
  Error: Invalid value for 'copy_images': maybe
  Expected: true or false
  """
And the exit code should be 1
And the config file should NOT be modified
```

### Scenario 9: Unknown configuration key rejected
```gherkin
When I run "mdslides config set unknown-key value"
Then I should see:
  """
  Error: Unknown configuration key: unknown-key

  Valid keys:
    - theme_dir
    - default_theme
    - copy_images
    - default_output_dir
  """
And the exit code should be 1
```

### Scenario 10: Interactive wizard sets multiple values
```gherkin
Given no configuration files exist
When I run "mdslides config" interactively
And I select option "2" for theme directory (user themes)
And I select option "2" for default theme (retisio)
And I complete the wizard
Then "~/.mdslides/config" should contain:
  """
  theme_dir=~/.mdslides/themes
  default_theme=retisio
  """
And I should see "Configuration complete!"
```

### Scenario 11: Path expansion works
```gherkin
When I run "mdslides config set theme-dir ~/themes"
Then the effective theme directory should be "/home/user/themes"
And not "~/themes" (literal tilde)
```

### Scenario 12: Missing config file is not an error
```gherkin
Given no configuration files exist
When I load configuration
Then the default values should be used
And no error should be shown
```

---

## Implementation Tasks (TDD Order)

### Phase 1: Infrastructure - Config File I/O
1. **Test:** Read existing config file returns key-value map
2. **Test:** Read non-existent config file returns empty map (no error)
3. **Test:** Write config file creates parent directories
4. **Test:** Parse config file ignores comments and blank lines
5. **Test:** Parse config file handles key=value format
6. **Test:** Invalid line in config file skipped silently

### Phase 2: Domain - Config Aggregate
7. **Test:** Config.defaults has built-in values
8. **Test:** Config.load() merges all sources with precedence
9. **Test:** Project config overrides user config
10. **Test:** User config overrides system config
11. **Test:** System config overrides defaults
12. **Test:** Path expansion converts ~ to home directory

### Phase 3: CLI - Non-Interactive Set
13. **Test:** `config set key value` writes to user config
14. **Test:** `config set --scope project key value` writes to project config
15. **Test:** `config set` validates key name
16. **Test:** `config set` validates value type
17. **Test:** `config set` with invalid key shows error
18. **Test:** `config set` with invalid value shows error

### Phase 4: CLI - Non-Interactive Get
19. **Test:** `config get key` returns value from effective config
20. **Test:** `config get` shows source (project/user/system/default)
21. **Test:** `config get` for unset key shows error
22. **Test:** `config get` for unknown key shows error

### Phase 5: CLI - List
23. **Test:** `config list` shows all settings
24. **Test:** `config list` shows source for each setting
25. **Test:** `config list --scope user` shows only user config
26. **Test:** `config list --scope project` shows only project config

### Phase 6: CLI - Unset
27. **Test:** `config unset key` removes key from user config
28. **Test:** `config unset --scope project key` removes from project config
29. **Test:** `config unset` for unset key is no-op (not error)

### Phase 7: Interactive Wizard
30. **Test:** `config` (no args) launches interactive wizard
31. **Test:** Interactive wizard shows current values
32. **Test:** Interactive wizard validates selections
33. **Test:** Interactive wizard writes to user config
34. **Test:** Interactive wizard shows summary at end

### Phase 8: Integration - Main CLI
35. **Test:** `render` command loads configuration
36. **Test:** CLI flags override configuration
37. **Test:** Configuration provides defaults when flags not specified

---

## Success Metrics

- ✅ 100% of configuration keys settable via CLI
- ✅ Three-tier precedence works correctly (0 bugs)
- ✅ Interactive wizard completable without consulting docs
- ✅ Non-interactive mode scriptable (exit codes, parseable output)
- ✅ Clear error messages for invalid configuration (95% self-service)
- ✅ Path expansion works on all platforms (Linux, macOS, Windows)

---

**Event Storming Complete**
**Next Step:** Create v0.4.0 ceremony document
