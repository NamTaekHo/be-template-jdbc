package com.springboot.coffee.service;

import com.springboot.coffee.entity.Coffee;
import com.springboot.coffee.repository.CoffeeRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;

import com.springboot.order.entity.Order;
import com.springboot.order.entity.OrderCoffee;
import com.springboot.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CoffeeService {
    private final CoffeeRepository coffeeRepository;

    private final OrderRepository orderRepository;

    public CoffeeService(CoffeeRepository coffeeRepository, OrderRepository orderRepository) {
        this.coffeeRepository = coffeeRepository;
        this.orderRepository = orderRepository;
    }

    public Coffee createCoffee(Coffee coffee) {
        verifyExistsCoffeeCode(coffee.getCoffeeCode().toUpperCase());
        coffee.setCoffeeCode(coffee.getCoffeeCode().toUpperCase());
        return coffeeRepository.save(coffee);
    }

    public Coffee updateCoffee(Coffee coffee) {
        Coffee foundCoffee = findVerifiedCoffee(coffee.getCoffeeId());

        Optional.ofNullable(coffee.getKorName())
                .ifPresent(korName -> foundCoffee.setKorName(korName));

        Optional.ofNullable(coffee.getEngName())
                .ifPresent(engName -> foundCoffee.setEngName(engName));

        Optional.ofNullable(coffee.getPrice())
                .ifPresent(price -> foundCoffee.setPrice(price));

        return coffeeRepository.save(foundCoffee);
    }

    public Coffee findCoffee(long coffeeId) {
        return findVerifiedCoffee(coffeeId);
    }

    public List<Coffee> findCoffees() {
        return (List<Coffee>)coffeeRepository.findAll();
    }

    public void deleteCoffee(long coffeeId) {
        coffeeRepository.delete(findVerifiedCoffee(coffeeId));
    }

    // 주문에 해당하는 커피 정보 조회
    public List<Coffee> findOrderedCoffees(Order order) {
        Order foundOrder = verifyExistsOrderId(order.getOrderId());

//        List<Coffee> list = new ArrayList<>();
//        Set<OrderCoffee> orderCoffees = order.getOrderCoffees();
//        for(OrderCoffee orderCoffee : orderCoffees){
//            Coffee coffee = findVerifiedCoffee(orderCoffee.getCoffeeId());
//            list.add(coffee);
//        }
//        return list;


        return foundOrder.getOrderCoffees().stream()
                .map(orderCoffee -> findVerifiedCoffee(orderCoffee.getCoffeeId()))
                .collect(Collectors.toList());
    }

    // DB에 주문 있는지 검증 로직
    private Order verifyExistsOrderId(long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
        return order;
    }

    //검증 예외처리 로직 분리
    private void verifyExistsCoffeeCode(String coffeeCode){
        Optional<Coffee> optionalCoffee = coffeeRepository.findByCoffeeCode(coffeeCode);
        if(optionalCoffee.isPresent()){
            throw new BusinessLogicException(ExceptionCode.COFFEE_CODE_EXISTS);
        }
    }

    private Coffee findVerifiedCoffee(long coffeeId){
        Optional<Coffee> optionalCoffee = coffeeRepository.findByCoffee(coffeeId);
        return optionalCoffee.orElseThrow(() -> new BusinessLogicException(ExceptionCode.COFFEE_NOT_FOUND));
    }

    private void verifyCoffee(long coffeeId){
        Optional<Coffee> optionalCoffee = coffeeRepository.findByCoffee(coffeeId);
        optionalCoffee.orElseThrow(() -> new BusinessLogicException(ExceptionCode.COFFEE_NOT_FOUND));
    }
}
