package com.lemusee.lemusee_prj.config.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.time.access}") private Long JWT_ACCESS_TOKEN_EXPTIME;
    @Value("${jwt.time.refresh}") private Long JWT_REFRESH_TOKEN_EXPTIME;
    @Value("${jwt.secret.access}") private String JWT_ACCESS_SECRET_KEY;
    @Value("${jwt.secret.refresh}") private String JWT_REFRESH_SECRET_KEY;

}