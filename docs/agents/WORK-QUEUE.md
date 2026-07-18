# mdslides — Work Queue

P4 priority. Work items are driven by feature requests, bugs, and maintenance needs.

*Last groomed: 2026-07-18 (GL-031 — org-wide `/groom-all` full sweep, Wave B).*
*Previous: "GL-031" (2026-07-11, targeted single-project groom) — MS-018/MS-019 evidence + v1.0.7
release + MS-017's Pre-Implementation Gate all independently reconfirmed real that session (see
HL-023–HL-025 and prior header text in file history for detail).*

> **GL-ID COLLISION (flag only, not resolved here):** this project's own header already used the
> label "GL-031" for the 2026-07-11 targeted single-project groom above. That session's use of
> GL-031 was never recorded in the org root's `docs/agents/GROOM-LOG.md` (whose last full-sweep
> entry before this run was GL-030, 2026-07-10) — so this run's org-assigned GL-031 for the full
> `/groom-all` sweep collides with a label already spent on a different, narrower session. Not
> resolved unilaterally here (mirrors the SEQUENCE-NEEDED/PROPAGATION-STALE "surface, don't
> auto-resolve" convention) — flagged for Tony/orchestrator to reconcile the org GL-numbering
> sequence. Flagged 2026-07-18.

**This run's findings and changes:**
1. **DN-005/DEFER-001 expiry (2026-07-18, today) did NOT lapse — it was already resolved 2 days
   early.** `git status` reconfirmed 0 uncommitted files this run (working tree fully clean, level
   with `origin/main`). `docs/agents/GIT-DURABILITY-DEFER.md` shows DEFER-001 closed 2026-07-16 ("50
   documentation files committed and pushed; working tree clean... Closed because the debt is gone,
   not because a date passed"). The "working-tree backlog... expires 2026-07-18" language in the
   prior header paragraph above is now stale/superseded — corrected here, not duplicated as a new
   escalation since there is nothing outstanding to escalate.
2. **MS-020 promoted `[PROPOSED]` → `Queued`.** Its org source, WQ-P4-092 (mdslides theme/asset
   rename), is itself `Queued`/unblocked (`Depends On: —`) in the org `WORK-QUEUE.md` as of this
   run, so nothing blocks MS-020 from proceeding. This breaks the 35-consecutive-no-op-session stall
   recorded in HL-026 through HL-060 (queue had zero rows with an eligible `Queued`/`Ready`/`In
   Progress` status). Implementation still requires its own Pre-Implementation Gate per project
   convention (MS-012/017/019 precedent) — promotion only makes it eligible for `/next` to pick up.
3. **MS-021 reclassified `[PROPOSED]` → `Blocked`** (not promoted). Its org source, WQ-P4-094
   (mdslides package rename), has `Depends On: WQ-P4-093` in the org queue, and WQ-P4-093
   (project-template package rename) is still `Queued`, not Done, as of this run. `Depends On`
   column corrected from `—` to `WQ-P4-093 (org)`. **Also corrected a real content-staleness bug
   while touching this row:** MS-021's description still cited the superseded target namespace
   `com.tjmsolutions.*`; WQ-P4-094 was revised per POL-020/DR-029 to the authoritative
   `solutions.tjm.mdslides.*` (reverse-DNS of `tjm.solutions`) and its scope was expanded
   (WQ-P4-136 scan) to cover 117 app-module files beyond the original 4 mill plugins — MS-021's text
   updated to match, so a future executor doesn't work off a stale, under-scoped description. No
   SEQUENCE-NEEDED tie created by this run — MS-020 is the sole Claude-owned Queued/unblocked item
   after these changes.
4. **Done-item evidence spot-checked** — `docs/agents/evidence/{MS-017,MS-018,MS-019,MS-023}.md`,
   `docs/spikes/spike-MS-001-fs2-ce-performance.md`, and
   `doc/internal/planning/design-{MS-012-mcp-server,MS-017-typed-slot-name,MS-019-mcp-server-tier2}.md`
   all confirmed present on disk. No evidence-missing Done items found; no reversions needed.
5. **Unchanged, out of this run's scope:** the 4 PROPAGATION-STALE flags below (flagged 2026-07-15 —
   resolving them is the org-orchestrator's Step 2b hash-diff check, not this per-project pass); the
   historical MS-017/018/019 SEQUENCE-NEEDED tie (flagged GL-030, fully resolved 2026-07-11 — all 3
   Done, see "Sequenced" line below `## Active`); active-idle status per POL-018 DR-027 unchanged.

> **PROPAGATION-STALE:** `.claude/hooks/stop-git-durability-gate.py` differs from harness-evolution's current copy — review (copy over, or merge if this project has real local customization) as the first action next time this project is touched. Flagged 2026-07-15 (harness-evolution HE-057 mechanism, applied directly this session per Tony's instruction — not a full /groom-all run).
> **PROPAGATION-STALE:** `.claude/skills/next/SKILL.md` differs from harness-evolution's current copy — review (copy over, or merge if this project has real local customization) as the first action next time this project is touched. Flagged 2026-07-15 (harness-evolution HE-057 mechanism, applied directly this session per Tony's instruction — not a full /groom-all run).
> **PROPAGATION-STALE:** `.claude/skills/handoff/SKILL.md` differs from harness-evolution's current copy — review (copy over, or merge if this project has real local customization) as the first action next time this project is touched. Flagged 2026-07-15 (harness-evolution HE-057 mechanism, applied directly this session per Tony's instruction — not a full /groom-all run).
> **PROPAGATION-STALE:** `.claude/settings.json` differs from harness-evolution's current copy — review (copy over, or merge if this project has real local customization) as the first action next time this project is touched. Flagged 2026-07-15 (harness-evolution HE-057 mechanism, applied directly this session per Tony's instruction — not a full /groom-all run).

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

