# MDSlides Product Roadmap

**Last Updated**: 2025-12-27
**Status**: Active Development
**Current Version**: v1.3.0
**Maintainer**: Tony Moores, TJM Solutions

---

## Vision

MDSlides aims to be the premier domain-driven, test-first presentation framework that transforms Markdown into professional HTML slide decks. Built on DDD principles with comprehensive test coverage, MDSlides prioritizes code quality, maintainability, and extensibility.

**Core Principles:**
1. **Domain-Driven Design**: Pure functional domain models, clear bounded contexts
2. **Test-First Development**: TDD with Event Storming → Three Amigos → Example Mapping → RED-GREEN-REFACTOR
3. **Quality Over Speed**: 100% test coverage, property-based testing, comprehensive validation
4. **Zero Technical Debt**: Fix issues immediately, maintain clean architecture
5. **Documentation as Code**: Every feature documented with Event Storming, Three Amigos, Example Mapping

---

## Release Timeline

### ✅ Phase 1: Foundation (v0.1.0 - v0.4.0) - COMPLETED

**Goal**: Establish core architecture and basic presentation capabilities

- **v0.1.0 MVP** (Nov 2024): Basic slide rendering, templates, validation
- **v0.2.0** (Dec 2024): Markdown formatting, code blocks, theme system
- **v0.3.0** (Dec 2024): Image support, asset copying
- **v0.4.0** (Dec 2024): Directory-based themes, background images, list rendering

**Achievements:**
- Established DDD architecture with pure domain models
- Comprehensive test suite (171 tests)
- Theme system with 4 built-in themes
- Clean separation of domain, infrastructure, and CLI layers

---

### ✅ Phase 2: Essential Features (v1.0.0 - v1.3.0) - COMPLETED

**Goal**: Add critical presentation features to reach feature parity with basic presentation tools

#### v1.0.0 - Speaker Notes Foundation (Dec 2024)
**Status**: ✅ RELEASED
**Key Features:**
- Speaker notes rendering with dual-pane layout
- Bidirectional synchronization between main and speaker views
- Timer functionality for presentations
- **Test Coverage**: 268 tests (138 domain, 130 infrastructure)

#### v1.1.0 - Speaker View Enhancement (Dec 2024)
**Status**: ✅ RELEASED
**Key Features:**
- Full speaker view window with next slide preview
- Cross-window sync via localStorage
- Enhanced keyboard navigation in speaker view
- **Test Coverage**: 290 tests (150 domain, 140 infrastructure)

#### v1.2.0 - Nested List Support (Dec 2024)
**Status**: ✅ RELEASED
**Key Features:**
- Up to 3 levels of list nesting
- Visual hierarchy (disc → circle → square)
- Numbering hierarchy (decimal → lower-alpha → lower-roman)
- Mixed nesting support
- **Test Coverage**: 290 tests (150 domain, 140 infrastructure)
- **Critical Bug Fix**: Scalatags fragment combining issue

#### v1.3.0 - Syntax Highlighting (Dec 2025) ✨ CURRENT
**Status**: ✅ RELEASED
**Key Features:**
- CDN-based highlight.js integration (190+ languages)
- Theme-aware highlighting (GitHub for light, Monokai Sublime for dark)
- Automatic highlighting on page load
- Graceful degradation if CDN unavailable
- **Test Coverage**: 322 tests (158 domain, 164 infrastructure)

**Phase 2 Impact:**
- Transformed from basic renderer to fully-featured presentation tool
- Added critical features: speaker notes, nested lists, syntax highlighting
- Maintained 100% test coverage throughout
- Discovered 2 critical bugs requiring hotfix (list ordering, 'S' key)

---

### 🔥 Phase 2.5: Critical Bugfixes (v1.3.1) - CURRENT

**Goal**: Fix critical bugs discovered in v1.3.0

#### v1.3.1 - Critical Bugfix Hotfix (Dec 2025)
**Status**: 🔴 IN PROGRESS
**Priority**: P0 - CRITICAL
**Target**: 2025-12-30 (3 days)

**Critical Bugs:**
- **BUG-001**: List Rendering Order Incorrect
  - Ordered and unordered lists render in wrong sequence
  - Affects all presentations with mixed list types
  - Fix: Preserve source order in FlexmarkAdapter or HTMLRenderer
  - Add regression test for list ordering

