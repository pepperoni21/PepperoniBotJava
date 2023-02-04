package fr.pepperoni21.pepperonibot.core.review;

import dev.morphia.query.experimental.filters.Filters;
import fr.pepperoni21.pepperonibot.core.db.DBReferences;
import fr.pepperoni21.pepperonibot.core.order.OrderState;
import fr.pepperoni21.pepperonibot.core.order.model.Order;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ReviewListener extends ListenerAdapter {

    private final ReviewManager reviewManager;

    public ReviewListener(ReviewManager reviewManager) {
        this.reviewManager = reviewManager;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(!event.getComponentId().equals("review")) return;
        User user = event.getUser();
        List<Order> orders = DBReferences.datastore.find("orders")
                .filter(Filters.eq("customerId", user.getIdLong()))
                .filter(Filters.eq("state", OrderState.DELIVERED))
                .filter(Filters.exists("review").not()).stream().map(o -> (Order) o).toList();
        if(orders.isEmpty()) {
            event.reply("You don't have any order to review.").setEphemeral(true).queue();
            return;
        }
        SelectMenu.Builder selectMenuBuilder = SelectMenu.create("review-select-order").setPlaceholder("Select an order to review");
        orders.forEach(o -> selectMenuBuilder
                .addOptions(SelectOption.of("#" + o.getOrderId(), String.valueOf(o.getOrderId()))
                        .withDescription(o.getDescription())));
        event.reply("Select an order to review")
                .addActionRow(selectMenuBuilder.build())
                .setEphemeral(true)
                .queue();
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        if(event.getComponentId().equals("review-select-order")){
            long orderId = Long.parseLong(event.getSelectedOptions().get(0).getValue());
            SelectMenu.Builder selectMenuBuilder = SelectMenu.create("review-select-rating:" + orderId).setPlaceholder("Select a rating");
            for (ReviewRating rating : ReviewRating.values()) {
                selectMenuBuilder.addOptions(SelectOption.of(rating.getEmoji(), rating.name()));
            }
            event.reply("Select a rating")
                    .addActionRow(selectMenuBuilder.build())
                    .setEphemeral(true)
                    .queue();
        }else if(event.getComponentId().startsWith("review-select-rating:")){
            long orderId = Long.parseLong(event.getComponentId().split(":")[1]);
            ReviewRating rating = ReviewRating.valueOf(event.getSelectedOptions().get(0).getValue());
            TextInput review = TextInput.create("review-comment", "Write your comment here", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Write your comment here")
                    .build();
            Modal modal = Modal.create("review-text-input:" + orderId + ":" + rating.name(), rating.getEmoji() + " #" + orderId)
                    .addActionRow(review)
                    .build();
            event.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(event.getModalId().startsWith("review-text-input:")){
            String[] split = event.getModalId().split(":");
            long orderId = Long.parseLong(split[1]);
            ReviewRating rating = ReviewRating.valueOf(split[2]);
            String comment = Objects.requireNonNull(event.getValue("review-comment")).getAsString();
            if(reviewManager.addReview(event.getUser(), orderId, rating, comment)){
                event.reply("Your review has been added!").setEphemeral(true).queue();
            }else {
                event.reply("An error occurred while adding your review.").setEphemeral(true).queue();
            }
        }
    }
}
