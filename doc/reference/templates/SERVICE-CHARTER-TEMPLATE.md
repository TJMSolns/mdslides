# {Service Name} Service Charter

**Service**: {service-name}  
**Port**: {port}  
**Database**: `gifting_{service}`  
**Version**: 1.0.0  
**Status**: {Active/Planned/Development}

---

## Purpose

{1-3 sentences explaining the business problem this service solves}

**Example**: The Order Service manages the lifecycle of gift orders from creation through delivery, coordinating scheduled and recurring fulfillment while delegating actual order placement to the platform.

---

## Responsibilities

### Core Capabilities

{List the main features this service provides}

**Example**:
- Create and manage gift orders (immediate, scheduled, recurring)
- Coordinate order templates for bulk gifting campaigns
- Track order status and delivery progress
- Handle order modifications and cancellations
- Coordinate with Platform, Payment, and Scheduler services

### API Endpoints

{List major endpoint categories - reference OpenAPI for full details}

**Example**:
- `POST /orders` - Create gift order
- `GET /orders` - List orders (paginated)
- `GET /orders/{id}` - Get order details
- `PUT /orders/{id}` - Update order
- `DELETE /orders/{id}` - Cancel order
- `GET /orders/{id}/status` - Get delivery status

---

## Scope Boundaries

### What This Service DOES

