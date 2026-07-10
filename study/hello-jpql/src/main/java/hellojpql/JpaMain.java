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
            String jpql = "select " +
                            "case when m.age <= 10 then '학생요금' " +
                            "      when m.age >=  60 then '경로요금' " +
                            "else '일반요금' " +
                            "end " +
                            "from Member m";

            List<String> resultList = em.createQuery(jpql, String.class).getResultList();
            for(String s : resultList){
                System.out.println(s);
            }

            // 하나씩 조회하여 null이 아니면 반환
            jpql = "select coalesce(m.username, '이름없는 회원') from Member m";
            List<String> resultList1 = em.createQuery(jpql, String.class).getResultList();
            for (String str : resultList1) {
                System.out.println(str);
            }

            // NULLIF 두 값이 같으면 null 반환, 다르면 첫번째 값 반환
            jpql = "select NULLIF(m.username, '관리자') from Member m";
            List<String> resultList2 = em.createQuery(jpql, String.class).getResultList();
            for (String str : resultList2) {
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
