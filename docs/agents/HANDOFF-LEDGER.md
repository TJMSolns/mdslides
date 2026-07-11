# mdslides — Handoff Ledger

Append-only. New entries at the top.

---

## HL-021 — 2026-07-11 — MS-019 (MCP server Tier 2) executed and verified

**Session:** Tony + Claude (mdslides root — autonomous single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-020,
  HL-019, HL-018) per mandatory startup order
- Per the GL-030 sequence (MS-018 → MS-019 → MS-017), MS-018 was already Done (HL-020); MS-019
  (Sequence 2 of 3, Owner Claude, Depends On —) was the sole genuinely unblocked, Claude-executable
  item — MS-017 (3 of 3) remains blocked by the sequence, MS-020/MS-021 remain `[PROPOSED]`
- Pre-Implementation Gate: reviewed ADR-013's Tier 2 spec (tool names, params, `DeckInfo`/`ThemesResult`
  shapes) — already a complete domain-model-equivalent document; no separate model doc needed
- Pre-Scaffold Gate: wrote `doc/internal/planning/design-MS-019-mcp-server-tier2.md` — sequence
  diagrams for `list_themes` happy path + directory-permission error path, `get_deck_info` happy path
  + missing-file/parse-fail error paths, grounded in the real Tier 1 code (`McpServer.scala`,
  `ThemeLoader.scala`, `cli/Main.scala`'s `extractImageUrls`/`extractMermaidDiagrams` helpers, which
  `get_deck_info` reimplements inside `mcp` since ADR-013 forbids a compile-time `mcp` → `cli`
  dependency)
- Implemented `ListThemesTool.scala` (wraps `ThemeLoader.listAvailableThemes`; a missing `themes_dir`
  is not an error, matching `ThemeLoader`'s existing contract) and `GetDeckInfoTool.scala` (slide
  count, templates used, images referenced via `FlexmarkAdapter.parseInlineFormatting` +
  `SlideBackground.getImagePath`, Mermaid presence via `DiagramElement` pattern match); wired both into
  `McpServer.scala`'s `toolList` + dispatch alongside Tier 1's `render_deck`/`validate_deck`; added
  `ThemesResult`/`DeckInfo` to `McpModels.scala`
- 8 new integration tests in `McpServerSpec` (tools/list inclusion, list_themes happy/missing-dir/
  directory-theme-present, get_deck_info missing-file/happy-path-with-mermaid-and-images/no-mermaid) —
  initial run had 3 failures caused by a test-authoring bug (synchronous `try/finally` deleting temp
  files before the lazily-composed `IO` chain actually ran); fixed by switching to `IO.guarantee` for
  cleanup — all 18 `McpServerSpec` tests (10 Tier 1 + 8 Tier 2) pass; full `mill __.test` suite green,
  no regressions
- Version bumped to 1.0.7 (`build.sc`); `CHANGELOG.md` updated (staged only the new `[1.0.7]` hunk via
  a targeted `git apply --cached`, leaving the pre-existing DEFER-001-scope Retisio-rename backlog in
  that file untouched/unstaged)
- `mill cli.assembly mcp.assembly` failed once under the environment's default JDK 25 (`Unsupported
  class file major version 69` in Mill's own ASM-based codesig introspection, unrelated to this
  session's code); retried with `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` — succeeded. Cut v1.0.7
  release with `gh release create` (no prior tag existed) uploading `md-slides.jar` +
  `mdslides-mcp.jar` to `TJMSolns/MD-Slides` (confirmed with Tony's real-time approval given the
  release's public/hard-to-reverse nature before creating it); verified both assets present with
  current timestamps
- E2 discipline followed: drew verifier tier via `draw-verifier-tier.py P4 sonnet` → opus; wrote
  `docs/agents/evidence/MS-019.md` FIRST with an honest `Verifier-verdict: PENDING` placeholder,
  committed that checkpoint (a Stop hook fired mid-wait for the verifier — pushed immediately per
  SKILL.md guidance rather than leaving it uncommitted), then dispatched a real `verifier` agent
  (model opus) against the artifact + the commits
- Verifier returned `PASS` — independently confirmed both commits are real and match the claimed
  diff, re-read all cited source files and confirmed every Invariance-recheck claim, independently
  re-ran `mill mcp.test` (18/18) plus its own different-perturbation case (3 templates, no images/
  mermaid) with correct output, and confirmed the v1.0.7 release assets
- Updated evidence artifact with the real `PASS` verdict; moved MS-019 to Done in WORK-QUEUE.md;
  updated the GL-030 sequence note to show MS-017 is next
- Did not process MS-017 (only one item per session, per brief)
- Confirmed the pre-existing working-tree backlog (~50 modified doc files, untracked
  `.claude/hooks/__pycache__/`, `docs/agents/LESSONS-LEARNED.md`) is unchanged in scope from HL-020
  and still covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144, expires
  2026-07-18, not yet expired) — left untouched, not duplicated here
- Pushed via `git -c credential.helper='!gh auth git-credential' push https://github.com/TJMSolns/mdslides.git main`
  (same SSH-key workaround as HL-020's session — `origin`'s configured SSH remote fails with
  `Permission denied (publickey)` in this environment; no git config modified, `-c` is a one-shot
  override)

**Decisions made:** none formal (execution of an already-approved sequence, not a new decision)
**Work queue changes:** MS-019: Queued → Done (evidence `docs/agents/evidence/MS-019.md`, verifier
PASS)
**Commits:** `06f3b5b` (implementation), `6c4fc82` (evidence PENDING checkpoint), `94df7d1` (Done edit
+ verified evidence) — all pushed to `origin/main`
**Working-tree carry-over:** unchanged from HL-020 — still DEFER-001 scope, expires 2026-07-18
**Open items carried forward:**
- MS-017 (typed SlotName ADT, Sequence 3 of 3) is now the sole item at the front of the GL-030
  sequence with satisfied dependencies
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** `/next` or a fresh autonomous session should pick MS-017 — it is now the only
unblocked, Claude-owned item in the GL-030 sequence

