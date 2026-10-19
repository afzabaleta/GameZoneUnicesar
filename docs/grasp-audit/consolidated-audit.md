# Consolidated GRASP Audit - GameZone Unicesar

**Consolidated by:** Andres Felipe Zabaleta Diaz (Technical Lead)
**Audited commit:** `develop` @ `81ea350` (merge of PR #39, 2026-10-12)
**Source reports:** `leader-audit.md`, `developer1-audit.md`,
`developer2-audit.md`

This document is the mandatory reference for Phase 2: the team corrects
exactly the violations listed in section 4.

## 1. Consolidation process

1. **Coverage:** the 43 classes of `src/main/java/com/gamezone` were listed
   and matched against the scope of each report (section 2). Methods that
   one member added to another member's class were assigned to their
   author, following the commit history (`git log -S`, `git blame`).
2. **Evidence check:** every violation was checked against `develop` at the
   audited commit (class, method and line numbers).
3. **Findings returned to their author:** the first version of
   `developer2-audit.md` (PR #41) had three findings without line numbers
   (V-01, V-04, V-08), registered the same `ReturnService` fragment four
   times (as V-05, V-09, V-10 and V-11) and had lost the `SaleService`
   validation finding of Requirement 6. Question 4 of `grasp-analysis.md`
   did not answer the question. These points were reported to Developer 2
   and, with his agreement, corrected by the Technical Lead in this pull
   request: the evidence lines were restored, the fragment is registered
   once (V-05, referenced from Indirection and Protected Variations), the
   original V-09 was restored and question 4 now answers the
   non-regression strategy.
4. **Boundary unification:** findings reported from two sides, or whose fix
   needs a class of another member, were merged into one violation with the
   parts split by owner (section 5).
5. **Team verdict:** a pattern is a team violation if at least one confirmed
   violation exists for it. A finding that affects two patterns is
   registered once, under the pattern that guides its fix, and the other
   pattern's section references it.

Naming note: the requirement calls the console class `ConsoleMenu`; in this
repository it is `com.gamezone.ui.GameZoneUI`.

## 2. Coverage table

| # | Package | Class | Audited by | Notes |
|---|---|---|---|---|
| 1 | (root) | `Main` | Technical Lead | |
| 2 | model | `Sale` | Technical Lead | `canBeReturned` by Developer 1 |
| 3 | persistence | `SaleRepository` | Technical Lead | Req. 6 wrapping by Developer 2 |
| 4 | service | `SaleService` | Technical Lead | Req. 6 lines 97-120 by Developer 2 |
| 5 | ui | `GameZoneUI` (`ConsoleMenu`) | Technical Lead | |
| 6 | model | `Product` | Developer 1 | |
| 7 | model | `VideoGame` | Developer 1 | |
| 8 | model | `Console` | Developer 1 | |
| 9 | persistence | `ProductRepository` | Developer 1 | Req. 6 wrapping by Developer 2 |
| 10 | service | `ProductService` | Developer 1 | `restoreStock` by Technical Lead |
| 11 | model | `Accessory` | Developer 1 | |
| 12 | model | `Controller` | Developer 1 | |
| 13 | model | `Cable` | Developer 1 | |
| 14 | model | `Memory` | Developer 1 | |
| 15 | model | `Promotion` | Developer 1 | |
| 16 | model | `PercentageDiscount` | Developer 1 | |
| 17 | model | `CategoryDiscount` | Developer 1 | |
| 18 | model | `BulkPurchaseDiscount` | Developer 1 | |
| 19 | model | `Return` | Developer 1 | warranty-refund part (A7) by Developer 2 |
| 20 | model | `Warranty` | Developer 1 | |
| 21 | model | `BasicWarranty` | Developer 1 | |
| 22 | model | `ExtendedWarranty` | Developer 1 | |
| 23 | exceptions | `GameZoneException` | Developer 1 | |
| 24 | exceptions | `ResourceNotFoundException` | Developer 1 | |
| 25 | exceptions | `BusinessRuleException` | Developer 1 | |
| 26 | exceptions | `InvalidDataException` | Developer 1 | |
| 27 | exceptions | `PersistenceException` | Developer 1 | |
| 28 | validation | `ProductValidator` | Developer 1 | |
| 29 | validation | `PersonValidator` | Developer 1 | |
| 30 | validation | `SaleValidator` | Developer 1 | |
| 31 | model | `Person` | Developer 2 | |
| 32 | model | `Customer` | Developer 2 | |
| 33 | model | `Seller` | Developer 2 | |
| 34 | persistence | `PersonRepository` | Developer 2 | |
| 35 | service | `PersonService` | Developer 2 | |
| 36 | persistence | `AccessoryRepository` | Developer 2 | |
| 37 | service | `AccessoryService` | Developer 2 | |
| 38 | persistence | `PromotionRepository` | Developer 2 | |
| 39 | service | `PromotionService` | Developer 2 | |
| 40 | persistence | `ReturnRepository` | Developer 2 | |
| 41 | service | `ReturnService` | Developer 2 | includes the monthly balance |
| 42 | persistence | `WarrantyRepository` | Developer 2 | includes `WarrantyRecord` |
| 43 | service | `WarrantyService` | Developer 2 | |

Result: 43 of 43 classes audited. The shared classes (`Sale`,
`SaleService`, `ProductService`, `Return` and the three original
repositories) were split by method, so no method was audited twice.

## 3. Team verdict per pattern

- **Information Expert - Violation.** V-04, V-05, V-06, V-07, V-08, V-11.
  (Leader: violation; Developer 1: violation; Developer 2: violation.)
- **Creator - Violation.** V-14. (Leader: violation; Developer 1 and
  Developer 2: correct.)
- **Controller - Correct.** `GameZoneUI` only receives console events and
  delegates each system operation to one service. The UI decisions that
  should not be there are classified under Creator (V-14) and Information
  Expert (V-07, V-08).
- **Low Coupling - Violation.** V-01, V-16. (Leader and Developer 2:
  violation; Developer 1: correct.)
- **High Cohesion - Violation.** V-15; V-06 and V-12 are referenced.
- **Polymorphism - Violation.** V-02, V-03, V-09, V-10.
- **Pure Fabrication - Violation.** V-12, V-17. (Developer 2: violation;
  Leader and Developer 1: correct.)
- **Indirection - Correct.** Services stand between the UI and the
  repositories, validators between services and exceptions, and
  `WarrantyRecord` decouples the warranty file from the model. Indirection
  is the tool used to fix V-01 and V-16.
- **Protected Variations - Violation.** V-13; V-02, V-04, V-09 and V-10 are
  referenced.

## 4. Definitive list of violations

Each violation shows: pattern; evidence at `81ea350`; agreed solution;
origin report; correction owner(s).

### V-01 - Low Coupling

- **Evidence:** `ReturnRepository` imports and stores `SaleService`,
  `ProductService` and `AccessoryService` (lines 7-9, 27-29) and calls them
  at lines 279, 304 and 314. Persistence depends on the service layer.
- **Solution:** `ReturnRepository` reads and writes a `ReturnRecord` with ids
  only; `ReturnService` resolves the sale and the items through
  `SaleService.findSaleByReference` and `InventoryService.findItem`, with the
  same messages.
- **Origin:** developer2-audit V-04.
- **Owner:** Diego (`ReturnRepository`, `ReturnService`); Andres (`Main`).

### V-02 - Polymorphism

- **Evidence:** `SaleService` duplicated pairs split by `instanceof
  Accessory` (lines 289-329, 337-403, 411-469, 478-509);
  `GameZoneUI.findItemById` (1944-1967); `ReturnService.restoreReturnedStock`
  (187-208) and `findProduct` (432-458).
- **Solution:** new Pure Fabrication `InventoryService` (`findItem`,
  `validateStock`, `decreaseStock`, `restoreStock`) that keeps the single
  type decision; the three classes use it.
- **Origin:** leader-audit V-06 and developer2-audit V-05.
- **Owner:** Andres (`InventoryService`, `SaleService`, `GameZoneUI`,
  `Main`); Diego (`ReturnService`).

### V-03 - Polymorphism

- **Evidence:** `instanceof Console` as the warranty rule in
  `SaleService.assignWarranties` (162),
  `GameZoneUI.selectExtendedWarrantyProducts` (1718) and
  `ReturnService.cancelReturnedConsoleWarranties` (169).
- **Solution:** `Product.isWarrantyEligible()` (`false` by default),
  overridden in `Console`.
- **Origin:** leader-audit V-07 and developer2-audit V-06.
- **Owner:** Sherly (`Product`, `Console`); Andres (`SaleService`,
  `GameZoneUI`); Diego (`ReturnService`).

### V-04 - Information Expert

- **Evidence:** `sale.getDate().toString()` used as the sale reference in
  `ReturnRepository` (281-283, 335-338), `WarrantyRepository` (153),
  `ReturnService` (262-265, 413-415) and `WarrantyService` (169-172,
  226-229, 410-412).
- **Solution:** `Sale.getReference()`; the eight places call it.
- **Origin:** developer2-audit V-01.
- **Owner:** Andres (`Sale`); Diego (four classes).

### V-05 - Information Expert

- **Evidence:** `Return.calculateRefundAmount` (208-221) and
  `generateReturnReceipt` (266-273) compute the discount share from `Sale`
  data, twice.
- **Solution:** `Sale.getDiscountRate()` and
  `Sale.calculateProportionalDiscount(amount)`, keeping the order of
  operations; `Return` uses them.
- **Origin:** developer1-audit V-01.
- **Owner:** Andres (`Sale`); Sherly (`Return`).

### V-06 - Information Expert

- **Evidence:** `ReturnService.calculateMonthlySales` (281-300) adds up
  sales.
- **Solution:** `SaleService.calculateMonthlySales(month, year)`;
  `ReturnService` keeps its validation and delegates.
- **Origin:** developer2-audit V-02.
- **Owner:** Andres (`SaleService`); Diego (`ReturnService`).

### V-07 - Information Expert

- **Evidence:** `GameZoneUI.findCustomerById` (1904-1916) and
  `findSellerById` (1924-1936).
- **Solution:** `PersonService.findCustomerById` / `findSellerById`
  (return `null` when absent).
- **Origin:** leader-audit V-01.
- **Owner:** Diego (`PersonService`); Andres (`GameZoneUI`).

### V-08 - Information Expert

- **Evidence:** `GameZoneUI.getAvailableConsoles` (1388-1401,
  `instanceof Console` at 1396).
- **Solution:** `ProductService.listConsoles()`.
- **Origin:** leader-audit V-02.
- **Owner:** Sherly (`ProductService`); Andres (`GameZoneUI`).

### V-09 - Polymorphism

- **Evidence:** `CategoryDiscount.calculateDiscount` (107-131) uses
  `instanceof VideoGame/Console/Accessory`.
- **Solution:** abstract `Product.getCategory()` implemented in
  `VideoGame`, `Console` and `Accessory`; comparison by value.
- **Origin:** developer1-audit V-02.
- **Owner:** Sherly.

### V-10 - Polymorphism

- **Evidence:** `AccessoryRepository.findByType` (79-93) uses
  `instanceof Controller/Cable/Memory`.
- **Solution:** abstract `Accessory.getAccessoryType()`; `findByType` and
  the type column of `toCsv` use it.
- **Origin:** developer2-audit V-07.
- **Owner:** Sherly (`Accessory` hierarchy); Diego (`AccessoryRepository`).

### V-11 - Information Expert

- **Evidence:** `PromotionService.registerCategoryDiscount` (83-91)
  duplicates `CategoryDiscount.setTargetCategory` (76-84).
- **Solution:** static `CategoryDiscount.validateTargetCategory(String)`
  used by the setter and by the service at the same point.
- **Origin:** developer2-audit V-03.
- **Owner:** Sherly (`CategoryDiscount`); Diego (`PromotionService`).

### V-12 - Pure Fabrication

- **Evidence:** `AccessoryService.validateAccessory` (223-270).
- **Solution:** new `AccessoryValidator` in package `validation` with the
  same checks, order, `IllegalArgumentException` and messages.
- **Origin:** developer2-audit V-08.
- **Owner:** Diego.

### V-13 - Protected Variations

- **Evidence:** `ProductService.registerVideoGame` (44-60) and
  `registerConsole` (67-83) are identical.
- **Solution:** `registerProduct(Product)`; the two methods delegate.
- **Origin:** developer1-audit V-03.
- **Owner:** Sherly.

### V-14 - Creator

- **Evidence:** `GameZoneUI.registerSale` creates
  `new Sale(LocalDate.now(), ...)` (1653-1658).
- **Solution:** `SaleService.registerSale(customer, seller, items,
  extendedWarrantyIds)` creates and returns the `Sale`.
- **Origin:** leader-audit V-03.
- **Owner:** Andres.

### V-15 - High Cohesion

- **Evidence:** `GameZoneUI`: 2,540 lines, seven menus, the same four
  `catch` blocks in 16 operations (e.g. lines 179, 818, 1672, 2480),
  duplicated imports (23-31).
- **Solution:** new Pure Fabrication `ConsoleErrorHandler` and one menu
  class per module in package `ui`; `GameZoneUI` keeps the main menu.
- **Origin:** leader-audit V-05.
- **Owner:** Andres.

### V-16 - Low Coupling

- **Evidence:** `SaleRepository` depends on three repositories (49-69) and
  calls `loadCustomers`, `loadSellers` and `loadProducts` for every record
  (230, 246, 262).
- **Solution:** Indirection: three lookup functions injected by `Main`
  (`PersonService` finders and `InventoryService.findItem`).
- **Origin:** leader-audit V-04.
- **Owner:** Andres.

### V-17 - Pure Fabrication

- **Evidence:** `SaleService.registerSale` (108-120) re-validates products
  after `SaleValidator`.
- **Solution:** the blank-identifier rule moves into `SaleValidator` (same
  message) and the loop is removed.
- **Origin:** developer2-audit V-09.
- **Owner:** Sherly (`SaleValidator`); Diego (`SaleService`, Req. 6 part).

### Totals and new classes

17 violations: 11 boundary findings (section 5) and 6 single-owner
findings (V-09, V-12, V-13, V-14, V-15, V-16).

- **Andres:** V-01 (Main), V-02, V-03, V-04, V-05, V-06, V-07, V-08, V-14,
  V-15, V-16.
- **Sherly:** V-03, V-05, V-08, V-09, V-10, V-11, V-13, V-17.
- **Diego:** V-01, V-02, V-03, V-04, V-06, V-07, V-10, V-11, V-12, V-17.

New classes (created by the owner of the originating violation):
`InventoryService` (service, V-02, Andres); `ConsoleErrorHandler` and the
seven module menus (ui, V-15, Andres); `AccessoryValidator` (validation,
V-12, Diego); `ReturnRepository.ReturnRecord` (persistence, V-01, Diego).