# Three Amigos Session #002
## Create Content Slide from Markdown

---

```yaml
# MACHINE-READABLE METADATA
session:
  id: 3A-002-CONTENT-SLIDE
  date: 2024-12-19
  user_story_id: US-002
  participants:
    - Tony Moores (Business/Dev/QA)
  duration_minutes: 30-45
  status: in_progress

story:
  title: Create Content Slide from Markdown
  type: Feature
  priority: P0 (Blocker)
  epic: Core Slide Deck Creation
```

---

## 📋 User Story

**As a** slide deck author
**I want to** create a standard content slide with heading and body
**So that** I can present structured information on a slide

**Business Value**: Most common slide type - needed for 80% of presentation content

**Technical Scope**: Tests the core template binding and validation pipeline for the workhorse slide template

---

## 🎭 Three Perspectives

### 👔 Business Perspective (Product Owner)

**What success looks like**:
- Author writes simple H2 heading + markdown body
- Gets immediate feedback if body is too long or heading too verbose
- Can use lists, bold, italics, links in body
- Output is a clean, validated slide ready for rendering

**Acceptance criteria**:
- ✅ Content slide can be created from markdown with H2 heading
- ✅ Front matter explicitly binds to `content` template
- ✅ Validation catches missing required slots (heading, body)
- ✅ Validation catches constraint violations (heading > 1 line, body > 12 lines)
- ✅ Body can contain markdown formatting (lists, bold, italics, links)
- ✅ Body word count validated (max 150 words)

---

### 💻 Development Perspective (Technical)

**Implementation approach**:
1. **Parse Markdown**: Use Flexmark to parse raw Markdown into AST
2. **Extract Front Matter**: Parse YAML front matter to get `template: content`
3. **Resolve Template**: Load `templates/content.yaml` from Template Library
4. **Extract Slots**:
   - Heading slot: First H2 heading
   - Body slot: All content after H2 heading (up to next separator or EOF)
5. **Validate**:
   - Heading required, max 1 line, max 80 chars
   - Body required, max 12 lines, max 150 words
6. **Build Slide**: Construct validated `Slide` aggregate

**Technical risks**:
- Body extraction complexity: What if markdown has multiple H2 headings?
- Line counting: Do we count visual lines or newline characters in markdown?
- Word counting: How to handle inline code, links, etc.?
- Markdown formatting: Preserve all markdown in body or strip some elements?

**Dependencies**:
- Template Library loading mechanism (US-008)
- Ubiquitous Language types (SlideId, SlotName, SlotContent, etc.)
- Validation framework (US-011, US-012, US-013)

---

### 🧪 Testing Perspective (Quality/Edge Cases)

**Happy path scenarios**:
1. Minimal content slide (heading + simple paragraph)
2. Content slide with bullet list
3. Content slide with numbered list
4. Content slide with markdown formatting (bold, italics, links)
5. Content slide with inline code

**Edge cases**:
1. Missing heading (no H2)
2. Heading exceeds 1 line (multiline heading)
3. Heading exceeds 80 characters
4. Body exceeds 12 lines
5. Body exceeds 150 words
6. Empty body (only heading)
7. Multiple H2 headings (which is heading, which is body?)
8. Body contains nested lists (max 2 levels deep)
9. Body contains code blocks (should fail? or count as single line?)
10. Body contains images (should fail? not allowed in content template)

**Non-functional requirements**:
- Validation errors must be human-readable
- All validation errors collected (not fail-fast)
- Performance: parsing + validation < 100ms for typical slide

---

## 🗂️ Example Mapping

### Rule 1: Heading slot is required (H2)

**Examples**:
- ✅ **Valid**: `## Key Takeaways` → heading slot filled
- ❌ **Invalid**: No H2 heading in markdown → validation error "Heading slot is required"
- ❌ **Invalid**: Only H1 heading `# Title` → validation error "Heading must be H2 (##), not H1 (#)"

