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

6. When the item is complete:
   - Verify evidence exists (file path, commit SHA, or decision ID)
   - Update `docs/agents/WORK-QUEUE.md`: move to Done with evidence
   - Write a HANDOFF-LEDGER entry (use the `/handoff` pattern)

7. Loop to the next unblocked item (go back to step 2).

8. Stop only when:
   - The queue is empty
   - All remaining items are blocked (report which and why)
   - The user says to stop

Report between items: one line — item ID, what was done, what's next.
