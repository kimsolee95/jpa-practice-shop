package com.example.demo.domain;

import com.example.demo.domain.item.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

  @Id @GeneratedValue
  @Column(name = "order_item_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Item item;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  private int orderPrice; //주문 가격
  private int count; //주문 수량

  //@NoArgsConstructor(access = AccessLevel.PROTECTED) 로 대체
//  protected OrderItem() {
//  }

  /**
   * 주문 상품 생성
   * */
  public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
    OrderItem orderItem = new OrderItem();
    orderItem.setItem(item);
    orderItem.setOrderPrice(orderPrice);
    orderItem.setCount(count);

    item.removeStock(count);
    return orderItem;
  }

  /**
   * 주문 취소 시, 상품 취소
   * */
  public void cancel() {
    getItem().addStock(count);
  }

  /**
   * 주문 총 가격 조회 시, 가격조회
   * */
  public int getTotalPrice() {
    return getOrderPrice() * getCount();
  }
}
