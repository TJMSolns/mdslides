package com.tjmsolutions.mdslides.cli

import cats.effect.IO
import java.nio.file.{Files, Path, Paths}
import scala.jdk.CollectionConverters.*

/**
 * Path resolution service for MDSlides CLI.
 *
 * Handles:
 * - Finding input markdown files (try .md then .markdown)
 * - Determining output directories from deck names
 * - Creating output directories (with parent directories)
 */
object PathResolver:

  /**
   * Find input markdown file from deck name.
   *
   * Resolution order:
   * 1. Try `deckName.md` (preferred)
   * 2. Try `deckName.markdown` (fallback)
   * 3. Error if neither exists
   *
   * @param deckName Deck name stem (without extension)
   * @param baseDir Directory to search in (defaults to current directory)
   * @return Right(Path) if found, Left(error message) if not found
   */
  def findInputFile(deckName: String, baseDir: Option[Path] = None): Either[String, Path] =
    val searchDir = baseDir.getOrElse(Paths.get("."))
    val mdPath = searchDir.resolve(s"$deckName.md")
    val markdownPath = searchDir.resolve(s"$deckName.markdown")

    // Prefer .md over .markdown
    if Files.exists(mdPath) then
      Right(mdPath)
    else if Files.exists(markdownPath) then
      Right(markdownPath)
    else
      // Neither exists - build helpful error message
      val availableFiles = listMarkdownFiles(searchDir)
      val availableList = if availableFiles.nonEmpty then
        "\n\nFound in current directory:\n" + availableFiles.map(f => s"  - $f").mkString("\n")
      else
        ""

      Left(s"✗ Input file not found: $deckName.md or $deckName.markdown$availableList")

  /**
   * Determine output directory path from deck name.
   *
   * Simple transformation: deckName → Path(deckName)
   *
   * Examples:
   * - "my-preso" → Path("my-preso")
   * - "talks/presentation" → Path("talks/presentation")
   *
   * @param deckName Deck name (may include path separators)
   * @return Output directory path
   */
  def determineOutputDir(deckName: String): Path =
    Paths.get(deckName)

  /**
   * Ensure output directory exists (create if missing).
   *
   * Creates parent directories as needed (mkdir -p behavior).
   * Succeeds if directory already exists.
   *
   * @param outputDir Path to output directory
   * @return IO effect that creates directory
   */
  def ensureOutputDirExists(outputDir: Path): IO[Unit] =
    IO {
      if !Files.exists(outputDir) then
        Files.createDirectories(outputDir)
      ()
    }

  /**
   * List available markdown files in directory.
   *
   * Used for helpful error messages when input file not found.
   *
   * @param dir Directory to search
   * @return List of .md and .markdown filenames
   */
  private def listMarkdownFiles(dir: Path): List[String] =
    if !Files.isDirectory(dir) then
      List.empty
    else
      try
        Files.list(dir)
          .iterator()
          .asScala
          .toList
          .filter(p => Files.isRegularFile(p))
          .map(_.getFileName.toString)
          .filter(name => name.endsWith(".md") || name.endsWith(".markdown"))
          .sorted
      catch
        case _: Exception => List.empty

end PathResolver
