package com.lemusee.lemusee_prj.service;

import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.dto.JoinReqDto;
import com.lemusee.lemusee_prj.dto.MemberInfoResDto;
import com.lemusee.lemusee_prj.repository.MemberRepository;
import com.lemusee.lemusee_prj.util.baseUtil.BaseException;
import com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus;
import com.lemusee.lemusee_prj.util.mapper.DataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.SERVER_ERROR;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberInfoResDto getMemberInfo(Integer userId) throws BaseException{
        Member member = memberRepository.findById(userId).orElseThrow(() -> new BaseException(SERVER_ERROR));
        return DataMapper.INSTANCE.memberToMemberInfoDto(member);
    }
}
