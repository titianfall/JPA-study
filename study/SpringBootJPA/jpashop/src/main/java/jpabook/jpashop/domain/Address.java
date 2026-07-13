package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter // 값 타입에 getter만 생성한다.
public class Address {
    private String city;
    private String street;
    private String zipcode;

    // 리플렉션, 프록시 등등을 위한 기본 생성자
    protected Address() {
    }

    // 생성할때만 값을 설정하며 getter로 값을 가져가기만 한다.
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
