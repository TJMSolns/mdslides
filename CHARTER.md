# CHARTER.md

## MDSlides: Ceremony-Based Framework Test

**Status**: Phase 2 - Specification COMPLETE → Ready for Phase 3 (TDD Implementation)
**Version**: 1.4.0
**Created**: December 18, 2024
**Last Updated**: December 20, 2024

### 1. Executive Summary

MDSlides is a standalone command-line application for generating beautiful, structured slide decks from a Markdown-based DSL with **template-driven authoring rails**. Inspired by Marp but with stronger guardrails around content structure and visual consistency, MDSlides separates slide structure (templates with slots) from visual style (themes). Built with Scala 3 and functional programming principles, it validates the DDD + BDD + TDD ceremony-based SDLC framework in a single-application (non-service) context.

### 2. Vision Statement

Enable rapid, ceremony-based creation of beautiful, data-driven slide decks through a DSL-based approach with:
- **Template-driven structure**: Slides conform to reusable templates (title, content, two-column, etc.) with named slots
- **Authoring rails**: Validation enforces "fits on slide" heuristics, GitHub Markdown compliance, and accessibility standards
- **Full traceability**: Every slide binds to a template, every validation rule is explicit
- **Type safety**: Pure functional domain model with no side effects

### 3. Scope

**In Scope**:
- **Slide deck DSL**: Markdown-based syntax with front matter for slide metadata (id, tags, template binding)
- **Template system**: Reusable slide templates (title, content, two-column, image, code) defining structural slots
- **Slot-based authoring**: Content mapped to named slots (title, subtitle, body, left_column, etc.) with constraints
- **Theme engine**: Color schemes, fonts, spacing (separate from templates - visual style not structure)
- **Validation framework**: Multi-stage validation (structure, density, content, accessibility)
  - Structure: slide count, title presence, template conformance
  - Density: "fits on slide" heuristics (max words, lines per slot)
  - Content: slot constraints (max chars, required fields)
  - Accessibility: WCAG AA compliance (color contrast, heading hierarchy)
- **Rendering pipeline**: HTML5 via Scalatags, PDF via HTML-to-PDF converter
- **Pure functional core**: Cats Effect 3 for effects, immutable domain model
- **CLI interface**: Decline for argument parsing, os-lib for file I/O
- **Template library management**: Load templates from templates/ directory, versioned template sets
- **BDD scenarios**: ScalaTest with Given/When/Then using ubiquitous language

**Out of Scope** (Phase 0):
- AI-based content generation (future: LLM-orchestrated slide creation)
- Real-time collaboration (future: web-based editor)
- Web service/API endpoints (CLI-only for v1.0)
- Database persistence (stateless file processing only)
- PPTX export (focus on HTML/PDF first)
- VS Code extension (future: live preview, template snippets)
- Template authoring UI (v1.0: hand-written YAML/JSON templates)
- Advanced template features (conditional slots, inheritance, composition)

### 4. Success Criteria

✅ **Bootstrap Validation**:
- All 14 copilot instructions present and accessible
- Framework documentation complete and navigable
- Build configuration ready (mill, Scala 3.3.1)

✅ **Phase 1: Domain Modeling** (after bootstrap):
- Domain aggregates identified and documented (SlideDeck, Template, Slide, Slot, Theme)
- Bounded contexts mapped (Slide Deck Authoring, Rendering Engine, CLI & File I/O)
- Ubiquitous language glossary created (with templates, slots, validation stages)
- Template/slot concepts integrated into domain model

✅ **Phase 3: TDD Implementation**:
- Example slide deck generated from Markdown DSL via CLI
- Templates loaded from templates/ directory (title, content, two-column)
- Slides bind to templates, content extracted into slots
- Multi-stage validation passes (structure, density, content, accessibility)
- Pure functional pipeline: Markdown → Template Binding → Validation → Rendering
- HTML output generated via Scalatags
- No blocking I/O (all effects in Cats Effect IO)

### 5. Key Assumptions

1. **Framework Maturity**: Ceremony-based SDLC framework (from copilot-training) is production-ready
2. **Mill Build System**: Mill 0.11.6 and plugins are stable and composable
3. **Team Readiness**: Single developer can complete ceremonies in 2-week sprints
4. **Technology Choices**: Scala 3.3.1 with Cats Effect 3 sufficient for standalone CLI
5. **No Microservices**: Single application architecture reduces ceremony overhead
6. **Template Simplicity**: v1.0 templates are static YAML/JSON (no dynamic composition or inheritance)
7. **Markdown Compatibility**: GitHub-flavored Markdown with YAML front matter is sufficient for DSL
8. **Validation is Compile-Time**: Template/slot validation happens at parse time (not render time)