- **BUG-002**: 'S' Key Does Not Open Speaker View
  - Documented functionality never implemented
  - No keyboard handler for 'S' key in navigation.js
  - Fix: Add 'S' key handler to open speaker.html via window.open()
  - Add test for keyboard handler presence

**Success Criteria:**
- Both bugs fixed and verified with integration tests
- 324+ tests passing (322 + 2 regression tests)
- No new bugs introduced
- CHANGELOG updated with hotfix notes
- Released within 3 days of discovery

**Estimated Effort**: 1 sprint (3 days)
**Dependencies**: None
**Risks**: Low - localized changes, well-tested areas

**Related Documentation:**
- [KNOWN-ISSUES.md](KNOWN-ISSUES.md) - Bug tracking

---

### 🎯 Phase 3: Quality & Accessibility (v1.4.0 - v1.6.0) - NEXT

**Goal**: Ensure presentations are accessible, high-quality, and standards-compliant

#### v1.4.0 - Accessibility Validation (Q1 2026)
**Status**: 📋 PLANNED
**Priority**: COULD HAVE → SHOULD HAVE
**Key Features:**
- WCAG 2.1 AA compliance checking
- Contrast ratio validation for themes
- Alt text validation for images
- Keyboard navigation audit
- Warning-based validation (non-blocking)

**Success Criteria:**
- All built-in themes pass WCAG 2.1 AA
- Clear accessibility warnings in CLI output
- Documentation on creating accessible presentations
- Automated accessibility tests

**Estimated Effort**: 2-3 sprints
**Dependencies**: None
**Risks**: WCAG standards complexity

---

#### v1.5.0 - Google Fonts Support (Q1-Q2 2026)
**Status**: 📋 PLANNED
**Priority**: COULD HAVE
**Key Features:**
- Load web fonts from Google Fonts API
- Theme configuration for custom font families
- Fallback to system fonts if CDN unavailable
- Font loading optimization (preload, font-display)

**Success Criteria:**
- Themes can specify Google Fonts URLs
- Automatic font preloading in HTML head
- Graceful degradation to system fonts
- Performance: fonts load without blocking render

**Estimated Effort**: 1-2 sprints
**Dependencies**: None
**Risks**: CDN availability, performance impact

---

#### v1.6.0 - Quality of Life Improvements (Q2 2026)
**Status**: 💡 IDEATION
**Priority**: COULD HAVE
**Potential Features:**
- Improved error messages with line numbers
- Preview mode (watch Markdown, auto-reload browser)
- Slide thumbnails in speaker view
- Presentation metadata (author, date, version)
- Export to PDF via headless browser

**Note**: Features to be refined based on user feedback

---

### 🚀 Phase 4: Advanced Features (v2.0.0+) - FUTURE

**Goal**: Extend capabilities for complex presentations and specialized use cases

#### v2.0.0 - Major Feature Release (Q3-Q4 2026)
**Status**: 🔮 FUTURE
**Priority**: WON'T HAVE (current release)
**Deferred Features:**
- **US-022**: Mermaid diagram support
  - Embed flowcharts, sequence diagrams, Gantt charts
  - Syntax highlighting for Mermaid code blocks
- **US-005-007**: Additional templates
  - Diagram template (centered diagram with caption)
  - Closing template (thank you slide)
  - Section title template (section dividers)
- **Configuration Management**
  - Global configuration file (~/.mdslides/config.json)
  - Project-level configuration (.mdslides/config.json)
  - CLI argument overrides
- **Custom Theme Generator**
  - CLI tool to scaffold new themes
  - Theme validation and linting
  - Theme preview mode

**Success Criteria:**
- Diagram support for 5+ Mermaid diagram types
- 3 new templates with comprehensive examples
- Configuration cascading works correctly
- Theme generator creates valid themes

**Estimated Effort**: 6-8 sprints (6-8 months)
**Dependencies**: v1.4.0 accessibility foundation

---

