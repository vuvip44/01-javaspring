package com.vuviet.jobhunter.service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.vuviet.jobhunter.entity.Job;
import com.vuviet.jobhunter.entity.Resume;
import com.vuviet.jobhunter.entity.User;
import com.vuviet.jobhunter.entity.response.*;
import com.vuviet.jobhunter.repository.ResumeRepository;
import com.vuviet.jobhunter.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ResumeService {
    ResCreateResumeDTO create(Resume resume);

    boolean checkResumeExistByUserAndJob(Resume resume);

    Resume getById(long id);

    ResFetchResumeDTO getResume(Resume resume);

    ResUpdateResumeDTO update(Resume resume);

    void delete(long id);

    ResultPaginationDTO getAll(Specification<Resume> spec, Pageable pageable);

    ResultPaginationDTO getResumeByUser(Pageable pageable);
}

@Service
class ResumeServiceImpl implements ResumeService{
    private final UserService userService;

    private final JobService jobService;

    private final ResumeRepository resumeRepository;

    private final FilterParser filterParser;

    private final FilterSpecificationConverter filterSpecificationConverter;

    ResumeServiceImpl(UserService userService, JobService jobService, ResumeRepository resumeRepository, FilterParser filterParser, FilterSpecificationConverter filterSpecificationConverter) {
        this.userService = userService;
        this.jobService = jobService;
        this.resumeRepository = resumeRepository;
        this.filterParser = filterParser;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @Override
    public ResCreateResumeDTO create(Resume resume) {
        this.resumeRepository.save(resume);
        ResCreateResumeDTO resCreateResumeDTO=new ResCreateResumeDTO();
        resCreateResumeDTO.setId(resume.getId());
        resCreateResumeDTO.setCreatedAt(resume.getCreatedAt());
        resCreateResumeDTO.setCreatedBy(resume.getCreatedBy());
        return resCreateResumeDTO;
    }

    @Override
    public boolean checkResumeExistByUserAndJob(Resume resume) {
        if(resume.getJob()==null){
            return false;
        }
        Optional<Job> jobOptional= Optional.ofNullable(this.jobService.getById(resume.getJob().getId()));
        if(jobOptional.isEmpty()){
            return false;
        }

        if(resume.getUser()==null){
            return false;
        }Optional<User> userOptional= Optional.ofNullable(this.userService.getById(resume.getJob().getId()));
        if(jobOptional.isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    public Resume getById(long id) {
        Optional<Resume> rs=this.resumeRepository.findById(id);
        if(rs.isPresent()){
            return rs.get();
        }
        return null;
    }

    @Override
    public ResFetchResumeDTO getResume(Resume resume) {
        ResFetchResumeDTO res=new ResFetchResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        res.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        res.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));

        if(resume.getJob()!=null){
            res.setCompanyName(resume.getJob().getCompany().getName());
        }
        return res;
    }

    @Override
    public ResUpdateResumeDTO update(Resume resumeDTO) {
        this.resumeRepository.save(resumeDTO);

        ResUpdateResumeDTO res=new ResUpdateResumeDTO();
        res.setUpdateAt(resumeDTO.getUpdatedAt());
        res.setUpdateBy(resumeDTO.getUpdatedBy());
        return res;
    }

    @Override
    public void delete(long id) {
        this.resumeRepository.deleteById(id);
    }

    @Override
    public ResultPaginationDTO getAll(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageResume=this.resumeRepository.findAll(spec,pageable);
        ResultPaginationDTO rs=new ResultPaginationDTO();
        Meta mt=new Meta();

        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);

        List<ResFetchResumeDTO> list=pageResume.getContent()
                .stream().map(item->this.getResume(item))
                .collect(Collectors.toList());
        rs.setResult(list);
        return rs;
    }

    @Override
    public ResultPaginationDTO getResumeByUser(Pageable pageable) {
        String email= SecurityUtil.getCurrentUserLogin().isPresent()?
                SecurityUtil.getCurrentUserLogin().get() : "";

        FilterNode node=filterParser.parse("email='"+email+"'");
        FilterSpecification<Resume> spec=filterSpecificationConverter.convert(node);
        Page<Resume> resumePage=this.resumeRepository.findAll(spec,pageable);

        ResultPaginationDTO rs=new ResultPaginationDTO();
        Meta meta=new Meta();

        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(resumePage.getTotalPages());
        meta.setTotal(resumePage.getTotalElements());

        rs.setMeta(meta);
        List<ResFetchResumeDTO> list=resumePage.getContent()
                .stream().map(item->this.getResume(item))
                .collect(Collectors.toList());
        rs.setResult(list);
        return rs;
    }
}