# Event Storming: US-004 (Speaker Notes Parsing)

**Date:** 2024-12-26
**User Story:** US-004 - Speaker Notes Parsing
**Participants:** Tony Moores (Product Owner + Developer)
**Related Governance:** Original v1.0 backlog item

---

## Domain Events (Orange Stickies)

Events that happen in the system, past tense:

1. **Frontmatter Parsed** (YAML metadata extracted from slide)
2. **Speaker Notes Field Found** (`notes:` present in frontmatter)
3. **Speaker Notes Validated** (check density, format)
4. **Notes Stored in Slide** (notes added to Slide aggregate)
5. **Notes Density Checked** (max words, max lines)
6. **Excessive Notes Detected** (too long for speaking)

### Error Events

7. **Invalid Notes Format** (notes field is not string or array)
8. **Notes Too Long** (exceeds density limits)

**Note:** Speaker notes are **NOT rendered** in v1.0 (rendering is US-034, v1.1)

---

## Commands (Blue Stickies)

User intentions that trigger events:

1. **Add Simple Notes** (frontmatter: `notes: "Remember to mention..."`)
2. **Add Multi-line Notes** (frontmatter: `notes: ["Point 1", "Point 2"]`)
3. **Validate Slide Deck** (includes notes density validation)

---

## Aggregates (Yellow Stickies)

Domain entities that handle commands and emit events:

### 1. **Slide** (existing aggregate - ENHANCE)
- **Current State:**
  - `template: String`
  - `slots: Map[String, FormattedContent]`
  - `backgroundImage: Option[String]`
- **NEW State:**
  - `notes: Option[String]` ← ADD THIS
