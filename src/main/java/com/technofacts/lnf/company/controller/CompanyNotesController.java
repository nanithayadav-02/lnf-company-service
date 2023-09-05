package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.CompanyNotesService;
import com.technofacts.lnf.dto.company.NotesDto;
import com.technofacts.lnf.util.QueryConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyNotesController {

    private final CompanyNotesService service;

    @GetMapping(value = "/company/notes", params = {QueryConstants.PAGE, QueryConstants.SIZE, QueryConstants.SORT_BY})
    public Page<NotesDto> findAllPaginatedAndSorted(@RequestParam(value = QueryConstants.PAGE) final int page,
                                                            @RequestParam(value = QueryConstants.SIZE) final int size,
                                                            @RequestParam(value = QueryConstants.SORT_BY) final String sortBy,
                                                            @RequestParam(value = QueryConstants.SORT_ORDER)
                                                            final String sortOrder) {
        return service.findPaginatedAndSorted(page, size, sortBy, sortOrder);
    }

    @GetMapping(value = "/company/notes", params = {QueryConstants.PAGE, QueryConstants.SIZE})
    public Page<NotesDto> findAllPaginated(@RequestParam(value = QueryConstants.PAGE) final int page,
                                                   @RequestParam(value = QueryConstants.SIZE) final int size) {
        return service.findPaginated(page, size);
    }

    @GetMapping(value = "/company/notes", params = {QueryConstants.SORT_BY})
    public List<NotesDto> findAllSorted(@RequestParam(value = QueryConstants.SORT_BY) final String sortBy,
                                                @RequestParam(value = QueryConstants.SORT_ORDER) final String sortOrder) {
        return service.findAllSorted(sortBy, sortOrder);
    }

    @GetMapping(value = "/company/{companyId}/notes")
    public List<NotesDto> findByCompanyId(@PathVariable("companyId") final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/notes/{notesID}")
    public NotesDto findById(@PathVariable("companyId") final UUID companyId, @PathVariable("notesID") final UUID notesID) {
        return service.findById(companyId, notesID);
    }

    @GetMapping(value = "/company/notes")
    public List<NotesDto>findAll(){
        return service.findAllNotes();
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

    @PutMapping(value = "/company/{companyId}/notes/{notesID}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId, @PathVariable("notesID") final UUID notesID,
                       @RequestBody final NotesDto resource) {
        service.update(companyId, notesID, resource);
    }

    @DeleteMapping(value = "/company/{companyId}/notes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/notes/{notesID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId, @PathVariable("notesID") final UUID notesID) {
        service.deleteById(companyId, notesID);
    }
}
