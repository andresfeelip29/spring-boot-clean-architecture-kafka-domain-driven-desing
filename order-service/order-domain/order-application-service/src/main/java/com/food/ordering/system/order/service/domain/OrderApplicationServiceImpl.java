package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderCreateCommadHandler orderCreateCommadHandler;

    private final OrderTrackCommadHandler orderTrackCommadHandler;

    public OrderApplicationServiceImpl(OrderCreateCommadHandler orderCreateCommadHandler,
                                       OrderTrackCommadHandler orderTrackCommadHandler) {
        this.orderCreateCommadHandler = orderCreateCommadHandler;
        this.orderTrackCommadHandler = orderTrackCommadHandler;
    }

    @Override
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        return this.orderCreateCommadHandler.createOrder(createOrderCommand);
    }

    @Override
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        return this.orderTrackCommadHandler.trackOrder(trackOrderQuery);
    }
}
