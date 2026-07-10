# MS-018 Spike: MD-Slides as a Learning Management System (LMS) Primitive

**Status:** Complete — see recommendations
**Date:** 2026-07-10
**Related:** MS-018 (mdslides WORK-QUEUE), feeds WQ-179 (open-agentic-platform, advisory only — not blocking)

---

## Scope

Evaluate MD-Slides as a candidate primitive for learning/course content, along two axes:

1. **Standalone-useful features** — improve MD-Slides for any presentation use case, LMS or not.
2. **LMS-only features** — only make sense when an LMS (completion tracking, scoring, enrollment) is in play.

For each candidate: effort tier (S/M/L), whether it requires server-side state, and whether it fits
MD-Slides' existing stateless file-in/file-out architecture (`render` command: markdown → `index.html` +
`speaker.html`, per `CLAUDE.md` Rendering Pipeline) or would require a new service.

---

## Method

Reviewed the current domain model (`Slide`, `SlideDeck`, `Template`/`SlotName`, `HeaderFooter`,
`PresentationTimer`, `NavigationHistory`) and infrastructure (`HTMLRenderer`, `SpeakerViewRenderer`,
`MermaidRenderer`) to establish what state and rendering hooks already exist, then evaluated each
candidate feature against that baseline rather than against a green-field LMS design.

Relevant existing primitives:
- `Slide.notes: Option[String]` — per-slide speaker notes, already rendered into `speaker.html` (US-034)
- `Slide.slots: Map[String, String]` — arbitrary named content per slide, template-constrained
- `HeaderFooter` — placeholder resolution (`{current-slide}`, `{total-slides}`, `{elapsed-time}`, static metadata)
- `PresentationTimer` — client-side elapsed-time state machine (start/pause/resume), no persistence
- `NavigationHistory` — client-side visit/forward stack, no persistence
- No existing concept of: learner identity, submitted answers, completion state, or any cross-session
  persistence. Every render is stateless and reproducible from the `.md` source alone.

---

## Axis 1: Features Independently Useful in MD-Slides (No LMS Required)

| Feature | Description | Effort | Server-side state? | Architecture fit |
|---|---|---|---|---|
| Section-level progress markers | Visual indicator (e.g. "Section 2 of 5") derived from a `## Section` heading convention in markdown, rendered into header/footer via existing `HeaderFooter` placeholder mechanism | S | No | Fits — pure extension of `HeaderFooter.resolvePlaceholders`; new placeholder + a slide→section index computed at render time |
| Per-slide metadata (`difficulty`, `est-minutes`, `tags`) | Front-matter-style key/value pairs per slide, parsed alongside existing `notes`/`backgroundImage` fields on `Slide`, surfaced in speaker view or exported as a JSON sidecar | S | No | Fits — additive field on `Slide` case class, same pattern as `notes: Option[String]` (US-004) |
| Speaker-notes-as-transcript export | CLI flag to emit `Slide.notes` for all slides concatenated as a standalone Markdown/plain-text transcript (script, handout, or captions source) | S | No | Fits — pure function over already-parsed `SlideDeck`; no new parsing or rendering pipeline stage, just a new CLI output target |
| Embedded self-scoring knowledge check (client-only) | A quiz slide type (e.g. `template: quiz`) with MCQ options and a correct-answer slot; scoring done entirely in the emitted JS (`sync.js`-style), result never leaves the browser | M | No | Fits — new `Template`/`SlotName` variant + JS scoring logic bundled into `HTMLRenderer` output, same pattern as existing Mermaid pre-rendering (self-contained static asset, no backend call) |
| Reading-time / density-based pacing estimate | Extend existing density validation (`SlotConstraints.maxWords`/`maxLines`, already producing `DensityWarning`) to also emit an estimated per-slide reading time, surfaced in speaker view next to the timer | S | No | Fits — derived value from data `Slide.validated` already computes; no new I/O |
| Print/handout export with notes inline | Static HTML/PDF-ready export combining slide content + notes in a linear reading layout, reusing `HTMLRenderer` templates in a "handout" mode | M | No | Fits — new renderer mode alongside `HTMLRenderer`/`SpeakerViewRenderer`, no new dependency class |

**Pattern:** every standalone-useful candidate above is achievable by extending `Slide`'s optional
metadata fields and/or adding a rendering mode — the same shape as the existing `notes` and
`backgroundImage` additions. None require I/O beyond the current read-markdown/write-HTML pipeline.

---

## Axis 2: Features That Only Make Sense With an LMS

