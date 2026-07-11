# MS-017: Typed Slot Name ADT — Domain Model Design (Pre-Implementation Gate)

**Status:** Approved (Tony, 2026-07-11 — "approved as written") — both Q1 (enum, reusing the dead
`SlotName` opaque type) and Q2 (scope to content slots only) accepted as recommended. Ready for
implementation per the mechanism below.
**Related:** LL-003 (`docs/agents/LESSONS-LEARNED.md:13-18`), ADR-008 (Slot-Based Content Model), WORK-QUEUE MS-017 (GL-030 sequence 3 of 3)
**Date:** 2026-07-11

---

## Problem (LL-003)

`Slide.slots` is `Map[String, String]` (`domain/.../Slide.scala:33`). The parser writes string
keys (`MarkdownParser.scala`), the renderer reads string keys (`HTMLRenderer.scala`,
`SpeakerViewRenderer.scala`), and `SlotDefinition.name` / `Template.getSlot` compare by raw
`String` equality (`Template.scala:39`). Nothing ties these three call sites together at compile
time. MS-005/HL-002 hit exactly this: the parser wrote a heading under one key, the renderer
looked up a different key, and the slide silently rendered without its heading — no error,
no warning, no test failure until someone noticed the output.

MS-017 asks for a typed `SlotName` ADT enforced across the parser→domain→renderer boundary so a
name mismatch becomes a compile error instead of a silent no-op.

## Current-state findings that change the shape of the fix

1. **A `SlotName` type already exists and is dead code.** `domain/.../SlotName.scala` defines
   `opaque type SlotName = String` with regex-validated construction (ADR-008 reference in its
   own doc comment), but `grep -rl SlotName` outside that file returns nothing — `Slide.slots`,
   `SlotDefinition.name`, and `Template.getSlot` never adopted it. Whatever we build, this file
   either becomes the vehicle or gets replaced; it should not stay unused.

2. **Templates are a closed, hardcoded set — not the open/config-driven set ADR-008 sketched.**
   `Template.scala`'s `fromName` is a `match` over exactly six literal names (`title`, `content`,
   `diagram`, `closing`, `section-title`, `two-column`) with `Left` for anything else. All six
   `Template` values are Scala `val`s built from `SlotDefinition` values also defined in code
   (`SlotDefinition.scala`). There is no code path today that loads a template (or its slot list)
   from theme JSON or any other external config — directory-based *themes* (PDR-013) carry colors
   and background image paths, not slot definitions. So the full slot-name keyspace is: `title`,
   `subtitle`, `author`, `heading`, `body`, `caption`, `leftColumn`, `rightColumn` — 8 names,
   closed, all known today. **This means a closed enum is actually a good fit for the current
   codebase**, unlike what ADR-008's forward-looking "custom templates in v1.1" language might
   suggest.

3. **`Slide.slots` is also used to carry per-slide frontmatter metadata, not just template
   content.** `MarkdownParser.scala:154-156` stuffs `vertical-align`, `header`, `footer` from
   YAML frontmatter into the *same* `Map[String, String]` that holds `heading`/`body`/etc., and
   `HTMLRenderer.scala:707,730,749` reads them back via the same `slide.getSlot(...)` call. These
   three keys are never declared in any `SlotDefinition` / `Template.slots` list — they're an
   out-of-band channel piggybacking on the slot map. A `SlotName` ADT that is scoped only to
   `SlotDefinition`-declared names would leave this second channel exactly as stringly-typed and
   exactly as exposed to the LL-003 failure mode as it is today.

## Two questions this needs a decision on before implementation starts

**Q1 — Closed ADT or reuse the existing open, validated `SlotName`?**
MS-017's WQ text says `sealed trait SlotName` with case objects. That directly conflicts with
CLAUDE.md's own convention ("enum over sealed traits for ADTs with label/value fields") — every
existing closed domain ADT here (`VerticalAlignment`, `TemplateLayout`, etc.) is an `enum`. Given
finding 2 above (the keyspace is genuinely closed today), an `enum SlotName` fits both the actual
domain and repo convention better than either a literal `sealed trait` or the existing open
opaque-type wrapper. Recommendation: replace `SlotName.scala`'s opaque type with
`enum SlotName { case Title, Subtitle, Author, Heading, Body, Caption, LeftColumn, RightColumn }`,
plus a `fromString`/`toString` pair mirroring the existing `TemplateLayout` pattern for the
JSON/markdown boundary where a raw string necessarily still arrives.

