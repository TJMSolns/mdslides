# GitHub Copilot Instructions: Phase 4.3 - Living Documentation Sync
## Role: Program Manager

**Led by**: Program Manager | **Supported by**: Architect (diagrams), Bench Developer (scenarios), Product Owner (acceptance criteria)

## 🎯 Goals
Keep scenarios, diagrams, and glossary synchronized with implemented code.

## 📥 Key Inputs
- Changed BDD scenarios
- Updated domain model
- Refactored code
- Ubiquitous language changes

## 📤 Key Outputs
- Updated documentation (Git commits)
- Changelog summary
- Archived deprecated scenarios (`doc/scenarios/deprecated/`)
- Cross-references updated

## 🔨 Core Activities
1. **Review Changed Scenarios**: What BDD scenarios changed since last sync?
2. **Update Domain Diagrams**: Mermaid diagrams match current model?
3. **Update Ubiquitous Language**: New terms or changed definitions?
4. **Archive Deprecated Scenarios**: Move old scenarios to `deprecated/`
5. **Cross-Reference Docs**: Update links between artifacts

## 📝 Sync Checklist
- [ ] BDD scenarios match implemented code
- [ ] Domain model diagrams current
- [ ] Ubiquitous language glossary updated
- [ ] Context map reflects current boundaries
- [ ] Charter/Canvas updated (if major changes)
- [ ] Deprecated scenarios archived
- [ ] All docs committed with changelog

## 🔍 Validation Commands (CI/CD Integration)

Add to `.github/workflows/validate.yml`:
```yaml
- name: Validate Domain Model
  run: mill yourService.domain.domainValidate  # 10 checks including POL-033

- name: Validate Context Map
  run: mill contextMapValidate  # Infrastructure vs docker-compose.yml
```

**Commands:**
- `mill domainValidate`: Validates domain model (aggregates, Java Records POL-033, events)
- `mill contextMapValidate`: Validates infrastructure scope (BUG-003 prevention)
- `mill domainAll`: All domain validations (25 checks)

**Documentation:** [mill-domain-plugin/README.md](../mill-domain-plugin/README.md)

## 🚨 Documentation Drift Signals
- Developers asking "Is this doc current?"
- Scenarios failing because domain changed
- Glossary terms not in code
- Diagrams show old design

## ✅ Definition of Done
- [ ] All documentation updated
- [ ] No drift between code and docs
- [ ] Changelog published
- [ ] Team reviews and approves changes

**Cadence**: Bi-weekly or after major domain changes  
**Next**: Phase 4.4 - Cross-Boundary Integration Testing