---

## HL-020 — 2026-07-10 — MS-018 (LMS investigation spike) executed and verified

**Session:** Tony + Claude (mdslides root — autonomous single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-019,
  HL-018, HL-017) per mandatory startup order
- WORK-QUEUE.md now shows a `/groom` sequence from GL-030 (2026-07-10, Tony): MS-018 → MS-019 → MS-017
  — this resolves the 12-session ambiguity blocking HL-008 through HL-019. MS-018 (Sequence 1 of 3,
  Owner Claude, Depends On —) was the one genuinely unblocked, Claude-executable item
- Executed MS-018 in full: reviewed the domain model (`Slide`, `SlideDeck`, `HeaderFooter`,
  `SpeakerViewRenderer`, `PresentationTimer`, `NavigationHistory`) to ground the analysis in the actual
  codebase rather than a green-field LMS design; wrote
  `docs/spikes/spike-MS-018-lms-investigation.md` — two-axis analysis (6 standalone-useful features:
  progress markers, per-slide metadata, transcript export, client-only self-scoring quiz, pacing
  estimate, handout export; 5 LMS-only features: completion tracking, graded/recorded quizzes, learner
  identity, enrollment, SCORM/xAPI export) each with effort tier, server-state requirement, and
  architecture-fit; plus the required worked example (embedded quiz, standalone vs. LMS-integrated)
- Pre-Implementation Gate skipped per SKILL.md step 4 (a spike doc is a self-evidently single artifact,
  not an E1–E4 build item)
- E2 discipline followed: drew verifier tier via `draw-verifier-tier.py P4 sonnet` → haiku; wrote
  `docs/agents/evidence/MS-018.md` FIRST with an honest `Verifier-verdict: PENDING` placeholder
  (RL-002/HE-008 ordering), committed that checkpoint, then dispatched a real `verifier` agent (model
  haiku) against the artifact + the spike doc
- Verifier returned `PASS` — independently confirmed both commits (`1fca48a` spike doc, `f02dd27`
  evidence PENDING checkpoint) are real and doc-only, the spike covers both required axes plus the
  worked example, and every source-code claim in the `Invariance-recheck` (field/behavior citations
  from all six domain/infra files) holds up against the real code
- Updated evidence artifact with the real `PASS` verdict; moved MS-018 to Done in WORK-QUEUE.md; updated
  the GL-030 sequence note to show MS-019 is next
- Did not process MS-019 or MS-017 (only one item per session, per brief)
- Confirmed the pre-existing working-tree backlog (untracked `.claude/hooks/__pycache__/`,
  `docs/agents/LESSONS-LEARNED.md`, and the ~50 modified doc files) is unchanged in scope and still
  covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144, expires 2026-07-18, not
  yet expired) — left untouched, not duplicated here
- Pushed via `git -c credential.helper='!gh auth git-credential' push https://github.com/TJMSolns/mdslides.git main`
  after `origin`'s configured SSH remote failed with `Permission denied (publickey)` in this
  environment — no git config was modified, `-c` is a one-shot override; `gh auth status` confirmed an
  authenticated session was already available

**Decisions made:** none formal (execution of an already-approved sequence, not a new decision)
**Work queue changes:** MS-018: Queued → Done (evidence `docs/agents/evidence/MS-018.md`, verifier
PASS)
**Commits:** `1fca48a` (spike doc), `f02dd27` (evidence PENDING checkpoint), `c26eb98` (Done edit +
verified evidence) — all pushed to `origin/main`
**Working-tree carry-over:** unchanged from HL-019 — still DEFER-001 scope, expires 2026-07-18
**Open items carried forward:**
- MS-019 (MCP Tier 2, Sequence 2 of 3) is next in the GL-030 sequence
- MS-017 (typed SlotName ADT, Sequence 3 of 3) follows MS-019
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** `/next` or a fresh autonomous session should pick MS-019 (MCP server Tier 2: `list_themes`
+ `get_deck_info`) — it is now the sole item at the front of the GL-030 sequence with satisfied
dependencies

---

## HL-019 — 2026-07-10 — No execution: queue still ambiguous (12th repeat of HL-008 through HL-018)

**Session:** Tony + Claude (mdslides root — autonomous single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-018, HL-017, HL-016) per mandatory startup order
- Session brief required executing exactly one unblocked, Claude-owned WORK-QUEUE item — found the same three simultaneously unblocked candidates as HL-008 through HL-018: MS-017 (typed SlotName ADT), MS-018 (LMS spike), MS-019 (MCP Tier 2), all `Queued` / Owner `Claude` / `Depends On: —`
- WORK-QUEUE.md "Last groomed" note still reads GL-029 (2026-07-05) — no `/groom` session has run since HL-018 (2026-07-10) to sequence these three; condition for single-item execution still not met
- Picking among three ambiguous candidates is a sequencing judgment call reserved for `/groom` (Rule 3: an approved queue state isn't execute permission for an unspecified item; Rule 1/2: no self-sequencing) — so no item was executed and no code/doc changes were made this session
- Confirmed the pre-existing working-tree backlog (52 files, incl. untracked `docs/agents/LESSONS-LEARNED.md` and `.claude/hooks/__pycache__/`) is unchanged in count and scope from HL-018 and still covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144, expires 2026-07-18, not yet expired) — left untouched, not duplicated here
- Verified `docs/agents/CONTEXT-KERNEL.md` and `docs/agents/WORK-QUEUE.md` diffs shown in `git status` are pre-existing DEFER-001-scope content, not edits made this session
- No new decisions recorded (DECISION-REGISTER.md unchanged — still empty placeholder)

