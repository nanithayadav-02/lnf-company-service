package com.technofacts.lnf.company.controller;

import java.util.List;
import java.util.UUID;

import com.technofacts.lnf.company.service.CompanyAddressService;
import com.technofacts.lnf.dto.company.AddressDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyAddressController {

    private final CompanyAddressService service;

    @GetMapping(value = "/company/{companyId}/address")
    public List<AddressDto> findByCompanyId(@PathVariable("companyId") final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/address/{addressId}")
    public AddressDto findById(@PathVariable("companyId") final UUID companyId,
                               @PathVariable("addressId") final UUID addressId) {
        return service.findById(companyId, addressId);
    }

    @PostMapping(value = "/company/{companyId}/address")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId,
                       @RequestBody final List<AddressDto> resource) {
        service.create(companyId, resource);
    }

    @PutMapping(value = "/company/{companyId}/address/{addressId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId,
                       @PathVariable("addressId") final UUID addressId, @RequestBody final AddressDto resource) {
        service.update(companyId, addressId, resource);
    }

    @DeleteMapping(value = "/company/{companyId}/address")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/address/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId,
                       @PathVariable("addressId") final UUID addressId) {
        service.deleteById(companyId, addressId);
    }
}
