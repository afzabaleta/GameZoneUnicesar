# Promotion Class Diagram

The following class diagram shows the promotion hierarchy, persistence
and service layers, and the integration with the existing sales system.

```mermaid
classDiagram

    class Promotion {
        <<abstract>>
        -String identifier
        -String name
        -LocalDate startDate
        -LocalDate endDate
        +Promotion(String identifier, String name, LocalDate startDate, LocalDate endDate)
        +String getIdentifier()
        +void setIdentifier(String identifier)
        +String getName()
        +void setName(String name)
        +LocalDate getStartDate()
        +void setStartDate(LocalDate startDate)
        +LocalDate getEndDate()
        +void setEndDate(LocalDate endDate)
        +boolean isActive(LocalDate date)
        +double calculateDiscount(Sale sale)
    }

    class PercentageDiscount {
        -double percentage
        +PercentageDiscount(...)
        +double getPercentage()
        +void setPercentage(double percentage)
        +double calculateDiscount(Sale sale)
    }

    class CategoryDiscount {
        -double percentage
        -String targetCategory
        +CategoryDiscount(...)
        +double getPercentage()
        +void setPercentage(double percentage)
        +String getTargetCategory()
        +void setTargetCategory(String targetCategory)
        +double calculateDiscount(Sale sale)
    }

    class BulkPurchaseDiscount {
        -int minimumQuantity
        -double percentage
        +BulkPurchaseDiscount(...)
        +int getMinimumQuantity()
        +void setMinimumQuantity(int minimumQuantity)
        +double getPercentage()
        +void setPercentage(double percentage)
        +double calculateDiscount(Sale sale)
    }

    class PromotionRepository {
        -String filePath
        +PromotionRepository()
        +void saveAll(List~Promotion~ promotions)
        +List~Promotion~ loadAll()
    }

    class PromotionService {
        -PromotionRepository promotionRepository
        +PromotionService(PromotionRepository promotionRepository)
        +void registerPercentageDiscount(...)
        +void registerCategoryDiscount(...)
        +void registerBulkPurchaseDiscount(...)
        +List~Promotion~ listAllPromotions()
        +List~Promotion~ listActivePromotions()
        +Promotion findBestPromotionFor(Sale sale)
        +Promotion findById(String id)
    }

    class Sale {
        -LocalDate date
        -Customer customer
        -Seller seller
        -List~Product~ products
        -String appliedPromotionName
        -double discountAmount
        +double calculateTotal()
        +String generateReceipt()
        +String getAppliedPromotionName()
        +void setAppliedPromotionName(String appliedPromotionName)
        +double getDiscountAmount()
        +void setDiscountAmount(double discountAmount)
    }

    class SaleService {
        -SaleRepository saleRepository
        -ProductService productService
        -AccessoryService accessoryService
        -PromotionService promotionService
        +void registerSale(Sale sale)
    }

    class Product {
        <<abstract>>
        -String identifier
        -String title
        -double price
        -int availableQuantity
        +double getPrice()
        +String getIdentifier()
        +String getTitle()
        +int getAvailableQuantity()
    }

    Promotion <|-- PercentageDiscount
    Promotion <|-- CategoryDiscount
    Promotion <|-- BulkPurchaseDiscount

    PromotionRepository --> Promotion : persists
    PromotionService --> PromotionRepository : uses
    PromotionService --> Promotion : manages

    PromotionService --> Sale : evaluates
    Promotion ..> Sale : calculates discount

    SaleService --> PromotionService : applies promotion
    SaleService --> Sale : registers

    Sale --> Product : contains
    CategoryDiscount --> Product : filters category
```

## Layer Integration

The promotion module follows the existing layered architecture:

```text
UI -> Service -> Persistence -> Model
```

`PromotionService` contains the business logic for selecting the best applicable promotion.

`PromotionRepository` is responsible for loading and saving promotions in
`data/promotions.csv`.

`SaleService` integrates the promotion logic into the sale registration
process.

`Sale` stores the promotion name and discount amount applied to the
transaction.

`CategoryDiscount` uses the products included in the sale to determine
the amount applicable to the target category.