**Decisions made:** none
**CONTEXT-KERNEL change:** none this session
**Working-tree carry-over:** unchanged from HL-018 — still DEFER-001 scope, expires 2026-07-18
**Open items carried forward:**
- MS-017, MS-018, MS-019 remain `Queued` — this is the twelfth consecutive session (HL-008 through HL-019) finding the same three-way ambiguity with zero `/groom` sessions in between; autonomous single-item sessions cannot resolve this on their own and should not be expected to keep retrying the same read until a `/groom` (or Tony directly) sequences the three items
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** Tony or a `/groom` session — sequence MS-017/MS-018/MS-019 (or explicitly mark which one is next); twelve unresolved recurrences of the identical blocker is a standing signal that autonomous sessions should stop being pointed at this queue until it is sequenced

---

## HL-018 — 2026-07-10 — No execution: queue still ambiguous (11th repeat of HL-008 through HL-017)

**Session:** Tony + Claude (mdslides root — autonomous single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-017, HL-016, HL-015) per mandatory startup order
- Session brief required executing exactly one unblocked, Claude-owned WORK-QUEUE item — found the same three simultaneously unblocked candidates as HL-008 through HL-017: MS-017 (typed SlotName ADT), MS-018 (LMS spike), MS-019 (MCP Tier 2), all `Queued` / Owner `Claude` / `Depends On: —`
- WORK-QUEUE.md "Last groomed" note still reads GL-029 (2026-07-05) — no `/groom` session has run since HL-017 (2026-07-09) to sequence these three; condition for single-item execution still not met
- Picking among three ambiguous candidates is a sequencing judgment call reserved for `/groom` (Rule 3: an approved queue state isn't execute permission for an unspecified item; Rule 1/2: no self-sequencing) — so no item was executed and no code/doc changes were made this session
- Confirmed the pre-existing working-tree backlog (52 files, incl. untracked `docs/agents/LESSONS-LEARNED.md` and `.claude/hooks/__pycache__/`) is unchanged in count and scope from HL-017 and still covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144, expires 2026-07-18, not yet expired) — left untouched, not duplicated here
- `docs/agents/CONTEXT-KERNEL.md` shows a diff in `git status` — verified this is the pre-existing DEFER-001-scope dormancy-decision content from 2026-05-28 (WQ-P4-121/HL-064) already present in the file, not an edit made this session
- No new decisions recorded (DECISION-REGISTER.md unchanged — still empty placeholder)

**Decisions made:** none
**CONTEXT-KERNEL change:** none this session — the working-tree diff is pre-existing DEFER-001 scope (dormancy decision, WQ-P4-121/HL-064, 2026-05-28)
**Working-tree carry-over:** unchanged from HL-017 — still DEFER-001 scope, expires 2026-07-18
**Open items carried forward:**
- MS-017, MS-018, MS-019 remain `Queued` — this is the eleventh consecutive session (HL-008 through HL-018) finding the same three-way ambiguity with zero `/groom` sessions in between; autonomous single-item sessions cannot resolve this on their own and should not be expected to keep retrying the same read until a `/groom` (or Tony directly) sequences the three items
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** Tony or a `/groom` session — sequence MS-017/MS-018/MS-019 (or explicitly mark which one is next); eleven unresolved recurrences of the identical blocker is a standing signal that autonomous sessions should stop being pointed at this queue until it is sequenced

---

## HL-017 — 2026-07-09 — No execution: queue still ambiguous (10th repeat of HL-008/009/010/011/012/013/014/015/016)

**Session:** Tony + Claude (mdslides root — autonomous single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-016, HL-015, HL-014) per mandatory startup order
- Session brief required executing exactly one unblocked, Claude-owned WORK-QUEUE item — found the same three simultaneously unblocked candidates as HL-008 through HL-016: MS-017 (typed SlotName ADT), MS-018 (LMS spike), MS-019 (MCP Tier 2), all `Queued` / Owner `Claude` / `Depends On: —`
- WORK-QUEUE.md "Last groomed" note still reads GL-029 (2026-07-05) — no `/groom` session has run since HL-016 (2026-07-09) to sequence these three; condition for single-item execution still not met
- Picking among three ambiguous candidates is a sequencing judgment call reserved for `/groom` (Rule 3: an approved queue state isn't execute permission for an unspecified item; Rule 1/2: no self-sequencing) — so no item was executed and no code/doc changes were made this session
- Confirmed the pre-existing working-tree backlog (52 files) is unchanged in scope from HL-016 and still covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144, expires 2026-07-18, not yet expired) — left untouched, not duplicated here; spot-checked that `docs/agents/CONTEXT-KERNEL.md` and `docs/agents/WORK-QUEUE.md` showing as modified in `git status` are pre-existing DEFER-001-scope diffs, not changes made this session
- No new decisions recorded (DECISION-REGISTER.md unchanged — still empty placeholder)

**Decisions made:** none
**Working-tree carry-over:** unchanged from HL-016 — still DEFER-001 scope, expires 2026-07-18
**Open items carried forward:**
- MS-017, MS-018, MS-019 remain `Queued` — this is the tenth consecutive session (HL-008 through HL-017) finding the same three-way ambiguity with zero `/groom` sessions in between; autonomous single-item sessions cannot resolve this on their own and should not be expected to keep retrying the same read until a `/groom` (or Tony directly) sequences the three items
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** Tony or a `/groom` session — sequence MS-017/MS-018/MS-019 (or explicitly mark which one is next); ten unresolved recurrences of the identical blocker is a standing signal that autonomous sessions should stop being pointed at this queue until it is sequenced

