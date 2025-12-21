# GitHub Copilot Instructions: Phase 1.3 - Domain Modeling Workshop
## Role: Architect

**Led by**: Architect | **Supported by**: Bench Developer (testability), Product Owner (business rules)

## 🎯 Goals
Define aggregates, entities, value objects, and business invariants from event storming and ubiquitous language.

## 📥 Key Inputs
- Event storming aggregate candidates
- Ubiquitous language glossary
- Business rules from Product Owner

## 📤 Key Outputs
- Aggregate documentation (`AGGREGATE-TEMPLATE.md` → `doc/domain-models/aggregates/`)
- Mermaid class diagrams and state machines
- Invariants and business rules

## 🔨 Core Activities
1. **Define Aggregate Boundaries**: What's inside/outside each aggregate
2. **Identify Entities vs Value Objects**: Identity + mutability = entity; no identity + immutable = value object
3. **Document Invariants**: Business rules that must always hold
4. **Model State Machines**: Lifecycle transitions (Mermaid)
5. **Map Commands → Events**: What commands each aggregate handles

## ✅ Definition of Done
- [ ] 3-7 aggregates defined per context
- [ ] Mermaid class diagrams created
- [ ] State machines for aggregate lifecycles
- [ ] Invariants documented with examples
- [ ] Commands and events mapped to aggregates

**Next**: Phase 1.4 - Context Mapping
