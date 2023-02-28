package jpabook.jpashop.security;

import jpabook.jpashop.entity.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;


//    @Override
//    public Member loadUserByUsername(String username) throws UsernameNotFoundException {
//        System.out.println("loadUserByUsername 실행");
//        Member member = memberRepository.findByNamed(username);
//        if(member == null) {
//            throw new UsernameNotFoundException(username);
//        }
//        return member;
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 이름을 기반으로 사용자 정보를 가져옵니다.
        Member member = memberRepository.findByNamed(username);
        if(member == null) {
            System.out.println("member=null");
            throw new UsernameNotFoundException("username에 해당하는 Entity 없음");
        }
        // Spring Security가 사용할 UserDetails 객체를 만듭니다.
        return User.builder()
                .username(member.getName())
                .password(member.getPassword())
                .roles("user")
                .build();

    }
}
