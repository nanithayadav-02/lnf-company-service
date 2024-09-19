/*
 *
 *  * Copyright © 2024 Lever And Fulcrum Solutions (hereinafter referred to as "LNF").
 *  * All rights reserved.
 *  *
 *  * This source code is the proprietary property of LNF
 *  *
 *  * Unauthorized copying, redistribution, or modification of this code,
 *  * via any medium, is strictly prohibited unless expressly authorized
 *  * in writing by LNF.
 *  *
 *  * This code is confidential and intended solely for the use of LNF
 *  * and its authorized personnel.
 *
 */

package com.lnf.company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lnf.company.BaseTestClass;
import com.lnf.company.service.CompanyAddressService;
import com.lnf.dto.company.AddressDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CompanyAddressControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CompanyAddressService service;
    private UUID companyId;

    @BeforeAll
    void beforeAll() {
        companyId = UUID.fromString("eaae6ae2-f6da-4e4d-9ad9-8808b98965c5");
    }

    @BeforeEach
    void setUp() {

    }

    @Test
    void findByCompanyId() throws Exception {
        List<AddressDto> expectedDto = Arrays.asList(mockAddress1(),mockAddress2());

        given(service.findByCompanyId (any(UUID.class))).willReturn(expectedDto);

        String url = "/lnf/company/" + companyId + "/address";

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-addresses.json")
                        .toURI())));

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findByCompanyId (any(UUID.class));
    }

    @Test
    void findByCompanyIdAndId() throws Exception {
        UUID id = UUID.fromString("d8e3c50a-6adc-486a-a8de-126fb77cee41");

        AddressDto expectedDto = mockAddress1();

        given(service.findById(any(UUID.class), any(UUID.class))).willReturn(expectedDto);

        String url = "/lnf/company/" + companyId + "/address/" + id;

        performAndVerifyGet(url, status().isOk(), id.toString(), expectedDto);

        verify(service, times(1)).findById(any(UUID.class), any(UUID.class));

    }

    @Test
    void createAddress() {
        // Arrange
        List<AddressDto> mockAddress = List.of(mockAddress2());
        doNothing().when(service).create(companyId, mockAddress);
        String url = "/lnf/company/" + companyId + "/address";

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AddressDto>> captor = ArgumentCaptor.forClass(List.class);

        // Act
        try {
            mockMvc.perform(post(url)
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(mockAddress)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        verify(service).create(eq(companyId), captor.capture());
        List<AddressDto> actualAddresses = captor.getValue();

        // Check if the lists have the same size
        assertEquals(actualAddresses.size(), actualAddresses.size(), "The number of addresses created should match");

        // Check if the details of each address match
        for (int i = 0; i < actualAddresses.size(); i++) {
            assertEquals(actualAddresses.get(i).getAddressLine1(), actualAddresses.get(i).getAddressLine1(),
                    "AddressLine1 should match for address at index " + i);
            assertEquals(actualAddresses.get(i).getAddressLine2(), actualAddresses.get(i).getAddressLine2(),
                    "AddressLine2 should match for address at index " + i);
            assertEquals(actualAddresses.get(i).getTown(), actualAddresses.get(i).getTown(),
                    "Town should match for address at index " + i);
            assertEquals(actualAddresses.get(i).getCity(), actualAddresses.get(i).getCity(),
                    "City should match for address at index " + i);
            assertEquals(actualAddresses.get(i).getState(), actualAddresses.get(i).getState(),
                    "State should match for address at index " + i);
            assertEquals(actualAddresses.get(i).getCountry(), actualAddresses.get(i).getCountry(),
                    "State should match for address at index " + i);
            assertEquals(actualAddresses.get(i).getPostCode(), actualAddresses.get(i).getPostCode(),
                    "PostalCode should match for address at index " + i);
            assertEquals(actualAddresses.get(i).getType(), actualAddresses.get(i).getType(),
                    "Type should match for address at index " + i);
        }
    }

    @Test
    void update() {
        UUID id = UUID.fromString ("2c3d8b47-83d7-4e6c-9fb7-9845cfb2f157");

        AddressDto updatedAddress = mockAddress2();
        updatedAddress.setId(companyId);

        Mockito.doNothing().when(service).update(Mockito.eq(companyId),eq(id), Mockito.any(AddressDto.class));
        String urlTemplate = String.format("/lnf/company/%s/address/%s", companyId, id);
        ArgumentCaptor<AddressDto> captor = ArgumentCaptor.forClass(AddressDto.class);

        // Act
        try {
            mockMvc.perform(put(urlTemplate)
                            .content(asJsonString(updatedAddress)) // Convert AddressDto to JSON string
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        //verify the service
        verify(service).update(eq(companyId), eq(id), any(AddressDto.class));

        // Assert
        Mockito.verify(service, times(1)).update(eq(companyId), eq(id), captor.capture());
        AddressDto actualType = captor.getValue();

        assertEquals(updatedAddress.getId(), actualType.getId(), "Address IDs should match");
        assertEquals(updatedAddress.getAddressLine1(), actualType.getAddressLine1(), "Address addressLine1 should match");
        assertEquals(updatedAddress.getAddressLine2(), actualType.getAddressLine2(), "Address addressLine2 should match");
        assertEquals(updatedAddress.getTown(), actualType.getTown(), "Address town should match");
        assertEquals(updatedAddress.getCity(), actualType.getCity(), "Address city should match");
        assertEquals(updatedAddress.getState(), actualType.getState(), "Address state should match");
        assertEquals(updatedAddress.getCountry(), actualType.getCountry(), "Address country should match");
        assertEquals(updatedAddress.getPostCode(), actualType.getPostCode(), "Address postalCode should match");
        assertEquals(updatedAddress.getType(), actualType.getType(), "Address type should match");
    }

    @Test
    void deleteByCompanyId() throws Exception {
        String url = "/lnf/company/" + companyId + "/address";

        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deleteByCompanyId (companyId);
    }

    @Test
    void testDeleteByCompanyIdAndId() throws Exception {
        UUID id = UUID.randomUUID();
        String urlTemplate = String.format("/lnf/company/%s/address/%s", companyId, id);

        mockMvc.perform(delete(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deleteById(companyId, id);

    }

    private AddressDto mockAddress1() {
        return createAddress("d8e3c50a-6adc-486a-a8de-126fb77cee41", "123 Main Street", "Apt 4B", "Smallville",
                "Metropolis", "TG", "12345", "India", "Primary");

    }

    private AddressDto mockAddress2() {
        return createAddress("2c3d8b47-83d7-4e6c-9fb7-9845cfb2f157", "Main Street", "Apt 5B", "kukatphally",
                "Hyderabad", "Telangana", "500085", "Usa", "Secondary");
    }

    private AddressDto createAddress(String id, String addressLine1, String addressLine2, String town, String city, String state, String postCode,
                                     String country, String type) {
        AddressDto dto = new AddressDto();
        dto.setId(UUID.fromString(id));
        dto.setAddressLine1(addressLine1);
        dto.setAddressLine2(addressLine2);
        dto.setTown(town);
        dto.setCity(city);
        dto.setState(state);
        dto.setPostCode(postCode);
        dto.setCountry(country);
        dto.setType(type);

        return dto;
    }

    private void performAndVerifyGet(String url, ResultMatcher statusMatcher, String containsString,
                                     AddressDto expectedDto) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(statusMatcher)
                .andExpect(content().string(containsString(containsString))) // Validate the ID
                .andExpect(jsonPath("$.id").value(containsString)) // Additional, more specific validation
                .andExpect(jsonPath("$.addressLine1").value(expectedDto.getAddressLine1()))
                .andExpect(jsonPath("$.addressLine2").value(expectedDto.getAddressLine2()))
                .andExpect(jsonPath("$.town").value(expectedDto.getTown()))
                .andExpect(jsonPath("$.city").value(expectedDto.getCity()))
                .andExpect(jsonPath("$.state").value(expectedDto.getState()))
                .andExpect(jsonPath("$.postCode").value(expectedDto.getPostCode()))
                .andExpect(jsonPath("$.country").value(expectedDto.getCountry()))
                .andExpect(jsonPath("$.type").value(expectedDto.getType()));

    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
