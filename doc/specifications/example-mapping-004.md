# Example Mapping: US-004 Speaker Notes in Markdown

**Session Date**: 2024-12-20
**Participants**: Tony Moores (Business/Dev/QA)
**Story**: US-004 - Speaker Notes in Markdown
**Status**: Complete

---

## 📖 Story Card

```
┌─────────────────────────────────────────────┐
│ 📘 USER STORY                               │
│                                             │
│ As a slide deck author                      │
│ I want to add speaker notes to slides       │
│ So that I can remember what to say during   │
│ presentations                               │
│                                             │
│ Priority: P0 (Blocker)                      │
│ Estimated Effort: 2 days                    │
│ v1.0 Scope: Parse only (no rendering)       │
└─────────────────────────────────────────────┘
```

---

## 🔵 Blue Cards: Business Rules

### Rule 1: Speaker notes added via `notes:` in front matter

Notes are specified in YAML front matter using the `notes:` field, supporting both single-line and multi-line text.

**YAML Syntax**:
- Single-line: `notes: Remember to pause.`
- Multi-line literal: `notes: |` (preserves line breaks)
- Multi-line folded: `notes: >` (folds into single line)

**Constraints**:
- Parse using YAML parser (handles multi-line automatically)
- Can appear in global or per-slide front matter

---

### Rule 2: Notes support markdown formatting

Speaker notes are treated as `markdown_block` type, supporting all markdown elements (bold, italics, lists, code, images, etc.).

**Rationale**: Presenters may want formatted notes for emphasis, checklists, or code snippets.

**Constraints**:
- Parse markdown using Flexmark (same as body slot)
- Rendering deferred to v1.1 (US-034 Speaker View)

---

### Rule 3: Notes max 500 chars → validation warning

Notes exceeding 500 characters trigger a validation warning (not error).

**Threshold**: 500 characters (raw markdown, including syntax)

**Warning Type**: DensityWarning

**Rationale**: Soft limit encourages concise notes but allows longer scripts for recorded presentations.

---

### Rule 4: Notes are optional

The `notes:` field is optional. Missing or empty notes are valid.

**Empty Conditions**:
- Field not present in front matter
- Field is empty string: `notes: ""`
- Field is whitespace only: `notes: |`

**Behavior**: All treated as empty (no validation error)

---

### Rule 5: Notes stored as slot in Slide aggregate

Notes are stored as an optional slot named `notes` with type `markdown_block`.

**Domain Model**:
```scala
slots:
  - name: notes
    type: markdown_block
    required: false
    constraints:
      max_chars: 500  // Warning if exceeded
```

---

### Rule 6: v1.0 scope: Parse only (no rendering)

v1.0 parses and validates notes but does NOT render them in HTML output.

**v1.0**: Parse, validate, store
**v1.1 (US-034)**: Render in speaker view

---

## 🟢 Green Cards: Examples

### ✅ EXAMPLE 1: Simple single-line notes

```markdown
GIVEN markdown with:
  ---
  template: content
  notes: Remember to pause after this slide.
  ---
  ## Key Takeaway
  Functional programming reduces bugs.

WHEN parsed

THEN:
  - SlideDeck has 1 Slide
  - Slide has notes: "Remember to pause after this slide."
  - validation: SUCCESS

Rules tested: 1, 4, 5
```

---

### ✅ EXAMPLE 2: Multi-line notes (literal block)

```markdown
GIVEN markdown with:
  ---
  template: content
  notes: |
    Remember to emphasize the key differentiator.
    Transition to next slide by asking a question.
    Allow 2 minutes for Q&A.
  ---
  ## Why Functional Programming?
  Content here.

WHEN parsed

THEN:
  - Slide has notes with 3 lines
  - Line breaks preserved
  - validation: SUCCESS

Rules tested: 1, 2, 5
```

---

### ✅ EXAMPLE 3: Multi-line notes (folded block)

```markdown
GIVEN markdown with:
  ---
  template: content
  notes: >
    This is a long note that will be folded into
    a single line with spaces instead of newlines.
  ---
  ## Slide Content

WHEN parsed

THEN:
  - Slide has notes as single line with spaces
  - validation: SUCCESS

Rules tested: 1, 5
```

---

### ✅ EXAMPLE 4: Notes with markdown formatting

```markdown
GIVEN markdown with:
  ---
  template: content
  notes: |
    **Important**: Mention partnership with Acme Corp.
    *Emphasize* the cost savings:
    - 30% reduction
    - 2x faster deployment
  ---
  ## Cost Savings
  Content here.

WHEN parsed

THEN:
  - Slide notes contain markdown (bold, italics, list)
  - Markdown stored as-is (not rendered in v1.0)
  - validation: SUCCESS

Rules tested: 1, 2, 5
```

---

### ✅ EXAMPLE 5: No notes (optional)

```markdown
GIVEN markdown with:
  ---
  template: content
  ---
  ## Simple Slide
  No notes needed.

WHEN parsed

THEN:
  - Slide has no notes (empty/null)
  - validation: SUCCESS

Rules tested: 4, 5
```

