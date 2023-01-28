package jpabook.jpashop.repository;

import jpabook.jpashop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    //select m from Member m where m.name = :name  을 자동으로 만들어줌
    List<Member> findByName(String name);
}
