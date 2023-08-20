package com.technofacts.lnf.company.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.technofacts.lnf.company.converter.AccountConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Account;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.repository.AccountRepository;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.dto.company.AccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class AccountService {

    private final AccountRepository repository;
    private final CompanyRepository companyRepository;

    public List<AccountDto> findAll() {
        List<Account> entities = repository.findAll();
        return entities.stream().map(AccountConverter::toTransportModel)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<AccountDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<Account> entities = repository.findByCompanyId(companyId);
        return entities.stream().map(AccountConverter::toTransportModel)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public AccountDto findById(UUID companyId, UUID accountId) {
        searchForCompany(companyId);
        return AccountConverter.toTransportModel(searchForAccount(accountId));
    }

    public void create(UUID companyId, List<AccountDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create account for company [%s] with null payload", companyId));
        Company company = searchForCompany(companyId);
        List<Account> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(accountDto -> {
            Account entity = AccountConverter.toEntityModel(accountDto);
            entity.setCompany(company);
            entities.add(entity);
        });
        save(entities);
        log.info(() -> "Account for company[" + companyId + "] successfully created");
    }

    public void create(UUID companyId, AccountDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create the account for company [%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        Account entity = AccountConverter.toEntityModel(resource);
        entity.setCompany(companyEntity);
        save(entity);
        log.info(() -> String.format("Account for company[%s] successfully created", companyId));
    }

    public void update(UUID companyId, UUID addressId, AccountDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to update the account for company [%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        searchForAccount(addressId);
        Account updatedEntity = AccountConverter.toEntityModel(resource);
        updatedEntity.setCompany(companyEntity);
        save(updatedEntity);
        log.info(() -> String.format("Account for company[%s] successfully created", companyId));
    }

    public void deleteById(UUID companyId, UUID addressId) {
        searchForCompany(companyId);
        Account entity = searchForAccount(addressId);
        try {
            repository.delete(entity);
            log.info(() -> String.format("Account[%s] for company [%s] successfully deleted", addressId, companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete account[[%s] for company [%s]", addressId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<Account> entities = repository.findByCompanyId(companyId);
        try {
            repository.deleteAll(entities);
            log.info(() -> String.format("Account for Company[%s] successfully deleted", companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete account for company [%s]", companyId);
            throw new LnFException(errorMessage);
        }
    }

    private void save(Account entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save account for company [%s]",
                    entity.getCompany().getCode());
            throw new LnFException(errorMessage);
        }
    }

    private void save(List<Account> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save account for company [%s]",
                    entities.get(0).getCompany().getCode());
            throw new LnFException(errorMessage);
        }
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist",
                        companyId)));
    }

    private Account searchForAccount(UUID accountId) {
        return repository.findById(accountId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Account with id [%s] does not exist",
                        accountId)));
    }

}
