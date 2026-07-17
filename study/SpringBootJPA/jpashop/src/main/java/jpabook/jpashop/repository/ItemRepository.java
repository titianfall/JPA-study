package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    // 상품 저장
    public void save(Item item) {
        if(item.getId() == null) {
            em.persist(item);
        } else {
            // 병합시 Item merge 객체를 반환하며 추가적인 수정이 필요할 경우 기존의 객체 대신 새로운 객체를 사용하여야함
            // 단, 병합은 모든 속성이 변경된다는 점이 원하는 속성만 변경하는 dirty checking과의 가장 큰 차이다.
            // 개중에는 속성값이 없을 경우 null로 업데이트 할 위험도 존재한다.
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
