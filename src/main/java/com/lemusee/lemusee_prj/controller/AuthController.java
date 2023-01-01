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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.SUCCESS;
import static com.lemusee.lemusee_prj.util.errorLogUtil.ErrorLogWriter.writeExceptionWithRequest;

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
     * @POST /auth/join
     *
     * @Body joinRequestDto
     */
    @PostMapping("/join")
    public BaseResponse<BaseResponseStatus> join(HttpServletRequest request, @RequestBody JoinRequestDto joinRequestDto) {
        try {
            authService.join(joinRequestDto);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException error) {
            writeExceptionWithRequest(error, request);
            return new BaseResponse<>(error.getStatus());
        }
    }

    /**
     * 1.2 일반 로그인 API
     * @POST /auth/login
     *
     * @Body loginRequestDto
     * @return accessToken
     **/
    @PostMapping("/login")
    public BaseResponse<String> login(@RequestBody LoginRequestDto loginRequestDto) {
        return new BaseResponse<>(authService.login(loginRequestDto));
    }

    /**
     * 1.3 자동 로그인 API
     * @POST /auth/login/auto
     *
     * @body loginRequestDto
     * @return accessToken
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
     * @POST /auth/jwt
     * access token 만료시 재발급
     * @cookie refreshToken
     * @return accessToken
     */
    @PostMapping("/jwt")
    public BaseResponse<String> reissue(HttpServletRequest request, @CookieValue(value = "refreshToken", required = false) String refreshToken, @RequestBody String accessToken) {
        try {
            return new BaseResponse<>(authService.reissue(refreshToken, accessToken));
        } catch (BaseException error) {
            writeExceptionWithRequest(error, request);
            return new BaseResponse<>(error.getStatus());
        }
    }

    /**
     * 1.5 이메일 중복 체크 api
     * @GET /email?email=
     *
     * @param email
     */
    @GetMapping("/email")
    public BaseResponse<BaseResponseStatus> checkEmailDuplicate(@RequestParam(required = true) String email) {
        try {
            authService.checkEmailDuplicate(email);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException error) {
            return new BaseResponse<>(error.getStatus());
        }
    }

    /**
     * 1.6 이메일 유효성 체크 api
     * @GET /auth/email/existence?email=
     *
     * @param email
     */
    @GetMapping("/email/existence")
    public BaseResponse<String> checkEmailExistence(HttpServletRequest request, @RequestParam(required = true) String email) {
        try {
            authService.checkEmailExistence(email);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException error) {
            return new BaseResponse<>(error.getStatus());
        }
    }
}
