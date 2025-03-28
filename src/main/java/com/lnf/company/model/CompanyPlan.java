package com.lnf.company.model;

import com.lnf.company.model.enums.CompanyPlanStatus;
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
@Table(name = "company_lnf_plan")
public class CompanyPlan extends AuditableEntity {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company company;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lnf_plan_id", referencedColumnName = "id", nullable = false)
    private Plan plan;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    private CompanyPlanStatus status;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "companyPlan", cascade = CascadeType.ALL)
    private Set<CompanyPlanAudit> CompanyPlanAudits = new HashSet<>();

}
