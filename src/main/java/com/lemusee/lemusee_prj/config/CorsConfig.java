package com.lemusee.lemusee_prj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://lemusee.site","https://prod.lemusee.site"));
        configuration.addAllowedHeader("*");
        configuration.setAllowedMethods(List.of("GET","POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }
}
