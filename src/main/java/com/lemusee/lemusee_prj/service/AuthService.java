package com.lemusee.lemusee_prj.service;

import com.lemusee.lemusee_prj.config.jwt.JwtTokenProvider;
import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.dto.JoinReqDto;
import com.lemusee.lemusee_prj.dto.LoginReqDto;
import com.lemusee.lemusee_prj.dto.PatchPasswordReqDto;
import com.lemusee.lemusee_prj.dto.TokenDto;
import com.lemusee.lemusee_prj.repository.MemberRepository;
import com.lemusee.lemusee_prj.util.baseUtil.BaseException;
import com.lemusee.lemusee_prj.util.mapper.DataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.lemusee.lemusee_prj.util.Constant.PROVIDER_NONE;
import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.*;

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


    public void join(JoinReqDto joinReqDto) throws BaseException {

        checkEmailDuplicate(joinReqDto.getEmail());
        Member member = joinReqDto.toMember();
        member.encodePassword(passwordEncoder, joinReqDto.getPassword());
        memberRepository.save(member);
    }

    public String login(LoginReqDto loginReqDto) {

        Authentication authentication = attemptAuthentication(loginReqDto);
        return jwtTokenProvider.createAccessToken(authentication);
    }

    public TokenDto loginAuto(LoginReqDto loginReqDto) {

        Authentication authentication = attemptAuthentication(loginReqDto);

        TokenDto tokenDto = jwtTokenProvider.createToken(authentication);

        // redis에 RefreshToken 저장
        redisTemplate.opsForValue()
                .set("refreshToken:" + authentication.getName(), tokenDto.getRefreshToken(),
                        JWT_REFRESH_TOKEN_EXPTIME, TimeUnit.MILLISECONDS);

        return tokenDto;
    }

    public String reissue(String refreshToken, String accessToken) throws BaseException{
        jwtTokenProvider.checkAccessTokenExpiration(accessToken);
        jwtTokenProvider.validRefreshToken(refreshToken);

        Authentication authentication = jwtTokenProvider.getAuthenticationFromRef(refreshToken);

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

    public void modifyPassword(PatchPasswordReqDto patchPasswordReqDto) throws BaseException {

        String email = patchPasswordReqDto.getEmail();

        Member member = memberRepository.findByEmailAndProvider(email,PROVIDER_NONE).orElseThrow(() -> new BaseException(USERS_EMPTY_USER_EMAIL));
        member.encodePassword(passwordEncoder, patchPasswordReqDto.getNewPassword());
    }

    private Authentication attemptAuthentication(LoginReqDto loginReqDto){

        // 1. Email/PW 기반 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginReqDto.getEmail(), loginReqDto.getPassword());

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 PrincipalDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }



}
