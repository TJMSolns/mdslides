package com.tjmsolutions.mdslides.domain

/**
 * Domain validation errors.
 *
 * Error types:
 * - StructureError: Missing required slots, invalid template binding
 * - ContentError: Slot constraint violations (max lines, max chars, etc.)
 * - DensityWarning: Readability warnings (non-blocking)
 *
 * Related Governance:
 * - ADR-002: Validation Pipeline Architecture
 * - ADR-003: Error Collection Pattern
 * - POL-001: Ubiquitous Language Enforcement
 */
enum ValidationError:
  /**
   * Structural validation failure (blocking).
   *
   * Examples:
   * - Missing required slot
   * - Template not found
   * - Invalid slot name
   */
  case StructureError(slideId: SlideId, message: String)

  /**
   * Content constraint violation (blocking).
   *
   * Examples:
   * - Title exceeds max 2 lines
   * - Author exceeds max 80 chars
   * - Body exceeds max 150 words
   */
  case ContentError(slideId: SlideId, slotName: String, message: String)

  /**
   * Density/readability warning (non-blocking).
   *
   * Examples:
   * - Body has 13 lines (over recommended 12)
   * - Content word count high (readability concern)
   */
  case DensityWarning(slideId: SlideId, slotName: String, message: String)

  /**
   * Human-readable error message.
   */
  def displayMessage: String = this match
    case StructureError(id, msg) =>
      s"Slide ${id.toInt}: Structure Error - $msg"
    case ContentError(id, slot, msg) =>
      s"Slide ${id.toInt}: Content Error in '$slot' slot - $msg"
    case DensityWarning(id, slot, msg) =>
      s"Slide ${id.toInt}: Density Warning in '$slot' slot - $msg"

end ValidationError
