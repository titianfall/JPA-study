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
            String jpql = "select m from Member m where m.age > (select avg(m2.age) from Member m2)";
            List<Member> subQuery1 = em.createQuery(jpql, Member.class).getResultList();

            jpql = "select m from Member m where (select count(o) from Order o where m = o.member) > 0";
            List<Member> subQuery2 = em.createQuery(jpql, Member.class).getResultList();

            jpql = "select m from Member m where exists (select t from m.team t where t.name = 'team 1')";
            List<Member> subQuery3 = em.createQuery(jpql, Member.class).getResultList();

            jpql = "select o from Order o where o.orderAmount > ALL(select p.stockAmount from Product p)";
            List<Order> subQuery4 = em.createQuery(jpql, Order.class).getResultList();

            jpql = "select m from Member m where m.team = ANY (select t from Team t)";
            List<Team> subQuery5 = em.createQuery(jpql, Team.class).getResultList();


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
