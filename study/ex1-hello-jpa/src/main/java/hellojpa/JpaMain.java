package hellojpa;

import jakarta.persistence.*;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        // resources/META-INF의 persistence.xml 설정을 읽고
        // EntityManager 객체를 생성하는 "hello" 이름의 싱글톤 EntityManagerFactory 객체를 생성합니다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
//            // 비영속
//            Member member = new Member();
//            member.setId(101L);
//            member.setName("HelloJPA");
//
//            // 영속
//            System.out.println("=== BEFORE ===");
//            em.persist(member); // 1차 캐시에 저장됨
//            System.out.println("=== AFTER ===");
//            // before 과 after 사이에 쿼리가 날라가지 않았다!

            // 1차 캐시에서 가져옴 ( sql 쿼리가 필요없음 )
            System.out.println("findMember1");
            Member findMember1 = em.find(Member.class, 101L);
            System.out.println("findMember2");
            Member findMember2 = em.find(Member.class, 101L);

            // 같은 트랜잭션 내에서 영속성 컨텍스트의 1차 캐시 덕분에 둘은 동일함
            System.out.println("isEqualTo = " + (findMember1 == findMember2));
            System.out.println("tx.commit()");
            tx.commit(); // 쿼리는 transaction commit 시에 날라간다.
        } catch(Exception e) {
            tx.rollback();
        } finally {
            // 역순으로 자원 해제
            em.close();
            emf.close();
        }
    }
}
