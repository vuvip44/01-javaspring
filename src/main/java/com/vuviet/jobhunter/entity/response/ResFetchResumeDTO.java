package com.vuviet.jobhunter.entity.response;

import com.vuviet.jobhunter.util.constant.ResumeStateEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
public class ResFetchResumeDTO {
    private  long id;

    private String email;

    private String url;

    @Enumerated(EnumType.STRING)
    private ResumeStateEnum status;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    private String companyName;

    private UserResume user;

    private JobResume job;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResume{
        private long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JobResume{
        private long id;
        private String name;
    }
}
