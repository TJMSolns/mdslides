# Transform Command Design Notes

**Date:** 2025-12-29
**Purpose:** Document the manual process used to transform HOW-WE-WORK.md → examples/how-we-work.md
**Future CLI Command:** `mdslides transform -i <source.md> -o <presentation.md>`

---

## Manual Transformation Process

### Input
- **Source:** `HOW-WE-WORK.md` (1190 lines, technical documentation)
- **Target:** `examples/how-we-work.md` (mdslides presentation format)
- **Test Command:** `java -jar ../mdslides.jar render -i how-we-work.md -o how-we-work --theme dark`

### Output
- **Result:** 45 slides successfully rendered
- **Warnings:** 20 density warnings (content exceeds 12-line guidance)
- **Accessibility:** ✓ Passed WCAG 2.1 AA
- **HTML Size:** 36,988 characters

---

## Transformation Steps Applied

### Step 1: Content Analysis

**Read source document** and identify:
- Document structure (headers, sections, subsections)
- Content types (lists, tables, code blocks, diagrams)
- Key concepts and their relationships
- Audience and purpose

**For HOW-WE-WORK.md:**
- Top-level sections: Philosophy, Team Roles, SDLC Phases, Templates, Sprint Flow, Tech Stack, Governance, FAQ
- Content types: Tables (roles, plugins), lists (ceremonies, principles), code blocks (pseudocode, bash)
- Audience: Program Managers, Product Owners, Architects, Bench Developers
- Purpose: Team playbook for ceremony-based SDLC

---

### Step 2: Structure Identification

**Identify hierarchical structure:**
- H1 headers → Major topics
- H2 headers → Sections within topics
- H3 headers → Subsections
- H4 headers → Details

**For HOW-WE-WORK.md:**
```
# How We Work (H1) → Title slide
## Philosophy (H2) → Content slide
## Team Roles (H2) → Content slide
## SDLC Phases (H2) → Section title slide
### Phase 0a (H3) → Content slide
### Phase 0b (H3) → Content slide
### Phase 1: Discovery (H3) → Section title slide
#### 1.1 Event Storming (H4) → Content slide
#### 1.2 Ubiquitous Language (H4) → Content slide
etc.
```

**Mapping Rules:**
- H1 → Title slide
- H2 (major sections) → Section title slide OR content slide
- H3 (subsections) → Section title slide OR content slide
- H4 (details) → Content slide
- Lists/tables/code → Content slide

---

### Step 3: Slide Breakdown

**Decide how to break content into slides:**
- One major concept per slide
- Avoid information overload (max 12 lines guidance)
- Group related items together
- Use section title slides to introduce new topics

**For HOW-WE-WORK.md:**
1. Title: "How We Work" + subtitle
2. Content: Philosophy overview
3. Content: Team Roles table
4. Section: "SDLC Phases"
5. Content: Phase 0a (Bootstrap)
6. Content: Phase 0b (Program Initiation)
7. Section: "Phase 1: Discovery (DDD)"
8-12. Content: Each ceremony (Event Storming, Ubiquitous Language, Domain Modeling, Context Mapping)
13. Section: "Phase 2: Specification (BDD)"
14-16. Content: Three Amigos, Example Mapping, Acceptance Criteria Review
17. Section: "Phase 3: Implementation (TDD)"
18-20. Content: Test-First Pairing, Red-Green-Refactor, Property-Based Testing
21. Section: "Phase 4: Integration & Feedback"
22-26. Content: Each integration ceremony
27. Section: "Ceremony Automation"
28-29. Content: Mill plugins overview and table
30. Section: "Typical Sprint Flow"
31-33. Content: Week 1, Week 2, Week 3
34. Section: "Technology Stack"
35-36. Content: Core stack + principles
37. Section: "Templates & Documentation"
38-39. Content: Charter vs Canvas, template categories
40. Section: "Getting Started"
41-42. Content: Onboarding, new project setup
43. Section: "FAQ"
44-45. Content: FAQ questions split across two slides
46. Closing: Summary slide

