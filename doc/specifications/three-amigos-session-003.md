# Three Amigos Session #003
## Parse Multi-Slide Markdown File

---

```yaml
# MACHINE-READABLE METADATA
session:
  id: 3A-003-MULTI-SLIDE-PARSING
  date: 2024-12-20
  user_story_id: US-003
  participants:
    - Tony Moores (Business/Dev/QA)
  duration_minutes: 45-60
  status: complete

story:
  title: Parse Multi-Slide Markdown File
  type: Feature
  priority: P0 (Blocker)
  epic: Core Slide Deck Creation
```

---

## 📋 User Story

**As a** slide deck author
**I want to** create multiple slides in one Markdown file
**So that** I can author complete presentations in a single document

**Business Value**: Foundational for all presentations - single-file authoring is the core workflow

**Technical Scope**: Tests the slide parsing pipeline, separator detection, and front matter inheritance

---

## 🎭 Three Perspectives

### 👔 Business Perspective (Product Owner)

**What success looks like**:
- Author writes one markdown file with multiple slides separated by `---`
- First slide can use global front matter (deck-level settings)
- Each slide can override with per-slide front matter
- Slide order is preserved exactly as authored
- Output is a validated SlideDeck with multiple Slide aggregates

**Acceptance criteria**:
- ✅ Parse markdown file with 5 slides separated by `---`
- ✅ Global front matter at top of file applies to entire deck
- ✅ Per-slide front matter overrides global settings for that slide
- ✅ Slide order preserved (slide 1 renders before slide 2, etc.)
- ✅ Empty slide content triggers validation error
- ✅ Malformed separators handled gracefully

---

### 💻 Development Perspective (Technical)

**Implementation approach**:
1. **Parse Markdown**: Use Flexmark to parse raw Markdown into AST
2. **Split on Separator**: Detect `---` (thematic break) as slide separator
3. **Extract Global Front Matter**: Parse YAML at top of file (before first slide)
4. **Parse Each Slide**:
   - Check for per-slide front matter (YAML block immediately after separator)
   - Merge per-slide front matter with global front matter (per-slide wins)
   - Extract slide content (everything after front matter until next separator)
5. **Build SlideDeck**: Construct SlideDeck aggregate with list of Slide aggregates
6. **Validate**: Check slide count > 0, each slide has content, etc.

**Technical risks**:
- **Separator ambiguity**: `---` is YAML front matter delimiter AND slide separator
  - How to distinguish YAML front matter from slide separator?
  - Answer: YAML must be at very top of file or immediately after `---` separator
- **Front matter inheritance**: Global vs per-slide precedence rules
- **Empty slides**: What counts as "empty"? (whitespace only? no content after heading?)
- **Separator variants**: `---`, `----`, `-----` - which are valid?
- **Nested front matter**: Can per-slide front matter have nested YAML?

**Dependencies**:
- Flexmark Markdown parser (Anticorruption Layer)
- YAML parser (SnakeYAML or similar)
- Template Library (to resolve `template:` field)
- Ubiquitous Language types (SlideDeck, Slide, SlideId, etc.)

---

### 🧪 Testing Perspective (Quality/Edge Cases)

**Happy path scenarios**:
1. Deck with 3 slides, no front matter (use defaults)
2. Deck with 5 slides, global front matter only
3. Deck with 3 slides, per-slide front matter only (no global)
4. Deck with 5 slides, global + per-slide front matter (per-slide overrides)
5. Deck with 10 slides (stress test slide count)

**Edge cases**:
1. Single slide (no separator) - is this valid?
2. Empty slide (separator followed immediately by separator)
3. Slide with only whitespace
4. Slide with only heading, no body
5. Malformed separator: `----` (4 dashes), `--` (2 dashes), `---   ` (trailing spaces)
6. Front matter in middle of slide content (not at top)
7. Separator inside code block (should not split slide)
8. Separator inside blockquote (should not split slide)
9. Very long deck (100+ slides) - performance test
10. Slide with only front matter, no content

