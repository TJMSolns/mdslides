# Example Mapping: US-011 Structure Validation

**Session Date**: 2024-12-20
**Participants**: Tony Moores (Business/Dev/QA)
**Story**: US-011 - Structure Validation
**Status**: Complete

---

## 📖 Story Card

```
┌─────────────────────────────────────────────┐
│ 📘 USER STORY                               │
│                                             │
│ As a slide deck author                      │
│ I want to receive validation errors for     │
│ structural issues                           │
│ So that I know my slide deck has all        │
│ required elements                           │
│                                             │
│ Priority: P0 (Blocker)                      │
│ Estimated Effort: 2 days                    │
└─────────────────────────────────────────────┘
```

---

## 🔵 Blue Cards: Business Rules

### Rule 1: SlideDeck must have 1-200 slides

Minimum 1 slide, maximum 200 slides.

**Rationale**:
- Min: Empty deck is meaningless
- Max: Decks with 200+ slides are course materials, not presentations

**Error Type**: StructureError

---

### Rule 2: Every slide must reference valid template

Each slide must have a `template:` field referencing a template that exists in the Template Library.

**Default**: If no `template:` specified, default to `content`

**Template Names**: Case-insensitive (normalize to lowercase)

**Error Type**: StructureError

---

### Rule 3: All required slots must be present

Every slot marked `required: true` in the template must be present in the slide.

**Presence vs Content**: Structure validation checks slot exists, not that it's non-empty (Content Validation checks emptiness).

**Error Type**: StructureError

---

### Rule 4: No extra slots beyond template definition

Slides cannot have slots that are not defined in their template.

**Rationale**: Extra slots likely indicate typos or author confusion about template.

**Error Type**: StructureError

---

### Rule 5: Slot types must match template

Each slot's type must match what the template expects.

**Slot Types**:
- `markdown_inline` (single-line)
- `markdown_block` (multi-line)
- `image` (image reference)
- `code` (code block)

**Type Detection**: Parser assigns type based on markdown structure

**Error Type**: StructureError

---

### Rule 6: All errors collected (not fail-fast)

Structure validation collects ALL errors before returning, using `Either[NonEmptyList[StructureError], SlideDeck]`.

**Rationale**: Author sees all problems at once, not just first error.

---

## 🟢 Green Cards: Examples

### ✅ EXAMPLE 1: Valid deck with 1 slide

```markdown
GIVEN markdown with:
  ---
  template: title
  ---
  # My Presentation

WHEN parsed and validated

THEN:
  - Structure validation: SUCCESS
  - SlideDeck has 1 Slide

Rules tested: 1, 2, 3
```

---

### ✅ EXAMPLE 2: Valid deck with 5 slides

```markdown
GIVEN markdown with 5 slides:
  - 1 title slide
  - 4 content slides

WHEN parsed and validated

THEN:
  - Structure validation: SUCCESS
  - SlideDeck has 5 Slides

Rules tested: 1, 2, 3
```

---

### ✅ EXAMPLE 3: Valid deck with 200 slides (boundary)

```markdown
GIVEN markdown with exactly 200 slides

WHEN parsed and validated

THEN:
  - Structure validation: SUCCESS
  - SlideDeck has 200 Slides

Rules tested: 1
```

---

### ❌ EXAMPLE 4: Empty deck (0 slides)

```markdown
GIVEN empty markdown file

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Error: StructureError
  - Message: "SlideDeck must contain at least 1 slide (found 0)"

Rules tested: 1
```

---

### ❌ EXAMPLE 5: Deck exceeds max (201 slides)

```markdown
GIVEN markdown with 201 slides

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Error: StructureError
  - Message: "SlideDeck exceeds max 200 slides (found 201)"

Rules tested: 1
```

---

### ❌ EXAMPLE 6: Slide references non-existent template

```markdown
GIVEN markdown with:
  ---
  template: nonexistent-template
  ---
  ## Slide Content

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Error: StructureError
  - Message: "Slide 1 references template 'nonexistent-template' which does not exist in Template Library"

Rules tested: 2
```

---

### ✅ EXAMPLE 7: Missing template field defaults to content

