# Three Amigos Session #004
## Speaker Notes in Markdown

---

```yaml
# MACHINE-READABLE METADATA
session:
  id: 3A-004-SPEAKER-NOTES
  date: 2024-12-20
  user_story_id: US-004
  participants:
    - Tony Moores (Business/Dev/QA)
  duration_minutes: 30-40
  status: complete

story:
  title: Speaker Notes in Markdown
  type: Feature
  priority: P0 (Blocker)
  epic: Core Slide Deck Creation
```

---

## 📋 User Story

**As a** slide deck author
**I want to** add speaker notes to slides
**So that** I can remember what to say during presentations

**Business Value**: Essential for presentation preparation - required by Charter Phase 1

**Technical Scope**: Parse and store speaker notes in domain model (v1.0), rendering deferred to v1.1 (US-034)

---

## 🎭 Three Perspectives

### 👔 Business Perspective (Product Owner)

**What success looks like**:
- Author adds speaker notes in YAML front matter using `notes:` field
- Notes support multi-line text (using YAML `|` syntax)
- Notes can include simple markdown formatting (bold, italics, lists)
- Notes are validated (max 500 chars → warning if exceeded)
- Notes are parsed and stored in Slide aggregate
- **v1.0**: Parse only, no rendering
- **v1.1**: Render in speaker view (US-034)

**Acceptance criteria**:
- ✅ Speaker notes added via `notes:` in front matter
- ✅ Multi-line notes supported (YAML `|` or `>` syntax)
- ✅ Notes can be empty (optional field)
- ✅ Notes max 500 chars → validation warning if exceeded
- ✅ Notes stored in Slide aggregate as SlotContent
- ✅ Notes parsing does NOT require rendering

---

### 💻 Development Perspective (Technical)

**Implementation approach**:
1. **Extend Front Matter Parsing**: Add `notes:` field to YAML front matter schema
2. **Parse Notes**: Extract `notes:` from per-slide front matter (or global front matter)
3. **Store in Domain Model**: Add `notes` slot to Slide aggregate (optional SlotContent)
4. **Validate Length**: Warn if notes exceed 500 chars (not an error, just warning)
5. **No Rendering**: v1.0 does NOT render notes - just parse and validate

**Technical risks**:
- **YAML Multi-line Syntax**: `|` (literal) vs `>` (folded) - which to support?
  - Answer: Support both. YAML parser handles this.
- **Markdown in Notes**: Should notes support markdown formatting?
  - Answer: Yes, parse as markdown_block (same as body slot)
- **Notes Slot vs Notes Field**: Is `notes` a slot or just metadata?
  - Answer: Treat as optional slot. Allows template-level constraints if needed.
- **Global vs Per-Slide Notes**: Can global front matter have notes?
  - Answer: Yes, but usually notes are per-slide. Global notes would apply to all slides (rare use case).

**Dependencies**:
- YAML parser (SnakeYAML or circe-yaml)
- Markdown parser (Flexmark) if notes support markdown
- Slide aggregate (add optional `notes` slot)
- Validation framework (for 500-char warning)

---

### 🧪 Testing Perspective (Quality/Edge Cases)

**Happy path scenarios**:
1. Slide with simple single-line notes
2. Slide with multi-line notes (using `|`)
3. Slide with markdown formatting in notes (bold, lists)
4. Slide with no notes (optional field)
5. Global front matter with notes (applied to all slides)

**Edge cases**:
1. Notes exceed 500 chars → validation warning
2. Notes with only whitespace → treated as empty
3. Notes with YAML special characters (`:`, `-`, `>`) → must be quoted or use `|`
4. Notes field is null/undefined → treated as empty
5. Notes with code blocks, images → allowed (markdown_block supports all)
6. Very long notes (10,000+ chars) → performance test

**Non-functional requirements**:
- Parsing notes should not significantly impact performance (< 5ms per slide)
- Warning message for long notes should be clear and actionable
- Notes should be accessible via Slide aggregate API

---

## 🗂️ Example Mapping

### Rule 1: Speaker notes added via `notes:` in front matter

**Examples**:
- ✅ **Valid**: Single-line notes
  ```markdown
  ---
  template: content
  notes: Remember to pause after this slide.
  ---
  ## Slide Content
  ```
  → Notes: "Remember to pause after this slide."

