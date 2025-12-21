# Example Mapping: US-012 Density Validation ("Fits on Slide")

**Session Date**: 2024-12-20
**Participants**: Tony Moores (Business/Dev/QA)
**Story**: US-012 - Density Validation ("Fits on Slide")
**Status**: Complete

---

## 📖 Story Card

```
┌─────────────────────────────────────────────┐
│ 📘 USER STORY                               │
│                                             │
│ As a slide deck author                      │
│ I want to receive warnings when slide       │
│ content is too dense                        │
│ So that my slides are readable and fit on   │
│ screen                                      │
│                                             │
│ Priority: P0 (Blocker)                      │
│ KEY DIFFERENTIATOR from Marp                │
│ Estimated Effort: 3 days                    │
└─────────────────────────────────────────────┘
```

---

## 🔵 Blue Cards: Business Rules

### Rule 1: Body max 12 lines (theme-configurable)

Body content cannot exceed 12 lines (counted as newline characters in markdown).

**Threshold**: 12 lines (default), theme can override via `theme.layout.maxBodyLines`

**Rationale**: 12 lines fits comfortably on 16:9 slide with standard font size

**Error Type**: DensityWarning (non-blocking)

---

### Rule 2: Body max 150 words

Body content cannot exceed 150 words (counted as whitespace-separated tokens).

**Threshold**: 150 words (default), theme can override via `theme.layout.maxBodyWords`

**Rationale**: Slides should be sparse - audience reads vs listens if too dense

**Error Type**: DensityWarning (non-blocking)

---

### Rule 3: Heading max 80 characters

Heading text cannot exceed 80 characters (plain text, excluding markdown syntax).

**Threshold**: 80 chars (default), theme can override via `theme.layout.maxHeadingChars`

**Rationale**: Long headings don't fit on one line, reduce font size to fit

**Error Type**: DensityWarning (non-blocking)

---

### Rule 4: Code block max 20 lines

Code blocks cannot exceed 20 lines (including blank lines).

**Threshold**: 20 lines (default), theme can override via `theme.layout.maxCodeLines`

**Rationale**: Code should be excerpts, not full files. 20 lines fits on slide.

**Error Type**: DensityWarning (non-blocking)

---

### Rule 5: Bullet list max 6 items

Bullet lists cannot exceed 6 top-level items.

**Threshold**: 6 items (default), theme can override via `theme.layout.maxListItems`

**Rationale**: 7+ items = too dense. Use sub-bullets or split slides.

**Error Type**: DensityWarning (non-blocking)

---

### Rule 6: Nested bullet list max 2 levels deep

Bullet lists cannot be nested more than 2 levels deep.

**Threshold**: 2 levels (default), theme can override via `theme.layout.maxListDepth`

**Rationale**: 3+ levels = hard to read. Flatten structure or use separate slides.

**Error Type**: DensityWarning (non-blocking)

---

### Rule 7: Warnings are non-blocking

Density warnings do NOT prevent slide deck from being processed. Author can proceed with dense slides if intentional.

**Rationale**: Warnings guide authors toward best practices but don't enforce strict limits (flexibility for special cases).

---

## 🟢 Green Cards: Examples

### ✅ EXAMPLE 1: Valid slide (no warnings)

```markdown
GIVEN markdown with:
  ---
  template: content
  ---
  ## Key Takeaways

  - Insight 1
  - Insight 2
  - Insight 3
  - Insight 4
  - Insight 5

WHEN parsed and validated

THEN:
  - Structure validation: SUCCESS
  - Density validation: SUCCESS (no warnings)
  - Body: 5 lines (under 12)
  - Body: 10 words (under 150)
  - Heading: 13 chars (under 80)
  - List: 5 items (under 6)

Rules tested: 1, 2, 3, 5
```

---

### ⚠️ EXAMPLE 2: Body exceeds 12 lines

```markdown
GIVEN markdown with:
  ## Long Content

  Line 1
  Line 2
  Line 3
  Line 4
  Line 5
  Line 6
  Line 7
  Line 8
  Line 9
  Line 10
  Line 11
  Line 12
  Line 13

WHEN parsed and validated

THEN:
  - Structure validation: SUCCESS
  - Density validation: SUCCESS with 1 warning
  - Warning: DensityWarning
  - Message: "Slide 1 body exceeds max 12 lines (found 13 lines). Consider splitting into multiple slides."

Rules tested: 1
```

