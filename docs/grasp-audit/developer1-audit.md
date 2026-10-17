# GRASP Audit - Developer 1

**Member:** Sherly Michell Corrales Maestre
**Role:** Developer 1 - Products, model hierarchies, exceptions and validators
**Audited branch / commit:** `develop` @ `81ea350`

## 1. Scope

Classes and methods audited, by module and requirement:

- **Products (Workshop 1)**
    - `Product`: constructor, getters, `setAvailableQuantity`,
      `getDescription` (abstract).
    - `VideoGame`: constructor, `getDescription`.
    - `Console`: constructor, `getDescription`.
    - `ProductRepository`: constructor, `save`, `findAll`, `findByIdentifier`,
      `loadProducts`, `saveProducts`.
    - `ProductService`: constructor, `registerVideoGame`, `registerConsole`,
      `listProducts`, `updateStock`.
- **Accessories (Requirement 1)**
    - `Accessory`, `Controller`, `Cable`, `Memory`: constructors,
      getters/setters, `getDescription`.
- **Promotions (Requirement 2)**
    - `Promotion`: constructor, getters/setters, `isActive`,
      `validateDateRange`, `calculateDiscount` (abstract).
    - `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`:
      constructors, setters with validation, `calculateDiscount`.
- **Returns (Requirement 3, Requirement 5 A5)**
    - `Return`: constructors, `validate*` methods, getters,
      `calculateRefundAmount` (proportional discount part),
      `generateReturnReceipt`.
    - `Sale`: `canBeReturned` only.
- **Warranties (Requirement 4)**
    - `Warranty`, `BasicWarranty`, `ExtendedWarranty`: constructor, getters,
      `isActive`, `generateWarrantyCertificate`, `getDurationInMonths`,
      `getWarrantyType`, `getAdditionalCost`.
- **Exceptions (Requirement 6)**
    - `GameZoneException`, `ResourceNotFoundException`,
      `BusinessRuleException`, `InvalidDataException`,
      `PersistenceException`: constructors, `buildMessage`, `getErrorCode`.
- **Validation (Requirement 6)**
    - `ProductValidator`, `PersonValidator`, `SaleValidator`:
      all static methods.

**Not audited here:**

- `ProductService.restoreStock` belongs to the Technical Lead
  (Requirement 3).
- The warranty-refund part of `Return` (second constructor,
  `validateWarrantyRefundAmount` and the `warrantyRefundAmount` term) was
  added by Developer 2 in Requirement 5 (A7, commit `a258d82`).
- The use of exceptions and validators inside `ProductService` and
  `ProductRepository` was written by Developer 2 in Requirement 6
  (commits `6afd385`, `1d06943`).

Developer 2 audits the last two items.

---

## 2. Information Expert

**Verdict: Violation**

Correct applications:

- `Product` owns and exposes its identifier, price and stock.
- `Sale.canBeReturned()` (lines 85-94) uses the sale date that `Sale` owns.
- `ExtendedWarranty.getAdditionalCost()` (line 32) computes 10 % of the
  price of its own product.
- `Promotion.isActive(date)` (lines 137-141) uses its own date range.

### V-01

- **Pattern:** Information Expert.
- **Module:** Returns (Requirement 3), proportional refund
  (Requirement 5, A5).
- **Evidence:** `Return.calculateRefundAmount`, lines 208-221, reads
  `originalSale.calculateSubtotal()` and `originalSale.getDiscountAmount()`
  and computes `saleDiscount * (returnedSubtotal / saleSubtotal)`.
  `Return.generateReturnReceipt`, lines 266-273, reads the same two values
  again and computes `discountRate = saleDiscount / saleSubtotal`.
- **Explanation:** the share of the discount that corresponds to an amount
  depends only on the subtotal and the discount of the sale, which `Sale`
  owns. `Return` pulls those values out and does the calculation twice,
  with two different formulas.
- **Consequence:** if the way a sale stores or applies its discount changes
  (several promotions, per-item discounts), both copies in `Return` must
  change and can drift apart, so the receipt could show a different value
  from the stored refund.
- **Proposed solution:** `Sale` exposes `getDiscountRate()` (discount divided
  by subtotal, 0 when there is no subtotal) and
  `calculateProportionalDiscount(amount)` (0 when there is no discount,
  otherwise `discountAmount * (amount / subtotal)`). `Return` calls them.
  The formulas keep the same order of operations, so the printed and stored
  values do not change. Pattern applied: Information Expert.
- **Other member's code:** yes - `Sale` (Technical Lead).

## 3. Creator

**Verdict: Correct**

- `ProductRepository.loadProducts()` (lines 76-102) creates the product
  list from the serialized file, so it holds the initializing data.
- The `Warranty` constructor (lines 24-61) calculates its own `endDate` from
  the data it receives.
- `Return` copies the returned products into its own list (line 74), so it
  owns what it aggregates.
- Validators and exceptions create no domain objects.

## 4. Controller

**Verdict: Correct**

`ProductService` is the use-case controller for products. The UI delegates
"register video game", "register console" and "list products" to it
(`registerVideoGame`, `registerConsole`, `listProducts`, lines 44-92).
No model class, validator or exception receives system events directly.
  