package com.example.demo.service.query;

import com.example.demo.domain.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.query.OrderDtoForQuery.OrderDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

  private final OrderRepository orderRepository;

  public List<OrderDto> orderV3() {
    List<Order> orders = orderRepository.findAllWithItem();
    List<OrderDto> result = orders.stream()
        .map(o -> new OrderDtoForQuery.OrderDto(o))
        .collect(Collectors.toList());
    return result;
  }


}
