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

//            // 1차 캐시에서 가져옴 ( sql 쿼리가 필요없음 )
//            System.out.println("findMember1");
//            Member findMember1 = em.find(Member.class, 101L);
//            System.out.println("findMember2");
//            Member findMember2 = em.find(Member.class, 101L);
//
//            // 같은 트랜잭션 내에서 영속성 컨텍스트의 1차 캐시 덕분에 둘은 동일함
//            System.out.println("isEqualTo = " + (findMember1 == findMember2));
//            System.out.println("tx.commit()");
//            tx.commit(); // 쿼리는 transaction commit 시에 날라간다.

//            Member member1 = new Member(150L, "A");
//            Member member2 = new Member(160L, "A");
//
//            em.persist(member1);
//            em.persist(member2);
//            System.out.println("================");
//            // 바로 쿼리가 날라가지 않고 persistenceContext에 쌓인다.

//            // select m.id, m.name from Member m where m.id = "150L";
//            Member memberA = em.find(Member.class, 150L);// DML 버퍼에 삽입
//            memberA.setName("hi"); // update 쿼리가 날라갔음
//            // em. persist(memberA); 가 필요하지 않음. 콜렉션에 값을 수정하고 다시 넣지 않는 것처럼

//            Member member = new Member(200L, "member200");
//            em.persist(member);
//
//            em.flush();
//
//            System.out.println("================");

            Member member = em.find(Member.class, 150L);
            member.setName("AAAAA");

            // em.detach(member); // 준영속 상태, jpa에서 더이상 관리하지 않게됨 >> select 쿼리만 나오고 update 쿼리가 날라가지 않음
            em.clear(); // 영속성 컨텍스트를 모두 지웠기 때문에 쿼리가 한번 더 발생함

            Member member2 = em.find(Member.class, 150L);

            System.out.println("=========================");
            tx.commit(); // commit 시에 한번에 db로 flush 되고 commit 된다.
        } catch(Exception e) {
            tx.rollback();
        } finally {
            // 역순으로 자원 해제
            em.close(); // 영속성 컨텍스트 종료
            emf.close();
        }
    }
}
