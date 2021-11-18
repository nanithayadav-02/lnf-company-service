package com.technofacts.lnf.company.model;

import lombok.*;

import javax.persistence.*;

@ToString
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company_Account", uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "number"}))
public class Account extends AuditableEntity {

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String branch;

    @Column(nullable = false)
    private String ifscCode;

    @Column(nullable = false)
    private String ibanNumber;

    @Column(nullable = false)
    private String address;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id",  nullable = false)
    private Company company;

}
