# MDSlides Product Roadmap

**Last Updated**: 2026-04-22
**Status**: Active Development
**Current Version**: v1.6.0
**Maintainer**: Tony Moores, TJM Solutions

---

## Vision

MDSlides is a domain-driven, test-first presentation framework that transforms Markdown into professional HTML slide decks. Built on DDD principles with comprehensive test coverage, MDSlides prioritises code quality, maintainability, and developer experience.

**Core Principles:**
1. **Domain-Driven Design**: Pure functional domain models, clear bounded contexts
2. **Test-First Development**: TDD — Event Storming → Three Amigos → Example Mapping → RED-GREEN-REFACTOR
3. **Quality Over Speed**: Full test coverage, property-based testing, comprehensive validation
4. **Zero Technical Debt**: Fix issues immediately, maintain clean architecture
5. **Documentation as Code**: Every feature documented with ceremony artifacts before implementation
6. **Dogfooding**: Tutorial (`examples/mdslides-tutorial.md`) demonstrates every feature; re-render on every significant change

---

## Release History

| Version | Release Date | Key Features | Tests | Status |
|---------|-------------|--------------|-------|--------|
| v0.1.0 | 2024-11 | MVP: Basic rendering, templates, validation | 121 | ✅ |
| v0.2.0 | 2024-12 | Markdown formatting, code blocks, theme system | 171 | ✅ |
| v0.3.0 | 2024-12 | Image support, asset copying | 192 | ✅ |
| v0.4.0 | 2024-12 | Directory themes, background images, lists | 235 | ✅ |
| v1.0.0 | 2024-12 | Speaker notes, presentation timer | 268 | ✅ |
| v1.1.0 | 2024-12 | Speaker view window, cross-window sync | 290 | ✅ |
| v1.2.0 | 2024-12 | Nested lists (3 levels, mixed ordered/unordered) | 290 | ✅ |
| v1.3.0 | 2025-12 | Syntax highlighting via highlight.js (190+ languages) | 322 | ✅ |
| v1.3.1 | 2025-12 | **Hotfix**: BUG-001 list order, BUG-002 'S' key | 328 | ✅ |
| v1.4.0 | 2026-Q1 | WCAG 2.1 AA accessibility validation, JSON report | ~340 | ✅ |
| v1.4.1 | 2026-04-21 | BUG-003 tables, BUG-004 slide overflow | ~345 | ✅ |
| v1.5.0 | 2026-04-21 | Google Fonts (`googleFonts` in theme JSON) | ~348 | ✅ |
| v1.6.0 | 2026-04-21 | Live reload (`--watch`), validation line numbers | ~350 | ✅ |

---

## What's In The Box (v1.6.0)

**Rendering Pipeline**: Markdown → domain validation → HTML + speaker view + JS assets

**Templates**: `title`, `content`, `diagram`, `section-title`, `closing`

**Themes**: `light`, `dark`, `corporate`, `retisio` (directory-based), `tjm-solutions`; custom JSON themes with Google Fonts

**Runtime features** (delivered as static JS files alongside the HTML):
- Navigation: arrow keys, Home/End, non-linear history (P/N), goto (G + number)
- Timer: auto-start, pause (T), reset (R), break mode (B), cross-window sync
- Header/footer: dynamic placeholders (`{{title}}`, `{{author}}`, `{{date}}`, `{{pageNumber}}`, `{{totalPages}}`, `{{timer}}`); multi-element layout via `footer-left/center/right`
- Speaker view (S): dual-pane with next-slide preview, notes, sync
- Session logging: per-slide timing, navigation events, break durations
- Syntax highlighting: highlight.js CDN, theme-aware

**Validation**: structure, content density, accessibility (WCAG 2.1 AA), line numbers in errors

**CLI**: `render`, `--theme`, `--watch`, `--no-copy-images`, `--skip-accessibility`, `--accessibility-report`

**Dogfood tutorial**: `examples/mdslides-tutorial.md` — ~96 slides, re-rendered in 4 themes on every significant change

---

## Current Focus (Post v1.6.0)

The v1.x backlog is fully shipped. **Next release version and scope TBD** — to be decided through brainstorming.

### Known quality gaps (not blocking, tracked for reference)

