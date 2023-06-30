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
    private final StreetAddress deliveryAddres;
    private final Money price;
    private final List<OrderItem> items;

    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessagges;

    /** CRTICAL BUSINESS LOGIC **/

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

    public void pay() {
        if (orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException("Order is not in correct state for pay operation!");
        }
        orderStatus = OrderStatus.PAID;
    }

    public void  approve(){
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order is not in correct state for approve operation!");
        }
        orderStatus = OrderStatus.APPROVED;
    }

    public void initCancel(List<String> failureMessagges){
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order is not in correct state for initCancel operation!");
        }
        orderStatus = OrderStatus.CANELLING;
        updateFailureMessages(failureMessagges);
    }

    public void cancel(List<String> failureMessagges){
        if (!(orderStatus == OrderStatus.PENDING || orderStatus == OrderStatus.CANELLING)) {
            throw new OrderDomainException("Order is not in correct state for cancel operation!");
        }
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessagges);
    }

    private void updateFailureMessages(List<String> failureMessagges) {
        if(this.failureMessagges != null && failureMessagges != null){
            this.failureMessagges.addAll(failureMessagges.stream().filter(messagge -> !messagge.isEmpty()).toList());
        }

        if(this.failureMessagges == null){
            this.failureMessagges = failureMessagges;
        }
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

    /*************************************************************************************************************/

    private Order(Builder builder) {
        super.setId(builder.orderId);
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        deliveryAddres = builder.deliveryAddres;
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

    public StreetAddress getDeliveryAddres() {
        return deliveryAddres;
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
        private StreetAddress deliveryAddres;
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

        public Builder deliveryAddres(StreetAddress val) {
            deliveryAddres = val;
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
