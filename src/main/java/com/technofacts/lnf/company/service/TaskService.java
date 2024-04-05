package com.technofacts.lnf.company.service;

import com.google.common.collect.Lists;
import com.technofacts.lnf.company.converter.TaskConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Task;
import com.technofacts.lnf.company.repository.TaskRepository;
import com.technofacts.lnf.dto.company.TaskDto;
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

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class TaskService implements PaginatedAndSortedService<TaskDto> {

    private final TaskRepository repository;

    @Override
    public Page<TaskDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<Task> resultPage = repository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<TaskDto> findAll() {
        List<Task> entities = repository.findAll();
        return entities.stream().map(TaskConverter::toTransportModel).filter(Objects::nonNull).toList();
    }

    @Override
    public Page<TaskDto> findPaginated(int page, int size) {
        Page<Task> resultPage = repository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<TaskDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<Task> entities = Lists.newArrayList(repository.findAll(sortInfo));
        return entities.stream().map(TaskConverter::toTransportModel).filter(Objects::nonNull).toList();
    }

    private Page<TaskDto> validateAndGetPages(int page, Page<Task> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(String.format("Total number of pages [%d], " + "requested page [%d] does not exist", resultPage.getTotalPages(), page));
        }
        return resultPage.map(TaskConverter::toTransportModel);
    }

    public TaskDto findByTaskId(UUID taskId) {
        searchForTask(taskId);
        Task entity = searchForTask(taskId);
        return TaskConverter.toTransportModel(entity);
    }

    public void create(List<TaskDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create Task with null payload");
        List<Task> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(taskDto -> {
            Task entity = TaskConverter.toEntityModel(taskDto, new Task());
            entities.add(entity);
        });
        save(entities);
        log.info(() -> String.format("Tasks[%s] successfully created", entities.get(0).getId()));
    }

    private void save(Task entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save Task [%s]", entity.getId());
            throw new LnFException(errorMessage);
        }
    }

    private void save(List<Task> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save Task [%s]", entities.get(0).getId());
            throw new LnFException(errorMessage);
        }
    }

    private Task searchForTask(UUID taskId) {
        return repository.findById(taskId)
                .orElseThrow(() -> new LnFEntityNotFoundException(String.format("Task with id [%s] does not exist", taskId)));
    }


    public void update(UUID taskId, TaskDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to update task with null payload");
        Task entity = searchForTask(taskId);
        save(TaskConverter.toEntityModel(resource, entity));
        log.info(() -> String.format("Task [%s] successfully updated", taskId));
    }


    public void deleteById(UUID taskId) {
        Task entity = searchForTask(taskId);
        try {
            repository.delete(entity);
            log.info(() -> String.format("Task[%s] successfully deleted", taskId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete Task[%s]", taskId);
            throw new LnFException(errorMessage);
        }
    }

}
