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

import java.util.function.Predicate;

public final class LnFBadRequestException extends LnFException {

    public LnFBadRequestException(String message) {
        super(LnFExceptionType.INVALID_REQUEST_EXCEPTION, message);
    }

    public static <T> void throwOnCondition(Predicate<T> predicate, T toTest, String message) {
        if (predicate.test(toTest)) {
            throw new LnFBadRequestException(message);
        }
    }
}
