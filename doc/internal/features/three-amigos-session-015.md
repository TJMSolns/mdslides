# Three Amigos Session #015
## Collect All Validation Errors (No Fail-Fast)

---

```yaml
# MACHINE-READABLE METADATA
session:
  id: 3A-015-COLLECT-ERRORS
  date: 2024-12-20
  user_story_id: US-015
  participants:
    - Tony Moores (Business/Dev/QA)
  duration_minutes: 45-60
  status: complete

story:
  title: Collect All Validation Errors (No Fail-Fast)
  type: Technical Infrastructure
  priority: P0 (Blocker)
  epic: Validation Framework
```

---

## 📋 User Story

**As a** slide deck author
**I want to** see all validation errors at once
**So that** I can fix all issues in one pass (not play "whack-a-mole")

**Business Value**: Better UX - don't make users fix one error only to discover another

**Technical Scope**: Return `Either[NonEmptyList[ValidationError], SlideDeck]` from all validation functions, accumulating errors across all slides

---

## 🎭 Three Perspectives

### 👔 Business Perspective (Product Owner)

**What success looks like**:
- Author sees ALL validation errors at once
- Errors grouped by slide number
- Each error shows:
  - Which slide (number + template)
  - Which validation type (Structure, Density, Content)
  - What constraint was violated
  - Actual vs expected values
- Author can fix all issues in one iteration

**Current Pain Point** (fail-fast approach):
```
❌ BAD (Fail-Fast):
Run 1: "Slide 1 missing required slot 'body'"
  → Fix, re-run
Run 2: "Slide 3 heading exceeds 80 chars"
  → Fix, re-run
Run 3: "Slide 5 references invalid template"
  → Fix, re-run
Total: 3 iterations
```

**Desired Behavior** (collect all errors):
```
✅ GOOD (Collect All):
Run 1:
  - Slide 1 missing required slot 'body'
  - Slide 3 heading exceeds 80 chars
  - Slide 5 references invalid template
  → Fix all 3, re-run
Run 2: Success!
Total: 1 iteration
```

**Acceptance criteria**:
- ✅ All validation errors collected (not fail-fast)
- ✅ Errors grouped by slide ID
- ✅ Each error has slide number, slot name, error message
- ✅ Errors formatted for readability
- ✅ Structure, Density, and Content errors collected separately
- ✅ Multiple errors on same slide → all shown

---

### 💻 Development Perspective (Technical)

**Implementation approach**:
1. **Error ADT** (Algebraic Data Type):
   ```scala
   sealed trait ValidationError {
     def slideId: SlideId
     def message: String
   }

   case class StructureError(
     slideId: SlideId,
     slotName: Option[String],
     violation: String
   ) extends ValidationError

   case class DensityWarning(
     slideId: SlideId,
     slotName: Option[String],
     metric: String,
     actual: Int,
     max: Int
   ) extends ValidationError

   case class ContentError(
     slideId: SlideId,
     slotName: String,
     constraint: String,
     actual: String,
     expected: String
   ) extends ValidationError
   ```

2. **Validation Return Type**:
   - Structure: `Either[NonEmptyList[StructureError], SlideDeck]`
   - Density: `Either[NonEmptyList[DensityWarning], SlideDeck]`
   - Content: `Either[NonEmptyList[ContentError], SlideDeck]`
   - **Combined**: Use Cats `Validated` or `ValidatedNel` for parallel accumulation

3. **Error Collection Pattern** (traverse slides):
   ```scala
   def validateSlideDeck(deck: SlideDeck): Either[NonEmptyList[ValidationError], SlideDeck] = {
     deck.slides.traverse { slide =>
       for {
         _ <- validateStructure(slide)
         _ <- validateDensity(slide)
         _ <- validateContent(slide)
       } yield slide
     }.map(_ => deck)
   }
   ```

4. **Error Formatting**:
   ```
   Validation failed with 3 errors:

   Slide 1 (template: title):
     [Structure] Title slot is required but missing

   Slide 3 (template: content):
     [Density] Body exceeds max 12 lines (found 15 lines)
     [Content] Heading exceeds max 80 chars (found 95 chars)
   ```

**Technical risks**:
- **Parallel vs Sequential**: Structure must pass before Content validation (dependency)
  - Structure checks slot presence
  - Content checks slot constraints
  - **Solution**: Run Structure first, then Density + Content in parallel
