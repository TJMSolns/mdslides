# US-019: Validation Errors with Source Line Numbers

**Story:** As a presenter author, when validation fails I see the markdown line number so I can jump directly to the problem.

## Rules

1. `ValidationError` cases gain `lineNumber: Option[Int] = None` — backward compatible default
2. `displayMessage` includes `(line N)` when `lineNumber` is `Some(N)`
3. `MarkdownParser` gains `slideLineNumbers(markdown: String): Map[Int, Int]` — pure function returning slide-index (1-based) → line-number (1-based) of that slide's opening `---`
4. `Main` calls `slideLineNumbers` before validation and annotates each `ValidationError` with the corresponding slide's start line via `copy(lineNumber = ...)`
5. Existing code constructing `ValidationError` without `lineNumber` compiles unchanged

## Decisions

- Line number = start of the slide's opening `---` delimiter; fine-grained intra-slide offsets deferred
- Annotation in `Main` (infrastructure/CLI boundary) keeps domain layer pure
- `slideLineNumbers` is a standalone pure method — trivially testable without I/O
