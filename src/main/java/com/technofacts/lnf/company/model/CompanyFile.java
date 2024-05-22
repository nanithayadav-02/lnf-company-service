package com.technofacts.lnf.company.model;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company_file")
public class CompanyFile extends AuditableEntity{

    @Column(name = "file_name", nullable = false)
    private String fileName;

    private String description;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id",  nullable = false)
    private Company company;

}
