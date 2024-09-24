package com.vuviet.jobhunter.entity.response;

import lombok.Data;

import java.time.Instant;

@Data
public class ResUpdateResumeDTO {
    private Instant updateAt;

    private String updateBy;
}
