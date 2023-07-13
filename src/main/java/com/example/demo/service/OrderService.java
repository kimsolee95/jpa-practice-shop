package com.example.demo.service;

import com.example.demo.domain.Delivery;
import com.example.demo.domain.Member;
import com.example.demo.domain.Order;
import com.example.demo.domain.OrderItem;
import com.example.demo.domain.item.Item;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final MemberRepository memberRepository;
  private final ItemRepository itemRepository;

  /**
   * 주문하기
   * */
  @Transactional
  public Long order(Long memberId, Long itemId, int count) {
    //basic info select
    Member member = memberRepository.findOne(memberId);
    Item item = itemRepository.findOne(itemId);
    //delivery info create
    Delivery delivery = new Delivery();
    delivery.setAddress(member.getAddress());
    //order item create
    OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
    //order create
    Order order = Order.createOrder(member, delivery, orderItem);
    orderRepository.save(order);
    return order.getId();
  }

  /**
   * 주문 취소
   * */
  @Transactional
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findOne(orderId);
    order.cancel(); //더티체킹을 통한 data 변경
  }

  //검색
}
