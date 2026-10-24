
# GRASP Audit - Developer 2

**Member:** Diego Armando Mestre Gomez
**Role:** Developer 2 - People, persistence and services of accessories, promotions, returns and warranties
**Audited branch / commit:** `develop` @ `81ea350`

## 1. Scope

Classes and methods audited, by module and requirement:

- **People (Workshop 1)**
    - `Person`, `Customer`, `Seller`: constructors, getters/setters, `validateField`.
    - `PersonRepository`: `saveCustomers`, `saveSellers`, `loadCustomers`, `loadSellers`, `createDefaultSellers`, path getters.
    - `PersonService`: constructor, `registerCustomer`, `listCustomers`, `listSellers`.
- **Accessories (Requirement 1)**
    - `AccessoryRepository`: `findAll`, `findByIdentifier`, `findByType`, `findCompatibleWithConsole`, `loadAccessories`, `parseAccessory`, `resolveCompatibleConsoles`, `save`, `saveAll`, `toCsv`.
    - `AccessoryService`: all methods, including `updateStock`, `restoreStock` (Requirement 5, A4) and `validateAccessory`.
- **Promotions (Requirement 2)**
    - `PromotionRepository`: `loadAll`, `saveAll`, `parsePromotion`, `toCsv`.
    - `PromotionService`: all methods, including `findBestPromotionFor`.
- **Returns (Requirement 3, Requirement 5 A4/A6/A7)**
    - `ReturnRepository`: all methods.
    - `ReturnService`: all methods, including the monthly balance (`calculateMonthlySales`, `calculateMonthlyReturns`, `generateMonthlyBalance`) and `cancelReturnedConsoleWarranties`.
    - `Return`: warranty-refund part only (second constructor, `validateWarrantyRefundAmount`, `warrantyRefundAmount` term in `calculateRefundAmount`).
- **Warranties (Requirement 4, Requirement 5 A2)**
    - `WarrantyRepository`: all methods and `WarrantyRecord`.
    - `WarrantyService`: all methods.
- **Exceptions and validations (Requirement 6)**
    - `ProductService`, `PersonService`, `SaleService`: delegation to the validators and use of typed exceptions (`ProductService` lines 28-125, `PersonService` lines 30-85, `SaleService` constructor lines 47-84 and `registerSale` lines 97-120).
    - `ProductRepository`, `PersonRepository`, `SaleRepository`: wrapping of I/O and parsing errors into `PersistenceException`.

## 2. Information Expert

**Verdict: Violation**

### V-01

- **Pattern:** Information Expert (also Protected Variations).
- **Module:** Returns and warranties (Requirements 3 and 4).
- **Evidence:** the reference of a sale is rebuilt as `sale.getDate().toString()` in eight places: `ReturnRepository` lines 281-283 and 335-338; `WarrantyRepository` line 153; `ReturnService` lines 262-265 and 413-415; `WarrantyService` lines 169-172, 226-229 and 410-412.
- - **Explanation:** `Sale` owns the sale date and should be responsible for providing the sale reference. The other classes should not know how that reference is formatted.
- **Consequence:** If the reference format changes, eight places must be updated. Missing one would cause inconsistent references when loading or matching returns and warranties.
- **Proposed solution:** `Sale` exposes `getReference()` (it returns `date.toString()` today). All eight places call it. The file format and the outputs stay the same. Pattern applied: Information Expert; the reference format becomes a protected variation.
- **Other member's code:** yes - `Sale` (Technical Lead).

### V-02

- **Pattern:** Information Expert (also High Cohesion).
- **Module:** Monthly balance (Requirement 3, Requirement 5 A6).
- **Evidence:** `ReturnService.calculateMonthlySales`, lines 281-300, iterates `saleService.listSales()`, filters by month and year and adds `sale.calculateTotal()`.
- **Explanation:** The total sold in a month is information about sales. `SaleService` owns the sales and is the expert. `ReturnService` should only answer for returns and combine both totals in the balance.
- **Consequence:** `ReturnService` changes whenever the sales rule changes (for example, excluding cancelled sales), and the sales total cannot be reused by a future sales report without depending on the returns module.
- **Proposed solution:** add `SaleService.calculateMonthlySales(month, year)` with the same loop. `ReturnService.calculateMonthlySales` keeps its month/year validation and delegates, so the UI and the outputs do not change.
- **Other member's code:** yes - `SaleService` (Technical Lead).

### V-03

- **Pattern:** Information Expert.
- **Module:** Promotions (Requirement 2), Requirement 5 A1.
- **Evidence:** `PromotionService.registerCategoryDiscount`, lines 83-91, checks `targetCategory` against `"VIDEOGAME"`, `"CONSOLE"` and `"ACCESSORY"` and throws the same message as `CategoryDiscount.setTargetCategory` (lines 76-84).
- **Explanation:** `CategoryDiscount` knows which categories are valid. The service keeps a second copy of that list.
- **Consequence:** Requirement 5 (A1) already had to update both lists to add `ACCESSORY`. A new category added to only one of them makes the service reject a category that the model accepts, or the opposite.
- **Proposed solution:** `CategoryDiscount` exposes a static `validateTargetCategory(String)` used by its own setter. `PromotionService` calls it at the same point where it validates today, so the error appears in the same order and with the same message.
- **Other member's code:** yes - `CategoryDiscount` (Developer 1).

