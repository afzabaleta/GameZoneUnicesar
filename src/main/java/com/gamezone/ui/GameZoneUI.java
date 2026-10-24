package com.gamezone.ui;

import com.gamezone.service.AccessoryService;
import com.gamezone.service.InventoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;

import java.util.Scanner;

/**
 * Main console menu of the GameZone system.
 *
 * <p>It shows the main menu and delegates each module to its own menu class
 * (High Cohesion). It creates those menus because it owns them and has the
 * data needed to build them (Creator). All data operations are delegated to
 * the service layer; no menu accesses repositories directly.</p>
 */
public class GameZoneUI {

    private final Scanner scanner;
    private final ProductMenu productMenu;
    private final AccessoryMenu accessoryMenu;
    private final PersonMenu personMenu;
    private final SaleMenu saleMenu;
    private final PromotionMenu promotionMenu;
    private final ReturnMenu returnMenu;
    private final WarrantyMenu warrantyMenu;

    /**
     * Creates the console UI and the menu of each module.
     *
     * @param personService service used for customer and seller operations
     * @param productService service used for product operations
     * @param accessoryService service used for accessory operations
     * @param inventoryService service used to find products and accessories
     * @param saleService service used for sale operations
     * @param promotionService service used for promotion operations
     * @param returnService service used for return operations
     * @param warrantyService service used for warranty operations
     */
    public GameZoneUI(
            PersonService personService,
            ProductService productService,
            AccessoryService accessoryService,
            InventoryService inventoryService,
            SaleService saleService,
            PromotionService promotionService,
            ReturnService returnService,
            WarrantyService warrantyService) {

        this.scanner = new Scanner(System.in);

        ConsoleErrorHandler errorHandler = new ConsoleErrorHandler();

        this.productMenu = new ProductMenu(
                scanner,
                errorHandler,
                productService
        );

        this.accessoryMenu = new AccessoryMenu(
                scanner,
                errorHandler,
                accessoryService,
                productService
        );

        this.personMenu = new PersonMenu(
                scanner,
                errorHandler,
                personService
        );

        this.saleMenu = new SaleMenu(
                scanner,
                errorHandler,
                saleService,
                personService,
                inventoryService,
                productMenu,
                accessoryMenu
        );

        this.promotionMenu = new PromotionMenu(
                scanner,
                errorHandler,
                promotionService
        );

        this.returnMenu = new ReturnMenu(
                scanner,
                errorHandler,
                returnService
        );

        this.warrantyMenu = new WarrantyMenu(
                scanner,
                errorHandler,
                warrantyService
        );
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
            System.out.println("5. Promociones");
            System.out.println("6. Devoluciones");
            System.out.println("7. Garantías");
            System.out.println("8. Salir");
            System.out.print("Selecciona una opción: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    productMenu.show();
                    break;

                case "2":
                    accessoryMenu.show();
                    break;

                case "3":
                    personMenu.show();
                    break;

                case "4":
                    saleMenu.show();
                    break;

                case "5":
                    promotionMenu.show();
                    break;

                case "6":
                    returnMenu.show();
                    break;

                case "7":
                    warrantyMenu.show();
                    break;

                case "8":
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
}
