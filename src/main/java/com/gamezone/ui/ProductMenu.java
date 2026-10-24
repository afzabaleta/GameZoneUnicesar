package com.gamezone.ui;

import com.gamezone.exceptions.GameZoneException;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.service.ProductService;

import java.util.List;
import java.util.Scanner;

/**
 * Console menu of the product module: register video games and consoles
 * and list products.
 */
public class ProductMenu {

    private final Scanner scanner;
    private final ConsoleErrorHandler errorHandler;
    private final ProductService productService;

    /**
     * Creates the product menu.
     *
     * @param scanner scanner shared by all the menus
     * @param errorHandler shows the messages of domain errors
     * @param productService service used for product operations
     */
    public ProductMenu(
            Scanner scanner,
            ConsoleErrorHandler errorHandler,
            ProductService productService) {

        this.scanner = scanner;
        this.errorHandler = errorHandler;
        this.productService = productService;
    }

    /**
     * Displays the product menu until the user goes back.
     */
    public void show() {
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
     * Registers a video game and handles validation errors.
     */
    private void registerVideoGame() {
        System.out.println();
        System.out.println("-- Registrar videojuego --");

        try {
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

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Los datos ingresados no son válidos: "
                            + "el precio y la cantidad deben ser números."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "No se pudo registrar el videojuego: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Registers a console and handles validation errors.
     */
    private void registerConsole() {
        System.out.println();
        System.out.println("-- Registrar consola --");

        try {
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

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Los datos ingresados no son válidos: "
                            + "el precio y la cantidad deben ser números."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "No se pudo registrar la consola: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Displays all registered products.
     */
    void listProducts() {
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
}
