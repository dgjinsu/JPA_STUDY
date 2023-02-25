## 스프링 부트의 예외 처리 방식

스프링 부트의 예외 처리 방식은 크게 2가지
- @ControllerAdvice 를 통한 모든 Controller에서 발생할 수 있는 예외 처리 (예외 발생 시 json 형태로 결과를 반환하기 위해선 @RestControllerAdvice 사용하면 됨)
- @ExceptionHandler 를 통한 특정 Controller의 예외 처리

즉 @ControllerAdvice로 모든 컨트롤러에서 발생할 예외를 정의하고, @ExceptionHandler를 통해 발생하는 예외마다 처리할 메소드를 정의

![image](https://user-images.githubusercontent.com/97269799/221368477-0089e7b5-bb1d-4ec6-a343-42ae05a1fbef.png)


### @ExceptionHandler
* 예외 처리 상황이 발생하면 해당 Handler로 처리하겠다고 명시하는 어노테이션
* 괄호에 어떤 ExceptionClass를 처리할지 설정할 수 있음
  * @ExceptionHandler(OOException.class)
* 글로벌 예외처리 보다 컨트롤러 예외처리가 우선순위가 높음

```java
@Slf4j
@RestControllerAdvice
public class ApiExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> exceptionHandler(Exception e) {
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        log.info(e.getLocalizedMessage());
        log.info("전역 컨트롤러 내 예외 핸들러 호출");

        Map<String, String> map = new HashMap<>();
        map.put("error type", httpStatus.getReasonPhrase());
        map.put("code", "400");
        map.put("message", "에러 발생");
        return new ResponseEntity<>(map, responseHeaders, httpStatus);
    }
}
```
에러 시 아래와 같은 응답이 옴 (code, type, message)
![image](https://user-images.githubusercontent.com/97269799/221370785-bcbc8318-adf2-4684-a788-f25c9c3b3811.png)


## Custom Exception


