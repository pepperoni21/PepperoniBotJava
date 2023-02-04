package fr.pepperoni21.pepperonibot.core.order.command;

import dev.morphia.query.experimental.filters.Filters;
import fr.pepperoni21.pepperonibot.core.db.DBReferences;
import fr.pepperoni21.pepperonibot.core.order.OrderManager;
import fr.pepperoni21.pepperonibot.core.order.OrderState;
import fr.pepperoni21.pepperonibot.core.order.OrderType;
import fr.pepperoni21.pepperonibot.core.order.model.Order;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OrderCommandExecutor extends ListenerAdapter {

    private final OrderManager orderManager;

    public OrderCommandExecutor(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(!event.getName().equals("order")) return;
        String subCommand = event.getSubcommandName();
        if(subCommand == null) return;
        if(subCommand.equals("create")) {
            this.processCreateOrder(event);
            return;
        }if(subCommand.equals("cancel")){
            this.processRemoveOrder(event);
        }
    }

    private void processCreateOrder(SlashCommandInteractionEvent event){
        User user = Objects.requireNonNull(event.getOption("user")).getAsUser();
        OrderType type = OrderType.valueOf(Objects.requireNonNull(event.getOption("type")).getAsString().toUpperCase());
        int price = Objects.requireNonNull(event.getOption("price")).getAsInt();
        String description = Objects.requireNonNull(event.getOption("description")).getAsString();
        this.orderManager.createOrder(user, type, price, description);
        event.reply("Order created!").setEphemeral(true).queue();
    }

    private void processRemoveOrder(SlashCommandInteractionEvent event){
        int id = Objects.requireNonNull(event.getOption("id")).getAsInt();
        Order order = DBReferences.datastore.find(Order.class).filter(Filters.eq("orderId", id)).first();
        if(this.orderManager.cancelOrder(order)) {
            event.reply("Order removed!").setEphemeral(true).queue();
        }else {
            event.reply("Order not found!").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(!event.getComponentId().startsWith("order:")) return;
        String[] split = event.getComponentId().split(":")[1].split("=");
        String action = split[0];
        int id = Integer.parseInt(split[1]);
        Order order = this.orderManager.fetchOrder(id);
        switch (action) {
            case "first-payment" -> {
                if (order.getState() != OrderState.FIRST_PAYMENT) return;
                this.orderManager.validateFirstPayment(order);
                event.reply("First payment validated!").setEphemeral(true).queue();
            }
            case "done" -> {
                if (order.getState() != OrderState.IN_PROGRESS) return;
                this.orderManager.setDone(order);
                event.reply("Order set as done!").setEphemeral(true).queue();
            }
            case "second-payment" -> {
                if (order.getState() != OrderState.SECOND_PAYMENT) return;
                this.orderManager.validateSecondPayment(order);
                event.reply("Second payment validated!").setEphemeral(true).queue();
            }
            case "delivery" -> {
                if (order.getState() != OrderState.DELIVERY) return;
                this.orderManager.setDelivered(order);
                event.reply("Order set as delivered!").setEphemeral(true).queue();
            }
            case "cancel" -> {
                if (this.orderManager.cancelOrder(order)) {
                    event.reply("Order canceled!").setEphemeral(true).queue();
                } else {
                    event.reply("Error while canceling the order!").setEphemeral(true).queue();
                }
            }
        }
    }
}
