package hellojpql;

import jakarta.persistence.*;

import java.util.List;

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

            // 반환 타입이 분명할 경우 TypedQuery<T> 사용
            TypedQuery<Member> query1 = em.createQuery("select m from Member m where m.id = 10L", Member.class);
            // TypedQuery<String> query2 = em.createQuery("select m.username from Member m where m.username = :username", String.class);
            TypedQuery<Member> query2 = em.createQuery("select m from Member m where m.username = ?1", Member.class);
            Member singleResult = query2.setParameter(1, member.getUsername()).getSingleResult();
            // String singleResult = query2.setParameter("username", "member1").getSingleResult();
            System.out.println(singleResult);

            // 반환 타입이 불분명할 경우에는 Query 사용
            Query query3 = em.createQuery("select m.username, m.age from Member m");

            // 반환 타입이 한개일 경우 (값이 있을때 써야함)
            // 결과가 없을 경우   : jakarta.persistence.NoResultException
            // 결과가 여러개일 경우: jakarta.persistence.NonUniqueResultException
            // spring data jpa > getSingleResult() > try-catch > null or Optinal 반환
            System.out.println("======================getSingleResult()==================");
            Member result = em.createQuery("select m from Member m", Member.class).getSingleResult();

            // 반환 타입이 컬렉션일경우 - 값이 없으면 빈 컬렉션 반환(NPE 걱정은 안해도 됨)
            System.out.println("=======================getResultList()=======================");
            List<Member> resultList = em.createQuery("select m from Member m", Member.class).getResultList();

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
