package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.persistence.AccessoryRepository;

import java.io.IOException;
import java.util.List;

/**
 * Service responsible for accessory business logic.
 */
public class AccessoryService {

    private final AccessoryRepository accessoryRepository;

    /**
     * Creates an accessory service with the specified repository.
     *
     * @param accessoryRepository repository used to manage accessories
     */
    public AccessoryService(AccessoryRepository accessoryRepository) {
        if (accessoryRepository == null) {
            throw new IllegalArgumentException(
                    "Accessory repository cannot be null");
        }

        this.accessoryRepository = accessoryRepository;
    }

    /**
     * Registers a controller accessory.
     *
     * @param controller controller to register
     */
    public void registerController(Controller controller) {
        validateAccessory(controller);
        accessoryRepository.save(controller);
    }

    /**
     * Registers a cable accessory.
     *
     * @param cable cable to register
     */
    public void registerCable(Cable cable) {
        validateAccessory(cable);
        accessoryRepository.save(cable);
    }

    /**
     * Registers a memory accessory.
     *
     * @param memory memory to register
     */
    public void registerMemory(Memory memory) {
        validateAccessory(memory);
        accessoryRepository.save(memory);
    }

    /**
     * Returns all accessories.
     *
     * @return list of accessories
     */
    public List<Accessory> listAccessories() {
        return accessoryRepository.findAll();
    }

    /**
     * Returns accessories filtered by their type.
     *
     * @param type accessory type to search
     * @return list of accessories with the specified type
     */
    public List<Accessory> listByType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException(
                    "Accessory type cannot be blank");
        }

        return accessoryRepository.findByType(type);
    }

    /**
     * Returns accessories compatible with a console.
     *
     * @param consoleId identifier of the console
     * @return list of compatible accessories
     */
    public List<Accessory> listCompatibleWithConsole(String consoleId) {
        if (consoleId == null || consoleId.isBlank()) {
            throw new IllegalArgumentException(
                    "Console identifier cannot be blank");
        }

        return accessoryRepository.findCompatibleWithConsole(consoleId);
    }

    /**
     * Updates the stock of an accessory.
     *
     * @param identifier accessory identifier
     * @param newStock new available quantity
     */
    public void updateStock(String identifier, int newStock) {

        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException(
                    "Accessory identifier cannot be blank");
        }

        if (newStock < 0) {
            throw new IllegalArgumentException(
                    "Accessory stock cannot be negative");
        }

        Accessory accessory =
                accessoryRepository.findByIdentifier(identifier);

        if (accessory == null) {
            throw new IllegalArgumentException(
                    "Accessory not found: " + identifier);
        }

        accessory.setAvailableQuantity(newStock);

        try {
            accessoryRepository.saveAll(
                    accessoryRepository.findAll());
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not update accessory stock.", e);
        }
    }

    /**
     * Validates the common accessory data.
     *
     * @param accessory accessory to validate
     */
    private void validateAccessory(Accessory accessory) {

        if (accessory == null) {
            throw new IllegalArgumentException(
                    "Accessory cannot be null");
        }

        if (accessory.getIdentifier() == null
                || accessory.getIdentifier().isBlank()) {
            throw new IllegalArgumentException(
                    "Accessory identifier cannot be blank");
        }

        if (accessory.getTitle() == null
                || accessory.getTitle().isBlank()) {
            throw new IllegalArgumentException(
                    "Accessory title cannot be blank");
        }

        if (accessory.getPrice() < 0) {
            throw new IllegalArgumentException(
                    "Accessory price cannot be negative");
        }

        if (accessory.getAvailableQuantity() < 0) {
            throw new IllegalArgumentException(
                    "Accessory stock cannot be negative");
        }

        if (accessoryRepository.findByIdentifier(
                accessory.getIdentifier()) != null) {
            throw new IllegalArgumentException(
                    "Accessory already exists: "
                            + accessory.getIdentifier());
        }
    }
}