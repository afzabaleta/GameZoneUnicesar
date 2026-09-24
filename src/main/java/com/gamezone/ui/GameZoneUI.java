package com.gamezone.ui;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Console;
import com.gamezone.model.Controller;
import com.gamezone.model.Customer;
import com.gamezone.model.Memory;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.VideoGame;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for the GameZone system.
 *
 * <p>Exposes menus for products, accessories, people and sales.
 * All data operations are delegated to the service layer and this class
 * never accesses repositories directly, respecting the layered architecture.</p>
 */
public class GameZoneUI {

    private final PersonService personService;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final SaleService saleService;
    private final Scanner scanner;

    /**
     * Creates the console UI wired to the services it depends on.
     *
     * @param personService service used for customer and seller operations
     * @param productService service used for product operations
     * @param accessoryService service used for accessory operations
     * @param saleService service used for sale operations
     */
    public GameZoneUI(
            PersonService personService,
            ProductService productService,
            AccessoryService accessoryService,
            SaleService saleService) {

        this.personService = personService;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.saleService = saleService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the main menu and routes the user to the selected submenu
     * until the user chooses to exit.
     */
    public void showMainMenu() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("===== GameZoneUnicesar =====");
            System.out.println("1. Productos");
            System.out.println("2. Accesorios");
            System.out.println("3. Personas");
            System.out.println("4. Ventas");
            System.out.println("5. Salir");
            System.out.print("Selecciona una opción: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    showProductMenu();
                    break;

                case "2":
                    showAccessoryMenu();
                    break;

                case "3":
                    showPersonMenu();
                    break;

                case "4":
                    showSaleMenu();
                    break;

                case "5":
                    running = false;
                    System.out.println(
                            "Cerrando GameZoneUnicesar. ¡Hasta pronto!"
                    );
                    break;

                default:
                    System.out.println(
                            "Opción inválida. Intenta de nuevo."
                    );
            }
        }
    }

    /**
     * Displays the product submenu: register video games, register
     * consoles and list all products.
     */
    public void showProductMenu() {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println();
            System.out.println("----- Productos -----");
            System.out.println("1. Registrar videojuego");
            System.out.println("2. Registrar consola");
            System.out.println("3. Listar productos");
            System.out.println("4. Volver");
            System.out.print("Selecciona una opción: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    registerVideoGame();
                    break;

                case "2":
                    registerConsole();
                    break;

                case "3":
                    listProducts();
                    break;

                case "4":
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
     * Displays the accessory submenu.
     */
    public void showAccessoryMenu() {
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
     * Displays the people submenu: register customers, list customers and
     * list sellers. Sellers are not registered here because they are
     * preloaded by the persistence layer.
     */
    public void showPersonMenu() {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println();
            System.out.println("----- Personas -----");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Listar vendedores");
            System.out.println("4. Volver");
            System.out.print("Selecciona una opción: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    registerCustomer();
                    break;

                case "2":
                    listCustomers();
                    break;

                case "3":
                    listSellers();
                    break;

                case "4":
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
     * Displays the sale submenu: register a sale, list all sales, view a
     * customer's purchase history and view a seller's sales history.
     */
    public void showSaleMenu() {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println();
            System.out.println("----- Ventas -----");
            System.out.println("1. Registrar venta");
            System.out.println("2. Listar todas las ventas");
            System.out.println("3. Historial de compras de un cliente");
            System.out.println("4. Historial de ventas de un vendedor");
            System.out.println("5. Volver");
            System.out.print("Selecciona una opción: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    registerSale();
                    break;

                case "2":
                    listAllSales();
                    break;

                case "3":
                    showCustomerPurchaseHistory();
                    break;

                case "4":
                    showSellerSalesHistory();
                    break;

                case "5":
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
     * Registers a video game.
     */
    private void registerVideoGame() {
        System.out.println();
        System.out.println("-- Registrar videojuego --");

        System.out.print("Identificador: ");
        String identifier = scanner.nextLine();

        System.out.print("Título: ");
        String title = scanner.nextLine();

        System.out.print("Precio: ");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.print("Cantidad disponible: ");
        int quantity = Integer.parseInt(scanner.nextLine());

        System.out.print("Plataforma: ");
        String platform = scanner.nextLine();

        System.out.print("Género: ");
        String genre = scanner.nextLine();

        System.out.print("Clasificación por edad: ");
        String ageRating = scanner.nextLine();

        try {
            VideoGame videoGame = new VideoGame(
                    identifier,
                    title,
                    price,
                    quantity,
                    platform,
                    genre,
                    ageRating
            );

            productService.registerVideoGame(videoGame);

            System.out.println(
                    "Videojuego registrado correctamente."
            );
        } catch (RuntimeException e) {
            System.out.println(
                    "No se pudo registrar el videojuego: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Registers a console.
     */
    private void registerConsole() {
        System.out.println();
        System.out.println("-- Registrar consola --");

        System.out.print("Identificador: ");
        String identifier = scanner.nextLine();

        System.out.print("Título: ");
        String title = scanner.nextLine();

        System.out.print("Precio: ");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.print("Cantidad disponible: ");
        int quantity = Integer.parseInt(scanner.nextLine());

        System.out.print("Marca: ");
        String brand = scanner.nextLine();

        System.out.print("Modelo: ");
        String model = scanner.nextLine();

        System.out.print("Generación: ");
        String generation = scanner.nextLine();

        try {
            Console console = new Console(
                    identifier,
                    title,
                    price,
                    quantity,
                    brand,
                    model,
                    generation
            );

            productService.registerConsole(console);

            System.out.println(
                    "Consola registrada correctamente."
            );
        } catch (RuntimeException e) {
            System.out.println(
                    "No se pudo registrar la consola: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Registers a controller accessory.
     */
    private void registerController() {
        System.out.println();
        System.out.println("-- Registrar controller --");

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

        try {
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

        try {
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

        try {
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
        } catch (RuntimeException e) {
            System.out.println(
                    "No se pudo registrar la memoria: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Displays all registered products.
     */
    private void listProducts() {
        System.out.println();
        System.out.println("-- Productos --");

        List<Product> products = productService.listProducts();

        if (products.isEmpty()) {
            System.out.println(
                    "Todavía no hay productos registrados."
            );
            return;
        }

        for (Product product : products) {
            System.out.println(
                    product.getIdentifier()
                            + " | "
                            + product.getTitle()
                            + " | $"
                            + product.getPrice()
                            + " | stock: "
                            + product.getAvailableQuantity()
                            + " | "
                            + product.getDescription()
            );
        }
    }

    /**
     * Displays all registered accessories.
     */
    private void listAccessories() {
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

        List<Console> consoles = getAvailableConsoles();

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

        List<Console> consoles = getAvailableConsoles();

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
     * Returns all available consoles.
     *
     * @return list of consoles
     */
    private List<Console> getAvailableConsoles() {
        List<Console> consoles = new ArrayList<>();

        List<Product> products =
                productService.listProducts();

        for (Product product : products) {
            if (product instanceof Console) {
                consoles.add((Console) product);
            }
        }

        return consoles;
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

    /**
     * Registers a customer.
     */
    private void registerCustomer() {
        System.out.println();
        System.out.println("-- Registrar cliente --");

        System.out.print("Nombre: ");
        String name = scanner.nextLine();

        System.out.print("Identificación: ");
        String identification = scanner.nextLine();

        System.out.print("Teléfono: ");
        String phone = scanner.nextLine();

        System.out.print("Correo electrónico: ");
        String email = scanner.nextLine();

        try {
            Customer customer = new Customer(
                    name,
                    identification,
                    phone,
                    email
            );

            personService.registerCustomer(customer);

            System.out.println(
                    "Cliente registrado correctamente."
            );
        } catch (RuntimeException e) {
            System.out.println(
                    "No se pudo registrar el cliente: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Lists registered customers.
     */
    private void listCustomers() {
        System.out.println();
        System.out.println("-- Clientes --");

        List<Customer> customers =
                personService.listCustomers();

        if (customers.isEmpty()) {
            System.out.println(
                    "Todavía no hay clientes registrados."
            );
            return;
        }

        for (Customer customer : customers) {
            System.out.println(
                    customer.getIdentification()
                            + " | "
                            + customer.getName()
                            + " | "
                            + customer.getEmail()
            );
        }
    }

    /**
     * Lists registered sellers.
     */
    private void listSellers() {
        System.out.println();
        System.out.println("-- Vendedores --");

        List<Seller> sellers =
                personService.listSellers();

        if (sellers.isEmpty()) {
            System.out.println(
                    "No hay vendedores precargados."
            );
            return;
        }

        for (Seller seller : sellers) {
            System.out.println(
                    seller.getIdentification()
                            + " | "
                            + seller.getName()
                            + " | turno: "
                            + seller.getWorkShift()
            );
        }
    }

    /**
     * Registers a sale.
     */
    private void registerSale() {
        System.out.println();
        System.out.println("-- Registrar venta --");

        System.out.print(
                "Identificación del cliente: "
        );

        String customerId = scanner.nextLine();

        Customer customer =
                findCustomerById(customerId);

        if (customer == null) {
            System.out.println(
                    "Cliente no encontrado."
            );
            return;
        }

        System.out.print(
                "Identificación del vendedor: "
        );

        String sellerId = scanner.nextLine();

        Seller seller =
                findSellerById(sellerId);

        if (seller == null) {
            System.out.println(
                    "Vendedor no encontrado."
            );
            return;
        }

        List<Product> selectedProducts =
                selectProductsForSale();

        if (selectedProducts.isEmpty()) {
            System.out.println(
                    "La venta debe tener al menos un producto. "
                            + "Venta cancelada."
            );
            return;
        }

        try {
            Sale sale = new Sale(
                    LocalDate.now(),
                    customer,
                    seller,
                    selectedProducts
            );

            saleService.registerSale(sale);

            System.out.println(
                    "Venta registrada correctamente. Total: $"
                            + sale.calculateTotal()
            );
        } catch (RuntimeException e) {
            System.out.println(
                    "No se pudo registrar la venta: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Selects products for a sale.
     *
     * @return selected products
     */
    private List<Product> selectProductsForSale() {
        List<Product> selectedProducts =
                new ArrayList<>();

        listProducts();

        boolean addingProducts = true;

        while (addingProducts) {
            System.out.print(
                    "Identificador del producto a agregar "
                            + "(vacío para terminar): "
            );

            String productId = scanner.nextLine();

            if (productId.isBlank()) {
                addingProducts = false;
                continue;
            }

            Product product =
                    findProductById(productId);

            if (product == null) {
                System.out.println(
                        "Producto no encontrado. Intenta de nuevo."
                );
                continue;
            }

            selectedProducts.add(product);

            System.out.println(
                    "Agregado: "
                            + product.getTitle()
            );
        }

        return selectedProducts;
    }

    /**
     * Lists all sales.
     */
    private void listAllSales() {
        System.out.println();
        System.out.println("-- Todas las ventas --");

        printSales(saleService.listSales());
    }

    /**
     * Displays customer purchase history.
     */
    private void showCustomerPurchaseHistory() {
        System.out.println();

        System.out.print(
                "Identificación del cliente: "
        );

        String customerId = scanner.nextLine();

        System.out.println(
                "-- Historial de compras de "
                        + customerId
                        + " --"
        );

        printSales(
                saleService.listSalesByCustomer(
                        customerId
                )
        );
    }

    /**
     * Displays seller sales history.
     */
    private void showSellerSalesHistory() {
        System.out.println();

        System.out.print(
                "Identificación del vendedor: "
        );

        String sellerId = scanner.nextLine();

        System.out.println(
                "-- Historial de ventas de "
                        + sellerId
                        + " --"
        );

        printSales(
                saleService.listSalesBySeller(
                        sellerId
                )
        );
    }

    /**
     * Prints sales information.
     *
     * @param sales sales to print
     */
    private void printSales(List<Sale> sales) {
        if (sales.isEmpty()) {
            System.out.println(
                    "No se encontraron ventas."
            );
            return;
        }

        for (Sale sale : sales) {
            System.out.println(
                    sale.getDate()
                            + " | Cliente: "
                            + sale.getCustomer().getName()
                            + " | Vendedor: "
                            + sale.getSeller().getName()
                            + " | Productos: "
                            + sale.getProducts().size()
                            + " | Total: $"
                            + sale.calculateTotal()
            );
        }
    }

    /**
     * Finds a customer by identification.
     *
     * @param id customer identification
     * @return matching customer or null
     */
    private Customer findCustomerById(String id) {
        for (Customer customer :
                personService.listCustomers()) {

            if (customer.getIdentification()
                    .equals(id)) {

                return customer;
            }
        }

        return null;
    }

    /**
     * Finds a seller by identification.
     *
     * @param id seller identification
     * @return matching seller or null
     */
    private Seller findSellerById(String id) {
        for (Seller seller :
                personService.listSellers()) {

            if (seller.getIdentification()
                    .equals(id)) {

                return seller;
            }
        }

        return null;
    }

    /**
     * Finds a product by identifier.
     *
     * @param id product identifier
     * @return matching product or null
     */
    private Product findProductById(String id) {
        for (Product product :
                productService.listProducts()) {

            if (product.getIdentifier()
                    .equals(id)) {

                return product;
            }
        }

        return null;
    }
}