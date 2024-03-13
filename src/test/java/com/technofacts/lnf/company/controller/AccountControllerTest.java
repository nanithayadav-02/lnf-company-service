package com.technofacts.lnf.company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.technofacts.lnf.company.service.AccountService;
import com.technofacts.lnf.dto.company.AccountDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountService service;
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

        List<AccountDto> expectedDto = Arrays.asList(mockAccount1(),mockAccount2());

        given(service.findByCompanyId (any(UUID.class))).willReturn(expectedDto);

        String url = "/lnf/company/" + companyId + "/account";

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-accounts.json")
                        .toURI())));

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent, true));

        verify(service, times(1)).findByCompanyId (any(UUID.class));

    }

    @Test
    void findByCompanyIdAndId () throws Exception {
        UUID id = UUID.fromString("d8e3c50a-6adc-486a-a8de-126fb77cee41");
        AccountDto expectedDto = mockAccount1();

        given(service.findById(any(UUID.class), any(UUID.class))).willReturn(expectedDto);

        String url = "/lnf/company/" + companyId + "/account/" + id;

        performAndVerifyGet(url, status().isOk(), id.toString(), expectedDto);

        verify(service, times(1)).findById(any(UUID.class), any(UUID.class));
    }

    @Test
    void createAccounts() {
        // Arrange
        List<AccountDto> mockAccount = List.of(mockAccount1(), mockAccount2());
        doNothing().when(service).create(companyId, mockAccount);
        String url = "/lnf/company/" + companyId + "/accounts";

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AccountDto>> captor = ArgumentCaptor.forClass(List.class);

        // Act
        try {
            mockMvc.perform (post(url)
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(mockAccount)))
                    .andExpect (status().isCreated());
        } catch (Exception e) {
            fail ("Unexpected exception: " + e.getMessage());
        }

        // Assert
        verify (service).create(eq(companyId), captor.capture());
        List<AccountDto> actualAccounts = captor.getValue();

        // Check if the lists have the same size
        assertEquals(actualAccounts.size(), actualAccounts.size(), "The number of accounts created should match");

        // Check if the details of each address match
        for (int i = 0; i < actualAccounts.size(); i++) {
            assertEquals (actualAccounts.get(i).getAddress(), actualAccounts.get(i).getAddress(),
                    "Address should match for account at index " + i);
            assertEquals (actualAccounts.get(i).getBranch(), actualAccounts.get(i).getBranch(),
                    "branch should match for account at index " + i);
            assertEquals (actualAccounts.get(i).getIbanNumber(), actualAccounts.get(i).getIbanNumber(),
                    "IBanNumber should match for account at index " + i);
            assertEquals (actualAccounts.get(i).getIfscCode(), actualAccounts.get(i).getIfscCode(),
                    "IfscCode should match for account at index " + i);
            assertEquals (actualAccounts.get(i).getNumber(), actualAccounts.get(i).getNumber(),
                    "Number should match for account at index " + i);
        }
    }

    @Test
    void createAccount() {
        AccountDto requestDto = mockAccount1();

        String url = "/lnf/company/" + companyId + "/account";

        doNothing().when(service).create(eq(companyId), any(AccountDto.class));

        try {
            mockMvc.perform(post(url)
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(requestDto)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        verify(service, times(1)).create(eq(companyId), any(AccountDto.class));
    }

    @Test
    void updateAccount() {
        UUID id = UUID.randomUUID();

        AccountDto updatedAccount = mockAccount2 ();
        updatedAccount.setId(companyId);

        Mockito.doNothing().when(service).update(Mockito.eq(companyId),eq(id), Mockito.any(AccountDto.class));
        String urlTemplate = String.format("/lnf/company/%s/account/%s", companyId, id);
        ArgumentCaptor<AccountDto> captor = ArgumentCaptor.forClass(AccountDto.class);

        // Act
        try {
            mockMvc.perform(put(urlTemplate)
                            .content(asJsonString(updatedAccount)) // Convert AccountDto to JSON string
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        //verify the service
        verify(service).update(eq(companyId), eq(id), any(AccountDto.class));

        // Assert
        Mockito.verify(service, times(1)).update(eq(companyId), eq(id), captor.capture());
        AccountDto actualType = captor.getValue();

        assertEquals(updatedAccount.getId(), actualType.getId(), "Account IDs should match");
        assertEquals(updatedAccount.getBranch (), actualType.getBranch (), "Account branch should match");
        assertEquals(updatedAccount.getNumber (), actualType.getNumber (), "Account number should match");
        assertEquals(updatedAccount.getIfscCode (), actualType.getIfscCode (), "Account ifscCode should match");
        assertEquals(updatedAccount.getIbanNumber (), actualType.getIbanNumber (), "Account iBanNumber should match");
        assertEquals(updatedAccount.getAddress (), actualType.getAddress (), "Account address should match");
    }

    @Test
    void deleteByAccountId() throws Exception {
        String url = "/lnf/company/" + companyId + "/account";

        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deleteByCompanyId(companyId);
    }

    @Test
    void deleteByAccountIdAndId() throws Exception {
        UUID id = UUID.randomUUID();
        String urlTemplate = String.format("/lnf/company/%s/account/%s", companyId, id);

        mockMvc.perform(delete(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deleteById(companyId, id);
    }

    private AccountDto mockAccount1() {
        return createAccount("d8e3c50a-6adc-486a-a8de-126fb77cee41", "00461010055186", "srinagar", "SBI562365",
                "DE56501201000000484637","Near metro pillar TG");
    }

    private AccountDto mockAccount2() {
        return createAccount("2c3d8b47-83d7-4e6c-9fb7-9845cfb2f157", "919652597554", "kukatphally", "SBI123433",
                "MT25501201000000484637","Near aditya school TG");
    }

    private AccountDto createAccount(String id, String number, String branch, String ifscCode, String ibanNumber, String address) {
        AccountDto dto = new AccountDto();
        dto.setId(UUID.fromString(id));
        dto.setNumber(number);
        dto.setIfscCode(ifscCode);
        dto.setBranch(branch);
        dto.setIbanNumber(ibanNumber);
        dto.setAddress(address);

        return dto;
    }

    private void performAndVerifyGet(String url, ResultMatcher statusMatcher, String containsString,
                                     AccountDto expectedDto) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(statusMatcher)
                .andExpect(content().string(containsString(containsString))) // Validate the ID
                .andExpect(jsonPath("$.id").value(containsString)) // Additional, more specific validation
                .andExpect(jsonPath("$.number").value(expectedDto.getNumber()))
                .andExpect(jsonPath("$.ifscCode").value(expectedDto.getIfscCode()))
                .andExpect(jsonPath("$.branch").value(expectedDto.getBranch()))
                .andExpect(jsonPath("$.ibanNumber").value(expectedDto.getIbanNumber()))
                .andExpect(jsonPath("$.address").value(expectedDto.getAddress()));

    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper ()
                    .registerModule(new JavaTimeModule ())
                    .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
