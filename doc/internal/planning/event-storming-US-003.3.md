# Event Storming: US-003.3 - Nested List Support

**Date:** 2024-12-27
**Participants:** Development Team
**User Story:** US-003.3 - Nested List Support

## User Story

**As a** presentation author
**I want** to use nested lists in my slide content
**So that** I can show hierarchical information and sub-points

## Domain Events (Time-ordered)

### 1. Markdown Parsed
**When:** User runs `mdslides render my-preso`
**What:** FlexmarkAdapter parses markdown content
**Triggers:** List structure extraction
**Data:** Markdown AST, slide content

### 2. List Node Encountered
**When:** Parser encounters `<ul>` or `<ol>` in AST
**What:** Identifies list structure to extract
**Triggers:** List item extraction
**Data:** List type (ordered/unordered), parent node

### 3. List Item Extracted
**When:** Parser processes `<li>` node
**What:** Extracts text content and checks for child lists
**Triggers:** Recursive list extraction (if nested)
**Data:** Item text, nesting level, child nodes

### 4. Nested List Detected
**When:** List item contains child `<ul>` or `<ol>` nodes
**What:** Recursively extracts nested list structure
**Triggers:** Nested item extraction
**Data:** Parent item, child list type, nesting depth

### 5. FormattedContent Created
**When:** All list items extracted (including nested)
**What:** Constructs FormattedContent with list items
**Triggers:** Domain model validation
**Data:** List items with hierarchy preserved

### 6. Nesting Depth Validated
**When:** FormattedContent validation runs
**What:** Checks max nesting depth (3 levels)
**Triggers:** Validation pass/fail
**Data:** Actual depth, max allowed depth

### 7. HTML Rendered
**When:** HTMLRenderer generates output
**What:** Renders nested `<ul>`/`<ol>` with proper indentation
**Triggers:** CSS styling application
**Data:** HTML structure, nesting depth

### 8. CSS Styling Applied
**When:** Browser displays presentation
**What:** Applies different bullet styles per nesting level
**Triggers:** Visual rendering
**Data:** CSS classes, bullet type (disc/circle/square)

## Commands (User Actions)

### Write Nested List in Markdown
**Actor:** Presentation Author
**Trigger:** Author types nested list structure
**Precondition:** Markdown file exists
**Postcondition:** Nested list structure present in markdown
**Business Rule:** Max 3 levels of nesting

**Example:**
```markdown
- Level 1 item
  - Level 2 item
    - Level 3 item (maximum depth)
```

### Render Presentation
**Actor:** Presentation Author
**Trigger:** Runs `mdslides render`
**Precondition:** Valid markdown with nested lists
**Postcondition:** HTML presentation generated
**Business Rule:** Nesting depth ≤ 3 levels

### Validate Nesting Depth
**Actor:** Validation System
**Trigger:** Automated during rendering
**Precondition:** FormattedContent created
**Postcondition:** Pass if depth ≤ 3, fail otherwise
**Business Rule:** Error if exceeds 3 levels

## Aggregates

### FormattedContent (Enhanced)
**Responsibilities:**
- Contains text spans, links, lists, and images
- Validates word count and line count
- **NEW**: Validates nesting depth ≤ 3

**Changes:**
- Add `maxNestingDepth` validation method
- No changes to data structure (already supports nested lists via ListItem)

### ListItem (Existing - No Changes)
**Current Structure:**
```scala
case class ListItem(
  content: List[TextSpan]  // Can include nested lists
)
```

**Already Supports Nesting**: TextSpan can be a `List[ListItem]`

### FlexmarkAdapter (Enhanced)
**Responsibilities:**
- Parses markdown to FormattedContent
- **NEW**: Recursively extracts nested list structures
- **NEW**: Tracks nesting depth during extraction

**Changes:**
- Add `extractNestedLists(node: Node, depth: Int): List[ListItem]`
- Modify `extractListItemContent` to handle child lists recursively

### HTMLRenderer (Enhanced)
**Responsibilities:**
- Renders FormattedContent to HTML
- **NEW**: Renders nested `<ul>`/`<ol>` with indentation
- **NEW**: Applies CSS classes for nesting levels

**Changes:**
- Modify list rendering to handle nested structures
- Add CSS classes: `list-level-1`, `list-level-2`, `list-level-3`

## Read Models (Queries)

