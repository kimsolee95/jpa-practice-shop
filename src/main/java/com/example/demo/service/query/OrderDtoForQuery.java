package com.example.demo.service.query;

import static java.util.stream.Collectors.toList;

import com.example.demo.domain.Address;
import com.example.demo.domain.Order;
import com.example.demo.domain.OrderItem;
import com.example.demo.domain.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

public class OrderDtoForQuery {
  @Getter
  static class OrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems; // 완전히 엔티티와 dto 간 의존을 끊는 것.

    public OrderDto(Order order) {
      orderId = order.getId();
      name = order.getMember().getName();
      orderDate = order.getOrderDate();
      orderStatus = order.getStatus();
      address = order.getDelivery().getAddress();
      orderItems = order.getOrderItems().stream()
          .map(orderItem -> new OrderItemDto(orderItem))
          .collect(toList());
    }
  }

  @Getter
  static class OrderItemDto {

    private String itemName; //상품명
    private int orderPrice; //주문 가격
    private int count; //주문 수량

    public OrderItemDto (OrderItem orderItem) {
      itemName = orderItem.getItem().getName();
      orderPrice = orderItem.getOrderPrice();
      count = orderItem.getCount();
    }
  }
}