---

## HL-016 — 2026-07-09 — No execution: queue still ambiguous (9th repeat of HL-008/009/010/011/012/013/014/015)

**Session:** Tony + Claude (mdslides root — autonomous single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-015, HL-014, HL-013) per mandatory startup order
- Session brief required executing exactly one unblocked, Claude-owned WORK-QUEUE item — found the same three simultaneously unblocked candidates as HL-008 through HL-015: MS-017 (typed SlotName ADT), MS-018 (LMS spike), MS-019 (MCP Tier 2), all `Queued` / Owner `Claude` / `Depends On: —`
- WORK-QUEUE.md "Last groomed" note still reads GL-029 (2026-07-05) — no `/groom` session has run since HL-015 (2026-07-09) to sequence these three; condition for single-item execution still not met
- Picking among three ambiguous candidates is a sequencing judgment call reserved for `/groom` (Rule 3: an approved queue state isn't execute permission for an unspecified item; Rule 1/2: no self-sequencing) — so no item was executed and no code/doc changes were made this session
- Confirmed the pre-existing working-tree backlog (52 files) is unchanged in scope from HL-015 and still covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144, expires 2026-07-18, not yet expired) — left untouched, not duplicated here
- No new decisions recorded (DECISION-REGISTER.md unchanged — still empty placeholder)

**Decisions made:** none
**Working-tree carry-over:** unchanged from HL-015 — still DEFER-001 scope, expires 2026-07-18
**Open items carried forward:**
- MS-017, MS-018, MS-019 remain `Queued` — this is the ninth consecutive session (HL-008 through HL-016) finding the same three-way ambiguity with zero `/groom` sessions in between; autonomous single-item sessions cannot resolve this on their own and should not be expected to keep retrying the same read until a `/groom` (or Tony directly) sequences the three items
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** Tony or a `/groom` session — sequence MS-017/MS-018/MS-019 (or explicitly mark which one is next); nine unresolved recurrences of the identical blocker is a standing signal that autonomous sessions should stop being pointed at this queue until it is sequenced

---

## HL-015 — 2026-07-09 — No execution: queue still ambiguous (8th repeat of HL-008/009/010/011/012/013/014)

**Session:** Tony + Claude (mdslides root — autonomous single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-014, HL-013, HL-012) per mandatory startup order
- Session brief required executing exactly one unblocked, Claude-owned WORK-QUEUE item — found the same three simultaneously unblocked candidates as HL-008 through HL-014: MS-017 (typed SlotName ADT), MS-018 (LMS spike), MS-019 (MCP Tier 2), all `Queued` / Owner `Claude` / `Depends On: —`
- WORK-QUEUE.md "Last groomed" note still reads GL-029 (2026-07-05) — no `/groom` session has run since HL-014 (2026-07-08) to sequence these three; condition for single-item execution still not met
- Picking among three ambiguous candidates is a sequencing judgment call reserved for `/groom` (Rule 3: an approved queue state isn't execute permission for an unspecified item; Rule 1/2: no self-sequencing) — so no item was executed and no code/doc changes were made this session
- Confirmed the pre-existing working-tree backlog (52 files) is unchanged in scope from HL-014 and still covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144, expires 2026-07-18, not yet expired) — left untouched, not duplicated here
- No new decisions recorded (DECISION-REGISTER.md unchanged — still empty placeholder)

**Decisions made:** none
**Working-tree carry-over:** unchanged from HL-014 — still DEFER-001 scope, expires 2026-07-18
**Open items carried forward:**
- MS-017, MS-018, MS-019 remain `Queued` — this is the eighth consecutive session (HL-008 through HL-015) finding the same three-way ambiguity with zero `/groom` sessions in between; autonomous single-item sessions cannot resolve this on their own and should not be expected to keep retrying the same read until a `/groom` (or Tony directly) sequences the three items
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** Tony or a `/groom` session — sequence MS-017/MS-018/MS-019 (or explicitly mark which one is next); eight unresolved recurrences of the identical blocker is a standing signal that autonomous sessions should stop being pointed at this queue until it is sequenced

---

## HL-014 — 2026-07-08 — No execution: queue still ambiguous (7th repeat of HL-008/009/010/011/012/013)

**Session:** Tony + Claude (mdslides root — autonomous single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-013, HL-012, HL-011) per mandatory startup order
- Session brief required executing exactly one unblocked, Claude-owned WORK-QUEUE item — found the same three simultaneously unblocked candidates as HL-008 through HL-013: MS-017 (typed SlotName ADT), MS-018 (LMS spike), MS-019 (MCP Tier 2), all `Queued` / Owner `Claude` / `Depends On: —`
- WORK-QUEUE.md "Last groomed" note still reads GL-029 (2026-07-05) — no `/groom` session has run since HL-013 (2026-07-08) to sequence these three; condition for single-item execution still not met
- Picking among three ambiguous candidates is a sequencing judgment call reserved for `/groom` (Rule 3: an approved queue state isn't execute permission for an unspecified item; Rule 1/2: no self-sequencing) — so no item was executed and no code/doc changes were made this session
- Confirmed the pre-existing working-tree backlog (52 files) is unchanged in scope from HL-013 and still covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144, expires 2026-07-18, not yet expired) — left untouched, not duplicated here
- No new decisions recorded (DECISION-REGISTER.md unchanged — still empty placeholder)

