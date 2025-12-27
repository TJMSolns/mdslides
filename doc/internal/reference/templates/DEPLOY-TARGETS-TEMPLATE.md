# DEPLOY-TARGETS.md Template

> **Purpose**: Track deployment configuration, history, and status for all environments.  
> **Location**: `services/<service-name>/DEPLOY-TARGETS.md`  
> **Maintained By**: mill-deploy-plugin (automatic updates)  
> **Related Policy**: [POL-030: Mill Deploy Plugin Usage Policy](../../governance/POL/POL-030-mill-deploy-plugin-usage.md)

---

## Service Information

**Service Name**: `<service-name>`  
**Repository**: `https://github.com/RETISIO/<service-name>`  
**Container Registry**: `ghcr.io/retisio/<service-name>`  
**Maintainer Team**: `@retisio/<team-name>`  
**Last Updated**: `<ISO-8601-timestamp>`

---

## Deployment Targets

### Local (Developer Workstation)

**Environment**: `local`  
**Cluster**: Docker Desktop / Minikube  
**Namespace**: `<service-name>-local`  
**Current Version**: `v0.1.0-dev`  
**Desired Version**: `v0.1.0-dev`  
**Last Deployed**: N/A (local only)  
**Status**: 🟢 Ready  

**Configuration**:
- **Image Tag Strategy**: `latest` (local build)
- **Replicas**: 1
- **Resources**: Minimal (100m CPU, 256Mi memory)
- **Database**: PostgreSQL (local Docker container)
- **Secrets**: `.env` file
- **Feature Flags**: Mock service (no external dependency)
- **Observability**: Local Prometheus (optional)

**Validation Level**: Minimal (4 checks)
- ✅ Tests passing
- ✅ Docker image builds
- ⚠️ Code coverage (warning only)
- ❌ Security scan (skipped)

**Access**: All developers

---

### Dev (Development Cluster)

**Environment**: `dev`  
**Cluster**: `retisio-dev-k8s.us-west-2.eks.amazonaws.com`  
**Namespace**: `<service-name>-dev`  
**Current Version**: `v1.2.3`  
**Desired Version**: `v1.2.4`  
**Last Deployed**: `2025-12-15T14:30:00Z` by `alice@retisio.com`  
**Status**: 🟢 Healthy  

**Configuration**:
- **Image Tag Strategy**: Git commit SHA (e.g., `v1.2.3` or `sha-abc123`)
- **Replicas**: 2
- **Resources**: Standard (500m CPU, 1Gi memory)
- **Database**: PostgreSQL (shared dev instance)
- **Secrets**: HashiCorp Vault (`secret/dev/<service-name>`)
- **Feature Flags**: LaunchDarkly (dev environment)
- **Observability**: Prometheus + Grafana + Jaeger

**Validation Level**: Standard (10 checks)
- ✅ All tests passing
- ✅ Code coverage ≥80%
- ⚠️ Property tests exist (warning)
- ✅ Docker image builds
- ✅ Security scan passed
- ✅ K8s manifests valid
- ✅ Database migrations tested
- ✅ Feature flags configured
- ✅ Secrets exist
- ✅ Resource quotas OK

**Access**: All developers (via `~/.kube/config-dev`)

**Service URL**: `http://<service-name>.dev.retisio.internal`

---

### Staging (Pre-Production Cluster)

**Environment**: `staging`  
**Cluster**: `retisio-staging-k8s.us-west-2.eks.amazonaws.com`  
**Namespace**: `<service-name>-staging`  
**Current Version**: `v1.2.2`  
**Desired Version**: `v1.2.3`  
**Last Deployed**: `2025-12-14T10:00:00Z` by `bob-techLead@retisio.com`  
**Status**: 🟢 Healthy  

**Configuration**:
- **Image Tag Strategy**: Semantic versioning (e.g., `v1.2.3`)
- **Replicas**: 3
- **Resources**: Production-like (1 CPU, 2Gi memory)
- **Database**: PostgreSQL (dedicated staging instance)
- **Secrets**: HashiCorp Vault (`secret/staging/<service-name>`)
- **Feature Flags**: LaunchDarkly (staging environment)
- **Observability**: Prometheus + Grafana + Jaeger + Alerts