---

### ⚠️ EXAMPLE 3: Body exceeds 150 words

```markdown
GIVEN markdown file with body containing 200 words

WHEN parsed and validated

THEN:
  - Density validation: SUCCESS with 1 warning
  - Warning: DensityWarning
  - Message: "Slide 1 body exceeds max 150 words (found 200 words). Consider condensing content or splitting into 2 slides."

Rules tested: 2
```

---

### ⚠️ EXAMPLE 4: Heading exceeds 80 chars

```markdown
GIVEN markdown with:
  ## This is an extremely long heading that exceeds the eighty character limit and should trigger a density warning

  Content here.

WHEN parsed and validated

THEN:
  - Density validation: SUCCESS with 1 warning
  - Warning: DensityWarning
  - Message: "Slide 1 heading exceeds max 80 chars (found 120 chars). Shorten heading."

Rules tested: 3
```

---

### ⚠️ EXAMPLE 5: Code block exceeds 20 lines

```markdown
GIVEN markdown with:
  ## Code Example

  ```scala
  // Line 1
  // Line 2
  // ...
  // Line 25
  ```

WHEN parsed and validated

THEN:
  - Density validation: SUCCESS with 1 warning
  - Warning: DensityWarning
  - Message: "Slide 1 code block exceeds max 20 lines (found 25 lines). Show excerpt or split into multiple slides."

Rules tested: 4
```

---

### ⚠️ EXAMPLE 6: Bullet list exceeds 6 items

```markdown
GIVEN markdown with:
  ## Too Many Items

  - Item 1
  - Item 2
  - Item 3
  - Item 4
  - Item 5
  - Item 6
  - Item 7
  - Item 8

WHEN parsed and validated

THEN:
  - Density validation: SUCCESS with 1 warning
  - Warning: DensityWarning
  - Message: "Slide 1 bullet list exceeds max 6 items (found 8 items). Split into multiple slides or use sub-bullets."

Rules tested: 5
```

---

### ⚠️ EXAMPLE 7: Nested list exceeds 2 levels

```markdown
GIVEN markdown with:
  ## Nested List

  - Level 1
    - Level 2
      - Level 3 (too deep!)

WHEN parsed and validated

THEN:
  - Density validation: SUCCESS with 1 warning
  - Warning: DensityWarning
  - Message: "Slide 1 has bullet list nested 3 levels deep (max 2). Flatten list structure."

Rules tested: 6
```

---

### ⚠️ EXAMPLE 8: Multiple density warnings

```markdown
GIVEN markdown with:
  - Heading: 90 chars (exceeds 80)
  - Body: 15 lines (exceeds 12)
  - Body: 180 words (exceeds 150)
  - List: 8 items (exceeds 6)

WHEN parsed and validated

THEN:
  - Density validation: SUCCESS with 4 warnings
  - Warnings:
    1. Heading exceeds max 80 chars
    2. Body exceeds max 12 lines
    3. Body exceeds max 150 words
    4. Bullet list exceeds max 6 items

Rules tested: 1, 2, 3, 5, 7
```

---

### ✅ EXAMPLE 9: Theme overrides default limits

```markdown
GIVEN theme with:
  layout:
    maxBodyLines: 15
    maxBodyWords: 200

GIVEN markdown with:
  - Body: 14 lines
  - Body: 180 words

WHEN parsed and validated with theme

THEN:
  - Density validation: SUCCESS (no warnings)
  - 14 lines under theme limit (15)
  - 180 words under theme limit (200)

Rules tested: 1, 2
```

---

### ✅ EXAMPLE 10: Boundary test (exactly at limit)

```markdown
GIVEN markdown with:
  - Body: exactly 12 lines
  - Body: exactly 150 words
  - Heading: exactly 80 chars
  - Code: exactly 20 lines
  - List: exactly 6 items
  - Nesting: exactly 2 levels

WHEN parsed and validated

THEN:
  - Density validation: SUCCESS (no warnings)
  - All content at limits (not exceeding)

Rules tested: 1, 2, 3, 4, 5, 6
```

---

### ⚠️ EXAMPLE 11: Code block blank lines count

