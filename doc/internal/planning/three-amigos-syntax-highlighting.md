# Three Amigos: Syntax Highlighting for Code Blocks

**Feature**: Syntax Highlighting (US-019)
**Priority**: SHOULD HAVE (v1.3)
**Date**: 2025-12-27
**Participants**: Product Owner, Developer, QA

---

## Event Storming Reference

This session validates the analysis from [Event Storming: Syntax Highlighting](event-storming-syntax-highlighting.md).

---

## User Story Review

**US-019: Syntax Highlighting**

> **As a** presentation author
> **I want** syntax-highlighted code blocks
> **So that** code is easier to read and understand in presentations

**Acceptance Criteria:**
1. Code blocks with language hints are syntax-highlighted
2. Highlighting uses appropriate colors for keywords, strings, comments, etc.
3. Highlighting works for common languages (Scala, Java, Python, JavaScript, SQL, Bash)
4. Theme-aware: light themes use light highlight.js theme, dark themes use dark theme
5. Graceful degradation if highlight.js fails to load
6. Existing auto-scaling for long code blocks still works (PDR-006)

---

## Questions & Clarifications

### Q1: Which languages must be supported?

**Developer**: "Highlight.js supports 190+ languages. Which are must-have vs nice-to-have?"

**Product Owner**: "Focus on developer presentation scenarios. Must-have: Scala, Java, Python, JavaScript, TypeScript, SQL, Bash, JSON, YAML, XML. Nice-to-have: Everything else highlight.js supports automatically."

**QA**: "Should we validate language hints or just pass them through?"

**Product Owner**: "Pass through. Highlight.js handles unknown languages gracefully (no highlighting). Authors can check highlight.js docs for supported languages."

**Decision**: No language validation. Support all highlight.js languages. Test with 10 core languages in acceptance tests.

---

### Q2: How should we map MDSlides themes to highlight.js themes?

**Developer**: "Highlight.js has 90+ themes. How do we choose?"

**Product Owner**: "Keep it simple. Light backgrounds → 'github' theme (clean, familiar). Dark backgrounds → 'monokai-sublime' theme (popular, high contrast)."

**QA**: "What about corporate and retisio themes?"

**Product Owner**: "Corporate is light → 'github'. Retisio is light → 'github'. If users want custom themes later, they can override CSS."

**Decision**:
- light → github
- dark → monokai-sublime
- corporate → github
- retisio → github
- Default fallback → github

---

### Q3: Should syntax colors from theme.json be used?

**Developer**: "All themes have `syntax` section with keyword/string/comment colors. Should we generate custom highlight.js theme from these?"

**Product Owner**: "No. That's complex and error-prone. Use built-in highlight.js themes for v1.3. The theme.json syntax colors can remain as fallback CSS or be ignored for now."

**QA**: "Should we remove syntax section from theme.json?"

**Product Owner**: "No, keep it. It's not hurting anything, and we might use it in v2.0 for custom theme generation."

**Decision**: Ignore theme.json syntax colors for v1.3. Use built-in highlight.js themes only.

---

### Q4: CDN or bundled highlight.js?

**Developer**: "CDN is simpler (no build changes, smaller JAR). Bundled works offline and is faster on first load."

**QA**: "How do we test if CDN is down?"

**Developer**: "We can test graceful degradation by rendering HTML without internet connection."

**Product Owner**: "Use CDN for v1.3. Most presentations are prepared with internet access. Offline support is nice-to-have for v2.0."

**Decision**: Use CDN (cdnjs.cloudflare.com). Test graceful degradation.

---

### Q5: Should code blocks without language hints be highlighted?

**Developer**: "Highlight.js has auto-detection, but it's less reliable. Should we enable it?"

**Product Owner**: "No. If author doesn't specify language, show plain code. Auto-detection can guess wrong and look unprofessional."

**QA**: "Should we warn users about missing language hints?"

**Product Owner**: "No warnings. Just don't highlight. Authors will notice and add hints if they want highlighting."

