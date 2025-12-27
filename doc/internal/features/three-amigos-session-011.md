# Three Amigos Session #011
## Structure Validation

---

```yaml
# MACHINE-READABLE METADATA
session:
  id: 3A-011-STRUCTURE-VALIDATION
  date: 2024-12-20
  user_story_id: US-011
  participants:
    - Tony Moores (Business/Dev/QA)
  duration_minutes: 45-60
  status: complete

story:
  title: Structure Validation
  type: Feature
  priority: P0 (Blocker)
  epic: Validation Framework
```

---

## 📋 User Story

**As a** slide deck author
**I want to** receive validation errors for structural issues
**So that** I know my slide deck has all required elements

**Business Value**: Ensures slide deck is well-formed before rendering - catches structural errors early

**Technical Scope**: Validates SlideDeck aggregate structure, template bindings, slot presence, and YAML front matter

---

## 🎭 Three Perspectives

### 👔 Business Perspective (Product Owner)

**What success looks like**:
- Author gets immediate feedback if deck structure is invalid
- Clear error messages explain what's wrong (not technical jargon)
- Errors indicate which slide has the problem
- Common issues caught:
  - Empty deck (no slides)
  - Slide references non-existent template
  - Slide missing required slots
  - Slide has extra slots not in template
  - Malformed YAML front matter

**Acceptance criteria**:
- ✅ Deck with 0 slides → StructureError
- ✅ Deck with 201 slides → StructureError (max 200)
- ✅ Slide with invalid template → StructureError
- ✅ Slide missing required slot → StructureError
- ✅ Slide with extra slots → StructureError
- ✅ Slot type mismatch → StructureError
- ✅ Malformed YAML → Parsing Error (before validation)

---

### 💻 Development Perspective (Technical)

**Implementation approach**:
1. **After Parsing**: Run structure validation on SlideDeck aggregate
2. **Validate Deck**:
   - Check slide count >= 1 and <= 200
3. **Validate Each Slide**:
   - Check template exists in Template Library
   - Load template definition
   - Check all required slots are present
   - Check no extra slots beyond template
   - Check slot types match template
4. **Collect Errors**: Use `Either[NonEmptyList[ValidationError], SlideDeck]`
5. **Return Result**: All errors collected (not fail-fast)

**Technical risks**:
- **Template Loading**: What if template library is empty?
  - Answer: Default templates (title, content) must always be present
- **Slot Type Checking**: How to verify slot type?
  - Answer: SlotContent should have type tag (markdown_block, markdown_inline, etc.)
- **Extra Slots**: Should this be error or warning?
  - Answer: Error. Extra slots likely indicate typo or misunderstanding of template.

**Dependencies**:
- Template Library (load template definitions)
- Domain Model (SlideDeck, Slide, Slot, SlotContent)
- Validation framework (NonEmptyList[ValidationError])

---

### 🧪 Testing Perspective (Quality/Edge Cases)

**Happy path scenarios**:
1. Valid deck with 1 slide
2. Valid deck with 5 slides
3. Valid deck with 100 slides
4. All slides use different templates (title, content, etc.)

**Edge cases**:
1. Deck with 0 slides (empty markdown file)
2. Deck with 201 slides (exceeds max)
3. Slide references "nonexistent-template"
4. Content slide missing required "body" slot
5. Content slide has extra "footer" slot (not in template)
6. Slot type mismatch (markdown_inline when template expects markdown_block)
7. Title slide missing required "title" slot
8. Malformed YAML front matter (parsing error, not validation)

**Non-functional requirements**:
- Validation completes in < 50ms for 50-slide deck
- Error messages include slide number/ID
- All errors collected (not just first error)

---

## 🗂️ Example Mapping

### Rule 1: SlideDeck must have 1-200 slides

**Examples**:
- ✅ **Valid**: Deck with 1 slide
- ✅ **Valid**: Deck with 5 slides
- ✅ **Valid**: Deck with 200 slides
- ❌ **Invalid**: Deck with 0 slides
  → StructureError: "SlideDeck must contain at least 1 slide (found 0)"
- ❌ **Invalid**: Deck with 201 slides
  → StructureError: "SlideDeck exceeds max 200 slides (found 201)"

**Questions**:
- Q1: Why max 200 slides?
  - **Decision**: Reasonable upper bound for presentations. Decks with 200+ slides are likely course materials, not presentations.
- Q2: Should max be configurable?
  - **Decision**: No for v1.0. Hard-coded to 200. Can add config in v2.0 if needed.

---

### Rule 2: Every slide must reference valid template

**Examples**:
- ✅ **Valid**: Slide with `template: content` (template exists)
- ✅ **Valid**: Slide with `template: title` (template exists)
- ❌ **Invalid**: Slide with `template: nonexistent`
  → StructureError: "Slide 3 references template 'nonexistent' which does not exist in Template Library"
- ❌ **Invalid**: Slide with no template specified (uses default)
  - **Clarification**: What's the default template?
  - **Decision**: If no `template:` field, default to `content`. Always valid since `content` is built-in.

