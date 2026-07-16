package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter // 단 @Setter는 필요한 경우에만 작접 선언하여 사용하는 것이 올바르다.
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    // entity를 외부 api로 노출하면 안되는 2가지이유
    // if) 필요에 의해 엔티티에 password를 추가하였다.
    // 이때문에 1. 패스워드 노출, 2. api 스펙 변경 두가지 오류가 발생한다.
    // private String password;
    private String name;
    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
