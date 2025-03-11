package com.lnf.company.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@ToString
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company_lnf_plan_audit")
public class CompanyPlanAudit extends AuditableEntity {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_lnf_plan_id", referencedColumnName = "id", nullable = false)
    private CompanyPlan companyPlan;

    @Column(name = "start_date")
    private Date startDate;

    @Column(nullable = false, name = "status")
    private String status;
}
