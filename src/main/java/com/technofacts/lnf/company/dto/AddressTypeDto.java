package com.technofacts.lnf.company.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressTypeDto {

    @NotNull(message = "type cannot be null")
    @NotBlank(message = "type cannot be blank")
    private String type;

    @NotNull(message = "description cannot be null")
    @NotBlank(message = "description cannot be blank")
    private String description;
}
