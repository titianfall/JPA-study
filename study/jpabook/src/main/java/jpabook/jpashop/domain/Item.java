package jpabook.jpashop.domain;

import jakarta.persistence.*;

// 조인 전략
// 아무런 조건을 걸지 않았을때는 한 테이블에 모두 들어가게된다. jpa single table strategy
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
// SINGLE_TABLE 전략에서는 필수로 만들어진다. 단일 테이블에서 어떤 테이블의 값인지 알수가 없기 때문이다.
@DiscriminatorColumn // DTYPE(not null) - movie, album, book 하나가 들어간다. 쿼리시 보기 좋다.
public abstract class Item{
    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}