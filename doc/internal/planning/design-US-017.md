# US-017: Google Fonts Support

**Story:** As a theme author, I can declare Google Font families in theme JSON so they load automatically without manual HTML editing.

## Rules

1. `FontScheme` gains `googleFonts: List[String]` (default `Nil`) — each entry is a Google Fonts family spec (e.g. `"Roboto"`, `"Open+Sans:wght@400;700"`)
2. JSON key `"googleFonts"` is optional; absent or `[]` → empty list (backward compat)
3. When `googleFonts.nonEmpty`, HTMLRenderer injects two `<link rel="preconnect">` tags and one `<link rel="stylesheet">` URL built from all families joined with `&family=`
4. URL format: `https://fonts.googleapis.com/css2?family=A&family=B&display=swap`
5. Built-in themes (`light`, `dark`, `corporate`) keep `googleFonts = Nil`

## Decisions

- Inject in HTMLRenderer head, after the highlight.js links
- `FontScheme` decoder changes from `deriveDecoder` to manual (optional field with default)
- No domain validation of font names — pass-through to browser
