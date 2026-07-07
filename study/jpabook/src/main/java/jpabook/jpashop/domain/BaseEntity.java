package jpabook.jpashop.domain;

import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

// 해당 디렉토리는 다대다를 다대일, 일대다로 만들어놓은 디렉토리이기때문에 extends BaseEntity를 상속받으면 추가할수있다.
// 단, 다대다에서 만들어지는 중간 테이블은 생기지 않는다는 단점을 기억하면 좋다.
@MappedSuperclass
public class BaseEntity {
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
