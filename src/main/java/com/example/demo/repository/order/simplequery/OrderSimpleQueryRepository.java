package com.example.demo.repository.order.simplequery;

import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

  private final EntityManager em;

  public List<OrderSimpleQueryDto> findORderDtos() {
    return  em.createQuery(
        "select new com.example.demo.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
        + " from Order o"
        + " join o.member m"
        + " join o.delivery d", OrderSimpleQueryDto.class
    ).getResultList();
  }

}
