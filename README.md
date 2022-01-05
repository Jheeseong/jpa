# JPA

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
