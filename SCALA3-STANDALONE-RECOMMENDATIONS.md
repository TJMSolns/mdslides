# MDSlides: Scala 3 Standalone Architecture Recommendations

## Overview

You've wisely chosen to build MDSlides as a **standalone Scala 3 CLI application** rather than a microservices architecture. This document outlines recommendations for adapting the ceremony-based framework to this context.

---

## Key Changes Made

### 1. Charter Updates
- ✅ Executive summary now describes standalone CLI app
- ✅ Removed Pekko, reactive Postgres, Karate (service-oriented tools)
- ✅ Added Cats Effect, Scalatags, Decline (CLI-appropriate tools)
- ✅ Added constraint: "Use Scala 3 enums, case classes, and opaque types (not Java Records)"
- ✅ Clarified out-of-scope: no web services, no databases

### 2. Build Configuration
- ✅ Replaced Pekko/R2DBC with Cats Effect 3
- ✅ Added functional libraries (Cats Core, Circe)
- ✅ Added CLI parsing (Decline)
- ✅ Added HTML generation (Scalatags)
- ✅ Added Markdown parsing (Flexmark)
- ✅ Updated test dependencies (ScalaTest with BDD, ScalaCheck, Cats Effect testing)

### 3. Context Map
- ✅ Defined 3 internal bounded contexts (not microservices):
  - Slide Deck Authoring (Core Domain)
  - Rendering Engine (Supporting)
  - CLI & File I/O (Generic)
- ✅ Removed infrastructure dependencies (PostgreSQL, Pekko, OpenTelemetry)

---

## Architecture Recommendations

### Pure Functional Core (Hexagonal Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                     CLI Layer (Main.scala)                   │
│  - Argument parsing (Decline)                                │
│  - Error formatting                                          │
│  - Cats Effect IOApp                                         │
└─────────────────────────────────┬───────────────────────────┘
                                  │
┌─────────────────────────────────▼───────────────────────────┐
│              Application Layer (Use Cases)                   │
│  - GenerateSlideDeck(input, output, theme)                   │
│  - ValidateSlideDeck(input)                                  │
│  - Pure functions returning IO[Result]                       │
└─────────────────────────────────┬───────────────────────────┘
                                  │
┌─────────────────────────────────▼───────────────────────────┐
│                  Domain Layer (Pure)                         │
│  Aggregates:                                                 │
│  - SlideDeck (root aggregate)                                │
│  - Slide                                                     │
│  - Theme                                                     │
│  Value Objects: SlideTitle, Content, Color, Font            │
│  Domain Events: SlideAdded, ThemeApplied                     │
│  NO SIDE EFFECTS - all pure functions                        │
└─────────────────────────────────┬───────────────────────────┘
                                  │
┌─────────────────────────────────▼───────────────────────────┐
│            Infrastructure Layer (Adapters)                   │
│  - FileSystemAdapter (os-lib + IO)                           │
│  - MarkdownParser (Flexmark wrapper)                         │
│  - HTMLRenderer (Scalatags)                                  │
│  - PDFRenderer (Flying Saucer or similar)                    │
│  - ThemeLoader (Circe JSON decoder)                          │
└─────────────────────────────────────────────────────────────┘
```

### Example Domain Model (Scala 3)

```scala
package solns.tjm.mdslides.domain

import cats.data.NonEmptyList

// Opaque types for type safety
opaque type SlideTitle = String
object SlideTitle:
  def apply(value: String): Either[String, SlideTitle] =
    if value.trim.isEmpty then Left("Title cannot be empty")
    else if value.length > 100 then Left("Title too long")
    else Right(value)

// Enums for slide types
enum SlideLayout:
  case Title, Content, TwoColumn, Image, Code

enum SlideTransition:
  case None, Fade, Slide, Zoom

// Value objects
case class Color(hex: String)
case class Font(family: String, size: Int)

// Theme aggregate
case class Theme(
  name: String,
  background: Color,
  foreground: Color,
  accentColor: Color,
  titleFont: Font,
  bodyFont: Font
)

