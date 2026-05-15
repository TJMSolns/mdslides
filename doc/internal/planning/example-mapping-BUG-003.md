# Example Mapping: BUG-003 — Markdown Table Rendering

**Date:** 2026-04-21
**Bug:** BUG-003 — Markdown tables render as plain text, not HTML `<table>` elements
**Affected:** examples/how-we-work.md slides 3, 28, 37
**Prerequisites:** Domain (`Table`, `TableElement`) and infrastructure (FlexmarkAdapter, HTMLRenderer) implementation already present
**Purpose:** Concrete examples to drive TDD tests that confirm (or expose bugs in) the existing implementation

---

## Story Rules

1. **Parsing:** A GFM markdown table is parsed into a `Table(headers, rows, alignment)` domain object and wrapped as `TableElement` in `FormattedContent.content`
2. **Header row:** The `|---|` separator row is NOT treated as a data row
3. **Cell text:** Inline formatting (`**bold**`, `_italic_`) inside cells is extracted as plain text
4. **Alignment:** `---`, `:---`, `---:`, `:---:` separator syntax maps to `""`, `"left"`, `"right"`, `"center"`
5. **HTML output:** `TableElement` renders as `<table><thead><tr><th>…</th></tr></thead><tbody><tr><td>…</td></tr>…</tbody></table>`
6. **CSS:** `.slide-body table` styles apply (border-collapse, zebra rows, theme colours)
7. **Source order:** Table appears at its position in the slide body, not after text or lists
8. **Empty table:** A table with headers but no body rows is valid and renders with empty `<tbody>`
9. **Density:** Tables are excluded from word/line count density checks (same as images and code blocks)

---

## Rule 1: Basic Table Parsing

### Example 1.1 — Two-column table, no alignment

**Input Markdown:**
```markdown
| Role | Responsibility |
|------|----------------|
| Program Manager | Coordination |
| Architect | Domain modeling |
```

**Expected domain object:**
```scala
Table(
  headers   = List("Role", "Responsibility"),
  rows      = List(List("Program Manager", "Coordination"),
                   List("Architect", "Domain modeling")),
  alignment = List("", "")
)
```

**Test assertions:**
- ✓ `headers` has 2 elements: `"Role"`, `"Responsibility"`
- ✓ `rows` has 2 entries; no separator row included
- ✓ `alignment` has 2 elements, both empty string (no alignment specified)

---

### Example 1.2 — Single-column table

**Input Markdown:**
```markdown
| Key |
|-----|
| alpha |
| beta |
```

**Expected:**
```scala
Table(headers = List("Key"), rows = List(List("alpha"), List("beta")), alignment = List(""))
```

**Test assertions:**
- ✓ Single header parsed
- ✓ Two body rows parsed
- ✓ No crash on single-column table

---

### Example 1.3 — Table with bold text in cells

**Input Markdown:**
```markdown
| Phase | Status |
|-------|--------|
| **Discovery** | ✅ Done |
| Implementation | 🔄 In progress |
```

**Expected:**
```scala
Table(
  headers = List("Phase", "Status"),
  rows    = List(List("Discovery", "✅ Done"), List("Implementation", "🔄 In progress")),
  ...
)
```

**Test assertions:**
- ✓ Bold markers stripped; cell text is `"Discovery"` not `"**Discovery**"`
- ✓ Unicode (emoji) preserved in cell text

---

## Rule 2: Column Alignment

### Example 2.1 — Left, center, right alignment

**Input Markdown:**
```markdown
| Name | Score | Grade |
|:-----|:-----:|------:|
| Alice | 95 | A |
| Bob | 80 | B |
```

**Expected:**
```scala
alignment = List("left", "center", "right")
```

**Test assertions:**
- ✓ `:---` → `"left"`
- ✓ `:---:` → `"center"`
- ✓ `---:` → `"right"`

---

### Example 2.2 — No alignment specified

**Input Markdown:**
```markdown
| A | B |
|---|---|
| 1 | 2 |
```

**Expected:**
```scala
alignment = List("", "")
```

**Test assertions:**
- ✓ Plain `---` separator → empty string alignment
- ✓ Renderer falls back to `text-align: left` default when alignment is `""`

---

## Rule 3: HTML Rendering

### Example 3.1 — Full table renders as HTML `<table>`

**Input domain object:**
```scala
Table(
  headers   = List("Tool", "Version"),
  rows      = List(List("Mill", "0.11.6"), List("Scala", "3.3.1")),
  alignment = List("left", "right")
)
```

**Expected HTML (simplified):**
```html
<table>
  <thead>
    <tr>
      <th style="text-align: left">Tool</th>
      <th style="text-align: right">Version</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="text-align: left">Mill</td>
      <td style="text-align: right">0.11.6</td>
    </tr>
    <tr>
      <td style="text-align: left">Scala</td>
      <td style="text-align: right">3.3.1</td>
    </tr>
  </tbody>
</table>
```

**Test assertions:**
- ✓ Output contains `<table>`
- ✓ Output contains `<thead>` and `<tbody>`
- ✓ Header cells use `<th>`, body cells use `<td>`
- ✓ Alignment style attribute applied to each cell
- ✓ Cell text matches input (no escaping issues)

---

### Example 3.2 — Table with no alignment still renders

**Input domain object:**
```scala
Table(headers = List("A", "B"), rows = List(List("1", "2")), alignment = List("", ""))
```

**Expected HTML includes:**
- `<th style="text-align: left">A</th>` (falls back to left)
- `<td style="text-align: left">1</td>`

**Test assertion:**
- ✓ Renderer does not crash on empty-string alignment

---

## Rule 4: Source Order Preservation

### Example 4.1 — Table between paragraphs

**Input Markdown:**
```markdown
First paragraph.

| A | B |
|---|---|
| 1 | 2 |

Last paragraph.
```

**Expected `FormattedContent.content` order:**
```
ParagraphElement("First paragraph.")
TableElement(Table(...))
ParagraphElement("Last paragraph.")
```

**Test assertion:**
- ✓ `content(0)` is `ParagraphElement`
- ✓ `content(1)` is `TableElement`
- ✓ `content(2)` is `ParagraphElement`

---

## Rule 5: Empty Table Body

### Example 5.1 — Headers only, no rows

**Input Markdown:**
```markdown
| Col1 | Col2 |
|------|------|
```

**Expected:**
```scala
Table(headers = List("Col1", "Col2"), rows = List.empty, alignment = List("", ""))
```

**Test assertions:**
- ✓ `rows` is empty, not an error
- ✓ Renders as table with `<thead>` and empty `<tbody>`

---

## Rule 6: Density Exclusion

### Example 6.1 — Table does not count toward line/word density

**Slide with only a table in body:**
```markdown
| A | B |
|---|---|
| word1 word2 word3 | word4 word5 word6 |
| word7 | word8 word9 word10 word11 word12 |
```

**Expected:** Body validation passes even if table cell text exceeds normal word limit
*(Tables are exempt from word/line count constraints, same as CodeBlockElement and ImageElement)*

---

## TDD Implementation Order

1. **Parser tests** (FlexmarkAdapter): Rules 1–4 → `FlexmarkAdapterTableSpec`
2. **Renderer tests** (HTMLRenderer): Rule 3 → `HTMLRendererTableSpec`
3. **Integration tests** (full parse → render round-trip): Rule 4, Rule 6 → `MarkdownParserTableSpec`

**Estimated new tests:** ~12–15

---

**Example Mapping Status:** ✅ COMPLETE
**Ready for:** Three Amigos acceptance criteria → TDD