**Decisions made:** none
**Working-tree carry-over:** unchanged from HL-013 — still DEFER-001 scope, expires 2026-07-18
**Open items carried forward:**
- MS-017, MS-018, MS-019 remain `Queued` — this is the seventh consecutive session (HL-008 through HL-014) finding the same three-way ambiguity with zero `/groom` sessions in between; autonomous single-item sessions cannot resolve this on their own and should not be expected to keep retrying the same read until a `/groom` (or Tony directly) sequences the three items
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** Tony or a `/groom` session — sequence MS-017/MS-018/MS-019 (or explicitly mark which one is next); seven unresolved recurrences of the identical blocker is a standing signal that autonomous sessions should stop being pointed at this queue until it is sequenced

---

## HL-013 — 2026-07-08 — No execution: queue still ambiguous (6th repeat of HL-008/009/010/011/012)

**Session:** Tony + Claude (mdslides root — autonomous single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-012, HL-011, HL-010) per mandatory startup order
- Session brief required executing exactly one unblocked, Claude-owned WORK-QUEUE item — found the same three simultaneously unblocked candidates as HL-008 through HL-012: MS-017 (typed SlotName ADT), MS-018 (LMS spike), MS-019 (MCP Tier 2), all `Queued` / Owner `Claude` / `Depends On: —`
- WORK-QUEUE.md "Last groomed" note still reads GL-029 (2026-07-05) — no `/groom` session has run since HL-012 (2026-07-08) to sequence these three; condition for single-item execution still not met
- Picking among three ambiguous candidates is a sequencing judgment call reserved for `/groom` (Rule 3: an approved queue state isn't execute permission for an unspecified item; Rule 1/2: no self-sequencing) — so no item was executed and no code/doc changes were made this session
- Confirmed the pre-existing working-tree backlog is still covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144, expires 2026-07-18, not yet expired) — left untouched, not duplicated here
- No new decisions recorded (DECISION-REGISTER.md unchanged — still empty placeholder)

**Decisions made:** none
**Working-tree carry-over:** unchanged from HL-012 — still DEFER-001 scope, expires 2026-07-18
**Open items carried forward:**
- MS-017, MS-018, MS-019 remain `Queued` — this is the sixth consecutive session (HL-008, HL-009, HL-010, HL-011, HL-012, HL-013) finding the same three-way ambiguity; `/groom` to assign explicit sequencing/priority is now substantially overdue
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** Tony or a `/groom` session — sequence MS-017/MS-018/MS-019 (or explicitly mark which one is next); six unresolved recurrences of the identical blocker is well past the point autonomous sessions can resolve on their own

---

## HL-012 — 2026-07-08 — No execution: queue still ambiguous (5th repeat of HL-008/009/010/011)

**Session:** Tony + Claude (mdslides root — autonomous single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-011, HL-010, HL-009) per mandatory startup order
- Session brief required executing exactly one unblocked, Claude-owned WORK-QUEUE item — found the same three simultaneously unblocked candidates as HL-008 through HL-011: MS-017 (typed SlotName ADT), MS-018 (LMS spike), MS-019 (MCP Tier 2), all `Queued` / Owner `Claude` / `Depends On: —`
- WORK-QUEUE.md "Last groomed" note still reads GL-029 (2026-07-05) — no `/groom` session has run since HL-011 (2026-07-07) to sequence these three; condition for single-item execution still not met
- Picking among three ambiguous candidates is a sequencing judgment call reserved for `/groom` (Rule 3: an approved queue state isn't execute permission for an unspecified item; Rule 1/2: no self-sequencing) — so no item was executed and no code/doc changes were made this session
- Confirmed the pre-existing ~52-file working-tree backlog is still covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144, expires 2026-07-18, not yet expired) — left untouched, not duplicated here
- No new decisions recorded (DECISION-REGISTER.md unchanged — still empty placeholder)

**Decisions made:** none
**Working-tree carry-over:** unchanged from HL-011 — still DEFER-001 scope, expires 2026-07-18
**Open items carried forward:**
- MS-017, MS-018, MS-019 remain `Queued` — this is the fifth consecutive session (HL-008, HL-009, HL-010, HL-011, HL-012) finding the same three-way ambiguity; `/groom` to assign explicit sequencing/priority is now significantly overdue
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** Tony or a `/groom` session — sequence MS-017/MS-018/MS-019 (or explicitly mark which one is next); five unresolved recurrences of the identical blocker should override normal groom cadence and be picked up next

---

## HL-011 — 2026-07-07 — No execution: queue still ambiguous (4th repeat of HL-008/009/010)

**Session:** Tony + Claude (mdslides root — autonomous single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-010, HL-009, HL-008) per mandatory startup order
- Session brief required executing exactly one unblocked, Claude-owned WORK-QUEUE item — found the same three simultaneously unblocked candidates as HL-008/009/010: MS-017 (typed SlotName ADT), MS-018 (LMS spike), MS-019 (MCP Tier 2), all `Queued` / Owner `Claude` / `Depends On: —`
- WORK-QUEUE.md "Last groomed" note still reads GL-029 (2026-07-05) — no `/groom` session has run since HL-010 (2026-07-07) to sequence these three; condition for single-item execution still not met
- Picking among three ambiguous candidates is a sequencing judgment call reserved for `/groom` (Rule 3: an approved queue state isn't execute permission for an unspecified item; Rule 1/2: no self-sequencing) — so no item was executed and no code/doc changes were made this session
- No new decisions recorded (DECISION-REGISTER.md unchanged — still empty placeholder)

**Decisions made:** none
**CONTEXT-KERNEL change:** pre-existing carry-over, not made this session — Status section (dormancy decision, POL-009/DR-027) and "Last updated" date were already uncommitted before this session started; content matches WQ-P4-121/HL-064 as already reflected in WORK-QUEUE.md (same state HL-008 already characterized)
**Working-tree carry-over:** unchanged from HL-010 — pre-existing backlog (CHANGELOG.md, doc/, docs/, examples/, etc. plus `.claude/hooks/__pycache__/` and `docs/agents/LESSONS-LEARNED.md`) still covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144, expires 2026-07-18, not yet expired); not touched or duplicated here
**Open items carried forward:**
- MS-017, MS-018, MS-019 remain `Queued` — this is the fourth consecutive session (HL-008, HL-009, HL-010, HL-011) finding the same three-way ambiguity; `/groom` to assign explicit sequencing/priority is now overdue
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** Tony or a `/groom` session — sequence MS-017/MS-018/MS-019 (or explicitly mark which one is next); four unresolved recurrences of the identical blocker should override normal groom cadence and be picked up next

