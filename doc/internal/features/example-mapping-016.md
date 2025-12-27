# Example Mapping: US-016 Render SlideDeck to HTML

**Session Date**: 2024-12-20
**Participants**: Tony Moores (Business/Dev/QA)
**Story**: US-016 - Render SlideDeck to HTML
**Status**: Complete

---

## 📖 Story Card

```
┌─────────────────────────────────────────────┐
│ 📘 USER STORY                               │
│                                             │
│ As a slide deck author                      │
│ I want to generate HTML from my slide deck  │
│ So that I can view my presentation in a     │
│ browser                                     │
│                                             │
│ Priority: P0 (Blocker)                      │
│ Estimated Effort: 4 days                    │
└─────────────────────────────────────────────┘
```

---

## 🔵 Blue Cards: Business Rules

### Rule 1: Render title slide template
Maps title slot → h1, subtitle → h2, author → p.author

### Rule 2: Render content slide template
Maps heading slot → h2, body → div.body with markdown rendered

### Rule 3: Theme CSS inlined into `<style>` tag
Theme colors, fonts, layout converted to CSS and injected into HTML head

### Rule 4: Keyboard navigation functional
Arrow keys (→, ←), Space, Home, End navigate slides

### Rule 5: Slide counter displayed
Shows "current / total" (e.g., "3 / 10"), updates on navigation

### Rule 6: Markdown formatted
Bold, italics, code, lists, code blocks rendered correctly

### Rule 7: HTML is valid
Passes W3C HTML5 validator

### Rule 8: No external dependencies
Single standalone HTML file, works offline

---

## 🟢 Green Cards: Examples

### ✅ EXAMPLE 1: Title slide rendered
```
GIVEN: Title slide with title, subtitle, author
WHEN: Rendered to HTML
THEN: HTML contains h1, h2, p.author
Rules tested: 1
```

### ✅ EXAMPLE 2: Content slide rendered
```
GIVEN: Content slide with heading + body (markdown)
WHEN: Rendered to HTML
THEN: HTML contains h2 + div.body with formatted markdown
Rules tested: 2, 6
```

### ✅ EXAMPLE 3: Theme CSS inlined
```
GIVEN: Theme with colors/fonts
WHEN: Rendered to HTML
THEN: <style> tag contains theme CSS rules
Rules tested: 3
```

### ✅ EXAMPLE 4: Keyboard navigation works
```
GIVEN: 5-slide deck rendered
WHEN: Press right arrow key
THEN: Slide 2 displayed, counter shows "2 / 5"
Rules tested: 4, 5
```

### ✅ EXAMPLE 5: Markdown bold/italics
```
GIVEN: Body with **bold** and *italics*
WHEN: Rendered
THEN: HTML contains <strong> and <em> tags
Rules tested: 6
```

### ✅ EXAMPLE 6: Markdown special characters escaped
```
GIVEN: Body with "<tag> & text"
WHEN: Rendered
THEN: HTML contains "&lt;tag&gt; &amp; text"
Rules tested: 6, 7
```

### ✅ EXAMPLE 7: Multi-slide deck
```
GIVEN: 5-slide deck
WHEN: Rendered
THEN: HTML has 5 div.slide elements, only first is active
Rules tested: 1, 2, 4
```

### ✅ EXAMPLE 8: Single-slide deck (no nav)
```
GIVEN: 1-slide deck
WHEN: Press right arrow
THEN: Remains on slide 1, counter shows "1 / 1"
Rules tested: 4, 5
```

### ✅ EXAMPLE 9: Valid HTML
```
GIVEN: Any deck
WHEN: Rendered to HTML
THEN: Passes W3C validator
Rules tested: 7
```

### ✅ EXAMPLE 10: Standalone HTML
```
GIVEN: Rendered HTML file
WHEN: Opened offline (no internet)
THEN: Works correctly (no external dependencies)
Rules tested: 8
```

---

