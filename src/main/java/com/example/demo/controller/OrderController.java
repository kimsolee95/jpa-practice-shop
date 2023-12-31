package com.example.demo.controller;

import com.example.demo.repository.OrderSearch;
import com.example.demo.service.ItemService;
import com.example.demo.service.MemberService;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final MemberService memberService;
  private final ItemService itemService;

  @PostMapping("/order")
  public ResponseEntity<?> order(
      @RequestParam("memberId") Long memberId,
      @RequestParam("itemId") Long itemId,
      @RequestParam("count") int count) {
    orderService.order(memberId, itemId, count);
    return new ResponseEntity<>("order success", HttpStatus.CREATED);
  }

  @GetMapping("/orders")
  public ResponseEntity<?> orderList(OrderSearch orderSearch) {
    return new ResponseEntity<>(orderService.findOrders(orderSearch), HttpStatus.OK);
  }

  @PostMapping("/orders/{orderId}/cancel")
  public ResponseEntity<Void> cancelOrder(@PathVariable("orderId") Long orderId) {
    orderService.cancelOrder(orderId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
