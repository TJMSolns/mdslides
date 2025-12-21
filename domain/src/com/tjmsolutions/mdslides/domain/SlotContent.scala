package com.tjmsolutions.mdslides.domain

/**
 * Content for a template slot.
 *
 * Wraps FormattedContent (parsed markdown with structure).
 * For v0.1.0 compatibility, also supports plain text (auto-converted).
 *
 * Validation constraints (max lines, max words) use plain text counts.
 *
 * Related Governance:
 * - POL-001: Ubiquitous Language Enforcement
 * - ADR-008: Slot-Based Content Model
 * - US-003: Full Markdown Rendering
 */
opaque type SlotContent = FormattedContent

object SlotContent:
  /**
   * Creates SlotContent from FormattedContent.
   *
   * @param content The formatted content
   * @return SlotContent wrapping the formatted content
   */
  def apply(content: FormattedContent): SlotContent = content

  /**
   * Creates SlotContent from plain text.
   *
   * Convenience method for backward compatibility and simple use cases.
   * Converts plain text to FormattedContent with no formatting.
   *
   * @param text Plain text (no markdown)
   * @return SlotContent with single unformatted text span
   */
  def fromPlainText(text: String): SlotContent =
    if text.isEmpty then FormattedContent.empty
    else FormattedContent(
      List(TextSpan(text, bold = false, italic = false, code = false)),
      List.empty
    )

  /**
   * Empty slot content.
   */
  val empty: SlotContent = FormattedContent.empty

  extension (content: SlotContent)
    /**
     * Get underlying FormattedContent.
     */
    def formatted: FormattedContent = content

    /**
     * Get plain text (strip formatting).
     *
     * Used for validation and display.
     */
    def plainText: String =
      val fc: FormattedContent = content
      fc.plainText

    /**
     * Count lines in plain text (newline-separated).
     */
    def lineCount: Int =
      val fc: FormattedContent = content
      fc.lineCount

    /**
     * Count characters in plain text.
     */
    def charCount: Int =
      val fc: FormattedContent = content
      fc.plainText.length

    /**
     * Count words in plain text (whitespace-separated).
     */
    def wordCount: Int =
      val fc: FormattedContent = content
      fc.wordCount
