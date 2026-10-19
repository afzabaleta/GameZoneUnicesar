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