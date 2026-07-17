package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@DiscriminatorValue(value = "Book")
public class Book extends Item{

    private String author;
    private String isbn;

    /**
     * 책 정보 변경 - Item의 공통 필드에 더해 Book 전용 필드까지 변경한다.
     */
    public void change(String name, int price, int stockQuantity, String author, String isbn) {
        change(name, price, stockQuantity);
        this.author = author;
        this.isbn = isbn;
    }
}
