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

            // 이미 영속성 컨텍스트에 엔티티가 있는 경우에 대하여 - jpa 특성상 같은 트랜잭션 레벨 안에서 동일한 영속성 컨텍스트를 가져야한다.
            // m1 이라는 엔티티로 db에서 조회하여 영속성 컨텍스트에 등록이 되었는데
            Member m1 = em.find(Member.class, 1L);
            System.out.println("m1 = " + m1.getClass());
            // 프록시가 아닌 Hellojpa.Member 가 나옴
            Member reference = em.getReference(Member.class, 1L);
            System.out.println("reference = " + reference.getClass());

            System.out.println("m1 == reference " + (m1 == reference));

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
