package com.vuviet.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.vuviet.jobhunter.entity.Company;
import com.vuviet.jobhunter.entity.Job;
import com.vuviet.jobhunter.entity.Resume;
import com.vuviet.jobhunter.entity.User;
import com.vuviet.jobhunter.entity.response.ResCreateResumeDTO;
import com.vuviet.jobhunter.entity.response.ResFetchResumeDTO;
import com.vuviet.jobhunter.entity.response.ResUpdateResumeDTO;
import com.vuviet.jobhunter.entity.response.ResultPaginationDTO;
import com.vuviet.jobhunter.service.ResumeService;
import com.vuviet.jobhunter.service.UserService;
import com.vuviet.jobhunter.util.SecurityUtil;
import com.vuviet.jobhunter.util.annotation.ApiMessage;
import com.vuviet.jobhunter.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;

    private final UserService userService;

    private final FilterBuilder filterBuilder;

    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService, UserService userService, FilterBuilder filterBuilder, FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@RequestBody @Valid Resume resume) throws IdInvalidException {
        if(!this.resumeService.checkResumeExistByUserAndJob(resume)){
            throw new IdInvalidException("User id/Job id không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get resume by id")
    public ResponseEntity<ResFetchResumeDTO> getResume(@PathVariable("id") long id) throws IdInvalidException{
        Resume resume=this.resumeService.getById(id);
        if(resume==null){
            throw new IdInvalidException("Id "+id+" không tồn tại");
        }
        return ResponseEntity.ok(this.resumeService.getResume(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resumeDTO) throws IdInvalidException{
        Resume resume=this.resumeService.getById(resumeDTO.getId());
        if(resume==null){
            throw new IdInvalidException("Id "+resumeDTO.getId()+" không tồn tại");
        }
        resume.setStatus(resumeDTO.getStatus());
        return ResponseEntity.ok(this.resumeService.update(resume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException{
        Resume resume=this.resumeService.getById(id);
        if(resume==null){
            throw new IdInvalidException("Id "+id+" không tồn tại");
        }
        this.resumeService.delete(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resume")
    public ResponseEntity<ResultPaginationDTO> getAllResume(
            @Filter Specification<Resume> spec,
            Pageable pageable
            ){

        //lay list resume theo cong viec cua cong ty bang vai tro nguoi dung
        List<Long> arrJobs=null;
        String email= SecurityUtil.getCurrentUserLogin().isPresent()==true
                ?SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser=this.userService.getByUsername(email);
        if(currentUser!=null){
            Company userCompany=currentUser.getCompany();
            if(userCompany!=null){
                List<Job> companyJobs=userCompany.getJobs();
                if(companyJobs!=null && companyJobs.size()>0){
                    arrJobs=companyJobs.stream().map(x->x.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInSpec=filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobs)).get());
        Specification<Resume> finalSpec=jobInSpec.and(spec);
        return ResponseEntity.ok(this.resumeService.getAll(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable){
        return ResponseEntity.ok(this.resumeService.getResumeByUser(pageable));
    }
}
