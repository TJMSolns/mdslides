# mdslides — Context Kernel

*Single source of truth for this implementation repo. Update at phase transitions only.*

**Last updated:** 2026-05-07
**Phase:** Active maintenance — P4 org priority

---

## Charter

**Mission:** To support TJM Solutions' productivity and engineering showcase by providing a Scala 3 / Cats Effect CLI tool for rendering markdown presentation decks to styled HTML with speaker view.

## Current Phase

**Maintenance + incremental feature development.** mdslides is a Scala 3 / Mill / Cats Effect CLI tool for rendering markdown presentation decks to HTML with speaker view. It is complete and used actively. Work is driven by feature requests and bugs, not a milestone roadmap.

---

## What This Is

Three-module Mill monorepo: `domain` → `infrastructure` → `cli`.

- Domain: pure FP, zero I/O. Slide/SlideDeck/Template/Theme aggregates, validation.
- Infrastructure: Flexmark (parse), Scalatags + HTMLRenderer (render), Circe (theme), os-lib (file I/O), Cats Effect.
- CLI: `Main extends IOApp`, wires modules, Decline for args, no business logic.

Tech stack: Scala 3.3.1 LTS, Mill 0.11.6, Cats Effect (not ZIO — mdslides predates org ZIO default), MUnit + ScalaCheck (300+ tests).

---

## Non-Negotiables

- `-Xfatal-warnings` — all warnings are fatal
- Domain layer must never depend on Cats Effect or os-lib
- Validation uses `Either[NonEmptyList[ValidationError], A]` — accumulate all errors
- Test names use domain language exactly
- ADR and PDR changes require governance doc before implementation

---

## Decisions

| DR-ID | Decision | Ref | Date |
|-------|---------|-----|------|
| (none recorded yet in harness — governance artifacts in doc/internal/governance/) | | | |
