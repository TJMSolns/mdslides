# Example Mapping: US-015 Collect All Validation Errors

**Session Date**: 2024-12-20
**Participants**: Tony Moores (Business/Dev/QA)
**Story**: US-015 - Collect All Validation Errors (No Fail-Fast)
**Status**: Complete

---

## 📖 Story Card

```
┌─────────────────────────────────────────────┐
│ 📘 USER STORY                               │
│                                             │
│ As a slide deck author                      │
│ I want to see all validation errors at once │
│ So that I can fix all issues in one pass    │
│                                             │
│ Priority: P0 (Blocker)                      │
│ Estimated Effort: 2 days                    │
└─────────────────────────────────────────────┘
```

---

## 🔵 Blue Cards: Business Rules

### Rule 1: All validation errors must be collected (not fail-fast)

Validation must collect ALL errors before returning, not stop at first error.

**Rationale**: Better UX - author sees all problems at once, fixes all issues in one iteration.

**Return Type**: `Either[NonEmptyList[ValidationError], SlideDeck]`

---

### Rule 2: Errors must be grouped by slide ID

Errors for the same slide must be grouped together in formatted output.

**Grouping**: Primary sort by slide number (ascending), secondary sort by validation type (Structure, Density, Content).

---

### Rule 3: Each error must include context (slide ID, slot name, message)

Every error must provide enough information to identify the problem:
- Slide number (and template name)
- Slot name (if applicable)
- Validation type (Structure, Density, Content)
- What constraint was violated
- Actual vs expected values

**Error ADT**: Sealed trait with StructureError, DensityWarning, ContentError variants.

---

### Rule 4: Structure errors block Content validation (dependency)

If Structure validation fails for a slide, Content validation must be skipped for that slide.

**Rationale**: Content validation assumes slots exist (Structure checks presence). Can't check constraints on missing slots.

**Density Validation**: Always runs (independent of Structure/Content).

---

### Rule 5: Density warnings are non-blocking

Density warnings don't cause validation to fail - they're informational.

**Error Type**: `DensityWarning` (collected separately from blocking errors)

**Return Value**: Validation can succeed with warnings present.

---

### Rule 6: Errors must be formatted for readability

Errors formatted for CLI output with:
- Clear section headers (slide number + template)
- Indented error messages
- Validation type tags ([Structure], [Density], [Content])
- Warning indicators for non-blocking issues

---

## 🟢 Green Cards: Examples

### ✅ EXAMPLE 1: Valid deck (no errors)

```markdown
GIVEN markdown with 5 valid slides

WHEN parsed and validated

THEN:
  - Structure validation: SUCCESS
  - Density validation: SUCCESS
  - Content validation: SUCCESS
  - Result: Right(SlideDeck)
  - No errors collected

Rules tested: 1
```

---

### ❌ EXAMPLE 2: Single slide with 1 error

```markdown
GIVEN markdown with:
  ---
  template: content
  ---
  ## Heading
  (missing body)

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Errors: 1 ContentError
  - Error: "Slide 1 slot 'body' is required but empty"
  - Result: Left(NonEmptyList(ContentError))

Rules tested: 1, 3
```

---

### ❌ EXAMPLE 3: Single slide with multiple errors

```markdown
GIVEN markdown with:
  ---
  template: content
  ---
  ## This is a very long heading that exceeds the eighty character constraint
  (missing body)

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Errors: 2 ContentErrors
  - Error 1: "Slide 1 slot 'body' is required but empty"
  - Error 2: "Slide 1 slot 'heading' exceeds max 80 chars (found 85 chars)"
  - Result: Left(NonEmptyList(error1, error2))

Rules tested: 1, 2, 3
```

---

### ❌ EXAMPLE 4: Multiple slides with errors

```markdown
GIVEN markdown with:
  - Slide 1: missing required slot 'body'
  - Slide 2: heading exceeds 80 chars
  - Slide 3: body exceeds 150 words

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Errors: 3 errors (1 Structure, 2 Content)
  - Error 1: "Slide 1 slot 'body' is required but missing"
  - Error 2: "Slide 2 slot 'heading' exceeds max 80 chars"
  - Error 3: "Slide 3 slot 'body' exceeds max 150 words"
  - Result: Left(NonEmptyList(error1, error2, error3))

Rules tested: 1, 2, 3
```

---

### ❌ EXAMPLE 5: Structure error blocks Content validation

```markdown
GIVEN markdown with:
  ---
  template: content
  ---
  (no heading, no body)

WHEN parsed and validated

THEN:
  - Structure validation: FAILED (missing required slots)
  - Content validation: SKIPPED for this slide
  - Density validation: RUNS
  - Errors: 2 StructureErrors
  - Error 1: "Slide 1 slot 'heading' is required but missing"
  - Error 2: "Slide 1 slot 'body' is required but missing"
  - Result: Left(NonEmptyList(error1, error2))

Rules tested: 1, 4
```

---

### ⚠️ EXAMPLE 6: Density warnings are non-blocking

