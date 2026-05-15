# Example Mapping: BUG-004 — Slide Content Overflow

**Date:** 2026-04-21
**Bug:** BUG-004 — Dense slides exceed viewport height; content is clipped or creates scroll
**Affected:** examples/how-we-work.md slides 38, 40
**Purpose:** Concrete examples to drive TDD tests and confirm the CSS + validation fix

---

## Root Cause Analysis

The CSS flexbox chain is:

```
.slide (display: flex; flex-direction: column; height: 100%)
  .slide-content-wrapper (flex: 1; overflow: hidden)   ← MISSING min-height: 0
    .content-slide (max-width: 900px)                  ← no height/overflow constraint
      .slide-heading
      .slide-body (no max-height, no overflow)         ← grows without bound
```

Without `min-height: 0` on a flex child, the child is NOT constrained to its allocated height — it can grow as large as its content. So `.slide-content-wrapper`'s `overflow: hidden` does not clip `.content-slide` correctly.

---

## Story Rules

1. **CSS clipping:** `.slide-content-wrapper` must correctly clip its child content. Requires `min-height: 0` on `.slide-content-wrapper` (standard CSS flexbox fix).
2. **Overflow visibility:** `.slide-body` must use `overflow-y: auto` so dense content is scrollable rather than silently hidden — the presenter can see content was cut off.
3. **Density warning preserved:** Existing density warnings (12 lines / 150 words) remain as `DensityWarning` — they are a guidance signal, not a hard error.
4. **No regression:** Slides with normal content volumes must look identical before and after the CSS fix.
5. **Layout structure:** The `min-height: 0` fix applies only to `.slide-content-wrapper`; other flex layout rules are unchanged.

---

## Rule 1: Flexbox Clipping Fix

### Example 1.1 — Normal content slide renders within bounds

**Input:** A content slide with 6 bullet points (well within 12-line limit)

**Expected:** Slide renders identically to pre-fix. No visual change. CSS property `min-height: 0` present on `.slide-content-wrapper`.

**Test assertion:**
- ✓ Generated HTML CSS contains `min-height: 0` in `.slide-content-wrapper` rule

---

### Example 1.2 — CSS contains `overflow-y: auto` on `.slide-body`

**Input:** Any slide deck

**Expected:** Generated HTML CSS contains `overflow-y: auto` in `.slide-body` rule

**Test assertion:**
- ✓ `html.contains("overflow-y: auto")`

---

## Rule 2: Overflow Visibility

### Example 2.1 — Dense content slide does not silently lose content

**Input:** A slide body with 25 bullet points (far exceeds 12-line limit)

**Expected HTML behaviour:** 
- Slide body has `overflow-y: auto` so dense content is scrollable within the slide area
- Content is NOT silently clipped with no indicator

**Test assertion:**
- ✓ Rendered HTML includes all 25 bullet points in the output (not clipped at render time — clipping is a browser display concern, not a render concern)
- ✓ Overflow is `auto` not `hidden` on `.slide-body`

---

## Rule 3: Density Warning Preserved

### Example 3.1 — Body exceeding 12 lines still produces DensityWarning

**Input:** Slide body with 15 lines

**Expected:**
```
⚠ DensityWarning: Slide N / body — Content exceeds recommended line limit (actual: 15, guidance: reduce to 12 lines)
```

**Test assertion:**
- ✓ `Slide.validated(...)` with 15-line body returns `Right(slide)` (warnings do not fail validation)
- ✓ Warning text contains line count

*Note: BUG-004 does NOT change validation severity — `DensityWarning` stays as warning, not error.*

---

## Rule 4: No Regression on Normal Slides

### Example 4.1 — Title slide renders without any changes

**Input:** Standard title slide

**Test assertion:**
- ✓ Title slide HTML unchanged (no new CSS properties interfere)
- ✓ `<h1>` and subtitle still render correctly

---

## TDD Implementation Order

1. **CSS tests** (HTMLRenderer): Rules 1–2 → `HTMLRendererOverflowSpec`
   - Verify `min-height: 0` in `.slide-content-wrapper`
   - Verify `overflow-y: auto` in `.slide-body`
2. **Implementation**: Add 2 CSS properties to `HTMLRenderer.generateCSS`
3. **Regression tests**: Rule 4 → existing `HTMLRendererSpec` must still pass

**Estimated new tests:** 4  
**Source changes:** 2 CSS property additions to `HTMLRenderer.scala`

---

**Example Mapping Status:** ✅ COMPLETE
**Ready for:** Three Amigos → TDD
