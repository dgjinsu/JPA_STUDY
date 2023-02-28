package jpabook.jpashop.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception{

    private String message;

    public CustomException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    //    private ExceptionClass exceptionClass;
//    private HttpStatus httpStatus;
//
//    public CustomException(ExceptionClass exceptionClass, HttpStatus httpStatus, String message) {
//        super(message);
//        this.exceptionClass = exceptionClass;
//        this.httpStatus = httpStatus;
//    }
//
//    public int getHttpStatusCode() {
//        return httpStatus.value();
//    }
//
//    public String getHttpStatusType() {
//        return httpStatus.getReasonPhrase();
//    }
//
//    public HttpStatus getHttpStatus() {
//        return httpStatus;
//    }
}
