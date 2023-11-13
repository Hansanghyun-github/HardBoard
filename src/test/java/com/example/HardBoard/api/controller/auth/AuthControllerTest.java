package com.example.HardBoard.api.controller.auth;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.auth.request.AuthEmailSendRequest;
import com.example.HardBoard.api.controller.auth.request.AuthJoinRequest;
import com.example.HardBoard.api.controller.auth.request.AuthLoginRequest;
import com.example.HardBoard.api.controller.auth.request.AuthRemadeTokenRequest;
import com.example.HardBoard.api.service.auth.AuthService;
import com.example.HardBoard.api.service.auth.MailService;
import com.example.HardBoard.api.service.auth.response.TokenResponse;
import com.example.HardBoard.api.service.token.TokenService;
import com.example.HardBoard.api.service.user.UserService;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = {AuthController.class})
@Import(TestSecurityConfig.class)
@WithMockUser
class AuthControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean AuthService authService;
    @MockBean TokenService tokenService;
    @MockBean UserService userService;
    @MockBean MailService mailService;

    @Test
    @DisplayName("로그인한다")
    void login() throws Exception {
        // given
        String email = "gks@gks";
        String password = "password1";

        AuthLoginRequest request = AuthLoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        // when // then
        mockMvc.perform(
                post("/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @ParameterizedTest
    @CsvSource(value = {"email1@gks,", ",password1", "email,password2"})
    @DisplayName("이메일이나 패스워드가 비어있거나 이메일 형식이 아니라면, 로그인 실패한다")
    void failLoginWhenEnterEmptyEmailOrEmptyPasswordOrNotEmail(String email, String password) throws Exception {
        // given
        AuthLoginRequest request = AuthLoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        // when // then
        mockMvc.perform(
                        post("/auth/login")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("회원가입한다")
    void join() throws Exception {
        // given
        AuthJoinRequest request = AuthJoinRequest.builder()
                .email("gkd@gks")
                .password("pass")
                .nickname("nick")
                .authNumber("1234")
                .build();

        // when // then
        mockMvc.perform(
                        post("/auth/join")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"));
    }

    // join validation 스킵
    @ParameterizedTest
    @CsvSource(value = {",password,nickname,authNum",
            "email1@gks,,nickname,authNum",
            "email1@gks,password,,authNum",
            "email1@gks,password,nickname,",
            "email1,password,nickname,authNum"
    })
    @DisplayName("파라미터가 비어있거나 email이 이메일 형식이 아니라면 회원가입 실패한다")
    void failJoinWhenBlankParameterOrWrongEmail(String email, String password, String nickname, String authNum) throws Exception {
        // given
        AuthJoinRequest request = AuthJoinRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .authNumber(authNum)
                .build();

        // when // then
        mockMvc.perform(
                        post("/auth/join")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("이메일에 인증번호를 보낸다")
    void sendNumberToEmailCheck() throws Exception {
        // given
        AuthEmailSendRequest request = new AuthEmailSendRequest("email@email");

        // when // then
        mockMvc.perform(
                        post("/auth/email/send")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"));
        mockMvc.perform(
                        post("/auth/users/email/send")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"));
    }
    @ParameterizedTest
    @CsvSource(value = {"", "email"})
    @DisplayName("올바르지 않은 이메일을 입력하면 sendNumber 실패한다")
    void failSendNumberWhenEnterWrongEmail(String email) throws Exception {
        // given
        AuthEmailSendRequest request = new AuthEmailSendRequest(email);

        // when // then
        mockMvc.perform(
                        post("/auth/email/send")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
        mockMvc.perform(
                        post("/auth/users/email/send")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("리프레시 토큰을 다시 만든다")
    @WithMockUser
    void remadeRefreshToken() throws Exception {
        // given
        AuthRemadeTokenRequest request = AuthRemadeTokenRequest.builder()
                .refreshToken("refreshToken")
                .build();

        // when // then
        mockMvc.perform(
                post("/users/refreshToken")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    @DisplayName("리프레시 토큰이 비어있으면 remade 실패한다")
    @WithMockUser
    void failRemadeRefreshTokenWithEmptyRefreshToken() throws Exception {
        // given
        AuthRemadeTokenRequest request = AuthRemadeTokenRequest.builder()
                .build();

        // when // then
        mockMvc.perform(
                        post("/users/refreshToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }
}