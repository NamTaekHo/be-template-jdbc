package com.springboot.order.dto;

import com.springboot.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponseDto {
    private long orderId;
    private long memberId;
    private Order.OrderStatus orderStatus;
    // 주문 상품에 대한 새로운 DTO, cofffeeId, 상품명, 가격, 수량
    private List<OrderCoffeeResponseDto> orderCoffees;
    private LocalDateTime createdAt;
}
