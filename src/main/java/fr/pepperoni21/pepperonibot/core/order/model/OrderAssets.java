package fr.pepperoni21.pepperonibot.core.order.model;

import dev.morphia.annotations.Entity;

@Entity
public class OrderAssets {

    private long orderListMessageId;

    private long orderChannelId;
    private long orderChannelMessageId;

    public OrderAssets() {
        this.orderListMessageId = -1;
        this.orderChannelId = -1;
        this.orderChannelMessageId = -1;
    }

    public long getOrderListMessageId() {
        return orderListMessageId;
    }

    public void setOrderListMessageId(long orderListMessageId) {
        this.orderListMessageId = orderListMessageId;
    }

    public long getOrderChannelId() {
        return orderChannelId;
    }

    public void setOrderChannelId(long orderChannelId) {
        this.orderChannelId = orderChannelId;
    }

    public long getOrderChannelMessageId() {
        return orderChannelMessageId;
    }

    public void setOrderChannelMessageId(long orderChannelMessageId) {
        this.orderChannelMessageId = orderChannelMessageId;
    }
}
