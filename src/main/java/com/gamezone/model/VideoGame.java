package com.gamezone.model;
/**
 * Represents a video game product in the GameZone catalog.
 * Extends the common attributes and behavior from Product.
 */

public class VideoGame extends Product {

    /**
     * Platform where the video game can be played.
     */

    private String platform;
    /**
     * Video game category or type.
     */
    private String genre;
    /**
     * Age rating classification of the video game.
     */
    private String ageRating;
    /**
     * Creates a new video game with the specified information.
     *
     * @param identifier unique product identifier
     * @param title video game title
     * @param price product price
     * @param availableQuantity available stock quantity
     * @param platform gaming platform
     * @param genre video game genre
     * @param ageRating age classification rating
     */

    public VideoGame(
            String identifier,
            String title,
            double price,
            int availableQuantity,
            String platform,
            String genre,
            String ageRating) {

        super(identifier, title, price, availableQuantity);

        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }

    /**
     * Provides a detailed description of the video game.
     *
     * @return formatted description containing video game information
     */

    @Override
    public String getDescription() {
        return "Video Game: " + getTitle()
                + " Platform: " + platform
                + " Genre: " + genre
                + " Rating: " + ageRating;
    }

}
