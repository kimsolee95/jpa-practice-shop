package com.example.demo.repository;

import com.example.demo.domain.Order;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

  private final EntityManager em;

  public void save(Order order) {
    em.persist(order);
  }

  public Order findOne(Long id) {
    return em.find(Order.class, id);
  }

  public List<Order> findAll(OrderSearch orderSearch) {
    return em.createQuery("select o from Order o join o.member m" +
        " where o.status = :status" +
        " and m.name like :name", Order.class)
        .setParameter("status", orderSearch.getOrderStatus())
        .setParameter("name", orderSearch.getMemberName())
        .setMaxResults(1000)
        .getResultList();
  }

  /**
   * Lazy 를 무시하고 객체에 값을 다 채우는 select
   * fetch join
   * */
  public List<Order> findAllWithMemberDelivery() {
    return em.createQuery(
        "select o from Order o" +
            " join fetch o.member" +
            " join fetch o.delivery d", Order.class
    ).getResultList();
  }

  public List<OrderSimpleQueryDto> findOrderDtos() {
    return em.createQuery(
        "select new com.example.demo.repository.OrderSimpleQueryDto(o.id, m.name, o.orderData, o.status, d.address) " +
            " from Order o" +
        " join o.member m" +
        " join o.delivery d", OrderSimpleQueryDto.class)
        .getResultList();
  }
}
