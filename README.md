# GameZoneUnicesar

## Descripción

GameZoneUnicesar es un sistema de consola desarrollado en Java para
administrar la operación de una tienda de videojuegos. Permite gestionar
el inventario de productos (videojuegos y consolas), los clientes y
vendedores de la tienda, y el registro de ventas asociadas a ellos. La
información se conserva entre ejecuciones mediante persistencia en
archivos.

## Tecnologías

- Java
- Maven
- IntelliJ IDEA
- Git y GitHub

## Funcionalidades

El sistema permite registrar y listar videojuegos, consolas, clientes y
vendedores desde un menú de consola. También permite registrar nuevas
ventas asociando un cliente, un vendedor y uno o más productos, y
consultar el historial completo de ventas, así como el historial de
compras de un cliente específico y el de ventas atendidas por un
vendedor específico.

El sistema también permite gestionar promociones, incluyendo el
registro, consulta y aplicación automática de la promoción activa que
representa el mayor descuento monetario para una venta.

Adicionalmente, el sistema permite gestionar devoluciones de productos.
Una devolución se asocia a una venta original y puede incluir solamente
algunos de los productos de esa venta. El sistema valida el plazo máximo
de 30 días, verifica que los productos pertenezcan a la venta original,
calcula automáticamente el monto reembolsado y restaura el stock de los
productos devueltos.

El módulo de devoluciones también permite consultar todas las
devoluciones registradas, consultar las devoluciones de un cliente o de
una venta específica y generar un balance mensual que representa la
diferencia entre las ventas y las devoluciones del período.

## Arquitectura

El sistema está organizado en cuatro capas con dependencias en un único
sentido:

- **UI**: gestiona la interacción con el usuario a través del menú de
  consola, sin contener reglas de negocio.
- **Service**: implementa las reglas de negocio (validaciones, cálculo
  de totales, actualización de stock) y coordina el uso de la persistencia.
- **Persistence**: se encarga de guardar y recuperar la información desde
  archivos, sin conocer reglas de negocio.
- **Model**: representa las entidades del dominio (productos, personas,
  ventas) y su comportamiento propio, sin depender de ninguna otra capa.

## Ejecución

La aplicación se ejecuta a partir de la clase `com.gamezone.Main`. Los
datos del sistema se almacenan en archivos dentro de la carpeta `data/`
y se cargan al iniciar la aplicación.

## Integrantes

| Integrante | Rol |
|---|---|
| Andres Felipe Zabaleta Diaz | Technical Lead — Sales + Integration |
| Sherly Michell Corrales Maestre | Developer 1 — Products |
| Diego Armando Mestre Gomez | Developer 2 — People |