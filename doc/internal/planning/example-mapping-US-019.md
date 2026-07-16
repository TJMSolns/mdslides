# Example Mapping: US-019 (Improved CLI UX)

**Date:** 2024-12-26
**User Story:** US-019 - Improved CLI UX
**Prerequisites:** Event Storming + Three Amigos complete
**Purpose:** Concrete examples for TDD implementation

---

## Story Rules (from Three Amigos)

1. **Simple form:** `mdslides render DECK_NAME` infers input/output
2. **Explicit form:** `mdslides render -i INPUT -o OUTPUT` uses specified paths
3. **Input resolution:** Try `.md` then `.markdown`
4. **Output structure:** Always directory with `index.html`
5. **Relative paths:** All assets use paths relative to `index.html`
6. **Error handling:** Clear messages with usage examples

---

## Rule 1: Simple Form - Happy Path

### Example 1.1: Basic render
```bash
$ ls
my-preso.md

$ mdslides render my-preso
```

**Expected Output:**
```
Reading markdown from: my-preso.md
Parsing markdown...
✓ Parsed 5 slide(s)
Validating slide deck...
✓ Validation passed
Rendering HTML with theme: light
✓ Generated 12KB HTML
Writing to: my-preso/index.html
✓ Successfully created presentation: my-preso/
```

**Expected File Structure:**
```
my-preso/
└── index.html
```

**Test Assertions:**
- ✓ `my-preso.md` read successfully
- ✓ `my-preso/` directory created
- ✓ `my-preso/index.html` exists
- ✓ HTML contains relative paths (no absolute paths)
- ✓ Exit code 0

---

### Example 1.2: Render with theme
```bash
$ mdslides render my-preso --theme retisio
```

**Expected Output:**
```
Loading theme: retisio
✓ Loaded theme: the prior organization v1.0.0
Reading markdown from: my-preso.md
...
Copying 5 theme backgrounds to my-preso/backgrounds/
✓ Successfully created presentation: my-preso/
```

**Expected File Structure:**
```
my-preso/
├── index.html
└── backgrounds/
    ├── retisio-title-page.png
    ├── retisio-content-page.png
    ├── retisio-diagram-page.png
    ├── retisio-section-title-page.png
    └── retisio-end-page.png
```

**Test Assertions:**
- ✓ Theme backgrounds copied to `my-preso/backgrounds/`
- ✓ HTML uses relative paths: `url(backgrounds/retisio-title-page.png)`
- ✓ 5 PNG files in backgrounds directory

---

### Example 1.3: Render with content images
```bash
$ cat my-preso.md
---
template: content
---

## Architecture

![System Diagram](images/architecture.svg)

$ mdslides render my-preso
```

**Expected File Structure:**
```
my-preso/
├── index.html
└── images/
    └── architecture.svg
```

**Test Assertions:**
- ✓ `images/architecture.svg` copied from source to `my-preso/images/`
- ✓ HTML contains: `<img src="images/architecture.svg">`
- ✓ NOT: `<img src="/tmp/images/architecture.svg">` (absolute path)

---

### Example 1.4: Portability test (output directory moved)
```bash
$ mdslides render my-preso --theme retisio
$ mv my-preso /tmp/different-location/
$ open /tmp/different-location/my-preso/index.html
```

**Expected Result:**
- ✓ All images load correctly (backgrounds, content images)
- ✓ Theme styles applied
- ✓ No broken asset links

**Test Method:**
- Generate presentation
- Move output directory
- Parse HTML, verify all `src` and `url()` paths are relative
- Verify asset files exist at relative paths

---

## Rule 2: Explicit Form - Full Control

### Example 2.1: Custom input/output names
```bash
$ ls
slides.txt  # Markdown file with unusual extension

$ mdslides render -i slides.txt -o presentation
```

**Expected Output:**
```
Reading markdown from: slides.txt
...
Writing to: presentation/index.html
✓ Successfully created presentation: presentation/
```

**Expected File Structure:**
```
presentation/
└── index.html
```

**Test Assertions:**
- ✓ `slides.txt` used as input (not `slides.md`)
- ✓ `presentation/` directory created (not `slides/`)
- ✓ Exit code 0

---

### Example 2.2: Nested output directory
```bash
$ mdslides render -i slides.md -o dist/presentations/my-talk
```

**Expected File Structure:**
```
dist/
└── presentations/
    └── my-talk/
        └── index.html
```

**Test Assertions:**
- ✓ Parent directories created (`dist/presentations/`)
- ✓ Output in `dist/presentations/my-talk/index.html`

---

### Example 2.3: Short flags
```bash
$ mdslides render -i slides.md -o output
```

**Expected:**
- ✓ Same behavior as `--input` / `--output` (aliases work)

---

## Rule 3: Input Resolution - Extensions

### Example 3.1: Prefers .md over .markdown
```bash
$ ls
talk.md
talk.markdown

$ mdslides render talk
```

