# Example Mapping: US-003 Parse Multi-Slide Markdown File

**Session Date**: 2024-12-20
**Participants**: Tony Moores (Business/Dev/QA)
**Story**: US-003 - Parse Multi-Slide Markdown File
**Status**: Complete

---

## 📖 Story Card

```
┌─────────────────────────────────────────────┐
│ 📘 USER STORY                               │
│                                             │
│ As a slide deck author                      │
│ I want to create multiple slides in one     │
│ Markdown file                               │
│ So that I can author complete presentations │
│ in a single document                        │
│                                             │
│ Priority: P0 (Blocker)                      │
│ Estimated Effort: 3 days                    │
└─────────────────────────────────────────────┘
```

---

## 🔵 Blue Cards: Business Rules

### Rule 1: Slides separated by `---` (thematic break)

Markdown files are split into multiple slides using the `---` separator (CommonMark thematic break).

**Constraints**:
- Use Flexmark's ThematicBreak node as separator
- Accepts `---`, `***`, `___` per CommonMark spec
- Separator inside code block does NOT split slides
- Separator inside blockquote does NOT split slides

---

### Rule 2: Global front matter applies to all slides

YAML front matter at the top of the file (before any slide content) applies to all slides in the deck.

**Format**:
```markdown
---
theme: corporate
author: Tony Moores
---
## First Slide
...
```

**Constraints**:
- Must appear at very top of file
- If text appears before `---`, that's slide content (no global front matter)

---

### Rule 3: Per-slide front matter overrides global

Each slide can have its own YAML front matter block immediately after the slide separator, which overrides global settings.

**Format**:
```markdown
## Slide 1
Content.
---
---
template: title
theme: minimal
---
## Slide 2
Content with overrides.
```

**Constraints**:
- Per-slide front matter must appear immediately after separator
- Per-slide settings merge with global (per-slide wins on conflicts)
- Malformed YAML triggers parsing error

---

### Rule 4: Slide order is preserved

Slides appear in the SlideDeck in the same order as authored in the markdown file.

**Constraints**:
- Slide[0] is first slide in file
- Slide[n-1] is last slide in file
- Slide IDs (if provided) don't affect ordering

---

### Rule 5: Empty slides are validation errors

Slides must contain content (not just front matter or whitespace).

**What counts as empty**:
- No text between separators
- Only whitespace between separators
- Only front matter, no content

**Error Type**: StructureError

---

### Rule 6: Slide IDs must be unique

If slides provide explicit `id:` in front matter, IDs must be unique across the deck.

**Auto-generated IDs**:
- If no `id:` provided, system generates `slide-1`, `slide-2`, etc.
- Auto-generated IDs never conflict

**Error Type**: StructureError if duplicates found

---

## 🟢 Green Cards: Examples

### ✅ EXAMPLE 1: Simple 3-slide deck

```markdown
GIVEN markdown with:
  ## Slide 1
  First slide content.
  ---
  ## Slide 2
  Second slide content.
  ---
  ## Slide 3
  Third slide content.

WHEN parsed

THEN:
  - SlideDeck has 3 Slides
  - Slide 1 heading: "Slide 1"
  - Slide 2 heading: "Slide 2"
  - Slide 3 heading: "Slide 3"
  - All use default template
  - validation: SUCCESS

Rules tested: 1, 4
```

---

### ✅ EXAMPLE 2: Global front matter

```markdown
GIVEN markdown with:
  ---
  theme: corporate
  author: Tony Moores
  ---
  ## Slide 1
  Content.
  ---
  ## Slide 2
  More content.

WHEN parsed

THEN:
  - SlideDeck has 2 Slides
  - All slides: theme="corporate"
  - All slides: author="Tony Moores"
  - validation: SUCCESS

Rules tested: 2, 4
```

---

### ✅ EXAMPLE 3: Per-slide override

