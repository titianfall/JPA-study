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
            String jpql = "select 'a' || 'b' From Member m";
            // jpql = "select concat('a', 'b') from Member m";
            List<String> resultList = em.createQuery(jpql, String.class).getResultList();
            for (String str :  resultList) {
                System.out.println(str);
            }

            jpql = "select subString(m.username, 2, 3) from Member m";
            jpql = "select length(m.username) from Member m";
            jpql = "select m from Member m where trim(m.username) = :username";
            jpql = "select m from Member m where lower(m.username) = :username";
            jpql = "select m from Member m where upper(m.username) = :username";
            jpql = "select locate('de', 'abcdef') from Member m";
            List<Integer> result = em.createQuery(jpql, Integer.class).getResultList();
            jpql = "select index(t.members) from Team t";
            jpql = "select m from Member m where abs(m.age - 30) < 5";
            jpql = "select m from Member m where mode(m.age, 2) = 0";
            jpql = "select m from Member m order by m.username asc";
            jpql = "select m from Member m order by m.age desc";
            jpql = "select lower(m.username) from Member m";

            jpql = "select function('group_concat', m.username) from Member m"; // 관리자 1, 관리자 2
            List<String> resultList1 = em.createQuery(jpql, String.class).getResultList();
            for (String str :  resultList1) {
                System.out.println(str);
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
