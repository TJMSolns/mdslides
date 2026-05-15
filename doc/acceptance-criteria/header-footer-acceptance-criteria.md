# Acceptance Criteria: Header/Footer Enhancements

**Feature**: Header/Footer Template Configuration (v3.0.0 - Feature 7 of 10)
**Date**: 2025-12-29
**Status**: Pending Approval

---

## User Story

**As a** presentation author
**I want to** configure header and footer elements with placeholders (timer, page numbers, title)
**So that** I can customize the presentation chrome and display runtime information

---

## Acceptance Criteria

### AC-1: Footer Configuration (YAML Template)
**Given** template defines:
```yaml
footer:
  bottom-left:
    content: "{{timer}}"
  bottom-center:
    content: "Confidential"
  bottom-right:
    content: "{{pageNumber}} / {{totalPages}}"
```
**Then** all three positions render in footer row

**BDD Scenarios**: header-footer.feature (4 scenarios)

---

### AC-2: Static Placeholder Resolution (Render Time)
**When** rendering slide 5 of 42-slide deck
**Then** `{{pageNumber}}` resolves to "6" (1-indexed)
**And** `{{totalPages}}` resolves to "42"

**BDD Scenarios**: header-footer.feature (4 scenarios)

---

### AC-3: Dynamic Placeholder Resolution (Runtime)
**When** `{{timer}}` placeholder used
**Then** timer updates every second via JavaScript
**And** displays current elapsed time (hh:mm:ss)

**BDD Scenarios**: header-footer.feature (4 scenarios)

---

### AC-4: Timer Format (hh:mm:ss)
**Then** timer displays as "00:00:00" at start
**And** "00:05:30" after 5 minutes 30 seconds
**And** "01:02:05" after 1 hour 2 minutes 5 seconds

**Integration**: Timer pauses during break/goto

**BDD Scenarios**: header-footer.feature (3 scenarios)

---

### AC-5: Page Number Placeholder (1-Indexed)
**On slide 0**: pageNumber = "1"
**On slide 41**: pageNumber = "42"

**Updates on navigation** via data-slide-index attribute

**BDD Scenarios**: header-footer.feature (4 scenarios)

---

### AC-6: Total Pages Placeholder (Static)
**For 42-slide deck**: totalPages = "42" on all slides

**BDD Scenarios**: header-footer.feature (2 scenarios)

---

### AC-7: Title Placeholder (Deck Metadata)
**Given** frontmatter `title: "MDSlides v3.0.0"`
**Then** title = "MDSlides v3.0.0"

**If no title in frontmatter**:
**Then** title = filename (without .md)

**BDD Scenarios**: header-footer.feature (3 scenarios)

---

### AC-8: Custom CSS Classes
**Given** `class: "footer-timer"`
**Then** HTML: `<div class="footer-left footer-timer">00:05:30</div>`

**BDD Scenarios**: header-footer.feature (2 scenarios)

---

### AC-9: Footer Overlay Positioning (Fixed)
**Then** footer has:
```css
position: fixed;
bottom: 0;
z-index: 50;
```
**And** overlays slide content (does not push up)

**BDD Scenarios**: header-footer.feature (2 scenarios)

---

### AC-10: Template Defaults (Fallback to None)
**Given** no footer defined in template
**Then** no footer rendered (graceful omission)

**BDD Scenarios**: header-footer.feature (2 scenarios)

---

### AC-11: 4-Layer Config Precedence
**Precedence**: CLI arg > project config > template default > none

**BDD Scenarios**: header-footer.feature (3 scenarios)

---

### AC-12: Placeholder Validation at Render
**Given** invalid placeholder `{{timr}}` (typo)
**Then** render error: "Unknown placeholder '{{timr}}'. Valid: {{timer}}, {{pageNumber}}, {{totalPages}}, {{title}}"

**BDD Scenarios**: header-footer.feature (2 scenarios)

---

### AC-13: Footer Element Width Distribution
**CSS Grid**: `grid-template-columns: 1fr 1fr 1fr`
**Result**: Each position occupies 33.33% width

**BDD Scenarios**: header-footer.feature (2 scenarios)

---

## Definition of Done

- [ ] All 13 acceptance criteria implemented
- [ ] 33 BDD scenarios passing
- [ ] Domain model: FooterConfig value object
- [ ] UI: CSS Grid footer with placeholders
- [ ] Documentation updated

**Approval**:
- Product Owner: ________________ Date: ________
