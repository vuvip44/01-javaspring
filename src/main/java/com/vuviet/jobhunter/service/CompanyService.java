package com.vuviet.jobhunter.service;

import com.vuviet.jobhunter.entity.dto.Meta;
import com.vuviet.jobhunter.entity.dto.ResultPaginationDTO;
import com.vuviet.jobhunter.entity.Company;
import com.vuviet.jobhunter.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface CompanyService {
    Company create(Company company);

    void deleteById(long id);

    Company update(Company companyDTO);

    ResultPaginationDTO getAllCompanies(Specification<Company> spec,Pageable pageable);

    Company getById(long id);
}
@Service
class CompanyServiceImpl implements CompanyService{
    private final CompanyRepository companyRepository;

    CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company create(Company company) {
        return this.companyRepository.save(company);
    }

    @Override
    public void deleteById(long id) {
        this.companyRepository.deleteById(id);
    }

    @Override
    public Company update(Company companyDTO) {
        Optional<Company> company=this.companyRepository.findById(companyDTO.getId());
        if(company.isPresent()){
            Company currentCompany=company.get();

            currentCompany.setName(companyDTO.getName());
            currentCompany.setDescription(companyDTO.getDescription());
            currentCompany.setLogo(companyDTO.getLogo());
            currentCompany.setAddress(companyDTO.getAddress());
            this.companyRepository.save(currentCompany);
            return currentCompany;
        }
        return null;
    }

    @Override
    public ResultPaginationDTO getAllCompanies(Specification<Company> spec,Pageable pageable) {
        Page<Company> pageCompany=this.companyRepository.findAll(spec,pageable);
        ResultPaginationDTO rs=new ResultPaginationDTO();
        Meta meta=new Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageCompany.getContent());
        return rs;
    }

    @Override
    public Company getById(long id) {
        Optional<Company> company=this.companyRepository.findById(id);
        if(company.isPresent()){
            return company.get();
        }
        return null;

    }
}
