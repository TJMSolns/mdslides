# Mill Spinoff Plugin

Mill plugin for spinning off bounded context services from a master training repository to production repositories.

## Overview

The **Mill Spinoff Plugin** automates the process of extracting a bounded context from a training/monorepo and creating a production-ready repository with:

- ✅ **Automated Validation**: 14 pre-flight checks ensure service readiness
- ✅ **Code Extraction**: Extracts domain, application, and infrastructure layers
- ✅ **Framework Files**: Copies 110+ ceremony instructions, templates, and SBPFs for self-sufficiency
- ✅ **Repository Generation**: Creates complete repository structure (build.sc, CI/CD, Dockerfile, k8s/)
- ✅ **GitHub Integration**: Creates repository, sets permissions, pushes initial code
- ✅ **Living Documentation**: Generates spinoff ADR and updates tracking docs

### 🧭 Scala 3 Migration Notes

- **Scala 3.3.1** baseline; enums replace sealed traits for validator checks.
- **Per-check `T.task`** execution for parallel validation.
- **FrameworkCopier** now copies governance + Marp `doc/presentations/` for self-sufficient spinoffs.
- **Dependencies**: ScalaTest 3.2.18; ScalaMock removed (no Scala 3 artifact).

### Framework Self-Sufficiency

Spun-off repositories are **fully autonomous** with complete framework files:

#### Ceremony Instructions (14 files → `.github/`)
All step-by-step guides for the ceremony-based SDLC:
- Phase 0: Program Initiation
- Phase 1: Event Storming, Ubiquitous Language, Domain Modeling, Context Mapping
- Phase 2: Three Amigos, Example Mapping, Acceptance Criteria Review
- Phase 3: Test-First Pairing, Red-Green-Refactor, Property-Based Testing
- Phase 4: Scenario-to-Test Decomposition, Domain Model Retrospective, Living Documentation Sync

#### SDLC Playbook
- `HOW-WE-WORK.md` - Complete 14-ceremony workflow documentation

#### Shared Best Practice Files (22 files → `doc/reference/SBPF/`)
Reusable patterns, configs, and implementation guides

#### Document Templates (34 files → `doc/reference/templates/`)
- `AGGREGATE-TEMPLATE.md` - Domain aggregate structure
- `EVENT-STORMING-TEMPLATE.md` - Event storming session format
- `FEATURE-TEMPLATE.feature` - BDD scenario template
- `ADR-TEMPLATE.md` - Architecture decision records
- Plus 30+ more templates for all artifacts

#### Validation Checklists (5 files → `doc/reference/validation/`)
Quality gate checklists for each ceremony phase

#### Framework Governance (8 files → `doc/governance/`)
- 5 Framework ADRs (Pekko, non-blocking I/O, reactive Postgres, Mill, spinoff)
- 3 Framework POLs (non-blocking mandate, documentation-as-code, ubiquitous language)

**Result**: Teams can execute all ceremonies without referencing the training repository

## Installation

### 1. Configure GitHub Packages Access

Create `~/.mill/ammonite/mill-repos.sc` to add GitHub Packages as a resolver:

```scala
import coursier.maven.MavenRepository

interp.repositories() = interp.repositories() :+ MavenRepository(
  "https://maven.pkg.github.com/RETISIO/*",
  authentication = Some(coursier.core.Authentication(
    sys.env.getOrElse("GITHUB_USERNAME", ""),
    sys.env.getOrElse("GITHUB_TOKEN", "")
  ))
)
```

### 2. Set Environment Variables

```bash
export GITHUB_USERNAME=your-github-username
export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx  # Same token used for spinoff
```

**Note**: Add these to `~/.bashrc` or `~/.zshrc` for persistence.

### 3. Import Plugin in build.sc

```scala
import $ivy.`com.retisio::mill-spinoff:1.0.0`
import com.retisio.mill.SpinoffModule

object billingService extends JavaModule with SpinoffModule {
  def spinoffCandidatesPath = T { millSourcePath / "doc" / "SPINOFF-CANDIDATES.md" }
  def contextMapPath = T { millSourcePath / "doc" / "domain-models" / "CONTEXT-MAP.md" }
  def targetOrg = T { "RETISIO" }
}
```

## Commands

### `spinoffList`

