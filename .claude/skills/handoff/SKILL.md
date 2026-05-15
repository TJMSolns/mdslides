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

4. **Update WORK-QUEUE:**
   - Set completed items to `done` with evidence (file path or decision ID)
   - Leave in_progress items as-is if genuinely still in progress
   - Add any newly discovered items

5. **Write HANDOFF-LEDGER entry** at the TOP of `docs/agents/HANDOFF-LEDGER.md`:
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

6. Confirm the entry was written. Print the "Next owner" line.