#### v2.1.0+ - Community & Ecosystem (2027+)
**Status**: 🌟 ASPIRATIONAL
**Potential Directions:**
- **Plugin System**: Extensible architecture for custom renderers, parsers, themes
- **Theme Marketplace**: Community-contributed themes with quality standards
- **Presentation Sharing**: Host presentations on static sites (GitHub Pages, Netlify)
- **Collaboration**: Multi-author presentations, version control integration
- **Analytics**: Track slide views, time per slide, engagement metrics
- **Responsive Design 2.0**: Mobile-optimized layouts, touch navigation
- **Offline Mode**: Service worker for offline presentations
- **Internationalization**: Multi-language support, RTL layouts

**Note**: These are aspirational goals based on community needs and feedback

---

## Strategic Focus Areas

### 1. Core Quality (Ongoing)
- **Goal**: Maintain 100% test coverage, zero technical debt
- **Activities**:
  - Comprehensive test suite for every feature
  - Property-based testing for domain invariants
  - Regular refactoring to keep codebase clean
  - Documentation updates with every release
- **Metrics**:
  - Test count: 322+ (growing with each release)
  - Code coverage: Domain + Infrastructure layers
  - Documentation: Event Storming, Three Amigos, Example Mapping for all features

### 2. User Experience (v1.4.0 - v2.0.0)
- **Goal**: Make MDSlides intuitive, fast, and delightful to use
- **Activities**:
  - Improved error messages with actionable guidance
  - Performance optimization (build times, output size)
  - Preview mode for iterative development
  - Better CLI output with progress indicators
- **Metrics**:
  - Build time <1s for typical presentations
  - Output size <100KB for 20-slide deck
  - Error message clarity (user feedback)

### 3. Accessibility (v1.4.0+)
- **Goal**: Ensure all presentations are accessible to everyone
- **Activities**:
  - WCAG 2.1 AA compliance validation
  - Keyboard navigation improvements
  - Screen reader compatibility testing
  - High-contrast theme variants
- **Metrics**:
  - 100% of built-in themes pass WCAG 2.1 AA
  - Keyboard navigation covers 100% of features
  - Accessibility warnings for common issues

### 4. Extensibility (v2.0.0+)
- **Goal**: Enable community contributions and customization
- **Activities**:
  - Plugin API design and implementation
  - Theme contribution guidelines
  - Template extension points
  - Custom renderer support
- **Metrics**:
  - 10+ community themes in marketplace
  - 5+ community plugins
  - Clear plugin development documentation

---

## Migration Path

### From v1.x to v2.0
- **Backward Compatibility**: All v1.x Markdown files work in v2.0
- **Theme Migration**: Automated theme converter for new template support
- **Configuration**: New config format, but v1.x CLI args still work
- **Breaking Changes**: Documented in CHANGELOG with migration guide

### Deprecation Policy
- **Features**: 2 major versions deprecation notice before removal
- **CLI Arguments**: 1 major version deprecation notice
- **Theme Format**: Support old format for 1 major version alongside new format
- **Communication**: Deprecation warnings in CLI output, CHANGELOG, documentation

---

## Success Metrics

### Product Metrics (Current)
- ✅ **Test Coverage**: 322 tests (158 domain, 164 infrastructure)
- ✅ **Built-in Themes**: 4 (light, dark, corporate, retisio)
- ✅ **Supported Markdown**: Bold, italic, code, links, images, lists (3-level nesting)
- ✅ **Code Highlighting**: 190+ languages via highlight.js
- ✅ **Templates**: 2 (title, content)

### Growth Targets (v2.0)
- **Test Coverage**: 500+ tests
- **Built-in Themes**: 6-8 themes
- **Community Themes**: 10+ contributed themes
- **Templates**: 5+ templates
- **Supported Features**: Diagrams, custom templates, plugin system
- **Documentation**: Comprehensive user guide, API docs, video tutorials

### Quality Targets (Ongoing)
- **Build Performance**: <1s for 50-slide presentation
- **Output Size**: <100KB base size (excluding images)
- **Accessibility**: 100% WCAG 2.1 AA compliance for built-in themes
- **Test Execution**: <30s for full test suite
- **Zero Bugs**: No known bugs in production releases

---

## Decision Framework

### Feature Prioritization (MoSCoW)
- **MUST HAVE**: Critical for release, blocks other features, high user value
- **SHOULD HAVE**: Important, high value, defer only if resource-constrained
- **COULD HAVE**: Nice-to-have, include if capacity allows
- **WON'T HAVE**: Out of scope for current release, document for future

