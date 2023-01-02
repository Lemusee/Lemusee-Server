package com.lemusee.lemusee_prj.util.mapper;

import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.dto.JoinReqDto;
import com.lemusee.lemusee_prj.dto.MemberInfoResDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DataMapper {
    DataMapper INSTANCE = Mappers.getMapper(DataMapper.class);
    @Mapping(source = "id", target = "userId")
    @Mapping(target = "team", expression = "java(member.getTeam().getType())")
    MemberInfoResDto memberToMemberInfoDto(Member member);
}