package com.gamezone.ui;

import com.gamezone.model.Console;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.VideoGame;
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
 * <p>Exposes a main menu with three submenus: products, people and sales.
 * All data operations are delegated to the service layer
 * (PersonService, ProductService, SaleService); this class never accesses
 * repositories directly, respecting the layered architecture.</p>
 */
public class GameZoneUI {

    private final PersonService personService;
    private final ProductService productService;
    private final SaleService saleService;
    private final Scanner scanner;

    /**
     * Creates the console UI wired to the three services it depends on.
     *
     * @param personService  service used for customer and seller operations
     * @param productService service used for product operations
     * @param saleService    service used for sale operations
     */
    public GameZoneUI(PersonService personService, ProductService productService, SaleService saleService) {
        this.personService = personService;
        this.productService = productService;
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
            System.out.println("2. Personas");
            System.out.println("3. Ventas");
            System.out.println("4. Salir");
            System.out.print("Selecciona una opción: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    showProductMenu();
                    break;
                case "2":
                    showPersonMenu();
                    break;
                case "3":
                    showSaleMenu();
                    break;
                case "4":
                    running = false;
                    System.out.println("Cerrando GameZoneUnicesar. ¡Hasta pronto!");
                    break;
                default:
                    System.out.println("Opción inválida. Intenta de nuevo.");
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
                    System.out.println("Opción inválida. Intenta de nuevo.");
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
                    System.out.println("Opción inválida. Intenta de nuevo.");
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
                    System.out.println("Opción inválida. Intenta de nuevo.");
            }
        }
    }

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
            VideoGame videoGame = new VideoGame(identifier, title, price, quantity, platform, genre, ageRating);
            productService.registerVideoGame(videoGame);
            System.out.println("Videojuego registrado correctamente.");
        } catch (RuntimeException e) {
            System.out.println("No se pudo registrar el videojuego: " + e.getMessage());
        }
    }

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
            Console console = new Console(identifier, title, price, quantity, brand, model, generation);
            productService.registerConsole(console);
            System.out.println("Consola registrada correctamente.");
        } catch (RuntimeException e) {
            System.out.println("No se pudo registrar la consola: " + e.getMessage());
        }
    }

    private void listProducts() {
        System.out.println();
        System.out.println("-- Productos --");
        List<Product> products = productService.listProducts();

        if (products.isEmpty()) {
            System.out.println("Todavía no hay productos registrados.");
            return;
        }

        for (Product product : products) {
            System.out.println(product.getIdentifier() + " | " + product.getTitle()
                    + " | $" + product.getPrice()
                    + " | stock: " + product.getAvailableQuantity()
                    + " | " + product.getDescription());
        }
    }

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
            Customer customer = new Customer(name, identification, phone, email);
            personService.registerCustomer(customer);
            System.out.println("Cliente registrado correctamente.");
        } catch (RuntimeException e) {
            System.out.println("No se pudo registrar el cliente: " + e.getMessage());
        }
    }

    private void listCustomers() {
        System.out.println();
        System.out.println("-- Clientes --");
        List<Customer> customers = personService.listCustomers();

        if (customers.isEmpty()) {
            System.out.println("Todavía no hay clientes registrados.");
            return;
        }

        for (Customer customer : customers) {
            System.out.println(customer.getIdentification() + " | " + customer.getName()
                    + " | " + customer.getEmail());
        }
    }

    private void listSellers() {
        System.out.println();
        System.out.println("-- Vendedores --");
        List<Seller> sellers = personService.listSellers();

        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores precargados.");
            return;
        }

        for (Seller seller : sellers) {
            System.out.println(seller.getIdentification() + " | " + seller.getName()
                    + " | turno: " + seller.getWorkShift());
        }
    }

    private void registerSale() {
        System.out.println();
        System.out.println("-- Registrar venta --");

        System.out.print("Identificación del cliente: ");
        String customerId = scanner.nextLine();
        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        System.out.print("Identificación del vendedor: ");
        String sellerId = scanner.nextLine();
        Seller seller = findSellerById(sellerId);
        if (seller == null) {
            System.out.println("Vendedor no encontrado.");
            return;
        }

        List<Product> selectedProducts = selectProductsForSale();
        if (selectedProducts.isEmpty()) {
            System.out.println("La venta debe tener al menos un producto. Venta cancelada.");
            return;
        }

        try {
            Sale sale = new Sale(LocalDate.now(), customer, seller, selectedProducts);
            saleService.registerSale(sale);
            System.out.println("Venta registrada correctamente. Total: $" + sale.calculateTotal());
        } catch (RuntimeException e) {
            System.out.println("No se pudo registrar la venta: " + e.getMessage());
        }
    }

    private List<Product> selectProductsForSale() {
        List<Product> selectedProducts = new ArrayList<>();
        listProducts();

        boolean addingProducts = true;
        while (addingProducts) {
            System.out.print("Identificador del producto a agregar (vacío para terminar): ");
            String productId = scanner.nextLine();

            if (productId.isBlank()) {
                addingProducts = false;
                continue;
            }

            Product product = findProductById(productId);
            if (product == null) {
                System.out.println("Producto no encontrado. Intenta de nuevo.");
                continue;
            }

            selectedProducts.add(product);
            System.out.println("Agregado: " + product.getTitle());
        }

        return selectedProducts;
    }

    private void listAllSales() {
        System.out.println();
        System.out.println("-- Todas las ventas --");
        printSales(saleService.listSales());
    }

    private void showCustomerPurchaseHistory() {
        System.out.println();
        System.out.print("Identificación del cliente: ");
        String customerId = scanner.nextLine();
        System.out.println("-- Historial de compras de " + customerId + " --");
        printSales(saleService.listSalesByCustomer(customerId));
    }

    private void showSellerSalesHistory() {
        System.out.println();
        System.out.print("Identificación del vendedor: ");
        String sellerId = scanner.nextLine();
        System.out.println("-- Historial de ventas de " + sellerId + " --");
        printSales(saleService.listSalesBySeller(sellerId));
    }

    private void printSales(List<Sale> sales) {
        if (sales.isEmpty()) {
            System.out.println("No se encontraron ventas.");
            return;
        }

        for (Sale sale : sales) {
            System.out.println(sale.getDate()
                    + " | Cliente: " + sale.getCustomer().getName()
                    + " | Vendedor: " + sale.getSeller().getName()
                    + " | Productos: " + sale.getProducts().size()
                    + " | Total: $" + sale.calculateTotal());
        }
    }

    private Customer findCustomerById(String id) {
        for (Customer customer : personService.listCustomers()) {
            if (customer.getIdentification().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    private Seller findSellerById(String id) {
        for (Seller seller : personService.listSellers()) {
            if (seller.getIdentification().equals(id)) {
                return seller;
            }
        }
        return null;
    }

    private Product findProductById(String id) {
        for (Product product : productService.listProducts()) {
            if (product.getIdentifier().equals(id)) {
                return product;
            }
        }
        return null;
    }
}