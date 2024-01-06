package com.technofacts.lnf.company.restapi;

import com.technofacts.lnf.dto.email.ThymeleafDocumentDto;
import com.technofacts.lnf.dto.email.ThymeleafEmailDto;
import com.technofacts.lnf.service.email.ThymeleafDocumentService;
import com.technofacts.lnf.service.email.ThymeleafEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class EmailClientImpl implements ThymeleafDocumentService, ThymeleafEmailService {

    private final WebClient webClient;

    @Autowired
    public EmailClientImpl(@Qualifier("emailService") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public byte[] generatePdf(ThymeleafDocumentDto resource) {
        return webClient.post()
                .uri("/lnf/pdf")
                .body(BodyInserters.fromValue(resource))
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }

    @Override
    public void sendEmail(ThymeleafEmailDto resource) {
        // To be implemented

    }

    @Override
    public void sendEmailWithPdf(ThymeleafEmailDto resource) {
        // To be implemented
    }
}