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
            String jpql = "select m.username, 'HELLO', true from Member m " +
                    // "where m.type = hellojpql.MemberType.ADMIN"; // 파라미터 바인딩으로 줄일수 있음
                     "where m.type = :userType";
            List resultList1 = em.createQuery(jpql)
                    .setParameter("userType", MemberType.ADMIN)
                    .getResultList();
            Object o = (Object []) resultList1.get(0);
            Object[] result =  (Object[]) o;
            for(Object o1 : result){
                System.out.println(o1);
            }


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
