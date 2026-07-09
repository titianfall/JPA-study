package hellojpa;

import jakarta.persistence.*;
import org.hibernate.Hibernate;

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
            Team team1 = new Team();
            team1.setName("teamA");
            em.persist(team1);

            Team team2 = new Team();
            team2.setName("teamB");
            em.persist(team2);

            // 프록시 인스턴스 초기화 여부 확인
            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setTeam(team1);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setTeam(team2);
            em.persist(member2);

            em.flush();
            em.clear();

            // FetchType.EAGER인 Member에 대해 jpql 을 사용한다 치자
            List<Member> members = em.createQuery("select m from Member m join fetch m.team", Member.class).getResultList();
            // 실제 실행을 시켜보면 쿼리가 join 된 쿼리가 아닌 2개의 쿼리가 나가는걸 확인할 수 있다.
            // 이걸 결과적으로 jpql N + 1 문제라고 한다. (추후 학습)

            // 때문에 모든 연관관계를 지연로딩으로 설정한다. jpql fetchJoin
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
