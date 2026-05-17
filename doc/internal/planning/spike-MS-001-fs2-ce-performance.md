# MS-001 Spike: fs2 / Cats Effect I/O Performance + Calico TUI

**Status:** Complete — see recommendations
**Date:** 2026-05-17
**Related:** MS-001 (mdslides WORK-QUEUE)

---

## Scope

Evaluate:
1. I/O performance improvements via deeper Cats Effect / fs2 usage (streaming file I/O, parallel rendering pipeline)
2. Calico applicability to JVM CLI

---

## Current Pipeline I/O Profile

| Step | Current implementation | I/O cost |
|------|----------------------|----------|
| Read input | `IO.blocking(Files.readString)` | ~1 ms for typical deck (<1 MB) |
| Parse markdown | Pure CPU (FlexmarkAdapter) | ~5–20 ms |
| Validate | Pure CPU (domain validation) | <1 ms |
| Load theme | Pure (built-in) / `IO.blocking` (JSON file) | <1 ms |
| Copy images | `ImageAssetCopier.copyImages` — sequential | 10–200 ms with 5–20 images |
| Pre-render Mermaid | Sequential external process per diagram | 500–2000 ms per diagram |
| Render HTML | Pure CPU (HTMLRenderer) | ~10–50 ms |
| Write output | 2 × `Files.writeString` | ~1 ms |

**Bottleneck:** Image copying and Mermaid pre-rendering are the only meaningful I/O costs. Everything else is sub-millisecond.

---

## Analysis

### 1. Parallel Image Copying

**Current:** `ImageAssetCopier.copyImages` copies images sequentially in a `List.map` inside `IO.blocking`.

**Opportunity:** Each image copy is independent. Parallelizing with bounded concurrency would halve total copy time for N images.

**Implementation:** Refactor `ImageAssetCopier` to return `IO[Either[String, CopiedImage]]` per image, then use `IO.parTraverseN(4)` from `cats.effect.syntax.parallel`.

**Effort:** Low (~30 LoC change in `ImageAssetCopier`).
**Dependency change:** None — `cats-effect` is already a dependency.
**Value:** Moderate — deck images are usually 3–10 files, so ~2–3x improvement on that step.

**Recommendation:** Worth doing in a future maintenance item. Not urgent (image copy is not the dominant user-perceived latency — parsing and rendering dominate).

### 2. Parallel Mermaid Pre-rendering

**Current:** `MermaidRenderer` runs one `mermaid-cli` process per diagram sequentially.

**Opportunity:** `IO.parTraverseN(N)` to run multiple diagrams concurrently (bounded by CPU cores).

**Effort:** Low (~20 LoC).
**Dependency change:** None.
**Value:** High if a deck has multiple Mermaid diagrams (each pre-rendering takes 500–2000 ms).

**Recommendation:** Worth doing, but gated on mermaid-cli being installed (optional feature). Low priority until a user reports slow rendering with multiple diagrams.

### 3. Streaming File I/O via fs2

**Current:** `Files.readString` reads the entire markdown file into memory at once.

**Analysis:** mdslides decks are bounded by the 200-slide limit. At ~500 words per slide and ~3 KB per slide, a 200-slide deck is ~600 KB of markdown — trivial to hold in memory. The streaming benefit only appears above ~10 MB files, which mdslides will never encounter.

**Recommendation:** fs2 streaming file I/O is **not applicable** at mdslides scale. No change needed.

### 4. Parallel Rendering (Multi-deck Batch Mode)

**Status:** No batch `render-all` command exists in mdslides. If one is added in future, `fs2.Stream` with `parEvalMapUnordered(N)` would be the right tool.

**Recommendation:** **Defer** — not applicable to current feature set.

### 5. fs2 as a Dependency

**Current status:** mdslides does not depend on fs2. The rendering pipeline uses plain `IO.blocking` for all I/O.

**Analysis:** Adding fs2 would enable:
- `fs2.io.file.Files[IO].readAll` for streaming reads (irrelevant at scale)
- `Stream.emits.parEvalMapUnordered` for parallel processing (achievable with `IO.parTraverseN`)

**Conclusion:** `IO.parTraverseN` from the existing `cats-effect` dependency covers all realistic parallelism needs. **fs2 is not worth adding as a dependency** for the current feature set.

---

## Calico Applicability to JVM CLI

**Result: Not applicable.**

Calico is a functional reactive UI library for Scala.js (browser). It targets the DOM via Laminar and is not available for JVM targets. There is no JVM version of Calico.

For JVM terminal UI, options would include:
- `tui-scala` (a thin wrapper around Lanterna) — minimal community
- `zio-terminal` — ZIO ecosystem only (mdslides uses Cats Effect)
- Plain `IO.println` (current approach) — sufficient for mdslides CLI use case

**Recommendation:** Continue using `IO.println` for progress output. No terminal UI library is needed at current complexity level.

---

## Summary Recommendations

| Item | Recommendation | Priority |
|------|---------------|----------|
| Parallel image copying via `IO.parTraverseN` | Do if image copy latency is user-reported | P4 |
| Parallel Mermaid pre-rendering | Do if multi-diagram decks are slow | P4 |
| fs2 streaming file I/O | Do not add — irrelevant at mdslides scale | N/A |
| fs2 batch rendering stream | Defer — no batch command exists | Backlog |
| Calico TUI | Not applicable (Scala.js only) | N/A |
| Additional CE/fs2 dependency | Do not add — CE already covers needs | N/A |

**Bottom line:** mdslides I/O is already efficient for its scale. The only actionable improvements (parallel image/diagram processing) can be done with the existing `cats-effect` dependency using `IO.parTraverseN`. fs2 and Calico are not applicable.
