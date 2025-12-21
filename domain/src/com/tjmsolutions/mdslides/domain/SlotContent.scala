package com.tjmsolutions.mdslides.domain

/**
 * Content for a template slot.
 *
 * Can contain plain text, Markdown formatting, or be empty.
 * Validation constraints (max lines, max chars) applied by template.
 *
 * Related Governance:
 * - POL-001: Ubiquitous Language Enforcement
 * - ADR-008: Slot-Based Content Model
 */
opaque type SlotContent = String

object SlotContent:
  /**
   * Creates SlotContent from a String.
   *
   * @param value The slot content text
   * @return SlotContent (empty string allowed)
   */
  def apply(value: String): SlotContent = value

  /**
   * Empty slot content.
   */
  val empty: SlotContent = ""

  extension (content: SlotContent)
    def value: String = content

    /**
     * Count lines in content (newline-separated).
     */
    def lineCount: Int =
      val s: String = content
      if s.length == 0 then 0
      else s.split("\n").length

    /**
     * Count characters in content.
     */
    def charCount: Int =
      val s: String = content
      s.length

    /**
     * Count words in content (whitespace-separated).
     */
    def wordCount: Int =
      val s: String = content
      if s.length == 0 then 0
      else s.split("\\s+").length
