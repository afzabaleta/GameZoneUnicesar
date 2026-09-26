# GameZoneUnicesar

## Descripción

GameZoneUnicesar es un sistema de consola desarrollado en Java para
administrar la operación de una tienda de videojuegos. Permite gestionar
el inventario de productos (videojuegos y consolas), los clientes y
vendedores de la tienda, el registro de ventas, promociones, devoluciones
y garantías.

La información se conserva entre ejecuciones mediante persistencia en
archivos.

## Tecnologías

- Java
- Maven
- IntelliJ IDEA
- Git y GitHub

## Funcionalidades

### Productos

El sistema permite registrar y listar videojuegos y consolas desde el
menú de consola.

### Personas

Permite registrar clientes y consultar clientes y vendedores registrados.

### Ventas

Permite registrar nuevas ventas asociando un cliente, un vendedor y uno o
más productos.

También permite consultar:

- Todas las ventas.
- Historial de compras de un cliente.
- Historial de ventas atendidas por un vendedor.

### Promociones

El sistema permite gestionar promociones mediante:

- Descuento porcentual.
- Descuento por categoría.
- Descuento por volumen.
- Consulta de promociones registradas.
- Consulta de promociones vigentes.
- Búsqueda de promociones por identificador.

Al registrar una venta, el sistema aplica automáticamente la promoción
activa que proporciona el mayor descuento monetario.

### Devoluciones

El sistema permite registrar devoluciones asociadas a una venta original.

Las devoluciones validan:

- El plazo máximo de 30 días.
- Que los productos pertenezcan a la venta original.
- El cálculo automático del monto reembolsado.
- La restauración del stock de los productos devueltos.

También permite:

- Consultar todas las devoluciones.
- Consultar devoluciones por cliente.
- Consultar devoluciones por venta.
- Generar un balance mensual de ventas y devoluciones.

### Garantías

Las consolas adquiridas en una venta reciben automáticamente una
garantía básica de 6 meses.

El sistema también permite solicitar una garantía extendida de 12 meses
para las consolas seleccionadas durante el registro de la venta.

La garantía extendida tiene un costo adicional equivalente al 10 % del
precio del producto.

Las garantías se almacenan de forma persistente y pueden consultarse
desde el menú de gestión de garantías.

El sistema permite:

- Consultar la garantía asociada a un producto de una venta específica.
- Listar todas las garantías registradas.
- Listar las garantías vigentes en la fecha actual.
- Listar las garantías próximas a vencer indicando los días de
  anticipación.

## Arquitectura

El sistema está organizado en cuatro capas con dependencias en un único
sentido:

- **UI**: gestiona la interacción con el usuario mediante el menú de
  consola, sin contener reglas de negocio.
- **Service**: implementa las reglas de negocio, validaciones, cálculo de
  totales, actualización de stock, promociones, devoluciones y garantías.
- **Persistence**: se encarga de guardar y recuperar la información desde
  archivos, sin contener reglas de negocio.
- **Model**: representa las entidades del dominio y su comportamiento
  propio, sin depender de otras capas.

## Persistencia

Los datos del sistema se almacenan en archivos dentro de la carpeta
`data/`.

Entre los archivos utilizados por el sistema se encuentran:

- `data/sales.txt`
- `data/warranties.csv`

Los datos se cargan automáticamente al iniciar la aplicación.

## Ejecución

La aplicación se ejecuta a partir de la clase:

```text
com.gamezone.Main
```

## Integrantes

| Integrante | Rol |
|---|---|
| Andres Felipe Zabaleta Diaz | Technical Lead — Sales + Integration |
| Sherly Michell Corrales Maestre | Developer 1 — Products |
| Diego Armando Mestre Gomez | Developer 2 — People |