**Non-functional requirements**:
- Parsing performance: < 100ms for 50-slide deck
- Memory usage: O(n) where n = number of slides
- Error messages must indicate which slide number has the problem

---

## 🗂️ Example Mapping

### Rule 1: Slides separated by `---` (thematic break)

**Examples**:
- ✅ **Valid**: Three slides with `---` separator between them
  ```markdown
  ## Slide 1
  Content here.
  ---
  ## Slide 2
  More content.
  ---
  ## Slide 3
  Final content.
  ```
  → Parses as 3 slides

- ✅ **Valid**: Single slide with no separator
  ```markdown
  ## Only Slide
  Content here.
  ```
  → Parses as 1 slide (valid deck)

- ❌ **Invalid**: Empty slide (separator immediately followed by separator)
  ```markdown
  ## Slide 1
  Content.
  ---
  ---
  ## Slide 3
  ```
  → Validation error: "Slide 2 is empty (no content between separators)"

**Questions**:
- Q1: Is a single slide (no `---`) a valid deck?
  - **Decision**: Yes. A deck with 1 slide is valid. Separator is optional for single-slide decks.
- Q2: How many dashes for separator? `---`, `----`, `-----`?
  - **Decision**: Exactly 3 dashes (`---`). Flexmark treats this as a thematic break. More dashes may be treated as content or separator depending on Flexmark parser settings.
  - **Implementation**: Use Flexmark's ThematicBreak node as separator. This handles `---`, `***`, `___` per CommonMark spec.
- Q3: What about separators inside code blocks or blockquotes?
  - **Decision**: Flexmark AST parsing handles this correctly. `---` inside fenced code block is NOT a ThematicBreak node, so won't split slides.

---

### Rule 2: Global front matter applies to all slides

**Examples**:
- ✅ **Valid**: Global front matter at top of file
  ```markdown
  ---
  theme: corporate
  author: Tony Moores
  ---
  ## Slide 1
  Content.
  ---
  ## Slide 2
  More content.
  ```
  → Both slides inherit `theme: corporate` and `author: Tony Moores`

- ✅ **Valid**: No global front matter (use defaults)
  ```markdown
  ## Slide 1
  Content.
  ---
  ## Slide 2
  Content.
  ```
  → Both slides use default theme, no author

- ❌ **Invalid**: Front matter in middle of file (not at top)
  ```markdown
  ## Slide 1
  Content.
  ---
  theme: corporate
  ---
  ## Slide 2
  Content.
  ```
  → **Clarification needed**: Is this per-slide front matter for Slide 2, or a parsing error?
  - **Decision**: This is per-slide front matter for Slide 2. See Rule 3.

**Questions**:
- Q4: Where can global front matter appear?
  - **Decision**: Only at the very top of the file, before any slide content.
- Q5: What if there's text before the first `---` front matter block?
  - **Decision**: That text is the first slide's content. No global front matter in this case.

---

### Rule 3: Per-slide front matter overrides global

**Examples**:
- ✅ **Valid**: Per-slide front matter overrides global theme
  ```markdown
  ---
  theme: corporate
  author: Tony Moores
  ---
  ## Slide 1
  Uses corporate theme.
  ---
  ---
  template: title
  theme: minimal
  ---
  ## Slide 2
  Uses minimal theme (overrides global).
  ```
  → Slide 1: theme=corporate, Slide 2: theme=minimal, both have author=Tony Moores

- ✅ **Valid**: Per-slide front matter without global
  ```markdown
  ## Slide 1
  No front matter, uses defaults.
  ---
  ---
  template: content
  ---
  ## Slide 2
  Has explicit template binding.
  ```
  → Slide 1: defaults, Slide 2: template=content

- ❌ **Invalid**: Front matter in middle of slide content
  ```markdown
  ## Slide 1
  Some content here.
  ---
  template: content
  ---
  More content here.
  ```
  → This is ambiguous. Is `---\ntemplate: content\n---` a per-slide front matter block or a separator + YAML + separator?
  - **Decision**: Per-slide front matter MUST appear immediately after the separator, before any content. If there's content before the YAML block, it's treated as slide content, not front matter.

