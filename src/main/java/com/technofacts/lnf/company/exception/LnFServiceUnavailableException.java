package com.technofacts.lnf.company.exception;

public final class LnFServiceUnavailableException extends LnFException {

    public LnFServiceUnavailableException(String message) {
        super(LnFExceptionType.SERVICE_UNAVAILABLE_EXCEPTION, message);
    }

}
