# mdslides — Work Queue

P4 priority. Work items are driven by feature requests, bugs, and maintenance needs.

---

## Active

| ID | Item | Owner | Status | Depends On |
|----|------|-------|--------|-----------|
| ~~MS-016~~ | ~~**BUG** — 5 infrastructure tests pre-existing failures~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |
| ~~MS-015~~ | ~~**BUG** — 20 domain tests pre-existing failures~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |
| ~~MS-013~~ | ~~**BUG** — PathResolverSpec `findInputFile - error when not found` failing~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |
| ~~MS-009~~ | ~~Add explicit version declaration to build.sc; wire into `--version` CLI flag~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |
| ~~MS-010~~ | ~~Audit `.slide-body`-only CSS selectors for `.column` coverage gaps~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |
| ~~MS-014~~ | ~~**BUG** — CSS coverage gaps in two-column layout~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |
| ~~MS-001~~ | ~~Spike: investigate I/O performance improvements via fs2/CE + Calico TUI~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |
| ~~MS-011~~ | ~~**P4 SPIKE** — MCP capability surface design~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |
| ~~MS-012~~ | ~~**P4** — Implement mdslides MCP server (Tier 1)~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |

---

## Done

| ID | Item | Evidence |
|----|------|---------|
| MS-001 | Spike: fs2/CE I/O perf + Calico TUI | `spike-MS-001-fs2-ce-performance.md` — IO.parTraverseN covers parallelism; fs2 not needed at mdslides scale; Calico N/A (Scala.js only) — 2026-05-17 |
| MS-012 | Implement MCP server Tier 1 (`render_deck` + `validate_deck`; JSON-RPC 2.0 stdio; stateless) | New `mcp` Mill module; 5 source files; 11 integration tests (McpServerSpec) — commit 029af0b (v1.0.6); release at https://github.com/TJMSolns/MD-Slides/releases/tag/v1.0.6 — 2026-05-17 |
| MS-014 | Fix CSS coverage gaps in two-column layout (lists, paragraphs, tables inside .column) | `HTMLRenderer.scala` all `.slide-body X` selectors duplicated to `.column X`; `HTMLRendererTwoColumnSpec` (12 tests) added — commit c3ec86e (v1.0.5); release at https://github.com/TJMSolns/MD-Slides/releases/tag/v1.0.5 — 2026-05-17 |
| MS-016 | Fix 5 infrastructure test failures (multi-slide active class, per-slide background path rewriting, corporate theme image, Mermaid fallback class) | Tests in HTMLRendererSpec, HTMLRendererTemplateV2Spec, ThemeJsonAdapterSpec updated — commit ecb377f (v1.0.4); release at https://github.com/TJMSolns/MD-Slides/releases/tag/v1.0.4 — 2026-05-17 |
| MS-015 | Fix 20 domain test failures (HeaderFooter placeholder format + density ContentError→DensityWarning alignment) | `HeaderFooter.scala` placeholder format corrected ({single} not {{double}}); SlideSpec/ContentSlideSpec/properties updated to DensityWarning assertions — commit (v1.0.3); release at https://github.com/TJMSolns/MD-Slides/releases/tag/v1.0.3 — 2026-05-17 |
| MS-013 | PathResolverSpec findInputFile error-when-not-found test fix | `PathResolver.scala` error message now includes `$deckName.md` as substring — commit (v1.0.2); release at https://github.com/TJMSolns/MD-Slides/releases/tag/v1.0.2 — 2026-05-17 |
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
