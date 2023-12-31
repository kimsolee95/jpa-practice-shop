package com.example.demo.api;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.example.demo.domain.Address;
import com.example.demo.domain.Order;
import com.example.demo.domain.OrderItem;
import com.example.demo.domain.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderSearch;
import com.example.demo.repository.order.query.OrderFlatDto;
import com.example.demo.repository.order.query.OrderItemQueryDto;
import com.example.demo.repository.order.query.OrderQueryDto;
import com.example.demo.repository.order.query.OrderQueryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

  private final OrderRepository orderRepository;
  private final OrderQueryRepository orderQueryRepository;

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
    List<OrderDto> result = orders.stream()
        .map(o -> new OrderDto(o))
        .collect(toList());
    return result;
  }

  @GetMapping("/api/v3/orders")
  public List<OrderDto> orderV3() {
    List<Order> orders = orderRepository.findAllWithItem();
    for (Order order : orders) {
      System.out.println("order ref= " + order + " id= " + order.getId());
    }

    List<OrderDto> result = orders.stream()
        .map(o -> new OrderDto(o))
        .collect(toList());
    return result;
  }

  @GetMapping("/api/v3.1/orders")
  public List<OrderDto> orderV3_page(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "100")int limit) {
    List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
    List<OrderDto> result = orders.stream()
        .map(o -> new OrderDto(o))
        .collect(toList());
    return result;
  }

  @GetMapping("/api/v4/orders")
  public List<OrderQueryDto> ordersV4() {
    return orderQueryRepository.findOrderQueryDtos();
  }

  @GetMapping("/api/v5/orders")
  public List<OrderQueryDto> ordersV5() {
    return orderQueryRepository.findAllByDto_optimization();
  }

  @GetMapping("/api/v6/orders")
  public List<OrderQueryDto> ordersV6() {
    //item을 기준으로 중복되어 select 된 data들
    List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
    //중복된 data를 주문번호 기준으로 그룹핑하여 가공시켜서 반환
    return flats.stream()
        .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
            mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
        )).entrySet().stream()
        .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
            e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
            e.getKey().getAddress(), e.getValue()))
        .collect(toList());
  }

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
