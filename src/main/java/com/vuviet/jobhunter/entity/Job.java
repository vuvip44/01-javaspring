package com.vuviet.jobhunter.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vuviet.jobhunter.util.SecurityUtil;
import com.vuviet.jobhunter.util.constant.LevelEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "name không được để trống")
    private String name;

    @NotBlank(message = "location không được để trống")
    private String location;

    private int salary;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private LevelEnum level;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private Instant startDate;

    private Instant endDate;

    private boolean active;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"jobs"})//bo thuoc tinh jobs nay o ben entity skill
    @JoinTable(name="job_skill",joinColumns = @JoinColumn(name = "job_id"),
    inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    @OneToMany(mappedBy = "job",fetch = FetchType.LAZY)
    @JsonIgnore
    List<Resume> resumes;

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