List all spinoff candidates from `SPINOFF-CANDIDATES.md`:

```bash
mill billingService.spinoffList
```

**Output**:
```
Found 3 spinoff candidates:

Service                        Status          Aggregates Readiness  
---------------------------------------------------------------------------
Invoice                        🟡 Monitoring   4          95%        
Payment                        🟢 Not Ready    2          60%        
Refund                         🔴 Triggered    3          100%       
```

### `spinoffValidate`

Validate readiness for spinning off a sub-service:

```bash
mill billingService.spinoffValidate Invoice
```

**Pre-flight Checks**:
1. ✅ **Charter exists** - Problem statement defined
2. ✅ **Domain model complete** - Aggregates, entities, value objects documented
3. ✅ **Context map defined** - Upstream/downstream relationships specified
4. ✅ **BDD scenarios exist** - Gherkin features in `features/`
5. ✅ **Unit tests exist** - >80% code coverage
6. ✅ **Integration contracts defined** - ACL/Conformist patterns
7. ✅ **API specification complete** - OpenAPI 3.1 spec
8. ✅ **Event schemas defined** - Kafka event schemas (Avro/JSON)
9. ✅ **Database migrations ready** - Flyway migrations
10. ✅ **Observability configured** - OpenTelemetry traces/metrics/logs
11. ✅ **Security controls defined** - AuthN/AuthZ model
12. ✅ **Deployment manifests ready** - Dockerfile + k8s/
13. ✅ **Team ownership assigned** - CODEOWNERS file
14. ✅ **Framework files present** - Ceremony instructions, templates, SBPFs (post-spinoff only)

**Output**:
```
Validation Results for Invoice:
================================================================================

✅ Charter exists                                          PASS
✅ Domain model complete                                   PASS
✅ Context map defined                                     PASS
✅ BDD scenarios exist                                     PASS
✅ Unit tests exist (≥3 test classes)                     PASS
✅ Integration contracts defined                           PASS
✅ API specification complete (OpenAPI)                    PASS
✅ Event schemas defined                                   PASS
✅ Database migrations ready                               PASS
✅ Observability configured (OTel)                         PASS
✅ Security controls defined                               PASS
✅ Deployment manifests ready                              PASS
✅ Team ownership assigned (CODEOWNERS)                    PASS

Total: 13/13 checks passed

✅ Invoice is READY for spinoff

Next steps:
  1. Review validation results
  2. Execute spinoff: mill billingService.spinoffExecute Invoice
```

### `spinoffExecute`

Execute spinoff of sub-service to new GitHub repository:

```bash
# Set GitHub token (requires 'repo' and 'admin:org' scopes)
export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx

# Execute spinoff
mill billingService.spinoffExecute Invoice
```

**Process** (8 steps):
1. ✅ Validate spinoff readiness (13/13 checks must pass)
2. ✅ Create GitHub repository (`RETISIO/invoice-service`)
3. ✅ Extract bounded context code (domain, application, infrastructure)
4. ✅ Generate repository structure (build.sc, CI/CD, Dockerfile, k8s/)
5. ✅ Create spinoff ADR (`ADR-001-spinoff-from-training-repository.md`)
6. ✅ Push initial code to GitHub
7. ✅ Configure branch protection and team permissions
8. ✅ Update `SPINOFF-CANDIDATES.md` (mark as ✅ Complete)

**⚠️ IMPORTANT: Post-Spinoff Cleanup Required**

After successful spinoff, the source directory **MUST** be deleted from the training repository to avoid dual source of truth:

```bash
# Verify production repo exists
curl -H "Authorization: token $GITHUB_TOKEN" \
  https://api.github.com/repos/RETISIO/invoice-service

# Delete source from training repo
rm -rf services/billing-service/bounded-contexts/invoice/

# Commit cleanup
git add -A
git commit -m "Remove Invoice after spinoff to RETISIO/invoice-service"
git push origin main
```

**Rationale**: The training repository should contain charters, ceremonies, templates, and tooling ONLY—not service implementations. Production code lives in dedicated service repositories.

**Future Enhancement**: `--cleanup` flag to automate source deletion:
```bash
mill billingService.spinoffExecute Invoice --cleanup
# Automatically deletes source after successful spinoff
```

