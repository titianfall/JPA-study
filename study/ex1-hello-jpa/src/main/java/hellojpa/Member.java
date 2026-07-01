package hellojpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// jpa 가 관리하는 entity로 인식 및 관리한다.
// 일반적으로 클래스 이름 = 테이블 이름 매핑
@Entity
public class Member {

    @Id
    private long id;
    private String name;

    // 기본 생성자가 필요하다. - 추후 학습
    public Member() {
    }

    public Member(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}
