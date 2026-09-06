package gamezone.model;
/**
 * Represents a video game product in the GameZone catalog.
 * Extends the common attributes and behavior from Product.
 */

public class VideoGame extends Product {

    private String platform;
    private String genre;
    private String ageRating;

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

    @Override
    public String getDescription() {
        return "Video Game: " + getTitle()
                + " Platform: " + platform
                + " Genre: " + genre
                + " Rating: " + ageRating;
    }

}
