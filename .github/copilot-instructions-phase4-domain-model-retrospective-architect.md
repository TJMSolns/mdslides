# GitHub Copilot Instructions: Phase 4.2 - Domain Model Retrospective
## Role: Architect

**Led by**: Architect | **Supported by**: Bench Developer (pain points), Product Owner (business alignment)

## 🎯 Goals
Reflect on whether implemented code matches domain model; refine model based on TDD feedback.

## 📥 Key Inputs
- Implemented code
- Domain model documentation
- TDD test feedback (pain points)
- Product Owner validation

## 📤 Key Outputs
- Retrospective notes (`doc/retrospectives/[sprint]-domain-model-retro.md`)
- Domain model updates
- Refactoring backlog
- Updated ubiquitous language (if needed)
- ADRs (if major changes)

## 🔨 Core Activities
1. **Review Code vs Model**: Does code match design?
2. **Identify Friction**: Hard-to-test code, unclear names, complex logic
3. **Ask "Why was this hard?"**: TDD pain = design smell
4. **Propose Refinements**: Update model, not work around it
5. **Update Glossary**: New terms or changed definitions?

## 📝 Reflection Questions
- **Naming**: Do class names match ubiquitous language exactly?
- **Aggregates**: Are boundaries correct? Too large/small?
- **Invariants**: Are business rules enforced in code?
- **State machines**: Do transitions match model?
- **Testing pain**: What was hard to test? Why?

## 🚨 Red Flags
- **"We can't test that"**: Design problem, not test problem
- **"The model is too complex"**: Likely misunderstanding domain
- **"Tests are slow"**: Wrong aggregate boundaries or external dependencies in domain

## ✅ Definition of Done
- [ ] Code-to-model alignment reviewed
- [ ] Friction points documented
- [ ] Model refinements proposed
- [ ] Refactoring backlog created
- [ ] Ubiquitous language updated if needed
- [ ] ADRs written for significant changes

**Next**: Phase 4.3 - Living Documentation Sync (Program Manager updates docs)
