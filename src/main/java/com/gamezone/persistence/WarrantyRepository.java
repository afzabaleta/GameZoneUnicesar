package com.gamezone.persistence;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Warranty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistence of warranty records.
 */
public class WarrantyRepository {

    private static final String FILE_PATH =
            "data/warranties.csv";

    private static final String SEPARATOR = ";";

    private final Path filePath;

    /**
     * Creates a warranty repository.
     */
    public WarrantyRepository() {
        this.filePath = Paths.get(FILE_PATH);
    }

    /**
     * Saves warranty records to the CSV file.
     *
     * Only warranty data and references are persisted.
     *
     * @param warranties warranties to persist
     */
    public void saveAll(List<Warranty> warranties) {

        if (warranties == null) {
            throw new IllegalArgumentException(
                    "Warranty list cannot be null."
            );
        }

        try {

            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            try (BufferedWriter writer =
                         Files.newBufferedWriter(filePath)) {

                for (Warranty warranty : warranties) {

                    writer.write(toCsvLine(warranty));
                    writer.newLine();
                }
            }

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Could not save warranties.",
                    e
            );
        }
    }

    /**
     * Loads persisted warranty records.
     *
     * The repository only reads stored identifiers and warranty data.
     * It does not resolve Product or Sale objects.
     *
     * @return persisted warranty records
     */
    public List<WarrantyRecord> loadAll() {

        List<WarrantyRecord> records =
                new ArrayList<>();

        if (Files.notExists(filePath)) {
            return records;
        }

        try (BufferedReader reader =
                     Files.newBufferedReader(filePath)) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) {
                    continue;
                }

                WarrantyRecord record =
                        fromCsvLine(line);

                if (record != null) {
                    records.add(record);
                }
            }

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Could not load warranties.",
                    e
            );
        }

        return records;
    }

    /**
     * Converts a warranty into a CSV record.
     *
     * @param warranty warranty to convert
     * @return CSV representation
     */
    private String toCsvLine(Warranty warranty) {

        String type;

        if (warranty instanceof BasicWarranty) {

            type = "BASIC";

        } else if (warranty instanceof ExtendedWarranty) {

            type = "EXTENDED";

        } else {

            throw new IllegalArgumentException(
                    "Unsupported warranty type."
            );
        }

        return String.join(
                SEPARATOR,
                type,
                warranty.getId(),
                warranty.getProduct().getIdentifier(),
                warranty.getSale().getDate().toString(),
                warranty.getStartDate().toString()
        );
    }

    /**
     * Converts a CSV record into a persistence-only record.
     *
     * @param line CSV record
     * @return persistence record
     */
    private WarrantyRecord fromCsvLine(String line) {

        String[] fields =
                line.split(SEPARATOR, -1);

        if (fields.length != 5) {

            throw new IllegalArgumentException(
                    "Invalid warranty record."
            );
        }

        String type = fields[0];
        String id = fields[1];
        String productId = fields[2];
        String saleId = fields[3];

        LocalDate startDate =
                LocalDate.parse(fields[4]);

        return new WarrantyRecord(
                type,
                id,
                productId,
                saleId,
                startDate
        );
    }

    /**
     * Persistence record containing only stored warranty data
     * and references.
     */
    public record WarrantyRecord(
            String type,
            String id,
            String productId,
            String saleId,
            LocalDate startDate) {
    }
}