**Decision**: Only highlight code blocks with explicit language hints (```scala, ```python, etc.). Plain ``` blocks render without highlighting.

---

### Q6: What about inline code (`backticks`)?

**Developer**: "Should inline code like `val x = 42` be highlighted?"

**Product Owner**: "No. Syntax highlighting is for code blocks only. Inline code stays monospace without highlighting."

**QA**: "That matches current behavior, right?"

**Developer**: "Yes, no changes to inline code rendering."

**Decision**: Inline code is not highlighted. Feature applies to fenced code blocks only.

---

### Q7: Does this affect speaker notes with code?

**Developer**: "Speaker notes can contain code blocks. Should those be highlighted too?"

**Product Owner**: "Yes, if the code block has a language hint. Use the same logic."

**QA**: "Same theme mapping?"

**Developer**: "Yes, speaker notes use same theme as slides."

**Decision**: Apply highlighting to code blocks in speaker notes using same rules.

---

### Q8: How do we verify auto-scaling still works?

**QA**: "PDR-006 auto-scales code blocks >20 lines. This is handled by existing JavaScript. Could highlight.js interfere?"

**Developer**: "Highlight.js runs `highlightAll()` on DOMContentLoaded. Our auto-scaling runs on window.load. Order shouldn't matter since they operate on different aspects (syntax classes vs font-size CSS)."

**QA**: "We should verify with a >20 line code block in testing."

**Decision**: Add test case with 25-line code block to verify auto-scaling + highlighting both work.

---

### Q9: What if highlight.js CDN fails?

**Developer**: "If script doesn't load, code blocks will render as plain `<pre><code>` with no highlighting."

**QA**: "Should we show an error?"

**Product Owner**: "No. Silent degradation is fine. Code is still readable."

**Developer**: "We could add a console warning if hljs is undefined, but it's not critical."

**Decision**: Graceful degradation. No error UI. Optional console.log if hljs unavailable.

---

### Q10: Do we need documentation updates?

**QA**: "Tutorial should show syntax highlighting, right?"

**Product Owner**: "Yes. Add a slide with Scala code block to demonstrate. Also update README to mention syntax highlighting feature."

**Developer**: "Language hint syntax should be documented too."

**Decision**: Update tutorial with highlighted code example. Update README feature list. No separate syntax guide needed (standard Markdown fenced code blocks).

---

## Edge Cases Identified

### Edge Case 1: Empty code block with language hint
```markdown
```scala
```
```

**Expected**: Renders as empty `<pre><code class="language-scala"></code></pre>`. Highlight.js does nothing (no content).

**Test**: Verify no errors, empty block renders correctly.

---

### Edge Case 2: Unknown/typo language hint
```markdown
```scalaaa
val x = 42
```
```

**Expected**: Renders with `class="language-scalaaa"`. Highlight.js doesn't recognize it, no highlighting applied. Code still readable.

**Test**: Verify graceful handling, no errors.

---

### Edge Case 3: Mixed case language hint
```markdown
```SCALA
val x = 42
```
```

**Expected**: Highlight.js is case-insensitive. Should highlight as Scala.

**Test**: Verify SCALA, scala, ScAlA all work.

---

### Edge Case 4: Code block exceeds PDR-006 limit (>20 lines)
```scala
// 25 lines of code
```

**Expected**: Syntax highlighting applies, AND auto-scaling reduces font size.

**Test**: Verify both features work together (highlighted + smaller font).

---

### Edge Case 5: Code block with special HTML characters
```html
<div class="test">
  <p>Hello & goodbye</p>
</div>
```

**Expected**: HTML entities already escaped by existing renderer (`&lt;`, `&gt;`, `&amp;`). Highlight.js highlights escaped content.

**Test**: Verify no XSS, HTML renders as code (not executed).

---

### Edge Case 6: Very long line (horizontal scrolling)
```javascript
const veryLongVariableName = "some very long string that exceeds slide width and should cause horizontal scrolling without breaking layout";
```

**Expected**: Existing CSS handles overflow. Syntax highlighting doesn't affect layout.

**Test**: Verify horizontal scroll works, no text wrapping in code blocks.

---

### Edge Case 7: CDN blocked by corporate firewall
**Scenario**: User presents in environment where CDN is blocked.

**Expected**: Code renders without highlighting. No broken layout. Console shows 404 for highlight.js script.

**Test**: Render HTML, disable internet, verify presentation still works.

---

### Edge Case 8: Multiple code blocks with different languages on one slide
```markdown
## Multi-Language Example

