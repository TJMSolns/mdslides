# Three Amigos Session #012
## Density Validation ("Fits on Slide")

---

```yaml
# MACHINE-READABLE METADATA
session:
  id: 3A-012-DENSITY-VALIDATION
  date: 2024-12-20
  user_story_id: US-012
  participants:
    - Tony Moores (Business/Dev/QA)
  duration_minutes: 60-75
  status: complete

story:
  title: Density Validation ("Fits on Slide")
  type: Feature
  priority: P0 (Blocker) - KEY DIFFERENTIATOR
  epic: Validation Framework
```

---

## 📋 User Story

**As a** slide deck author
**I want to** receive warnings when slide content is too dense
**So that** my slides are readable and fit on screen

**Business Value**: Core differentiator from Marp - prevents "wall of text" slides that are unreadable

**Technical Scope**: Validates content density using "fits on slide" heuristics (line counts, word counts, item counts)

---

## 🎭 Three Perspectives

### 👔 Business Perspective (Product Owner)

**What success looks like**:
- Author gets clear warnings when slides are too dense
- Warnings explain WHAT is too dense and BY HOW MUCH
- Warnings suggest HOW to fix (e.g., "Split into 2 slides" or "Reduce to 6 bullet points")
- Warnings are NON-BLOCKING (can proceed with dense slides if intentional)
- Common density issues caught:
  - Body text exceeds 12 lines
  - Body text exceeds 150 words
  - Heading exceeds 80 characters
  - Code block exceeds 20 lines
  - Bullet list exceeds 6 items
  - Nested bullets exceed 2 levels deep

**Why this matters**:
- **Readability**: Dense slides are hard to read from back of room
- **Audience Engagement**: Too much text = audience reads instead of listens
- **Professional Quality**: Sparse slides look more professional
- **Competitive Advantage**: Marp doesn't enforce this - we do

**Acceptance criteria**:
- ⚠️ Body > 12 lines → DensityWarning
- ⚠️ Body > 150 words → DensityWarning
- ⚠️ Heading > 80 chars → DensityWarning
- ⚠️ Code block > 20 lines → DensityWarning
- ⚠️ Bullet list > 6 items → DensityWarning
- ⚠️ Nested bullets > 2 levels → DensityWarning (not error)

---

### 💻 Development Perspective (Technical)

**Implementation approach**:
1. **After Structure Validation**: Run density validation on valid SlideDeck
2. **For Each Slide**:
   - Count body lines (how: count `\n` in markdown)
   - Count body words (how: split on whitespace, count tokens)
   - Count heading chars (how: string length of heading text)
   - Count code block lines (how: count lines in fenced code blocks)
   - Count bullet list items (how: count `- ` or `1. ` at start of lines)
   - Check nesting depth (how: count leading spaces/tabs in markdown)
3. **Compare to Thresholds**:
   - Default thresholds: body 12 lines, 150 words, heading 80 chars, etc.
   - Theme can override: `theme.layout.maxBodyLines`, etc.
