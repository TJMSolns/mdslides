---
template: title
---
# MDSlides Training
## Learn to Create Professional Slide Decks
MDSlides Development Team

---
template: content
---
## What is MDSlides?

MDSlides is a command-line tool for converting Markdown to HTML slides.
Built with Domain-Driven Design and comprehensive testing.

Key benefits:
- Type-safe validation
- Enforces readability constraints
- Clean, standalone HTML output

---
template: content
---
## Why Use MDSlides?

Traditional presentation tools have limitations.
MDSlides enforces best practices automatically.

Benefits:
- Version control friendly (plain text)
- Automated density validation
- No vendor lock-in
- Keyboard-first navigation

---
template: title
---
# Getting Started
## Installation and Setup

---
template: content
---
## Installation Requirements

You need two things to use MDSlides.

Prerequisites:
- Java 11 or higher
- Mill build tool (for building from source)

Optional:
- Git (for cloning the repository)

---
template: content
---
## Quick Installation

Clone and build the project.

Steps:
1. Clone: git clone <repo-url>
2. Build: mill __.compile
3. Test: mill __.test
4. Run: mill cli.run input.md output.html

Ready to create your first presentation!

---
template: title
---
# Slide Templates
## Available Template Types

---
template: content
---
## Title Slide Template

Use for opening slides and section dividers.

Required:
- Title (max 2 lines)

Optional:
- Subtitle (max 2 lines)
- Author (max 80 characters)

Perfect for presentation openings!

---
template: content
---
## Content Slide Template

Use for main content delivery.

Required:
- Heading (max 80 chars, single line)
- Body (max 12 lines, max 150 words)

Enforces cognitive load limits.
Keeps slides focused and readable.

---
template: title
---
# Creating Your First Deck
## Hands-On Tutorial

---
template: content
---
## Markdown Structure

Each slide starts with frontmatter.

Format:
- Use --- to separate slides
- Specify template type
- Add your content
- Repeat for each slide

Simple and clean!

---
template: content
---
## Example Title Slide

Start with frontmatter and content.

Code:
---
template: title
---
# My Presentation
## A Great Topic

See how easy that is?

---
template: content
---
## Example Content Slide

Add heading and body content.

Code:
---
template: content
---
## Key Points
Body text goes here.

Markdown formatting preserved for now.

---
template: title
---
# Validation Rules
## Keeping Your Slides Readable

---
template: content
---
## Why Validation Matters

Cognitive load affects learning.
Too much text overwhelms audiences.

MDSlides enforces limits:
- Line count limits
- Word count limits
- Character count limits

Science-backed constraints!

---
template: content
---
## Title Slide Constraints

Keep titles concise and impactful.

Limits:
- Title: max 2 lines
- Subtitle: max 2 lines
- Author: max 80 characters

Exceeding limits causes validation errors.

---
template: content
---
## Content Slide Constraints

Balance information and readability.

Limits:
- Heading: max 80 characters
- Body: max 12 lines
- Body: max 150 words

Forces clear, focused messaging.

---
template: content
---
## Validation Error Messages

MDSlides reports all errors together.

Example output:
- Slide 1: Content Error in body - exceeds max 12 lines
- Slide 2: Structure Error - missing heading

Fix all issues before generating HTML.

---
template: title
---
# Using the CLI
## Command-Line Interface

---
template: content
---
## Basic CLI Usage

Simple command structure.

Syntax:
mill cli.run input.md output.html

Or with JAR:
java -jar mdslides.jar input.md output.html

That's all you need!

---
template: content
---
## CLI Output

MDSlides provides detailed feedback.

Shows:
- Reading input file
- Parsing progress
- Validation results
- HTML generation status
- Output file location

Know exactly what's happening!

---
template: content
---
## Handling Validation Errors

Errors are clear and actionable.

Process:
1. Run conversion
2. Read error messages
3. Fix violations
4. Re-run conversion
5. Success!

Iterative improvement workflow.

---
template: title
---
# Keyboard Navigation
## Presenting Your Slides

