package com.lemusee.lemusee_prj.service;

import com.lemusee.lemusee_prj.config.jwt.JwtTokenProvider;
import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.dto.MemberProfileReqDto;
import com.lemusee.lemusee_prj.dto.MemberProfileResDto;
import com.lemusee.lemusee_prj.repository.MemberRepository;
import com.lemusee.lemusee_prj.util.baseUtil.BaseException;
import com.lemusee.lemusee_prj.util.mapper.DataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.SERVER_ERROR;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberProfileResDto getMemberProfile(String email) throws BaseException{
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new BaseException(SERVER_ERROR));
        return DataMapper.INSTANCE.memberToMemberProfileDto(member);
    }

    public void logout(String accessToken) throws BaseException{
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        // Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제
        if (redisTemplate.opsForValue().get("refreshToken:" + authentication.getName()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("refreshToken:" + authentication.getName());
        }
        // 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(accessToken);

        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    public void modifyMemberProfile(String email, MemberProfileReqDto memberProfileReqDto) throws BaseException{
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new BaseException(SERVER_ERROR));
        DataMapper.INSTANCE.updateMemberProfile(memberProfileReqDto,member);
    }
}
