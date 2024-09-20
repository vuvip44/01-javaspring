package com.vuviet.jobhunter.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message="Không được để trống username")
    private String username;

    @NotBlank(message="Không được để trống password")
    private String password;
}
