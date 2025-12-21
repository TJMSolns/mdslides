package com.tjmsolutions.mdslides.domain

/**
 * Slide template defining available slots and constraints.
 *
 * Templates control:
 * - Which slots are required/optional
 * - Content constraints per slot (max lines, max chars, etc.)
 * - Allowed content types per slot
 *
 * Example templates:
 * - "title": title, subtitle (opt), author (opt)
 * - "content": heading, body
 * - "code": heading, code (with syntax highlighting)
 *
 * Related Governance:
 * - ADR-008: Slot-Based Content Model
 * - PDR-004: Template Constraints
 */
case class Template(
  name: String,
  slots: List[SlotDefinition]
):
  /**
   * Get required slots for this template.
   */
  def requiredSlots: List[SlotDefinition] =
    slots.filter(_.required)

  /**
   * Get optional slots for this template.
   */
  def optionalSlots: List[SlotDefinition] =
    slots.filterNot(_.required)

  /**
   * Find slot definition by name.
   */
  def getSlot(name: String): Option[SlotDefinition] =
    slots.find(_.name == name)

end Template

object Template:
  /**
   * Title template.
   *
   * Slots:
   * - title (required, max 2 lines)
   * - subtitle (optional, max 2 lines)
   * - author (optional, max 80 chars)
   *
   * Related: US-001 (Create Title Slide)
   */
  val title: Template =
    Template(
      name = "title",
      slots = List(
        SlotDefinition.title,
        SlotDefinition.subtitle,
        SlotDefinition.author
      )
    )

  /**
   * Content template.
   *
   * Slots:
   * - heading (required, max 80 chars)
   * - body (required, max 12 lines, max 150 words)
   *
   * Related: US-002 (Create Content Slide)
   */
  val content: Template =
    Template(
      name = "content",
      slots = List(
        SlotDefinition.heading,
        SlotDefinition.body
      )
    )

  /**
   * Get template by name.
   */
  def fromName(name: String): Either[String, Template] =
    name match
      case "title" => Right(title)
      case "content" => Right(content)
      case _ => Left(s"Template '$name' not found")

end Template
