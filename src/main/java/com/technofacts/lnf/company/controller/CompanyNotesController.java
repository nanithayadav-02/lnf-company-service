package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.CompanyNotesService;
import com.technofacts.lnf.dto.common.PageRequestDto;
import com.technofacts.lnf.dto.company.NotesDto;
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
public class CompanyNotesController {

    private final CompanyNotesService service;
    private final PaginationAndSortingHandler paginationAndSortingHandler;

    @GetMapping(value = "/company/notes")
    public ResponseEntity<?> findAll(@PageableAsQueryParam PageRequestDto pageRequest) {
        return paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
    }

    @GetMapping(value = "/company/{companyId}/notes")
    public List<NotesDto> findByCompanyId(@PathVariable("companyId") final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/notes/{notesID}")
    public NotesDto findById(@PathVariable("companyId") final UUID companyId, @PathVariable("notesID") final UUID notesID) {
        return service.findById(companyId, notesID);
    }

    @PostMapping(value = "/company/{companyId}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId, @RequestBody final List<NotesDto> resource) {
        service.create(companyId, resource);
    }

    @PostMapping(value = "/company/{companyId}/note")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId, @RequestBody final NotesDto resource) {
        service.create(companyId, resource);
    }

    @PutMapping(value = "/company/{companyId}/notes/{notesId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId, @PathVariable("notesId") final UUID notesId,
                       @RequestBody final NotesDto resource) {
        service.update(companyId, notesId, resource);
    }

    @DeleteMapping(value = "/company/{companyId}/notes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/notes/{notesId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId, @PathVariable("notesId") final UUID notesId) {
        service.deleteById(companyId, notesId);
    }
}
