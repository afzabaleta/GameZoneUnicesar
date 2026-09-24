package com.gamezone.persistence;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for persisting promotions in CSV format.
 */
public class PromotionRepository {

    private static final String FILE_PATH = "data/promotions.csv";

    private final Path promotionsPath;

    /**
     * Creates a promotion repository using the default CSV file.
     */
    public PromotionRepository() {
        this.promotionsPath = Paths.get(FILE_PATH);
    }

    /**
     * Loads all promotions from the CSV file.
     *
     * @return list of promotions
     */
    public List<Promotion> loadAll() {
        List<Promotion> promotions = new ArrayList<>();

        if (!Files.exists(promotionsPath)) {
            return promotions;
        }

        try {
            List<String> lines = Files.readAllLines(promotionsPath);

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                Promotion promotion = parsePromotion(line);

                if (promotion != null) {
                    promotions.add(promotion);
                }
            }

            return promotions;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to load promotions.",
                    e
            );
        }
    }

    /**
     * Saves all promotions to the CSV file.
     *
     * @param promotions promotions to save
     */
    public void saveAll(List<Promotion> promotions) {
        try {
            Path parent = promotionsPath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = new ArrayList<>();

            for (Promotion promotion : promotions) {
                lines.add(toCsv(promotion));
            }

            Files.write(promotionsPath, lines);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to save promotions.",
                    e
            );
        }
    }

    /**
     * Parses a CSV row into a promotion.
     *
     * Format:
     * TYPE|IDENTIFIER|NAME|START_DATE|END_DATE|PERCENTAGE|CATEGORY|MINIMUM_QUANTITY
     *
     * @param line CSV row
     * @return parsed promotion
     */
    private Promotion parsePromotion(String line) {
        String[] data = line.split("\\|", -1);

        if (data.length != 8) {
            throw new IllegalArgumentException(
                    "Invalid promotion CSV row: " + line
            );
        }

        String type = data[0].trim().toUpperCase();
        String identifier = data[1].trim();
        String name = data[2].trim();

        LocalDate startDate =
                LocalDate.parse(data[3].trim());

        LocalDate endDate =
                LocalDate.parse(data[4].trim());

        double percentage =
                Double.parseDouble(data[5].trim());

        return switch (type) {

            case "PERCENTAGE" -> new PercentageDiscount(
                    identifier,
                    name,
                    startDate,
                    endDate,
                    percentage
            );

            case "CATEGORY" -> new CategoryDiscount(
                    identifier,
                    name,
                    startDate,
                    endDate,
                    data[6].trim(),
                    percentage
            );

            case "BULK" -> new BulkPurchaseDiscount(
                    identifier,
                    name,
                    startDate,
                    endDate,
                    Integer.parseInt(data[7].trim()),
                    percentage
            );

            default -> throw new IllegalArgumentException(
                    "Unknown promotion type: " + type
            );
        };
    }

    /**
     * Converts a promotion to its CSV representation.
     *
     * @param promotion promotion to convert
     * @return CSV row
     */
    private String toCsv(Promotion promotion) {

        if (promotion instanceof PercentageDiscount percentage) {
            return String.join(
                    "|",
                    "PERCENTAGE",
                    percentage.getIdentifier(),
                    percentage.getName(),
                    percentage.getStartDate().toString(),
                    percentage.getEndDate().toString(),
                    String.valueOf(
                            percentage.getDiscountPercentage()
                    ),
                    "-",
                    "-"
            );
        }

        if (promotion instanceof CategoryDiscount category) {
            return String.join(
                    "|",
                    "CATEGORY",
                    category.getIdentifier(),
                    category.getName(),
                    category.getStartDate().toString(),
                    category.getEndDate().toString(),
                    String.valueOf(
                            category.getDiscountPercentage()
                    ),
                    category.getCategory(),
                    "-"
            );
        }

        if (promotion instanceof BulkPurchaseDiscount bulk) {
            return String.join(
                    "|",
                    "BULK",
                    bulk.getIdentifier(),
                    bulk.getName(),
                    bulk.getStartDate().toString(),
                    bulk.getEndDate().toString(),
                    String.valueOf(
                            bulk.getDiscountPercentage()
                    ),
                    "-",
                    String.valueOf(
                            bulk.getMinimumQuantity()
                    )
            );
        }

        throw new IllegalArgumentException(
                "Unsupported promotion type."
        );
    }
}
