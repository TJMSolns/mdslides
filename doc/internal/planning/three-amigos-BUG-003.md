# Three Amigos: BUG-003 — Markdown Table Rendering

**Date:** 2026-04-21
**Participants:** Product Owner, Architect, Bench Developer
**Bug:** BUG-003 — Tables in Markdown source render as plain text, not HTML `<table>` elements
**Priority:** P1 (High)
**Target Fix:** v1.4.1 or v1.5.0

---

## Bug Recap

**As a** presentation author  
**I want** Markdown tables to render as formatted HTML tables  
**So that** I can present structured data clearly on a slide

**Reported Behaviour:** Tables (GFM pipe syntax) are rendered as raw text or ignored.  
**Expected Behaviour:** Tables render as `<table><thead><tbody>` HTML with proper styling.

**Known affected files:**
- `examples/how-we-work.md` — slides 3, 28, 37

---

## Acceptance Criteria

### AC1: Basic Table Rendering
**Given** a slide body contains a GFM markdown table  
**When** the presentation is rendered  
**Then** the output HTML contains a `<table>` element  
**And** column headers appear in `<th>` elements inside `<thead>`  
**And** data rows appear in `<td>` elements inside `<tbody>`  
**And** the separator row (`|---|---|`) does NOT appear as a data row

---

### AC2: Column Alignment
**Given** a table separator row uses alignment syntax  
**When** the presentation is rendered  
**Then** `:---` produces `text-align: left` on cells in that column  
**And** `---:` produces `text-align: right`  
**And** `:---:` produces `text-align: center`  
**And** plain `---` produces no explicit alignment (browser default left)

---

### AC3: Inline Formatting in Cells
**Given** a table cell contains `**bold**` or `_italic_` markdown  
**When** the table is parsed  
**Then** the cell text is extracted without markdown syntax characters  
*(Inline formatting inside cells is rendered as plain text in v1.x — full inline formatting in cells is deferred)*

---

### AC4: Source Order Preservation
**Given** a slide body contains a table between two paragraphs  
**When** the content is parsed  
**Then** the rendered HTML shows: paragraph, then table, then paragraph  
**And** the table does NOT float to the bottom or top of the body

---

### AC5: Density Validation Exclusion
**Given** a slide body contains only a table with many cells  
**When** validation runs  
**Then** the table cell text does NOT count toward the body word or line density limits  
*(Same exemption as code blocks and images)*

---

### AC6: Empty Table Body
**Given** a markdown table has headers but no data rows  
**When** the presentation is rendered  
**Then** the output contains a `<table>` with `<thead>` and an empty `<tbody>`  
**And** no error or validation failure is raised

---

## Out of Scope (Deferred)

- **Rich inline formatting in cells** (bold, italic, links as HTML tags inside `<td>`) — deferred to v2.0.0
- **`caption` element** — deferred
- **Multi-row headers** — out of scope, not valid GFM
- **Column span / row span** — out of scope

---

## Decisions

### D1: Inline formatting in cells — plain text extraction
**Options:**
- A) Extract only plain text (strip markdown syntax) — simple
- B) Render inline formatting as HTML inside `<td>` — complex, requires recursive Scalatags rendering

**Decision:** A — plain text for v1.x  
**Rationale:** Matches existing `extractCellText` implementation; full rendering risks layout issues in table cells. Revisit at v2.0.0.

### D2: Density counting for tables
**Decision:** Exclude table cell text from word/line count  
**Rationale:** Consistent with existing policy for `CodeBlockElement` and `ImageElement` (PDR-006, PDR-008). Tables are structural, not prose.

### D3: Table CSS location
**Decision:** Keep in inline `<style>` block of `index.html` (existing `.slide-body table` rules)  
**Rationale:** Consistent with how code block, image, and list styles are handled. No external stylesheet dependency.

---

## Test Scenarios

### Scenario 1: Happy Path — Basic Table
```
Given: Slide body with a 2-column table, 2 header cols, 3 data rows
When: render my-preso
Then: index.html contains <table> with <thead> (2 × <th>) and <tbody> (3 × <tr>)
```

### Scenario 2: Alignment Columns
```
Given: Table separator |:---|:---:|---:|
When: render
Then: columns have text-align left, center, right respectively
```

### Scenario 3: Table Between Paragraphs — Source Order
```
Given: "Intro.\n\n| A | B |\n|---|---|\n| 1 | 2 |\n\nConclusion."
When: parse then render
Then: HTML order is <p>Intro</p> <table>…</table> <p>Conclusion</p>
```

### Scenario 4: how-we-work.md slide 3 — Real Regression Test
```
Given: examples/how-we-work.md slide 3 (known affected slide with a roles table)
When: render how-we-work
Then: slide 3 contains <table> not plain-text pipe characters
```

---

## Success Criteria

BUG-003 is resolved when:
1. ✅ All new MUnit tests pass (`FlexmarkAdapterTableSpec`, `HTMLRendererTableSpec`)
2. ✅ No existing tests broken
3. ✅ `mill __.test` green
4. ✅ KNOWN-ISSUES.md updated: BUG-003 marked FIXED
5. ✅ CHANGELOG.md entry added

---

**Status:** ✅ Ready for TDD
**Next Step:** Write failing MUnit tests → verify Green → fix if Red
