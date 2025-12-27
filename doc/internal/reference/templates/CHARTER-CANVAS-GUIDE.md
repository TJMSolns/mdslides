# Charter & Canvas Template Guide

## Philosophy

Every significant artifact in this repository has **two representations**:

### 📋 Charter (Comprehensive Documentation)
- **Length**: 10-50 pages
- **Audience**: Team members who need full context
- **Usage**: Discovery ceremonies, architecture reviews, onboarding new team members
- **Update Frequency**: Major changes only (quarterly or on significant decisions)
- **Format**: Detailed markdown with sections for scope, responsibilities, architecture, dependencies, risks, etc.

### 🎨 Canvas (Quick Reference)
- **Length**: 1-2 pages (single markdown table)
- **Audience**: Anyone needing fast answers or lookups
- **Usage**: Daily standups, API consumer reference, quick decision-making, status dashboards
- **Update Frequency**: Weekly or as key metrics/dependencies change
- **Format**: Structured table with sections for APIs, NFRs, dependencies, metrics, runbooks

---

## Template Pairs

### 1. Program/Project Level

#### 📋 PROJECT-CHARTER-TEMPLATE.md
**Use When**: Starting a new multi-service program or major initiative  
**Location**: Project root (e.g., `CHARTER.md`)  
**Sections**:
- Executive summary (problem, solution, impact)
- Sub-projects/services overview
- Dependency graph (Mermaid)
- Execution phases
- Timeline (Gantt chart)
- Budget & resources
- Risks & mitigation
- Success criteria

#### 🎨 PROJECT-CANVAS-TEMPLATE.md
**Use When**: Need one-page program snapshot for stakeholder updates  
**Location**: Project root (e.g., `PROJECT-CANVAS.md`) or `doc/planning/`  
**Sections** (table format):
- Internal details (owners, links)
- Mission, value prop, strategic alignment
- Business impact (metrics: current vs target)
- Sub-projects with status
- Execution phases
- Dependencies & critical path
- Budget & resources
- Teams & ownership
- Risks & mitigation
- Success criteria & NFRs
- Observability metrics
- Key milestones

**Example**: See [PROJECT-CANVAS-TEMPLATE.md](./PROJECT-CANVAS-TEMPLATE.md)

---

### 2. Service Level (Microservices)

#### 📋 SERVICE-CHARTER-TEMPLATE.md
**Use When**: Documenting a bounded context / microservice  
**Location**: `doc/services/<service-name>/SERVICE-CHARTER.md`  
**Sections**:
- Service scope & goals
- Bounded context definition (DDD)
- Aggregates & entities
- Domain events
- API contracts (REST/gRPC)
- Data ownership & schema design
- Integration patterns
- BDD scenarios
- NFRs (availability, performance, security)
- Observability & monitoring

#### 🎨 MICROSERVICE-CANVAS-TEMPLATE.md
**Use When**: Need quick API/NFR reference for service consumers  
**Location**: `doc/services/<service-name>/MICROSERVICE-CANVAS.md`  
**Sections** (table format):
- Internal details (PM, architect, SME, links)
- Charter, value prop, positioning
- Dependencies (internal services, external APIs)
- API contracts:
  - Commands (sync/async)
  - Queries
  - Events (inbound/outbound)
  - Reasonable use thresholds
- NFRs (availability, performance SLAs)
- Observability (health checks, metrics, dashboards)
- Runbooks (provisioning, maintenance, troubleshooting)

**Example**: See [MICROSERVICE-CANVAS-TEMPLATE.md](./MICROSERVICE-CANVAS-TEMPLATE.md)

---

### 3. Product Level

#### 📋 PRODUCT-CHARTER-TEMPLATE.md
**Use When**: Defining product strategy, roadmap, and features  
**Location**: `doc/products/<product-name>/PRODUCT-CHARTER.md`  
**Sections**:
- Executive summary (vision, mission, value prop)
- Customer needs & problem statements
- Objectives & key results (OKRs)
- Features & capabilities
- Feature roadmap (v1.0, v2.0, v3.0)
- Competitive analysis
- Product architecture
- Technology stack
- Dependencies (internal/external)
- NFRs
- Documentation
- Release plan

