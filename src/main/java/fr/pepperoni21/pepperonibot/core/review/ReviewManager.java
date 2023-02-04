package fr.pepperoni21.pepperonibot.core.review;

import dev.morphia.query.experimental.filters.Filters;
import fr.pepperoni21.pepperonibot.core.PepperoniBot;
import fr.pepperoni21.pepperonibot.core.db.DBReferences;
import fr.pepperoni21.pepperonibot.core.order.model.Order;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import static fr.pepperoni21.pepperonibot.References.DOTENV;

public class ReviewManager {

    public ReviewManager(){
        JDA jda = PepperoniBot.getInstance().getJDA();
        jda.addEventListener(new ReviewListener(this));
        this.generateMessage(jda);
    }

    private void generateMessage(JDA jda){
        long makeReviewChannelId = Long.parseLong(DOTENV.get("MAKE_REVIEW_CHANNEL_ID"));
        TextChannel makeReviewChannel = jda.getTextChannelById(makeReviewChannelId);
        assert makeReviewChannel != null;
        MessageHistory history = makeReviewChannel.getHistoryFromBeginning(1).complete();
        if(history.isEmpty()){
            makeReviewChannel.sendMessageEmbeds(new EmbedBuilder()
                    .setTitle("Make a review")
                    .setDescription("**To make a review, you need to have made an order. Then, you can click on the button below to review your order.**\n\nNo review will be deleted, no matter what you write. However, I let myself the right to reply.")
                    .build()).addActionRow(Button.success("review", "Make a review")).queue();
        }
    }

    public boolean addReview(User user, long orderId, ReviewRating rating, String comment){
        Order order = DBReferences.datastore.find(Order.class).filter(Filters.eq("orderId", orderId)).first();
        if(order == null) return false;
        TextChannel reviewsChannel = PepperoniBot.getInstance().getJDA().getTextChannelById(Long.parseLong(DOTENV.get("REVIEWS_CHANNEL_ID")));
        assert reviewsChannel != null;
        reviewsChannel.sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Review #" + orderId)
                        .addField("Customer", user.getAsMention(), false)
                        .addField("Rating", rating.getEmoji(), false)
                        .addField("Comment", comment, true)
                        .setAuthor(user.getName(), null, user.getAvatarUrl())
                .build()).queue(msg -> {
                    Review review = new Review(rating, comment, msg.getIdLong());
                    order.setReview(review);
                    DBReferences.datastore.save(order);
        });
        return true;
    }

}
