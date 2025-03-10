package com.lnf.company.model;

import com.lnf.company.model.enums.CompanyLnfPlanStatus;
import com.lnf.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
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
public class CompanyLnfPlan extends AuditableEntity {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company company;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lnf_plan_id", referencedColumnName = "id", nullable = false)
    private LnfPlan lnfPlan;

    @Column(name = "start_date")
    private Date startDate;

    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    private CompanyLnfPlanStatus status;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "companyLnfPlan", cascade = CascadeType.ALL)
    private Set<CompanyLnfPlanAudit> CompanyLnfPlanAudits = new HashSet<>();

}
