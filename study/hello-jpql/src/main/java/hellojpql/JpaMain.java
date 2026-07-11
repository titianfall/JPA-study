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
//            // 영속성 컨텍스트와 프록시에 대한 이해
//            String jpql = "select m from Member m";
//            List<Member> resultList = em.createQuery(jpql, Member.class).getResultList();
//            for (Member member : resultList) {
//                // 회원1, 팀A(SQL)
//                // 회원2, 팀A(1차 캐시 - 영속성 컨텍스트)
//                // 회원3, x (NPE 에러 조심)
//                // if) 회원3, 팀B(SQL) > 결과적으로 쿼리가 1 + 2 번 나갔다. 만약 회원이 100명인데 모두 팀이 다르다면? 100방 쿼리가 나간다.
//                // 이때 N + 1 문제가 발생한다.
//                System.out.println(member.getUsername()); // 프록시에서 reference = Member(진짜 엔티티 객체) 연결
//                System.out.println(member.getTeam()); // db 에서 Team 엔티티 1차캐시에 저장, null 반환(회원3)
//                                    // member.getTeam().getName() >> NPE
//            }
//
//            // fetch join - jpql 성능 최적화를 위해 제공하는 기능
//            String sql = "select m.*, t.* from Member m inner join Team t on m.TEAM_ID = t.id";
//            // 회원을 조회하면서 연관된 팀도 함께 조회(SQL 한 번에) - fetch = fetchType.LAZY
//            jpql = "select m from Member m join fetch m.team"; // Member m (inner) join Team t (프록시 없음)
//            List<Member> result = em.createQuery(jpql, Member.class).getResultList();
//
//            for (Member member : result) {
//                System.out.println(member.getUsername() + " " +  member.getTeam().getName());
//            }

//            // collection fetch join - oneToMany 관계
//            String sql = "select t.*, m.* from Team t inner join Member m on t.TEAM_ID = m.TEAM_ID";
//            String jpql = "select distinct t from Team t join fetch t.members";
//            // 쿼리는 반드시 DB로 조회된다. 1차 캐시 외의 만족값이 DB에 존재할 수 있기 때문
//            List<Team> resultList2 = em.createQuery(jpql, Team.class).getResultList();
//
//            for (Team team : resultList2) {
//                // hibernate 6. 부터는 distinct 를 안써도 컬렉션 페치 조인 시 같은 식별자 엔티티를 자동으로 중복 제거한다.
//                // 데이터가 뻥튀기 되지 않는 이유다. (DB row 는 여전히 2줄, 엔티티로 조립할 때 걸러진다)
//                System.out.println("team = " + team.getName() + " | members = " + team.getMembers().size());
//                for (Member member : team.getMembers()) {
//                    System.out.println("member | username= " + member.getUsername() + " | name= " + member.getTeam().getName());
//                }
//            }
//
//            // DB row 뻥튀기를 눈으로 확인 - 스칼라 프로젝션은 엔티티 조립(중복 제거)을 거치지 않는다.
//            // 페치 조인 대상에는 별칭을 못 쓰므로 일반 join 으로 조회.
//            // size(컬렉션) : 컬렉션의 크기를 반환하는 JPQL 함수 (SQL 에서는 서브쿼리로 번역됨)
//            String scalarJpql = "select t.id, t.name, size(t.members) from Team t join t.members m";
//            List<Object[]> rows = em.createQuery(scalarJpql, Object[].class).getResultList();
//
//            for (Object[] row : rows) {
//                // Object[] 의 각 칸 = SELECT 절에 나열한 항목 (테이블 칼럼이 아님)
//                System.out.println("team = " + row[1] + " | members.size() = " + row[2]);
//            }

            // 근데 일반 조인과 다른점은 무엇인가?
            String sql = "select t.* from Team t inner join Member m on t.id = m.MEMBER_ID where t.name = '팀A'";
            String jpql = "select t From Team t join t.members m"; // 쿼리를 살펴보면 team에 대한 칼럼만 가지고 온다 + 일대다 쿼리에 따른 뻥튀기
            List<Team> resultList3 = em.createQuery(jpql, Team.class).getResultList();
            for (Team team : resultList3) {
                System.out.println("team = " + team.getName() + " | members = " + team.getMembers().size());
                List<Member> members = team.getMembers(); //
                for (Member member : members) {
                    System.out.println(member.getUsername() + " | member = " + member.getTeam().getName());
                }
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
