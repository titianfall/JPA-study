package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Team;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            Team team = new Team();
            team.setName("TeamA");
//            team.getMembers().add(member); // 읽기전용 가짜 mapping 에 값을 추가
            em.persist(team); // jpa >> insert 쿼리 생성

            // 저장
            Member member = new Member();
            member.setUsername("member2");
            member.changeTeam(team); // 연관 관계의 주인에 insert 쿼리를 수행해야한다. 반드시
            em.persist(member); // jpa가 team pk 값을 꺼내 fk 값에 insert 시에 fk 값을 자동으로 사용한다.

            // 근데 까먹기 일수이기 때문에 연관관계 편의 메소드를 사용하면 좋다.
            // team.getMembers().add(member); // 아무 영향을 주지 않음
            // 이라고 생각했는데 flush, clear 없다고 가정하고 em.find로 점프해보자

            em.flush();
            em.clear();

            // 영속성 컨텍스트에 들어간 team은 아무것도 없는 객체를 반환한다.
            // 즉, 객체지향 적으로 틀린 설계가 되어진다. 때문에 team, member 둘다 객체를 세팅해줘야한다.
            Team findTeam = em.find(Team.class, team.getId());
            List<Member> members = findTeam.getMembers();
            System.out.println("===============");
            System.out.println("members = " + findTeam.toString());
            System.out.println("===============");

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