```markdown
GIVEN markdown with:
  ```scala
  val x = 1

  val y = 2

  val z = 3
  ```
  (5 lines total including 2 blank lines)

WHEN parsed and validated

THEN:
  - Code block: 5 lines
  - Density validation: SUCCESS (no warnings, under 20 lines)

Rules tested: 4
```

---

### ⚠️ EXAMPLE 12: Multiple code blocks

```markdown
GIVEN markdown with:
  ```scala
  // Code block 1 (15 lines)
  ```

  ```scala
  // Code block 2 (15 lines)
  ```

WHEN parsed and validated

THEN:
  - Each code block validated separately
  - Both under 20 lines
  - Density validation: SUCCESS (no warnings)

Rules tested: 4
```

---

## 🔴 Red Cards: Questions (All Resolved ✅)

### Q1: How to count lines in markdown? ✅

**Answer**: Count newline characters `\n` in markdown text. Blank lines count.

---

### Q2: Do bullet list items count as lines? ✅

**Answer**: Yes. Each list item is a line for line count purposes.

---

### Q3: Code block count toward body limit? ✅

**Answer**: No. Code blocks have separate limit (20 lines). Don't count toward body line limit.

---

### Q4: How to count words? ✅

**Answer**: Split on whitespace `\s+`, count tokens. Markdown syntax counts (e.g., `**bold**` = 1 word).

---

### Q5: Links count as multiple words? ✅

**Answer**: Link text counts, syntax doesn't. `[click here](url)` = 2 words ("click here").

---

### Q6: Inline code count? ✅

**Answer**: Yes, counts as 1 word. `` `grep` `` = 1 word.

---

### Q7: Include markdown syntax in char count? ✅

**Answer**: No. Count plain text only for headings. `**Bold Heading**` = 12 chars, not 16.

---

### Q8: Emoji in headings? ✅

**Answer**: Each emoji = 1 char (for simplicity). Theme can disable emoji if needed.

---

### Q9: Count blank lines in code? ✅

**Answer**: Yes. Blank lines in code blocks count toward 20-line limit.

---

### Q10: Multiple code blocks on one slide? ✅

**Answer**: Each code block validated separately. Both must be under 20 lines individually.

---

### Q11: What counts as list item? ✅

**Answer**: Top-level items only. `- Item` or `1. Item`. Nested items counted for nesting depth (Rule 6), not item count.

---

### Q12: Numbered vs bullet lists? ✅

**Answer**: Same limit (6 items) for both unordered (`-`) and ordered (`1.`) lists.

---

### Q13: Detect nesting depth? ✅

**Answer**: Count leading spaces/tabs in markdown. 0-1 spaces = level 1, 2-3 spaces = level 2, 4+ spaces = level 3 (warning).

---

### Q14: 3-level nesting error or warning? ✅

**Answer**: Warning only. Some decks may intentionally use deep nesting (e.g., detailed course outlines).

---

### Q15: Theme with invalid limit? ✅

**Answer**: Theme validation (US-009) catches invalid limits before density validation runs. Assume theme is valid.

---

### Q16: Theme disable density checks? ✅

**Answer**: Yes. Set very high limits (e.g., `maxBodyLines: 999`) or use `densityValidation: false` flag in theme.

---

## 🎯 Story Readiness Assessment

### Coverage Summary

| Metric | Count |
|--------|-------|
| Rules | 7 blue cards |
| Examples | 12 green cards (3 success, 9 warning) |
| Questions | 16 red cards (all resolved ✅) |

### Rule → Example Coverage

```
Rule 1 (Body max 12 lines):
├── Example 1 ✅ (5 lines, no warning)
├── Example 2 ⚠️ (13 lines, warning)
├── Example 9 ✅ (theme override)
└── Example 10 ✅ (exactly 12 lines)

Rule 2 (Body max 150 words):
├── Example 1 ✅ (10 words, no warning)
├── Example 3 ⚠️ (200 words, warning)
├── Example 9 ✅ (theme override)
└── Example 10 ✅ (exactly 150 words)

Rule 3 (Heading max 80 chars):
├── Example 1 ✅ (13 chars, no warning)
├── Example 4 ⚠️ (120 chars, warning)
└── Example 10 ✅ (exactly 80 chars)

Rule 4 (Code max 20 lines):
├── Example 5 ⚠️ (25 lines, warning)
├── Example 10 ✅ (exactly 20 lines)
├── Example 11 ⚠️ (blank lines count)
└── Example 12 ⚠️ (multiple blocks)

Rule 5 (List max 6 items):
├── Example 1 ✅ (5 items, no warning)
├── Example 6 ⚠️ (8 items, warning)
└── Example 10 ✅ (exactly 6 items)

Rule 6 (Nesting max 2 levels):
├── Example 7 ⚠️ (3 levels, warning)
└── Example 10 ✅ (exactly 2 levels)

Rule 7 (Warnings non-blocking):
└── Example 8 ⚠️ (multiple warnings, still succeeds)
```

