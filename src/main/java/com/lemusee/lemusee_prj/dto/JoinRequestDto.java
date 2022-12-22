package com.lemusee.lemusee_prj.dto;

import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.util.type.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRequestDto {

    private String email;
    private String password;
    private String nickname;

    public Member toMember() {
        return Member.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .role(Role.USER)
                .build();
    }
}