```markdown
GIVEN markdown with:
  ## Slide with no template specified
  Content here.

WHEN parsed and validated

THEN:
  - Slide uses template: "content" (default)
  - Structure validation: SUCCESS

Rules tested: 2
```

---

### ✅ EXAMPLE 8: Template name is case-insensitive

```markdown
GIVEN markdown with:
  ---
  template: Content
  ---
  ## Slide

WHEN parsed and validated

THEN:
  - Template normalized to "content"
  - Structure validation: SUCCESS

Rules tested: 2
```

---

### ❌ EXAMPLE 9: Content slide missing required body slot

```markdown
GIVEN markdown with:
  ---
  template: content
  ---
  ## Heading Only
  (no body content)

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Error: StructureError
  - Message: "Slide 1 (template: content) is missing required slot 'body'"

Rules tested: 3
```

---

### ❌ EXAMPLE 10: Title slide missing required title slot

```markdown
GIVEN markdown with:
  ---
  template: title
  ---
  (no title heading)

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Error: StructureError
  - Message: "Slide 1 (template: title) is missing required slot 'title'"

Rules tested: 3
```

---

### ❌ EXAMPLE 11: Slide has extra slot

```markdown
GIVEN parsed Slide with:
  - template: content
  - slots: heading, body, footer

WHEN validated

THEN:
  - Validation: FAILED
  - Error: StructureError
  - Message: "Slide 1 (template: content) has unexpected slot 'footer' (not in template definition)"

Rules tested: 4
```

---

### ❌ EXAMPLE 12: Slot type mismatch

```markdown
GIVEN parsed Slide with:
  - template: content
  - heading slot type: markdown_block
    (template expects markdown_inline)

WHEN validated

THEN:
  - Validation: FAILED
  - Error: StructureError
  - Message: "Slide 1 slot 'heading' has type markdown_block but template expects markdown_inline"

Rules tested: 5
```

---

### ❌ EXAMPLE 13: Multiple errors collected

```markdown
GIVEN markdown with:
  - Slide 1: missing required slot 'body'
  - Slide 2: references invalid template 'unknown'
  - Slide 3: has extra slot 'footer'

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Errors: 3 StructureErrors
  - All errors collected in NonEmptyList
  - Error 1: "Slide 1... missing required slot 'body'"
  - Error 2: "Slide 2 references template 'unknown'..."
  - Error 3: "Slide 3... unexpected slot 'footer'"

Rules tested: 3, 4, 6
```

---

## 🔴 Red Cards: Questions (All Resolved ✅)

### Q1: Why max 200 slides? ✅

**Answer**: Reasonable upper bound for presentations. 200+ slides are course materials, not presentations.

---

### Q2: Should max be configurable? ✅

**Answer**: No for v1.0. Hard-coded to 200. Can add config in v2.0 if needed.

---

### Q3: Default template if missing? ✅

**Answer**: Default to `content` (most common slide type).

---

### Q4: Template name case-sensitive? ✅

**Answer**: No. Normalize to lowercase (e.g., `Content` → `content`).

---

### Q5: Empty slot vs missing slot? ✅

**Answer**: Structure checks presence of slot. Content Validation (US-013) checks if slot is empty.

**Example**: Slide with heading slot containing only whitespace passes Structure Validation but fails Content Validation.

---

### Q6: Extra slots error or warning? ✅

**Answer**: Error. Extra slots likely indicate typo or confusion. Better to fail explicitly.

---

### Q7: Templates with arbitrary slots? ✅

**Answer**: Not in v1.0. Templates define fixed slots. Dynamic slots deferred to v2.0+ (template inheritance).

---

### Q8: How to determine slot type? ✅

**Answer**: Parser assigns type based on markdown structure:
- Single-line text → `markdown_inline`
- Multi-line text/paragraphs → `markdown_block`
- Image reference → `image`
- Code block → `code`

---

### Q9: Ambiguous slot types? ✅

**Answer**: Parser makes best guess based on structure. Validation checks if it matches template expectation. If mismatch, error.

---

### Q10: YAML validation part of Structure? ✅

**Answer**: No. YAML parsing happens before validation. Malformed YAML is a **Parsing Error**, not **Validation Error**.

---

## 🎯 Story Readiness Assessment

### Coverage Summary

