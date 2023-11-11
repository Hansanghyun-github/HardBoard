package com.example.HardBoard.config;

import com.example.HardBoard.config.auth.JwtAccessDeniedHandler;
import com.example.HardBoard.config.filter.JwtAuthorizationFilter;
import com.example.HardBoard.config.filter.JwtExceptionHandlingFilter;
import com.example.HardBoard.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final UserRepository userRepository;
    private final CorsConfig corsConfig;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .exceptionHandling()
                .accessDeniedHandler(jwtAccessDeniedHandler)        // Authorization fail handler

                .and()
                .apply(new MyCustomDsl())

                .and()
                .authorizeRequests(auth -> auth
                        .antMatchers("/notices/**", "/admin/**")
                        .hasAuthority("ROLE_ADMIN")
                        .antMatchers("/auth/**", "/public/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                );

        return http.build();
    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilterBefore(corsConfig.corsFilter(), LogoutFilter.class)
                    .addFilterAfter(new JwtAuthorizationFilter(userRepository)
                            ,ExceptionTranslationFilter.class)
                    .addFilterBefore(new JwtExceptionHandlingFilter(), JwtAuthorizationFilter.class);
        }
    }
}
