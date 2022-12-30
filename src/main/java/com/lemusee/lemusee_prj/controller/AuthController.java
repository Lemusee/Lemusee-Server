package com.lemusee.lemusee_prj.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.lemusee.lemusee_prj.dto.JoinRequestDto;
import com.lemusee.lemusee_prj.dto.LoginRequestDto;
import com.lemusee.lemusee_prj.dto.LoginResponseDto;
import com.lemusee.lemusee_prj.dto.TokenDto;
import com.lemusee.lemusee_prj.service.AuthService;
import com.lemusee.lemusee_prj.util.baseUtil.BaseException;
import com.lemusee.lemusee_prj.util.baseUtil.BaseResponse;

import com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.service.TokenEndpoint;

import javax.servlet.http.HttpServletResponse;

import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.time.refresh}")
    private Long JWT_REFRESH_TOKEN_EXPTIME;

    private final AuthService authService;

    /**
     * 1.1 일반 회원가입 API
     * [POST] /auth/join
     *
     * @Body joinRequestDto
     */
    @PostMapping("/join")
    public BaseResponse<BaseResponseStatus> join(@RequestBody JoinRequestDto joinRequestDto) {
        try {
            authService.join(joinRequestDto);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException error) {
            return new BaseResponse<>(error.getStatus());
        }
    }

    /**
     * 1.2 일반 로그인 API
     * [POST] /auth/login
     *
     * @Body loginRequestDto
     **/
    @PostMapping("/login")
    public BaseResponse<String> login(@RequestBody LoginRequestDto loginRequestDto) {
        return new BaseResponse<>(authService.login(loginRequestDto));
    }

    /**
     * 1.3 자동 로그인 API
     * [POST] /auth/login/auto
     *
     * @Body loginRequestDto
     **/
    @PostMapping("/login/auto")
    public BaseResponse<String> loginAuto(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        TokenDto tokenDto = authService.loginAuto(loginRequestDto);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
                .maxAge(JWT_REFRESH_TOKEN_EXPTIME)
                .httpOnly(true)
                .path("/")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());

        return new BaseResponse<>(tokenDto.getAccessToken());
    }

    /**
     * 1.4 토큰 재발급 api
     * [GET] /auth/jwt
     * access token 만료시 재발급
     * @cookie refreshToken
     * @return accessToken
     */
    @PostMapping("/jwt")
    public BaseResponse<String> reissue(@CookieValue(value = "refreshToken", required = false) String refreshToken, @RequestBody String accessToken) {
        try {
            return new BaseResponse<>(authService.reissue(refreshToken, accessToken));
        } catch (BaseException error) {
            return new BaseResponse<>(error.getStatus());
        }
    }
}
