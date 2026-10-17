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

## 5. Low Coupling

**Verdict: Correct**

- The model hierarchies depend only on other model classes.
  `Promotion.calculateDiscount(Sale)` is the only coupling between
  promotions and sales, and the requirement needs it.
- `ProductRepository` depends only on `Product` and `PersistenceException`.
- Validators depend only on the model and the exceptions package, and hold
  no state (`final` classes with private constructors).
- The exceptions package depends on nothing in the system.

## 6. High Cohesion

**Verdict: Correct**

- Each subclass adds only its own attributes and its own description
  (`Cable`, `Memory`, `Controller`, `VideoGame`, `Console`).
- There is one validator per aggregate (product, person, sale) and one
  exception per error category.

`Return.generateReturnReceipt` and `Warranty.generateWarrantyCertificate`
format text inside the model. This is accepted because Requirements 3 and 4
asked for these methods on these classes. They use only the object's own
data and do no I/O.

## 7. Polymorphism

**Verdict: Violation**

Correct applications:

- `Product.getDescription()` is overridden in every product and accessory.
- `Promotion.calculateDiscount(Sale)` is overridden in the three promotion
  types.
- `Warranty` declares `getDurationInMonths()`, `getWarrantyType()` and
  `getAdditionalCost()` as abstract, and each subclass answers for itself.
- The exception hierarchy lets the UI catch errors by category.

### V-02

- **Pattern:** Polymorphism (also Protected Variations).
- **Module:** Promotions (Requirement 2), category for accessories
  (Requirement 5, A1).
- **Evidence:** `CategoryDiscount.calculateDiscount`, lines 107-131: three
  `if` blocks that combine the category string with
  `product instanceof VideoGame`, `instanceof Console` and
  `instanceof Accessory`. `CategoryDiscount.setTargetCategory`, lines 76-84,
  repeats the closed list `"VIDEOGAME"`, `"CONSOLE"`, `"ACCESSORY"`.
- **Explanation:** the category of a product is a property of the product.
  Instead of asking the product, the promotion inspects its concrete class.
  This is the conditional on type that Polymorphism is meant to remove.
- **Consequence:** adding a new product type (for example `Collectible`)
  requires a new `instanceof` branch in `CategoryDiscount`, a new string in
  its validation, and probably new branches elsewhere. Requirement 5 (A1)
  already had to edit this method for accessories.
- **Proposed solution:** add the abstract method `String getCategory()` to
  `Product`: `VideoGame` returns `"VIDEOGAME"`, `Console` returns
  `"CONSOLE"` and `Accessory` returns `"ACCESSORY"`. `calculateDiscount`
  compares `targetCategory.equals(product.getCategory())`. The valid
  categories stay listed in one place inside `CategoryDiscount`.
- **Other member's code:** no.

## 8. Pure Fabrication

**Verdict: Correct**

- `ProductRepository` keeps file access and serialization out of
  `Product`.
- `ProductValidator`, `PersonValidator` and `SaleValidator` are fabricated
  classes. They centralize validation that belongs to no single domain
  object; they are stateless, `final` and do no I/O, as Requirement 6
  required.

## 9. Indirection

**Verdict: Correct**

The validators sit between the services and the exception types. Services
call `ProductValidator.validateProductExists(...)` and do not build
`ResourceNotFoundException` messages themselves (`ProductValidator`,
lines 52-56). `GameZoneException` is an indirection point: the UI depends
on four categories, not on every place that can fail.

## 10. Protected Variations

**Verdict: Violation**

Correct applications:

- The `Promotion` abstraction protects `SaleService` and `PromotionService`
  from new promotion types.
- The exception hierarchy (`GameZoneException` plus `getErrorCode()`)
  protects the UI from new specific exceptions.

### V-03

- **Pattern:** Protected Variations.
- **Module:** Products (Workshop 1), Requirement 6.
- **Evidence:** `ProductService.registerVideoGame(VideoGame)` (lines 44-60)
  and `ProductService.registerConsole(Console)` (lines 67-83) have identical
  bodies: null check, `ProductValidator.validateProductData(...)` with the
  same four getters, and `productRepository.save(...)`. The only difference
  is the parameter type.
- **Explanation:** registration does not depend on the concrete product
  type, but the service exposes one method per type. The variation point
  (product type) is not protected behind the `Product` abstraction.
- **Consequence:** each new product type needs a copy of the method. A
  change to the registration rule (for example, rejecting duplicate
  identifiers) must be repeated in each copy.
- **Proposed solution:** a single `registerProduct(Product)` with the common
  body. `registerVideoGame` and `registerConsole` delegate to it, so the UI
  and its messages do not change. Pattern applied: Protected Variations
  through the `Product` abstraction.
- **Other member's code:** no.

---

## Summary

- **V-01** - Information Expert - `Return` - other member: Technical Lead
  (`Sale`).
- **V-02** - Polymorphism - `CategoryDiscount` and the `Product`
  hierarchy - other member: none.
- **V-03** - Protected Variations - `ProductService` - other member: none.
  