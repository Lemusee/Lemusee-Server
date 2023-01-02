package com.lemusee.lemusee_prj.controller;

import com.lemusee.lemusee_prj.dto.MemberInfoResDto;
import com.lemusee.lemusee_prj.service.MemberService;
import com.lemusee.lemusee_prj.util.baseUtil.BaseException;
import com.lemusee.lemusee_prj.util.baseUtil.BaseResponse;
import com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.lemusee.lemusee_prj.util.Constant.AUTHORIZATION_HEADER;
import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.SUCCESS;
import static com.lemusee.lemusee_prj.util.errorLogUtil.ErrorLogWriter.writeExceptionWithAuthorizedRequest;
import static com.lemusee.lemusee_prj.util.errorLogUtil.ErrorLogWriter.writeExceptionWithRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * 2.1 프로필 조회 API
     *
     * @GET /members
     */
    @GetMapping("")
    public BaseResponse<MemberInfoResDto> getMemberInfo(HttpServletRequest request) {
        String email = String.valueOf(request.getAttribute("email"));
        try {
            return new BaseResponse<>(memberService.getMemberInfo(email));
        } catch (BaseException error) {
            writeExceptionWithAuthorizedRequest(error, request);
            return new BaseResponse<>(error.getStatus());
        }
    }

    @PostMapping("/logout")
    public BaseResponse<BaseResponseStatus> logout(HttpServletRequest request) {
        String accessToken = request.getHeader(AUTHORIZATION_HEADER);
        try {
            memberService.logout(accessToken);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException error) {
            return new BaseResponse<>(error.getStatus());
        }
    }

}
