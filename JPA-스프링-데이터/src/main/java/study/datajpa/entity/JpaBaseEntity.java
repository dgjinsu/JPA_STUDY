package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass //컬럼을 자식으로 내리는 어노테이션
@Getter
public class JpaBaseEntity {
    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    // 처음 등록할 때 값 세팅
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updateDate = now;
    }

    // 수정할 때 값 세팅
    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now();
    }
}
