# Three Amigos Session #001
## Create Title Slide from Markdown

---

```yaml
# MACHINE-READABLE METADATA
session:
  id: 3A-001-TITLE-SLIDE
  date: 2024-12-19
  user_story_id: US-001
  participants:
    - Tony Moores (Business/Dev/QA)
  duration_minutes: 30-45
  status: in_progress

story:
  title: Create Title Slide from Markdown
  type: Feature
  priority: High
  epic: Core Slide Deck Creation
```

---

## 📋 User Story

**As a** slide deck author
**I want to** create a title slide from Markdown with explicit template binding
**So that** I can generate a properly validated title slide with title, subtitle, and author

**Business Value**: Foundation for all slide deck creation - every presentation needs a title slide

**Technical Scope**: Exercises the complete pipeline from Markdown parsing → Template resolution → Slot extraction → Validation

---

## 🎭 Three Perspectives

### 👔 Business Perspective (Product Owner)

**What success looks like**:
- Author writes simple Markdown with YAML front matter
- Gets immediate feedback if title is missing or too long
- Can optionally add subtitle and author
- Output is a clean, validated slide ready for rendering

**Acceptance criteria**:
- ✅ Title slide can be created from minimal Markdown (`# Title`)
- ✅ Front matter explicitly binds to `title` template
- ✅ Validation catches missing required slots (title)
- ✅ Validation catches constraint violations (title > 2 lines)
- ✅ Optional slots (subtitle, author) work when present or absent

---

### 💻 Development Perspective (Technical)

**Implementation approach**:
1. **Parse Markdown**: Use Flexmark to parse raw Markdown into AST
2. **Extract Front Matter**: Parse YAML front matter to get `template: title`
3. **Resolve Template**: Load `templates/title.yaml` from Template Library
4. **Extract Slots**: Map Markdown content to slots (title, subtitle, author)
5. **Validate**: Run slot constraint validation (max_lines, required, etc.)
6. **Build Slide**: Construct validated `Slide` aggregate

