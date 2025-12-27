---
marp: true
theme: default
paginate: true
header: '[Project Name]: Sprint [X] Kickoff'
footer: '[Date]'
---

# Sprint [X] Kickoff
## [Sprint Theme/Focus Area]

**Date:** [YYYY-MM-DD]  
**Sprint Goal:** [One sentence sprint objective]  
**Status:** 🔴 Ready to Start

---

## Sprint Overview

**Duration:** [X hours/days]  
**Items:** [N tasks]  
**Priority:** [HIGH/MEDIUM/LOW]

**Dependencies:** [List dependencies or "None"]

---

## Sprint Context

### What Happened Last Sprint
- ✅ [Achievement 1]
- ✅ [Achievement 2]
- ⚠️ [Issue/Learning from previous sprint]

### Why This Sprint Matters
[2-3 sentences on business/technical value]

---

## Objectives

### Primary Goal
[Single most important outcome]

### Secondary Goals
- [Goal 1]
- [Goal 2]
- [Goal 3]

---

## Scope: [N] Enhancements/Features

### [ENH-XXX/FEATURE-1]: [Name]
**Command/Feature:** `[command or feature name]`  
**Purpose:** [One sentence purpose]  
**Effort:** [X hours]

**Why Now?**
- [Reason 1]
- [Reason 2]

**Success Criteria:**
- [ ] [Criterion 1]
- [ ] [Criterion 2]

---

### [ENH-XXX/FEATURE-2]: [Name]
**Command/Feature:** `[command or feature name]`  
**Purpose:** [One sentence purpose]  
**Effort:** [X hours]

**Why Now?**
- [Reason 1]
- [Reason 2]

**Success Criteria:**
- [ ] [Criterion 1]
- [ ] [Criterion 2]

---

## Implementation Plan

### Phase 1: [Phase Name] ([X hours])
**Timeline:** Day 1-[N]

**Tasks:**
1. [Task 1]
2. [Task 2]
3. [Task 3]

**Deliverable:** [What's done after this phase]

---

### Phase 2: [Phase Name] ([X hours])
**Timeline:** Day [N]-[M]

**Tasks:**
1. [Task 1]
2. [Task 2]

**Deliverable:** [What's done after this phase]

---

### Training Materials (If Major Migration/New Tech)
**Effort:** 4-8 hours  
**Timeline:** Concurrent with implementation or sprint end

**Deliverables:**
- [ ] Migration guide (doc/reference/*.md)
- [ ] Training session deck (doc/training/*.md)
- [ ] Code examples from codebase
- [ ] Troubleshooting section

**Note:** Per Sprint 11 lessons learned, major migrations (Scala 3, new frameworks) should include training materials in scope. Budget 4-8h for comprehensive guides + session materials.

---

### Phase 3: [Phase Name] ([X hours])
**Timeline:** Day [M]-[End]

**Tasks:**
1. [Task 1]
2. [Task 2]

**Deliverable:** [What's done after this phase]

---

### Phase 3: [Phase Name] ([X hours])
**Timeline:** Day [M]-[End]

**Tasks:**
1. [Task 1]
2. [Task 2]

**Deliverable:** [What's done after this phase]

---

## Success Metrics

| Metric | Baseline | Target | Measurement |
|--------|----------|--------|-------------|
| [Metric 1] | [Current value] | [Target value] | [How measured] |
| [Metric 2] | [Current value] | [Target value] | [How measured] |
| [Metric 3] | [Current value] | [Target value] | [How measured] |

---

## Risk Assessment

### High Risk
- ⚠️ **[Risk 1]**
  - **Mitigation:** [Strategy]
  - **Owner:** [Name]

### Medium Risk
- 🟡 **[Risk 2]**
  - **Mitigation:** [Strategy]
  - **Owner:** [Name]

---

## Team Assignments

| Task | Owner | Support | Est. Hours |
|------|-------|---------|------------|
| [Task 1] | [Name] | [Name] | [X]h |
| [Task 2] | [Name] | [Name] | [X]h |
| [Task 3] | [Name] | [Name] | [X]h |

---

## Definition of Done

### Code
- [ ] All tests pass (unit, integration, BDD)
- [ ] Code reviewed by [Role]
- [ ] No compiler warnings
- [ ] Mill validation commands pass

### Documentation
- [ ] READMEs updated
- [ ] Mermaid diagrams current
- [ ] Changelog updated
- [ ] [Other doc requirement]

### Governance
- [ ] ADRs/POLs created if decisions made
- [ ] Ubiquitous language updated
- [ ] Context maps current

---

## Sprint Ceremonies

### Daily Standup
**Time:** [Time]  
**Duration:** 15 minutes  
**Format:** 
- What I completed yesterday
- What I'm working on today
- Blockers

### Mid-Sprint Check-in
**Time:** [Day X] at [Time]  
**Duration:** 30 minutes  
**Purpose:** Review progress, adjust plan if needed

---

## Communication Plan

### Updates
- **Daily:** Standup + Slack updates
- **Mid-sprint:** Progress check-in
- **End-of-sprint:** Retrospective + demo

### Blockers
- **Immediate:** Post in #blockers channel
- **Critical:** @ Program Manager
- **Non-critical:** Discuss in standup

---

## Reference Materials

### Key Documents
- [CHARTER.md](../../CHARTER.md) - Project charter
- [ARCHITECTURE.md](../../ARCHITECTURE.md) - System architecture
- [HOW-WE-WORK.md](../../HOW-WE-WORK.md) - SDLC playbook
- [[Related doc 1]]
- [[Related doc 2]]

### Tools & Commands
```bash
# Validation commands
mill domainValidate
mill scenarioValidate
mill testFirstValidate

# Build & test
mill __.compile
mill __.test
```

---

## Questions & Clarifications

### Open Questions
1. [Question 1]
   - **Owner:** [Name]
   - **Due:** [Date]

2. [Question 2]
   - **Owner:** [Name]
   - **Due:** [Date]

### Assumptions
- [Assumption 1]
- [Assumption 2]

---

## Next Steps (After This Meeting)

1. **Immediate** ([Today]):
   - [ ] [Action 1]
   - [ ] [Action 2]

2. **This Week**:
   - [ ] [Action 3]
   - [ ] [Action 4]

3. **Before Next Meeting**:
   - [ ] [Action 5]

---

# Let's Build! 🚀

**Questions?**

---

## Appendix: Timeline

```
Week View:
Mon     Tue     Wed     Thu     Fri
───────────────────────────────────
Phase 1 Phase 1 Phase 2 Phase 2 Phase 3
Task 1  Task 2  Task 3  Task 4  Testing

Daily Activities:
- Morning: Standup (15 min)
- Core work: 6 hours
- End of day: Update status
```

---

## Appendix: Tools Setup

### Required Tools
- Mill 0.11.6+
- Java 21+
- [Other tools]

### Verification
```bash
# Check versions
mill --version
java --version
[other checks]

# Test environment
mill __.compile
```

---

## Contact Information

| Role | Name | Slack | Email |
|------|------|-------|-------|
| Program Manager | [Name] | @[handle] | [email] |
| Product Owner | [Name] | @[handle] | [email] |
| Architect | [Name] | @[handle] | [email] |
| Lead Developer | [Name] | @[handle] | [email] |
