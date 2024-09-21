package com.vuviet.jobhunter.entity.response;

import com.vuviet.jobhunter.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
public class ResCreateUserDTO {
    private long id;

    private String name;

    private String email;

    private GenderEnum gender;

    private String address;

    private int age;

    private Instant createdAt;

    private CompanyUser companyUser;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyUser{
        private long id;
        private String name;
    }


}
