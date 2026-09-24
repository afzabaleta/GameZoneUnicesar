package com.gamezone.service;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
import com.gamezone.persistence.PromotionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for promotion business operations.
 */
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(
            PromotionRepository promotionRepository) {

        if (promotionRepository == null) {
            throw new IllegalArgumentException(
                    "Promotion repository cannot be null."
            );
        }

        this.promotionRepository = promotionRepository;
    }

    public void registerPercentageDiscount(
            String identifier,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            double percentage) {

        savePromotion(
                new PercentageDiscount(
                        identifier,
                        name,
                        startDate,
                        endDate,
                        percentage
                )
        );
    }

    public void registerCategoryDiscount(
            String identifier,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            double percentage,
            String targetCategory) {

        savePromotion(
                new CategoryDiscount(
                        identifier,
                        name,
                        startDate,
                        endDate,
                        targetCategory,
                        percentage
                )
        );
    }

    public void registerBulkPurchaseDiscount(
            String identifier,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            int minimumQuantity,
            double percentage) {

        savePromotion(
                new BulkPurchaseDiscount(
                        identifier,
                        name,
                        startDate,
                        endDate,
                        minimumQuantity,
                        percentage
                )
        );
    }

    public List<Promotion> listAllPromotions() {
        return promotionRepository.loadAll();
    }

    public List<Promotion> listActivePromotions() {

        LocalDate today = LocalDate.now();

        List<Promotion> activePromotions =
                new ArrayList<>();

        for (Promotion promotion :
                promotionRepository.loadAll()) {

            if (promotion.isActive(today)) {
                activePromotions.add(promotion);
            }
        }

        return activePromotions;
    }

    /**
     * Finds a promotion by identifier.
     *
     * @param id promotion identifier
     * @return matching promotion or null
     */
    public Promotion findById(String id) {

        if (id == null || id.isBlank()) {
            return null;
        }

        for (Promotion promotion :
                promotionRepository.loadAll()) {

            if (promotion.getIdentifier().equals(id)) {
                return promotion;
            }
        }

        return null;
    }

    private void savePromotion(Promotion promotion) {

        List<Promotion> promotions =
                new ArrayList<>(
                        promotionRepository.loadAll()
                );

        for (Promotion existing : promotions) {

            if (existing.getIdentifier()
                    .equals(promotion.getIdentifier())) {

                throw new IllegalArgumentException(
                        "Promotion identifier already exists: "
                                + promotion.getIdentifier()
                );
            }
        }

        promotions.add(promotion);
        promotionRepository.saveAll(promotions);
    }
}