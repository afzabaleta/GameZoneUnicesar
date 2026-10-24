package com.gamezone.ui;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.GameZoneException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.service.InventoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.SaleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console menu of the sale module: register sales and show the sale
 * history of customers and sellers.
 */
public class SaleMenu {

    private final Scanner scanner;
    private final ConsoleErrorHandler errorHandler;
    private final SaleService saleService;
    private final PersonService personService;
    private final InventoryService inventoryService;
    private final ProductMenu productMenu;
    private final AccessoryMenu accessoryMenu;

    /**
     * Creates the sale menu.
     *
     * @param scanner scanner shared by all the menus
     * @param errorHandler shows the messages of domain errors
     * @param saleService service used for sale operations
     * @param personService service used to find customers and sellers
     * @param inventoryService service used to find products and accessories
     * @param productMenu menu used to print the available products
     * @param accessoryMenu menu used to print the available accessories
     */
    public SaleMenu(
            Scanner scanner,
            ConsoleErrorHandler errorHandler,
            SaleService saleService,
            PersonService personService,
            InventoryService inventoryService,
            ProductMenu productMenu,
            AccessoryMenu accessoryMenu) {

        this.scanner = scanner;
        this.errorHandler = errorHandler;
        this.saleService = saleService;
        this.personService = personService;
        this.inventoryService = inventoryService;
        this.productMenu = productMenu;
        this.accessoryMenu = accessoryMenu;
    }

    /**
     * Displays the sale menu until the user goes back.
     */
    public void show() {
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
     * Registers a sale and handles each custom exception separately.
     */
    private void registerSale() {
        System.out.println();
        System.out.println("-- Registrar venta --");

        try {
            System.out.print("Identificación del cliente: ");
            String customerId = scanner.nextLine().trim();

            Customer customer =
                    personService.findCustomerById(customerId);

            if (customer == null) {
                throw new ResourceNotFoundException(
                        "cliente",
                        customerId
                );
            }

            System.out.print("Identificación del vendedor: ");
            String sellerId = scanner.nextLine().trim();

            Seller seller =
                    personService.findSellerById(sellerId);

            if (seller == null) {
                throw new ResourceNotFoundException(
                        "vendedor",
                        sellerId
                );
            }

            List<Product> selectedItems = selectItemsForSale();

            if (selectedItems.isEmpty()) {
                throw new BusinessRuleException(
                        "La venta debe contener al menos un producto "
                                + "o accesorio."
                );
            }

            List<String> extendedWarrantyIds =
                    selectExtendedWarrantyProducts(selectedItems);

            Sale sale = saleService.registerSale(
                    customer,
                    seller,
                    selectedItems,
                    extendedWarrantyIds
            );

            System.out.println(
                    "Venta registrada correctamente."
            );

            System.out.println();
            System.out.println(sale.generateReceipt());

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (RuntimeException e) {

            System.out.println(
                    "No se pudo registrar la venta: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Asks whether each product of the sale that has a warranty should
     * receive an extended warranty.
     *
     * @param products products included in the sale
     * @return identifiers of consoles requesting extended warranty
     */
    private List<String> selectExtendedWarrantyProducts(
            List<Product> products) {

        List<String> extendedWarrantyIds =
                new ArrayList<>();

        for (Product product : products) {

            if (!product.isWarrantyEligible()) {
                continue;
            }

            System.out.println();
            System.out.println(
                    "Consola: "
                            + product.getTitle()
                            + " | ID: "
                            + product.getIdentifier()
            );

            System.out.print(
                    "¿Desea agregar garantía extendida? (S/N): "
            );

            String option =
                    scanner.nextLine()
                            .trim()
                            .toUpperCase();

            if ("S".equals(option)) {

                extendedWarrantyIds.add(
                        product.getIdentifier()
                );
            }
        }

        return extendedWarrantyIds;
    }

    /**
     * Selects products and accessories for a sale.
     *
     * @return selected products and accessories
     */
    private List<Product> selectItemsForSale() {
        List<Product> selectedItems =
                new ArrayList<>();

        System.out.println();
        System.out.println(
                "===== Productos disponibles ====="
        );
        productMenu.listProducts();

        System.out.println();
        System.out.println(
                "===== Accesorios disponibles ====="
        );
        accessoryMenu.listAccessories();

        boolean addingItems = true;

        while (addingItems) {
            System.out.print(
                    "Identificador del producto o accesorio "
                            + "(vacío para terminar): "
            );

            String itemId = scanner.nextLine().trim();

            if (itemId.isBlank()) {
                addingItems = false;
                continue;
            }

            Product item =
                    inventoryService.findItem(itemId);

            if (item == null) {
                System.out.println(
                        "Producto o accesorio no encontrado. "
                                + "Intenta de nuevo."
                );
                continue;
            }

            selectedItems.add(item);

            System.out.println(
                    "Agregado: "
                            + item.getTitle()
                            + " | $"
                            + item.getPrice()
            );
        }

        return selectedItems;
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
}