- Density validation uses line count as a proxy; actual rendered height is not measured
- `background-size` for per-slide images uses `cover` (may crop on non-16:9 viewports); theme template backgrounds use `100% 100%`
- Headers/footers are in-flow flex children — fixed-position overlays (e.g. logo badges) are not currently supported
- Presentation timer appears as a `position: fixed` overlay — styling options are limited (no theme control)

---

## Future — Candidate Ideas

> Ideas move through: **brainstorm → design seed → ceremony → User Story → TDD → release**.  
> Items marked ⬜ Pre-ceremony are not committed — ceremonies define scope and acceptance criteria.

---

### Zone/Slot Branding System ⬜ Pre-ceremony

**Status**: Brainstorm complete (2026-04-22). Three Amigos + Example Mapping required before implementation.  
**Design seed**: [`design-seed-zone-slot-system.md`](design-seed-zone-slot-system.md)  
**Likely target**: v1.7.0 (infrastructure-only) or v2.0.0 (if domain model touched) — TBD post-ceremony

**Problem**: Current header/footer bundles visual chrome with content into one monolithic element. No overlay layer exists. No way to suppress a global header/footer on specific slides or templates.

**Direction agreed in brainstorm**:

Three primitives:

- **Zones** (Layer 1, in-flow) — named visual containers (`top`, `bottom`, `left`, `right`) that frame the content area; have their own background, height, colour; can bind to a slot
- **Corners** (Layer 2, overlay) — small image anchors (`corner-top-left` etc.) that float above content without affecting layout
- **Slots** (formalised) — named content placeholders (`header`, `footer`); support existing `{{placeholder}}` syntax; rendering location determined by zone binding

**Zone/slot binding**: a zone declares `"slot": "header"` — the zone provides the chrome, the slot provides the content. If no zone is configured, slots render standalone (backward-compatible).

**Override hierarchy**: slide frontmatter > template config > theme global.

**Motivating example** (TJM Solutions): navy bottom bar with logo + page number on content slides; corner logo overlay; no zones on title/closing slides.

**Open questions for ceremony**: `left`/`right` zones in scope?; multi-element zone content without raw HTML?; domain model impact of per-slide zone overrides?; template-defined layout grids (v2?); accessibility of decorative corner images.

---

### Other Candidates (unranked, unceremoned)

**Authoring**
- Slide aspect ratio constraint — lock to 16:9 with letterboxing on non-matching viewports
- Per-theme/template CSS override blocks in theme JSON
- Hotkey customisation via config

**Export & Distribution**
- PDF export via headless Chromium / Playwright
- Self-contained single-file output (inline all assets)
- Static hosting deployment helper (GitHub Pages etc.)

**Tooling & DX**
- CLI `new` command — scaffold a new presentation
- `validate` command — validate without rendering
- Thumbnail generation for speaker view

**Themes**
- Theme generator CLI
- Theme linting / contrast pre-check before render
- Dark-mode auto-switching variant

**v2.0.0 Candidates (Major)**
- Plugin / extension API
- User-defined custom templates (in theme directory)
- Configuration file cascade (`~/.mdslides/config.json` → `.mdslides/config.json` → CLI)

---

## Process

| Tier | When | Ceremony |
|------|------|----------|
| Bug fix | Regression found | TDD only — no design docs, no plan mode |
| Feature | New user-visible behaviour | 1 design doc + TDD |
| Design change | Architecture / process / principles | Full ceremony (Event Storming → Three Amigos → Example Mapping) |
| Pre-ceremony idea | Brainstorm produces direction | Design seed → ceremony → User Story → TDD |

**Roadmap update rule**: update this file whenever a version ships, a bug closes, a brainstorm produces a decision, or a ceremony changes the scope of a pre-ceremony item. Keeping the roadmap current is part of the process.

---

## Strategic Focus Areas

### Core Quality (Ongoing)
Maintain full test coverage (~350 tests and growing), zero technical debt, clean DDD layering.

### Developer Experience (v1.x → v2.0)
Make authoring and debugging presentations fast and pleasant: better error messages, live reload, `validate`-only mode, improved density feedback.

### Extensibility (v2.0+)
Plugin API, custom templates, config cascade — allow MDSlides to serve teams with bespoke brand requirements without forking the core.

---

**Next Review**: when brainstorming session concludes
**Next Release**: TBD
