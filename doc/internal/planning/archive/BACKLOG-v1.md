# Product Backlog: MDSlides
## User Stories & Acceptance Criteria

---

```yaml
# MACHINE-READABLE METADATA
backlog:
  product: MDSlides
  version: 1.0.0
  last_updated: 2024-12-19
  owner: Tony Moores, TJM Solutions

status_legend:
  - status: 📋 Ready
    description: Story refined, acceptance criteria defined, ready for implementation
  - status: 🏗️ In Progress
    description: Currently being implemented
  - status: ✅ Done
    description: Implemented, tested, and accepted
  - status: 📝 Draft
    description: Initial draft, needs refinement
  - status: 🔮 Future
    description: Identified but not yet refined
```

---

## Epic 1: Core Slide Deck Creation

**Goal**: Implement the foundational pipeline for parsing Markdown into validated slide decks

**Success Criteria**:
- Parse Markdown with front matter
- Resolve templates from Template Library
- Extract content into slots
- Validate against template constraints
- Produce immutable SlideDeck aggregate

---

### 📋 US-001: Create Title Slide from Markdown

**Status**: 📋 Ready for Implementation
**Priority**: HIGH (P0)
**Estimated Effort**: 2-3 days

**User Story**:
> **As a** slide deck author
> **I want to** create a title slide from Markdown with explicit template binding
> **So that** I can generate a properly validated title slide with title, subtitle, and author

**Business Value**:
Foundation for all slide deck creation - every presentation needs a title slide

**Acceptance Criteria**:

#### Functional Criteria

1. ✅ **AC1**: Given valid markdown with `template: title`, a title slide is created
2. ✅ **AC2**: Title slot is required - validation fails if missing
3. ✅ **AC3**: Title max 2 lines - validation fails if exceeded
4. ✅ **AC4**: Subtitle is optional - validation passes if absent
5. ✅ **AC5**: Subtitle max 2 lines - validation fails if exceeded
6. ✅ **AC6**: Author is optional - validation passes if absent
7. ✅ **AC7**: Author max 80 chars - validation fails if exceeded
8. ✅ **AC8**: Template resolution works via front matter `template: title`
9. ✅ **AC9**: Non-existent template triggers validation error
10. ✅ **AC10**: Markdown formatting preserved in headings (if theme allows)
11. ✅ **AC11**: Unicode mathematical symbols preserved (∑, π, etc.)

#### Technical Criteria

12. ✅ **AC12**: All domain terms from ubiquitous language used in code
13. ✅ **AC13**: Validation returns `Either[NonEmptyList[ValidationError], Slide]`
14. ✅ **AC14**: Flexmark integration uses Anticorruption Layer pattern
15. ✅ **AC15**: All validation errors collected (not fail-fast)
16. ✅ **AC16**: Unit tests for each scenario using ScalaTest with BDD style
17. ✅ **AC17**: Front matter parsed using YAML parser (Docusaurus compatible)

#### Non-Functional Criteria

18. ✅ **AC18**: Parsing + validation completes in < 100ms for typical slide
19. ✅ **AC19**: Error messages are human-readable (no technical jargon)
20. ✅ **AC20**: Pure functional code (no side effects in domain layer)

**Scenarios**: 7 concrete examples documented
- 4 success paths
- 3 failure paths

**Dependencies**:
- Template Library loading mechanism
- YAML front matter parser
- Flexmark Markdown parser
- Validation framework (NonEmptyList[ValidationError])

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-001.md)
- [Example Mapping](doc/specifications/example-mapping-001.md)
- [Template Definition](templates/title.yaml)
- [Domain Model](doc/domain-models/aggregates/slide-deck-aggregate.md)

**Notes**:
- Author specified via front matter only (`author: John Doe`)
- Emoji allowed but MDSlides internal docs avoid them
- Mathematical symbols (∑, π) encouraged
- Theme options control formatting behavior

---

### 📝 US-002: Create Content Slide from Markdown

**Status**: 📝 Draft
**Priority**: HIGH (P0)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** create a standard content slide with heading and body
> **So that** I can present structured information on a slide

**Business Value**:
Most common slide type - needed for 80% of presentation content

**Template**: `templates/content.yaml`
- Heading (required, H2, max 1 line)
- Body (required, markdown_block, max 12 lines, max 150 words)

**Next Steps**:
- Run Three Amigos session
- Define acceptance criteria
- Create example mapping

---

### 📝 US-003: Create Two-Column Comparison Slide

**Status**: 📝 Draft
**Priority**: MEDIUM (P1)
**Estimated Effort**: 2-3 days

**User Story**:
> **As a** slide deck author
> **I want to** create a two-column comparison slide
> **So that** I can show side-by-side comparisons or contrasts

**Business Value**:
Common pattern for pros/cons, before/after, feature comparisons

