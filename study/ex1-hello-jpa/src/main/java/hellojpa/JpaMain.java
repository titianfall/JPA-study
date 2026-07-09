package hellojpa;

import jakarta.persistence.*;

public class JpaMain {

    public static void main(String[] args) {

        // resources/META-INF의 persistence.xml 설정을 읽고
        // EntityManager 객체를 생성하는 "hello" 이름의 싱글톤 EntityManagerFactory 객체를 생성합니다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{

            Member member1 = new Member();
            member1.setUsername("member1");

            em.persist(member1);

            em.flush();
            em.clear();

            // 프록시 - 준영속 상태일때에 대하여
            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember = " + refMember.getClass()); // Proxy

            // 준영속으로 만드는 방법 - close(), detach(Entity), clear()
            em.detach(refMember);
            // em.close(); // 최신 버전에서는 트랜잭션이 끝날때 실제 자원 해제가 이루어져 에러가 안나게됨
            // em.clear();

            // detach(entity), clear() : could not initialize proxy - no Session
            // close() : could not initialize proxy - the owning Session was closed
            System.out.println("refMember.getUsername() = " + refMember.getUsername());

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
