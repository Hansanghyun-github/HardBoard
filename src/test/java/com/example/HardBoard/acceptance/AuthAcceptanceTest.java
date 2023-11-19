package com.example.HardBoard.acceptance;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.auth.request.AuthChangePasswordRequest;
import com.example.HardBoard.api.controller.auth.request.AuthJoinRequest;
import com.example.HardBoard.api.controller.auth.request.AuthLoginRequest;
import com.example.HardBoard.api.controller.auth.request.AuthRemadeTokenRequest;
import com.example.HardBoard.api.service.auth.MailService;
import com.example.HardBoard.api.service.auth.request.MailCheckServiceRequest;
import com.example.HardBoard.api.service.auth.response.TokenResponse;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.refreshToken.RefreshTokenRepository;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthAcceptanceTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired RefreshTokenRepository refreshTokenRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @MockBean MailService mailService;
    
    @Test
    @Order(1)
    @DisplayName("회원가입 API 테스트")
    void joinApiTest() throws Exception {
        // given
        String email = "email@email";
        AuthJoinRequest joinRequest = AuthJoinRequest.builder()
                .email(email)
                .password("password")
                .nickname("husi")
                .authNumber("283")
                .build();

        when(mailService.isCorrectNumber(any())).thenReturn(true);
        
        // when // then
        mockMvc.perform(
                post("/auth/join")
                        .content(objectMapper.writeValueAsString(joinRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        verify(mailService, times(1))
                .isCorrectNumber(any(MailCheckServiceRequest.class));

        User user = userRepository.findByEmail(email)
                .orElseThrow();
        assertThat(user.getNickname()).isEqualTo("husi");
        assertThat(passwordEncoder
                .matches("password", user.getPassword())).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("로그인 API 테스트")
    void loginApiTest() throws Exception {
        // given
        String email = "email@email";
        String password = "password";
        String nickname = "husi";

        when(mailService.isCorrectNumber(any())).thenReturn(true);

        mockMvc.perform(
                post("/auth/join")
                        .content(objectMapper.writeValueAsString(
                                AuthJoinRequest.builder()
                                        .email(email)
                                        .password(password)
                                        .nickname(nickname)
                                        .authNumber("283")
                                        .build()))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        AuthLoginRequest request = AuthLoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        // when
        String content = mockMvc.perform(
                post("/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        Object data = apiResponse.getData();
        TokenResponse tokenResponse = objectMapper
                .readValue(objectMapper.writeValueAsString(data), TokenResponse.class);
        DecodedJWT verified = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
                .verify(tokenResponse.getAccessToken()
                        .replace(JwtProperties.TOKEN_PREFIX, ""));

        // then
        assertThat(apiResponse.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(verified.getClaim("email").asString()).isEqualTo(email);
        assertThat(verified.getSubject()).isEqualTo(nickname);
        assertThat(
                refreshTokenRepository.findByRefreshToken(tokenResponse.getRefreshToken())
                        .isEmpty()
        ).isFalse();
        assertThat(
                refreshTokenRepository.findByRefreshToken(tokenResponse.getRefreshToken())
                        .orElseThrow().getUser().getEmail())
                .isEqualTo(email);
    }

    @Test
    @Order(3)
    @DisplayName("Join부터 Logout까지 테스트")
    void joinToLogout() throws Exception {
        // given
        String email = "email@email";
        AuthJoinRequest joinRequest = AuthJoinRequest.builder()
                .email(email)
                .password("password")
                .nickname("husi")
                .authNumber("283")
                .build();

        AuthLoginRequest loginRequest = AuthLoginRequest.builder()
                .email(email)
                .password("password")
                .build();

        when(mailService.isCorrectNumber(any())).thenReturn(true);

        mockMvc.perform(
                post("/auth/join")
                        .content(objectMapper.writeValueAsString(joinRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String content = mockMvc.perform(
                post("/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        Object data = apiResponse.getData();
        TokenResponse tokenResponse = objectMapper
                .readValue(objectMapper.writeValueAsString(data), TokenResponse.class);

        // when // then
        mockMvc.perform(
                post("/users/logout")
                        .header(JwtProperties.HEADER_STRING, tokenResponse.getAccessToken())
        ).andExpect(status().isOk());

        assertThat(
                refreshTokenRepository.findByRefreshToken(tokenResponse.getRefreshToken())
                        .isEmpty()
        ).isTrue();
    }

    @Test
    @DisplayName("로그인 없이 비밀번호 변경")
    void changePasswordWithoutLogin() throws Exception {
        // given
        String email = "email@email";
        String password = "password";
        String nickname = "husi";

        when(mailService.isCorrectNumber(any())).thenReturn(true);

        mockMvc.perform(
                post("/auth/join")
                        .content(objectMapper.writeValueAsString(
                                AuthJoinRequest.builder()
                                        .email(email)
                                        .password(password)
                                        .nickname(nickname)
                                        .authNumber("283")
                                        .build()))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String newPassword = "newPassword";
        AuthChangePasswordRequest request = AuthChangePasswordRequest.builder()
                .email(email)
                .prevPassword(password)
                .newPassword(newPassword)
                .authNumber("283")
                .build();

        when(mailService.isCorrectNumber(any(MailCheckServiceRequest.class)))
                .thenReturn(true);

        // when
        mockMvc.perform(
                post("/auth/password/change")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        assertThat(passwordEncoder.matches(newPassword, userRepository.findByEmail(email)
                .orElseThrow().getPassword())).isTrue();
    }

    @Test
    @DisplayName("토큰들을 변경한다")
    void changeTokens() throws Exception {
        // given
        String email = "email@email";
        String password = "password";
        String nickname = "husi";

        when(mailService.isCorrectNumber(any())).thenReturn(true);

        mockMvc.perform(
                post("/auth/join")
                        .content(objectMapper.writeValueAsString(
                                AuthJoinRequest.builder()
                                        .email(email)
                                        .password(password)
                                        .nickname(nickname)
                                        .authNumber("283")
                                        .build()))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        AuthLoginRequest loginRequest = AuthLoginRequest.builder()
                .email(email)
                .password("password")
                .build();

        String content = mockMvc.perform(
                post("/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        Object data = apiResponse.getData();
        TokenResponse tokenResponse = objectMapper
                .readValue(objectMapper.writeValueAsString(data), TokenResponse.class);

        AuthRemadeTokenRequest request = AuthRemadeTokenRequest.builder()
                .refreshToken(tokenResponse.getRefreshToken())
                .build();

        String prevRefreshToken = tokenResponse.getRefreshToken();

        // when // then
        String newContent = mockMvc.perform(
                        post("/users/refreshToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(JwtProperties.HEADER_STRING, tokenResponse.getAccessToken()))
                .andReturn().getResponse().getContentAsString();
        ApiResponse newApiResponse = objectMapper.readValue(newContent, ApiResponse.class);
        Object newData = newApiResponse.getData();
        TokenResponse newTokenResponse = objectMapper
                .readValue(objectMapper.writeValueAsString(newData), TokenResponse.class);

        assertThat(refreshTokenRepository
                .findByRefreshToken(prevRefreshToken)
                .isPresent()).isFalse();
        assertThat(refreshTokenRepository
                .findByRefreshToken(newTokenResponse.getRefreshToken())
                .isPresent()).isTrue();
    }
}