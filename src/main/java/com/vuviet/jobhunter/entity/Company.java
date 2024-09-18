package com.vuviet.jobhunter.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vuviet.jobhunter.util.SecurityUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;
@Entity
@Data
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Không được để trống tên")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String address;

    private String logo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updateAt;

    private String createBy;

    private String updateBy;

    @PrePersist
    public void handleBeforeCreate(){
        this.createBy= SecurityUtil.getCurrentUserLogin().isPresent()==true?
            SecurityUtil.getCurrentUserLogin().get():"";
        this.createAt=Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate(){
        this.updateBy=SecurityUtil.getCurrentUserLogin().isPresent()==true?
                SecurityUtil.getCurrentUserLogin().get():"";
        this.updateAt=Instant.now();
    }
}
