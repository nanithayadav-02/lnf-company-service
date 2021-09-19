package com.technofacts.lnf.company.exception;

public final class LnFEntityNotFoundException extends LnFException {

    public LnFEntityNotFoundException(String message) {
        super(LnFExceptionType.ENTITY_NOT_FOUND_EXCEPTION, message);
    }

}
