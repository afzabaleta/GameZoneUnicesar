# GRASP Refactor Log - Requirement 7

This log records the Phase 2 corrections of the consolidated GRASP audit
(`docs/grasp-audit/consolidated-audit.md`): the non-regression strategy, the
verification results and one row for each change.

## 1. Non-regression strategy

The corrections are refactors: the same inputs must produce the same
results, the same messages and the same data files. The team checks this
with a black-box comparison of the console output and the data files,
before and after the corrections.

**How it works**

- `docs/regression/scripts/` contains one input script per area. Each line
  is what a user types in the console menu.
- `docs/regression/fixture-data/` is a copy of the `data/` folder of
  `develop` before Phase 2. Every script starts from a fresh copy of it.
- `docs/regression/run-regression.sh <label>` (Git Bash, after
  `mvn clean compile`) runs every script and stores the console output and
  the final content of every data file in
  `docs/regression/results/<label>/`. `products.dat` is binary, so its
  SHA-256 hash is stored.
- Values that change on every run are replaced by fixed tags: today's date
  and the warranty end dates, the return ids (`RET-<ID>`), the warranty ids
  (`WAR-...-<ID>`) and the object reference printed in the return receipt
  (`Sale@<HASH>`). Scripts use `{TODAY}`, `{MONTH}` and `{YEAR}`, which the
  runner replaces with the current date.
- Two runs are compared with
  `git diff --no-index docs/regression/results/baseline docs/regression/results/<label>`.
  No output means no difference.

**Operations covered (31)**

| Script | Menu operations |
|---|---|
| 01_products | Register video game, register console, list products |
| 02_accessories | Register controller, cable, memory; list; list by type; compatible |
| 03_people | Register customer, list customers, list sellers |
| 04_promotions | Register percentage, category, bulk; list all; list active; find by id |
| 05_sales | Register sale; list all sales; customer history; seller history |
| 06_category_accessory | Accessory category promotion applied to a sale; monthly balance |
| 07_category_console | Console category promotion; sale without extended warranty |
| 08_returns | Register return; list all; by customer; by sale; monthly balance |
| 09_warranties | Find warranty; list all; list active; list expiring soon |
| 10_menu_navigation | Invalid options in every menu |

The scripts include valid data and invalid cases: blank identifiers,
non-numeric prices and quantities, price 0, duplicated identifiers, invalid
email, invalid category, wrong date format, unknown customer, seller,
product and sale, insufficient stock, expired return period and invalid
month.

**When it runs**

1. On `develop` before the first correction (reference result, `baseline`).
2. In each Phase 2 Pull Request, on the operations its violations affect.
3. At the end of Phase 2, over all the scripts (`final`).

## 2. Verification results

### 2.1 Initial verification (baseline)

- **Branch / commit:** `develop` @ cd5fd54
- **Command:** `mvn clean compile` and
  `bash docs/regression/run-regression.sh baseline`
- **Result:** BUILD SUCCESS. The 10 scripts ran to the end of the menu
  ("Cerrando GameZoneUnicesar") with no exceptions. The results are stored
  in `docs/regression/results/baseline/`.
  
### 2.2 Final verification

Recorded by the Technical Lead at the end of Phase 2.

## 3. Change log

