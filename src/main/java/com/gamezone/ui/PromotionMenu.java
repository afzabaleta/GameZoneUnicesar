package com.gamezone.ui;

import com.gamezone.exceptions.GameZoneException;
import com.gamezone.model.Promotion;
import com.gamezone.service.PromotionService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console menu of the promotion module: register and query promotions.
 */
public class PromotionMenu {

    private final Scanner scanner;
    private final ConsoleErrorHandler errorHandler;
    private final PromotionService promotionService;

    /**
     * Creates the promotion menu.
     *
     * @param scanner scanner shared by all the menus
     * @param errorHandler shows the messages of domain errors
     * @param promotionService service used for promotion operations
     */
    public PromotionMenu(
            Scanner scanner,
            ConsoleErrorHandler errorHandler,
            PromotionService promotionService) {

        this.scanner = scanner;
        this.errorHandler = errorHandler;
        this.promotionService = promotionService;
    }

    /**
     * Displays the promotion menu until the user goes back.
     */
    public void show() {

        boolean inMenu = true;

        while (inMenu) {

            System.out.println();
            System.out.println("----- Promociones -----");
            System.out.println("1. Registrar descuento porcentual");
            System.out.println("2. Registrar descuento por categoría");
            System.out.println("3. Registrar descuento por volumen");
            System.out.println("4. Listar todas las promociones");
            System.out.println("5. Listar promociones vigentes");
            System.out.println("6. Buscar promoción por identificador");
            System.out.println("7. Volver");
            System.out.print("Selecciona una opción: ");

            String option = scanner.nextLine();

            switch (option) {

                case "1":
                    registerPercentagePromotion();
                    break;

                case "2":
                    registerCategoryPromotion();
                    break;

                case "3":
                    registerBulkPromotion();
                    break;

                case "4":
                    listPromotions();
                    break;

                case "5":
                    listActivePromotions();
                    break;

                case "6":
                    findPromotionById();
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
     * Registers a percentage discount promotion.
     */
    private void registerPercentagePromotion() {
        System.out.println();
        System.out.println("-- Registrar descuento porcentual --");

        try {
            System.out.print("Identificador: ");
            String identifier = scanner.nextLine();

            System.out.print("Nombre: ");
            String name = scanner.nextLine();

            System.out.print("Fecha de inicio (YYYY-MM-DD): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine());

            System.out.print("Fecha de fin (YYYY-MM-DD): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine());

            System.out.print("Porcentaje de descuento: ");
            double percentage = Double.parseDouble(scanner.nextLine());

            promotionService.registerPercentageDiscount(
                    identifier,
                    name,
                    startDate,
                    endDate,
                    percentage
            );

            System.out.println(
                    "Promoción porcentual registrada correctamente."
            );

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Los datos ingresados no son válidos: "
                            + "el porcentaje debe ser un número."
            );

        } catch (DateTimeParseException e) {

            System.out.println(
                    "La fecha ingresada no es válida. "
                            + "Utiliza el formato YYYY-MM-DD."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "No se pudo registrar la promoción: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Registers a category discount promotion.
     */
    private void registerCategoryPromotion() {
        System.out.println();
        System.out.println("-- Registrar descuento por categoría --");

        try {
            System.out.print("Identificador: ");
            String identifier = scanner.nextLine();

            System.out.print("Nombre: ");
            String name = scanner.nextLine();

            System.out.print("Fecha de inicio (YYYY-MM-DD): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine());

            System.out.print("Fecha de fin (YYYY-MM-DD): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine());

            System.out.print("Porcentaje de descuento: ");
            double percentage = Double.parseDouble(scanner.nextLine());

            System.out.print(
                    "Categoría (VIDEOGAME/CONSOLE/ACCESSORY): "
            );

            String targetCategory =
                    scanner.nextLine().trim().toUpperCase();

            promotionService.registerCategoryDiscount(
                    identifier,
                    name,
                    startDate,
                    endDate,
                    percentage,
                    targetCategory
            );

            System.out.println(
                    "Promoción por categoría registrada correctamente."
            );

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Los datos ingresados no son válidos: "
                            + "el porcentaje debe ser un número."
            );

        } catch (DateTimeParseException e) {

            System.out.println(
                    "La fecha ingresada no es válida. "
                            + "Utiliza el formato YYYY-MM-DD."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "No se pudo registrar la promoción: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Registers a bulk purchase discount promotion.
     */
    private void registerBulkPromotion() {
        System.out.println();
        System.out.println("-- Registrar descuento por volumen --");

        try {
            System.out.print("Identificador: ");
            String identifier = scanner.nextLine();

            System.out.print("Nombre: ");
            String name = scanner.nextLine();

            System.out.print("Fecha de inicio (YYYY-MM-DD): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine());

            System.out.print("Fecha de fin (YYYY-MM-DD): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine());

            System.out.print("Cantidad mínima de productos: ");
            int minimumQuantity = Integer.parseInt(scanner.nextLine());

            System.out.print("Porcentaje de descuento: ");
            double percentage = Double.parseDouble(scanner.nextLine());

            promotionService.registerBulkPurchaseDiscount(
                    identifier,
                    name,
                    startDate,
                    endDate,
                    minimumQuantity,
                    percentage
            );

            System.out.println(
                    "Promoción por volumen registrada correctamente."
            );

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Los datos ingresados no son válidos: "
                            + "la cantidad y el porcentaje deben ser números."
            );

        } catch (DateTimeParseException e) {

            System.out.println(
                    "La fecha ingresada no es válida. "
                            + "Utiliza el formato YYYY-MM-DD."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "No se pudo registrar la promoción: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Lists all registered promotions.
     */
    private void listPromotions() {

        System.out.println();
        System.out.println("-- Todas las promociones --");

        List<Promotion> promotions =
                promotionService.listAllPromotions();

        if (promotions.isEmpty()) {
            System.out.println(
                    "No hay promociones registradas."
            );
            return;
        }

        printPromotions(promotions);
    }

    /**
     * Lists currently active promotions.
     */
    private void listActivePromotions() {

        System.out.println();
        System.out.println("-- Promociones vigentes --");

        List<Promotion> promotions =
                promotionService.listActivePromotions();

        if (promotions.isEmpty()) {
            System.out.println(
                    "No hay promociones vigentes."
            );
            return;
        }

        printPromotions(promotions);
    }

    /**
     * Finds a promotion by identifier.
     */
    private void findPromotionById() {

        System.out.println();
        System.out.println("-- Buscar promoción --");

        System.out.print("Identificador: ");

        String identifier =
                scanner.nextLine().trim();

        Promotion promotion =
                promotionService.findById(identifier);

        if (promotion == null) {
            System.out.println(
                    "Promoción no encontrada."
            );
            return;
        }

        System.out.println(
                "Identificador: "
                        + promotion.getIdentifier()
        );

        System.out.println(
                "Nombre: "
                        + promotion.getName()
        );

        System.out.println(
                "Inicio: "
                        + promotion.getStartDate()
        );

        System.out.println(
                "Fin: "
                        + promotion.getEndDate()
        );

        System.out.println(
                "Tipo: "
                        + promotion.getClass()
                        .getSimpleName()
        );
    }

    /**
     * Prints promotion information.
     *
     * @param promotions promotions to print
     */
    private void printPromotions(
            List<Promotion> promotions) {

        for (Promotion promotion : promotions) {

            System.out.println(
                    promotion.getIdentifier()
                            + " | "
                            + promotion.getName()
                            + " | "
                            + promotion.getClass()
                            .getSimpleName()
                            + " | Inicio: "
                            + promotion.getStartDate()
                            + " | Fin: "
                            + promotion.getEndDate()
            );
        }
    }
}