**Template**: `templates/two-column.yaml`
- Heading (required, H2, max 1 line)
- Left Column (required, markdown_block, max 10 lines, max 75 words)
- Right Column (required, markdown_block, max 10 lines, max 75 words)

**Next Steps**:
- After US-001 and US-002 complete
- Run Three Amigos session

---

### 🔮 US-004: Create Image Slide

**Status**: 🔮 Future
**Priority**: MEDIUM (P1)

**User Story**:
> **As a** slide deck author
> **I want to** create an image-focused slide with caption
> **So that** I can present visual content effectively

**Template**: `templates/image.yaml`

---

### 🔮 US-005: Create Code Snippet Slide

**Status**: 🔮 Future
**Priority**: MEDIUM (P1)

**User Story**:
> **As a** slide deck author
> **I want to** create a code snippet slide with syntax highlighting
> **So that** I can show code examples in presentations

**Template**: `templates/code.yaml`

---

## Epic 2: Multi-Slide Deck Support

**Goal**: Support multiple slides in a single Markdown file

**Success Criteria**:
- Parse slide separators (`---`)
- Create SlideDeck with multiple Slide entities
- Validate deck-level constraints (min 1, max 200 slides)
- Maintain slide order

---

### 📝 US-006: Parse Multi-Slide Markdown File

**Status**: 📝 Draft
**Priority**: HIGH (P0)
**Estimated Effort**: 3 days

**User Story**:
> **As a** slide deck author
> **I want to** create multiple slides in one Markdown file
> **So that** I can author complete presentations in a single document

**Slide Separator**: `---` (triple dash, Marp-compatible)

**Next Steps**:
- After US-001 complete
- Define separation logic

---

## Epic 3: Rendering Pipeline

**Goal**: Transform validated SlideDeck into HTML/PDF output

---

### 🔮 US-007: Render SlideDeck to HTML

**Status**: 🔮 Future
**Priority**: HIGH (P0)

**User Story**:
> **As a** slide deck author
> **I want to** generate HTML from my slide deck
> **So that** I can view my presentation in a browser

**Technology**: Scalatags, theme-driven CSS

---

### 🔮 US-008: Export SlideDeck to PDF

**Status**: 🔮 Future
**Priority**: MEDIUM (P1)

**User Story**:
> **As a** slide deck author
> **I want to** export my slide deck to PDF
> **So that** I can share it as a static document

**Technology**: HTML → PDF converter

---

## Epic 4: CLI Interface

**Goal**: Provide command-line interface for MDSlides

---

### 🔮 US-009: Generate Slides via CLI

**Status**: 🔮 Future
**Priority**: HIGH (P0)

**User Story**:
> **As a** slide deck author
> **I want to** run `mdslides input.md output.html`
> **So that** I can generate slides from the command line

**Technology**: Decline for argument parsing

---

## Epic 5: Validation & Error Reporting

**Goal**: Provide clear, actionable validation errors

---

### 📝 US-010: Human-Readable Validation Errors

**Status**: 📝 Draft
**Priority**: HIGH (P0)

**User Story**:
> **As a** slide deck author
> **I want to** see clear error messages when validation fails
> **So that** I know exactly what to fix in my markdown

**Requirements**:
- Error messages use ubiquitous language
- Include slide ID and slot name
- Suggest fixes where possible

---

## Backlog Statistics

```
Total Stories: 10
├── Ready: 1 (US-001)
├── In Progress: 0
├── Done: 0
├── Draft: 4 (US-002, 003, 006, 010)
└── Future: 5 (US-004, 005, 007, 008, 009)

Priority Breakdown:
├── P0 (High): 6 stories
└── P1 (Medium): 4 stories

Next Sprint Candidates:
1. US-001 (Title Slide) - READY
2. US-002 (Content Slide) - needs refinement
3. US-006 (Multi-Slide) - needs refinement
```

---

## Definition of Ready (DoR)

A story is "Ready" when:
- ✅ User story format complete (As a/I want/So that)
- ✅ Business value clearly stated
- ✅ Acceptance criteria defined (functional + technical + non-functional)
- ✅ Three Amigos session completed
- ✅ Example Mapping done (all questions resolved)
- ✅ Dependencies identified
- ✅ Testable scenarios documented

---

## Definition of Done (DoD)

A story is "Done" when:
- ✅ All acceptance criteria met
- ✅ Unit tests written (BDD style with ScalaTest)
- ✅ Property-based tests (where applicable)
- ✅ Code follows ubiquitous language
- ✅ Pure functional (no side effects in domain)
- ✅ Documentation updated
- ✅ Peer review completed (self-review for solo dev)

---

**Last Updated**: 2024-12-19
**Product Owner**: Tony Moores, TJM Solutions
**Backlog Grooming Cadence**: Weekly (or as needed for solo dev)
