package com.lemusee.lemusee_prj.service;

import com.lemusee.lemusee_prj.config.jwt.JwtTokenProvider;
import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.domain.PrincipalDetails;
import com.lemusee.lemusee_prj.dto.JoinRequestDto;
import com.lemusee.lemusee_prj.dto.LoginRequestDto;
import com.lemusee.lemusee_prj.dto.TokenDto;
import com.lemusee.lemusee_prj.repository.MemberRepository;
import com.lemusee.lemusee_prj.util.baseUtil.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.lemusee.lemusee_prj.util.Constant.PROVIDER_NONE;
import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.*;
import static org.hibernate.id.enhanced.OptimizerFactory.NONE;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    @Value("${jwt.time.refresh}") private Long JWT_REFRESH_TOKEN_EXPTIME;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;


    public void join(JoinRequestDto joinRequestDto) throws BaseException {

        checkEmailDuplicate(joinRequestDto.getEmail());
        Member member = joinRequestDto.toMember();
        member.encodePassword(passwordEncoder);
        memberRepository.save(member);
    }

    public String login(LoginRequestDto loginRequestDto) {

        Authentication authentication = attemptAuthentication(loginRequestDto);
        return jwtTokenProvider.createAccessToken(authentication);
    }

    public TokenDto loginAuto(LoginRequestDto loginRequestDto) {

        Authentication authentication = attemptAuthentication(loginRequestDto);

        TokenDto tokenDto = jwtTokenProvider.createToken(authentication);

        // redis에 RefreshToken 저장
        redisTemplate.opsForValue()
                .set("refreshToken:" + authentication.getName(), tokenDto.getRefreshToken(),
                        JWT_REFRESH_TOKEN_EXPTIME, TimeUnit.MILLISECONDS);

        return tokenDto;
    }

    public String reissue(String refreshToken, String accessToken) throws BaseException{
        // 토큰 유효성 검증
        jwtTokenProvider.validRefreshToken(refreshToken);
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        // Redis의 토큰과 비교
        String redisRefreshToken = String.valueOf(redisTemplate.opsForValue().get("refreshToken:" + authentication.getName()));
        if(!redisRefreshToken.equals(refreshToken)) {
            throw new BaseException(DIFFERENT_REFRESH_TOKEN);
        }
        return jwtTokenProvider.createAccessToken(authentication);
    }

    @Transactional(readOnly = true)
    public void checkEmailDuplicate(String email) throws BaseException{
        if (memberRepository.existsByEmailAndProvider(email, PROVIDER_NONE)) {
            throw new BaseException(USERS_EXISTS_EMAIL);
        }
    }

    @Transactional(readOnly = true)
    public void checkEmailExistence(String email) throws BaseException{
        if (!memberRepository.existsByEmailAndProvider(email, PROVIDER_NONE)) {
            throw new BaseException(USERS_EMPTY_USER_EMAIL);
        }
    }

    private Authentication attemptAuthentication(LoginRequestDto loginRequestDto){

        // 1. Email/PW 기반 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 PrincipalDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }



}
