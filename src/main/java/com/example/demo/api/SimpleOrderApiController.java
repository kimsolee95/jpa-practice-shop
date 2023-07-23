package com.example.demo.api;

import com.example.demo.domain.Address;
import com.example.demo.domain.Order;
import com.example.demo.domain.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderSearch;
import com.example.demo.repository.OrderSimpleQueryDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SimpleOrderApiController {

  private final OrderRepository orderRepository;

  /**
   * anti pattern
   * 엔티티를 바로 반환하는 안티 패턴 예시
   * 필요한 response만 줄 수 있도록 API 스펙을 정리해야 한다.
   * */
  @GetMapping("/api/v1/simple-orders")
  public List<Order> ordersV1() {
    List<Order> all = orderRepository.findAll(new OrderSearch());
    for (Order order : all) {
      order.getMember().getName(); // Lazy 강제 초기화
      order.getDelivery().getAddress(); // Lazy 강제 초기화
    }
    return all;
  }

  /**
   * response로 dto를 반환하는 API 예시
   * Lazy loading 문제 발생 -> table 3개 join
   * N + 1 문제 발생
   * */
  @GetMapping("/api/v2/simple-orders")
  public List<SimpleOrderDto> orderV2() {
    List<Order> orders = orderRepository.findAll(new OrderSearch());
    List<SimpleOrderDto> list = orders.stream()
        .map(o -> new SimpleOrderDto(o))
        .collect(Collectors.toList());
    return list;
  }

  /**
   * fetch join 사용 예시
   * N + 1 문제 fix
   * */
  @GetMapping("/api/v3/simple-orders")
  public List<SimpleOrderDto> orderV3() {
    List<Order> orders = orderRepository.findAllWithMemberDelivery();
    List<SimpleOrderDto> result = orders.stream()
        .map(o -> new SimpleOrderDto(o))
        .collect(Collectors.toList());
    return result;
  }

  @GetMapping("/api/v4/simple-orders")
  public List<OrderSimpleQueryDto> orderV4() {
    return orderRepository.findOrderDtos();
  }



  @Data
  static class SimpleOrderDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public SimpleOrderDto(Order order) {
      orderId = order.getId();
      name = order.getMember().getName(); // Lazy 초기화
      orderDate = order.getOrderDate();
      orderStatus = order.getStatus();
      address = order.getDelivery().getAddress(); // Lazy 초기화
    }
  }
}
