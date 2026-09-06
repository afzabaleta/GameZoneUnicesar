# Team

## Members

| Member | Student ID | Role | Module |
|---|---|---|---|
| Andres Felipe Zabaleta Diaz | 1080574183 | Technical Lead | Sales + Integration |
| Sherly Michell Corrales Maestre | 1119837499 | Developer 1 | Products |
| Diego Armando Mestre Gomez | 1063590824 | Developer 2 | People |

## Class distribution

Based on the class diagram produced in `docs/class-diagram.md`, following
the vertical-cut-by-module criterion.

**Andres Felipe Zabaleta Diaz — Technical Lead (5 classes)**
- `Sale` (model)
- `SaleRepository` (persistence)
- `SaleService` (service)
- `ConsoleUI` (ui)
- `Main`

**Sherly Michell Corrales Maestre — Developer 1 (5 classes)**
- `Product` (abstract, model)
- `VideoGame` (model)
- `Console` (model)
- `ProductRepository` (persistence)
- `ProductService` (service)

**Diego Armando Mestre Gomez — Developer 2 (5 classes)**
- `Person` (abstract, model)
- `Customer` (model)
- `Seller` (model)
- `PersonRepository` (persistence)
- `PersonService` (service)

## Committed activities per member

### Technical Lead
1. Create the GitHub repository with initial configuration (README, .gitignore, license).
2. Configure the `main` and `develop` branches and enable branch protection.
3. Configure the Maven project with the initial `pom.xml` and the four-layer package structure.
4. Write `TEAM.md` with team information, assigned roles, and class distribution.
5. Implement the `Sale` domain class with its attributes, constructor, and basic methods.
6. Implement the sale total calculation method.
7. Implement the `SaleRepository` persistence class.
8. Implement the `SaleService` class with validation rules (minimum one product, stock check, inventory update).
9. Implement the basic structure of `ConsoleUI` (main menu).
10. Implement the submenus of `ConsoleUI` for each of the three modules.
11. Implement the `Main` class with initial data loading and dependency wiring.
12. Review and merge developers' Pull Requests into `develop`.
13. Write the final `README.md` with build and run instructions.

### Developer 1 — Products
1. Create the `feature/product-model` branch.
2. Implement the abstract base class `Product` with common attributes, constructor, and common methods.
3. Declare the abstract `getDescription()` method that subclasses must implement.
4. Implement `VideoGame` with its own attributes and its `getDescription()` implementation.
5. Implement `Console` with its own attributes and its `getDescription()` implementation.
6. Implement `ProductRepository` with save/load methods from files.
7. Implement `ProductService` with registration, listing, and stock update methods.
8. Document all module classes with JavaDoc in English.
9. Request Pull Requests to the Technical Lead for module integration.

### Developer 2 — People
1. Create the `feature/person-model` branch.
2. Implement the abstract base class `Person` with common attributes, constructor, and common methods.
3. Implement `Customer` with its own attributes.
4. Implement `Seller` with its own attributes.
5. Implement `PersonRepository` with save/load methods, including at least three preloaded sellers.
6. Implement `PersonService` with registration and listing methods.
7. Document all module classes with JavaDoc in English.
8. Request Pull Requests to the Technical Lead for module integration.

## Feature branches

| Developer | Branch |
|---|---|
| Technical Lead | `feature/sale-module`, `feature/ui-integration` |
| Developer 1 | `feature/product-model` |
| Developer 2 | `feature/person-model` |