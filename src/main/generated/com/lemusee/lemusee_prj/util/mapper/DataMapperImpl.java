package com.lemusee.lemusee_prj.util.mapper;

import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.dto.MemberProfileResDto;
import com.lemusee.lemusee_prj.dto.MemberProfileResDto.MemberProfileResDtoBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-01-07T01:04:34+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.16 (Eclipse Adoptium)"
)
@Component
public class DataMapperImpl implements DataMapper {

    @Override
    public MemberProfileResDto memberToMemberProfileDto(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberProfileResDtoBuilder memberProfileResDto = MemberProfileResDto.builder();

        memberProfileResDto.userId( member.getId() );
        memberProfileResDto.nickname( member.getNickname() );
        memberProfileResDto.email( member.getEmail() );
        memberProfileResDto.birthYear( member.getBirthYear() );
        memberProfileResDto.department( member.getDepartment() );
        memberProfileResDto.phone( member.getPhone() );
        memberProfileResDto.studentId( member.getStudentId() );
        memberProfileResDto.introduce( member.getIntroduce() );
        memberProfileResDto.isChief( member.getIsChief() );

        memberProfileResDto.team( member.getTeam().getType() );

        return memberProfileResDto.build();
    }
}
