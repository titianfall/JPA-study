package jpabook.jpashop.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
// entity name이 default이다.
// 만약 회사 정책상 우리는 테이블을 A 이렇게 받을거라고 설정할 경우
@DiscriminatorValue(value = "A") // 따로 설정이 가능하다.
public class Album extends Item{
    private String artist;
}
