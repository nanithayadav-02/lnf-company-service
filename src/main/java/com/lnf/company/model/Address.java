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

package com.lnf.company.model;

import com.lnf.company.model.enums.AddressType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

@ToString
@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class Address extends AuditableEntity {

    @Column(nullable = false, name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "town")
    private String town;

    @Column(nullable = false, name = "city")
    private String city;

    @Column(nullable = false, name = "state")
    private String state;

    @Column(nullable = false, name = "post_code")
    private String postCode;

    @Column(nullable = false, name = "country")
    private String country;

    @Column(nullable = false, name = "type")
    @Enumerated(EnumType.STRING)
    private AddressType type;

}
