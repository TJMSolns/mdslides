# Marp-Inspired Slide Authoring System with Rails, Templates, and Automation

## 1. Purpose and Vision

This project aims to create a **Markdown-based slide authoring system** inspired by Marp but with stronger **rails** around:

* Content structure and consistency
* Visual standards (fonts, sizes, themes)
* GitHub-compatible Markdown
* Diagrams and tables
* Automation via **CLI, VS Code plugin, agents, and LLMs**

The end goal is a system that:

* Feels as simple as “Markdown in, slides out”
* Provides **opinionated guardrails** so slides are readable and consistent
* Is easily orchestrated by tools (LLMs, agents, CI pipelines, CLIs)

## 2. High-Level Goals

1. **Slide templates as first-class citizens**

   * Comparable to PowerPoint/Google Slides templates and layouts
   * Templates are declarative, versionable, and callable by name/ID

2. **Authoring rails / linting / validation**

   * Enforce “content fits on slide” heuristics
   * Enforce GitHub-flavored Markdown compatibility
   * Enforce consistent use of headings, tables, code blocks, and Mermaid diagrams

3. **Multi-surface tooling**

   * **CLI** for generation, validation, and CI integration
   * **VS Code extension** for interactive authoring and preview
   * Stable JSON/CLI contract so **LLMs and agents** can orchestrate workflows

4. **Interoperability with Marp and similar tools**

   * Able to emit Marp-compatible Markdown and/or HTML where useful
   * Possible to incrementally adopt this system on top of existing Marp decks

## 3. Core Concepts

### 3.1 Slides as Structured Units

Each slide is:

* A **delimited block** in Markdown (e.g., `---` separators)
* Annotated with:

  * A **template** reference
  * Optional **metadata** (`id`, `tags`, `speaker_notes`, etc.)

Example:

```markdown
--- slide: title
id: intro
tags: [overview]

# Project Name
## Subtitle

Author Name
```

### 3.2 Templates vs Themes

* **Template**

  * Defines the **content structure**: expected elements and their roles.
  * Example: “title slide”, “two-column comparison”, “code + notes”.

* **Theme**

  * Defines **visual style**: fonts, sizes, colors, spacing.

The system separates **what goes where** (template) from **how it looks** (theme), similar to PowerPoint layouts + slide masters.

### 3.3 Rails

Rails are enforced through:

* **Lint rules** (Markdown, slide structure, template adherence)
* **Validation rules** (content density, required fields)
* **Style rules** (heading levels, code block conventions, mermaid usage patterns)

Rails should be configurable per project but have **sane defaults**.

## 4. Slide Templates

### 4.1 Template Definition Format

Templates are defined in a declarative YAML or JSON format, e.g.:

```yaml
id: title
name: Title Slide
description: Primary title slide with subtitle and author.
slots:
  - name: title
    type: markdown_block
    required: true
    constraints:
      max_lines: 2
      recommended_heading_level: 1
  - name: subtitle
    type: markdown_block
    required: false
    constraints:
      max_lines: 2
  - name: author
    type: markdown_inline
    required: false
    constraints:
      max_chars: 80
```

The engine:

* Knows which slots exist
* Enforces constraints when rendering or linting
* Can auto-generate boilerplate content from templates

### 4.2 Authoring Templates in Markdown

Authors can either:

1. Use **front-matter-like directives** to bind the slide to a template:

   ```markdown
   --- slide: title
   title: Project X
   subtitle: A Marp-Inspired Authoring System
   author: Tony Moores
   ---
   ```

2. Or use **inline markers** to indicate slot content if needed:

   ```markdown
   --- slide: comparison
   @slot(title)
   # Approaches Compared

   @slot(left_column)
   - Option A
   - Option B

   @slot(right_column)
   - Pros/Cons
   - Tradeoffs
   ---
   ```

The **parser** maps these to template slots and validates them.

### 4.3 Managing Template Libraries

* Templates live in a known directory (e.g., `templates/`).

* Projects can declare which template set they use:

  ```yaml
  slide_config:
    templates_dir: ./templates
    default_template: content
  ```

* Templates are versionable and can be shared across projects.

## 5. Authoring Rails: Linting and Validation

### 5.1 GitHub Markdown Compliance

Rails around Markdown:

* Enforce GitHub-flavored Markdown via:

  * Line length rules
  * Heading style rules
  * Code block style rules
  * Table formatting rules
* Provide a configuration file (e.g., `.slides-mdlint.json`) that:

  * Wraps `markdownlint` or similar tool
  * Adds slide-specific rules

### 5.2 Slide Density / “Fits on Slide” Heuristics

The system provides a **lint/validate** subcommand to approximate whether each slide is overloaded.

Heuristics per slide:

* Max characters, words, or lines per slot (template-driven)
* Warnings for:

  * Too many bullet points
  * Excessive nested lists
  * Overly long paragraphs

Output example:

```text
Slide intro (template: title)
  OK

Slide problem-context (template: content)
  WARNING: body slot has 230 words (recommended <= 150)
  SUGGESTION: consider splitting into two slides.
```

These heuristics are customizable in config or per-template.

