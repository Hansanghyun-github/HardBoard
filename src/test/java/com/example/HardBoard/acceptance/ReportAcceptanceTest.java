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
public class ReportAcceptanceTest {
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
    @DisplayName("게시글을 신고한다")
    void reportPost() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("게시글을 신고할 때는 올바른 postId를 입력해야 한다")
    void reportPostWithWrongPostId() throws Exception {
        // given

        // when

        // then
    }
    
    @Test
    @DisplayName("게시글을 신고할 때는 사유를 입력해야 한다")
    void reportPostWithWrongComments() throws Exception {
        // given
        
        // when
        
        // then
    }

    @Test
    @DisplayName("댓글을 신고한다")
    void reportComment() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("댓글을 신고할 때는 올바른 commentId를 입력해야 한다")
    void reportCommentWithWrongCommentId() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("댓글을 신고할 때는 사유를 입력해야 한다")
    void reportCommentWithEmptyComments() throws Exception {
        // given

        // when

        // then
    }
    
    @Test
    @DisplayName("신고를 취소한다")
    void cancelReport() throws Exception {
        // given
        
        // when
        
        // then
    }

    @Test
    @DisplayName("신고를 취소할 때는 올바른 reportId를 입력해야 한다")
    void cancelReportWithWrongReportId() throws Exception {
        // given

        // when

        // then
    }
    
    @Test
    @DisplayName("자신의 신고만 취소할 수 있다")
    void cancelOtherReportInFail() throws Exception {
        // given
        
        // when
        
        // then
    }
}
