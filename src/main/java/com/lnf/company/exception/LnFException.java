/*
 *
 *  * Copyright © 2024 Lever And Fulcrum Solutions (hereinafter referred to as "LNF").
 *  * All rights reserved.
 *  *
 *  * This source code is the proprietary property of LNF
 *  *
 *  * Unauthorized copying, redistribution, or modification of this code,
 *  * via any medium, is strictly prohibited unless expressly authorized
 *  * in writing by LNF.
 *  *
 *  * This code is confidential and intended solely for the use of LNF
 *  * and its authorized personnel.
 *
 */

package com.lnf.company.exception;

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