### Confidence Level

**HIGH CONFIDENCE** ✅

**Reasons**:
- All 16 questions resolved
- 7 clear business rules identified
- 12 concrete examples cover success, warning, and boundary cases
- Examples cover theme overrides and multiple violations
- Clear rationale for each density limit
- Non-blocking warnings allow author flexibility

**Risks Identified**:
- Line/word counting implementation (must be accurate)
- Theme integration (loading limits from theme JSON)
- Performance for large decks (counting operations on 200 slides)

**Ready for TDD**: YES ✅

---

## 📋 Acceptance Criteria Summary

### Must Have (Critical)

1. ✅ Body > 12 lines → DensityWarning
2. ✅ Body > 150 words → DensityWarning
3. ✅ Heading > 80 chars → DensityWarning
4. ✅ Code block > 20 lines → DensityWarning
5. ✅ List > 6 items → DensityWarning
6. ✅ Nesting > 2 levels → DensityWarning
7. ✅ Warnings are non-blocking (deck still valid)
8. ✅ Multiple warnings collected

### Should Have (Important)

9. ✅ Theme can override limits
10. ✅ Boundary cases (exactly at limit) → no warning
11. ✅ Warning messages actionable (suggest fixes)
12. ✅ Warning messages include actual vs max

### Nice to Have (Enhancement)

13. ✅ Validation < 100ms for 50-slide deck
14. ⚠️ Theme can disable density checks entirely

---

## 🎨 Visual Example Map

```
┌────────────────────────────────────────────────────────────┐
│                      📘 USER STORY                         │
│        Density Validation "Fits on Slide" (US-012)        │
│              KEY DIFFERENTIATOR from Marp                  │
└────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R1 │          │ 🔵 R2 │          │ 🔵 R3 │
    │Body 12│          │Body150│          │Head 80│
    │ Lines │          │ Words │          │ Chars │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴───┬──────┐   ┌──┴──┬──────┐   ┌──┴──┐
    │✅ E1  │⚠️ E2 │   │✅ E1│⚠️ E3 │   │✅ E1│
    │✅ E9  │✅ E10│   │✅ E9│✅ E10│   │⚠️ E4│
    └───────┴──────┘   └─────┴──────┘   │✅ E10│
                                        └──────┘

    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R4 │          │ 🔵 R5 │          │ 🔵 R6 │
    │Code 20│          │List 6 │          │Nest 2 │
    │ Lines │          │ Items │          │Levels │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴───┬──────┐   ┌──┴──┬──────┐   ┌──┴──┐
    │⚠️ E5  │✅ E10│   │✅ E1│⚠️ E6 │   │⚠️ E7│
    │⚠️ E11 │⚠️ E12│   │✅ E10│     │   │✅ E10│
    └───────┴──────┘   └─────┴──────┘   └─────┘

    ┌───────┐
    │ 🔵 R7 │
    │ Non-  │
    │Block  │
    └───┬───┘
        │
    ┌───┴───┐
    │⚠️ E8  │
    │(4 warn│
    │still ✅│
    └───────┘

Questions (All ✅):
Q1-Q16: All resolved (see Red Cards section)
```

---

## 🔄 Next Steps

1. ✅ **Three Amigos Complete**: All rules and examples documented
2. ✅ **Example Mapping Complete**: 12 examples with full coverage
3. 🔄 **Update BACKLOG-V3.md**: Mark US-012 as Ready for Implementation
4. ⏭️ **Proceed to US-013**: Content Validation (next in validation framework)

---

**Session Type**: Ceremony 2.2 - Example Mapping Workshop
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Story Status**: Ready for TDD ✅
