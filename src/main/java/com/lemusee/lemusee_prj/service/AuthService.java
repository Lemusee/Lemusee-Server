package com.lemusee.lemusee_prj.service;

import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.dto.JoinRequestDto;
import com.lemusee.lemusee_prj.repository.MemberRepository;
import com.lemusee.lemusee_prj.util.baseUtil.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.lemusee.lemusee_prj.util.Constant.PROVIDER_NONE;
import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.POST_USERS_EXISTS_EMAIL;
import static org.hibernate.id.enhanced.OptimizerFactory.NONE;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public void createMember(JoinRequestDto joinRequestDto) throws BaseException {
        if (memberRepository.findByEmailAndProvider(joinRequestDto.getEmail(),PROVIDER_NONE).isPresent()) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        Member member = joinRequestDto.toMember();
        member.encodePassword(passwordEncoder);
        memberRepository.save(member);
    }
}
