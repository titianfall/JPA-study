package hellojpa;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    // 주소
    @Embedded
    private Address homeAddress;

    // ElementCollection 의 fetch default는 FetchType.LAZY 이다.
    // 선호 음식
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "FAVORITE_FOOD",  joinColumns = {
            @JoinColumn(name = "MEMBER_ID") // 왜래키로 인식
    })
    @Column(name = "FOOD_NAME") // 예외적으로 가능 - column 이 하나이기 때문
    private Set<String> favoriteFoods = new HashSet<>();

    // 집 주소들 - 값 타입 매핑
//    @ElementCollection(fetch = FetchType.LAZY)
//    @CollectionTable(name = "ADDRESS", joinColumns = {
//            @JoinColumn(name = "MEMBER_ID") // 왜래키로 인식
//    })
//    private List<Address> addressesHistory = new ArrayList<>();

    // 집 주소들 - entity 매핑
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID")
    private List<AddressEntity> addressHistory = new ArrayList<>();

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

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Set<String> getFavoriteFoods() {
        return favoriteFoods;
    }

    public void setFavoriteFoods(Set<String> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
    }
}
