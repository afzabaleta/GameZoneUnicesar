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
}