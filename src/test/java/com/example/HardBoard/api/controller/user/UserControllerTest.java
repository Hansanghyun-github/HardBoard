package com.example.HardBoard.api.controller.user;

import com.example.HardBoard.api.controller.user.request.UserChangeNicknameRequest;
import com.example.HardBoard.api.controller.user.request.UserChangePasswordRequest;
import com.example.HardBoard.api.service.auth.AuthValidationService;
import com.example.HardBoard.api.service.user.UserService;
import com.example.HardBoard.config.TestSecurityConfig;
import com.example.HardBoard.domain.user.UserConverter;
import com.example.HardBoard.domain.user.UserRepository;
import com.example.HardBoard.domain.user.request.UserCreateDomainRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
@MockBean(JpaMetamodelMappingContext.class)
class UserControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean UserService userService;
    @MockBean AuthValidationService authValidationService;

    @Test
    @DisplayName("유저id를 이용해 유저를 찾는다")
    void findUserById() throws Exception {
        // given
        Long userId = 1L;

        // when // then
        mockMvc.perform(get("/users/" + userId))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    @DisplayName("닉네임을 변경한다")
    void changeNickname() throws Exception {
        // given
        Long userId = 1L;
        UserChangeNicknameRequest request = UserChangeNicknameRequest.builder()
                .newNickname("new")
                .build();

        // when // then
        mockMvc.perform(patch("/users/"+userId+"/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    @DisplayName("파라미터 한 개라도 비어있다면 비밀번호를 변경에 실패한다")
    void failChangeNicknameFromEmptyNickname() throws Exception {
        // given
        Long userId = 1L;
        UserChangeNicknameRequest request = UserChangeNicknameRequest.builder()
                .newNickname(" ")
                .build();

        // when // then
        mockMvc.perform(patch("/users/"+userId+"/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }
    
    @Test
    @DisplayName("비밀번호를 변경한다")
    void changePassword() throws Exception {
        // given
        Long userId = 1L;
        UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                .prevPassword("prev")
                .newPassword("new")
                .build();

        // when // then
        mockMvc.perform(patch("/users/"+userId+"/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"));
    }
    
    @ParameterizedTest
    @CsvSource(value = {"prev,", ",new"})
    @DisplayName("파라미터 한 개라도 비어있다면 비밀번호를 변경에 실패한다")
    void failChangePasswordFromEmptyPassword(String prevPassword, String newPassword) throws Exception {
        // given
        Long userId = 1L;
        UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                .prevPassword(prevPassword)
                .newPassword(newPassword)
                .build();
        
        // when // then
        mockMvc.perform(patch("/users/"+userId+"/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("회원 탈퇴")
    void deleteUser() throws Exception {
        // given
        Long userId = 1L;

        // when // then
        mockMvc.perform(delete("/users/"+userId))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"));
    }
}