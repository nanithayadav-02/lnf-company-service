package com.technofacts.lnf.company.exception;

public class LnFException extends RuntimeException {

    protected final LnFExceptionType exceptionType;

    public LnFExceptionType getExceptionType() { return exceptionType; }

    protected LnFException(LnFExceptionType exceptionType, String message) {
        super(message);
        this.exceptionType = exceptionType;
    }

    public LnFException(String message) {
        super(message);
        this.exceptionType = LnFExceptionType.UNKNOWN_EXCEPTION;
    }

    public LnFException(String message, Throwable cause) {
        super(message, cause);
        this.exceptionType = LnFExceptionType.UNKNOWN_EXCEPTION;
    }
}
