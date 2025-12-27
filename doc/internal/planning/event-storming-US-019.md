# Event Storming: US-019 (Improved CLI UX)

**Date:** 2024-12-26
**User Story:** US-019 - Improved CLI UX
**Participants:** Tony Moores (Product Owner + Developer)
**Related Governance:** PDR-015 (CLI UX Design)

---

## Domain Events (Orange Stickies)

Events that happen in the system, past tense:

1. **CLI Arguments Parsed** (user invoked command)
2. **Deck Name Resolved** (DECK_NAME → DECK_NAME.md)
3. **Input File Located** (my-preso.md found)
4. **Output Directory Determined** (DECK_NAME → DECK_NAME/ or explicit -o DIR)
5. **Output Directory Created** (mkdir my-preso/)
6. **Markdown Parsed** (same as current)
7. **Slide Deck Validated** (same as current)
8. **Images Copied to Output** (images/ → my-preso/images/)
9. **Theme Backgrounds Copied** (backgrounds/ → my-preso/backgrounds/)
10. **HTML Rendered** (with relative asset paths)
11. **Index File Written** (my-preso/index.html created)
12. **Presentation Generated Successfully**

### Error Events

13. **Input File Not Found** (DECK_NAME.md missing)
14. **Output Directory Conflict Detected** (my-preso/ exists and not empty)
15. **Theme Not Found** (invalid --theme)

---

## Commands (Blue Stickies)

User intentions that trigger events:

1. **Render Deck** (simple: `mdslides render DECK_NAME`)
2. **Render with Theme** (`mdslides render DECK_NAME --theme dark`)
3. **Render with Explicit Paths** (`mdslides render -i INPUT -o OUTPUT`)
4. **Skip Image Copying** (`--no-copy-images` flag)
5. **Show Help** (`--help`)
6. **Show Version** (`--version`)

---

## Aggregates (Yellow Stickies)

Domain entities that handle commands and emit events:

### 1. **CLIArguments** (new aggregate)
- **Responsibility:** Parse and validate command-line arguments
- **State:**
  - `deckName: Option[String]` (simple form)
  - `inputFile: Option[Path]` (explicit -i)
  - `outputDir: Option[Path]` (explicit -o)
  - `themeName: String` (default: "light")
  - `copyImages: Boolean` (default: true)
- **Invariants:**
  - Either deckName OR (inputFile + outputDir) must be specified
  - Cannot mix simple and explicit forms
- **Methods:**
  - `parse(args: Array[String]): Either[String, CLIArguments]`
  - `resolveInputPath(): Either[String, Path]`
  - `resolveOutputDir(): Path`

### 2. **PathResolver** (new domain service)
- **Responsibility:** Resolve deck names to file paths
- **Methods:**
  - `findInputFile(deckName: String): Either[String, Path]`
    - Looks for `DECK_NAME.md` then `DECK_NAME.markdown`
  - `determineOutputDir(deckName: String): Path`
    - Returns `DECK_NAME/`
  - `ensureOutputDirExists(outputDir: Path): IO[Unit]`

### 3. **AssetPathResolver** (new infrastructure service)
- **Responsibility:** Generate relative paths for HTML
- **Methods:**
  - `relativeImagePath(imagePath: Path): String`
    - `images/diagram.png` (relative to index.html)
  - `relativeBackgroundPath(bgPath: Path): String`
    - `backgrounds/retisio-title-page.png`

### Existing Aggregates (unchanged)

4. **SlideDeck** (domain - no changes)
5. **Theme** (domain - no changes)
6. **HTMLRenderer** (infrastructure - UPDATE: use relative paths)

---

## Read Models / Queries (Green Stickies)

Information retrieval without side effects:

1. **List Available Themes** (`themes/` directory scan)
2. **Check Input File Exists** (file system query)
3. **Check Output Directory Status** (empty? exists? writable?)

---

## Policies (Lilac Stickies)

Business rules triggered by events:

1. **When CLI Arguments Parsed:**
   - IF deckName present → Resolve to input file path
   - IF inputFile/outputDir present → Use explicit paths
   - ELSE → Error: "Either DECK_NAME or -i/-o required"

2. **When Input File Not Found:**
   - Emit error with helpful message
   - Show available .md files in current directory (helpful hint)

3. **When Output Directory Conflict Detected:**
   - IF directory empty → Proceed
   - IF directory not empty → Error with resolution options

4. **When Rendering HTML:**
   - Use relative paths for all assets (images, backgrounds)
   - Ensures output directory is portable

