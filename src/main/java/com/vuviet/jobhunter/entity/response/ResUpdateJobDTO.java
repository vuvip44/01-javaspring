package com.vuviet.jobhunter.entity.response;

import com.vuviet.jobhunter.util.constant.LevelEnum;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.Instant;
import java.util.List;
@Data
public class ResUpdateJobDTO {
    private long id;

    private String name;

    private String location;

    private double salary;

    private int quantity;

    private LevelEnum level;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private Instant startDate;

    private Instant endDate;

    private boolean active;

    private Instant updateAt;

    private String updateBy;

    private List<String> skills;
}
