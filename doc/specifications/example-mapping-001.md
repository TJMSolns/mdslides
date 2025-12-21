# Example Mapping: Create Title Slide from Markdown
## User Story #001

---

```yaml
# MACHINE-READABLE METADATA
example_mapping:
  id: EM-001-TITLE-SLIDE
  user_story_id: US-001
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
│ 📝 USER STORY #001                                      │
│                                                         │
│ AS A slide deck author                                 │
│ I WANT TO create a title slide from Markdown           │
│ SO THAT I can generate a properly validated slide      │
│         with title, subtitle, and author                │
│                                                         │
│ Priority: HIGH | Epic: Core Slide Deck Creation        │
└─────────────────────────────────────────────────────────┘
```

---

## 📐 Business Rules (Blue Cards)

### Rule 1: Title Slot Required
```
┌─────────────────────────────────────────────┐
│ 📘 RULE 1: Title slot is required          │
│                                             │
│ Every title slide MUST have a title        │
│ (H1 heading in Markdown)                    │
│                                             │
│ Template: templates/title.yaml             │
│ Slot: title (required: true)               │
└─────────────────────────────────────────────┘
```

**Examples**: 3 scenarios
**Questions**: 1 resolved

---

### Rule 2: Title Max 2 Lines
```
┌─────────────────────────────────────────────┐
│ 📘 RULE 2: Title max 2 lines               │
│                                             │
│ Title content cannot exceed 2 lines         │
│ (prevents title from overflowing slide)     │
│                                             │
│ Constraint: max_lines: 2                   │
└─────────────────────────────────────────────┘
```

**Examples**: 2 scenarios
**Questions**: 1 resolved

---

### Rule 3: Subtitle Optional, Max 2 Lines
```
┌─────────────────────────────────────────────┐
│ 📘 RULE 3: Subtitle optional, max 2 lines  │
│                                             │
│ Subtitle (H2) is optional                   │
│ If present, max 2 lines                     │
│                                             │
│ Slot: subtitle (required: false)           │
│ Constraint: max_lines: 2                   │
└─────────────────────────────────────────────┘
```

**Examples**: 2 scenarios
**Questions**: 1 resolved

---

### Rule 4: Author Optional, Max 80 Chars
```
┌─────────────────────────────────────────────┐
│ 📘 RULE 4: Author optional, max 80 chars   │
│                                             │
│ Author specified in front matter            │
│ Format: author: John Doe                    │
│                                             │
│ Slot: author (required: false)             │
│ Constraint: max_chars: 80                  │
└─────────────────────────────────────────────┘
```

**Examples**: 2 scenarios
**Questions**: 1 resolved (front matter vs paragraph)

---

### Rule 5: Template Binding Required
```
┌─────────────────────────────────────────────┐
│ 📘 RULE 5: Front matter specifies template │
│                                             │
│ Markdown must have front matter with       │
│ template: title                             │
│                                             │
│ Template resolution: explicit binding       │
│ (no heuristics for this story)             │
└─────────────────────────────────────────────┘
```

**Examples**: 2 scenarios
**Questions**: 1 resolved

---

### Rule 6: Content Formatting Preserved
```
┌─────────────────────────────────────────────┐
│ 📘 RULE 6: Markdown/Unicode preserved      │
│                                             │
│ Inline markdown (**bold**, [links]) OK      │
│ Unicode mathematical symbols (∑, π) OK      │
│ Emoji allowed (author's choice)             │
│                                             │
│ Theme option: content.preserveMarkdownInHeadings │
│ Theme option: content.allowEmoji           │
└─────────────────────────────────────────────┘
```

**Examples**: 1 scenario
**Questions**: 2 resolved

---

## ✅ Examples (Green Cards)

### Example 1: Minimal Valid Title Slide
```
┌─────────────────────────────────────────────┐
│ ✅ EXAMPLE 1: Minimal title slide          │
│                                             │
│ GIVEN markdown with:                        │
│   ---                                       │
│   template: title                           │
│   ---                                       │
│   # Welcome to MDSlides                     │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - 1 Slide created                         │
│   - template: "title"                       │
│   - title slot: "Welcome to MDSlides"       │
│   - subtitle slot: empty                    │
│   - author slot: empty                      │
│   - validation: SUCCESS                     │
│                                             │
│ Rules tested: 1, 5                          │
└─────────────────────────────────────────────┘
```