**Expected:**
- ✓ Reads `talk.md` (NOT `talk.markdown`)
- ✓ Output mentions: "Reading markdown from: talk.md"

---

### Example 3.2: Falls back to .markdown
```bash
$ ls
talk.markdown  # Only .markdown exists, no .md

$ mdslides render talk
```

**Expected:**
- ✓ Reads `talk.markdown`
- ✓ Output: "Reading markdown from: talk.markdown"

---

## Rule 4: Error Handling

### Example 4.1: Input file not found
```bash
$ ls
my-preso.md
other-talk.md

$ mdslides render nonexistent
```

**Expected Output:**
```
✗ Input file not found: nonexistent.md or nonexistent.markdown

Found in current directory:
  - my-preso.md
  - other-talk.md

Usage:
  mdslides render DECK_NAME [--theme THEME]
  mdslides render -i INPUT -o OUTPUT [--theme THEME]

Examples:
  mdslides render my-preso
  mdslides render my-preso --theme dark
  mdslides render -i slides.md -o dist
```

**Test Assertions:**
- ✓ Exit code 1 (error)
- ✓ Error message displayed
- ✓ Lists available .md files
- ✓ Shows usage examples

---

### Example 4.2: Invalid theme
```bash
$ mdslides render my-preso --theme nonexistent
```

**Expected Output:**
```
✗ Theme 'nonexistent' not found in ./themes/

Available themes:
  - light (default)
  - dark
  - tjm-solutions
  - retisio

Usage: mdslides render DECK_NAME --theme THEME
```

**Test Assertions:**
- ✓ Exit code 1
- ✓ Lists available themes
- ✓ Helpful error message

---

### Example 4.3: Missing required argument
```bash
$ mdslides render
```

**Expected Output:**
```
✗ Missing required argument: DECK_NAME

Usage:
  mdslides render DECK_NAME [--theme THEME]
  mdslides render -i INPUT -o OUTPUT [--theme THEME]
```

**Test Assertions:**
- ✓ Exit code 1
- ✓ Clear error message

---

### Example 4.4: Mixing simple and explicit forms
```bash
$ mdslides render my-preso -i slides.md
```

**Expected Output:**
```
✗ Cannot mix simple form (DECK_NAME) with explicit form (-i/-o)

Usage:
  Simple:   mdslides render DECK_NAME
  Explicit: mdslides render -i INPUT -o OUTPUT
```

**Test Assertions:**
- ✓ Exit code 1
- ✓ Clear explanation of the conflict

---

## Rule 5: Special Cases

### Example 5.1: Deck name with spaces
```bash
$ ls
"my talk.md"

$ mdslides render "my talk"
```

**Expected File Structure:**
```
my talk/
└── index.html
```

**Test Assertions:**
- ✓ Reads `my talk.md`
- ✓ Creates `my talk/` directory
- ✓ Works correctly with spaces

---

### Example 5.2: Absolute input path
```bash
$ mdslides render -i /home/user/slides/presentation.md -o output
```

**Expected:**
- ✓ Reads from absolute path `/home/user/slides/presentation.md`
- ✓ Creates `output/` in current directory

---

### Example 5.3: Output directory already exists (non-empty)
```bash
$ ls my-preso/
index.html  old-file.txt

$ mdslides render my-preso
```

**Expected Behavior:**
- ✓ Overwrites `my-preso/index.html`
- ✓ Leaves `old-file.txt` untouched
- ✓ No error (allows incremental updates)

**Alternative (if we want to be safer):**
```
⚠ Warning: Output directory exists: my-preso/
Overwriting files...
```

---

### Example 5.4: No copy images flag
```bash
$ mdslides render my-preso --no-copy-images
```

**Expected File Structure:**
```
my-preso/
└── index.html
(no images/ or backgrounds/ directories)
```

**Expected Behavior:**
- ✓ HTML still contains image references (may be broken)
- ✓ No asset copying performed
- ✓ Useful for when images are hosted elsewhere

---

## TDD Test Structure

### Phase 1: Argument Parsing (CLIArguments)

**Test 1.1:** Parse simple form
```scala
test("CLIArguments.parse - simple form with deck name"):
  val args = Array("render", "my-preso")
  val result = CLIArguments.parse(args)

  assert(result.isRight)
  result.foreach { cliArgs =>
    assertEquals(cliArgs.deckName, Some("my-preso"))
    assertEquals(cliArgs.inputFile, None)
    assertEquals(cliArgs.outputDir, None)
  }
```

**Test 1.2:** Parse explicit form
```scala
test("CLIArguments.parse - explicit form with -i and -o"):
  val args = Array("render", "-i", "slides.md", "-o", "output")
  val result = CLIArguments.parse(args)

  assert(result.isRight)
  result.foreach { cliArgs =>
    assertEquals(cliArgs.deckName, None)
    assertEquals(cliArgs.inputFile, Some(Path.of("slides.md")))
    assertEquals(cliArgs.outputDir, Some(Path.of("output")))
  }
```

