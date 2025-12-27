# Example Mapping: US-004 (Speaker Notes Parsing)

**Date:** 2024-12-26
**User Story:** US-004 - Speaker Notes Parsing
**Prerequisites:** Event Storming + Three Amigos complete
**Purpose:** Concrete examples for TDD implementation

---

## Story Rules (from Three Amigos)

1. **Notes field:** Slide frontmatter accepts `notes:` (string or array)
2. **Storage:** Notes stored in Slide aggregate as `Option[String]`
3. **Normalization:** Arrays joined with `\n`
4. **Validation:** Warnings for >150 words or >10 lines
5. **Graceful errors:** Invalid format → warning + ignore
6. **No rendering:** Notes NOT in HTML output (v1.0)

---

## Rule 1: Simple Notes (String)

### Example 1.1: Basic string notes
```markdown
---
template: content
notes: "Remember to pause after this point."
---

## Key Concept

The main idea is...
```

**Expected Slide Aggregate:**
```scala
Slide(
  template = "content",
  slots = Map("heading" -> ..., "body" -> ...),
  backgroundImage = None,
  notes = Some("Remember to pause after this point.")
)
```

**Test Assertions:**
- ✓ Notes field parsed from frontmatter
- ✓ Stored as-is in Slide.notes
- ✓ Validation passes (8 words, 1 line)

---

### Example 1.2: Multi-sentence notes
```markdown
---
template: content
notes: "Emphasize security benefits. Mention customer testimonial from Acme Corp. Transition smoothly to implementation."
---
```

**Expected:**
```scala
notes = Some("Emphasize security benefits. Mention customer testimonial from Acme Corp. Transition smoothly to implementation.")
```

**Test Assertions:**
- ✓ Multi-sentence string parsed correctly
- ✓ Validation passes (13 words, 1 line)

---

## Rule 2: Multi-line Notes (Array)

### Example 2.1: Simple array
```markdown
---
template: content
notes:
  - "Point one"
  - "Point two"
  - "Point three"
---
```

**Expected:**
```scala
notes = Some("Point one\nPoint two\nPoint three")
```

**Test Assertions:**
- ✓ Array items joined with `\n`
- ✓ Preserves order
- ✓ Validation passes (6 words, 3 lines)

---

### Example 2.2: Array with detailed items
```markdown
---
template: content
notes:
  - "Start by stating the problem clearly"
  - "Transition to our solution with enthusiasm"
  - "End with a call to action"
---
```

**Expected:**
```scala
notes = Some("Start by stating the problem clearly\nTransition to our solution with enthusiasm\nEnd with a call to action")
```

**Test Assertions:**
- ✓ Multi-word items joined correctly
- ✓ Validation passes (18 words, 3 lines)

---

## Rule 3: YAML Multi-line Syntax

### Example 3.1: Pipe syntax (literal)
```markdown
---
template: content
notes: |
  Line one
  Line two
  Line three
---
```

**Expected:**
```scala
notes = Some("Line one\nLine two\nLine three")
```

**Test Assertions:**
- ✓ YAML parser handles pipe syntax
- ✓ Newlines preserved
- ✓ Stored as single string

---

### Example 3.2: Greater-than syntax (folded)
```markdown
---
template: content
notes: >
  This is a long note
  that will be folded
  into a single line.
---
```

**Expected:**
```scala
notes = Some("This is a long note that will be folded into a single line.")
```

**Test Assertions:**
- ✓ YAML parser folds into single line
- ✓ Spaces preserved between words

---

## Rule 4: No Notes (Optional)

### Example 4.1: Slide without notes field
```markdown
---
template: content
---

## Slide Title

Content...
```

**Expected:**
```scala
Slide(
  template = "content",
  slots = ...,
  backgroundImage = None,
  notes = None  // No notes field
)
```

**Test Assertions:**
- ✓ Notes field defaults to None
- ✓ No validation errors
- ✓ Parsing succeeds

---

## Rule 5: Validation Warnings

