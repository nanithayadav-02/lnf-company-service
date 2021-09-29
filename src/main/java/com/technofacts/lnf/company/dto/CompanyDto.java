package com.technofacts.lnf.company.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.*;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {

    private UUID id;

    @NotNull(message = "companyId cannot be null")
    @NotBlank(message = "companyId cannot be blank")
    private String companyId;

    @NotNull(message = "name cannot be null")
    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotNull(message = "status cannot be null")
    @NotBlank(message = "status cannot be blank")
    private String status;

    @NotNull(message = "email cannot be null")
    @NotBlank(message = "email cannot be blank")
    private String email;

    @NotNull(message = "telephone cannot be null")
    @NotBlank(message = "telephone cannot be blank")
    private String telephone;

    private String mobile;

    private String website;

    private String businessCategory;

    private String businessDescription;

    @NotNull(message = "pan cannot be null")
    @NotBlank(message = "pan cannot be blank")
    private String pan;

    private String arn;

    private LocalDate arnIssueDate;

    private Long sacCode;

    private List<AddressDto> address = new ArrayList<>();

}
