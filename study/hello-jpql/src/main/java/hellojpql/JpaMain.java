package hellojpql;

import jakarta.persistence.*;
import org.intellij.lang.annotations.Language;

import java.util.List;
import java.util.Objects;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hellojpql");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{

            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.changeTeam(teamA);
            member1.setAge(10);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.changeTeam(teamA);
            member2.setAge(20);
            em.persist(member2);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            member3.setAge(30);
            em.persist(member3);

            em.flush();
            em.clear();

            System.out.println("========================START============================");
            // fetch join 한계
            // fetch join 대상에는 별칭(alias) 를 줄수없다. Hibernate는 가능하나, 가급적 사용하지 말것
            String jpql = "select t from Team t"; // 별명을 사용할 경우 객체 그래프의 사상과 맞지 않다.
            List<Team> resultList = em.createQuery(jpql, Team.class)
                    .setFirstResult(0)
                    .setMaxResults(2)
                    .getResultList();

            for (Team team : resultList) {
                System.out.println(team.getName());
                team.getMembers().forEach(member -> System.out.println(member));
            }
            // 둘 이상의 컬렉션은 페치 조인 할 수 없다.
            // 일대다 x 일대다 를 페치조인할 경우 뻥튀기가 일어남

            // 컬렉션을 페치 조인하면 페이징 API를 사용할 수 없다.
            // teamA는 2명의 회원을 가졌는데 페치 조인을 1개만 설정하면?
            // 실제 2명의 회원이 아닌 단 한명의 정보만 JPA 입장에서는 회원이 한명밖에 없다고 판단하게 된다.
            // 이럴 경우 팀멤버에 대해 쿼리를 db에 계속 날리게 되는데 이를 해결하기 위해
            // @BatchSize(size = 1000) 1000이하의 값을 넣어줄 경우 N + 1 쿼리를 해결할 수 있다.
            // 이를 persistence.xml에 등록하여 전역 설정으로 사용한다.

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
