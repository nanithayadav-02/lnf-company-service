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

import com.lnf.company.model.enums.EventType;
import com.lnf.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@ToString
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company_event")
public class CompanyEvent extends AuditableEntity {


    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(name = "date_and_time", nullable = false)
    private LocalDate dateAndTime;

    @Column(name = "assign_to", nullable = false)
    private String assignTo;

    @Column(name = "event_description", nullable = false)
    private String eventDescription;

    @Column(name = "status", nullable = false)
    private String status;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company company;

}
