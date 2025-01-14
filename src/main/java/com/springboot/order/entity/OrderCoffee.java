package com.springboot.order.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@Table("ORDER_COFFEE")
public class OrderCoffee {
    @Id
    private long orderCoffeeId;
    private long coffeeId;
    private int quantity;
}
