package fr.pepperoni21.pepperonibot.core.review;

public enum ReviewRating {

    ONE_STAR("1", "⭐"),
    TWO_STARS("2", "⭐⭐"),
    THREE_STARS("3", "⭐⭐⭐"),
    FOUR_STARS("4", "⭐⭐⭐⭐"),
    FIVE_STARS("5", "⭐⭐⭐⭐⭐");

    private final String name;
    private final String emoji;

    ReviewRating(String name, String emoji) {
        this.name = name;
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }

}
