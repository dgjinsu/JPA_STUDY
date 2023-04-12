# 링크라이브러리 프로젝트 진행하면서 오류 해결 or 어려웠던 점

### cascade.ALL 의 위험성1

![image](https://user-images.githubusercontent.com/97269799/231462995-04d5d8ca-7978-4e3d-9cb3-72e93394eab0.png)

* @OneToOne이나 @OneToMany에서 붙혀주는 영속성 전이 Cascade 때문에 일어난 문제

* 필드에 cascade = CascadeType.ALL을 붙혀주면 그 필드와 연관된 엔티티를 persist 해주지 않아도 persist한 효과가 나면서 영속성이 된다.

* 하지만 Cascade를 사용하면 편리하긴하지만 주의해야할 점이 있다. 두가지 조건을 만족해야 사용할 수 있다.
1.등록 삭제 등 라이프 사이클이 똑같을 때

2.단일 엔티티에 완전히 종속적일때만 사용 가능하다. 


### cascade.ALL 의 위험성2
```java

public class User extends BaseEntity{
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Category> categories = new ArrayList<>();
}
```

```java
public class Category {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();
}
```

```java
public class Post extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
```

```java
    @Modifying
    @Query("delete from Category c where c.id=:id")
    void deleteById(@Param("id") Long id);
```
* @Modifying @Query 를 사용하면 영속성 컨텍스트를 거치지 않고 바로 데이터를 조작한다. 따라서 delete 쿼리가 나가고 category가 삭제된다.
* 하지만 이 경우 category와 견결된 post들은 삭제되지 않기 때문에 참조 외래키 무결성 조건이 위반되며 에러가 난다.
* 에러메시지
 ```java
 Request processing failed; nested exception is org.springframework.dao.DataIntegrityViolationException: could not execute statement; SQL [n/a]; constraint [\"FKG6L1YDP1PWKMYJ166TEIUOV1B: PUBLIC.POST FOREIGN KEY(CATEGORY_ID) REFERENCES PUBLIC.CATEGORY(CATEGORY_ID) (CAST(4 AS BIGINT))\"; SQL statement:\ndelete from category where category_id=? [23503-214]]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement
```