**Questions**:
- Q3: What if template field is missing?
  - **Decision**: Default to `content` template (most common slide type).
- Q4: Is template name case-sensitive?
  - **Decision**: No. Normalize to lowercase (e.g., `Content` → `content`).

---

### Rule 3: All required slots must be present

**Examples**:
- ✅ **Valid**: Content slide with `heading` and `body` slots
- ✅ **Valid**: Title slide with `title` slot (subtitle and author optional)
- ❌ **Invalid**: Content slide missing `body` slot
  → StructureError: "Slide 2 (template: content) is missing required slot 'body'"
- ❌ **Invalid**: Content slide missing `heading` slot
  → StructureError: "Slide 2 (template: content) is missing required slot 'heading'"
- ❌ **Invalid**: Title slide missing `title` slot
  → StructureError: "Slide 1 (template: title) is missing required slot 'title'"

**Questions**:
- Q5: What if slide has heading but it's empty?
  - **Decision**: That's a Content Validation issue (US-013), not Structure. Structure checks presence, not emptiness.

---

### Rule 4: No extra slots beyond template definition

**Examples**:
- ✅ **Valid**: Content slide with `heading`, `body`, `notes` (all defined in template)
- ❌ **Invalid**: Content slide with `heading`, `body`, `footer`
  → StructureError: "Slide 2 (template: content) has unexpected slot 'footer' (not in template definition)"
- ❌ **Invalid**: Title slide with `title`, `subtitle`, `author`, `company`
  → StructureError: "Slide 1 (template: title) has unexpected slot 'company' (not in template definition)"

**Questions**:
- Q6: Should extra slots be error or warning?
  - **Decision**: Error. Extra slots likely indicate typo or author confusion. Better to fail explicitly.
- Q7: What if template allows arbitrary slots?
  - **Decision**: Not in v1.0. Templates define fixed slots. Dynamic slots deferred to v2.0+ (template inheritance).

---

### Rule 5: Slot types must match template

**Examples**:
- ✅ **Valid**: Content `heading` slot is `markdown_inline` (matches template)
- ✅ **Valid**: Content `body` slot is `markdown_block` (matches template)
- ❌ **Invalid**: Content `heading` slot is `markdown_block` (template expects `markdown_inline`)
  → StructureError: "Slide 2 slot 'heading' has type markdown_block but template expects markdown_inline"

**Questions**:
- Q8: How to determine slot type from parsed content?
  - **Decision**: Parsing layer assigns type based on markdown structure:
    - Single-line text → `markdown_inline`
    - Multi-line text → `markdown_block`
    - Image reference → `image`
    - Code block → `code`
- Q9: What if slot content could be either type?
  - **Decision**: Parser makes best guess. Validation checks if it matches template. If mismatch, error.

---

### Rule 6: YAML front matter must be valid

**Examples**:
- ✅ **Valid**: `---\ntemplate: content\n---`
- ❌ **Invalid**: `---\ntemplate content\n---` (missing colon)
  → ParsingError: "Slide 2 has malformed YAML front matter: expected ':' at line 1"
- ❌ **Invalid**: `---\n  template: content\n  theme:\n---` (incomplete nested YAML)
  → ParsingError: "Slide 2 has malformed YAML front matter: unexpected end of stream"

**Questions**:
- Q10: Is YAML validation part of Structure Validation?
  - **Decision**: No, YAML parsing happens before validation. Malformed YAML is a **Parsing Error**, not **Validation Error**. Validation assumes YAML is already parsed.

---

## 📝 Concrete Examples (Given/When/Then)

### Example 1: Valid Deck

```gherkin
Feature: Structure Validation

  Scenario: Valid deck with multiple slides
    Given I have a markdown file with content:
      """
      ---
      template: title
      ---
      # My Presentation

      ---
      ---
      template: content
      ---
      ## Slide 2
      Content here.

      ---
      ---
      template: content
      ---
      ## Slide 3
      More content.
      """
    When I parse and validate the markdown
    Then structure validation succeeds
    And the SlideDeck has 3 Slides
```

---

### Example 2: Empty Deck ❌

```gherkin
  Scenario: Empty deck triggers error
    Given I have an empty markdown file
    When I parse and validate the markdown
    Then structure validation fails with StructureError
    And the error message contains "SlideDeck must contain at least 1 slide"
```

---

### Example 3: Deck Exceeds Max 200 Slides ❌

```gherkin
  Scenario: Deck with 201 slides triggers error
    Given I have a markdown file with 201 slides
    When I parse and validate the markdown
    Then structure validation fails with StructureError
    And the error message contains "SlideDeck exceeds max 200 slides (found 201)"
```

---

### Example 4: Slide References Non-Existent Template ❌

```gherkin
  Scenario: Slide references invalid template
    Given I have a markdown file with content:
      """
      ---
      template: nonexistent-template
      ---
      ## Slide Content
      """
    When I parse and validate the markdown
    Then structure validation fails with StructureError
    And the error message contains "references template 'nonexistent-template' which does not exist"
```

