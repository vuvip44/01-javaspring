package com.vuviet.jobhunter.service;

import com.vuviet.jobhunter.entity.Company;
import com.vuviet.jobhunter.entity.Job;
import com.vuviet.jobhunter.entity.Skill;
import com.vuviet.jobhunter.entity.response.*;
import com.vuviet.jobhunter.repository.CompanyRepository;
import com.vuviet.jobhunter.repository.JobRepository;
import com.vuviet.jobhunter.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface JobService {
    ResCreateJobDTO createJob(Job job);

    Job getById(long id);

    ResUpdateJobDTO updateJob(Job job,Job jodDB);

    void deleteJob(Job job);

    ResultPaginationDTO getAllJob(Specification<Job> spec, Pageable pageable);
}
@Service
class JobServiceImpl implements JobService{
    private final JobRepository jobRepository;

    private final SkillRepository skillRepository;

    private final CompanyRepository companyRepository;


    JobServiceImpl(JobRepository jobRepository, SkillRepository skillRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public ResCreateJobDTO createJob(Job job) {
        if(job.getSkills()!=null){
            List<Long> reqSkills=new ArrayList<>();
            for(Skill skill: job.getSkills()){
                reqSkills.add(skill.getId());
            }
//          List<Long> reqSkills= job.getSkills()
//                .stream().map(x->x.getId()).toList();

            List<Skill> dbSkills=this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        if(job.getCompany()!=null){
            Optional<Company> company=this.companyRepository.findById(job.getCompany().getId());
            if(company.isPresent()){
                job.setCompany(company.get());
            }
        }
        Job currentJob=this.jobRepository.save(job);

        ResCreateJobDTO res=new ResCreateJobDTO();
        res.setId(currentJob.getId());
        res.setName(currentJob.getName());
        res.setSalary(currentJob.getSalary());
        res.setQuantity(currentJob.getQuantity());
        res.setLevel(currentJob.getLevel());
        res.setDescription(currentJob.getDescription());
        res.setStartDate(currentJob.getStartDate());
        res.setEndDate(currentJob.getEndDate());
        res.setActive(currentJob.isActive());
        res.setCreatedAt(currentJob.getCreatedAt());
        res.setCreatedBy(currentJob.getCreatedBy());

        if(currentJob.getSkills()!=null){
            List<String> nameSkills=new ArrayList<>();
            for(Skill skill:currentJob.getSkills()){
                nameSkills.add(skill.getName());
            }
            res.setSkills(nameSkills);
        }

        return res;
    }

    @Override
    public Job getById(long id) {
        Optional<Job> jobOptional=this.jobRepository.findById(id);
        if(jobOptional.isPresent()){
            return jobOptional.get();
        }
        return null;
    }

    @Override
    public ResUpdateJobDTO updateJob(Job job,Job jobDB) {
        if(job.getSkills()!=null){
            List<Long> rqskills=new ArrayList<>();
            for(Skill s: job.getSkills()){
                rqskills.add(s.getId());
            }
            List<Skill> skill=this.skillRepository.findByIdIn(rqskills);
            jobDB.setSkills(skill);
        }
        if(job.getCompany()!=null){
            Optional<Company> companyOptional=this.companyRepository.findById(job.getCompany().getId());
            if(companyOptional.isPresent()){
                jobDB.setCompany(companyOptional.get());
            }
        }

        jobDB.setName(job.getName());
        jobDB.setSalary(job.getSalary());
        jobDB.setQuantity(job.getQuantity());
        jobDB.setLocation(job.getLocation());
        jobDB.setLevel(job.getLevel());
        jobDB.setStartDate(job.getStartDate());
        jobDB.setEndDate(job.getEndDate());
        jobDB.setActive(job.isActive());

        Job currentJob= this.jobRepository.save(jobDB);
        ResUpdateJobDTO res=new ResUpdateJobDTO();
        res.setId(job.getId());
        res.setName(currentJob.getName());
        res.setSalary(currentJob.getSalary());
        res.setQuantity(currentJob.getQuantity());
        res.setLevel(currentJob.getLevel());
        res.setDescription(currentJob.getDescription());
        res.setStartDate(currentJob.getStartDate());
        res.setEndDate(currentJob.getEndDate());
        res.setActive(currentJob.isActive());
        res.setUpdateAt(currentJob.getUpdatedAt());
        res.setUpdateBy(currentJob.getUpdatedBy());


        return res;
    }

    @Override
    public void deleteJob(Job job) {
        this.jobRepository.delete(job);
    }

    @Override
    public ResultPaginationDTO getAllJob(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob=this.jobRepository.findAll(spec,pageable);
        ResultPaginationDTO res=new ResultPaginationDTO();
        Meta meta=new Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());

        res.setMeta(meta);
        res.setResult(pageJob.getContent());
        return res;
    }
}
