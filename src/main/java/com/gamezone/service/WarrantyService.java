package com.gamezone.service;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for warranty assignment and warranty queries.
 */
public class WarrantyService {

    private final WarrantyRepository repository;
    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final List<Warranty> warranties;

    /**
     * Creates a WarrantyService and loads persisted warranties.
     *
     * @param repository repository used for warranty persistence
     * @param saleRepository repository used to resolve sales
     * @param productService service used to resolve products
     */
    public WarrantyService(
            WarrantyRepository repository,
            SaleRepository saleRepository,
            ProductService productService) {

        if (repository == null) {
            throw new IllegalArgumentException(
                    "Warranty repository cannot be null."
            );
        }

        if (saleRepository == null) {
            throw new IllegalArgumentException(
                    "Sale repository cannot be null."
            );
        }

        if (productService == null) {
            throw new IllegalArgumentException(
                    "Product service cannot be null."
            );
        }

        this.repository = repository;
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.warranties = loadWarranties();
    }

    /**
     * Returns all registered warranties.
     *
     * @return copy of all registered warranties
     */
    public List<Warranty> listAllWarranties() {
        return new ArrayList<>(warranties);
    }

    /**
     * Creates and persists a basic warranty.
     *
     * @param product associated product
     * @param sale associated sale
     * @param startDate warranty start date
     * @return created basic warranty
     */
    public BasicWarranty assignBasicWarranty(
            Product product,
            Sale sale,
            LocalDate startDate) {

        String id =
                "WAR-BAS-"
                        + UUID.randomUUID()
                        .toString()
                        .substring(0, 8);

        BasicWarranty warranty =
                new BasicWarranty(
                        id,
                        product,
                        sale,
                        startDate
                );

        warranties.add(warranty);
        repository.saveAll(warranties);

        return warranty;
    }

    /**
     * Creates and persists an extended warranty.
     *
     * @param product associated product
     * @param sale associated sale
     * @param startDate warranty start date
     * @return created extended warranty
     */
    public ExtendedWarranty assignExtendedWarranty(
            Product product,
            Sale sale,
            LocalDate startDate) {

        String id =
                "WAR-EXT-"
                        + UUID.randomUUID()
                        .toString()
                        .substring(0, 8);

        ExtendedWarranty warranty =
                new ExtendedWarranty(
                        id,
                        product,
                        sale,
                        startDate
                );

        warranties.add(warranty);
        repository.saveAll(warranties);

        return warranty;
    }

    /**
     * Finds a warranty by product and sale reference.
     *
     * @param productId product identifier
     * @param saleId sale reference
     * @return matching warranty or null when none exists
     */
    public Warranty findWarrantyByProduct(
            String productId,
            String saleId) {

        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException(
                    "Product identifier cannot be blank."
            );
        }

        if (saleId == null || saleId.isBlank()) {
            throw new IllegalArgumentException(
                    "Sale identifier cannot be blank."
            );
        }

        for (Warranty warranty : warranties) {

            boolean sameProduct =
                    warranty.getProduct()
                            .getIdentifier()
                            .equalsIgnoreCase(productId);

            boolean sameSale =
                    warranty.getSale()
                            .getDate()
                            .toString()
                            .equals(saleId);

            if (sameProduct && sameSale) {
                return warranty;
            }
        }

        return null;
    }

    /**
     * Returns warranties that are active on the current date.
     *
     * @return list of active warranties
     */
    public List<Warranty> listActiveWarranties() {

        LocalDate today = LocalDate.now();
        List<Warranty> activeWarranties =
                new ArrayList<>();

        for (Warranty warranty : warranties) {

            if (warranty.isActive(today)) {
                activeWarranties.add(warranty);
            }
        }

        return activeWarranties;
    }

    /**
     * Returns warranties whose expiration date is within the next
     * specified number of days.
     *
     * @param daysAhead number of days to look ahead
     * @return list of warranties expiring soon
     */
    public List<Warranty> listWarrantiesExpiringSoon(
            int daysAhead) {

        if (daysAhead < 0) {
            throw new IllegalArgumentException(
                    "Days ahead cannot be negative."
            );
        }

        LocalDate today = LocalDate.now();
        LocalDate limitDate =
                today.plusDays(daysAhead);

        List<Warranty> expiringWarranties =
                new ArrayList<>();

        for (Warranty warranty : warranties) {

            LocalDate endDate =
                    warranty.getEndDate();

            boolean expiresSoon =
                    !endDate.isBefore(today)
                            && !endDate.isAfter(limitDate);

            if (expiresSoon) {
                expiringWarranties.add(warranty);
            }
        }

        return expiringWarranties;
    }

    /**
     * Loads persisted warranties and resolves their references
     * through the service layer.
     *
     * @return reconstructed warranties
     */
    private List<Warranty> loadWarranties() {

        List<Warranty> loadedWarranties =
                new ArrayList<>();

        List<Sale> sales =
                saleRepository.loadSales();

        for (WarrantyRepository.WarrantyRecord record :
                repository.loadAll()) {

            Product product =
                    findProduct(record.productId());

            Sale sale =
                    findSale(
                            sales,
                            record.saleId()
                    );

            if (product == null || sale == null) {
                continue;
            }

            Warranty warranty;

            if ("BASIC".equalsIgnoreCase(record.type())) {

                warranty =
                        new BasicWarranty(
                                record.id(),
                                product,
                                sale,
                                record.startDate()
                        );

            } else if ("EXTENDED".equalsIgnoreCase(
                    record.type())) {

                warranty =
                        new ExtendedWarranty(
                                record.id(),
                                product,
                                sale,
                                record.startDate()
                        );

            } else {
                throw new IllegalArgumentException(
                        "Unsupported warranty type: "
                                + record.type()
                );
            }

            loadedWarranties.add(warranty);
        }

        return loadedWarranties;
    }

    /**
     * Finds a product by identifier.
     *
     * @param productId product identifier
     * @return matching product or null
     */
    private Product findProduct(String productId) {

        for (Product product :
                productService.listProducts()) {

            if (product.getIdentifier()
                    .equalsIgnoreCase(productId)) {

                return product;
            }
        }

        return null;
    }

    /**
     * Finds a sale using its stored date reference.
     *
     * @param sales loaded sales
     * @param saleId sale reference
     * @return matching sale or null
     */
    private Sale findSale(
            List<Sale> sales,
            String saleId) {

        for (Sale sale : sales) {

            if (sale.getDate()
                    .toString()
                    .equals(saleId)) {

                return sale;
            }
        }

        return null;
    }
}