## 3. Creator

**Verdict: Correct**

- `PromotionService` creates the promotions it records (`registerPercentageDiscount`, `registerCategoryDiscount`, `registerBulkPurchaseDiscount`).
- `WarrantyService` creates the warranties it registers (`assignBasicWarranty`, `assignExtendedWarranty`).
- `ReturnService.registerReturn` creates the `Return` it persists (lines 131-139).
- `PersonRepository.createDefaultSellers` (lines 182-188) creates the preloaded sellers.

The `if/else` on the stored type in `WarrantyService.loadWarranties` (lines 342-368) and the `switch` in `parseAccessory` and `parsePromotion` are the factory step that turns a stored type code into an object. They are accepted as correct: at that point there is no object yet to ask polymorphically.

## 4. Controller

**Verdict: Correct**

`PersonService`, `AccessoryService`, `PromotionService`, `ReturnService` and `WarrantyService` each receive the operations of their submenu from the UI and coordinate them (for example, `ReturnService.registerReturn` validates, cancels warranties, restores stock and persists). None of my classes reads from the console.

## 5. Low Coupling

**Verdict: Violation**

### V-04

- **Pattern:** Low Coupling.
- **Module:** Returns (Requirement 3), Requirement 5 A4.
- **Evidence:** `ReturnRepository` imports `SaleService`, `ProductService` and `AccessoryService` (lines 7-9), keeps them as fields (lines 27-29) and calls `saleService.listSales()` (line 279), `productService.listProducts()` (line 304) and `accessoryService.listAccessories()` (line 314).
- **Explanation:** A repository is a persistence component. Depending on services reverses the intended layer direction and couples file storage to business operations.
- **Consequence:** Changes to those services can force changes in `ReturnRepository`, and the repository cannot be tested independently without bringing in service dependencies.
- **Proposed solution:** `ReturnRepository` should persist return data without depending on services. `ReturnService` resolves the sale and the items through `SaleService` and `InventoryService`, keeping the same error messages. The file format does not change.
- **Other member's code:** yes - `Main` wiring (Technical Lead).

## 6. High Cohesion

**Verdict: Violation (registered under Information Expert and Pure Fabrication)**

- `ReturnService` also calculates sales totals: registered as V-02, because the root cause is who owns the information.
- `AccessoryService` contains its own validation rules: registered as V-08 (Pure Fabrication).

Correct: `PersonRepository`, `PromotionRepository` and `WarrantyRepository` each handle only one file format.
## 7. Polymorphism

**Verdict: Violation**

Correct applications: `PromotionService.findBestPromotionFor`
(lines 196-220) calls `promotion.calculateDiscount(sale)` without checking
the type.

Accepted with justification: the `instanceof` chains in the `toCsv` methods
of `AccessoryRepository` (lines 321-342), `PromotionRepository`
(lines 171-223) and `WarrantyRepository` (lines 133-146) map each concrete
class to its file columns. That mapping is persistence knowledge; moving it
into the model would put file-format concerns in domain classes, against
the layer rules of the project.

### V-05

- **Pattern:** Polymorphism (also Protected Variations).
- **Module:** Returns (Requirement 3), accessory returns (Requirement 5, A4).
- **Evidence:** `ReturnService.restoreReturnedStock`, lines 187-208:
  `if (product instanceof Accessory)` calls `accessoryService.restoreStock`,
  otherwise `productService.restoreStock`. `ReturnService.findProduct`,
  lines 432-458, searches `productService.listProducts()` and then
  `accessoryService.listAccessories()`.
- **Explanation:** `ReturnService` decides with a type check where each
  kind of item is stored. The same decision exists in `SaleService` and
  `GameZoneUI` (Technical Lead).
- **Consequence:** a new kind of sellable item needs a new branch here and in
  the sale flow, and returns would silently restore stock in the wrong
  repository if one branch is forgotten.
- **Proposed solution:** use the `InventoryService` agreed with the
  Technical Lead (`findItem`, `restoreStock`). `ReturnService` no longer
  knows whether an item is a product or an accessory.
- **Other member's code:** yes - `InventoryService` is created by the Technical Lead (boundary finding).

### V-06

- **Pattern:** Polymorphism.
- **Module:** Warranty cancellation on returns (Requirement 5, A7).
- **Evidence:** `ReturnService.cancelReturnedConsoleWarranties`, line 169:
  `if (product instanceof Console)`.
- **Explanation:** the rule "only consoles have a warranty" is a type check,
  not a behaviour of the product.
- **Consequence:** if warranties are extended to another product type,
  returns would not cancel or refund them unless this check is found.
- **Proposed solution:** use `product.isWarrantyEligible()`.
- **Other member's code:** yes - `Product` and `Console` (Developer 1); the
  same check is in `SaleService` and `GameZoneUI` (Technical Lead).

### V-07

