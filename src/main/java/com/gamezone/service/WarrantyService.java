package com.gamezone.service;

import com.gamezone.model.Warranty;
import com.gamezone.persistence.WarrantyRepository;

import java.util.List;


/**
 * Provides business operations for warranties.
 */
public class WarrantyService {


    private final WarrantyRepository warrantyRepository;


    /**
     * Creates a warranty service.
     *
     * @param warrantyRepository repository used to store warranties
     */
    public WarrantyService(WarrantyRepository warrantyRepository) {

        if (warrantyRepository == null) {
            throw new IllegalArgumentException(
                    "Warranty repository cannot be null.");
        }

        this.warrantyRepository = warrantyRepository;
    }


    /**
     * Registers a warranty.
     *
     * @param warranty warranty to register
     * @return registered warranty
     */
    public Warranty registerWarranty(Warranty warranty) {


        if (warranty == null) {
            throw new IllegalArgumentException(
                    "Warranty cannot be null.");
        }


        warrantyRepository.save(warranty);

        return warranty;
    }


    /**
     * Returns all warranties.
     *
     * @return warranty list
     */
    public List<Warranty> listWarranties() {

        return warrantyRepository.findAll();
    }


    /**
     * Finds warranties by product.
     *
     * @param productId product identifier
     * @return warranties associated with product
     */
    public List<Warranty> findByProduct(String productId) {


        if (productId == null || productId.isBlank()) {

            throw new IllegalArgumentException(
                    "Product identifier cannot be blank.");
        }


        List<Warranty> result = new java.util.ArrayList<>();


        for (Warranty warranty : warrantyRepository.findAll()) {

            if (warranty.getProduct()
                    .getIdentifier()
                    .equals(productId)) {

                result.add(warranty);
            }
        }


        return result;
    }
}