package com.lemusee.lemusee_prj.config.jwt.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.INVALID_USER_JWT;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info(INVALID_USER_JWT.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{" + "\"isSuccess\":false, "
                + "\"code\":\"" + INVALID_USER_JWT.getCode() + "\","
                + "\"message\":\"" + INVALID_USER_JWT.getMessage() + "\"}");
        response.getWriter().flush();
    }
}