- **Pattern:** Polymorphism (also Protected Variations).
- **Module:** Accessories (Requirement 1).
- **Evidence:** `AccessoryRepository.findByType`, lines 79-93, combines
  `"controller"` with `instanceof Controller`, `"cable"` with
  `instanceof Cable` and `"memory"` with `instanceof Memory`.
- **Explanation:** this is a query, not a file mapping: "which accessories
  are of type X" should ask each accessory for its type.
- **Consequence:** a new accessory type needs a new branch in the query,
  besides the new class.
- **Proposed solution:** abstract `Accessory.getAccessoryType()`
  (`"CONTROLLER"`, `"CABLE"`, `"MEMORY"`); `findByType` compares
  `accessory.getAccessoryType().equalsIgnoreCase(normalizedType)`, and
  `toCsv` also uses it for the type column.
- **Other member's code:** yes - `Accessory`, `Controller`, `Cable` and
  `Memory` (Developer 1).
## 8. Pure Fabrication

**Verdict: Violation**

Correct applications: my repositories are fabricated classes that keep file
access out of the model, and in Requirement 6 `ProductService` and
`PersonService` delegate their checks to `ProductValidator` and
`PersonValidator` (for example, `PersonService` lines 61-75).

### V-08

- **Pattern:** Pure Fabrication (also High Cohesion).
- **Module:** Accessories (Requirement 1).
- **Evidence:** `AccessoryService.validateAccessory`, lines 223-270: null,
  identifier, title, price, stock and duplicate checks written inline with
  `IllegalArgumentException`, called from `registerController`,
  `registerCable` and `registerMemory` (lines 42, 53, 64).
- **Explanation:** the service mixes business operations with validation
  responsibilities that can be separated into a dedicated validator.
- **Consequence:** validation logic becomes harder to reuse and test
  independently.
- **Proposed solution:** create `AccessoryValidator` in package
  `validation` with the same checks, order, exception type and messages, so
  the UI shows exactly the same text. `AccessoryService` delegates to it.
- **Other member's code:** no, within Developer 2's responsibility.

### V-09

- **Pattern:** Pure Fabrication.
- **Module:** Exceptions and validations (Requirement 6).
- **Evidence:** `SaleService.registerSale`, lines 108-120: after
  `SaleValidator.validateSaleData(...)` (lines 102-106), a loop checks again
  for `null` products (unreachable, because `SaleValidator` already rejects
  them) and throws `InvalidDataException("product identifier", ...)` for
  blank identifiers.
- **Explanation:** my Requirement 6 refactor left part of the sale-data
  validation inside the service instead of delegating all of it to
  `SaleValidator`.
- **Consequence:** sale validation is split across two classes; a new rule
  may be added to one and forgotten in the other.
- **Proposed solution:** move the blank-identifier rule into
  `SaleValidator` (same field name and message) and remove the loop from
  `SaleService`.
- **Other member's code:** yes - `SaleValidator` (Developer 1).

## 9. Indirection

**Verdict: Correct**

`WarrantyRecord` (lines 197-203 of `WarrantyRepository`) is an intermediate
object that decouples the warranty file from `Product` and `Sale`, and
`PersonService` reaches the uniqueness rule through `PersonValidator`.

The direct use of `ProductService` and `AccessoryService` from
`ReturnService` is the same finding as V-05. It is registered once, under
Polymorphism, because the fix (the shared `InventoryService`) removes the
type check; Indirection is the mechanism of that fix, not a separate
violation.

## 10. Protected Variations

**Verdict: Violation (registered under Information Expert and
Polymorphism)**

The unprotected variation points in my code are the sale reference format
(V-01) and the kinds of item and accessory (V-05, V-06, V-07). They are not
registered again here, to avoid counting the same fragment twice.

Correct applications: in Requirement 6, `ProductRepository`,
`PersonRepository` and `SaleRepository` wrap every I/O and parsing error
into `PersistenceException` with the file name (for example,
`PersonRepository` lines 54-59 and 121-127).

## 11. Summary

- **V-01** - Information Expert - `ReturnRepository`, `ReturnService`,
  `WarrantyRepository`, `WarrantyService` - other member: Technical Lead.
- **V-02** - Information Expert - `ReturnService` - other member: Technical
  Lead.
- **V-03** - Information Expert - `PromotionService` - other member:
  Developer 1.
- **V-04** - Low Coupling - `ReturnRepository` - other member: Technical
  Lead (`Main`).
- **V-05** - Polymorphism - `ReturnService` - other member: Technical Lead
  (`InventoryService`). Also covers the Indirection and Protected
  Variations aspects of the same fragment.
- **V-06** - Polymorphism - `ReturnService` - other members: Developer 1
  and Technical Lead.
- **V-07** - Polymorphism - `AccessoryRepository` - other member:
  Developer 1.
- **V-08** - Pure Fabrication - `AccessoryService` - other member: none.
- **V-09** - Pure Fabrication - `SaleService` (Requirement 6 part) - other
  member: Developer 1 (`SaleValidator`).

The proposed solutions are recommendations for the correction phase; no
Java code is changed in this audit.