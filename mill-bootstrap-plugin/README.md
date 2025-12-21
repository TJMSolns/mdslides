# mill-bootstrap-plugin

**Create new projects with complete ceremony-based SDLC framework in <60 seconds**

Bootstrap new project repositories with the full **DDD + BDD + TDD ceremony framework** from RETISIO/copilot-training. Each bootstrapped project includes:

- ✅ **HOW-WE-WORK.md** (complete SDLC playbook)
- ✅ **14 Copilot Instructions** (.github/copilot-instructions*.md)
- ✅ **22 Shared Best Practice Files** (doc/reference/SBPF/)
- ✅ **34 Document Templates** (doc/reference/templates/)
- ✅ **5 Ceremony Validation Checklists** (doc/reference/validation/)
- ✅ **mill-spinoff-plugin** (service extraction automation)
- ✅ **mill-deploy-plugin** (deployment automation)
- ✅ **Project Stubs** (CHARTER.md, ARCHITECTURE.md, README.md, build.sc, .gitignore)
- ✅ **GitHub Integration** (repository creation, branch protection, topics)

**Target**: <60 seconds bootstrap time, 100% framework completeness, self-sufficient repositories

---

## Table of Contents

- [Overview](#overview)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Commands](#commands)
  - [bootstrapValidate](#bootstrapvalidate)
  - [bootstrapExecute](#bootstrapexecute)
  - [bootstrapList](#bootstraplist)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Workflow](#workflow)
- [Examples](#examples)
- [Architecture](#architecture)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [References](#references)

---

## Overview

### Purpose

**mill-bootstrap-plugin** automates the creation of new project repositories with the complete **ceremony-based SDLC framework**. It replaces manual project setup (4-8 hours) with a single command (<60 seconds).

### Distinction from Other Plugins

| Plugin | Purpose | Input | Output |
|--------|---------|-------|--------|
| **mill-bootstrap-plugin** | Create new project with framework | Project name + description | New repo with ~110 framework files |
| **mill-spinoff-plugin** | Extract existing service to production | Bounded context code | Production service repo |
| **mill-deploy-plugin** | Deploy service to Kubernetes | Service module | Kubernetes deployment |

**Example**: Use bootstrap to create `ecommerce-platform`, spinoff to extract `TenantManagement` service, deploy to push to Kubernetes.

### Key Features

- ✅ **Flexible Target Paths**: Default to services/ subdirectory or specify custom --path
- ✅ **Optional GitHub Integration**: Create local-only projects or link to GitHub repos
- ✅ **Automatic Plugin Vendoring**: Copies Mill plugins when bootstrapping outside training repo
- ✅ **Pre-Flight Validation**: Check naming, git config, Mill version before executing
- ✅ **Template-Based Stubs**: Generate CHARTER.md, ARCHITECTURE.md, README.md, build.sc, .gitignore
- ✅ **Framework Completeness**: ~110 files (HOW-WE-WORK.md, copilot instructions, templates, plugins)
- ✅ **GitHub API Integration**: Optional repository creation, branch protection, topics configuration

### 🧭 Scala 3 Migration Notes

- **Scala 3.3.1** baseline; no cross-build with 2.13.
- **Enums** replace sealed traits in validation/model types; given/using replaces implicits.
- **Parallel tasks**: validation steps exposed as `T.task` for faster execution.
- **Dependencies**: ScalaTest 3.2.18 on Scala 3; ScalaMock removed (no Scala 3 artifact).

### Bootstrap Modes

**Training Repo Mode** (default):
- Target: `services/<project-name>` subdirectory
- Plugins: Referenced from parent ../mill-*-plugin directories
- GitHub: Not required (local development/learning)
- Use case: Exploring ceremony framework, training exercises

**Standalone Mode** (with --path):
- Target: Custom directory path
- Plugins: Vendored to `mill-plugins/` directory (7 plugins copied)
- GitHub: Optional (--create-repo or --repo flags)
- Use case: Production projects, separate repositories

**Example Workflow**:
```bash
# Start in training repo (learning)
cd copilot-training
mill bootstrap.bootstrapExecute --name my-service
# → services/my-service/ (uses ../mill-*-plugin)

# Graduate to standalone repo (production)
mill bootstrap.bootstrapExecute --name order-mgmt --path ~/projects --create-repo --org RETISIO
# → ~/projects/order-mgmt/ with vendored mill-plugins/
```

---

## Installation

### Prerequisites

- **Mill 0.11.6+**: Install via Coursier (`cs install mill`)
- **Git**: Configured with `user.name` and `user.email`
- **GitHub Token**: Personal Access Token with `repo` (full) and `admin:org` scopes
- **copilot-training**: Clone of RETISIO/copilot-training (source of framework files)

### Add Plugin to build.sc

```scala
// In copilot-training/build.sc
import $ivy.`com.retisio::mill-bootstrap:1.0.0`
import com.retisio.mill.BootstrapModule

object bootstrap extends BootstrapModule {
  def targetOrg = T { "RETISIO" }  // Default organization (can override)
}
```

**Note**: Plugin is **embedded in copilot-training** (not published to Maven Central). Import via local build, not ivy resolution.

---

## Quick Start

### 1. Export GitHub Token

```bash
# Create token at https://github.com/settings/tokens/new
# Select scopes: repo (all), admin:org (read/write)

export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx
```

### 2. Validate Prerequisites

```bash
mill bootstrap.validate ecommerce-platform
```

**Output** (all checks pass):
```
✅ Bootstrap Validation: ecommerce-platform

Validation Results:
✅ Check 1: GitHub token valid (scopes: repo, admin:org)
✅ Check 2: Target organization exists (RETISIO)
✅ Check 3: Repository name available (RETISIO/ecommerce-platform)
✅ Check 4: Project name follows conventions (kebab-case)
✅ Check 5: Git configured locally (user: tmoores-retisio, email: tom@retisio.com)
✅ Check 6: Mill 0.11.6+ installed (version: 0.11.6)
✅ Check 7: Source repo valid (/home/tjm/Cloud/GitHub/copilot-training)

Result: PASS (all 7 checks passed)
Ready to bootstrap!
```

### 3. Execute Bootstrap

```bash
# Default: Create in services/ subdirectory of current repo
mill bootstrap.bootstrapExecute --name ecommerce-platform

# Or create in custom location with GitHub repo
mill bootstrap.bootstrapExecute --name ecommerce-platform --path ~/projects --create-repo --org RETISIO
```

**Output** (successful bootstrap in services/):
```
🚀 Bootstrap Execute: ecommerce-platform

[1/7] Validating prerequisites...
  ✅ All checks passed

[2/7] Creating target directory...
  ✅ Target: services/ecommerce-platform

[4/9] Copying framework files (110 files)...
  ✅ HOW-WE-WORK.md copied
  ✅ .github/copilot-instructions*.md (14 files) copied
  ✅ doc/reference/SBPF/ (22 files) copied
  ✅ doc/reference/templates/ (34 files) copied
  ✅ doc/reference/validation/ (5 files) copied
  ✅ mill-spinoff-plugin/ (15 files) copied
  ✅ mill-deploy-plugin/ (10 files) copied
  ✅ Framework files copied successfully

[5/9] Generating project stubs (7 files)...
  ✅ CHARTER.md (CharterTemplate)
  ✅ ARCHITECTURE.md (ArchitectureTemplate)
  ✅ README.md (ReadmeTemplate)
  ✅ build.sc (RootBuildScTemplate)
  ✅ .gitignore (GitignoreTemplate)
  ✅ GOVERNANCE-BACKLOG.md (empty framework)
  ✅ STATUS.md (Phase 0 status)

[6/9] Updating file references (copilot-training → ecommerce-platform)...
  ✅ 23 files updated

[7/9] Creating initial commit...
  ✅ Commit: Initial project bootstrap from RETISIO/copilot-training v1.0.0

[8/9] Pushing to GitHub (main branch)...
  ✅ Pushed to https://github.com/RETISIO/ecommerce-platform

[9/9] Configuring repository settings...
  ✅ Branch protection enabled (main)
  ✅ Topics set: ddd, bdd, tdd, ceremony-based, mill-build
  ✅ Wiki disabled

✅ Bootstrap Complete!

Repository: https://github.com/RETISIO/ecommerce-platform
Clone URL: git@github.com:RETISIO/ecommerce-platform.git
Next Steps:
  1. Clone repository: git clone git@github.com:RETISIO/ecommerce-platform.git
  2. Complete CHARTER.md (Phase 0: Program Initiation)
  3. Run Phase 1: Event Storming (see .github/copilot-instructions-phase1-event-storming-architect.md)

Time: 47 seconds
```

### 4. Clone and Start Working

```bash
git clone git@github.com:RETISIO/ecommerce-platform.git
cd ecommerce-platform

# Verify build configuration
mill resolve __

# Start Phase 0: Program Initiation
# Edit CHARTER.md (see .github/copilot-instructions-phase0-program-initiation-program-manager.md)
```

---

## Commands

### bootstrapValidate

**Purpose**: Run 7 pre-flight checks without executing bootstrap.

**Usage**:
```bash
mill bootstrap.validate <project-name> [--org <organization>]
```

**Arguments**:
- `project-name`: Name of project (kebab-case, 3-50 characters)
- `--org`: Override target organization (default: `targetOrg` in build.sc)

**Exit Codes**:
- `0`: All checks passed (ready to bootstrap)
- `1`: One or more checks failed

**Example**:
```bash
# Validate with default organization
mill bootstrap.validate ecommerce-platform

# Validate with custom organization
mill bootstrap.validate ecommerce-platform --org Acme
```

**Output** (failure example):
```
❌ Bootstrap Validation: ecommerce-platform

Validation Results:
✅ Check 1: GitHub token valid (scopes: repo, admin:org)
❌ Check 2: Target organization exists (Acme)
   Error: Organization 'Acme' does not exist or is not accessible
✅ Check 3: Repository name available (Acme/ecommerce-platform)
✅ Check 4: Project name follows conventions (kebab-case)
✅ Check 5: Git configured locally (user: tmoores-retisio, email: tom@retisio.com)
✅ Check 6: Mill 0.11.6+ installed (version: 0.11.6)
✅ Check 7: Source repo valid (/home/tjm/Cloud/GitHub/copilot-training)

Result: FAIL (1 check failed)
Cannot proceed with bootstrap.
```

---

### bootstrapExecute

**Purpose**: Create and configure a new service with complete framework.

**Usage**:
```bash
mill bootstrap.bootstrapExecute --name <project-name> [flags]
```

**Required Arguments**:
- `--name <project-name>`: Name of project (kebab-case, 3-50 characters)

**Optional Arguments**:
- `--path <directory>`: Target directory (default: services/ subdirectory in current repo)
- `--create-repo`: Create new GitHub repository
- `--repo <url>`: Link to existing GitHub repository
- `--org <organization>`: GitHub organization (default: RETISIO)

**Exit Codes**:
- `0`: Bootstrap successful
- `1`: Validation failed or bootstrap error

**Examples**:
```bash
# Default: Create in services/ subdirectory (training repo pattern)
mill bootstrap.bootstrapExecute --name order-management

# Custom local path (no GitHub)
mill bootstrap.bootstrapExecute --name order-management --path ~/my-projects

# Custom path with new GitHub repo
mill bootstrap.bootstrapExecute --name order-management --path ~/projects --create-repo --org RETISIO

# Link to existing GitHub repo
mill bootstrap.bootstrapExecute --name order-management --repo https://github.com/RETISIO/order-mgmt
```

**Output**: See [Quick Start](#3-execute-bootstrap) for detailed output example.

**What Gets Created**:

1. **GitHub Repository** (if --create-repo or --repo specified):
   - Private repository in target organization
   - Initial commit with framework files
   - Optional branch protection and topics configuration

2. **Framework Files** (~110 files copied):
   - HOW-WE-WORK.md (complete SDLC playbook)
   - .github/copilot-instructions*.md (14 ceremony instruction files)
   - doc/reference/SBPF/ (22 shared best practices)
   - doc/reference/templates/ (34 document templates)
   - doc/reference/validation/ (5 ceremony checklists)
   - mill-spinoff-plugin/ (15 source files)
   - mill-deploy-plugin/ (10 source files)
   - doc/governance/ADR/ (5 framework ADRs)
   - doc/governance/POL/ (3 framework POLs)

3. **Generated Stubs** (7 files):
   - CHARTER.md (project charter with TODO sections)
   - ARCHITECTURE.md (system architecture overview)
   - README.md (project overview with quickstart)
   - build.sc (root build configuration)
   - .gitignore (Mill/JVM ignore patterns)
   - GOVERNANCE-BACKLOG.md (empty governance tracking)
   - STATUS.md (Phase 0 status dashboard)

4. **Directory Structure**:
   ```
   ecommerce-platform/
   ├── .github/
   │   └── copilot-instructions*.md (14 files)
   ├── doc/
   │   ├── domain-models/       (empty, for event storming)
   │   ├── governance/
   │   │   ├── ADR/             (5 framework ADRs)
   │   │   ├── PDR/             (empty, for process decisions)
   │   │   └── POL/             (3 framework POLs)
   │   ├── planning/            (empty, for project planning)
   │   └── reference/
   │       ├── SBPF/            (22 shared best practices)
   │       ├── templates/       (34 document templates)
   │       └── validation/      (5 ceremony checklists)
   ├── mill-plugins/            (vendored if not in training repo)
   │   ├── mill-bootstrap-plugin/
   │   ├── mill-spinoff-plugin/
   │   ├── mill-deploy-plugin/
   │   ├── mill-domain-plugin/
   │   ├── mill-specification-plugin/
   │   ├── mill-testing-plugin/
   │   └── mill-quality-plugin/
   ├── services/                (empty, for bounded contexts)
   ├── CHARTER.md               (project charter stub)
   ├── ARCHITECTURE.md          (architecture stub)
   ├── README.md                (project README)
   ├── HOW-WE-WORK.md           (SDLC playbook)
   ├── build.sc                 (root build configuration)
   ├── .gitignore               (Mill/JVM patterns)
   ├── GOVERNANCE-BACKLOG.md    (governance tracking)
   └── STATUS.md                (status dashboard)
   ```

**Time**: Target <60 seconds (typical: 40-50 seconds)

---

### bootstrapList

**Purpose**: List recently created projects (bootstrapped via plugin).

**Usage**:
```bash
mill bootstrap.list [--org <organization>] [--limit <count>]
```

**Arguments**:
- `--org`: Filter by organization (default: `targetOrg` in build.sc)
- `--limit`: Max number of projects to display (default: 10)

**Exit Codes**:
- `0`: Always (informational command)

**Example**:
```bash
# List projects in default organization
mill bootstrap.list

# List projects in custom organization
mill bootstrap.list --org Acme

# List more projects
mill bootstrap.list --limit 20
```

**Output**:
```
📋 Recently Bootstrapped Projects (RETISIO)

Repository                           Created              Description
-----------------------------------  -------------------  ---------------------------------
RETISIO/ecommerce-platform           2025-12-16 14:23:45  Multi-tenant ecommerce platform
RETISIO/api-gateway                  2025-12-15 09:15:22  API gateway for microservices
RETISIO/data-pipeline                2025-12-14 16:42:18  Real-time data processing pipeline

Total: 3 projects
Filter: topic:ceremony-based
```

**Detection**: Plugin queries GitHub API for repositories with topic `ceremony-based` and commit message containing "Initial project bootstrap".

---

## Prerequisites

Before running bootstrap, ensure the following prerequisites are met:

### 1. GitHub Token Valid

**Requirement**: GITHUB_TOKEN environment variable with scopes `repo` (full) and `admin:org` (read/write)

**Setup**:
1. Go to https://github.com/settings/tokens/new
2. Select scopes:
   - `repo` (full control of private repositories) - **ALL checkboxes**
   - `admin:org` (full control of orgs and teams) - `read:org` + `write:org`
3. Generate token
4. Export: `export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx`

**Error Example**: "GitHub token invalid or missing required scopes"  
**Resolution**: Create new token with correct scopes

---

### 2. Target Organization Exists

**Requirement**: GitHub organization exists and is accessible with token

**Error Example**: "Organization 'Acme' does not exist or is not accessible"  
**Resolution**: Create organization via GitHub UI, or verify token has access

---

### 3. Repository Name Available

**Requirement**: Repository name not already taken in organization

**Error Example**: "Repository 'RETISIO/ecommerce-platform' already exists"  
**Resolution**: Choose different name or delete existing repository

---

### 4. Project Name Conventions

**Requirement**: kebab-case, 3-50 characters, starts with letter, lowercase letters/numbers/hyphens only

**Pattern**: `^[a-z][a-z0-9]*(-[a-z0-9]+)*$`

**Valid Examples**:
- `ecommerce-platform` ✅
- `tenant-management` ✅
- `api-gateway` ✅
- `order-processing-service` ✅

**Invalid Examples**:
- `Ecommerce_Platform` ❌ (uppercase, underscore)
- `ecommerce-` ❌ (trailing hyphen)
- `123ecommerce` ❌ (starts with number)
- `ecommerce--platform` ❌ (consecutive hyphens)
- `ec` ❌ (too short, <3 characters)

**Error Example**: "Project name 'Ecommerce_Platform' violates naming conventions"  
**Resolution**: Use kebab-case format

---

### 5. Git Configured Locally

**Requirement**: `git config user.name` and `git config user.email` set

**Setup**:
```bash
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
```

**Error Example**: "Git user.name not configured"  
**Resolution**: Run `git config` commands above

---

### 6. Mill 0.11.6+ Installed

**Requirement**: Mill 0.11.6 or higher

**Setup**:
```bash
# Install via Coursier
cs install mill

# Verify version
mill --version  # Should be 0.11.6 or higher
```

**Error Example**: "Mill 0.10.5 installed, 0.11.6+ required"  
**Resolution**: Upgrade Mill (`cs install mill`)

---

### 7. Source Repository Valid

**Requirement**: copilot-training repository exists with framework files

**Setup**:
```bash
git clone https://github.com/RETISIO/copilot-training.git
cd copilot-training
```

**Error Example**: "Source repository /home/tjm/copilot-training missing HOW-WE-WORK.md"  
**Resolution**: Clone copilot-training from GitHub, or update `sourceRepoPath` in build.sc

---

## Configuration

### Default Configuration

```scala
// In copilot-training/build.sc
object bootstrap extends BootstrapModule {
  // Default organization (can override via --org flag)
  def targetOrg = T { "RETISIO" }
  
  // GitHub token from environment (GITHUB_TOKEN)
  // def githubToken = T { sys.env("GITHUB_TOKEN") }  // Default implementation
  
  // Source repository path (default: current directory)
  // def sourceRepoPath = T { os.pwd }  // Default implementation
}
```

### Override Organization (Per-Command)

```bash
# Override organization for single command
mill bootstrap.execute my-project "Description" --org Acme
```

### Override Source Path (Advanced)

```scala
// In build.sc (if copilot-training not in current directory)
object bootstrap extends BootstrapModule {
  def targetOrg = T { "RETISIO" }
  def sourceRepoPath = T { os.Path("/custom/path/to/copilot-training") }
}
```

### Environment Variables

| Variable | Required | Purpose | Example |
|----------|----------|---------|---------|
| `GITHUB_TOKEN` | Yes | GitHub API authentication | `ghp_xxxxxxxxxxxxxxxxxxxx` |

**Security**: Never commit token to git. Use `.env` file (gitignored) or CI secrets.

---

## Workflow

### Phase 0: Bootstrap (New)

**Before any ceremonies**, bootstrap the project:

1. **Validate Prerequisites**:
   ```bash
   mill bootstrap.validate ecommerce-platform
   ```

2. **Execute Bootstrap**:
   ```bash
   export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx
   mill bootstrap.execute ecommerce-platform "Multi-tenant ecommerce platform"
   ```

3. **Clone Repository**:
   ```bash
   git clone git@github.com:RETISIO/ecommerce-platform.git
   cd ecommerce-platform
   ```

4. **Verify Build**:
   ```bash
   mill resolve __  # Should succeed (no compilation errors)
   ```

### Phase 0: Program Initiation (Existing)

After bootstrap, run Program Initiation ceremony:

1. **Complete CHARTER.md** (see `.github/copilot-instructions-phase0-program-initiation-program-manager.md`)
2. **Get stakeholder approval** (Product Owner, Architect)
3. **Create GOVERNANCE-BACKLOG.md entries** (ADRs, POLs, PDRs to create)

### Phase 1: Strategic DDD

After Program Initiation, run Strategic DDD ceremonies:

1. **Event Storming** (see `.github/copilot-instructions-phase1-event-storming-architect.md`)
2. **Ubiquitous Language** (see `.github/copilot-instructions-phase1-ubiquitous-language-architect.md`)
3. **Domain Modeling** (see `.github/copilot-instructions-phase1-domain-modeling-architect.md`)
4. **Context Mapping** (see `.github/copilot-instructions-phase1-context-mapping-architect.md`)

**Continue with Phase 2 (Tactical DDD), Phase 3 (TDD), Phase 4 (Feedback & Integration)** as documented in HOW-WE-WORK.md.

---

## Examples

### Example 1: Internal RETISIO Project

**Scenario**: Create new internal project for real-time analytics.

```bash
# Validate
mill bootstrap.validate analytics-platform

# Bootstrap
export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx
mill bootstrap.execute analytics-platform "Real-time analytics platform for business intelligence"

# Clone and verify
git clone git@github.com:RETISIO/analytics-platform.git
cd analytics-platform
mill resolve __
```

**Result**: `RETISIO/analytics-platform` repository with complete framework.

---

### Example 2: Client Delivery (Custom Organization)

**Scenario**: Create project for consulting client "Acme Corp".

```bash
# Validate (custom org)
mill bootstrap.validate ecommerce-platform --org Acme

# Bootstrap (custom org)
export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx
mill bootstrap.execute ecommerce-platform "Multi-tenant ecommerce platform" --org Acme

# Clone and verify
git clone git@github.com:Acme/ecommerce-platform.git
cd ecommerce-platform
mill resolve __
```

**Result**: `Acme/ecommerce-platform` repository with complete framework.

---

### Example 3: Batch Bootstrap (Multiple Projects)

**Scenario**: Create multiple projects for new platform.

```bash
export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx

# Bootstrap API Gateway
mill bootstrap.execute api-gateway "API gateway for microservices"

# Bootstrap Order Service
mill bootstrap.execute order-service "Order processing and management"

# Bootstrap Inventory Service
mill bootstrap.execute inventory-service "Inventory tracking and management"

# List created projects
mill bootstrap.list
```

**Result**: 3 repositories created (api-gateway, order-service, inventory-service).

---

## Architecture

### Components

```
BootstrapModule (trait)
├── bootstrapValidate (command)
│   └── BootstrapValidator
│       ├── validateGitHubToken()
│       ├── validateOrganization()
│       ├── validateRepositoryName()
│       ├── validateProjectName()
│       ├── validateGitConfig()
│       ├── validateMillVersion()
│       └── validateSourceRepo()
├── bootstrapExecute (command)
│   ├── BootstrapValidator (run 7 checks)
│   ├── GitHubClient
│   │   ├── createRepository()
│   │   ├── configureBranchProtection()
│   │   ├── setTopics()
│   │   └── disableWiki()
│   ├── FrameworkCopier
│   │   ├── copyFrameworkFiles() (recursive, ~110 files)
│   │   └── updateFileReferences() (copilot-training → project-name)
│   ├── ProjectStubGenerator
│   │   ├── CharterTemplate.generate()
│   │   ├── ArchitectureTemplate.generate()
│   │   ├── ReadmeTemplate.generate()
│   │   ├── RootBuildScTemplate.generate()
│   │   └── GitignoreTemplate.generate()
│   └── GitOperations
│       ├── clone()
│       ├── commit()
│       └── push()
└── bootstrapList (command)
    └── GitHubClient
        └── listRepositories() (filter by topic:ceremony-based)
```

### Data Flow

```
User
  │
  ├─ mill bootstrap.validate <project-name>
  │   │
  │   └─> BootstrapModule.bootstrapValidate()
  │        │
  │        └─> BootstrapValidator.runChecks()
  │             │
  │             ├─ Check 1: GitHub token valid
  │             ├─ Check 2: Organization exists
  │             ├─ Check 3: Repository available
  │             ├─ Check 4: Project name conventions
  │             ├─ Check 5: Git configured
  │             ├─ Check 6: Mill 0.11.6+
  │             └─ Check 7: Source repo valid
  │             │
  │             └─> Result: PASS/FAIL
  │
  ├─ mill bootstrap.execute <project-name> <description>
  │   │
  │   └─> BootstrapModule.bootstrapExecute()
  │        │
  │        ├─> [1/9] BootstrapValidator.runChecks() (fail fast if any check fails)
  │        │
  │        ├─> [2/9] GitHubClient.createRepository()
  │        │    └─> POST https://api.github.com/orgs/:org/repos
  │        │
  │        ├─> [3/9] GitOperations.clone()
  │        │    └─> git clone https://github.com/:org/:repo /tmp/bootstrap-:repo
  │        │
  │        ├─> [4/9] FrameworkCopier.copyFrameworkFiles()
  │        │    │
  │        │    ├─ Copy HOW-WE-WORK.md
  │        │    ├─ Copy .github/copilot-instructions*.md (14 files)
  │        │    ├─ Copy doc/reference/SBPF/ (22 files)
  │        │    ├─ Copy doc/reference/templates/ (34 files)
  │        │    ├─ Copy doc/reference/validation/ (5 files)
  │        │    ├─ Copy mill-spinoff-plugin/ (15 files)
  │        │    └─ Copy mill-deploy-plugin/ (10 files)
  │        │
  │        ├─> [5/9] ProjectStubGenerator.generateStubs()
  │        │    │
  │        │    ├─ CharterTemplate.generate() → CHARTER.md
  │        │    ├─ ArchitectureTemplate.generate() → ARCHITECTURE.md
  │        │    ├─ ReadmeTemplate.generate() → README.md
  │        │    ├─ RootBuildScTemplate.generate() → build.sc
  │        │    ├─ GitignoreTemplate.generate() → .gitignore
  │        │    ├─ Empty framework → GOVERNANCE-BACKLOG.md
  │        │    └─ Phase 0 status → STATUS.md
  │        │
  │        ├─> [6/9] FrameworkCopier.updateFileReferences()
  │        │    └─ Replace "copilot-training" → "<project-name>" (23 files)
  │        │
  │        ├─> [7/9] GitOperations.commit()
  │        │    └─> git commit -m "Initial project bootstrap from RETISIO/copilot-training v1.0.0"
  │        │
  │        ├─> [8/9] GitOperations.push()
  │        │    └─> git push origin main
  │        │
  │        └─> [9/9] GitHubClient.configureRepository()
  │             │
  │             ├─ PUT /repos/:org/:repo/branches/main/protection (branch protection)
  │             ├─ PUT /repos/:org/:repo/topics (set topics: ddd, bdd, tdd, ceremony-based, mill-build)
  │             └─ PATCH /repos/:org/:repo (disable wiki)
  │
  └─ mill bootstrap.list
      │
      └─> BootstrapModule.bootstrapList()
           │
           └─> GitHubClient.listRepositories()
                │
                └─> GET https://api.github.com/orgs/:org/repos?type=all
                     │
                     └─ Filter: topic:ceremony-based AND commit message contains "Initial project bootstrap"
```

---

## Troubleshooting

### Issue: "GitHub token invalid or missing required scopes"

**Cause**: GITHUB_TOKEN not set or missing `repo` or `admin:org` scopes.

**Solution**:
1. Create new token at https://github.com/settings/tokens/new
2. Select scopes: `repo` (all), `admin:org` (read/write)
3. Export: `export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx`
4. Retry: `mill bootstrap.validate <project-name>`

---

### Issue: "Organization 'Acme' does not exist or is not accessible"

**Cause**: Organization doesn't exist or token doesn't have access.

**Solution**:
1. Verify organization exists: https://github.com/Acme
2. Verify token has access (member of organization or admin:org scope)
3. Retry with correct organization

---

### Issue: "Repository 'RETISIO/ecommerce-platform' already exists"

**Cause**: Repository name already taken in organization.

**Solution**:
1. Choose different name (e.g., `ecommerce-platform-v2`)
2. Or delete existing repository (if safe to do so)
3. Retry with new name

---

### Issue: "Project name 'Ecommerce_Platform' violates naming conventions"

**Cause**: Project name not kebab-case.

**Solution**:
1. Use kebab-case format: `ecommerce-platform`
2. Lowercase letters, numbers, hyphens only
3. Start with letter, 3-50 characters
4. Retry with correct name

---

### Issue: "Mill 0.10.5 installed, 0.11.6+ required"

**Cause**: Mill version too old.

**Solution**:
```bash
# Upgrade Mill via Coursier
cs install mill

# Verify version
mill --version  # Should be 0.11.6+
```

---

### Issue: "Source repository /home/tjm/copilot-training missing HOW-WE-WORK.md"

**Cause**: copilot-training not cloned or path incorrect.

**Solution**:
```bash
# Clone copilot-training
git clone https://github.com/RETISIO/copilot-training.git

# Run bootstrap from copilot-training directory
cd copilot-training
mill bootstrap.execute <project-name> <description>

# Or override sourceRepoPath in build.sc
```

---

### Issue: Bootstrap succeeds but build fails (`mill resolve __`)

**Cause**: Plugin sources not copied correctly or framework version mismatch.

**Solution**:
1. Verify plugin sources exist:
   ```bash
   ls -la mill-spinoff-plugin/
   ls -la mill-deploy-plugin/
   ```
2. Verify build.sc syntax (check for typos)
3. Check Mill version in build.sc matches installed Mill version
4. Re-bootstrap (delete repository and try again)

---

### Issue: GitHub API rate limit exceeded

**Cause**: Too many API calls in short time (5000/hour limit for authenticated requests).

**Solution**:
1. Wait 1 hour for rate limit reset
2. Reduce frequency of bootstrap operations
3. Use different GitHub token (if multiple users bootstrapping)

---

## FAQ

### Q: Can I bootstrap multiple projects simultaneously?

**A**: Discouraged (but not blocked). GitHub API rate limits and potential conflicts. Bootstrap sequentially (<60 seconds each).

---

### Q: Can I modify framework files (HOW-WE-WORK.md, SBPFs, templates) in bootstrapped repos?

**A**: Discouraged (creates framework drift). Instead:
1. Submit PR to copilot-training with fix
2. Sync update to bootstrapped repos (manual or future `mill bootstrap.sync`)

**Exception**: Bug fixes only (document in GOVERNANCE-BACKLOG.md).

---

### Q: How do I update framework in existing bootstrapped projects?

**A**: Manual sync (quarterly or when major updates released):
1. Clone copilot-training (main branch)
2. Compare HOW-WE-WORK.md, SBPFs, templates, copilot instructions
3. Copy changes to project repository
4. Commit: "Update ceremony framework from copilot-training v1.1.0"

**Future**: Automated sync via `mill bootstrap.sync` (Q3 2026).

---

### Q: Can I delete plugin sources (mill-spinoff-plugin/, mill-deploy-plugin/) from bootstrapped repos?

**A**: No (violates self-sufficiency). Plugin sources copied (not referenced) so bootstrapped repos work independently.

---

### Q: What if I want to create a non-ceremony project (no DDD/BDD/TDD)?

**A**: Don't use mill-bootstrap-plugin. Create repository manually (GitHub UI) or use plain Mill project template.

---

### Q: Can I cherry-pick framework files (copy only some files)?

**A**: No (use plugin to copy all ~110 files). Cherry-picking creates incomplete framework, missing ceremonies.

---

### Q: How do I bootstrap for open source (public repository)?

**A**: Plugin creates private repositories by default. Manual workaround:
1. Bootstrap normally (creates private repo)
2. Change visibility to public via GitHub UI (Settings → Danger Zone → Change visibility)
3. Future: Add `--public` flag to plugin (feature request)

---

## References

### Documentation
- **ADR-064**: Bootstrap via Mill Plugin (decision to use plugin)
- **POL-031**: Mill Bootstrap Plugin Usage Policy (usage rules)
- **HOW-WE-WORK.md**: Complete SDLC process framework
- **mill-bootstrap-plugin/doc/planning/DESIGN.md**: Plugin architecture
- **mill-bootstrap-plugin/doc/planning/VALIDATION-CRITERIA.md**: 7 validation checks
- **mill-bootstrap-plugin/doc/planning/WORKFLOW.md**: 3 command workflows

### Related Plugins
- **mill-spinoff-plugin**: ADR-060 (extract services to production)
- **mill-deploy-plugin**: Deployment automation

### External Resources
- **Mill Build Tool**: https://mill-build.com
- **GitHub API**: https://docs.github.com/en/rest
- **Ceremony-Based SDLC**: See HOW-WE-WORK.md in copilot-training

---

## Contributing

**Issues**: https://github.com/RETISIO/copilot-training/issues  
**Discussions**: https://github.com/RETISIO/copilot-training/discussions  
**Pull Requests**: Follow HOW-WE-WORK.md process (BDD scenarios → TDD → implementation)

---

## License

Same as copilot-training (see LICENSE in copilot-training repository).

---

## Contact

**Program Manager**: RETISIO Engineering Team  
**Architect**: [Your Name]  
**Questions**: Slack #ceremony-based-sdlc or GitHub Discussions