| Feature | Description | Effort | Server-side state? | Architecture fit |
|---|---|---|---|---|
| Completion tracking | Record which learner completed which deck/section and when | L | **Yes** | Does not fit — requires persistent learner↔deck completion records; needs a new service |
| Quiz scoring backend (graded, recorded) | Same quiz UI as the standalone knowledge check, but the score is submitted, stored, and attributable to a learner (for grading, prerequisites, certification) | L | **Yes** | Does not fit — requires learner identity + a submission/storage endpoint; the file-in/file-out model has no place to persist a score across renders |
| Learner identity / authentication | Who is taking the course | L | **Yes** | Does not fit — MD-Slides has no session or user concept at any layer (domain is pure, infra is I/O-only, no auth primitive exists) |
| Course enrollment / sequencing across decks | Which learners are enrolled in which multi-deck "course," prerequisite gating between decks | L | **Yes** | Does not fit — decks are currently independent render units; a "course" spanning multiple `SlideDeck`s with gating is a new aggregate with cross-deck, cross-learner state |
| SCORM / xAPI export | Package a deck (+ quiz results schema) for import into a third-party LMS (Moodle, Canvas, etc.) | M | Partially (needs a stable results schema, but export itself can be stateless) | Partial fit — the *export* (a SCORM-compliant zip/manifest wrapping existing render output) is a stateless transform and fits the current pipeline; the *tracking data* SCORM expects to read/write at runtime requires the external LMS's own state, not MD-Slides' |

**Pattern:** every LMS-only candidate requires state that outlives a single `render` invocation and is
keyed by learner identity — something MD-Slides' domain model has no aggregate for today (`SlideDeck`
has no concept of "who is viewing this" or "what have they done"). This is not a rendering-pipeline gap;
it is the absence of an entire bounded context (learners, attempts, records) that MD-Slides was never
scoped to own.

---

## Worked Example: "Embedded Quiz at End of Each Section"

Evaluated both ways per the spike brief, using the two designs actually available given the current
architecture:

### Standalone (self-scoring JS, no backend)

- New `quiz` template/slot type: question slot + N option slots + a `correct` slot (or answer-key
  metadata not rendered to the visible DOM).
- Scoring logic emitted as static JS in `HTMLRenderer`'s output — identical pattern to existing
  client-side behavior (`sync.js` for speaker-view cross-window sync, `PresentationTimer`/
  `NavigationHistory` client-side state machines already have no server component).
- Result exists only in the browser tab's DOM/local state; refreshing the page loses it — acceptable
  for a self-check, not for anything that needs to persist or be reported.
- **Verdict:** fits cleanly. Effort M (new template type + client JS + validation rules for the answer
  slot). No new module, no new dependency, no server.

### LMS-integrated (scored, tracked, reported)

- Requires everything the standalone version has, plus: a submission endpoint, learner identity to
  attribute the submission to, and a store to persist the result and later report it back (e.g. "learner
  X scored 80% on section 3 quiz on 2026-07-10").
- None of that state can live in the rendered HTML or in MD-Slides' domain layer as currently scoped
  (`domain` is explicitly "zero I/O" per `CLAUDE.md`'s non-negotiables — a submission endpoint is I/O
  by definition, and persistence is a new bounded context, not an extension of `SlideDeck`).
- **Verdict:** does not fit MD-Slides as a file-in/file-out CLI. Would require a new service (separate
  from the `domain → infrastructure → cli` monorepo) that MD-Slides' rendered output calls out to — i.e.
  MD-Slides would become a *content producer* for an LMS, not become the LMS itself.

---

## Summary Recommendations

| Item | Recommendation | Priority |
|---|---|---|
| Section-level progress markers | Independently valuable — candidate for a future MS item | P4 |
| Per-slide metadata fields | Independently valuable — low effort, additive | P4 |
| Speaker-notes-as-transcript export | Independently valuable — low effort, new CLI output only | P4 |
| Client-only self-scoring knowledge check | Independently valuable — worth doing regardless of LMS ambitions | P4 (worked example above) |
| Reading-time/pacing estimate | Independently valuable — derived from existing density validation | P4 |
| Handout/print export | Independently valuable, moderate effort | P4 |
| Completion tracking / scored & recorded quizzes / learner identity / enrollment | **Out of scope for MD-Slides as currently architected.** All require persistent, learner-keyed state and I/O that would violate the domain layer's zero-I/O non-negotiable and the CLI's stateless file-in/file-out model. Would need a genuinely separate service. | N/A — not a MD-Slides feature |
| SCORM/xAPI export | Partial fit: the packaging/export step is stateless and could be added to MD-Slides; the tracking runtime it targets is necessarily external | Backlog — only if a concrete LMS integration is requested |

**Bottom line:** MD-Slides can absorb every LMS-*adjacent* feature that stays client-side and
stateless (progress markers, metadata, transcripts, self-scoring quizzes, pacing estimates, handout
export) using the same additive-field/new-render-mode pattern already used for `notes` and
`backgroundImage`. Anything requiring learner identity, submission, or persistence (completion
tracking, graded/recorded quizzes, enrollment, prerequisite gating) is a distinct bounded context that
does not fit the file-in/file-out architecture and would need a new service — MD-Slides would be a
content producer feeding that service, not the service itself. No architectural change is recommended
to MD-Slides itself as a result of this spike; the standalone-useful candidates above are each
independently queueable as their own WORK-QUEUE items if/when prioritized.
