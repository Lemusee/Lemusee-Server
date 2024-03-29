package com.lemusee.lemusee_prj.config;

import com.lemusee.lemusee_prj.config.jwt.JwtTokenProvider;
import com.lemusee.lemusee_prj.config.jwt.filter.JwtAuthorizationFilter;
import com.lemusee.lemusee_prj.config.jwt.handler.CustomAccessDeniedHandler;
import com.lemusee.lemusee_prj.config.jwt.handler.CustomAuthenticationEntryPointHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CorsConfig corsConfig;
    private final RedisTemplate<String, Object> redisTemplate;


    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http
                .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class);
        http
                .csrf().disable() // 세션 사용 안하므로
                .formLogin().disable()
                .httpBasic().disable();
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 세션 사용 안함
        http
                .addFilter(corsConfig.corsFilter())
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPointHandler()) // 인증 예외 처리
                .accessDeniedHandler(new CustomAccessDeniedHandler()); // 인가 예외 처리
        http
                .authorizeRequests()
                // swagger
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/health").permitAll()
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();
        // .antMatchers("/").permitAll()

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