Scala:
```scala
val x = 42
```

Python:
```python
x = 42
```
```

**Expected**: Each block highlighted with appropriate language syntax.

**Test**: Verify both blocks have correct highlighting independently.

---

### Edge Case 9: Nested code blocks (code block inside list item)
```markdown
- Configure database:
  ```sql
  CREATE TABLE users (id INT, name VARCHAR);
  ```
```

**Expected**: Code block rendered inside `<li>`, syntax highlighted normally.

**Test**: Verify nesting doesn't break highlighting or layout.

---

### Edge Case 10: Speaker notes with code block
```markdown
Note: Implementation uses factory pattern:
```scala
object UserFactory {
  def create(name: String): User = new User(name)
}
```
```

**Expected**: Speaker notes code block highlighted with same theme as slide code.

**Test**: Verify speaker notes rendering includes highlight.js.

---

## Concrete Examples (for Example Mapping)

### Example 1: Basic Scala code block
**Given**: Slide with Scala code block
```markdown
```scala
case class User(name: String, age: Int)
val user = User("Alice", 30)
```
```

**When**: Rendered to HTML

**Then**:
- Output includes `<link>` to github.min.css (light theme)
- Output includes `<script>` to highlight.min.js
- Code block has `<code class="language-scala">`
- Keywords (`case`, `class`, `val`) highlighted in keyword color
- Strings (`"Alice"`) highlighted in string color
- Numbers (`30`) highlighted in number color

---

### Example 2: Python code block with dark theme
**Given**: Slide with Python code, dark theme selected
```markdown
```python
def fibonacci(n):
    if n <= 1:
        return n
    return fibonacci(n-1) + fibonacci(n-2)
```
```

**When**: Rendered with `--theme dark`

**Then**:
- Output includes `<link>` to monokai-sublime.min.css
- Keywords (`def`, `if`, `return`) highlighted with monokai colors
- Dark background, light text

---

### Example 3: Code block without language hint
**Given**: Code block with no language specified
```markdown
```
This is plain text
or pseudocode
```
```

**When**: Rendered to HTML

**Then**:
- Output includes highlight.js script (for other blocks)
- This block has `<code>` without `language-*` class
- No syntax highlighting applied to this block
- Renders as plain monospace text

---

### Example 4: Long code block (>20 lines)
**Given**: Code block with 25 lines, language hint
```markdown
```java
public class Example {
  // ... 25 lines total
}
```
```

**When**: Rendered to HTML

**Then**:
- Syntax highlighting applied (Java keywords colored)
- Auto-scaling JavaScript reduces font size (PDR-006)
- Both features active simultaneously

---

### Example 5: Multiple languages on one slide
**Given**: Slide with SQL and Bash blocks
```markdown
```sql
SELECT * FROM users WHERE age > 18;
```

