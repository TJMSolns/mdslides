# GitHub Copilot Instructions: Phase 0 - Program Initiation
## Role: Program Manager

---

## 🎯 Your Role in This Ceremony

As **Program Manager**, you lead the **Program Initiation** phase, establishing the foundation for the entire program. You coordinate with the Architect to create comprehensive charter and canvas documents that will guide all subsequent work.

---

## 📋 Ceremony Overview

**Phase**: Phase 0 - Program Initiation  
**Ceremony**: Program Initiation  
**Led by**: Program Manager  
**Supported by**: Architect (technical strategy), Product Owner (business requirements), Bench Developer (feasibility input)  
**When**: Once at program start  
**Duration**: 2-5 days  
**Cadence**: One-time (updated only for major pivots)

---

## 🎯 Ceremony Goals

1. Document the problem and proposed solution
2. Identify bounded contexts and service breakdown
3. Establish high-level roadmap and milestones
4. Create both deep (Charter) and quick-reference (Canvas) documentation
5. Align all stakeholders on program vision

---

## 📥 Inputs Required

- Business problem statement
- Stakeholder requirements
- High-level solution vision
- Budget and timeline constraints
- Team composition
- Strategic objectives (OKRs)

---

## 📤 Outputs You'll Create

| Artifact | Template | Location | Purpose |
|----------|----------|----------|---------|
| Project Charter | `PROJECT-CHARTER-TEMPLATE.md` | `CHARTER.md` (root) | Deep context, full details |
| Project Canvas | `PROJECT-CANVAS-TEMPLATE.md` | `PROJECT-CANVAS.md` (root) | Quick reference, 1-2 pages |
| Initial backlog | N/A | `doc/planning/backlog.md` | Prioritized work items |
| Stakeholder map | N/A | In charter | Who's involved, roles |

---

## 🔨 How to Execute This Ceremony

### Step 1: Gather Stakeholder Input (Day 1)

**Actions**:
1. Schedule kickoff meeting with all stakeholders
2. Document business problem (what's broken, what's the pain)
3. Capture proposed solution (how will we fix it)
4. Identify success criteria (what does "done" look like)
5. Define release targets and milestones

**Copilot Prompt Examples**:
```
"Create a project charter problem statement for a multi-tenant SaaS platform migration"

"Generate stakeholder roles and responsibilities table for enterprise Java microservices program"

"Write OKRs for Phase 1 domain discovery in a DDD project"
```

**Key Questions to Answer**:
- Why are we doing this project?
- What's the business impact if we don't do it?
- Who are the users/customers?
- What's the timeline and budget?
- What are the risks?

---

### Step 2: Decompose into Bounded Contexts (Day 2, with Architect)

**Actions**:
1. Review domain with Architect and Product Owner
2. Identify candidate bounded contexts
3. Map high-level dependencies between contexts
4. Estimate complexity and risk per context
5. Define initial service breakdown

**Copilot Prompt Examples**:
```
"Identify bounded contexts for an e-commerce platform with tenant management, order processing, and billing"

"Create a service decomposition table showing bounded contexts, responsibilities, and dependencies"

"Generate a Mermaid C4 context diagram showing system boundaries"
```

**Collaboration Points**:
- Architect provides technical decomposition
- Product Owner validates business alignment
- Bench Developer provides feasibility feedback

---

### Step 3: Create Project Charter (Days 3-4)

**Actions**:
1. Use `doc/reference/templates/PROJECT-CHARTER-TEMPLATE.md`
2. Document problem, solution, goals, constraints
3. Define team structure and roles
4. Establish governance framework (POLs, PDRs, ADRs)
5. Create high-level architecture overview (with Architect)
6. Define success metrics and SLAs

**Charter Sections** (from template):
- Executive Summary
- Problem Statement
- Proposed Solution
- Goals & Objectives (OKRs)
- Scope (in-scope, out-of-scope, future considerations)
- Bounded Contexts & Services
- Architecture Overview
- Team & Roles
- Governance Framework
- Timeline & Milestones
- Risks & Mitigations
- Success Metrics

