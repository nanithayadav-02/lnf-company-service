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

public enum LnFExceptionType {

    UNKNOWN_EXCEPTION("LNF-ERR-0000"),
    ENTITY_NOT_FOUND_EXCEPTION("LNF-ERR-0001"),
    INVALID_REQUEST_EXCEPTION("LNF-ERR-0002"),
    SERVICE_UNAVAILABLE_EXCEPTION("LNF-ERR-0003");

    private final String code;

    public String getCode() { return code; }

    LnFExceptionType(String code) { this.code = code; }
}