```bash
./deploy.sh --env production
```
```

**When**: Rendered to HTML

**Then**:
- SQL block highlighted with SQL syntax
- Bash block highlighted with Bash syntax
- Both use same highlight.js theme (github or monokai-sublime)

---

### Example 6: CDN unavailable
**Given**: HTML rendered normally
**When**: Opened in browser with no internet access
**Then**:
- Code blocks render as plain `<pre><code>`
- No highlighting, but still readable
- No broken layout or error messages
- (Optional) Console shows "hljs is not defined" warning

---

### Example 7: Corporate theme presentation
**Given**: Code block rendered with corporate theme
**When**: HTML generated with `--theme corporate`
**Then**:
- Highlight.js theme is "github" (light theme)
- Code highlighting uses github theme colors
- Corporate theme's background/fonts applied to slide

---

### Example 8: Speaker notes with code
**Given**: Speaker notes contain code block
```markdown
Note: Use this pattern:
```scala
trait Service {
  def execute(): Unit
}
```
```

**When**: Rendered to HTML

**Then**:
- Speaker notes `<aside>` contains highlighted code block
- Same highlight.js theme as main slides
- Highlight.js initialized for entire document (including notes)

---

## Acceptance Test Checklist

### Functional Tests
- [ ] Scala code block renders with syntax highlighting (light theme)
- [ ] Python code block renders with syntax highlighting (dark theme)
- [ ] JavaScript code block renders with syntax highlighting
- [ ] SQL code block renders with syntax highlighting
- [ ] Code block without language hint renders without highlighting
- [ ] Multiple language blocks on one slide all highlighted correctly
- [ ] Speaker notes code blocks are highlighted
- [ ] Empty code block with language hint renders without errors

### Integration Tests
- [ ] Long code block (>20 lines) has both highlighting AND auto-scaling
- [ ] Nested code block (in list) renders and highlights correctly
- [ ] Code with HTML characters (`<`, `>`, `&`) renders safely (no XSS)
- [ ] Long lines trigger horizontal scroll without breaking layout

### Theme Tests
- [ ] Light theme → github highlight.js theme loaded
- [ ] Dark theme → monokai-sublime highlight.js theme loaded
- [ ] Corporate theme → github highlight.js theme loaded
- [ ] Retisio theme → github highlight.js theme loaded

### Graceful Degradation
- [ ] Render HTML, open without internet → code still readable, no errors
- [ ] Unknown language hint → no highlighting, no errors
- [ ] Mixed case language hint (SCALA) → highlights correctly

### Documentation
- [ ] Tutorial updated with highlighted code example
- [ ] README mentions syntax highlighting feature
- [ ] CHANGELOG includes v1.3 syntax highlighting

---

## Open Questions for Example Mapping

1. Should we test all 10 "must-have" languages or sample 5?
   - Recommendation: Test Scala, Python, JavaScript, SQL, Bash in automated tests. Manually verify others.

2. Do we need performance tests for slides with many code blocks?
   - Recommendation: No specific perf tests. Highlight.js is fast. Defer to v2.0 if issues arise.

3. Should highlight.js version be configurable?
   - Recommendation: No. Hardcode v11.9.0 in HTMLRenderer. Update manually in future releases.

---

## Implementation Scope

### In Scope (v1.3)
- Modify HTMLRenderer to include highlight.js CDN in `<head>`
- Map theme names to highlight.js theme CSS files
- Add `hljs.highlightAll()` initialization script
- Test with 5 core languages
- Update tutorial and README

### Out of Scope (Defer to v2.0)
- Custom theme.json → highlight.js theme generation
- Bundled highlight.js (offline support)
- Server-side syntax highlighting
- Line numbering
- Copy-to-clipboard button
- Language hint validation
- Auto-detection for unhinted code blocks

---

## Risks & Mitigations

**Risk 1**: CDN outage during presentation
- **Mitigation**: Graceful degradation (code still readable). Document offline workaround in v2.0.

**Risk 2**: Highlight.js theme clashes with MDSlides theme CSS
- **Mitigation**: Use scoped CSS classes. Test all 4 built-in themes before release.

**Risk 3**: Breaking existing presentations that rely on unstyled code
- **Mitigation**: No breaking changes. Code blocks without language hints remain unstyled.

**Risk 4**: Auto-scaling JavaScript conflicts with highlight.js
- **Mitigation**: Test interaction explicitly. Run highlighting on DOMContentLoaded, scaling on window.load.

---

## Next Steps

1. Proceed to Example Mapping session to formalize test scenarios
2. Begin TDD implementation:
   - RED: Write test for HTMLRenderer including highlight.js CDN
   - GREEN: Implement CDN inclusion logic
   - REFACTOR: Clean up theme mapping logic
3. Update tutorial and documentation
4. Manual testing with sample presentation

---

**Sign-off**: Ready for Example Mapping
