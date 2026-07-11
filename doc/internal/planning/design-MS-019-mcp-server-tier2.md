# MS-019: MCP Server Tier 2 — Sequence Diagrams (Pre-scaffold Gate)

**Status:** Pre-scaffold gate — sequence diagrams for review before implementation
**Related:** ADR-013 (file-in/file-out architecture, Tier 2 capability surface), MS-019 (WORK-QUEUE), MS-012 (Tier 1 — same pattern)
**Date:** 2026-07-11

---

## list_themes — Happy Path

```mermaid
sequenceDiagram
    participant C as MCP Client (Claude Code)
    participant S as McpServer (mcp module)
    participant P as ListThemesTool
    participant T as ThemeLoader (infrastructure)

    C->>S: stdin: tools/call {name: "list_themes", themes_dir?}
    S->>P: dispatch(ToolRequest)
    P->>P: builtins = List("light", "dark", "corporate")
    P->>P: resolveThemesDir(params.themesDir) → Path (default "./themes")
    P->>T: ThemeLoader.listAvailableThemes(themesDir) → IO[List[String]]
    T-->>P: List("acme", "tjmsolutions") [empty if dir missing — not an error]
    P->>P: themes = (builtins ++ directoryThemes).distinct.sorted
    P-->>S: ThemesResult(themes)
    S->>C: stdout: {result: ThemesResult}
```

**Notes:**
- Built-in themes (`light`, `dark`, `corporate`) are always included — they require no directory.
- `themes_dir` is optional; defaults to `./themes` (same default as CLI `--theme-dir`).
- A missing or nonexistent `themes_dir` is NOT an error — `ThemeLoader.listAvailableThemes` already returns `Nil` for a missing directory (see `infrastructure/.../theme/ThemeLoader.scala:98-111`), so the result degrades to built-ins only.
- A directory theme is valid only if it is a directory containing `theme.json` — same rule `ThemeLoader` already enforces.

---

## list_themes — Primary Error Path

```mermaid
sequenceDiagram
    participant C as MCP Client
    participant S as McpServer
    participant P as ListThemesTool

    Note over C,P: Error Path — themes_dir exists but is not readable (permissions)

    C->>S: stdin: tools/call {name: "list_themes", themes_dir: "/root/themes"}
    S->>P: dispatch(ToolRequest)
    P->>P: ThemeLoader.listAvailableThemes(Paths.get("/root/themes"))
    P->>P: Files.list(themesDir) → throws AccessDeniedException
    P-->>S: Left(McpError("Cannot read themes directory: /root/themes"))
    S->>C: stdout: {error: {code: -32603, message: "Cannot read themes directory: /root/themes"}}
```

**Notes:**
- There is deliberately no "not found" error path for `list_themes` — a missing directory is a valid, empty result (see happy-path note above). Only I/O failures on an existing-but-unreadable directory are errors.

---

## get_deck_info — Happy Path

```mermaid
sequenceDiagram
    participant C as MCP Client
    participant S as McpServer
    participant P as GetDeckInfoTool
    participant F as FlexmarkAdapter
    participant M as MarkdownParser

    C->>S: stdin: tools/call {name: "get_deck_info", input_path}
    S->>P: dispatch(ToolRequest)
    P->>P: Files.exists(input_path) → true
    P->>P: Files.readString(input_path) → markdownText
    P->>M: MarkdownParser.parse(markdownText) → SlideDeck
    M-->>P: Right(SlideDeck)
    P->>P: templatesUsed = deck.slides.map(_.templateName).distinct.sorted
    P->>F: slots.values.map(FlexmarkAdapter.parseInlineFormatting) (per slide, per slot)
    F-->>P: FormattedContent(contentImages, content: List[DiagramElement | ...])
    P->>P: imagesReferenced = contentImages.map(_.url) ++ backgroundImage paths, distinct
    P->>P: hasMermaidDiagrams = any FormattedContent.content contains DiagramElement
    P-->>S: DeckInfo(slideCount, templatesUsed, imagesReferenced, hasMermaidDiagrams)
    S->>C: stdout: {result: DeckInfo}
```

