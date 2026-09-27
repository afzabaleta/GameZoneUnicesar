# Warranty Class Diagram

Class diagram for the warranty module after the dependency decoupling
implemented in R5 A2.

```mermaid
classDiagram

    %% ===== MODEL =====

    class Product {
        <<abstract>>
        -identifier : String
        -title : String
        -price : double
        -availableQuantity : int
        +getIdentifier() String
    }

    class Sale {
        -date : LocalDate
        -customer : Customer
        -seller : Seller
        -products : List~Product~
        +getDate() LocalDate
        +getProducts() List~Product~
    }

    class Warranty {
        <<abstract>>
        -id : String
        -product : Product
        -sale : Sale
        -startDate : LocalDate
        +getId() String
        +getProduct() Product
        +getSale() Sale
        +getStartDate() LocalDate
    }

    class BasicWarranty {
    }

    class ExtendedWarranty {
    }

    %% ===== PERSISTENCE =====

    class WarrantyRepository {
        -filePath : Path
        +saveAll(warranties: List~Warranty~) void
        +loadAll() List~WarrantyRecord~
    }

    class WarrantyRecord {
        +type : String
        +id : String
        +productId : String
        +saleId : String
        +startDate : LocalDate
    }

    %% ===== SERVICES =====

    class WarrantyService {
        -repository : WarrantyRepository
        -saleRepository : SaleRepository
        -productService : ProductService
        -warranties : List~Warranty~
        +assignBasicWarranty(product: Product, sale: Sale, startDate: LocalDate) BasicWarranty
        +assignExtendedWarranty(product: Product, sale: Sale, startDate: LocalDate) ExtendedWarranty
        +findWarrantyByProduct(productId: String, saleId: String) Warranty
        +listAllWarranties() List~Warranty~
        +listActiveWarranties() List~Warranty~
        +listWarrantiesExpiringSoon(daysAhead: int) List~Warranty~
    }

    class SaleRepository {
        +loadSales() List~Sale~
    }

    class ProductService {
        +listProducts() List~Product~
    }

    %% ===== INHERITANCE =====

    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty

    %% ===== WARRANTY REFERENCES =====

    Warranty --> Product : references
    Warranty --> Sale : references

    %% ===== PERSISTENCE =====

    WarrantyRepository ..> Warranty : saves
    WarrantyRepository ..> WarrantyRecord : loads
    WarrantyRepository ..> WarrantyRecord : creates

    %% ===== SERVICE DEPENDENCIES =====

    WarrantyService ..> WarrantyRepository
    WarrantyService ..> SaleRepository
    WarrantyService ..> ProductService

    %% ===== RESOLUTION =====

    WarrantyService ..> Sale : resolves references
    WarrantyService ..> Product : resolves references