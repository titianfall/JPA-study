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
            Member member = new Member();
            member .setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("========================START============================");
            // 프로젝션 대상 : 엔티티 / 임베디드 타입 / 스칼라 타입
            // 엔티티 프로젝션
            em.createQuery("select m from Member m", Member.class);
            // 묵시적 조인
            List<Team> result = em.createQuery("select m.team from Member m", Team.class).getResultList();

            // 임베디드 타입 프로젝션
            List<Address> resultAddress = em.createQuery("select o.address from Order o", Address.class).getResultList();

            // 스칼라 타입 프로젝션
            List resultList = em.createQuery("select m.username, m.age from Member m").getResultList();
            Object o = resultList.get(0);
            Object[] resultObject = (Object[]) o;

            System.out.println(resultObject[0]);
            System.out.println(resultObject[1]);

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
