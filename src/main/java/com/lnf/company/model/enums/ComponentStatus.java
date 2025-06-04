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

package com.lnf.company.model.enums;

import lombok.Getter;

@Getter
public enum ComponentStatus {

    ACTIVE("Active"),

    IN_ACTIVE("In Active");

    private final String label;

    ComponentStatus(String label) {
        this.label = label;
    }

    public static ComponentStatus valueOfLabel(String label) {
        for (ComponentStatus at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }
}
