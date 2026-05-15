# Acceptance Criteria: Goto Function

**Feature**: Goto Function with Popup Validation (v3.0.0 - Feature 5 of 10)
**Date**: 2025-12-29
**Status**: Pending Approval

---

## User Story

**As a** presenter
**I want to** press G to open a goto popup and jump to a specific slide number
**So that** I can quickly navigate to any slide during Q&A or non-linear presentations

---

## Acceptance Criteria

### AC-1: Goto Popup Activation with G Key
**When** I press the 'G' key
**Then** the goto popup opens with an empty input field
**And** the input field has focus (can type immediately)
**And** break mode is NOT active

**Edge Case**: G key is case-insensitive ('g' and 'G' both work)

**BDD Scenarios**: goto-function.feature (3 scenarios)

---

### AC-2: Real-Time Input Validation (Numeric)
**Given** the goto popup is open
**When** I type "abc"
**Then** the error message displays: "Please enter a valid number"
**And** the Enter key is blocked (no navigation)

**BDD Scenarios**: goto-function.feature (1 scenario)

---

### AC-3: Real-Time Input Validation (Range)
**Given** total slides is 42
**When** I type "43"
**Then** the error message displays: "Slide number must be between 1 and 42"

**When** I type "0"
**Then** the error message displays: "Slide number must be between 1 and 42"

**BDD Scenarios**: goto-function.feature (4 scenarios)

---

### AC-4: Navigation with Enter Key (1-Indexed to 0-Indexed)
**Given** total slides is 42
**When** I type "25" and press Enter
**Then** I navigate to slide 24 (0-indexed internally)
**And** the popup closes
**And** the timer resumes

**Mapping**: `internalIndex = userInput - 1`

**BDD Scenarios**: goto-function.feature (3 scenarios)

---

### AC-5: Dismiss Popup with Escape Key
**Given** the goto popup is open
**When** I type "25" and press Escape
**Then** the popup closes WITHOUT navigating
**And** I remain on the current slide
**And** the timer resumes

**BDD Scenarios**: goto-function.feature (1 scenario)

---

### AC-6: Timer Pauses During Goto Popup
**Given** the timer is at 00:05:30
**When** I press 'G'
**Then** the timer pauses at 00:05:30

**Given** the popup is open for 10 seconds
**When** I press Enter or Escape
**Then** the timer resumes from 00:05:30 (popup time excluded)

**BDD Scenarios**: goto-function.feature (2 scenarios)

---

### AC-7: Enter Key Blocked for Invalid Input
**Given** I type "abc" (invalid)
**When** I press Enter
**Then** the error is shown
**And** the popup stays open
**And** NO navigation occurs

**BDD Scenarios**: goto-function.feature (1 scenario)

---

### AC-8: Goto Clears Forward History
**Given** I have forward history [15, 20]
**When** I goto slide 25
**Then** the forward stack is cleared
**And** pressing 'N' advances linearly (not redo)

**BDD Scenarios**: goto-function.feature (1 scenario)

---

### AC-9: Session Logging Integration
**When** I press 'G' at 14:30:15
**Then** event logged: `{"timestamp": "2025-12-29T14:30:15Z", "key": "G", "action": "goto_popup_opened"}`

**When** I goto slide 25
**Then** slide visit logged with navigationMethod: "Goto"

**BDD Scenarios**: goto-function.feature (3 scenarios)

---

### AC-10: Cross-Window Synchronization
**Given** speaker view is open
**When** I goto slide 25 in main view
**Then** speaker view navigates to slide 25

**Note**: Popup UI does NOT sync (main view only).

**BDD Scenarios**: goto-function.feature (2 scenarios)

---

### AC-11: Goto Disabled During Break Mode
**Given** break mode is active
**When** I press 'G'
**Then** the popup does NOT open
**And** error message: "Goto is disabled during break mode"

**BDD Scenarios**: goto-function.feature (2 scenarios)

---

### AC-12: Focus Management
**When** the popup opens
**Then** the input field receives focus

**When** the popup closes (Enter or Escape)
**Then** focus returns to the main presentation

**BDD Scenarios**: goto-function.feature (1 scenario)

---

### AC-13: ARIA Attributes for Accessibility
**Then** popup has role="dialog"
**And** popup has aria-modal="true"
**And** input has aria-label="Enter slide number"

**BDD Scenarios**: goto-function.feature (1 scenario)

---

## Definition of Done

- [ ] All 13 acceptance criteria implemented
- [ ] 25 BDD scenarios passing
- [ ] Domain model: GotoPopup aggregate
- [ ] UI: Modal popup with input validation
- [ ] Timer integration working
- [ ] Documentation updated

**Approval**:
- Product Owner: ________________ Date: ________