// Slide entity
case class Slide(
  id: SlideId,
  title: SlideTitle,
  content: String,
  layout: SlideLayout,
  transition: SlideTransition
)

// Root aggregate
case class SlideDeck(
  title: SlideTitle,
  author: String,
  theme: Theme,
  slides: NonEmptyList[Slide]
):
  // Domain methods (pure)
  def addSlide(slide: Slide): SlideDeck =
    copy(slides = slides :+ slide)

  def changeTheme(newTheme: Theme): SlideDeck =
    copy(theme = newTheme)

  def validate: Either[NonEmptyList[String], SlideDeck] =
    // Validation logic
    if slides.size > 100 then Left(NonEmptyList.one("Too many slides"))
    else Right(this)
```

### Example Application Use Case

```scala
package solns.tjm.mdslides.application

import cats.effect.IO
import cats.implicits.*
import solns.tjm.mdslides.domain.*
import solns.tjm.mdslides.infrastructure.*

trait GenerateSlideDeckUseCase:
  def execute(input: os.Path, output: os.Path, themeName: String): IO[Unit]

class GenerateSlideDeckUseCaseImpl(
  parser: MarkdownParser,
  themeLoader: ThemeLoader,
  htmlRenderer: HTMLRenderer,
  fileSystem: FileSystemAdapter
) extends GenerateSlideDeckUseCase:

  def execute(input: os.Path, output: os.Path, themeName: String): IO[Unit] =
    for
      // Read input file
      markdown <- fileSystem.readFile(input)

      // Load theme
      theme <- themeLoader.load(themeName)

      // Parse markdown to domain model
      slideDeck <- parser.parse(markdown, theme)

      // Validate domain model
      validDeck <- IO.fromEither(slideDeck.validate.leftMap(errors =>
        new RuntimeException(s"Validation failed: ${errors.toList.mkString(", ")}")
      ))

      // Render to HTML
      html <- htmlRenderer.render(validDeck)

      // Write output
      _ <- fileSystem.writeFile(output, html)
    yield ()
```

### Example CLI Entry Point

```scala
package solns.tjm.mdslides

import cats.effect.{ExitCode, IO, IOApp}
import com.monovore.decline.*
import com.monovore.decline.effect.*

object Main extends CommandIOApp(
  name = "mdslides",
  header = "Generate beautiful slide decks from Markdown"
):
  override def main: Opts[IO[ExitCode]] =
    val inputOpt = Opts.argument[os.Path]("input")
    val outputOpt = Opts.option[os.Path]("output", short = "o", help = "Output file")
    val themeOpt = Opts.option[String]("theme", short = "t", help = "Theme name").withDefault("default")

    (inputOpt, outputOpt, themeOpt).mapN { (input, output, theme) =>
      // Wire up dependencies (poor man's DI for now)
      val parser = MarkdownParserImpl()
      val themeLoader = ThemeLoaderImpl()
      val htmlRenderer = HTMLRendererImpl()
      val fileSystem = FileSystemAdapterImpl()
      val useCase = GenerateSlideDeckUseCaseImpl(parser, themeLoader, htmlRenderer, fileSystem)

      useCase.execute(input, output, theme).as(ExitCode.Success)
    }
```

---

## Testing Recommendations

### BDD with ScalaTest (not Karate)

Since you're not building a service, use **ScalaTest's BDD DSL** instead of Karate:

```scala
package solns.tjm.mdslides.domain

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers

class SlideDeckSpec extends AnyFunSpec with GivenWhenThen with Matchers:

  describe("SlideDeck") {
    describe("when adding a slide") {
      it("should append the slide to the deck") {
        Given("a slide deck with 2 slides")
        val deck = SlideDeck(
          title = SlideTitle("My Deck").toOption.get,
          author = "John Doe",
          theme = Theme.default,
          slides = NonEmptyList.of(slide1, slide2)
        )

        When("I add a third slide")
        val newDeck = deck.addSlide(slide3)

        Then("the deck should have 3 slides")
        newDeck.slides.size shouldBe 3

        And("the new slide should be last")
        newDeck.slides.last shouldBe slide3
      }
    }
  }