**Questions**:
- Q6: How to parse per-slide front matter?
  - **Decision**: After splitting on `---` separator, check if the next block starts with `---` (YAML delimiter). If yes, parse YAML until closing `---`. Everything after that is slide content.
- Q7: Can per-slide front matter have nested YAML?
  - **Decision**: Yes, YAML supports nested structures. Parse entire YAML block.
- Q8: What if per-slide front matter is malformed YAML?
  - **Decision**: Parsing error. Fail with clear message: "Slide 3 has malformed front matter: <YAML error>"

---

### Rule 4: Slide order is preserved

**Examples**:
- ✅ **Valid**: 5 slides in order
  ```markdown
  ## Slide 1
  ---
  ## Slide 2
  ---
  ## Slide 3
  ---
  ## Slide 4
  ---
  ## Slide 5
  ```
  → SlideDeck contains Slide[0], Slide[1], Slide[2], Slide[3], Slide[4] in that order

- ✅ **Valid**: Slide IDs assigned sequentially
  ```markdown
  ---
  id: intro
  ---
  ## Introduction
  ---
  ---
  id: body
  ---
  ## Main Content
  ```
  → Slide 1 has id=intro, Slide 2 has id=body. Order preserved.

**Questions**:
- Q9: Are slide IDs auto-generated or user-provided?
  - **Decision**: User can provide `id:` in front matter. If not provided, system auto-generates (e.g., `slide-1`, `slide-2`, etc.).
- Q10: What if two slides have the same `id`?
  - **Decision**: Validation error: "Duplicate slide ID 'intro' found on slides 1 and 3"

---

### Rule 5: Empty slides are validation errors

**Examples**:
- ❌ **Invalid**: Empty slide (no content)
  ```markdown
  ## Slide 1
  Content.
  ---
  ---
  ## Slide 3
  ```
  → Validation error: "Slide 2 is empty (no content between separators)"

- ❌ **Invalid**: Slide with only whitespace
  ```markdown
  ## Slide 1
  ---


  ---
  ## Slide 3
  ```
  → Validation error: "Slide 2 is empty (only whitespace)"

- ❌ **Invalid**: Slide with only front matter, no content
  ```markdown
  ## Slide 1
  ---
  ---
  template: content
  ---
  ---
  ## Slide 3
  ```
  → Validation error: "Slide 2 has front matter but no content"

**Questions**:
- Q11: What counts as "content"?
  - **Decision**: Any non-whitespace text after front matter. Headings, paragraphs, lists, code blocks, etc. all count as content.
- Q12: Is a slide with only a heading (no body) valid?
  - **Decision**: Depends on template. Title template requires only `title` slot (heading is enough). Content template requires `heading` + `body`, so heading-only would fail validation.

---

### Rule 6: Malformed separators handled gracefully

**Examples**:
- ⚠️ **Warning**: Four dashes (treated as separator by Flexmark)
  ```markdown
  ## Slide 1
  ----
  ## Slide 2
  ```
  → Warning: "Using 4-dash separator is non-standard. Use `---` for clarity."
  → Still parses as 2 slides (Flexmark treats `----` as thematic break)

- ✅ **Valid**: Separator with trailing spaces
  ```markdown
  ## Slide 1
  ---
  ## Slide 2
  ```
  → Parsed as 2 slides (Flexmark ignores trailing whitespace on thematic break)

- ✅ **Not a separator**: Two dashes
  ```markdown
  ## Slide 1
  --
  ## This is not a second slide
  ```
  → Parsed as 1 slide. `--` is not a thematic break per CommonMark spec.

**Questions**:
- Q13: Should we enforce exactly 3 dashes?
  - **Decision**: Accept Flexmark's ThematicBreak node as separator (which accepts `---`, `***`, `___`). Warn if non-standard separators are used, but still parse them.