- ✅ **Valid**: Multi-line notes using `|` (literal)
  ```markdown
  ---
  template: content
  notes: |
    Remember to emphasize the key differentiator.
    Transition to next slide by asking a question.
    Allow 2 minutes for Q&A.
  ---
  ## Slide Content
  ```
  → Notes: Three lines preserved exactly

- ✅ **Valid**: Multi-line notes using `>` (folded)
  ```markdown
  ---
  template: content
  notes: >
    This is a long note that will be folded into
    a single line with spaces instead of newlines.
  ---
  ## Slide Content
  ```
  → Notes: Single line with spaces

**Questions**:
- Q1: Can notes be in global front matter?
  - **Decision**: Yes, but rare. Global notes would apply to all slides (e.g., "Speak slowly" for entire deck).
- Q2: Are notes required or optional?
  - **Decision**: Optional. Empty/missing notes are valid.

---

### Rule 2: Notes support markdown formatting

**Examples**:
- ✅ **Valid**: Notes with bold/italics
  ```markdown
  ---
  notes: |
    **Important**: Mention the partnership with Acme Corp.
    *Emphasize* the cost savings.
  ---
  ```
  → Notes contain markdown which will be rendered in speaker view (v1.1)

- ✅ **Valid**: Notes with bullet list
  ```markdown
  ---
  notes: |
    Key points to cover:
    - Cost savings (30%)
    - Faster deployment (2x)
    - Better reliability (99.9% uptime)
  ---
  ```
  → Notes contain bullet list

- ✅ **Valid**: Notes with inline code
  ```markdown
  ---
  notes: |
    Mention that we use `Scala 3` for type safety.
  ---
  ```

**Questions**:
- Q3: Should notes support code blocks, images?
  - **Decision**: Yes, treat notes as `markdown_block` type (supports all markdown elements). However, images in speaker notes are uncommon.

---

### Rule 3: Notes max 500 chars → validation warning

**Examples**:
- ✅ **Valid**: Notes with 450 chars
  ```markdown
  ---
  notes: |
    [450 characters of speaker notes here...]
  ---
  ```
  → Validation: SUCCESS (no warning)

- ⚠️ **Warning**: Notes with 600 chars
  ```markdown
  ---
  notes: |
    [600 characters of speaker notes here...]
  ---
  ```
  → Validation: WARNING
  → Message: "Speaker notes on slide 3 exceed recommended 500 chars (found 600 chars). Consider breaking into multiple slides."

- ⚠️ **Warning**: Notes with 10,000 chars
  ```markdown
  ---
  notes: |
    [10,000 characters of speaker notes here...]
  ---
  ```
  → Validation: WARNING
  → Message: "Speaker notes on slide 3 are excessively long (10,000 chars). Max recommended: 500 chars."

**Questions**:
- Q4: Is 500 chars a hard limit (error) or soft limit (warning)?
  - **Decision**: Soft limit (warning only). Authors may have valid reasons for longer notes (e.g., full script for recorded presentations).
- Q5: How to count chars? Include markdown syntax or just plain text?
  - **Decision**: Count raw markdown characters (including syntax). Simpler to implement and author can see char count in editor.

---

### Rule 4: Notes are optional

**Examples**:
- ✅ **Valid**: Slide with no notes
  ```markdown
  ---
  template: content
  ---
  ## Slide Content
  No speaker notes needed.
  ```
  → Notes: empty/null

- ✅ **Valid**: Notes field missing entirely
  ```markdown
  ---
  template: content
  theme: corporate
  ---
  ## Slide Content
  ```
  → Notes: empty/null (not a validation error)

- ✅ **Valid**: Notes field is empty string
  ```markdown
  ---
  template: content
  notes: ""
  ---
  ## Slide Content
  ```
  → Notes: empty

- ✅ **Valid**: Notes with only whitespace
  ```markdown
  ---
  template: content
  notes: |


  ---
  ## Slide Content
  ```
  → Notes: treated as empty (whitespace trimmed)

**Questions**:
- Q6: Should whitespace-only notes be flagged?
  - **Decision**: No. Treat as empty, no warning.

---

### Rule 5: Notes stored in Slide aggregate

**Examples**:
- ✅ **Implementation**: Notes as optional slot
  ```scala
  case class Slide(
    id: SlideId,
    template: TemplateId,
    slots: Map[SlotName, SlotContent],
    notes: Option[SlotContent] = None  // Speaker notes
  )
  ```