```markdown
GIVEN markdown with:
  ---
  theme: corporate
  template: content
  ---
  ## Slide 1
  Uses corporate theme.
  ---
  ---
  theme: minimal
  template: title
  ---
  ## Slide 2
  Uses minimal theme.

WHEN parsed

THEN:
  - SlideDeck has 2 Slides
  - Slide 1: theme="corporate", template="content"
  - Slide 2: theme="minimal", template="title"
  - validation: SUCCESS

Rules tested: 2, 3, 4
```

---

### ✅ EXAMPLE 4: Single slide (no separator)

```markdown
GIVEN markdown with:
  ## Only Slide
  This is the only slide in the deck.

WHEN parsed

THEN:
  - SlideDeck has 1 Slide
  - Slide 1 heading: "Only Slide"
  - validation: SUCCESS

Rules tested: 1, 4
```

---

### ❌ EXAMPLE 5: Empty slide

```markdown
GIVEN markdown with:
  ## Slide 1
  Content.
  ---
  ---
  ## Slide 3
  Content.

WHEN parsed

THEN:
  - Validation: FAILED
  - Error: StructureError
  - Message: "Slide 2 is empty (no content between separators)"

Rules tested: 5
```

---

### ❌ EXAMPLE 6: Slide with only whitespace

```markdown
GIVEN markdown with:
  ## Slide 1
  Content.
  ---


  ---
  ## Slide 3
  Content.

WHEN parsed

THEN:
  - Validation: FAILED
  - Error: StructureError
  - Message: "Slide 2 is empty (only whitespace)"

Rules tested: 5
```

---

### ❌ EXAMPLE 7: Slide with only front matter

```markdown
GIVEN markdown with:
  ## Slide 1
  Content.
  ---
  ---
  template: content
  theme: minimal
  ---
  ---
  ## Slide 3
  Content.

WHEN parsed

THEN:
  - Validation: FAILED
  - Error: StructureError
  - Message: "Slide 2 has front matter but no content"

Rules tested: 5
```

---

### ❌ EXAMPLE 8: Duplicate slide IDs

```markdown
GIVEN markdown with:
  ---
  id: intro
  ---
  ## Introduction
  First intro slide.
  ---
  ---
  id: intro
  ---
  ## Another Introduction
  Second intro slide.

WHEN parsed

THEN:
  - Validation: FAILED
  - Error: StructureError
  - Message: "Duplicate slide ID 'intro' found on slides 1 and 2"

Rules tested: 6
```

---

### ✅ EXAMPLE 9: Separator inside code block

```markdown
GIVEN markdown with:
  ## Code Example

  Here's a YAML example:

  ```yaml
  ---
  theme: corporate
  ---
  ```

  This should all be one slide.

WHEN parsed

THEN:
  - SlideDeck has 1 Slide
  - Slide 1 contains code block with "---"
  - validation: SUCCESS

Rules tested: 1
```

---

### ✅ EXAMPLE 10: Large deck (stress test)

```markdown
GIVEN markdown file with 50 slides separated by "---"

WHEN parsed

THEN:
  - SlideDeck has 50 Slides
  - Parsing completes in < 100ms
  - validation: SUCCESS

Rules tested: 1, 4
```

---

### ✅ EXAMPLE 11: Auto-generated slide IDs

```markdown
GIVEN markdown with:
  ## Slide 1
  Content.
  ---
  ## Slide 2
  Content.
  ---
  ## Slide 3
  Content.

WHEN parsed

THEN:
  - Slide 1 has id: "slide-1"
  - Slide 2 has id: "slide-2"
  - Slide 3 has id: "slide-3"
  - validation: SUCCESS

Rules tested: 6
```

---

### ✅ EXAMPLE 12: Mixed user/auto IDs

```markdown
GIVEN markdown with:
  ---
  id: intro
  ---
  ## Slide 1
  Content.
  ---
  ## Slide 2
  Content (no id).
  ---
  ---
  id: conclusion
  ---
  ## Slide 3
  Content.

WHEN parsed

THEN:
  - Slide 1 has id: "intro"
  - Slide 2 has id: "slide-2"
  - Slide 3 has id: "conclusion"
  - validation: SUCCESS

Rules tested: 6
```

