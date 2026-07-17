package jpabook.jpashop.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 책 수정에 필요한 데이터만 담아 서비스로 넘기는 DTO.
 * 파라미터를 나열하면 순서를 헷갈리기 쉬워서 DTO로 묶었다.
 */
@Getter
@AllArgsConstructor
public class UpdateBookDto {

    private String name;
    private int price;
    private int stockQuantity;

    private String author;
    private String isbn;
}
