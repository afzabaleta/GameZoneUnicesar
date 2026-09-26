package com.gamezone.service;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
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
    private final List<Warranty> warranties;

    /**
     * Creates a WarrantyService and loads persisted warranties.
     *
     * @param repository repository used for warranty persistence
     */
    public WarrantyService(WarrantyRepository repository) {

        if (repository == null) {
            throw new IllegalArgumentException(
                    "Warranty repository cannot be null."
            );
        }

        this.repository = repository;
        this.warranties = repository.loadAll();
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
        List<Warranty> activeWarranties = new ArrayList<>();

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
    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {

        if (daysAhead < 0) {
            throw new IllegalArgumentException(
                    "Days ahead cannot be negative."
            );
        }

        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(daysAhead);

        List<Warranty> expiringWarranties = new ArrayList<>();

        for (Warranty warranty : warranties) {

            LocalDate endDate = warranty.getEndDate();

            boolean expiresSoon =
                    !endDate.isBefore(today)
                            && !endDate.isAfter(limitDate);

            if (expiresSoon) {
                expiringWarranties.add(warranty);
            }
        }

        return expiringWarranties;
    }
}