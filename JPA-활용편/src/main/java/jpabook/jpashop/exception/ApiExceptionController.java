package jpabook.jpashop.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ApiExceptionController {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, String>> exceptionHandler2(CustomException e) {
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        Map<String, String> map = new HashMap<>();
//        map.put("error type", e.getHttpStatusType());
//        map.put("error code", Integer.toString(e.getHttpStatusCode()));
        map.put("message", e.getMessage());

        return new ResponseEntity<>(map, responseHeaders, httpStatus);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> exceptionHandler3(UsernameNotFoundException e) {

        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        log.info(e.getLocalizedMessage());
        log.info("전역 컨트롤러 내 예외 핸들러 호출");
        System.out.println(e.getMessage());


        Map<String, String> map = new HashMap<>();
        map.put("message", "이름에 해당하는 엔티티 없음");
        return new ResponseEntity<>(map, responseHeaders, httpStatus); //responseBody / ResponseHead / Status
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> exceptionHandler4(BadCredentialsException e) {
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        log.info(e.getLocalizedMessage());
        log.info("전역 컨트롤러 내 예외 핸들러 호출");
        System.out.println(e.getMessage());

        Map<String, String> map = new HashMap<>();
        map.put("message", "비밀번호 틀림");
        return new ResponseEntity<>(map, responseHeaders, httpStatus); //responseBody / ResponseHead / Status
    }
}