### 6. Constraints & Risks

**Constraints**:
- Must use pure functional programming (no side effects in domain)
- Domain layer must be pure, no infrastructure dependencies
- Use Scala 3 enums, case classes, and opaque types (not Java Records)
- No blocking I/O (use effect system: Cats Effect IO)
- Single standalone application (no HTTP servers or databases)
- Templates and themes are immutable (new version = new file)
- Slide deck is immutable once validated (pure functional pipeline)
- All domain terms from ubiquitous language MUST appear in code
- Banned terms (Manager, Service, Handler, DTO) MUST NOT appear in domain code

**Risks**:
- Bootstrap plugin may have integration issues with root build.sc
- Framework files may reference copilot-training paths that need updates
- Cats Effect 3 learning curve for single developer (error handling, resource management)
- PDF rendering library choice (Flying Saucer, WeasyPrint, LaTeX) affects complexity
- Standalone architecture may not fully exercise ceremony framework (designed for services)
- Template/slot abstraction may be over-engineered for v1.0 (could start simpler)
- Flexmark AST mapping to domain model may be complex (Anticorruption Layer risk)
- Density validation heuristics ("fits on slide") may need empirical tuning
- WCAG AA compliance for user-provided content (images, code) may be hard to enforce

### 7. Phase Progress

**Phase 1 - Discovery** ✅ COMPLETE:
1. ✅ **Event Storming**: Identified domain events, aggregates (SlideDeck, Template, Slide, Slot)
2. ✅ **Ubiquitous Language**: Created glossary with template/slot concepts
3. ✅ **Domain Modeling**: Documented aggregates in detail
   - `doc/domain-models/aggregates/slide-deck-aggregate.md`
   - `doc/domain-models/aggregates/template-aggregate.md`
4. ✅ **Context Mapping**: Refined context map with template library boundary
5. ✅ **Example Templates**: Created template YAML files (title.yaml, content.yaml)

**Phase 2 - Specification** ✅ COMPLETE (12/12 stories - 100%):
1. ✅ **US-001**: Title Slide (Three Amigos + Example Mapping)
2. ✅ **US-002**: Content Slide (Three Amigos + Example Mapping)
3. ✅ **US-003**: Multi-Slide Parsing (Three Amigos + Example Mapping)
4. ✅ **US-004**: Speaker Notes (Three Amigos + Example Mapping)
5. ✅ **US-011**: Structure Validation (Three Amigos + Example Mapping)
6. ✅ **US-012**: Density Validation (Three Amigos + Example Mapping)
7. ✅ **US-013**: Content Validation (Three Amigos + Example Mapping)
8. ✅ **US-015**: Collect All Errors (Three Amigos + Example Mapping)
9. ✅ **US-008**: Apply Theme (Three Amigos + Example Mapping)
10. ✅ **US-009**: Custom Theme Validation (Three Amigos + Example Mapping)
11. ✅ **US-016**: HTML Rendering (Three Amigos + Example Mapping)
12. ✅ **US-019**: CLI Interface (Three Amigos + Example Mapping)

**Ceremony Artifacts Created**:
- 12 Three Amigos sessions (~120 KB documentation)
- 12 Example Mappings (~100 KB documentation)
- 2 Template definitions (title.yaml, content.yaml)
- 1 Default theme (default.json)
- Total: ~220 KB ceremony documentation

**Ready for TDD**: All 12 v1.0 MVP stories ready for implementation (100% complete)

---

**Prepared by**: Framework Bootstrap (Auto-Generated, enhanced with domain insights)
**Reviewed by**: Tony Moores, TJM Solutions (2024-12-19)
**Approved by**: Tony Moores, TJM Solutions (2024-12-19)

**Change Log**:
- v1.0.0 (2024-12-18): Initial bootstrap from mill-bootstrap-plugin
- v1.1.0 (2024-12-19): Added template/slot concepts from initial-thoughts.md, integrated Event Storming results
- v1.2.0 (2024-12-20): Updated status to Phase 2 - Specification, documented US-001 and US-002 ceremony completion
- v1.3.0 (2024-12-20): Phase 2 progress update - 5/12 stories ceremony-complete (US-001, 002, 003, 004, 011)
- v1.4.0 (2024-12-20): **Phase 2 COMPLETE** - All 12 v1.0 MVP stories ceremony-complete (100% coverage). Ready for Phase 3: TDD Implementation.
