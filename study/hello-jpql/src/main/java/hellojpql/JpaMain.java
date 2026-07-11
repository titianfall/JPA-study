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

            Product product1 = new Product();
            product1.setName("product1");
            product1.setPrice(12345);
            product1.setStockAmount(5);
            em.persist(product1);

            em.flush();
            em.clear();

            System.out.println("========================START============================");
            // 벌크 연산 - 쿼리 한번으로 여러 테이블 row 변경(entity)
            //
            // if) 재고가 10개 미만인 모든 상품의 가격을 상승시키고자한다.
            // JPA 변경 감지만으로 실행할 경우 1. 조회, 2. 증가, 3. 트랜잭션 커밋 시점에 변경감지
            // 변경된 데이터가 100건이라면? 100번의 UPDATE SQL 이 동작한다.

            // UPDATE/DELETE 는 SELECT 가 아니므로 결과 타입 지정 X, getResultList() X
            // executeUpdate() 가 영향받은 row 수를 반환한다
            // ⚠️ 강의(Hibernate 5)와 차이 - Hibernate 6 은 타입 검사가 엄격해서
            //    Double(p.price * 1.1)을 Integer(p.price)에 바로 대입 불가 → cast 필요
            String jpql = "update Product p " +
                    "set p.price = cast(p.price * 1.1 as Integer) " +
                    "where p.stockAmount < :stockAmount";
            System.out.println("벌크 연산 전 stock = " + product1.getStockAmount());
            int resultCount = em.createQuery(jpql)
                    .setParameter("stockAmount", 10)
                    .executeUpdate();
            System.out.println("resultCount = " + resultCount);
            System.out.println("영속성 컨텍스트 초기화 전 price = " + product1.getPrice());
            // ⚠️ 벌크 연산은 영속성 컨텍스트를 무시하고 DB에 직접 쿼리한다.
            // 초기화하지 않으면 1차 캐시의 product1 은 인상 전 가격(12345)을 그대로 들고 있다.
            em.clear();

            Product findProduct = em.find(Product.class, product1.getId());
            System.out.println("가격 인상 후 = " + findProduct.getPrice());

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
