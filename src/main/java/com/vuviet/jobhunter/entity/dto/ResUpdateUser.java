package com.vuviet.jobhunter.entity.dto;

import lombok.Data;

import java.time.Instant;
@Data
public class ResUpdateUser {
    private long id;

    private String name;

    private String gender;

    private String address;

    private int age;

    private Instant updatedAt;
}