**Validation Level**: Strict (15 checks + 1 approval)
- ✅ All tests passing
- ✅ Code coverage ≥80%
- ✅ Property tests exist
- ✅ Docker image builds
- ✅ Security scan passed
- ✅ K8s manifests valid
- ✅ Database migrations tested
- ✅ Feature flags configured
- ✅ Secrets exist
- ✅ Resource quotas OK
- ✅ Dependencies healthy
- ✅ API contract tests pass
- ✅ Load test baseline met
- ✅ Observability working
- ✅ Deployment approval obtained (1 Tech Lead)

**Access**: Tech Leads + Platform Team (via `~/.kube/config-staging`)

**Service URL**: `http://<service-name>.staging.retisio.internal`

**Approval Process**:
1. Create GitHub issue: `Deploy <service-name> to Staging - v<version>`
2. Link to dev deployment (must be successful)
3. Tag Tech Lead for approval: `@bob-techLead`
4. Tech Lead comments: `@retisio-deploy approve staging`
5. Plugin verifies approval before deployment

---

### Production (Live Customer Environment)

**Environment**: `production`  
**Cluster**: `retisio-prod-k8s.us-east-1.eks.amazonaws.com` (Primary)  
**Namespace**: `<service-name>-prod`  
**Current Version**: `v1.2.1`  
**Desired Version**: `v1.2.2`  
**Last Deployed**: `2025-12-10T16:00:00Z` by `platform-team@retisio.com`  
**Status**: 🟢 Healthy  

**Configuration**:
- **Image Tag Strategy**: Semantic versioning (e.g., `v1.2.1`)
- **Replicas**: 5 (auto-scaled 3-10)
- **Resources**: Production (2 CPU, 4Gi memory)
- **Database**: PostgreSQL (RDS Multi-AZ)
- **Secrets**: HashiCorp Vault (`secret/production/<service-name>`)
- **Feature Flags**: LaunchDarkly (production environment)
- **Observability**: Prometheus + Grafana + Jaeger + PagerDuty

**Validation Level**: Strict (15 checks + 2 approvals)
- ✅ All tests passing
- ✅ Code coverage ≥80%
- ✅ Property tests exist
- ✅ Docker image builds
- ✅ Security scan passed
- ✅ K8s manifests valid
- ✅ Database migrations tested
- ✅ Feature flags configured
- ✅ Secrets exist
- ✅ Resource quotas OK
- ✅ Dependencies healthy
- ✅ API contract tests pass
- ✅ Load test baseline met
- ✅ Observability working
- ✅ Deployment approval obtained (1 Tech Lead + 1 Product Owner)

**Access**: Platform Team ONLY (via `~/.kube/config-prod`, MFA required)

**Service URL**: `https://<service-name>.retisio.com`

**Approval Process**:
1. Create GitHub issue: `Deploy <service-name> to Production - v<version>`
2. Link to successful staging deployment (within last 7 days)
3. Tag Tech Lead + Product Owner: `@bob-techLead @carol-productOwner`
4. Tech Lead comments: `@retisio-deploy approve production technical`
5. Product Owner comments: `@retisio-deploy approve production business`
6. Plugin verifies both approvals before deployment

**Deployment Window**: Monday-Thursday, 10am-4pm EST (avoid Fridays)

**Rollback SLA**: <2 minutes (P95)

---

## Deployment History

### Recent Deployments (Last 10)

