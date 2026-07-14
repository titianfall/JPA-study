package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // @Component 내장 컴포넌트 스캔대상
@RequiredArgsConstructor
public class MemberRepository {

    // @PersistenceUnit
    // private EntityManagerFactory emf; // 쓸일은 없음

    // 자동으로 emf 로부터 em를 주입받음
    // @PersistenceContext
    private final EntityManager em;

    // cmd + shift + T > test 파일 생성
    public void save(Member member) {
        em.persist(member);
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
