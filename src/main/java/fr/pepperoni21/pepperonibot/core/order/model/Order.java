package fr.pepperoni21.pepperonibot.core.order.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import fr.pepperoni21.pepperonibot.core.order.OrderState;
import fr.pepperoni21.pepperonibot.core.order.OrderType;
import fr.pepperoni21.pepperonibot.core.review.Review;
import org.bson.types.ObjectId;

import java.util.UUID;

@Entity("orders")
public class Order {

    @Id
    private ObjectId id;

    private int orderId;

    private OrderType type;
    private OrderState state;
    private int price;
    private long customerId;

    private String description;

    private OrderAssets assets;

    private Review review;

    public Order(OrderType type, int price, long customerId, String description) {
        this.orderId = Math.abs(UUID.randomUUID().hashCode());
        this.type = type;
        this.state = OrderState.FIRST_PAYMENT;
        this.price = price;
        this.customerId = customerId;
        this.description = description;
        this.assets = new OrderAssets();
    }

    public Order() {
    }

    public int getOrderId() {
        return orderId;
    }

    public OrderType getType() {
        return type;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public int getPrice() {
        return price;
    }

    public long getCustomerId() {
        return customerId;
    }

    public String getDescription() {
        return description;
    }

    public OrderAssets getAssets() {
        return assets;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
