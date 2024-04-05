package com.technofacts.lnf.company.model;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company_address", uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "type"}))
public class CompanyAddress extends Address {

    @Column(name = "branch_name")
    private String branch_name;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id",  nullable = false)
    private Company company;

}