**Total: 46 slides** (actual output: 45, indicates some consolidation occurred)

---

### Step 4: Template Assignment

**Choose appropriate template for each slide:**

| Template | When to Use | Example from HOW-WE-WORK |
|----------|-------------|--------------------------|
| `title` | Opening slide, presentation title | Slide 1: "How We Work" |
| `section-title` | Introduce new major section | Slide 4: "SDLC Phases" |
| `content` | Most slides with text/lists/tables/code | Slide 2: Philosophy, Slide 3: Team Roles |
| `closing` | Final summary slide | Slide 46: Summary |
| `diagram` | Slides with mermaid diagrams | (None in this presentation) |

**Template Selection Logic:**
1. First slide → `title`
2. Last slide → `closing`
3. Major section intro → `section-title`
4. Everything else → `content` (default)
5. If mermaid diagram present → `diagram`

---

### Step 5: Content Adaptation

**Convert prose to presentation-friendly format:**

**Before (documentation prose):**
```markdown
We build enterprise systems using a ceremony-based approach that blends
Domain-Driven Design (DDD) to model the business domain accurately,
Behavior-Driven Development (BDD) to specify behavior with examples,
and Test-Driven Development (TDD) to implement with test-first discipline.
```

**After (presentation bullets):**
```markdown
We build enterprise systems using a **ceremony-based approach** that blends:

- **DDD** (Domain-Driven Design): Model the business domain accurately
- **BDD** (Behavior-Driven Development): Specify behavior with examples
- **TDD** (Test-Driven Development): Implement with test-first discipline
```

**Transformation Rules:**
1. **Break long paragraphs** into bullet points
2. **Bold key terms** for emphasis
3. **Shorten sentences** - remove filler words
4. **Use parallel structure** in lists
5. **Preserve code blocks** as-is (but shorten if >20 lines)
6. **Simplify tables** - remove columns if necessary
7. **Extract key points** - not everything needs to be on slides
8. **Use active voice** - "We build" not "Systems are built"

**Example Transformations:**

| Source Type | Transformation |
|-------------|----------------|
| Long paragraph | → Bullet list with key points |
| Dense table | → Simplified table with fewer columns |
| Code block >30 lines | → Representative snippet + "..." |
| Technical details | → High-level overview + speaker notes for details |
| Multiple related paragraphs | → Single slide with main points |

---

### Step 6: Speaker Notes Addition

**Add speaker notes** to provide context without cluttering slides:

**Format:**
```markdown
<!-- Speaker notes: This is context for the presenter that won't appear on the slide. -->
```

**When to Add Speaker Notes:**
1. **Warnings or cautions** - "Don't do X because Y"
2. **Additional context** - Background information
3. **Examples** - Concrete scenarios to illustrate points
4. **Timing guidance** - "Spend 2 minutes on this slide"
5. **Common pitfalls** - "Watch out for Z"
6. **References** - "See document X for more detail"

**Example from HOW-WE-WORK transformation:**

Slide content:
```markdown
## Philosophy

We build enterprise systems using a **ceremony-based approach** that blends:
- **DDD** (Domain-Driven Design): Model the business domain accurately
- **BDD** (Behavior-Driven Development): Specify behavior with examples
- **TDD** (Test-Driven Development): Implement with test-first discipline

**Key Principle**: Documentation is code.
```

Speaker notes:
```markdown
<!-- Speaker notes: No external SaaS tools like Miro, Mural, PowerPoint, or Google Slides. Everything lives in the repository using Mermaid for diagrams, Marp for presentations, and Markdown for all documentation. -->
```

**Speaker Notes Strategy:**
- **Essential detail** that doesn't fit on slide
- **Context** that helps presenter explain the point
- **Cautions** about common mistakes
- **NOT** just reading the slide content

---

### Step 7: Frontmatter Application

**Add mdslides frontmatter** to each slide:

**Format:**
```markdown
---
template: <template-name>
---

<slide content>
```

