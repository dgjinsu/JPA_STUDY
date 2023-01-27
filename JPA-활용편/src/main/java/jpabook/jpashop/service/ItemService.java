package jpabook.jpashop.service;

import jpabook.jpashop.entity.item.Book;
import jpabook.jpashop.entity.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public void updateItem(Long itemId, Book b) {
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(b.getPrice());
        findItem.setName(b.getName());
        findItem.setStockQuantity(b.getStockQuantity());
        //이렇게 변경만 하고 따로 save 안 해줘도 됨 -> 영속상태 이기 때문
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
