# Three Amigos Session #013
## Content Validation (Slot Constraints)

---

```yaml
# MACHINE-READABLE METADATA
session:
  id: 3A-013-CONTENT-VALIDATION
  date: 2024-12-20
  user_story_id: US-013
  participants:
    - Tony Moores (Business/Dev/QA)
  duration_minutes: 45-60
  status: complete

story:
  title: Content Validation (Slot Constraints)
  type: Feature
  priority: P0 (Blocker)
  epic: Validation Framework
```

---

## 📋 User Story

**As a** slide deck author
**I want to** receive validation errors for constraint violations
**So that** my slide content meets template requirements

**Business Value**: Ensures content fits template constraints before rendering - prevents broken slides

**Technical Scope**: Validates slot content against template-defined constraints (required, max_lines, max_chars, max_words, etc.)

---

## 🎭 Three Perspectives

### 👔 Business Perspective (Product Owner)

**What success looks like**:
- Author gets clear errors when slot content violates constraints
- Errors explain WHICH slot and WHAT constraint was violated
- Errors are BLOCKING (must fix before rendering)
- Common issues caught:
  - Required slot is empty
  - Slot content exceeds max_lines
  - Slot content exceeds max_chars
  - Slot content exceeds max_words
  - Image path doesn't exist
  - Code language is invalid

**Difference from Density Validation (US-012)**:
- **Content Validation**: Template-specific constraints (ERRORS - blocking)
- **Density Validation**: General "fits on slide" heuristics (WARNINGS - non-blocking)

**Acceptance criteria**:
- ❌ Required slot is empty → ContentError
- ❌ Slot > max_lines → ContentError
- ❌ Slot > max_chars → ContentError
- ❌ Slot > max_words → ContentError
- ❌ Image path doesn't exist → ContentError
- ❌ Code language invalid → ContentError

---

### 💻 Development Perspective (Technical)

**Implementation approach**:
1. **After Structure + Density Validation**: Run content validation on SlideDeck
2. **For Each Slide**:
   - Load template definition
   - For each slot in template:
     - Check if required slot is present (Structure already checked this)
     - Check if required slot is non-empty (Content checks this)
     - Check max_lines constraint (if defined)
     - Check max_chars constraint (if defined)
     - Check max_words constraint (if defined)
     - Check type-specific constraints (image path, code language)
3. **Collect Errors**: Use `NonEmptyList[ContentError]`
4. **Return Result**: `Either[NonEmptyList[ContentError], SlideDeck]`

**Technical risks**:
- **Empty vs Missing**: Structure checks presence, Content checks emptiness
  - Slide can have slot but it's empty (whitespace only) → ContentError
- **Constraint Inheritance**: Do constraints come from template or theme?
  - Answer: Template defines constraints. Theme defines density limits (different).
- **Type-Specific Validation**: How to validate image paths, code languages?
  - Image: Check file exists relative to markdown file
  - Code: Check language is in supported list (scala, java, python, etc.)

**Dependencies**:
- Structure Validation (must pass first - checks slot presence)
- Template Library (load slot constraints)
- File system (to check image paths exist)

---

### 🧪 Testing Perspective (Quality/Edge Cases)

**Happy path scenarios**:
1. All required slots non-empty
2. All slots within max_lines/chars/words limits
3. Image paths exist
4. Code languages valid

**Edge cases (errors)**:
1. Required slot is empty (only whitespace)
2. Slot exceeds max_lines by 1
3. Slot exceeds max_chars by 1
4. Slot exceeds max_words by 1
5. Image path is relative but doesn't exist
6. Image path is absolute but doesn't exist
7. Code block has unsupported language
8. Multiple constraint violations on one slot

**Boundary cases**:
1. Slot with exactly max_lines → valid
2. Slot with exactly max_chars → valid
3. Slot with exactly max_words → valid

**Non-functional requirements**:
- Validation < 50ms for 50-slide deck
- Error messages indicate which slide and which slot
- All errors collected (not fail-fast)

---

## 🗂️ Example Mapping

### Rule 1: Required slots must be non-empty

**Examples**:
- ✅ **Valid**: Required `title` slot has content "My Presentation"
- ✅ **Valid**: Required `body` slot has content "Content here."
- ❌ **Invalid**: Required `title` slot is empty string
  → ContentError: "Slide 1 slot 'title' is required but empty"
- ❌ **Invalid**: Required `body` slot has only whitespace
  → ContentError: "Slide 2 slot 'body' is required but empty (whitespace only)"

**Questions**:
- Q1: What counts as "empty"?
  - **Decision**: Empty string `""` or whitespace-only content (spaces, tabs, newlines only).
- Q2: Does Structure Validation already check this?
  - **Decision**: No. Structure checks slot PRESENCE. Content checks slot is NON-EMPTY.

---

### Rule 2: Slot content must not exceed max_lines (if constraint defined)

