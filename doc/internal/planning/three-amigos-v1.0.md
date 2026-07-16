# Three Amigos Session: v1.0 MVP

**Date:** 2024-12-26
**User Stories:** US-019 (Improved CLI UX) + US-004 (Speaker Notes Parsing)
**Participants:** Tony Moores (Product Owner + Developer + QA)
**Prerequisites:** Event Storming completed for both user stories

---

## Session Goals

1. Refine acceptance criteria for US-019 and US-004
2. Identify edge cases and test scenarios
3. Define "done" criteria for each story
4. Create example mapping for TDD implementation

---

## US-019: Improved CLI UX

### Refined Acceptance Criteria

**AC1:** Simple form infers input/output from deck name
- **Given** a markdown file `my-preso.md` exists
- **When** I run `mdslides render my-preso --theme dark`
- **Then** the system:
  - Reads `my-preso.md`
  - Creates `my-preso/` directory
  - Writes `my-preso/index.html`
  - Copies assets to `my-preso/images/` and `my-preso/backgrounds/`
  - Uses relative paths in HTML (`<img src="images/...">`)

**AC2:** Explicit form uses specified paths
- **Given** a markdown file `slides.txt` exists
- **When** I run `mdslides render -i slides.txt -o dist`
- **Then** the system:
  - Reads `slides.txt`
  - Creates `dist/` directory
  - Writes `dist/index.html`
  - Copies assets to `dist/images/` and `dist/backgrounds/`

**AC3:** Input file resolution with extensions
- **Given** `my-preso.md` exists
- **When** I run `mdslides render my-preso`
- **Then** the system reads `my-preso.md`

- **Given** `my-preso.markdown` exists (and no `my-preso.md`)
- **When** I run `mdslides render my-preso`
- **Then** the system reads `my-preso.markdown`

**AC4:** Error handling for missing input
- **Given** no file `missing.md` or `missing.markdown` exists
- **When** I run `mdslides render missing`
- **Then** the system:
  - Exits with error code 1
  - Displays: "✗ Input file not found: missing.md or missing.markdown"
  - Lists available `.md` files in current directory (helpful hint)
  - Shows usage example

**AC5:** Output directory creation
- **Given** output directory does not exist
- **When** I render a presentation
- **Then** the system creates the directory (including parent directories)

**AC6:** Portability of output
- **Given** a presentation has been rendered to `my-preso/`
- **When** I move/copy `my-preso/` to another location
- **Then** all assets load correctly (relative paths work)

### Edge Cases to Test

1. **File name with spaces:** `mdslides render "my talk"`
   - Should resolve to `my talk.md` → `my talk/index.html`

2. **Both `.md` and `.markdown` exist:**
   - Preference: `.md` takes priority
   - Error if both exist? NO - just use `.md`

3. **Output directory exists and is not empty:**
   - **Decision:** Overwrite files, don't delete directory
   - Rationale: Allows incremental updates

4. **Special characters in deck name:** `my-preso!.md`
   - Allow any valid filesystem characters
   - No sanitization needed (user controls naming)

5. **Absolute vs relative input paths:**
   - `mdslides render /path/to/slides` should work
   - Look for `/path/to/slides.md`

6. **Current directory as output:**
   - `mdslides render slides -o .` should error
   - Reason: Would pollute current directory with index.html

---

## US-004: Speaker Notes Parsing

### Refined Acceptance Criteria

**AC1:** Parse simple notes from frontmatter
- **Given** a slide with frontmatter:
  ```yaml
  notes: "Remember to pause here."
  ```
- **When** the deck is parsed
- **Then** the Slide aggregate contains `notes: Some("Remember to pause here.")`

**AC2:** Parse multi-line notes (array form)
- **Given** a slide with frontmatter:
  ```yaml
  notes:
    - "Point one"
    - "Point two"
  ```
- **When** the deck is parsed
- **Then** the Slide aggregate contains `notes: Some("Point one\nPoint two")`

**AC3:** Parse multi-line notes (YAML pipe syntax)
- **Given** a slide with frontmatter:
  ```yaml
  notes: |
    Line one
    Line two
  ```