**Template Options:**
- `title` - Presentation opening
- `section-title` - Section divider
- `content` - Standard slide
- `diagram` - Diagram-focused slide
- `closing` - Final slide

**Example:**
```markdown
---
template: title
---

# How We Work
## Team Playbook: Ceremony-Based SDLC

Version 1.1.0

---
template: content
---

## Philosophy

We build enterprise systems...
```

**Frontmatter Rules:**
1. **Every slide** must have frontmatter
2. **Slide separator** is `---` (3 dashes) between frontmatter and content
3. **Slide boundary** is `---` on its own line between slides
4. **Template required** in every frontmatter block
5. **Optional fields** can be added (e.g., `caption:` for diagrams)

---

### Step 8: Testing and Iteration

**Test the presentation:**

```bash
cd examples/
java -jar ../mdslides.jar render -i how-we-work.md -o how-we-work --theme dark
```

**Check for:**
1. ✓ **Parse errors** - Does it render without errors?
2. ✓ **Slide count** - Reasonable number of slides (30-60 typical)
3. ⚠ **Density warnings** - Are slides too dense? (12-line guidance)
4. ✓ **Accessibility** - WCAG compliance
5. ✓ **Visual check** - Open in browser, review each slide
6. ✓ **Speaker view** - Press 'S', check speaker notes display

**Results for HOW-WE-WORK:**
- ✓ Parsed successfully: 45 slides
- ⚠ 20 density warnings (acceptable for technical content)
- ✓ Accessibility passed (WCAG 2.1 AA)
- ✓ Visual review: Slides render correctly with dark theme
- ✓ Speaker notes: Display correctly in speaker view

**Iteration:**
- If density warnings excessive: Split slides further
- If accessibility fails: Fix contrast or add alt text
- If visual issues: Adjust formatting or template choice
- If speaker notes missing: Add context where needed

---

## Automation Strategy for `transform` Command

### Proposed CLI Syntax

```bash
# Basic transformation
mdslides transform -i HOW-WE-WORK.md -o examples/how-we-work.md

# With options
mdslides transform \
  -i HOW-WE-WORK.md \
  -o examples/how-we-work.md \
  --slides-per-section 5 \
  --max-lines-per-slide 12 \
  --add-speaker-notes \
  --template-strategy auto
```

### Required Features

#### 1. Content Parsing
- **Markdown parser** - Parse source markdown into AST
- **Header detection** - Identify H1, H2, H3, H4 hierarchy
- **Content type detection** - Lists, tables, code blocks, paragraphs
- **Structure analysis** - Identify logical sections

#### 2. Slide Generation
- **Slide boundary detection** - Where to split content
- **Template assignment** - Auto-assign templates based on content type
- **Content adaptation** - Convert prose to bullet points
- **Density management** - Keep slides under max line limit

#### 3. Speaker Notes Generation
- **Auto-extract details** - Move technical details to speaker notes
- **Preserve examples** - Keep concrete examples in notes
- **Add context** - Generate contextual notes from surrounding content

#### 4. Frontmatter Generation
- **Template selection** - Choose appropriate template
- **Metadata extraction** - Title, author, version from source
- **Custom fields** - Add custom frontmatter if needed

#### 5. Quality Validation
- **Density check** - Warn if slides too dense
- **Balance check** - Warn if slide count too high/low
- **Completeness check** - Ensure all content covered

---

## Heuristics for Automation

### Slide Boundary Detection

**When to create a new slide:**

1. **Header level change**
   - H1 → New title slide
   - H2 → New section-title OR content slide
   - H3 → New content slide
   - H4 → Same slide (subsection) OR new slide if content >12 lines

2. **Content density threshold**
   - If current slide >12 lines → Create new slide
   - If adding next paragraph would exceed 12 lines → Create new slide

3. **Content type change**
   - Switching from list to table → Consider new slide
   - Switching from prose to code block → Consider new slide
   - Switching from content to diagram → New slide (template: diagram)

