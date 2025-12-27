package com.tjmsolutions.mdslides.cli

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*
import com.tjmsolutions.mdslides.domain.{SlideDeck, Theme}
import com.tjmsolutions.mdslides.infrastructure.parser.{MarkdownParser, FlexmarkAdapter}
import com.tjmsolutions.mdslides.infrastructure.renderer.HTMLRenderer
import com.tjmsolutions.mdslides.infrastructure.theme.{ThemeJsonAdapter, ThemeLoader}
import com.tjmsolutions.mdslides.infrastructure.assets.{ImageAssetCopier, CopiedImage}

import java.nio.file.{Files, Path, Paths, StandardOpenOption}

/**
 * CLI entry point for MDSlides (v1.0).
 *
 * Commands:
 * - render: Convert markdown to HTML presentation directory
 *
 * Usage:
 * ```
 * # Simple form (inferred input/output)
 * mdslides render my-preso
 * mdslides render my-preso --theme dark
 *
 * # Explicit form (custom paths)
 * mdslides render -i slides.md -o output-dir
 * mdslides render --input slides.md --output dist --theme retisio
 *
 * # Flags
 * mdslides render my-preso --no-copy-images
 * ```
 *
 * Related Governance:
 * - US-019: Improved CLI UX (v1.0)
 * - PDR-015: CLI UX Design
 * - ADR-012: Image Asset Copying Strategy
 * - US-016: Directory-Based Themes
 */
object Main extends IOApp:

  /**
   * Load theme from name using directory-based themes (US-016).
   */
  def loadTheme(themeName: String): IO[Theme] =
    themeName match
      case "light" => IO.pure(Theme.light)
      case "dark" => IO.pure(Theme.dark)
      case "corporate" | "tjm-solutions" => IO.pure(Theme.corporate)
      case name =>
        // Load theme from directory
        val themeDir = Paths.get("./themes")
        ThemeLoader.loadTheme(name, themeDir)
          .flatTap(t => IO.println(s"✓ Loaded theme: ${t.name} v${t.version}"))
          .handleErrorWith { error =>
            IO.println(s"✗ Theme load error: ${error.getMessage}") *>
              IO.raiseError(error)
          }

  /**
   * Extract all image URLs from deck (content + backgrounds).
   */
  def extractImageUrls(deck: SlideDeck): List[String] =
    import com.tjmsolutions.mdslides.domain.{SlideBackground, BackgroundConfig}

    val contentImages = deck.slides.toList.flatMap { slide =>
      slide.slots.values.toList.flatMap { slotContent =>
        FlexmarkAdapter.parseInlineFormatting(slotContent).contentImages.map(_.url)
      }
    }

    val backgroundImages = deck.slides.toList.flatMap { slide =>
      slide.backgroundImage match
        case Some(bg: String) => List(bg)
        case Some(bg: BackgroundConfig) => List(bg.imagePath)
        case None => List()
    }

    (contentImages ++ backgroundImages).distinct

  /**
   * Extract theme background images (US-012).
   */
  def extractThemeImages(theme: Theme): List[String] =
    val defaultBg = theme.background.image.toList
    val templateBgs = theme.templateBackgrounds.values.toList
    (defaultBg ++ templateBgs).distinct

  /**
   * Main rendering logic.
   */
  def renderPresentation(cliArgs: CLIArguments): IO[ExitCode] =
    for {
      // Resolve input and output paths
      inputPath <- cliArgs.deckName match
        case Some(deckName) =>
          // Simple form: infer from deck name
          IO.fromEither(PathResolver.findInputFile(deckName).left.map(new RuntimeException(_)))
        case None =>
          // Explicit form: use provided input file
          IO.pure(cliArgs.inputFile.get)

      outputDir <- cliArgs.deckName match
        case Some(deckName) =>
          // Simple form: DECK_NAME → DECK_NAME/ directory
          IO.pure(PathResolver.determineOutputDir(deckName))
        case None =>
          // Explicit form: use provided output directory
          IO.pure(cliArgs.outputDir.get)

      // Create output directory
      _ <- PathResolver.ensureOutputDirExists(outputDir)

      // Load theme
      _ <- IO.println(s"Loading theme: ${cliArgs.themeName}")
      theme <- loadTheme(cliArgs.themeName)

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

      // Copy images if enabled
      _ <- if cliArgs.copyImages then
        for {
          _ <- IO.println("Copying images...")

          // Extract deck images
          deckImages = extractImageUrls(validatedDeck)

          // Extract theme images
          themeImages = extractThemeImages(theme)

          sourceDir = inputPath.getParent match
            case null => Paths.get(".")
            case dir => dir

          // Copy deck images to OUTPUT_DIR/images/
          imagesDir = outputDir.resolve("images")
          deckResult <- if deckImages.nonEmpty then
            IO.fromEither(
              ImageAssetCopier.copyImages(deckImages, sourceDir, imagesDir)
                .left.map(err => new RuntimeException(err))
            )
          else
            IO.pure(List.empty[CopiedImage])

          // Copy theme images to OUTPUT_DIR/backgrounds/
          backgroundsDir = outputDir.resolve("backgrounds")
          themeResult <- if themeImages.nonEmpty && cliArgs.themeName != "light" && cliArgs.themeName != "dark" && cliArgs.themeName != "corporate" then
            val themeDir = Paths.get("./themes").resolve(cliArgs.themeName)
            IO.fromEither(
              ImageAssetCopier.copyImages(themeImages, themeDir, backgroundsDir)
                .left.map(err => new RuntimeException(err))
            )
          else
            IO.pure(List.empty[CopiedImage])

          result = deckResult ++ themeResult
          _ <- if result.isEmpty then
            IO.println("  No images to copy")
          else
            val totalSize = result.map(_.sizeBytes).sum
            val totalSizeKB = totalSize / 1024.0
            IO.println(s"✓ Copied ${result.length} image(s) (${f"$totalSizeKB%.1f"} KB total)")
        } yield ()
      else
        IO.println("Image copying disabled (--no-copy-images)")

      // Render to HTML with theme
      _ <- IO.println(s"Rendering HTML with theme: ${theme.name}")
      html = HTMLRenderer.renderDeck(validatedDeck, theme)
      _ <- IO.println(s"✓ Generated ${html.length} characters of HTML")

      // Write index.html to output directory
      indexPath = outputDir.resolve("index.html")
      _ <- IO.println(s"Writing HTML to: $indexPath")
      _ <- IO.blocking(Files.writeString(indexPath, html, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))

      _ <- IO.println(s"✓ Successfully created presentation: $outputDir/")
    } yield ExitCode.Success

  /**
   * Parse args and run program.
   */
  override def run(args: List[String]): IO[ExitCode] =
    CLIArguments.parse(args.toArray) match
      case Right(cliArgs) =>
        renderPresentation(cliArgs).handleErrorWith { error =>
          IO.println(s"Error: ${error.getMessage}").as(ExitCode.Error)
        }
      case Left(errorMessage) =>
        IO.println(errorMessage).as(ExitCode.Error)

end Main
