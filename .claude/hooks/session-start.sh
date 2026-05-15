#!/usr/bin/env bash
# Session orientation banner for mdslides

CONTEXT="docs/agents/CONTEXT-KERNEL.md"
QUEUE="docs/agents/WORK-QUEUE.md"
LEDGER="docs/agents/HANDOFF-LEDGER.md"

if [ -f "$CONTEXT" ]; then
  PHASE=$(sed -n '/Current Phase/,/^##/p' "$CONTEXT" 2>/dev/null \
    | grep -v "^##\|^$\|Current Phase" | head -1 \
    | sed 's/\*\*//g' | xargs | cut -c1-45)
  [ -z "$PHASE" ] && PHASE="(see CONTEXT-KERNEL)"
else
  PHASE="(CONTEXT-KERNEL missing)"
fi

if [ -f "$QUEUE" ]; then
  ACTIVE=$(grep -cE "\| MS-[0-9]+" "$QUEUE" 2>/dev/null || echo 0)
else
  ACTIVE="?"
fi

if [ -f "$LEDGER" ]; then
  LAST_HL=$(grep -m1 "^## HL-" "$LEDGER" 2>/dev/null | sed 's/^## //' | cut -c1-50)
  [ -z "$LAST_HL" ] && LAST_HL="(none yet)"
else
  LAST_HL="(HANDOFF-LEDGER missing)"
fi

echo "┌─ mdslides ───────────────────────────────────────────────┐"
printf "│ Phase:       %-45s │\n" "$PHASE"
printf "│ Queue:       %-45s │\n" "$ACTIVE item(s) tracked"
printf "│ Last handoff: %-44s │\n" "$LAST_HL"
echo "│ MANDATORY: read CONTEXT-KERNEL → WORK-QUEUE → HANDOFF   │"
echo "│ Try /status for details, /next to start working          │"
echo "└──────────────────────────────────────────────────────────┘"
