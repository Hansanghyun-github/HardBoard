package com.example.HardBoard.api.controller.auth;

import com.example.HardBoard.api.controller.auth.request.*;
import com.example.HardBoard.api.service.auth.AuthService;
import com.example.HardBoard.api.service.auth.MailService;
import com.example.HardBoard.api.service.auth.request.MailCheckServiceRequest;
import com.example.HardBoard.api.service.token.TokenService;
import com.example.HardBoard.api.service.user.UserService;
import com.example.HardBoard.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        when(mailService.isCorrectNumber(any(MailCheckServiceRequest.class)))
                .thenReturn(true);

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
    @DisplayName("회원가입을 위한 이메일에 인증번호를 보낸다")
    void sendNumberToEmailCheckForJoin() throws Exception {
        // given
        AuthEmailSendRequest request = AuthEmailSendRequest.builder()
                .email("email@email")
                .build();

        when(userService.existsByEmail(any())).thenReturn(false);
        // when // then
        mockMvc.perform(
                        post("/auth/email/send")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    @DisplayName("비밀번호 변경을 위한 이메일에 인증번호를 보낸다")
    void sendNumberToEmailCheckForChangePassword() throws Exception {
        // given
        AuthEmailSendRequest request = AuthEmailSendRequest.builder()
                .email("email@email")
                .build();

        when(userService.existsByEmail(any())).thenReturn(true);
        // when // then
        mockMvc.perform(
                        post("/auth/users/email/send")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "email"}, nullValues = {"null"})
    @DisplayName("올바르지 않은 이메일을 입력하면 sendNumberForJoin 실패한다")
    void failSendNumberForJoinWhenEnterWrongEmail(String email) throws Exception {
        // given
        AuthEmailSendRequest request = AuthEmailSendRequest.builder()
                .email(email)
                .build();

        // when // then
        mockMvc.perform(
                        post("/auth/email/send")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "email"}, nullValues = {"null"})
    @DisplayName("올바르지 않은 이메일을 입력하면 sendNumberForPassword 실패한다")
    void failSendNumberForPasswordWhenEnterWrongEmail(String email) throws Exception {
        // given
        AuthEmailSendRequest request = AuthEmailSendRequest.builder()
                .email(email)
                .build();

        // when // then
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

    @Test
    @DisplayName("인증없이 유저의 비밀번호를 변경한다")
    void changePasswordWithoutAuthentication() throws Exception {
        // given
        AuthChangePasswordRequest request = AuthChangePasswordRequest.builder()
                .email("email@email")
                .prevPassword("prev")
                .newPassword("new")
                .authNumber("283")
                .build();

        when(mailService.isCorrectNumber(any(MailCheckServiceRequest.class)))
                .thenReturn(true);

        // when // then
        mockMvc.perform(
                post("/auth/password/change")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));
    }
}