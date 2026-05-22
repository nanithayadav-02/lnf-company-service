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

import com.lnf.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@ToString
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company")
public class Company extends AuditableEntity {

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String mobile;

    @Column
    private String website;

    @Column(name = "business_category")
    private String businessCategory;

    @Column(name = "business_description")
    private String businessDescription;

    @Column(unique = true, nullable = false)
    private String pan;

    @Column
    private String arn;

    @Column(name = "arn_issue_date")
    private LocalDate arnIssueDate;

    @Column(name = "sac_code")
    private Long sacCode;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<CompanyAddress> address = new HashSet<>();

    @ToString.Exclude
    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
    private Image image;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<CompanyGst> gst = new HashSet<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<Theme> theme = new HashSet<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<CompanyPolicy> policies = new HashSet<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<CompanyNotes> companyNotes = new HashSet<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<CompanyEvent> companyEvent = new HashSet<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<CompanyHoliday> companyHoliday = new HashSet<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<CompanyFile> companyFile = new HashSet<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<CompanyPlan> companyPlans = new HashSet<>();

}
