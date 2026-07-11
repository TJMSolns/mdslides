# mdslides — Work Queue

P4 priority. Work items are driven by feature requests, bugs, and maintenance needs.

*Last groomed: 2026-07-10 (GL-030; first substantive groom action since GL-029 — MS-017/MS-018/MS-019 three-way tie (unresolved across HL-008 through HL-019, 12 consecutive autonomous sessions) flagged via SEQUENCE-NEEDED marker below per Rule 1/3, not auto-ranked; Done items re-verified, all have evidence; MS-018's `[⬇ WQ-179 (OAP)]` annotation confirmed still accurate; no blocked items, no splits needed; MS-020/MS-021 remain [PROPOSED] and out of the tie-group per checklist scoping; active-idle per POL-018 DR-027 (clock stopped); 52 uncommitted artifacts pending commit — still tracked org-wide by WQ-P4-144/DEFER-001 (expires 2026-07-18), not duplicated here)*

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
| ~~MS-019~~ | ~~**P4** — Implement mdslides MCP server Tier 2: add `list_themes` and `get_deck_info` tools~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |

**Sequenced (Tony, GL-030, 2026-07-10):** MS-018 → MS-019 → MS-017 (Bayesian-nudge rationale: MS-018 directly feeds open-agentic-platform's WQ-179 — nudged up; MS-019 completes the MCP surface, a stated precondition for "any new MCP work" — nudged up moderately; MS-017 is a valuable, self-contained bug-class fix with nothing else named depending on it). MS-018 (1 of 3) done 2026-07-10; MS-019 (2 of 3) done 2026-07-11 — MS-017 (3 of 3) is next in sequence.

| MS-017 | **[Sequence: 3 of 3 — GL-030]** **P4** — Introduce typed slot name ADT (`sealed trait SlotName` with case objects for each slot: `Heading`, `Body`, `Code`, etc.) and enforce via the parser-renderer interface. Eliminates the class of silent slot-name mismatch bugs (LL-003: heading slot extracted but renderer looked for different key — no error, feature just disappeared). Source: retro-all RL-002. | Claude | Queued | — |
| [PROPOSED] MS-020 | **P4 MAINTENANCE** [⬆ WQ-P4-092 (org)] — Execute theme/asset rename: update any `retisio`-keyed theme identifiers, asset filenames, and references in mdslides source and examples to `tjmsolutions`-prefixed equivalents. Run `mill __.compile && mill __.test` to validate; bump PATCH version if any CLI-visible strings change. Source: HL-060 / WQ-P4-092 deferred to mdslides session. | Claude | [PROPOSED] | — |
| [PROPOSED] MS-021 | **P4 MAINTENANCE** [⬆ WQ-P4-094 (org)] — Execute package rename: replace all `com.retisio.*` package declarations and imports with `com.tjmsolutions.*` across the mdslides Mill monorepo (domain, infrastructure, cli, mcp modules). Validate with `mill __.compile && mill __.test`; update CHANGELOG; bump MINOR version (breaking package API). Source: HL-060 / WQ-P4-094 deferred to mdslides session. | Claude | [PROPOSED] | — |
| ~~[PROPOSED] MS-022~~ | ~~**P4 GOVERNANCE** — Record active-idle dormancy status decision.~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |

---

## Done

| ID | Item | Evidence |
|----|------|---------|
| MS-019 | Implement MCP server Tier 2 (`list_themes` + `get_deck_info`; completes ADR-013's 4-tool surface) | Pre-scaffold gate `doc/internal/planning/design-MS-019-mcp-server-tier2.md`; 2 new tool files + McpServer/McpModels wiring; 8 new integration tests (18/18 `McpServerSpec` total); evidence `docs/agents/evidence/MS-019.md` (Verifier-verdict: PASS, opus tier); commits 06f3b5b (implementation) + 6c4fc82 (evidence PENDING checkpoint) — 2026-07-11; release v1.0.7 at https://github.com/TJMSolns/MD-Slides/releases/tag/v1.0.7 |
| MS-018 | LMS investigation spike — two-axis feature analysis (standalone-useful vs. LMS-only), quiz worked example | `docs/spikes/spike-MS-018-lms-investigation.md`; evidence `docs/agents/evidence/MS-018.md` (Verifier-verdict: PASS, haiku tier); commits 1fca48a (spike) + f02dd27 (evidence PENDING checkpoint) — 2026-07-10; feeds WQ-179 (OAP, advisory) |
| MS-022 | Record active-idle dormancy status decision | Dormancy decision landed via WQ-P4-121 Part 1 / HL-064 (2026-05-28); CONTEXT-KERNEL Status section updated; classified `active-idle` per POL-018 (DR-027); dormancy clock reset to 2026-05-28 — no separate PDR needed |
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
MS-001 (fs2/CE spike) is Done — CI is green as of v1.0.4.
MS-018 (LMS spike) feeds WQ-179 (OAP) as an advisory input — not blocking either item.
