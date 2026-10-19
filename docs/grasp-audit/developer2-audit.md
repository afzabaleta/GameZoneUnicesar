
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
