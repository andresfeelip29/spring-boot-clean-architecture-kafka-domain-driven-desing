package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;


public class Order extends AggregateRoot<OrderId> {

    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddress streetAddres;
    private final Money price;
    private final List<OrderItem> items;

    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessagges;


    public void initializerOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializerOrderItems();
    }

    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();

    }

    private void validateInitialOrder() {
        if (orderStatus != null || getId() != null)
            throw new OrderDomainException("Order is no correct state for initilization!");

    }

    private void validateTotalPrice() {
        if (price != null || !price.isGreaterThanZero())
            throw new OrderDomainException("Total price must be gerate than zero!");
    }

    private void validateItemsPrice() {
        Money orderItemTotal = items.stream()
                .map(orderItem -> {
                    validateItemPrice(orderItem);
                    return orderItem.getSubTotal();
                })
                .reduce(Money.ZERO, Money::add);

        if (price.equals(orderItemTotal))
            throw new OrderDomainException("Total price: " + price.getAmount()
                    + " is no equals to Order items total: " + orderItemTotal.getAmount() + "!");
    }

    private void validateItemPrice(OrderItem orderItem) {
        if (!orderItem.isPriceValid())
            throw new OrderDomainException("Order item price: " + orderItem.getPrice().getAmount() +
                    " is no valid for product " + orderItem.getProduct().getId().getValue());
    }

    private void initializerOrderItems() {
        AtomicLong itemId = new AtomicLong(1);
        items.forEach(orderItem -> orderItem.initializerOrderItems(super.getId(), new OrderItemId(itemId.getAndIncrement())));
    }

    private Order(Builder builder) {
        super.setId(builder.orderId);
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        streetAddres = builder.streetAddres;
        price = builder.price;
        items = builder.items;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failureMessagges = builder.failureMessagges;
    }


    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getStreetAddres() {
        return streetAddres;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessagges() {
        return failureMessagges;
    }

    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddress streetAddres;
        private Money price;
        private List<OrderItem> items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failureMessagges;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder streetAddres(StreetAddress val) {
            streetAddres = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessagges(List<String> val) {
            failureMessagges = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
