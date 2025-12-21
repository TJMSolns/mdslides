# Example Mapping: Create Content Slide from Markdown
## User Story #002

---

```yaml
# MACHINE-READABLE METADATA
example_mapping:
  id: EM-002-CONTENT-SLIDE
  user_story_id: US-002
  date: 2024-12-19
  participants:
    - Tony Moores (Business/Dev/QA)
  status: complete

legend:
  - color: yellow
    represents: User Story
  - color: blue
    represents: Business Rules
  - color: green
    represents: Examples (Scenarios)
  - color: red
    represents: Questions
```

---

## 📒 The Story (Yellow Card)

```
┌─────────────────────────────────────────────────────────┐
│ 📝 USER STORY #002                                      │
│                                                         │
│ AS A slide deck author                                 │
│ I WANT TO create a content slide with heading and body │
│ SO THAT I can present structured information           │
│                                                         │
│ Priority: P0 (Blocker) | Epic: Core Slide Creation     │
│ Business Value: Most common slide type (80% of slides) │
└─────────────────────────────────────────────────────────┘
```

---

## 📐 Business Rules (Blue Cards)

### Rule 1: Heading Slot Required (H2 Level)

```
┌─────────────────────────────────────────────┐
│ 📘 RULE 1: Heading slot required (H2)     │
│                                             │
│ Every content slide MUST have a heading    │
│ using H2 (##) markdown syntax               │
│                                             │
│ Template: templates/content.yaml           │
│ Slot: heading (required: true)             │
│ Heading Level: 2 (H2)                      │
└─────────────────────────────────────────────┘
```

**Examples**: 3 scenarios
**Questions**: 1 resolved

---

### Rule 2: Heading Max 1 Line, Max 80 Chars

```
┌─────────────────────────────────────────────┐
│ 📘 RULE 2: Heading constraints             │
│                                             │
│ Heading cannot exceed:                      │
│ - 1 line (inherent to markdown H2)         │
│ - 80 characters                             │
│                                             │
│ Constraint: max_lines: 1, max_chars: 80    │
└─────────────────────────────────────────────┘
```

**Examples**: 1 scenario
**Questions**: 1 resolved

---

### Rule 3: Body Required, Max 12 Lines, Max 150 Words

```
┌─────────────────────────────────────────────┐
│ 📘 RULE 3: Body constraints                │
│                                             │
│ Body content MUST exist and cannot exceed: │
│ - 12 lines (theme-configurable)            │
│ - 150 words                                 │
│                                             │
│ Slot: body (required: true)                │
│ Constraints: max_lines: 12, max_words: 150 │
└─────────────────────────────────────────────┘
```

**Examples**: 3 scenarios
**Questions**: 3 resolved

---

### Rule 4: Body Allows Markdown Formatting

```
┌─────────────────────────────────────────────┐
│ 📘 RULE 4: Markdown formatting allowed     │
│                                             │
│ Body can contain:                           │
│ ✅ Lists (bullet, numbered, max 2 levels)  │
│ ✅ Bold, italics, links, inline code       │
│ ✅ Blockquotes, paragraphs                 │
│                                             │
│ Allowed elements defined in template       │
└─────────────────────────────────────────────┘
```

**Examples**: 2 scenarios
**Questions**: 3 resolved

---

### Rule 5: Body Disallows Code Blocks and Images

```
┌─────────────────────────────────────────────┐
│ 📘 RULE 5: Disallowed elements             │
│                                             │
│ Body CANNOT contain:                        │
│ ❌ Code blocks (use code template)         │
│ ❌ Images (use image template)             │
│ ❌ Additional headings (use separator ---)  │
│                                             │
│ Disallowed elements trigger validation error│
└─────────────────────────────────────────────┘
```

**Examples**: 3 scenarios
**Questions**: 0

---

### Rule 6: One H2 Heading Per Slide

```
┌─────────────────────────────────────────────┐
│ 📘 RULE 6: Single heading per slide        │
│                                             │
│ Content template allows exactly ONE H2      │
│ heading. Multiple headings = multiple slides│
│                                             │
│ If multiple H2s found: validation error     │
│ Solution: Use slide separator (---)         │
└─────────────────────────────────────────────┘
```

**Examples**: 1 scenario
**Questions**: 1 resolved

---

## ✅ Examples (Green Cards)

### Example 1: Minimal Valid Content Slide

