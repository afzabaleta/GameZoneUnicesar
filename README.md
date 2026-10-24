# GameZoneUnicesar

## Descripción

GameZoneUnicesar es un sistema de consola desarrollado en Java para
administrar la operación integrada de una tienda de videojuegos.

El sistema permite gestionar personas, productos, accesorios, ventas,
promociones, garantías y devoluciones, manteniendo la información mediante
persistencia en archivos.

La integración permite que los diferentes módulos trabajen de manera
coherente dentro del mismo flujo de negocio.

## Tecnologías

- Java
- Maven
- IntelliJ IDEA
- Git
- GitHub

## Funcionalidades

### Productos

El sistema permite registrar y consultar productos del catálogo, incluyendo:

- Videojuegos.
- Consolas.

Cada producto mantiene información como identificador, título, precio y
cantidad disponible.

### Accesorios

El sistema permite gestionar accesorios compatibles con diferentes
consolas.

Entre los accesorios disponibles se encuentran:

- Cables.
- Controles.
- Memorias.

Los accesorios poseen inventario independiente y pueden participar en las
ventas y promociones.

### Personas

El sistema permite gestionar:

- Clientes.
- Vendedores.

Los clientes pueden estar asociados a sus compras y los vendedores a las
ventas que atienden.

### Ventas

El sistema permite registrar ventas asociando:

- Un cliente.
- Un vendedor.
- Uno o más productos o accesorios.

Durante el registro de una venta el sistema:

1. Valida que exista al menos un producto o accesorio.
2. Verifica la disponibilidad del inventario.
3. Calcula el subtotal.
4. Busca automáticamente la mejor promoción vigente.
5. Calcula el descuento sobre el subtotal.
6. Genera la garantía correspondiente para las consolas.
7. Agrega el costo de las garantías extendidas solicitadas.
8. Calcula el total final.
9. Actualiza el inventario correspondiente.
10. Persiste la venta y las garantías.

El total final de una venta se calcula como:

```text
subtotal - discount + extended warranty cost
```

El sistema también permite consultar:

- Todas las ventas.
- Historial de compras de un cliente.
- Historial de ventas atendidas por un vendedor.

### Promociones

El sistema permite gestionar diferentes tipos de promociones:

- Descuento porcentual.
- Descuento por categoría.
- Descuento por volumen.

Los descuentos por categoría pueden aplicarse a:

- `VIDEOGAME`
- `CONSOLE`
- `ACCESSORY`

Al registrar una venta, el sistema busca la promoción vigente que genere
el mayor descuento monetario.

El sistema también permite:

- Listar todas las promociones.
- Listar promociones vigentes.
- Buscar promociones por identificador.

### Garantías

Las consolas adquiridas mediante una venta reciben automáticamente una
garantía básica de 6 meses.

El sistema también permite solicitar una garantía extendida de 12 meses
para las consolas seleccionadas durante el registro de la venta.

La garantía extendida tiene un costo adicional equivalente al 10 % del
precio de la consola.

El sistema permite:

- Consultar la garantía asociada a un producto de una venta.
- Listar todas las garantías.
- Listar las garantías vigentes.
- Listar garantías próximas a vencer.
- Cancelar las garantías asociadas a una consola devuelta.

Cuando se devuelve una consola:

- La garantía básica no genera un valor adicional de reembolso.
- La garantía extendida devuelve su costo adicional correspondiente.

### Devoluciones

El sistema permite registrar devoluciones asociadas a una venta original.

Las devoluciones validan:

- El plazo máximo de 30 días.
- Que los productos pertenezcan a la venta original.
- Los identificadores de los productos devueltos.
- El motivo de la devolución.

Durante una devolución el sistema:

1. Calcula el reembolso proporcional al descuento aplicado en la venta
   original.
2. Cancela las garantías asociadas a las consolas devueltas.
3. Incluye el valor reembolsable de las garantías extendidas.
4. Restaura el inventario de productos y accesorios.
5. Persiste la devolución.

Los productos devueltos pueden ser productos normales o accesorios.

El recibo de devolución muestra:

- Precio de lista.
- Descuento proporcional.
- Monto reembolsado por producto.
- Reembolso de garantía.
- Valor total de la devolución.

El sistema también permite:

- Consultar todas las devoluciones.
- Consultar devoluciones por cliente.
- Consultar devoluciones por venta.

