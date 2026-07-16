## Tier 0: Foundation (No Dependencies)
- occasion-setup-default.feature (Issue #84) - Basic occasion data
- platform-authentication.feature (Issue #84) - Core auth

## Tier 1: Platform Integration Basics (Depends on Tier 0)
- platform-user-mapping.feature (Issue #121) - User data mapping
- platform-catalog-config.feature (Issue #130) - Catalog configuration
- platform-search-delegation.feature (Issue #122) - Basic search
- platform-payment-integration.feature (Issue #126) - Payment methods
- platform-address-book.feature (Issue #132) - Address book
- platform-account-integration.feature (Issue #133) - Account integration

## Tier 2: Core Gifting Features (Depends on Tier 0-1)
- gift-discovery-occasion-search.feature (Issue #94) - Occasion-based search
- gift-selection-workflow.feature (Issue #134) - Selection workflow
- order-processing-regular.feature (Issue #79) - Regular orders
- occasion-delivery-deadlines.feature (Issue #88) - Deadline calculation
- occasion-message-personalization.feature (Issue #87) - Message templates

## Tier 3: Enhanced Discovery (Depends on Tier 2)
- gift-discovery-multi-criteria.feature (Issue #95) - Advanced search
- gift-discovery-inventory.feature (Issue #96) - Inventory integration
- gift-discovery-guided.feature (Issue #97) - Guided selection
- gift-discovery-collections.feature (Issue #98) - Curated collections
- platform-enhanced-search.feature (Issue #129) - Enhanced search

## Tier 4: Advanced Ordering (Depends on Tier 2-3)
- order-processing-scheduled.feature (Issue #78) - Scheduled orders
- order-processing-recurring.feature (Issue #80) - Recurring orders
- order-processing-bulk.feature (Issue #83) - Bulk orders
- order-processing-holiday.feature (Issue #82) - Holiday campaigns
- order-processing-loyalty.feature (Issue #81) - Loyalty gifting

## Tier 5: Templates and Automation (Depends on Tier 4)
- occasion-order-templates.feature (Issue #86) - Order templates
- multi-dimensional-order-templates.feature (Issue #95) - Complex templates
- enhanced-scheduled-order-management.feature (Issue #98) - Enhanced scheduling
- subscription-gifting-engine.feature (Issue #96) - Subscriptions

## Tier 6: Restrictions and Compliance (Depends on Tier 2-3)
- catalog-restrictions-basic.feature (Issue #105) - Basic restrictions
- occasion-product-eligibility.feature (Issue #85) - Eligibility validation
- occasion-catalog-restrictions.feature (Issue #91) - Occasion restrictions
- catalog-restrictions-overrides.feature (Issue #101) - Override handling
- catalog-restrictions-search-patterns.feature (Issue #100) - Search patterns
- catalog-restrictions-compliance.feature (Issue #103) - Compliance validation

## Tier 7: Performance and Analytics (Depends on Tier 6)
- catalog-restrictions-bulk-validation.feature (Issue #104) - Bulk validation
- catalog-restrictions-analytics.feature (Issue #106) - Restriction analytics
- occasion-bulk-validation.feature (Issue #92) - Bulk eligibility
- occasion-analytics.feature (Issue #93) - Occasion analytics

## Tier 8: Progressive Planning (Depends on Tier 2-4)
- progressive-planning-sessions.feature (Issue #65) - Session creation
- progressive-planning-recipient-first.feature (Issue #67) - Recipient management
- progressive-planning-persistence.feature (Issue #68) - Auto-save
- progressive-planning-multi-session.feature (Issue #69) - Multi-session
- progressive-planning-continuity.feature (Issue #70) - Session resumption
- progressive-planning-reminders.feature (Issue #66) - Deadline reminders
- corporate-session-bulk-gifting.feature (Issue #97) - Corporate sessions

## Tier 9: Integration Enablement (Depends on Tier 1-8)
- progressive-enablement-quick-start.feature (Issue #74) - Quick start
- progressive-enablement-tier1.feature (Issue #71) - Tier 1 features
- progressive-enablement-operator.feature (Issue #75) - Operator dashboard
- progressive-enablement-recommendations.feature (Issue #73) - Recommendations
- progressive-enablement-integration.feature (Issue #76) - Integration mgmt
- progressive-enablement-enterprise.feature (Issue #77) - Enterprise features
- progressive-enablement-scale.feature (Issue #72) - Scale features

## Tier 10: Advanced Intelligence (Depends on Tier 3-9)
- gift-discovery-ai-recommendations.feature (Issue #99) - AI recommendations
- platform-recommendation-delegation.feature (Issue #123) - Recommendation delegation
- platform-recommendation-config.feature (Issue #124) - Recommendation config

## Tier 11: Platform Resilience (Depends on all platform features)
- platform-error-handling.feature (Issue #128) - Error handling
- platform-failure-handling.feature (Issue #127) - Failure handling
- platform-property-mapping.feature (Issue #125) - Advanced mapping
- occasion-cross-platform.feature (Issue #89) - Cross-platform

## Tier 12: Client Applications (Depends on all API features)
- customer-web-app.feature (Issue #62) - Customer app
- operator-admin-app.feature (Issue #59) - Operator app
- custom-ui-integration.feature (Issue #63) - Custom UI
- developer-sdk.feature (Issue #61) - SDK

## Feature Processing

1. Identify all the prior organization gifting services required to implement this feature
2. for each service related to this feature, analyze this feature against the service docs impl/<service>/src/main/resources/openapi.yaml 
3. Identify all endpoints required to implement this feature.
4. identify any missing endpoints or services that should be add if there is a gap
5. for each missing endpoint, document the gap in doc/feature-gaps.md cumulatively.
6. Create/update doc/features/<feature-name>.md according to the mardown template below

---

# <FEATURE-NAME> - Technical Documentation

**Feature File**: [<RELATIVE-PATH-TO-FEATURE-FILE>](<RELATIVE-PATH-TO-FEATUE-FILE>)  
**Descriptor File**: [<RELATIVE-PATH-TO-THIS-FILE>](<RELATIVE-PATH-TO-THIS-FILE>)  
**Status**: Todo  
**Last Updated**: <CURRENT-DATE>

## Overview

<PARAGRAPH-DESCRIBING-THIS-FEATURE-SUITABLE-FOR-RFP>
<PARAGRAPH-DESCRIBING-CUSTOMER-VALUE-PROPOSITION>
<PARAGRAPH-DESCRIBING-OPERATOR-VALUE-PROPOSITION>
<PARAGRAPH-DESCRIBING-the prior organization-VALUE-PROPOSITION>

### Applicable ADRs
 <LIST-OF-APPLICABLE-ADRs>
 - [<ADR-NAME>](<PATH-TO-ADR-File>) - <SHORT-ADR-DESCRIPTION>
 - [<ADR-NAME>](<PATH-TO-ADR-File>) - <SHORT-ADR-DESCRIPTION>
 </LIST-OF-APPLICABLE-ADRs>

### Applicable PDRs
 <LIST-OF-APPLICABLE-PDRs>
 - [<PDR-NAME>](<PATH-TO-PDR-File>) - <SHORT-PDR-DESCRIPTION>
 - [<PDR-NAME>](<PATH-TO-PDR-File>) - <SHORT-PDR-DESCRIPTION>
 </LIST-OF-APPLICABLE-PDRs>

### Applicable POLs
 <LIST-OF-APPLICABLE-POLs>
 - [<POL-NAME>](<PATH-TO-POL-File>) - <SHORT-POL-DESCRIPTION>
 - [<POL-NAME>](<PATH-TO-POL-File>) - <SHORT-POL-DESCRIPTION>
 </LIST-OF-APPLICABLE-POLs>


## Platform Delegation & Dependancy

<detailed technical description of data, services, endpoints, processes, actions and/or events with dependancy or delegation on an host commerce system or other external system>

## Sequence Diagrams

<description or all processes (sequences) that comprise this feature>

 <LIST-OF-DIAGRAMS>
  ### <Sequence name>

  <description of sequence>

  <Mermaid diagram with participants limited to user, host-commerce-platform, and 12 retisio gifting services. use endpoints with http verbs and codes for transitions (like POST /api/gifting/order/v1/scheduled → 201). you may use only endpoints documented in swagger and those documented as gaps to do so

  ### <Sequence name>

  <description of sequence>

  <Mermaid diagram with participants limited to user, host-commerce-platform, and 12 retisio gifting services. use endpoints for transition>
 </LIST-OF-DIAGRAMS>

## Implementation Guidance

<A detailed tecnical description of how to best implement this feature to be compliant withall ADRs, PDRs, & Policies>
