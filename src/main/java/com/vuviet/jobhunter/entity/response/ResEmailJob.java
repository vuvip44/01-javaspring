package com.vuviet.jobhunter.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class ResEmailJob {
    private String name;

    private double salary;

    private CompanyEmail company;

    private List<SkillEmail> skills;

    @Data
    @AllArgsConstructor
    public static class CompanyEmail{
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class SkillEmail{
        private String name;
    }
}
