package com.tjmsolutions.mdslides.cli

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*
import com.tjmsolutions.mdslides.domain.SlideDeck
import com.tjmsolutions.mdslides.infrastructure.parser.MarkdownParser
import com.tjmsolutions.mdslides.infrastructure.renderer.HTMLRenderer
import com.monovore.decline.*

import java.nio.file.{Files, Paths, StandardOpenOption}

/**
 * CLI entry point for MDSlides.
 *
 * Commands:
 * - render: Convert markdown to HTML
 *
 * Usage:
 * ```
 * mdslides input.md output.html
 * ```
 *
 * Related Governance:
 * - ADR-001: CLI Architecture
 * - PDR-002: Error Handling Guidelines
 */
object Main extends IOApp:

  /**
   * Command-line options parser.
   */
  val inputFile = Opts.argument[String]("input")
  val outputFile = Opts.argument[String]("output")

  val command = Command(
    name = "mdslides",
    header = "MDSlides - Markdown to HTML Slide Deck Converter"
  ) {
    (inputFile, outputFile).mapN { (input, output) =>
      (Paths.get(input), Paths.get(output))
    }
  }

  /**
   * Main program logic.
   */
  def renderSlides(inputPath: java.nio.file.Path, outputPath: java.nio.file.Path): IO[ExitCode] =
    for {
      // Read input file
      _ <- IO.println(s"Reading markdown from: $inputPath")
      markdown <- IO.blocking(Files.readString(inputPath))

      // Parse markdown
      _ <- IO.println("Parsing markdown...")
      deck <- MarkdownParser.parse(markdown) match
        case Right(deck) =>
          IO.println(s"✓ Parsed ${deck.slideCount} slide(s)").as(deck)
        case Left(error) =>
          IO.println(s"✗ Parse error: $error") *> IO.raiseError(new RuntimeException(error))

      // Validate deck
      _ <- IO.println("Validating slide deck...")
      validatedDeck <- SlideDeck.validated(deck.slides) match
        case Right(validDeck) =>
          IO.println("✓ Validation passed").as(validDeck)
        case Left(errors) =>
          val errorMessages = errors.toList.map(_.displayMessage).mkString("\n  - ")
          IO.println(s"✗ Validation failed:\n  - $errorMessages") *>
            IO.raiseError(new RuntimeException("Validation failed"))

      // Render to HTML
      _ <- IO.println("Rendering HTML...")
      html = HTMLRenderer.renderDeck(validatedDeck)
      _ <- IO.println(s"✓ Generated ${html.length} characters of HTML")

      // Write output file
      _ <- IO.println(s"Writing HTML to: $outputPath")
      _ <- IO.blocking(Files.writeString(outputPath, html, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))

      _ <- IO.println(s"✓ Successfully created presentation: $outputPath")
    } yield ExitCode.Success

  /**
   * Parse args and run program.
   */
  override def run(args: List[String]): IO[ExitCode] =
    command.parse(args) match
      case Right((inputPath, outputPath)) =>
        renderSlides(inputPath, outputPath).handleErrorWith { error =>
          IO.println(s"Error: ${error.getMessage}").as(ExitCode.Error)
        }
      case Left(help) =>
        IO.println(help.toString).as(ExitCode.Error)

end Main
