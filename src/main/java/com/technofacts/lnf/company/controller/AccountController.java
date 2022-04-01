package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.AccountService;
import com.technofacts.lnf.dto.company.AccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class AccountController {

    private final AccountService service;

    @GetMapping(value = "/company/{companyId}/account")
    public List<AccountDto> findByCompanyId(@PathVariable("companyId") final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/account/{accountId}")
    public AccountDto findById(@PathVariable("companyId") final UUID companyId,
                               @PathVariable("accountId") final UUID accountId) {
        return service.findById(companyId, accountId);
    }

    @PostMapping(value = "/company/{companyId}/account")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId, @RequestBody final AccountDto resource) {
        service.create(companyId, resource);
    }

    @PutMapping(value = "/company/{companyId}/account/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId,
                       @PathVariable("accountId") final UUID accountId,
                       @RequestBody final AccountDto resource) {
        service.update(companyId, accountId, resource);
    }

    @DeleteMapping(value = "/company/{companyId}/account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/account/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId,
                       @PathVariable("accountId") final UUID accountId) {
        service.deleteById(companyId, accountId);
    }
    
}
