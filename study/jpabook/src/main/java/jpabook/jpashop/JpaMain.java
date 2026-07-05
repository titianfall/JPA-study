package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Team;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            // 저장
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team); // jpa >> insert 쿼리 생성

            Member member = new Member();
            member.setUsername("member2");
            member.setTeam(team); // 단방향 연관관계 설정, 참조 저장
            em.persist(member); // jpa가 team pk 값을 꺼내 fk 값에 insert 시에 fk 값을 자동으로 사용한다.

            // 쿼리를 보고싶으면
            em.flush(); // db와 sync를 맞추고
            em.clear(); // 영속성 컨텍스트 초기화 >> select 쿼리가 날라갈거임

            // 조회
            Member findMember = em.find(Member.class, member.getId()); // 이때 left (equi) join 함 why?
            Team findTeam = findMember.getTeam();

            System.out.println("findTeam = " + findTeam.getName());

            // 수정 - 100번 team 있다고 가정
            Team newTeam = em.find(Team.class, 100L);
            findMember.setTeam(newTeam);

            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }
}