#### 🎨 PRODUCT-CANVAS-TEMPLATE.md
**Use When**: Need one-page product summary for sales, marketing, or executives  
**Location**: `doc/products/<product-name>/PRODUCT-CANVAS.md`  
**Sections** (table format):
- Internal details (PM, architect, SME)
- Charter, value prop, positioning
- Customer needs & problem statements
- OKRs
- Features (user stories)
- Competitive analysis
- Dependencies
- Documentation links
- NFRs
- Observability
- Runbooks
- Releases (current, pending, EOL)

**Example**: See [PRODUCT-CANVAS-TEMPLATE.md](./PRODUCT-CANVAS-TEMPLATE.md)

---

### 4. Micro-Frontend Level

#### 📋 MICROFRONTEND-CHARTER-TEMPLATE.md
**Use When**: Documenting UI components in a micro-frontend architecture  
**Location**: `doc/frontends/<mfe-name>/MICROFRONTEND-CHARTER.md`  
**Sections**:
- Component scope & responsibilities
- Routes & screens
- Component architecture (hierarchy, catalog)
- Technology stack
- Dependencies (backend services, other MFEs)
- State management
- API integration
- UI/UX guidelines
- Performance targets (Lighthouse)
- Testing strategy
- Deployment & DevOps
- Observability

#### 🎨 MICROFRONTEND-CANVAS-TEMPLATE.md
**Use When**: Need quick component/route reference for frontend developers  
**Location**: `doc/frontends/<mfe-name>/MICROFRONTEND-CANVAS.md`  
**Sections** (table format):
- Internal details (PM, frontend lead, UX designer)
- Charter, value prop, positioning
- Dependencies (backend services, other MFEs)
- Routes (URI patterns, screens, roles)
- Components (catalog, reusable widgets)
- Lighthouse scores (FCP, TTI, LCP, CLS)
- Documentation links
- Runbooks

**Example**: See [MICROFRONTEND-CANVAS-TEMPLATE.md](./MICROFRONTEND-CANVAS-TEMPLATE.md)

---

### 5. Web Application Level

#### 📋 WEBAPP-CHARTER-TEMPLATE.md
**Use When**: Documenting full web applications (SPAs, SSR apps, etc.)  
**Location**: `doc/webapps/<app-name>/WEBAPP-CHARTER.md`  
**Sections**:
- Application scope & goals
- Application architecture
- User interface & routes
- Technology stack
- Dependencies (backend services, third-party APIs)
- Authentication & authorization (RBAC)
- State management
- Performance targets (Lighthouse)
- UI/UX design
- Testing strategy
- Deployment & DevOps
- Observability
- User features

#### 🎨 WEBAPP-CANVAS-TEMPLATE.md
**Use When**: Need quick application reference for development/support  
**Location**: `doc/webapps/<app-name>/WEBAPP-CANVAS.md`  
**Sections** (table format):
- Internal details (PM, frontend lead, backend lead)
- Charter, value prop, positioning
- Dependencies (backend services)
- Components (key features)
- Lighthouse scores (FCP, TTI, LCP, CLS)
- Features (as user stories)
- Documentation links
- Runbooks

**Example**: See [WEBAPP-CANVAS-TEMPLATE.md](./WEBAPP-CANVAS-TEMPLATE.md)

---

## DDD/BDD Ceremony Templates

In addition to charter/canvas pairs, we have templates for ceremony outputs:

### 6. Event Storming Sessions

#### EVENT-STORMING-TEMPLATE.md
**Use When**: Conducting Event Storming ceremony (Phase 1: Discovery)  
**Location**: `doc/domain-models/event-storming/[context-name]-events.md`  
**Sections**:
- Domain events (temporal flow with Mermaid timeline)
- Actors/external systems
- Commands
- Read models/queries
- Business rules/invariants
- Questions/issues
- Aggregate candidates
- Bounded context boundaries

**Example**: See [EVENT-STORMING-TEMPLATE.md](./EVENT-STORMING-TEMPLATE.md)

---

### 7. Domain Aggregates

#### AGGREGATE-TEMPLATE.md
**Use When**: Documenting domain aggregate after Domain Modeling Workshop  
**Location**: `doc/domain-models/aggregates/[aggregate-name]-aggregate.md`  
**Sections**:
- Aggregate structure (Mermaid class diagram)
- Aggregate root, entities, value objects
- Business invariants
- State transition rules (Mermaid state machine)
- Commands & events
- Queries
- BDD scenarios
- Aggregate boundary justification

