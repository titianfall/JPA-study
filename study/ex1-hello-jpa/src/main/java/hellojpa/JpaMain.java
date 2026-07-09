package hellojpa;

import jakarta.persistence.*;
import org.hibernate.Hibernate;

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
            Parent parent = new Parent();
            parent.setName("Parent");

            Child child1 = new Child();
            child1.setName("Child1");

            Child child2 = new Child();
            child2.setName("Child2");

            parent.addChild(child1);
            parent.addChild(child2);

            // persist를 3번이나 하게된다. 이걸 줄일수 있는 방법이 영속성 전이: cascade를 사용하는 방법이다.
            em.persist(parent);
            // cascade = CascadeType.ALL로 인한 생략
//             em.persist(child1);
//             em.persist(child2);

            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());

            // 첫번째 자식을 없애본다. (orphanRemoval = true)
            // findParent.getChildList().remove(0);

            em.remove(findParent); // cascade시에 orphanRemoval = true 생략이 가능하다. 전파가 되기 때문
            findParent.getChildList().remove(0);

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
