package hellojpa;

import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        // resources/META-INF의 persistence.xml 설정을 읽고
        // EntityManager 객체를 생성하는 "hello" 이름의 싱글톤 EntityManagerFactory 객체를 생성합니다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            Address address = new Address("city", "street", "10000");

            // 가정 : 동일한 주소를 쓰는 member1 과 member2가 있다
            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setWorkPeriod(new Period(LocalDateTime.now()));
            member1.setHomeAddress(address);

            // 값을 복사하여 사용하면 참조 에러를 막을 수 있다.
            // copyAddress = address 처럼 자바는 공유 참조를 막을수는 없다. 컴파일러 레벨에서 해결이 불가능
            // 때문에 객체 타입을 수정할 수 없게 만들면 부작용을 원천 차단할 수 있다.
            // 생성자로만 값을 설정, 수정자(setter) 만들지 않음. or Integer 나 String을 사용하면 된다.
            Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());
            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setWorkPeriod(new Period(LocalDateTime.now()));
            // member2.setHomeAddress(address) or member.getHomeAddress()
            member2.setHomeAddress(copyAddress);

            em.persist(member1);
            em.persist(member2);

            // 불변 값을 통해 선언을 하였다.
            member1.getHomeAddress().setCity("newCity");

            em.flush();
            em.clear();

            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            // 역순으로 자원 해제
            em.close(); // 영속성 컨텍스트 종료
            emf.close();
        }
    }
}
