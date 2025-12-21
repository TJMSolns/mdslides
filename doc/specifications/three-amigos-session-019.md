# Three Amigos Session #019
## Generate Slides via CLI

---

```yaml
# MACHINE-READABLE METADATA
session:
  id: 3A-019-CLI-INTERFACE
  date: 2024-12-20
  user_story_id: US-019
  participants:
    - Tony Moores (Business/Dev/QA)
  duration_minutes: 45-60
  status: complete

story:
  title: Generate Slides via CLI
  type: Feature
  priority: P0 (Blocker)
  epic: CLI Interface
```

---

## 📋 User Story

**As a** slide deck author
**I want to** run `mdslides input.md output.html`
**So that** I can generate slides from the command line

**Business Value**: No CLI = no product. Essential user-facing interface for v1.0.

**Technical Scope**: Command-line interface with argument parsing, file I/O, error reporting, and exit codes

---

## 🎭 Three Perspectives

### 👔 Business Perspective (Product Owner)

**What success looks like**:
- Author runs `mdslides slides.md` → HTML generated
- Clear error messages if validation fails
- Success message with output filename
- Proper exit codes (0 = success, 1 = error)

**CLI Syntax**:
```bash
mdslides <input.md> [output.html] [options]

Options:
  --theme <name>       Specify theme (default: default)
  --validate-only      Run validation without rendering
  --help               Show help message
  --version            Show version
```

**Use Cases**:
- `mdslides slides.md` → generates `slides.html`
- `mdslides slides.md deck.html` → generates `deck.html`
- `mdslides slides.md --theme dark` → uses dark theme
- `mdslides slides.md --validate-only` → validates, no output file
- `mdslides --help` → shows help
- `mdslides --version` → shows version

**Acceptance criteria**:
- ✅ Parse input filename (required)
- ✅ Parse output filename (optional, default: replace .md with .html)
- ✅ Parse --theme option (optional, default: "default")
- ✅ Parse --validate-only flag
- ✅ Parse --help flag
- ✅ Parse --version flag
- ✅ Read input markdown file
- ✅ Parse → Validate → Render pipeline
- ✅ Write output HTML file
- ✅ Print success message
- ✅ Print validation errors (if any)
- ✅ Exit code 0 on success, 1 on error

---

### 💻 Development Perspective (Technical)

**Implementation approach**:
1. **Technology**: Decline library for argument parsing
   ```scala
   import com.monovore.decline._

   val inputFile = Argument[String](metavar = "input.md")
   val outputFile = Argument[String](metavar = "output.html").orNone
   val theme = Opts.option[String]("theme", help = "Theme name").withDefault("default")
   val validateOnly = Opts.flag("validate-only", help = "Validate without rendering").orFalse
   val version = Opts.flag("version", help = "Show version").orFalse
   val help = Opts.flag("help", help = "Show help").orFalse

   val opts = (inputFile, outputFile, theme, validateOnly, version, help).tupled
   ```

2. **Main Pipeline**:
   ```scala
   def main(args: Array[String]): Unit = {
     Command("mdslides", "Markdown to Slides converter")(opts).parse(args) match {
       case Left(help) =>
         System.err.println(help)
         sys.exit(1)
       case Right((input, output, theme, validateOnly, showVersion, showHelp)) =>
         if (showVersion) {
           println("mdslides v1.0.0")
           sys.exit(0)
         }
         if (showHelp) {
           println(helpText)
           sys.exit(0)
         }

         val result = for {
           markdown <- readFile(input)
           deck <- parseSlideDeck(markdown)
           _ <- validateSlideDeck(deck)
           theme <- loadTheme(theme)
           html <- renderHTML(deck, theme)
           _ <- if (!validateOnly) writeFile(outputFile.getOrElse(deriveOutput(input)), html) else Right(())
         } yield outputFile.getOrElse(deriveOutput(input))

         result match {
           case Right(filename) =>
             if (!validateOnly) println(s"✓ Generated $filename")
             else println("✓ Validation passed")
             sys.exit(0)
           case Left(errors) =>
             System.err.println(formatErrors(errors))
             sys.exit(1)
         }
     }
   }
   ```

