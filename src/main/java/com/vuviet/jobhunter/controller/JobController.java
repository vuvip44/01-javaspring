package com.vuviet.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.vuviet.jobhunter.entity.Job;
import com.vuviet.jobhunter.entity.response.ResCreateJobDTO;
import com.vuviet.jobhunter.entity.response.ResUpdateJobDTO;
import com.vuviet.jobhunter.entity.response.ResultPaginationDTO;
import com.vuviet.jobhunter.service.JobService;
import com.vuviet.jobhunter.util.annotation.ApiMessage;
import com.vuviet.jobhunter.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> createJob(@RequestBody @Valid Job jobDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.createJob(jobDTO));
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get job by id")
    public ResponseEntity<Job> getJobById(@PathVariable("id") long id)throws IdInvalidException {
        Job job=this.jobService.getById(id);
        if(job==null){
            throw  new IdInvalidException("Id "+id+" không tồn tại");
        }
        return ResponseEntity.ok(job);
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@RequestBody @Valid Job jobDTO) throws IdInvalidException{
        Job job=this.jobService.getById(jobDTO.getId());
        if(job==null){
            throw  new IdInvalidException("Id "+jobDTO.getId()+" không tồn tại");
        }
        return ResponseEntity.ok(this.jobService.updateJob(jobDTO,job));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") long id) throws IdInvalidException{
        Job job=this.jobService.getById(id);
        if(job==null){
            throw  new IdInvalidException("Id "+id+" không tồn tại");
        }
        this.jobService.deleteJob(job);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/jobs")
    @ApiMessage("fetch all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJob(
            @Filter Specification<Job> spec,
            Pageable pageable
            ){
        return ResponseEntity.ok(this.jobService.getAllJob(spec,pageable));
    }
}
