
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
- **Evidence:** `ReturnRepository` and `WarrantyRepository`, together with `ReturnService` and `WarrantyService`, construct a sale reference with `sale.getDate().toString()` in eight places.
- **Explanation:** `Sale` owns the sale date and should be responsible for providing the sale reference. The other classes should not know how that reference is formatted.
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
