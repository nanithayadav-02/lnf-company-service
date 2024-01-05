package com.technofacts.lnf.company.service;

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
import com.technofacts.lnf.service.email.ThymeleafDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class CompanyHolidayService {

    private final ThymeleafDocumentService documentService;
    private final CompanyHolidayRepository repository;
    private final CompanyRepository companyRepository;

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
        log.info(() -> String.format("Holidays for Company[%s] successfully created", companyId));
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist",
                        companyId)));
    }

    private void save(List<CompanyHoliday> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save Holiday for company [%s]",
                    entities.get(0).getCompany().getCode());
            throw new LnFException(errorMessage);
        }
    }

    public List<CompanyHolidayDto> getAllHolidays() {
        List<CompanyHoliday> holidays = repository.findAll();
        return holidays.stream().map(holiday -> CompanyHolidayConverter.toTransportModel(holiday)).toList();
    }

    public void update(UUID companyId, UUID holidayId, CompanyHolidayDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create holiday for company [%s] with null payload", companyId));
        searchForCompany(companyId);
        CompanyHoliday entity = searchForHoliday(holidayId);
        save(CompanyHolidayConverter.toEntityModel(resource, entity));
        log.info(() -> String.format("Holiday for Company[%s] successfully created", companyId));
    }

    private CompanyHoliday searchForHoliday(UUID holidayId) {
        return repository.findById(holidayId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Address with id [%s] does not exist",
                        holidayId)));
    }

    private void save(CompanyHoliday entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save Address for company [%s]",
                    entity.getCompany().getCode());
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyHoliday> entities = repository.findByCompanyId(companyId);
        try {
            repository.deleteAll(entities);
            log.info(() -> String.format("Holiday for Company[%s] successfully deleted", companyId));
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
            log.info(() -> String.format("Holiday[%s] for company [%s] successfully deleted", holidayId, companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete Holiday[[%s] for company [%s]", holidayId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    public CompanyHolidayDto getHoliday(UUID holidayId, UUID companyId) {

        CompanyHoliday entity = repository.findByCompanyIdAndHolidayId(holidayId, companyId);
        return CompanyHolidayConverter.toTransportModel(entity);

    }

    public ResponseEntity<byte[]> downloadHolidaysAsPdf(UUID companyId) {

        List<CompanyHoliday> entities = repository.findByCompanyId(companyId);
        List<CompanyHolidayDto> holidays=entities.stream().map(CompanyHolidayConverter::toTransportModel).toList();
        byte[] pdfBytes = generatePdfFromHolidayDtos(holidays, "company-holidays", "company-holidays.pdf");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + "company-holidays.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    private byte[] generatePdfFromHolidayDtos(List<CompanyHolidayDto> holidays, String templateName, String fileName) {
        Map<String, Object> dynamicData = new HashMap<>();
        dynamicData.put("listObjects", holidays);

        ThymeleafDocumentDto thymeleafDocumentDto = new ThymeleafDocumentDto();
        thymeleafDocumentDto.setTemplateName(templateName);
        thymeleafDocumentDto.setFileName(fileName);
        thymeleafDocumentDto.setDynamicData(dynamicData);

        return documentService.generatePdf(thymeleafDocumentDto);
    }

    public List<CompanyHolidayDto> getHolidayByLoc(String location, long year) {

       List<CompanyHoliday> entities= repository.findByLocationAndYear(location,year);
       return entities.stream().map(CompanyHolidayConverter::toTransportModel).toList();
    }
}
