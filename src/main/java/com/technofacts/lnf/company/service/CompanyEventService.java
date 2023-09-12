package com.technofacts.lnf.company.service;

import com.google.common.collect.Lists;
import com.technofacts.lnf.company.converter.CompanyEventConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.CompanyEvent;
import com.technofacts.lnf.company.model.enums.EventType;
import com.technofacts.lnf.company.repository.CompanyEventRepository;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.company.util.RestUtil;
import com.technofacts.lnf.dto.company.CompanyEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class CompanyEventService {

    private final CompanyEventRepository companyEventRepository;
    private final CompanyRepository companyRepository;

    public Page<CompanyEventDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<CompanyEvent> resultPage = companyEventRepository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    public List<CompanyEventDto> findAll() {
        List<CompanyEvent> entities = companyEventRepository.findAll();
        return entities.stream().map(CompanyEventConverter::toTransportModel).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Page<CompanyEventDto> findPaginated(int page, int size) {
        Page<CompanyEvent> resultPage = companyEventRepository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }


    public List<CompanyEventDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<CompanyEvent> entities = Lists.newArrayList(companyEventRepository.findAll(sortInfo));
        return entities.stream().map(CompanyEventConverter::toTransportModel).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Page<CompanyEventDto> validateAndGetPages(int page, Page<CompanyEvent> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(String.format("Total number of pages [%d], " + "requested page [%d] does not exist", resultPage.getTotalPages(), page));
        }
        return resultPage.map(CompanyEventConverter::toTransportModel);
    }


    public List<CompanyEventDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyEvent> entities = companyEventRepository.findByCompanyId(companyId);
        return entities.stream().map(CompanyEventConverter::toTransportModel).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist", companyId)));
    }

    public void create(UUID companyId, CompanyEventDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, String.format("Failed to create companyEvent for company [%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        CompanyEvent entity = CompanyEventConverter.toEntityModel(resource, new CompanyEvent());
        entity.setCompany(companyEntity);
        save(entity);
        log.info(() -> String.format("CompanyEvent for Company[%s] successfully created", companyId));
    }


    public void create(UUID companyId, List<CompanyEventDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, String.format("Failed to create companyEvent for company [%s] with null payload", companyId));
        Company company = searchForCompany(companyId);
        List<CompanyEvent> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(companyEventDto -> {
            CompanyEvent entity = CompanyEventConverter.toEntityModel(companyEventDto, new CompanyEvent());
            entity.setCompany(company);
            entities.add(entity);
        });
        save(entities);
        log.info(() -> String.format("companyEvents for Company[%s] successfully created", companyId));
    }

    private void save(CompanyEvent entity) {
        try {
            companyEventRepository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save companyEvent for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private void save(List<CompanyEvent> entities) {
        try {
            companyEventRepository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save companyEvent for company [%s]", entities.get(0).getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private CompanyEvent searchForCompanyEvent(UUID eventId) {
        return companyEventRepository.findById(eventId).orElseThrow(() -> new LnFEntityNotFoundException(String.format("companyEvent with id [%s] does not exist", eventId)));
    }

    public CompanyEventDto findById(UUID companyId, UUID eventId) {
        searchForCompany(companyId);
        return CompanyEventConverter.toTransportModel(searchForCompanyEvent(eventId));
    }

    public void update(UUID companyId, UUID eventId, CompanyEventDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, String.format("Failed to create companyEvent for company [%s] with null payload", companyId));
        searchForCompany(companyId);
        CompanyEvent entity = searchForCompanyEvent(eventId);
        save(CompanyEventConverter.toEntityModel(resource, entity));
        log.info(() -> String.format("companyEvent for Company[%s] successfully created", companyId));
    }


    public void deleteById(UUID companyId, UUID eventId) {
        searchForCompany(companyId);
        CompanyEvent entity = searchForCompanyEvent(eventId);
        try {
            companyEventRepository.delete(entity);
            log.info(() -> String.format("CompanyEvent[%s] for company [%s] successfully deleted", eventId, companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete CompanyEvent[[%s] for company [%s]", eventId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyEvent> entities = companyEventRepository.findByCompanyId(companyId);
        try {
            companyEventRepository.deleteAll(entities);
            log.info(() -> String.format("Notes for company[%s] successfully deleted", companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete event for company [%s]", companyId);
            throw new LnFException(errorMessage);
        }
    }
}



