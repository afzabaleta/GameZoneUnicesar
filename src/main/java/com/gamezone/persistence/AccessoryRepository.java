package com.gamezone.persistence;

import com.gamezone.model.Accessory;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for persisting and loading accessories.
 */
public class AccessoryRepository {

    private final List<Accessory> accessories;

    /**
     * Creates an accessory repository.
     */
    public AccessoryRepository() {
        this.accessories = new ArrayList<>();
    }

    /**
     * Returns all registered accessories.
     *
     * @return list of accessories
     */
    public List<Accessory> findAll() {
        return new ArrayList<>(accessories);
    }

    /**
     * Finds an accessory by its identifier.
     *
     * @param identifier accessory identifier
     * @return matching accessory or null if not found
     */
    public Accessory findByIdentifier(String identifier) {
        for (Accessory accessory : accessories) {
            if (accessory.getIdentifier().equals(identifier)) {
                return accessory;
            }
        }

        return null;
    }
}