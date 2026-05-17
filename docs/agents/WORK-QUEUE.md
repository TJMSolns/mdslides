# mdslides — Work Queue

P4 priority. Work items are driven by feature requests, bugs, and maintenance needs.

---

## Active

| ID | Item | Owner | Status | Depends On |
|----|------|-------|--------|-----------|
| MS-009 | Add explicit version declaration to build.sc; wire into `--version` CLI flag | Claude | Queued | — |
| MS-010 | Audit all remaining `.slide-body`-only CSS selectors in HTMLRenderer — find any other `.column` coverage gaps (e.g., code block styling, blockquotes, inline formatting) | Claude | Queued | — |
| MS-001 | Spike: investigate I/O performance improvements via deeper Cats Effect / fs2 usage (streaming file I/O, parallel rendering pipeline) and Calico for terminal UI — Calico applicability to JVM CLI needs validation (primarily Scala.js); CE/fs2 JVM applicability is not in question | Claude | Queued | — |

---

## Done

| ID | Item | Evidence |
|----|------|---------|
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
