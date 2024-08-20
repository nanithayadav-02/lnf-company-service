package com.technofacts.lnf.company.service;

import com.google.common.collect.Lists;
import com.technofacts.lnf.company.converter.CompanyHolidayConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.CompanyHoliday;
import com.technofacts.lnf.company.repository.CompanyHolidayRepository;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.dto.company.CompanyHolidayDto;
import com.technofacts.lnf.dto.email.ThymeleafDocumentDto;
import com.technofacts.lnf.service.common.page.PaginatedAndSortedService;
import com.technofacts.lnf.service.email.ThymeleafDocumentService;
import com.technofacts.lnf.service.specification.GenericSpecificationBuilder;
import com.technofacts.lnf.util.RestUtil;
import com.technofacts.lnf.util.specification.SpecificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyHolidayService implements PaginatedAndSortedService<CompanyHolidayDto> {

    private final ThymeleafDocumentService documentService;
    private final CompanyHolidayRepository repository;
    private final CompanyRepository companyRepository;

    @Value("${company.holidays.template}")
    private String companyHolidaysTemplate;

    @Override
    public Page<CompanyHolidayDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<CompanyHoliday> resultPage = repository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<CompanyHolidayDto> findAll() {
        List<CompanyHoliday> holidays = repository.findAll();
        return holidays.stream().map(CompanyHolidayConverter::toTransportModel).toList();
    }

    @Override
    public Page<CompanyHolidayDto> findPaginated(int page, int size) {
        Page<CompanyHoliday> resultPage = repository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<CompanyHolidayDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<CompanyHoliday> entities = Lists.newArrayList(repository.findAll(sortInfo));
        return entities.stream().map(CompanyHolidayConverter::toTransportModel).filter(Objects::nonNull).toList();
    }

    private Page<CompanyHolidayDto> validateAndGetPages(int page, Page<CompanyHoliday> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(String.format("Total number of pages [%d], " + "requested page [%d] does not exist", resultPage.getTotalPages(), page));
        }
        return resultPage.map(CompanyHolidayConverter::toTransportModel);
    }

    public CompanyHolidayDto findHolidayById(UUID companyId, UUID holidayId) {
        CompanyHoliday entity = repository.findByHolidayId(companyId, holidayId);
        return CompanyHolidayConverter.toTransportModel(entity);
    }

    public List<CompanyHolidayDto> findHolidaysByYearAndLocation(UUID companyId, long year, String location) {
        List<CompanyHoliday> entities = repository.findByYearAndLocation(companyId, year, location);
        return entities.stream().map(CompanyHolidayConverter::toTransportModel).toList();
    }

    public byte[] getCompanyHolidaysAsPdf(UUID companyId) {
        List<CompanyHoliday> entities = repository.findByCompanyId(companyId);
        List<CompanyHolidayDto> holidays = entities.stream().map(CompanyHolidayConverter::toTransportModel).toList();
        return generateCompanyHolidayPdf(holidays);
    }

    private byte[] generateCompanyHolidayPdf(List<CompanyHolidayDto> holidays) {
        Map<String, Object> dynamicData = new HashMap<>();
        dynamicData.put("listObjects", holidays);

        ThymeleafDocumentDto thymeleafDocumentDto = new ThymeleafDocumentDto();
        thymeleafDocumentDto.setTemplateName(companyHolidaysTemplate);
        thymeleafDocumentDto.setFileName("company-holidays.pdf");
        thymeleafDocumentDto.setDynamicData(dynamicData);

        return documentService.generatePdf(thymeleafDocumentDto);
    }

    public void create(UUID companyId, List<CompanyHolidayDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create Holidays for company [%s] with null payload", companyId));
        Company company = searchForCompany(companyId);
        List<CompanyHoliday> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(companyHolidayDto -> {
            CompanyHoliday entity = CompanyHolidayConverter.toEntityModel(companyHolidayDto, new CompanyHoliday());
            entity.setCompany(company);
            entities.add(entity);
        });
        save(entities);
        log.debug("Holidays for the company {} is successfully created", companyId);
    }

    private void save(List<CompanyHoliday> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save holiday for company [%s]",
                    entities.get(0).getCompany().getCode());
            throw new LnFException(errorMessage);
        }
    }

    public void update(UUID companyId, UUID holidayId, CompanyHolidayDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create holiday for company [%s] with null payload", companyId));
        searchForCompany(companyId);
        CompanyHoliday entity = searchForHoliday(holidayId);
        save(CompanyHolidayConverter.toEntityModel(resource, entity));
        log.debug("Holiday for Company {} successfully created", companyId);
    }

    public void deleteAll(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyHoliday> entities = repository.findByCompanyId(companyId);
        try {
            repository.deleteAll(entities);
            log.debug("Company {} all holiday are successfully deleted", companyId);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete Holiday for company [%s]", companyId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteById(UUID companyId, UUID holidayId) {
        searchForCompany(companyId);
        CompanyHoliday entity = searchForHoliday(holidayId);
        try {
            repository.delete(entity);
            log.debug("Company {} holiday {} is successfully deleted", holidayId, companyId);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete Holiday[[%s] for company [%s]", holidayId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist",
                        companyId)));
    }

    private CompanyHoliday searchForHoliday(UUID holidayId) {
        return repository.findById(holidayId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Holiday with id [%s] does not exist",
                        holidayId)));
    }

    private void save(CompanyHoliday entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save holiday for company [%s]",
                    entity.getCompany().getCode());
            throw new LnFException(errorMessage);
        }
    }

    public List<CompanyHolidayDto> findAll(String search) {
        Specification<CompanyHoliday> specification = buildEmployeeSpecification(search);
        List<CompanyHoliday> entities = repository.findAll(specification);
        return convertToDtos(entities);
    }

    private Specification<CompanyHoliday> buildEmployeeSpecification(String search) {
        GenericSpecificationBuilder<CompanyHoliday> employeeBuilder = new GenericSpecificationBuilder<>();
        Function<String, Class<?>> fieldClassForEmployee = this::getFieldClassFromEmployee;
        return SpecificationUtil.buildSpecification(search, employeeBuilder, fieldClassForEmployee);
    }

    private Class<?> getFieldClassFromEmployee(String fieldName) {
        return SpecificationUtil.getFieldClass(CompanyHoliday.class, fieldName);
    }

    private List<CompanyHolidayDto> convertToDtos(List<CompanyHoliday> entities) {
        return entities.stream()
                .map(CompanyHolidayConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<CompanyHolidayDto> findHolidaysByYear(UUID companyId, long year) {
        List<CompanyHoliday> entities = repository.findByYear(companyId, year);
        return entities.stream().map(CompanyHolidayConverter::toTransportModel).toList();

    }
}
