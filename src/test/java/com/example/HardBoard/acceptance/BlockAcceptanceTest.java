package com.example.HardBoard.acceptance;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.block.request.BlockRequest;
import com.example.HardBoard.api.service.block.response.BlockResponse;
import com.example.HardBoard.api.service.notice.response.NoticeResponse;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.block.Block;
import com.example.HardBoard.domain.block.BlockRepository;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class BlockAcceptanceTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    BlockRepository blockRepository;

    User user;
    Long userId;
    String accessToken;

    @BeforeEach
    void setAccessToken(){
        user = userRepository.save(
                User.builder()
                        .email("email@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("husi")
                        .build());
        userId = user.getId();
        accessToken = JwtProperties.TOKEN_PREFIX +
                JWT.create()
                        .withSubject(user.getNickname())
                        .withExpiresAt(new Date(
                                System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                        .withClaim("email", user.getEmail())
                        .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }

    @Test
    @DisplayName("유저를 차단한다")
    void blockUser() throws Exception {
        // given
        User blockUser = userRepository.save(
                User.builder()
                        .email("block1email@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("husiblock1")
                        .build());

        BlockRequest request = BlockRequest.builder()
                .comments("comments")
                .build();

        // when
        String content = mockMvc.perform(post("/blocks/" + blockUser.getId())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        BlockResponse blockResponse = objectMapper.readValue(objectMapper
                .writeValueAsString(apiResponse.getData()), BlockResponse.class);

        // then
        assertThat(blockResponse).isNotNull()
                .satisfies(bR -> {
                    assertThat(bR.getUserId()).isEqualTo(userId);
                    assertThat(bR.getBlockUserId()).isEqualTo(blockUser.getId());
                });
    }

    @Test
    @DisplayName("유저를 차단할 떄는 올바른 userId를 입력해야 한다")
    void blockUserWithWrongUserId() throws Exception {
        // given
        BlockRequest request = BlockRequest.builder()
                .comments("comments")
                .build();

        // when // then
        mockMvc.perform(post("/blocks/" + 1L) // 없는 userId
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid userId"));
    }

    @Test
    @DisplayName("유저 차단을 취소한다")
    void cancelBlockUser() throws Exception {
        // given
        User blockUser = userRepository.save(
                User.builder()
                        .email("block1email@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("husiblock1")
                        .build());

        Long blockId = blockRepository.save(
                Block.builder()
                        .user(user)
                        .blockUser(blockUser)
                        .comments("comments1")
                        .build()
        ).getId();

        // when
        String content = mockMvc.perform(delete("/blocks/" + blockUser.getId())
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        BlockResponse blockResponse = objectMapper.readValue(objectMapper
                .writeValueAsString(apiResponse.getData()), BlockResponse.class);

        // then
        assertThat(blockRepository.findById(blockId).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("유저 차단을 취소할 때는 올바른 userId를 입력해야 한다")
    void cancelBlockUserWithWrongUserId() throws Exception {
        // given
        User blockUser = userRepository.save(
                User.builder()
                        .email("block1email@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("husiblock1")
                        .build());

        Long blockId = blockRepository.save(
                Block.builder()
                        .user(user)
                        .blockUser(blockUser)
                        .comments("comments1")
                        .build()
        ).getId();

        // when // then
        mockMvc.perform(delete("/blocks/" + blockUser.getId())
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid userId"));

        assertThat(blockRepository.findById(blockId).isEmpty()).isFalse();
    }

    @Test
    @DisplayName("자신이 차단한 유저만 차단을 취소할 수 있다")
    void cancelBlockUserWithOtherUserId() throws Exception {
        // given
        User blockUser = userRepository.save(
                User.builder()
                        .email("block1email@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("husiblock1")
                        .build());

        User anotherUser = userRepository.save(
                User.builder()
                        .email("Anotheremail@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("husiAnother")
                        .build());

        Long blockId = blockRepository.save(
                Block.builder()
                        .user(anotherUser)
                        .blockUser(blockUser)
                        .comments("comments1")
                        .build()
        ).getId();

        // when // then
        mockMvc.perform(delete("/blocks/" + blockUser.getId())
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can't delete other block user"));

        assertThat(blockRepository.findById(blockId).isEmpty()).isFalse();
    }

    @Test
    @Disabled
    @DisplayName("차단한 유저 리스트를 조회한다")
    void getBlockUserList() throws Exception {
        // given

        // when

        // then
    }
}
