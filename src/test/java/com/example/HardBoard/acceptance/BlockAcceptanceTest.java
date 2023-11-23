package com.example.HardBoard.acceptance;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

        // when

        // then
    }

    @Test
    @DisplayName("유저를 차단할 떄는 올바른 userId를 입력해야 한다")
    void blockUserWithWrongUserId() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("유저 차단을 취소한다")
    void cancelBlockUser() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("유저 차단을 취소할 때는 올바른 userId를 입력해야 한다")
    void cancelBlockUserWithWrongUserId() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("자신이 차단한 유저만 차단을 취소할 수 있다")
    void cancelBlockUserWithOtherUserId() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("차단한 유저 리스트를 조회한다")
    void getBlockUserList() throws Exception {
        // given

        // when

        // then
    }
}
