package com.lemusee.lemusee_prj.dto;

import lombok.Data;

@Data
public class PasswordReqDto {
    private String email;
    private String oldPassword;
    private String newPassword;
}