**Examples**:
- ✅ **Valid**: Title slot with 1 line (max_lines: 2)
- ✅ **Valid**: Title slot with exactly 2 lines (max_lines: 2)
- ❌ **Invalid**: Title slot with 3 lines (max_lines: 2)
  → ContentError: "Slide 1 slot 'title' exceeds max 2 lines (found 3 lines)"

**Questions**:
- Q3: How to count lines?
  - **Decision**: Count newline characters `\n`. Same as Density Validation.
- Q4: Difference between max_lines in template vs theme?
  - **Decision**: Template `max_lines` is CONSTRAINT (error if exceeded). Theme `maxBodyLines` is HEURISTIC (warning if exceeded).

---

### Rule 3: Slot content must not exceed max_chars (if constraint defined)

**Examples**:
- ✅ **Valid**: Author slot with 50 chars (max_chars: 80)
- ✅ **Valid**: Author slot with exactly 80 chars (max_chars: 80)
- ❌ **Invalid**: Author slot with 85 chars (max_chars: 80)
  → ContentError: "Slide 1 slot 'author' exceeds max 80 chars (found 85 chars)"

**Questions**:
- Q5: Include markdown syntax in char count?
  - **Decision**: No. Plain text only (consistent with Density Validation heading rule).

---

### Rule 4: Slot content must not exceed max_words (if constraint defined)

**Examples**:
- ✅ **Valid**: Body slot with 100 words (max_words: 150)
- ✅ **Valid**: Body slot with exactly 150 words (max_words: 150)
- ❌ **Invalid**: Body slot with 160 words (max_words: 150)
  → ContentError: "Slide 2 slot 'body' exceeds max 150 words (found 160 words)"

**Questions**:
- Q6: How to count words?
  - **Decision**: Split on whitespace. Same as Density Validation.

---

### Rule 5: Image slot path must exist (if slot type is image)

