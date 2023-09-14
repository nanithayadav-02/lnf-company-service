package com.technofacts.lnf.company.model;


import com.technofacts.lnf.company.model.enums.ComponentStatus;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payroll_configuration")
public class PayrollConfiguration extends AuditableEntity{

    @Column(name = "component_code", nullable = false)
    private String componentCode;

    @Column(name = "component_name", nullable = false)
    private String componentName;

    @Column(name = "component_value", nullable = false)
    private String  componentValue;

    @Column(name = "percentage", nullable = false)
    private Double percentage;

    @Column(name = "financial_year", nullable = false)
    private String financialYear;

    @Column(name = "componentStatus",nullable = false)
    @Enumerated(EnumType.STRING)
    private ComponentStatus componentStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id",referencedColumnName="id", nullable = false)
    private Company company;
}
