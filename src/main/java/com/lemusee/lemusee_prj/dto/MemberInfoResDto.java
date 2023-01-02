package com.lemusee.lemusee_prj.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberInfoResDto {

    private Integer userId;
    private String nickname;
    private String email;
    private String birthYear;
    private String department;
    private String phone;
    private String studentId;
    private String introduce;
    private String team;
    private Boolean isChief;

}
