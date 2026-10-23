
package com.gamezone.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists return records without resolving domain objects.
 */
public class ReturnRepository {

    private static final String FILE_PATH = "data/returns.csv";
    private final Path returnsPath = Paths.get(FILE_PATH);

    public ReturnRepository() {
    }

    public void saveAll(List<ReturnRecord> records) {
        try {
            Files.createDirectories(returnsPath.getParent());

            List<String> lines = new ArrayList<>();

            for (ReturnRecord record : records) {
                lines.add(toCsv(record));
            }

            Files.write(returnsPath, lines);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to write returns file: " + FILE_PATH, e);
        }
    }

    public List<ReturnRecord> loadAll() {
        List<ReturnRecord> records = new ArrayList<>();

        if (!Files.exists(returnsPath)) {
            return records;
        }

        try {
            for (String line : Files.readAllLines(returnsPath)) {
                if (!line.isBlank()) {
                    records.add(fromCsv(line));
                }
            }

            return records;

        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException(
                    "Unable to read returns file: " + FILE_PATH, e);
        }
    }

    private String toCsv(ReturnRecord record) {
        String reason = record.reason().replace(";", ",");

        return record.identifier()
                + ";" + record.returnDate()
                + ";" + record.saleReference()
                + ";" + record.productIds()
                + ";" + reason
                + ";" + record.refundAmount()
                + ";" + record.warrantyRefundAmount();
    }

    private ReturnRecord fromCsv(String line) {
        String[] fields = line.split(";", -1);

        if (fields.length != 6 && fields.length != 7) {
            throw new IllegalArgumentException(
                    "Invalid return record: expected 6 or 7 fields.");
        }

        String identifier = fields[0].trim();
        LocalDate returnDate = LocalDate.parse(fields[1].trim());
        String saleReference = fields[2].trim();
        String productIds = fields[3].trim();
        String reason = fields[4];
        double refundAmount = Double.parseDouble(fields[5].trim());

        double warrantyRefundAmount = fields.length == 7
                ? Double.parseDouble(fields[6].trim())
                : 0.0;

        return new ReturnRecord(
                identifier,
                returnDate,
                saleReference,
                productIds,
                reason,
                refundAmount,
                warrantyRefundAmount);
    }
}
