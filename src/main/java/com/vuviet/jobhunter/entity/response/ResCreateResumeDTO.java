package com.vuviet.jobhunter.entity.response;

import lombok.Data;

import java.time.Instant;

@Data
public class ResCreateResumeDTO {
    private long id;

    private Instant createdAt;

    private String createdBy;
}
