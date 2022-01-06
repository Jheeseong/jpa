package jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();
        EntityTransaction entityTransaction = em.getTransaction();
        entityTransaction.begin();

        try {
//            Member member = new Member();
//            member.setId(1L);
//            member.setName("JPA");
//
//            em.persist(member);

//            Member findMember = em.find(Member.class, 1L);
//            findMember.setName("정희성");
//            System.out.println("findMember.id = " + findMember.getId());
//            System.out.println("findMember.getName() = " + findMember.getName());


//            List<Member> result = em.createQuery("select m from Member m", Member.class)
//                    .setFirstResult(0)
//                    .setMaxResults(8)
//                    .getResultList();
//
//            for (Member member1 : result) {
//                System.out.println("member1.getName() = " + member1.getName());
//            }

            entityTransaction.commit();
        }catch (Exception e) {
            entityTransaction.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
