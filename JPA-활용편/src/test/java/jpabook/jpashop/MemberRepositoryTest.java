//package jpabook.jpashop;
//
//import org.assertj.core.api.Assertions;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//@Rollback(value = false)
//public class MemberRepositoryTest {
//
//    @Autowired MemberRepository memberRepository;
//
//    @Test
//    @Transactional
//    public void testMember() {
//        Member member = new Member();
//        member.setUserName("memberA");
//
//        Long savedId = memberRepository.save(member);
//
//        Member findMember = memberRepository.find(savedId);
//
//
//        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
//        Assertions.assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
//        Assertions.assertThat(findMember).isEqualTo(member);
//
//    }
//
//}