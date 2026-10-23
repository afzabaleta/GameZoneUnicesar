package com.gamezone.ui;

import com.gamezone.exceptions.GameZoneException;
import com.gamezone.model.Return;
import com.gamezone.service.ReturnService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console menu of the return module: register returns, query them and
 * show the monthly balance.
 */
public class ReturnMenu {

    private final Scanner scanner;
    private final ConsoleErrorHandler errorHandler;
    private final ReturnService returnService;

    /**
     * Creates the return menu.
     *
     * @param scanner scanner shared by all the menus
     * @param errorHandler shows the messages of domain errors
     * @param returnService service used for return operations
     */
    public ReturnMenu(
            Scanner scanner,
            ConsoleErrorHandler errorHandler,
            ReturnService returnService) {

        this.scanner = scanner;
        this.errorHandler = errorHandler;
        this.returnService = returnService;
    }

    /**
     * Displays the return menu until the user goes back.
     */
    public void show() {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("===== GESTIÓN DE DEVOLUCIONES =====");
            System.out.println("1. Registrar nueva devolución");
            System.out.println("2. Consultar todas las devoluciones");
            System.out.println("3. Consultar devoluciones por cliente");
            System.out.println("4. Consultar devoluciones por venta");
            System.out.println("5. Consultar balance mensual");
            System.out.println("6. Volver");

            System.out.print("Seleccione una opción: ");

            String option = scanner.nextLine();

            switch (option) {

                case "1":
                    registerReturn();
                    break;

                case "2":
                    listAllReturns();
                    break;

                case "3":
                    listReturnsByCustomer();
                    break;

                case "4":
                    listReturnsBySale();
                    break;

                case "5":
                    showMonthlyBalance();
                    break;

                case "6":
                    running = false;
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    /**
     * Registers a product return and handles custom exceptions.
     */
    private void registerReturn() {
        System.out.println();
        System.out.println("===== REGISTRAR DEVOLUCIÓN =====");

        System.out.print(
                "Referencia de la venta (YYYY-MM-DD): "
        );
        String saleId = scanner.nextLine().trim();

        if (saleId.isBlank()) {
            System.out.println(
                    "La referencia de la venta no puede estar vacía."
            );
            return;
        }

        System.out.print(
                "IDs de los productos a devolver (separados por coma): "
        );
        String productsInput = scanner.nextLine().trim();

        if (productsInput.isBlank()) {
            System.out.println(
                    "Debe indicar al menos un producto."
            );
            return;
        }

        List<String> productIds = new ArrayList<>();

        String[] products = productsInput.split(",");

        for (String productId : products) {
            String cleanId = productId.trim();

            if (!cleanId.isBlank()) {
                productIds.add(cleanId);
            }
        }

        if (productIds.isEmpty()) {
            System.out.println(
                    "Debe indicar al menos un producto válido."
            );
            return;
        }

        System.out.print("Motivo de la devolución: ");
        String reason = scanner.nextLine().trim();

        if (reason.isBlank()) {
            System.out.println(
                    "El motivo no puede estar vacío."
            );
            return;
        }

        try {
            Return returnItem =
                    returnService.registerReturn(
                            saleId,
                            productIds,
                            reason
                    );

            System.out.println();
            System.out.println(
                    "Devolución registrada correctamente."
            );
            System.out.println();
            System.out.println(
                    returnItem.generateReturnReceipt()
            );

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (RuntimeException e) {

            System.out.println(
                    "No fue posible registrar la devolución: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Lists all registered returns.
     */
    private void listAllReturns() {

        System.out.println();
        System.out.println("===== TODAS LAS DEVOLUCIONES =====");

        List<Return> returns =
                returnService.viewAllReturns();

        printReturns(returns);
    }

    /**
     * Lists the returns of a customer.
     */
    private void listReturnsByCustomer() {

        System.out.println();
        System.out.println("===== DEVOLUCIONES POR CLIENTE =====");

        System.out.print("Identificación del cliente: ");
        String customerId = scanner.nextLine().trim();

        if (customerId.isBlank()) {
            System.out.println(
                    "La identificación del cliente no puede estar vacía."
            );
            return;
        }

        List<Return> returns =
                returnService.viewReturnsByCustomer(customerId);

        printReturns(returns);
    }

    /**
     * Lists the returns of a sale.
     */
    private void listReturnsBySale() {

        System.out.println();
        System.out.println("===== DEVOLUCIONES POR VENTA =====");

        System.out.print(
                "Referencia de la venta (YYYY-MM-DD): "
        );

        String saleId = scanner.nextLine().trim();

        if (saleId.isBlank()) {
            System.out.println(
                    "La referencia de la venta no puede estar vacía."
            );
            return;
        }

        List<Return> returns =
                returnService.viewReturnsBySale(saleId);

        printReturns(returns);
    }

    /**
     * Displays the monthly sales, returns and net balance.
     */
    private void showMonthlyBalance() {
        System.out.println();
        System.out.println("===== BALANCE MENSUAL =====");

        System.out.print("Mes (1-12): ");
        String monthInput = scanner.nextLine().trim();

        System.out.print("Año: ");
        String yearInput = scanner.nextLine().trim();

        try {
            int month = Integer.parseInt(monthInput);
            int year = Integer.parseInt(yearInput);

            double totalSales =
                    returnService.calculateMonthlySales(
                            month,
                            year
                    );

            double totalReturns =
                    returnService.calculateMonthlyReturns(
                            month,
                            year
                    );

            double netBalance =
                    returnService.generateMonthlyBalance(
                            month,
                            year
                    );

            System.out.println();
            System.out.println(
                    "===== RESULTADO DEL BALANCE ====="
            );

            System.out.println(
                    "Total de ventas: $" + totalSales
            );

            System.out.println(
                    "Total de devoluciones: $" + totalReturns
            );

            System.out.println(
                    "Balance neto: $" + netBalance
            );

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Los datos ingresados no son válidos: "
                            + "el mes y el año deben ser números enteros."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "No fue posible calcular el balance: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Prints return receipts.
     *
     * @param returns returns to print
     */
    private void printReturns(List<Return> returns) {

        if (returns == null || returns.isEmpty()) {

            System.out.println(
                    "No se encontraron devoluciones."
            );

            return;
        }

        for (Return returnItem : returns) {

            System.out.println();
            System.out.println(
                    returnItem.generateReturnReceipt()
            );
            System.out.println(
                    "========================================"
            );
        }
    }
}
