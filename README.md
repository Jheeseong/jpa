# jpa

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
