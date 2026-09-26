package com.gamezone.persistence;

import com.gamezone.model.Warranty;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides persistence operations for warranties.
 */
public class WarrantyRepository {

    private final List<Warranty> warranties;


    /**
     * Creates a warranty repository.
     */
    public WarrantyRepository() {

        this.warranties = new ArrayList<>();
    }


    /**
     * Saves a warranty.
     *
     * @param warranty warranty to save
     */
    public void save(Warranty warranty) {

        if (warranty == null) {
            throw new IllegalArgumentException(
                    "Warranty cannot be null.");
        }

        warranties.add(warranty);
    }


    /**
     * Returns all warranties.
     *
     * @return warranty list
     */
    public List<Warranty> findAll() {

        return new ArrayList<>(warranties);
    }
}