---

## External Systems (Pink Stickies)

Systems outside our domain:

1. **File System** (read input, write output, create directories)
2. **Terminal/Shell** (receive arguments, display output/errors)

---

## Hotspots / Pain Points (Red Stickies)

Areas of uncertainty or complexity:

1. **⚠️ Output Directory Overwrite:**
   - What if `my-preso/` exists with old content?
   - Options:
     - A) Error and require manual deletion
     - B) Auto-clean (dangerous - data loss)
     - C) Warn but proceed
   - **Decision:** Option A (error, require explicit deletion)

2. **⚠️ Multiple Input Extensions:**
   - What order to check? `.md` then `.markdown`?
   - What if both exist?
   - **Decision:** `.md` takes priority, error if both exist

3. **⚠️ Relative Path Correctness:**
   - All asset paths in HTML must be relative to `index.html`
   - HTMLRenderer must generate `images/foo.png` not `/tmp/images/foo.png`
   - **Risk:** Absolute paths break portability
   - **Mitigation:** Test with moving output directory

---

## Timeline / Flow

```
User types: mdslides render my-preso --theme dark
    ↓
[CLI Arguments Parsed]
    ↓
[Deck Name Resolved] (my-preso → look for my-preso.md)
    ↓
[Input File Located] (my-preso.md found)
    ↓
[Output Directory Determined] (my-preso/)
    ↓
[Output Directory Created] (mkdir -p my-preso/)
    ↓
[Markdown Parsed] (existing flow)
    ↓
[Slide Deck Validated] (existing flow)
    ↓
[Theme Loaded] (dark theme from themes/dark/)
    ↓
[Images Copied to Output] (images/ → my-preso/images/)
    ↓
[Theme Backgrounds Copied] (themes/dark/backgrounds/ → my-preso/backgrounds/)
    ↓
[HTML Rendered] (with relative paths: <img src="images/...">)
    ↓
[Index File Written] (my-preso/index.html)
    ↓
[Presentation Generated Successfully]
    ↓
Output:
✓ Successfully created presentation: my-preso/
  - index.html (12KB)
  - images/ (3 files, 45KB)
  - backgrounds/ (5 files, 764KB)
```

### Error Flow (Input Not Found)

```
User types: mdslides render missing-deck
    ↓
[CLI Arguments Parsed]
    ↓
[Deck Name Resolved] (missing-deck → look for missing-deck.md)
    ↓
[Input File Not Found] ❌
    ↓
Error Output:
✗ Input file not found: missing-deck.md or missing-deck.markdown

Found in current directory:
  - my-preso.md
  - slides.md

Usage: mdslides render DECK_NAME [--theme THEME]
```

---

## Key Domain Insights

1. **Two Modes, One Command:**
   - Simple mode (inferred): `mdslides render DECK_NAME`
   - Explicit mode (full control): `mdslides render -i INPUT -o OUTPUT`
   - Modes are mutually exclusive (can't mix)

2. **Convention:** Deck name stem used for both input and output
   - `my-preso` → `my-preso.md` + `my-preso/index.html`
   - Reduces redundancy, enforces consistency

3. **Directory as Unit:**
   - Output is always a directory (even for single HTML file)
   - Future-proofs for multi-file features (speaker view, handouts)

4. **Portability:**
   - Relative paths enable moving/zipping output directory
   - `my-preso/` is self-contained (can share via zip, git, etc.)

---

## Open Questions

1. **Q:** Should we support glob patterns? (`mdslides render talks/*.md`)
   - **A:** No, out of scope for v1.0. Single deck at a time.

2. **Q:** Should `--output` flag work in simple mode? (`mdslides render my-preso --output dist`)
   - **A:** Yes, `-o` overrides inferred output directory.

3. **Q:** What if input file has spaces? (`my preso.md`)
   - **A:** Must quote: `mdslides render "my preso"` or use explicit: `mdslides render -i "my preso.md" -o output`

4. **Q:** Should we create parent directories? (`mdslides render -o dist/presentations/my-talk`)
   - **A:** Yes, `mkdir -p` behavior (create all parent directories).

---

## Next Steps

1. ✅ Event Storming complete
2. **NEXT:** Three Amigos session (refine acceptance criteria)
3. **THEN:** Example Mapping (concrete examples)
4. **THEN:** TDD implementation

---

**Event Storming Status:** ✅ COMPLETE
**Ready for:** Three Amigos Session
