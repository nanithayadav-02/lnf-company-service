package com.lnf.company.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false, name = "status")
    private String status;
}
