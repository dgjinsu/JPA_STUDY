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

> 결론 : @Query같은 경우 영속성 컨텍스트를 거치지 않고 쿼리가 나가기 때문에 cascade 걸려있는 다른 엔티티들은 외래키 무결성 조건에 위반됨. 또한 무분별한 cascade.ALL 보단 REMOVDE를 먼저 사용하고 필요한 경우에만 ALL 로 바꿔주는게 좋을 것 같음 



### UserDetails 구현체 PrincipalDetails 필드에 Entity를 저장하면 안 되는 이유

```java
@Getter
public class PrincipalDetails implements UserDetails {

    private UserDto userDto;

    public PrincipalDetails(UserDto userDto) {
        this.userDto = userDto;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(userDto.getRole().toString())); //역할을 문자열로 바꾸어 return
    }

    @Override
    public String getPassword() {
        return userDto.getPassword();
    }

    @Override
    public String getUsername() {
        return userDto.getLoginId();
    }
}
```
* 위에는 현재 구현되어있는 PrincipalDetails 이다.
* 이 객체가 어디 쓰이는지부터 알아보자
    * 로그인 시 받은 Id와 Password 로 부터 UsernamePasswordAuthenticationToken 을 만들고 이를 통해 authentication 을 만든다. 밑은 코드이다
```java
UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginFormDto.getLoginId(), loginFormDto.getPassword());

Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
```
    * authenticate(authenticationToken) 함수가 실행되면 UserDetailsService 인터페이스를 구현하는 loadUserByUsername() 메서드를 호출하여 사용자 정보를 가져온다.
    * 이때 loadUserByUsername() 함수의 반환 객체는 UserDetails 인터페이스인데 이를 구현한게 PrincipalDetails 이다. 
    * 그런 후 authentication 을 이용해 만든 토큰을 클라이언트에 반환에 추후에 토큰을 통해 요청을 받을 수 있게 한다.

* 이전의 코드에선 UserDto 대신 UserEntity를 넣어두었다. 실제 많은 블로그, 강의에서 엔티티를 필드로 갖는다.
* 하지만 이는 보안상의 이유로 실무에선 **절대 사용하지 않는다**
* 가장 큰 이유는 보안상의 이유이다. User 엔티티의 경우 가족관계, 주민등록번호, 전화번호 등 여러 정보가 있을 수 있다. 이를 PrincipalDetails 에 저장하는건 보안상의 문제가 있다.
* 따라서 필요한 정보만 뽑아 저장하는 방식으로 로직을 짜야한다. 그래서 나는 DTO를 사용하는 방식으로 바꾸었다.


### JOIN
* INNER JOIN(내부 조인)은 두 테이블을 조인할 때, 두 테이블에 모두 지정한 열의 데이터가 있어야 한다.
    * jpql에선 일반 join하면 inner join이 된다.

![image](https://user-images.githubusercontent.com/97269799/232319081-b3383327-f78e-4627-8a91-f7b93be62700.png)


* OUTER JOIN(외부 조인)은 두 테이블을 조인할 때, 1개의 테이블에만 데이터가 있어도 결과가 나온다.
    * LEFT OUTER JOIN: 왼쪽 테이블의 모든 값이 출력되는 조인
    * RIGHT OUTER JOIN: 오른쪽 테이블의 모든 값이 출력되는 조인
    * FULL OUTER JOIN: 왼쪽 또는 오른쪽 테이블의 모든 값이 출력되는 조인

![image](https://user-images.githubusercontent.com/97269799/232319032-98166bca-092d-4a30-ab55-5c90abdac7bd.png)

* Join 시 행이 많은 테이블을 기준으로 데이터가 늘어난다.
    * 4행 join 11행 하면 최대 11행까지 늘어날 수 있음. (내부,외부 조인에서)
