# Example Mapping: Syntax Highlighting for Code Blocks

**Feature**: Syntax Highlighting (US-019)
**Priority**: SHOULD HAVE (v1.3)
**Date**: 2025-12-27

---

## Story Card

**US-019: Syntax Highlighting**

> **As a** presentation author
> **I want** syntax-highlighted code blocks
> **So that** code is easier to read and understand in presentations

---

## Rules (from Three Amigos)

### Rule 1: Only highlight code blocks with explicit language hints
- Code blocks with ` ```scala ` → highlighted
- Code blocks with plain ` ``` ` → not highlighted (plain monospace)
- Inline code (`` `code` ``) → never highlighted

### Rule 2: Theme mapping to highlight.js themes
- light → github.min.css
- dark → monokai-sublime.min.css
- corporate → github.min.css
- retisio → github.min.css
- Unknown/custom themes → github.min.css (default)

### Rule 3: Highlight.js via CDN only
- Script: `https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js`
- CSS: `https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/{theme}.min.css`
- Initialization: `hljs.highlightAll()` on DOMContentLoaded

### Rule 4: Graceful degradation if CDN fails
- Code blocks still render as `<pre><code>`
- No highlighting, but content readable
- No error UI shown to user

### Rule 5: Preserve existing auto-scaling (PDR-006)
- Code blocks >20 lines auto-scale font size
- Highlighting does not interfere with auto-scaling JavaScript
- Both features work simultaneously

### Rule 6: Language hints are case-insensitive
- `scala`, `SCALA`, `Scala` → all work
- Passed directly to highlight.js (no validation)

### Rule 7: Speaker notes code blocks use same highlighting
- Same theme as main slides
- Same initialization logic
- Rendered within `<aside class="notes">`

---

## Examples

### Example 1: Scala code block with light theme ✅
**Given**: Markdown with Scala code block
```markdown
```scala
case class User(name: String)
val user = User("Alice")
```
```

**And**: Theme is "light"

**When**: Rendered to HTML

**Then**: HTML contains:
```html
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github.min.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
<script>document.addEventListener('DOMContentLoaded', () => { hljs.highlightAll(); });</script>
```

**And**: Code block contains:
```html
<pre><code class="language-scala">case class User(name: String)
val user = User("Alice")</code></pre>
```

**Test Layer**: Infrastructure (HTMLRenderer)
**Test Type**: Unit test (verify CDN links present, class attribute correct)

---

### Example 2: Python code block with dark theme ✅
**Given**: Markdown with Python code block
```markdown
```python
def hello(name):
    return f"Hello, {name}"
```
```

**And**: Theme is "dark"

**When**: Rendered to HTML

**Then**: CSS link is:
```html
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/monokai-sublime.min.css">
```

**And**: Code block has `class="language-python"`

**Test Layer**: Infrastructure (HTMLRenderer)
**Test Type**: Unit test (verify monokai-sublime CSS for dark theme)

---

### Example 3: Code block without language hint ✅
**Given**: Markdown with plain code block
```markdown
```
This is pseudocode
or plain text
```
```

**When**: Rendered to HTML

**Then**: Code block is:
```html
<pre><code>This is pseudocode
or plain text</code></pre>
```

**Note**: No `class="language-*"` attribute
**Note**: highlight.js ignores this block (no highlighting)

**Test Layer**: Infrastructure (HTMLRenderer)
**Test Type**: Unit test (verify no language class when hint absent)

---

### Example 4: Multiple code blocks with different languages ✅
**Given**: Slide with SQL and Bash blocks
```markdown
## Database Setup

```sql
SELECT * FROM users;
```

```bash
./deploy.sh
```
```

**When**: Rendered to HTML

**Then**: First block has `class="language-sql"`
**And**: Second block has `class="language-bash"`
**And**: Both blocks in same slide HTML
**And**: Single `hljs.highlightAll()` call highlights both

**Test Layer**: Infrastructure (HTMLRenderer)
**Test Type**: Unit test (verify multiple code blocks handled independently)

---

### Example 5: Corporate theme uses light highlighting ✅
**Given**: Markdown with Java code block
```markdown
```java
public class Example {}
```
```