- ✅ **Alternative Implementation**: Notes as regular slot
  ```scala
  // Template definition includes optional notes slot
  slots:
    - name: heading
      type: markdown_inline
      required: true
    - name: body
      type: markdown_block
      required: true
    - name: notes
      type: markdown_block
      required: false
      constraints:
        max_chars: 500  // Warning if exceeded
  ```

**Questions**:
- Q7: Should notes be a dedicated field or just another slot?
  - **Decision**: Treat as slot (named `notes`). This allows template-level constraints and keeps Slide aggregate simpler.
- Q8: What is the type of notes slot?
  - **Decision**: `markdown_block` (supports multi-line, markdown formatting, lists, etc.)

---

### Rule 6: v1.0 scope: Parse only (no rendering)

**Examples**:
- ✅ **v1.0**: Parse and validate notes
  ```scala
  val slide = parseSlide(markdown)
  slide.slots("notes") // SlotContent exists
  ```

- ❌ **NOT v1.0**: Render notes in HTML
  ```scala
  val html = renderSlide(slide)
  // v1.0 does NOT include speaker notes in output
  // v1.1 (US-034) will render in separate speaker view
  ```

**Questions**:
- Q9: Should v1.0 HTML output include notes in comments?
  - **Decision**: No. HTML output in v1.0 is just the slide content. Notes rendering is entirely deferred to v1.1 (US-034 Speaker View).

---

## 📝 Concrete Examples (Given/When/Then)

### Example 1: Simple Single-Line Notes

```gherkin
Feature: Speaker Notes

  Scenario: Add simple single-line notes
    Given I have a markdown file with content:
      """
      ---
      template: content
      notes: Remember to pause after this slide.
      ---
      ## Key Takeaway
      Functional programming reduces bugs.
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And the Slide has notes "Remember to pause after this slide."
    And validation succeeds
```

---

### Example 2: Multi-Line Notes

```gherkin
  Scenario: Add multi-line notes using YAML literal block
    Given I have a markdown file with content:
      """
      ---
      template: content
      notes: |
        Remember to emphasize the key differentiator.
        Transition to next slide by asking a question.
        Allow 2 minutes for Q&A.
      ---
      ## Why Functional Programming?
      Content here.
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And the Slide has notes with 3 lines
    And validation succeeds
```

---

### Example 3: Notes with Markdown Formatting

```gherkin
  Scenario: Notes with markdown formatting
    Given I have a markdown file with content:
      """
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
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And the Slide notes contain markdown (bold, italics, list)
    And validation succeeds
```

---

### Example 4: No Notes (Optional)

```gherkin
  Scenario: Slide with no speaker notes
    Given I have a markdown file with content:
      """
      ---
      template: content
      ---
      ## Simple Slide
      No notes needed.
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And the Slide has no notes (empty/null)
    And validation succeeds
```

---

### Example 5: Notes Exceed 500 Chars ⚠️

```gherkin
  Scenario: Notes exceed recommended 500 char limit
    Given I have a markdown file with content:
      """
      ---
      template: content
      notes: |
        [600 characters of detailed speaker notes here that exceed
        the recommended 500 character limit for concise speaker notes.
        This is a warning, not an error, because some presenters may
        need detailed scripts for recorded presentations or training...]
      ---
      ## Complex Topic
      Content here.
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And validation succeeds with warning
    And the warning message contains "Speaker notes exceed 500 chars (found 600)"
```

---

### Example 6: Global Front Matter Notes

```gherkin
  Scenario: Global front matter with notes (applied to all slides)
    Given I have a markdown file with content:
      """
      ---
      notes: Remember to speak slowly for this entire deck.
      ---
      ## Slide 1
      Content.
      ---
      ## Slide 2
      Content.
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 2 Slides
    And Slide 1 has notes "Remember to speak slowly for this entire deck."
    And Slide 2 has notes "Remember to speak slowly for this entire deck."
    And validation succeeds
```

---

### Example 7: Per-Slide Notes Override Global

```gherkin
  Scenario: Per-slide notes override global notes
    Given I have a markdown file with content:
      """
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
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 2 Slides
    And Slide 1 has notes "Default notes for all slides."
    And Slide 2 has notes "Custom notes for this slide only."
    And validation succeeds
```

---

### Example 8: Whitespace-Only Notes (Treated as Empty)