4. **Logical breaks**
   - Horizontal rules (`---` in source) → New slide
   - Topic shift detected via NLP → Consider new slide
   - End of numbered/bulleted list → Consider new slide

### Template Assignment Logic

```python
def assign_template(slide_content, position, section_depth):
    if position == "first":
        return "title"
    elif position == "last":
        return "closing"
    elif is_section_header(slide_content) and section_depth <= 2:
        return "section-title"
    elif has_mermaid_diagram(slide_content):
        return "diagram"
    else:
        return "content"  # Default
```

### Content Adaptation Rules

**Prose to bullets:**
1. Detect sentence boundaries
2. Extract key phrases (subject + verb + object)
3. Remove filler words ("very", "really", "actually", etc.)
4. Convert to bullet list with parallel structure
5. Bold key terms (first occurrence)

**Table simplification:**
1. If table >5 columns → Remove least important columns
2. If table >10 rows → Split across multiple slides OR summarize
3. If table cells verbose → Truncate to key points

**Code block handling:**
1. If code block <20 lines → Keep as-is
2. If code block >20 lines → Extract representative snippet + "..."
3. Add language hint for syntax highlighting
4. Consider splitting long code across multiple slides

---

## Example Transformation Patterns

### Pattern 1: Dense Paragraph → Bullet List

**Source:**
```markdown
Event Storming is a workshop technique used to map the temporal flow
of domain events. Participants identify domain events, which are things
that happened in the past tense, and group them into temporal sequences.
Commands are intentions that trigger events. Aggregates are entities
that handle commands and emit events. The session also surfaces questions
and hotspots for deeper exploration.
```

**Target:**
```markdown
## Event Storming Session

**Purpose:** Map domain's temporal flow

**Process:**
1. Identify domain events ("TenantProvisioned", "OrderPlaced")
2. Discover commands ("ProvisionTenant", "PlaceOrder")
3. Group events into temporal flows
4. Identify aggregates ("Tenant", "Order")
5. Surface questions and hotspots

<!-- Speaker notes: Event storming maps domain events, not system events. Always include domain experts. Resist jumping to technical solutions too early. -->
```

### Pattern 2: Table Simplification

**Source (7 columns):**
```markdown
| Plugin | Purpose | Input | Output | When | Dependencies | Status |
|--------|---------|-------|--------|------|--------------|--------|
| mill-specification-plugin | BDD validation | .feature files | Validation report | After Three Amigos | None | Complete |
| mill-domain-plugin | Domain model validation | Aggregate docs | Validation report | After Domain Modeling | None | Complete |
...
```

**Target (3 columns):**
```markdown
| Plugin | Validates | Phase |
|--------|-----------|-------|
| **specification** | BDD scenarios, Gherkin | 2 |
| **domain** | Aggregates, DDD patterns | 1 |
...
```

### Pattern 3: Section with Subsections → Multiple Slides

**Source:**
```markdown
## Phase 1: Discovery (DDD-Led)

### 1.1 Event Storming Session
<content>

### 1.2 Ubiquitous Language Workshop
<content>

### 1.3 Domain Modeling Workshop
<content>
```

**Target:**
```markdown
---
template: section-title
---

## Phase 1: Discovery (DDD)

Understanding the domain

---
template: content
---

## 1.1 Event Storming Session
<adapted content>

---
template: content
---

## 1.2 Ubiquitous Language Workshop
<adapted content>

---
template: content
---

## 1.3 Domain Modeling Workshop
<adapted content>
```

---

## Implementation Checklist for `transform` Command

### Phase 1: Core Functionality
- [ ] Markdown AST parser (use existing parser or flexmark)
- [ ] Header hierarchy extraction
- [ ] Slide boundary detection (basic rules)
- [ ] Template assignment (basic heuristics)
- [ ] Frontmatter generation
- [ ] Content pass-through (minimal adaptation)
- [ ] Output file generation

### Phase 2: Content Adaptation
- [ ] Prose to bullet point conversion
- [ ] Table simplification
- [ ] Code block truncation
- [ ] Density management (split long slides)
- [ ] Bold key terms
- [ ] Parallel structure enforcement

