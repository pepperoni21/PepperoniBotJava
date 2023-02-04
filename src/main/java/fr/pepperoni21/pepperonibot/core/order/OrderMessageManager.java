package fr.pepperoni21.pepperonibot.core.order;

import fr.pepperoni21.pepperonibot.core.PepperoniBot;
import fr.pepperoni21.pepperonibot.core.order.model.Order;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static fr.pepperoni21.pepperonibot.References.DOTENV;

public class OrderMessageManager {

    public EmbedBuilder orderChannelMessage(Order order){
        return new EmbedBuilder()
                .setTitle("Order #" + order.getOrderId())
                .addField("Type", order.getType().name(), true)
                .addField("Price", order.getPrice() + " USD", true)
                .setDescription(order.getState().getMessage()
                        .replace("%price%", String.valueOf(order.getPrice() / 2)));
    }

    public void updateChannelMessage(Order order, TextChannel channel){
        channel.retrieveMessageById(order.getAssets().getOrderChannelMessageId())
                .queue(msg -> msg.editMessageEmbeds(this.orderChannelMessage(order).build()).queue());
    }

    public void updateOrderListMessage(Order order){
        JDA jda = PepperoniBot.getInstance().getJDA();
        TextChannel ordersChannel = jda.getTextChannelById(Long.parseLong(DOTENV.get("ORDERS_CHANNEL_ID")));
        assert ordersChannel != null;
        ordersChannel.retrieveMessageById(order.getAssets().getOrderListMessageId())
                .queue(msg -> {
                    jda.retrieveUserById(order.getCustomerId()).queue(user ->
                            msg.editMessageEmbeds(this.orderListMessage(order, user, ordersChannel))
                            .setActionRow(this.generateActionRows(order)).queue());
                });
    }

    public void sendFirstPaymentMessage(TextChannel channel){
        channel.sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("First payment validated!")
                        .setColor(Color.GREEN).build())
                .queue();
    }

    public void sendDoneMessage(TextChannel channel){
        channel.sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("Order done!")
                        .setDescription("Please send the rest of the money with the link in the pinned message.")
                        .setColor(Color.GREEN).build())
                .queue();
    }

    public void sendSecondPaymentMessage(TextChannel channel) {
        channel.sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("Second payment validated!")
                        .setColor(Color.GREEN).build())
                .queue();
    }

    public MessageEmbed orderListMessage(Order order, User user, TextChannel channel){
        return new EmbedBuilder()
                .setTitle("Order #" + order.getOrderId())
                .addField("Customer", user.getAsMention(), true)
                .addField("Type", order.getType().getName(), false)
                .addField("Price", order.getPrice() + "$", false)
                .addField("Channel", channel.getAsMention(), false)
                .addField("State", order.getState().getName(), false)
                .build();
    }

    public List<ItemComponent> generateActionRows(Order order){
        List<ItemComponent> actionRows = new ArrayList<>();
        OrderState state = order.getState();
        actionRows.add(Button.primary(state.getAction() + "=" + order.getOrderId(), state.getActionRowLabel()));
        actionRows.add(Button.danger("order:cancel=" + order.getOrderId(), "Cancel"));
        return actionRows;
    }

    public void addToArchive(Order order){
        JDA jda = PepperoniBot.getInstance().getJDA();
        TextChannel archiveChannel = jda.getTextChannelById(Long.parseLong(DOTENV.get("ORDERS_ARCHIVE_CHANNEL_ID")));
        assert archiveChannel != null;
        jda.retrieveUserById(order.getCustomerId()).queue(user -> {
            archiveChannel.sendMessageEmbeds(
                    new EmbedBuilder().setTitle("Order #" + order.getOrderId())
                            .addField("Customer", user.getAsMention(), true)
                            .addField("Type", order.getType().getName(), false)
                            .addField("Price", order.getPrice() + "$", false)
                            .addField("State", order.getState().getName(), false)
                            .build()
            ).queue();
        });
    }

}