```gherkin
  Scenario: Notes with only whitespace are treated as empty
    Given I have a markdown file with content:
      """
      ---
      template: content
      notes: |


      ---
      ## Slide Content
      Content here.
      """
    When I parse the markdown into a SlideDeck
    Then the SlideDeck has 1 Slide
    And the Slide has no notes (whitespace trimmed to empty)
    And validation succeeds
```

---

### Example 9: Very Long Notes (Stress Test) ⚠️

```gherkin
  Scenario: Very long notes (10,000 chars) trigger warning
    Given I have a markdown file with notes containing 10,000 characters
    When I parse the markdown into a SlideDeck
    Then validation succeeds with warning
    And the warning message contains "Speaker notes are excessively long (10,000 chars)"
```

---

## 🚧 Open Questions

| ID | Question | Status | Decision |
|----|----------|--------|----------|
| Q1 | Can notes be in global front matter? | ✅ Resolved | Yes, applies to all slides |
| Q2 | Are notes required or optional? | ✅ Resolved | Optional |
| Q3 | Support code blocks, images in notes? | ✅ Resolved | Yes, treat as markdown_block |
| Q4 | Is 500 chars hard or soft limit? | ✅ Resolved | Soft limit (warning only) |
| Q5 | How to count chars (raw or plain text)? | ✅ Resolved | Count raw markdown (simpler) |
| Q6 | Flag whitespace-only notes? | ✅ Resolved | No, treat as empty |
| Q7 | Notes as field or slot? | ✅ Resolved | Treat as slot (named `notes`) |
| Q8 | Type of notes slot? | ✅ Resolved | markdown_block |
| Q9 | Should v1.0 HTML include notes? | ✅ Resolved | No, rendering deferred to v1.1 |

---

## ✅ Acceptance Criteria (Definition of Done)

### Functional Criteria

1. ✅ **AC1**: Notes added via `notes:` field in YAML front matter
2. ✅ **AC2**: Single-line notes supported
3. ✅ **AC3**: Multi-line notes supported (YAML `|` and `>` syntax)
4. ✅ **AC4**: Notes support markdown formatting (bold, italics, lists, code, etc.)
5. ✅ **AC5**: Notes are optional (missing or empty is valid)
6. ✅ **AC6**: Whitespace-only notes treated as empty
7. ✅ **AC7**: Notes max 500 chars → validation warning (not error)
8. ✅ **AC8**: Global front matter notes apply to all slides
9. ✅ **AC9**: Per-slide notes override global notes
10. ✅ **AC10**: Notes stored in Slide aggregate as slot (named `notes`)

### Technical Criteria

11. ✅ **AC11**: Notes slot type is `markdown_block`
12. ✅ **AC12**: Notes slot is optional (required: false)
13. ✅ **AC13**: Validation warns if notes > 500 chars
14. ✅ **AC14**: Char count is raw markdown (not plain text)
15. ✅ **AC15**: Notes parsing uses YAML parser (SnakeYAML or circe-yaml)
16. ✅ **AC16**: v1.0 does NOT render notes (parse only)
17. ✅ **AC17**: All domain terms from ubiquitous language used
18. ✅ **AC18**: Unit tests for each scenario using ScalaTest

### Non-Functional Criteria

19. ✅ **AC19**: Parsing notes adds < 5ms per slide
20. ✅ **AC20**: Warning messages are clear and actionable
21. ✅ **AC21**: Notes accessible via Slide aggregate API
22. ✅ **AC22**: Pure functional code (no side effects)

**Scenarios**: 9 concrete examples documented
- 7 success paths
- 2 warning paths (long notes)

**Dependencies**:
- YAML parser (SnakeYAML or circe-yaml)
- Markdown parser (Flexmark) for markdown_block parsing
- Validation framework (warnings collection)
- Slide aggregate (notes slot)

---

## 📚 Related Artifacts

- **User Story Tracker**: [BACKLOG-V3.md](../../BACKLOG-V3.md)
- **Domain Model**: [slide-deck-aggregate.md](../domain-models/aggregates/slide-deck-aggregate.md)
- **Ubiquitous Language**: [ubiquitous-language.md](../domain-models/ubiquitous-language.md)
- **Related Story**: [US-034 Speaker View Rendering](../../BACKLOG-V3.md#us-034-speaker-view-with-notes) (v1.1 - deferred)

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
