package com.tjmsolutions.mdslides.infrastructure.theme

import com.tjmsolutions.mdslides.domain.*
import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.parser.*

/**
 * Anticorruption Layer for Theme JSON parsing.
 *
 * Translates JSON theme files to domain Theme objects.
 * Isolates domain from Circe JSON library.
 *
 * Supported formats:
 * - Built-in themes: "light", "dark", "corporate"
 * - Custom JSON files following PDR-007 schema
 *
 * Validation:
 * - JSON structure validation (Circe)
 * - Required fields: name, version, background.color, colors, fonts, spacing
 * - Optional fields: background.image, background.opacity, etc.
 * - WCAG AA contrast validation (future: validate in domain/infrastructure)
 *
 * Related Governance:
 * - ADR-007: Anticorruption Layer
 * - ADR-010: Markdown Library Selection (same pattern for Flexmark)
 * - PDR-007: Theme JSON Schema
 * - US-008: Theme System
 * - US-009: Built-in Themes
 */
object ThemeJsonAdapter:

  /**
   * Parse JSON string to Theme.
   *
   * @param json JSON string following PDR-007 schema
   * @return Either error message or Theme
   */
  def parseTheme(json: String): Either[String, Theme] =
    decode[Theme](json).left.map(_.getMessage)

  /**
   * Predefined light theme (same as Theme.light).
   *
   * Preserves v0.1.0 default styling.
   */
  def light: Theme = Theme.light

  /**
   * Predefined dark theme (same as Theme.dark).
   *
   * Modern dark mode styling.
   */
  def dark: Theme = Theme.dark

  /**
   * Predefined corporate theme (same as Theme.corporate).
   *
   * Professional business styling with watermark support.
   */
  def corporate: Theme = Theme.corporate

  // ===== Circe Decoders =====

  /**
   * Decoder for Background - handles optional image fields.
   *
   * Default values:
   * - opacity: 1.0
   * - image, position, size: None
   */
  given Decoder[Background] = (c: HCursor) =>
    for
      color <- c.downField("color").as[String]
      image <- c.downField("image").as[Option[String]]
      opacity <- c.downField("opacity").as[Option[Double]].map(_.getOrElse(1.0))
      position <- c.downField("position").as[Option[String]]
      size <- c.downField("size").as[Option[String]]
    yield Background(color, image, opacity, position, size)

  /**
   * Decoder for ColorScheme - all fields required.
   */
  given Decoder[ColorScheme] = deriveDecoder[ColorScheme]

  /**
   * Decoder for FontScheme - all fields required.
   */
  given Decoder[FontScheme] = deriveDecoder[FontScheme]

  /**
   * Decoder for Spacing - all fields required.
   */
  given Decoder[Spacing] = deriveDecoder[Spacing]

  /**
   * Decoder for SyntaxColors - all fields required.
   */
  given Decoder[SyntaxColors] = deriveDecoder[SyntaxColors]

  /**
   * Decoder for SlideCounter - all fields required.
   */
  given Decoder[SlideCounter] = deriveDecoder[SlideCounter]

  /**
   * Decoder for Theme - top-level aggregate.
   *
   * Handles optional templateBackgrounds field (US-012).
   */
  given Decoder[Theme] = (c: HCursor) =>
    for
      name <- c.downField("name").as[String]
      version <- c.downField("version").as[String]
      background <- c.downField("background").as[Background]
      colors <- c.downField("colors").as[ColorScheme]
      fonts <- c.downField("fonts").as[FontScheme]
      spacing <- c.downField("spacing").as[Spacing]
      syntax <- c.downField("syntax").as[SyntaxColors]
      slideCounter <- c.downField("slideCounter").as[SlideCounter]
      templateBackgrounds <- c.downField("templateBackgrounds").as[Option[Map[String, String]]].map(_.getOrElse(Map.empty))
    yield Theme(name, version, background, colors, fonts, spacing, syntax, slideCounter, templateBackgrounds)

end ThemeJsonAdapter
