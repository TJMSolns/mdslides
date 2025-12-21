# Software Best Practice Framework (SBPF)

## Purpose
This directory contains **reusable best practice guides** independent of any specific project. These documents serve as organizational knowledge and methodology references.

## Contents
The SBPF includes comprehensive guides covering:

### Integration Methodologies
- **Blending-DDD-BDD-TDD.md**: Master guide for ceremony-based integration (THIS DOCUMENT IS KEY)
- **DDD-TDD-BDD-Integration.md**: Practical integration patterns

### Domain-Driven Design (DDD)
- **DDD-Best-Practices.md**: Core DDD principles
- **Domain-Modeling-Techniques.md**: Event storming, context mapping
- **Ubiquitous-Language.md**: Building shared vocabulary

### Behavior-Driven Development (BDD)
- **BDD-Best-Practices.md**: Three Amigos, Example Mapping
- **Gherkin-Writing-Guide.md**: Feature file standards
- **Living-Documentation.md**: Keeping specs aligned with code

### Test-Driven Development (TDD)
- **TDD-Best-Practices.md**: Red-Green-Refactor cycle
- **Unit-Testing-Patterns.md**: Test organization and structure
- **Property-Based-Testing.md**: Generative testing approaches

### Architectural Patterns
- **Microservices-Best-Practices.md**: Service boundaries and communication
- **Event-Driven-Architecture.md**: Event sourcing, CQRS
- **Reactive-Programming.md**: Backpressure, stream processing
- **Actor-Model.md**: Concurrency patterns

### Additional Topics
- **API-Design-Best-Practices.md**: RESTful conventions
- **Database-Design-Patterns.md**: Schema design, normalization
- **Security-Best-Practices.md**: Authentication, authorization
- **DevOps-Practices.md**: CI/CD, infrastructure as code
- **Code-Review-Guidelines.md**: Effective peer reviews

## Usage

### For New Team Members
Start with **Blending-DDD-BDD-TDD.md** to understand how methodologies integrate through ceremonies.

### For Practitioners
Reference methodology-specific guides (DDD, BDD, TDD) during:
- Discovery ceremonies (Event Storming, Context Mapping)
- Specification ceremonies (Three Amigos, Example Mapping)
- Implementation ceremonies (TDD Red-Green-Refactor)

### For Architects
Use architectural pattern guides when making technology and design decisions. Link to these documents in ADRs (Architectural Decision Records).

### For Teams
Fork and adapt these best practices to create organizational standards in `doc/reference/standards/`.

## Relationship to Project Artifacts

```
SBPF (Reusable Patterns) ← YOU ARE HERE
    ↓
Exhibits (Concrete Examples)
    ↓
Services (Living Implementation)
```

- **SBPF**: "Here's how to do DDD/BDD/TDD"
- **Exhibits** (`doc/exhibits/`): "Here's an example of DDD/BDD/TDD in action (12 service charters)"
- **Services** (`doc/services/`): "Here's our actual DDD/BDD/TDD artifacts for this service"

## Contributing
To add or update best practices:
1. Create PR with proposed changes
2. Discuss in architecture review or team meeting
3. Document rationale for changes in PR description
4. After merge, notify teams of updates

## Related Documentation
- **Concrete examples**: `doc/exhibits/` (service charters showing best practices applied)
- **Templates**: `doc/reference/templates/` (scaffolding for applying best practices)
- **Standards**: `doc/reference/standards/` (organization-specific adaptations)
