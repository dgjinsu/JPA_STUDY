
### 기존의 세션 방식
![image](https://user-images.githubusercontent.com/97269799/224673072-0af41a45-3830-4667-aecc-ff567c62203d.png)

* 단점
  * 클라이언트가 너무 많으면 서버가 여러개 필요
  * 로드밸런싱을 통해 서버를 여러개 두는데
  * 로그인 요청할 땐 A 서버에 가서 세션이 생성됐는데 다음 요청때 B로 가면 안 됨
  * 따라서 같은 곳으로 가게 따로 설정이 필요함
  * 이를 해결하기 위해 redis 를 사용하여 세션을 한곳에 모으는 램 공간을 만듦
  
  -> 이런 세션의 문제점들은 JWT 를 사용하여 해결할 수 있다.
  
* 2가지를 해결해야함
1. 열쇠 전달 문제 (A -> B로 문서를 보낼 때 금고에 문서를 넣어서 보낸다 하자. 잘 보내 져도 B도 열쇠를 갖고 있어야 함. 이때 열쇠는 어떻게 전달할 것인가)
2. 누구로 부터 전달됐는가 문제 (A->B 로 문서를 보낼 때 C가 가로챘다고 하자. 열쇠 전달 문제가 해결됐으면 C는 문서를 볼 수 없다. 하지만 새로운 문서를 만들어 B한테 보낼 순 있다. 따라서 누구로 부터 왔는가를 알아야 한다.


### RSA
* public key
* private key

A -> B
공개키(B)로 잠구면 B는 개인키(B)로 열어볼 수 있다 (암호와)
개인키로(A) 잠구면 B는 공개키(A)로 열어볼 수 있다 (전자서명)

상대방 공개키로 잠군 메세지를 자신의 개인키로 한번 더 잠굼
-> 상대방의 입장 : A의 공개키로 열어봤을 때 열리면 인증 해결, B의 개인키로 열어봐서 열리면 암호화 해결


## JWT
* jwt = json web token
* 당사자간에 정보를 json 객체로 안전하게 전송하기 위한 표
* jwt 는 HMAC 알고리즘 또는 RSA 또는 ECDSA를 사용하는 공개/개인 키 쌍을 사용

## 구조
xxxxx.yyyyy.zzzzz 구조
(header).(payload).(signature)


* 헤더
  * 어떤 알고리즘을 사용해서 서명했는지의 내용
* 페이로드
  * 등록된 클레임(데이터)
    * 필수는 아니지만 권장되는 미리 정의 된 클레임 집합
    * 발행자, 만료시간, 주제, 청중 등
  * 개인 클레임
    * userid 등을 넣는 
  * 서명
    * 해더 + 페이로드 + 개인키 를 HMAC 으로 암호화
> HMAC SHA256 암호화
![image](https://user-images.githubusercontent.com/97269799/224692628-c7dca5f8-be0a-41f3-8426-3452b4052db1.png)


> RSA 암호화 (이게 더 간단)
![image](https://user-images.githubusercontent.com/97269799/224692981-56099a8a-24bc-4f5a-9343-ce25e5e8aa7c.png)


이 방법으로 하면 서버1 이든 서버2 이든 세션을 검증할 필요가 없어진다. 
서버들이 코스라는 시크릿 키만 알고 있으면 된다

jwt 프로젝트 끝난 후 추가 
https://bcp0109.tistory.com/301


* jwt 에서 로그아웃은 redis 를 사용하여 로그아웃 처리를 해줘야 한다.
  * https://wildeveloperetrain.tistory.com/61
* redis 설치
  * https://inpa.tistory.com/entry/REDIS-%F0%9F%93%9A-Window10-%ED%99%98%EA%B2%BD%EC%97%90-Redis-%EC%84%A4%EC%B9%98%ED%95%98%EA%B8%B0
* 추가로 aws 배포할 때 따로 작업 필요.
