package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test
    void 상품_단건_조회() {
        // given
        Book book = new Book();
        book.addStock(10);

        // when
        itemRepository.save(book);
        Item foundItem = itemRepository.findOne(book.getId());

        // then
        assertThat(foundItem).isSameAs(book);
        assertThat(foundItem.getStockQuantity()).isEqualTo(10);
    }

    @Test
    void 상품_전체_조회() {
        // given
        Book book1 = new Book();
        Book book2 = new Book();
        itemRepository.save(book1);
        itemRepository.save(book2);

        // when
        List<Item> items = itemRepository.findAll();

        // then
        assertThat(items).contains(book1, book2);
    }
}
