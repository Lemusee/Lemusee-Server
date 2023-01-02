package com.lemusee.lemusee_prj.util.mapper;

import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.dto.MemberInfoResDto;
import com.lemusee.lemusee_prj.dto.MemberInfoResDto.MemberInfoResDtoBuilder;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-01-02T23:55:32+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.16 (Eclipse Adoptium)"
)
public class DataMapperImpl implements DataMapper {

    @Override
    public MemberInfoResDto memberToMemberInfoDto(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberInfoResDtoBuilder memberInfoResDto = MemberInfoResDto.builder();

        memberInfoResDto.userId( member.getId() );
        memberInfoResDto.nickname( member.getNickname() );
        memberInfoResDto.email( member.getEmail() );
        memberInfoResDto.birthYear( member.getBirthYear() );
        memberInfoResDto.department( member.getDepartment() );
        memberInfoResDto.phone( member.getPhone() );
        memberInfoResDto.studentId( member.getStudentId() );
        memberInfoResDto.introduce( member.getIntroduce() );
        memberInfoResDto.isChief( member.getIsChief() );

        memberInfoResDto.team( member.getTeam().getType() );

        return memberInfoResDto.build();
    }
}
