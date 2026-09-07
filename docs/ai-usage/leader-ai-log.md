# Leader AI Log

## Member

**Name:** Andres Felipe Zabaleta Diaz  
**Role:** Technical Lead — Sales + Integration

## AI Usage

### 1. Project architecture review
**Purpose:** Verify the proposed layer structure and dependencies.

**AI assistance:** Reviewed the separation between UI, Service, Persistence, and Model layers and checked that dependencies followed the expected direction.

**Student decision:** The team maintained the architecture:
`UI → Service → Persistence → Model`.

---

### 2. Git and branch workflow
**Purpose:** Verify the Git Flow used during the project.

**AI assistance:** Received guidance about feature branches, pull requests, branch cleanup, commit naming, and protected branches.

**Student decision:** The team used `develop` for integration and feature branches for individual work.

---

### 3. Sales module review
**Purpose:** Review the implementation of the sales module.

**AI assistance:** Used AI to review the logic for registering sales, validating available stock, calculating totals, and updating inventory.

**Student decision:** The `SaleService` was implemented using the existing project services and repositories.

---

### 4. Application integration
**Purpose:** Review the connection between repositories, services, `GameZoneUI`, and `Main`.

**AI assistance:** Used AI to identify integration problems and verify dependency injection in the application entry point.

**Student decision:** `Main` creates the repositories and services and injects them into the console UI.

---

### 5. Persistence testing
**Purpose:** Verify that information remains available after restarting the application.

**AI assistance:** Used AI to analyze why registered products were initially lost after restarting the program.

**AI assistance result:** Identified that products were only being stored in memory and that persistence methods were not being called during registration/loading.

**Student decision:** The repository was adjusted so products are loaded when the repository starts and saved when products are registered.

---

### 6. Stock persistence review
**Purpose:** Verify that inventory changes produced by a sale remain after the operation.

**AI assistance:** Used AI to identify that `updateStock()` modified the object in memory without persisting the updated product list.

**Student decision:** The stock update was adjusted so the modified product list is persisted.

---

## Final Reflection

AI was used as a support tool for reviewing architecture, debugging, validating integration, and understanding persistence behavior. The team made the final implementation decisions and tested the application manually through the console.