package com.gamezone.model;

/**
 * Represents a video game product available in the GameZone store.
 * It includes specific information such as platform, genre, and age rating.
 */

public class VideoGame extends Product {

    private String platform;
    private String genre;
    private String ageRating;

    /**
     * Creates a video game with its common and specific information.
     *
     * @param id unique identifier of the video game
     * @param title title of the video game
     * @param price price of the video game
     * @param stock available quantity in inventory
     * @param platform platform supported by the video game
     * @param genre genre of the video game
     * @param ageRating recommended age rating
     */

    public VideoGame(String id, String title, double price, int stock,
                     String platform, String genre, String ageRating) {

        super(id, title, price, stock);

        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }


    /**
     * Returns a complete description of the video game.
     *
     * @return the video game description
     */

    @Override
    public String getDescription() {
        return "Video Game: " + getTitle()
                + " | Platform: " + platform
                + " | Genre: " + genre
                + " | Age Rating: " + ageRating
                + " | Price: " + getPrice()
                + " | Stock: " + getStock();
    }
}