---

### Example 2: Full Title Slide
```
┌─────────────────────────────────────────────┐
│ ✅ EXAMPLE 2: Full slide with all slots    │
│                                             │
│ GIVEN markdown with:                        │
│   ---                                       │
│   template: title                           │
│   author: Tony Moores                       │
│   ---                                       │
│   # Welcome to MDSlides                     │
│   ## Create Beautiful Presentations         │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - title: "Welcome to MDSlides"            │
│   - subtitle: "Create Beautiful..."         │
│   - author: "Tony Moores"                   │
│   - validation: SUCCESS                     │
│                                             │
│ Rules tested: 1, 3, 4, 5                    │
└─────────────────────────────────────────────┘
```

---

### Example 3: Title Exceeds Max Lines ❌
```
┌─────────────────────────────────────────────┐
│ ❌ EXAMPLE 3: Title too many lines         │
│                                             │
│ GIVEN markdown with:                        │
│   # This is a very long title               │
│   that spans three                          │
│   separate lines                            │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - validation: FAILURE                     │
│   - error: ContentError                     │
│   - message: "Title exceeds max 2 lines"    │
│                                             │
│ Rules tested: 2                             │
└─────────────────────────────────────────────┘
```

---

### Example 4: Missing Required Title ❌
```
┌─────────────────────────────────────────────┐
│ ❌ EXAMPLE 4: No title heading             │
│                                             │
│ GIVEN markdown with:                        │
│   ---                                       │
│   template: title                           │
│   ---                                       │
│   ## Just a subtitle, no title              │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - validation: FAILURE                     │
│   - error: StructureError                   │
│   - message: "Title slot is required"       │
│                                             │
│ Rules tested: 1                             │
└─────────────────────────────────────────────┘
```

---

### Example 5: Author Too Long ❌
```
┌─────────────────────────────────────────────┐
│ ❌ EXAMPLE 5: Author exceeds char limit    │
│                                             │
│ GIVEN front matter with:                    │
│   author: Very long name... (90+ chars)     │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - validation: FAILURE                     │
│   - error: ContentError                     │
│   - message: "Author exceeds max 80 chars"  │
│                                             │
│ Rules tested: 4                             │
└─────────────────────────────────────────────┘
```

---

### Example 6: Template Not Found ❌
```
┌─────────────────────────────────────────────┐
│ ❌ EXAMPLE 6: Non-existent template        │
│                                             │
│ GIVEN front matter with:                    │
│   template: super-fancy-template            │
│                                             │
│ WHEN parsed                                 │
│                                             │
│ THEN:                                       │
│   - validation: FAILURE                     │
│   - error: StructureError                   │
│   - message: "Template '...' not found"     │
│                                             │
│ Rules tested: 5                             │
└─────────────────────────────────────────────┘
```

---

### Example 7: Markdown in Title ✅
```
┌─────────────────────────────────────────────┐
│ ✅ EXAMPLE 7: Formatted title              │
│                                             │
│ GIVEN markdown with:                        │
│   # Introduction to **Fourier** and ∑       │
│                                             │
│ WHEN parsed with theme option:              │
│   content.preserveMarkdownInHeadings: true  │
│                                             │
│ THEN:                                       │
│   - title: "Introduction to **Fourier** and ∑" │
│   - formatting preserved                    │
│   - validation: SUCCESS                     │
│                                             │
│ Rules tested: 6                             │
└─────────────────────────────────────────────┘
```

---

## ❓ Questions (Red Cards)

### Q1: Auto-promote H2 to H1?
```
┌─────────────────────────────────────────────┐
│ ❓ Q1: Auto-promote H2 to H1 if missing?   │
│                                             │
│ If markdown has only H2, should we promote  │
│ it to title slot?                           │
│                                             │
│ ✅ RESOLVED: No, fail validation           │
│ Rationale: Author should fix their markdown │
└─────────────────────────────────────────────┘
```

---