**Q2 — Does `SlotName` cover the frontmatter metadata keys too?**
Two honest options:
- **(a) Scope MS-017 to content slots only** (the 8 names in finding 2) and leave `header`/
  `footer`/`vertical-align` as raw `String` keys in a note explaining why (they're config,
  not template content — arguably don't belong in `Slide.slots` at all, but that's a separate,
  larger refactor of `HeaderFooter`/frontmatter handling, out of MS-017's stated scope).
- **(b) Add three more `SlotName` cases** (`Header`, `Footer`, `VerticalAlign`) so the entire
  `Slide.slots` map is typed, closing that channel to the same LL-003 risk class.

Recommendation: **(a)**. LL-003 was specifically about template-declared content slots
(parser writes a content key, renderer reads a content key, `SlotDefinition` declares neither).
The frontmatter keys are a different, already-consistent contract (`MarkdownParser` and
`HTMLRenderer`/`HeaderFooter` already agree on 3 literal strings with no `SlotDefinition`
indirection in between) — folding them in enlarges the change (touches `ConfigLoader.scala`,
`ThemeJsonAdapter.scala`, `HeaderFooter.scala` too) without addressing a bug that's actually been
observed there. Worth a follow-up WQ item if desired, not silently bundled into MS-017.

## Proposed mechanism (pending review)

1. Replace `domain/.../SlotName.scala`'s `opaque type` with
   `enum SlotName derives CanEqual { case Title, Subtitle, Author, Heading, Body, Caption, LeftColumn, RightColumn }`
   plus `SlotName.fromString(s: String): Either[String, SlotName]` and an extension `.value: String`
   (kebab/camel string per existing literal, e.g. `LeftColumn.value == "leftColumn"`) for the JSON
   and Markdown boundaries.
2. `SlotDefinition.name: String` → `SlotDefinition.name: SlotName`; the 8 existing
   `SlotDefinition` vals (`SlotDefinition.title`, `.heading`, etc. in `SlotDefinition.scala`)
   updated to pass the enum case instead of a string literal.
3. `Slide.slots: Map[String, String]` → `Slide.slots: Map[SlotName, String]`; `getSlot`/`hasSlot`/
   `slotNames` retyped accordingly. `Template.getSlot(name: String)` → `getSlot(name: SlotName)`,
   comparison becomes `==` on the enum (compiler-checked exhaustiveness available at call sites
   that pattern-match).
4. `MarkdownParser.scala` — the parser now writes `SlotName.Heading -> ...` etc. directly (no
   intermediate string), which is the actual compile-time enforcement: a typo like
   `SlotName.Heding` doesn't compile, whereas `"heding"` silently type-checked before.
5. `HTMLRenderer.scala` / `SpeakerViewRenderer.scala` — every `slide.getSlot("heading")`-style
   call becomes `slide.getSlot(SlotName.Heading)`; the color-map keyed by slot name
   (`HTMLRenderer.scala:686-689`) becomes `Map[SlotName, ...]`.
6. `header`/`footer`/`vertical-align` frontmatter keys are left as `String` per Q2(a) — no change
   to `ConfigLoader.scala`, `ThemeJsonAdapter.scala`, `HeaderFooter.scala`, or the 3 literal
   frontmatter reads in `MarkdownParser.scala:154-156` / `HTMLRenderer.scala:707,730,749`.
7. Test fallout (updating, not new coverage): `SlideSpec`, `TemplateV2Spec`, `MarkdownParserSpec`,
   `HTMLRendererSpec`, `HTMLRendererTemplateV2Spec`, `HTMLRendererTableSpec`,
   `HTMLRendererImageSpec`, `HTMLRendererTwoColumnSpec`, `ContentSlideSpec`, and the property
   generators (`DomainGenerators.scala`, `SlideProperties.scala`, `SlideDeckProperties.scala`,
   `ContentSlideProperties.scala`) all construct `Slide`/`SlotDefinition` values with string slot
   keys today and need updating to `SlotName` cases.

## Why this pauses here

This touches the domain model's core aggregate (`Slide`) and its two hardest boundaries (parser
input, renderer output) across ~9 source files and ~12 test files, and resolves a real
disagreement between MS-017's literal wording (`sealed trait`) and the repo's own ADT convention
(`enum`), plus a scope decision (Q2) not stated in the WQ item text. Per this project's
Pre-Implementation Gate (`CLAUDE.md` Design Gates: "domain model document must exist and be
reviewed before implementation starts") and the `/next` skill's explicit instruction to pause
when a design note is newly written rather than proceed in the same pass, this document stops
here for review rather than proceeding straight to implementation.
