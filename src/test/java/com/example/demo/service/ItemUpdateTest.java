package com.example.demo.service;

import com.example.demo.domain.item.Book;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

  @Autowired
  EntityManager em;

  @Test
  public void updateTest() throws Exception {
    Book book = em.find(Book.class, 1L);
    //TX

  }

}
