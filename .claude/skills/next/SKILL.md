---
name: next
description: Pick the next unblocked item from WORK-QUEUE and drive it to completion. Continues until the queue is empty or all items are blocked.
user-invocable: true
tools: Read, Write, Edit, Glob, Grep, Agent
---

Autonomously drive the WORK-QUEUE forward.

## Steps

1. Read `docs/agents/CONTEXT-KERNEL.md` — verify current phase and non-negotiables
2. Read `docs/agents/WORK-QUEUE.md` — identify the next unblocked item using priority lanes:
   - Scan lanes P1 → P2 → P4 in order (no P3 — Chora Stack and TJM Platform are unified under P2)
   - Skip P1 (belsouri — tracked in its own isolated session)
   - Within each lane, pick the first item with status `queued` or `active` whose dependencies are all in Done
   - Report which lane was selected and why (e.g. "P2 has no unblocked items; falling through to P3")
   - If nothing is unblocked in any lane, report the specific blockers and stop
3. Read `docs/agents/HANDOFF-LEDGER.md` (last 3 entries) for recent context

4. **Pre-Implementation Gate** (applies to implementation items only — skip for docs/design/tooling):
   - Check if a design document (domain model, sequence diagrams) exists for the target
   - If missing: write the design document and pause for review before proceeding
   - If present: proceed normally

5. Execute the work item:
   - For design work: produce the artifact (ADR, note, diagram, etc.) in the appropriate location
   - For implementation: write or edit code in the target repo/module
   - For governance: use the `/decide` pattern to record decisions

5b. **Commit-hash evidence gate (BLOCKING — WQ-P4-104)** — if this item produced code changes (any source file, build script, Dockerfile, etc.):
   - The evidence written in the next step MUST cite at least one commit hash
   - Every cited hash MUST exist: `git cat-file -e <hash> 2>/dev/null && echo OK || echo MISSING`
   - If any hash is MISSING, or the evidence has no hash for a code-producing item, do **not** mark done. Commit + push the missing work first, then re-write the evidence with the correct hash. (GATE-004; complements /handoff WQ-P4-103.)

6. When the item is complete:
   - Verify evidence exists (file path, commit SHA, or decision ID)
   - **Evidence-artifact + independent-verifier gate (propagated from harness-evolution HE-005,
     DN-001/DN-002):** create `docs/agents/evidence/<ID>.md` with `Commit:`, `Run-count:`,
     `Invariance-recheck:`, `Verified-by:`, `Verifier-tier:`, and `Verifier-verdict:` filled in.
     Spawn the `verifier` agent (fresh context, tier drawn by running `python3 .claude/hooks/draw-verifier-tier.py <P1|P2|P3> <doer-tier>` — HE-009/RL-001, the ONLY correct way to draw; never hand-derive the offset/clamp/stake-weighting yourself) and require its `PASS` before the Done edit; do not self-certify. A
     `PreToolUse` hook (`.claude/hooks/pretooluse-done-gate.py`) structurally blocks (exit 2) the
     WORK-QUEUE edit if this is missing, incomplete, or the verdict is VETO/ESCALATE — this is the
     enforcement, not this line. On ESCALATE: do not fix-and-retry, log to
     `docs/agents/ESCALATIONS.md` and stop for Tony instead.
     - **Gap 1 closed (HE-011/DN-006):** a written `PASS` is now cross-checked against the session
       transcript's actual `Agent` tool-call record (`subagent_type: "verifier"`, matching `model`,
       a completed result ending in `PASS`) — spawning the verifier for real is what makes the check
       pass, not just writing the field.
       - **Model unavailable (HE-012/DN-007):** if the `Agent` call fails because the
         drawn tier can't be invoked in this environment, redraw once via
         `draw-verifier-tier.py <P1|P2|P3> <doer-tier> <excluded-tiers-comma-separated>` —
         it falls back to your own tier if both others are excluded. If that redraw itself
         outputs `tier=ESCALATE`, do not retry again — emit `ESCALATE` yourself and stop.
   - Update `docs/agents/WORK-QUEUE.md`: move to Done with evidence
   - Write a HANDOFF-LEDGER entry (use the `/handoff` pattern)

7. Loop to the next unblocked item (go back to step 2).

8. Stop only when:
   - The queue is empty
   - All remaining items are blocked (report which and why)
   - The user says to stop

Report between items: one line — item ID, what was done, what's next.