| Version | Environment | Timestamp | Deployed By | Status | Duration | Approval |
|---------|-------------|-----------|-------------|--------|----------|----------|
| `v1.2.4` | dev | 2025-12-15T14:30:00Z | alice@retisio.com | 🟢 success | 90s | N/A |
| `v1.2.3` | dev | 2025-12-14T16:00:00Z | alice@retisio.com | 🟢 success | 85s | N/A |
| `v1.2.3` | staging | 2025-12-14T10:00:00Z | bob-techLead@retisio.com | 🟢 success | 120s | [#456](https://github.com/RETISIO/copilot-training/issues/456) |
| `v1.2.2` | dev | 2025-12-13T11:00:00Z | alice@retisio.com | 🟢 success | 95s | N/A |
| `v1.2.2` | staging | 2025-12-12T14:00:00Z | bob-techLead@retisio.com | 🟢 success | 110s | [#450](https://github.com/RETISIO/copilot-training/issues/450) |
| `v1.2.2` | production | 2025-12-10T16:00:00Z | platform-team@retisio.com | 🟢 success | 180s | [#445](https://github.com/RETISIO/copilot-training/issues/445) |
| `v1.2.1` | production | 2025-12-05T15:00:00Z | platform-team@retisio.com | 🟢 success | 175s | [#430](https://github.com/RETISIO/copilot-training/issues/430) |
| `v1.2.0` | production | 2025-12-03T14:00:00Z | platform-team@retisio.com | 🔴 failed | 320s | [#425](https://github.com/RETISIO/copilot-training/issues/425) |
| `v1.1.9` | production | 2025-12-03T14:06:00Z | mill-deploy-plugin | 🔄 rolled_back | 95s | N/A (automatic) |
| `v1.2.0` | staging | 2025-12-02T10:00:00Z | bob-techLead@retisio.com | 🟢 success | 125s | [#420](https://github.com/RETISIO/copilot-training/issues/420) |

**Status Legend**:
- 🟢 `success` - Deployment completed successfully
- 🔴 `failed` - Deployment failed (validation or runtime)
- 🔄 `rolled_back` - Automatic or manual rollback performed
- 🟡 `in_progress` - Currently deploying

---

### Rollback Events

| Timestamp | Environment | Failed Version | Rolled Back To | Reason | Duration |
|-----------|-------------|----------------|----------------|--------|----------|
| 2025-12-03T14:06:00Z | production | v1.2.0 | v1.1.9 | Smoke test failed: HTTP 500 (database connection timeout) | 95s |
| 2025-11-20T11:35:00Z | staging | v1.1.5 | v1.1.4 | Pod crashes: CrashLoopBackOff (NullPointerException in startup) | 110s |
| 2025-11-10T09:15:00Z | dev | v1.1.0 | v1.0.9 | Error rate spike: 15% (Kafka consumer misconfigured) | 65s |

---

## Environment Comparison

| Aspect | Local | Dev | Staging | Production |
|--------|-------|-----|---------|------------|
| **Purpose** | Developer testing | Integration testing | Pre-prod validation | Customer-facing |
| **Replicas** | 1 | 2 | 3 | 5 (auto-scaled) |
| **CPU** | 100m | 500m | 1000m | 2000m |
| **Memory** | 256Mi | 1Gi | 2Gi | 4Gi |
| **Database** | Local Docker | Shared dev | Dedicated staging | RDS Multi-AZ |
| **Secrets** | `.env` file | Vault | Vault | Vault |
| **Validation** | Minimal (4) | Standard (10) | Strict (15+1) | Strict (15+2) |
| **Approval** | None | None | 1 Tech Lead | 1 Tech Lead + 1 PO |
| **Rollback** | Manual | Automatic | Automatic | Automatic |
| **Monitoring** | Optional | Standard | Full | Full + PagerDuty |

---

## Configuration Details

### Database Migrations

**Strategy**: Run migrations **before** Kubernetes deployment (backward-compatible only)

**Execution**:
```bash
mill <service-name>.runMigrations <environment>
```

**Rollback Migrations**: Available in `db/migrations/rollback/` (manual execution only)

**Policy**: All migrations MUST be backward-compatible (old code works with new schema) for at least one deployment cycle.

---

### Feature Flags

**Service**: LaunchDarkly  
**SDK**: Java Server-Side SDK  

**Required Flags** (defined in `features/feature-flags.yaml`):
- `enable-new-tenant-validation` (boolean)
- `max-tenants-per-organization` (number)
- `tenant-provisioning-mode` (string: "sync" or "async")

**Configuration**:
- Local: Mock service (all flags default to `false`)
- Dev: LaunchDarkly dev environment
- Staging: LaunchDarkly staging environment
- Production: LaunchDarkly production environment

---

### Secrets

**Provider**: HashiCorp Vault  

**Required Secrets**:
- `database-url` (PostgreSQL connection string)
- `kafka-bootstrap-servers` (Kafka broker addresses)
- `api-key-encryption-key` (AES-256 key for encrypting tenant API keys)

**Access**:
- Local: `.env` file in project root
- Dev/Staging/Production: Kubernetes External Secrets Operator pulls from Vault

**Rotation**: Every 90 days (automated via Vault)

---

### Observability

**Metrics**: Prometheus (`:9090/metrics` endpoint)  
**Traces**: OpenTelemetry → Jaeger  
**Logs**: Structured JSON → Loki  
**Dashboards**: Grafana (`https://grafana.retisio.internal/d/<service-name>`)  
**Alerts**: PagerDuty (production only)

**Key Metrics**:
- `http_requests_total` (counter)
- `http_request_duration_seconds` (histogram)
- `tenant_provisioning_duration_seconds` (histogram)
- `active_tenants` (gauge)

---

## Troubleshooting

### Deployment Fails with "Validation Failed"

**Symptom**: `mill <service-name>.deployValidate <env>` returns failures

**Solution**:
1. Review validation output (lists all failed checks)
2. Fix issues locally (tests, coverage, migrations, etc.)
3. Re-run validation: `mill <service-name>.deployValidate <env>`
4. Once all checks pass, deploy: `mill <service-name>.deployExecute <env>`

**Common Failures**:
- **Tests failing**: Run `mill <service-name>.test` locally, fix failing tests
- **Coverage <80%**: Add more unit tests, run `mill <service-name>.coverage`
- **Security scan failed**: Update vulnerable dependencies in `build.sc`
- **K8s manifests invalid**: Check YAML syntax in `k8s/overlays/<env>/`

---

### Deployment Succeeds but Pods Crash

**Symptom**: Deployment completes but pods enter `CrashLoopBackOff`

**Solution**:
1. Check pod logs: `kubectl logs -n <service-name>-<env> deployment/<service-name>`
2. Check events: `kubectl get events -n <service-name>-<env>`
3. Common causes:
   - Missing secrets (check Vault)
   - Database connection failure (check connection string)
   - Memory limit too low (increase in `k8s/overlays/<env>/kustomization.yaml`)
4. Plugin should **automatically rollback** within 5 minutes

---

### Automatic Rollback Triggered

**Symptom**: Deployment rolls back without manual intervention

**Solution**:
1. Check `DEPLOY-TARGETS.md` for rollback reason
2. Review Grafana dashboard for error rate, memory usage
3. Check Jaeger traces for failed requests
4. Fix issue in code, deploy again with higher version

**Rollback Triggers**:
- Smoke test failed (health endpoint returns non-200)
- Pods crashing (3+ restarts within 5 minutes)
- Error rate >5% (sustained for 2 minutes)
- Memory usage >90% (sustained for 3 minutes)

---

### Manual Rollback Needed

**Symptom**: Need to rollback to previous version manually

**Solution**:
```bash
# Rollback to previous version (automatic)
mill <service-name>.deployRollback <env>

# OR rollback to specific version
mill <service-name>.deployRollback <env> --version=v1.2.0
```

**Verification**:
1. Check pods healthy: `kubectl get pods -n <service-name>-<env>`
2. Check logs: `kubectl logs -n <service-name>-<env> deployment/<service-name>`
3. Test service: `curl https://<service-name>.<env>.retisio.com/health`

---

## Related Documentation

- **Policy**: [POL-030: Mill Deploy Plugin Usage Policy](../../governance/POL/POL-030-mill-deploy-plugin-usage.md)
- **Architecture**: [ADR-061: Deploy Validation Criteria Selection](../../governance/ADR/ADR-061-deploy-validation-criteria-selection.md)
- **Architecture**: [ADR-062: Kubernetes Integration Strategy](../../governance/ADR/ADR-062-kubernetes-integration-strategy.md)
- **Architecture**: [ADR-063: Deployment Rollback Mechanism](../../governance/ADR/ADR-063-deployment-rollback-mechanism.md)
- **Workflow**: [HOW-WE-WORK.md - Phase 4 Deployment](../../../HOW-WE-WORK.md#phase-4-delivery)
- **Plugin Usage**: [mill-deploy-plugin README](../../../mill-deploy-plugin/README.md)

---

## Maintenance

**Last Updated**: 2025-12-16  
**Update Frequency**: Automatic (plugin updates on every deployment)  
**Manual Updates**: Environment configuration changes (replicas, resources, URLs)  
**Owner**: Platform Team (`@retisio/platform-team`)
