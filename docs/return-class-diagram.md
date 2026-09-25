# Return Class Diagram

The following class diagram shows the return module, its persistence and service layers, and its integration with the existing sales and product system.

```mermaid
classDiagram

    %% ===== MODEL LAYER =====

    class Return {
        -String identifier
        -LocalDate returnDate
        -Sale originalSale
        -List~Product~ returnedProducts
        -String reason
        -double refundAmount
        +Return(String identifier, LocalDate returnDate, Sale originalSale, List~Product~ returnedProducts, String reason)
        +String getIdentifier()
        +LocalDate getReturnDate()
        +Sale getOriginalSale()
        +List~Product~ getReturnedProducts()
        +String getReason()
        +double getRefundAmount()
        +double calculateRefundAmount()
        +String generateReturnReceipt()
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
        +boolean canBeReturned()
        +LocalDate getDate()
        +Customer getCustomer()
        +Seller getSeller()
        +List~Product~ getProducts()
        +String getAppliedPromotionName()
        +double getDiscountAmount()
    }

    class Product {
        <<abstract>>
        -String identifier
        -String title
        -double price
        -int availableQuantity
        +String getIdentifier()
        +String getTitle()
        +double getPrice()
        +int getAvailableQuantity()
        +void setAvailableQuantity(int quantity)
    }

    %% ===== PERSISTENCE LAYER =====

    class ReturnRepository {
        -String filePath
        -SaleService saleService
        -ProductService productService
        +ReturnRepository(SaleService saleService, ProductService productService)
        +void saveAll(List~Return~ returns)
        +List~Return~ loadAll()
    }

    %% ===== SERVICE LAYER =====

    class ReturnService {
        -ReturnRepository returnRepository
        -SaleService saleService
        -ProductService productService
        +ReturnService(ReturnRepository returnRepository, SaleService saleService, ProductService productService)
        +Return registerReturn(String saleId, List~String~ productIds, String reason)
        +List~Return~ viewAllReturns()
        +List~Return~ viewReturnsByCustomer(String customerId)
        +List~Return~ viewReturnsBySale(String saleId)
        +double generateMonthlyBalance(int month, int year)
    }

    class SaleService {
        -SaleRepository saleRepository
        -ProductService productService
        -AccessoryService accessoryService
        -PromotionService promotionService
        +Sale registerSale(Sale sale)
        +List~Sale~ listSales()
        +List~Sale~ listSalesByCustomer(String customerId)
        +List~Sale~ listSalesBySeller(String sellerId)
    }

    class ProductService {
        -ProductRepository productRepository
        +void registerVideoGame(VideoGame videoGame)
        +void registerConsole(Console console)
        +List~Product~ listProducts()
        +void updateStock(String productIdentifier, int quantity)
        +void restoreStock(String productId, int quantity)
    }

    %% ===== UI LAYER =====

    class GameZoneUI {
        -PersonService personService
        -ProductService productService
        -AccessoryService accessoryService
        -SaleService saleService
        -PromotionService promotionService
        -ReturnService returnService
        +void showMainMenu()
        +void showReturnMenu()
        +void registerReturn()
        +void listReturns()
        +void listReturnsByCustomer()
        +void listReturnsBySale()
        +void showMonthlyBalance()
    }

    %% ===== MAIN =====

    class Main {
        +main(args: String[]) void
    }

    %% ===== RETURN RELATIONSHIPS =====

    Return --> Sale : references original sale
    Return --> Product : contains returned products
    Sale "1" --> "1..*" Product : contains

    %% ===== RETURN PERSISTENCE =====

    ReturnRepository ..> Return : persists
    ReturnRepository ..> SaleService : resolves sale
    ReturnRepository ..> ProductService : resolves products

    %% ===== RETURN SERVICE =====

    ReturnService --> ReturnRepository : uses
    ReturnService --> SaleService : validates sale
    ReturnService --> ProductService : restores stock
    ReturnService --> Return : manages

    %% ===== SALE INTEGRATION =====

    Sale --> Customer : belongs to customer
    Sale --> Seller : attended by

    %% ===== UI INTEGRATION =====

    GameZoneUI ..> ReturnService : uses
    GameZoneUI ..> SaleService : accesses sales
    GameZoneUI ..> ProductService : accesses products

    %% ===== APPLICATION INTEGRATION =====

    Main ..> GameZoneUI : starts
```

## Layer Integration

The return module follows the existing layered architecture:

```text
UI -> Service -> Persistence -> Model
```

`Return` belongs to the model layer and represents a registered product return.

`ReturnRepository` belongs to the persistence layer and stores returns in `data/returns.csv`.

`ReturnService` contains the business rules for registering returns, validating the 30-day period, verifying product ownership, restoring stock, querying returns, and generating the monthly balance.

`ProductService.restoreStock(...)` is reused to increase the stock of returned products.

`Sale.canBeReturned()` provides the domain-level return period validation required by the module.

`GameZoneUI` integrates the return operations into the console interface.

The monthly balance is calculated by combining sales information from `SaleService` with return information managed by `ReturnService`.