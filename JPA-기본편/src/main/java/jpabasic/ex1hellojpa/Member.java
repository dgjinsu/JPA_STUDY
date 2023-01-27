package jpabasic.ex1hellojpa;

import javax.persistence.*;
import java.util.Date;

//@Entity
public class Member {
    @Id
    private Long id;
    @Column(name = "name")
    private String username;
    private Integer age;
    @Enumerated(EnumType.STRING)
    private RoleType roleType;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;
    @Lob  //큰 콘텐츠를 쓰고 싶을 때
    private String description;

}