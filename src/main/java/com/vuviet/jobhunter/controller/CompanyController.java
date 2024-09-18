package com.vuviet.jobhunter.controller;

import com.vuviet.jobhunter.dto.ResultPaginationDTO;
import com.vuviet.jobhunter.entity.Company;
import com.vuviet.jobhunter.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createNewCompany(@RequestBody @Valid Company companyDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.create(companyDTO));
    }

    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            @RequestParam("current") Optional<String> currentPageOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional
    ){
        String sCurrentPage=currentPageOptional.isPresent()? currentPageOptional.get() : "";
        String sPageSize=pageSizeOptional.isPresent()? pageSizeOptional.get() : "";

        int current=Integer.parseInt(sCurrentPage);
        int size=Integer.parseInt(sPageSize);

        Pageable pageable= PageRequest.of(current-1,size);
        return ResponseEntity.ok(this.companyService.getAllCompanies(pageable));
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getById(@PathVariable ("id") long id){
        return ResponseEntity.ok(this.companyService.getById(id));
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@RequestBody Company companyDTO){
        return ResponseEntity.ok(this.companyService.update(companyDTO));
    }
}
