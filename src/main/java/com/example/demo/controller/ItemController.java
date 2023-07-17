package com.example.demo.controller;

import com.example.demo.domain.item.Book;
import com.example.demo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/items/new")
    public ResponseEntity<String> create(@RequestBody BookForm form) {
        itemService.saveItem(Book.createBook(form));
        return new ResponseEntity<>("success new item create", HttpStatus.CREATED);
    }

}
