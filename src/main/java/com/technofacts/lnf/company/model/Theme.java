package com.technofacts.lnf.company.model;

import com.technofacts.lnf.company.model.enums.ThemeType;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "theme")
public class Theme extends AuditableEntity {

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ThemeType type;

    @Column(name = "value", nullable = false)
    private String value;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", referencedColumnName="id", nullable = false)
    private Company company;

}
