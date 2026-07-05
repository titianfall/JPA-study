package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{

            // 1차로 주문 id를 찾고, 그걸통해 또 탐색해야한다.
            Order order = em.find(Order.class, 1L);
            Long memberId = order.getMemberId();

            em.find(Member.class, memberId);
            // 객체지향 프로그램을 만들고있는데 전혀 객체지향적이지 않다.

            // Member findMember = order.getMember();
            // findMember.get...
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
