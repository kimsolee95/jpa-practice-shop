package com.example.demo.service;

import com.example.demo.domain.item.Book;
import com.example.demo.domain.item.Item;
import com.example.demo.repository.ItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

  private final ItemRepository itemRepository;

  @Transactional
  public void saveItem(Item item) {
    itemRepository.save(item);
  }

  @Transactional
  public void updateItem(Long itemId, Book param) {
    Item findItem = itemRepository.findOne(itemId); //영속상태의 객체 -> 더티체킹 (변경감지)
    findItem.setPrice(param.getPrice());
    findItem.setName(param.getName());
    findItem.setStockQuantity(param.getStockQuantity());
  }

  public List<Item> findItems() {
    return itemRepository.findAll();
  }

  public Item findOne(Long itemId) {
    return itemRepository.findOne(itemId);
  }

}
