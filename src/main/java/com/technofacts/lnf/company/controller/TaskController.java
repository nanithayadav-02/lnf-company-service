package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.TaskService;
import com.technofacts.lnf.dto.common.PageRequestDto;
import com.technofacts.lnf.dto.company.TaskDto;
import com.technofacts.lnf.service.common.page.PageableAsQueryParam;
import com.technofacts.lnf.service.common.page.PaginationAndSortingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class TaskController {

    private final TaskService service;
    private final PaginationAndSortingHandler paginationAndSortingHandler;

    @GetMapping(value = "/company/tasks")
    public ResponseEntity<?> findAll(@PageableAsQueryParam PageRequestDto pageRequest) {
        return paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
    }

    @GetMapping(value = "/company/task/{taskId}")
    public TaskDto findById(@PathVariable("taskId") final UUID taskId) {
        return service.findByTaskId(taskId);
    }

    @PostMapping(value = "/company/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody final List<TaskDto> resource) {
        service.create(resource);
    }

    @PutMapping(value = "/company/task/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("taskId") final UUID taskId,
                       @RequestBody final TaskDto resource) {
        service.update(taskId, resource);
    }

    @DeleteMapping(value = "/company/task/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete( @PathVariable("taskId") final UUID taskId) {
        service.deleteById(taskId);
    }

}
