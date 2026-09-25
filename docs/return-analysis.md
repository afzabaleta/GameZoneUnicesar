# Return Module Analysis

## 1. Relationship between Return and Sale

The relationship between `Return` and `Sale` is an **association**.

A `Return` references an existing `Sale` because the return must identify the original transaction from which the products were purchased. However, the `Sale` does not own the `Return`, and both objects can exist independently in the system.

This is not inheritance because a `Return` is not a specialized type of `Sale`. It is also not aggregation or composition because the lifecycle of the `Sale` does not depend on the `Return`, and removing one object does not imply removing the other.

Therefore, the appropriate relationship is a direct association where `Return` stores a reference to the original `Sale`.

## 2. Representation of partially returned products

A return does not necessarily contain every product from the original sale.

The `Return` class stores a list of returned products using the attribute `List<Product> returnedProducts`.

This list contains only the products that the customer wants to return from the referenced sale.

The original `Sale` remains unchanged and continues to contain all products that were purchased. The `Return` object represents the subset of products that were actually returned.

This allows partial returns while maintaining the relationship with the complete original sale.

## 3. Validation of the 30-day return period

The 30-day return rule is a business rule, so the main validation belongs in the **service layer**, specifically in `ReturnService`.

Before creating a return, `ReturnService` must verify that the referenced sale exists and that its date is within the allowed 30-calendar-day period.

The `Sale` class provides the additive method `canBeReturned()`. This method determines whether the current date is still within the allowed return period.

Java's `java.time` API can be used to calculate the difference between two dates. In particular, `ChronoUnit.DAYS.between(...)` can calculate the number of calendar days between the sale date and the current date.

Keeping the main business validation in `ReturnService` is consistent with the layered architecture because the service layer coordinates business rules and the UI only handles user interaction.

## 4. Reusing the existing stock update logic

When a return is processed successfully, the stock of each returned product must be increased.

The module must reuse the existing `ProductService.restoreStock(String productId, int quantity)` method.

`ReturnService` invokes `ProductService.restoreStock(...)` for every returned product when processing the return.

Reusing this method avoids duplicating inventory update logic inside `ReturnService`. It also centralizes stock modification in `ProductService`, which is already responsible for product-related business operations and persistence coordination.

This approach reduces duplication and ensures that inventory updates follow the same rules regardless of whether stock changes are caused by another operation or by a product return.

## 5. Location of the monthly balance report

The monthly balance report belongs in `ReturnService`.

The report combines information from two different modules:

- Sales obtained through `SaleService`.
- Returns obtained through the return module.

The method `generateMonthlyBalance(int month, int year)` must calculate the total sales for the requested month and year, calculate the total returns for the same period, and subtract the return amount from the sales amount.

This location is coherent with the layered architecture because the calculation is a business operation that combines information from multiple modules. The console interface should only collect the month and year and display the result, while the service layer performs the calculation.

The service therefore needs access to the sale information through `SaleService` and access to the registered returns through the return module.