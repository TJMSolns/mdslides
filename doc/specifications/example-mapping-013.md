# Example Mapping: US-013 Content Validation

**Session Date**: 2024-12-20
**Participants**: Tony Moores (Business/Dev/QA)
**Story**: US-013 - Content Validation (Slot Constraints)
**Status**: Complete

---

## 📖 Story Card

```
┌─────────────────────────────────────────────┐
│ 📘 USER STORY                               │
│                                             │
│ As a slide deck author                      │
│ I want to receive validation errors for     │
│ constraint violations                       │
│ So that my slide content meets template     │
│ requirements                                │
│                                             │
│ Priority: P0 (Blocker)                      │
│ Estimated Effort: 2 days                    │
└─────────────────────────────────────────────┘
```

---

## 🔵 Blue Cards: Business Rules

### Rule 1: Required slots must be non-empty

Required slots must contain content (not empty string or whitespace-only).

**Difference from Structure Validation**: Structure checks slot PRESENCE, Content checks slot is NON-EMPTY.

**Empty Definition**: Empty string `""` or whitespace-only content (spaces, tabs, newlines only).

**Error Type**: ContentError

---

### Rule 2: Slot content must not exceed max_lines (if constraint defined)

If template defines `max_lines` constraint for a slot, content must not exceed it.

**Line Counting**: Count `\n` newline characters (same as Density Validation).

**Difference from Density**: Template `max_lines` is CONSTRAINT (error if exceeded). Theme `maxBodyLines` is HEURISTIC (warning if exceeded).

**Error Type**: ContentError

---

### Rule 3: Slot content must not exceed max_chars (if constraint defined)

If template defines `max_chars` constraint for a slot, content must not exceed it.

**Character Counting**: Plain text only (exclude markdown syntax - consistent with Density Validation heading rule).

**Error Type**: ContentError

---

### Rule 4: Slot content must not exceed max_words (if constraint defined)

If template defines `max_words` constraint for a slot, content must not exceed it.

**Word Counting**: Split on whitespace (same as Density Validation).

**Error Type**: ContentError

---

### Rule 5: Image slot path must exist (if slot type is image)

Image references must point to files that exist at validation time.

**Path Resolution**:
- Relative paths resolved from markdown file directory
- Absolute paths supported
- Check at validation time (fail early)

**Error Type**: ContentError

---

### Rule 6: Code slot language must be valid (if slot type is code)

Code block language must be in supported list (Flexmark syntax highlighting languages).

**Supported Languages**: scala, java, python, javascript, bash, sql, yaml, json, markdown, xml, html, css, go, rust, kotlin

**No Language**: Valid. Treated as plain text with no syntax highlighting.

**Error Type**: ContentError

---

## 🟢 Green Cards: Examples

### ✅ EXAMPLE 1: Valid slide (all constraints met)

```markdown
GIVEN markdown with:
  ---
  template: content
  ---
  ## Key Takeaways

  Functional programming reduces bugs.

WHEN parsed and validated

THEN:
  - Structure validation: SUCCESS
  - Density validation: SUCCESS
  - Content validation: SUCCESS

Rules tested: 1, 2, 3, 4
```

---

### ✅ EXAMPLE 2: Content exactly at limits (boundary test)

```markdown
GIVEN markdown with:
  - Title with exactly 2 lines (max_lines: 2)
  - Author with exactly 80 chars (max_chars: 80)
  - Body with exactly 150 words (max_words: 150)

WHEN parsed and validated

THEN:
  - Content validation: SUCCESS

Rules tested: 2, 3, 4
```

---

### ❌ EXAMPLE 3: Required slot is empty (whitespace only)

```markdown
GIVEN markdown with:
  ---
  template: content
  ---
  ## Heading



WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Error: ContentError
  - Message: "Slide 1 slot 'body' is required but empty (whitespace only)"

Rules tested: 1
```

---

### ❌ EXAMPLE 4: Required slot is empty string

```markdown
GIVEN markdown with:
  ---
  template: content
  ---
  ## Heading

WHEN parsed (body slot exists but is empty)
AND validated

THEN:
  - Validation: FAILED
  - Error: ContentError
  - Message: "Slide 1 slot 'body' is required but empty"

Rules tested: 1
```

---

### ❌ EXAMPLE 5: Title slot exceeds max 2 lines

```markdown
GIVEN markdown with:
  ---
  template: title
  ---
  # Line 1
  Line 2
  Line 3

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Error: ContentError
  - Message: "Slide 1 slot 'title' exceeds max 2 lines (found 3 lines)"

Rules tested: 2
```

---

### ❌ EXAMPLE 6: Heading slot exceeds max 80 chars

```markdown
GIVEN markdown with:
  ---
  template: content
  ---
  ## This is a very long heading that exceeds the eighty character constraint for content slide headings

  Body content here.

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Error: ContentError
  - Message: "Slide 1 slot 'heading' exceeds max 80 chars (found 105 chars)"

Rules tested: 3
```