**And**: Theme is "corporate"

**When**: Rendered to HTML

**Then**: CSS link is:
```html
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github.min.css">
```

**Test Layer**: Infrastructure (HTMLRenderer)
**Test Type**: Unit test (verify corporate → github theme mapping)

---

### Example 6: Retisio theme uses light highlighting ✅
**Given**: Markdown with JavaScript code block
```markdown
```javascript
const x = 42;
```
```

**And**: Theme is "retisio"

**When**: Rendered to HTML

**Then**: CSS link is:
```html
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github.min.css">
```

**Test Layer**: Infrastructure (HTMLRenderer)
**Test Type**: Unit test (verify retisio → github theme mapping)

---

### Example 7: Long code block with highlighting and auto-scaling ✅
**Given**: Markdown with 25-line Scala code block
```markdown
```scala
object Example {
  def method1(): Unit = ???
  def method2(): Unit = ???
  // ... 25 lines total
}
```
```

**When**: Rendered to HTML and opened in browser

**Then**: Code block has syntax highlighting (Scala keywords colored)
**And**: Font size is reduced (auto-scaling applied)
**And**: Both features work without conflict

**Test Layer**: Integration (manual or browser-based)
**Test Type**: Visual inspection or Selenium test

---

### Example 8: Empty code block with language hint ✅
**Given**: Markdown with empty code block
```markdown
```scala
```
```

**When**: Rendered to HTML

**Then**: Code block is:
```html
<pre><code class="language-scala"></code></pre>
```

**And**: No errors during rendering
**And**: highlight.js handles gracefully (no content to highlight)

