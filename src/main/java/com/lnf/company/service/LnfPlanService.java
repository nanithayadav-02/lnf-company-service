package com.lnf.company.service;

import com.google.common.collect.Lists;
import com.lnf.company.converter.LnfPlanConverter;
import com.lnf.company.exception.LnFBadRequestException;
import com.lnf.company.exception.LnFEntityNotFoundException;
import com.lnf.company.exception.LnFException;
import com.lnf.company.model.LnfPlan;
import com.lnf.company.repository.LnfPlanRepository;
import com.lnf.dto.company.LnfPlanDto;
import com.lnf.service.common.page.PaginatedAndSortedService;
import com.lnf.util.RestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LnfPlanService implements PaginatedAndSortedService<LnfPlanDto> {

    private final LnfPlanRepository repository;

    @Override
    public Page<LnfPlanDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<LnfPlan> resultPage = repository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public Page<LnfPlanDto> findPaginated(int page, int size) {
        Page<LnfPlan> resultPage = repository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<LnfPlanDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<LnfPlan> entities = Lists.newArrayList(repository.findAll(sortInfo));
        return entities.stream().map(LnfPlanConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<LnfPlanDto> findAll() {
        return repository.findAll().stream().
                map(LnfPlanConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    public void create(List<LnfPlanDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create LnfPlan with null payload");
        List<LnfPlan> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(LnfPlanDto -> {
            LnfPlan entity = LnfPlanConverter.toEntityModel(LnfPlanDto, new LnfPlan());
            entities.add(entity);
        });
        save(entities);
        log.debug("LnfPlans successfully created");
    }

    public void create(LnfPlanDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create LnfPlan with null payload");
        LnfPlan entity = LnfPlanConverter.toEntityModel(resource, new LnfPlan());
        save(entity);
        log.debug("LnfPlan successfully created");
    }


    private void save(List<LnfPlan> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to save LnfPlans";
            throw new LnFException(errorMessage);
        }
    }

    private void save(LnfPlan entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to save LnfPlan";
            throw new LnFException(errorMessage);
        }
    }

    public void update(UUID lnfPlanId, LnfPlanDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, "Failed to update LnfPlan with null payload");
        LnfPlan entity = searchForLnfPlan(lnfPlanId);
        save(LnfPlanConverter.toEntityModel(resource, entity));
        log.debug("LnfPlan for Id {} successfully created", lnfPlanId);
    }

    private LnfPlan searchForLnfPlan(UUID lnfPlanId) {
        return repository.findById(lnfPlanId).
                orElseThrow(() -> new LnFEntityNotFoundException("LnfPlan with id [%s] does not exist".formatted(lnfPlanId)));
    }

    public void deleteById(UUID lnfPlanId) {
        LnfPlan entity = searchForLnfPlan(lnfPlanId);
        try {
            repository.delete(entity);
            log.debug("LnfPlan {} successfully deleted", lnfPlanId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete LnfPlan[%s]".formatted(lnfPlanId);
            throw new LnFException(errorMessage);
        }
    }

    public LnfPlanDto findById(UUID lnfPlanId) {
        return LnfPlanConverter.toTransportModel(searchForLnfPlan(lnfPlanId));
    }

    private Page<LnfPlanDto> validateAndGetPages(int page, Page<LnfPlan> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(("Total number of pages [%d], " +
                    "requested page [%d] does not exist").formatted(resultPage.getTotalPages(), page));
        }
        return resultPage.map(LnfPlanConverter::toTransportModel);
    }

}