---

### ❌ EXAMPLE 7: Body slot exceeds max 150 words

```markdown
GIVEN markdown with content template body containing 160 words

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Error: ContentError
  - Message: "Slide 1 slot 'body' exceeds max 150 words (found 160 words)"

Rules tested: 4
```

---

### ❌ EXAMPLE 8: Image path doesn't exist (relative path)

```markdown
GIVEN markdown with:
  ![Architecture](./diagrams/missing.png)

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Error: ContentError
  - Message: "Slide 1 slot 'diagram' references image './diagrams/missing.png' which does not exist"

Rules tested: 5
```

---

### ✅ EXAMPLE 9: Image path exists (relative path)

```markdown
GIVEN image file exists at "./images/diagram.png"
AND markdown with:
  ![Architecture](./images/diagram.png)

WHEN parsed and validated

THEN:
  - Content validation: SUCCESS

Rules tested: 5
```

---

### ❌ EXAMPLE 10: Code block has unsupported language

```markdown
GIVEN markdown with:
  ```foobar
  some code here
  ```

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Error: ContentError
  - Message: "Slide 1 slot 'code' has unsupported language 'foobar'. Supported: scala, java, python, javascript, bash, sql, yaml, json, markdown, xml, html, css, go, rust, kotlin"

Rules tested: 6
```

---

### ✅ EXAMPLE 11: Code block with valid language

```markdown
GIVEN markdown with:
  ```scala
  val x = 42
  println(x)
  ```

WHEN parsed and validated

THEN:
  - Content validation: SUCCESS

Rules tested: 6
```

---

### ✅ EXAMPLE 12: Code block with no language specified

```markdown
GIVEN markdown with:
  ```
  some plain text code
  ```

WHEN parsed and validated

THEN:
  - Content validation: SUCCESS
  - Code treated as plain text (no syntax highlighting)

Rules tested: 6
```

---

### ❌ EXAMPLE 13: Multiple constraint violations on one slide

```markdown
GIVEN markdown with:
  - Required slot 'body' is empty (whitespace only)
  - Slot 'title' exceeds max_lines (3 lines, max 2)
  - Slot 'author' exceeds max_chars (90 chars, max 80)

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Errors: 3 ContentErrors
  - All errors collected in NonEmptyList
  - Error 1: "Slide 1 slot 'body' is required but empty (whitespace only)"
  - Error 2: "Slide 1 slot 'title' exceeds max 2 lines (found 3 lines)"
  - Error 3: "Slide 1 slot 'author' exceeds max 80 chars (found 90 chars)"

Rules tested: 1, 2, 3
```

---

### ❌ EXAMPLE 14: Multiple slides with errors

```markdown
GIVEN markdown with:
  - Slide 1: body slot empty
  - Slide 2: heading exceeds 80 chars
  - Slide 3: body exceeds 150 words

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Errors: 3 ContentErrors
  - All errors collected with slide numbers
  - Error 1: "Slide 1 slot 'body'..."
  - Error 2: "Slide 2 slot 'heading'..."
  - Error 3: "Slide 3 slot 'body'..."

Rules tested: 1, 3, 4
```

---

## 🔴 Red Cards: Questions (All Resolved ✅)

### Q1: What counts as "empty"? ✅

**Answer**: Empty string `""` or whitespace-only content (spaces, tabs, newlines only).

---

### Q2: Structure vs Content validation? ✅

**Answer**: Structure checks slot PRESENCE. Content checks slot is NON-EMPTY.

**Example**: Slide with heading slot containing only whitespace passes Structure Validation but fails Content Validation.

---

### Q3: How to count lines? ✅

**Answer**: Count `\n` newline characters. Same as Density Validation.

---

### Q4: Template vs theme constraints? ✅

**Answer**:
- Template `max_lines` = CONSTRAINT (ContentError if exceeded - blocking)
- Theme `maxBodyLines` = HEURISTIC (DensityWarning if exceeded - non-blocking)

---

### Q5: Include markdown syntax in char count? ✅

**Answer**: No. Plain text only (consistent with Density Validation heading rule).

**Example**: `**bold**` counts as 4 chars (the word "bold"), not 8 chars.

---

### Q6: How to count words? ✅

**Answer**: Split on whitespace. Same as Density Validation.

---

### Q7: Relative vs absolute image paths? ✅

**Answer**: Support both. Relative paths resolved from markdown file directory.

---

### Q8: Check image at validation or render time? ✅

**Answer**: Validation time (fail early). Content Validation checks file existence.

---

### Q9: Supported code languages? ✅

**Answer**: Flexmark syntax highlighting supports: scala, java, python, javascript, bash, sql, yaml, json, markdown, xml, html, css, go, rust, kotlin.

---

### Q10: No language specified? ✅

**Answer**: Valid. Treated as plain text with no syntax highlighting.

---

## 🎯 Story Readiness Assessment

