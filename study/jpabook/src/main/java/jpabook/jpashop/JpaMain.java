package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            // 주문을 받았다고 가정한다.
            Order order = new Order();
            // order.addOrderItem(new OrderItem()); // setter 변형, 양방향 편의 메소드 작성
            em.persist(order);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            em.persist(orderItem);
            // 단방향도 사실 개발하는 데에는 큰 지장이 없다.
            // 그러나 JPQL을 작성하다 보면 단방향 관계 대신
            // 양방향 관계를 쿼리의 편리함을 위해 도입하는 경우가 많다.
            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }
}
