# Mill Deploy Plugin

**Automated deployment validation and execution for the prior organization microservices**

[![Build Status](https://github.com/the prior organization/copilot-training/actions/workflows/mill-deploy-plugin.yml/badge.svg)](https://github.com/the prior organization/copilot-training/actions)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

---

## Overview

The **mill-deploy-plugin** automates microservice deployment to Kubernetes environments with:
- ✅ **15 validation checks** (tests, security, infrastructure, integration, governance)
- 🚀 **Automatic rollback** on failure (<2 min MTTR)
- 📊 **Audit trail** (deployment history tracked in `DEPLOY-TARGETS.md`)
- 🔒 **RBAC enforcement** (role-based deployment access)
- 🔄 **Environment consistency** (local → dev → staging → production)

**Replaces manual `kubectl apply` with ceremony-driven deployment automation.**

### 🧭 Scala 3 Migration Notes

- **Scala 3.3.1** baseline; enums replace sealed traits in validation checks.
- **Per-check `T.task`** targets for deployment validations to enable parallelism.
- **Dependencies**: Scala 3 toolchain; ScalaTest 3.2.18; ScalaMock removed.
- **Non-blocking rule**: keep validations side-effect free; no blocking I/O.

---

## Table of Contents

- [Quick Start](#quick-start)
- [Installation](#installation)
- [Usage](#usage)
  - [List Deployment Targets](#list-deployment-targets)
  - [Validate Deployment](#validate-deployment)
  - [Execute Deployment](#execute-deployment)
  - [Rollback Deployment](#rollback-deployment)
- [Configuration](#configuration)
- [Validation Criteria](#validation-criteria)
- [Environments](#environments)
- [Rollback Mechanism](#rollback-mechanism)
- [Troubleshooting](#troubleshooting)
- [Architecture](#architecture)
- [Contributing](#contributing)
- [Related Documentation](#related-documentation)

---

## Quick Start

### Prerequisites
- ✅ **Mill** ≥0.11.6 installed
- ✅ **kubectl** ≥1.28 installed
- ✅ **kustomize** ≥5.0 installed
- ✅ **Kubeconfig** configured for target environment
- ✅ **Docker** (for building images)

### 1. Install Plugin

Add to your service's `build.sc`:

```scala
import $ivy.`io.github.retisio::mill-deploy-plugin:0.1.0`
import com.retisio.mill.DeployModule

object tenantManagement extends DeployModule {
  // Your existing module configuration
}
```

### 2. Create DEPLOY-TARGETS.md

```bash
cp doc/reference/templates/DEPLOY-TARGETS-TEMPLATE.md services/tenant-management/DEPLOY-TARGETS.md
# Edit file to configure your environments
```

### 3. Deploy to Dev

```bash
# Validate readiness
mill tenantManagement.deployValidate Dev

# If all checks pass, deploy
mill tenantManagement.deployExecute Dev
```

**Result**: Service deployed to dev cluster with automatic smoke tests and rollback monitoring.

---

## Installation

### Add Plugin Dependency

**build.sc** (root):
```scala
import $ivy.`io.github.retisio::mill-deploy-plugin:0.1.0`
```

**build.sc** (service module):
```scala
import com.retisio.mill.DeployModule

object myService extends DeployModule {
  def scalaVersion = "2.13.12"
  
  // Optional: Override default settings
  def deployTargetsFile = T { pwd / "DEPLOY-TARGETS.md" }
  def kubernetesManifestsDir = T { pwd / "k8s" }
}
```

### Environment Setup

#### 1. Configure Kubeconfig Files

```bash
# Dev environment
export KUBECONFIG=~/.kube/config-dev
kubectl config get-contexts  # Verify access

# Staging environment
export KUBECONFIG=~/.kube/config-staging
kubectl config get-contexts

# Production environment
export KUBECONFIG=~/.kube/config-prod
kubectl config get-contexts
```

**See**: [ADR-062: Kubernetes Integration Strategy](doc/governance/ADR/ADR-062-kubernetes-integration-strategy.md)

#### 2. Create Kustomize Overlays

```bash
mkdir -p services/my-service/k8s/{base,overlays/{local,dev,staging,production}}
```

**Example structure**:
```
services/my-service/k8s/
├── base/
│   ├── deployment.yaml
│   ├── service.yaml
│   └── kustomization.yaml
└── overlays/
    ├── local/
    │   └── kustomization.yaml
    ├── dev/
    │   └── kustomization.yaml
    ├── staging/
    │   └── kustomization.yaml
    └── production/
        └── kustomization.yaml
```

**See**: [ADR-062: Kubernetes Integration Strategy - Manifest Templating](doc/governance/ADR/ADR-062-kubernetes-integration-strategy.md#3-manifest-templating-with-kustomize)

#### 3. Initialize DEPLOY-TARGETS.md

```bash
cp doc/reference/templates/DEPLOY-TARGETS-TEMPLATE.md services/my-service/DEPLOY-TARGETS.md
```

Edit to configure your environments (cluster URLs, namespaces, resources, etc.)

---

## Usage

### List Deployment Targets

**Show all configured deployment environments and their current status:**

```bash
mill myService.deployList
```

**Example Output**:
```
╔════════════════════════════════════════════════════════════════════════════╗
║                       DEPLOYMENT TARGETS: my-service                        ║
╠════════════════════════════════════════════════════════════════════════════╣
║ Environment │ Current Version │ Desired Version │ Status    │ Last Deploy ║
╠═════════════╪═════════════════╪═════════════════╪═══════════╪═════════════╣
║ local       │ v0.1.0-dev      │ v0.1.0-dev      │ 🟢 Ready  │ N/A         ║
║ dev         │ v1.2.3          │ v1.2.4          │ 🟡 Update │ 2 hours ago ║
║ staging     │ v1.2.2          │ v1.2.3          │ 🟡 Update │ 1 day ago   ║
║ production  │ v1.2.1          │ v1.2.2          │ 🟢 Healthy│ 5 days ago  ║
╚═════════════╧═════════════════╧═════════════════╧═══════════╧═════════════╝

Legend:
  🟢 Healthy  - Current version running successfully
  🟡 Update   - Newer version available
  🔴 Failed   - Current version experiencing issues
  🔄 Rolling  - Deployment in progress
```

---

### Validate Deployment

**Check if service is ready to deploy to target environment:**

```bash
mill myService.deployValidate <TargetName>
```

**Example**:
```bash
mill tenantManagement.deployValidate Staging
```

**Example Output** (all checks passed):
```
╔════════════════════════════════════════════════════════════════════════════╗
║             DEPLOYMENT VALIDATION: tenant-management → Staging              ║
╠════════════════════════════════════════════════════════════════════════════╣
║ Testing
║  ✅ Check 1: All tests passing (152 tests, 0 failures)
║  ✅ Check 2: Code coverage ≥80% (87% line coverage)
║  ✅ Check 3: Property-based tests exist (8 property tests found)
║
║ Security
║  ✅ Check 4: Docker image builds (image: ghcr.io/retisio/tenant-management:v1.2.3)
║  ✅ Check 5: Container security scan passed (0 HIGH/CRITICAL vulnerabilities)
║
║ Infrastructure
║  ✅ Check 6: Kubernetes manifests valid (5 resources validated)
║  ✅ Check 7: Database migrations tested (3 migrations validated)
║  ✅ Check 8: Feature flags configured (3 required flags exist)
║  ✅ Check 9: Secrets exist in Vault (4 secrets verified)
║  ✅ Check 10: Resource quotas OK (CPU: 1000m/8000m, Memory: 2Gi/16Gi)
║
║ Integration
║  ✅ Check 11: Dependencies healthy (2/2 services running)
║  ✅ Check 12: API contract tests pass (5 contract scenarios passed)
║  ✅ Check 13: Load test baseline met (P95: 120ms < 200ms, errors: 0.1% < 1%)
║
║ Governance
║  ✅ Check 14: Observability working (metrics, traces, logs flowing)
║  ✅ Check 15: Deployment approval obtained (1 approval: @bob-techLead)
║
╠════════════════════════════════════════════════════════════════════════════╣
║ ✅ ALL CHECKS PASSED - Ready to deploy
╚════════════════════════════════════════════════════════════════════════════╝

Next steps:
  mill tenantManagement.deployExecute Staging
```

**Example Output** (some checks failed):
```
╔════════════════════════════════════════════════════════════════════════════╗
║             DEPLOYMENT VALIDATION: tenant-management → Staging              ║
╠════════════════════════════════════════════════════════════════════════════╣
║ Testing
║  ❌ Check 1: All tests passing - 3 tests failed
║      → src/test/TenantServiceTest.java:45 - assertion failed
║      → src/test/TenantRepositoryTest.java:78 - NullPointerException
║  ✅ Check 2: Code coverage ≥80% (87% line coverage)
║  ⚠️  Check 3: Property-based tests exist - warning: only 2 property tests
║
║ Security
║  ✅ Check 4: Docker image builds
║  ❌ Check 5: Security scan - 2 HIGH vulnerabilities found
║      → CVE-2023-12345 (log4j-core 2.17.0 → upgrade to 2.20.0)
║      → CVE-2023-67890 (jackson-databind 2.13.0 → upgrade to 2.15.0)
║
║ [...truncated...]
║
╠════════════════════════════════════════════════════════════════════════════╣
║ ❌ VALIDATION FAILED - 2 blocking failures, 1 warning
╚════════════════════════════════════════════════════════════════════════════╝

Fix the issues above and re-run validation.
```

---

### Execute Deployment

**Deploy service to target environment (after validation passes):**

```bash
mill myService.deployExecute <TargetName>
```

**Example**:
```bash
mill tenantManagement.deployExecute Staging
```

**Example Output**:
```
╔════════════════════════════════════════════════════════════════════════════╗
║                     DEPLOYING: tenant-management → Staging                  ║
╚════════════════════════════════════════════════════════════════════════════╝

[1/8] Validating deployment readiness...
  ✅ All 15 validation checks passed

[2/8] Building Docker image...
  ✅ Image built: ghcr.io/retisio/tenant-management:v1.2.3 (sha256:abc123...)
  
[3/8] Pushing image to registry...
  ✅ Image pushed to ghcr.io/retisio/tenant-management:v1.2.3

[4/8] Building Kubernetes manifests...
  ✅ Kustomize build complete: target/tenant-management/k8s/staging/manifests.yaml

[5/8] Applying manifests to cluster...
  ✅ kubectl apply -n tenant-management-staging -f manifests.yaml
     deployment.apps/tenant-management configured
     service/tenant-management unchanged

[6/8] Waiting for rollout...
  ⏳ Waiting for deployment "tenant-management" rollout to finish: 1 old replicas are pending termination...
  ⏳ Waiting for deployment "tenant-management" rollout to finish: 1 of 3 updated replicas are available...
  ⏳ Waiting for deployment "tenant-management" rollout to finish: 2 of 3 updated replicas are available...
  ✅ deployment "tenant-management" successfully rolled out (45s)

[7/8] Running smoke tests...
  ✅ Health check: http://tenant-management.staging.tjm.internal/health → 200 OK
  ✅ Readiness check: 3/3 pods ready

[8/8] Post-deployment monitoring (5 minutes)...
  ⏳ Monitoring for error rate spikes, pod crashes, memory leaks...
  [00:30] ✅ All checks passing (error rate: 0.0%, memory: 45%)
  [01:00] ✅ All checks passing (error rate: 0.1%, memory: 47%)
  [01:30] ✅ All checks passing (error rate: 0.0%, memory: 48%)
  [02:00] ✅ All checks passing (error rate: 0.1%, memory: 50%)
  [...truncated...]
  [05:00] ✅ All checks passing (error rate: 0.0%, memory: 52%)

╔════════════════════════════════════════════════════════════════════════════╗
║ ✅ DEPLOYMENT SUCCESSFUL                                                    ║
╠════════════════════════════════════════════════════════════════════════════╣
║ Service:      tenant-management                                             ║
║ Environment:  staging                                                       ║
║ Version:      v1.2.3                                                        ║
║ Duration:     6m 45s                                                        ║
║ Deployed By:  bob-techLead@tjm.solutions                                     ║
╚════════════════════════════════════════════════════════════════════════════╝

Updated: services/tenant-management/DEPLOY-TARGETS.md
```

---

### Rollback Deployment

#### Automatic Rollback
**Plugin automatically rolls back on failure** (no manual action needed):
- Smoke tests fail (health check returns non-200)
- Pods crash (CrashLoopBackOff, 3+ restarts)
- Error rate spike (>5% for 2 minutes)
- Memory leak (>90% for 3 minutes)
- Startup timeout (pods not ready within 5 minutes)

**Example (automatic rollback triggered)**:
```
[7/8] Running smoke tests...
  ❌ Health check failed: HTTP 500 (database connection timeout)
  🔄 AUTOMATIC ROLLBACK TRIGGERED

Rolling back from v1.2.3 → v1.2.2...
  ✅ kubectl rollout undo deployment/tenant-management -n tenant-management-staging
  ✅ Waiting for rollback to complete (45s)
  ✅ Rolled-back version healthy (HTTP 200 OK)

╔════════════════════════════════════════════════════════════════════════════╗
║ 🔄 DEPLOYMENT ROLLED BACK                                                   ║
╠════════════════════════════════════════════════════════════════════════════╣
║ Service:         tenant-management                                          ║
║ Environment:     staging                                                    ║
║ Failed Version:  v1.2.3                                                     ║
║ Rolled Back To:  v1.2.2                                                     ║
║ Reason:          Smoke test failed - HTTP 500 (database connection timeout) ║
║ Duration:        1m 35s                                                     ║
╚════════════════════════════════════════════════════════════════════════════╝

Notification sent to Slack: #deployments
Updated: services/tenant-management/DEPLOY-TARGETS.md
```

---

#### Manual Rollback

**Rollback to previous successful version:**

```bash
mill myService.deployRollback <TargetName>
```

**Rollback to specific version:**

```bash
mill myService.deployRollback <TargetName> --version=v1.2.0
```

**Example**:
```bash
mill tenantManagement.deployRollback Staging --version=v1.2.1
```

---

## Configuration

### DEPLOY-TARGETS.md Structure

Required file: `services/<service-name>/DEPLOY-TARGETS.md`

**Key Sections**:
1. **Service Information** - Name, repository, registry, maintainer
2. **Deployment Targets** - Environment-specific configuration (local, dev, staging, production)
3. **Deployment History** - Last 10 deployments with status
4. **Rollback Events** - History of automatic/manual rollbacks
5. **Configuration Details** - Secrets, feature flags, observability

**See**: [DEPLOY-TARGETS.md Template](doc/reference/templates/DEPLOY-TARGETS-TEMPLATE.md)

---

### Kubernetes Manifests with Kustomize

**Base manifests** (common to all environments):
```yaml
# services/my-service/k8s/base/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-service
spec:
  replicas: 1  # Overridden per environment
  selector:
    matchLabels:
      app: my-service
  template:
    metadata:
      labels:
        app: my-service
    spec:
      containers:
      - name: my-service
        image: ghcr.io/retisio/my-service:latest
        ports:
        - containerPort: 8080
```

**Environment overlays** (environment-specific overrides):
```yaml
# services/my-service/k8s/overlays/production/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: my-service-prod
resources:
- ../../base
images:
- name: ghcr.io/retisio/my-service
  newTag: v1.2.3  # Set by plugin at deploy time
replicas:
- name: my-service
  count: 5  # Scale up in production
```

**See**: [ADR-062: Kubernetes Integration Strategy](doc/governance/ADR/ADR-062-kubernetes-integration-strategy.md)

---

## Validation Criteria

### 15 Checks Across 5 Categories

| # | Check | Local | Dev | Staging | Prod |
|---|-------|-------|-----|---------|------|
| **Testing** |
| 1 | All tests passing | ✅ | ✅ | ✅ | ✅ |
| 2 | Code coverage ≥80% | ⚠️ | ✅ | ✅ | ✅ |
| 3 | Property tests exist | ❌ | ⚠️ | ✅ | ✅ |
| **Security** |
| 4 | Docker image builds | ✅ | ✅ | ✅ | ✅ |
| 5 | Security scan passed | ❌ | ✅ | ✅ | ✅ |
| **Infrastructure** |
| 6 | K8s manifests valid | ❌ | ✅ | ✅ | ✅ |
| 7 | Migrations tested | ❌ | ✅ | ✅ | ✅ |
| 8 | Feature flags configured | ❌ | ✅ | ✅ | ✅ |
| 9 | Secrets exist | ❌ | ✅ | ✅ | ✅ |
| 10 | Resource quotas OK | ❌ | ✅ | ✅ | ✅ |
| **Integration** |
| 11 | Dependencies healthy | ❌ | ⚠️ | ✅ | ✅ |
| 12 | Contract tests pass | ❌ | ⚠️ | ✅ | ✅ |
| 13 | Load baseline met | ❌ | ❌ | ✅ | ✅ |
| **Governance** |
| 14 | Observability working | ❌ | ⚠️ | ✅ | ✅ |
| 15 | Approval obtained | ❌ | ❌ | ✅ | ✅ |

**Legend**:
- ✅ **Blocking** - Deployment fails if check fails
- ⚠️ **Warning** - Deployment proceeds with warning
- ❌ **Skipped** - Check not applicable

**See**: [ADR-061: Deploy Validation Criteria Selection](doc/governance/ADR/ADR-061-deploy-validation-criteria-selection.md)

---

## Environments

### Local (Developer Workstation)
- **Cluster**: Docker Desktop / Minikube
- **Validation**: Minimal (4 checks)
- **Approval**: None
- **Use Case**: Rapid local development

### Dev (Shared Development Cluster)
- **Cluster**: AWS EKS (dev)
- **Validation**: Standard (10 checks)
- **Approval**: None
- **Use Case**: Integration testing, feature development

### Staging (Pre-Production)
- **Cluster**: AWS EKS (staging)
- **Validation**: Strict (15 checks)
- **Approval**: 1 Tech Lead
- **Use Case**: Final validation before production

### Production (Customer-Facing)
- **Cluster**: AWS EKS (production, Multi-AZ)
- **Validation**: Strict (15 checks + governance)
- **Approval**: 1 Tech Lead + 1 Product Owner
- **Use Case**: Live customer traffic

**See**: [POL-030: Mill Deploy Plugin Usage Policy - Environment Requirements](doc/governance/POL/POL-030-mill-deploy-plugin-usage.md#environment-specific-requirements)

---

## Rollback Mechanism

### Automatic Rollback Triggers

1. **Smoke Test Failure** → Rollback after 5 failed retries (50s)
2. **Pod Crashes** → Rollback after 3 restarts within 5 minutes
3. **Error Rate Spike** → Rollback if error rate >5% for 2 minutes
4. **Memory Leak** → Rollback if memory >90% for 3 minutes
5. **Startup Timeout** → Rollback if pods not ready within 5 minutes

### Rollback Process

1. Identify previous successful version (from `DEPLOY-TARGETS.md`)
2. Execute `kubectl rollout undo` (fast, <30s)
3. Wait for rollout completion (target: <2 minutes)
4. Verify rolled-back version is healthy
5. Record rollback event in `DEPLOY-TARGETS.md`
6. Send Slack notification

**Target MTTR**: <2 minutes (P95)

**See**: [ADR-063: Deployment Rollback Mechanism](doc/governance/ADR/ADR-063-deployment-rollback-mechanism.md)

---

## Troubleshooting

### Validation Fails: "Tests failing"

**Symptom**: Check 1 fails with test failures

**Solution**:
```bash
# Run tests locally
mill myService.test

# Fix failing tests, then re-validate
mill myService.deployValidate Dev
```

---

### Validation Fails: "Security scan failed"

**Symptom**: Check 5 fails with HIGH/CRITICAL vulnerabilities

**Solution**:
```bash
# Update vulnerable dependencies in build.sc
# Example: Update log4j-core from 2.17.0 to 2.20.0

# Re-run security scan locally
trivy image ghcr.io/retisio/my-service:latest

# Re-validate
mill myService.deployValidate Dev
```

---

### Deployment Fails: "Insufficient approvals"

**Symptom**: Check 15 fails for staging/production

**Solution**:
1. Create GitHub issue: `Deploy my-service to Staging - v1.2.3`
2. Tag Tech Lead for approval: `@bob-techLead`
3. Tech Lead comments: `@retisio-deploy approve staging`
4. Re-run validation (approval detected automatically)

---

### Pods Crash After Deployment

**Symptom**: Deployment succeeds but pods enter `CrashLoopBackOff`

**Solution**:
```bash
# Check pod logs
kubectl logs -n my-service-dev deployment/my-service

# Check events
kubectl get events -n my-service-dev

# Common causes:
# - Missing secrets (check Vault)
# - Database connection failure
# - Memory limit too low

# Plugin should auto-rollback within 5 minutes
# If not, manual rollback:
mill myService.deployRollback Dev
```

---

### Rollback Fails

**Symptom**: Rollback initiated but pods never become ready

**Solution**:
1. Check rollback logs (plugin collects diagnostics)
2. Escalate to Platform Team (PagerDuty alert sent)
3. Manual investigation required (previous version may also be broken)

**See**: [ADR-063: Rollback Failure Scenarios](doc/governance/ADR/ADR-063-deployment-rollback-mechanism.md#rollback-failure-scenarios)

---

## Architecture

### Component Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                      mill-deploy-plugin                          │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │  DeployList  │  │DeployValidate│  │DeployExecute │         │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘         │
│         │                  │                  │                  │
│         └──────────────────┴──────────────────┘                  │
│                            │                                     │
│         ┌──────────────────▼──────────────────┐                 │
│         │       DeployTargetsParser           │                 │
│         │  (reads DEPLOY-TARGETS.md)          │                 │
│         └──────────────────┬──────────────────┘                 │
│                            │                                     │
│         ┌──────────────────▼──────────────────┐                 │
│         │       DeployValidator               │                 │
│         │  (runs 15 validation checks)        │                 │
│         └──────────────────┬──────────────────┘                 │
│                            │                                     │
│         ┌──────────────────▼──────────────────┐                 │
│         │       DeployExecutor                │                 │
│         │  (kubectl, kustomize, rollback)     │                 │
│         └──────────────────┬──────────────────┘                 │
│                            │                                     │
└────────────────────────────┼──────────────────────────────────────┘
                             │
              ┌──────────────▼──────────────┐
              │  External Integrations      │
              ├─────────────────────────────┤
              │  • kubectl (Kubernetes API) │
              │  • kustomize (manifest build)│
              │  • Trivy (security scan)    │
              │  • Prometheus (metrics)     │
              │  • Vault (secrets)          │
              │  • LaunchDarkly (flags)     │
              │  • Slack (notifications)    │
              └─────────────────────────────┘
```

**See**: [Design Document](doc/planning/mill-deploy-plugin/DESIGN.md)

---

## Contributing

### Development Setup

```bash
# Clone repository
git clone https://github.com/the prior organization/copilot-training.git
cd copilot-training/mill-deploy-plugin

# Compile plugin
mill millDeployPlugin.compile

# Run tests
mill millDeployPlugin.test

# Package plugin
mill millDeployPlugin.publishLocal
```

### Testing Changes

```bash
# Use local plugin in service
import $ivy.`io.github.retisio::mill-deploy-plugin:0.1.0-SNAPSHOT`

# Test against tenant-management
cd services/tenant-management
mill tenantManagement.deployValidate Dev
```

### Submitting Changes

1. Create feature branch: `git checkout -b feature/my-enhancement`
2. Make changes with tests
3. Run full test suite: `mill millDeployPlugin.test`
4. Submit PR with description

**Code Review Required**: 2 Platform Team members

---

## Related Documentation

### Governance
- **[POL-030: Mill Deploy Plugin Usage Policy](doc/governance/POL/POL-030-mill-deploy-plugin-usage.md)** - Mandatory usage policy
- **[ADR-061: Deploy Validation Criteria Selection](doc/governance/ADR/ADR-061-deploy-validation-criteria-selection.md)** - Why 15 checks
- **[ADR-062: Kubernetes Integration Strategy](doc/governance/ADR/ADR-062-kubernetes-integration-strategy.md)** - kubectl/kustomize usage
- **[ADR-063: Deployment Rollback Mechanism](doc/governance/ADR/ADR-063-deployment-rollback-mechanism.md)** - Automatic rollback design

### Templates
- **[DEPLOY-TARGETS.md Template](doc/reference/templates/DEPLOY-TARGETS-TEMPLATE.md)** - Configuration file structure

### Design Documents
- **[Design](doc/planning/mill-deploy-plugin/DESIGN.md)** - Architecture and requirements
- **[Validation Criteria](doc/planning/mill-deploy-plugin/VALIDATION-CRITERIA.md)** - Detailed check specifications
- **[Workflow](doc/planning/mill-deploy-plugin/WORKFLOW.md)** - Deployment flow diagrams

### Related Plugins
- **[mill-spinoff-plugin](mill-spinoff-plugin/README.md)** - Extracts services from training repo to production repos

### SDLC Process
- **[HOW-WE-WORK.md](HOW-WE-WORK.md)** - Complete ceremony-driven development process
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - System architecture overview

---

## License

Apache License 2.0 - see [LICENSE](LICENSE) for details

---

## Support

- **Issues**: https://github.com/the prior organization/copilot-training/issues
- **Discussions**: https://github.com/the prior organization/copilot-training/discussions
- **Slack**: #platform-team (internal)
- **Email**: platform-team@tjm.solutions

---

**Last Updated**: December 16, 2025  
**Version**: 0.1.0 (Phase 0 - Design Complete)  
**Maintainer**: Platform Team (@retisio/platform-team)
