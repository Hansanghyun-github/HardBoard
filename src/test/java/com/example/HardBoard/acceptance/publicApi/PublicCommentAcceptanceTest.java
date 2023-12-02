package com.example.HardBoard.acceptance.publicApi;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.post.PostRecommendRepository;
import com.example.HardBoard.domain.post.PostRepository;
import com.example.HardBoard.domain.post.PostUnrecommendRepository;
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
public class PublicCommentAcceptanceTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PostRepository postRepository;
    @Autowired
    PostRecommendRepository postRecommendRepository;
    @Autowired
    PostUnrecommendRepository postUnrecommendRepository;

    User user;
    Long userId;
    String accessToken;

    @BeforeEach
        // TODO BeforeAll로 바꿔서 최적화
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
    @DisplayName("postId의 commentList를 조회한다")
    void getCommentListWithPostId() throws Exception {
        // given
        
        // when
        
        // then
    }

    @Test
    @DisplayName("postId의 commentList를 조회할 때, 로그인 했다면, 차단한 유저의 comment 제외해야 한다")
    void getCommentListWithPostIdWithoutCommentOfBlockUserIfLogin() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("postId의 commentList를 조회할 때 올바른 postId를 입력해야 한다")
    void getCommentListWithWrongPostId() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("postId의 commentList를 조회할 대 올바른 page를 입력해야 한다")
    void getCommentListWithPostIdAndWrongPage() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("postId의 commentList를 조회할 때 마지막 페이지 or 댓글이 20개 미만이라면, 20개 미만의 댓글이 조회된다")
    void getCommentListWithPostIdWhenLastPageOrCommentIsLessThan20() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("userId의 commentList를 조회한다")
    void getCommentListOfUserId() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("userId의 commentList를 조회할 때 올바른 userId를 입력해야 한다")
    void getCommentListOfWrongUserId() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("userId의 commentList를 조회할 때 올바른 page를 입력해야 한다")
    void getCommentListOfUserIdAndWrongPage() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("userId의 commentList를 조회할 때 마지막 페이지 or 댓글이 20개 미만이라면, 20개 미만의 댓글이 조회된다")
    void getCommentListOfUserIdAndWrongPageWhenLastPageOrCommentIsLessThan20() throws Exception {
        // given

        // when

        // then
    }
}