| Class | Change | Violation | Pattern | Commit / PR      | Responsible |
|---|---|---|---|------------------|---|
| Sale | Adds getReference() | V-04 | Information Expert | cf6826c (PR #43) | Andres |
| Sale | Adds getDiscountRate(), calculateProportionalDiscount() | V-05 | Information Expert | cf6826c (PR #43) | Andres |
| SaleService | Adds calculateMonthlySales(int, int) | V-06 | Information Expert | b610aef (PR #43) | Andres |
| SaleService | Adds findSaleByReference(String) | V-01 | Low Coupling | b610aef (PR #43) | Andres |
| InventoryService | New class: finds items, validates and moves stock | V-02 | Pure Fabrication | 896d079 (PR #43) | Andres |
| Product | Adds abstract getCategory() | V-09 | Polymorphism | 96cdf62 (PR #44) | Sherly |
| VideoGame | Returns VIDEOGAME; keeps serialVersionUID | V-09 | Polymorphism | 96cdf62 (PR #44) | Sherly |
| Console | Returns CONSOLE; keeps serialVersionUID | V-09 | Polymorphism | 96cdf62 (PR #44) | Sherly |
| Accessory | Returns ACCESSORY for every accessory | V-09 | Polymorphism | 96cdf62 (PR #44) | Sherly |
| CategoryDiscount | Uses getCategory() instead of instanceof | V-09 | Polymorphism | 96cdf62 (PR #44) | Sherly |
| ProductService | Adds registerProduct(Product); others delegate | V-13 | Protected Variations | bec143f (PR #44) | Sherly |
| Product | Adds isWarrantyEligible() (false by default) | V-03 | Polymorphism | ce73efe (PR #44) | Sherly |
| Console | Overrides isWarrantyEligible() (true) | V-03 | Polymorphism | ce73efe (PR #44) | Sherly |
| Accessory | Adds abstract getAccessoryType() | V-10 | Polymorphism | b7f1a19 (PR #44) | Sherly |
| Controller | Returns CONTROLLER from getAccessoryType() | V-10 | Polymorphism | b7f1a19 (PR #44) | Sherly |
| Cable | Returns CABLE from getAccessoryType() | V-10 | Polymorphism | b7f1a19 (PR #44) | Sherly |
| Memory | Returns MEMORY from getAccessoryType() | V-10 | Polymorphism | b7f1a19 (PR #44) | Sherly |
| ProductService | Adds listConsoles() to filter registered consoles | V-08 | Information Expert | b7f477b (PR #44) | Sherly |
| CategoryDiscount | Adds static validateTargetCategory(String); setter reuses validation | V-11 | Information Expert | 6bea9b5 (PR #44) | Sherly |
| SaleValidator | Adds blank product identifier validation to validateSaleData() | V-17 | Pure Fabrication | b5c8228 (PR #44) | Sherly |
| Return | calculateRefundAmount() delegates proportional discount calculation to Sale | V-05 | Information Expert | 02658e0 (PR #44) | Sherly |
| Return | generateReturnReceipt() uses Sale.getDiscountRate() | V-05 | Information Expert | 2d0418c (PR #44) | Sherly |
| PersonService | Adds findCustomerById(String), findSellerById(String) | V-07 | Information Expert | 32bb636 (PR #45) | Diego |
| AccessoryValidator | New class: accessory data, uniqueness, type, console and stock checks | V-12 | Pure Fabrication | c38c60c (PR #45) | Diego |
| AccessoryRepository | findByType and CSV type use getAccessoryType() | V-10 | Polymorphism | 5dabfcd (PR #45) | Diego |
| PromotionService | Delegates target category validation to CategoryDiscount.validateTargetCategory() | V-11 | Information Expert | 7b280a4 (PR #45) | Diego |
| SaleService | Removes duplicated product validation after SaleValidator.validateSaleData() | V-17 | Pure Fabrication | 4d0f61b (PR #45) | Diego |
| ReturnRepository, WarrantyRepository, ReturnService, WarrantyService | Uses Sale.getReference() instead of date strings to identify sales | V-04 | Information Expert | f7b9727 (PR #45) | Diego |
| ReturnService | Validates month and year, then delegates monthly sales calculation to SaleService.calculateMonthlySales() | V-06 | Information Expert | c565b38 (PR #45) | Diego |
| ReturnService | Uses Product.isWarrantyEligible() instead of instanceof Console when cancelling warranties | V-03 | Polymorphism | 0ab2775 (PR #45) | Diego |
| ReturnService | Delegates item lookup and returned-stock restoration to InventoryService | V-02 | Polymorphism | 8f0e8b7 (PR #45) | Diego |
| ReturnService | Delegates sale lookup to SaleService.findSaleByReference() | V-01 | Low Coupling | 0ed0783 (PR #45) | Diego |
| AccessoryService | Delegates every accessory check to AccessoryValidator | V-12 | Pure Fabrication | c38c60c (PR #45) | Diego |
| Main | Creates InventoryService and passes it to ReturnService | V-02 | Polymorphism | 549a55c (PR #45) | Diego |
| ReturnRecord | New record: stored return data and ids only | V-01 | Low Coupling | 6a7768a (PR #45) | Diego |
| ReturnRepository | Reads and writes ReturnRecord; no service dependencies | V-01 | Low Coupling | 6a7768a (PR #45) | Diego |
| ReturnService | Builds each Return through SaleService and InventoryService | V-01 | Low Coupling | 6a7768a (PR #45) | Diego |
| Main | Creates ReturnRepository without services | V-01 | Low Coupling | 6a7768a (PR #45) | Diego |
| WarrantyService | cancelWarranties uses Sale.getReference() | V-04 | Information Expert | | 200538f (PR #45) | Andres |
| SaleService | assignWarranties uses Product.isWarrantyEligible() | V-03 | Polymorphism | bd4dbc9 (PR #__) | Andres |
| SaleService | Validates and decreases stock through InventoryService; product/accessory pairs removed | V-02 | Polymorphism | 38cac87 (PR #__) | Andres |
| Main | Passes InventoryService to SaleService | V-02 | Polymorphism | 38cac87 (PR #__) | Andres |
| SaleService | registerSale(Customer, Seller, List, List) creates and returns the Sale | V-14 | Creator | 1bb7875 (PR #__) | Andres |
| GameZoneUI | registerSale no longer creates the Sale | V-14 | Creator | 1bb7875 (PR #__) | Andres |
| SaleRepository | Resolves customers, sellers and items through injected lookup functions | V-16 | Low Coupling (Indirection) | aeede52 (PR #__) | Andres |
| Main | Creates SaleRepository with the PersonService and InventoryService lookups | V-16 | Low Coupling (Indirection) | aeede52 (PR #__) | Andres |
| GameZoneUI | Uses PersonService.findCustomerById / findSellerById | V-07 | Information Expert | 423d473 (PR #__) | Andres |
| GameZoneUI | Uses ProductService.listConsoles() | V-08 | Information Expert | 74947e2 (PR #__) | Andres |
| GameZoneUI | selectExtendedWarrantyProducts uses isWarrantyEligible() | V-03 | Polymorphism | 075feb0 (PR #__) | Andres |
| GameZoneUI | Finds sale items with InventoryService.findItem() | V-02 | Polymorphism | c72eef9 (PR #__) | Andres |
| Main | Passes InventoryService to GameZoneUI | V-02 | Polymorphism | c72eef9 (PR #__) | Andres |

