# JPA

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
      
      public class MemberObjects {

        @Id @GeneratedValue
        private Long id;

        @ManyToOne
        @JoinColumn(name = "MEMBER_ID")
        private MappingMember member;

        @ManyToOne
        @JoinColumn(name = "OBJECT_ID")
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
