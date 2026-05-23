package com.djqueue.common.exceptions;

public class JobProcessingException extends RuntimeException {
    public JobProcessingException(String msg) {
        super(msg);
    }
}