| Metric | Count |
|--------|-------|
| Rules | 6 blue cards |
| Examples | 13 green cards (3 success, 10 failure) |
| Questions | 10 red cards (all resolved ✅) |

### Rule → Example Coverage

```
Rule 1 (1-200 slides):
├── Example 1 ✅ (1 slide)
├── Example 2 ✅ (5 slides)
├── Example 3 ✅ (200 slides)
├── Example 4 ❌ (0 slides)
└── Example 5 ❌ (201 slides)

Rule 2 (Valid template):
├── Example 1 ✅ (title template)
├── Example 2 ✅ (mixed templates)
├── Example 6 ❌ (nonexistent template)
├── Example 7 ✅ (default to content)
└── Example 8 ✅ (case-insensitive)

Rule 3 (Required slots):
├── Example 1 ✅ (title has title)
├── Example 2 ✅ (content has heading+body)
├── Example 9 ❌ (missing body)
└── Example 10 ❌ (missing title)

Rule 4 (No extra slots):
└── Example 11 ❌ (extra footer slot)

Rule 5 (Slot types match):
└── Example 12 ❌ (type mismatch)

Rule 6 (All errors collected):
└── Example 13 ❌ (3 errors collected)
```

### Confidence Level

**HIGH CONFIDENCE** ✅

**Reasons**:
- All 10 questions resolved
- 6 clear business rules identified
- 13 concrete examples cover success and failure paths
- Examples cover boundary conditions (0, 1, 200, 201 slides)
- Examples cover error collection (multiple errors)
- Clear separation between Structure vs Content validation

**Risks Identified**:
- Template Library loading mechanism (must be available at validation time)
- Slot type detection in parser (must be accurate)
- Performance for large decks (200 slides with validation)

**Ready for TDD**: YES ✅

---

## 📋 Acceptance Criteria Summary

### Must Have (Critical)

1. ✅ Deck with 0 slides → StructureError
2. ✅ Deck with 201 slides → StructureError
3. ✅ Deck with 1-200 slides → SUCCESS
4. ✅ Invalid template → StructureError
5. ✅ Missing required slot → StructureError
6. ✅ Extra slot → StructureError
7. ✅ Slot type mismatch → StructureError
8. ✅ All errors collected (NonEmptyList)

### Should Have (Important)

9. ✅ Missing template defaults to `content`
10. ✅ Template names case-insensitive
11. ✅ Error messages include slide number
12. ✅ Error messages are human-readable

### Nice to Have (Enhancement)

13. ✅ Validation < 50ms for 50-slide deck

---

## 🎨 Visual Example Map

```
┌────────────────────────────────────────────────────────────┐
│                      📘 USER STORY                         │
│             Structure Validation (US-011)                  │
└────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R1 │          │ 🔵 R2 │          │ 🔵 R3 │
    │ 1-200 │          │ Valid │          │Require│
    │Slides │          │ Tmpl  │          │ Slots │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴───┬──────┐   ┌──┴──┬──────┐   ┌──┴──┬──────┐
    │✅ E1-3│❌ E4 │   │✅ E1│✅ E7 │   │✅ E1│❌ E9 │
    │       │❌ E5 │   │✅ E2│✅ E8 │   │✅ E2│❌ E10│
    └───────┴──────┘   │❌ E6│      │   └─────┴──────┘
                       └─────┴──────┘

    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R4 │          │ 🔵 R5 │          │ 🔵 R6 │
    │  No   │          │ Type  │          │Collect│
    │ Extra │          │ Match │          │  All  │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴───┐          ┌──┴──┐           ┌──┴──┐
    │❌ E11 │          │❌E12│           │❌E13│
    └───────┘          └─────┘           └─────┘

Questions (All ✅):
Q1-Q10: All resolved (see Red Cards section)
```

---

## 🔄 Next Steps

1. ✅ **Three Amigos Complete**: All rules and examples documented
2. ✅ **Example Mapping Complete**: 13 examples with full coverage
3. 🔄 **Update BACKLOG-V3.md**: Mark US-011 as Ready for Implementation
4. ⏭️ **Proceed to US-012**: Density Validation (next in validation framework)

---

**Session Type**: Ceremony 2.2 - Example Mapping Workshop
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Story Status**: Ready for TDD ✅