- **When** the deck is parsed
- **Then** the Slide aggregate contains `notes: Some("Line one\nLine two")`

**AC4:** Notes are optional
- **Given** a slide without `notes` field
- **When** the deck is parsed
- **Then** the Slide aggregate contains `notes: None`

**AC5:** Notes density validation (warnings)
- **Given** a slide with 200-word notes (exceeds 150 limit)
- **When** validation runs
- **Then** the system:
  - Emits warning: "Speaker notes too long (200 words). Aim for <150 words."
  - Does NOT fail validation
  - Stores notes in Slide aggregate

**AC6:** Invalid notes format (graceful handling)
- **Given** a slide with frontmatter:
  ```yaml
  notes: 12345  # number, not string
  ```
- **When** the deck is parsed
- **Then** the system:
  - Emits warning: "Invalid notes format (expected string or array). Notes ignored."
  - Sets `notes: None`
  - Continues parsing

**AC7:** Notes are not rendered in HTML (v1.0)
- **Given** a slide with notes
- **When** HTML is rendered
- **Then** notes do NOT appear in the HTML output
- **Note:** Rendering is US-034 (v1.1)

### Edge Cases to Test

1. **Empty notes string:**
   ```yaml
   notes: ""
   ```
   - Store as `None` (empty string is useless)

2. **Empty notes array:**
   ```yaml
   notes: []
   ```
   - Store as `None`

3. **Notes with special characters:**
   ```yaml
   notes: "Use \"quotes\" and 'apostrophes'"
   ```
   - YAML parser handles escaping, store as-is

4. **Very long notes (500+ words):**
   - Warning: "Speaker notes very long (500 words). Consider splitting across slides."
   - Still store, don't block

5. **Notes with newlines in array items:**
   ```yaml
   notes:
     - "First point\nwith newline"
     - "Second point"
   ```
   - Result: `"First point\nwith newline\nSecond point"` (double newline between items)

---

## Integration Between US-019 and US-004

**Q:** Does CLI change affect speaker notes?
**A:** No. Speaker notes are frontmatter field (parsing), independent of CLI (rendering).

**Q:** Should notes be included in density validation output?
**A:** Yes, but as separate section:
```
Validating slide deck...
✓ Validation passed

Speaker Notes Summary:
  - 5 slides with notes
  - ⚠ 1 warning: Slide 7 notes too long (200 words)
```

**Q:** Do notes affect output directory structure?
**A:** No. Notes not rendered in v1.0, so no output files affected.

---

## Definition of Done (US-019)

### Functional Requirements
- [ ] Simple form works: `mdslides render DECK_NAME`
- [ ] Explicit form works: `mdslides render -i INPUT -o OUTPUT`
- [ ] Input resolution tries `.md` then `.markdown`
- [ ] Output directory created with `index.html`
- [ ] Assets copied to `OUTPUT/images/` and `OUTPUT/backgrounds/`
- [ ] HTML uses relative paths (portability)
- [ ] Error handling for missing input file
- [ ] Helpful error messages with usage examples

### Testing Requirements
- [ ] Unit tests for argument parsing (CLIArguments)
- [ ] Unit tests for path resolution (PathResolver)
- [ ] Integration tests for end-to-end rendering
- [ ] Test: output directory portability (move and verify)
- [ ] Test: special characters in deck names
- [ ] Test: missing input file error handling

### Documentation Requirements
- [ ] Update tutorial examples to new CLI syntax
- [ ] Update README with new usage
- [ ] Add migration note for v0.x users (if any)

---

## Definition of Done (US-004)

### Functional Requirements
- [ ] Frontmatter parsing handles `notes:` field (string or array)
- [ ] Notes stored in Slide aggregate
- [ ] Notes density validation (warnings, not errors)
- [ ] Invalid format handled gracefully (warning + ignore)
- [ ] Notes NOT rendered in HTML (v1.0)
- [ ] Empty notes treated as None