---

### ✅ EXAMPLE 6: Empty notes (empty string)

```markdown
GIVEN markdown with:
  ---
  template: content
  notes: ""
  ---
  ## Slide Content

WHEN parsed

THEN:
  - Slide has no notes (treated as empty)
  - validation: SUCCESS

Rules tested: 4, 5
```

---

### ✅ EXAMPLE 7: Global front matter notes

```markdown
GIVEN markdown with:
  ---
  notes: Remember to speak slowly for this entire deck.
  ---
  ## Slide 1
  Content.
  ---
  ## Slide 2
  Content.

WHEN parsed

THEN:
  - SlideDeck has 2 Slides
  - Slide 1 has notes: "Remember to speak slowly..."
  - Slide 2 has notes: "Remember to speak slowly..."
  - validation: SUCCESS

Rules tested: 1, 5
```

---

### ✅ EXAMPLE 8: Per-slide override global

```markdown
GIVEN markdown with:
  ---
  notes: Default notes for all slides.
  ---
  ## Slide 1
  Content.
  ---
  ---
  notes: Custom notes for this slide only.
  ---
  ## Slide 2
  Content.

WHEN parsed

THEN:
  - Slide 1 has notes: "Default notes for all slides."
  - Slide 2 has notes: "Custom notes for this slide only."
  - validation: SUCCESS

Rules tested: 1, 5
```

---

### ✅ EXAMPLE 9: Whitespace-only notes

```markdown
GIVEN markdown with:
  ---
  template: content
  notes: |


  ---
  ## Slide Content

WHEN parsed

THEN:
  - Slide has no notes (whitespace trimmed to empty)
  - validation: SUCCESS

Rules tested: 4, 5
```

---

### ⚠️ EXAMPLE 10: Notes exceed 500 chars

```markdown
GIVEN markdown with:
  ---
  template: content
  notes: |
    [600 characters of detailed speaker notes here that exceed
    the recommended 500 character limit for concise speaker notes.
    This is a warning, not an error, because some presenters may
    need detailed scripts for recorded presentations or training.
    Additional text to reach 600 characters... Lorem ipsum dolor
    sit amet, consectetur adipiscing elit. Sed do eiusmod tempor
    incididunt ut labore et dolore magna aliqua.]
  ---
  ## Complex Topic

WHEN parsed

THEN:
  - validation: SUCCESS with WARNING
  - Warning: DensityWarning
  - Message: "Speaker notes on slide 1 exceed 500 chars (found 600)"

Rules tested: 3, 5
```

---

### ⚠️ EXAMPLE 11: Very long notes (10,000 chars)

```markdown
GIVEN markdown file with notes containing 10,000 characters

WHEN parsed

THEN:
  - validation: SUCCESS with WARNING
  - Warning: DensityWarning
  - Message: "Speaker notes on slide 1 are excessively long (10,000 chars). Max recommended: 500 chars."

Rules tested: 3, 5
```

---

### ✅ EXAMPLE 12: Notes with inline code

```markdown
GIVEN markdown with:
  ---
  notes: |
    Mention that we use `Scala 3` for type safety.
    Highlight the `case class` pattern.
  ---
  ## Type Safety

WHEN parsed

THEN:
  - Slide notes contain inline code
  - validation: SUCCESS

Rules tested: 2, 5
```

---

### ✅ EXAMPLE 13: Notes with code block

```markdown
GIVEN markdown with:
  ---
  notes: |
    Show this example code:
    ```scala
    case class User(id: UserId, name: String)
    ```
  ---
  ## Domain Modeling

WHEN parsed

THEN:
  - Slide notes contain code block
  - validation: SUCCESS

Rules tested: 2, 5
```

---

## 🔴 Red Cards: Questions (All Resolved ✅)

### Q1: Can notes be in global front matter? ✅

**Answer**: Yes. Global notes apply to all slides (e.g., "Speak slowly" for entire deck).

**Use Case**: Rare, but valid for deck-wide reminders.

---

### Q2: Are notes required or optional? ✅

**Answer**: Optional. Missing or empty notes are valid.

**Rationale**: Not all slides need speaker notes (e.g., image slides, dividers).

---

### Q3: Support code blocks, images in notes? ✅

**Answer**: Yes. Treat as `markdown_block` which supports all markdown elements.

**Rationale**: Presenters may want code snippets or reference images in notes.

---

### Q4: Is 500 chars hard or soft limit? ✅

**Answer**: Soft limit (warning only).

**Rationale**: Some presenters need detailed scripts (recorded presentations, training).

---

### Q5: How to count chars (raw or plain text)? ✅

**Answer**: Count raw markdown characters (including syntax).

**Rationale**: Simpler to implement. Author can see char count in editor.

---

### Q6: Flag whitespace-only notes? ✅

**Answer**: No. Treat as empty, no warning.

**Rationale**: Whitespace-only is effectively empty.

