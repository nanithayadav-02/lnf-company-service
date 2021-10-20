package com.technofacts.lnf.company.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@ToString
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company_image")
public class Image extends AuditableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "content", nullable = false)
    private byte[] content;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

}