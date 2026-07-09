package hellojpa;

import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaMain {

    public static void main(String[] args) {

        // resources/META-INF의 persistence.xml 설정을 읽고
        // EntityManager 객체를 생성하는 "hello" 이름의 싱글톤 EntityManagerFactory 객체를 생성합니다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street", "10000"));
            member.setWorkPeriod(new Period(LocalDateTime.now()));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressesHistory().add(new Address("old1", "street1", "20000"));
            member.getAddressesHistory().add(new Address("old2", "street2", "30000"));
            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("========================= START ==================");
            Member findMember = em.find(Member.class, member.getId());
            // 값 타입의 수정(update)
            Address address = findMember.getHomeAddress();
            findMember.setHomeAddress(new Address("newCity", address.getStreet(), address.getZipcode()));

            // 치킨 > 한식(delete(단일) > insert(신규)
            findMember.getFavoriteFoods().remove("치킨");
            findMember.getFavoriteFoods().add("한식");

            // 주소 수정( delete(전체) > insert(기존) > insert(신규)
            findMember.getAddressesHistory().remove(new Address("old1", "street1", "20000"));
            findMember.getAddressesHistory().add(new Address("newCity1", "street4", "40000"));

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
