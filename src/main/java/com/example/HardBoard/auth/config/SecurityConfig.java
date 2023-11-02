package com.example.HardBoard.auth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.authorizeRequests(auth -> auth
                .antMatchers("/public/**", "/auth/**")
                .permitAll()
                .antMatchers(
                        "/users/**", "/blocks/**",
                        "/posts/**", "/comments/**",
                        "/inquiries/**", "/reports/**")
                .authenticated()
                .antMatchers("/admin/**", "/notices/**")
                .hasAuthority("ROLE_ADMIN"));
        return http.build();
    }
}