### Balance mensual

El sistema permite consultar el balance mensual de ventas y devoluciones.

El reporte muestra:

- Total de ventas.
- Total de devoluciones.
- Balance neto.

El total de ventas utiliza el valor final de cada venta, incluyendo
descuentos y costos de garantías extendidas.

El balance neto se calcula como:

```text
total sales - total returns
```

## Arquitectura

El sistema está organizado en cuatro capas:

### UI

La interfaz delega las operaciones a la capa de servicios y no accede
directamente a los repositorios.

`GameZoneUI` muestra el menú principal y cada módulo tiene su propio menú:
`ProductMenu`, `AccessoryMenu`, `PersonMenu`, `SaleMenu`, `PromotionMenu`,
`ReturnMenu` y `WarrantyMenu`. Los mensajes de error se muestran desde
`ConsoleErrorHandler`.

### Service

Contiene las reglas de negocio, validaciones y coordinación entre módulos.

Incluye servicios para:

- Personas.
- Productos.
- Accesorios.
- Promociones.
- Ventas.
- Garantías.
- Devoluciones.

Los servicios pueden coordinarse entre sí cuando el flujo de negocio lo
requiere.

Por ejemplo:

- `SaleService` coordina productos, accesorios, promociones y garantías.
- `ReturnService` coordina ventas, productos, accesorios y garantías.
- `WarrantyService` resuelve las referencias de ventas y productos.

### Persistence

Se encarga de guardar y recuperar la información desde archivos.

Los repositorios no contienen las reglas principales del negocio y se
encargan de la persistencia de sus respectivos módulos.

### Model

Representa las entidades del dominio y sus comportamientos propios.

Entre las principales entidades se encuentran:

- `Person`
- `Customer`
- `Seller`
- `Product`
- `VideoGame`
- `Console`
- `Accessory`
- `Cable`
- `Controller`
- `Memory`
- `Sale`
- `Promotion`
- `PercentageDiscount`
- `CategoryDiscount`
- `BulkPurchaseDiscount`
- `Warranty`
- `BasicWarranty`
- `ExtendedWarranty`
- `Return`

## Persistencia

Los datos del sistema se almacenan en archivos dentro de la carpeta
`data/`.

La persistencia permite conservar la información entre ejecuciones de la
aplicación.

Los diferentes módulos utilizan repositorios para cargar y guardar sus
respectivos datos.

## Ejecución

La aplicación se ejecuta a partir de la clase:

```text
com.gamezone.Main
```

El proyecto utiliza Maven para la compilación y ejecución de las pruebas.

Para verificar el proyecto se puede ejecutar:

```bash
mvn clean test
```

## Documentación

La carpeta `docs/` contiene la documentación técnica y los diagramas del
sistema.

Entre los principales documentos se encuentran:

- `docs/analysis.md`
- `docs/class-diagram.md`
- `docs/layers-diagram.md`
- `docs/integration-analysis.md`
- `docs/integrated-class-diagram.md`

## Integración

La integración del sistema se realizó mediante los siguientes ajustes:

- **A1:** Descuentos por categoría para accesorios.
- **A2:** Eliminación de la dependencia circular del módulo de garantías.
- **A3:** Unificación del flujo de registro de ventas.
- **A4:** Restauración del inventario de accesorios en devoluciones.
- **A5:** Cálculo proporcional del reembolso de ventas con descuento.
- **A6:** Reporte completo del balance mensual.
- **A7:** Cancelación y reembolso de garantías al devolver consolas.
- **A8:** Documentación de la integración.
- **A9:** Publicación de la versión integrada.

## Estructura del proyecto

```text
GameZoneUnicesar/
├── data/
├── docs/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── gamezone/
│                   ├── exceptions/
│                   ├── model/
│                   ├── persistence/
│                   ├── service/
│                   ├── ui/
│                   ├── validation/
│                   └── Main.java
├── pom.xml
└── README.md
```
## Exception Handling and Validation

The GameZone Unicesar system includes a dedicated exception-handling
and validation layer to improve error management and maintainability.

### Custom Exceptions

The package `com.gamezone.exceptions` contains the following classes:

- `GameZoneException`: root exception for application errors.
- `ResourceNotFoundException`: represents resources that cannot be found.
- `BusinessRuleException`: represents violations of business rules.
- `InvalidDataException`: represents invalid input data.
- `PersistenceException`: represents errors when reading or writing files.

