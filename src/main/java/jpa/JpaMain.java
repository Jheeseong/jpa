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

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
//            Member member = new Member();
//            member.setUsername("JPA");
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
            //프록시 - find,Reference 비교
            Member member1 = new Member();
            member1.setUsername("member1");
            em.persist(member1);

            em.flush();
            em.clear();

            Member reference = em.getReference(Member.class, member1.getId());
            Member findMember = em.find(Member.class, member1.getId());

            System.out.println("reference = " + reference.getClass());
            System.out.println("findMember = " + findMember.getClass());
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
