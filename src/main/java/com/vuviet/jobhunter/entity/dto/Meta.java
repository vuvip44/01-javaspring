package com.vuviet.jobhunter.entity.dto;

import lombok.Data;

@Data
public class Meta {
    private int page;
    private int pageSize;
    private int pages;
    private long total;
}
