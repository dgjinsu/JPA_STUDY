package hellojpa;

import hellojpa.entity.Member;
import hellojpa.entity.MemberDto;
import hellojpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jinsu");
        EntityManager em = emf.createEntityManager();
        EntityTransaction ts = em.getTransaction();
        ts.begin();
        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(100);
            member.setTeam(team);

            em.persist(member);

            String query = "select m from Member m inner join m.team t";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();
            System.out.println("result.size() = " + result.size());


            ts.commit();

        } catch(Exception e) {
            ts.rollback();
            e.printStackTrace();
            System.out.println("예외터짐");
        } finally {
            em.close();
        }
        emf.close();

    }
}