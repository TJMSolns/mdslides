# US-018: Live Reload (--watch)

**Story:** As a presenter author, when I run `mdslides render my-deck --watch`, the browser auto-refreshes whenever I save the markdown file.

## Rules

1. `CLIArguments` gains `watch: Boolean = false`; parsed from `--watch` / `-w`
2. When `watch=true`, `HTMLRenderer.renderDeck` injects `<meta http-equiv="refresh" content="2">` — browser polls every 2s without a local HTTP server
3. `Main`: after initial render, start `java.nio.file.WatchService` on the input file's parent directory; re-run render pipeline on each change event targeting the input file
4. Re-render failures print error and continue watching (do not exit)
5. Ctrl+C exits cleanly via Cats Effect IOApp cancellation

## Decisions

- `liveReload: Boolean = false` param added to `HTMLRenderer.renderDeck` (not in Main string-replacement) — keeps the concern testable
- Debounce: 500ms sleep after each WatchService event before re-rendering
- Only the input markdown file is watched (v1 scope); theme file watching is deferred
- `meta http-equiv="refresh"` chosen over WebSocket: works with `file://` protocol, zero extra deps
