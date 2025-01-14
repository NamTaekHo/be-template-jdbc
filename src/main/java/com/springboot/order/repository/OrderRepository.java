package com.springboot.order.repository;

import com.springboot.order.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