```
┌─────────────────────────────────────────────┐
│ ✅ EXAMPLE 1: Minimal content slide        │
│                                             │
│ GIVEN markdown with:                        │
│   ---                                       │
│   template: content                         │
│   ---                                       │
│   ## Key Takeaways                          │
│                                             │
│   Domain-Driven Design helps us build       │
│   better software.                          │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - 1 Slide created                         │
│   - template: "content"                     │
│   - heading: "Key Takeaways"                │
│   - body: "Domain-Driven Design..."         │
│   - validation: SUCCESS                     │
│                                             │
│ Rules tested: 1, 3                          │
└─────────────────────────────────────────────┘
```

---

### Example 2: Content Slide with Bullet List

```
┌─────────────────────────────────────────────┐
│ ✅ EXAMPLE 2: Slide with bullet list      │
│                                             │
│ GIVEN markdown with:                        │
│   ## Key Principles                         │
│                                             │
│   - Ubiquitous Language                     │
│   - Bounded Contexts                        │
│   - Aggregates and Entities                 │
│   - Strategic Design                        │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - heading: "Key Principles"               │
│   - body: 4-item bullet list                │
│   - body lines: 4                           │
│   - validation: SUCCESS                     │
│                                             │
│ Rules tested: 1, 3, 4                       │
└─────────────────────────────────────────────┘
```

---

### Example 3: Content Slide with Rich Formatting

```
┌─────────────────────────────────────────────┐
│ ✅ EXAMPLE 3: Rich markdown formatting    │
│                                             │
│ GIVEN markdown with:                        │
│   ## Why Functional Programming?            │
│                                             │
│   FP offers **immutability** by default,    │
│   making code easier to reason about.       │
│   Pure functions have *no side effects*.    │
│                                             │
│   Learn more at [FP Resources](url).        │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - heading: "Why Functional Programming?"  │
│   - body: preserves bold, italics, links    │
│   - body lines: 5                           │
│   - body words: ~35                         │
│   - validation: SUCCESS                     │
│                                             │
│ Rules tested: 1, 3, 4                       │
└─────────────────────────────────────────────┘
```

---

### Example 4: Body Exceeds Max Lines ❌

```
┌─────────────────────────────────────────────┐
│ ❌ EXAMPLE 4: Body too many lines         │
│                                             │
│ GIVEN markdown with:                        │
│   ## Long Content                           │
│                                             │
│   Line 1                                    │
│   Line 2                                    │
│   ...                                       │
│   Line 13                                   │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - validation: FAILURE                     │
│   - error: DensityError                     │
│   - message: "Body exceeds max 12 lines     │
│              (found 13 lines)"              │
│                                             │
│ Rules tested: 3                             │
└─────────────────────────────────────────────┘
```

---

### Example 5: Body Exceeds Max Words ❌

```
┌─────────────────────────────────────────────┐
│ ❌ EXAMPLE 5: Body too many words         │
│                                             │
│ GIVEN markdown with:                        │
│   ## Overly Verbose Content                 │
│                                             │
│   [200 words of text that exceeds the      │
│    150 word limit...]                       │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - validation: FAILURE                     │
│   - error: DensityError                     │
│   - message: "Body exceeds max 150 words    │
│              (found 200 words)"             │
│                                             │
│ Rules tested: 3                             │
└─────────────────────────────────────────────┘
```

---

### Example 6: Missing Required Heading ❌

```
┌─────────────────────────────────────────────┐
│ ❌ EXAMPLE 6: No heading                  │
│                                             │
│ GIVEN markdown with:                        │
│   ---                                       │
│   template: content                         │
│   ---                                       │
│   This is just body text with no heading.   │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - validation: FAILURE                     │
│   - error: StructureError                   │
│   - message: "Heading slot is required"     │
│                                             │
│ Rules tested: 1                             │
└─────────────────────────────────────────────┘
```

---

### Example 7: Missing Required Body ❌

```
┌─────────────────────────────────────────────┐
│ ❌ EXAMPLE 7: No body content             │
│                                             │
│ GIVEN markdown with:                        │
│   ## Lonely Heading                         │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - validation: FAILURE                     │
│   - error: StructureError                   │
│   - message: "Body slot is required"        │
│                                             │
│ Rules tested: 3                             │
└─────────────────────────────────────────────┘
```

---

### Example 8: Heading Exceeds Max Characters ❌

```
┌─────────────────────────────────────────────┐
│ ❌ EXAMPLE 8: Heading too long            │
│                                             │
│ GIVEN markdown with:                        │
│   ## This is an extremely long heading that │
│   exceeds the eighty character limit...     │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - validation: FAILURE                     │
│   - error: ContentError                     │
│   - message: "Heading exceeds max 80 chars" │
│                                             │
│ Rules tested: 2                             │
└─────────────────────────────────────────────┘
```

