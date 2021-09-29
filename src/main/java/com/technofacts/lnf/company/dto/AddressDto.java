package com.technofacts.lnf.company.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private UUID id;

    @NotNull(message = "addressLine1 cannot be null")
    @NotBlank(message = "addressLine1 cannot be blank")
    private String addressLine1;

    private String addressLine2;

    private String town;

    @NotNull(message = "city cannot be null")
    @NotBlank(message = "city cannot be blank")
    private String city;

    @NotNull(message = "state cannot be null")
    @NotBlank(message = "state cannot be blank")
    private String state;

    @NotNull(message = "postCode cannot be null")
    @NotBlank(message = "postCode cannot be blank")
    private String postCode;

    @NotNull(message = "country cannot be null")
    @NotBlank(message = "country cannot be blank")
    private String country;

    private String branchName;


    @NotNull(message = "Address type cannot be null")
    @NotBlank(message = "Address type cannot be blank")
    private String type;

}
