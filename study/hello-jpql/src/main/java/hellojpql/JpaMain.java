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
            // 다형성 쿼리
            String sql = "select i from Item i where i.DTYPE = 'B' and i.author = 'kim'";
            // String jpql = "select i from Item i where treat(i as Book).author = 'kim'";

            // 엔티티 직접 사용
            String jpql = "select count(m.id) from Member m"; // 일반적인 사용법
            jpql = "select count(m) from Member m"; // sql에서 해당 엔티티의 기본값을 사용 m.id
            // 둘다 같은 다음 SQL 실행
            sql = "select count(m.id) as cnt from Member m";

//            // 엔티티를 파라미터로 전달
//            jpql = "select m from Member m where m = :member";
//            List<Member> resultList = em.createQuery(jpql, Member.class)
//                    .setParameter("member", member1)
//                    .getResultList();
//
//            // 식별자를 직접 전달 - 식별자(Long)를 넘길 때는 비교 대상도 m.id 여야 한다
//            // (m = :memberId 에 Long 을 바인딩하면 QueryArgumentException: Long != Member 타입 불일치)
//            jpql = "select m from Member m where m.id = :memberId";
//            List<Member> resultList2 = em.createQuery(jpql, Member.class)
//                    .setParameter("memberId", member1.getId())
//                    .getResultList();
//
//            // 둘다 같은 SQL 실행
//            sql = "select m.* from Member m where m.MEMBER_ID = ?";
//
//            jpql = "select m from  Member m where m.team = :team"; // :team, :teamId
//            List<Member> members = em.createQuery(jpql, Member.class)
//                    .setParameter("team", teamA.getId()) // teamA
//                    .getResultList();
//            for (Member member : members) {
//                System.out.println(member);
//            }

            // Named 쿼리 - 쿼리 재활용, 정적 쿼리, 어노테이션 or XML 정의가능
            // 애플리케이션 로딩 시점에 초기화 후 재사용 가능 - 파싱 및 캐시로 가져오기 때문에 cost가 없음 + 검증 가능
            List<Member> resultList1 = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", member1.getUsername())
                    .getResultList();

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
