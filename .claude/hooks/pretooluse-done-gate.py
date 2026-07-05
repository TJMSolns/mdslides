#!/usr/bin/env python3
"""PreToolUse gate: block moving a WORK-QUEUE item into ## Done without a valid
evidence artifact at docs/agents/evidence/<ID>.md (DN-001/GATE-004; DN-002 E2 extension)."""
import sys
import json
import re
import os

REQUIRED_FIELDS = [
    "Commit:",
    "Run-count:",
    "Invariance-recheck:",
    "Verified-by:",
    "Verifier-tier:",
    "Verifier-verdict:",
]


def done_ids(text):
    """Item-ID prefix is intentionally generic (not hardcoded to HE-) so this
    hook works unmodified when propagated to another project's WORK-QUEUE.md
    with its own convention (e.g. mdslides' MS-NNN) — caught while attempting
    HE-005's propagation: the original HE-only regex would have made this gate
    a silent no-op on any other project's queue, never matching any ID."""
    m = re.search(r"^## Done\b(.*)", text, re.S | re.M)
    if not m:
        return set()
    section = m.group(1)
    m2 = re.search(r"^## ", section, re.M)
    if m2:
        section = section[: m2.start()]
    return set(re.findall(r"\|\s*([A-Z]{2,8}-[A-Za-z0-9]+)\s*\|", section))


def field_value(evidence_text, field):
    """Match the field's value on ITS OWN LINE only. `[ \\t]*` (not `\\s*`) and the
    `$`/MULTILINE anchor prevent a blank value from capturing the next line's
    content — `\\s*(.+)` without anchoring did exactly that (found by the E2
    verifier's independent re-check on HE-002: an empty `Commit:` line was being
    satisfied by the following `Run-count:` line's text)."""
    m = re.search(r"^" + re.escape(field) + r"[ \t]*(.*)$", evidence_text, re.MULTILINE)
    if not m:
        return None
    return m.group(1).strip()


def missing_field(evidence_text):
    for field in REQUIRED_FIELDS:
        value = field_value(evidence_text, field)
        if not value:
            return field
    return None


def verifier_veto(evidence_text):
    """DN-002/DN-004: structural veto — anything other than PASS blocks regardless
    of how complete the other fields are. Distinguishes VETO (doer should fix and
    re-verify) from ESCALATE (DN-004: do NOT loop fix->re-verify — this needs
    Tony; log to docs/agents/ESCALATIONS.md instead) purely in the message text —
    both block identically at the hook layer."""
    verdict = field_value(evidence_text, "Verifier-verdict:")
    if not verdict:
        return "Verifier-verdict: field missing", "missing"
    if verdict.upper().startswith("ESCALATE"):
        return verdict, "escalate"
    if not verdict.upper().startswith("PASS"):
        return verdict, "veto"
    return None, None


def main():
    try:
        data = json.loads(sys.stdin.read())
    except Exception:
        sys.exit(0)

    tool_name = data.get("tool_name")
    tool_input = data.get("tool_input", {}) or {}
    file_path = tool_input.get("file_path", "")

    if tool_name not in ("Edit", "Write"):
        sys.exit(0)
    if not file_path.replace("\\", "/").endswith("docs/agents/WORK-QUEUE.md"):
        sys.exit(0)

    try:
        with open(file_path, "r") as f:
            old_content = f.read()
    except FileNotFoundError:
        old_content = ""

    if tool_name == "Edit":
        old_string = tool_input.get("old_string", "")
        new_string = tool_input.get("new_string", "")
        if not old_string or old_string not in old_content:
            sys.exit(0)  # can't reliably reconstruct post-edit state; fail open
        if tool_input.get("replace_all"):
            new_content = old_content.replace(old_string, new_string)
        else:
            new_content = old_content.replace(old_string, new_string, 1)
    else:
        new_content = tool_input.get("content", "")

    newly_done = done_ids(new_content) - done_ids(old_content)
    if not newly_done:
        sys.exit(0)

    evidence_dir = os.path.join(os.path.dirname(os.path.abspath(file_path)), "evidence")
    problems = []
    for item_id in sorted(newly_done):
        ev_path = os.path.join(evidence_dir, f"{item_id}.md")
        if not os.path.isfile(ev_path):
            problems.append(f"{item_id}: no evidence file at docs/agents/evidence/{item_id}.md")
            continue
        with open(ev_path) as f:
            ev_text = f.read()
        field = missing_field(ev_text)
        if field:
            problems.append(f"{item_id}: evidence file missing/empty required field '{field}'")
            continue
        veto, kind = verifier_veto(ev_text)
        if veto:
            if kind == "escalate":
                problems.append(
                    f"{item_id}: verifier ESCALATED, not VETOed — {veto} "
                    f"(do NOT fix-and-retry; log to docs/agents/ESCALATIONS.md and wait for Tony)"
                )
            else:
                problems.append(f"{item_id}: verifier did not PASS — {veto}")

    if problems:
        print("BLOCKED — Done transition without valid evidence artifact (DN-001/GATE-004; DN-002 E2; DN-004 E4):", file=sys.stderr)
        for p in problems:
            print(f"  - {p}", file=sys.stderr)
        print(
            "Create docs/agents/evidence/<ID>.md with Commit:/Run-count:/Invariance-recheck:/Verified-by:/"
            "Verifier-tier:/Verifier-verdict: (PASS required) before marking Done. An ESCALATE verdict means "
            "stop and log to docs/agents/ESCALATIONS.md instead of retrying.",
            file=sys.stderr,
        )
        sys.exit(2)

    sys.exit(0)


if __name__ == "__main__":
    main()
