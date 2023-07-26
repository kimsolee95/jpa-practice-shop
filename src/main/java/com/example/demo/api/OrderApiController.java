package com.example.demo.api;

import com.example.demo.domain.Address;
import com.example.demo.domain.Order;
import com.example.demo.domain.OrderItem;
import com.example.demo.domain.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderSearch;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

  private final OrderRepository orderRepository;

  /**
   * anti pattern
   * */
  @GetMapping("/api/v1/orders")
  public List<Order> ordersV1() {
    List<Order> all = orderRepository.findAll(new OrderSearch());
    for (Order order : all) {
      order.getMember().getName();
      order.getDelivery().getAddress();
      List<OrderItem> orderItems = order.getOrderItems();
//      for (OrderItem orderItem : orderItems) {
//        orderItem.getItem().getName();
//      }
      orderItems.stream().forEach(o -> o.getItem().getName());
    }
    return all;
  }

  @GetMapping("/api/v2/orders")
  public List<OrderDto> ordersV2() {
    List<Order> orders = orderRepository.findAll(new OrderSearch());
    List<OrderDto> collect = orders.stream()
        .map(o -> new OrderDto(o))
        .collect(Collectors.toList());
    return collect;
  }

  static class OrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItem> orderItems;


    public OrderDto(Order o) {
    }
  }

}
