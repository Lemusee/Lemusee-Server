package com.lemusee.lemusee_prj.config.jwt;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.lemusee.lemusee_prj.domain.PrincipalDetails;
import com.lemusee.lemusee_prj.dto.TokenDto;
import com.lemusee.lemusee_prj.util.baseUtil.BaseException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static com.lemusee.lemusee_prj.util.baseUtil.BaseResponseStatus.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    @Value("${jwt.time.access}") private Long JWT_ACCESS_TOKEN_EXPTIME;
    @Value("${jwt.time.refresh}") private Long JWT_REFRESH_TOKEN_EXPTIME;
    @Value("${jwt.secret.access}") private String JWT_ACCESS_SECRET_KEY;
    @Value("${jwt.secret.refresh}") private String JWT_REFRESH_SECRET_KEY;
    private Key accessKey;
    private  Key refreshKey;

    @PostConstruct
    public void initialize() {
        byte[] accessKeyBytes = Decoders.BASE64.decode(JWT_ACCESS_SECRET_KEY);
        this.accessKey = Keys.hmacShaKeyFor(accessKeyBytes);

        byte[] secretKeyBytes = Decoders.BASE64.decode(JWT_REFRESH_SECRET_KEY);
        this.refreshKey = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    public TokenDto createToken(Authentication authentication) {
        // 권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();

        String accessToken = Jwts.builder()
                .setSubject(((PrincipalDetails)authentication.getPrincipal()).getUserId().toString()) // payload "sub": "pk"
                .claim(AUTHORITIES_KEY,authorities) // payload "auth": "ROLE_USER"
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JWT_ACCESS_TOKEN_EXPTIME)) // set Expire Time
                .signWith(accessKey, SignatureAlgorithm.HS512)  // 사용할 암호화 알고리즘과
                .compact();

        String refreshToken =  Jwts.builder()
                .setSubject(((PrincipalDetails)authentication.getPrincipal()).getUserId().toString()) // payload "sub": "pk"
                .claim(AUTHORITIES_KEY,authorities) // payload "auth": "ROLE_USER"
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JWT_REFRESH_TOKEN_EXPTIME)) // set Expire Time
                .signWith(refreshKey, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();

        return new TokenDto(accessToken, refreshToken);
    }

    public String createAccessToken(Authentication authentication) {
        // 권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        return Jwts.builder()
                .setSubject(((PrincipalDetails)authentication.getPrincipal()).getUserId().toString()) // payload "sub": "pk"
                .claim(AUTHORITIES_KEY,authorities) // payload "auth": "ROLE_USER"
                .setExpiration(new Date(now.getTime() + JWT_ACCESS_TOKEN_EXPTIME)) // set Expire Time
                .signWith(accessKey, SignatureAlgorithm.HS256)  // 사용할 암호화 알고리즘과
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        // 권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();

        return Jwts.builder()
                .setSubject(((PrincipalDetails)authentication.getPrincipal()).getUserId().toString()) // payload "sub": "pk"
                .claim(AUTHORITIES_KEY,authorities) // payload "auth": "ROLE_USER"
                .setExpiration(new Date(now.getTime() + JWT_REFRESH_TOKEN_EXPTIME)) // set Expire Time
                .signWith(refreshKey, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);
    }

    // 토큰에서 회원 정보 추출
    public String getUseridFromAcs(String token) {
        return Jwts.parserBuilder().setSigningKey(accessKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getUseridFromRef(String token) {
        return Jwts.parserBuilder().setSigningKey(refreshKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // Jwt 토큰 유효성 검사
    public boolean validateAccessToken(String jwtHeader) throws BaseException {
        try {
            Jwts
                    .parserBuilder().setSigningKey(accessKey).build()
                    .parseClaimsJws(jwtHeader);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
            throw new BaseException(INVALID_JWT);
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
            throw new BaseException(INVALID_JWT);
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            throw new BaseException(EXPIRED_JWT);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            throw new BaseException(UNSUPPORTED_JWT);
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    // Refresh 토큰 유효성 검사
    public void validRefreshToken(String token) throws BaseException {
        try {
            Jwts
                    .parserBuilder().setSigningKey(refreshKey).build()
                    .parseClaimsJws(token);
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
            throw new BaseException(INVALID_JWT);
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
            throw new BaseException(INVALID_JWT);
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            throw new BaseException(EXPIRED_JWT);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            throw new BaseException(UNSUPPORTED_JWT);
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
            throw new BaseException(UNKNOWN_ERROR_JWT);
        }
    }

}