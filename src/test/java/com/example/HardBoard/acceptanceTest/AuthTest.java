package com.example.HardBoard.acceptanceTest;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.auth.request.AuthEmailSendRequest;
import com.example.HardBoard.api.controller.auth.request.AuthJoinRequest;
import com.example.HardBoard.api.controller.auth.request.AuthLoginRequest;
import com.example.HardBoard.api.service.auth.MailService;
import com.example.HardBoard.api.service.auth.response.TokenResponse;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class AuthTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean MailService mailService;

    @Test
    @DisplayName("Join부터 Logout까지 테스트")
    void joinToLogout() throws Exception {
        AuthJoinRequest joinRequest = AuthJoinRequest.builder()
                .email("email@email")
                .password("password")
                .nickname("husi")
                .authNumber("283")
                .build();

        AuthLoginRequest loginRequest = AuthLoginRequest.builder()
                .email("email@email")
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
        TokenResponse tokenResponse = objectMapper.readValue(objectMapper.writeValueAsString(data), TokenResponse.class);


        mockMvc.perform(
                post("/users/logout")
                        .header(JwtProperties.HEADER_STRING, tokenResponse.getAccessToken())
        ).andExpect(status().isOk());
    }
}