**Output**:
```
Executing spinoff for: Invoice
================================================================================

Step 1/8: Validating spinoff readiness...
✅ Validation passed (13/13 checks)

Step 2/8: Creating GitHub repository...
✅ Created repository: https://github.com/RETISIO/invoice-service

Step 3/8: Extracting bounded context code...
✅ Extracted code to: /tmp/spinoff-invoice-xyz

Step 4/8: Generating service repository structure...
  📋 Copying framework files...
    ✅ Copied 112 framework files
  🏗️  Generating service scaffolds...
    ✅ Generated service scaffolds
✅ Generated repository structure

Step 5/8: Creating spinoff ADR...
✅ Created spinoff ADR: /tmp/spinoff-invoice-xyz/doc/governance/ADR/ADR-001-spinoff-from-training-repository.md

Step 6/8: Pushing initial code to GitHub...
✅ Pushed initial code to https://github.com/RETISIO/invoice-service

Step 7/8: Configuring repository settings...
✅ Configured branch protection and permissions

Step 8/8: Updating SPINOFF-CANDIDATES.md...
✅ Updated SPINOFF-CANDIDATES.md (marked Invoice as Complete)

================================================================================
✅ Spinoff COMPLETE for Invoice

Repository: https://github.com/RETISIO/invoice-service

Next steps:
  1. Review repository: https://github.com/RETISIO/invoice-service
  2. Configure CI/CD secrets (GitHub Actions)
  3. Deploy to staging environment
  4. Update cross-boundary integration tests
  5. Document spinoff in Ceremony 4.3 (Living Documentation Sync)
```

## Configuration

### SPINOFF-CANDIDATES.md

Document services ready for spinoff:

```markdown
# Spinoff Candidates

## Current Candidates

### 1. Invoice Service

- **Status**: 🔴 Triggered (Ready for spinoff)
- **Trigger Conditions**: All 13 validation checks passed, BDD scenarios complete
- **Aggregates**: 4 (Invoice, InvoiceLineItem, InvoiceTax, InvoicePayment)
- **Readiness**: 100%
- **Domain Events**: InvoiceCreated, InvoiceApproved, InvoiceVoided, InvoicePaid
- **Team**: @RETISIO/billing-team
- **Repository**: (To be created)
- **Spun Off**: (Pending)
```

### CONTEXT-MAP.md

Define bounded context relationships:

```markdown
# Context Map

## Billing Context

### Upstream Contexts

- ⬆️ **Tenant Management** (ACL) - Provides tenant validation
- ⬆️ **Authentication** (Conformist) - Provides JWT tokens

### Downstream Contexts

- ⬇️ **Reporting** (OHS) - Consumes InvoiceCreated events
- ⬇️ **Analytics** (PL) - Consumes financial metrics

### Shared Kernels

- 🔗 **Domain Primitives** (Money, Currency, TenantId)
```

## Requirements

- **JDK**: 21+ (Temurin, Corretto, or Zulu)
- **Mill**: 0.11.6+
- **Scala**: 2.13.12 (for plugin development)
- **GitHub Token**: Personal access token with `repo` and `admin:org` scopes

### GitHub Token Scopes

Required scopes for `GITHUB_TOKEN`:

- ✅ `repo` - Full control of private repositories
- ✅ `admin:org` - Full control of orgs and teams (for repository creation)
- ✅ `workflow` - Update GitHub Actions workflows

**Create token**: https://github.com/settings/tokens/new

```bash
# Set token in environment
export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx

# Or add to ~/.bashrc / ~/.zshrc
echo 'export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx' >> ~/.bashrc
```

## Architecture

### Plugin Components

