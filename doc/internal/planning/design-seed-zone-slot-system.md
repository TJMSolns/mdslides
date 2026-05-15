# Design Seed: Zone/Slot Branding System

**Status**: Pre-ceremony — brainstorm complete, no implementation committed  
**Date**: 2026-04-22  
**Author**: Tony Moores (brainstorm), captured by Claude Code  
**Next step**: Three Amigos → Example Mapping → User Story → TDD

---

## Problem Statement

The current header/footer system bundles two separate concerns into one element:

- **Visual chrome** — background colour, height, position in the layout
- **Slot content** — text, placeholders (`{{title}}`, `{{pageNumber}}`), HTML fragments

This monolith makes corporate branding impossible to express cleanly:

1. **No overlay layer** — there is nowhere to put a logo in a corner that floats above slide content without compressing the content area downward. Every element is in-flow.
2. **No suppression mechanism** — if a global footer is set in the theme, individual slides cannot opt out. Title slides and closing slides have no way to say "no footer here."
3. **No zone backgrounds** — a top brand bar with its own background colour, height, and content cannot be expressed without hardcoding it into a template's rendering logic.

**Motivating use case**: TJM Solutions theme with a navy bottom band (logo left, page number right) on all `content`/`diagram`/`section-title` slides, a corner logo overlay on all slides, and no brand chrome on `title` and `closing` slides.

This is a standard corporate branding requirement that the current system cannot satisfy.

---

## Design Direction

### The Three-Layer Model

```
Layer 0 — background  : image or colour behind the whole slide (exists today)
Layer 1 — flow        : zones and content in document flow; compress the content area
Layer 2 — overlay     : corners, always above flow, never move content
```

The layer an element occupies is determined by its **type** — authors never specify z-index directly. Zones are always Layer 1. Corners are always Layer 2.

---

### Primitives

#### Zones (Layer 1 — in-flow)

Named visual containers that frame the content area. A zone takes up real space and shrinks the content region inward.

Named positions: `top`, `bottom`, `left`, `right`

Properties (proposed, subject to ceremony):
- `height` / `width` — size of the zone
- `background` — colour, image, or gradient
- `color` — text colour for content within the zone
- `slot` — (optional) name of the slot whose content fills this zone

Zones without a `slot` binding are pure visual elements — a colour band, a logo strip, a decorative bar.

#### Corners (Layer 2 — overlay)

Small overlay anchors positioned absolutely within the slide. Never affect layout.

Named positions: `corner-top-left`, `corner-top-right`, `corner-bottom-left`, `corner-bottom-right`

Properties (proposed):
- `src` — image path (relative to theme directory)
- `size` — width/height of the image
- `padding` — offset from the slide edge

#### Slots (formalised existing concept)

Named content placeholders. `header` and `footer` are slots today, but they also carry their own chrome — that monolith is broken up by this design. Going forward, a slot is just content; a zone is just chrome.

Slots support:
- Placeholder syntax: `{{title}}`, `{{author}}`, `{{date}}`, `{{pageNumber}}`, `{{totalPages}}`, `{{timer}}`
- Arbitrary HTML fragments (as today)

---

### Zone/Slot Binding

A zone with `"slot": "header"` declares that the header slot's content fills that zone's visual container.

| Zone exists? | Slot has content? | Result |
|---|---|---|
| Yes | Yes | Content rendered inside zone chrome |
| Yes | No | Zone renders as pure visual band |
| No | Yes | Slot renders standalone, no chrome — **current behaviour preserved** |
| No | No | Nothing rendered |

**Backward compatibility**: themes without a `zones` block behave exactly as today. No breaking change to existing decks or themes.

---

### Proposed Theme JSON Grammar

```json
"zones": {
  "top": {
    "height": "60px",
    "background": "#1a3a5c",
    "color": "#ffffff",
    "slot": "header"
  },
  "bottom": {
    "height": "52px",
    "background": "#1a3a5c",
    "color": "#ffffff",
    "slot": "footer"
  },
  "corner-bottom-right": {
    "src": "assets/tjm-logo.png",
    "size": "40px"
  }
}
```

This is **illustrative** — the exact schema is a ceremony output, not a brainstorm output.

---

### Proposed Override Hierarchy

1. **Slide frontmatter** `header: "..."` — sets slot content for that slide; wins over theme default
2. **Slide frontmatter** `zones: false` or `zones: { top: false }` — suppresses zone(s) for that slide
3. **`templateConfig.<template>.zones: false`** — suppresses all zones for a template type (e.g. `title`, `closing`)
4. **Theme `zones` block** — global default

---

### TJM Solutions Example (full picture)

```json
"zones": {
  "bottom": {
    "height": "52px",
    "background": "#1a3a5c",
    "color": "#ffffff",
    "slot": "footer"
  },
  "corner-top-right": {
    "src": "assets/tjm-logo.png",
    "size": "36px",
    "padding": "10px"
  }
},
"header": "{{title}}",
"footer": "<span class='footer-left'><img src='assets/tjm-logo-small.png' height='20'></span><span class='footer-right'>{{pageNumber}} / {{totalPages}}</span>",
"templateConfig": {
  "title":   { "zones": false },
  "closing": { "zones": false }
}
```

Result:
- All `content`/`diagram`/`section-title` slides: navy bottom bar with logo + page count, corner logo overlay
- `title` and `closing` slides: no zones, no corner logo — clean full-bleed
- Any individual slide can set `zones: false` to opt out

---

## Open Questions for Ceremony

The following are deliberately **not decided** — they are inputs to the Three Amigos session:

1. **`left`/`right` zones** — in scope for the first release, or deferred? These are less common (sidebar layouts) and add implementation complexity.

2. **Multi-element zone content** — how does a zone express "logo left, page number right" without requiring raw HTML? A sub-slot syntax? CSS classes? The current approach (HTML fragment with `footer-left`/`footer-right` classes) is functional but not pretty.

3. **Domain model impact** — does `Slide` need a `zones` field, or is this entirely an infrastructure/rendering concern? If zones can be overridden per slide, the parser needs to read zone frontmatter, which touches the domain boundary.

4. **Template-defined regions** — should templates be able to define their *own* layout grid (not just suppress global zones)? E.g. a `two-column` template where the left column has a brand background. This may belong in v2 alongside user-defined templates.

5. **Accessibility** — decorative images in corners need `aria-hidden`; zone backgrounds need contrast checking against slot text; corner overlays need to not trap keyboard focus.

6. **`left`/`right` zones and content reflow** — if a left zone exists, does the content area use CSS Grid to reflow naturally, or does it use margin/padding tricks? This is an implementation choice with UX implications.

---

## Ceremonies Required

| Ceremony | Purpose | Status |
|---|---|---|
| Three Amigos | Agree scope, resolve open questions, define acceptance criteria | ⬜ Not started |
| Example Mapping | Concrete scenarios, edge cases, rule discovery | ⬜ Not started |
| Event Storming | Probably not needed — scope is contained to infrastructure layer (TBC in Three Amigos) | ⬜ TBC |

**Implementation is blocked on ceremonies.** This document is the input to Three Amigos, not a spec.

---

## Likely Candidate Release

TBD — depends on ceremony output, specifically whether the domain model is touched.

- **Infrastructure-only** (zones are a rendering concern, no `Slide` changes): candidate for **v1.7.0**
- **Domain model touched** (slide frontmatter zone overrides require parser + domain changes): candidate for **v2.0.0**
