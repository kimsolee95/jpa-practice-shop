package com.example.demo.repository.order.query;

import com.example.demo.domain.Address;
import com.example.demo.domain.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrderItemQueryDto {

  private String itemName;
  private int orderPrice;
  private int count;


}
