## Spring Security 동장 방식
![image](https://user-images.githubusercontent.com/97269799/221396757-07763bbf-bf7e-40ed-a0fc-66470ca0efda.png)

1. 사용자가 request 를 보낼 때 AuthenticationFilter가 받아 Token 생성
2. 토큰을 AuthenticationManager가 받아 이의 구현체인 Provider로 넘김
3. Provider는 PasswordEncoder을 통해 Hashed password를 받음
4. 또한 Provider가 UserDetailsService를 사용하여 db의 user,role 에 접근
5. UserDetailsService에서 loadUserByUsername()을 통해 UserDetails를 리턴받음
6. UserDetials의 password와 사용자가 넘겨준 password(Hashed password)를 비교하여 확인
7. 인증이 이루어지면 AuthenticationFilter안에 SecurityContext에 Authentication정보를 저장


![image](https://user-images.githubusercontent.com/97269799/221490298-e3678383-7b21-4997-9f0a-a8b06359f630.png)



작성해야할 코드들
* gradle
```java
//spring security 적용
implementation 'org.springframework.boot:spring-boot-starter-security'
testImplementation 'org.springframework.security:spring-security-test'
```  

* SecurityConfig
```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService; // 성공할 때 실행되어야 하는 CustomLoginSuccessHandler 를 빈으로 등록
    
    /**
     *
     * 스프링 시큐리티 룰을 무시할 URL 규칙 설정
     * 정적 자원에 대해서는 Security 설정을 적용하지 않음
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/css/**", "/js/**");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/", "/members/new", "/items", "/login").permitAll() //인증이 없어도 접근 가능
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(customLoginSuccessHandler())
                .failureForwardUrl("/members/new")
                .and()
                .logout()
                .logoutUrl("/logout");
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        // 비밀번호 암호화 할때 사용할 BCrypthPasswordEncoder 를 빈으로 등록
        return new BCryptPasswordEncoder();
    }


    @Bean
    public CustomLoginSuccessHandler customLoginSuccessHandler() {
        // 성공할 때 실행되어야 하는 CustomLoginSuccessHandler 를 빈으로 등록
        return new CustomLoginSuccessHandler();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
        //AuthenticationManager 에 등록
        authenticationManagerBuilder.userDetailsService(userDetailsService);
    }
  }
```

* CustomAuthenticationProvider  (이거 없어도 동작 함. 나중에 커스텀 필요하면 공부)
```java
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //DB에서 사용자 정보가 실제로 유효한지 확인 후 인증된 Authentication 리턴
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication; //생성된 토큰으로부터 아이디와 비밀번호 조회
        String userName = token.getName();
        String userPassword = (String) token.getCredentials(); //UserDetailsService를 통해 아이디로 사용자 조회...?? 여기 뭐지

        System.out.println(userName);
        System.out.println(userPassword);

        Member member = (Member) userDetailsService.loadUserByUsername(userName); //이걸 오버라이드 해야하나봄

        if(passwordEncoder.matches(userPassword, member.getPassword()) == false) {
            throw new BadCredentialsException(member.getName() + "  비밀번호를 확인해주세요");
        }
        return new UsernamePasswordAuthenticationToken(member, userPassword, member.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
```

* CustomLoginSuccessHandler
```java
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 인증이 성공한 경우 아래 로직 수행
        // SecurityContextHolder > SecurityContext 에 Authentication 을 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("인증 성공");

        // / 페이지로 redirect 해준다.
        response.sendRedirect("/members");
    }
}
```

* UserDetailsServiceImpl
```java
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            // 사용자 이름을 기반으로 사용자 정보를 가져옵니다.
            Member member = memberRepository.findByNamed(username);
            if(member == null) {
                System.out.println("member=null");
                throw new UsernameNotFoundException("username에 해당하는 Entity 없음");
            }
            // Spring Security가 사용할 UserDetails 객체를 만듭니다.
            return User.builder().username(member.getName()).password(member.getPassword()).roles("user").build();
        }
}
```

### 만들면서 겪었던 에러들
* UserDetailsService 구현을 원래 있던 Service 클래스에 하면 스프링 빈 등록할 때 꼬임. 따로 만들어줘야 함
* 로그인 form에서 input안의 name지정을 username, password로 해야 동작함. 따로 설정하려면 http.formLogin().usernameParameter("email") 이런식으로 설정해줘야 함
* authenticated() 옵션은 인증이 된 유저만 접속 가능하게끔 하는 설정이다. 인증 없어도 접속가능하게끔 하는 설정만 하니 인증 없이도 모든 url이 뚫린다.
* User.builder() 할 때 roles 값이 비어있으면 안 됨
* 로그인 실패시 이동할 url 설정을 똑같이 login 으로 하면 뭐 이상한 버그뜸. 팝업 띄웠다가 다시 돌아오는 방식이 좋을듯
* 커스텀 프로바이더 는 없는게 편한것같음. 나중에 필요하다 싶으면 그때 공부
* **시큐리티 관련 클래스에 있는 Exception들은 전역예외처리에서 잡히지 않음.** 왜 그런진 모르겠음


### 주의점들
* 회원가입시 아이디,비번은 notnull 조건
* 비밀번호는 bCryptPasswordEncoder 를 통해서 인코딩해서 repo에 저장

### 로그아웃 메소드
![image](https://user-images.githubusercontent.com/97269799/221491126-c8754dfb-11ce-414a-adf4-46333b79eef4.png)
* post 형식으로 전달해야함!
  * 물론 로그인도 마찬가지