3. **Error Formatting**:
   ```scala
   def formatErrors(errors: NonEmptyList[ValidationError]): String = {
     val header = s"Validation failed with ${errors.size} error(s):\n"
     val formatted = errors.toList.groupBy(_.slideId).map { case (slideId, errs) =>
       s"Slide $slideId:\n" + errs.map(e => s"  - ${e.message}").mkString("\n")
     }.mkString("\n\n")
     header + formatted
   }
   ```

4. **Output Filename Derivation**:
   ```scala
   def deriveOutput(input: String): String = {
     input.replaceAll("\\.md$", ".html")
   }
   ```

**Technical risks**:
- **File I/O errors**: Handle missing files, permissions gracefully
- **Large files**: Memory usage for 200-slide decks
  - **Mitigation**: Stream processing (deferred to v1.1)
- **Error message clarity**: Users must understand what went wrong

**Dependencies**:
- Decline library (CLI parsing)
- Cats Effect (IO)
- Entire pipeline (parse, validate, theme, render)

---

### 🧪 Testing Perspective (Quality/Edge Cases)

**Happy path scenarios**:
1. `mdslides slides.md` → generates `slides.html`, exit 0
2. `mdslides slides.md deck.html` → generates `deck.html`, exit 0
3. `mdslides slides.md --theme dark` → dark theme applied, exit 0
4. `mdslides slides.md --validate-only` → no output file, exit 0
5. `mdslides --version` → prints version, exit 0
6. `mdslides --help` → prints help, exit 0

**Edge cases (errors)**:
1. `mdslides missing.md` → file not found error, exit 1
2. `mdslides invalid.md` → validation errors printed, exit 1
3. `mdslides slides.md --theme nonexistent` → theme not found, exit 1
4. `mdslides` (no args) → help message, exit 1
5. `mdslides slides.md /readonly/output.html` → write permission error, exit 1

**Boundary cases**:
1. Input file exactly 200 slides → renders successfully
2. Input file is empty → validation error (0 slides)
3. Output filename with spaces → handled correctly (if quoted)

**Non-functional requirements**:
- CLI parsing < 10ms
- Clear error messages (actionable)
- Help text < 50 lines (readable)

---

## 🗂️ Example Mapping

### Rule 1: Parse required input filename

**Examples**:
- ✅ `mdslides slides.md` → input = "slides.md"
- ❌ `mdslides` → error: missing input file

**Questions**:
- Q1: Support stdin input?
  - **Decision**: Not in v1.0. Stdin support deferred to v1.1.

---

### Rule 2: Parse optional output filename

**Examples**:
- ✅ `mdslides slides.md` → output = "slides.html" (derived)
- ✅ `mdslides slides.md deck.html` → output = "deck.html"

**Questions**:
- Q2: How to derive output filename?
  - **Decision**: Replace `.md` extension with `.html`. If no `.md`, append `.html`.

---

### Rule 3: Parse --theme option

**Examples**:
- ✅ `mdslides slides.md` → theme = "default" (default)
- ✅ `mdslides slides.md --theme dark` → theme = "dark"

**Questions**:
- Q3: Theme validation in CLI or pipeline?
  - **Decision**: Pipeline (theme loading). CLI just parses string.

---

### Rule 4: Parse --validate-only flag

**Examples**:
- ✅ `mdslides slides.md --validate-only` → validateOnly = true, no output file

**Questions**:
- Q4: Print validation success message?
  - **Decision**: Yes. Print "✓ Validation passed" on success.

---

### Rule 5: Exit code 0 on success, 1 on error

**Examples**:
- ✅ Success → exit 0
- ❌ Validation error → exit 1
- ❌ File not found → exit 1

**Questions**:
- Q5: Different exit codes for different errors?
  - **Decision**: No for v1.0. All errors exit 1.

---

### Rule 6: Print clear error messages

**Examples**:
- ❌ File not found → "Error: File 'slides.md' not found"
- ❌ Validation errors → formatted error list

**Questions**:
- Q6: Color-coded errors (red text)?
  - **Decision**: Not in v1.0. Plain text only.

---

## 📝 Concrete Examples (Given/When/Then)

### Example 1: Basic Usage (Generate HTML)

```gherkin
Feature: CLI Interface

  Scenario: Generate HTML from markdown
    Given I have a file slides.md with valid markdown
    When I run `mdslides slides.md`
    Then HTML file slides.html is created
    And stdout shows "✓ Generated slides.html"
    And exit code is 0
```

