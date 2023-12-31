package com.example.demo.repository;

import com.example.demo.domain.Order;
import com.example.demo.domain.OrderStatus;
import com.example.demo.domain.QMember;
import com.example.demo.domain.QOrder;
import com.example.demo.repository.order.simplequery.OrderSimpleQueryDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
    // case 1. 항상 조건이 들어오는 경우 (동적 쿼리 x)
//    return em.createQuery("select o from Order o join o.member m" +
//        " where o.status = :status" +
//        " and m.name like :name", Order.class)
//        .setParameter("status", orderSearch.getOrderStatus())
//        .setParameter("name", orderSearch.getMemberName())
//        .setMaxResults(1000)
//        .getResultList();

    // case 2. 동적쿼리
    String jpql = "select o from Order o join o.member m";
    boolean isFirstCondition = true;

    //주문 상태 검색
    if (orderSearch.getOrderStatus() != null) {
      if (isFirstCondition) {
        jpql += " where";
        isFirstCondition = false;
      } else {
        jpql += " and";
      }
      jpql += " o.status = :status";
    }

    if (StringUtils.hasText(orderSearch.getMemberName())) {
      if (isFirstCondition) {
        jpql += " where";
        isFirstCondition = false;
      } else {
        jpql += " and";
      }
      jpql += " m.name like :name";
    }

    TypedQuery<Order> query = em.createQuery(jpql, Order.class)
        .setMaxResults(1000);

    if (orderSearch.getOrderStatus() != null) {
      query = query.setParameter("status", orderSearch.getOrderStatus());
    }

    if (StringUtils.hasText(orderSearch.getMemberName())) {
      query = query.setParameter("name", orderSearch.getMemberName());
    }

    return query.getResultList();
  }

  /**
   * findAll refactoring
   * */
  public List<Order> findAllByQueryDsl(OrderSearch orderSearch) {
    QOrder order = QOrder.order;
    QMember member = QMember.member;

    JPAQueryFactory query = new JPAQueryFactory(em);
    return query
        .select(order)
        .from(order)
        .join(order.member, member)
        .where(statusEq(orderSearch.getOrderStatus()))
        .limit(1000)
        .fetch();
  }

  private BooleanExpression statusEq(OrderStatus statusCond) {
    if (statusCond == null) {
      return null;
    }
    return QOrder.order.status.eq(statusCond);
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
        "select new com.example.demo.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderData, o.status, d.address) " +
            " from Order o" +
        " join o.member m" +
        " join o.delivery d", OrderSimpleQueryDto.class)
        .getResultList();
  }

  public List<Order> findAllWithItem() {
    return em.createQuery(
        "select distinct o from Order o" +
        " join fetch o.member m" +
        " join fetch o.delivery d" +
        " join fetch o.orderItems oi" +
        " join fetch oi.item i", Order.class)
        .getResultList();
  }

  public List<Order> findAllWithMemberDelivery(int offset, int limit) {
    return em.createQuery(
        "select o from Order o" +
            " join fetch o.member" +
            " join fetch o.delivery d", Order.class)
        .setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
  }
}