**Test 1.3:** Parse with theme
```scala
test("CLIArguments.parse - simple form with --theme"):
  val args = Array("render", "my-preso", "--theme", "dark")
  val result = CLIArguments.parse(args)

  result.foreach { cliArgs =>
    assertEquals(cliArgs.themeName, "dark")
  }
```

**Test 1.4:** Reject mixing simple and explicit
```scala
test("CLIArguments.parse - error when mixing forms"):
  val args = Array("render", "my-preso", "-i", "slides.md")
  val result = CLIArguments.parse(args)

  assert(result.isLeft)
  result.left.foreach { error =>
    assert(error.contains("Cannot mix"))
  }
```

---

### Phase 2: Path Resolution (PathResolver)

**Test 2.1:** Find .md file
```scala
test("PathResolver.findInputFile - finds .md file"):
  // Setup: create temp my-preso.md
  val result = PathResolver.findInputFile("my-preso")

  assert(result.isRight)
  result.foreach { path =>
    assert(path.toString.endsWith("my-preso.md"))
  }
```

**Test 2.2:** Fallback to .markdown
```scala
test("PathResolver.findInputFile - fallback to .markdown"):
  // Setup: create temp my-preso.markdown (no .md)
  val result = PathResolver.findInputFile("my-preso")

  assert(result.isRight)
  result.foreach { path =>
    assert(path.toString.endsWith("my-preso.markdown"))
  }
```

**Test 2.3:** Error when neither exists
```scala
test("PathResolver.findInputFile - error when not found"):
  val result = PathResolver.findInputFile("nonexistent")

  assert(result.isLeft)
  result.left.foreach { error =>
    assert(error.contains("Input file not found"))
  }
```

**Test 2.4:** Determine output directory
```scala
test("PathResolver.determineOutputDir - from deck name"):
  val outputDir = PathResolver.determineOutputDir("my-preso")

  assertEquals(outputDir.getFileName.toString, "my-preso")
```

---

### Phase 3: Asset Path Generation (AssetPathResolver)

**Test 3.1:** Relative image path
```scala
test("AssetPathResolver.relativeImagePath - generates relative path"):
  val imagePath = Path.of("images/diagram.png")
  val relative = AssetPathResolver.relativeImagePath(imagePath)

  assertEquals(relative, "images/diagram.png")
```

**Test 3.2:** Relative background path
```scala
test("AssetPathResolver.relativeBackgroundPath - generates relative path"):
  val bgPath = Path.of("backgrounds/title.png")
  val relative = AssetPathResolver.relativeBackgroundPath(bgPath)

  assertEquals(relative, "backgrounds/title.png")
```

---

### Phase 4: Integration Tests

**Test 4.1:** End-to-end simple form
```scala
test("Integration - render simple form creates directory output"):
  // Setup: create my-preso.md in temp dir
  // Execute: mdslides render my-preso
  // Assert:
  //   - my-preso/ directory exists
  //   - my-preso/index.html exists
  //   - HTML contains relative paths
```

**Test 4.2:** End-to-end with images
```scala
test("Integration - render with images copies to output"):
  // Setup: create deck with image reference
  // Execute: mdslides render my-preso
  // Assert:
  //   - my-preso/images/diagram.png exists
  //   - HTML contains: <img src="images/diagram.png">
```

**Test 4.3:** End-to-end portability
```scala
test("Integration - output directory is portable"):
  // Setup: render deck
  // Execute: move output directory
  // Assert: all asset paths still resolve correctly
```

---

## Implementation Order (TDD Phases)

1. **Phase 1:** CLIArguments parsing (5 tests)
   - Parse simple form
   - Parse explicit form
   - Parse theme flag
   - Reject invalid combinations
   - Reject missing arguments

2. **Phase 2:** PathResolver (4 tests)
   - Find .md file
   - Fallback to .markdown
   - Error when not found
   - Determine output directory

3. **Phase 3:** AssetPathResolver (2 tests)
   - Relative image paths
   - Relative background paths

4. **Phase 4:** HTMLRenderer update (3 tests)
   - Use AssetPathResolver for images
   - Use AssetPathResolver for backgrounds
   - No absolute paths in output

5. **Phase 5:** Main.scala integration (4 tests)
   - Wire up argument parsing
   - Create output directory
   - Copy assets to correct locations
   - Generate index.html

6. **Phase 6:** Error handling (4 tests)
   - Missing input file
   - Invalid theme
   - Missing arguments
   - Mixed forms

7. **Phase 7:** End-to-end integration (3 tests)
   - Simple form complete flow
   - Explicit form complete flow
   - Portability verification

**Total Estimated Tests:** ~25 tests

---

**Example Mapping Status:** ✅ COMPLETE
**Ready for:** TDD Implementation (Phase 1)
**Estimated Implementation Time:** 4-6 hours for all phases
