# mdslides — Work Queue

P4 priority. Work items are driven by feature requests, bugs, and maintenance needs.

---

## Active

| ID | Item | Owner | Status | Depends On |
|----|------|-------|--------|-----------|
| MS-001 | Spike: investigate I/O performance improvements via deeper Cats Effect / fs2 usage (streaming file I/O, parallel rendering pipeline) and Calico for terminal UI — Calico applicability to JVM CLI needs validation (primarily Scala.js); CE/fs2 JVM applicability is not in question | Claude | Queued | — |

---

## Done

| ID | Item | Evidence |
|----|------|---------|
| MS-003 | Public release: create MD-Slides repo, sanitize, write README/CONTRIBUTING/LICENSE/CI | https://github.com/TJMSolns/MD-Slides — public, v1.0.0, MIT — 2026-05-15 |
| MS-002 | Install GitHub App on repo | Consolidated to WQ-P4-023 (org WQ) — 2026-05-15 |
| — | Initial harness applied | CLAUDE.md, docs/agents/, .claude/ created 2026-05-07 |

---

## Notes

mdslides (private planning root) → MD-Slides (https://github.com/TJMSolns/MD-Slides) is the public repo.
New feature work should be tracked here and implemented in MD-Slides.
MS-001 (fs2/CE spike) remains queued for the public repo once CI is confirmed green.