4. **Collect Warnings**: Use `List[DensityWarning]` (warnings don't block)
5. **Return Result**: `Either[NonEmptyList[ValidationError], (SlideDeck, List[DensityWarning])]`

**Technical risks**:
- **Line Counting**: How to count lines with word-wrapped text?
  - Answer: Count newline characters `\n` in raw markdown (not rendered line count)
- **Word Counting**: How to handle inline code, links, markdown syntax?
  - Answer: Split on whitespace, count tokens. Markdown syntax counts as words.
- **Theme Integration**: What if theme doesn't specify limits?
  - Answer: Use hard-coded defaults. Theme overrides are optional.
- **Nested List Depth**: How to detect nesting in markdown?
  - Answer: Count leading spaces/tabs. 2+ spaces = 1 level deeper.

**Dependencies**:
- Structure Validation (must pass before density check)
- Theme Loading (to get configurable thresholds)
- Markdown parser (Flexmark) to analyze structure

---

### 🧪 Testing Perspective (Quality/Edge Cases)

**Happy path scenarios**:
1. Slide with 10 lines, 100 words → no warnings
2. Slide with 5-item bullet list → no warnings
3. Slide with 15-char heading → no warnings
4. Slide with 10-line code block → no warnings

**Edge cases (warnings)**:
1. Body with 13 lines (exceeds 12) → warning
2. Body with 200 words (exceeds 150) → warning
3. Heading with 85 chars (exceeds 80) → warning
4. Code block with 25 lines (exceeds 20) → warning
5. Bullet list with 8 items (exceeds 6) → warning
6. Nested bullets 3 levels deep (exceeds 2) → warning
7. Slide with MULTIPLE density issues → multiple warnings

**Boundary cases**:
1. Body with exactly 12 lines → no warning (at limit)
2. Body with exactly 150 words → no warning (at limit)
3. Heading with exactly 80 chars → no warning (at limit)

**Non-functional requirements**:
- Validation completes in < 100ms for 50-slide deck
- Warning messages are actionable (suggest how to fix)
- Warnings include actual vs max (e.g., "found 13 lines, max 12")

---

## 🗂️ Example Mapping

### Rule 1: Body max 12 lines (theme-configurable)

**Examples**:
- ✅ **Valid**: Body with 10 lines
- ✅ **Valid**: Body with exactly 12 lines (at limit)
- ⚠️ **Warning**: Body with 13 lines
  → DensityWarning: "Slide 2 body exceeds max 12 lines (found 13 lines). Consider splitting into multiple slides."
- ⚠️ **Warning**: Body with 20 lines
  → DensityWarning: "Slide 2 body exceeds max 12 lines (found 20 lines). Split into 2 slides."

**Questions**:
- Q1: How to count lines in markdown?
  - **Decision**: Count newline characters `\n` in markdown text. Blank lines count.
- Q2: Do bullet list items count as lines?
  - **Decision**: Yes. Each list item is a line.
- Q3: Does code block count toward body line limit?
  - **Decision**: Code blocks have separate limit (max 20 lines). Don't count toward body limit.

---

### Rule 2: Body max 150 words

**Examples**:
- ✅ **Valid**: Body with 100 words
- ✅ **Valid**: Body with exactly 150 words (at limit)
- ⚠️ **Warning**: Body with 160 words
  → DensityWarning: "Slide 2 body exceeds max 150 words (found 160 words). Consider condensing content."
- ⚠️ **Warning**: Body with 300 words
  → DensityWarning: "Slide 2 body exceeds max 150 words (found 300 words). Split into 3 slides."

**Questions**:
- Q4: How to count words?
  - **Decision**: Split on whitespace `\s+`, count tokens. Markdown syntax counts (e.g., `**bold**` = 1 word, not 3 tokens).
- Q5: Do links count as multiple words?
  - **Decision**: Link text counts. Syntax doesn't. `[click here](url)` = 2 words ("click here").
- Q6: Does inline code count?
  - **Decision**: Yes. `` `grep` `` = 1 word.

---

### Rule 3: Heading max 80 chars

**Examples**:
- ✅ **Valid**: Heading with 50 chars
- ✅ **Valid**: Heading with exactly 80 chars (at limit)
- ⚠️ **Warning**: Heading with 85 chars
  → DensityWarning: "Slide 2 heading exceeds max 80 chars (found 85 chars). Shorten heading."
- ⚠️ **Warning**: Heading with 120 chars
  → DensityWarning: "Slide 2 heading exceeds max 80 chars (found 120 chars). Heading too verbose."

**Questions**:
- Q7: Include markdown syntax in char count?
  - **Decision**: No. Count plain text only. `**Bold Heading**` = 12 chars, not 16.
- Q8: What about emoji in headings?
  - **Decision**: Each emoji = 1 char (for simplicity). Theme can disable emoji if needed.

---

### Rule 4: Code block max 20 lines

**Examples**:
- ✅ **Valid**: Code block with 15 lines
- ✅ **Valid**: Code block with exactly 20 lines (at limit)
- ⚠️ **Warning**: Code block with 25 lines
  → DensityWarning: "Slide 2 code block exceeds max 20 lines (found 25 lines). Show excerpt or split into multiple slides."

**Questions**:
- Q9: Count blank lines in code block?
  - **Decision**: Yes. Blank lines in code count toward limit.
- Q10: Multiple code blocks on one slide?
  - **Decision**: Each code block validated separately. If slide has 2 code blocks (10 lines each), both pass.

---

### Rule 5: Bullet list max 6 items

**Examples**:
- ✅ **Valid**: Bullet list with 5 items
- ✅ **Valid**: Bullet list with exactly 6 items (at limit)
- ⚠️ **Warning**: Bullet list with 8 items
  → DensityWarning: "Slide 2 bullet list exceeds max 6 items (found 8 items). Split into multiple slides or use sub-bullets."

**Questions**:
- Q11: What counts as a "list item"?
  - **Decision**: Top-level items only. `- Item` or `1. Item`. Nested items counted separately (Rule 6).
- Q12: Numbered lists vs bullet lists?
  - **Decision**: Same limit (6 items) for both.

---

### Rule 6: Nested bullet list max 2 levels deep

**Examples**:
- ✅ **Valid**: 1-level list (no nesting)
- ✅ **Valid**: 2-level nested list
  ```markdown
  - Level 1
    - Level 2
  ```
- ⚠️ **Warning**: 3-level nested list
  ```markdown
  - Level 1
    - Level 2
      - Level 3 (too deep!)
  ```
  → DensityWarning: "Slide 2 has bullet list nested 3 levels deep (max 2). Flatten list structure."

**Questions**:
- Q13: How to detect nesting depth in markdown?
  - **Decision**: Count leading spaces/tabs. 0-1 spaces = level 1, 2-3 spaces = level 2, 4+ spaces = level 3 (too deep).
- Q14: Is 3-level nesting an error or warning?
  - **Decision**: Warning only. Some decks may intentionally use deep nesting (e.g., detailed outlines).

---

### Rule 7: Theme can override default limits

**Examples**:
- ✅ **Theme Override**: `theme.layout.maxBodyLines: 15` → body limit is 15 lines (not 12)
- ✅ **Theme Override**: `theme.layout.maxBodyWords: 200` → body limit is 200 words (not 150)
- ✅ **No Theme Override**: Use defaults (12 lines, 150 words, 80 chars, 20 code lines, 6 items, 2 levels)

**Questions**:
- Q15: What if theme specifies invalid limit (e.g., -1 lines)?
  - **Decision**: Theme validation (US-009) catches this. Density validation assumes theme is already validated.
- Q16: Can theme disable density checks entirely?
  - **Decision**: Yes. Set `maxBodyLines: 999` or similar high value. Or `densityValidation: false` flag.

---

## 📝 Concrete Examples (Given/When/Then)

### Example 1: Valid Slide (No Warnings)

```gherkin
Feature: Density Validation

  Scenario: Slide within all density limits
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Key Takeaways

      - Insight 1
      - Insight 2
      - Insight 3
      - Insight 4
      - Insight 5
      """
    When I parse and validate the markdown
    Then structure validation succeeds
    And density validation succeeds with no warnings
```

---

### Example 2: Body Exceeds 12 Lines ⚠️

```gherkin
  Scenario: Body exceeds max 12 lines
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
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
      """
    When I parse and validate the markdown
    Then structure validation succeeds
    And density validation succeeds with 1 warning
    And the warning is DensityWarning
    And the warning message contains "body exceeds max 12 lines (found 13 lines)"
```

---

### Example 3: Body Exceeds 150 Words ⚠️

```gherkin
  Scenario: Body exceeds max 150 words
    Given I have a markdown file with body containing 200 words
    When I parse and validate the markdown
    Then density validation succeeds with 1 warning
    And the warning message contains "body exceeds max 150 words (found 200 words)"
```

---

### Example 4: Heading Exceeds 80 Chars ⚠️

```gherkin
  Scenario: Heading exceeds max 80 characters
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## This is an extremely long heading that exceeds the eighty character limit and should trigger a density warning

      Content here.
      """
    When I parse and validate the markdown
    Then density validation succeeds with 1 warning
    And the warning message contains "heading exceeds max 80 chars"
```

---

### Example 5: Code Block Exceeds 20 Lines ⚠️

```gherkin
  Scenario: Code block exceeds max 20 lines
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Code Example

      ```scala
      // 25 lines of code here...
      (lines 1-25)
      ```
      """
    When I parse and validate the markdown
    Then density validation succeeds with 1 warning
    And the warning message contains "code block exceeds max 20 lines (found 25 lines)"
```

---

### Example 6: Bullet List Exceeds 6 Items ⚠️

```gherkin
  Scenario: Bullet list exceeds max 6 items
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Too Many Items

      - Item 1
      - Item 2
      - Item 3
      - Item 4
      - Item 5
      - Item 6
      - Item 7
      - Item 8
      """
    When I parse and validate the markdown
    Then density validation succeeds with 1 warning
    And the warning message contains "bullet list exceeds max 6 items (found 8 items)"
```

---

### Example 7: Nested List Exceeds 2 Levels ⚠️

```gherkin
  Scenario: Nested list exceeds max 2 levels deep
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Nested List

      - Level 1
        - Level 2
          - Level 3 (too deep!)
      """
    When I parse and validate the markdown
    Then density validation succeeds with 1 warning
    And the warning message contains "nested 3 levels deep (max 2)"
```

---

### Example 8: Multiple Density Warnings

```gherkin
  Scenario: Multiple density violations on one slide
    Given I have a markdown file with:
      - Heading with 90 chars (exceeds 80)
      - Body with 15 lines (exceeds 12)
      - Body with 180 words (exceeds 150)
      - Bullet list with 8 items (exceeds 6)
    When I parse and validate the markdown
    Then density validation succeeds with 4 warnings
    And warnings include heading, body lines, body words, and list items
```

---

### Example 9: Theme Overrides Default Limits

```gherkin
  Scenario: Theme increases body line limit to 15
    Given I have a theme with maxBodyLines: 15
    And I have a markdown file with body containing 14 lines
    When I parse and validate the markdown with the theme
    Then density validation succeeds with no warnings
```

---

### Example 10: Boundary Test (Exactly at Limit)

```gherkin
  Scenario: Content exactly at limits triggers no warning
    Given I have a markdown file with:
      - Body with exactly 12 lines
      - Body with exactly 150 words
      - Heading with exactly 80 chars
    When I parse and validate the markdown
    Then density validation succeeds with no warnings
```

---

## 🚧 Open Questions

| ID | Question | Status | Decision |
|----|----------|--------|----------|
| Q1 | How to count lines in markdown? | ✅ Resolved | Count `\n` newline characters (blank lines count) |
| Q2 | Do bullet list items count as lines? | ✅ Resolved | Yes, each item is a line |
| Q3 | Code block count toward body limit? | ✅ Resolved | No, separate limit (20 lines) |
| Q4 | How to count words? | ✅ Resolved | Split on whitespace, count tokens |
| Q5 | Links count as multiple words? | ✅ Resolved | Link text counts, syntax doesn't |
| Q6 | Inline code count? | ✅ Resolved | Yes, counts as 1 word |
| Q7 | Include markdown syntax in char count? | ✅ Resolved | No, plain text only |
| Q8 | Emoji in headings? | ✅ Resolved | Each emoji = 1 char |
| Q9 | Count blank lines in code? | ✅ Resolved | Yes |
| Q10 | Multiple code blocks on one slide? | ✅ Resolved | Each validated separately |
| Q11 | What counts as list item? | ✅ Resolved | Top-level items only |
| Q12 | Numbered vs bullet lists? | ✅ Resolved | Same limit (6 items) |
| Q13 | Detect nesting depth? | ✅ Resolved | Count leading spaces/tabs |
| Q14 | 3-level nesting error or warning? | ✅ Resolved | Warning only |
| Q15 | Theme with invalid limit? | ✅ Resolved | Theme validation catches (US-009) |
| Q16 | Theme disable density checks? | ✅ Resolved | Yes, set high limits or flag |

---

## ✅ Acceptance Criteria (Definition of Done)

### Functional Criteria

1. ✅ **AC1**: Body > 12 lines → DensityWarning
2. ✅ **AC2**: Body > 150 words → DensityWarning
3. ✅ **AC3**: Heading > 80 chars → DensityWarning
4. ✅ **AC4**: Code block > 20 lines → DensityWarning
5. ✅ **AC5**: Bullet list > 6 items → DensityWarning
6. ✅ **AC6**: Nested list > 2 levels → DensityWarning
7. ✅ **AC7**: Content at exact limit → no warning
8. ✅ **AC8**: Multiple violations → multiple warnings collected
9. ✅ **AC9**: Theme can override default limits

### Technical Criteria

10. ✅ **AC10**: Warnings returned as `List[DensityWarning]` (not errors)
11. ✅ **AC11**: Line counting uses `\n` newline characters
12. ✅ **AC12**: Word counting splits on whitespace
13. ✅ **AC13**: Heading char count excludes markdown syntax
14. ✅ **AC14**: Code block line count includes blank lines
15. ✅ **AC15**: List item count is top-level only
16. ✅ **AC16**: Nesting depth detected via leading spaces
17. ✅ **AC17**: Theme limits loaded from `theme.layout.*` fields
18. ✅ **AC18**: All domain terms from ubiquitous language used

### Non-Functional Criteria

19. ✅ **AC19**: Validation < 100ms for 50-slide deck
20. ✅ **AC20**: Warning messages are actionable (suggest fixes)
21. ✅ **AC21**: Warnings include actual vs max values
22. ✅ **AC22**: Pure functional code (no side effects)

**Scenarios**: 10 concrete examples documented
- 2 success paths (no warnings)
- 7 warning paths (density violations)
- 1 boundary test (exactly at limit)

**Dependencies**:
- Structure Validation (must pass first)
- Theme Loading (for configurable limits)
- Markdown parser (Flexmark) for structure analysis

---

## 📚 Related Artifacts

- **User Story Tracker**: [BACKLOG-V3.md](../../BACKLOG-V3.md)
- **Domain Model**: [slide-deck-aggregate.md](../domain-models/aggregates/slide-deck-aggregate.md)
- **Ubiquitous Language**: [ubiquitous-language.md](../domain-models/ubiquitous-language.md)
- **Related Stories**: US-011 Structure Validation, US-013 Content Validation
- **Theme Definition**: US-008 Apply Theme, US-009 Custom Theme Validation

---

## 🎯 Next Steps

1. **Create Example Mapping visual** (Ceremony 2.2)
2. **Document formal acceptance criteria** in BACKLOG-V3.md
3. **Proceed to Ceremony 2.2**: Example Mapping Workshop (refine scenarios)

---

**Session Type**: Ceremony 2.1 - Three Amigos Session
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Next Review**: After Example Mapping Workshop (Ceremony 2.2)
