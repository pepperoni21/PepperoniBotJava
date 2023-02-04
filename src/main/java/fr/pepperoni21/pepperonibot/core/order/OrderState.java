package fr.pepperoni21.pepperonibot.core.order;

public enum OrderState {

    FIRST_PAYMENT("Waiting first payment", "Please process the first payment of %price% USD to the following address: https://paypal.me/MaxiGiantFR", "order:first-payment", "Set first payment paid"),
    IN_PROGRESS("In progress", "Your order is in progress...", "order:done", "Set as done"),
    SECOND_PAYMENT("Waiting second payment", "Please process the second payment of %price% USD to the following address: https://paypal.me/MaxiGiantFR", "order:second-payment", "Set second payment paid"),
    DELIVERY("Waiting delivery", "Your delivery is coming...", "order:delivery", "Set as delivered"),
    DELIVERED("Delivered", null, null, null),
    CANCELED("Canceled", null, null, null);

    private final String name;
    private final String message;
    private final String action;
    private final String actionRowLabel;

    OrderState(String name, String message, String action, String actionRowLabel) {
        this.name = name;
        this.message = message;
        this.action = action;
        this.actionRowLabel = actionRowLabel;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getAction() {
        return action;
    }

    public String getActionRowLabel() {
        return actionRowLabel;
    }
}
