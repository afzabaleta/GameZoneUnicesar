package com.gamezone.ui;

import com.gamezone.exceptions.GameZoneException;
import com.gamezone.model.Customer;
import com.gamezone.model.Seller;
import com.gamezone.service.PersonService;

import java.util.List;
import java.util.Scanner;

/**
 * Console menu of the people module: register customers and list
 * customers and sellers.
 */
public class PersonMenu {

    private final Scanner scanner;
    private final ConsoleErrorHandler errorHandler;
    private final PersonService personService;

    /**
     * Creates the person menu.
     *
     * @param scanner scanner shared by all the menus
     * @param errorHandler shows the messages of domain errors
     * @param personService service used for customer and seller operations
     */
    public PersonMenu(
            Scanner scanner,
            ConsoleErrorHandler errorHandler,
            PersonService personService) {

        this.scanner = scanner;
        this.errorHandler = errorHandler;
        this.personService = personService;
    }

    /**
     * Displays the people menu until the user goes back. Sellers are
     * not registered here because they are preloaded by the persistence layer.
     */
    public void show() {
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
     * Registers a customer and handles validation and persistence errors.
     */
    private void registerCustomer() {
        System.out.println();
        System.out.println("-- Registrar cliente --");

        try {
            System.out.print("Nombre: ");
            String name = scanner.nextLine();

            System.out.print("Identificación: ");
            String identification = scanner.nextLine();

            System.out.print("Teléfono: ");
            String phone = scanner.nextLine();

            System.out.print("Correo electrónico: ");
            String email = scanner.nextLine();

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

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (RuntimeException e) {

            System.out.println(
                    "No se pudo registrar el cliente: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Lists registered customers and handles persistence errors.
     */
    private void listCustomers() {
        System.out.println();
        System.out.println("-- Clientes --");

        try {
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

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (RuntimeException e) {

            System.out.println(
                    "No se pudieron consultar los clientes: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Lists registered sellers and handles persistence errors.
     */
    private void listSellers() {
        System.out.println();
        System.out.println("-- Vendedores --");

        try {
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

        } catch (GameZoneException e) {

            errorHandler.showError(e);

        } catch (RuntimeException e) {

            System.out.println(
                    "No se pudieron consultar los vendedores: "
                            + e.getMessage()
            );
        }
    }
}
