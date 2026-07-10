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

            for(int i = 0; i < 100; ++i){
                Member member = new Member();
                member .setUsername("member" + i);
                member.setAge(i);
                em.persist(member);
            }
            em.flush();
            em.clear();

            System.out.println("========================START============================");
            // 페이징 두 api
            // setFirstResult(int startPosition)
            // setMaxResults(int maxResults)

            List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(1) // offset ? rows
                    .setMaxResults(10) // fetch first ? rows only
                    .getResultList();

            System.out.println("result.size() = " + result.size());
            for(Member m : result){
                System.out.println(m);
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