### 5.3 Tables

Rails for tables:

* Validate tables conform to a **canonical style**:

  * Header row required
  * Aligned pipe structure `|---|---|`
  * Max columns and max rows configurable
* Optionally “pretty print” tables via a CLI formatting command:

  * Normalizes spacing
  * Ensures GitHub-compatible rendering

### 5.4 Mermaid Diagrams

Rails for diagrams:

* Require diagrams to be inside fenced ` ```mermaid ` blocks.
* Validate diagrams with:

  * Optional integration to Mermaid CLI for syntax checking
* Enforce:

  * Max complexity (e.g., node count) per slide
  * Allowed diagram types (flowchart, sequence, etc., configurable)

## 6. Theming: Fonts and Sizes by Usage

Themes define:

* **Base font family** and sizes for:

  * Title
  * Subtitle
  * Headings (H1/H2/H3)
  * Body text
  * Tables
  * Code blocks
* **Spacing and layout rules**:

  * Padding/margins
  * Line-height
  * Bullet indentation

Example theme snippet (conceptual):

```yaml
id: default
fonts:
  base_family: "Inter, system-ui, sans-serif"
  title_size: 52
  subtitle_size: 40
  h1_size: 44
  h2_size: 36
  body_size: 28
  table_size: 22
  code_size: 24

layout:
  slide_padding: 40
  max_body_lines: 12
  max_table_rows: 8
```

Theme files can be rendered to:

* Marp-compatible CSS
* HTML/CSS for custom engines
* PDF via a headless browser

## 7. CLI Design

### 7.1 Core Commands

Proposed CLI name: `slides` (placeholder).

Commands:

* `slides init`

  * Scaffold a new slide project with:

    * Default config
    * Example templates
    * Example themes
* `slides new <name>`

  * Create a new deck from a base template.
* `slides lint <file|glob>`

  * Run Markdown + slide + template + theme validations.
* `slides render <file>`

  * Render deck to:

    * HTML
    * PDF
    * PPTX
* `slides check <file>`

  * Validate “fits on slide” constraints, return machine-readable JSON.
* `slides list-templates`

  * List available templates.
* `slides list-themes`

  * List available themes.

### 7.2 Machine-Readable Output for Agents/LLMs

All commands support:

* `--json` flag to output structured results:

  * Lint errors
  * Slide metadata
  * Template usage

This enables:

* LLMs/agents to:

  * Inspect decks
  * Propose fixes
  * Generate patches

Example JSON lint output:

```json
{
  "file": "deck.md",
  "slides": [
    {
      "id": "intro",
      "template": "title",
      "issues": []
    },
    {
      "id": "problem-context",
      "template": "content",
      "issues": [
        {
          "severity": "warning",
          "code": "density.max_words",
          "message": "Body has 230 words, recommended <= 150."
        }
      ]
    }
  ]
}
```

## 8. VS Code Extension Design

### 8.1 Core Capabilities

The VS Code extension should provide:

* Live preview (Marp-style or custom renderer)
* Template-aware snippets:

  * Insert slide by template ID
  * Insert template-specific slots
* Linting integration:

  * Surface CLI lint results inline in the editor
  * Quick fixes where possible
* Command palette integration:

  * “Validate slides”
  * “Render deck”
  * “Switch theme”
  * “Switch template for current slide”

### 8.2 LLM-Oriented UX Hooks

Optional features:

* Commands that output the structured representation of the current slide or deck:

  * “Show slide AST”
  * “Show template binding”
* These can be used by external agents to:

  * Analyze deck structure
  * Propose edits or reorganizations

## 9. LLMs, Agents, and Services

### 9.1 LLM-Orchestrated Workflows

LLMs or agents can:

* Generate new decks using:

  * Template IDs (e.g., “build a deck with [title, agenda, 3 x content, summary] templates”)
* Call CLI commands to:

  * `init`, `lint`, `render` decks
* Read JSON outputs and:

  * Suggest content restructuring when slides are too dense
  * Fix formatting that breaks Markdown/Marp

### 9.2 Service Integration

Potential service endpoints:

* `POST /lint`

  * Input: deck source
  * Output: lint results in JSON
* `POST /render`

  * Input: deck source + desired format
  * Output: URL / artifact reference
* `POST /structure`

  * Input: deck source
  * Output: AST / slide/slot representation

Services provide a clean boundary for:

* CI workflows
* UX shells (web UI, internal tools)
* LLM-based agents calling them via API rather than shell commands.

## 10. Implementation Notes and Non-Goals

### 10.1 Implementation Leanings

* Parsing:

  * Likely Markdown + a thin directive/slot layer on top
* Rendering:

  * Initially can target Marp-compatible HTML/CSS
  * Later, custom renderer with more control
* Linting:

  * Wrap existing Markdown linters where possible
  * Add slide/template-specific rules in our own engine

### 10.2 Non-Goals (Initial Phase)

* WYSIWYG editor in the browser (Markdown + preview is enough)
* Full clone of PowerPoint feature set
* Deep interactive content (live code, embedded iframes, etc.) beyond simple HTML/Markdown patterns
