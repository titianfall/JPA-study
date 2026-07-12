package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Member;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            Book book = new Book();
            book.setName("JPA");
            book.setAuthor("김영한");

            em.persist(book);

            em.flush();
            em.clear();
            System.out.println("===================START=======================");
            // Inheritance(strategy = InheritanceType.SINGLE_TABLE) 전략으로 만들었기 때문에
            // DTYPE = book 이고 ITEM 테이블에서 조회를 하게된다.
            Book findBook = em.find(Book.class, book.getId());
            System.out.println("findBook.getName() = " + findBook.getName());
            System.out.println("findBook.getAuthor() = " + findBook.getAuthor());

            em.remove(findBook);

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
