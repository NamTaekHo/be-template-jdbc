package com.springboot.order.controller;

import com.springboot.order.entity.Order;
import com.springboot.order.dto.OrderPostDto;
import com.springboot.order.dto.OrderResponseDto;
import com.springboot.order.service.OrderService;
import com.springboot.order.mapper.OrderMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v5/orders")
@Validated
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper mapper;

    public OrderController(OrderService orderService, OrderMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postOrder(@Valid @RequestBody OrderPostDto orderPostDto) {
        Order order = orderService.createOrder(mapper.orderPostDtoToOrder(orderPostDto));

        URI location = UriComponentsBuilder
                .newInstance()
                .path("/v5/orders" + "/{order-id}")
                .buildAndExpand(order.getOrderId())
                .toUri(); // -> /v5/orders/{order-id}

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{order-id}")
    public ResponseEntity getOrder(@PathVariable("order-id") @Positive long orderId) {
        Order order = orderService.findOrder(orderId);

        return new ResponseEntity<>(mapper.orderToOrderResponseDto(order), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getOrders() {
        List<Order> orders = orderService.findOrders();

        List<OrderResponseDto> response =
                orders.stream()
                        .map(order -> mapper.orderToOrderResponseDto(order))
                        .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{order-id}")
    public ResponseEntity cancelOrder(@PathVariable("order-id") long orderId) {
        System.out.println("# cancel order");
        orderService.cancelOrder(orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
