# Layers Diagram

This diagram represents the four architectural layers of the integrated
GameZone Unicesar system and the dependencies between them.

```mermaid
graph TD

    %% =========================================================
    %% APPLICATION ENTRY POINT
    %% =========================================================

    Main["Main"]

    %% =========================================================
    %% UI LAYER
    %% =========================================================

    subgraph UI["UI Layer"]
        GameZoneUI["GameZoneUI"]
    end

    %% =========================================================
    %% SERVICE LAYER
    %% =========================================================

    subgraph SERVICE["Service Layer"]
        PersonService["PersonService"]
        ProductService["ProductService"]
        AccessoryService["AccessoryService"]
        PromotionService["PromotionService"]
        SaleService["SaleService"]
        WarrantyService["WarrantyService"]
        ReturnService["ReturnService"]
    end

    %% =========================================================
    %% PERSISTENCE LAYER
    %% =========================================================

    subgraph PERSISTENCE["Persistence Layer"]
        PersonRepository["PersonRepository"]
        ProductRepository["ProductRepository"]
        AccessoryRepository["AccessoryRepository"]
        PromotionRepository["PromotionRepository"]
        SaleRepository["SaleRepository"]
        WarrantyRepository["WarrantyRepository"]
        ReturnRepository["ReturnRepository"]
    end

    %% =========================================================
    %% MODEL LAYER
    %% =========================================================

    subgraph MODEL["Model Layer"]

        Person["Person"]
        Customer["Customer"]
        Seller["Seller"]

        Product["Product"]
        VideoGame["VideoGame"]
        Console["Console"]

        Accessory["Accessory"]
        Cable["Cable"]
        Controller["Controller"]
        Memory["Memory"]

        Promotion["Promotion"]
        PercentageDiscount["PercentageDiscount"]
        CategoryDiscount["CategoryDiscount"]
        BulkPurchaseDiscount["BulkPurchaseDiscount"]

        Sale["Sale"]

        Warranty["Warranty"]
        BasicWarranty["BasicWarranty"]
        ExtendedWarranty["ExtendedWarranty"]

        Return["Return"]
    end

    %% =========================================================
    %% APPLICATION ENTRY
    %% =========================================================

    Main --> GameZoneUI

    %% =========================================================
    %% UI -> SERVICE
    %% =========================================================

    GameZoneUI --> PersonService
    GameZoneUI --> ProductService
    GameZoneUI --> AccessoryService
    GameZoneUI --> PromotionService
    GameZoneUI --> SaleService
    GameZoneUI --> WarrantyService
    GameZoneUI --> ReturnService

    %% =========================================================
    %% SERVICE -> PERSISTENCE
    %% =========================================================

    PersonService --> PersonRepository

    ProductService --> ProductRepository

    AccessoryService --> AccessoryRepository

    PromotionService --> PromotionRepository

    SaleService --> SaleRepository

    WarrantyService --> WarrantyRepository
    WarrantyService --> SaleRepository

    ReturnService --> ReturnRepository

    %% =========================================================
    %% SERVICE -> SERVICE
    %% =========================================================

    SaleService --> ProductService
    SaleService --> AccessoryService
    SaleService --> PromotionService
    SaleService --> WarrantyService

    WarrantyService --> ProductService

    ReturnService --> SaleService
    ReturnService --> ProductService
    ReturnService --> AccessoryService
    ReturnService --> WarrantyService

    %% =========================================================
    %% SERVICE -> MODEL
    %% =========================================================

    PersonService --> Customer
    PersonService --> Seller

    ProductService --> Product
    ProductService --> VideoGame
    ProductService --> Console

    AccessoryService --> Accessory
    AccessoryService --> Cable
    AccessoryService --> Controller
    AccessoryService --> Memory

    PromotionService --> Promotion
    PromotionService --> Sale

    SaleService --> Sale

    WarrantyService --> Warranty
    WarrantyService --> BasicWarranty
    WarrantyService --> ExtendedWarranty

    ReturnService --> Return

    %% =========================================================
    %% PERSISTENCE -> MODEL
    %% =========================================================

    PersonRepository --> Customer
    PersonRepository --> Seller

    ProductRepository --> Product
    ProductRepository --> VideoGame
    ProductRepository --> Console

    AccessoryRepository --> Accessory
    AccessoryRepository --> Cable
    AccessoryRepository --> Controller
    AccessoryRepository --> Memory

    PromotionRepository --> Promotion
    PromotionRepository --> PercentageDiscount
    PromotionRepository --> CategoryDiscount
    PromotionRepository --> BulkPurchaseDiscount

    SaleRepository --> Sale
    SaleRepository --> Customer
    SaleRepository --> Seller
    SaleRepository --> Product

    WarrantyRepository --> Warranty

    ReturnRepository --> Return
    ReturnRepository --> Sale
    ReturnRepository --> Product
    ReturnRepository --> Accessory

    %% =========================================================
    %% MODEL INHERITANCE
    %% =========================================================

    Person --> Customer
    Person --> Seller

    Product --> VideoGame
    Product --> Console
    Product --> Accessory

    Accessory --> Cable
    Accessory --> Controller
    Accessory --> Memory

    Promotion --> PercentageDiscount
    Promotion --> CategoryDiscount
    Promotion --> BulkPurchaseDiscount

    Warranty --> BasicWarranty
    Warranty --> ExtendedWarranty
 ```   

## Dependency Direction

The integrated system follows the same one-way architectural dependency:

```text
UI
↓
Service
↓
Persistence
↓
Model
```

The service layer may also coordinate other services when the business
process requires integration between modules.

For example:

- `SaleService` coordinates products, accessories, promotions, and warranties.
- `ReturnService` coordinates sales, products, accessories, and warranties.
- `WarrantyService` resolves sale and product references.