**Questions**:
- Q1: Should we auto-demote H1 to H2 if no H2 exists?
  - **Decision**: No, fail validation. Content slides use H2 for semantic hierarchy (H1 reserved for title slides).

---

### Rule 2: Heading must not exceed 1 line and 80 characters

**Examples**:
- ✅ **Valid**: `## Key Takeaways`
- ❌ **Invalid**: `## This is an extremely long heading that exceeds the eighty character limit and should trigger a validation error` → validation error "Heading exceeds max 80 characters"
- ❌ **Invalid**: Multiline heading (Markdown doesn't support this, but test parser behavior)

**Questions**:
- Q2: How do we count "1 line"?
  - **Decision**: Single H2 text node. Markdown headings are inherently single-line (newline ends heading).

---

### Rule 3: Body is required, max 12 lines, max 150 words

**Examples**:
- ✅ **Valid**: Simple paragraph (5 lines, 50 words)
- ✅ **Valid**: Bullet list (6 items, 80 words)
- ❌ **Invalid**: No body content → validation error "Body slot is required"
- ❌ **Invalid**: Body with 15 lines → validation error "Body exceeds max 12 lines (found 15 lines)"
- ❌ **Invalid**: Body with 200 words → validation error "Body exceeds max 150 words (found 200 words)"

**Questions**:
- Q3: How do we count lines in markdown body?
  - **Decision**: Count newline characters `\n` in the body content. Blank lines count.
- Q4: How do we count words?
  - **Decision**: Split on whitespace, count tokens. Ignore markdown syntax (e.g., `**bold**` counts as 1 word, not 3 tokens).
- Q5: Do code blocks count as lines?
  - **Decision**: Code blocks NOT allowed in content template body (use code template US-007 instead). Validation error if detected.

---

### Rule 4: Body can contain markdown formatting

**Examples**:
- ✅ **Valid**: `This is **bold** and *italic* text.`
- ✅ **Valid**: `Visit [our website](https://example.com) for details.`
- ✅ **Valid**: Bullet list with `- Item 1\n- Item 2\n- Item 3`
- ✅ **Valid**: Numbered list with `1. First\n2. Second\n3. Third`
- ✅ **Valid**: Inline code with backticks: `use the \`grep\` command`
- ❌ **Invalid**: Nested lists more than 2 levels deep → validation warning "Nested lists too deep (max 2 levels)"
- ❌ **Invalid**: Code blocks (```) → validation error "Code blocks not allowed in content template (use code template instead)"
- ❌ **Invalid**: Images (`![alt](url)`) → validation error "Images not allowed in content template (use image template instead)"

**Questions**:
- Q6: Should we allow nested lists?
  - **Decision**: Yes, max 2 levels deep. Warn if exceeded (not error).
- Q7: Should we allow inline code?
  - **Decision**: Yes, inline code with backticks is allowed. Code blocks are not.
- Q8: Should we allow blockquotes?
  - **Decision**: Yes, blockquotes allowed. Count lines normally.

---

### Rule 5: Multiple H2 headings - which is heading, which is body?

**Examples**:
- ❌ **Invalid**:
  ```markdown
  ## First Heading
  Some content here.
  ## Second Heading
  More content here.
  ```
  → validation error "Content template allows only one H2 heading. Found 2 headings. Use multi-slide separator (---) instead."

**Questions**:
- Q9: What if there are multiple H2 headings?
  - **Decision**: Validation error. Content template = 1 slide with 1 heading. If user wants multiple headings, they need multiple slides (use `---` separator).

---

## 📝 Concrete Examples (Given/When/Then)

### Example 1: Minimal Valid Content Slide

```gherkin
Feature: Content Slide Creation

  Scenario: Create content slide with minimal content
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Key Takeaways

      Domain-Driven Design helps us build better software.
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And the Slide uses the "content" Template
    And the "heading" slot contains "Key Takeaways"
    And the "body" slot contains "Domain-Driven Design helps us build better software."
    And validation succeeds
```

---

### Example 2: Content Slide with Bullet List

```gherkin
  Scenario: Create content slide with bullet list
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Key Principles

      - Ubiquitous Language
      - Bounded Contexts
      - Aggregates and Entities
      - Strategic Design
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And the "heading" slot contains "Key Principles"
    And the "body" slot contains a bullet list with 4 items
    And the body has 4 lines
    And validation succeeds
```

---

### Example 3: Content Slide with Markdown Formatting

```gherkin
  Scenario: Content slide with rich markdown formatting
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Why Functional Programming?

      Functional programming offers **immutability** by default, making code
      easier to reason about. Pure functions have *no side effects*, which
      simplifies testing and debugging.

      Learn more at [FP Resources](https://functional.org).
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And the "heading" slot contains "Why Functional Programming?"
    And the "body" slot preserves markdown formatting (bold, italics, links)
    And the body has 5 lines
    And the body has approximately 35 words
    And validation succeeds
```

---

### Example 4: Body Exceeds Max Lines ❌

```gherkin
  Scenario: Body exceeds 12 line constraint
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
    When I parse the markdown into a SlideDeck
    Then validation fails with DensityError
    And the error message contains "Body exceeds max 12 lines (found 13 lines)"
```

---

### Example 5: Body Exceeds Max Words ❌

```gherkin
  Scenario: Body exceeds 150 word constraint
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Overly Verbose Content

      [200 words of text here that exceeds the 150 word limit...]
      """
    When I parse the markdown into a SlideDeck
    Then validation fails with DensityError
    And the error message contains "Body exceeds max 150 words (found 200 words)"
```

---

### Example 6: Missing Required Heading ❌

```gherkin
  Scenario: No heading in markdown
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      This is just body text with no heading.
      """
    When I parse the markdown into a SlideDeck
    Then validation fails with StructureError
    And the error message contains "Heading slot is required"
```

---

### Example 7: Missing Required Body ❌

```gherkin
  Scenario: Heading but no body content
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Lonely Heading
      """
    When I parse the markdown into a SlideDeck
    Then validation fails with StructureError
    And the error message contains "Body slot is required"
```

---

### Example 8: Code Block in Body ❌

```gherkin
  Scenario: Code block not allowed in content template
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Code Example

      Here's some code:

      ```scala
      def hello(): Unit = println("Hello")
      ```
      """
    When I parse the markdown into a SlideDeck
    Then validation fails with StructureError
    And the error message contains "Code blocks not allowed in content template. Use code template instead."
```

---

### Example 9: Image in Body ❌

```gherkin
  Scenario: Image not allowed in content template
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Architecture Diagram

      Here's our system architecture:

      ![Architecture](./diagrams/architecture.png)
      """
    When I parse the markdown into a SlideDeck
    Then validation fails with StructureError
    And the error message contains "Images not allowed in content template. Use image template instead."
```

---

### Example 10: Multiple H2 Headings ❌

```gherkin
  Scenario: Multiple H2 headings trigger error
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## First Topic

      Some content about the first topic.

      ## Second Topic

      Some content about the second topic.
      """
    When I parse the markdown into a SlideDeck
    Then validation fails with StructureError
    And the error message contains "Content template allows only one H2 heading. Found 2 headings. Use slide separator (---) instead."
```

---

## 🚧 Open Questions

| ID | Question | Status | Decision |
|----|----------|--------|----------|
| Q1 | Auto-demote H1 to H2 if missing? | ✅ Resolved | No, fail validation (semantic hierarchy) |
| Q2 | How to count "1 line" for heading? | ✅ Resolved | Single H2 text node (markdown headings are inherently single-line) |
| Q3 | How to count lines in body? | ✅ Resolved | Count newline characters `\n` (blank lines count) |
| Q4 | How to count words in body? | ✅ Resolved | Split on whitespace, ignore markdown syntax tokens |
| Q5 | Do code blocks count as lines? | ✅ Resolved | Code blocks NOT allowed in content template (validation error) |
| Q6 | Should we allow nested lists? | ✅ Resolved | Yes, max 2 levels deep (warn if exceeded) |
| Q7 | Should we allow inline code? | ✅ Resolved | Yes, inline code with backticks allowed |
| Q8 | Should we allow blockquotes? | ✅ Resolved | Yes, blockquotes allowed (count lines normally) |
| Q9 | Multiple H2 headings? | ✅ Resolved | Validation error (use slide separator `---` instead) |

---

## ✅ Acceptance Criteria (Definition of Done)

### Functional Criteria

1. ✅ **AC1**: Given valid markdown with `template: content`, a content slide is created
2. ✅ **AC2**: Heading slot is required - validation fails if missing
3. ✅ **AC3**: Heading must be H2 (not H1, H3, etc.) - validation fails otherwise
4. ✅ **AC4**: Heading max 1 line - inherent to markdown (semantic constraint, not validated)
5. ✅ **AC5**: Heading max 80 chars - validation fails if exceeded
6. ✅ **AC6**: Body slot is required - validation fails if missing
7. ✅ **AC7**: Body max 12 lines - validation fails if exceeded (DensityError)
8. ✅ **AC8**: Body max 150 words - validation fails if exceeded (DensityError)
9. ✅ **AC9**: Body can contain markdown formatting (lists, bold, italics, links, inline code)
10. ✅ **AC10**: Code blocks in body trigger validation error (use code template)
11. ✅ **AC11**: Images in body trigger validation error (use image template)
12. ✅ **AC12**: Multiple H2 headings trigger validation error (use slide separator)

### Technical Criteria

13. ✅ **AC13**: All domain terms from ubiquitous language used in code
14. ✅ **AC14**: Validation returns `Either[NonEmptyList[ValidationError], Slide]`
15. ✅ **AC15**: Flexmark integration uses Anticorruption Layer pattern
16. ✅ **AC16**: All validation errors collected (not fail-fast)
17. ✅ **AC17**: Unit tests for each scenario using ScalaTest with BDD style
18. ✅ **AC18**: Front matter parsed using YAML parser (Docusaurus compatible)

### Non-Functional Criteria

19. ✅ **AC19**: Parsing + validation completes in < 100ms for typical slide
20. ✅ **AC20**: Error messages are human-readable (no technical jargon)
21. ✅ **AC21**: Pure functional code (no side effects in domain layer)

**Scenarios**: 10 concrete examples documented
- 3 success paths
- 7 failure paths

**Dependencies**:
- Template Library loading mechanism
- YAML front matter parser
- Flexmark Markdown parser
- Validation framework (NonEmptyList[ValidationError])
- Template: `templates/content.yaml`

---

## 📚 Related Artifacts

- **User Story Tracker**: [BACKLOG-V3.md](../../BACKLOG-V3.md)
- **Domain Model**: [slide-deck-aggregate.md](../domain-models/aggregates/slide-deck-aggregate.md)
- **Template Definition**: [templates/content.yaml](../../templates/content.yaml)
- **Ubiquitous Language**: [ubiquitous-language.md](../domain-models/ubiquitous-language.md)

---

## 🎯 Next Steps

1. **Create Example Mapping visual** (Ceremony 2.2)
2. **Document formal acceptance criteria** in BACKLOG-V3.md
3. **Create template definition** in `templates/content.yaml`
4. **Proceed to Ceremony 2.2**: Example Mapping Workshop (refine scenarios)

---

**Session Type**: Ceremony 2.1 - Three Amigos Session
**Date**: 2024-12-19
**Facilitator**: Tony Moores (TJM Solutions)
**Next Review**: After Example Mapping Workshop (Ceremony 2.2)
