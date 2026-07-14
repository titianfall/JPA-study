package jpabook.jpashop.service;

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
class ItemServiceTest {

    @Autowired ItemService itemService;

    @Test
    void 상품_단건_조회() {
        // given
        Book book = new Book();
        book.addStock(5);

        // when
        itemService.saveItem(book);
        Item foundItem = itemService.findOne(book.getId());

        // then
        assertThat(foundItem).isSameAs(book);
        assertThat(foundItem.getStockQuantity()).isEqualTo(5);
    }

    @Test
    void 상품_목록_조회() {
        // given
        Book book1 = new Book();
        Book book2 = new Book();
        itemService.saveItem(book1);
        itemService.saveItem(book2);

        // when
        List<Item> items = itemService.findItems();

        // then
        assertThat(items).contains(book1, book2);
    }
}
