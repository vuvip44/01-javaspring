package com.vuviet.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.vuviet.jobhunter.util.annotation.ApiMessage;
import com.vuviet.jobhunter.entity.dto.ResultPaginationDTO;
import com.vuviet.jobhunter.entity.Company;
import com.vuviet.jobhunter.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    @ApiMessage("create company")
    public ResponseEntity<Company> createNewCompany(@RequestBody @Valid Company companyDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.create(companyDTO));
    }

    @GetMapping("/companies")
    @ApiMessage("fetch all companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            @Filter Specification<Company> spec,
            Pageable pageable
            ){

        return ResponseEntity.ok(this.companyService.getAllCompanies(spec,pageable));
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("get company by id")
    public ResponseEntity<Company> getById(@PathVariable ("id") long id){
        return ResponseEntity.ok(this.companyService.getById(id));
    }

    @PutMapping("/companies")
    @ApiMessage("update company")
    public ResponseEntity<Company> updateCompany(@RequestBody Company companyDTO){
        return ResponseEntity.ok(this.companyService.update(companyDTO));
    }
}