### Testing Requirements
- [ ] Unit tests for Slide with notes field
- [ ] Unit tests for FrontmatterAdapter notes parsing
- [ ] Unit tests for NotesValidator (word/line limits)
- [ ] Integration tests: parse deck with notes
- [ ] Test: simple string notes
- [ ] Test: array notes
- [ ] Test: YAML pipe syntax notes
- [ ] Test: invalid format (number, object)
- [ ] Test: excessive notes (warning)
- [ ] Test: verify notes NOT in HTML output

### Documentation Requirements
- [ ] Update tutorial with speaker notes example
- [ ] Document notes field in frontmatter spec
- [ ] Add example slide with notes

---

## Risk Assessment

### US-019 Risks

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Breaking existing scripts | High | High | Acceptable (pre-v1.0). Document in release notes. |
| Path resolution bugs (Windows/Mac) | Medium | Medium | Test on multiple platforms, use `java.nio.file.Path` |
| Absolute paths break portability | High | Low | Comprehensive testing, explicit relative path generation |
| Output directory overwrite data loss | High | Low | Don't auto-delete, only overwrite files |

### US-004 Risks

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| YAML parsing edge cases | Medium | Medium | Comprehensive test suite, graceful error handling |
| Notes too long to be useful | Low | Medium | Warnings guide users, don't block |
| Future: notes rendering complexity | Low | Low | V1.0 only parses, rendering is v1.1 concern |

---

## Example Scenarios for Testing

### Scenario 1: Simple Happy Path (Combined)

```bash
# Create markdown with notes
$ cat > my-preso.md
---
template: title
notes: "Start with enthusiasm! Mention the company vision."
---

# Welcome to MDSlides

---

template: content
notes:
  - "Explain each benefit clearly"
  - "Pause for questions"

## Key Benefits

- Easy to use
- Professional themes

^D

# Render with new CLI
$ mdslides render my-preso --theme retisio

Output:
Loading theme: retisio
✓ Loaded theme: the prior organization v1.0.0
Reading markdown from: my-preso.md
Parsing markdown...
✓ Parsed 2 slide(s)
Validating slide deck...
✓ Validation passed

Speaker Notes Summary:
  - 2 slides with notes

Rendering HTML with theme: the prior organization
✓ Generated 12KB HTML
Writing to: my-preso/index.html
Copying 5 theme backgrounds to my-preso/backgrounds/
✓ Successfully created presentation: my-preso/

# Verify output structure
$ ls my-preso/
index.html  backgrounds/

# Verify notes NOT in HTML
$ grep -i "enthusiasm" my-preso/index.html
(no match - notes not rendered)
```

**Expected:** ✓ Pass all acceptance criteria for both US-019 and US-004

### Scenario 2: Error Handling

```bash
$ mdslides render nonexistent

Output:
✗ Input file not found: nonexistent.md or nonexistent.markdown

Found in current directory:
  - my-preso.md
  - tutorial.md

Usage:
  mdslides render DECK_NAME [--theme THEME]
  mdslides render -i INPUT -o OUTPUT [--theme THEME]

Exit code: 1
```

**Expected:** ✓ Clear error message, helpful hints

### Scenario 3: Notes Validation Warning

```bash
$ cat > long-notes.md
---
template: content
notes: "This is a very long note with 200+ words of detailed content that goes on and on..."
---

## Slide Title

Content...

^D

$ mdslides render long-notes

Output:
...
Validating slide deck...
✓ Validation passed

Speaker Notes Summary:
  - 1 slide with notes
  - ⚠ Warning: Slide 1 notes too long (200 words). Aim for <150 words.

✓ Successfully created presentation: long-notes/
```

**Expected:** ✓ Warning shown, but rendering succeeds

---

## Next Steps

1. ✅ Three Amigos session complete
2. **NEXT:** Example Mapping (detailed examples for TDD)
3. **THEN:** Begin TDD implementation
   - Phase 1: US-019 (CLI UX) - foundation
   - Phase 2: US-004 (Speaker Notes) - builds on CLI

---

**Session Status:** ✅ COMPLETE
**Ready for:** Example Mapping
**Estimated Implementation:** 2-3 days (both user stories)