---

## 🔴 Red Cards: Questions (All Resolved ✅)

### Q1: Is single slide (no `---`) valid? ✅

**Answer**: Yes. A deck with 1 slide is valid. Separator is optional for single-slide decks.

**Rationale**: Some presentations are single-slide (e.g., poster, infographic). Should be supported.

---

### Q2: How many dashes? `---` vs `----`? ✅

**Answer**: Accept Flexmark's ThematicBreak node as separator. This includes `---`, `***`, `___` per CommonMark spec. Warn if non-standard separator is used.

**Rationale**: Flexmark handles this per spec. We shouldn't be more restrictive than CommonMark.

---

### Q3: Separator inside code/blockquote? ✅

**Answer**: Flexmark AST parsing handles this correctly. `---` inside fenced code block is NOT a ThematicBreak node, so won't split slides.

**Rationale**: Flexmark AST differentiates between ThematicBreak nodes and text content.

---

### Q4: Where can global front matter appear? ✅

**Answer**: Only at the very top of the file, before any slide content.

**Rationale**: Consistent with Jekyll, Docusaurus, and other static site generators.

---

### Q5: Text before first `---`? ✅

**Answer**: That text is the first slide's content. No global front matter in this case.

**Rationale**: YAML front matter must be the absolute first thing in the file.

---

### Q6: How to parse per-slide front matter? ✅

**Answer**: After splitting on separator, check if next block starts with `---` (YAML delimiter). If yes, parse YAML until closing `---`. Everything after is slide content.

**Rationale**: YAML front matter is always delimited by `---` ... `---`.

---

### Q7: Nested YAML in front matter? ✅

**Answer**: Yes, YAML supports nested structures. Parse entire YAML block.

**Example**:
```yaml
---
template: content
metadata:
  tags:
    - functional-programming
    - scala
  importance: high
---
```

---

### Q8: Malformed per-slide front matter? ✅

**Answer**: Parsing error. Fail with clear message: "Slide 3 has malformed front matter: <YAML error>"

**Rationale**: Better to fail fast than proceed with incorrect data.

---

### Q9: Auto-generated vs user-provided IDs? ✅

**Answer**: User can provide `id:` in front matter. If not provided, system auto-generates (e.g., `slide-1`, `slide-2`).

**Rationale**: Flexibility for users who want semantic IDs, convenience for those who don't.

---

### Q10: Duplicate slide IDs? ✅

**Answer**: Validation error: "Duplicate slide ID 'intro' found on slides 1 and 3"

**Rationale**: Slide IDs must be unique for cross-referencing, TOC generation, etc.

---

### Q11: What counts as "content"? ✅

**Answer**: Any non-whitespace text after front matter. Headings, paragraphs, lists, code blocks all count.

**Rationale**: Whitespace-only is not meaningful content.

---

### Q12: Heading-only slide valid? ✅

**Answer**: Depends on template. Title template requires only `title` slot (heading is enough). Content template requires `heading` + `body`.

**Rationale**: Template validation will catch this. Parsing layer doesn't enforce template-specific rules.

---

### Q13: Enforce exactly 3 dashes? ✅

**Answer**: Accept ThematicBreak node, warn if non-standard (e.g., `****`, `____`).

**Rationale**: CommonMark spec allows alternatives. Warn for clarity, but don't break parsing.

---

## 🎯 Story Readiness Assessment

### Coverage Summary

| Metric | Count |
|--------|-------|
| Rules | 6 blue cards |
| Examples | 12 green cards (7 success, 5 failure) |
| Questions | 13 red cards (all resolved ✅) |

### Rule → Example Coverage