**Examples**:
- ✅ **Valid**: Image slot with path `./images/diagram.png` (file exists)
- ❌ **Invalid**: Image slot with path `./images/missing.png` (file doesn't exist)
  → ContentError: "Slide 3 slot 'diagram' references image './images/missing.png' which does not exist"

**Questions**:
- Q7: Relative vs absolute paths?
  - **Decision**: Support both. Relative paths resolved from markdown file directory.
- Q8: Check at validation time or render time?
  - **Decision**: Validation time (Content Validation). Fail early.

---

### Rule 6: Code slot language must be valid (if slot type is code)

**Examples**:
- ✅ **Valid**: Code slot with language `scala`
- ✅ **Valid**: Code slot with language `java`
- ❌ **Invalid**: Code slot with language `foobar`
  → ContentError: "Slide 4 slot 'code' has unsupported language 'foobar'. Supported: scala, java, python, javascript, bash, sql, yaml, json, markdown"

**Questions**:
- Q9: What languages are supported?
  - **Decision**: Flexmark syntax highlighting supports: scala, java, python, javascript, bash, sql, yaml, json, markdown, xml, html, css, go, rust, kotlin.
- Q10: No language specified (empty)?
  - **Decision**: Valid. Treated as plain text with no syntax highlighting.

---

## 📝 Concrete Examples (Given/When/Then)

### Example 1: Valid Slide (All Constraints Met)

```gherkin
Feature: Content Validation

  Scenario: Slide meets all content constraints
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Key Takeaways

      Functional programming reduces bugs.
      """
    When I parse and validate the markdown
    Then structure validation succeeds
    And density validation succeeds
    And content validation succeeds
```

---

### Example 2: Required Slot is Empty ❌

```gherkin
  Scenario: Required slot has only whitespace
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Heading


      """
    When I parse and validate the markdown
    Then content validation fails with ContentError
    And the error message contains "slot 'body' is required but empty"
```

---

### Example 3: Slot Exceeds max_lines ❌

```gherkin
  Scenario: Title slot exceeds max 2 lines
    Given I have a markdown file with content:
      """
      ---
      template: title
      ---
      # Line 1
      Line 2
      Line 3
      """
    When I parse and validate the markdown
    Then content validation fails with ContentError
    And the error message contains "slot 'title' exceeds max 2 lines (found 3 lines)"
```

---

### Example 4: Slot Exceeds max_chars ❌

```gherkin
  Scenario: Author slot exceeds max 80 chars
    Given I have a markdown file with content:
      """
      ---
      template: title
      ---
      # My Presentation

      By: This is a very long author name that exceeds the eighty character constraint
      """
    When I parse and validate the markdown
    Then content validation fails with ContentError
    And the error message contains "slot 'author' exceeds max 80 chars"
```

---

### Example 5: Slot Exceeds max_words ❌

```gherkin
  Scenario: Body slot exceeds max 150 words
    Given I have a markdown file with content template body containing 160 words
    When I parse and validate the markdown
    Then content validation fails with ContentError
    And the error message contains "slot 'body' exceeds max 150 words (found 160 words)"
```

---

### Example 6: Image Path Doesn't Exist ❌

```gherkin
  Scenario: Image slot references non-existent file
    Given I have a markdown file with image reference:
      """
      ![Architecture](./diagrams/missing.png)
      """
    When I parse and validate the markdown
    Then content validation fails with ContentError
    And the error message contains "image './diagrams/missing.png' which does not exist"
```

---

### Example 7: Code Language Invalid ❌

```gherkin
  Scenario: Code block has unsupported language
    Given I have a markdown file with content:
      """
      ```foobar
      some code here
      ```
      """
    When I parse and validate the markdown
    Then content validation fails with ContentError
    And the error message contains "unsupported language 'foobar'"
```

---

### Example 8: Multiple Constraint Violations

```gherkin
  Scenario: Multiple content errors on one slide
    Given I have a markdown file with:
      - Required slot 'body' is empty
      - Slot 'title' exceeds max_lines (3 lines, max 2)
      - Slot 'author' exceeds max_chars (90 chars, max 80)
    When I parse and validate the markdown
    Then content validation fails with 3 errors
    And all errors are collected in NonEmptyList[ContentError]
```

---

### Example 9: Boundary Test (Exactly at Limit)

```gherkin
  Scenario: Content exactly at constraint limits
    Given I have a markdown file with:
      - Title with exactly 2 lines (max_lines: 2)
      - Author with exactly 80 chars (max_chars: 80)
      - Body with exactly 150 words (max_words: 150)
    When I parse and validate the markdown
    Then content validation succeeds
```

---

### Example 10: Image Path Exists ✅

```gherkin
  Scenario: Image slot references existing file
    Given I have an image file at "./images/diagram.png"
    And I have a markdown file with image reference:
      """
      ![Architecture](./images/diagram.png)
      """
    When I parse and validate the markdown
    Then content validation succeeds
```

---

## 🚧 Open Questions

| ID | Question | Status | Decision |
|----|----------|--------|----------|
| Q1 | What counts as "empty"? | ✅ Resolved | Empty string or whitespace-only |
| Q2 | Structure vs Content validation? | ✅ Resolved | Structure checks presence, Content checks non-empty |
| Q3 | How to count lines? | ✅ Resolved | Count `\n` newline characters |
| Q4 | Template vs theme constraints? | ✅ Resolved | Template = error, Theme = warning |
| Q5 | Include markdown in char count? | ✅ Resolved | No, plain text only |
| Q6 | How to count words? | ✅ Resolved | Split on whitespace |
| Q7 | Relative vs absolute image paths? | ✅ Resolved | Support both |
| Q8 | Check image at validation or render? | ✅ Resolved | Validation time (fail early) |
| Q9 | Supported code languages? | ✅ Resolved | Flexmark list (scala, java, python, etc.) |
| Q10 | No language specified? | ✅ Resolved | Valid, treated as plain text |

---

## ✅ Acceptance Criteria (Definition of Done)

### Functional Criteria

1. ✅ **AC1**: Required slot is empty → ContentError
2. ✅ **AC2**: Slot > max_lines → ContentError
3. ✅ **AC3**: Slot > max_chars → ContentError
4. ✅ **AC4**: Slot > max_words → ContentError
5. ✅ **AC5**: Image path doesn't exist → ContentError
6. ✅ **AC6**: Code language invalid → ContentError
7. ✅ **AC7**: Content at exact limit → valid (no error)
8. ✅ **AC8**: Multiple violations → all errors collected

### Technical Criteria

9. ✅ **AC9**: Validation returns `Either[NonEmptyList[ContentError], SlideDeck]`
10. ✅ **AC10**: Line counting uses `\n` newline characters
11. ✅ **AC11**: Word counting splits on whitespace
12. ✅ **AC12**: Char count excludes markdown syntax
13. ✅ **AC13**: Image paths resolved relative to markdown file
14. ✅ **AC14**: Image existence checked at validation time
15. ✅ **AC15**: Code languages validated against supported list
16. ✅ **AC16**: Whitespace-only content treated as empty
17. ✅ **AC17**: All domain terms from ubiquitous language used

### Non-Functional Criteria

18. ✅ **AC18**: Validation < 50ms for 50-slide deck
19. ✅ **AC19**: Error messages indicate slide number and slot name
20. ✅ **AC20**: Error messages include actual vs max values
21. ✅ **AC21**: Pure functional code (no side effects)

**Scenarios**: 10 concrete examples documented
- 2 success paths
- 8 error paths

**Dependencies**:
- Structure Validation (must pass first)
- Template Library (load slot constraints)
- File system (check image paths)

---

## 📚 Related Artifacts

- **User Story Tracker**: [BACKLOG-V3.md](../../BACKLOG-V3.md)
- **Domain Model**: [slide-deck-aggregate.md](../domain-models/aggregates/slide-deck-aggregate.md)
- **Template Aggregate**: [template-aggregate.md](../domain-models/aggregates/template-aggregate.md)
- **Ubiquitous Language**: [ubiquitous-language.md](../domain-models/ubiquitous-language.md)
- **Related Stories**: US-011 Structure Validation, US-012 Density Validation, US-015 Collect All Errors

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
