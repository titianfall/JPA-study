package hellojpa;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaMain {

    public static void main(String[] args) {

        // resources/META-INF의 persistence.xml 설정을 읽고
        // EntityManager 객체를 생성하는 "hello" 이름의 싱글톤 EntityManagerFactory 객체를 생성합니다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            Member member = new Member();
            member.setUsername("kimjiho");
            member.setHomeAddress(new Address());

            member.getFavoriteFoods().add("치킨");
            member.getAddressHistory().add(new AddressEntity("city", "street", "10000"));
            em.persist(member);

            em.flush();
            em.clear();
            System.out.println("================START=========================");
            // criteria - 너무 복잡하고 실용성 없음
            // 사용 준비
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> query = cb.createQuery(Member.class);

            // 루트 클래스 (조회를 시작할 클래스)
            Root<Member> m = query.from(Member.class);

            // 쿼리 생성 - 오타, 동적 쿼리 장점
            CriteriaQuery<Member> cq = query.select(m);
            String username = "";
            if(username != null){
                cq.where(cb.equal(m.get("username"), "kim"));
            }
            List<Member> resultList = em.createQuery(cq).getResultList();

//            // JPQL - QueryDSL
//            // SQL : select m from member m where m.age > 18
//            JPAQueryFactory query = new JPAQueryFactory(em);
//            QMemeber m = QMember.member;
//
//            List<Member> resultList = query.selectFrom(m)
//                    .where(m.age.gt(18))
//                    .orderBy(m.name.desc())
//                    .fetch();

            // dynamic native SQL query
            List<Member> nativeResultList = em.createNativeQuery("select * from member", Member.class).getResultList();


            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            // 역순으로 자원 해제
            em.close(); // 영속성 컨텍스트 종료
            emf.close();
        }
    }
}
