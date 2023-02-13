package jpabasic.ex1hellojpa;


import jpabasic.ex1hellojpa.domain.*;
import jpabasic.ex1hellojpa.domain2.Address;
import jpabasic.ex1hellojpa.domain2.AddressEntity;
import jpabasic.ex1hellojpa.domain2.Member;

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

			List<Member> result = em.createQuery("select m from Member m where m.name like '%kim%'", Member.class).getResultList();

			for (Member member : result) {
				System.out.println("member = " + member);
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
