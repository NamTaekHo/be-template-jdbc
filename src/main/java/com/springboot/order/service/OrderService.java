package com.springboot.order.service;

import com.springboot.coffee.service.CoffeeService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.service.MemberService;
import com.springboot.order.entity.Order;
import com.springboot.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final CoffeeService coffeeService;

    public OrderService(OrderRepository orderRepository, MemberService memberService, CoffeeService coffeeService) {
        this.orderRepository = orderRepository;
        this.memberService = memberService;
        this.coffeeService = coffeeService;
    }

    public Order createOrder(Order order) {
        // TODO should business logic
        verifyOrder(order);
        // TODO order 객체는 나중에 DB에 저장 후, 되돌려 받는 것으로 변경 필요.
        return orderRepository.save(order);
    }

    public Order findOrder(long orderId) {
        // TODO should business logic

        // TODO order 객체는 나중에 DB에서 조회 하는 것으로 변경 필요.
        return verifyExistsOrderId(orderId);
    }

    // 주문 수정 메서드는 사용하지 않습니다.

    public List<Order> findOrders() {
        return (List<Order>)orderRepository.findAll();
    }

    public void cancelOrder(long orderId) {
        Order foundOrder = verifyExistsOrderId(orderId);
        if(foundOrder.getOrderStatus().getStepNumber() == 1){
            foundOrder.setOrderStatus(Order.OrderStatus.ORDER_CANCLE);
            orderRepository.save(foundOrder);
        } else {
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_ORDER);
        }
    }

    private void verifyOrder(Order order){
        memberService.verifyExistsMemberId(order.getMemberId());
        order.getOrderCoffees()
                .forEach(coffee -> coffeeService.findCoffee(coffee.getCoffeeId()));
        if(order.getOrderStatus().getStepNumber() != 1){
            throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
        }
    }

    private Order verifyExistsOrderId(long orderId){
        return orderRepository.findById(orderId).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND)
        );
    }
}
