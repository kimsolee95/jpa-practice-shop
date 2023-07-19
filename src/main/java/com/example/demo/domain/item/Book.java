package com.example.demo.domain.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.example.demo.controller.BookForm;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("B")
@Getter @Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Book extends Item {

  private String author;
  private String isbn;

  public static Book createBook(BookForm form) {
    return Book.builder()
            .author(form.getAuthor())
            .isbn(form.getIsbn())
            .name(form.getName())
            .price(form.getPrice())
            .stockQuantity(form.getStockQuantity())
            .build();
  }

  public static Book from(BookForm form) {
    return Book.builder()
        .id(form.getId())
        .name(form.getName())
        .price(form.getPrice())
        .stockQuantity(form.getStockQuantity())
        .author(form.getAuthor())
        .isbn(form.getIsbn())
        .build();
  }

}
