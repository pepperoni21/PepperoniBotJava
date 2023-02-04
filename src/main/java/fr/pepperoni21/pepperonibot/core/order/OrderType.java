package fr.pepperoni21.pepperonibot.core.order;

public enum OrderType {

    PLUGIN("Plugin"), MOD("Mod"), DISCORD("Discord app"), OTHER("Other");

    private final String name;

    OrderType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
