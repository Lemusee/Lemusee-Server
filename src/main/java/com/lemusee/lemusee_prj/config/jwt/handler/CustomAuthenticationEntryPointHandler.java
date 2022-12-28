package com.lemusee.lemusee_prj.config.jwt.handler;

import com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.*;

@Slf4j
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String)request.getAttribute("exception").toString();
        log.debug("log: exception: {} ", exception);
        BaseResponseStatus errorCode;

        /**
         * 토큰 없는 경우
         */
        if(exception.isEmpty()) {
            errorCode = EMPTY_JWT;
            setResponse(response, errorCode);
            return;
        }

        /**
         * 토큰 만료된 경우
         */
        if(exception.equals(EXPIRED_JWT.getCode()+"")) {
            errorCode = EXPIRED_JWT;
            setResponse(response, errorCode);
            return;
        }

        /**
         * 토큰 시그니처가 다른 경우
         */
        if(exception.equals(INVALID_JWT.getCode()+"")) {
            errorCode = INVALID_JWT;
            setResponse(response, errorCode);
        }
    }

    private void setResponse(HttpServletResponse response, BaseResponseStatus errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{" + "\"isSuccess\":false, "
                + "\"code\":\"" + errorCode.getCode() +"\","
                + "\"message\":\"" + errorCode.getMessage() + "\"}");
        response.getWriter().flush();
    }
}
