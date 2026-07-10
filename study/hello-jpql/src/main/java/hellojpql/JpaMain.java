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
            member.changeTeam(team);
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("========================START============================");
            // 조인 : 내부 조인, 외부 조인, 세타 조인
            String jpql = "select m from Member m inner join m.team t";
            List<Member> inner = em.createQuery(jpql, Member.class).getResultList();

            jpql = "select m from Member m left outer join m.team t";
            List<Member> outer = em.createQuery(jpql, Member.class).getResultList();

            // 연관관계 없는 엔티티 외부 조인이 가능하다.
            jpql = "select count(m) from Member m, Team t where m.username = t.name";
            List<Member> theta = em.createQuery(jpql, Member.class).getResultList();
            System.out.println(theta.size());

            jpql = "select m from Member m left join m.team t on t.name = 'team 1'";
            List<Member> joinOn = em.createQuery(jpql, Member.class).getResultList();

            jpql = "select m from Member m left join Team t on m.username = t.name";
            List<Member> where = em.createQuery(jpql, Member.class).getResultList();
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
