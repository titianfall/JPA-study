package jpabook.jpashop.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.ManyToAny;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBER_ID") // 대소문자는 회사의 룰을 따르면 됨
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    // !!주인이 아니다.!! 읽기 전용으로 설정해야 한다.
    // 일대다 관계에서 양방향 관계를 설정하고자 할때 하는 과정이다.
    @ManyToOne
    @JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
    private Team team;

    @OneToOne
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;

    // 다대다 관계 실습 member - product
    // @ManyToMany
    // @JoinTable(name = "MEMEBER_PRODUCT") // 연결 테이블의 pk > fk 로 바꾸니느 구조
    // private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<MemberProduct> memberProducts = new ArrayList<>();

    public List<MemberProduct> getMemberProducts() {
        return memberProducts;
    }

    public void setMemberProducts(List<MemberProduct> memberProducts) {
        this.memberProducts = memberProducts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }
}
