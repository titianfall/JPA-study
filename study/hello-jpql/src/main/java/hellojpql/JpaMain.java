package hellojpql;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hellojpql");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{

            Team team = new Team();
            team.setName("Team 1");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member 1");
            member.changeTeam(team);
            member.setAge(10);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("========================START============================");
            // 경로 표현식 (.)으로 객체 그래프 탐색
            // 상태 필드(state field) : 경로 탐색의 끝, 탐색x
            String jpql = "select m.username from Member m";

            // 단일 값 연관 필드 ( 묵시적 내부 조인 ), 탐색 o
            jpql = "select m.team from Member m";
            jpql = "select m.team.name from Member m";

            // 컬렉션 값 연관 경로 : 묵시적 내부 조인 발생, 탐색 X
            jpql = "select t.members from Team t";
            // jpql = "select t.members.username from Team t; // 불가능
            // 그러나 from 절에 명시적 조인을 통해 탐색이 가능
            jpql = "select t.members from Member m inner join m.team t";

            tx.commit();
        } catch(Exception e){
            e.printStackTrace();
            tx.rollback();
        } finally{
            if(em != null) em.close();
            if(emf != null) emf.close();
        }
    }
}