**Example**: See [AGGREGATE-TEMPLATE.md](./AGGREGATE-TEMPLATE.md)

---

### 8. Ubiquitous Language

#### UBIQUITOUS-LANGUAGE-TEMPLATE.md
**Use When**: Creating/updating living glossary (Ubiquitous Language Workshop)  
**Location**: `doc/domain-models/ubiquitous-language.md`  
**Sections**:
- Core entities with definitions
- Value objects
- Commands (operations)
- Domain events
- Business rules
- Banned terms (do not use)
- Terminology conflicts & resolutions
- Changelog

**Example**: See [UBIQUITOUS-LANGUAGE-TEMPLATE.md](./UBIQUITOUS-LANGUAGE-TEMPLATE.md)

---

### 9. Context Maps

#### CONTEXT-MAP-TEMPLATE.md
**Use When**: Mapping bounded context relationships (Context Mapping ceremony)  
**Location**: `doc/domain-models/context-maps/system-context-map.md`  
**Sections**:
- System context map (Mermaid diagram)
- Context catalog
- Context relationships (Published Language, ACL, Conformist, Shared Kernel, etc.)
- Integration patterns
- Context boundaries & ownership
- Evolution & refactoring

**Example**: See [CONTEXT-MAP-TEMPLATE.md](./CONTEXT-MAP-TEMPLATE.md)

---

### 10. Integration Contracts

#### INTEGRATION-CONTRACT-TEMPLATE.md
**Use When**: Defining cross-context integration (Cross-Boundary Integration Testing)  
**Location**: `doc/architecture/integration-contracts/[upstream]-to-[downstream].md`  
**Sections**:
- Integration overview
- Context relationship
- Integration method (events, REST API)
- Event schemas / API contracts
- Anticorruption layer (ACL)
- Failure handling
- Contract testing
- Observability
- Versioning & evolution

**Example**: See [INTEGRATION-CONTRACT-TEMPLATE.md](./INTEGRATION-CONTRACT-TEMPLATE.md)

---

### 11. Example Mapping

#### EXAMPLE-MAP-TEMPLATE.md
**Use When**: Conducting Example Mapping session (Phase 2: Specification)  
**Location**: `doc/scenarios/example-maps/[story-id]-examples.md`  
**Sections**:
- User story
- Rules → Examples → Questions (table format)
- Open questions (track resolutions)
- Scenarios to implement (prioritized)
- Example mapping summary

**Example**: See [EXAMPLE-MAP-TEMPLATE.md](./EXAMPLE-MAP-TEMPLATE.md)

---

### 12. Acceptance Criteria

#### ACCEPTANCE-CRITERIA-TEMPLATE.md
**Use When**: Defining acceptance criteria for user story (Three Amigos)  
**Location**: `doc/scenarios/acceptance-criteria/[story-id].md`  
**Sections**:
- User story
- Acceptance criteria (Given/When/Then)
- Out of scope
- Non-functional requirements
- Test cases (manual + automated)
- Definition of Done checklist

**Example**: See [ACCEPTANCE-CRITERIA-TEMPLATE.md](./ACCEPTANCE-CRITERIA-TEMPLATE.md)

---

### 13. BDD Feature Files

#### FEATURE-TEMPLATE.feature
**Use When**: Writing Gherkin scenarios (Three Amigos, Acceptance Criteria Review)  
**Location**: `features/[feature-name].feature` or `doc/services/<service>/scenarios/`  
**Format**: Gherkin (Given/When/Then)

**Note**: Feature files are **executable specifications** - they serve as both documentation AND test automation. Do NOT create separate "feature charters" - the `.feature` file IS the living documentation.

**Example**: See [FEATURE-TEMPLATE.feature](./FEATURE-TEMPLATE.feature)

---

## When to Use Which

### Use Charter When:
- Starting a new program, service, or product
- Onboarding new team members
- Conducting discovery ceremonies (Event Storming, Domain Modeling)
- Architecture reviews
- Making significant design decisions
- Need comprehensive historical context

### Use Canvas When:
- Daily standups (quick status reference)
- API consumers need to integrate with your service
- Validating NFR compliance
- Weekly status updates
- Leadership reviews (executive summary)
- Quick troubleshooting (runbook links)
- Comparing services side-by-side

---

## Workflow: Charter → Canvas

