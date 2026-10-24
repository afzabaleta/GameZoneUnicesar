# Exception Handling Analysis

## Introduction

The GameZone Unicesar system was extended with a dedicated validation
layer and a hierarchy of custom exceptions to improve error handling.

Before this change, several services used generic exceptions to report
different problems, including invalid input, violations of business
rules, missing resources, and persistence failures.

The new design separates these error categories and allows the user
interface to display a meaningful message for each type of failure.

The implementation introduces the following components:

- `GameZoneException`
- `ResourceNotFoundException`
- `BusinessRuleException`
- `InvalidDataException`
- `PersistenceException`
- `ProductValidator`
- `PersonValidator`
- `SaleValidator`

The existing services, repositories, and console interface are
refactored to use these components without changing the system's
existing business functionality.

---

## 1. Should GameZoneException Be Checked or Unchecked?

`GameZoneException` should extend `RuntimeException`, making it an
unchecked exception.

This choice is appropriate because the custom exceptions represent
validation failures, business rule violations, missing resources, and
persistence problems that cannot always be handled meaningfully inside
the method where they originate.

Using an unchecked exception allows services and repositories to
report failures without declaring every possible exception in their
method signatures.

For example, a service can report that a product does not exist by
throwing `ResourceNotFoundException`. The exception can propagate to
the user interface, where it is handled according to its specific type.

If `GameZoneException` extended `Exception`, it would become a checked
exception. Methods that propagate it would generally need to declare it
with `throws` or handle it locally. This would introduce additional
exception-handling requirements throughout the application.

Using `RuntimeException` simplifies these method signatures while
preserving explicit handling at the appropriate architectural boundary.

Unchecked exceptions must still be documented, understood, and tested.
The choice does not remove the responsibility of handling errors.

---

## 2. Why Use a Custom Exception Hierarchy?

A custom exception hierarchy is preferable to using
`IllegalArgumentException` for every failure because different errors
require different handling strategies.

The hierarchy provides four specialized exception types:

- `ResourceNotFoundException` identifies a requested resource that
  does not exist.
- `BusinessRuleException` identifies an operation that violates a
  business rule.
- `InvalidDataException` identifies input that does not satisfy the
  required format or range.
- `PersistenceException` identifies a failure while reading or writing
  persistent data.

All four inherit from `GameZoneException`, which provides a common
parent type and an error code.

This design improves error classification, readability, and
maintainability.

It also supports several object-oriented design principles.

### Single Responsibility Principle

Each exception communicates a specific category of failure instead of
combining unrelated problems in one generic exception.

### Abstraction and Polymorphism

The common parent class provides a shared abstraction for application
errors. The user interface can handle individual subclasses differently
while treating them as part of the same exception hierarchy.

### Open/Closed Principle

Additional exception categories can be introduced as subclasses
without requiring the entire exception-handling architecture to be
replaced.

The hierarchy therefore makes the system easier to understand,
maintain, and extend.

---

## 3. Where Should Validators Be Invoked?

The validators are invoked by the service layer before the corresponding
business operation continues.

For example, when registering a product, `ProductService` delegates
the validation of its identifier, title, price, and stock to
`ProductValidator`.

When registering a customer, `PersonService` delegates data validation
and identifier uniqueness checks to `PersonValidator`.

When registering a sale, `SaleService` uses `SaleValidator` to validate
the required customer, seller, and product information. It also uses
`ProductValidator` when product existence and stock availability must
be checked.

This organization separates validation rules from the services'
business coordination responsibilities.

The validation layer is logically located between the service layer
and the domain model:

```text
User Interface
      |
      v
Service Layer
      |
      v
Validation Layer
      |
      v
Domain Model
```

The repositories remain responsible for persistence operations.

Validators must not access the persistence package or perform file
operations. Instead, services obtain the necessary objects from the
appropriate collaborators and pass them to the validators.

For example, `validateProductExists(Product product, String productId)`
checks a product reference already obtained by the service. It does not
query a repository itself.

This arrangement respects the application's layered architecture,
reduces coupling, and makes validation rules easier to test.

---

## 4. How Is Existing Behaviour Preserved?

The refactoring must improve error reporting without changing the
existing business functionality.

The strategy is to preserve the successful operation flows and replace
the generic error-reporting mechanism with specific exception types.

The following principles guide the refactoring:

1. Existing public operations remain available.
2. The menu continues to expose the existing ten operations.
3. The same invalid conditions continue to cause an error.
4. Existing business rules, inventory rules, and sale calculations
   remain unchanged.
5. Services delegate validation to the dedicated validators.
6. Repositories translate persistence failures into
   `PersistenceException` while retaining the original cause.
7. The user interface displays messages according to the exception type.

The team must verify this behaviour through automated and manual tests.

The Maven test suite should be executed with:

```bash
mvn clean test
```

The team should also perform regression tests covering the existing
menu operations, including product management, people management,
sales, accessories, promotions, warranties, and returns.

Additional negative tests should verify missing resources, invalid
data, duplicate identifiers, insufficient stock, and persistence
failures.

The evidence should include successful compilation, passing automated
tests, documented manual test results, and a review of the changes
introduced by the refactoring.

These checks provide evidence that the new exception-handling design
does not break the existing system.

---

## 5. How Should the User Interface Handle the Exceptions?

The user interface should use a single `try` block with multiple
specific `catch` blocks.

Each block handles one custom exception type and provides a message
appropriate to that category of failure.

The order should place the specialized exception handlers before any
general runtime-exception handler.

This approach is preferable to using only `catch (GameZoneException)`
because a single general handler would not provide the same explicit
differentiation between resource errors, business rule violations,
invalid input, and persistence failures.

It is also clearer than duplicating operations across multiple
independent `try` blocks.

The expected messages are:

- `ResourceNotFoundException`:
  "No se encontró el recurso solicitado: <detalle>"

- `BusinessRuleException`:
  "No se puede completar la operación: <detalle>"

- `InvalidDataException`:
  "Los datos ingresados no son válidos: <detalle>"

- `PersistenceException`:
  "Ocurrió un problema al acceder a los datos del sistema. Contacte
  al administrador."

Persistence messages should not expose raw stack traces, filesystem
details, or other technical information to the user.

The original exception and its cause should remain available internally
for debugging and diagnosis.

The user interface should also retain appropriate handling for
unexpected runtime errors and invalid numeric input.

Using multiple specific `catch` blocks improves readability and makes
the error-handling behaviour explicit.

---

## Conclusion

The new exception-handling architecture separates validation rules,
business errors, missing resources, and persistence failures into
distinct categories.

The custom exception hierarchy provides a common abstraction, while
the dedicated validators centralize checks that were previously
embedded in service methods.

The repositories provide contextual information when persistence
operations fail, and the user interface translates known exception
types into understandable Spanish messages.

Together, these changes improve maintainability and error reporting
while preserving the existing functionality of GameZone Unicesar.