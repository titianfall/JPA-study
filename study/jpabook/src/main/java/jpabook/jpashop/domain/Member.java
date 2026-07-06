package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBER_ID") // 대소문자는 회사의 룰을 따르면 됨
    private Long id;
    private String name;
    private String city;
    private String street;
    private String zipcode;

    // 만약 Member(1) 쪽에서 다(N)쪽의 정보인 Order 정보를 얻고 싶다.
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    // 예제니 모두 만들지만 setter()를 만들 때는 고민이 필요하다.
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }


}
