---
template: title
---
# MDSlides Training
## **Ceremony-Based** Development
*Domain-Driven Design + BDD + TDD*

---
template: content
---
## What is MDSlides?

A **markdown-based** presentation tool demonstrating *ceremony-based SDLC*.

Converts markdown files into **standalone HTML** slide decks with *zero dependencies*.

Built using `DDD`, `BDD`, and `TDD` practices.

---
template: content
---
## Three Ceremonies Framework

**Phase 1: Event Storming** - *Domain discovery*
Identify domain events, commands, and aggregates

**Phase 2: Governance Documentation** - *Decision capture*
Write ADRs, PDRs, and POLs before coding

**Phase 3: TDD Implementation** - *Red-Green-Refactor*
Write tests first, implement pure functions

---
template: content
---
## Domain-Driven Design

**Ubiquitous Language** - Terms match business language
`Slide`, `SlideDeck`, `Template`, `SlotContent`

**Pure Functional Domain** - No side effects
All domain functions return `Either[Error, Result]`

**Bounded Contexts** - Clear module boundaries
`domain/`, `infrastructure/`, `cli/`

---
template: content
---
## Behavior-Driven Development

**User Stories** define *what* to build
`US-001`: Title Slide Creation
`US-003`: **Full Markdown Rendering**

**Acceptance Criteria** define *done*
Given valid markdown, when parsed, then formatted

**Example Mapping** explores *edge cases*
Empty slides, max limits, special characters

---
template: content
---
## Test-Driven Development

**Property-Based Testing** with `ScalaCheck`
Validates invariants across generated inputs

**Example-Based Testing** with `MUnit`
Documents specific scenarios and edge cases

**Test Coverage** - *96 tests passing*
Domain layer: **100% coverage**

---
template: content
---
## Architecture Patterns

**Anticorruption Layer** isolates external libs
`FlexmarkAdapter` translates **Flexmark** → `FormattedContent`

**Three-Layer Architecture** separates concerns
Domain is *pure*, infrastructure has *I/O*, CLI wires

**Immutability** everywhere - `case class`, `val`
No `var`, no `null`, no exceptions

---
template: content
---
## Governance Documentation

**ADR** - *Architecture Decision Records*
Technical choices: Flexmark, Scalatags, Mill

**PDR** - *Product Decision Records*
Business rules: 12-line limit, 150-word limit

**POL** - *Policy Documents*
Team standards: Pure functional domain, property testing

---
template: content
---
## v0.2.0 New Features

**Inline Formatting** (US-003) - ✅ *Complete!*
**Bold**, *italic*, `inline code` now render

**Code Blocks** (US-004) - *Coming soon*
Syntax highlighting with auto-scaling fonts

**Images** (US-005) - *Coming soon*
Background and content images with validation

**Themes** (US-008) - *Coming soon*
JSON-based color schemes and fonts

---
template: content
---
## Live Demo

This presentation uses **v0.2.0** features!

Notice the *italic emphasis* and **bold text**?
See the `code formatting` for technical terms?

All rendered from **pure markdown** with *zero JavaScript libraries* (except for navigation).

---
template: content
---
## Key Takeaways

Start with **ceremonies**, not code
Write **governance docs** before implementing
Use **TDD** throughout - tests drive design

Keep **domain pure** - no I/O, no exceptions
Use **property-based tests** for invariants
**Document decisions** in ADRs, PDRs, POLs

---
template: title
---
# Ready to Build?
## Start with *Event Storming*
*Discover your domain first!*