- **Error Type Hierarchy**: How to combine StructureError, DensityWarning, ContentError?
  - **Option 1**: Common `ValidationError` trait (chosen)
  - **Option 2**: Separate collections for each type
- **Performance**: Traversing 200 slides with multiple validators
  - **Mitigation**: Short-circuit if Structure validation fails (Content depends on Structure)

**Dependencies**:
- Cats library (NonEmptyList, Validated, traverse)
- Structure Validation (US-011)
- Density Validation (US-012)
- Content Validation (US-013)

---

### 🧪 Testing Perspective (Quality/Edge Cases)

**Happy path scenarios**:
1. Valid deck with 1 slide → SUCCESS (no errors)
2. Valid deck with 50 slides → SUCCESS (no errors)

**Edge cases (errors)**:
1. Single slide with 1 error → 1 error collected
2. Single slide with 3 errors (structure + density + content) → 3 errors collected
3. Multiple slides with errors → all errors collected, grouped by slide
4. Deck with 50 slides, 20 have errors → all 20 errors collected
5. Structure error prevents Content validation → only Structure error shown

**Boundary cases**:
1. Deck with exactly 200 slides, all valid → SUCCESS
2. Deck with exactly 200 slides, 100 have errors → all 100 errors collected

**Error Formatting Tests**:
1. Error message includes slide number
2. Error message includes template name
3. Error message includes slot name (if applicable)
4. Error message includes actual vs expected values
5. Errors sorted by slide number (ascending)

**Non-functional requirements**:
- Error collection < 5ms overhead (compared to fail-fast)
- Error formatting < 10ms for 100 errors
- Memory usage: O(n) for n errors (reasonable for max 200 slides)

---

## 🗂️ Example Mapping

### Rule 1: All validation errors must be collected (not fail-fast)

**Examples**:
- ✅ **Valid**: Deck with no errors → SUCCESS, empty error list
- ❌ **Invalid**: Deck with 3 slides, each has 1 error → 3 errors collected
  → Validation fails with NonEmptyList[ValidationError] containing 3 errors
- ❌ **Invalid**: Slide 1 has 2 errors (structure + content) → both errors collected
  → Error 1: StructureError("Slide 1...")
  → Error 2: ContentError("Slide 1...")

**Questions**:
- Q1: Should validation stop after Structure errors?
  - **Decision**: Yes. Content validation depends on Structure (slots must exist before checking constraints). If Structure fails, skip Content validation for that slide. Density validation is independent, always run.
