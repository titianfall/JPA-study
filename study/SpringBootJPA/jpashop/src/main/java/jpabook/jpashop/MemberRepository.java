package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository // @Component 내장 컴포넌트 스캔대상
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    // cmd + shift + T > test 파일 생성
    public Long save(Member member) {
        em.persist(member);
        return member.getId();

        // return member 하면 되지않는가?
        // 1. 커맨드와 쿼리를 분리한다. - 저장과 조회의 분리
        // 조회하는 쪽에서 객체를 계속 사용하게 만들지 않는다.
        // 2. 사이트 이펙트
        // 반환받은 member 객체를 호출부에서 수정하면 영속상태에 의해 의도치않은 update 쿼리가 발생한다.
        // 3. 조회용 키만 돌려주도록 설계
        // CQS(Command-Query Separation)을 만족시키면서 호출부가 다시 엔티티를 찾을 수 있게한다.
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
