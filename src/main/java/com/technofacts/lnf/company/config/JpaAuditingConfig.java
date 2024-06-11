package com.technofacts.lnf.company.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    private static final String CLAIM = "email";
    private static final String DEFAULT_AUDITOR = "SYSTEM";

    @Bean
    public AuditorAware<String> auditorProvider() {
        return this::getAuditor;
    }

    private Optional<String> getAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return Optional.ofNullable(jwt.getClaimAsString(CLAIM));
        }
        return Optional.of(DEFAULT_AUDITOR);
    }
}
