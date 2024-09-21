package com.vuviet.jobhunter.service;

import com.vuviet.jobhunter.entity.User;
import com.vuviet.jobhunter.entity.response.Meta;
import com.vuviet.jobhunter.entity.response.ResultPaginationDTO;
import com.vuviet.jobhunter.entity.Company;
import com.vuviet.jobhunter.repository.CompanyRepository;
import com.vuviet.jobhunter.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
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

    private final UserRepository userRepository;

    CompanyServiceImpl(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Company create(Company company) {
        return this.companyRepository.save(company);
    }

    @Override
    public void deleteById(long id) {
        Optional<Company> companyOptional=this.companyRepository.findById(id);
        if(companyOptional.isPresent()){
            Company com=companyOptional.get();

            //Tim tat ca nguoi dung trong cong ty nay
            List<User> users=this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(users);
        }
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
