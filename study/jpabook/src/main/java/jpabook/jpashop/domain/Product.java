package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {
    @Id @GeneratedValue
    @Column(name = "PRODUCT_ID")
    private Long id;

    private String name;

    // 써놓고 보니 나쁘지 않아보이는데 왜 실무에서 사용하지 않는가?
    // 단순 연결만 하고 끝나지 않는 경우가 많다. 데이터가 들어온다면?
    // 쿼리중간중간 생각하지 못한 쿼리가 발생한다. 중간 테이블이 숨겨져있기 때문

    // 다대다 관계에서 양방향 관계를 정의한다.
    // 중간 관계 테이블이 생성된다는 점이 있다.
    // @ManyToMany(mappedBy = "products")
    // private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<MemberProduct> memberProducts = new ArrayList<>();

    public List<MemberProduct> getMemberProducts() {
        return memberProducts;
    }

    public void setMemberProducts(List<MemberProduct> memberProducts) {
        this.memberProducts = memberProducts;
    }

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
}
