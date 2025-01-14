package com.springboot.order.mapper;

import com.springboot.coffee.entity.Coffee;
import com.springboot.coffee.service.CoffeeService;
import com.springboot.order.dto.OrderCoffeeResponseDto;
import com.springboot.order.entity.Order;
import com.springboot.order.dto.OrderPostDto;
import com.springboot.order.dto.OrderResponseDto;
import com.springboot.order.entity.OrderCoffee;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
//    Order orderPostDtoToOrder(OrderPostDto orderPostDto);

    default Order orderPostDtoToOrder(OrderPostDto orderPostDto) {
        // 반환할 새로운 order 객체 생성
        Order order = new Order();
        // DTO에 있는 memberId -> order에 셋팅
        order.setMemberId(orderPostDto.getMemberId());
        // DTO에 있는 List<orderCoffeeDto>를 Set<OrderCoffee>로 변경
        // 한 개가 아니므로, 스트림을 돌면서 하나씩 DTO -> OrderCoffee로 변경 후 패키징
        Set<OrderCoffee> orderCoffees = orderPostDto.getOrderCoffees().stream()
                .map(orderCoffeeDto ->
                        OrderCoffee.builder()
                                .coffeeId(orderCoffeeDto.getCoffeeId())
                                .quantity(orderCoffeeDto.getQuantity())
                                .build())
                .collect(Collectors.toSet());
        // 변경한 Set<OrderCoffee>를 order에 셋팅
        order.setOrderCoffees(orderCoffees);
        // order의 주문 상태 셋팅
        order.setOrderStatus(Order.OrderStatus.ORDER_REQUEST);
        // order의 주문 일자 셋팅
        order.setCreatedAt(LocalDateTime.now());
        // 완료된 order 객체 반환
        return order;
    }
    default OrderResponseDto orderToOrderResponseDto(Order order, CoffeeService coffeeService) {
        OrderResponseDto responseDto = new OrderResponseDto(
                order.getOrderId(),
                order.getMemberId(),
                order.getOrderStatus(),
                order.getOrderCoffees().stream()
                        .map(orderCoffee -> orderCoffeeToOrderCoffeeResponseDto(orderCoffee, coffeeService))
                        .collect(Collectors.toList()),
                order.getCreatedAt()
        );
        return responseDto;
    }
    // Order 엔티티에 속해있는 OrderCoffee 객체를 OrderCoffeeResponseDto의 형태로 바꾸기 위한 메서드
    default OrderCoffeeResponseDto orderCoffeeToOrderCoffeeResponseDto(OrderCoffee orderCoffee,
                                                                       CoffeeService coffeeService) {
        // 커피의 정보가 필요하기 때문에, orderCoffee에 있는 커피 id를 통해
        // coffeeService를 주입받아 findCoffee 메서드로 해당 id의 커피 정보를 조회하여 사용합니다.
        Coffee findCoffee = coffeeService.findCoffee(orderCoffee.getCoffeeId());
        return new OrderCoffeeResponseDto(
                findCoffee.getCoffeeId(),
                findCoffee.getKorName(),
                findCoffee.getEngName(),
                findCoffee.getPrice(),
                orderCoffee.getQuantity()
        );
    }
}
