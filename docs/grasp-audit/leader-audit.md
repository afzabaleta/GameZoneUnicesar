# GRASP Audit - Technical Lead

**Member:** Andres Felipe Zabaleta Diaz
**Role:** Technical Lead - Sales, UI and integration
**Audited branch / commit:** `develop` @ `81ea350`

## 1. Scope

The requirement calls the console class `ConsoleMenu`. In this repository it
is `com.gamezone.ui.GameZoneUI`; both names refer to the same class.

Classes and methods audited, by module and requirement:

- **Sales, UI and startup (Workshop 1)**
    - `Sale`: constructor, `calculateSubtotal`, `calculateTotal`,
      getters/setters.
    - `SaleRepository`: constructor, `saveSales`, `loadSales`, `buildLine`,
      `parseLine`, `findCustomerById`, `findSellerById`, `findItemById`.
    - `SaleService`: constructor, `listSales`, `listSalesByCustomer`,
      `listSalesBySeller`.
    - `GameZoneUI`: all menus and operations.
    - `Main`: `main` (dependency wiring).
- **Accessories (Requirement 1)**
    - `SaleService`: `countProductQuantities`, `countAccessoryQuantities`,
      `validateAccessoryStock`, `updateAccessoryStock`, `findAccessoryById`.
    - `GameZoneUI`: accessory submenu.
- **Promotions (Requirement 2)**
    - `SaleService.applyBestPromotion`; in `Sale`, `generateReceipt`,
      `appliedPromotionName` and `discountAmount`.
    - `GameZoneUI`: promotion submenu.
- **Returns (Requirement 3)**
    - `ProductService.restoreStock` only; `GameZoneUI`: return submenu.
- **Warranties (Requirement 4)**
    - `SaleService`: `assignWarranties`, `normalizeWarrantyProductIds`; in
      `Sale`, `warrantyAdditionalCost`. `GameZoneUI`: warranty submenu.
- **Integration (Requirement 5, A3)**
    - `SaleService`: `registerSale` (unified flow), `validateProductStock`,
      `updateProductStock`, `findProductById`.
- **Exceptions (Requirement 6)**
    - `GameZoneUI`: differentiated `catch` blocks in every operation.

**Not audited here:** `Sale.canBeReturned` (Developer 1, Requirement 3) and
lines 97-120 of `SaleService.registerSale` (null check and delegation to the
validators, written by Developer 2 in Requirement 6, commit `bd5d330`).

---

## 2. Information Expert

**Verdict: Violation**

Correct applications: `Sale.calculateSubtotal()` and `Sale.calculateTotal()`
(lines 57-78) compute totals from the products, discount and warranty cost
that `Sale` owns.

### V-01

- **Pattern:** Information Expert.
- **Module:** Sales and UI (Workshop 1), Requirement 5 (A3).
- **Evidence:** `GameZoneUI.findCustomerById` (lines 1904-1916) and
  `GameZoneUI.findSellerById` (lines 1924-1936) iterate
  `personService.listCustomers()` / `listSellers()` and compare
  identifications; `registerSale` uses them (lines 1620, 1632).
- **Explanation:** `PersonService` holds the customer and seller
  collections, so it is the expert for "find a person by identification".
  The UI pulls the whole list and searches it itself.
- **Consequence:** every new client of people (returns, reports, a future
  GUI) must repeat the search, and a change in how people are identified
  forces a change in the UI.
- **Proposed solution:** add `findCustomerById(String)` and
  `findSellerById(String)` to `PersonService`; the UI calls them.
- **Other member's code:** yes - `PersonService` (Developer 2).

### V-02

- **Pattern:** Information Expert.
- **Module:** Accessories UI (Requirement 1).
- **Evidence:** `GameZoneUI.getAvailableConsoles` (lines 1388-1401) filters
  `productService.listProducts()` with `if (product instanceof Console)`
  (line 1396); it is used at lines 1275 and 1331.
- **Explanation:** knowing which products are consoles is product-catalogue
  knowledge that belongs to `ProductService`. The UI is classifying domain
  objects.
- **Consequence:** the UI depends on the concrete `Console` class and on the
  internal organisation of the catalogue.
- **Proposed solution:** add `listConsoles()` to `ProductService`; the UI
  uses it.
- **Other member's code:** yes - `ProductService` (Developer 1).

## 3. Creator

**Verdict: Violation**

Correct applications: `Main` creates every repository, service and the UI,
because it is the composition root and has all the initializing data.

### V-03

- **Pattern:** Creator.
- **Module:** Sales and UI (Workshop 1), Requirement 5 (A3).
- **Evidence:** `GameZoneUI.registerSale`, lines 1653-1658:
  `new Sale(LocalDate.now(), customer, seller, selectedItems)`, followed by
  `saleService.registerSale(sale, extendedWarrantyIds)` (lines 1660-1663).
- **Explanation:** `SaleService` records sales, coordinates their
  registration and persists them, so by Creator it should create `Sale`.
  The UI also decides a business rule: the sale date is "today".
- **Consequence:** any other entry point must remember to build the `Sale`
  with the right date before calling the service, and the UI is coupled to
  the `Sale` constructor.
- **Proposed solution:** `SaleService.registerSale` receives the customer,
  the seller, the selected items and the extended-warranty ids, creates the
  `Sale` with `LocalDate.now()` and returns it, so the UI can print the
  receipt.
- **Other member's code:** no.

## 4. Controller

**Verdict: Correct**

`GameZoneUI` receives the console events and delegates each system
operation to one service that acts as use-case controller:
`saleService.registerSale(...)` (line 1660), `returnService.registerReturn(...)`
(line 2323) and `promotionService.register...` (lines 167, 254, 338). The UI
never touches repositories. The places where the UI takes a domain decision
are registered as V-01, V-02 (Information Expert) and V-03 (Creator),
because those patterns explain them better.

