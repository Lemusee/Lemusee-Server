package com.lemusee.lemusee_prj.service;

import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.domain.PrincipalDetails;
import com.lemusee.lemusee_prj.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.USERS_EMPTY_USER_EMAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("LOGIN");
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(USERS_EMPTY_USER_EMAIL.getMessage()));
        return PrincipalDetails.of(member);
    }

}