### Release Criteria
1. **All tests passing** (322+ tests)
2. **Zero known bugs** in MUST/SHOULD features
3. **Documentation complete**: Event Storming, Three Amigos, Example Mapping
4. **CHANGELOG updated** with release notes
5. **Tutorial updated** demonstrating new features
6. **Backward compatibility** verified with test suite

### Go/No-Go Decision Points
- **Before implementation**: Event Storming, Three Amigos, Example Mapping complete
- **Before merge**: All tests passing, code review complete
- **Before release**: Release criteria met, manual testing complete
- **Before tagging**: Git history clean, documentation updated

---

## Risk Management

### Technical Risks
1. **Scalability**: Large presentations (100+ slides) may have performance issues
   - **Mitigation**: Performance testing, lazy rendering, optimization
2. **Browser Compatibility**: Advanced CSS/JS features may not work in older browsers
   - **Mitigation**: Feature detection, graceful degradation, browser testing
3. **CDN Availability**: highlight.js, Google Fonts CDN failures
   - **Mitigation**: Graceful degradation, fallback to local resources in v2.0

### Process Risks
1. **Scope Creep**: Feature requests may derail roadmap
   - **Mitigation**: Strict MoSCoW prioritization, defer to next release
2. **Technical Debt**: Shortcuts to meet deadlines
   - **Mitigation**: Zero technical debt policy, fix issues immediately
3. **Testing Overhead**: 100% coverage slows development
   - **Mitigation**: TDD from start, parallel test execution, property-based testing

### Market Risks
1. **Competitor Features**: Other tools may have features we lack
   - **Mitigation**: Focus on DDD quality, extensibility, not feature parity
2. **User Adoption**: Limited user base for niche tool
   - **Mitigation**: Excellent documentation, tutorial, community engagement

---

## Communication Plan

### Stakeholder Updates
- **Monthly**: Roadmap review, adjust priorities based on feedback
- **Per Sprint**: Release notes, feature demos, test coverage reports
- **Per Release**: CHANGELOG, migration guides, announcement

### Community Engagement
- **GitHub Discussions**: Feature requests, Q&A, roadmap feedback
- **GitHub Issues**: Bug reports, feature tracking
- **Documentation**: Comprehensive guides, video tutorials, API docs
- **Blog Posts**: Release announcements, deep dives on architecture

---

## Appendix: Release History

| Version | Release Date | Key Features | Test Count | Status |
|---------|-------------|--------------|------------|--------|
| v0.1.0 | 2024-11 | MVP: Basic rendering, templates, validation | 121 | ✅ Released |
| v0.2.0 | 2024-12 | Markdown formatting, code blocks, themes | 171 | ✅ Released |
| v0.3.0 | 2024-12 | Image support, asset copying | 192 | ✅ Released |
| v0.4.0 | 2024-12 | Directory themes, background images, lists | 235 | ✅ Released |
| v1.0.0 | 2024-12 | Speaker notes rendering | 268 | ✅ Released |
| v1.1.0 | 2024-12 | Speaker view window, sync, timer | 290 | ✅ Released |
| v1.2.0 | 2024-12 | Nested lists (3 levels), visual hierarchy | 290 | ✅ Released |
| v1.3.0 | 2025-12 | Syntax highlighting (190+ languages) | 322 | ✅ Released (bugs) |
| v1.3.1 | 2025-12 | **HOTFIX**: List order, 'S' key handler | 324 | 🔴 In Progress |
| v1.4.0 | 2026-Q1 | Accessibility validation (WCAG 2.1 AA) | TBD | 📋 Planned |
| v1.5.0 | 2026-Q2 | Google Fonts support | TBD | 📋 Planned |
| v1.6.0 | 2026-Q2 | Quality of life improvements | TBD | 💡 Ideation |
| v2.0.0 | 2026-Q4 | Diagrams, templates, config, theme generator | TBD | 🔮 Future |

---

**Next Review**: 2025-12-28 (daily until v1.3.1 released)
**Next Release**: v1.3.1 (2025-12-30 - Critical Hotfix)
**Next Major Release**: v1.4.0 (Q1 2026)
**Long-Term Goal**: v2.0.0 with plugin system, marketplace, community ecosystem