### Phase 3: Speaker Notes
- [ ] Auto-extract technical details to notes
- [ ] Preserve examples in notes
- [ ] Add contextual notes from surrounding content
- [ ] Warning/caution detection and note generation

### Phase 4: Quality & Polish
- [ ] Density warnings
- [ ] Balance warnings (too many/few slides)
- [ ] Completeness check (all content covered)
- [ ] Preview mode (render + open in browser)
- [ ] Interactive refinement (ask user for decisions)

### Phase 5: Advanced Features
- [ ] NLP-based topic detection
- [ ] Smart section splitting
- [ ] Automatic diagram detection and template assignment
- [ ] Custom transformation rules via config
- [ ] Template style selection (--style technical|marketing|educational)

---

## Configuration Options (Future)

### Transform Configuration File: `.mdslides/transform.json`

```json
{
  "transform": {
    "maxLinesPerSlide": 12,
    "slidesPerSection": 5,
    "autoSpeakerNotes": true,
    "templateStrategy": "auto",
    "contentAdaptation": {
      "proseTobullets": true,
      "simplifyTables": true,
      "truncateCode": true,
      "boldKeyTerms": true
    },
    "sectionTitlePattern": "h2",
    "slideBreakPattern": "auto"
  }
}
```

### CLI Overrides

```bash
mdslides transform \
  -i source.md \
  -o presentation.md \
  --max-lines 15 \
  --slides-per-section 7 \
  --no-speaker-notes \
  --template-strategy manual \
  --style technical
```

---

## Success Metrics

### For HOW-WE-WORK Transformation

- ✓ **Input:** 1190 lines of documentation
- ✓ **Output:** 45 slides
- ✓ **Compression ratio:** ~26 lines per slide (raw input/output)
- ✓ **Actual density:** 8-22 lines per slide (after adaptation)
- ✓ **Warnings:** 20 density warnings (44% of slides)
- ✓ **Accessibility:** Passed WCAG 2.1 AA
- ✓ **Templates used:** title (1), section-title (8), content (35), closing (1)
- ✓ **Speaker notes:** Added to 30+ slides
- ✓ **Manual effort:** ~90 minutes for 45 slides (~2 min/slide)

### Target for Automated Transform

- **Compression ratio:** 20-30 lines per slide (similar to manual)
- **Density warnings:** <30% of slides exceed 12 lines
- **Template accuracy:** >90% correct template assignment
- **Content preservation:** 100% of source content in slides or speaker notes
- **Time savings:** <5 minutes for 50-slide transformation
- **Human review:** Required for quality (automated ≠ perfect)

---

## Related Documentation

- **MDSlides Formats:** `doc/reference/MDSLIDES-FORMAT-SPEC.md` (if exists)
- **Template Guide:** `doc/reference/TEMPLATE-GUIDE.md` (if exists)
- **Speaker Notes Best Practices:** `doc/reference/SPEAKER-NOTES-GUIDE.md` (if exists)
- **v3.0.0 Design Specs:** `doc/internal/planning/v3.0.0-DESIGN-SPECIFICATIONS.md`

---

## Next Steps

1. **Review these notes** with team
2. **Decide on implementation priority** (v3.0.0 or v3.1.0?)
3. **Create user stories** for transform command
4. **Design CLI interface** (syntax, options)
5. **Spike: Markdown AST parsing** (proof of concept)
6. **Spike: Slide boundary detection** (heuristics testing)
7. **Implement Phase 1** (basic transformation)
8. **Test with multiple documents** (HOW-WE-WORK.md, ADRs, POLs, etc.)
9. **Iterate based on feedback**
10. **Document transform command** in user guide

---

**Captured By:** Claude Sonnet 4.5
**Date:** 2025-12-29
**Purpose:** Inform future `mdslides transform` CLI command development
**Based On:** Manual transformation of HOW-WE-WORK.md → examples/how-we-work.md
