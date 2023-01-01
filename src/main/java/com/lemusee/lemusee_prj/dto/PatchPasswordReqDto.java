package com.lemusee.lemusee_prj.dto;

import lombok.Data;

@Data
public class PatchPasswordReqDto {
    private String email;
    private String newPassword;
}