---

### Example 9: Code Block in Body ❌

```
┌─────────────────────────────────────────────┐
│ ❌ EXAMPLE 9: Code block not allowed      │
│                                             │
│ GIVEN markdown with:                        │
│   ## Code Example                           │
│                                             │
│   Here's some code:                         │
│                                             │
│   ```scala                                  │
│   def hello(): Unit = println("Hello")      │
│   ```                                       │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - validation: FAILURE                     │
│   - error: StructureError                   │
│   - message: "Code blocks not allowed in    │
│              content template. Use code     │
│              template instead."             │
│                                             │
│ Rules tested: 5                             │
└─────────────────────────────────────────────┘
```

---

### Example 10: Image in Body ❌

```
┌─────────────────────────────────────────────┐
│ ❌ EXAMPLE 10: Image not allowed          │
│                                             │
│ GIVEN markdown with:                        │
│   ## Architecture Diagram                   │
│                                             │
│   Here's our system architecture:           │
│                                             │
│   ![Architecture](./diagrams/arch.png)      │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - validation: FAILURE                     │
│   - error: StructureError                   │
│   - message: "Images not allowed in content │
│              template. Use image template   │
│              instead."                      │
│                                             │
│ Rules tested: 5                             │
└─────────────────────────────────────────────┘
```

---

### Example 11: Multiple H2 Headings ❌

```
┌─────────────────────────────────────────────┐
│ ❌ EXAMPLE 11: Multiple headings          │
│                                             │
│ GIVEN markdown with:                        │
│   ## First Topic                            │
│   Some content about the first topic.       │
│                                             │
│   ## Second Topic                           │
│   Some content about the second topic.      │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - validation: FAILURE                     │
│   - error: StructureError                   │
│   - message: "Content template allows only  │
│              one H2 heading. Found 2. Use   │
│              slide separator (---) instead."│
│                                             │
│ Rules tested: 6                             │
└─────────────────────────────────────────────┘
```

---

## ❓ Questions (Red Cards)

### Q1: Auto-demote H1 to H2?

```
┌─────────────────────────────────────────────┐
│ ❓ Q1: Auto-demote H1 to H2 if H2 missing? │
│                                             │
│ If markdown has H1 instead of H2, should we │
│ auto-demote it to H2?                       │
│                                             │
│ ✅ RESOLVED: No, fail validation           │
│ Rationale: Content slides use H2 for        │
│ semantic hierarchy (H1 reserved for title   │
│ slides). Author should fix their markdown.  │
└─────────────────────────────────────────────┘
```

---

### Q2: How to Count Lines?

```
┌─────────────────────────────────────────────┐
│ ❓ Q2: How to count lines in body?        │
│                                             │
│ Do we count visual lines or newline chars?  │
│ Do blank lines count?                       │
│                                             │
│ ✅ RESOLVED: Count newline characters \n   │
│ Blank lines count as lines.                 │
│ Example: "Line 1\n\nLine 3" = 3 lines      │
└─────────────────────────────────────────────┘
```

---

### Q3: How to Count Words?

```
┌─────────────────────────────────────────────┐
│ ❓ Q3: How to count words in body?        │
│                                             │
│ Do markdown syntax tokens count as words?   │
│ E.g., does **bold** count as 1 or 3?        │
│                                             │
│ ✅ RESOLVED: Split on whitespace, ignore   │
│ markdown syntax. "**bold**" = 1 word.       │
│ Strip markdown tokens before counting.      │
└─────────────────────────────────────────────┘
```

---

### Q4: Code Blocks as Lines?

```
┌─────────────────────────────────────────────┐
│ ❓ Q4: Do code blocks count as lines?     │
│                                             │
│ If code block present, count lines or error?│
│                                             │
│ ✅ RESOLVED: Code blocks NOT allowed in    │
│ content template (validation error).        │
│ Use code template (US-007) instead.         │
└─────────────────────────────────────────────┘
```

---

### Q5: Nested Lists Depth?

```
┌─────────────────────────────────────────────┐
│ ❓ Q5: Allow nested lists? Max depth?     │
│                                             │
│ Should we allow nested bullet/numbered      │
│ lists? If yes, max depth?                   │
│                                             │
│ ✅ RESOLVED: Yes, max 2 levels deep.       │
│ Warn (not error) if exceeded.               │
│ Example:                                    │
│ - Level 1                                   │
│   - Level 2 ✅                              │
│     - Level 3 ⚠️ Warning                   │
└─────────────────────────────────────────────┘
```

