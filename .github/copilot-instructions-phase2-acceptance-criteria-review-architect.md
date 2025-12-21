# GitHub Copilot Instructions: Phase 2.3 - Acceptance Criteria Review
## Role: Architect

**Led by**: Architect | **Supported by**: Product Owner (business validation), Bench Developer (implementation clarity)

## 🎯 Goals
Validate BDD scenarios align with domain model and use correct ubiquitous language.

## 📥 Key Inputs
- Draft scenarios from Three Amigos
- Ubiquitous language glossary
- Domain model (aggregates, state machines)

## 📤 Key Outputs
- Approved scenarios (ready for implementation)
- Model refinements (if misalignment found)
- Review notes

## 🔨 Core Activities
1. **Check Ubiquitous Language**: Every term matches glossary exactly
2. **Validate Against Domain Model**: Can model support this behavior?
3. **Identify Model Gaps**: Missing aggregates, commands, or events?
4. **Check State Transitions**: Valid according to state machine?
5. **Approve or Request Changes**: Scenario passes → approve, issues → refine

## ✅ Review Checklist
- [ ] All terms from ubiquitous language (no synonyms)
- [ ] Scenario maps to existing aggregate(s)
- [ ] State transitions are valid
- [ ] Commands and events match model
- [ ] No technical implementation details in scenario
- [ ] Scenario testable through domain API

## 🚨 Common Issues
- **Wrong terms**: "Account" used instead of "Tenant" → reject, use glossary
- **Invalid state transition**: "TERMINATED → ACTIVE" impossible → reject or refine model
- **Missing aggregate**: Scenario needs aggregate not yet modeled → add to backlog

## ✅ Definition of Done
- [ ] All scenarios reviewed
- [ ] Approved scenarios marked ready for implementation
- [ ] Model gaps documented and scheduled
- [ ] Product Owner signs off on business accuracy

**Next**: Phase 3.1 - Test-First Pairing (Bench Developer implements)