---

### Example 5: Slide Missing Required Slot ❌

```gherkin
  Scenario: Content slide missing required body slot
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Heading Only
      """
    When I parse and validate the markdown
    Then structure validation fails with StructureError
    And the error message contains "missing required slot 'body'"
```

---

### Example 6: Slide Has Extra Slot ❌

```gherkin
  Scenario: Slide has unexpected slot not in template
    Given I have a parsed Slide with:
      - template: content
      - slots: heading, body, footer
    When I validate the Slide structure
    Then structure validation fails with StructureError
    And the error message contains "unexpected slot 'footer'"
```

---

### Example 7: Slot Type Mismatch ❌

```gherkin
  Scenario: Slot type does not match template
    Given I have a parsed Slide with:
      - template: content
      - heading slot type: markdown_block (template expects markdown_inline)
    When I validate the Slide structure
    Then structure validation fails with StructureError
    And the error message contains "slot 'heading' has type markdown_block but template expects markdown_inline"
```

---

### Example 8: Multiple Errors Collected

```gherkin
  Scenario: Multiple structure errors collected
    Given I have a markdown file with:
      - Slide 1: missing required slot 'body'
      - Slide 2: references invalid template 'unknown'
      - Slide 3: has extra slot 'footer'
    When I parse and validate the markdown
    Then structure validation fails with 3 errors
    And all errors are collected in NonEmptyList[StructureError]
```

---

## 🚧 Open Questions

| ID | Question | Status | Decision |
|----|----------|--------|----------|
| Q1 | Why max 200 slides? | ✅ Resolved | Reasonable upper bound for presentations |
| Q2 | Should max be configurable? | ✅ Resolved | No for v1.0, hard-coded to 200 |
| Q3 | Default template if missing? | ✅ Resolved | Default to `content` |
| Q4 | Template name case-sensitive? | ✅ Resolved | No, normalize to lowercase |
| Q5 | Empty slot vs missing slot? | ✅ Resolved | Structure checks presence, Content checks emptiness |
| Q6 | Extra slots error or warning? | ✅ Resolved | Error (likely typo/confusion) |
| Q7 | Templates with arbitrary slots? | ✅ Resolved | Not in v1.0, deferred to v2.0+ |
| Q8 | How to determine slot type? | ✅ Resolved | Parser assigns based on structure |
| Q9 | Ambiguous slot types? | ✅ Resolved | Parser makes best guess, validation checks |
| Q10 | YAML validation part of Structure? | ✅ Resolved | No, parsing error (before validation) |

---

## ✅ Acceptance Criteria (Definition of Done)

### Functional Criteria

1. ✅ **AC1**: Deck with 0 slides triggers StructureError
2. ✅ **AC2**: Deck with 201+ slides triggers StructureError
3. ✅ **AC3**: Deck with 1-200 slides passes (structure check)
4. ✅ **AC4**: Slide with invalid template triggers StructureError
5. ✅ **AC5**: Slide missing required slot triggers StructureError
6. ✅ **AC6**: Slide with extra slot triggers StructureError
7. ✅ **AC7**: Slot type mismatch triggers StructureError
8. ✅ **AC8**: Missing template field defaults to `content`
9. ✅ **AC9**: Template names are case-insensitive

### Technical Criteria

10. ✅ **AC10**: Validation returns `Either[NonEmptyList[StructureError], SlideDeck]`
11. ✅ **AC11**: All errors collected (not fail-fast)
12. ✅ **AC12**: Error messages include slide number/ID
13. ✅ **AC13**: Error messages are human-readable (no jargon)
14. ✅ **AC14**: Validation loads templates from Template Library
15. ✅ **AC15**: Default templates (title, content) always present
16. ✅ **AC16**: All domain terms from ubiquitous language used

### Non-Functional Criteria

17. ✅ **AC17**: Validation completes in < 50ms for 50-slide deck
18. ✅ **AC18**: Pure functional code (no side effects)
19. ✅ **AC19**: Unit tests for each scenario using ScalaTest

**Scenarios**: 8 concrete examples documented
- 1 success path
- 7 failure paths

**Dependencies**:
- Template Library (loading templates)
- Domain Model (SlideDeck, Slide, Slot, SlotContent, TemplateId)
- Validation framework (NonEmptyList[ValidationError])

---

## 📚 Related Artifacts

- **User Story Tracker**: [BACKLOG-V3.md](../../BACKLOG-V3.md)
- **Domain Model**: [slide-deck-aggregate.md](../domain-models/aggregates/slide-deck-aggregate.md)
- **Template Aggregate**: [template-aggregate.md](../domain-models/aggregates/template-aggregate.md)
- **Ubiquitous Language**: [ubiquitous-language.md](../domain-models/ubiquitous-language.md)
- **Related Stories**: US-012 Density Validation, US-013 Content Validation, US-015 Collect All Errors

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
