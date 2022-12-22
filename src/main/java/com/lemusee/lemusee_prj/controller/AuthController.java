package com.lemusee.lemusee_prj.controller;

import com.lemusee.lemusee_prj.dto.JoinRequestDto;
import com.lemusee.lemusee_prj.service.AuthService;
import com.lemusee.lemusee_prj.util.baseUtil.BaseResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public BaseResponse<String> join(@RequestBody JoinRequestDto joinRequestDto) {
        authService.createUser(joinRequestDto);
        return new BaseResponse<>(SUCCESS);
    }
}
