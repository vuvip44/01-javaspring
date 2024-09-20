package com.vuviet.jobhunter.entity.dto;

import com.vuviet.jobhunter.util.constant.GenderEnum;
import lombok.*;

import java.time.Instant;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private long id;
    private String email;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
    private Instant createdAt;
}