## 🔴 Red Cards: Questions (All Resolved ✅)

### Q1: Missing subtitle slot? ✅
**Answer**: Render only h1 + author (subtitle optional)

### Q2: Render markdown in body? ✅
**Answer**: Use Flexmark to convert markdown → HTML

### Q3: Minify CSS? ✅
**Answer**: Not in v1.0 (deferred to v1.1 optimization)

### Q4: Navigate past last slide? ✅
**Answer**: No-op (stay on last slide)

### Q5: Update counter on navigation? ✅
**Answer**: Yes, JavaScript updates counter text

### Q6: Syntax highlighting? ✅
**Answer**: v1.0 no highlighting (deferred to v1.1)

### Q7: Ensure valid HTML? ✅
**Answer**: Scalatags generates valid HTML + W3C validator test

### Q8: Custom web fonts? ✅
**Answer**: Not in v1.0 (system fonts only)

---

## 🎯 Story Readiness Assessment

### Coverage Summary
| Metric | Count |
|--------|-------|
| Rules | 8 blue cards |
| Examples | 10 green cards (all success) |
| Questions | 8 red cards (all resolved ✅) |

### Rule → Example Coverage
All 8 rules have at least 1 example. Examples focus on integration (complete rendering pipeline).

### Confidence Level
**HIGH CONFIDENCE** ✅

**Ready for TDD**: YES ✅

---

## 📋 Acceptance Criteria Summary

### Must Have
1. ✅ Render title slide (h1, h2, p.author)
2. ✅ Render content slide (h2, div.body)
3. ✅ Theme CSS inlined
4. ✅ Keyboard navigation (→, ←, Space, Home, End)
5. ✅ Slide counter ("current / total")
6. ✅ Markdown formatted
7. ✅ HTML is valid
8. ✅ No external dependencies

### Should Have
9. ✅ Rendering < 500ms for 50-slide deck
10. ✅ HTML file size < 100 KB/slide
11. ✅ Browser compatibility (Chrome, Firefox, Safari, Edge)

---

## 🎨 Visual Example Map

```
┌────────────────────────────────────────────────────────────┐
│                      📘 USER STORY                         │
│           Render SlideDeck to HTML (US-016)                │
└────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R1 │          │ 🔵 R2 │          │ 🔵 R3 │
    │ Title │          │Content│          │ Theme │
    │ Slide │          │ Slide │          │  CSS  │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴──┐          ┌───┴──┬──────┐   ┌───┴──┐
    │✅ E1 │          │✅ E2 │✅ E5 │   │✅ E3 │
    └──────┘          │✅ E6 │      │   └──────┘
                      └──────┴──────┘

    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R4 │          │ 🔵 R5 │          │ 🔵 R6 │
    │  Key  │          │Counter│          │Markdwn│
    │  Nav  │          │       │          │Format │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴──┬──────┐   ┌───┴──┐          ┌───┴──┬──────┐
    │✅ E4 │✅ E7 │   │✅ E4 │          │✅ E2 │✅ E5 │
    │✅ E8 │      │   │✅ E8 │          │✅ E6 │      │
    └──────┴──────┘   └──────┘          └──────┴──────┘

    ┌───────┐          ┌───────┐
    │ 🔵 R7 │          │ 🔵 R8 │
    │ Valid │          │ Alone │
    │  HTML │          │       │
    └───┬───┘          └───┬───┘
        │                  │
    ┌───┴──┬──────┐   ┌───┴──┐
    │✅ E6 │✅ E9 │   │✅E10 │
    └──────┴──────┘   └──────┘
```

---

## 🔄 Next Steps

1. ✅ Three Amigos Complete
2. ✅ Example Mapping Complete
3. 🔄 Update BACKLOG-V3.md
4. ⏭️ Proceed to US-019 (CLI Interface)

---

**Session Type**: Ceremony 2.2 - Example Mapping Workshop
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Story Status**: Ready for TDD ✅