The custom exception hierarchy allows the application to distinguish
between different categories of errors.

### Validation Layer

The package `com.gamezone.validation` contains:

- `ProductValidator`: validates product information and stock availability.
- `PersonValidator`: validates person information and identifier uniqueness.
- `SaleValidator`: validates sale information and required references.

The services use these validators to separate validation responsibilities
from the main business logic.

### Error Handling

The menus of the `ui` package catch the custom exceptions and the
`ConsoleErrorHandler` class shows a different message to users according
to the type of error.

The application distinguishes between:

- Missing resources.
- Business rule violations.
- Invalid input data.
- Persistence failures.

User-facing error messages are displayed in Spanish.

### Related Documentation

The following documents describe the design and implementation:

- `docs/exception-analysis.md`
- `docs/exception-class-diagram.md`

## Requirement 7 - GRASP Audit and Correction

This requirement does not add menu operations. The team audited the code
against the nine GRASP patterns (Phase 1) and corrected the violations
found, without changing what the users see (Phase 2).

### Phase 1 - Audit

- Individual reports: `docs/grasp-audit/leader-audit.md`,
  `docs/grasp-audit/developer1-audit.md` and
  `docs/grasp-audit/developer2-audit.md`.
- Consolidated audit: `docs/grasp-audit/consolidated-audit.md`, with the
  coverage of the 43 classes, the team verdict for each pattern, the 17
  definitive violations (V-01 to V-17), the boundary findings and five
  out-of-scope functional defects (OOS-01 to OOS-05).
- Guiding questions: `docs/grasp-analysis.md`.

### Phase 2 - Design changes

- **Polymorphism:** `Product.getCategory()`, `Product.isWarrantyEligible()`
  and `Accessory.getAccessoryType()` replace the `instanceof` checks
  (V-03, V-09, V-10).
- **Information Expert:** `Sale` gives its reference and its discount share
  (V-04, V-05); `SaleService.calculateMonthlySales` (V-06);
  `PersonService.findCustomerById/findSellerById` (V-07);
  `ProductService.listConsoles` (V-08);
  `CategoryDiscount.validateTargetCategory` (V-11).
- **Pure Fabrication:** `InventoryService` keeps the only product/accessory
  decision (V-02); `AccessoryValidator` (V-12); the blank identifier rule
  lives in `SaleValidator` (V-17); `ConsoleErrorHandler` shows the domain
  error messages (V-15).
- **Creator:** `SaleService` creates each `Sale` (V-14).
- **Low Coupling and Indirection:** `ReturnRepository` reads and writes a
  `ReturnRecord` and no longer depends on services (V-01);
  `SaleRepository` receives lookup functions instead of three repositories
  (V-16).
- **High Cohesion:** the console UI is split into one menu per module;
  `GameZoneUI` keeps only the main menu (V-15).
- **Protected Variations:** one `ProductService.registerProduct(Product)`
  for every kind of product (V-13).

New classes: `service.InventoryService`, `validation.AccessoryValidator`,
`persistence.ReturnRecord`, `ui.ConsoleErrorHandler`, `ui.ProductMenu`,
`ui.AccessoryMenu`, `ui.PersonMenu`, `ui.SaleMenu`, `ui.PromotionMenu`,
`ui.ReturnMenu` and `ui.WarrantyMenu`.

### Non-regression

`docs/regression/` contains one input script per menu area, a copy of the
initial data and a runner. From Git Bash, after `mvn clean compile`:

```bash
bash docs/regression/run-regression.sh <label>
git diff --no-index --stat docs/regression/results/baseline docs/regression/results/<label>
```

The baseline (before Phase 2) and the final run are identical. The
strategy and the results are in `docs/grasp-refactor-log.md`.

### Related documentation

- `docs/grasp-class-diagram.md`: class diagrams before and after.
- `docs/grasp-refactor-log.md`: one row per change, with violation,
  pattern, commit, Pull Request and member.
- `docs/ai-usage/`: AI usage log of each member, Phase 1 and Phase 2.



## Integrantes

| Integrante | Rol |
|---|---|
| Andres Felipe Zabaleta Diaz | Technical Lead — Sales + Integration |
| Sherly Michell Corrales Maestre | Developer 1 — Products |
| Diego Armando Mestre Gomez | Developer 2 — People |