package com.vuviet.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.vuviet.jobhunter.entity.Resume;
import com.vuviet.jobhunter.entity.response.ResCreateResumeDTO;
import com.vuviet.jobhunter.entity.response.ResFetchResumeDTO;
import com.vuviet.jobhunter.entity.response.ResUpdateResumeDTO;
import com.vuviet.jobhunter.entity.response.ResultPaginationDTO;
import com.vuviet.jobhunter.service.ResumeService;
import com.vuviet.jobhunter.util.annotation.ApiMessage;
import com.vuviet.jobhunter.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
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
        return ResponseEntity.ok(this.resumeService.getAll(spec, pageable));
    }
}