## 5. Low Coupling

**Verdict: Violation**

Correct applications: `GameZoneUI` depends only on services and the model;
`Main` is the only class that knows every concrete repository.

### V-04

- **Pattern:** Low Coupling.
- **Module:** Sales persistence (Workshop 1), Requirement 1.
- **Evidence:** the `SaleRepository` constructor (lines 49-69) requires
  `PersonRepository`, `ProductRepository` and `AccessoryRepository`.
  `findCustomerById` (line 230) calls `personRepository.loadCustomers()`,
  `findSellerById` (line 246) calls `personRepository.loadSellers()` and
  `findItemById` (line 262) calls `productRepository.loadProducts()` for
  every record.
- **Explanation:** `SaleRepository` depends on the loading protocol of three
  other repositories, not on a narrow lookup. `loadProducts()` also clears
  and refills the product cache of `ProductRepository`, so loading sales has
  a hidden side effect on another repository.
- **Consequence:** any change to how people or products are loaded breaks
  sale loading, and each sale line re-reads the customer, seller and
  product files.
- **Proposed solution:** Indirection: `SaleRepository` receives three lookup
  functions (customer, seller and item by id) instead of the repositories.
  `Main` wires them to the `PersonService` finders and to
  `InventoryService.findItem`. Messages and file format stay the same.
- **Other member's code:** no edit to another member's class; it reuses the
  `PersonService` finders of V-01 and the `InventoryService` of V-06.

## 6. High Cohesion

**Verdict: Violation**

Correct applications: `Sale` keeps only sale data and sale calculations;
`SaleRepository` only handles the `sales.txt` format.

### V-05

- **Pattern:** High Cohesion.
- **Module:** UI (Workshop 1) and exception handling (Requirement 6).
- **Evidence:** `GameZoneUI` has 2,540 lines and 53 methods covering seven
  menus. The same four `catch` blocks (`ResourceNotFoundException`,
  `BusinessRuleException`, `InvalidDataException`, `PersistenceException`)
  with identical messages are repeated in 16 operations, for example the
  blocks starting at lines 179 (`registerPercentagePromotion`), 818
  (`registerVideoGame`), 1672 (`registerSale`) and 2480
  (`showMonthlyBalance`). The four exception imports are duplicated
  (lines 23-26 and 28-31).
- **Explanation:** the class mixes menu navigation for every module, input
  parsing, output formatting and the translation of exceptions into user
  messages. The error-translation responsibility is copied 16 times.
- **Consequence:** changing one user message means editing 16 places,
  adding a new exception type means touching every operation, and every
  module change produces merge conflicts in the same file.
- **Proposed solution:** a Pure Fabrication `ConsoleErrorHandler` in package
  `ui` that runs an operation and translates the exceptions in one place,
  with exactly the same texts, and one menu class per module
  (`ProductMenu`, `AccessoryMenu`, `PersonMenu`, `SaleMenu`,
  `PromotionMenu`, `ReturnMenu`, `WarrantyMenu`), with `GameZoneUI` keeping
  only the main menu.
- **Other member's code:** no.

## 7. Polymorphism

**Verdict: Violation**

Correct applications: `SaleService.applyBestPromotion` (lines 219-230)
calls `promotion.calculateDiscount(sale)` without asking the promotion
type, and `warranty.getAdditionalCost()` (line 175) is resolved by each
warranty subclass.

### V-06

- **Pattern:** Polymorphism (also Protected Variations).
- **Module:** Accessories integration (Requirement 1), unified sale flow
  (Requirement 5, A3).
- **Evidence:** `SaleService` splits every step with `instanceof Accessory`:
  `countProductQuantities` (lines 289-305, check at 295) and
  `countAccessoryQuantities` (lines 313-329, check at 319);
  `validateProductStock` (337-366) and `validateAccessoryStock` (374-403);
  `updateProductStock` (411-436) and `updateAccessoryStock` (444-469);
  `findProductById` (478-489) and `findAccessoryById` (498-509).
  `GameZoneUI.findItemById` (lines 1944-1967) repeats "search products,
  then accessories".
- **Explanation:** the decision "products and accessories are stored in
  different places" is repeated as type checks and duplicated method pairs
  instead of being encapsulated once.
- **Consequence:** a new kind of sellable item stored elsewhere requires a
  new trio of methods in `SaleService` and a new branch in the UI.
- **Proposed solution:** a Pure Fabrication `InventoryService` (package
  `service`) that hides where each item is stored: `findItem`,
  `validateStock`, `decreaseStock` and `restoreStock`. `SaleService` and the
  UI use it; the single type decision lives in one class. Validation order
  and messages stay the same.
- **Other member's code:** the same pattern appears in `ReturnService`
  (Developer 2), reported for the consolidation as a boundary finding.

### V-07

- **Pattern:** Polymorphism (also Protected Variations).
- **Module:** Warranties (Requirement 4).
- **Evidence:** `SaleService.assignWarranties`, line 162, and
  `GameZoneUI.selectExtendedWarrantyProducts`, line 1718:
  `if (!(product instanceof Console)) { continue; }`.
- **Explanation:** the business rule "only consoles have a warranty" is
  written as type checks in two classes instead of being a behaviour of
  the product.
- **Consequence:** if the store starts selling warranties for another
  product type, every type check must be found and changed.
- **Proposed solution:** polymorphic method `Product.isWarrantyEligible()`
  (`false` by default) overridden in `Console` (`true`); both call sites use
  it.
- **Other member's code:** yes - `Product` and `Console` (Developer 1). The
  same check exists in `ReturnService` (Developer 2).