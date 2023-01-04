package com.lemusee.lemusee_prj.dto;

import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.util.type.Role;
import com.lemusee.lemusee_prj.util.type.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinReqDto {

    private String email;
    private String password;
    private String nickname;

    public Member toMember() {
        return Member.builder()
                .nickname(nickname)
                .email(email)
                .team(Team.INACTIVE)
                .role(Role.USER)
                .build();
    }
}
