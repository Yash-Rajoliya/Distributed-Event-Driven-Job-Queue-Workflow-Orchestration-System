package com.djqueue.common.exceptions;

public class RetryExceededException extends RuntimeException {
    public RetryExceededException(String msg) {
        super(msg);
    }
}