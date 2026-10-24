package com.gamezone.ui;

import com.gamezone.exceptions.GameZoneException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Warranty;
import com.gamezone.service.WarrantyService;

import java.util.List;
import java.util.Scanner;

/**
 * Console menu of the warranty module: query and list warranties.
 */
public class WarrantyMenu {

    private final Scanner scanner;
    private final ConsoleErrorHandler errorHandler;
    private final WarrantyService warrantyService;

    /**
     * Creates the warranty menu.
     *
     * @param scanner scanner shared by all the menus
     * @param errorHandler shows the messages of domain errors
     * @param warrantyService service used for warranty operations
     */
    public WarrantyMenu(
            Scanner scanner,
            ConsoleErrorHandler errorHandler,
            WarrantyService warrantyService) {

        this.scanner = scanner;
        this.errorHandler = errorHandler;
        this.warrantyService = warrantyService;
    }

    /**
     * Displays the warranty menu until the user goes back.
     */
    public void show() {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("===== GESTIÓN DE GARANTÍAS =====");
            System.out.println("1. Consultar garantía por producto y venta");
            System.out.println("2. Listar todas las garantías");
            System.out.println("3. Listar garantías vigentes");
            System.out.println("4. Listar garantías próximas a vencer");
            System.out.println("5. Volver");

            System.out.print("Seleccione una opción: ");

            String option = scanner.nextLine();

            switch (option) {

                case "1":
                    findWarranty();
                    break;

                case "2":
                    listAllWarranties();
                    break;

                case "3":
                    listActiveWarranties();
                    break;

                case "4":
                    listWarrantiesExpiringSoon();
                    break;

                case "5":
                    running = false;
                    break;

                default:
                    System.out.println(
                            "Opción inválida."
                    );
            }
        }
    }

    /**
     * Finds a warranty by product and sale reference.
     * Handles custom exceptions separately.
     */
    private void findWarranty() {
        System.out.println();
        System.out.println("===== CONSULTAR GARANTÍA =====");

        System.out.print("Identificador del producto: ");
        String productId = scanner.nextLine().trim();

        System.out.print(
                "Referencia de la venta (YYYY-MM-DD): "
        );
        String saleId = scanner.nextLine().trim();

        try {
            Warranty warranty =
                    warrantyService.findWarrantyByProduct(
                            productId,
                            saleId
                    );

            if (warranty == null) {
                throw new ResourceNotFoundException(
                        "garantía",
                        productId + " / " + saleId
                );
            }

            System.out.println();
            System.out.println(
                    warranty.generateWarrantyCertificate()
            );

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (RuntimeException e) {

            System.out.println(
                    "No fue posible consultar la garantía: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Lists all registered warranties.
     */
    private void listAllWarranties() {

        System.out.println();
        System.out.println(
                "===== TODAS LAS GARANTÍAS ====="
        );

        List<Warranty> warranties =
                warrantyService.listAllWarranties();

        printWarranties(warranties);
    }

    /**
     * Lists warranties that are currently active.
     */
    private void listActiveWarranties() {

        System.out.println();
        System.out.println(
                "===== GARANTÍAS VIGENTES ====="
        );

        List<Warranty> warranties =
                warrantyService.listActiveWarranties();

        printWarranties(warranties);
    }

    /**
     * Lists warranties that will expire within a given number of days.
     */
    private void listWarrantiesExpiringSoon() {
        System.out.println();
        System.out.println(
                "===== GARANTÍAS PRÓXIMAS A VENCER ====="
        );

        System.out.print(
                "¿Cuántos días de anticipación?: "
        );

        String daysInput = scanner.nextLine().trim();

        try {
            int daysAhead = Integer.parseInt(daysInput);

            List<Warranty> warranties =
                    warrantyService.listWarrantiesExpiringSoon(
                            daysAhead
                    );

            printWarranties(warranties);

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Los datos ingresados no son válidos: "
                            + "debe indicar un número entero."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "No fue posible consultar las garantías: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Prints warranty information.
     *
     * @param warranties warranties to print
     */
    private void printWarranties(
            List<Warranty> warranties) {

        if (warranties == null
                || warranties.isEmpty()) {

            System.out.println(
                    "No se encontraron garantías."
            );

            return;
        }

        for (Warranty warranty : warranties) {

            System.out.println();
            System.out.println(
                    warranty.generateWarrantyCertificate()
            );
            System.out.println(
                    "----------------------------------------"
            );
        }
    }
}