---

### Example 2: Custom Output Filename

```gherkin
  Scenario: Specify custom output filename
    Given I have a file slides.md
    When I run `mdslides slides.md deck.html`
    Then HTML file deck.html is created
    And stdout shows "✓ Generated deck.html"
    And exit code is 0
```

---

### Example 3: Custom Theme

```gherkin
  Scenario: Use custom theme
    Given I have a file slides.md
    And I have a theme file themes/dark.json
    When I run `mdslides slides.md --theme dark`
    Then HTML file slides.html is created with dark theme
    And exit code is 0
```

---

### Example 4: Validate Only

```gherkin
  Scenario: Validate without rendering
    Given I have a file slides.md with valid markdown
    When I run `mdslides slides.md --validate-only`
    Then no HTML file is created
    And stdout shows "✓ Validation passed"
    And exit code is 0
```

---

### Example 5: Input File Not Found ❌

```gherkin
  Scenario: Input file does not exist
    When I run `mdslides missing.md`
    Then stderr shows "Error: File 'missing.md' not found"
    And exit code is 1
```

---

### Example 6: Validation Errors ❌

```gherkin
  Scenario: Markdown has validation errors
    Given I have a file invalid.md with validation errors
    When I run `mdslides invalid.md`
    Then stderr shows formatted validation errors
    And exit code is 1
    And no HTML file is created
```

---

### Example 7: Show Version

```gherkin
  Scenario: Show version
    When I run `mdslides --version`
    Then stdout shows "mdslides v1.0.0"
    And exit code is 0
```

---

### Example 8: Show Help

```gherkin
  Scenario: Show help
    When I run `mdslides --help`
    Then stdout shows usage and options
    And exit code is 0
```

---

## 🚧 Open Questions

| ID | Question | Status | Decision |
|----|----------|--------|----------|
| Q1 | Support stdin input? | ✅ Resolved | Not in v1.0 (deferred to v1.1) |
| Q2 | Derive output filename? | ✅ Resolved | Replace `.md` with `.html` |
| Q3 | Theme validation in CLI? | ✅ Resolved | No, in pipeline (theme loading) |
| Q4 | Print validation success? | ✅ Resolved | Yes, "✓ Validation passed" |
| Q5 | Different exit codes? | ✅ Resolved | No for v1.0 (all errors exit 1) |
| Q6 | Color-coded errors? | ✅ Resolved | Not in v1.0 (plain text) |

---

## ✅ Acceptance Criteria (Definition of Done)

### Functional Criteria

1. ✅ **AC1**: Parse input filename (required)
2. ✅ **AC2**: Parse output filename (optional)
3. ✅ **AC3**: Parse --theme option
4. ✅ **AC4**: Parse --validate-only flag
5. ✅ **AC5**: Parse --help flag
6. ✅ **AC6**: Parse --version flag
7. ✅ **AC7**: Read input markdown file
8. ✅ **AC8**: Run parse → validate → render pipeline
9. ✅ **AC9**: Write output HTML file
10. ✅ **AC10**: Print success message
11. ✅ **AC11**: Print validation errors
12. ✅ **AC12**: Exit code 0 on success, 1 on error

### Technical Criteria

13. ✅ **AC13**: Use Decline for CLI parsing
14. ✅ **AC14**: Clear error messages
15. ✅ **AC15**: Help text comprehensive
16. ✅ **AC16**: All domain terms used

### Non-Functional Criteria

17. ✅ **AC17**: CLI parsing < 10ms
18. ✅ **AC18**: Help text < 50 lines

**Scenarios**: 8 concrete examples documented
- 5 success paths
- 3 error paths

**Dependencies**:
- Decline library
- Cats Effect (IO)
- Entire pipeline

---

## 📚 Related Artifacts

- **User Story Tracker**: [BACKLOG-V3.md](../../BACKLOG-V3.md)
- **Related Stories**: US-016 HTML Rendering (entire pipeline)

---

## 🎯 Next Steps

1. **Create Example Mapping visual** (Ceremony 2.2)
2. **Document formal acceptance criteria** in BACKLOG-V3.md
3. **FINAL CEREMONY** for v1.0 MVP!

---

**Session Type**: Ceremony 2.1 - Three Amigos Session
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Next Review**: After Example Mapping Workshop (Ceremony 2.2)
