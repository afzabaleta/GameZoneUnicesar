
package com.gamezone;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.PromotionRepository;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.WarrantyRepository;

import com.gamezone.service.AccessoryService;
import com.gamezone.service.InventoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;

import com.gamezone.ui.GameZoneUI;

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
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        PersonRepository personRepository =
                new PersonRepository();

        ProductRepository productRepository =
                new ProductRepository();

        AccessoryRepository accessoryRepository =
                new AccessoryRepository(
                        productRepository
                );

        PromotionRepository promotionRepository =
                new PromotionRepository();

        PersonService personService =
                new PersonService(
                        personRepository
                );

        ProductService productService =
                new ProductService(
                        productRepository
                );

        AccessoryService accessoryService =
                new AccessoryService(
                        accessoryRepository
                );

        InventoryService inventoryService =
                new InventoryService(
                        productService,
                        accessoryService
                );

        SaleRepository saleRepository =
                new SaleRepository(
                        personService::findCustomerById,
                        personService::findSellerById,
                        inventoryService::findItem
                );

        PromotionService promotionService =
                new PromotionService(
                        promotionRepository
                );

        WarrantyRepository warrantyRepository =
                new WarrantyRepository();

        WarrantyService warrantyService =
                new WarrantyService(
                        warrantyRepository,
                        saleRepository,
                        productService
                );

        SaleService saleService =
                new SaleService(
                        saleRepository,
                        inventoryService,
                        promotionService,
                        warrantyService
                );

        ReturnRepository returnRepository =
                new ReturnRepository();

        ReturnService returnService =
                new ReturnService(
                        returnRepository,
                        saleService,
                        inventoryService,
                        warrantyService
                );

        GameZoneUI gameZoneUI =
                new GameZoneUI(
                        personService,
                        productService,
                        accessoryService,
                        inventoryService,
                        saleService,
                        promotionService,
                        returnService,
                        warrantyService
                );

        gameZoneUI.showMainMenu();
    }
}
