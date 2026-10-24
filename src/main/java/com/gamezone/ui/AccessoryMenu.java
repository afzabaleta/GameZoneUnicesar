package com.gamezone.ui;

import com.gamezone.exceptions.GameZoneException;
import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Console;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console menu of the accessory module: register, list and search
 * accessories.
 */
public class AccessoryMenu {

    private final Scanner scanner;
    private final ConsoleErrorHandler errorHandler;
    private final AccessoryService accessoryService;
    private final ProductService productService;

    /**
     * Creates the accessory menu.
     *
     * @param scanner scanner shared by all the menus
     * @param errorHandler shows the messages of domain errors
     * @param accessoryService service used for accessory operations
     * @param productService service used to list the registered consoles
     */
    public AccessoryMenu(
            Scanner scanner,
            ConsoleErrorHandler errorHandler,
            AccessoryService accessoryService,
            ProductService productService) {

        this.scanner = scanner;
        this.errorHandler = errorHandler;
        this.accessoryService = accessoryService;
        this.productService = productService;
    }

    /**
     * Displays the accessory menu until the user goes back.
     */
    public void show() {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println();
            System.out.println("----- Accesorios -----");
            System.out.println("1. Registrar controller");
            System.out.println("2. Registrar cable");
            System.out.println("3. Registrar memoria");
            System.out.println("4. Listar accesorios");
            System.out.println("5. Listar accesorios por tipo");
            System.out.println("6. Buscar accesorios compatibles con consola");
            System.out.println("7. Volver");
            System.out.print("Selecciona una opción: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    registerController();
                    break;

                case "2":
                    registerCable();
                    break;

                case "3":
                    registerMemory();
                    break;

                case "4":
                    listAccessories();
                    break;

                case "5":
                    listAccessoriesByType();
                    break;

                case "6":
                    listCompatibleAccessories();
                    break;

                case "7":
                    inMenu = false;
                    break;

                default:
                    System.out.println(
                            "Opción inválida. Intenta de nuevo."
                    );
            }
        }
    }

    /**
     * Registers a controller accessory.
     */
    private void registerController() {
        System.out.println();
        System.out.println("-- Registrar controller --");

        try {
            System.out.print("Identificador: ");
            String identifier = scanner.nextLine();

            System.out.print("Título: ");
            String title = scanner.nextLine();

            System.out.print("Precio: ");
            double price = Double.parseDouble(scanner.nextLine());

            System.out.print("Cantidad disponible: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            System.out.print("Tipo de conexión: ");
            String connectionType = scanner.nextLine();

            List<Console> compatibleConsoles =
                    selectCompatibleConsoles();

            Controller controller = new Controller(
                    identifier,
                    title,
                    price,
                    quantity,
                    connectionType,
                    compatibleConsoles
            );

            accessoryService.registerController(controller);

            System.out.println(
                    "Controller registrado correctamente."
            );

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Los datos ingresados no son válidos: "
                            + "el precio y la cantidad deben ser números."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "No se pudo registrar el controller: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Registers a cable accessory.
     */
    private void registerCable() {
        System.out.println();
        System.out.println("-- Registrar cable --");

        try {
            System.out.print("Identificador: ");
            String identifier = scanner.nextLine();

            System.out.print("Título: ");
            String title = scanner.nextLine();

            System.out.print("Precio: ");
            double price = Double.parseDouble(scanner.nextLine());

            System.out.print("Cantidad disponible: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            System.out.print("Longitud en metros: ");
            double length = Double.parseDouble(scanner.nextLine());

            System.out.print("Tipo de conector: ");
            String connectorType = scanner.nextLine();

            Cable cable = new Cable(
                    identifier,
                    title,
                    price,
                    quantity,
                    length,
                    connectorType
            );

            accessoryService.registerCable(cable);

            System.out.println(
                    "Cable registrado correctamente."
            );

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Los datos ingresados no son válidos: "
                            + "el precio, la cantidad y la longitud "
                            + "deben ser números."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "No se pudo registrar el cable: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Registers a memory accessory.
     */
    private void registerMemory() {
        System.out.println();
        System.out.println("-- Registrar memoria --");

        try {
            System.out.print("Identificador: ");
            String identifier = scanner.nextLine();

            System.out.print("Título: ");
            String title = scanner.nextLine();

            System.out.print("Precio: ");
            double price = Double.parseDouble(scanner.nextLine());

            System.out.print("Cantidad disponible: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            System.out.print("Capacidad en GB: ");
            double capacityGB = Double.parseDouble(scanner.nextLine());

            System.out.print("Tipo de memoria: ");
            String memoryType = scanner.nextLine();

            List<Console> compatibleConsoles =
                    selectCompatibleConsoles();

            Memory memory = new Memory(
                    identifier,
                    title,
                    price,
                    quantity,
                    capacityGB,
                    memoryType,
                    compatibleConsoles
            );

            accessoryService.registerMemory(memory);

            System.out.println(
                    "Memoria registrada correctamente."
            );

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Los datos ingresados no son válidos: "
                            + "el precio, la cantidad y la capacidad "
                            + "deben ser números."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "No se pudo registrar la memoria: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Displays all registered accessories.
     */
    void listAccessories() {
        System.out.println();
        System.out.println("-- Accesorios --");

        List<Accessory> accessories =
                accessoryService.listAccessories();

        if (accessories.isEmpty()) {
            System.out.println(
                    "Todavía no hay accesorios registrados."
            );
            return;
        }

        printAccessories(accessories);
    }

    /**
     * Displays accessories filtered by type.
     */
    private void listAccessoriesByType() {
        System.out.println();
        System.out.println("-- Buscar accesorios por tipo --");
        System.out.println(
                "Tipos disponibles: CONTROLLER, CABLE, MEMORY"
        );

        System.out.print("Escribe el tipo: ");
        String type = scanner.nextLine().trim().toUpperCase();

        try {
            List<Accessory> accessories =
                    accessoryService.listByType(type);

            if (accessories.isEmpty()) {
                System.out.println(
                        "No se encontraron accesorios de ese tipo."
                );
                return;
            }

            printAccessories(accessories);
        } catch (RuntimeException e) {
            System.out.println(
                    "No se pudieron consultar los accesorios: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Displays accessories compatible with a console.
     */
    private void listCompatibleAccessories() {
        System.out.println();
        System.out.println(
                "-- Accesorios compatibles con consola --"
        );

        List<Console> consoles = productService.listConsoles();

        if (consoles.isEmpty()) {
            System.out.println(
                    "No hay consolas registradas."
            );
            return;
        }

        System.out.println("Consolas disponibles:");

        for (Console console : consoles) {
            System.out.println(
                    console.getIdentifier()
                            + " | "
                            + console.getTitle()
            );
        }

        System.out.print(
                "Identificador de la consola: "
        );

        String consoleId = scanner.nextLine().trim();

        try {
            List<Accessory> accessories =
                    accessoryService.listCompatibleWithConsole(
                            consoleId
                    );

            if (accessories.isEmpty()) {
                System.out.println(
                        "No se encontraron accesorios compatibles."
                );
                return;
            }

            printAccessories(accessories);
        } catch (RuntimeException e) {
            System.out.println(
                    "No se pudieron consultar los accesorios: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Selects compatible consoles for an accessory.
     *
     * @return selected consoles
     */
    private List<Console> selectCompatibleConsoles() {
        List<Console> compatibleConsoles =
                new ArrayList<>();

        List<Console> consoles = productService.listConsoles();

        System.out.println();
        System.out.println(
                "-- Consolas disponibles --"
        );

        if (consoles.isEmpty()) {
            System.out.println(
                    "No hay consolas registradas."
            );
            return compatibleConsoles;
        }

        for (Console console : consoles) {
            System.out.println(
                    console.getIdentifier()
                            + " | "
                            + console.getTitle()
            );
        }

        System.out.print(
                "Identificadores de consolas separados por coma "
                        + "(vacío para ninguna): "
        );

        String input = scanner.nextLine().trim();

        if (input.isBlank()) {
            return compatibleConsoles;
        }

        String[] identifiers = input.split(",");

        for (String identifier : identifiers) {
            String trimmedIdentifier =
                    identifier.trim();

            for (Console console : consoles) {
                if (console.getIdentifier()
                        .equals(trimmedIdentifier)) {

                    compatibleConsoles.add(console);
                    break;
                }
            }
        }

        return compatibleConsoles;
    }

    /**
     * Prints accessory information.
     *
     * @param accessories accessories to print
     */
    private void printAccessories(
            List<Accessory> accessories) {

        for (Accessory accessory : accessories) {
            System.out.println(
                    accessory.getIdentifier()
                            + " | "
                            + accessory.getTitle()
                            + " | $"
                            + accessory.getPrice()
                            + " | stock: "
                            + accessory.getAvailableQuantity()
                            + " | "
                            + accessory.getDescription()
            );
        }
    }
}