**Copilot Prompt Examples**:
```
"Write a problem statement for migrating a monolithic tenant management system to microservices"

"Create a risks and mitigations table for a DDD/BDD/TDD enterprise Java project"

"Generate a timeline with milestones for 4 bounded contexts over 6 months"
```

---

### Step 4: Create Project Canvas (Day 4)

**Actions**:
1. Use `doc/reference/templates/PROJECT-CANVAS-TEMPLATE.md`
2. Distill charter into 1-2 page quick reference
3. Focus on: problem, solution, key metrics, team, milestones
4. Make it scannable (tables, bullet points, no prose)

**Canvas Must Include**:
- One-sentence problem statement
- One-sentence solution
- Top 3 OKRs
- Team roster with roles
- Bounded contexts table
- Key milestones with dates
- Critical metrics (SLAs, SLOs)

**Copilot Prompt Examples**:
```
"Summarize a 20-page project charter into a 1-page canvas format"

"Create a bounded contexts table showing name, responsibility, team lead, and status"

"Generate a key metrics table with metric name, target, current, and trend"
```

---

### Step 5: Establish Governance Framework (Day 5)

**Actions**:
1. Create `doc/governance/` directory structure
2. Identify initial policies (POLs) that apply program-wide
3. Document architectural principles (ADRs)
4. Set up decision-making process
5. Define documentation standards

**Directory Structure**:
```
doc/governance/
├── POL/           # Policies (company-wide rules)
├── PDR/           # Product Decisions (business choices)
├── ADR/           # Architecture Decisions (technical choices)
└── GOVERNANCE-BACKLOG.md
```

**Copilot Prompt Examples**:
```
"Create a policy (POL) for non-blocking I/O mandate in Java enterprise microservices"

"Write an ADR for choosing Pekko over Spring Boot"

"Generate a governance backlog tracking 25 POLs, 14 PDRs, and 55 ADRs"
```

---

### Step 6: Initialize Repository Structure (Day 5)

**Actions**:
1. Set up Git repository with standard structure
2. Create `doc/` hierarchy (see below)
3. Copy all templates from `doc/reference/templates/`
4. Create initial `STATUS.md` and `ARCHITECTURE.md`
5. Configure CI/CD skeleton

**Repository Structure**:
```
/
├── .github/
│   └── copilot-instructions.md
├── CHARTER.md
├── PROJECT-CANVAS.md
├── HOW-WE-WORK.md
├── STATUS.md
├── ARCHITECTURE.md
├── doc/
│   ├── architecture/
│   ├── domain-models/
│   ├── governance/
│   ├── planning/
│   ├── reference/
│   ├── scenarios/
│   └── services/
├── features/
└── src/
```

**Copilot Prompt Examples**:
```
"Generate a README.md for an enterprise Java microservices program using DDD/BDD/TDD"

"Create a STATUS.md template tracking program progress across 4 phases"

"Write a .gitignore for Java 21 Maven project with IntelliJ and VS Code"
```

---

## ✅ Definition of Done

This ceremony is complete when:

- [ ] Project Charter created and reviewed by all stakeholders
- [ ] Project Canvas created and published
- [ ] Bounded contexts identified and documented
- [ ] Repository structure initialized with templates
- [ ] Governance framework established (POL/PDR/ADR structure)
- [ ] Initial backlog created with prioritized epics
- [ ] Team roles assigned and documented
- [ ] Kickoff meeting conducted with all roles
- [ ] Charter and Canvas approved by executive sponsor
- [ ] Initial OKRs defined and tracked

---

## 🚨 Common Pitfalls to Avoid

### Pitfall 1: Charter Too Long
**Problem**: 100-page charter nobody reads  
**Solution**: Keep charter 10-50 pages. Use Canvas for daily reference.

### Pitfall 2: Skipping Canvas
**Problem**: Team can't quickly answer "what are we building?"  
**Solution**: Canvas is REQUIRED. Update weekly.

