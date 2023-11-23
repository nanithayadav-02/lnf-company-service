package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.SalaryConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class ReloadConfigurationController {

    private final SalaryConfigurationService service;

    @PostMapping("/configuration/reload")
    @ResponseStatus(HttpStatus.CREATED)
    public void reloadConfiguration() {
        service.reloadCacheBySalaryConfigurations();
    }
}
