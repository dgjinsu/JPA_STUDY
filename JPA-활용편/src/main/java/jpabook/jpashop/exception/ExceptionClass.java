package jpabook.jpashop.exception;

import lombok.Data;

public enum ExceptionClass {

    TEST("Test"), TEST2("Test2");

    private String exceptionClass;

    ExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }
}