**Notes:**
- Read-only: does not validate structure/content and does not render — deliberately cheaper than `validate_deck`.
- `images_referenced` combines inline content images (parsed per-slot via `FlexmarkAdapter.parseInlineFormatting`, same helper `RenderDeckTool`/CLI `Main.extractImageUrls` already use) and per-slide `backgroundImage` overrides (string or `BackgroundConfig`), deduplicated — mirrors `cli.Main.extractImageUrls` logic without depending on the `cli` module (ADR-013: `mcp` has no compile-time dependency on `cli`).
- `has_mermaid_diagrams` is derived by scanning parsed slot content for `DiagramElement`, not by checking `templateName == "diagram"` — a deck can reference Mermaid outside the dedicated diagram template.

---

## get_deck_info — Primary Error Paths

```mermaid
sequenceDiagram
    participant C as MCP Client
    participant S as McpServer
    participant P as GetDeckInfoTool
    participant M as MarkdownParser

    Note over C,M: Error Path A — Input file not found

    C->>S: stdin: tools/call {name: "get_deck_info", input_path: "missing.md"}
    S->>P: dispatch(ToolRequest)
    P->>P: Files.exists(input_path) → false
    P-->>S: Left(McpError("Input file not found: missing.md"))
    S->>C: stdout: {error: {code: -32602, message: "Input file not found: missing.md"}}

    Note over C,M: Error Path B — Markdown parse fails (fatal structure error)

    C->>S: stdin: tools/call {name: "get_deck_info", input_path: "broken.md"}
    S->>P: dispatch(ToolRequest)
    P->>P: Files.readString(input_path) → markdownText
    P->>M: MarkdownParser.parse(markdownText)
    M-->>P: Left(ParseError("No slides found in deck"))
    P-->>S: Left(McpError("Parse failed: No slides found in deck"))
    S->>C: stdout: {error: {code: -32603, message: "Parse failed: No slides found in deck"}}
```

**Error code conventions (JSON-RPC 2.0, unchanged from MS-012):**
- `-32700` — Parse error (malformed JSON in request)
- `-32602` — Invalid params (input file not found, bad param types)
- `-32603` — Internal error (I/O failure, deck parse failure, unexpected exception)

---

## MCP Protocol Layer — tools/list Additions

```json
{
  "name": "list_themes",
  "description": "List available built-in and directory-based themes.",
  "inputSchema": {
    "type": "object",
    "properties": {
      "themes_dir": {
        "type": "string",
        "description": "Directory containing directory-based themes (default: ./themes)"
      }
    },
    "required": []
  }
}
```

```json
{
  "name": "get_deck_info",
  "description": "Inspect a markdown slide deck without rendering: slide count, templates used, images referenced, and whether it contains Mermaid diagrams.",
  "inputSchema": {
    "type": "object",
    "properties": {
      "input_path": {
        "type": "string",
        "description": "Absolute or relative path to the .md deck file"
      }
    },
    "required": ["input_path"]
  }
}
```

**Result shapes (per ADR-013):**

```json
{"themes": ["corporate", "dark", "light", "tjmsolutions"]}
```

```json
{
  "slide_count": 12,
  "templates_used": ["title", "two-column", "body"],
  "images_referenced": ["images/logo.png"],
  "has_mermaid_diagrams": false
}
```

---

## Module Structure (additions to MS-012's layout)

```
mcp/
  src/com/tjmsolutions/mdslides/mcp/
    McpServer.scala          — add "list_themes" / "get_deck_info" tool descriptors + dispatch cases
    tools/
      ListThemesTool.scala    — list_themes handler (wraps infrastructure ThemeLoader)
      GetDeckInfoTool.scala   — get_deck_info handler
    model/
      McpModels.scala         — add ThemesResult, DeckInfo case classes + Encoders
```

**Estimated LoC:** ~60 for the two tool handlers + ~30 for models + ~30 for McpServer wiring = ~120 total (smaller than Tier 1 — both tools are read-only, no rendering/writing).

---

## Gate Status

Pre-scaffold gate: **PASSED** — sequence diagrams for `list_themes` happy path + primary error path, and `get_deck_info` happy path + primary error paths, present. Implementation (MS-019) may proceed.
