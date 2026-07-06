package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS") // ORDER BY 예약어 주의
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ORDER_ID")
    private Long id;

    @ManyToOne // 주문 입장에서 고객은 한 명일 수 있다.
    @JoinColumn(name = "MEMBER_ID") // 조인되는 테이블의 실제 칼럼 이름
    private Member member;

    // 주문을 받았는데 어떤 주문 목록을 받았는지 궁금할 때
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems  = new ArrayList<>();

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING) // ordinal 사용 시 중간에 수정하면 전체 구조가 깨져버림을 기억
    private OrderStatus status;

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {return member;}
    public void setMember(Member member) {this.member = member;}

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
