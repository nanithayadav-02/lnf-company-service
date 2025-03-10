package com.lnf.company.model;

import com.lnf.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@ToString
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lnf_plan")
public class LnfPlan extends AuditableEntity {

    @Column(name = "plan_name", nullable = false, unique = true)
    private String planName;

    @Column(name = "description")
    private String description;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "lnfPlan", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<CompanyLnfPlan> companyLnfPlans = new HashSet<>();

}
