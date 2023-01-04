package com.lemusee.lemusee_prj.dto;

import lombok.Data;

@Data
public class MemberProfileReqDto {

    private String nickname;
    private String birthYear;
    private String department;
    private String phone;
    private String studentId;
    private String introduce;
}
