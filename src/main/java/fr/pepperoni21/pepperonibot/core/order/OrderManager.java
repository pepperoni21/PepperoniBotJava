package fr.pepperoni21.pepperonibot.core.order;

import dev.morphia.query.experimental.filters.Filters;
import fr.pepperoni21.pepperonibot.core.PepperoniBot;
import fr.pepperoni21.pepperonibot.core.db.DBReferences;
import fr.pepperoni21.pepperonibot.core.order.command.OrderCommand;
import fr.pepperoni21.pepperonibot.core.order.command.OrderCommandExecutor;
import fr.pepperoni21.pepperonibot.core.order.model.Order;
import fr.pepperoni21.pepperonibot.core.order.model.OrderAssets;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.Objects;

import static fr.pepperoni21.pepperonibot.References.DOTENV;

public class OrderManager {

    private final PepperoniBot pepperoniBot = PepperoniBot.getInstance();

    private final OrderMessageManager messageManager;

    public OrderManager(){
        DBReferences.datastore.getMapper().map(Order.class, OrderAssets.class);
        this.registerCommand();

        this.messageManager = new OrderMessageManager();
    }

    public void createOrder(User user, OrderType type, int price, String description){
        Order order = new Order(type, price, user.getIdLong(), description);
        long ordersCategoryId = Long.parseLong(DOTENV.get("ORDERS_CATEGORY_ID"));
        JDA jda = this.pepperoniBot.getJDA();
        Category category = jda.getCategoryById(ordersCategoryId);
        assert category != null;
        Guild guild = category.getGuild();
        guild.createTextChannel("order-" + order.getOrderId())
                .setParent(category)
                .addMemberPermissionOverride(user.getIdLong(), List.of(Permission.MESSAGE_SEND), null)
                .queue(textChannel -> {
                    textChannel.sendMessageEmbeds(this.messageManager.orderChannelMessage(order).build())
                            .queue(msg -> {
                                order.getAssets().setOrderChannelMessageId(msg.getIdLong());
                                msg.pin().queue();
                            });
                    TextChannel ordersChannel = jda.getTextChannelById(Long.parseLong(DOTENV.get("ORDERS_CHANNEL_ID")));
                    assert ordersChannel != null;
                    ordersChannel.sendMessageEmbeds(this.messageManager.orderListMessage(order, user, textChannel))
                            .addActionRow(this.messageManager.generateActionRows(order))
                            .queue(msg -> {
                                order.getAssets().setOrderChannelId(textChannel.getIdLong());
                                order.getAssets().setOrderListMessageId(msg.getIdLong());
                                DBReferences.datastore.save(order);
                            });
                });
    }

    public boolean cancelOrder(Order order){
        if(order == null) return false;
        this.endOrder(order);
        order.setState(OrderState.CANCELED);
        this.messageManager.addToArchive(order);
        DBReferences.datastore.save(order);
        return true;
    }

    private void endOrder(Order order){
        long orderChannelId = order.getAssets().getOrderChannelId();
        long orderListMessageId = order.getAssets().getOrderListMessageId();
        JDA jda = this.pepperoniBot.getJDA();
        if(orderChannelId != -1){
            Objects.requireNonNull(jda.getTextChannelById(orderChannelId)).delete().queue();
        }if(orderListMessageId != -1){
            TextChannel textChannel = this.getOrderListChannel();
            textChannel.retrieveMessageById(orderListMessageId).queue(message -> message.delete().queue());
        }
    }

    public void validateFirstPayment(Order order){
        if(order.getState() != OrderState.FIRST_PAYMENT) return;
        order.setState(OrderState.IN_PROGRESS);
        TextChannel channel = PepperoniBot.getInstance().getJDA().getTextChannelById(order.getAssets().getOrderChannelId());
        assert channel != null;
        this.messageManager.updateChannelMessage(order, channel);
        this.messageManager.sendFirstPaymentMessage(channel);
        this.messageManager.updateOrderListMessage(order);
        DBReferences.datastore.save(order);
    }

    public void setDone(Order order){
        if(order.getState() != OrderState.IN_PROGRESS) return;
        order.setState(OrderState.SECOND_PAYMENT);
        TextChannel channel = PepperoniBot.getInstance().getJDA().getTextChannelById(order.getAssets().getOrderChannelId());
        assert channel != null;
        this.messageManager.updateChannelMessage(order, channel);
        this.messageManager.sendDoneMessage(channel);
        this.messageManager.updateOrderListMessage(order);
        DBReferences.datastore.save(order);
    }

    public void validateSecondPayment(Order order){
        if(order.getState() != OrderState.SECOND_PAYMENT) return;
        order.setState(OrderState.DELIVERY);
        TextChannel channel = PepperoniBot.getInstance().getJDA().getTextChannelById(order.getAssets().getOrderChannelId());
        assert channel != null;
        this.messageManager.updateChannelMessage(order, channel);
        this.messageManager.sendSecondPaymentMessage(channel);
        this.messageManager.updateOrderListMessage(order);
        DBReferences.datastore.save(order);
    }

    public void setDelivered(Order order){
        if(order.getState() != OrderState.DELIVERY) return;
        this.endOrder(order);
        order.setState(OrderState.DELIVERED);
        this.messageManager.addToArchive(order);
        DBReferences.datastore.save(order);
    }

    public Order fetchOrder(int orderId){
        return DBReferences.datastore.find(Order.class).filter(Filters.eq("orderId", orderId)).first();
    }

    private void registerCommand(){
        JDA jda = this.pepperoniBot.getJDA();
        OrderCommand orderCommand = new OrderCommand();
        jda.upsertCommand(orderCommand.getCommandData()).queue();
        jda.addEventListener(new OrderCommandExecutor(this));
    }

    private TextChannel getOrderListChannel(){
        JDA jda = this.pepperoniBot.getJDA();
        long orderListChannelId = Long.parseLong(DOTENV.get("ORDERS_CHANNEL_ID"));
        return jda.getTextChannelById(orderListChannelId);
    }

    public OrderMessageManager getMessageManager() {
        return messageManager;
    }
}
