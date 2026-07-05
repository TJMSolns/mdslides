#!/usr/bin/env python3
"""Canonical, deterministic verifier-tier draw (DN-002 E2 coin). Replaces the
hand-transcribed prose in planner.md/next SKILL.md that produced one real
process slip (HE-004: P1 stake-weighting mistakenly applied to a P2 draw,
caught before submission but not by any mechanism — this script exists so it
can't happen again). Uses real randomness (python3's random module, seeded
from the OS), not a value picked by the calling agent."""
import sys
import random

# Tier NAMES, not model versions/IDs — the Agent tool's `model` param resolves
# "sonnet"/"opus"/"haiku" to whatever the current model in that tier is, so a
# routine version bump (e.g. Sonnet 5 -> Sonnet 6) needs no change here.
# KNOWN LIMITATION (not fixed, documented instead — no API exists to discover
# tiers at runtime): if Anthropic adds a new graded tier, retires one of these
# three, or renames the category itself, this list needs a manual edit. Fable
# is deliberately excluded, not missing by oversight — DN-002/ADR-002 scoped
# it as "separate" from the graded ±1 ladder (research-findings-parcel1.md:
# "Haiku < Sonnet < Opus [Fable separate]") since it isn't a consistently
# weaker/stronger tier the way Haiku/Sonnet/Opus are.
LADDER = ["haiku", "sonnet", "opus"]


def draw(priority, doer_tier):
    doer_idx = LADDER.index(doer_tier)
    offset = random.choice([-1, 0, 1])
    raw_offset = offset
    resampled = False
    if priority == "P1" and offset == -1:
        if random.random() < 0.5:
            offset = random.choice([0, 1])
            resampled = True
    idx = max(0, min(2, doer_idx + offset))
    return LADDER[idx], raw_offset, offset, resampled


def main():
    if len(sys.argv) != 3:
        print("Usage: draw-verifier-tier.py <P1|P2|P3> <doer-tier: haiku|sonnet|opus>", file=sys.stderr)
        sys.exit(1)

    priority = sys.argv[1].upper()
    doer_tier = sys.argv[2].lower()

    if doer_tier not in LADDER:
        print(f"Invalid doer tier: '{doer_tier}'. Must be one of {LADDER}.", file=sys.stderr)
        sys.exit(1)
    if priority not in ("P1", "P2", "P3"):
        print(f"Invalid priority: '{priority}'. Must be P1, P2, or P3.", file=sys.stderr)
        sys.exit(1)

    tier, raw_offset, final_offset, resampled = draw(priority, doer_tier)
    print(
        f"tier={tier} raw_offset={raw_offset} final_offset={final_offset} "
        f"resampled={resampled} priority={priority} doer={doer_tier}"
    )


if __name__ == "__main__":
    main()
