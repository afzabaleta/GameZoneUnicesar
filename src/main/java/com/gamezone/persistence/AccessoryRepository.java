package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Console;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.model.Product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository responsible for persisting and loading accessories.
 */
public class AccessoryRepository {

    private static final String FILE_PATH = "data/accessories.csv";

    private final Path accessoriesPath;
    private final List<Accessory> accessories;
    private final ProductRepository productRepository;

    /**
     * Creates an accessory repository using the default CSV file.
     */
    public AccessoryRepository(ProductRepository productRepository) {
        if (productRepository == null) {
            throw new IllegalArgumentException("Product repository cannot be null");
        }

        this.accessoriesPath = Paths.get(FILE_PATH);
        this.accessories = new ArrayList<>();
        this.productRepository = productRepository;

        try {
            loadAccessories();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load accessories.", e);
        }
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

    /**
     * Returns accessories of the requested type.
     *
     * @param type accessory type: controller, cable or memory
     * @return matching accessories
     */
    public List<Accessory> findByType(String type) {
        String normalizedType = type == null
                ? ""
                : type.trim().toLowerCase();

        return accessories.stream()
                .filter(accessory ->
                        (normalizedType.equals("controller")
                                && accessory instanceof Controller)
                                || (normalizedType.equals("cable")
                                && accessory instanceof Cable)
                                || (normalizedType.equals("memory")
                                && accessory instanceof Memory))
                .collect(Collectors.toList());
    }

    /**
     * Returns accessories compatible with a console identifier.
     *
     * @param consoleIdentifier console identifier
     * @return compatible accessories
     */
    public List<Accessory> findCompatibleWithConsole(
            String consoleIdentifier) {

        if (consoleIdentifier == null || consoleIdentifier.isBlank()) {
            return new ArrayList<>();
        }

        try {
            loadAccessories();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to refresh accessories before compatibility query.",
                    e
            );
        }

        List<Accessory> result = new ArrayList<>();

        for (Accessory accessory : accessories) {
            for (Console console : accessory.getCompatibleConsoles()) {

                if (console.getIdentifier().equals(consoleIdentifier)) {
                    result.add(accessory);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Loads accessories from the CSV file.
     *
     * @return loaded accessories
     * @throws IOException if the file cannot be read
     */
    public List<Accessory> loadAccessories() throws IOException {

        accessories.clear();

        if (!Files.exists(accessoriesPath)) {
            return accessories;
        }

        List<String> lines = Files.readAllLines(accessoriesPath);

        for (String line : lines) {

            if (line.isBlank()) {
                continue;
            }

            Accessory accessory = parseAccessory(line);

            if (accessory != null) {
                accessories.add(accessory);
            }
        }

        return new ArrayList<>(accessories);
    }

    /**
     * Parses one CSV row into an accessory object.
     *
     * @param line CSV row
     * @return parsed accessory or null for invalid rows
     */
    private Accessory parseAccessory(String line) {

        String[] data = line.split("\\|", -1);

        if (data.length < 8) {
            return null;
        }

        String type = data[0].trim().toLowerCase();
        String identifier = data[1].trim();
        String title = data[2].trim();
        double price = Double.parseDouble(data[3].trim());
        int quantity = Integer.parseInt(data[4].trim());

        List<Console> compatibleConsoles =
                resolveCompatibleConsoles(data[7]);

        return switch (type) {

            case "controller" -> new Controller(
                    identifier,
                    title,
                    price,
                    quantity,
                    data[5].trim(),
                    compatibleConsoles
            );

            case "cable" -> {

                Cable cable = new Cable(
                        identifier,
                        title,
                        price,
                        quantity,
                        Double.parseDouble(data[5].trim()),
                        data[6].trim()
                );

                cable.setCompatibleConsoles(compatibleConsoles);

                yield cable;
            }

            case "memory" -> new Memory(
                    identifier,
                    title,
                    price,
                    quantity,
                    Double.parseDouble(data[5].trim()),
                    data[6].trim(),
                    compatibleConsoles
            );

            default -> null;
        };
    }

    /**
     * Resolves console identifiers to existing Console objects.
     *
     * @param rawIdentifiers comma-separated console identifiers
     * @return matching consoles
     */
    private List<Console> resolveCompatibleConsoles(
            String rawIdentifiers) {

        List<Console> consoles = new ArrayList<>();

        if (rawIdentifiers == null || rawIdentifiers.isBlank()) {
            return consoles;
        }

        List<Product> products = productRepository.findAll();

        for (String rawIdentifier : rawIdentifiers.split(",")) {

            String identifier = rawIdentifier.trim();

            for (Product product : products) {

                if (product instanceof Console
                        && product.getIdentifier().equals(identifier)) {

                    consoles.add((Console) product);
                    break;
                }
            }
        }

        return consoles;
    }

    /**
     * Saves a new accessory and persists the full accessory list.
     *
     * @param accessory accessory to save
     */
    public void save(Accessory accessory) {
        accessories.add(accessory);

        try {
            saveAll(accessories);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to save accessory.", e);
        }
    }

    /**
     * Persists all accessories in CSV format.
     *
     * @param accessoriesToSave accessories to persist
     * @throws IOException if the file cannot be written
     */
    public void saveAll(List<Accessory> accessoriesToSave)
            throws IOException {

        Path parent = accessoriesPath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<Accessory> snapshot =
                new ArrayList<>(accessoriesToSave);

        List<String> lines = new ArrayList<>();

        for (Accessory accessory : snapshot) {
            lines.add(toCsv(accessory));
        }

        Files.write(accessoriesPath, lines);

        accessories.clear();
        accessories.addAll(snapshot);
    }

    /**
     * Converts an accessory to the repository CSV representation.
     *
     * @param accessory accessory to convert
     * @return CSV row
     */
    private String toCsv(Accessory accessory) {

        String type;
        String specific1;
        String specific2;

        if (accessory instanceof Controller controller) {

            type = "CONTROLLER";
            specific1 = controller.getConnectionType();
            specific2 = "-";

        } else if (accessory instanceof Cable cable) {

            type = "CABLE";
            specific1 = String.valueOf(cable.getLength());
            specific2 = cable.getConnectorType();

        } else if (accessory instanceof Memory memory) {

            type = "MEMORY";
            specific1 = String.valueOf(memory.getCapacityGB());
            specific2 = memory.getMemoryType();

        } else {
            throw new IllegalArgumentException(
                    "Unsupported accessory type.");
        }

        String compatibleIds = accessory.getCompatibleConsoles()
                .stream()
                .map(Console::getIdentifier)
                .collect(Collectors.joining(","));

        return String.join(
                "|",
                type,
                accessory.getIdentifier(),
                accessory.getTitle(),
                String.valueOf(accessory.getPrice()),
                String.valueOf(accessory.getAvailableQuantity()),
                specific1,
                specific2,
                compatibleIds
        );
    }
}