- Q2: What about warnings vs errors?
  - **Decision**: Density warnings are non-blocking (collect but don't fail). Structure/Content errors are blocking (fail validation).

---

### Rule 2: Errors must be grouped by slide ID

**Examples**:
- ❌ **Invalid**: Slide 1 has 2 errors, Slide 3 has 1 error
  → Error formatting:
  ```
  Slide 1 (template: content):
    - Error A
    - Error B

  Slide 3 (template: title):
    - Error C
  ```

**Questions**:
- Q3: What order should errors be presented?
  - **Decision**: Sort by slide number (ascending). Within a slide, sort by validation type: Structure, Density, Content.

---

### Rule 3: Each error must include slide number, slot name, and message

**Examples**:
- ❌ **Invalid**: Slide 2 content body exceeds max words
  → ContentError:
    - `slideId`: 2
    - `slotName`: "body"
    - `constraint`: "max_words"
    - `actual`: "160 words"
    - `expected`: "150 words"
  → Formatted message: "Slide 2 slot 'body' exceeds max 150 words (found 160 words)"

**Questions**:
- Q4: What if error has no slot name (e.g., deck-level error)?
  - **Decision**: Slot name is `Option[String]`. Deck-level errors use `None` (e.g., "SlideDeck exceeds max 200 slides").

---

### Rule 4: Structure errors block Content validation (dependency)

**Examples**:
- ❌ **Invalid**: Slide 1 missing required slot 'body'
  → Structure validation fails
  → Content validation SKIPPED for Slide 1 (can't check constraints on missing slot)
  → Density validation RUNS for Slide 1 (independent)
  → Result: 1 StructureError collected

**Questions**:
- Q5: Should we skip Density validation if Structure fails?
  - **Decision**: No. Density validation is independent of Structure (checks existing content, doesn't assume specific slots).

---

### Rule 5: Density warnings are non-blocking

**Examples**:
- ⚠️ **Warning**: Slide 1 body exceeds 12 lines (found 15 lines)
  → DensityWarning collected
  → Validation continues (warning, not error)
  → Final result: `Either[NonEmptyList[DensityWarning], SlideDeck]` with warnings
- ❌ **Error**: Slide 1 body is empty (required slot)
  → ContentError collected
  → Validation FAILS (error, blocking)

**Questions**:
- Q6: How to return both errors and warnings?
  - **Decision**: Two separate return values:
    - `errors: Either[NonEmptyList[ValidationError], SlideDeck]` (blocking)
    - `warnings: List[DensityWarning]` (non-blocking)
  - If errors present, return Left(errors) + warnings
  - If no errors, return Right(deck) + warnings

---

### Rule 6: All errors formatted for readability

**Examples**:
- ❌ **Invalid**: Multiple errors across slides
  → Formatted output:
  ```
  Validation failed with 5 errors:

  Slide 1 (template: title):
    [Structure] Title slot is required but missing

  Slide 2 (template: content):
    [Density] Body exceeds max 12 lines (found 15 lines) [WARNING]

  Slide 3 (template: content):
    [Content] Heading exceeds max 80 chars (found 95 chars)
    [Content] Body exceeds max 150 words (found 160 words)

  Slide 5 (template: custom):
    [Structure] Slide references template 'custom' which does not exist
  ```

**Questions**:
- Q7: Should warnings be shown in the same output as errors?
  - **Decision**: Yes. Mark warnings with `[WARNING]` tag. Show all issues to author.

---

## 📝 Concrete Examples (Given/When/Then)

### Example 1: Valid Deck (No Errors)

```gherkin
Feature: Error Collection

  Scenario: Valid deck with multiple slides
    Given I have a markdown file with 5 valid slides
    When I parse and validate the markdown
    Then structure validation succeeds
    And density validation succeeds
    And content validation succeeds
    And no errors are collected
    And result is Right(SlideDeck)
```

---

### Example 2: Single Slide with Multiple Errors

```gherkin
  Scenario: One slide violates structure and content constraints
    Given I have a markdown file with:
      """
      ---
      template: content
      ---
      ## This is a very long heading that exceeds the eighty character constraint
      """
    When I parse and validate the markdown
    Then structure validation succeeds (heading + body present)
    And content validation fails with 2 errors:
      - Slide 1 slot 'body' is required but empty
      - Slide 1 slot 'heading' exceeds max 80 chars (found 85 chars)
    And both errors are collected in NonEmptyList[ContentError]
    And result is Left(NonEmptyList(error1, error2))
```

---

### Example 3: Multiple Slides with Errors

```gherkin
  Scenario: Multiple slides each have different errors
    Given I have a markdown file with:
      - Slide 1: missing required slot 'body'
      - Slide 2: heading exceeds 80 chars
      - Slide 3: body exceeds 150 words
    When I parse and validate the markdown
    Then validation fails with 3 errors
    And errors are grouped by slide:
      - Slide 1: StructureError (missing required slot 'body')
      - Slide 2: ContentError (heading exceeds 80 chars)
      - Slide 3: ContentError (body exceeds 150 words)
    And result is Left(NonEmptyList(error1, error2, error3))
```

---

### Example 4: Errors and Warnings Combined

```gherkin
  Scenario: Deck has blocking errors and non-blocking warnings
    Given I have a markdown file with:
      - Slide 1: body exceeds 12 lines (15 lines) [Density Warning]
      - Slide 2: body is empty [Content Error]
    When I parse and validate the markdown
    Then content validation fails with 1 error:
      - Slide 2 slot 'body' is required but empty
    And density validation produces 1 warning:
      - Slide 1 slot 'body' exceeds max 12 lines (found 15 lines)
    And result is Left(NonEmptyList(ContentError)) + warnings
```

---

### Example 5: Structure Error Blocks Content Validation

```gherkin
  Scenario: Structure error prevents content validation for that slide
    Given I have a markdown file with:
      - Slide 1: missing required slot 'body'
    When I parse and validate the markdown
    Then structure validation fails for Slide 1
    And content validation is SKIPPED for Slide 1
    And density validation RUNS for Slide 1
    And result is Left(NonEmptyList(StructureError))
```

---

### Example 6: Large Deck with Many Errors

```gherkin
  Scenario: 50-slide deck with 20 errors
    Given I have a markdown file with 50 slides
    And 20 slides have various errors:
      - 5 structure errors
      - 10 content errors
      - 5 density warnings
    When I parse and validate the markdown
    Then validation fails with 15 errors (structure + content)
    And 5 warnings are collected separately (density)
    And all errors are grouped by slide ID
    And result is Left(NonEmptyList[ValidationError]) with 15 errors
```

---

### Example 7: Error Formatting

```gherkin
  Scenario: Errors formatted for CLI output
    Given I have validation errors:
      - Slide 1: StructureError (missing slot 'body')
      - Slide 3: ContentError (heading too long)
      - Slide 3: ContentError (body too many words)
    When I format the errors for display
    Then output is:
      """
      Validation failed with 3 errors:

      Slide 1 (template: content):
        [Structure] Slot 'body' is required but missing

      Slide 3 (template: content):
        [Content] Heading exceeds max 80 chars (found 95 chars)
        [Content] Body exceeds max 150 words (found 160 words)
      """
```

---

## 🚧 Open Questions

| ID | Question | Status | Decision |
|----|----------|--------|----------|
| Q1 | Stop after Structure errors? | ✅ Resolved | Yes, skip Content validation if Structure fails (dependency) |
| Q2 | Warnings vs errors? | ✅ Resolved | Density = warnings (non-blocking), Structure/Content = errors (blocking) |
| Q3 | Error ordering? | ✅ Resolved | Sort by slide number, then by validation type |
| Q4 | Error without slot name? | ✅ Resolved | Slot name is `Option[String]` |
| Q5 | Skip Density if Structure fails? | ✅ Resolved | No, Density is independent |
| Q6 | Return errors and warnings? | ✅ Resolved | Two separate return values |
| Q7 | Show warnings with errors? | ✅ Resolved | Yes, mark warnings with `[WARNING]` tag |

---

## ✅ Acceptance Criteria (Definition of Done)

### Functional Criteria

1. ✅ **AC1**: All validation errors collected (not fail-fast)
2. ✅ **AC2**: Errors grouped by slide ID
3. ✅ **AC3**: Each error includes slide number, slot name, message
4. ✅ **AC4**: Structure errors block Content validation for that slide
5. ✅ **AC5**: Density warnings are non-blocking
6. ✅ **AC6**: Errors formatted for readability
7. ✅ **AC7**: Errors sorted by slide number (ascending)
8. ✅ **AC8**: Multiple errors on same slide → all shown

### Technical Criteria

9. ✅ **AC9**: Return type is `Either[NonEmptyList[ValidationError], SlideDeck]`
10. ✅ **AC10**: `ValidationError` ADT with StructureError, DensityWarning, ContentError
11. ✅ **AC11**: Errors collected using Cats `NonEmptyList`
12. ✅ **AC12**: Structure validation runs first (blocking)
13. ✅ **AC13**: Density + Content validation run after Structure (if Structure passes)
14. ✅ **AC14**: Warnings returned separately from errors
15. ✅ **AC15**: Error formatter produces CLI-friendly output
16. ✅ **AC16**: All domain terms from ubiquitous language used

### Non-Functional Criteria

17. ✅ **AC17**: Error collection < 5ms overhead vs fail-fast
18. ✅ **AC18**: Error formatting < 10ms for 100 errors
19. ✅ **AC19**: Memory usage O(n) for n errors
20. ✅ **AC20**: Pure functional code (no side effects)

**Scenarios**: 7 concrete examples documented
- 1 success path
- 6 error/warning paths

**Dependencies**:
- Cats library (NonEmptyList, Validated)
- Structure Validation (US-011)
- Density Validation (US-012)
- Content Validation (US-013)

---

## 📚 Related Artifacts

- **User Story Tracker**: [BACKLOG-V3.md](../../BACKLOG-V3.md)
- **Domain Model**: [slide-deck-aggregate.md](../domain-models/aggregates/slide-deck-aggregate.md)
- **Ubiquitous Language**: [ubiquitous-language.md](../domain-models/ubiquitous-language.md)
- **Related Stories**: US-011 Structure Validation, US-012 Density Validation, US-013 Content Validation

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
