package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.dto.ImageDto;
import com.technofacts.lnf.company.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class ImageController {

    private final ImageService service;

    @GetMapping(value = "/company/{companyId}/image")
    @ResponseStatus(HttpStatus.CREATED)
    public ImageDto findByEmployeeId(@PathVariable("companyId") final String companyId) throws IOException {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/image/{imageId}")
    public ResponseEntity<byte[]> findById(@PathVariable("companyId") final String companyId,
                                           @PathVariable("imageId") final UUID imageId) {
        return service.findById(companyId, imageId);
    }

    @PostMapping(value = "/company/{companyId}/image")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final String companyId, @RequestParam MultipartFile image) {
        service.create(companyId, image);
    }

    @PutMapping(value = "/company/{companyId}/image/{imageId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final String companyId, @PathVariable("imageId") final UUID imageId,
                       @RequestParam MultipartFile image) throws IOException {
        service.update(companyId, imageId, image);
    }

    @DeleteMapping(value = "/company/{companyId}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final String companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/image/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final String companyId,
                       @PathVariable("imageId") final UUID imageId) {
        service.deleteById(companyId, imageId);
    }

}