```markdown
GIVEN markdown with:
  ---
  template: content
  ---
  ## Heading

  Line 1
  Line 2
  ...
  Line 15
  (15 lines, exceeds max 12)

WHEN parsed and validated

THEN:
  - Structure validation: SUCCESS
  - Content validation: SUCCESS
  - Density validation: WARNING
  - Warnings: 1 DensityWarning
  - Warning: "Slide 1 slot 'body' exceeds max 12 lines (found 15 lines) [WARNING]"
  - Result: Right(SlideDeck) + warnings list

Rules tested: 5
```

---

### ❌ EXAMPLE 7: Errors and warnings combined

```markdown
GIVEN markdown with:
  - Slide 1: body exceeds 12 lines (Density Warning)
  - Slide 2: body is empty (Content Error)

WHEN parsed and validated

THEN:
  - Content validation: FAILED
  - Errors: 1 ContentError
  - Error: "Slide 2 slot 'body' is required but empty"
  - Warnings: 1 DensityWarning
  - Warning: "Slide 1 slot 'body' exceeds max 12 lines (found 15 lines)"
  - Result: Left(NonEmptyList(ContentError)) + warnings

Rules tested: 1, 5
```

---

### ❌ EXAMPLE 8: Large deck with many errors

```markdown
GIVEN markdown with 50 slides
AND 20 slides have errors:
  - 5 structure errors
  - 10 content errors
  - 5 density warnings

WHEN parsed and validated

THEN:
  - Validation: FAILED
  - Errors: 15 errors (5 structure + 10 content)
  - Warnings: 5 density warnings
  - All errors collected in NonEmptyList
  - Result: Left(NonEmptyList[ValidationError]) with 15 errors

Rules tested: 1, 2
```

---

### ❌ EXAMPLE 9: Error formatting (CLI output)

```markdown
GIVEN validation errors:
  - Slide 1: StructureError (missing slot 'body')
  - Slide 3: ContentError (heading too long)
  - Slide 3: ContentError (body too many words)

WHEN formatted for CLI display

THEN output is:
  """
  Validation failed with 3 errors:

  Slide 1 (template: content):
    [Structure] Slot 'body' is required but missing

  Slide 3 (template: content):
    [Content] Heading exceeds max 80 chars (found 95 chars)
    [Content] Body exceeds max 150 words (found 160 words)
  """

Rules tested: 2, 3, 6
```

---

### ❌ EXAMPLE 10: Errors sorted by slide number

```markdown
GIVEN validation errors:
  - Slide 5: ContentError
  - Slide 1: StructureError
  - Slide 3: ContentError

WHEN formatted for display

THEN errors are sorted by slide number:
  - Slide 1: StructureError
  - Slide 3: ContentError
  - Slide 5: ContentError

Rules tested: 2
```

---

### ❌ EXAMPLE 11: Multiple validation types on same slide

```markdown
GIVEN markdown with:
  - Slide 1: missing slot 'body' (Structure)
  - Slide 1: heading exceeds 12 lines (Density)

WHEN parsed and validated

THEN:
  - Structure validation: FAILED
  - Content validation: SKIPPED (Structure failed)
  - Density validation: WARNING
  - Errors: 1 StructureError
  - Warnings: 1 DensityWarning
  - Formatted output shows both issues grouped under Slide 1

Rules tested: 2, 4, 5, 6
```

---

### ✅ EXAMPLE 12: Empty error list (all valid)

```markdown
GIVEN markdown with 10 valid slides

WHEN parsed and validated

THEN:
  - All validation passes
  - Error list is empty (not NonEmptyList)
  - Result: Right(SlideDeck)

Rules tested: 1
```

---

## 🔴 Red Cards: Questions (All Resolved ✅)

### Q1: Stop after Structure errors? ✅

**Answer**: Yes, skip Content validation if Structure fails for a slide (dependency). Continue Density validation (independent).

---

### Q2: Warnings vs errors? ✅

**Answer**:
- **Density**: Warnings (non-blocking)
- **Structure + Content**: Errors (blocking)

---

### Q3: Error ordering? ✅

**Answer**: Sort by slide number (ascending), then by validation type (Structure, Density, Content).

---

### Q4: Error without slot name? ✅

**Answer**: Slot name is `Option[String]`. Deck-level errors use `None` (e.g., "SlideDeck exceeds max 200 slides").

---

### Q5: Skip Density if Structure fails? ✅

