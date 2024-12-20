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

package com.lnf.company.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfiguration {

    @Value("${file.service.url}")
    private String fileServiceUrl;

    @Value("${email.service.url}")
    private String emailServiceUrl;

    @Value("${application.maxInMemorySize}")
    private int maxInMemorySize;

    @Value("${connection.timeout}")
    private int timeOut;

    @Bean
    WebClient emailWebClient() {
        return createWebClient(emailServiceUrl);
    }

    @Bean
    WebClient fileWebClient() {
        return createWebClient(fileServiceUrl);
    }

    private WebClient createWebClient(String baseUrl) {

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(maxInMemorySize))
                .build();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOut)
                .responseTimeout(Duration.ofMillis(timeOut))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(timeOut, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(timeOut, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

}
