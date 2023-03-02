//package jpabook.jpashop.security;
//
//import jpabook.jpashop.entity.Member;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 로그인 실패 횟수 등 을 구현할 때 사용
 */

//@Slf4j
//@RequiredArgsConstructor
//public class CustomAuthenticationProvider implements AuthenticationProvider {
//
//    private final UserDetailsService userDetailsService;
//    private final BCryptPasswordEncoder passwordEncoder;
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        //DB에서 사용자 정보가 실제로 유효한지 확인 후 인증된 Authentication 리턴
//        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication; //생성된 토큰으로부터 아이디와 비밀번호 조회
//        String userName = token.getName();
//        String userPassword = (String) token.getCredentials(); //UserDetailsService를 통해 아이디로 사용자 조회...?? 여기 뭐지
//
//
//        Member member = (Member) userDetailsService.loadUserByUsername(userName); //이걸 오버라이드 해야하나봄
//        System.out.println("가져온 멤버의 비밀번호 = " + member.getPassword());
//
//        if(passwordEncoder.matches(userPassword, member.getPassword()) == false) {
//            System.out.println("check");
//            throw new BadCredentialsException(member.getName() + "  비밀번호를 확인해주세요");
//        }
//        return new UsernamePasswordAuthenticationToken(member, userPassword, member.getAuthorities());
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return authentication.equals(UsernamePasswordAuthenticationToken.class);
//    }
//}
//
//
////https://velog.io/@soyeon207/%EC%8B%A4%EC%8A%B5%ED%8E%B8-%EC%8A%A4%ED%94%84%EB%A7%81-%EC%8B%9C%ED%81%90%EB%A6%AC%ED%8B%B0-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0
