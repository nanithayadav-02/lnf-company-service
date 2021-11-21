package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.dto.ThemeDto;
import com.technofacts.lnf.company.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class ThemeController {

    private final ThemeService service;

    @GetMapping(value = "/company/{companyId}/theme")
    public List<ThemeDto> findByCompanyId(@PathVariable("companyId") final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/theme/{themeId}")
    public ThemeDto findById(@PathVariable("companyId") final UUID companyId, @PathVariable("themeId") final UUID themeId) {
        return service.findById(companyId, themeId);
    }

    @PostMapping(value = "/company/{companyId}/theme")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId, @RequestBody final List<ThemeDto> resource) {
        service.create(companyId, resource);
    }

    @PutMapping(value = "/company/{companyId}/theme/{themeId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId, @PathVariable("themeId") final UUID themeId,
                       @RequestBody final ThemeDto resource) {
        service.update(companyId, themeId, resource);
    }

    @DeleteMapping(value = "/company/{companyId}/theme")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/theme/{themeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId, @PathVariable("themeId") final UUID themeId) {
        service.deleteById(companyId, themeId);
    }

}
