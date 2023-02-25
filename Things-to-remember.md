## 영속성 컨텍스트

* 엔티티를 영구 저장하는 환경
* 엔티티 매니저를 통해 접근

 **영속성 컨텍스트의 이점**

* 1차 캐시
  * 조회 시 영속 컨텍스트안에서 1차 캐시를 조회 후 해당 엔티티가 있을 경우 캐시를 조회 해 온다. 엔티티가 없을 경우 데이터베이스에서 조회 해 온다.
  * 데이터베이스 트랜잭션 내부에서 만들고 종료

* 동일성 보장
  * 자바 컬렉션에서 값을 가져오는 것 처럼 동일성을 보장한다

* 쓰기 지연 
  * 쓰기 지연 SQL 저장소에 SQL을 모아뒀다가 commit() 시점에 쿼리들을 실행
* 변경 감지(dirty-checking)
  * 1차 캐시안에는 `@Id`, `Entity` , `스냅샷` 이 있다. 여기서 스냅샷 은 최초로 영속성 컨텍스트(1차캐시)에 들어오는순간 스냅샷을 찍어서 저장해둔다.
  * JPA는 트랜잭션이 커밋(commit)되는 순간 엔티티와 스냅샷을 모두 비교한다. 
  * 변경된 것이 있을 경우 쓰기지연 SQL 저장소 에 업데이트 쿼리를 저장하고 수행하게 된다. 

### 플러시

영속성 컨텍스트를 플러시 하는 방법
1. em.flush()  : 직접 호출
2. 트랜잭션 커밋: 플러시 자동 호출
3. JPQL 쿼리 실행: 플러시 자동 호출

## 엔티티 매핑, 연관관계

### @Enumerated

* 자바 Enum 타입을 매핑할 때 사용
* ORDINAL 타입을 사용하지 말자.
    → enum타입이 추가,변경,삭제 되어 순서가 달라질 경우 사이드 이펙트가 생긴다. 
* EnumType.ORDINAL: ENUM 순서를 데이터베이스에 저장
* EnumType.STRING: ENUM 이름을 데이터베이스에 저장


### 연관관계 주인

* 양방향 매핑 규칙
  * 객체의 두 관계중 하나를 연관관계의 주인으로 지정
  * 연관관계의 주인만이 외래 키를 관리(등록, 수정)
  * 주인이 아닌쪽은 읽기만 가능
    * 주인이 아닌 쪽에서 값을 넣어도 반영되지 않음
  * 주인이 아니면 mappedBy속성으로 주인을 지정한다. 
  * 외래 키가 있는 곳을 주인으로 지
  * 양방향일시 연관관계 편의 메서드를 작성할 것

### 다대일
* 외래 키가 있는 쪽이 연관관계의 주인

### 일대다
일(One)이 연관관계의 주인이다  →  권장하는 방법은 아니다 실무에서도 거의 사용되지 않음. 
> 결론: 기본은 다대일(N:1)로 구현하다 필요에 의해 양방향 다대일(N:1) 관계를 수립하도록 하자.

### 일대일
* 주 테이블이나 대상 테이블 중에 외래 키 선택 가능
* 외래키에 데이터베이스 유니크 제약조건 추가

