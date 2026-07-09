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

            Member member = new Member();
            member.setUsername("hello");
            Member member2 = new Member();
            member2.setUsername("world");

            em.persist(member);
            em.persist(member2);

            em.flush();
            em.clear();

            // em.find(): DB를 즉시 조회해서 실제 엔티티 반환
            // em.getReference(): DB 조회를 미루는 프록시 반환 (이 시점엔 SELECT 없음)
            // Member findMember = em.find(Member.class, 1L); // 이것만을 위해서도 select 쿼리가 나감
            Member findMember = em.find(Member.class, member.getId());
            Member findMember2 = em.getReference(Member.class, member2.getId());
            System.out.println("findMember2.class = " + findMember2.getClass()); // 프록시 클래스
            System.out.println("findMember2.id = " + findMember2.getId());

            System.out.println("findMember.username = " + findMember.getUsername()); // 이 시점에 초기화(SELECT)

            // 타입체크에 대하여
            System.out.println(findMember.getClass()); // class Hellojpa.Member
            System.out.println(findMember2.getClass()); // class Hellojpa.Member$HibernateProxy$xxxx

            // 클래스 비교
            // false
            System.out.println("findMember(find) == findMember2(ref) : " + (findMember.getClass() == findMember2.getClass()));

            // instance of 비교 프록시는 Member를 상속하기 때문에 둘다 true가 된다.
            System.out.println(findMember instanceof Member);
            System.out.println(findMember2 instanceof Member);

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
