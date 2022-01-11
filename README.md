# JPA

# v1.09 1/11
## 객체지향 쿼리(JPQL)
- JPA는 JPQL, JPA Criteria, QueryDSL, 네이티브 SQL, JDBC API 직접 사용, MyBatis, SpringJdbcTemplate 를 지원
### JPQL
- JPA를 사용하면 엔티티 객체를 중심으로 개발
- 검색 할 때도 테이블이 아닌 엔티티 객체를 대삼으로 검색
- 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
- 애플리케이션이 필요한 데이터만 불러오려면 결국 검색 조건이 포함된 SQL이 필요
- JPA는 SQL을 추상화한 JPQL 이라는 객체 지향 쿼리 언어를 제공
- SQL 문법과 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원

      List<MappingMember> resultList = 
      em.createQuery("select m from MappingMember m where m.name like '%member%'"
                            ,MappingMember.class)
                    .getResultList();

### JPA Criteria
- 문자가 아닌 자바코드로 JPQL 작성
- JPA 공식 기능
- 단점 : 복잡, 실용성이 없음

### QueryDSL
- 문자가 아닌 자바코드로 JPQL 작성
- 컴파일 시점에 문법 오류 확인 가능
- 동적 쿼리 작성 편리
- 실무 사용 권장
      
      JPAFactoryQuery query = new JPAQueryFactory(em);
      MappingMember m = MappingMember.member; 
      List<MappingMember> list = 
      query.selectFrom(m)
      .where(m.name.gt(18)) 
      .orderBy(m.name.desc())
      .fetch();
      
 ### JPQL 기본 문법 및 기능
 ![image](https://user-images.githubusercontent.com/96407257/148914514-b7f4f7cb-4d7b-4b50-92d5-dcc4e7af986f.png)
 - 엔티티와 속성은 대소문자 구분 O
 - JPQL 키워드는 대소문자 구분 X(SELECT,FROM,where)
 - 엔티티 이름 사용, 테이블 이름 사용 X
 - 별칭은 필수(as는 생략 가능)

### TypeQuery, Query
- TypeQuery : 반환 타입이 명확할 떄 사용  
      
      TypedQuery<Member> query = 
          em.createQuery("SELECT m FROM Member m", Member.class);  

- Query : 반환 타입이 명확하지 않을 때 사용  
      
      Query query = 
          em.createQuery("SELECT m.username, m.age from Member m");  

### 결과 조회 API
- query.getResultList(): 결과가 하나 이상일 때, 리스트 반환(결과가 없으면 빈 리스트 반환)
- query.getSingleResult() : 결과가 정확히 하나, 단일 객체 반환(결과가 없으면 Exeption)

### 파라미터 바인딩
- 이름기준

      SELECT m FROM Member m where m.username=:username 
      query.setParameter("username", usernameParam);

- 위치 기준

      SELECT m FROM Member m where m.username=?1 
      query.setParameter(1, usernameParam);
      
## 프로젝션
- SELECT 절에 조회할 대상을 지정하는 것
- select m from Member m -> 엔티티 프로젝션
- select m.team from Member m -> 엔티티 프로젝션(or select t from Member m join m.team t)
- select m.address from Member m -> 임베디드 타입 프로젝션
- select m.name, m.age from Member m -> 스칼라 타입 프로젝션

### 프로젝션 - 여러 값 조회
- select m.name, m.age from Member m
- Query 타입으로 조회
- Object[] 타입으로 조회
- new 명령어로 조회
      
      Member member = new Member();
      member.setUsername("member1");
      member.setAge(10);
      em.persist(member);
      
      List<MemberDTO> resultList1 = em.createQuery("select new jpa.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();
                    
      
      public class MemberDTO {

      private String username;
      private int age;

      public MemberDTO(String name, int age) {
          this.username = name;
          this.age = age;
          }
      ....
      }
      
- 단순 값을 DTO로 조회
- 패키지 명을 포함한 전체 클래스 명 입력
- 순서와 타입이 일치하는 생성자 필요

## 페이징
- setFirstResult(int startPosition) : 조회 시작 위치(0부터 시작)
- setMaxResults(int maxResult) : 조회할 데이터 수

      List<MemberDTO> resultList1 = em.createQuery("select new jpa.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .setFirstResult(0)
                    .setMaxResults(100)
                    .getResultList();

## 조인
- 내부 조인 : A테이블과 B테이블의 교집합, select m from Member m [INNER] join m.team t
- 외부 조인 : A테이블 기준 B테이블의 합집합, select m from Member m left[OUTER] join m.team t
- 세타 조인 : 조인 조건을 적용한 조인, select count from Member m team t where m.username = t.name

#### 조인 ON 절
- 조인 대상 필터링
 
      String query = "select m from MappingMember m left join m.team t on t.name='teamA'";
      List<MappingMember> resultList2 = em.createQuery(query, MappingMember.class)
              .getResultList();
      
- 연관관계 없는 엔티티 외부 조인  
(MappingMember와 MappingTeam이 연관관계가 아니라는 가정)
      
      String query1 = "select m from MappingMember m left join MappingTeam t on m.name = t.name";
      List<MappingMember> resultList3 = em.createQuery(query1, MappingMember.class)
              .getResultList();

### 서브 쿼리
- 쿼리 내부에 또 다른 쿼리를 만드는 방법
- [NOT] EXISTS (subquery) : 서브쿼리에 결과가 존재하면 참

      //팀A 소속인 회원
      select m from Member m where exists (select t from m.team t where t.name ='teamA')
      
- {ALL|ANY|SOME| (subquery) : ALL(모두 만족하면 참), ANY,SOME(조건을 하나라도 만족하면 참)

      //전체 상품 각각의 재고보다 주문량이 많은 주문들
      select o from Order o where o.orderAmount > ALL(select p.stockAount from product p)

- [NOT] IN (subquery) : 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참

      //어떤 팀이든 팀에 소속된 회원
      select m from Member m where m.team = ANY(select t from Team t)
      
#### 서브 쿼리 한계
- JPA는 where, having 절에서만 서브 쿼리 사용 가능
- select 절도 가능(하이버네이트에서 지원)
- from 절의 서브 쿼리는 불가능 -> 조인으로 풀 수 있으면 풀어서 해결
# v1.08 1/10
## 값 타입
### 값 타입 분류
- 기본 값 타입 : 자바 기본 타입(int, double), 래퍼 클래스(Integer, Long), String
- 임베디드 타입
- 컬렉션 값 타입

### 기본 값 타입
- 생명주기를 엔티티에 의존
- 값 타입은 공유X
- 기본 타입은 항상 값을 복사함
- Integer, String 같은 경우 공유 가능한 객체지만 변경 X

### 임베디드 타입
- 새로운 값 타입을 직접 정의
- JPA는 임베디드 타입
- 주로 기본 값 타입을 모아서 복합 값 타입이라고도 함
- 예시
![image](https://user-images.githubusercontent.com/96407257/148741628-85c70461-6ec2-4280-a0ea-f80e81937620.png)
- @Embeddable : 값 타입을 정의하는 곳에 표시
- @Embedded : 값 타입을 사용하는 곳에 표시
- 기본 생성자 필수

      @Embeddable
      public class Address {
          private String city;
          private String street;
          private String  zipcode;
       ....
       }
       
       @Embeddable
       public class Period {
          private LocalDateTime startDate;
          private LocalDateTime endDate;
       ....
       }
       
       @Entity
       public class Member {

       ....
          @Embedded
          private Address homeAddress;
          @Embedded
          private Period workPeriod;
       ....
       }
### 임베디드 타입의 장점
- 재사용, 높은 응집도
- 값 타입만 사용하는 의미 있는 메소드 생성 가능
- 임베디드 타입을 포함한 모든 값 타입은 값타입을 소유한 엔티티에 생명주기를 의존
- 임베디드 타입을 사용하기 전 후에 매핑하는 테이블은 같음
- 객테와 테이블을 세밀하게 매핑 가능

### @AttributeOverride 속성
- 한 엔티티에서 값은 값 타입 사용 시 컬럼이 중복
- @AttributeOverrides, @AttributeOverride를 사용하여 컬러 명 속성 재정의
      @Entity
       public class Member {

       ....
          @Embedded
          private Address homeAddress;
          @Embedded
          private Period workPeriod;
          @Embedded
          @AttributeOverrides({
                  @AttributeOverride(name = "city",column = @Column(name = "WORK_CITY")),
                  @AttributeOverride(name = "street",column = @Column(name = "WORK_STREET")),
                  @AttributeOverride(name = "zipcode",column = @Column(name = "WORK_ZIPCODE"))
          })
          private Address workAddress;
       ....
       }
       
### 값 타입, 객체 타입
- 임베디드 타입 같은 값 타입을 여러 엔티티에서 공유 시 부작용 발생(값 변경 시 참조한 값들을 다 변경)

![image](https://user-images.githubusercontent.com/96407257/148766584-450c3126-2f4c-4140-ac12-299302b170a6.png)
- 값 타입의 실제 인스턴스인 값을 공유하는 것은 위험 -> 인스턴스 값을 복사해서 사용
- 그러나 임베디드 타입 같은 값은 객체 타입으로 참조 값을 직접 대입하는 것을 막지 못함
- 객체의 공유 참조는 피할 수 없음
![image](https://user-images.githubusercontent.com/96407257/148766950-31c2bf5e-ff1a-4ad6-9739-590ddf2dc70f.png)

### 불변 객체
- 객체 타입의 공유 참조를 원천 차단하기 위해 생성 이후 값을 변경할 수 없는 객체로 설계
- 생성자로만 값을 설정하고 수정자(setter)를 만들지 않으면 됨

### 값 타입 비교
- 동일성(identity) 비교 : 인스턴스의 참고 값을 비교(== 사용)
- 동등성(equivalence) 비교 : 인스턴스의 값을 비교,(equals() 사용)
- 값 타입은 a.equals(b)를 사용하여 동등성 비교를 해야함

### 값 타입 컬렉션
- 값 타입을 하나 이상 저장할 떄 사용
- @ElementCollection, @CollectionTable 사용
- 데이터베이스는 컬렉션 같은 테이블에 저장X
- 컬렉션 저장을 위한 별도의 테이블 필요

![image](https://user-images.githubusercontent.com/96407257/148778346-ff770a55-ea2d-40a8-9781-a5cfed523a1f.png)


      @ElementCollection
      @CollectionTable(name = "FAVORITE_FOOD", joinColumns =@JoinColumn(name = "MEMBER_ID"))
      @Column(name = "FOOD_NAME")
      private Set<String> favoriteFoods = new HashSet<>();
    
      @ElementCollection
      @CollectionTable(name = "ADDRESS", joinColumns = @JoinColumn(name = "MEMBER_ID"))
      private List<Address> addressHistory = new ArrayList<>();
      
- 값 타입 컬렉션도 지연 로딩 전략을 사용
- 값 타입 컬렉션은 영속성 전이(Cascade)를 필수로 가짐

#### 값 타입 컬렉션 제약사항
- 값 타입은 엔티티와 다르게 식별자 개념X
- 값 변경 시 추적이 힘듦
- 값 타입 컬렉션에 변경 사항 발생 시 주인 엔티티와 연관된 모든 데이터를 삭제 후 현재 값 모두를 다시 저장
- 값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본 키로 구성(null 입력X, 중복 저장X)

#### 값 타입 컬렉션 대안
-  상황에 따라 값 타입 컬렉션 대신 일대다 관계를 고려
-  일대다 관계를 위한 엔티티를 만들고 여기에 값 타입을 사용
-  영속성 전이(Cascade)를 사용하여 값 타입 컬렉션 처럼 사용

## 엔티티 타입 vs 값 타입
### 엔티티 타입
- 식별자O
- 생명 주기 관리
- 공유
### 값 타입
- 식별자X
- 생명 주기를 엔티티에 의존
- 공유하지 않는 것이 안전(복사해서 사용)
- 불변 객체로 만드는 것이 안전


# v1.07 1/10

## 즉시 로딩과 지연 로딩
### 프록시를 이용한 지연 로딩(LAZY)
![image](https://user-images.githubusercontent.com/96407257/148730642-2c4f6772-3ec8-424d-90d8-7778c795ce7e.png)
- 지연 로딩 이전에는 Member 호출 시 Team도 함께 쿼리가 날라감.
- 지연 로딩 후 Team은 프록시를 이용하여 지연시키고 실제 Team 호출 시 초기화.
- 지연 로딩으로 인해 Member만 쿼리 발생.

    @Entity
    public class MappingMember extends BaseEntity {
    ....
    @ManyToOne(fetch = FetchType.LAZY) //지연 로딩
    @JoinColumn(name = "TEAM_ID")
    private MappingTeam team;
    ....
    }

### 프록시와 즉시로딩 주의
- 가급적 지연 로딩 사용(특히 실무에서)
- 즉시 로딩은 JPQL에서 N+1 문제 발생
- @ManyToOne, @OneToOne은 기본이 즉시 로딩 -> LAZY로 설정

## 영속성 전이 : CASCADE
### CASCADE
- 특정 엔티티를 역속 상태로 만들 떄 연관된 엔티티도 함께 영속 상태로 만듦
- 영속성 전이는 연관관계 매핑과 관련 X
- 엔티티 영속화할 때 연관 엔티티도 함께 영속화하는 편리함을 제공할 뿐
![image](https://user-images.githubusercontent.com/96407257/148731921-9cfdb0ca-ef1c-4d9c-9fb2-9fdb399591c5.png)

      public class MappingTeam extends BaseEntity {
      ....
      @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
      List<MappingMember> members = new ArrayList<>();

      public void addTeam(MappingMember member) {
          members.add(member);
          member.setTeam(this);
      ....
      }
      
      public static void main(String[] args) {
      ....
          MappingMember m1 = new MappingMember();
          MappingMember m2 = new MappingMember();

          MappingTeam team = new MappingTeam();
          team.addTeam(m1);
          team.addTeam(m2);

          em.persist(team);
      ....
      }

### CASCADE의 종류
- ALL : 모두 적용
- PERSIST : 영속
- REMOVE : 삭제


# v1.06 1/9

## 프록시
### 프록시 기초
- em.find : 데이터베이스를 통해서 실제 엔티티 객체 조회
- em.getReference : 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체 조회

      //프록시 - find,Reference 비교
      Member member1 = new Member();
      member1.setUsername("member1");
      em.persist(member1);

      em.flush()
      em.clear();

      Member reference = em.getReference(Member.class, member1.getId())
      Member findMember = em.find(Member.class, member1.getId());

      System.out.println("reference = " + reference.getClass());            
      System.out.println("findMember = " + findMember.getClass());


### 프톡시 특징
- 실제 클래스를 상속 받아서 만들어짐
- 실제 클래스와 겉 모양이 같음
- 사용하는 입장에서 실제 객체 프록시 객체 구분하지 않고 사용하면 됨(이론상)
- 프록시 객체는 실제 객체의 참조(target)를 보관
- 프록시 객체를 호출하면 프록시 객체는 실제 객체 메소드 호출
![image](https://user-images.githubusercontent.com/96407257/148669200-aaa9f3af-908c-4c56-b452-a82effce3292.png)

### 프록시 객체 초기화
    Member member = em.getReference(Member.class, "id1");
    member.getName();
    
![image](https://user-images.githubusercontent.com/96407257/148669216-86fdbde0-aa40-4cc3-a0a1-dd55662b109c.png)

### 프록시 특징
- 프록시 객체는 처음 사용 시 한 번만 초기화
- 프록시 객체 초기화 할 때 프록시 객체가 실제 엔티티로 바뀌지 않음. 초기화 되면 프록시를 통해 실제 엔티티에 접근 가능
- 프록시 객체는 원본 엔티티 상속 받음(타입은 다름, 따라서 ==비교 실패, 대신 instance of 사용)
- 영속성 컨텍스트에 찾는 엔티티가 있다면 Reference를 호출하여도 실제 엔티티 반환
- 준영속 상태 일 떄 프록시 초기화 시 문제 발생(org.hibernate.LazyInitializationException 예외)

# v1.05 1/8

## 상속관게 매핑
- 관계형 데이터베이스는 상속 관계가 아님
- 슈퍼타입 서브타입 관계라는 모델링 기법이 객체 상속과 유사
- 객체의 상속 구조와 DB의 슈퍼타입 서브타입 관계를 매핑
![image](https://user-images.githubusercontent.com/96407257/148634257-5b108549-6d11-4161-9e0a-5e8f03881e44.png)

### 상속관계 매핑 방법 종류
- 조인 전략 : 각각의 테이블로 변환
- 단일 테이블 전략 : 통합 테이블로 변환
- 구현 클래스마다 테이블 전략 : 서브타입 테이블로 전환

### 조인 전략
![image](https://user-images.githubusercontent.com/96407257/148634301-2cf60eb0-7bdd-45ba-a428-8af560fe2209.png)
- 장점 : 테이블 정규화, 외래키 참조 무결성 제약조건 활용가능, 저장공간 효율화
- 단점 : 조회 시 조인 많이 사용(성능저하), 조회 쿼리 복잡, 데이터 저장 시 쿼리 2번 호출

      @Entity
      @Inheritance(strategy = InheritanceType.JOINED)
      @DiscriminatorColumn(name = "TYPE")
      public class InheritanceMain {
          @Id @GeneratedValue
          private Long id;
          ....
      }
      
      @Entity
      @DiscriminatorValue("One")
      public class InheritanceTableOne extends InheritanceMain {

          private String oneName;
          private String oneOther;
          ....
      }
      
      @Entity
      @DiscriminatorValue("Two")
      public class InheritanceTableTwo extends InheritanceMain {

          private String twoName;
          private String twoOther;
          ....
      }
      
      @Entity
      @DiscriminatorValue("Three")
      public class InheritanceTableThree extends InheritanceMain {

          private String threeName;
          private String threeOther;
          ....
      }

### 단일 테이블 전략
![image](https://user-images.githubusercontent.com/96407257/148634489-553e2c80-6bac-4628-a926-a7e33c188d83.png)
- 장점 : 조인이 필요없어 성능이 높음, 조회 쿼리 단순
- 단점 : 자식 엔티티가 매핑한 컬럼은 모두 null, 단일 테이블에 모든 것을 저장해 테이블이 커질 수 있음

      @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
      @DiscriminatorColumn(name = "TYPE")
      public class InheritanceMain {
      ....
      }
      
### 구현 클래스마다 테이블 전략
![image](https://user-images.githubusercontent.com/96407257/148634550-1b2d58e2-4f4d-4ed9-b153-be80ccb51d58.png)
- 장점 : 서브 타입 명확하게 구분할 때 효과적, not null 제약조건 사용 가능
- 단점 : 여러 자식 테이블을 함께 조회할 때 성능 저하, 자식 테이블을 통합해서 쿼리 어려움
- 이 전략은 비추천

      @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
      @DiscriminatorColumn(name = "TYPE")
      public class InheritanceMain {
          ....
          }
          
### MappedSuperclass
- 공통 매핑 정보가 필요할 때 사용
- 상속관계 매핑X, 엔티티X, 테이블과 매핑X
- 부모 클래스를 상속 받는 자식 클래스에 매핑 정보만 제공
- 조회, 검색 불가
- 추상 클래스 권장
![image](https://user-images.githubusercontent.com/96407257/148634964-b256a20d-501e-4b99-87ff-5abac2ec1c39.png)

       @MappedSuperclass
       public class BaseEntity {

          private String createBy;
          private LocalDateTime createDate;
          private String ModifiedBy;
          private LocalDateTime ModifiedDate;
          ....
          }
# v1.04 1/7

## 연관 관계 매핑 종류
- ManyToOne(N:1)  
- OneToMany(1:N)
- OneToOne(1:1)
- ManyToMany(N:N)

### 1. ManyToOne(N:1)
![image](https://user-images.githubusercontent.com/96407257/148533978-ddae1bfd-e264-4882-8119-1951135a5cdc.png)
- 가장 많이 사용하는 연관관계
- 외래 키가 있는 쪽이 연관관계의 주인
- 양쪾을 서로 참조하여 개발

### 2. OneToMany(1:N)
![image](https://user-images.githubusercontent.com/96407257/148534183-362cb351-b9eb-46ab-a028-2627d1d4c9b5.png)
#### OneToMany 단방향
- OneToMany 단방향의 경우 1이 연관관계의 주인이지만 테이블에서는 다(N)쪽에 외래 키가 존재
- 객체와 테이블 차이로 반대편 테이블의 외래키를 관리하는 특이 구조
- @JoinColumn 사용은 필수 사용하지 않을 시 Jointable 구조 방식을 사용(중간에 테이블 하나 추가하는 방식)
- 연관관계 관리를 위해 추가 UPDATE 쿼리가 실행

      @Entity
      public class MappingTeam {
      ....
      @OneToMany
      @JoinColumn(name = "TEAM_ID") // 연관관계 주인 설정
      List<MappingMember> members = new ArrayList<>();
      ....
      }
      
#### OneToMany 양방향
![image](https://user-images.githubusercontent.com/96407257/148534682-b089b115-00f4-4422-b41e-360607c389c1.png)
- 이런 매핑은 공식적으로 존재 X
- @JoinColumn(insertable=false, updatable=false)를 사용
- 읽기 전용 필드를 사용하여 양방향처럼 사용하는 방법
- 다대일 양방향 사용을 권장!!

      @Entity
      public class MappingMember {
      ....
      // OneToMany 양방향 읽기 전용 필드
      @JoinColumn(insertable = false, updatable = false)
      private MappingTeam team;
      ....
      }

### 3. OneToOne(1:1)
![image](https://user-images.githubusercontent.com/96407257/148535028-30bd993a-4022-48ec-9b8f-675d035d6368.png)
- 주 테이블이나 대상 테이블 중에 외래 키 선택 가능
- 외래 키에 데이터베이스 유니크(UNI) 제약조건 추가
- 양방향 시 ManyToOne처럼 외래키가 있는 곳이 연관관계의 주인(반대편은 mappedBy 적용)
- 주 테이블에 외래 키 적용이 유용
- 대상 테이블에 외래 키 적용 시 프록시 기능의 한계로 지연 로딩을 설정해도 즉시 로딩이 됨

       @Entity
      public class MappingMember {
        ....
        @OneToOne
        @JoinColumn(name = "ONE_ID") //연관관계 주인 설정
        private MappingOneObject oneObject;
        ....
      }
      
      @Entity
      public class MappingOneObject {

        @Id @GeneratedValue
        private Long id;
        private String name;

        @OneToOne(mappedBy = "oneObject")
        private MappingMember mappingMember;
      }
       
### 4. ManyToMany(N:M)
![image](https://user-images.githubusercontent.com/96407257/148549745-1477d048-a1c1-4c77-947a-0f1b7b1abd74.png)

- 관계형 DB는 정규화된 테이블 2개로 다대다 관계 표현이 불가능
- 연결 테이블을 추가해서 일대다, 다대일 관계를 풀어내야함
- 객체는 컬렉션을 사용하여 다대다 관계 가능

#### 다대다 매핑의 한계
- 편리해 보이지만 실무에서 사용X
- 연결 테이블이 단순히 연결만 하는 것이 아닌 다른 데이터들이 들어올 가능성이 있음

      @Entity
      public class MappingMember {
        ....
        ManyToMany 한계
        @ManyToMany
        @JoinColumn(name = "MEMBER_OBJECTS")
        private List<MappingManyObject> manyObjects = new ArrayList<>();
        ....
      }
      
      @Entity
      public class MappingManyObject {
        ....
        // ManyToMany 한계
       @ManyToMany(mappedBy = "manyObjects")
       private List<MappingMember> members = new ArrayList<>();
        ....
      }

#### 다대다 한계 극복
![image](https://user-images.githubusercontent.com/96407257/148550637-ed2c3cec-fc17-4534-aa45-0d6512c1eab5.png)
- 연결 테이블용 엔티티 추가(연결 테이블을 엔티티로 승격)
- @ManyToMany -> @OneToMany, @ManyToOne

      @Entity
      public class MappingMember {
        ....
        //ManyToMany 한계 극복
      @OneToMany(mappedBy = "member")
      private List<MemberObjects> memberObjects = new ArrayList<>();
        ....
      }
      
      @Entity
      public class MappingManyObject {
        ....
        // ManyToMany 한계 극복
        @OneToMany(mappedBy = "manyObject")
        private List<MemberObjects> memberObjects = new ArrayList<>();
        ....
      }
      
      @Entity
      public class MemberObjects {

        @Id @GeneratedValue
        private Long id;

        @ManyToOne
        @JoinColumn(name = "MAP_MEMBER_ID")
        private MappingMember member;

        @ManyToOne
        @JoinColumn(name = "MAP_OBJECT_ID")
        private MappingManyObject manyObject;
      }


# v1.03 1/7

### 연관관계 매핑

#### 연관관계가 없는 객체
![image](https://user-images.githubusercontent.com/96407257/148393262-dad0f8b3-db10-4d5d-b09f-3e6a851e47b8.png)

- 엔티티 클래스 생성(외래 키를 잠조 없이 그대로 사용)
    
      @Entity
      public class MappingMember {
        @Id @GeneratedValue
        @Column(name = "MAP_MEMBER_ID")
        private Long id;

        private String name;

        //참조대신 외래 키 그대로 사용
        @Column(name = "TEAM_ID")
        private Long teamId;
        ....
     
      @Entity
      public class Team {
        @Id @GeneratedValue
        private Long id;
        private String name; 
         … 
        }
 
 
- 데이터 값 주입

      MappingTeam mappingTeam = new MappingTeam();
            mappingTeam.setName("teamA");

            em.persist(mappingTeam);

            MappingMember mappingmember = new MappingMember();
            mappingMember.setName("memberA");
            member.setTeamId(team.getId());
            
            em. persist(mappingmember)
            
           ...
           MappingMember findMember = em.find(MappingMember.class, mappingmember.getId());
            //식별자로 다시 조회.. 객체지향적이지 않음
            Long findTeamId = findMember.getTeamId();
            MappingTeam findTeam = em.find(MappingTeam.class, findTeamId);
            
- 객체를 테이블에 맞춰 데이터 중심 모델링을 하면 협력 관계(객체지향)적이지 않음

#### 단방향 연관관계
![image](https://user-images.githubusercontent.com/96407257/148395317-a7df9903-a789-4dcb-be86-6147ce918f72.png)


- 엔티티 클래스 생성(객체 참조와 테이블 외래 키 매핑)
      
       @Entity
       public class MappingMember {
          @Id @GeneratedValue
          @Column(name = "MAP_MEMBER_ID")
          private Long id;

          private String name;

          //객체의 참조와 테이블의 외래 키 참조
          @ManyToOne(fetch = FetchType.LAZY)
          @JoinColumn(name = "TEAM_ID")
          private MappingTeam team;
          
- 데이터 값 주입


           MappingTeam mappingTeam = new MappingTeam();
            mappingTeam.setName("teamA");

            em.persist(mappingTeam);

            MappingMember mappingmember = new MappingMember();
            mappingmember.setName("memberA");
            //단방향 연관관계 설정
            mappingmember.setTeam(mappingTeam);
            em.persist(mappingmember);
            
            ...
            
          // 단방향 매핑을 통한 member -> team 조회
            MappingTeam findTeam = findMember.getTeam();
            System.out.println("findTeam = " + findTeam.getName());
            
#### 양방향 연관관계, 연관관계 주인
![image](https://user-images.githubusercontent.com/96407257/148396187-582717ce-d0c6-497f-99fe-95a7b2f46027.png)

- 객체들은 양방향 연관관계가 존재하지만 테이블은 외래 키 하나를 통해 서로 연관관계를 가진다(방향 X, 양쪽으로 조인 가능)  
- 사실 객체의 양방향 관계 보다 서로 다른 방향 관계가 2개인 것이다.  
- 양방향 매핑 시 객테의 두 관계 중 하나를 연관관계의 주인으로 지정해야한다.  
- 연관관계의 주인만 외래 키를 관리(등록, 수정)
- 주인이 아닌 쪽은 읽기만 가능
- 주인이 아닌 쪽에 mappedBy 속성을 통해 주인을 지정
- 외래 키가 있는 곳을 주인으로 지정(ManyToOne일 때 Many부분을 주인으로 지정)
- 단방향 매핑을 하며 필요 시 양방향 매핑을 추가하는 방향으로 설계

      @Entity
      public class MappingTeam {
          @Id @GeneratedValue
          private Long id;
          private String name;

          //mappedBy를 통해 member를 주인지정, 저 team은 member.team을 의미
          @OneToMany(mappedBy = "team") 
          List<MappingMember> members = new ArrayList<>();

          
- 데이터 값 주입
      
       // 양방향 매핑을 통한 team -> member 조회
            List<MappingMember> members = findMember.getTeam().getMembers();

            for (MappingMember member : members) {
                System.out.println("member.getName() = " + member.getName());
            }
            
### 연관관계 매핑 주의점
- 연관관계의 주인에 값을 입력(역방향만 연관관계를 설정하면 안됨)  
- 양방향으로 둘 다 값을 입력(한방향이 아닌 양방향 모두 값을 설정)  
- 실수를 줄이기위해 엔티티에 값을 설정하는 것이 좋음  
- 양방향 매핑 시 무한 루프에 조심(ex toString(), lombok, JSON 생성 라이브러리)  

      @Entity
      public class MappingMember {
          ....
       /연관관계 편의 메소드
       public void changeTeam(MappingTeam team) {
        this.team = team;
        team.getMembers().add(this);
        }

혹은  

        @Entity
        public class MappingTeam {
         ....
        / 연관관계 편의 메소드
         public void addMember(MappingMember mappingMember) {
           mappingMember.setTeam(this);
           members.add(mappingMember);
         }
            
# v1.02 1/6

### 엔티티 매핑

#### 엔티티 매핑 종류
- 객체와 테이블 매핑 : @Entity, @Table
- 필드와 컬럼 매핑 : @Column
- 기본 키 매핑 : @Id
- 연관관계 매핑 @ManyToOne,@JoinColumn....

#### 1. 객체와 테이블  
#### @Entity
- 엔티티는 JPA가 관리, JPA 사용하여 테이블 매핑 시 @Entity는 필수
- 기본 생성자 필수(public, protected 생성자)
- final 클래스, enum, interface, inner 클래스 사용X
- 저장할 필드에 final 사용 X
- 기본 값은 클래스 이름 사용, 가급적 기본값을 사용

#### @Table
속성 | 기능 | 기본값
---- |---- | ----
name |매핑할 테이블 이름| 엔티티 이름 사용
catalog | 데이터베이스 catalog 매핑 |
schema | 데이터 베이스 schema 매핑 |
uniqueConstraints(DDL) | DDL 생성 시 유니크 제약 조건 생성 |

#### DB 스키마 자동 생성
- DDL을 애플리케이션 실행 시점에 자동 생성
- 테이블 중심 -> 객체 중심
- DDL은 개발 장비에서만 사용  
- resources/META-INF/persistence.xml hibernate.hbm2ddl.auto 추가
- resources/application.yml ddl-auto 추가
- DDL 생성 기능은 자동 생성 때만 사용되고 JPA 실행 로직에는 영향 X

옵션 | 설명
---- | ----
create | 기존테이블 삭제 후 다시 생성(DROP + CREATE)
create-drop | create와 같으나 종료시점에 테이블 drop
update | 변경분만 만영(운영DB 사용X)
validate | 엔티티와 테이블이 정상 매핑 되었는지만 확인
none | 사용X

#### 2. 필드와 컬럼  
#### 매핑 어노테이션 정리
어노테이션 | 설명
----| ----
@Column | 컬럼 매핑
@Temporal | 날짜 타입 매핑
@Enumerated | enum 타입 매핑(사용 시 EnumType.STRING을 사용_ ORDINAL 사용 시 원치않은 값이 나올 가능성 존재)
@Lob | BLOB,CLOB 매핑
@Transient | 매핑 무시

#### @Column
![image](https://user-images.githubusercontent.com/96407257/148343036-294420a0-027c-435f-8197-fd57b2c5bf7e.png)

#### @Temporal
- value 종류
- TemporalType.DATE(날짜, 데이터베이스 date 타입과 매핑)
- TemporalType.TIME(시간, 데이터베이스 tiem 타입과 매핑)
- TemporalType.TIMESTAMP(날짜, 시간, 데이터베이스 tiemstamp 타입과 매핑)

#### 3. 기본 키 매핑
#### @Id
- 직접 할당
#### @GeneratedValue
- 자동 생성
- IDENTITY : 기본 키 생성을 데이터베이스에 위임, em.persist() 시점에 즉시 INSERT SQL 실행하고 DB에서 식별자 조회

      @Entity
      public class Member {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      
- SEQUENCE : 데이터베이스 시퀸스는 유일한 값을 순서대로 생성하는 특별한 오브젝트 (ORACLE, PostgreSQL, H2데이터베이스), @SequenceGenerator 필요, allocationSize를 통해 설정 값만큼 DB에서 가져오면서 성능을 최적화(기본값 50)

      @Entity 
      @SequenceGenerator( 
              name = “MEMBER_SEQ_GENERATOR", 
              sequenceName = “MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
              initialValue = 1, allocationSize = 1) 
      public class Member { 
      @Id 
      @GeneratedValue(strategy = GenerationType.SEQUENCE, 
            generator = "MEMBER_SEQ_GENERATOR") 
      private Long id; 
      
![image](https://user-images.githubusercontent.com/96407257/148350045-584340bf-bbd7-422f-a6ab-50c4acc06734.png)


- TABLE : 키 생성용 테이블 사용, 모든 DB에서 사용(@TableGenerator 필요)
- AUTO : SQL 종류에 따라 자동 지정


# v1.01 1/5

### JPA가 유용한 2가지

- Object Relational Mapping(ORM) 객체와 관계형 데이터베이스 매핑  
- 영속성 컨텍스트

#### 영속성 컨텍스트

- EntityManager을 통해서 영속성 컨텍스트에 접근 가능  
- 엔티티를 저장하는 환경  
- 엔티티의 생명주기는 비영속(new/transient), 영속(managed), 준영속(detached), 삭제(removed) 4가지가 존재  

#### 비영속
- 엔티티 객체 생성  
- 영속성 컨텍스트나 DB에 연관 X  
<img width="674" alt="JPA_3_3" src="https://user-images.githubusercontent.com/96407257/148226028-27373aa1-1b24-42cf-95e7-e81b861020fa.png">

    Member member = new Member();
    member.setId(1L);
    member.setName("userA");

#### 영속
- EntityManager를 통해 엔티티를 영속성 컨텍스트에 저장
- 영속상태 = 영속성 컨텍스트에 의해 관리된다는 뜻
- EntityManager.find()나 JPQL을 사용해서 조회한 엔티티도 연속상태
- persist 떄 영속되는 것이 아닌 commit 시점에 영속 상태가 된다!!

<img width="575" alt="JPA_3_4" src="https://user-images.githubusercontent.com/96407257/148226203-f8e8039e-f115-41ce-95c0-2e33426665b0.png">
    
    EntityManager em = emf.createEntityManager();
    em.getTransaction().begin();
    
    em.persist(member);
    
#### 영속성 컨텍스트의 이점
- 1차 캐시
- 동일성 보장
- 트랜잭션을 지원하는 쓰기 지연
- 변경 감지(Dirty checking)
- 지연 로징(Lazy Loading)

#### 1차 캐시에서 조회

![download](https://user-images.githubusercontent.com/96407257/148228604-2d4097dd-bae5-4714-8f69-ad6e4711406b.png)

    Member member = new Member();
    member.SetId("member1")
    member.setName("userA")
    //1차 캐시에 저장
    em.persist(member);
    // 1차 캐시에서 조회
    Member findMember = em.find(Member.class, "member1");

#### DB에서 조회

![image](https://user-images.githubusercontent.com/96407257/148229259-ea0fe91a-e5c1-40b2-b440-9cbe8070a05c.png)

    //DB 저장 후 조회
    Member findMember2 = em.find(Member.class, "member2");
    
#### 엔티티 등록 - 트렌잭션을 지원하는 지연 로딩

    EntityManager em = emf.createEntityManager();
    EntityTransaction transaction = em.getTransaction();
    
    transaction.begin(); //트랜잭션 시작
    em.persist(memberA);
    em.persist(memberB);
    // Insert SQL로 DB에 보내지 않음
    // 커밋하는 순간 DB에 전송
    transaction.commit(); // 트랜잭션 시작
    
![image](https://user-images.githubusercontent.com/96407257/148230061-fe0374b5-674d-4f49-91a5-07a9a683d0d6.png)

#### 엔티티 수정 - Dirty Checking

    EntityManager em = emf.createEntityManager();
    EntityTransaction transaction = em.getTransaction();
    transaction.begin(); //트랜잭션 시작
    // 영속 엔티티 조회
    Member memberA = em.find(Member.class, "memberA");
    // 영속 엔티티 수정
    memberA.setName("JPA");
    memberA.setId("2L")
    // em.update(member)같은 코드 필요 XX
    
    transaction.commit(); // 트랜잭션 시작
    
![image](https://user-images.githubusercontent.com/96407257/148230585-4d2d7509-58f5-4f08-94c6-0f8732caaacd.png)

#### 플러시
- 영속성 컨텍스트의 변경내용을 DB에 반영
- 플러시 발생 시 변경 감지, 수정된 엔티티 쓰기 지연 SQL 저장소에 등록, 쓰기 지연 SQL 저장소의 쿼리를 DB에 전송
- em.flush() //직접 호출 
- 트랜잭션 커밋, JPQL 쿼리 실행 // 플러시 자동 호출
- 플러시는 영속성 컨텍스트를 비우지 않음
- 영속성 컨텍스트의 변경 내용을 DB에 동기화
- 트랙잭션 작업 단위가 중요 -> 커밋 직전에만 동기화(insert,update 등등)하면 됨

# v1.00 1/5

### JPA를 통한 DB 접근

#### JPA 구동 방식

1.설정 정보 조회(META-INF/persistence.xml) -> 2.생성(persistence - EntityManagerFactory) -> 
3. 생성 (EntityManager)  

##### 엔티티 매니저 팩토리 생성을 통한 DB 접근

- 엔티티 매니저 팩토리는 하나만 생성하여 전체에 공유  
- 엔티티 매니저는 쓰레드간 공유 X
- JPA의 모든 데이터 변경은 트랜잭션 안에서 실행

      EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

      EntityManager em = emf.createEntityManager();
      EntityTransaction entityTransaction = em.getTransaction();

##### 객체 저장

- 회원 등록

      entityTransaction.begin();
      
      Member member = new Member();
      member.setId(1L);
      member.setName("JPA");

      em.persist(member);
      
      entityTransaction.commit();

- 회원 조회 및 수정
회원 수정 시 조회 후 set을 해주면 수정이 가능(변경 감지 기능_dirty checking)

      entityTransaction.begin();
      
      Member findMember = em.find(Member.class, 1L);
      findMember.setName("정희성");
      
      entityTransaction.commit();
      
##### JPQL

- entityManager.createQuery를 통해 sql 쿼리를 직접 주입  
- JPA를 사용하면 엔티티 객체 중심으로 개발 -> 검색 쿼리가 문제!  
- 검색할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색  
- 애플리케이션에 필요한 DB를 가져오기 위해서 검색 조건을 포함한 SQL이 필요!!  
- JPQL은 객체 지향 SQL!

      entityTransaction.begin();
    
      List<Member> result = em.createQuery("select m from Member m", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(8)
                    .getResultList();
      
      entityTransaction.commit();
