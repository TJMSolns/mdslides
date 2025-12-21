# Example Mapping: US-019 Generate Slides via CLI

**Session Date**: 2024-12-20
**Participants**: Tony Moores (Business/Dev/QA)
**Story**: US-019 - Generate Slides via CLI
**Status**: Complete

---

## 📖 Story Card

```
┌─────────────────────────────────────────────┐
│ 📘 USER STORY                               │
│                                             │
│ As a slide deck author                      │
│ I want to run `mdslides input.md output.    │
│ html`                                       │
│ So that I can generate slides from the      │
│ command line                                │
│                                             │
│ Priority: P0 (Blocker)                      │
│ Estimated Effort: 2 days                    │
└─────────────────────────────────────────────┘
```

---

## 🔵 Blue Cards: Business Rules

### Rule 1: Parse required input filename
Input markdown file is required argument

### Rule 2: Parse optional output filename
Output HTML file optional (defaults to input.md → input.html)

### Rule 3: Parse --theme option
Optional theme name (default: "default")

### Rule 4: Parse --validate-only flag
Skip rendering, validate only

### Rule 5: Exit code 0 on success, 1 on error
Proper exit codes for scripting

### Rule 6: Print clear error messages
Validation errors, file I/O errors formatted clearly

---

## 🟢 Green Cards: Examples

### ✅ EXAMPLE 1: Basic usage
```
GIVEN: slides.md (valid)
WHEN: `mdslides slides.md`
THEN: slides.html created, "✓ Generated slides.html", exit 0
Rules tested: 1, 2, 5
```

### ✅ EXAMPLE 2: Custom output filename
```
GIVEN: slides.md
WHEN: `mdslides slides.md deck.html`
THEN: deck.html created, "✓ Generated deck.html", exit 0
Rules tested: 1, 2, 5
```

### ✅ EXAMPLE 3: Custom theme
```
GIVEN: slides.md, themes/dark.json
WHEN: `mdslides slides.md --theme dark`
THEN: slides.html with dark theme, exit 0
Rules tested: 1, 2, 3, 5
```

### ✅ EXAMPLE 4: Validate only
```
GIVEN: slides.md (valid)
WHEN: `mdslides slides.md --validate-only`
THEN: No HTML file, "✓ Validation passed", exit 0
Rules tested: 1, 4, 5
```

### ✅ EXAMPLE 5: Show version
```
WHEN: `mdslides --version`
THEN: "mdslides v1.0.0", exit 0
Rules tested: 5
```

### ✅ EXAMPLE 6: Show help
```
WHEN: `mdslides --help`
THEN: Usage and options displayed, exit 0
Rules tested: 5
```

### ❌ EXAMPLE 7: File not found
```
WHEN: `mdslides missing.md`
THEN: "Error: File 'missing.md' not found", exit 1
Rules tested: 1, 5, 6
```

### ❌ EXAMPLE 8: Validation errors
```
GIVEN: invalid.md (validation errors)
WHEN: `mdslides invalid.md`
THEN: Formatted validation errors to stderr, exit 1, no HTML file
Rules tested: 1, 5, 6
```

### ❌ EXAMPLE 9: Theme not found
```
WHEN: `mdslides slides.md --theme nonexistent`
THEN: "Error: Theme 'nonexistent' not found...", exit 1
Rules tested: 1, 3, 5, 6
```

### ❌ EXAMPLE 10: No arguments
```
WHEN: `mdslides`
THEN: Help message to stderr, exit 1
Rules tested: 1, 5, 6
```

---

## 🔴 Red Cards: Questions (All Resolved ✅)

### Q1: Support stdin input? ✅
**Answer**: Not in v1.0 (deferred to v1.1)

### Q2: Derive output filename? ✅
**Answer**: Replace `.md` with `.html`

### Q3: Theme validation in CLI? ✅
**Answer**: No, in pipeline (theme loading step)

### Q4: Print validation success? ✅
**Answer**: Yes, "✓ Validation passed"

### Q5: Different exit codes? ✅
**Answer**: No for v1.0 (all errors exit 1)

### Q6: Color-coded errors? ✅
**Answer**: Not in v1.0 (plain text)

---

## 🎯 Story Readiness Assessment

### Coverage Summary
| Metric | Count |
|--------|-------|
| Rules | 6 blue cards |
| Examples | 10 green cards (6 success, 4 failure) |
| Questions | 6 red cards (all resolved ✅) |

### Confidence Level
**HIGH CONFIDENCE** ✅

**Ready for TDD**: YES ✅

---

## 📋 Acceptance Criteria Summary

### Must Have
1. ✅ Parse input filename (required)
2. ✅ Parse output filename (optional)
3. ✅ Parse --theme option
4. ✅ Parse --validate-only flag
5. ✅ Parse --help, --version flags
6. ✅ Exit code 0 (success) / 1 (error)
7. ✅ Clear error messages
8. ✅ Run full pipeline (parse → validate → render)

### Should Have
9. ✅ CLI parsing < 10ms
10. ✅ Help text comprehensive

---

## 🎨 Visual Example Map

```
┌────────────────────────────────────────────────────────────┐
│                      📘 USER STORY                         │
│            Generate Slides via CLI (US-019)                │
└────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R1 │          │ 🔵 R2 │          │ 🔵 R3 │
    │ Input │          │Output │          │ Theme │
    │  Req  │          │ Opt   │          │  Opt  │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴──┬──────┬──┐ ┌──┴──┬──────┐   ┌───┴──┬──────┐
    │✅ E1 │✅ E2 │❌E7│✅ E1│✅ E2 │   │✅ E3 │❌ E9 │
    │❌ E8 │❌E10 │   └──────┴──────┘   └──────┴──────┘
    └──────┴──────┴──────┘

    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R4 │          │ 🔵 R5 │          │ 🔵 R6 │
    │Validat│          │ Exit  │          │ Error │
    │ Only  │          │ Codes │          │  Msgs │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴──┐          ┌───┴──┬──────┬──┐ ┌──┴──┬──────┐
    │✅ E4 │          │✅ E1 │✅ E5 │✅E6│❌ E7│❌ E8 │
    └──────┘          │❌ E7 │❌ E8 │❌E9│❌E10 │      │
                      │❌E10 │      │   └──────┴──────┘
                      └──────┴──────┴──────┘
```

---

## 🔄 Next Steps

1. ✅ Three Amigos Complete
2. ✅ Example Mapping Complete
3. 🔄 Update BACKLOG-V3.md
4. 🎉 **ALL 12 v1.0 MVP CEREMONIES COMPLETE!**

---

**Session Type**: Ceremony 2.2 - Example Mapping Workshop
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Story Status**: Ready for TDD ✅

---

## 🎉 MILESTONE ACHIEVED

**ALL 12 v1.0 MVP stories have completed ceremonies!**

Ready to transition to Phase 3: TDD Implementation