---

## 📝 Concrete Examples (Given/When/Then)

### Example 1: Simple Multi-Slide Deck

```gherkin
Feature: Multi-Slide Parsing

  Scenario: Parse 3 slides with no front matter
    Given I have a markdown file with content:
      """
      ## Slide 1
      First slide content.
      ---
      ## Slide 2
      Second slide content.
      ---
      ## Slide 3
      Third slide content.
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 3 Slides
    And Slide 1 heading is "Slide 1"
    And Slide 2 heading is "Slide 2"
    And Slide 3 heading is "Slide 3"
    And all slides use default template
    And validation succeeds
```

---

### Example 2: Global Front Matter

```gherkin
  Scenario: Global front matter applies to all slides
    Given I have a markdown file with content:
      """
      ---
      theme: corporate
      author: Tony Moores
      ---
      ## Slide 1
      Content.
      ---
      ## Slide 2
      More content.
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 2 Slides
    And all slides have theme "corporate"
    And all slides have author "Tony Moores"
    And validation succeeds
```

---

### Example 3: Per-Slide Front Matter Override

```gherkin
  Scenario: Per-slide front matter overrides global
    Given I have a markdown file with content:
      """
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
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 2 Slides
    And Slide 1 has theme "corporate" and template "content"
    And Slide 2 has theme "minimal" and template "title"
    And validation succeeds
```

---

### Example 4: Single Slide (No Separator)

```gherkin
  Scenario: Single slide without separator is valid
    Given I have a markdown file with content:
      """
      ## Only Slide
      This is the only slide in the deck.
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And Slide 1 heading is "Only Slide"
    And validation succeeds
```

---

### Example 5: Empty Slide ❌

```gherkin
  Scenario: Empty slide triggers validation error
    Given I have a markdown file with content:
      """
      ## Slide 1
      Content.
      ---
      ---
      ## Slide 3
      Content.
      """
    When I parse the markdown into a SlideDeck
    Then validation fails with StructureError
    And the error message contains "Slide 2 is empty"
```

---

### Example 6: Slide with Only Whitespace ❌

```gherkin
  Scenario: Slide with only whitespace triggers error
    Given I have a markdown file with content:
      """
      ## Slide 1
      Content.
      ---


      ---
      ## Slide 3
      Content.
      """
    When I parse the markdown into a SlideDeck
    Then validation fails with StructureError
    And the error message contains "Slide 2 is empty (only whitespace)"
```

---

### Example 7: Slide with Only Front Matter ❌

```gherkin
  Scenario: Slide with only front matter, no content
    Given I have a markdown file with content:
      """
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
      """
    When I parse the markdown into a SlideDeck
    Then validation fails with StructureError
    And the error message contains "Slide 2 has front matter but no content"
```

---

### Example 8: Duplicate Slide IDs ❌

```gherkin
  Scenario: Duplicate slide IDs trigger error
    Given I have a markdown file with content:
      """
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
      """
    When I parse the markdown into a SlideDeck
    Then validation fails with StructureError
    And the error message contains "Duplicate slide ID 'intro'"
```

---

### Example 9: Separator Inside Code Block (Should Not Split)

```gherkin
  Scenario: Separator inside code block does not split slide
    Given I have a markdown file with content:
      """
      ## Code Example

      Here's a YAML example:

      ```yaml
      ---
      theme: corporate
      ---
      ```

      This should all be one slide.
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And Slide 1 contains a code block with "---"
    And validation succeeds
```

---

### Example 10: Large Deck (Stress Test)

```gherkin
  Scenario: Parse 50-slide deck efficiently
    Given I have a markdown file with 50 slides separated by "---"
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 50 Slides
    And parsing completes in less than 100ms
    And validation succeeds
```

---

## 🚧 Open Questions

