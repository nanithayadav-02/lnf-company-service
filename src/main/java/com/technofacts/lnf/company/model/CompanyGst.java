package com.technofacts.lnf.company.model;

import lombok.*;

import javax.persistence.*;

@ToString
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company_gst", uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "number"}))
public class CompanyGst extends AuditableEntity {

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "number", nullable = false)
    private String number;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="company_id", referencedColumnName="id", nullable = false)
    private Company company;

}
