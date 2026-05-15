# Three Amigos: BUG-004 — Slide Content Overflow

**Date:** 2026-04-21
**Participants:** Product Owner, Architect, Bench Developer
**Bug:** BUG-004 — Dense slides exceed viewport height; content clipped or scroll appears
**Priority:** P1 (High)
**Target Fix:** v1.4.1

---

## Bug Recap

**As a** presentation author  
**I want** slide content to stay within the viewport  
**So that** I never present a slide where content is silently missing or unexpectedly scrollable

**Root cause:** Missing `min-height: 0` on `.slide-content-wrapper`. This is a standard CSS flexbox constraint — without it, a flex child with `overflow: hidden` cannot shrink below its natural content height.

**Known affected files:** `examples/how-we-work.md` slides 38, 40

---

## Acceptance Criteria

### AC1: CSS Flexbox Clipping Corrected
**Given** any slide deck is rendered  
**When** the HTML CSS is inspected  
**Then** `.slide-content-wrapper` includes `min-height: 0`  
**And** overflow clipping is applied correctly by the browser

---

### AC2: Overflow Visibility — Not Silent
**Given** any slide deck is rendered  
**When** the HTML CSS is inspected  
**Then** `.slide-body` has `overflow-y: auto`  
**So that** a presenter can see (and scroll to) content that exceeds the visible area

*Rationale: `overflow-y: hidden` silently hides content. `auto` shows a scrollbar when needed, signalling the author that the slide is too dense.*

---

### AC3: Density Validation Unchanged
**Given** a slide body that exceeds 12 lines or 150 words  
**When** `Slide.validated()` is called  
**Then** a `DensityWarning` is returned (not a `ContentError`)  
**And** validation still returns `Right(slide)` — warnings do not fail the build

*BUG-004 does not change validation severity — it fixes the display layer, not the domain.*

---

### AC4: No Regression — Normal Slides
**Given** a slide with body content well within limits (≤12 lines, ≤150 words)  
**When** the deck is rendered  
**Then** the HTML output is structurally equivalent to pre-fix output  
**And** the new CSS properties do not affect layout of normal slides

---

## Decisions

### D1: `overflow-y: auto` vs `overflow-y: hidden`
**Decision:** `auto`  
**Rationale:** `hidden` silently removes content from the presenter's view during the session — they don't know slides are incomplete. `auto` makes overflow visible (scrollbar appears) and confirms to the author that the slide needs splitting. The density validation system provides the earlier authoring-time signal.

### D2: Validation severity — keep DensityWarning, not ContentError
**Decision:** Keep `DensityWarning`  
**Rationale:** Authoring dense slides is a style decision; the tool should warn, not block. Changing to `ContentError` would break existing presentations and violates PDR-001 (density validation is guidance, not enforcement).

### D3: Scope — CSS-only fix, no domain changes
**Decision:** Fix is entirely in `HTMLRenderer.generateCSS`  
**Rationale:** The domain model is correct. The overflow is a CSS rendering concern. Adding `min-height: 0` and `overflow-y: auto` requires two lines of CSS.

---

## Test Scenarios

### Scenario 1: CSS Properties Present
```
When: HTMLRenderer.renderDeck(anyDeck)
Then: generated CSS contains "min-height: 0" in slide-content-wrapper block
And:  generated CSS contains "overflow-y: auto" in slide-body block
```

### Scenario 2: Dense Content Not Lost at Render Time
```
Given: Slide body with 25 bullet points
When:  rendered to HTML
Then:  all 25 items present in HTML output
       (visual clipping is browser behaviour, not render behaviour)
```

### Scenario 3: Existing Tests Still Pass
```
When: mill infrastructure.test runs
Then: all pre-existing HTMLRendererSpec tests pass
      (layout, heading, body, navigation, counter — no regressions)
```

---

## Success Criteria

BUG-004 is resolved when:
1. ✅ New CSS tests pass (`HTMLRendererOverflowSpec`)
2. ✅ All existing `HTMLRendererSpec` tests still pass (no regressions)
3. ✅ `mill __.test` shows no new failures in infrastructure module
4. ✅ KNOWN-ISSUES.md updated: BUG-004 marked FIXED
5. ✅ CHANGELOG.md entry added

---

**Status:** ✅ Ready for TDD
**Next Step:** Write failing CSS tests → add 2 CSS properties → verify Green
