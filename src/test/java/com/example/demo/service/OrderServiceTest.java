package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.domain.Address;
import com.example.demo.domain.Member;
import com.example.demo.domain.Order;
import com.example.demo.domain.OrderStatus;
import com.example.demo.domain.item.Book;
import com.example.demo.domain.item.Item;
import com.example.demo.repository.OrderRepository;
import javax.persistence.EntityManager;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

  @Autowired EntityManager em;
  @Autowired OrderService orderService;
  @Autowired OrderRepository orderRepository;

  @Test
  public void order_product() throws Exception {
    //given
    Member member = new Member();
    member.setName("회원1");
    member.setAddress(new Address("서울", "강가", "123-123"));
    em.persist(member);

    Book book = new Book();
    book.setName("JPA");
    book.setPrice(10000);
    book.setStockQuantity(10);
    em.persist(book);

    int orderCount = 2;

    //when
    Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

    //then
    Order getOrder = orderRepository.findOne(orderId);
    Assert.assertEquals("사움 주문 시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
    Assert.assertEquals("주문 상품 종류 개수 확인", 1, getOrder.getOrderItems().size());
  }

  @Test
  public void order_cancel() throws Exception {
    //given
    //when
    //then
  }

  @Test
  public void out_of_stock() throws Exception {
    //given
    //when
    //then
  }

}