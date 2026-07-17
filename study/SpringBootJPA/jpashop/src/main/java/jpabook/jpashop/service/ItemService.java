package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
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

    @Transactional // readOnly = false
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, UpdateBookDto dto) {
        // 변경 감지 - id를 통해 실제 영속상태인 엔티티를 가져왔음
        Book findItem = (Book) itemRepository.findOne(itemId);
        // setter 대신 의미있는 메서드로 변경한다.
        findItem.change(dto.getName(), dto.getPrice(), dto.getStockQuantity(),
                dto.getAuthor(), dto.getIsbn());

        // 별도의 merge 작업이 필요하지 않음
    }
    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }
}
