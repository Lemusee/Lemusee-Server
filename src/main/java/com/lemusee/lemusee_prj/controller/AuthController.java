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

import org.springframework.http.ResponseCookie;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /**
    * 1.1 일반 회원가입 API
    * [POST] /auth/join
    *
    * @Body joinRequestDto
    * */
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
    public BaseResponse<TokenDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return new BaseResponse<>(authService.login(loginRequestDto));
    }
}