**Technical risks**:
- Flexmark AST mapping complexity (need Anticorruption Layer)
- YAML front matter parsing edge cases (malformed YAML)
- Slot extraction heuristics (how to distinguish title from subtitle?)
- Validation error accumulation (collect all errors, don't fail-fast)

**Dependencies**:
- Template Library must load templates at startup
- Ubiquitous Language types (SlideId, SlotName, SlotContent, etc.)

---

### 🧪 Testing Perspective (Quality/Edge Cases)

**Happy path scenarios**:
1. Minimal title slide (title only)
2. Full title slide (title + subtitle + author)
3. Title with emoji/special characters
4. Multi-line title (within 2-line limit)

**Edge cases**:
1. Missing required slot (no title)
2. Title exceeds 2 lines
3. Title is empty or whitespace-only
4. Subtitle exceeds 2 lines
5. Author exceeds 80 characters
6. Front matter missing or malformed
7. Template binding to non-existent template
8. Markdown has no headings (pure text)
9. Multiple H1 headings (which is title?)

**Non-functional requirements**:
- Validation errors must be human-readable
- All validation errors collected (not fail-fast)
- Performance: parsing + validation < 100ms for typical slide

---

## 🗂️ Example Mapping

### Rule 1: Title slot is required

**Examples**:
- ✅ **Valid**: `# Welcome to MDSlides` → title slot filled
- ❌ **Invalid**: No heading in markdown → validation error "Title slot is required"
- ❌ **Invalid**: Only H2 heading `## Subtitle` → validation error "Title slot is required"

**Questions**:
- Q1: Should we auto-promote H2 to H1 if no H1 exists?
  - **Decision**: No, fail validation. Author must fix their markdown.

---

### Rule 2: Title must not exceed 2 lines

**Examples**:
- ✅ **Valid**: `# Short Title`
- ✅ **Valid**: `# Two Line\nTitle Here`
- ❌ **Invalid**: `# Line 1\nLine 2\nLine 3` → validation error "Title exceeds max 2 lines"

**Questions**:
- Q2: How do we count lines in Markdown heading?
  - **Decision**: Count newlines within the H1 text node (not paragraph breaks)

---

### Rule 3: Subtitle is optional, max 2 lines

**Examples**:
- ✅ **Valid**: No subtitle present → slot empty, validation passes
- ✅ **Valid**: `## A Brief Overview` → subtitle slot filled
- ❌ **Invalid**: `## Line 1\nLine 2\nLine 3` → validation error "Subtitle exceeds max 2 lines"

**Questions**:
- Q3: What if there are multiple H2 headings?
  - **Decision**: First H2 is subtitle, remaining H2s are ignored (warning logged)

---

### Rule 4: Author is optional, max 80 chars

**Examples**:
- ✅ **Valid**: No author in front matter → slot empty, validation passes
- ✅ **Valid**: `author: John Doe` in front matter → author slot filled
- ❌ **Invalid**: `author: Very Long Name That Exceeds The Maximum Character Limit Of Eighty Characters Total` → validation error

**Questions**:
- Q4: How do we identify the author?
  - **Decision**: Use front matter field `author: John Doe` (Docusaurus compatibility)

---

### Rule 5: Front matter must specify template

**Examples**:
- ✅ **Valid**:
  ```markdown
  ---
  template: title
  ---
  # My Presentation
  ```
- ❌ **Invalid**: No front matter → validation error "Template not specified"
- ❌ **Invalid**: `template: nonexistent` → validation error "Template 'nonexistent' not found"

**Questions**:
- Q5: Should we have a default template if front matter is missing?
  - **Decision**: Yes, use `content` as default. Update rule: front matter is optional for this story.
  - **Revision**: For title slide story, require explicit `template: title` (no heuristics yet)

---

## 📝 Concrete Examples (Given/When/Then)

### Example 1: Minimal Valid Title Slide

```gherkin
Feature: Title Slide Creation

  Scenario: Create title slide with minimal content
    Given I have a markdown file with content:
      """
      ---
      template: title
      ---
      # Welcome to MDSlides
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And the Slide uses the "title" Template
    And the "title" slot contains "Welcome to MDSlides"
    And the "subtitle" slot is empty
    And the "author" slot is empty
    And validation succeeds
```

---

### Example 2: Full Title Slide with All Slots

```gherkin
  Scenario: Create title slide with all optional slots
    Given I have a markdown file with content:
      """
      ---
      template: title
      author: Tony Moores
      ---
      # Welcome to MDSlides
      ## Create Beautiful Presentations with Markdown
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And the "title" slot contains "Welcome to MDSlides"
    And the "subtitle" slot contains "Create Beautiful Presentations with Markdown"
    And the "author" slot contains "Tony Moores"
    And validation succeeds
```

---

### Example 3: Title Exceeds Max Lines (Validation Failure)

```gherkin
  Scenario: Title exceeds 2 line constraint
    Given I have a markdown file with content:
      """
      ---
      template: title
      ---
      # This is a very long title
      that spans three
      separate lines
      """
    When I parse the markdown into a SlideDeck
    Then validation fails with ContentError
    And the error message contains "Title exceeds max 2 lines"
```

---

### Example 4: Missing Required Title Slot

```gherkin
  Scenario: No title heading in markdown
    Given I have a markdown file with content:
      """
      ---
      template: title
      ---
      ## Just a subtitle, no title
      """
    When I parse the markdown into a SlideDeck
    Then validation fails with StructureError
    And the error message contains "Title slot is required"
```

---

### Example 5: Author Exceeds Character Limit

```gherkin
  Scenario: Author name too long
    Given I have a markdown file with content:
      """
      ---
      template: title
      author: This is an extremely long author name that definitely exceeds the eighty character limit imposed by the template
      ---
      # My Presentation
      """
    When I parse the markdown into a SlideDeck
    Then validation fails with ContentError
    And the error message contains "Author exceeds max 80 characters"
```

---

### Example 6: Template Not Found

```gherkin
  Scenario: Front matter specifies non-existent template
    Given I have a markdown file with content:
      """
      ---
      template: super-fancy-template
      ---
      # My Title
      """
    And the template "super-fancy-template" does not exist in the Template Library
    When I parse the markdown into a SlideDeck
    Then validation fails with StructureError
    And the error message contains "Template 'super-fancy-template' not found"
```

---

### Example 7: Title with Markdown Formatting and Unicode

```gherkin
  Scenario: Title with inline markdown and mathematical symbols
    Given I have a markdown file with content:
      """
      ---
      template: title
      author: Tony Moores
      ---
      # Introduction to **Fourier Transforms** and ∑ Notation
      ## Mathematical foundations for signal processing
      """
    And the theme has "content.preserveMarkdownInHeadings" set to true
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And the "title" slot contains "Introduction to **Fourier Transforms** and ∑ Notation"
    And the markdown formatting is preserved for rendering
    And validation succeeds
```

---

## 🚧 Open Questions

| ID | Question | Status | Decision |
|----|----------|--------|----------|
| Q1 | Auto-promote H2 to H1 if missing? | ✅ Resolved | No, fail validation |
| Q2 | How to count lines in H1? | ✅ Resolved | Count newlines in heading text node |
| Q3 | Multiple H2 headings? | ✅ Resolved | First H2 is subtitle, rest ignored with warning |
| Q4 | How to identify author? | ✅ Resolved | Use front matter `author: John Doe` (Docusaurus compatibility) |
| Q5 | Default template if missing? | ✅ Resolved | Require explicit template for this story |
| Q6 | Unicode/emoji in title? | ✅ Resolved | Allow (author's choice), but MDSlides internal docs avoid emojis. Mathematical symbols (∑, π, etc.) encouraged. Theme option: `content.allowEmoji` |
| Q7 | Inline markdown in title? | ✅ Resolved | Preserve markdown formatting (bold, links, code). Author's choice. Theme option: `content.preserveMarkdownInHeadings` |
| Q8 | Should emoji/markdown handling be theme options? | ✅ Resolved | Yes, add `content.allowEmoji` and `content.preserveMarkdownInHeadings` to theme schema |

---

## ✅ Acceptance Criteria (Definition of Done)

### Functional Criteria

1. ✅ **AC1**: Given valid markdown with `template: title`, a title slide is created
2. ✅ **AC2**: Title slot is required - validation fails if missing
3. ✅ **AC3**: Title max 2 lines - validation fails if exceeded
4. ✅ **AC4**: Subtitle is optional - validation passes if absent
5. ✅ **AC5**: Subtitle max 2 lines - validation fails if exceeded
6. ✅ **AC6**: Author is optional - validation passes if absent
7. ✅ **AC7**: Author max 80 chars - validation fails if exceeded
8. ✅ **AC8**: Template resolution works via front matter `template: title`
9. ✅ **AC9**: Non-existent template triggers validation error

### Technical Criteria

10. ✅ **AC10**: All domain terms from ubiquitous language used in code
11. ✅ **AC11**: Validation returns `Either[NonEmptyList[ValidationError], Slide]`
12. ✅ **AC12**: Flexmark integration uses Anticorruption Layer pattern
13. ✅ **AC13**: All validation errors collected (not fail-fast)
14. ✅ **AC14**: Unit tests for each scenario using ScalaTest with BDD style

### Non-Functional Criteria

15. ✅ **AC15**: Parsing + validation completes in < 100ms for typical slide
16. ✅ **AC16**: Error messages are human-readable (no technical jargon)
17. ✅ **AC17**: Pure functional code (no side effects in domain layer)

---

## 📚 Related Artifacts

- **User Story Tracker**: [BACKLOG.md](../../BACKLOG.md) (to be created)
- **Domain Model**: [slide-deck-aggregate.md](../domain-models/aggregates/slide-deck-aggregate.md)
- **Template Definition**: [templates/title.yaml](../../templates/title.yaml)
- **Ubiquitous Language**: [ubiquitous-language.md](../domain-models/ubiquitous-language.md)

---

## 🎯 Next Steps

1. **Resolve open questions** (Q4, Q6, Q7) with Product Owner (you!)
2. **Create Example Mapping visual** (index cards or Miro board simulation)
3. **Document formal acceptance criteria** in BACKLOG.md
4. **Proceed to Ceremony 2.2**: Example Mapping Workshop (refine scenarios)

---

**Session Type**: Ceremony 2.1 - Three Amigos Session
**Date**: 2024-12-19
**Facilitator**: Tony Moores (TJM Solutions)
**Next Review**: After Example Mapping Workshop (Ceremony 2.2)
