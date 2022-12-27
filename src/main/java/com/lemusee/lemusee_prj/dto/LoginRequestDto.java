package com.lemusee.lemusee_prj.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}