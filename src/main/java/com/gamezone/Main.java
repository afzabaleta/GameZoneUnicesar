package com.gamezone;

import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;
import com.gamezone.ui.GameZoneUI;
import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.service.AccessoryService;

/**
 * Entry point of the GameZoneUnicesar application.
 *
 * <p>Wires together repositories, services and the console UI,
 * then starts the application.</p>
 */
public class Main {

    /**
     * Builds the dependency graph and starts the console interface.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        PersonRepository personRepository = new PersonRepository();
        ProductRepository productRepository = new ProductRepository();
        AccessoryRepository accessoryRepository = new AccessoryRepository(productRepository);
        SaleRepository saleRepository = new SaleRepository(personRepository, productRepository, accessoryRepository);

        PersonService personService = new PersonService(personRepository);
        ProductService productService = new ProductService(productRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository);
        SaleService saleService = new SaleService(saleRepository, productService, accessoryService);

        GameZoneUI gameZoneUI = new GameZoneUI(personService, productService, accessoryService, saleService);
        gameZoneUI.showMainMenu();
    }
}