### Get Nesting Depth
**Query:** calculateNestingDepth(item: ListItem)
**Returns:** Int (max depth of this item's children)
**Used By:** FormattedContent validation

### Get List Hierarchy
**Query:** extractListHierarchy(node: Node)
**Returns:** List[ListItem] with nesting preserved
**Used By:** FlexmarkAdapter during parsing

## Policies (Business Rules)

### P1: Max Nesting Depth
**Policy:** Lists cannot exceed 3 levels of nesting
**Rationale:** Prevents overly complex slides, maintains readability
**Implementation:** Validation error if depth > 3

### P2: Mixed Nesting Allowed
**Policy:** Ordered lists can contain unordered, and vice versa
**Rationale:** Flexibility for authors, common markdown pattern
**Implementation:** FlexmarkAdapter handles both `BulletList` and `OrderedList` children

### P3: Bullet Style Hierarchy
**Policy:** Different bullet styles per nesting level
**Rationale:** Visual hierarchy aids comprehension
**Implementation:** CSS rules for `list-level-N`

**Bullet Styles:**
- Level 1: disc (•)
- Level 2: circle (◦)
- Level 3: square (▪)

### P4: Indentation Scaling
**Policy:** Each nesting level indents 2em
**Rationale:** Clear visual hierarchy without excessive whitespace
**Implementation:** CSS `margin-left: 2em` per level

## External Systems

### Flexmark Library
**Purpose:** Markdown parsing, AST construction
**Dependency:** com.vladsch.flexmark:flexmark-all
**Usage:** Provides `BulletList`, `OrderedList`, `ListItem` nodes

### Browser CSS Engine
**Purpose:** Renders nested list styling
**Dependency:** CSS3 support (universal)
**Usage:** Applies `list-style-type` and indentation

## Open Questions

### Q1: Should we validate nesting depth or just warn?
**Options:**
- Hard error (validation failure)
- Warning (allow but discourage)

**Decision:** Hard error (validation failure)
**Rationale:** Enforces best practices, prevents overly complex slides

### Q2: Should we support definition lists (`<dl>`)?
**Options:**
- Yes (add support for `<dt>`/`<dd>`)
- No (defer to v1.3)

**Decision:** No, defer to v1.3
**Rationale:** Focus on nested `<ul>`/`<ol>` for MVP, definition lists are less common

### Q3: Should indentation be configurable via theme?
**Options:**
- Yes (theme.json field: `listIndentation`)
- No (fixed 2em)

**Decision:** No, fixed 2em for v1.2
**Rationale:** Simpler implementation, configurable in v1.3 if needed

### Q4: Should we render nested lists in speaker notes?
**Options:**
- Yes (full HTML rendering)
- No (plain text only)

**Decision:** No, plain text only for v1.2
**Rationale:** Speaker notes are simple text, nested lists add complexity

## Implementation Notes

### Parsing Strategy
1. FlexmarkAdapter encounters `BulletList` or `OrderedList` node
2. For each `ListItem` child:
   - Extract text content (existing logic)
   - Check for child `BulletList`/`OrderedList` nodes
   - If found, recursively call `extractNestedLists(child, depth + 1)`
   - Append nested items to parent item's content
3. Track max depth during extraction
4. Validate max depth ≤ 3 after extraction

### Rendering Strategy
- Recursive HTML generation:
  ```scala
  def renderList(items: List[ListItem], level: Int): String =
    items.map { item =>
      val content = renderTextSpans(item.content)
      val nested = item.nestedLists.map(renderList(_, level + 1))
      s"<li class=\"list-level-$level\">$content$nested</li>"
    }.mkString
  ```

### CSS Strategy
```css
.slide ul, .slide ol {
  margin-left: 0;
}

.slide ul ul, .slide ol ol, .slide ul ol, .slide ol ul {
  margin-left: 2em;
}

.slide ul { list-style-type: disc; }
.slide ul ul { list-style-type: circle; }
.slide ul ul ul { list-style-type: square; }

.slide ol { list-style-type: decimal; }
.slide ol ol { list-style-type: lower-alpha; }
.slide ol ol ol { list-style-type: lower-roman; }
```

## Success Metrics

1. **Parsing accuracy:** Nested lists extracted correctly
2. **Validation:** Depth > 3 caught and reported
3. **Rendering fidelity:** HTML structure matches markdown intent
4. **Browser compatibility:** Works in Chrome, Firefox, Safari, Edge
5. **Visual hierarchy:** Clear distinction between nesting levels

## Related Documentation

- US-003.2: List Support (v0.4.0 - completed)
- FlexmarkAdapter implementation: `infrastructure/src/.../parser/FlexmarkAdapter.scala`
- FormattedContent domain model: `domain/src/.../domain/FormattedContent.scala`

---

**Status:** ✅ Complete - Ready for Three Amigos
**Next Step:** Three Amigos session to refine acceptance criteria
