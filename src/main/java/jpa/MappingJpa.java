package jpa;

import javax.persistence.*;
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

            MappingTeam mappingTeam2 = new MappingTeam();
            mappingTeam2.setName("teamB");
            em.persist(mappingTeam2);

            MappingMember mappingmember1 = new MappingMember();
            mappingmember1.setName("memberA");
            mappingmember1.changeTeam(mappingTeam);
            em.persist(mappingmember1);

            MappingMember mappingMember2 = new MappingMember();
            mappingMember2.setName("memberB");
            mappingMember2.changeTeam(mappingTeam);
            em.persist(mappingMember2);

            MappingMember mappingMember3 = new MappingMember();
            mappingMember3.setName("memberC");
            mappingMember3.changeTeam(mappingTeam2);
            em.persist(mappingMember3);
//            member.setTeamId(team.getId());
            //단방향 연관관계 설정


            List<MappingMember> resultList = em.createQuery("select m from MappingMember m where m.name like '%member%'"
                            ,MappingMember.class)
                    .getResultList();
            for (MappingMember member : resultList) {
                System.out.println("member = " + member.getName());
            }

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);
            List<MemberDTO> resultList1 = em.createQuery("select new jpa.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .setFirstResult(0)
                    .setMaxResults(100)
                    .getResultList();

            String query = "select m from MappingMember m left join m.team t on t.name='teamA'";
            List<MappingMember> resultList2 = em.createQuery(query, MappingMember.class)
                    .getResultList();

            String query1 = "select m from MappingMember m left join MappingTeam t on m.name = t.name";
            List<MappingMember> resultList3 = em.createQuery(query1, MappingMember.class)
                    .getResultList();

//            String result = em.createQuery("select m.name from MappingMember m join m.team t", String.class)
//                    .getSingleResult();
//
//            System.out.println("result = " + result);

            List<MappingMember> resultList4 = em.createQuery("select m From MappingMember m",MappingMember.class)
                    .getResultList();

            for (MappingMember mappingMember : resultList4) {
                System.out.println("mappingmember = " + mappingMember);
            }

            List<MappingMember> resultList5 = em.createQuery("select m from MappingMember m join fetch m.team", MappingMember.class)
                    .getResultList();
            for (MappingMember mappingMember : resultList5) {
                System.out.println("mappingMember = " + mappingMember);
            }

            List<MappingTeam> resultList6 = em.createQuery("select t from MappingTeam t join fetch t.members", MappingTeam.class)
                    .getResultList();
            for (MappingTeam team : resultList6) {
                System.out.println("team = " + team.getName() + "|members = " + team.getMembers().size());
            }

//            em.createNamedQuery("Member.findByName", MappingMember.class)
//                    .setParameter("name", "회원1")
//                    .getResultList();
//            MappingMember findMember = em.find(MappingMember.class, mappingmember.getId());
//            //식별자로 다시 조회.. 객체지향적이지 않음
//            Long findTeamId = findMember.getTeamId();
//            MappingTeam findTeam = em.find(MappingTeam.class, findTeamId);
//            // 단방향 매핑을 통한 member -> team 조회
//            MappingTeam findTeam = findMember.getTeam();
//            System.out.println("findTeam = " + findTeam.getName());
//            // 양방향 매핑을 통한 team -> member 조회
//            List<MappingMember> members = findMember.getTeam().getMembers();
//
//            for (MappingMember member : members) {
//                System.out.println("member.getName() = " + member.getName());
//            }

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