**Sequenced (Tony, GL-030, 2026-07-10):** MS-018 → MS-019 → MS-017 (Bayesian-nudge rationale: MS-018 directly feeds open-agentic-platform's WQ-179 — nudged up; MS-019 completes the MCP surface, a stated precondition for "any new MCP work" — nudged up moderately; MS-017 is a valuable, self-contained bug-class fix with nothing else named depending on it). MS-018 (1 of 3) done 2026-07-10; MS-019 (2 of 3) done 2026-07-11; MS-017 (3 of 3) done 2026-07-11 — see `## Done` below. MS-023 (the Done-gate hook bug MS-017's own closing attempt discovered) also resolved 2026-07-11 by propagating harness-evolution's already-verified fix.

| MS-020 | **P4 MAINTENANCE** [⬆ WQ-P4-092 (org)] — Execute theme/asset rename: update any `retisio`-keyed theme identifiers, asset filenames, and references in mdslides source and examples to `tjmsolutions`-prefixed equivalents. Run `mill __.compile && mill __.test` to validate; bump PATCH version if any CLI-visible strings change. Source: HL-060 / WQ-P4-092 deferred to mdslides session. **[Promoted Queued — GL-031, 2026-07-18: org source WQ-P4-092 confirmed Queued/unblocked, `Depends On: —`.]** | Claude | Queued | — |
| MS-021 | **P4 MAINTENANCE** [⬆ WQ-P4-094 (org)] — Execute package rename: replace `com.retisio.*` (mill plugins) **and** `com.tjmsolutions.mdslides.*` (117 app-module source/test files across domain/cli/infrastructure/mcp + `build.sc` + coupled docs) with **`solutions.tjm.mdslides.*`** — target corrected GL-031 (2026-07-18) to match WQ-P4-094's current, POL-020/DR-029-revised text (this row previously cited the superseded `com.tjmsolutions.*` form). Validate with `mill __.compile && mill __.test` (full 300+-test suite); update CHANGELOG; bump MINOR version (breaking package API); update coupled docs (CLAUDE.md build command, README, ADR-009/012, PDR-014, POL-001 examples) in the same pass per WQ-P4-094. Source: WQ-P4-094 deferred to mdslides session. **[Blocked — GL-031, 2026-07-18: org source WQ-P4-094 itself depends on WQ-P4-093 (project-template package rename), still Queued/not Done.]** | Claude | Blocked | WQ-P4-093 (org) |
| ~~[PROPOSED] MS-022~~ | ~~**P4 GOVERNANCE** — Record active-idle dormancy status decision.~~ | ~~Claude~~ | ~~done~~ | ~~—~~ |

---

## Done

| ID | Item | Evidence |
|----|------|---------|
| MS-017 | **[Sequence: 3 of 3 — GL-030]** **P4** — Introduce typed slot name ADT and enforce via the parser-renderer interface. Eliminates the class of silent slot-name mismatch bugs (LL-003). Source: retro-all RL-002. `enum SlotName` replaces the dead `opaque type`; `SlotDefinition`/`Slide`/`Template`/`MarkdownParser`/`HTMLRenderer`/`SpeakerViewRenderer` retyped to `SlotName`; frontmatter keys (`header`/`footer`/`vertical-align`) left as raw `String` per approved scope (Q2a); `mill __.compile`/`mill __.test` clean (687/687). Independent verifier PASS (sonnet tier) — round 1 VETO'd a real disclosure-completeness gap (2 of 6 extra-touched test files undercounted, `SlotDefinition`'s val-count miscounted), fixed with no code change; round 2 independently re-derived the full touched-file set from `git show --stat` and got an exact match, PASS. Was held out of `## Done` by MS-023 (this project's own Done-gate hook couldn't corroborate the transcript) — now resolved. | Pre-Implementation Gate `doc/internal/planning/design-MS-017-typed-slot-name.md` (Approved, Tony, 2026-07-11 — "approved as written"); commits `a40c4db`, `697178b`, `1abd734`; evidence `docs/agents/evidence/MS-017.md` (Verifier-verdict: PASS, sonnet tier) |
| MS-023 | **BUG** — `pretooluse-done-gate.py`'s DN-006 transcript-corroboration check couldn't recognize a `{"type":"queue-operation",...,"content":"<task-notification>..."}` transcript entry (no top-level `"message"` key) — the shape a nested subagent dispatch's own verifier notification arrives as in this execution environment, discovered live while trying to close MS-017. Same root defect as harness-evolution's own HE-043 (and the repo-scope leak HE-042); already fixed and independently verified there (HE-042: haiku PASS; HE-043: sonnet PASS, empirically confirmed against 828 real production `queue-operation` notifications machine-wide, 516 invisible under the pre-fix code). Resolved here by propagating that already-fixed, byte-identical file into mdslides' own `.claude/hooks/pretooluse-done-gate.py` — independently re-verified (opus tier) that the propagation itself (not the underlying fix a second time) was faithful: byte-identical, syntactically valid, and behaves correctly within mdslides' own repo scope. This same Done-transition (MS-017, immediately above) is the real end-to-end proof the fix works. | `docs/agents/evidence/MS-023.md` (Verifier-verdict: PASS, opus tier); harness-evolution commit `b76f2d5` (origin of the fix); mdslides commit `c44a889` (propagation) |
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
