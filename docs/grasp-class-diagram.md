# GRASP Class Diagram - Before and After

Classes involved in the violations of the consolidated audit
(`docs/grasp-audit/consolidated-audit.md`). The notes show the violation
ids (V-XX) related to each class. Only the attributes, methods and
relations relevant to the violations are shown.

## Before (start of Phase 2)

State of `develop` at the start of Phase 2, before the first correction.

### 1. Products, accessories and promotions

```mermaid
classDiagram
    direction TB
    class Product {
        <<abstract>>
        -String identifier
        -String title
        -double price
        -int availableQuantity
        +getDescription()* String
        +setAvailableQuantity(int) void
    }
    class VideoGame {
        -String platform
        -String genre
        -String ageRating
    }
    class Console {
        -String brand
        -String model
        -String generation
    }
    class Accessory {
        <<abstract>>
        -List~Console~ compatibleConsoles
    }
    class Controller
    class Cable
    class Memory
    class CategoryDiscount {
        -double percentage
        -String targetCategory
        +setTargetCategory(String) void
        +calculateDiscount(Sale) double
    }
    class PromotionService {
        +registerCategoryDiscount(...) void
    }
    class ProductService {
        +registerVideoGame(VideoGame) void
        +registerConsole(Console) void
        +listProducts() List~Product~
    }
    class AccessoryRepository {
        +findByType(String) List~Accessory~
        -toCsv(Accessory) String
    }
    class AccessoryService {
        -validateAccessory(Accessory) void
    }
    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory
    CategoryDiscount ..> VideoGame : instanceof
    CategoryDiscount ..> Console : instanceof
    CategoryDiscount ..> Accessory : instanceof
    PromotionService ..> CategoryDiscount : creates
    AccessoryRepository ..> Controller : instanceof
    AccessoryRepository ..> Cable : instanceof
    AccessoryRepository ..> Memory : instanceof
    AccessoryService --> AccessoryRepository
    ProductService ..> Product
    note for CategoryDiscount "V-09: instanceof to find the category"
    note for CategoryDiscount "V-11: valid categories also in PromotionService"
    note for PromotionService "V-11: duplicates the category list"
    note for AccessoryRepository "V-10: findByType uses instanceof"
    note for AccessoryService "V-12: validation rules inside the service"
    note for ProductService "V-13: two identical register methods<br>V-08: no query for consoles"
    note for Console "V-03: warranty rule written as instanceof Console elsewhere"
```

### 2. Sales and user interface

```mermaid
classDiagram
    direction TB
    class GameZoneUI {
        -registerSale() void
        -findCustomerById(String) Customer
        -findSellerById(String) Seller
        -findItemById(String) Product
        -getAvailableConsoles() List~Console~
        -selectExtendedWarrantyProducts(List) List
    }
    class SaleService {
        +registerSale(Sale, List~String~) void
        -assignWarranties(Sale, Set) void
        -countProductQuantities(List) Map
        -countAccessoryQuantities(List) Map
        -validateProductStock(Map, List) void
        -validateAccessoryStock(Map, List) void
        -updateProductStock(Map, List) void
        -updateAccessoryStock(Map, List) void
    }
    class Sale {
        -LocalDate date
        -double discountAmount
        -double warrantyAdditionalCost
        +calculateSubtotal() double
        +calculateTotal() double
    }
    class SaleRepository {
        -findCustomerById(String) Customer
        -findSellerById(String) Seller
        -findItemById(String) Product
    }
    class SaleValidator {
        +validateSaleData(Customer, Seller, List) void
    }
    class PersonService {
        +listCustomers() List~Customer~
        +listSellers() List~Seller~
    }
    class ProductService
    class AccessoryService
    class PersonRepository
    class ProductRepository
    class AccessoryRepository
    GameZoneUI --> SaleService
    GameZoneUI --> PersonService
    GameZoneUI --> ProductService
    GameZoneUI --> AccessoryService
    GameZoneUI ..> Sale : creates
    SaleService --> SaleRepository
    SaleService --> ProductService
    SaleService --> AccessoryService
    SaleService ..> SaleValidator
    SaleRepository --> PersonRepository
    SaleRepository --> ProductRepository
    SaleRepository --> AccessoryRepository
    note for GameZoneUI "V-14: creates Sale with LocalDate.now()"
    note for GameZoneUI "V-07: searches customers and sellers<br>V-08: filters consoles"
    note for GameZoneUI "V-02: findItemById<br>V-03: instanceof Console"
    note for GameZoneUI "V-15: 2,540 lines, repeated catch blocks"
    note for SaleService "V-02: product/accessory pairs by instanceof<br>V-03: instanceof Console"
    note for SaleService "V-17: re-validates products"
    note for SaleRepository "V-16: depends on the load methods of 3 repositories"
```

### 3. Returns and warranties

```mermaid
classDiagram
    direction TB
    class Return {
        -Sale originalSale
        -List~Product~ returnedProducts
        -double refundAmount
        +calculateRefundAmount() double
        +generateReturnReceipt() String
    }
    class Sale {
        -LocalDate date
        -double discountAmount
        +calculateSubtotal() double
        +getDiscountAmount() double
    }
    class ReturnService {
        +registerReturn(String, List, String) Return
        +calculateMonthlySales(int, int) double
        -cancelReturnedConsoleWarranties(String, List) double
        -restoreReturnedStock(List) void
        -findSale(String) Sale
        -findProduct(String) Product
    }
    class ReturnRepository {
        -SaleService saleService
        -ProductService productService
        -AccessoryService accessoryService
        -findSaleByReference(String) Sale
        -findItemById(String) Product
    }
    class WarrantyService {
        +cancelWarranties(String, String) double
        -findSale(List, String) Sale
    }
    class WarrantyRepository {
        -toCsvLine(Warranty) String
    }
    class SaleService
    class ProductService
    class AccessoryService
    Return --> Sale
    ReturnService --> ReturnRepository
    ReturnService --> SaleService
    ReturnService --> ProductService
    ReturnService --> AccessoryService
    ReturnService --> WarrantyService
    ReturnRepository --> SaleService
    ReturnRepository --> ProductService
    ReturnRepository --> AccessoryService
    WarrantyService --> WarrantyRepository
    note for Return "V-05: computes the discount share from Sale data, twice"
    note for ReturnService "V-02: instanceof Accessory to restore stock"
    note for ReturnService "V-03: instanceof Console"
    note for ReturnService "V-04: sale reference as getDate().toString()"
    note for ReturnService "V-06: adds up sales totals"
    note for ReturnRepository "V-01: persistence depends on services"
    note for ReturnRepository "V-04: sale reference as getDate().toString()"
    note for WarrantyService "V-04: sale reference as getDate().toString()"
    note for WarrantyRepository "V-04: sale reference as getDate().toString()"
    note for Sale "V-04, V-05: information used by other classes"
```

## After (end of Phase 2)

Recorded by the Technical Lead at the end of Phase 2.