{Explicit list of what's in scope}

**Example**:
- ✅ Manages gift order lifecycle
- ✅ Schedules future order placement
- ✅ Tracks order status from gifting perspective
- ✅ Validates gift restrictions before placement
- ✅ Provides gift-specific order analytics

### What This Service DOES NOT Do

{Explicit list of what's out of scope - helps prevent scope creep}

**Example**:
- ❌ Does NOT process payments (delegates to Payment service)
- ❌ Does NOT calculate taxes (delegates to Platform service)
- ❌ Does NOT calculate shipping costs (delegates to Platform service)
- ❌ Does NOT manage product inventory (delegates to Platform service)
- ❌ Does NOT send notifications (delegates to Notification service)
- ❌ Does NOT manage user accounts (delegates to User service)

**Rationale**: {Why these boundaries exist - usually references ADRs}

**Example**: Order service delegates all standard commerce operations to Platform service per ADR-040 (Adaptive Platform Proxy Architecture). This prevents duplicating complex commerce logic and maintains single source of truth.

---

## Architecture Pattern

**Type**: {Event-Sourced / Reference Data / Delegation / Hybrid}

**Pattern Description**:
{Explain the architectural approach for this service}

**Example - Event Sourced**:
- **CQRS**: Commands via OrderActor (write), queries via OrderRepository (read)
- **Event Sourcing**: OrderActor persists immutable events to event_journal
- **Cluster Sharding**: Distributed actor instances for horizontal scaling
- **Eventual Consistency**: Events projected to orders table for fast queries

**Example - Reference Data**:
- **Stateless REST**: Simple CRUD operations via Pekko HTTP routes
- **JPA Persistence**: Direct database access via JPA repositories
- **No Actor Model**: No event sourcing needed for reference data
- **Immediate Consistency**: Synchronous database updates

**Example - Delegation**:
- **Adapter Pattern**: Translates gifting operations to platform API calls
- **Circuit Breaker**: Protects against platform unavailability
- **Retry Logic**: Exponential backoff for transient failures
- **Response Translation**: Maps platform responses to gifting domain

---

## Dependencies

### Platform Service Dependency

**Coordination Level**: {High 70%+ / Medium 40-70% / Low 20-40% / None 0%}

{Explain how this service interacts with Platform service}

**Example - High (70%+)**:
Order service coordinates gift order creation but delegates:
- Order placement → Platform.createOrder()
- Order status → Platform.getOrderStatus()
- Order cancellation → Platform.cancelOrder()

**Example - None (0%)**:
Occasion service is self-contained reference data with no platform dependencies.

### Service-to-Service Dependencies

{List which other services this one depends on}

**Example**:
- **Platform** (70%): Order placement, status tracking, cancellation
- **Payment** (40%): Payment authorization before order placement
- **Scheduler** (30%): Schedule future order placement
- **User** (20%): Validate user and recipient IDs
- **Catalog** (10%): Check gift restrictions before placement
- **Notification** (10%): Send order confirmation emails

### External Dependencies

{List external systems/services beyond the 12 microservices}

**Example**:
- PostgreSQL 16 (database)
- Keycloak (OAuth2 authentication)
- Prometheus (metrics)
- OpenTelemetry (tracing)

---

## Domain Concepts

### Key Entities

{List the main domain objects this service manages}

**Example**:
1. **Order** - Gift order entity (scheduled, recurring, or immediate)
2. **OrderItem** - Individual line item in order
3. **OrderTemplate** - Reusable order configuration
4. **OrderStatus** - Order lifecycle state tracking
5. **DeliverySchedule** - Future delivery date configuration

### Entity Relationships

{Brief description or diagram of how entities relate}

**Example**:
```
Order (1) ─── (N) OrderItem
  ├── orderId: UUID
  ├── userId: UUID
  ├── status: OrderStatus
  └── scheduledDate: Instant

OrderTemplate (1) ─── (N) TemplateItem
  ├── templateId: UUID
  ├── name: String
  └── isRecurring: Boolean
```

### Business Rules

{Key business logic and validation rules}

**Example**:
1. Orders cannot be placed for past dates
2. Recurring orders require minimum 2 deliveries
3. Scheduled orders can be cancelled up to 24 hours before placement
4. Gift restrictions are validated before order placement
5. Order total must match sum of items + tax + shipping

---

## Event Model (for Event-Sourced Services)

### Commands

{List of commands this service handles}

**Example**:
- `CreateOrder` - Initialize new gift order
- `UpdateOrder` - Modify existing order
- `CancelOrder` - Cancel before fulfillment
- `ProcessScheduledOrder` - Execute scheduled order (scheduler trigger)

### Events

{List of events this service emits}

**Example**:
- `OrderCreated` - Order initialized
- `OrderScheduled` - Future delivery scheduled
- `OrderPlaced` - Submitted to platform
- `OrderFulfilled` - Delivery completed
- `OrderCancelled` - Order cancelled

### State Machine

{Describe the entity lifecycle}

**Example**:
```
EMPTY → DRAFT → SCHEDULED → PLACED → FULFILLED
                    ↓           ↓
                CANCELLED ← CANCELLED
```

---

## Database Schema

**Database Name**: `gifting_{service}`  
**Migration Tool**: Flyway  
**Location**: `src/main/resources/db/migration/`

### Primary Tables

{List main database tables}

**Example**:
- `orders` - Main order entity
- `order_items` - Order line items
- `order_templates` - Reusable order configurations
- `order_events` - Event sourcing journal (Pekko Persistence)

### Schema Notes

{Important schema design decisions}

**Example**:
- `orders.scheduled_date` - NULL for immediate orders, timestamp for scheduled
- `orders.recurrence_pattern` - JSONB for flexible recurring patterns
- `orders.platform_order_id` - Foreign key to platform's order system
- Indexes on `user_id`, `status`, `scheduled_date` for common queries

---

## Configuration

### Environment Variables

{List required and optional configuration}

**Example**:
```bash
# Required
DB_URL=jdbc:postgresql://localhost:5432/gifting_order
DB_USER=gifting_order_user
DB_PASSWORD=<secret>

# Optional
PLATFORM_BASE_URL=http://localhost:8080 (default)
SCHEDULER_BASE_URL=http://localhost:8085 (default)
ACTOR_TIMEOUT_SECONDS=5 (default)
MAX_ORDER_ITEMS=50 (default)
```

### Application Configuration

{Key settings in application.conf}

**Example**:
```hocon
order-service {
  http.port = 8081
  actor.timeout = 5s
  scheduler.poll-interval = 1m
  max-order-items = 50
}
```

---

## Performance & Scalability

### Expected Load

{Describe anticipated traffic patterns}

**Example**:
- Peak: 1000 orders/minute during holiday season
- Average: 100 orders/minute
- Database: 10M orders total, 500K active orders

### Scaling Strategy

{How this service scales}

**Example - Event Sourced**:
- Horizontal scaling via Pekko Cluster Sharding
- Actor instances distributed across cluster nodes
- Database read replicas for query-side scaling

**Example - Stateless**:
- Horizontal scaling via load balancer
- Stateless routes allow unlimited instances
- Database connection pooling

---

## Testing Strategy

### Unit Tests

{What gets unit tested}

**Example**:
- Actor command handling
- Business rule validation
- Entity state transitions
- OpenAPI schema validation

### Integration Tests

{What gets integration tested}

**Example**:
- HTTP endpoint responses
- Database persistence
- Service-to-service calls
- Error handling scenarios

### Test Location

`src/test/java/{package}/`

---

## Monitoring & Observability

### Health Checks

{Health check endpoints}

**Example**:
- `GET /health` - Service health (liveness)
- `GET /health/ready` - Ready to accept traffic (readiness)
- `GET /health/startup` - Startup completion check

### Metrics

{Key metrics this service exposes}

**Example**:
- `order_creation_total` - Total orders created
- `order_creation_duration_seconds` - Order creation latency
- `scheduled_order_execution_total` - Scheduled orders processed
- `platform_api_calls_total` - Platform API invocations
- `database_query_duration_seconds` - DB query latency

### Logs

{Important log events}

**Example**:
- Order created (INFO)
- Order placed to platform (INFO)
- Platform API failure (ERROR)
- Actor timeout (WARN)
- Database connection failure (ERROR)

---

## Governance Compliance

### Architecture Decision Records

{List relevant ADRs this service implements}

**Example**:
- ADR-010: Apache Pekko Actor Model Architecture
- ADR-030: PostgreSQL Database Selection
- ADR-040: Adaptive Platform Proxy Architecture
- ADR-050: Richardson Level 3 REST API

### Policies

{List relevant POLs this service follows}

**Example**:
- POL-020: API Cross-Endpoint Consistency
- POL-070: Error Handling and Logging Standards
- POL-110: Reactive Design Principles

### Process Documents

{List relevant PDRs}

**Example**:
- PDR-010: Mill Build System
- PDR-050: Database Migration Process

---

## Known Limitations & Future Work

### Current Limitations

{What this service doesn't do yet but might in future}

**Example**:
- No support for partial order cancellation
- No order modification after placement
- No bulk order import via CSV
- Actor persistence not implemented (in-memory only)

### Roadmap

{Planned improvements}

**Example**:
- Phase 2: Implement Pekko Persistence for actor durability
- Phase 3: Add bulk order import endpoint
- Phase 4: Support order modification workflow
- Phase 5: Implement order splitting for large campaigns

---

## References

### Documentation

- OpenAPI Spec: `src/main/resources/openapi.yaml`
- Global Charter: `/CHARTER.md`
- Alignment Report: `/doc/services/{service}/alignment-report.md`

### Related Services

{List services this one commonly interacts with}

**Example**:
- Platform Service (order placement)
- Payment Service (payment authorization)
- Scheduler Service (future order execution)
- Notification Service (order confirmations)

---

**Last Updated**: {date}  
**Author**: RETISIO Engineering  
**Review Cycle**: Quarterly per PDR-060
