# Promotion Module Analysis

## 1. Promotion hierarchy and polymorphism

The three promotion types share common attributes and behavior, so the system uses an abstract base class named `Promotion`.

The common attributes are:

- `identifier`
- `name`
- `startDate`
- `endDate`

The concrete classes are:

- `PercentageDiscount`
- `CategoryDiscount`
- `BulkPurchaseDiscount`

Each concrete class extends `Promotion` and provides its own implementation of `calculateDiscount(Sale sale)`.

Polymorphism allows the rest of the system to work with a collection of `Promotion` objects without knowing the concrete promotion type. The selected promotion can calculate its discount through the common method defined by the base class.

This design avoids conditional logic in the service layer for every promotion type and keeps each discount rule inside the class responsible for it.

## 2. Abstract discount calculation method

The `Promotion` class declares:

```java
public abstract double calculateDiscount(Sale sale);
```

The method is abstract because the base class does not know how the discount should be calculated for every promotion type.

This declaration guarantees that every concrete subclass must provide its own implementation of `calculateDiscount`.

Using an abstract method also allows the service layer to invoke the same operation polymorphically on any promotion.

## 3. Selection of the best promotion

The logic for selecting the promotion that provides the highest monetary discount belongs in `PromotionService`.

`PromotionService` is responsible for business rules related to promotions, so it is the appropriate layer for:

1. Obtaining the active promotions.
2. Calculating the discount that each promotion would provide.
3. Comparing the resulting monetary discounts.
4. Returning the promotion that provides the highest discount.

This logic should not be placed in `Sale` because `Sale` represents a transaction and should not be responsible for managing the promotion catalog.

It should not be placed in the console menu because the UI should only handle user interaction and delegate business rules to the service layer.

This preserves the layered architecture:

```text
UI -> Service -> Persistence -> Model
```

## 4. Modifications required in Sale and generateReceipt

The `Sale` class must store the promotion information applied to the transaction.

Two new attributes are required:

- `appliedPromotionName`
- `discountAmount`

Getters and setters must be provided for both attributes.

The receipt generation method must show:

1. The subtotal.
2. The promotion applied, when applicable.
3. The discount amount.
4. The final total.

The existing behavior of the sale must remain valid. If no promotion is applicable, the discount amount must be zero and the final total must remain equal to the subtotal.

These modifications are additive because they extend the sale information without removing the existing customer, seller, product and total functionality.

## 5. Validation of promotion validity

The promotion validity rule should be implemented in `Promotion.isActive(LocalDate date)`.

The method checks whether the provided date is inside the promotion's validity range:

- `date >= startDate`
- `date <= endDate`

`PromotionService.listActivePromotions()` can use this method to obtain the promotions that are active on the current date.

Keeping the date comparison inside `Promotion` centralizes the common validity rule for all promotion types.

The service layer remains responsible for deciding which active promotions can be applied to a specific sale and which one provides the highest monetary discount.