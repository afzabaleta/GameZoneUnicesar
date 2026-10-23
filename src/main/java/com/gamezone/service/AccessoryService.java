
package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.validation.AccessoryValidator;

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
                    "Accessory repository cannot be null"
            );
        }

        this.accessoryRepository = accessoryRepository;
    }

    /**
     * Registers a controller accessory.
     *
     * @param controller controller to register
     */
    public void registerController(Controller controller) {
        registerAccessory(controller);
    }

    /**
     * Registers a cable accessory.
     *
     * @param cable cable to register
     */
    public void registerCable(Cable cable) {
        registerAccessory(cable);
    }

    /**
     * Registers a memory accessory.
     *
     * @param memory memory to register
     */
    public void registerMemory(Memory memory) {
        registerAccessory(memory);
    }

    /**
     * Validates and registers an accessory.
     *
     * @param accessory accessory to register
     */
    private void registerAccessory(Accessory accessory) {

        AccessoryValidator.validateAccessoryData(accessory);

        Accessory existingAccessory =
                accessoryRepository.findByIdentifier(
                        accessory.getIdentifier()
                );

        AccessoryValidator.validateUniqueAccessory(
                accessory,
                existingAccessory
        );

        accessoryRepository.save(accessory);
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

        AccessoryValidator.validateAccessoryType(type);

        return accessoryRepository.findByType(type);
    }

    /**
     * Returns accessories compatible with a console.
     *
     * @param consoleId identifier of the console
     * @return list of compatible accessories
     */
    public List<Accessory> listCompatibleWithConsole(String consoleId) {

        AccessoryValidator.validateConsoleIdentifier(consoleId);

        return accessoryRepository.findCompatibleWithConsole(
                consoleId
        );
    }

    /**
     * Updates the stock of an accessory.
     *
     * @param identifier accessory identifier
     * @param newStock new available quantity
     */
    public void updateStock(String identifier, int newStock) {

        AccessoryValidator.validateStockIdentifier(identifier);
        AccessoryValidator.validateNewStock(newStock);

        Accessory accessory =
                accessoryRepository.findByIdentifier(identifier);

        AccessoryValidator.validateAccessoryExists(
                accessory,
                identifier
        );

        accessory.setAvailableQuantity(newStock);

        try {
            accessoryRepository.saveAll(
                    accessoryRepository.findAll()
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not update accessory stock.",
                    e
            );
        }
    }

    /**
     * Restores accessory stock after a successful return.
     *
     * @param accessoryId identifier of the accessory
     * @param quantity quantity to restore
     */
    public void restoreStock(String accessoryId, int quantity) {

        AccessoryValidator.validateStockIdentifier(accessoryId);
        AccessoryValidator.validateRestoredQuantity(quantity);

        Accessory accessory =
                accessoryRepository.findByIdentifier(accessoryId);

        AccessoryValidator.validateAccessoryExists(
                accessory,
                accessoryId
        );

        int restoredStock =
                accessory.getAvailableQuantity() + quantity;

        accessory.setAvailableQuantity(restoredStock);

        try {
            accessoryRepository.saveAll(
                    accessoryRepository.findAll()
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not restore accessory stock.",
                    e
            );
        }
    }
}
