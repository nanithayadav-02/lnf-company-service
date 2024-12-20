/*
 *
 *  * Copyright © 2024 Lever And Fulcrum Solutions (hereinafter referred to as "LNF").
 *  * All rights reserved.
 *  *
 *  * This source code is the proprietary property of LNF
 *  *
 *  * Unauthorized copying, redistribution, or modification of this code,
 *  * via any medium, is strictly prohibited unless expressly authorized
 *  * in writing by LNF.
 *  *
 *  * This code is confidential and intended solely for the use of LNF
 *  * and its authorized personnel.
 *
 */

package com.lnf.company.controller;

import com.lnf.company.service.TaskService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.TaskDto;
import com.lnf.service.common.page.PageableAsQueryParam;
import com.lnf.service.common.page.PaginationAndSortingHandler;
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
    public TaskDto findById(@PathVariable final UUID taskId) {
        return service.findByTaskId(taskId);
    }

    @PostMapping(value = "/company/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody final List<TaskDto> resource) {
        service.create(resource);
    }

    @PutMapping(value = "/company/task/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable final UUID taskId,
                       @RequestBody final TaskDto resource) {
        service.update(taskId, resource);
    }

    @DeleteMapping(value = "/company/task/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID taskId) {
        service.deleteById(taskId);
    }

}
