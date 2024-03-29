package com.lemusee.lemusee_prj.controller;

import com.lemusee.lemusee_prj.dto.JoinReqDto;
import com.lemusee.lemusee_prj.dto.LoginReqDto;
import com.lemusee.lemusee_prj.dto.PasswordReqDto;
import com.lemusee.lemusee_prj.dto.TokenDto;
import com.lemusee.lemusee_prj.service.AuthService;
import com.lemusee.lemusee_prj.service.YoutubeService;
import com.lemusee.lemusee_prj.util.baseUtil.BaseException;
import com.lemusee.lemusee_prj.util.baseUtil.BaseResponse;

import com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.SUCCESS;
import static com.lemusee.lemusee_prj.util.errorLogUtil.ErrorLogWriter.writeExceptionWithRequest;
import static com.lemusee.lemusee_prj.util.errorLogUtil.ErrorLogWriter.writeExceptionWithRequestNoQuery;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.time.refresh}")
    private Long JWT_REFRESH_TOKEN_EXPTIME;

    private final AuthService authService;
    private final YoutubeService youtubeService;


    /**
     * 1.1 일반 회원가입 API
     *
     * @POST /auth/join
     * @Body joinRequestDto
     */
    @PostMapping("/join")
    public BaseResponse<BaseResponseStatus> join(HttpServletRequest request, @RequestBody JoinReqDto joinReqDto) {
        try {
            authService.join(joinReqDto);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException error) {
            writeExceptionWithRequest(error, request);
            return new BaseResponse<>(error.getStatus());
        }
    }

    /**
     * 1.2 일반 로그인 API
     *
     * @return accessToken
     * @POST /auth/login
     * @Body loginRequestDto
     **/
    @PostMapping("/login")
    public BaseResponse<String> login(@RequestBody LoginReqDto loginReqDto) {
        return new BaseResponse<>(authService.login(loginReqDto));
    }

    /**
     * 1.3 자동 로그인 API
     *
     * @return accessToken
     * @POST /auth/login/auto
     * @body loginRequestDto
     **/
    @PostMapping("/login/auto")
    public BaseResponse<String> loginAuto(@RequestBody LoginReqDto loginReqDto, HttpServletResponse response) {
        TokenDto tokenDto = authService.loginAuto(loginReqDto);

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
     *
     * @return accessToken
     * @POST /auth/jwt
     * access token 만료시 재발급
     * @cookie refreshToken
     */
    @PostMapping("/jwt")
    public BaseResponse<String> reissue(HttpServletRequest request, @CookieValue(value = "refreshToken", required = false) String refreshToken, @RequestBody String accessToken) {
        try {
            return new BaseResponse<>(authService.reissue(refreshToken, accessToken));
        } catch (BaseException error) {
            writeExceptionWithRequestNoQuery(error, request);
            return new BaseResponse<>(error.getStatus());
        }
    }

    /**
     * 1.5 이메일 중복 체크 api
     *
     * @param email
     * @GET /email?email=
     */
    @GetMapping("/email")
    public BaseResponse<BaseResponseStatus> checkEmailDuplicate(HttpServletRequest request, @RequestParam(required = true) String email) {
        try {
            authService.checkEmailDuplicate(email);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException error) {
            writeExceptionWithRequest(error, request);
            return new BaseResponse<>(error.getStatus());
        }
    }

    /**
     * 1.6 이메일 유효성 체크 api
     *
     * @param email
     * @GET /auth/email/existence?email=
     */
    @GetMapping("/email/existence")
    public BaseResponse<String> checkEmailExistence(HttpServletRequest request, @RequestParam(required = true) String email) {
        try {
            authService.checkEmailExistence(email);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException error) {
            writeExceptionWithRequest(error, request);
            return new BaseResponse<>(error.getStatus());
        }
    }

    /**
     * 1.7 비밀번호 재설정 (비 로그인 상태) API
     *
     * @param passwordReqDto
     * @PATCH /auth/password
     */
    @PatchMapping("/password")
    public BaseResponse<BaseResponseStatus> modifyPassword(HttpServletRequest request, @RequestBody PasswordReqDto passwordReqDto) {
        try {
            authService.modifyPassword(passwordReqDto);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException error) {
            writeExceptionWithRequest(error, request);
            return new BaseResponse<>(error.getStatus());
        }
    }

    @GetMapping("/youtube")
    public void getPlaylistInfo(@RequestParam(required = true) String playlistId) throws Exception {
        youtubeService.addPlaylistVideos(playlistId);
    }
}