### Coverage Summary

| Metric | Count |
|--------|-------|
| Rules | 6 blue cards |
| Examples | 14 green cards (5 success, 9 failure) |
| Questions | 10 red cards (all resolved ✅) |

### Rule → Example Coverage

```
Rule 1 (Required slots non-empty):
├── Example 1 ✅ (valid content)
├── Example 3 ❌ (whitespace only)
├── Example 4 ❌ (empty string)
└── Example 13 ❌ (empty in multi-error)

Rule 2 (max_lines constraint):
├── Example 1 ✅ (within limit)
├── Example 2 ✅ (exactly at limit)
├── Example 5 ❌ (exceeds limit)
└── Example 13 ❌ (exceeds in multi-error)

Rule 3 (max_chars constraint):
├── Example 1 ✅ (within limit)
├── Example 2 ✅ (exactly at limit)
├── Example 6 ❌ (exceeds limit)
└── Example 13 ❌ (exceeds in multi-error)

Rule 4 (max_words constraint):
├── Example 1 ✅ (within limit)
├── Example 2 ✅ (exactly at limit)
├── Example 7 ❌ (exceeds limit)
└── Example 14 ❌ (exceeds in multi-slide)

Rule 5 (Image path exists):
├── Example 8 ❌ (missing file)
└── Example 9 ✅ (file exists)

Rule 6 (Code language valid):
├── Example 10 ❌ (unsupported language)
├── Example 11 ✅ (valid language)
└── Example 12 ✅ (no language - plain text)
```

### Confidence Level

**HIGH CONFIDENCE** ✅

**Reasons**:
- All 10 questions resolved
- 6 clear business rules identified
- 14 concrete examples cover success and failure paths
- Examples cover boundary conditions (exactly at limit)
- Examples cover multiple errors (single slide + multiple slides)
- Clear separation between Content vs Structure vs Density validation

**Risks Identified**:
- File system access at validation time (must handle IO)
- Path resolution (relative vs absolute, OS differences)
- Performance for large decks with many image references

**Ready for TDD**: YES ✅

---

## 📋 Acceptance Criteria Summary

### Must Have (Critical)

1. ✅ Required slot is empty → ContentError
2. ✅ Slot > max_lines → ContentError
3. ✅ Slot > max_chars → ContentError
4. ✅ Slot > max_words → ContentError
5. ✅ Image path doesn't exist → ContentError
6. ✅ Code language invalid → ContentError
7. ✅ Content at exact limit → valid (no error)
8. ✅ Multiple violations → all errors collected

### Should Have (Important)

9. ✅ Whitespace-only content treated as empty
10. ✅ Error messages include slide number and slot name
11. ✅ Error messages include actual vs max values
12. ✅ Image paths resolved relative to markdown file
13. ✅ Plain text char counting (exclude markdown syntax)

### Nice to Have (Enhancement)

14. ✅ Validation < 50ms for 50-slide deck (including file checks)

---

## 🎨 Visual Example Map

```
┌────────────────────────────────────────────────────────────┐
│                      📘 USER STORY                         │
│             Content Validation (US-013)                    │
└────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R1 │          │ 🔵 R2 │          │ 🔵 R3 │
    │ Non-  │          │  Max  │          │  Max  │
    │ Empty │          │ Lines │          │ Chars │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴──┬──────┐   ┌───┴──┬──────┐   ┌───┴──┬──────┐
    │✅ E1 │❌ E3 │   │✅ E1 │✅ E2 │   │✅ E1 │✅ E2 │
    │      │❌ E4 │   │      │❌ E5 │   │      │❌ E6 │
    └──────┴──────┘   └──────┴──────┘   └──────┴──────┘

    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R4 │          │ 🔵 R5 │          │ 🔵 R6 │
    │  Max  │          │ Image │          │ Code  │
    │ Words │          │ Path  │          │ Lang  │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴──┬──────┐   ┌───┴──┬──────┐   ┌───┴──┬──────┐
    │✅ E1 │✅ E2 │   │❌ E8 │✅ E9 │   │❌E10 │✅E11 │
    │      │❌ E7 │   └──────┴──────┘   │      │✅E12 │
    └──────┴──────┘                     └──────┴──────┘

Multiple Errors:
├── Example 13 ❌ (3 errors on one slide)
└── Example 14 ❌ (3 errors across slides)

Questions (All ✅):
Q1-Q10: All resolved (see Red Cards section)
```

---

## 🔄 Next Steps

1. ✅ **Three Amigos Complete**: All rules and examples documented
2. ✅ **Example Mapping Complete**: 14 examples with full coverage
3. 🔄 **Update BACKLOG-V3.md**: Mark US-013 as Ready for Implementation
4. ⏭️ **Proceed to US-015**: Collect All Errors (complete validation framework)

---

**Session Type**: Ceremony 2.2 - Example Mapping Workshop
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Story Status**: Ready for TDD ✅
