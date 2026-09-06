S## People in the system

### 1. Common vs. specific attributes
All people who interact with the store share three basic identity attributes:
name, identification number, and phone number. These belong to the base
class `Person`.

Each role adds its own specific attributes:
- **Customer**: email address and purchase history.
- **Seller**: employee code and assigned work shift.

This distinction is modeled as an inheritance hierarchy: `Person` is the
abstract base class, and `Customer` and `Seller` are its concrete
subclasses, each extending it with role-specific attributes.

### 2. Should there be an instantiable generic "Person" class?
No. In the real business context, every person who interacts with the
store is either a customer or a seller — a role-less person has no
meaning in this domain. Allowing `Person` to be instantiated would let
the system create objects with no business purpose.

Therefore, `Person` must be declared as an **abstract class**. This
enforces that only its concrete subclasses (`Customer`, `Seller`) can be
instantiated, while still allowing `Person` to define the shared
structure and behavior. This reflects the OOP principle of
**abstraction**: the base class models what is common without
representing a real, standalone entity.

## Products in the system

### 3. Common vs. specific characteristics
Every product the store sells shares four basic attributes: identifier,
title, price, and available quantity. These belong to the base class
`Product`.

Each product type adds its own specific characteristics:
- **VideoGame**: platform, genre, and age rating.
- **Console**: brand, model, and generation.

This is modeled as an inheritance hierarchy: `Product` is the abstract
base class, and `VideoGame` and `Console` are its concrete subclasses,
each extending it with type-specific attributes.

### 4. Declaring the description behavior in the base class
Since each product type must present a description that integrates its
own particular characteristics, `Product` declares an **abstract
method**:

    public abstract String getDescription();

This forces every concrete subclass to provide its own implementation,
or the class will not compile. `VideoGame` overrides it to include
platform, genre, and age rating; `Console` overrides it to include
brand, model, and generation.

This is an application of **abstraction** together with **polymorphism**:
the base class defines the contract (that a description must exist)
without knowing how to build it, while each subclass supplies its own
behavior. At runtime, calling `getDescription()` on a `Product`
reference automatically executes the correct subclass implementation
based on the object's real type, with no need for type-checking
conditionals.

## Sales and relationships between entities

### 5. Relationships between Sale and other classes
`Sale` does not inherit from `Customer`, `Seller`, or `Product` — a sale
is not a kind of customer, seller, or product, so inheritance does not
apply. Instead, `Sale` **uses** them: it holds a reference to one
`Customer`, one `Seller`, and a list of `Product` instances.

This is a relationship of **association**: `Sale` depends on these
classes to represent a transaction, but none of them are owned
exclusively by the sale, nor do they cease to exist independently if a
sale is removed. This rules out composition, which would imply that the
related objects cannot exist without the container.

### 6. Should Sale calculate its own total?
Yes. The total depends exclusively on data the `Sale` object already
holds (its list of products), so calculating it is part of the object's
own responsibility over its own state. `SaleService` is responsible for
deciding *when* the total is calculated (when the sale is registered),
but the calculation itself belongs inside `Sale`, keeping the
domain class self-contained and easy to test in isolation.

## Business rules

### 7. Guaranteeing a sale has at least one product
`Sale` represents the sale and its own domain behavior (such as
calculating its total), but the rule that determines *whether* a sale
can be registered belongs to `SaleService`, in the service layer. Before
persisting a new sale, `SaleService` validates that the list of products
is not empty; if it is, the registration is rejected and the persistence
layer is never called. The user interface only collects the data — it
does not decide whether the operation is valid.

### 8. Automatic inventory update
This operation involves two classes: `SaleService`, which coordinates
the sale registration, and `ProductService`, which updates the stored
quantity of each product sold. When a sale is registered, `SaleService`
checks that there is enough stock and then asks `ProductService` to
decrease the quantity of each product involved, instead of modifying
`Product` data directly itself.

## Layered architecture

### 9. Classes per layer
- **model**: domain entities that represent business concepts and their
  own behavior (`Person`, `Customer`, `Seller`, `Product`, `VideoGame`,
  `Console`, `Sale`).
- **persistence**: classes that read and write data to files
  (`PersonRepository`, `ProductRepository`, `SaleRepository`).
- **service**: classes that implement business rules and orchestrate
  operations across modules (`PersonService`, `ProductService`,
  `SaleService`).
- **ui**: classes that interact with the user through the console menu.

The criterion for placing a class is asking what its responsibility is:
representing a business concept, storing/retrieving data, deciding a
business rule, or talking to the user.

### 10. Why persistence logic must stay out of the domain classes
If `model` classes contained file-reading or file-writing logic, any
change to the storage format (for example, switching from plain text
to CSV) would force changes inside business entities that have nothing
to do with storage. It would also make the domain classes harder to
reuse and test in isolation, since they would carry a second
responsibility that changes for different reasons than the business
rules they represent. Keeping persistence separate follows the
principle that each class should have a single reason to change.

### 11. Allowed and forbidden dependencies between layers
Allowed dependencies point "inward": `ui → service`, `service → model`,
`service → persistence`, and `persistence → model`. `model` depends on
nothing else.

Forbidden dependencies are any that point in the opposite direction —
`model` depending on `service`, `persistence`, or `ui` — and any
dependency that skips a layer, such as `ui` calling `persistence`
directly. This sense of dependency keeps the domain (`model`) isolated
and reusable, and ensures that changes in the interface or in storage
never ripple into the core business logic.