```
mill-spinoff-plugin/
├── src/com/retisio/mill/
│   ├── SpinoffModule.scala           # Main trait (spinoffValidate, spinoffExecute, spinoffList)
│   ├── SpinoffValidator.scala        # 13 pre-flight checks
│   ├── GitHubClient.scala            # GitHub API wrapper
│   ├── SpinoffCandidatesParser.scala # Parse SPINOFF-CANDIDATES.md
│   ├── ContextMapParser.scala        # Parse CONTEXT-MAP.md
│   ├── CodeExtractor.scala           # Extract bounded context code
│   ├── RepositoryGenerator.scala     # Generate repository structure
│   ├── SpinoffCandidatesUpdater.scala # Update tracking docs
│   └── templates/
│       ├── BuildScTemplate.scala     # Generate build.sc
│       ├── CIWorkflowTemplate.scala  # Generate .github/workflows/ci.yml
│       ├── CDStagingTemplate.scala   # Generate cd-staging.yml
│       ├── CDProductionTemplate.scala # Generate cd-production.yml
│       ├── DockerfileTemplate.scala  # Generate Dockerfile
│       ├── K8sManifestsTemplate.scala # Generate k8s/ manifests
│       ├── READMETemplate.scala      # Generate README.md
│       └── SpinoffADRTemplate.scala  # Generate spinoff ADR
└── test/com/retisio/mill/
    ├── SpinoffValidatorTest.scala
    ├── GitHubClientTest.scala
    └── ...
```

### Integration Pattern

Services import the plugin and mix in `SpinoffModule`:

```scala
import $ivy.`com.retisio::mill-spinoff:1.0.0`
import com.retisio.mill.SpinoffModule

object myService extends JavaModule with SpinoffModule {
  // Override configuration paths if needed
  def spinoffCandidatesPath = T { millSourcePath / "doc" / "SPINOFF-CANDIDATES.md" }
  def contextMapPath = T { millSourcePath / "doc" / "domain-models" / "CONTEXT-MAP.md" }
  def targetOrg = T { "RETISIO" }
}
```

## Troubleshooting

### GitHub API Rate Limit

**Error**: `403 API rate limit exceeded`

**Solution**: Use authenticated token (increases limit from 60/hour to 5,000/hour):

```bash
export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx
```

### Repository Already Exists

**Error**: `Repository RETISIO/invoice-service already exists`

**Solution**: Delete existing repository or rename the service in `SPINOFF-CANDIDATES.md`.

### Validation Failures

**Error**: `❌ Validation FAILED. Invoice is not ready for spinoff.`

**Solution**: Fix failed checks and re-run `spinoffValidate`:

```bash
mill billingService.spinoffValidate Invoice
```

### Missing Context Map Entry

**Error**: `Invoice not found in context map`

**Solution**: Add entry to `CONTEXT-MAP.md`:

```markdown
### Invoice Context

- ⬆️ **Tenant Management** (ACL) - Provides tenant validation
```

## Governance

This plugin is mandated by:

- **ADR-060**: Spinoff via Mill Plugin
- **POL-029**: Mill Spinoff Plugin Usage Policy (services MUST use ≥1.0.0)

**Policy**: All RETISIO services MUST use the Mill Spinoff Plugin for spinoffs. Custom spinoff scripts are prohibited.

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md) for development setup and guidelines.

### Development Setup

1. **Clone repository**:
   ```bash
   git clone https://github.com/RETISIO/mill-spinoff-plugin.git
   cd mill-spinoff-plugin
   ```

2. **Compile plugin**:
   ```bash
   mill millSpinoffPlugin.compile
   ```

3. **Run tests**:
   ```bash
   mill millSpinoffPlugin.test
   ```

4. **Publish locally** (for testing):
   ```bash
   mill millSpinoffPlugin.publishLocal
   ```

5. **Publish to GitHub Packages**:
   ```bash
   export GITHUB_USERNAME=your-github-username
   export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx
   mill millSpinoffPlugin.publish
   ```

## License

Apache License 2.0 - See [LICENSE](./LICENSE)

## Support

- **GitHub Issues**: https://github.com/RETISIO/mill-spinoff-plugin/issues
- **Slack**: #mill-spinoff-plugin (RETISIO workspace)
- **Email**: architecture@retisio.com

## References

- [Mill Build Tool](https://mill-build.com/)
- [ADR-060: Spinoff via Mill Plugin](../doc/governance/ADR/ADR-060-spinoff-via-mill-plugin.md)
- [POL-029: Mill Spinoff Plugin Usage Policy](../doc/governance/POL/POL-029-mill-spinoff-plugin-usage.md)
- [Service Repository Spinoff Process](../doc/reference/SBPF/Service-Repository-Spinoff-Process.md)
- [Service Repository Structure Template](../doc/reference/templates/SERVICE-REPOSITORY-STRUCTURE.md)
