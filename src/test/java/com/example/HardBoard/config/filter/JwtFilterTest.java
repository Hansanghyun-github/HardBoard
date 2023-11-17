package com.example.HardBoard.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.api.service.auth.AuthValidationService;
import com.example.HardBoard.api.service.user.UserService;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class JwtFilterTest {
    @MockBean UserService userService;
    @MockBean AuthValidationService authValidationService;

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("Authorization 헤더가 비어 있다면 인증에 실패한다")
    void emptyAuthorizationHeaderBeFailAuthentication() throws Exception {
        // when // then
        mockMvc.perform(get("/users/1")) // 인증이 필요한 URL
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Access is denied"));
    }

    @Test
    @DisplayName("Authorization 헤더의 값이 Token Prefix로 시작하지 않는다면 인증에 실패한다")
    void notStartWithTokenPrefixBeFailAuthentication() throws Exception {
        // when // then
        mockMvc.perform(get("/users/1")
                        .header(JwtProperties.HEADER_STRING, "anyString"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("token prefix is wrong"));
    }

    @Test
    @DisplayName("만료된 토큰을 보내면 인증에 실패한다")
    void expiredTokenBeFailAuthentication() throws Exception {
        // given
        String nickname = "husi";
        String email = "email@email";
        String password = "password";

        userRepository.save(
                User.builder()
                        .email(email)
                        .nickname(nickname)
                        .password(password)
                        .build());

        String accessToken = JWT.create()
                .withSubject(nickname)
                .withExpiresAt(new Date(System.currentTimeMillis() - JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("email", email)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        // when // then
        mockMvc.perform(get("/users/1")
                        .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("The Token has expired"));
    }

    @Test
    @DisplayName("잘못된 Secret의 토큰을 보내면 실패한다")
    void wrongSecretTokenBeFailAuthentication() throws Exception {
        // given
        String nickname = "husi";
        String email = "email@email";
        String password = "password";

        userRepository.save(
                User.builder()
                        .email(email)
                        .nickname(nickname)
                        .password(password)
                        .build());

        String accessToken = JWT.create()
                .withSubject(nickname)
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("email", email)
                .sign(Algorithm.HMAC512("wrong"));

        // when // then
        mockMvc.perform(get("/users/1")
                        .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message")
                        .value("The Token's Signature resulted invalid when verified using the Algorithm: HmacSHA512"));
    }

    @Test
    @DisplayName("잘못된 claim의 토큰을 보내면 실패한다")
    void wrongClaimTokenBeFailAuthentication() throws Exception {
        // given
        String nickname = "husi";
        String email = "email@email";
        String password = "password";

        userRepository.save(
                User.builder()
                        .email(email)
                        .nickname(nickname)
                        .password(password)
                        .build());

        String accessToken = JWT.create()
                .withSubject(nickname)
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("wrongEmail", email)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        // when // then
        mockMvc.perform(get("/users/1")
                        .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Email claim is null"));
    }

    @Test
    @DisplayName("잘못된 이메일의 토큰을 보내면 실패한다")
    void wrongEmailTokenBeFailAuthentication() throws Exception {
        // given
        String nickname = "husi";
        String email = "email@email";
        String password = "password";

        userRepository.save(
                User.builder()
                        .email(email)
                        .nickname(nickname)
                        .password(password)
                        .build());

        String accessToken = JWT.create()
                .withSubject(nickname)
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("email", email + "wrong")
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        // when // then
        mockMvc.perform(get("/users/1")
                        .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Email is wrong"));
    }
}