1. **Start with Charter**: Create comprehensive documentation using charter template
2. **Extract Canvas**: Once charter is complete, distill key info into canvas format
3. **Maintain Both**:
   - Charter: Update on major changes (architecture, scope, API contracts)
   - Canvas: Update weekly or when metrics/dependencies change
4. **Link Them**: Canvas should link to charter for full details

---

## Template Locations

All templates are in: `doc/reference/templates/`

```
templates/
├── PROJECT-CHARTER-TEMPLATE.md         # Program charter (comprehensive)
├── PROJECT-CANVAS-TEMPLATE.md          # Program canvas (quick reference)
├── SERVICE-CHARTER-TEMPLATE.md         # Service charter (comprehensive)
├── MICROSERVICE-CANVAS-TEMPLATE.md     # Service canvas (quick reference)
├── PRODUCT-CHARTER-TEMPLATE.md         # Product charter (strategy & roadmap)
├── PRODUCT-CANVAS-TEMPLATE.md          # Product canvas (one-page summary)
├── MICROFRONTEND-CHARTER-TEMPLATE.md   # Micro-frontend charter (UI component architecture)
├── MICROFRONTEND-CANVAS-TEMPLATE.md    # Micro-frontend canvas (component reference)
├── WEBAPP-CHARTER-TEMPLATE.md          # Web app charter (full application documentation)
├── WEBAPP-CANVAS-TEMPLATE.md           # Web app canvas (application reference)
├── EVENT-STORMING-TEMPLATE.md          # Event storming session output
├── AGGREGATE-TEMPLATE.md               # Domain aggregate documentation
├── UBIQUITOUS-LANGUAGE-TEMPLATE.md     # Living glossary
├── CONTEXT-MAP-TEMPLATE.md             # Bounded context relationships
├── EXAMPLE-MAP-TEMPLATE.md             # Example mapping session output
├── ACCEPTANCE-CRITERIA-TEMPLATE.md     # Story acceptance criteria
├── INTEGRATION-CONTRACT-TEMPLATE.md    # Cross-context contracts
├── POL-TEMPLATE.md                     # Policy record
├── PDR-TEMPLATE.md                     # Product decision record
├── ADR-TEMPLATE.md                     # Architectural decision record
├── FEATURE-TEMPLATE.feature            # BDD scenario (Gherkin)
└── RETROSPECTIVE-TEMPLATE.md           # Sprint retrospective
```

---

## Examples in This Repository

### Program Level
- **Charter**: [CHARTER.md](../../../CHARTER.md) (master program charter with 12 services)
- **Canvas**: [PROJECT-CANVAS-TEMPLATE.md](./PROJECT-CANVAS-TEMPLATE.md) (template - real canvas TBD)

### Service Level
- **Charters**: [doc/exhibits/CHARTER-*.md](../../exhibits/) (12 service charters)
- **Canvases**: Use [MICROSERVICE-CANVAS-TEMPLATE.md](./MICROSERVICE-CANVAS-TEMPLATE.md) to create per-service canvases

---

## Best Practices

### Charter Best Practices
1. **Start with charter template** - Ensures comprehensive coverage
2. **Include Mermaid diagrams** - Visualize dependencies, flows, architecture
3. **Link to decision records** - Reference ADRs, PDRs, POLs for rationale
4. **Version control** - Git track all changes with clear commit messages
5. **Review during ceremonies** - Update charter based on Event Storming, Three Amigos outputs

### Canvas Best Practices
1. **Keep it one page** - If it doesn't fit in a table, it belongs in the charter
2. **Update frequently** - Weekly or when metrics change
3. **Use links liberally** - Canvas points to deeper resources (runbooks, dashboards)
4. **Color code status** - Use emojis or badges (🟢 Green, 🟡 Yellow, 🔴 Red)
5. **Print-friendly** - Canvas should be readable when printed as PDF

---

## Related Documentation

- **Philosophy**: [README.md](../../../README.md) (charter/canvas philosophy section)
- **Reference Guide**: [doc/reference/README.md](../README.md) (template usage)
- **Methodology**: [doc/reference/SBPF/Blending-DDD-BDD-TDD.md](../SBPF/Blending-DDD-BDD-TDD.md) (ceremony integration)
- **Exhibits**: [doc/exhibits/](../../exhibits/) (real-world charter examples)

---

**Last Updated**: December 9, 2025  
**Maintained By**: Architecture Team
