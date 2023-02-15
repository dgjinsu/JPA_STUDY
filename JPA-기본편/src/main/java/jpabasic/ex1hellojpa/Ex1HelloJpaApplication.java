package jpabasic.ex1hellojpa;


import jpabasic.ex1hellojpa.domain2.Member;
import jpabasic.ex1hellojpa.domain2.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

//@SpringBootApplication
public class Ex1HelloJpaApplication {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

		EntityManager em = emf.createEntityManager();
		EntityTransaction ts = em.getTransaction();
		ts.begin();
		try {

			Team team = new Team();
			team.setName("456");

			Member member = new Member();
			member.setName("123");
			member.setTeam(team);

			em.persist(team);
			em.persist(member);

			List<Member> result = em.createQuery("select m from Member m join m.team t", Member.class).getResultList();

			for (Member member1 : result) {
				System.out.println("member1 = " + member1);
			}


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

/**
 * 복습 1일차 : ~엔티티 매핑
 * 복습 2일차 : ~프록시
 */
