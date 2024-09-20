package com.vuviet.jobhunter.entity.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ResCreateUser {
    private long id;

    private String name;

    private String email;

    private String gender;

    private String address;

    private int age;

    private Instant createdAt;

}
