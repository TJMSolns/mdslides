---
name: handoff
description: Write an end-of-session HANDOFF-LEDGER entry, update WORK-QUEUE statuses, and close the session cleanly. Run at the end of every working session.
user-invocable: true
tools: Read, Write, Edit, Glob, Grep
---

Close this session cleanly by writing a handoff entry.

## Steps

1. Read `docs/agents/WORK-QUEUE.md` — note what was in_progress or completed this session
2. Read `docs/agents/DECISION-REGISTER.md` — note any decisions made this session
3. Review recent conversation context to summarize what happened

4. **Harvest check:** Are there patterns, lessons, or decisions from this session worth generalizing to other projects or org methodology? If yes, note them as `**Harvest candidates:**` in the ledger entry. If no, omit.

4b. **Harvest gate (blocking — POL-010):** Before writing the HL entry, resolve every candidate named in step 4:

   - **Already has a WQ item:** Record the WQ item ID in the HL entry — no action needed.
   - **No WQ item:** Choose one before closing:
     - **(a) Queue inline:** Add a concrete WQ item to `docs/agents/WORK-QUEUE.md` now; add the new ID to the HL entry.
     - **(b) Defer explicitly:** Add to the HL entry body:
       ```
       **Harvest deferred:** <candidate name> — <reason> — revisit by <date, within 30 days>
       ```

   **Do not write the HL entry until every named harvest candidate has either a WQ item ID or an explicit deferral.** (POL-010: 7-day SLA from HL entry date)

   Separately: scan the last 3 HANDOFF-LEDGER entries for older `**Harvest candidates:**` lines without visible resolution. If any are found, print:
   ```
   ⚠  Unresolved harvest candidates in prior sessions — queue or defer per POL-010 (7-day SLA).
   ```

5. **Update WORK-QUEUE:**
   - Set completed items to `done` with evidence (file path or decision ID)
   - Leave in_progress items as-is if genuinely still in progress
   - Add any newly discovered items

6. **Write HANDOFF-LEDGER entry** at the TOP of `docs/agents/HANDOFF-LEDGER.md`:
   ```markdown
   ## HL-<next-id> — <date> — <session summary in 5 words>

   **Session:** <who worked, what mode>
   **What happened:**
   - <bullet: artifact created or decision made, with file path>
   - ...

   **Decisions made:** <DR-IDs or "none">
   **Open items carried forward:**
   - <description>
   **Next owner:** <person or "any"> — <what they should do next>

   ---
   ```

7. Confirm the entry was written. Print the "Next owner" line.