### Example 5.1: Excessive word count
```markdown
---
template: content
notes: "This is a very long note that contains way too much information for a speaker to read while presenting. It goes on and on with detailed explanations, multiple anecdotes, historical context, potential audience questions, detailed answers, and far more content than anyone could reasonably reference during a presentation. The speaker would be reading from the notes instead of engaging with the audience. This defeats the purpose of speaker notes which should be brief reminders, not a full script. We recommend keeping notes under 150 words for easy reference during presentations. This note has exceeded that limit significantly and should be split across multiple slides or condensed to key points only. Remember that effective presentations rely on the speaker's knowledge and engagement, not on reading extensive notes verbatim."
---
```

**Word Count:** ~140 words (let's say 160 for this example)

**Expected Validation Output:**
```
⚠ Warning: Slide 1 - Speaker notes too long (160 words). Aim for <150 words for easy reference.
```

**Expected Slide:**
```scala
notes = Some("This is a very long note...")  // Still stored despite warning
```

**Test Assertions:**
- ✓ Warning emitted
- ✓ Notes still stored in Slide
- ✓ Validation does NOT fail (warning only)

---

### Example 5.2: Excessive line count
```markdown
---
template: content
notes:
  - "Point 1"
  - "Point 2"
  - "Point 3"
  - "Point 4"
  - "Point 5"
  - "Point 6"
  - "Point 7"
  - "Point 8"
  - "Point 9"
  - "Point 10"
  - "Point 11"
  - "Point 12"
---
```

**Line Count:** 12 lines

**Expected Validation Output:**
```
⚠ Warning: Slide 1 - Speaker notes too long (12 lines). Aim for <10 lines to fit on screen.
```

**Expected:**
```scala
notes = Some("Point 1\nPoint 2\n...\nPoint 12")  // Still stored
```

**Test Assertions:**
- ✓ Line count warning emitted
- ✓ Notes stored despite warning

---

### Example 5.3: Both word and line warnings
```markdown
---
template: content
notes: |
  This is line one with many words in it
  Line two also has many words
  Line three continues the pattern
  Line four with more content
  Line five adds even more
  Line six keeps going
  Line seven is here too
  Line eight continues
  Line nine adds more
  Line ten is the last
  Line eleven exceeds limit
---
```

**Expected Validation Output:**
```
⚠ Warning: Slide 1 - Speaker notes too long (55 words). Aim for <150 words for easy reference.
⚠ Warning: Slide 1 - Speaker notes too long (11 lines). Aim for <10 lines to fit on screen.
```

**Test Assertions:**
- ✓ Both warnings emitted
- ✓ Notes still stored

---

## Rule 6: Invalid Format (Graceful Handling)

### Example 6.1: Number instead of string
```markdown
---
template: content
notes: 12345
---
```

**Expected Validation Output:**
```
⚠ Warning: Slide 1 - Invalid notes format (expected string or array, got number). Notes ignored.
```

**Expected:**
```scala
notes = None  // Invalid format, set to None
```

**Test Assertions:**
- ✓ Warning emitted
- ✓ Notes set to None
- ✓ Parsing continues (doesn't fail)

---

### Example 6.2: Object instead of string/array
```markdown
---
template: content
notes:
  title: "Not allowed"
  content: "This is an object"
---
```

**Expected Warning:**
```
⚠ Warning: Slide 1 - Invalid notes format (expected string or array, got object). Notes ignored.
```

**Expected:**
```scala
notes = None
```

---

### Example 6.3: Boolean instead of string
```markdown
---
template: content
notes: true
---
```

**Expected Warning:**
```
⚠ Warning: Slide 1 - Invalid notes format (expected string or array, got boolean). Notes ignored.
```

**Expected:**
```scala
notes = None
```

---

## Rule 7: Edge Cases

### Example 7.1: Empty string
```markdown
---
template: content
notes: ""
---
```

**Expected:**
```scala
notes = None  // Empty string treated as no notes
```

**Test Assertions:**
- ✓ Empty string converted to None
- ✓ No validation warnings

---

### Example 7.2: Empty array
```markdown
---
template: content
notes: []
---
```

**Expected:**
```scala
notes = None  // Empty array treated as no notes
```

---

### Example 7.3: Whitespace-only notes
```markdown
---
template: content
notes: "   "
---
```

**Expected:**
```scala
notes = None  // Whitespace-only treated as empty
```

---

### Example 7.4: Special characters in notes
```markdown
---
template: content
notes: "Use \"quotes\" and 'apostrophes' with <HTML> & symbols"
---
```

**Expected:**
```scala
notes = Some("Use \"quotes\" and 'apostrophes' with <HTML> & symbols")
```

**Test Assertions:**
- ✓ Special characters preserved
- ✓ YAML escaping handled by parser
- ✓ Stored as-is

---

### Example 7.5: Array with newlines in items
```markdown
---
template: content
notes:
  - "First point\nwith internal newline"
  - "Second point"
---
```

**Expected:**
```scala
notes = Some("First point\nwith internal newline\nSecond point")
```

**Test Assertions:**
- ✓ Internal newlines preserved
- ✓ Array separator adds additional newline

---

## Rule 8: Notes NOT Rendered (v1.0)

### Example 8.1: Notes excluded from HTML
```markdown
---
template: content
notes: "This is a secret note for the presenter."
---

## Public Slide

This content is visible.
```

**Generated HTML (simplified):**
```html
<div class="slide">
  <h2>Public Slide</h2>
  <p>This content is visible.</p>
</div>
<!-- NO speaker notes in HTML -->
```

**Test Assertions:**
- ✓ HTML does NOT contain "This is a secret note"
- ✓ No `<div class="notes">` element
- ✓ Notes stored in domain model but not rendered

---

## TDD Test Structure

### Phase 1: Domain - Slide with Notes

**Test 1.1:** Slide accepts notes field
```scala
test("Slide - accepts notes field"):
  val slide = Slide(
    template = "content",
    slots = Map.empty,
    backgroundImage = None,
    notes = Some("Test notes")
  )

  assertEquals(slide.notes, Some("Test notes"))
```

**Test 1.2:** Slide notes default to None
```scala
test("Slide - notes default to None"):
  val slide = Slide(
    template = "content",
    slots = Map.empty,
    backgroundImage = None
    // notes not specified
  )

  assertEquals(slide.notes, None)
```

---

### Phase 2: Infrastructure - Frontmatter Parsing

**Test 2.1:** Parse simple string notes
```scala
test("FrontmatterAdapter - parse string notes"):
  val markdown = """---
template: content
notes: "Remember this point"
---"""

  val result = FrontmatterAdapter.parseFrontmatter(markdown)

  result.foreach { frontmatter =>
    assertEquals(frontmatter.notes, Some("Remember this point"))
  }
```

**Test 2.2:** Parse array notes
```scala
test("FrontmatterAdapter - parse array notes"):
  val markdown = """---
template: content
notes:
  - "Point one"
  - "Point two"
---"""

  val result = FrontmatterAdapter.parseFrontmatter(markdown)

  result.foreach { frontmatter =>
    assertEquals(frontmatter.notes, Some("Point one\nPoint two"))
  }
```

**Test 2.3:** Parse YAML pipe syntax
```scala
test("FrontmatterAdapter - parse YAML pipe syntax"):
  val markdown = """---
template: content
notes: |
  Line one
  Line two
---"""

  val result = FrontmatterAdapter.parseFrontmatter(markdown)

  result.foreach { frontmatter =>
    assertEquals(frontmatter.notes, Some("Line one\nLine two"))
  }
```

**Test 2.4:** Handle missing notes field
```scala
test("FrontmatterAdapter - missing notes field returns None"):
  val markdown = """---
template: content
---"""

  val result = FrontmatterAdapter.parseFrontmatter(markdown)

  result.foreach { frontmatter =>
    assertEquals(frontmatter.notes, None)
  }
```

**Test 2.5:** Handle invalid format (number)
```scala
test("FrontmatterAdapter - invalid notes format returns None"):
  val markdown = """---
template: content
notes: 12345
---"""

  val result = FrontmatterAdapter.parseFrontmatter(markdown)

  result.foreach { frontmatter =>
    assertEquals(frontmatter.notes, None)
  }
  // TODO: Verify warning emitted
```

**Test 2.6:** Handle empty string
```scala
test("FrontmatterAdapter - empty string notes returns None"):
  val markdown = """---
template: content
notes: ""
---"""

  val result = FrontmatterAdapter.parseFrontmatter(markdown)

  result.foreach { frontmatter =>
    assertEquals(frontmatter.notes, None)
  }
```

**Test 2.7:** Handle empty array
```scala
test("FrontmatterAdapter - empty array notes returns None"):
  val markdown = """---
template: content
notes: []
---"""

  val result = FrontmatterAdapter.parseFrontmatter(markdown)

  result.foreach { frontmatter =>
    assertEquals(frontmatter.notes, None)
  }
```

---

### Phase 3: Validation - Notes Density

**Test 3.1:** Valid notes (under limits)
```scala
test("NotesValidator - valid notes pass"):
  val notes = "Short reminder about key point."  // 5 words, 1 line
  val errors = NotesValidator.validate(notes)

  assertEquals(errors, List.empty)
```

**Test 3.2:** Excessive words warning
```scala
test("NotesValidator - warns when exceeding word limit"):
  val notes = "word " * 160  // 160 words
  val errors = NotesValidator.validate(notes)

  assertEquals(errors.length, 1)
  assert(errors.head.message.contains("too long"))
  assert(errors.head.message.contains("160 words"))
```

**Test 3.3:** Excessive lines warning
```scala
test("NotesValidator - warns when exceeding line limit"):
  val notes = (1 to 12).map(i => s"Line $i").mkString("\n")  // 12 lines
  val errors = NotesValidator.validate(notes)

  assertEquals(errors.length, 1)
  assert(errors.head.message.contains("too long"))
  assert(errors.head.message.contains("12 lines"))
```

**Test 3.4:** Both warnings
```scala
test("NotesValidator - warns for both word and line limits"):
  val notes = (1 to 12).map(i => s"This is line $i with many words").mkString("\n")
  val errors = NotesValidator.validate(notes)

  assertEquals(errors.length, 2)  // Both word and line warnings
```

---

### Phase 4: Integration - End-to-End

**Test 4.1:** Parse deck with notes
```scala
test("Integration - parse deck with speaker notes"):
  val markdown = """---
template: content
notes: "Remember to pause here"
---

## Slide Title

Content...
"""

  val deck = MarkdownParser.parse(markdown)

  assert(deck.isRight)
  deck.foreach { slideDeck =>
    assertEquals(slideDeck.slides.head.notes, Some("Remember to pause here"))
  }
```

**Test 4.2:** Validation includes notes warnings
```scala
test("Integration - validation includes notes warnings"):
  val markdown = """---
template: content
notes: "This is a very long note with 200 words..."  // Exceeds limit
---

## Slide
"""

  val deck = MarkdownParser.parse(markdown)
  val validationResult = deck.flatMap(SlideDeckValidator.validate)

  // Validation succeeds (warnings don't fail)
  assert(validationResult.isRight)

  validationResult.foreach { warnings =>
    assert(warnings.exists(_.message.contains("Speaker notes too long")))
  }
```

**Test 4.3:** Notes not in HTML output
```scala
test("Integration - notes excluded from HTML"):
  val markdown = """---
template: content
notes: "Secret presenter notes"
---

## Slide Title
"""

  val deck = MarkdownParser.parse(markdown)
  val html = deck.flatMap(d => HTMLRenderer.render(d, theme))

  html.foreach { htmlString =>
    assert(!htmlString.contains("Secret presenter notes"))
  }
```

---

## Implementation Order (TDD Phases)

1. **Phase 1:** Domain model update (2 tests)
   - Slide accepts notes field
   - Notes default to None

2. **Phase 2:** Frontmatter parsing (7 tests)
   - Parse string notes
   - Parse array notes
   - Parse YAML multi-line
   - Handle missing field
   - Handle invalid format
   - Handle empty string
   - Handle empty array

3. **Phase 3:** Notes validation (4 tests)
   - Valid notes pass
   - Excessive words warning
   - Excessive lines warning
   - Both warnings

4. **Phase 4:** Integration (3 tests)
   - Parse deck with notes
   - Validation includes notes warnings
   - Notes not in HTML output

**Total Estimated Tests:** ~16 tests

---

**Example Mapping Status:** ✅ COMPLETE
**Ready for:** TDD Implementation (after US-019 complete)
**Estimated Implementation Time:** 2-3 hours
