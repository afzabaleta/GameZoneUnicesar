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