---

### Q6: Inline Code Allowed?

```
┌─────────────────────────────────────────────┐
│ ❓ Q6: Allow inline code in body?         │
│                                             │
│ Should backticks `code` be allowed?         │
│                                             │
│ ✅ RESOLVED: Yes, inline code allowed.     │
│ Only code BLOCKS (```) are disallowed.      │
│ Inline: use the `grep` command ✅           │
│ Block: ```scala ... ``` ❌                  │
└─────────────────────────────────────────────┘
```

---

### Q7: Blockquotes Allowed?

```
┌─────────────────────────────────────────────┐
│ ❓ Q7: Allow blockquotes in body?         │
│                                             │
│ Should > blockquote syntax be allowed?      │
│                                             │
│ ✅ RESOLVED: Yes, blockquotes allowed.     │
│ Count lines normally (each line in          │
│ blockquote counts as 1 line).               │
└─────────────────────────────────────────────┘
```

---

### Q8: Multiple H2 Headings?

```
┌─────────────────────────────────────────────┐
│ ❓ Q8: What if multiple H2 headings?      │
│                                             │
│ Should we use first H2 as heading and rest  │
│ as body? Or fail validation?                │
│                                             │
│ ✅ RESOLVED: Fail validation (error).      │
│ Content template = 1 slide with 1 heading.  │
│ Multiple headings require multiple slides.  │
│ Solution: Use slide separator (---).        │
└─────────────────────────────────────────────┘
```

---

### Q9: Heading Line Count?

```
┌─────────────────────────────────────────────┐
│ ❓ Q9: How to enforce "1 line" for heading?│
│                                             │
│ Do we need to validate this, or is it       │
│ inherent to markdown H2 syntax?             │
│                                             │
│ ✅ RESOLVED: Inherent to markdown.         │
│ Markdown headings are single-line by        │
│ definition (newline ends heading).          │
│ No explicit validation needed.              │
└─────────────────────────────────────────────┘
```

---

## 📊 Example Mapping Summary

```
Story Coverage:
├── Rules: 6 blue cards
├── Examples: 11 green cards (3 success, 8 failure)
├── Questions: 9 red cards (all resolved ✅)
└── Confidence: HIGH

Rule → Example Coverage:
├── Rule 1 (Heading required): Examples 1, 2, 3, 6
├── Rule 2 (Heading constraints): Example 8
├── Rule 3 (Body constraints): Examples 1, 2, 3, 4, 5, 7
├── Rule 4 (Markdown allowed): Examples 2, 3
├── Rule 5 (Disallowed elements): Examples 9, 10
└── Rule 6 (Single heading): Example 11

Validation Coverage:
├── StructureError: Examples 6, 7, 9, 10, 11
├── ContentError: Example 8
├── DensityError: Examples 4, 5
└── Success: Examples 1, 2, 3
```

---

## 🎯 Readiness Assessment

### Story is READY ✅

**Criteria Met**:
- ✅ Business rules clearly defined (6 rules)
- ✅ All examples have Given/When/Then (11 scenarios)
- ✅ Edge cases identified (8 failure scenarios)
- ✅ All questions resolved (9 questions)
- ✅ Acceptance criteria documented
- ✅ Testable scenarios written

**Complexity**: Medium
**Estimated Effort**: 2 days (TDD implementation)

**Dependencies**:
- Template Library loading mechanism (US-008)
- Flexmark Markdown parser integration (Anticorruption Layer)
- YAML front matter parser
- Validation error accumulation (NonEmptyList[ValidationError])
- Template: `templates/content.yaml` ✅ Created

---

## 📚 Related Artifacts

- **Three Amigos Session**: [three-amigos-session-002.md](three-amigos-session-002.md)
- **Domain Model**: [slide-deck-aggregate.md](../domain-models/aggregates/slide-deck-aggregate.md)
- **Template**: [templates/content.yaml](../../templates/content.yaml) ✅ Created
- **Theme**: [themes/default.json](../../themes/default.json)
- **Next Ceremony**: TDD Implementation (Phase 3)

---

**Ceremony Type**: Ceremony 2.2 - Example Mapping Workshop
**Date**: 2024-12-19
**Facilitator**: Tony Moores (TJM Solutions)
**Status**: Complete - Ready for Implementation
