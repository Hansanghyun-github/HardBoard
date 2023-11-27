package com.example.HardBoard.acceptance;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.user.request.UserChangeNicknameRequest;
import com.example.HardBoard.api.controller.user.request.UserChangePasswordRequest;
import com.example.HardBoard.api.service.user.response.UserResponse;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class UserAcceptanceTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Autowired UserRepository userRepository;

    @Autowired PasswordEncoder passwordEncoder;

    User user;
    String accessToken;

    @BeforeEach
    void setAccessToken(){
        user = userRepository.save(
                User.builder()
                        .email("email@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("husi")
                        .build());
        accessToken = JwtProperties.TOKEN_PREFIX +
                JWT.create()
                .withSubject(user.getNickname())
                .withExpiresAt(new Date(
                        System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("email", user.getEmail())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }

    @Test
    @DisplayName("유저id를 통해 객체를 찾는다")
    void findById() throws Exception {
        // given
        Long userId = user.getId();

        // when
        String content = mockMvc.perform(get("/users/" + userId)
                        .header(JwtProperties.HEADER_STRING, accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        UserResponse userResponse = objectMapper
                .readValue(objectMapper
                        .writeValueAsString(apiResponse.getData()), UserResponse.class);

        // then
        assertThat(userRepository.findByEmail(userResponse.getEmail())
                .orElseThrow().getNickname()).isEqualTo("husi");
    }
    
    @Test
    @DisplayName("닉네임을 변경한다")
    void changeNickname() throws Exception {
        // given
        Long userId = user.getId();
        String prevNickname = user.getNickname();
        String newNickname = "newNickname";
        UserChangeNicknameRequest request = UserChangeNicknameRequest.builder()
                .newNickname(newNickname)
                .build();

        // when // then
        mockMvc.perform(post("/users/" + userId + "/nickname")
                        .header(JwtProperties.HEADER_STRING, accessToken)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"));

        assertThat(userRepository.findById(userId)
                .orElseThrow().getNickname())
                .isNotEqualTo(prevNickname)
                .isEqualTo(newNickname);
    }

    @Test
    @DisplayName("중복된 닉네임으로 변경할 수 없다")
    void changeDuplicateNickname() throws Exception {
        // given
        Long userId = user.getId();
        String prevNickname = user.getNickname();
        UserChangeNicknameRequest request = UserChangeNicknameRequest.builder()
                .newNickname(prevNickname)
                .build();

        // when // then
        mockMvc.perform(post("/users/" + userId + "/nickname")
                        .header(JwtProperties.HEADER_STRING, accessToken)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("nickname is duplicated"));
    }

    @Test
    @DisplayName("비밀번호를 변경한다")
    void changePassword() throws Exception {
        // given
        Long userId = user.getId();
        String prevPassword = "password";
        String newPassword = "newPassword";
        UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                .prevPassword(prevPassword)
                .newPassword(newPassword)
                .build();

        // when // then
        mockMvc.perform(post("/users/" + userId + "/password")
                        .header(JwtProperties.HEADER_STRING, accessToken)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"));

        String password = userRepository.findById(userId).orElseThrow().getPassword();
        assertThat(passwordEncoder.matches(prevPassword, password)).isFalse();
        assertThat(passwordEncoder.matches(newPassword, password)).isTrue();
    }

    @Test
    @DisplayName("이전 비밀번호가 맞지 않는다면 비밀번호 변경에 실패한다")
    void prevPasswordIsWrongChangePasswordInFail() throws Exception {
        // given
        Long userId = user.getId();
        String prevPassword = "password";
        String newPassword = "newPassword";
        UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                .prevPassword(prevPassword + "wrong")
                .newPassword(newPassword)
                .build();

        // when // then
        mockMvc.perform(post("/users/" + userId + "/password")
                        .header(JwtProperties.HEADER_STRING, accessToken)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));

        String password = userRepository.findById(userId).orElseThrow().getPassword();
        assertThat(passwordEncoder.matches(prevPassword, password)).isTrue();
        assertThat(passwordEncoder.matches(newPassword, password)).isFalse();
    }

    @Test
    @DisplayName("유저를 삭제한다")
    void deleteUser() throws Exception {
        // given
        Long userId = user.getId();

        // when // then
        mockMvc.perform(delete("/users/" + userId)
                        .header(JwtProperties.HEADER_STRING, accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"));

        assertThatThrownBy(() -> userRepository.findById(userId).orElseThrow())
                .isInstanceOf(NoSuchElementException.class);
    }
}
