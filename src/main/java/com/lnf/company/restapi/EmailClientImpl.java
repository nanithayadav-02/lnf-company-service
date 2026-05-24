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

package com.lnf.company.restapi;

import com.lnf.dto.email.ThymeleafDocumentDto;
import com.lnf.dto.email.ThymeleafEmailDto;
import com.lnf.dto.email.ThymeleafEmailsDto;
import com.lnf.exception.LnFException;
import org.springframework.web.multipart.MultipartFile;
import com.lnf.service.email.ThymeleafDocumentService;
import com.lnf.service.email.ThymeleafEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class EmailClientImpl extends BaseWebClientService implements ThymeleafDocumentService, ThymeleafEmailService {

    private final WebClient webClient;

    public EmailClientImpl(@Qualifier("emailWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public byte[] generatePdf(ThymeleafDocumentDto resource) {
        try {
            WebClient.RequestHeadersSpec<?> spec = webClient.post()
                    .uri("/lnf/pdf")
                    .body(BodyInserters.fromValue(resource));

            // Conditionally add the JWT token to the request headers
            addJwtToken(spec);

            // Execute the request and block to get the response
            return spec.retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception ex) {
            log.error("Failed to generate Pdf {}", ex.getMessage());
            throw new LnFException("Failed to generate Pdf with exception: ", ex);
        }
    }

    @Override
    public void sendEmail(ThymeleafEmailDto resource) {
        // To be implemented
    }

    @Override
    public void sendEmailWithPdf(ThymeleafEmailDto resource) {
        // To be implemented
    }

    @Override
    public void sendEmailWithPdfs(ThymeleafEmailsDto resource) {
        // To be implemented
    }

    @Override
    public void sendEmailWithPdfs(ThymeleafEmailsDto resource, MultipartFile[] attachments) {
        // To be implemented
    }

}