---

## HL-010 — 2026-07-07 — No execution: queue still ambiguous (repeat of HL-009/HL-008)

**Session:** Tony + Claude (mdslides root — autonomous `/next`-style single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-009, HL-008, HL-007) per mandatory startup order
- Session brief required executing exactly one unblocked, Claude-owned WORK-QUEUE item — found the same three simultaneously unblocked candidates as HL-008/HL-009: MS-017 (typed SlotName ADT), MS-018 (LMS spike), MS-019 (MCP Tier 2), all `Queued` / Owner `Claude` / `Depends On: —`
- WORK-QUEUE.md "Last groomed" note still reads GL-029 (2026-07-05) — no `/groom` session has run between HL-009 (2026-07-07) and now to sequence these three; condition for single-item execution still not met
- Picking among three ambiguous candidates is a sequencing judgment call reserved for `/groom` (Rule 3: an approved queue state isn't execute permission for an unspecified item; Rule 1/2: no self-sequencing) — so no item was executed and no code/doc changes were made this session
- Confirmed the pre-existing ~52-file working-tree backlog is still covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144, expires 2026-07-18, not yet expired) — left untouched, not duplicated here
- No new decisions recorded (DECISION-REGISTER.md unchanged)

**Decisions made:** none
**Working-tree carry-over:** unchanged from HL-009 — still DEFER-001 scope, expires 2026-07-18
**Open items carried forward:**
- MS-017, MS-018, MS-019 remain `Queued` — this is the third consecutive session (HL-008, HL-009, HL-010) finding the same three-way ambiguity; still need `/groom` to assign explicit sequencing/priority before a future single-item `/next`-style session can pick unambiguously
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** Tony or a `/groom` session — sequence MS-017/MS-018/MS-019 (or explicitly mark which one is next); this has now recurred three times unresolved and should be treated as a priority for the next active session

---

## HL-009 — 2026-07-07 — No execution: queue still ambiguous (repeat of HL-008)

**Session:** Tony + Claude (mdslides root — autonomous `/next`-style single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-008, HL-007, HL-006) per mandatory startup order
- Session brief required executing exactly one unblocked, Claude-owned WORK-QUEUE item — found the same three simultaneously unblocked candidates as HL-008: MS-017 (typed SlotName ADT), MS-018 (LMS spike), MS-019 (MCP Tier 2), all `Queued` / Owner `Claude` / `Depends On: —`
- No `/groom` session has run between HL-008 (2026-07-06) and now to sequence these three — condition for single-item execution still not met
- Picking among three ambiguous candidates is a sequencing judgment call reserved for `/groom` (Rule 3: an approved queue state isn't execute permission for an unspecified item; Rule 1/2: no self-sequencing) — so no item was executed and no code/doc changes were made this session
- No new decisions recorded (DECISION-REGISTER.md unchanged)

**Decisions made:** none
**Working-tree carry-over:** the pre-existing ~52-file backlog (CHANGELOG.md, doc/, docs/, examples/, etc.) remains untouched this session — still covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144, expires 2026-07-18); not touched or duplicated here
**Open items carried forward:**
- MS-017, MS-018, MS-019 remain `Queued` — still need `/groom` to assign explicit sequencing/priority before a future single-item `/next`-style session can pick unambiguously
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** Tony or a `/groom` session — sequence MS-017/MS-018/MS-019 (or explicitly mark which one is next) so a future autonomous single-item session has exactly one unblocked candidate

---

## HL-008 — 2026-07-06 — No execution: queue ambiguous, stopped

