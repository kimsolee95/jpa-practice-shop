package com.example.demo.domain;

import com.example.demo.domain.item.Item;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class OrderItem {

  @Id @GeneratedValue
  @Column(name = "order_item_id")
  private Long id;

  @ManyToOne
  private Item item;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;

  private int orderPrice; //주문 가격
  private int count; //주문 수량
}