```
Rule 1 (Slide separator):
├── Example 1 ✅ (3 slides)
├── Example 4 ✅ (single slide)
├── Example 9 ✅ (separator in code block)
└── Example 10 ✅ (50-slide stress test)

Rule 2 (Global front matter):
├── Example 2 ✅ (global theme + author)
└── Example 3 ✅ (with override)

Rule 3 (Per-slide override):
└── Example 3 ✅ (override theme + template)

Rule 4 (Slide order):
├── Example 1 ✅ (3 slides in order)
├── Example 2 ✅ (2 slides)
├── Example 3 ✅ (2 slides)
├── Example 4 ✅ (1 slide)
└── Example 10 ✅ (50 slides)

Rule 5 (Empty slides error):
├── Example 5 ❌ (empty slide)
├── Example 6 ❌ (whitespace only)
└── Example 7 ❌ (front matter only)

Rule 6 (Unique slide IDs):
├── Example 8 ❌ (duplicate IDs)
├── Example 11 ✅ (auto-generated IDs)
└── Example 12 ✅ (mixed user/auto IDs)
```

### Confidence Level

**HIGH CONFIDENCE** ✅

**Reasons**:
- All 13 questions resolved
- 6 clear business rules identified
- 12 concrete examples cover success and failure paths
- Examples cover edge cases (empty slides, code blocks, large decks, ID conflicts)
- Technical approach is clear (Flexmark ThematicBreak + YAML parsing)

**Risks Identified**:
- Flexmark configuration (must enable ThematicBreak extension)
- YAML parser choice (SnakeYAML vs circe-yaml)
- Performance for very large decks (100+ slides)

**Ready for TDD**: YES ✅

---

## 📋 Acceptance Criteria Summary

### Must Have (Critical)

1. ✅ Parse multiple slides separated by `---`
2. ✅ Single slide (no separator) is valid
3. ✅ Global front matter applies to all slides
4. ✅ Per-slide front matter overrides global
5. ✅ Slide order preserved
6. ✅ Empty slides trigger error
7. ✅ Duplicate IDs trigger error

### Should Have (Important)

8. ✅ Auto-generated IDs if not provided
9. ✅ Separator in code block doesn't split
10. ✅ Parsing 50 slides < 100ms

### Nice to Have (Enhancement)

11. ⚠️ Warn on non-standard separators (`****`, `____`)
12. ⚠️ Clear error messages with slide number

---

## 🎨 Visual Example Map

```
┌────────────────────────────────────────────────────────────┐
│                      📘 USER STORY                         │
│         Parse Multi-Slide Markdown File (US-003)           │
└────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R1 │          │ 🔵 R2 │          │ 🔵 R3 │
    │  ---  │          │Global │          │ Per-  │
    │ Split │          │ FM    │          │Slide  │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴───┬──────┐   ┌──┴──┐           ┌──┴──┐
    │ ✅ E1 │ ✅ E4│   │✅ E2│           │✅ E3│
    │ ✅ E9 │ ✅ E10   │     │           │     │
    └───────┴──────┘   └─────┘           └─────┘

    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R4 │          │ 🔵 R5 │          │ 🔵 R6 │
    │Order  │          │ Empty │          │ Unique│
    │Preserv│          │ Error │          │  IDs  │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴───┐          ┌───┴───┬──────┐   ┌──┴──┬──────┐
    │All Ex.│          │❌ E5  │❌ E6 │   │❌ E8│✅ E11│
    │✅     │          │❌ E7  │      │   │✅ E12│      │
    └───────┘          └───────┴──────┘   └─────┴──────┘

Questions (All ✅):
Q1-Q13: All resolved (see Red Cards section)
```

---

## 🔄 Next Steps

1. ✅ **Three Amigos Complete**: All rules and examples documented
2. ✅ **Example Mapping Complete**: 12 examples with full coverage
3. 🔄 **Update BACKLOG-V3.md**: Mark US-003 as Ready for Implementation
4. ⏭️ **Proceed to US-004**: Speaker Notes (next in sequence)

---

**Session Type**: Ceremony 2.2 - Example Mapping Workshop
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Story Status**: Ready for TDD ✅
