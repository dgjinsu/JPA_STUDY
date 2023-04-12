# 링크라이브러리 프로젝트 진행하면서 오류 해결 or 어려웠던 점

### cascade.ALL 의 위험성1

```java

public class User extends BaseEntity{

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ProfileImg profileImg;

}
```

```java
public class ProfileImg {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;
}
```

* 위 같은 코드가 있을 때 ProfileImg 를 삭제해도 삭제되지 않는다
* 왜냐? cascade의 Type을 ALL로 걸어놨기 때문이다
* ALL은 REMOVE와 PERSISTENCE를 동시에 진행한다. 따라서 ProfileImg를 삭제하면 User에 걸려있는 cascade 때문에 다시 영속화 된다.
* 결과적으로 트랜잭션이 끝나고 flush 될 때 바뀐 사항이 없기 때문에 query가 나가지 않는다


> 이 같은 문제로 하나의 오류가 더 발생했다.

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

* 이 코드에서 보면 Post의 부모는 Category 와 user인것을 알수있다.
* 이때 post와 연결된 category를 삭제하면 post도 삭제될것이다. 하지만 문제는 user와도 연결이 되어있다는 것이다.
* user는 cascade.ALL로 되어 있으니 삭제된 post를 다시 영속화 한다.
* 따라서 category 쿼리는실행되지 않는다.
* 이를 해결하기 위해 구글링한 결과 아래 코드를 repo에 추가했다.

```java
    @Modifying
    @Query("delete from Category c where c.id=:id")
    void deleteById(@Param("id") Long id);
```
* @Modifying @Query 를 사용하면 영속성 컨텍스트를 거치지 않고 바로 데이터를 조작한다. 따라서 delete 쿼리가 나가고 category가 삭제된다.
* 하지만 이 경우 category와 견결된 post들은 삭제되지 않기 때문에 참조 외래키 무결성 조건이 위반되며 에러가 난다.

--------------------------------------------
* 1번 같은 문제를 해결하기 위해선 cascade.ALL 을 REMOVE 로 바꿔주어야 한다.
* 2번도 마찬가지로 전부 REMOVE로 바꿔주어야 한다.
* 여기서 많이 해맸는데 REMOVE 로 바꿔주고 @Modyfing @Query 문은 그대로 나두었기 때문에 계속 오류가 났다.
* REMOVE 로 전부 설정 했기 때문에 쿼리가 정상적으로 실행되지만 
