package jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class MappingJpa {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            MappingTeam mappingTeam = new MappingTeam();
            mappingTeam.setName("teamA");

            em.persist(mappingTeam);

            MappingMember mappingmember = new MappingMember();
            mappingmember.setName("memberA");
//            member.setTeamId(team.getId());
            //단방향 연관관계 설정
            mappingmember.changeTeam(mappingTeam);
            em.persist(mappingmember);

            em.flush();
            em.clear();

            MappingMember findMember = em.find(MappingMember.class, mappingmember.getId());
//            //식별자로 다시 조회.. 객체지향적이지 않음
//            Long findTeamId = findMember.getTeamId();
//            MappingTeam findTeam = em.find(MappingTeam.class, findTeamId);
            // 단방향 매핑을 통한 member -> team 조회
            MappingTeam findTeam = findMember.getTeam();
            System.out.println("findTeam = " + findTeam.getName());
            // 양방향 매핑을 통한 team -> member 조회
            List<MappingMember> members = findMember.getTeam().getMembers();

            for (MappingMember member : members) {
                System.out.println("member.getName() = " + member.getName());
            }

            tx.commit();

        }catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
