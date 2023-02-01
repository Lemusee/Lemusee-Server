package com.lemusee.lemusee_prj.config.jwt.filter;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.lemusee.lemusee_prj.config.jwt.JwtTokenProvider;
import com.lemusee.lemusee_prj.util.baseUtil.BaseException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jdk.swing.interop.SwingInterOpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static com.lemusee.lemusee_prj.util.Constant.AUTHORIZATION_HEADER;
import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.*;
import static com.lemusee.lemusee_prj.util.errorLogUtil.ErrorLogWriter.writeExceptionWithAuthorizedRequest;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request.getRequestURI().startsWith("/auth")
                || request.getRequestURI().startsWith("/favicon")) { // "/auth/*" uri들은 jwt체크 불필요
            log.info("JWT 인증 통과");
            chain.doFilter(request, response);
            return;
        }
        log.info("JWT 인증 시작");

        try {
            String jwtHeader = request.getHeader(AUTHORIZATION_HEADER);

            if (!StringUtils.hasText(jwtHeader)) {
                request.setAttribute("exception", "");
            }
            if (StringUtils.hasText(jwtHeader) && jwtTokenProvider.validateAccessToken(jwtHeader)) {
                // (추가) Redis 에 해당 accessToken logout 여부 확인
                String isLogout = (String) redisTemplate.opsForValue().get(jwtHeader);
                if (ObjectUtils.isEmpty(isLogout)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(jwtHeader);
                    String email = authentication.getName();
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    request.setAttribute("email", email);
                }
            }
        } catch (BaseException e) {
            request.setAttribute("exception", e.getStatus().getCode());
        }
        chain.doFilter(request, response);
    }
}