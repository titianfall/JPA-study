package jpabook.jpashop.domain;

import jakarta.persistence.*;

@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBER_ID") // 대소문자는 회사의 룰을 따르면 됨
    private Long id;

    @Column(name = "USERNAME")
    private String username;

//    @Column(name = "TEAM_ID")
//    private Long teamId;

    // 자바 문법으로는 완전한 코드이지만, 런타인 에러이다.
    // JpaMain > Hibernate mapping data 만드는 순간 터짐
    @ManyToOne // Member N : Team 1
    @JoinColumn(name = "TEAM_ID") // 매핑할 FK column
    // fetch = FetchType.EAGER // Member를 가져올떄 Team도 즉시 채워라
    private Team team;

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
}
