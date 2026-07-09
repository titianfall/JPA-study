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

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setWorkPeriod(new Period(LocalDateTime.now()));
            member1.setHomeAddress(address);

            // 값 타입을 불변 객체로 선언하였는데, 실제로 값을 변경할 일이 생겼다고 가정하자.
            // 여러 방법이 있지만 새로운 객체를 만들어 reference를 갈아 끼우는것도 하나의 방법이다.
            Address newAddress = new Address("NewCity", address.getStreet(), address.getZipcode());
            member1.setHomeAddress(newAddress);

            em.persist(member1);

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
