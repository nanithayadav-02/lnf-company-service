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
public class ImageDto {

    private UUID id;

    @NotNull(message = "name cannot be null")
    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotNull(message = "size cannot be null")
    @NotBlank(message = "size cannot be blank")
    private Long size;

    @NotNull(message = "url cannot be null")
    @NotBlank(message = "url cannot be blank")
    private String url;

    @NotNull(message = "contentType cannot be null")
    @NotBlank(message = "contentType cannot be blank")
    private String contentType;

    @NotNull(message = "content cannot be null")
    @NotBlank(message = "content cannot be blank")
    private byte[] content;

}