# Hierarchy Diagram

This diagram represents the inheritance hierarchies identified in the analysis.
It includes only generalization/specialization relationships between classes
in the `model` layer.

```mermaid
classDiagram
    class Person {
        <<abstract>>
    }

    class Customer
    class Seller

    Person <|-- Customer
    Person <|-- Seller

    class Product {
        <<abstract>>
    }

    class VideoGame
    class Console

    Product <|-- VideoGame
    Product <|-- Console
```