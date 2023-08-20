package com.technofacts.lnf.company.model;

import com.technofacts.lnf.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company_policy", uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "name"}))
public class CompanyPolicy extends AuditableEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "content", nullable = false)
    private byte[] content;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", referencedColumnName="id", nullable = false)
    private Company company;

}
