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
| InventoryService | New class: finds items, validates and moves stock | V-02 | Pure Fabrication | 896d079 (PR #_43 | Andres |
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