```

### Property-Based Testing with ScalaCheck

```scala
package solns.tjm.mdslides.domain

import org.scalacheck.{Arbitrary, Gen, Prop}
import org.scalacheck.Prop.forAll
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class SlideDeckProperties extends AnyPropSpec with ScalaCheckPropertyChecks:

  // Generators
  val colorGen: Gen[Color] = Gen.hexStr.map(hex => Color(s"#$hex"))

  val themeGen: Gen[Theme] = for
    name <- Gen.alphaNumStr
    bg <- colorGen
    fg <- colorGen
    accent <- colorGen
  yield Theme(name, bg, fg, accent, Font.default, Font.default)

  // Property: Adding a slide always increases size by 1
  property("adding a slide increases deck size by 1") {
    forAll { (deck: SlideDeck, slide: Slide) =>
      val original = deck.slides.size
      val updated = deck.addSlide(slide)
      updated.slides.size == original + 1
    }
  }

  // Property: Theme change preserves slide count
  property("changing theme preserves slides") {
    forAll(themeGen) { newTheme =>
      forAll { (deck: SlideDeck) =>
        val updated = deck.changeTheme(newTheme)
        updated.slides.size == deck.slides.size
      }
    }
  }
```

---

## Ceremony Adaptation for Standalone Apps

### Ceremonies to Keep (All Relevant)

✅ **Phase 1: Discovery**
- Event Storming (identify domain events like SlideAdded, ThemeApplied)
- Ubiquitous Language (SlideDeck, Slide, Theme, Rendering, etc.)
- Domain Modeling (define aggregates, value objects)
- Context Mapping (internal contexts, not microservices)

✅ **Phase 2: Specification**
- Three Amigos (write BDD scenarios with ScalaTest)
- Example Mapping (concrete examples for validation rules)
- Acceptance Criteria Review (ensure scenarios match domain model)

✅ **Phase 3: Implementation**
- Test-First Pairing (write failing ScalaTest before implementation)
- Red-Green-Refactor (TDD cycle)
- Property-Based Testing (use ScalaCheck for invariants)

✅ **Phase 4: Integration & Feedback**
- Scenario-to-Test Decomposition (trace BDD → unit tests)
- Domain Model Retrospective (does code match model?)
- Living Documentation Sync (keep diagrams/glossary current)

### Ceremonies to Simplify

⚠️ **Cross-Boundary Integration Testing**
- No external services, so focus on:
  - Testing file system interactions (use test directories)
  - Testing Markdown parser integration
  - Testing HTML/PDF renderer integration
- Use test doubles (in-memory file system) where appropriate

⚠️ **Automated Deployment**
- No Kubernetes, so adapt to:
  - Native image creation (GraalVM)
  - Distribution packaging (Coursier bootstrap, Homebrew tap)
  - GitHub releases with binaries

---

## Technology Stack Summary

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Effects** | Cats Effect 3 | Pure functional I/O, concurrency |
| **Parsing** | Flexmark | Markdown → AST |
| **HTML** | Scalatags | Type-safe HTML generation |
| **PDF** | Flying Saucer | HTML → PDF (via xhtml2pdf or similar) |
| **CLI** | Decline | Command-line argument parsing |
| **File I/O** | os-lib + Cats Effect IO | File system operations |
| **JSON** | Circe | Theme configuration parsing |
| **Testing** | ScalaTest + ScalaCheck | BDD, property-based testing |

---

## Alternative: ZIO Instead of Cats Effect

If you prefer **ZIO 2** over Cats Effect:

```scala
// build.sc changes
ivy"dev.zio::zio:2.0.21",
ivy"dev.zio::zio-cli:0.5.0",
ivy"dev.zio::zio-json:0.6.2"

// Main.scala with ZIO
import zio.*
import zio.cli.*

