# Phase 0: Design - Complete

**Date**: December 16, 2025  
**Plugin**: mill-bootstrap-plugin  
**Status**: ✅ Complete

---

## Deliverables

| Document | Lines | Status | Purpose |
|----------|-------|--------|---------|
| DESIGN.md | 850+ | ✅ Complete | Complete plugin architecture, components, templates |
| VALIDATION-CRITERIA.md | 500+ | ✅ Complete | 7 validation checks with implementation details |
| WORKFLOW.md | 450+ | ✅ Complete | 3 commands with process flows and examples |

**Total**: ~1,800 lines of design documentation

---

## Key Decisions

### Design Philosophy
✅ **Complete Framework Copy**: Not cherry-picking - copy entire ceremony infrastructure  
✅ **Project-Ready Stubs**: Generate placeholder files for new project  
✅ **Self-Sufficient Repository**: Includes plugin sources (no copilot-training dependency after bootstrap)  
✅ **GitHub-First**: Atomic repository creation with configuration  

### Architecture
✅ **8 Core Components**:
1. BootstrapModule (main trait with 3 commands)
2. BootstrapValidator (7 prerequisite checks)
3. FrameworkCopier (recursive file copy with filtering)
4. ProjectStubGenerator (generate CHARTER.md, README.md, etc.)
5. GitHubClient (reused from mill-spinoff-plugin)
6. CharterTemplate (project charter stub)
7. ArchitectureTemplate (system architecture stub)
8. ReadmeTemplate (project README)
9. RootBuildScTemplate (root build.sc)
10. GitignoreTemplate (.gitignore patterns)

### Validation Strategy
✅ **7 Checks, All Blocking**:
1. GitHub token valid (repo + admin:org scopes)
2. Target organization exists
3. Repository name available
4. Project name follows conventions (kebab-case)
5. Git configured locally
6. Mill 0.11.6+ installed
7. Source repo path valid

No environment variance (unlike mill-deploy-plugin) - all checks mandatory for project setup.

### Workflow
✅ **9-Step Bootstrap Process**:
1. Validate prerequisites (all 7 checks)
2. Create GitHub repository
3. Clone empty repository locally
4. Copy framework files (SBPFs, templates, copilot instructions, plugins)
5. Generate project stubs (CHARTER.md, README.md, etc.)
6. Update file references (copilot-training → project name)
7. Create initial commit
8. Push to GitHub
9. Configure repository (branch protection, topics, disable wiki)

### Success Criteria
✅ Bootstrap time: <60 seconds  
✅ Framework completeness: 100% (all files copied)  
✅ Repository readiness: 100% (can start Phase 0 immediately)  
✅ GitHub integration: 100% (repository created, configured, pushed)  
✅ Self-sufficiency: 100% (no copilot-training dependency)  

---

## Commands Designed

### Command 1: bootstrapValidate
```bash
mill bootstrap.validate ecommerce-platform
```
- Run 7 prerequisite checks
- Display results (pass/fail with details)
- Exit code 0 (pass) or 1 (fail)

### Command 2: bootstrapExecute
```bash
mill bootstrap.execute ecommerce-platform "Multi-tenant ecommerce platform"
```
- Run validation (must pass)
- Create GitHub repository
- Copy framework + generate stubs
- Push and configure

### Command 3: bootstrapList
```bash
mill bootstrap.list
```
- Show recently bootstrapped projects
- Filter by "ceremony-based" topic

---

## Files to Copy

### Framework Files (~110 files)
- HOW-WE-WORK.md (1 file)
- .github/copilot-instructions*.md (14 files)
- doc/reference/SBPF/ (22 guides)
- doc/reference/templates/ (34 templates)
- doc/reference/validation/ (5 checklists)
- mill-spinoff-plugin/ (15 source files)
- mill-deploy-plugin/ (10 source files)
- doc/governance/ADR/ (5 framework ADRs)
- doc/governance/POL/ (3 framework POLs)

### Generated Stubs (7 files)
- CHARTER.md (project charter)
- ARCHITECTURE.md (system architecture)
- README.md (project overview)
- build.sc (root build configuration)
- .gitignore (Mill/JVM patterns)
- GOVERNANCE-BACKLOG.md (tracking)
- STATUS.md (Phase 0 status)

**Total**: ~117 files

---

## Open Questions Status

All questions resolved:

| Question | Decision | Date |
|----------|----------|------|
| Plugin versioning | Copy plugin sources (self-sufficient) | 2025-12-16 |
| Framework updates | Manual for v1.0.0 | 2025-12-16 |
| Organization customization | Generic templates (no branding) | 2025-12-16 |

---

## Risk Assessment

| Risk | Severity | Mitigation | Status |
|------|----------|------------|--------|
| GitHub API rate limits | Medium | Cache token, conditional requests | 🟢 Documented |
| Large file copy (slow) | Low | Progress indicators | 🟢 Designed |
| Template generation errors | High | Comprehensive unit tests | 🟢 Planned |
| Broken references in copied files | High | Post-copy validation, replace "copilot-training" | 🟢 In workflow |
| Missing dependencies | High | Copy plugin sources, not references | 🟢 Designed |

---

## Next: Phase 1 (Documentation)

Create governance documents and user documentation:

### Governance Documents
1. **ADR-064**: Bootstrap via Mill Plugin
   - Decision: Mill plugin for project bootstrapping
   - Comparison: Manual setup vs automated bootstrap
   - Consequences: Rapid project creation with framework

2. **POL-031**: Mill Bootstrap Plugin Usage Policy
   - Who can create new projects (Program Managers + Architects)
   - Naming conventions enforcement
   - Post-bootstrap responsibilities
   - Framework update policy

### User Documentation
3. **mill-bootstrap-plugin/README.md**
   - Installation instructions
   - Usage examples (all 3 commands)
   - Configuration options
   - Troubleshooting guide
   - Architecture overview

### Process Integration
4. **Update HOW-WE-WORK.md**
   - Add Phase 0 bootstrap step
   - Link to mill-bootstrap-plugin

5. **Update GOVERNANCE-BACKLOG.md**
   - Add ADR-064, POL-031

---

## Metrics

| Metric | Value |
|--------|-------|
| Design documents | 3 |
| Total design lines | ~1,800 |
| Components designed | 10 |
| Validation checks | 7 |
| Commands | 3 |
| Files to copy | ~110 |
| Files to generate | 7 |
| Workflow steps | 9 |
| Time spent | ~2 hours |

---

## Conclusion

Phase 0 (Design) is complete with comprehensive documentation covering:
- ✅ Complete plugin architecture
- ✅ All validation checks specified
- ✅ Detailed workflow for all 3 commands
- ✅ Template designs for all generated files
- ✅ Success criteria and risk mitigation
- ✅ All open questions resolved

Ready to proceed to Phase 1 (Documentation): Governance docs, README, and process integration.
