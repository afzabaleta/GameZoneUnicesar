# Developer 1 AI Log

## Member

**Name:** Sherly Michell Corrales Maestre  
**Role:** Developer 1 — Products

## AI Usage

### 1. Product model review
**Purpose:** Review the structure of the product model.

**AI assistance:** Used AI to review the abstract `Product` class and the `VideoGame` and `Console` specializations.

**Student decision:** The product hierarchy was kept consistent with the project class diagram.

---

### 2. Product inheritance and abstraction
**Purpose:** Verify inheritance and overridden behavior.

**AI assistance:** Used AI to review the use of inheritance and the `getDescription()` method in the product subclasses.

**Student decision:** `VideoGame` and `Console` override the required product behavior.

---

### 3. Product persistence
**Purpose:** Review the storage and loading of products.

**AI assistance:** Used AI to analyze the persistence implementation and identify problems related to saving and loading product information.

**Student decision:** Product persistence was implemented using files so product information remains available between executions.

---

### 4. Product service
**Purpose:** Review the business operations related to products.

**AI assistance:** Used AI to review product registration, product listing, and stock update operations.

**Student decision:** Product operations are handled through `ProductService`, which uses `ProductRepository`.

---

### 5. Stock validation
**Purpose:** Verify valid stock values.

**AI assistance:** Used AI to review the validation of stock quantities and identify possible invalid values.

**Student decision:** Negative stock quantities are rejected.

---

---

### 6. R5 - Accessory category discount integration

**Date:** 2026-09-27  
**Tool:** ChatGPT  
**Phase/Branch:** R5 - `feature/accessory-category-discount`

**Purpose:** Implement A1, adding support for `ACCESSORY` in category promotions.

**AI query:** Reviewed the R5 integration requirements and requested step-by-step guidance to modify `CategoryDiscount`, `PromotionService`, `GameZoneUI`, and `data/promotions.csv`.

**AI response summary:** The assistant guided the implementation of accessory category support, validation of the `ACCESSORY` category, the promotion menu update, and the addition of an accessory category promotion.

**Student decision:** The proposed changes were reviewed and implemented. The project was verified with `mvn clean test`, which completed successfully.

**Related commit:** `feat: support accessory category discounts`
## Final Reflection

AI was used as a support tool to review the product model, persistence, service logic, and validation rules. The final implementation decisions were made by the student and verified through project testing.