package com.technofacts.lnf.company.service;

import com.google.common.collect.Lists;
import com.technofacts.lnf.company.converter.CompanyNotesConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.CompanyNotes;
import com.technofacts.lnf.company.repository.CompanyNotesRepository;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.dto.company.NotesDto;
import com.technofacts.lnf.service.common.page.PaginatedAndSortedService;
import com.technofacts.lnf.util.RestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class CompanyNotesService implements PaginatedAndSortedService<NotesDto> {

    private final CompanyRepository companyRepository;
    private final CompanyNotesRepository companyNotesRepository;

    @Override
    public Page<NotesDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<CompanyNotes> resultPage = companyNotesRepository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public Page<NotesDto> findPaginated(int page, int size) {
        Page<CompanyNotes> resultPage = companyNotesRepository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<NotesDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<CompanyNotes> entities = Lists.newArrayList(companyNotesRepository.findAll(sortInfo));
        return entities.stream().map(CompanyNotesConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<NotesDto> findAll() {
        return companyNotesRepository.findAll().stream().
                map(CompanyNotesConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    public void create(UUID companyId, List<NotesDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create notes for company [%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        List<CompanyNotes> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(notesDto -> {
            CompanyNotes entity = CompanyNotesConverter.toEntityModel(notesDto);
            entity.setCompany(companyEntity);
            entities.add(entity);
        });
        save(entities);
        log.info(() -> String.format("Notes for company[%s] successfully created", companyId));
    }

    public void create(UUID companyId, NotesDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create notes for company [%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        CompanyNotes entity = CompanyNotesConverter.toEntityModel(resource);
        entity.setCompany(companyEntity);
        save(entity);
        log.info(() -> String.format("notes for company[%s] successfully created", companyId));
    }
    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist", companyId)));
    }

    private void save(List<CompanyNotes> entities) {
        try {
            companyNotesRepository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save notes for company [%s]", entities.get(0).getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private void save(CompanyNotes entity) {
        try {
            companyNotesRepository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save notes for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

public void update(UUID companyId, UUID notesId, NotesDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, "Failed to update Notes with null payload");
        searchForCompany(companyId);
        CompanyNotes entity = searchForNotes(notesId);
        entity.setNotes(resource.getNotes());
        save(CompanyNotesConverter.toEntityModel(resource, entity));
        log.info(() -> String.format("Notes for Employee[%s] successfully created", notesId));
}

    private CompanyNotes searchForNotes(UUID notesId) {
        return companyNotesRepository.findById(notesId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("notes with id [%s] does not exist", notesId)));
    }

    public void deleteById(UUID companyId, UUID notesId) {
        searchForCompany(companyId);
        CompanyNotes entity = searchForNotes(notesId);
        try {
            companyNotesRepository.delete(entity);
            log.info(() -> String.format("Notes[%s] for company[%s] successfully deleted", notesId, companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete Notes[%s] for company [%s]", notesId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyNotes> entities = companyNotesRepository.findByCompanyId(companyId);
        try {
            companyNotesRepository.deleteAll(entities);
            log.info(() -> String.format("Notes for company[%s] successfully deleted", companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete notes for company [%s]", companyId);
            throw new LnFException(errorMessage);
        }
    }

    public List<NotesDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyNotes> entities = companyNotesRepository.findByCompanyId(companyId);
        return entities.stream().map(CompanyNotesConverter::toTransportModel).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public NotesDto findById(UUID companyId, UUID notesId) {
        searchForCompany(companyId);
        return CompanyNotesConverter.toTransportModel(searchForNotes(notesId));
    }

    private Page<NotesDto> validateAndGetPages(int page, Page<CompanyNotes> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(String.format("Total number of pages [%d], " +
                    "requested page [%d] does not exist", resultPage.getTotalPages(), page));
        }
        return resultPage.map(CompanyNotesConverter::toTransportModel);
    }

}