---

### Q7: Notes as field or slot? ✅

**Answer**: Treat as slot (named `notes`).

**Rationale**: Allows template-level constraints, keeps Slide aggregate simpler.

---

### Q8: Type of notes slot? ✅

**Answer**: `markdown_block` (supports multi-line, formatting, lists, code, etc.)

---

### Q9: Should v1.0 HTML include notes? ✅

**Answer**: No. HTML output in v1.0 is slide content only. Notes rendering deferred to v1.1 (US-034).

**Rationale**: Speaker view is a separate feature requiring split-screen rendering.

---

## 🎯 Story Readiness Assessment

### Coverage Summary

| Metric | Count |
|--------|-------|
| Rules | 6 blue cards |
| Examples | 13 green cards (11 success, 2 warning) |
| Questions | 9 red cards (all resolved ✅) |

### Rule → Example Coverage

```
Rule 1 (Notes in front matter):
├── Example 1 ✅ (single-line)
├── Example 2 ✅ (multi-line literal)
├── Example 3 ✅ (multi-line folded)
├── Example 4 ✅ (with markdown)
├── Example 7 ✅ (global)
└── Example 8 ✅ (per-slide override)

Rule 2 (Markdown formatting):
├── Example 4 ✅ (bold, italics, list)
├── Example 12 ✅ (inline code)
└── Example 13 ✅ (code block)

Rule 3 (Max 500 chars warning):
├── Example 10 ⚠️ (600 chars)
└── Example 11 ⚠️ (10,000 chars)

Rule 4 (Optional):
├── Example 5 ✅ (no notes)
├── Example 6 ✅ (empty string)
└── Example 9 ✅ (whitespace only)

Rule 5 (Stored as slot):
└── All examples ✅

Rule 6 (v1.0 parse only):
└── Implicit in all examples ✅
```

### Confidence Level

**HIGH CONFIDENCE** ✅

**Reasons**:
- All 9 questions resolved
- 6 clear business rules identified
- 13 concrete examples cover success, warning, and edge cases
- Examples cover YAML syntax variants (literal, folded)
- Examples cover global/per-slide inheritance
- v1.0 scope clearly bounded (parse only, no rendering)

**Risks Identified**:
- YAML parser choice (SnakeYAML vs circe-yaml)
- Markdown parsing for notes (reuse Flexmark infrastructure)
- Performance for very long notes (10,000+ chars)

**Ready for TDD**: YES ✅

---

## 📋 Acceptance Criteria Summary

### Must Have (Critical)

1. ✅ Parse `notes:` from YAML front matter
2. ✅ Support single-line and multi-line notes
3. ✅ Support markdown formatting in notes
4. ✅ Notes are optional (missing/empty is valid)
5. ✅ Warn if notes > 500 chars
6. ✅ Store notes as slot in Slide aggregate

### Should Have (Important)

7. ✅ Global notes apply to all slides
8. ✅ Per-slide notes override global
9. ✅ Whitespace-only treated as empty

### Nice to Have (Enhancement)

10. ✅ Clear warning messages with char count
11. ⚠️ v1.0 does NOT render notes (deferred to v1.1)

---

## 🎨 Visual Example Map

```
┌────────────────────────────────────────────────────────────┐
│                      📘 USER STORY                         │
│           Speaker Notes in Markdown (US-004)               │
│                  v1.0: Parse Only                          │
└────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R1 │          │ 🔵 R2 │          │ 🔵 R3 │
    │notes: │          │ MD    │          │ 500   │
    │ FM    │          │Format │          │ Warn  │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴───┬──────┐   ┌──┴──┬──────┐   ┌──┴──┐
    │✅ E1  │✅ E2 │   │✅ E4│✅ E12│   │⚠️E10│
    │✅ E3  │✅ E7 │   │✅ E13│     │   │⚠️E11│
    │✅ E8  │      │   │     │     │   │     │
    └───────┴──────┘   └─────┴──────┘   └─────┘

    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R4 │          │ 🔵 R5 │          │ 🔵 R6 │
    │Option │          │ Slot  │          │Parse  │
    │ -al   │          │Storage│          │ Only  │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴───┬──────┐   ┌──┴──┐           ┌──┴──┐
    │✅ E5  │✅ E6 │   │All  │           │ v1.0│
    │✅ E9  │      │   │ ✅   │           │ ✅   │
    └───────┴──────┘   └─────┘           └─────┘

Questions (All ✅):
Q1-Q9: All resolved (see Red Cards section)
```

---

## 🔄 Next Steps

1. ✅ **Three Amigos Complete**: All rules and examples documented
2. ✅ **Example Mapping Complete**: 13 examples with full coverage
3. 🔄 **Update BACKLOG-V3.md**: Mark US-004 as Ready for Implementation
4. ⏭️ **Proceed to US-011**: Structure Validation (next in sequence)

---

**Session Type**: Ceremony 2.2 - Example Mapping Workshop
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Story Status**: Ready for TDD ✅
