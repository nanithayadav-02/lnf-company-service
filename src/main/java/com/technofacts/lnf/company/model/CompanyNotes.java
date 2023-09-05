package com.technofacts.lnf.company.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company_notes")
public class CompanyNotes extends AuditableEntity {

    @Column(name = "notes", nullable = false)
    private String notes;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="company_id", referencedColumnName="id",  nullable = false)
    private Company company;

}