object Main extends ZIOCliDefault:
  val command: Command[Unit] =
    Command("generate")
      .withHelp("Generate slide deck")
      .map { _ =>
        for
          _ <- Console.printLine("Generating slides...")
          // Use case execution
        yield ()
      }
```

**Recommendation**: Stick with **Cats Effect** unless you have strong ZIO experience. Cats Effect has better library ecosystem and is more aligned with DDD patterns.

---

## Next Steps

### Phase 1: Domain Modeling (Start Here!)

1. **Event Storming Session** (solo or with a rubber duck):
   - Events: `SlideDeckCreated`, `SlideAdded`, `ThemeApplied`, `DeckValidated`, `HTMLGenerated`, `PDFExported`
   - Commands: `CreateSlideDeck`, `AddSlide`, `ApplyTheme`, `ValidateDeck`, `GenerateHTML`, `ExportToPDF`
   - Aggregates: `SlideDeck` (root), `Slide`, `Theme`

2. **Ubiquitous Language**:
   - Create `doc/domain-models/ubiquitous-language.md`
   - Define terms: SlideDeck, Slide, Theme, Layout, Transition, Rendering, DSL, etc.
   - Ban terms like: "Manager", "Handler", "Service", "Util"

3. **Domain Model**:
   - Create `doc/domain-models/aggregates/slide-deck-aggregate.md`
   - Document invariants (e.g., "Deck must have at least 1 slide", "Title max 100 chars")
   - Create Mermaid class diagram

4. **Context Map**:
   - Already done! (see updated CONTEXT-MAP.md)

### Phase 2: First BDD Scenario

Write your first acceptance criteria in ScalaTest:

```gherkin
Feature: Generate slide deck from Markdown

  Scenario: Simple slide deck generation
    Given a Markdown file with title and 3 slides
    And a "default" theme
    When I run "mdslides input.md -o output.html -t default"
    Then an HTML file should be generated
    And the HTML should contain 3 slide sections
    And the HTML should use the default theme colors
```

Then implement as ScalaTest:

```scala
class GenerateSlideDeckSpec extends AnyFunSpec with GivenWhenThen:
  describe("Feature: Generate slide deck from Markdown") {
    it("Scenario: Simple slide deck generation") {
      Given("a Markdown file with title and 3 slides")
      val markdown = """
        # My Presentation
        ## Slide 1
        Content 1
        ## Slide 2
        Content 2
        ## Slide 3
        Content 3
      """

      And("a 'default' theme")
      val theme = Theme.default

      When("I parse the markdown")
      val result = parser.parse(markdown, theme)

      Then("a slide deck should be generated")
      result shouldBe a[Right[_, SlideDeck]]

      And("the deck should contain 3 slides")
      result.toOption.get.slides.size shouldBe 3
    }
  }
```

---

## Common Pitfalls to Avoid

### ❌ Don't Do This (Service Mindset)

```scala
// ❌ Creating unnecessary layers
class SlideDeckService
class SlideDeckRepository  // No database!
class SlideDeckController  // No HTTP!
class SlideDeckDTO         // Just use domain model

// ❌ Using blocking I/O
def readFile(path: String): String =
  scala.io.Source.fromFile(path).mkString  // BLOCKS!

// ❌ Side effects in domain
case class SlideDeck(...):
  def save(): Unit =  // Side effect!
    // Write to database...
```

### ✅ Do This (Standalone Mindset)

```scala
// ✅ Pure domain model
case class SlideDeck(...):
  def addSlide(slide: Slide): SlideDeck = copy(...)  // Pure!

// ✅ Effects in application layer
def readFile(path: os.Path): IO[String] =
  IO.blocking(os.read(path))  // Non-blocking via IO

// ✅ Dependency injection via constructor
class GenerateSlideDeckUseCase(
  parser: MarkdownParser,
  renderer: Renderer
)
```

---

## Questions?

Feel free to ask about:
- Scala 3 idioms (enums, opaque types, extension methods)
- Cats Effect patterns (Resource, Stream, Fiber)
- Domain modeling in functional style
- Adapting ceremonies for standalone apps

Good luck with MDSlides! 🚀