---
template: content
---
## Navigation Controls

Navigate with keyboard shortcuts.

Keys:
- Right Arrow or Space: Next slide
- Left Arrow: Previous slide
- Home: First slide
- End: Last slide

No mouse needed during presentation!

---
template: content
---
## Presentation Tips

Open HTML in any browser.

Best practices:
- Use full-screen mode (F11)
- Test navigation beforehand
- Practice your timing
- Keep backup copy

Professional delivery guaranteed!

---
template: title
---
# Architecture Overview
## How MDSlides Works

---
template: content
---
## Three-Layer Architecture

Clean separation of concerns.

Layers:
- CLI: User interface and I/O
- Infrastructure: Parsing and rendering
- Domain: Pure business logic

Pure functional domain model!

---
template: content
---
## Domain-Driven Design

Business logic in domain layer.

Concepts:
- Aggregates: Slide, SlideDeck
- Value Objects: SlideId, SlotContent
- Validation: Structure and content

Type-safe and testable!

---
template: content
---
## Testing Strategy

Comprehensive test coverage.

Includes:
- 81 total tests
- Property-based testing
- Example-based testing
- Edge case coverage

Quality you can trust!

---
template: title
---
# Best Practices
## Creating Great Presentations

---
template: content
---
## One Idea Per Slide

Focus on single concepts.

Guidelines:
- Clear heading
- 3-5 key points
- Use bullet lists
- Avoid paragraphs

Audiences retain more!

---
template: content
---
## Use Consistent Structure

Template selection matters.

Patterns:
- Title for section breaks
- Content for main points
- Title-Content-Content rhythm works well

Find your style!

---
template: content
---
## Keep It Simple

Less is more in presentations.

Tips:
- Short sentences
- Active voice
- Concrete examples
- Visual hierarchy

Let validation guide you!

---
template: title
---
# Current Limitations
## v0.1.0 MVP Scope

---
template: content
---
## What's Not Included Yet

MVP focuses on core functionality.

Not available:
- Full markdown rendering (bold, italic, links)
- Code syntax highlighting
- Image support
- Custom themes

Coming in future releases!

---
template: content
---
## Planned Features

Roadmap for future versions.

v0.2.0:
- Markdown rendering
- Code blocks
- Image embedding

v0.3.0:
- Speaker notes
- PDF export
- Live preview

---
template: title
---
# Getting Help
## Resources and Support

---
template: content
---
## Documentation

Comprehensive docs available.

Resources:
- README.md: User guide
- INSTALL.md: Setup instructions
- CHANGELOG.md: Version history
- doc/governance/: Design decisions

Everything is documented!

---
template: content
---
## Example Files

Learn by example.

Included:
- example.md: Sample presentation
- mdslides-training.md: This deck!
- Test files in test suites

Study and adapt!

---
template: content
---
## Contributing

Help improve MDSlides!

Ways to contribute:
- Report bugs
- Suggest features
- Submit pull requests
- Write documentation

Community-driven development!

---
template: title
---
# Quick Reference
## Essential Commands

---
template: content
---
## Build Commands

Common operations.

Commands:
- mill __.compile: Build all modules
- mill __.test: Run all tests
- mill cli.run: Convert markdown
- mill clean: Clean build artifacts

Your daily toolkit!

---
template: content
---
## File Structure

Organize your project.

Layout:
- presentation.md: Your slides
- output.html: Generated presentation
- images/: Supporting assets (future)

Keep it organized!

---
template: title
---
# Conclusion
## Start Creating Amazing Slides!

---
template: content
---
## What You Learned Today

Comprehensive MDSlides training complete.

Covered:
- Installation and setup
- Template types
- Validation rules
- CLI usage
- Best practices

You're ready to create!

---
template: content
---
## Next Steps

Put your knowledge into practice.

Actions:
1. Create your first presentation
2. Experiment with templates
3. Learn from validation errors
4. Share your work
5. Provide feedback

Happy presenting!

---
template: title
---
# Thank You!
## Questions?
MDSlides Development Team
