# Developer 2 AI Log

## Member

**Name:** Diego Armando Mestre Gomez  
**Role:** Developer 2 — People

## AI Usage

### 1. Person model review
**Purpose:** Review the structure of the people domain model.

**AI assistance:** Used AI to review the abstract `Person` class and the `Customer` and `Seller` specializations.

**Student decision:** The inheritance structure was kept consistent with the project class diagram.

---

### 2. Encapsulation and attributes
**Purpose:** Verify the visibility and access methods of the people classes.

**AI assistance:** Used AI to review private attributes, constructors, getters, and setters.

**Student decision:** The classes use private attributes and only the accessors required by the project design.

---

### 3. Person persistence
**Purpose:** Review how customers and sellers are stored and loaded.

**AI assistance:** Used AI to review the repository persistence logic using text files.

**Student decision:** `PersonRepository` is responsible for saving and loading customers and sellers.

---

### 4. Customer and seller registration
**Purpose:** Review the business operations for people.

**AI assistance:** Used AI to review customer registration, customer listing, and seller listing.

**Student decision:** People operations are handled through `PersonService` instead of being accessed directly from the UI.

---

### 5. Preloaded sellers
**Purpose:** Verify the required initial sellers.

**AI assistance:** Used AI to review the initialization behavior when the seller data file does not exist.

**Student decision:** The system initializes and persists three sellers so they are available when the application starts.

---

### 6. R5 - Warranty repository dependency decoupling

**Date:** 2026-09-27  
**Tool:** ChatGPT  
**Phase/Branch:** R5 - `feature/warranty-repository-decoupling`

**Purpose:** Implement A2 to remove the circular dependency involving `WarrantyRepository`, `WarrantyService`, and `SaleService`.

**AI query:** Reviewed the R5 A2 requirements and requested step-by-step guidance to decouple `WarrantyRepository`, move reference resolution to `WarrantyService`, update `Main`, and document the new dependencies.

**AI response summary:** The assistant guided the modification of `WarrantyRepository` so it persists identifiers only, the injection of `SaleRepository` and `ProductService` into `WarrantyService`, the corresponding `Main` construction changes, and the creation of `docs/warranty-class-diagram.md`.

**Student decision:** The proposed architecture was reviewed and implemented. The project was verified with `mvn clean test`, which completed successfully.

**Related commit:** `refactor: decouple warranty repository dependencies`

---

### 7. R5 - Return accessory integration

**Date:** 2026-09-27  
**Tool:** ChatGPT  
**Phase/Branch:** R5 - `feature/return-accessory-integration`

**Purpose:** Implement A4 so returns support accessories, restore accessory inventory, and resolve accessories when persisted returns are loaded.

**AI query:** Reviewed the R5 A4 requirements and requested step-by-step guidance to add accessory stock restoration, inject `AccessoryService` into `ReturnService` and `ReturnRepository`, update `Main`, and organize the return stock restoration logic.

**AI response summary:** The assistant guided the implementation of `AccessoryService.restoreStock`, the integration of `AccessoryService` into `ReturnService`, the resolution of accessories in `ReturnRepository`, and the corresponding dependency injection changes in `Main`. The return stock restoration logic was also extracted into a dedicated method in `ReturnService`.

**Student decision:** The proposed changes were reviewed and implemented. The project was verified with `mvn clean test`, which completed successfully.

**Related commits:**
- `feat: restore accessory stock on returns`
- `refactor: restore stock by returned item type`
- `refactor: resolve accessories when loading returns`
- `refactor: inject accessory service into returns`
- `refactor: centralize return stock restoration`
- `docs: update developer 2 AI usage log`

## Final Reflection

AI was used as a support tool to review the people model, persistence, validation, service logic, repository dependencies, and return integration. The final implementation decisions were made by the student and verified through project testing.