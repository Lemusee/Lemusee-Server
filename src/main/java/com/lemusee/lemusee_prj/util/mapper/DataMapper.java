package com.lemusee.lemusee_prj.util.mapper;

import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.dto.MemberProfileReqDto;
import com.lemusee.lemusee_prj.dto.MemberProfileResDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DataMapper {
    DataMapper INSTANCE = Mappers.getMapper(DataMapper.class);
    @Mapping(source = "id", target = "userId")
    @Mapping(target = "team", expression = "java(member.getTeam().getType())")
    MemberProfileResDto memberToMemberProfileDto(Member member);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMemberProfile(MemberProfileReqDto memberProfileReqDto, @MappingTarget Member member);
}