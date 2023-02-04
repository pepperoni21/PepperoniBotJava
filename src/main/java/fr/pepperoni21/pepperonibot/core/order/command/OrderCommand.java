package fr.pepperoni21.pepperonibot.core.order.command;

import fr.pepperoni21.pepperonibot.core.order.OrderType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class OrderCommand {

    public CommandData getCommandData(){
        CommandDataImpl commandData = new CommandDataImpl("order", "Manager orders");
        commandData.addSubcommands(this.getCreateCommand());
        commandData.addSubcommands(this.getCancelCommand());
        commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS));
        return commandData;
    }

    private SubcommandData getCreateCommand(){
        SubcommandData command = new SubcommandData("create", "Create an order");
        command.addOption(OptionType.USER, "user", "User who ordered", true);
        OptionData data = new OptionData(OptionType.STRING, "type", "Type of order", true);
        for (OrderType type : OrderType.values()) {
            data.addChoice(type.name().toLowerCase(), type.name().toLowerCase());
        }
        command.addOptions(data);
        command.addOption(OptionType.INTEGER, "price", "Price of the order", true);
        command.addOption(OptionType.STRING, "description", "Description of the order", true);
        return command;
    }

    private SubcommandData getCancelCommand(){
        SubcommandData command = new SubcommandData("cancel", "Cancel an order");
        command.addOption(OptionType.INTEGER, "id", "Id of the order", true);
        return command;
    }
}