| ID | Question | Status | Decision |
|----|----------|--------|----------|
| Q1 | Is single slide (no `---`) valid? | ✅ Resolved | Yes, valid 1-slide deck |
| Q2 | How many dashes? `---` vs `----`? | ✅ Resolved | Accept Flexmark ThematicBreak, warn if non-standard |
| Q3 | Separator inside code/blockquote? | ✅ Resolved | Flexmark AST handles correctly (not a separator) |
| Q4 | Where can global front matter appear? | ✅ Resolved | Only at top of file |
| Q5 | Text before first `---`? | ✅ Resolved | That's first slide's content |
| Q6 | How to parse per-slide front matter? | ✅ Resolved | After separator, check for YAML block |
| Q7 | Nested YAML in front matter? | ✅ Resolved | Yes, supported |
| Q8 | Malformed per-slide front matter? | ✅ Resolved | Parsing error with clear message |
| Q9 | Auto-generated vs user-provided IDs? | ✅ Resolved | User can provide, else auto-generated |
| Q10 | Duplicate slide IDs? | ✅ Resolved | Validation error |
| Q11 | What counts as "content"? | ✅ Resolved | Any non-whitespace text |
| Q12 | Heading-only slide valid? | ✅ Resolved | Depends on template requirements |
| Q13 | Enforce exactly 3 dashes? | ✅ Resolved | Accept ThematicBreak, warn if non-standard |

---

## ✅ Acceptance Criteria (Definition of Done)

### Functional Criteria

1. ✅ **AC1**: Parse markdown file with multiple slides separated by `---`
2. ✅ **AC2**: Single slide (no separator) is valid 1-slide deck
3. ✅ **AC3**: Global front matter at top of file applies to all slides
4. ✅ **AC4**: Per-slide front matter overrides global front matter
5. ✅ **AC5**: Slide order is preserved in SlideDeck
6. ✅ **AC6**: Empty slides trigger validation error
7. ✅ **AC7**: Slides with only whitespace trigger validation error
8. ✅ **AC8**: Slides with only front matter (no content) trigger error
9. ✅ **AC9**: Duplicate slide IDs trigger validation error
10. ✅ **AC10**: Separator inside code block does not split slides
11. ✅ **AC11**: Malformed per-slide front matter triggers parsing error
12. ✅ **AC12**: User-provided slide IDs are respected
13. ✅ **AC13**: Auto-generated slide IDs if not provided

### Technical Criteria

14. ✅ **AC14**: Use Flexmark ThematicBreak node as slide separator
15. ✅ **AC15**: Parse YAML front matter with SnakeYAML or equivalent
16. ✅ **AC16**: Front matter inheritance: per-slide merges with global (per-slide wins)
17. ✅ **AC17**: Validation returns `Either[NonEmptyList[ValidationError], SlideDeck]`
18. ✅ **AC18**: All domain terms from ubiquitous language used in code
19. ✅ **AC19**: Unit tests for each scenario using ScalaTest with BDD style

### Non-Functional Criteria

20. ✅ **AC20**: Parsing 50-slide deck completes in < 100ms
21. ✅ **AC21**: Memory usage is O(n) where n = number of slides
22. ✅ **AC22**: Error messages indicate slide number where problem occurred
23. ✅ **AC23**: Pure functional code (no side effects in domain layer)

**Scenarios**: 10 concrete examples documented
- 4 success paths
- 6 failure paths

**Dependencies**:
- Flexmark Markdown parser with ThematicBreak extension
- YAML parser (SnakeYAML or circe-yaml)
- Template Library (to resolve `template:` field)
- Validation framework (NonEmptyList[ValidationError])

---

## 📚 Related Artifacts

- **User Story Tracker**: [BACKLOG-V3.md](../../BACKLOG-V3.md)
- **Domain Model**: [slide-deck-aggregate.md](../domain-models/aggregates/slide-deck-aggregate.md)
- **Ubiquitous Language**: [ubiquitous-language.md](../domain-models/ubiquitous-language.md)
- **Related Story**: [US-001 Title Slide](three-amigos-session-001.md)
- **Related Story**: [US-002 Content Slide](three-amigos-session-002.md)

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
