
# ✅ 1. What v1.0 **must** contain (true MVP)

**Definition of Done for MVP:**
A user can author a valid slide deck in Markdown → the system can parse it → validate it → bind templates → render to HTML → and do all of this from a CLI.

This aligns perfectly with **Phase 1 + Phase 3 in the Charter**.
Everything else — themes, advanced layouts, LaTeX-like enhancements — belongs to later phases.

---

# 🟦 **MVP (v1.0)** — Required Stories

## EPIC 1 — Core Authoring DSL (P0)

These are mandatory because without them MDSlides cannot produce *any* real decks.

### **✓ US-001 Title Slide** (Ready)

Baseline template binding.
→ Must include.
Priority: P0.

### **✓ US-002 Content Slide** (Draft)

80% of slides = content slides.
→ Must include.

### **✓ US-003 Multi-Slide Parsing**

Cannot build real decks without this.
→ Must include.

### **✓ US-004 Speaker Notes**

Charter says speaker notes are in scope and part of core DSL.
→ Must include.

---

## EPIC 3 — Validation Framework (P0)

The Charter explicitly stresses **multi-stage validation** as a core mission of the project.
Validation is literally part of the value proposition.

### **✓ US-011 Structure Validation**

Required so the deck is meaningfully parsed.

### **✓ US-012 Density Validation (“fits on slide”)**

This is one of the *core differentiators* of MDSlides compared to Marp.
→ Must include.

### **✓ US-013 Content Validation**

Slot constraints must be validated at parse time.
→ Must include.

### **✓ US-015 Collect All Validation Errors (no fail-fast)**

Domain-correct and expected in a functional pipeline.
→ Must include.

(US-014 Accessibility Validation is *important*, but not MVP-critical; see later.)

---

## EPIC 2 — Themes (minimal viable)

Charter says that **structure and style must be separate**, but MVP should *not* support everything.

### **✓ US-008 Apply Theme to SlideDeck**

Without this, HTML output will look unstyled.
But MVP theme can be *simple* (one default theme).

### **✓ US-009 Custom Theme Validation**

This one is borderline for MVP, but the Charter says *branding* and *WCAG checks* are core.
I recommend **partial implementation** for MVP:

* Validate theme structure
* Apply default theme
* Support a minimal JSON theme
* Defer accessibility enforcement to v1.1

So for MVP:
→ Only **structural validation**, not **full accessibility validation**.

---

## EPIC 4 — Rendering (Phase 3 in Charter)

Rendering is explicitly required for Phase 3 Before MDSlides is usable.

### **✓ US-016 Render SlideDeck to HTML**

Absolutely required.
This is the minimal render target.

> **Note:** The Charter does *not* require PDF for Phase 3.

---

## EPIC 5 — CLI Interface (Phase 3 in Charter)

### **✓ US-019 Generate Slides via CLI**

Absolutely required.
No CLI = no product.

---

## Minimum MVP Output Capabilities

* Input: markdown + optional front matter + templates
* Output: **HTML only** (not PDF)
* Validation: structure, density, content
* Themes: one default theme + basic custom theme loading
* Template types supported: title, content
* Speaker notes supported
* CLI works
* Errors are collected and reported
* Entire pipeline is pure FP (as per Charter’s constraints)

---

# 🎯 Final v1.0 MVP Story List

| Epic       | Story ID             | Title                                             |
| ---------- | -------------------- | ------------------------------------------------- |
| Core DSL   | **US-001**           | Title slide                                       |
| Core DSL   | **US-002**           | Content slide                                     |
| Core DSL   | **US-003**           | Multi-slide parsing                               |
| Core DSL   | **US-004**           | Speaker notes                                     |
| Validation | **US-011**           | Structure validation                              |
| Validation | **US-012**           | Density validation                                |
| Validation | **US-013**           | Slot content validation                           |
| Validation | **US-015**           | Collect all errors                                |
| Themes     | **US-008**           | Theme application (basic)                         |
| Themes     | **US-009** (partial) | Custom theme loading & structural validation only |
| Rendering  | **US-016**           | HTML rendering                                    |
| CLI        | **US-019**           | CLI generation                                    |

👉 **Total: 12 stories** — exactly the size of a reasonable *major version MVP.*

---

# 🟩 2. What should be included in **v1.1 (next major release after MVP)**

This version focuses on **completeness and professionalism**, not core viability.

### **P1 Features from the backlog that build on MVP**

* **US-014 Accessibility Validation**
  (Contrast checking, alt text, hierarchy checking)

* **US-005 Two-Column Layout**

* **US-006 Image Slide w/ Caption**

* **US-007 Code Snippet Slide**

These are "higher-value slide types" and are strongly aligned with a **usable product**, but not required to demonstrate the DSL.

---

# 🟧 3. What should be in **v2.0 (Feature-rich & Enterprise-ready)**

This version focuses on structure, automation, and document sophistication.

### **LaTeX-inspired structural features (high value)**

* **US-026 Variable substitution**
* **US-027 Section hierarchy**
* **US-028 Auto-generate TOC**
* **US-029 Section divider slides**
* **US-030 Appendix slide numbering**

This begins turning MDSlides into a *presentation-grade* alternative to LaTeX Beamer.

### Additional enhancements

* **US-017 PDF export**
* **US-022 Mermaid diagrams**
* **US-024 Custom templates from directory**

---

# 🟥 4. What should be in **v3.0 (Advanced Capabilities)**

This version includes features that are valuable but have high complexity or limited short-term ROI.

### Large / complex / niche features

* **US-021 Math typesetting**
* **US-023 Slide backgrounds**
* **US-025 Template inheritance**
* **US-031 Cross references**
* **US-032 References / bibliography**

These are the types of features enterprise users love, but they’re expensive and risky to attempt before the core system is stable.

---

# 📌 5. Why this ordering works

### ✔ Matches the Charter’s 3-phase plan

MVP = Phase 1 + Phase 3
Fonts, themes, slides = Phase 1
HTML rendering + CLI = Phase 3

### ✔ Enables early adopters to *actually use the system*

A tool that renders HTML slides from Markdown with validation is already a real product.

### ✔ Avoids getting bogged down in aesthetics or exotic layouts early

Two-column, image slides, appendix numbering, TOC — all great, but not what you build first.

### ✔ Keeps the functional core small, pure, and testable

v1.0 will be a **rock-solid FP pipeline**, setting you up for the long-term product vision.

### ✔ v1.1 + v2.0 align with natural feedback cycles

MVP → usability extensions → enterprise-level structure → advanced academic features.

---

# 🧭 6. Summary (Your Roadmap)

### **v1.0 (MVP)**

Core DSL + validation + HTML renderer + CLI.

### **v1.1**

More slide types + accessibility + theme enrichment.

### **v2.0**

Document structure features (TOC, sections, appendix) + PDF + diagramming.

### **v3.0**

Advanced academic/enterprise features (math, refs, cross-references, template inheritance).