### Pitfall 3: Technical Details in Charter
**Problem**: Charter full of class diagrams and code  
**Solution**: Charter is strategic. Technical details go in ADRs and service documentation.

### Pitfall 4: No Bounded Context Decomposition
**Problem**: Building monolith, not microservices  
**Solution**: Work with Architect to identify contexts early. Use Event Storming if needed.

### Pitfall 5: One-Time Charter
**Problem**: Charter created once, never updated  
**Solution**: Review charter quarterly. Update Canvas weekly/biweekly.

---

## 🔄 Ceremony Relationships

### Upstream (Inputs to This Ceremony)
- Business strategy and goals
- Executive sponsor requirements
- Budget approval
- Team availability

### Downstream (This Ceremony Feeds Into)
- **Phase 1.1 - Event Storming**: Charter provides domain context
- **Phase 1.4 - Context Mapping**: Bounded contexts from charter
- **Phase 2.1 - Three Amigos**: Backlog items from charter
- **Phase 4.3 - Living Documentation Sync**: Charter updated based on learnings

---

## 📊 Success Metrics

Track these to measure ceremony effectiveness:

| Metric | Target | How to Measure |
|--------|--------|----------------|
| Stakeholder alignment | 100% approval | Charter sign-off |
| Charter completeness | All sections filled | Template checklist |
| Canvas usage | Referenced weekly | Team surveys |
| Bounded context clarity | Zero overlap | Architect validation |
| Time to first ceremony | <1 week after charter | Project timeline |

---

## 🛠️ Tools & Templates

### Required Templates
- `doc/reference/templates/PROJECT-CHARTER-TEMPLATE.md`
- `doc/reference/templates/PROJECT-CANVAS-TEMPLATE.md`
- `doc/reference/templates/CHARTER-CANVAS-GUIDE.md`

### Recommended Tools
- **Mermaid**: For architecture diagrams (C4 context, bounded context map)
- **Git**: Version control for all artifacts
- **Markdown**: All documentation in `.md` format

### Example Copilot Workflows

**Creating Charter**:
1. Open `PROJECT-CHARTER-TEMPLATE.md`
2. Ask Copilot: "Fill in problem statement for multi-tenant SaaS migration"
3. Review and refine each section
4. Ask Copilot: "Generate OKRs from this problem statement"
5. Iterate until complete

**Creating Canvas**:
1. Open completed `CHARTER.md`
2. Open `PROJECT-CANVAS-TEMPLATE.md`
3. Ask Copilot: "Summarize CHARTER.md into PROJECT-CANVAS format"
4. Refine for clarity and brevity

---

## 📚 Related Documentation

- **HOW-WE-WORK.md**: Complete SDLC playbook
- **doc/reference/SBPF/Blending-DDD-BDD-TDD.md**: Ceremony methodology
- **doc/reference/templates/CHARTER-CANVAS-GUIDE.md**: Detailed charter/canvas guidance
- **.github/copilot-instructions.md**: General project context (global instructions)

---

## 🎓 Learning Resources

### Internal References
- Example charter: `doc/exhibits/example-project-charter.md`
- Bounded context guide: `doc/reference/SBPF/Bounded-Contexts.md`
- OKR framework: `doc/reference/SBPF/OKR-Based-Planning.md`

### Key Principles
1. **Charter for depth, Canvas for speed**: Different audiences, different formats
2. **Living documents**: Update quarterly (charter) and weekly (canvas)
3. **Decompose early**: Bounded contexts identified now save months later
4. **Governance from day 1**: POL/PDR/ADR structure prevents decision debt
5. **Stakeholder alignment**: No surprises—everyone reads charter before coding starts

---

**Remember**: As Program Manager, you own coordination and documentation. The charter and canvas are YOUR responsibility. Keep them current, or the team will drift.

---

**Next Ceremony**: Phase 1.1 - Event Storming Session (Led by Architect)  
**Handoff**: Provide charter and canvas to Architect for domain discovery
