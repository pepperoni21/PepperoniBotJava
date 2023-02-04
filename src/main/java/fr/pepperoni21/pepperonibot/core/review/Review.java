package fr.pepperoni21.pepperonibot.core.review;

import dev.morphia.annotations.Entity;

@Entity
public record Review(ReviewRating rating, String comment, long messageId) {

}
