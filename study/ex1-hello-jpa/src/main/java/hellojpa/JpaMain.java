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

            // 그럼 반대로 프록시가 먼저 영속성 컨텍스트에 반영되었을 경우에는?
            Member reference = em.getReference(Member.class, member1.getId());
            System.out.println("reference = " + reference.getClass()); // Proxy

            Member findMember = em.find(Member.class, member1.getId());
            System.out.println("findMember = " + findMember.getClass()); // Member 일것같지만

            // true 결과
            System.out.println("reference == findMember : " + (reference.getClass() == findMember.getClass()));
            tx.commit(); // commit 시에 한번에 db로 flush 되고 commit 된다.
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