- **Invariants:**
  - Notes are optional (most slides won't have them)
  - Notes are plain text (no formatting in v1.0)
  - Notes are for author only (not rendered in HTML in v1.0)

### 2. **FrontmatterAdapter** (existing infrastructure - ENHANCE)
- **Current Responsibility:** Parse YAML frontmatter (template, background)
- **NEW Responsibility:** Parse `notes` field
- **Methods to ADD:**
  - `parseNotes(yaml: String): Option[String]`
    - Single string: `notes: "Text"`
    - Array: `notes: ["Line 1", "Line 2"]` → join with newlines
    - Invalid: non-string/non-array → None (ignore gracefully)

### 3. **NotesValidator** (NEW domain service)
- **Responsibility:** Validate speaker notes density
- **State:** Validation limits (configurable)
  - `maxWords: Int` (default: 150 words per slide)
  - `maxLines: Int` (default: 10 lines per slide)
- **Methods:**
  - `validate(notes: String): List[ValidationError]`
    - Check word count ≤ maxWords
    - Check line count ≤ maxLines
    - Return warnings (not hard errors)

**Why separate validator?**
- Notes validation is independent of slide content validation
- Different limits (notes can be longer than slide body)
- Future: may add notes-specific rules (e.g., no markdown in notes)

---

## Read Models / Queries (Green Stickies)

Information retrieval without side effects:

1. **Get Slide Notes** (retrieve notes for presenter)
2. **Count Notes Characters/Words** (for density validation)
3. **List Slides with Notes** (query: which slides have speaker notes?)

---

## Policies (Lilac Stickies)

Business rules triggered by events:

1. **When Frontmatter Parsed:**
   - IF `notes` field present AND valid → Store in Slide
   - IF `notes` field invalid (not string/array) → Ignore, log warning
   - IF `notes` field absent → Set to None

2. **When Speaker Notes Validated:**
   - IF notes > 150 words → Warning: "Speaker notes too long (X words). Aim for <150 words."
   - IF notes > 10 lines → Warning: "Speaker notes too long (X lines). Aim for <10 lines."
   - **Important:** Warnings only, not validation errors (don't block rendering)

3. **Notes Normalization:**
   - Simple form: `notes: "Text"` → stored as "Text"
   - Array form: `notes: ["A", "B", "C"]` → stored as "A\nB\nC" (newline-separated)
   - Preserves bullet structure for multi-point notes

---

## External Systems (Pink Stickies)

None (notes are internal to domain model)

---

## Hotspots / Pain Points (Red Stickies)

1. **⚠️ Notes Format:**
   - Should notes support markdown formatting?
   - **Decision:** v1.0 = plain text only. Formatting deferred to v1.1 with rendering.

2. **⚠️ Validation Severity:**
   - Should excessive notes block rendering?
   - **Decision:** No, warnings only. Let authors decide what's appropriate.

3. **⚠️ Array vs String:**
   - How to represent multi-line notes internally?
   - **Decision:** Always store as single string, use `\n` for line breaks.
     - Simple form: `notes: "Single line"` → `"Single line"`
     - Array form: `notes: ["A", "B"]` → `"A\nB"`

4. **⚠️ Notes in Which Slots:**
   - Should notes be per-slot or per-slide?
   - **Decision:** Per-slide only (frontmatter field, not slot content).
   - Rationale: Notes are meta-information about the slide, not slide content.

---

## Timeline / Flow

### Happy Path (Simple Notes)

```
Slide markdown:
---
template: content
notes: "Remember to pause after the key point. Mention the 2023 study."
---

## Slide Title

Content here...

    ↓
[Frontmatter Parsed]
    ↓
[Speaker Notes Field Found] ("Remember to pause...")
    ↓
[Speaker Notes Validated] (35 words, 2 lines → OK)
    ↓
[Notes Stored in Slide] (notes: Some("Remember to pause..."))
    ↓
[Slide Validated] (content + notes)
    ↓
[Slide Created Successfully]
```

### Happy Path (Multi-line Notes)

```
Slide markdown:
---
template: content
notes:
  - "Key point: emphasize security benefits"
  - "Mention customer testimonial from Acme Corp"
  - "Transition: Next slide covers implementation"
---

    ↓
[Frontmatter Parsed]
    ↓
[Speaker Notes Field Found] (array with 3 elements)
    ↓
[Notes Normalized] → "Key point...\nMention...\nTransition..."
    ↓
[Speaker Notes Validated] (82 words, 3 lines → OK)
    ↓
[Notes Stored in Slide]
    ↓
[Slide Created Successfully]
```

### Warning Path (Excessive Notes)

```
Slide markdown:
---
template: content
notes: "Very long notes... (200 words of text)"
---

    ↓
[Frontmatter Parsed]
    ↓
[Speaker Notes Field Found]
    ↓
[Speaker Notes Validated] (200 words, 15 lines)
    ↓
[Excessive Notes Detected] ⚠️
    ↓
Validation Output:
⚠ Warning: Slide 5 - Speaker notes too long (200 words). Aim for <150 words for easy reference.
⚠ Warning: Slide 5 - Speaker notes too long (15 lines). Aim for <10 lines to fit on screen.

    ↓
[Notes Stored in Slide] (warnings don't block storage)
    ↓
[Slide Created Successfully] (with warnings)
```

### Error Path (Invalid Format)

```
Slide markdown:
---
template: content
notes: 12345  # Invalid: number not string
---

    ↓
[Frontmatter Parsed]
    ↓
[Invalid Notes Format Detected]
    ↓
Warning Output:
⚠ Warning: Slide 3 - Invalid notes format (expected string or array, got number). Notes ignored.

    ↓
[Notes Stored in Slide] (notes: None)
    ↓
[Slide Created Successfully] (without notes)
```

---

## Key Domain Insights

1. **Notes are Metadata:**
   - Not rendered in HTML (v1.0)
   - Stored in Slide aggregate for future use (US-034 speaker view, v1.1)
   - Optional field (most slides won't have notes)

2. **Validation is Soft:**
   - Warnings, not errors
   - Let authors exceed limits if they want
   - Goal: guide users, don't block them

3. **Two Input Formats, One Storage:**
   - Accept: string OR array
   - Store: always as string (with `\n` separators)
   - Simplifies domain model

4. **Plain Text Only (v1.0):**
   - No markdown formatting in notes
   - Keeps parser simple
   - Formatting can be added in v1.1 when rendering is implemented

---

## Examples for Testing

### Example 1: Minimal Notes

```markdown
---
template: content
notes: "Quick reminder: mention the security aspect."
---

## Secure Architecture

Our system uses industry-standard encryption.
```

**Expected:**
- Notes: Some("Quick reminder: mention the security aspect.")
- Validation: ✓ Pass (9 words, 1 line)

### Example 2: Multi-line Notes

```markdown
---
template: content
notes:
  - "Start with the problem statement"
  - "Transition to our solution"
  - "End with call to action"
---

## The Solution

We solve this by...
```

**Expected:**
- Notes: Some("Start with the problem statement\nTransition to our solution\nEnd with call to action")
- Validation: ✓ Pass (15 words, 3 lines)

### Example 3: Excessive Notes (Warning)

```markdown
---
template: content
notes: "This is a very long note that goes on and on for many sentences. It contains detailed information about the slide, background context, historical data, multiple anecdotes, several key points to remember, potential questions from the audience, and detailed answers to those questions. This is way too much to read while presenting."
---
```

**Expected:**
- Notes: Some("This is a very long note...")
- Validation: ⚠ Warning (57 words - over 150 limit, 4 lines)

### Example 4: No Notes

```markdown
---
template: content
---

## Slide Without Notes

Content...
```

**Expected:**
- Notes: None
- Validation: ✓ Pass (no notes to validate)

### Example 5: Invalid Format

```markdown
---
template: content
notes:
  title: "Not a simple array"
  content: "This is an object"
---
```

**Expected:**
- Notes: None
- Warning: "Invalid notes format (expected string or array, got object). Notes ignored."

---

## Open Questions

1. **Q:** Should we support YAML multi-line syntax (`|` or `>`)?
   ```yaml
   notes: |
     Line 1
     Line 2
   ```
   - **A:** Yes, YAML parser handles this automatically. Will be parsed as single string with `\n`.

2. **Q:** Max word limit - 150 words reasonable?
   - **A:** Yes. Research shows 150-200 words ≈ 1 minute of speaking at conversational pace.

3. **Q:** Should we count notes in overall slide density validation?
   - **A:** No. Notes don't appear on slide, so they don't affect slide visual density.

4. **Q:** Should there be a global "notes enabled/disabled" flag?
   - **A:** Not in v1.0. Feature is opt-in per slide anyway (notes field optional).

---

## Implementation Notes

### Domain Changes

**File:** `domain/src/.../domain/Slide.scala`
```scala
case class Slide(
  template: String,
  slots: Map[String, FormattedContent],
  backgroundImage: Option[String] = None,
  notes: Option[String] = None  // NEW FIELD
)
```

### Infrastructure Changes

**File:** `infrastructure/src/.../parser/FrontmatterAdapter.scala`
```scala
// Parse notes field from YAML
private def parseNotes(yamlNode: Node): Option[String] =
  Option(yamlNode.get("notes")).flatMap {
    case scalar: ScalarNode =>
      // Simple string: notes: "text"
      Some(scalar.getValue)
    case seq: SequenceNode =>
      // Array: notes: ["a", "b", "c"]
      val items = seq.getValue.asScala.collect {
        case s: ScalarNode => s.getValue
      }
      if items.nonEmpty then Some(items.mkString("\n"))
      else None
    case _ =>
      // Invalid type (object, etc.) - ignore
      None
  }
```

### Validation Changes

**File:** `domain/src/.../validation/NotesValidator.scala` (NEW)
```scala
object NotesValidator:
  val MaxWords = 150
  val MaxLines = 10

  def validate(notes: String): List[ValidationError] =
    val wordCount = notes.split("\\s+").length
    val lineCount = notes.split("\n").length

    val errors = List.newBuilder[ValidationError]

    if wordCount > MaxWords then
      errors += ValidationError(
        s"Speaker notes too long ($wordCount words). Aim for <$MaxWords words."
      )

    if lineCount > MaxLines then
      errors += ValidationError(
        s"Speaker notes too long ($lineCount lines). Aim for <$MaxLines lines."
      )

    errors.result()
```

---

## Next Steps

1. ✅ Event Storming complete
2. **NEXT:** Three Amigos session (refine acceptance criteria with US-019)
3. **THEN:** Example Mapping (concrete examples for both user stories)
4. **THEN:** TDD implementation (US-019 first, then US-004)

---

**Event Storming Status:** ✅ COMPLETE
**Ready for:** Three Amigos Session (combined with US-019)
