package com.lemusee.lemusee_prj.controller;

import com.lemusee.lemusee_prj.dto.MemberProfileReqDto;
import com.lemusee.lemusee_prj.dto.MemberProfileResDto;
import com.lemusee.lemusee_prj.dto.PasswordReqDto;
import com.lemusee.lemusee_prj.service.MemberService;
import com.lemusee.lemusee_prj.util.baseUtil.BaseException;
import com.lemusee.lemusee_prj.util.baseUtil.BaseResponse;
import com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.lemusee.lemusee_prj.util.Constant.AUTHORIZATION_HEADER;
import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.SUCCESS;
import static com.lemusee.lemusee_prj.util.errorLogUtil.ErrorLogWriter.*;

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
    public BaseResponse<MemberProfileResDto> getMemberProfile(HttpServletRequest request) {
        String email = String.valueOf(request.getAttribute("email"));
        return new BaseResponse<>(memberService.getMemberProfile(email));
    }

    /**
     * 2.2 로그아웃 API
     *
     * @POST /members/logout
     */
    @PostMapping("/logout")
    public BaseResponse<BaseResponseStatus> logout(HttpServletRequest request) {
        String accessToken = request.getHeader(AUTHORIZATION_HEADER);
        try {
            memberService.logout(accessToken);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException error) {
            writeExceptionWithAuthorizedRequest(error, request);
            return new BaseResponse<>(error.getStatus());
        }
    }

    /**
     * 2.3 포로필 변경 API
     *
     * @PATCH /members/profile
     */
    @PatchMapping("/profile")
    public BaseResponse<BaseResponseStatus> modifyMemberProfile(HttpServletRequest request, MemberProfileReqDto memberProfileReqDto) {
        String email = String.valueOf(request.getAttribute("email"));
        memberService.modifyMemberProfile(email, memberProfileReqDto);
        return new BaseResponse<>(SUCCESS);
    }

    /**
     * 2.5 비밀번호 재설정(로그인 상태) API
     *
     * @PATCH /members/password
     */
    @PatchMapping("/password")
    public BaseResponse<BaseResponseStatus> modifyMemberProfile(HttpServletRequest request, @RequestBody PasswordReqDto passwordReqDto) {
        String email = String.valueOf(request.getAttribute("email"));
        try {
            memberService.modifyPassword(email, passwordReqDto);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException error) {
            writeExceptionWithAuthorizedRequest(error, request);
            return new BaseResponse<>(error.getStatus());
        }
    }


    /**
     * 2.6 회원 탈퇴 API
     *
     * @DELETE /members/secession
     */
    @DeleteMapping("/secession")
    public BaseResponse<BaseResponseStatus> removeMember(HttpServletRequest request) {
        String email = String.valueOf(request.getAttribute("email"));
        memberService.removeMember(email);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }
}
