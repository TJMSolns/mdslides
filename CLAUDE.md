# mdslides — Implementation Repo

**Role:** Scala 3 / Mill CLI tool for rendering markdown presentation decks to HTML with speaker view.
**Org:** TJM Solutions LLC — P4 priority.

---

## Mandatory Startup Read Order

Read these in order at the start of every session. Do not skip. If starting from a context summary, STOP — read these files now before doing anything else.

1. This file (`CLAUDE.md`)
2. `docs/agents/CONTEXT-KERNEL.md` — current phase and non-negotiables
3. `docs/agents/WORK-QUEUE.md` — what's in progress, what's next
4. `docs/agents/HANDOFF-LEDGER.md` — last 2–3 entries only

---

## Available Skills

| Skill | Purpose |
|-------|---------|
| `/next` | Pick next unblocked WORK-QUEUE item and drive to completion |
| `/handoff` | End-of-session: write HANDOFF-LEDGER entry, update WORK-QUEUE |
| `/status` | One-screen status digest |
| `/decide adr\|pol\|pdr <title>` | Record a durable decision |
| `/health` | Audit methodology infrastructure |
| `/retro` | Structured retrospective |
| `/audit <path>` | Governance review of any artifact |
| `/groom` | Review and reorder WORK-QUEUE |

---

## Build System

Mill 0.11.6, Scala 3.3.1 LTS. All warnings are fatal (`-Xfatal-warnings`).

```bash
mill __.compile                                        # Compile all modules
mill __.test                                           # Run all tests
mill domain.test.testOnly com.tjmsolutions.mdslides.domain.SlideSpec  # Single suite
mill cli.run -- render my-preso                        # Run CLI
mill cli.assembly                                      # Build fat JAR (mdslides.jar)
```

## Architecture

Three-module layered design with strict dependency flow: `domain` → `infrastructure` → `cli`.

**domain** — Pure functional, zero I/O, zero side effects. Depends only on Cats Core. Contains aggregates (`Slide`, `SlideDeck`, `Template`, `Theme`), value objects (`SlideId`, `ValidationError`, `FormattedContent`), and all validation logic. The domain layer must never acquire Cats Effect or os-lib as dependencies.

**infrastructure** — I/O adapters only. Flexmark parses markdown into domain models; Scalatags + HTMLRenderer renders them back out; Circe loads theme JSON; os-lib handles file I/O; Cats Effect runs effects. Infrastructure adapts the domain — it does not extend it.

**cli** — `Main extends IOApp`. Wires modules together, parses CLI args with Decline, orchestrates the rendering pipeline. No business logic lives here.

## Rendering Pipeline

`render` command flow:
1. Load and merge configuration (CLI args → project config → global config → defaults)
2. Parse markdown via `FlexmarkAdapter` → `SlideDeck`
3. Validate structure, content, density, and accessibility
4. Load theme (built-in: `light`, `dark`, `corporate`; or directory-based JSON)
5. Copy image assets (`ImageAssetCopier`)
6. Pre-render Mermaid diagrams (`MermaidRenderer`)
7. Emit `index.html` + `speaker.html` + JS support files via `HTMLRenderer` + `SpeakerViewRenderer`

## Ubiquitous Language

Terms used exactly in code, tests, and docs. Never substitute "DTO", "Manager", "Handler", "Helper", or "Service" in domain code (infrastructure adapters may use technical terms).

| Term | Meaning |
|---|---|
| **Slide** | Single presentation unit bound to a Template |
| **SlideDeck** | Complete presentation; enforced 1–200 slides (NonEmptyList) |
| **Template** | Structural definition — declares named slots and their constraints |
| **Slot** | Named content area within a Template (e.g., `title`, `body`, `heading`) |
| **Theme** | Visual styling — colors, fonts, backgrounds |
| **FormattedContent** | Parsed inline markdown (bold, italic, code, links, images, nested lists) |
| **ValidationError** | Blocking structural/content error; warnings are density violations |

## Testing

MUnit + ScalaCheck (property-based). 300+ tests across both modules.

- Example-based tests live in `*Spec.scala`; property-based generators live in `properties/` subdirectories
- Test names use domain language — match ubiquitous language exactly
- Domain tests are pure (no I/O); infrastructure tests may use temporary files via os-lib

## Key Conventions

- Scala 3 idioms: `enum` over sealed traits for ADTs with `label`/`value` fields; `given`/`using` over `implicit`
- Validation uses `Either[NonEmptyList[ValidationError], A]` — accumulate all errors, never short-circuit
- Configuration merges at three levels; `ConfigurationMerger` in domain handles the merge logic
- ADRs (`doc/internal/governance/adr/`) and PDRs (`doc/internal/governance/pdr/`) document all significant decisions — check them before changing architecture or adding features

## Governance Artifacts

When implementing a feature, cross-reference with:
- User Stories (`US-XXX`) in `doc/internal/planning/`
- Product Decision Records (`PDR-XXX`) in `doc/internal/governance/pdr/`
- Architecture Decision Records (`ADR-XXX`) in `doc/internal/governance/adr/`