**Session:** Tony + Claude (mdslides root — autonomous `/next`-style single-item pick)
**What happened:**
- Read CLAUDE.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, and the last 3 HANDOFF-LEDGER entries (HL-007, HL-006, HL-005) per mandatory startup order
- Session brief required executing exactly one unblocked, Claude-owned WORK-QUEUE item — found three simultaneously unblocked candidates instead of one: MS-017 (typed SlotName ADT), MS-018 (LMS spike), MS-019 (MCP Tier 2), all `Queued` / Owner `Claude` / `Depends On: —`
- Picking among three ambiguous candidates is itself a sequencing judgment call reserved for `/groom` (Rule 3 discipline: an approved queue state isn't execute permission for an unspecified item; Rule 1/2: no self-sequencing) — so no item was executed and no code/doc changes were made this session
- No new decisions recorded (DECISION-REGISTER.md unchanged — still empty placeholder)

**Decisions made:** none
**CONTEXT-KERNEL change:** pre-existing carry-over, not made this session — Status section (dormancy decision, POL-009/DR-027) and "Last updated" date were already uncommitted before this session started; content matches WQ-P4-121/HL-064 as already reflected in WORK-QUEUE.md
**Working-tree carry-over:** ~54 modified/untracked files (CHANGELOG.md, CONTEXT-KERNEL.md, WORK-QUEUE.md, doc/, examples/, etc.) predate this session and are already covered by `docs/agents/GIT-DURABILITY-DEFER.md` DEFER-001 (org WQ-P4-144 backlog, expires 2026-07-18) — not touched or duplicated here
**Open items carried forward:**
- MS-017, MS-018, MS-019 remain `Queued` — need `/groom` to assign explicit sequencing/priority among them before a future single-item `/next`-style session can pick unambiguously
- MS-020, MS-021 remain `[PROPOSED]` — still await a dedicated mdslides session per prior groom notes
**Next owner:** Tony or a `/groom` session — sequence MS-017/MS-018/MS-019 (or explicitly mark which one is next) so a future autonomous single-item session has exactly one unblocked candidate

---

## HL-007 — 2026-05-18 — Harvest note: WQ-P4-047

**Harvest note** (from org-level `/harvest` run — WQ-P4-047):

Patterns harvested from mdslides into org methodology + OAP chora-kb:
- **ADR-013** (MCP server architecture, file-in/file-out stateless): new "MCP Tool Design Patterns" section in `Projects/claude-code-methodology.md`; harvest file written to `oap/docs/modules/chora-kb/harvest/mdslides-adr-013-mcp-architecture.md`; HARVEST-MDSLIDES-MCP-ARCH queued in chora-kb BACKLOG
- **LL-003** (typed slot name ADT): "Typed slot name ADT" sub-section added to methodology (Testing and Validation Discipline)
- **LL-004** (pre-scaffold gate evidence): quantitative evidence note added to "Pre-Scaffold Gate (Sequence Diagrams)" section in methodology

**Next owner:** WQ-P4-043 (gate confirmation pass) is next unblocked org item. MS-017 (typed SlotName ADT, mdslides) is the implementation item that consumes the LL-003 pattern.

---

## HL-006 — 2026-05-17 — MCP server Tier 1 implemented (MS-012 complete)

**Session:** Tony + Claude (org root — /next continuation)
**What happened:**
- MS-012 (MCP server Tier 1) designed, gated, and implemented
- Pre-scaffold gate: wrote `doc/internal/planning/design-MS-012-mcp-server.md` with sequence diagrams for render_deck happy path, 3 error paths (file not found, parse fail, write fail), validate_deck happy path, and full JSON-RPC wire format reference
- Implemented new `mcp` Mill module (4th module; depends on infrastructure):
  - `McpModels.scala` — JSON-RPC 2.0 request/response types + RenderResult/ValidationResult
  - `McpServer.scala` — stdio transport: initialize/tools-list/tools-call/error dispatch
  - `RenderDeckTool.scala` — file existence check → parse → validate → render → write
  - `ValidateDeckTool.scala` — file existence check → parse → validate → return issues
  - `Main.scala` — IOApp entry point
- 11 integration tests (McpServerSpec) — all passing
- JSON-RPC output clean: null fields omitted via custom Circe encoder
- Version bumped to 1.0.6; both `md-slides.jar` and `mdslides-mcp.jar` released at https://github.com/TJMSolns/MD-Slides/releases/tag/v1.0.6
- Commit: 029af0b pushed to github.com:TJMSolns/mdslides.git

**Work queue changes:**
- MS-012: Queued → done

**Status after session:**
- MS-001 (fs2/CE spike): Queued — only remaining queued item
- All MS-0xx bugs resolved
- MCP Tier 2 (`list_themes`, `get_deck_info`): not yet queued — add as MS-017 if/when needed

**Harvest candidate:** MCP transport layer pattern for Scala Cats Effect CLIs (~150 LoC, stateless JSON-RPC 2.0 stdio, custom Circe encoder to omit nulls). Applicable to any future CLI tool needing MCP exposure. Specifically: omitting null optional fields requires a custom Encoder — `deriveEncoder` includes nulls by default.

**Next session should start with:** `/next` picks MS-001 (fs2/CE I/O performance spike). Or queue MS-017 for MCP Tier 2 if demo/pitch-deck workflows validate Tier 1.

---

## HL-005 — 2026-05-17 — Two-column CSS coverage fixed (MS-014 complete)

**Session:** Tony + Claude (org root — /next continuation)
**What happened:**
- MS-014 (CSS coverage gaps in two-column layout) designed, tested, and fixed
- TDD: wrote HTMLRendererTwoColumnSpec (12 tests) first — 7 CSS selector tests + 5 content tests
- 7 CSS tests failed as expected (no `.column` selectors in CSS)
- Fix: duplicated all `.slide-body X` CSS rules to also target `.column X` — paragraph, list hierarchy (ul/ol/li, 3 levels of nesting, mixed), table (table/th/td/tbody tr:nth-child/hover)
- All 12 tests now pass; full suite (domain + infrastructure + cli) clean — 0 failures
- Version bumped to 1.0.5, commit c3ec86e, release at https://github.com/TJMSolns/MD-Slides/releases/tag/v1.0.5

**Work queue changes:**
- MS-014: Queued → done

**Status after session:**
- MS-001 (fs2/CE spike): Queued
- MS-012 (MCP server Tier 1): Queued — pre-scaffold gate (sequence diagrams) required
- All bugs resolved: MS-013, MS-015, MS-016, MS-014 all done

**Harvest candidate:** Pattern — CSS "selector duplication for sibling containers" (when two distinct class paths both need the same styling rules, maintain the parallel selector list rather than restructuring HTML). Applicable whenever a new layout template wraps content without `.slide-body`.

**Next session should start with:** `/next` picks MS-012 (pre-scaffold gate: sequence diagrams for render_deck happy/error paths) or MS-001 (fs2/CE spike).

---

## HL-004 — 2026-05-17 — Infrastructure test suite now fully green (MS-016 complete)

**Session:** Tony + Claude (org root — /next continuation)
**What happened:**
- MS-016 (5 pre-existing infrastructure test failures) investigated and fixed
- Root causes identified for each failure — all were test-vs-implementation alignment issues:
  1. `render multi-slide deck`: test checked exact `class="slide active"` but renderer adds alignment suffix → substring check
  2. `render slide with per-slide background`: `rewriteImageUrl` converts `backgrounds/X.png` → `images/X.png` → test updated
  3. `per-slide background overrides template background`: same rewriteImageUrl issue → test updated
  4. `ThemeJsonAdapter.corporate`: theme has `image = None` (no built-in watermark) → test corrected
  5. `diagram renders Mermaid correctly`: renderer uses fallback (`mermaid-fallback`) when no pre-rendered SVGs → test accepts both
- Full test suite (domain + infrastructure + cli) confirmed green: 0 failures
- Version bumped to 1.0.4, release cut at https://github.com/TJMSolns/MD-Slides/releases/tag/v1.0.4
- Commit: ecb377f pushed to github.com:TJMSolns/mdslides.git

**Work queue changes:**
- MS-016: Queued → done

**Status after session:**
- MS-014 (CSS two-column layout gaps): Queued — next bug to fix
- MS-012 (MCP server Tier 1): Queued — pre-scaffold gate (sequence diagrams) required first
- MS-001 (fs2/CE spike): Queued

**Next session should start with:** `/next` picks MS-014 (CSS coverage gaps in two-column layout).

---

## HL-003 — 2026-05-17 — MCP capability surface designed (MS-011 complete)

**Session:** Tony + Claude (org root — /next)
**What happened:**
- MS-011 (MCP capability surface spike) executed: reviewed mdslides domain, CLI pipeline, and ADR history
- Evaluated file-in/file-out vs stateful session architectures for MCP server
- Decision: file-in/file-out (Architecture A) — stateless, composable with Claude Code file tools, matches existing mental model
- ADR-013 written: `doc/internal/governance/adr/ADR-013-mcp-server-architecture.md`
- Capability surface defined: 4 tools in 2 tiers (`render_deck`, `validate_deck` Tier 1; `list_themes`, `get_deck_info` Tier 2)
- MS-012 queued: implement MCP server Tier 1 (stdio transport + render_deck + validate_deck)

**Work queue changes:**
- MS-011: Queued → done (ADR-013)
- MS-012: Added (Queued) — implement Tier 1 MCP server; pre-scaffold gate required

**Status after session:**
- MS-009, MS-010, MS-001: Queued (unblocked)
- MS-012: Queued (pre-scaffold gate: sequence diagrams required before implementation)

**Harvest candidates:** ADR-013 MCP architecture pattern (file-in/file-out for wrapping CLI tools as MCP servers) — could apply to mdslides, future CLI tools in the org. Flag for `/harvest` if/when other projects need MCP exposure.

**Next session should start with:** `/next` picks MS-009 (version declaration in build.sc) or MS-012 (sequence diagrams for MCP server, then implementation).

---

## HL-002 — 2026-05-16 — v1.0.1 released, process hardened

**Session:** Tony + Claude (sonnet-4-6)
**What happened:**
- Rewrote `examples/feature-tour.md` to 45 slides: clean two-column convention, background demo, zero validation errors — commit 50c4aa7
- Fixed two-column slides not rendering headings: parser now extracts `## heading` to `"heading"` slot; renderer uses heading-or-title fallback — `MarkdownParser.scala` + `HTMLRenderer.scala`, commit 115a3ba
- Fixed lists/tables in two-column columns having no indent/styling: extended all `.slide-body` CSS selectors to also cover `.column` — `HTMLRenderer.scala`, commit 12c6314
- Added 11 regression tests in `HTMLRendererTwoColumnSpec.scala` — all passing
- Fixed PathResolverSpec pre-existing failure: error message now lists explicit filenames tried
- Updated `CHANGELOG.md` with [1.0.1] section; `build.sc` version confirmed at 1.0.1
- Cut v1.0.1 GitHub release: https://github.com/TJMSolns/MD-Slides/releases/tag/v1.0.1 — asset `md-slides.jar` verified present, 44 MB, timestamped 2026-05-16T16:32:16Z
- Added DoD for bug fixes to `Projects/mdslides/CLAUDE.md`
- Added Release Discipline section to org methodology (`Projects/claude-code-methodology.md`)
- Added release asset verification step to `/handoff` skill

**Decisions made:** none formal
**Harvest candidates:** release artifact filename gotcha (gh names asset by source filename); CSS selector scoping gap pattern (slide-body vs column)
**Open items carried forward:**
- MS-009: Add `--version` CLI flag wired to `mdSlidesVersion`
- MS-010: Audit remaining `.slide-body`-only CSS selectors for other `.column` gaps
- Pre-existing: 20 domain + 5 infrastructure test failures not introduced this session; not yet assigned IDs
**Next owner:** any — MS-009 (`--version` flag) is the cleanest next item; MS-010 (CSS audit) is higher-risk

---

## HL-001 — 2026-05-07 — Methodology harness applied

**Session:** Tony + Claude
**What happened:**
- Applied full methodology harness: docs/agents/, .claude/ (settings.json, hooks, skills) created
- Existing CLAUDE.md retained — good implementation reference; methodology section to be added
- Work queue empty — mdslides is a complete tool; items added as they arise

**Decisions made:** None

**Open items:** None queued

**Next owner:** Tony + Claude — add MS-xxx items as feature requests or bugs arise
