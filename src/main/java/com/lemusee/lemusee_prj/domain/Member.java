package com.lemusee.lemusee_prj.domain;

import com.lemusee.lemusee_prj.dto.MemberProfileReqDto;
import com.lemusee.lemusee_prj.util.converter.RoleConverter;
import com.lemusee.lemusee_prj.util.converter.TeamConverter;
import com.lemusee.lemusee_prj.util.type.Role;
import com.lemusee.lemusee_prj.util.type.Team;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Size;

@DynamicInsert
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(length = 10)
    @Size(max = 10)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Size(max = 6)
    private String birthYear;

    @Size(max = 30)
    private String department;

    @Size(min = 9, max = 14)
    private String phone;

    @Size(max = 9)
    private String studentId;

    @Size(max = 20)
    private String introduce;

    @ColumnDefault("false")
    private Boolean isChief;

    @ColumnDefault("'none'")
    private String provider;

    private String providerId;

    @Convert(converter = TeamConverter.class)
    private Team team;

    @Convert(converter = RoleConverter.class)
    private Role role;

    public void encodePassword(PasswordEncoder passwordEncoder, String password) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateProfile(MemberProfileReqDto memberProfileReqDto) {
        if (memberProfileReqDto.getNickname() != null) {
            this.nickname = memberProfileReqDto.getNickname();
        }
        if (memberProfileReqDto.getBirthYear() != null) {
            this.birthYear = memberProfileReqDto.getBirthYear();
        }
        if (memberProfileReqDto.getDepartment() != null) {
            this.department = memberProfileReqDto.getDepartment();
        }
        if (memberProfileReqDto.getPhone() != null) {
            this.phone = memberProfileReqDto.getPhone();
        }
        if (memberProfileReqDto.getStudentId() != null) {
            this.studentId = memberProfileReqDto.getStudentId();
        }
        if (memberProfileReqDto.getIntroduce() != null) {
            this.introduce = memberProfileReqDto.getIntroduce();
        }
    }


}
