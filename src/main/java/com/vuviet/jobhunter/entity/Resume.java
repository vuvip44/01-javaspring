package com.vuviet.jobhunter.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vuviet.jobhunter.util.SecurityUtil;
import com.vuviet.jobhunter.util.constant.ResumeStateEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "resumes")
@Data
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;

    @NotBlank(message = "Không được để trống email")
    private String email;

    @NotBlank(message = "Không được để trống file (upload cv chưa thành công)")
    private String url;

    @Enumerated(EnumType.STRING)
    private ResumeStateEnum status;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @PrePersist
    public void handleBeforeCreate(){
        this.createdBy= SecurityUtil.getCurrentUserLogin().isPresent()==true?
                SecurityUtil.getCurrentUserLogin().get():"";
        this.createdAt=Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate(){
        this.updatedBy= SecurityUtil.getCurrentUserLogin().isPresent()==true?
                SecurityUtil.getCurrentUserLogin().get():"";
        this.updatedAt=Instant.now();
    }
}