### 상속관계 매핑
1. 조인 전략
![image](https://user-images.githubusercontent.com/97269799/219606796-1d1e6aba-4f02-40a9-ac39-828ca89fa591.png)
* 장점
  * 정규화도 되어있고, 제약조건을 부모에 걸어 맞출 수 있다.
* 단점
  * 조회시 조인이 많을 경우 성능 저하
  * 조회 쿼리가 복잡함.
2. 단일 테이블 전략
![image](https://user-images.githubusercontent.com/97269799/219607205-c39e7dc5-a605-46bc-8445-337ed41de639.png)
* 논리모델을 한 테이블로 합쳐버리는 방법.
* 한 테이블에 다 넣어 놓고 어떤 테이블인지 구분하는 컬럼(ex:DTYPE)을 통해 구분한다.

* 장점
  * 조인이 필요 없기에 일반적으로 조회 성능이 빠름
  * 조회 쿼리가 단순함.
* 단점
  * 자식 엔티티가 매핑한 컬럼은 모두 nullable 해야 한다.
  * 단일 테이블에 모든 것을 저장하기에 테이블이 커질 수 있고 상황에 따라서
  * 조회성능이 더 느려질 수 있다.

#### 주요 어노테이션
`@inheritance` strategy = JOINED(조인전략), SINGLE_TABLE(단일테이블)

`@DiscriminatorColumn` name = ~~   컬럼명 (DTYPE 이 기본)

`@DiscriminatorValue` value = ~~   DTYPE 에 들어갈때 이름 설


### `@MappedSuperclass`
* 상속관계 매핑이 아니다.
* 엔티티도 아니고, 테이블과 매핑되지도 않는다.
* 부모 클래스를 상속받는 자식 클래스에 매핑 정보만 제공 -> 컬럼만 내려주는 역할
* 직접 생성해서 사용할 일이 없으므로 추상클래스 추천

## 즉시로딩, 지연로딩

### 프록시
* 실제 클래스를 상속받아서 만들어짐
* 실제 클래스와 겉 모양이 같다.
* 프록시는 처음 사용할 때 한 번만 초기화
* 프록시 객체를 초기화 할 때 프록시 객체가 실제 엔티티로 바뀌는 것은 아님, 초기화되면 프록시 객체를 통해 실제 엔티티에 접근 가능

### 지연로딩
> fetch = FetchType.LAZY
* 연관관계에 있는 다른 엔티티를 사용하는 빈도수가 낮을 경우 지연로딩을 사용해 불필요한 엔티티 조회를 막을 수 있다.
* 엔티티를 가져올 때 연관관계에 해당하는 엔티티는 프록시로 가져옴. 실제 사용할때 select 쿼리

### 즉시로딩
> fetch = FetchType.EAGER
* Member를 가져오는 시점에서 연관관계에 있는 Team까지 바로 가져오는 것을 즉시 로딩이라 한다. 
* 즉시로딩은 JPQL 에서 `N + 1 의 문제` 를 일으킨다.

#### 결론
1. 모든 연관관계에 지연 로딩을 사용하자.
2. 실무에서 즉시 로딩을 사용하지 마라.
3. JPQL fetch 조인이나, 엔티티 그래프 기능을 사용해라.


### 영속성 전이와 고아객체
* `parent` 만 persist 하면 `child` 도 전부 persist 
* CASCADE의 종류
 * ALL: 모두 적용(모든 곳에서 맞춰야 하면 해당 옵션)
 * PERSIST: 영속(저장할 때만 사용 할 것이면 해당 옵션)
 * REMOVE: 삭제
* 사용해야 할 때
 * 라이프 사이클이 동일할 때
 * 단일 소유자 관계일 때 


## 기본값 타입
> 스프링에선 크게 `엔티티 타입` 과 `값 타입` 으로 나뉜다

### 임베디드 타입

* 새로운 값 타입을 직접 정의할 수 있다.
* 임베디드 타입 사용법
 * @Embeddable: 값 타입을 정의하는 곳에 표시
 * @Embedded: 값타입을 사용하는 곳에 표시
 * 기본 생성자 필수
 * `장점`
 1. 재사용
   Period나 Address는 다른 객체에서도 사용 할 수 있어 재사용성을 높힌다.
 2. 높은 응집도
 3. Period.isWork()처럼 해당 값 타입만 사용하는 의미있는 메소드를 만들 수 있음.
* 임베디드 타입을 통해 `객체를 분리`하더라도 테이블은 `하나만 매핑`된다.
![image](https://user-images.githubusercontent.com/97269799/219611625-858a236b-50ee-4d2a-a25a-6e8cf1b97d0a.png)

#### 임베디드 타입 사용 시 주의점
1. 임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 위험하다.
 * Address address = new Address("a", "a", "a") 를 만들고 이 address 를 여러곳에서 공유하면 안 됨. address 값을 바꾸면 공유한 모든 곳의 값이   
2. 값(인스턴스)을 복사해서 사용한다.
3. 값 타입은 불변 객체(immutable object)로 설계해야 한다. == `setter` 를 없애는 것
 * 불변 객체: 생성 시점 이후 절대 값을 변경할 수 없는 객체
4. 임베디드 타입은 객체이기 때문에 `동일성 비교`시 false 가 뜬다. `동등성 비교`를 해야함 -> equals() 를 사용



## 엔티티 설계시 주의점
* 엔티티에서는 가급적 Setter를 사용하지 말자.
 * Setter가 열려있으면 변경 포인트가 너무 많아 유지보수가 어렵다.
* 모든 연관관계는 지연로딩으로 설정
 * `ManyToOne`이나  `OneToOne`과 같은 xxxToOne 매핑은 기본 전략이 EAGER이므로 수동으로 LAZE로 바꾸도록 해야 한다. 
* 컬렉션은 필드에서 초기화 하자.
* 비즈니스 로직은 엔티티 안에 있는것이 응집도를 높힐 수 있다.(= 객체지향 설계) (ex 상품을 샀다면 재고도 감소하게 엔티티에 로직 작성) 이를 `도메인 모델 패턴` 이라 한다.

### 서비스 계층 개발시
* 클래스단에 `@Transactional(readOnly = true)` 을 걸어주고 필요한 곳에 따로 @Transactional 을 써줌 

### 테스트시
* 예외 발생 여부를 테스트하고싶을때는 @Test annotation의 expected = Exception.class 속성을 사용하면 된다.
   → @Test(expected = IllegalStateException.class)
   
   
## 변경 감지와 병합
1. 엔티티를 변경할 때는 항상 변경 감지를 사용하도록 하자.
2. 컨트롤러에서 어설프게 엔티티를 생성하지 마세요.
3. 트랜잭션이 있는 서비스 계층에 식별자와 변경할 데이터를 명확하게 전달하자.(파라미터 or DTO)
4. 트랜잭션 커밋 시점에 변경감지가 실행됩니다.

## 컨트롤러에서 주의사항
* 커맨드성 로직들은 Controller에서는 식별자만 넘겨주고, Service단에서 핵심 로직들을 수행하는게 좋다.
* 트랜잭션 내부에서 관리되고 엔티티들이 영속상태인데, Controller단에서 **엔티티를 파라미터로 받으면, 해당 엔티티는 준영속 상태**가 되기때문에 부작용(sideEffect)를 유도할 수 있다.

## API 개발

### return 할 때
아래와 같이 한번 감싸주어 반환해야 함. 이렇게 하면 어레이를 반환할 때 다른 데이터도 함께 반환이 되고 향후 필요한 필드를 추가할 수 있다. 
``` java
public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
 ```

## xToOne 에서의 조회

### 엔티티 반환
* 문제점
 * 엔티티가 변경되면 API의 스펙이 변한다. 
 * 엔티티에 API 검증을 위한 로직이 들어간다.(ex: @NotEmpty ...) 


### DTO 반환
* 엔티티와 API 스펙을 명확하게 분리할 수 있다.
* 엔티티가 변경되어도 API스펙이 변경되지 않는다. 

* 정리 → 실무에서는 API스펙에 엔티티가 노출되어서는 안된다.  
* 그렇기 때문에 각각에 API에 맞는 DTO를 만들어서 엔티티와 분리시키는게 중요하다.
* 엔티티를 그대로 쓸 경우의 장점은 아주 조금 간편해진다는 것 뿐이다. 

### 엔티티를 DTO 로 변환
```java
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> collect = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return collect;

    }
    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }
    }
```


**쿼리 방식 선택 권장 순서**
1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.
2. 필요하면 fetch join으로 성능을 최적화 한다. → 대부분의 성능 이슈 해결
3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.

## OneToMany 에서의 조회

* oneToMany관계일 때 `distinct`를 사용하지 않으면 row수가 ONE의 갯수가 아닌 Many의 갯수만큼 증가한다. 
* 그 결과 위 코드에서 distinct를 빼면 Order 엔티티의 조회 수도 증가하게 된다. 
* JPQL에서 distinct를 사용하게 되면 SQL에 distinct를 추가하고, 더하여 애플리케이션에서 `같은 엔티티중복을 걸러준다`. 
* 이로써 컬렉션 페치 조인 때문에 중복 조회 되는것을 막을 수 있다. 
* 하지만.... DB에선 불가능

따라서 페이징시 데이터를 메모리에 다 끌고와 페이징을 한다. 데이터가 커지면 out of memory 예외 발생

### 한계돌파
* ToOne(OneToOne, ManyToOne) 관계를 모두 페치조인 한다. ToOne관계는 row수를 증가시키지 않으므로 페이징 쿼리에 영향을 주지 않는다.
* **컬렉션은 지연 로딩**으로 조회한다.
* 지연 로딩 성능 최적화를 위해 hibernate.default_batch_fetch_sieze , @BatchSize 를 적용한다.
 * hibernate.default_batch_fetch_size: 글로벌 설정
 * @BatchSize: 개별 최적화
* 이 옵션을 사용하면 컬렉션이나, 프록시 객체를 한꺼번에 설정한 size만큼 IN쿼리로 조회한다.

**쿼리 방식 선택 권장 순서**
1. 엔티티 조회 방식으로 우선 접근
 a. 페치조인으로 쿼리 수를 최적화
 b. 컬렉션 최적화
  * 페이징 필요 hibernate.default_batch_fetch_size , @BatchSize 로 최적화
  * 페이징 필요 X → 페치 조인 사용
2. 엔티티 조회 방식으로 해결이 안되면 DTO조회 방식 사용


## 스프링 데이터

```java
@Query("select m from Member m where m.username= :username and m.age = :age" ) 
List<Member> findUser(@Param("username") String username, @Param("age") int age);
```

* @Query 어노테이션을 사용한다.
* JPA Named 쿼리처럼 애플리케이션 실행 시점에 문법 오류를 발견할 수 있다.


* DTO에 직접 매핑 해주려면 new 명령어를 사용해야 하고, 패키지경로를 모두 작성해줘야 한다.
```java
@Query("select new study.datajpa.repository.MemberDto(m.id, m.username, t.name)" +
       " from Member m join m.team t")
List<MemberDto> findMemberDto();
```

* 컬랙션 파라미터 바인딩도 가능하다.
```java
@Query("select m from Member m where m.username in :names")
List<Member> findByNames(@Param("names") List<String> names);
```

* 벌크성 수정, 삭제 쿼리는 @Modifying 어노테이션을 사용해야 한다.

* @EntityGraph (findAll 같이 메서드 명으로 쿼리를 끝낼때 사용학 편리하다) 
```java
@Override
@EntityGraph(attributePaths = {"team"})
List<Member> findAll();
```

### Auditing
> 보통 솔루션 운용을 할 때 엔티티 생성, 변경할 때 생성(변경)한 시간과 사람이 누군지에 대한 기록은 다 남기는 것이 좋다.

```java
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class JpaBaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;
}
```
* @EntityListeners(AuditingEntityListener.class) 는 엔티티에 적용
* → 스프링 부트 설정 클래스에 @EnableJpaAuditing 을 적용한다.


### 페이징과 정렬

```java
@RequestMapping(value = "/members_page", method = RequestMethod.GET) 
public String list(@PageableDefault(size = 12, sort = “username”,direction = Sort.Direction.DESC) Pageable pageable) {
  ...
}
```


## Querydsl

* JPQL 이 제공하는 모든 검색 조건을 제공
- member.username.eq("a") : username = 'a'
- member.username.ne("a") : username ≠ 'a'
- member.username.eq("a").not() : username ≠ 'a'
- member.username.isNotNull() : username is not null
- member.age.in(10,20) : age in (10,20)
- member.age.notIn(10,20) : age not in(10,20)
- member.age.between(10,30) : age between 10, 30
- member.age.goe(30) : age ≥ 30
- member.age.gt(30) : age > 30
- member.age.loe(30) : age ≤ 30
- member.age.lt(30) : age < 30
- member.username.like("member%") : username like 'member%'
- member.username.contains("member') : username like '%member%'
- member.username.startsWith("member") : like 'member%' 


### 정렬
회원 정렬 순서 예
 * 1. 회원 나이 내림차순(desc)
 * 2. 회원 이름 올림차순(asc)
 * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
```java
orderBy(member.age.desc(), member.username.asc().nullsLast())
```

### 집합
* JPQL이 제공하는 모든 집합 함수를 Querydsl 에서 제공한다.

```java
List<Tuple> result = queryFactory
         .select(
                 member.count(), //회원수
                 member.age.sum(),//나이 합
                 member.age.avg(),//나이 평균
                 member.age.max(),//최대 나이
                 member.age.min()//최소 나이
         )
         .from(member)
         .fetch();

 Tuple tuple = result.get(0);
 assertThat(tuple.get(member.count())).isEqualTo(4);
 assertThat(tuple.get(member.age.sum())).isEqualTo(100);
 assertThat(tuple.get(member.age.avg())).isEqualTo(25);
 assertThat(tuple.get(member.age.max())).isEqualTo(40);
 assertThat(tuple.get(member.age.min())).isEqualTo(10);
```

### JOIN

* 일반 조인
```java
.select(team.name, member.age)
            .from(member)
            .join(member.team, team)
```

* 세타 조인
```java
  .select(member, team)
            .from(member)
            .leftjoin(member.team, team).on(team.name.eq("teamA"))
            .fetch();
```


### 서브쿼리
* `JPAExpressions` 를 사용하여 서브쿼리 사용 가능

ex) 나이가 가장 많은 회원 조회
```java
.selectFrom(member)
            .where(member.age.eq(JPAExpressions
                    .select(memberSub.age.max())
                    .from(memberSub)
            ))
            .fetch();
```

### CASE 문
```java
    List<String> result = queryFactory
            .select(member.age
                    .when(10).then("열살")
                    .when(20).then("스무살")
                    .otherwise("기타")
            )
            .from(member)
            .fetch();
```

## Querydsl 에서 DTO 조회

* 결과를 DTO반환할 때 사용하며 4가지 방법이 존재한다.
1. 프로퍼티 접근
2. 필드 직접 접근
3. 생성자 사용
4. QueryProjection   (이 방법 권장)

### distinct 사용
```java
    List<String> result = queryFactory
            .select(member.username).distinct()
```



### 동적 쿼리 해결

1. BooleanBuilder 
2. Where 다중 파라미터 사용 (이 방법 권장)

```java
private List<Member> searchMember2(String usernameCond, Integer ageCond) {
    return queryFactory
            .selectFrom(member)
            .where(usernameEq(usernameCond), ageEq(ageCond))
            .fetch();
}

private BooleanExpression usernameEq(String usernameCond) {
    return usernameCond != null ? member.username.eq(usernameCond):null;
}

private BooleanExpression ageEq(Integer ageCond) {
    return ageCond != null ? member.age.eq(ageCond):null;
}
```

### 수정, 삭제 벌크연산

* `.fetch()` 대신 `.execute()` 사용
```java
long count = queryFactory
        .update(member)
        .set(member.username, "비회원")
        .where(member.age.lt(28))
        .execute();
em.flush();
em.clear();
```
* JPQL 배치와 마찬가지로, 영속성 컨텍스트에 있는 엔티티를 무시하고 실행되기 때문에 배치 쿼리를 실행하고 나면 영속성 컨텍스트를 초기화 하는 것이 안전하다.



### 사용자 정의 Repository
1. 사용자 정의 인터페이스 작성
2. 사용자 정의 인터페이스 구현
3. 스프링 데이터 리포지토리에 사용자 정의 인터페이스 상속


### Querydsl 페이징 연동

```java
public Page<MemberDto> findMemberBySearchCond(MemberSearchCond memberSearchCond, Pageable pageable) {

   List<MemberDto> result = query
           .select(new QMemberDto(member.name, member.email, member.age))
           .from(member)
           .where(
                   nameEq(memberSearchCond.getName()),
                   AgeGoe(memberSearchCond.getAge()))
           .offset(pageable.getOffset())
           .limit(pageable.getPageSize())
           .fetch();
   return new PageImpl<>(result, pageable, result.size());
}
```



---------------------------------------------------------------------------------------------
## API 문서툴
* Swagger 사용

1. Gradle 추가
implementation 'io.springfox:springfox-boot-starter:3.0.0'
implementation 'io.springfox:springfox-swagger-ui:3.0.0'


2. 설정 클래스 (스프링 @Configuration 어노테이션 사용)
* '.basePackage()' 부분에 Swagger를 적용할 패키지 지정. 
* 해당 패키지 이하의 모든 rest api가 자동으로 swagger 문서로 생성됨. (이하 예시에서는 'com.nahwasa.iot.controller' 패키지 이하를 모두 적용)
```java
@Configuration
@EnableWebMvc
public class SwaggerConfig {

    private ApiInfo swaggerInfo() {
        return new ApiInfoBuilder().title("IoT API")
                .description("IoT API Docs").build();
    }

    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .consumes(getConsumeContentTypes())
                .produces(getProduceContentTypes())
                .apiInfo(swaggerInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.nahwasa.iot.controller"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false);
    }

    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json;charset=UTF-8");
        consumes.add("application/x-www-form-urlencoded");
        return consumes;
    }

    private Set<String> getProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add("application/json;charset=UTF-8");
        return produces;
    }
}
```
* Swagger 문서로 만들고 싶지 않을 경우 (예를들어 테스트용 컨트롤러나 api 작업자에게 보이기 싫은 api 등) 해당 컨트롤러에 @ApiIgnore 어노테이션을 추가하여 제외시킬 수 있음.
* @ApiOperation(value="멤버 등록", notes="멤버를 등록하고 홈 화면으로 리다이랙트")
![image](https://user-images.githubusercontent.com/97269799/220830085-bdfab477-9fce-452e-8e0c-6e93cff3cfc6.png)

---------------------------------------------------------------------------------------------
## GIT 협업
1. 팀장이 저장소 만들고 팀원 초대
2. 브랜치 만들기
3. 작업 시작 전 메인 브랜치 내용을 pull 하기 (중요)
4. 각자 브랜치에 push 하기
5. 메인 브랜치에 pull request 하기
6. 확인이 되면 merge 하기

## COMMIT 작성법

* type: tytle 형식
* ex) feat: 회원가입 구현, refactor: 필요 없는 코드 제거
![image](https://user-images.githubusercontent.com/97269799/221097787-e3e4bcd8-9b9c-4c89-83c0-895c50368d96.png)