**Test Layer**: Infrastructure (HTMLRenderer)
**Test Type**: Unit test (verify empty block doesn't crash)

---

### Example 9: Unknown language hint ✅
**Given**: Markdown with typo in language hint
```markdown
```scalaaa
val x = 42
```
```

**When**: Rendered to HTML

**Then**: Code block is:
```html
<pre><code class="language-scalaaa">val x = 42</code></pre>
```

**And**: highlight.js doesn't recognize language
**And**: Code renders without highlighting (plain monospace)
**And**: No errors thrown

**Test Layer**: Infrastructure (HTMLRenderer)
**Test Type**: Unit test (verify unknown language doesn't break rendering)

---

### Example 10: Mixed case language hint ✅
**Given**: Markdown with uppercase language hint
```markdown
```SCALA
val x = 42
```
```

**When**: Rendered to HTML

**Then**: Code block has `class="language-SCALA"`
**And**: highlight.js handles case-insensitively (highlights as Scala)

**Test Layer**: Infrastructure (HTMLRenderer)
**Test Type**: Unit test (verify case preserved in class attribute)
**Note**: Browser testing confirms highlight.js is case-insensitive

---

### Example 11: Code block with HTML special characters ✅
**Given**: Markdown with HTML code block
```markdown
```html
<div class="test">
  <p>Hello & goodbye</p>
</div>
```
```

**When**: Rendered to HTML

**Then**: Code block contains escaped HTML:
```html
<pre><code class="language-html">&lt;div class="test"&gt;
  &lt;p&gt;Hello &amp; goodbye&lt;/p&gt;
&lt;/div&gt;</code></pre>
```

**And**: Highlight.js highlights the escaped content
**And**: No XSS vulnerability (HTML not executed)

**Test Layer**: Infrastructure (HTMLRenderer)
**Test Type**: Unit test (verify HTML escaping preserved)

---

### Example 12: Nested code block inside list item ✅
**Given**: Markdown with code block inside list
```markdown
- Step 1: Configure database
  ```sql
  CREATE TABLE users (id INT);
  ```
- Step 2: Run migration
```

**When**: Rendered to HTML

**Then**: Code block is inside `<li>`:
```html
<ul>
  <li>Step 1: Configure database
    <pre><code class="language-sql">CREATE TABLE users (id INT);</code></pre>
  </li>
  <li>Step 2: Run migration</li>
</ul>
```

**And**: Syntax highlighting applied to SQL code
**And**: Layout not broken

**Test Layer**: Integration (existing list rendering + highlighting)
**Test Type**: Unit test (verify code block in nested context)

---

### Example 13: Speaker notes with code block ✅
**Given**: Markdown with speaker notes containing code
```markdown
## Slide Title

Content here.

Note: Use this implementation:
```scala
trait Service {
  def execute(): Unit
}
```
```

**When**: Rendered to HTML

**Then**: Speaker notes `<aside>` contains highlighted code:
```html
<aside class="notes">
  <p>Use this implementation:</p>
  <pre><code class="language-scala">trait Service {
  def execute(): Unit
}</code></pre>
</aside>
```

**And**: Same highlight.js theme as main slides
**And**: `hljs.highlightAll()` highlights both slide and notes code

**Test Layer**: Infrastructure (HTMLRenderer)
**Test Type**: Unit test (verify notes rendering includes highlighted code)

---

### Example 14: CDN unavailable (graceful degradation) ⚠️
**Given**: HTML rendered normally (includes CDN script tags)

**When**: HTML opened in browser with no internet access

**Then**: Code blocks render as plain `<pre><code class="language-*">`
**And**: No syntax highlighting (script failed to load)
**And**: Content still readable
**And**: No broken layout
**And**: (Optional) Browser console shows 404 for highlight.js script

**Test Layer**: Manual testing
**Test Type**: Disconnect internet, open rendered HTML, verify degradation

---

### Example 15: Inline code is not highlighted ✅
**Given**: Markdown with inline code
```markdown
Use the `val` keyword for immutable variables.
```

**When**: Rendered to HTML

**Then**: Inline code is:
```html
<p>Use the <code>val</code> keyword for immutable variables.</p>
```

**Note**: No `class="language-*"` attribute
**Note**: highlight.js ignores inline `<code>` (only highlights `<pre><code>`)
**Note**: Existing inline code rendering unchanged

**Test Layer**: Infrastructure (HTMLRenderer)
**Test Type**: Unit test (verify inline code unaffected)

---

### Example 16: Very long line causes horizontal scroll ✅
**Given**: Markdown with code containing very long line
```markdown
```javascript
const veryLongVariableName = "some very long string that exceeds slide width and should cause horizontal scrolling without breaking layout or wrapping text";
```
```

**When**: Rendered to HTML and displayed in browser

**Then**: Code block has horizontal scrollbar
**And**: Line not wrapped (preserves code formatting)
**And**: Syntax highlighting applied to entire line
**And**: Slide layout not broken

**Test Layer**: Integration (existing overflow CSS + highlighting)
**Test Type**: Visual inspection (manual test)

---

## Questions Arising from Examples

### Q1: Should we test all supported languages?
**Answer**: No. Test 5 core languages in automated tests (Scala, Python, JavaScript, SQL, Bash). Manually verify others (Java, TypeScript, YAML, JSON, XML) before release.

### Q2: How do we automate browser-based tests (highlighting + auto-scaling)?
**Answer**: For v1.3, manual testing sufficient. Automated browser tests (Selenium) deferred to v2.0.

### Q3: Do we need a test for custom themes?
**Answer**: No custom themes exist yet. Test only 4 built-in themes. Custom theme support is v2.0+.

### Q4: Should we test CDN failure automatically?
**Answer**: No. Manual test only (disconnect internet, verify degradation). Automated testing would require mocking network failures (overkill for v1.3).

---

## Test Scenarios Summary

### Unit Tests (Infrastructure - HTMLRenderer)

**Test Group 1: CDN Inclusion**
1. Verify `<script>` tag for highlight.js CDN in rendered HTML
2. Verify `<link>` tag for highlight.js CSS in rendered HTML
3. Verify initialization script `hljs.highlightAll()` in rendered HTML

**Test Group 2: Theme Mapping**
4. Light theme → github.min.css
5. Dark theme → monokai-sublime.min.css
6. Corporate theme → github.min.css
7. Retisio theme → github.min.css

**Test Group 3: Code Block Rendering**
8. Code block with language hint → `class="language-{lang}"`
9. Code block without language hint → no `class` attribute
10. Empty code block with language hint → renders without errors
11. Unknown language hint → renders with class, no errors
12. Mixed case language hint → class preserves case
13. Multiple code blocks on one slide → each has correct class
14. Code block with HTML characters → HTML entities escaped
15. Inline code → no language class, unaffected

**Test Group 4: Integration with Existing Features**
16. Code block in nested list → renders correctly
17. Code block in speaker notes → highlighted with same theme

### Manual Tests

**Manual Test 1: Visual Verification**
- Render sample deck with Scala, Python, JavaScript, SQL, Bash code blocks
- Open in browser, verify keywords/strings/comments have distinct colors
- Verify github theme colors for light, monokai-sublime for dark

**Manual Test 2: Auto-Scaling + Highlighting**
- Render code block with >20 lines
- Verify syntax highlighting present AND font size reduced

**Manual Test 3: Graceful Degradation**
- Render HTML normally
- Disconnect internet
- Open HTML, verify code readable without highlighting

**Manual Test 4: Long Line Scroll**
- Render code block with 200-character line
- Verify horizontal scrollbar appears, no text wrapping

---

## Implementation Checklist

### Phase 1: HTMLRenderer Modification (TDD)
- [ ] RED: Test that rendered HTML includes highlight.js script CDN
- [ ] GREEN: Add script tag to HTMLRenderer
- [ ] RED: Test that rendered HTML includes highlight.js CSS CDN
- [ ] GREEN: Add link tag with theme mapping logic
- [ ] RED: Test that initialization script is included
- [ ] GREEN: Add `hljs.highlightAll()` script
- [ ] REFACTOR: Extract theme mapping to helper function

### Phase 2: Theme Mapping (TDD)
- [ ] RED: Test light theme → github CSS
- [ ] GREEN: Implement mapping
- [ ] RED: Test dark theme → monokai-sublime CSS
- [ ] GREEN: Implement mapping
- [ ] RED: Test corporate theme → github CSS
- [ ] GREEN: Implement mapping
- [ ] RED: Test retisio theme → github CSS
- [ ] GREEN: Implement mapping

### Phase 3: Code Block Class Rendering (TDD)
- [ ] RED: Test code block with language → class="language-{lang}"
- [ ] GREEN: Verify existing renderer already does this (no change needed)
- [ ] RED: Test code block without language → no class
- [ ] GREEN: Verify existing renderer handles this (no change needed)

### Phase 4: Edge Cases (TDD)
- [ ] RED: Test empty code block with language
- [ ] GREEN: Verify no crash
- [ ] RED: Test unknown language hint
- [ ] GREEN: Verify renders without errors
- [ ] RED: Test code block in speaker notes
- [ ] GREEN: Ensure notes rendering includes highlight.js

### Phase 5: Manual Testing
- [ ] Render tutorial with code examples, verify highlighting
- [ ] Test all 4 themes visually
- [ ] Test >20 line code block (auto-scaling + highlighting)
- [ ] Test offline mode (CDN failure)

### Phase 6: Documentation
- [ ] Update tutorial with syntax-highlighted Scala example
- [ ] Update README feature list
- [ ] Update CHANGELOG with v1.3 syntax highlighting entry

---

## Risks & Open Issues

**Risk 1**: highlight.js version hardcoded (v11.9.0)
- **Mitigation**: Document version in code comments. Update manually in future.

**Risk 2**: Theme CSS might clash with MDSlides CSS
- **Mitigation**: Test all 4 themes visually before release. Highlight.js uses scoped classes (`.hljs-*`).

**Risk 3**: Auto-scaling JavaScript might run before highlighting
- **Mitigation**: Test integration manually. Order: DOMContentLoaded (highlighting) → window.load (auto-scaling).

---

## Success Criteria

1. All unit tests pass (17 new tests)
2. Manual visual inspection confirms highlighting works
3. Auto-scaling + highlighting coexist without conflict
4. Graceful degradation verified (offline mode)
5. Tutorial renders with highlighted code examples
6. No breaking changes to existing presentations

---

**Sign-off**: Ready for TDD implementation

**Next Step**: Begin RED-GREEN-REFACTOR cycle, starting with HTMLRenderer tests for CDN inclusion.
