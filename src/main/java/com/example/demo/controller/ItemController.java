package com.example.demo.controller;

import com.example.demo.domain.item.Book;
import com.example.demo.domain.item.Item;
import com.example.demo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/items/new")
    public ResponseEntity<String> create(@RequestBody BookForm form) {
        itemService.saveItem(Book.createBook(form));
        return new ResponseEntity<>("success new item create", HttpStatus.CREATED);
    }

    @GetMapping("/items")
    public ResponseEntity<List<Item>> list() {
        return new ResponseEntity<>(itemService.findItems(), HttpStatus.OK);
    }

    @PostMapping("/items/{itemId}/edit")
    public ResponseEntity<?> updateItem(
            @PathVariable("itemId") String itemId, @RequestBody BookForm form) {
        //todo: 해당 user가 item에 대한 권한이 있는지 check 하는 logic
        Book book = Book.from(form);
        itemService.saveItem(book);
        return new ResponseEntity<>("book create success", HttpStatus.OK);
    }

}