### Q2: Count Lines in H1?
```
┌─────────────────────────────────────────────┐
│ ❓ Q2: How to count lines in heading?      │
│                                             │
│ Do newlines within H1 count as multiple     │
│ lines?                                      │
│                                             │
│ ✅ RESOLVED: Count newlines in text node   │
│ Example: "Line 1\nLine 2" = 2 lines        │
└─────────────────────────────────────────────┘
```

---

### Q3: Multiple H2 Headings?
```
┌─────────────────────────────────────────────┐
│ ❓ Q3: What if multiple H2s exist?         │
│                                             │
│ Which H2 becomes the subtitle?              │
│                                             │
│ ✅ RESOLVED: First H2 is subtitle          │
│ Rest ignored with warning logged            │
└─────────────────────────────────────────────┘
```

---

### Q4: Identify Author?
```
┌─────────────────────────────────────────────┐
│ ❓ Q4: How to identify author?             │
│                                             │
│ Option A: Paragraph prefix "Author: ..."    │
│ Option B: Front matter "author: ..."        │
│ Option C: Both (front matter wins)          │
│                                             │
│ ✅ RESOLVED: Front matter only             │
│ Rationale: Docusaurus compatibility         │
└─────────────────────────────────────────────┘
```

---

### Q6: Unicode/Emoji in Title?
```
┌─────────────────────────────────────────────┐
│ ❓ Q6: Allow emoji/unicode in titles?      │
│                                             │
│ Should we allow/sanitize emoji and unicode? │
│                                             │
│ ✅ RESOLVED: Allow both (author's choice)  │
│ - Emoji: allowed (theme option)             │
│ - Math symbols (∑, π): encouraged           │
│ - MDSlides docs: avoid emoji internally     │
│                                             │
│ Theme option: content.allowEmoji            │
│ Theme option: content.allowUnicodeSymbols   │
└─────────────────────────────────────────────┘
```

---

### Q7: Inline Markdown in Title?
```
┌─────────────────────────────────────────────┐
│ ❓ Q7: Preserve inline markdown?           │
│                                             │
│ If title has **bold**, [links], `code`?     │
│                                             │
│ ✅ RESOLVED: Preserve formatting           │
│ Author's choice, theme configurable         │
│                                             │
│ Theme option: content.preserveMarkdownInHeadings │
└─────────────────────────────────────────────┘
```

---

## 📊 Example Mapping Summary

```
Story Coverage:
├── Rules: 6 blue cards
├── Examples: 7 green cards (4 success, 3 failure)
├── Questions: 6 red cards (all resolved ✅)
└── Confidence: HIGH

Rule → Example Coverage:
├── Rule 1 (Title required): Examples 1, 4
├── Rule 2 (Title max 2 lines): Examples 3
├── Rule 3 (Subtitle optional): Examples 2
├── Rule 4 (Author optional): Examples 2, 5
├── Rule 5 (Template binding): Examples 1, 6
└── Rule 6 (Formatting preserved): Example 7

Validation Coverage:
├── StructureError: Examples 4, 6
├── ContentError: Examples 3, 5
└── Success: Examples 1, 2, 7
```

---

## 🎯 Readiness Assessment

### Story is READY ✅

**Criteria Met**:
- ✅ Business rules clearly defined
- ✅ All examples have Given/When/Then
- ✅ Edge cases identified (failure scenarios)
- ✅ All questions resolved
- ✅ Acceptance criteria documented
- ✅ Testable scenarios written

**Complexity**: Medium
**Estimated Effort**: 2-3 days (TDD implementation)

**Dependencies**:
- Template Library loading mechanism
- Flexmark Markdown parser integration (Anticorruption Layer)
- YAML front matter parser
- Validation error accumulation (NonEmptyList[ValidationError])

---

## 📚 Related Artifacts

- **Three Amigos Session**: [three-amigos-session-001.md](three-amigos-session-001.md)
- **Domain Model**: [slide-deck-aggregate.md](../domain-models/aggregates/slide-deck-aggregate.md)
- **Template**: [templates/title.yaml](../../templates/title.yaml)
- **Theme**: [themes/default.json](../../themes/default.json)
- **Next Ceremony**: TDD Implementation (Phase 3)

---

**Ceremony Type**: Ceremony 2.2 - Example Mapping Workshop
**Date**: 2024-12-19
**Facilitator**: Tony Moores (TJM Solutions)
**Status**: Complete - Ready for Implementation
