## Spring Security 동장 방식
![image](https://user-images.githubusercontent.com/97269799/221396757-07763bbf-bf7e-40ed-a0fc-66470ca0efda.png)

1. 사용자가 request 를 보낼 때 AuthenticationFilter가 받아 Token 생성
2. 토큰을 AuthenticationManager가 받아 이의 구현체인 Provider로 넘김
3. Provider는 PasswordEncoder을 통해 Hashed password를 받음
4. 또한 Provider가 UserDetailsService를 사용하여 db의 user,role 에 접근
5. UserDetailsService에서 loadUserByUsername()을 통해 UserDetails를 리턴받음
6. UserDetials의 password와 사용자가 넘겨준 password(Hashed password)를 비교하여 확인
7. 인증이 이루어지면 AuthenticationFilter안에 SecurityContext에 Authentication정보를 저장

