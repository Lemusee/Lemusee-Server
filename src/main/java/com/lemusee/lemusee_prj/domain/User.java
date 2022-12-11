package com.lemusee.lemusee_prj.domain;

import com.lemusee.lemusee_prj.util.converter.RoleConverter;
import com.lemusee.lemusee_prj.util.converter.TeamConverter;
import com.lemusee.lemusee_prj.util.type.Role;
import com.lemusee.lemusee_prj.util.type.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import javax.validation.constraints.Size;

@DynamicInsert
@Entity
@Getter
@NoArgsConstructor
public class User extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 10, unique = true)
    @Size(max = 10)
    private String nickname;

    @Column(nullable = false)
    private String email;

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

    @Convert(converter = TeamConverter.class)
    private Team team;

    @Convert(converter = RoleConverter.class)
    private Role role;
}
