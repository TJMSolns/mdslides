# mdslides — Work Queue

P4 priority. Work items are driven by feature requests, bugs, and maintenance needs.

---

## Active

| ID | Item | Owner | Status | Depends On |
|----|------|-------|--------|-----------|
| MS-013 | **BUG** — `PathResolverSpec.findInputFile - error when not found` failing: assert that error message contains `"nonexistent.md"` fails; pre-existing as of cd067f6 (confirmed: failure present before MS-009 changes via git stash verification); MS-007 fix may not have covered this specific test case | Claude | Queued | — |
| ~~MS-009~~ | ~~Add explicit version declaration to build.sc; wire into `--version` CLI flag~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |
| ~~MS-010~~ | ~~Audit `.slide-body`-only CSS selectors for `.column` coverage gaps~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |
| MS-014 | **BUG** — CSS coverage gaps in two-column layout: `.slide-body`-scoped selectors for lists (ul/ol/li, bullet hierarchy), paragraphs (p margins), and tables (th/td styling, alternating rows) do NOT apply inside `.column.column-left` / `.column.column-right` because column content is not wrapped in `.slide-body`. Fix: duplicate each `.slide-body` selector rule to also target `.column` (e.g. `.slide-body ul, .column ul`). Affects HTMLRenderer.scala lines 442–519. Add regression tests in HTMLRendererTwoColumnSpec. | Claude | Queued | — |
| MS-001 | Spike: investigate I/O performance improvements via deeper Cats Effect / fs2 usage (streaming file I/O, parallel rendering pipeline) and Calico for terminal UI — Calico applicability to JVM CLI needs validation (primarily Scala.js); CE/fs2 JVM applicability is not in question | Claude | Queued | — |
| ~~MS-011~~ | ~~**P4 SPIKE** — MCP capability surface design~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |
| MS-012 | **P4** — Implement mdslides MCP server (Tier 1): `mcp` Mill module, stdio transport layer (~200 LoC), `render_deck` + `validate_deck` tools; Pre-scaffold gate: sequence diagrams for render_deck happy path + primary error path required before implementation | Claude | Queued | MS-011 ✅ (ADR-013 accepted) |

---

## Done

| ID | Item | Evidence |
|----|------|---------|
| MS-010 | Audit `.slide-body`-only CSS gaps in HTMLRenderer | Audit complete: 15 selectors (list hierarchy, paragraphs, tables) gap inside `.column` elements; MS-014 queued — 2026-05-17 |
| MS-009 | Add version constant to build.sc; wire `--version` flag | `build.sc` `mdSlidesVersion="1.0.1"` + `BuildInfo.scala` generated + `Main.scala` updated — commit ead6d25 — 2026-05-17 |
| MS-011 | MCP capability surface spike | `doc/internal/governance/adr/ADR-013-mcp-server-architecture.md` — file-in/file-out architecture; 4-tool surface (`render_deck`, `validate_deck`, `list_themes`, `get_deck_info`); MS-012 queued for implementation — 2026-05-17 |
| MS-008 | Update CHANGELOG, bump to v1.0.1, cut proper release | `CHANGELOG.md` + `build.sc` — v1.0.1 release at https://github.com/TJMSolns/MD-Slides/releases/tag/v1.0.1 — 2026-05-16 |
| MS-007 | Fix PathResolverSpec pre-existing failure | `PathResolver.scala` error message now lists `.md` variant explicitly; test passing — 2026-05-16 |
| MS-006 | Write regression tests: HTMLRendererTwoColumnSpec (heading slot, column CSS) | `infrastructure/test/.../HTMLRendererTwoColumnSpec.scala` — 11 tests all passing — 2026-05-16 |
| MS-005 | Bug fix: two-column slides not rendering headings — parser/renderer slot contract mismatch | `MarkdownParser.scala` + `HTMLRenderer.scala` — commit 115a3ba — 2026-05-16 |
| MS-004 | Rewrite feature-tour.md: 45 slides, clean two-column convention, background demo, bug-free | `examples/feature-tour.md` — commit 50c4aa7; v1.0.0 release asset updated — 2026-05-16 |
| MS-003 | Public release: create MD-Slides repo, sanitize, write README/CONTRIBUTING/LICENSE/CI | https://github.com/TJMSolns/MD-Slides — public, v1.0.0, MIT — 2026-05-15 |
| MS-002 | Install GitHub App on repo | Consolidated to WQ-P4-023 (org WQ) — 2026-05-15 |
| — | Initial harness applied | CLAUDE.md, docs/agents/, .claude/ created 2026-05-07 |

---

## Notes

mdslides (private planning root) → MD-Slides (https://github.com/TJMSolns/MD-Slides) is the public repo.
New feature work should be tracked here and implemented in MD-Slides.
MS-001 (fs2/CE spike) remains queued for the public repo once CI is confirmed green.
