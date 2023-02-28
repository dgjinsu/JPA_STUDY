package jpabook.jpashop.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
                .antMatchers("/", "/members/new", "/items", "/login/**", "/v3/api-docs",
                        "/swagger*/**").permitAll() //인증이 없어도 접근 가능
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(customLoginSuccessHandler())
                .failureUrl("/login/error")
                .failureHandler(failureHandler())
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

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return new CustomAuthFailureHandler();
    }

//    @Bean
//    public CustomAuthenticationProvider customAuthenticationProvider() {
//        // 실제 인증 당담 객체를 빈으로 등록
//        return new CustomAuthenticationProvider(userDetailsService, bCryptPasswordEncoder());
//    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        //AuthenticationManager 에 등록
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

}