**Answer**: No. Density validation is independent (checks existing content, doesn't assume specific slots).

---

### Q6: Return errors and warnings? ✅

**Answer**: Two separate return values:
- `errors: Either[NonEmptyList[ValidationError], SlideDeck]`
- `warnings: List[DensityWarning]`

---

### Q7: Show warnings with errors? ✅

**Answer**: Yes. Mark warnings with `[WARNING]` tag. Show all issues to author.

---

## 🎯 Story Readiness Assessment

### Coverage Summary

| Metric | Count |
|--------|-------|
| Rules | 6 blue cards |
| Examples | 12 green cards (2 success, 10 failure/warning) |
| Questions | 7 red cards (all resolved ✅) |

### Rule → Example Coverage

```
Rule 1 (Collect all errors):
├── Example 1 ✅ (no errors)
├── Example 2 ❌ (1 error)
├── Example 3 ❌ (multiple errors on 1 slide)
├── Example 4 ❌ (multiple slides with errors)
├── Example 5 ❌ (structure errors)
├── Example 7 ❌ (errors + warnings)
├── Example 8 ❌ (large deck with many errors)
└── Example 12 ✅ (empty error list)

Rule 2 (Grouped by slide ID):
├── Example 3 ❌ (same slide, multiple errors)
├── Example 4 ❌ (different slides)
├── Example 8 ❌ (50 slides)
├── Example 9 ❌ (formatting example)
└── Example 10 ❌ (sorted by slide number)

Rule 3 (Error context):
├── Example 2 ❌ (single error)
├── Example 3 ❌ (multiple errors)
├── Example 4 ❌ (different error types)
└── Example 9 ❌ (formatted output)

Rule 4 (Structure blocks Content):
├── Example 5 ❌ (structure fails → content skipped)
└── Example 11 ❌ (structure + density on same slide)

Rule 5 (Density warnings non-blocking):
├── Example 6 ⚠️ (density warning only)
├── Example 7 ❌ (density warning + content error)
└── Example 11 ❌ (density warning + structure error)

Rule 6 (Error formatting):
├── Example 9 ❌ (formatted CLI output)
└── Example 11 ❌ (multiple types grouped)
```

### Confidence Level

**HIGH CONFIDENCE** ✅

**Reasons**:
- All 7 questions resolved
- 6 clear business rules identified
- 12 concrete examples cover success, errors, warnings
- Examples cover complex scenarios (errors + warnings, large decks, multiple types)
- Clear separation between blocking errors and non-blocking warnings
- Error formatting examples show CLI output

**Risks Identified**:
- Cats library usage (NonEmptyList, Validated) - team must be familiar
- Performance for large decks (200 slides × multiple validators)
- Error message consistency across validation types

**Ready for TDD**: YES ✅

---

## 📋 Acceptance Criteria Summary

### Must Have (Critical)

1. ✅ All validation errors collected (not fail-fast)
2. ✅ Errors grouped by slide ID
3. ✅ Each error includes slide number, slot name, message
4. ✅ Structure errors block Content validation
5. ✅ Density warnings are non-blocking
6. ✅ Errors formatted for readability
7. ✅ Errors sorted by slide number
8. ✅ Multiple errors on same slide shown

### Should Have (Important)

9. ✅ Return type: `Either[NonEmptyList[ValidationError], SlideDeck]`
10. ✅ Warnings returned separately from errors
11. ✅ Error formatter produces CLI-friendly output
12. ✅ Validation type tags ([Structure], [Density], [Content])

### Nice to Have (Enhancement)

13. ✅ Error collection < 5ms overhead vs fail-fast
14. ✅ Error formatting < 10ms for 100 errors

---

## 🎨 Visual Example Map

```
┌────────────────────────────────────────────────────────────┐
│                      📘 USER STORY                         │
│        Collect All Validation Errors (US-015)              │
└────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R1 │          │ 🔵 R2 │          │ 🔵 R3 │
    │Collect│          │ Group │          │Context│
    │  All  │          │  By   │          │  Info │
    └───┬───┘          │ Slide │          └───┬───┘
        │              └───┬───┘              │
    ┌───┴──┬─────┬──┐  ┌──┴──┬──────┐   ┌───┴──┬──────┐
    │✅ E1 │❌ E2│❌E3 │❌ E3│❌ E4 │   │❌ E2│❌ E3 │
    │✅ E12│❌ E4│❌E5 │❌ E8│❌ E9 │   │❌ E4│❌ E9 │
    │      │❌ E7│❌E8 │     │❌E10│   └──────┴──────┘
    └──────┴─────┴────┘  └─────┴──────┘

    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R4 │          │ 🔵 R5 │          │ 🔵 R6 │
    │Struct │          │Density│          │Format │
    │Blocks │          │Non-Blk│          │Readabl│
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴──┬──────┐   ┌───┴──┬──────┐   ┌───┴──┬──────┐
    │❌ E5 │❌E11 │   │⚠️ E6 │❌ E7 │   │❌ E9 │❌E11 │
    └──────┴──────┘   │      │❌E11 │   └──────┴──────┘
                      └──────┴──────┘

Questions (All ✅):
Q1-Q7: All resolved (see Red Cards section)

Error Types:
- StructureError (blocking)
- ContentError (blocking)
- DensityWarning (non-blocking)
```

---

## 🔄 Next Steps

1. ✅ **Three Amigos Complete**: All rules and examples documented
2. ✅ **Example Mapping Complete**: 12 examples with full coverage
3. 🔄 **Update BACKLOG-V3.md**: Mark US-015 as Ready for Implementation
4. ⏭️ **Proceed to US-008**: Apply Theme to SlideDeck (theme system)

---

**Session Type**: Ceremony 2.2 - Example Mapping Workshop
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Story Status**: Ready for TDD ✅
