package com.example.HardBoard.acceptance.publicAPI;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.block.BlockRepository;
import com.example.HardBoard.domain.comment.CommentRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class PublicPostSearchAcceptanceTest {

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

    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRecommendRepository postRecommendRepository;
    @Autowired
    PostUnrecommendRepository postUnrecommendRepository;

    User user;
    Long userId;
    String accessToken;

    //@BeforeEach
    void setAccessToken(){
        user = userRepository.save(
                User.builder()
                        .email("han41562@email")
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
    @DisplayName("검색어를 1개 이상 입력해야 한다")
    void searchPostsWithZeroKeywords() throws Exception {
        // when // then
        mockMvc.perform(get("/public/posts/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Required request parameter 'keywords' for method parameter type List is not present"));
    }
    
    @Test
    @DisplayName("검색어에 공백이나 null이 없어야 한다")
    void searchPostsWithNonChracterKeywords() throws Exception {
        // when // then
        mockMvc.perform(get("/public/posts/search?category=wrong&keywords= ,k2&sortCriteria=sort&searchCriteria=search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("keywords must contain characters"));
    }

    @Test
    @DisplayName("올바른 카테고리를 입력해야 한다")
    void searchPostsWithWrongCategory() throws Exception {
        // when // then
        mockMvc.perform(get("/public/posts/search?category=wrong&keywords=k1,k2&sortCriteria=sort&searchCriteria=search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Category is wrong"));
    }

    @Test
    @DisplayName("올바른 정렬기준을 입력해야 한다")
    void searchPostsWithWrongSortBase() throws Exception {
        mockMvc.perform(get("/public/posts/search?category=All&keywords=k1,k2&sortCriteria=wrong&searchCriteria=Title"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("SortCriteria is wrong"));
    }
    
    @Test
    @DisplayName("올바른 검색기준을 입력해야 한다")
    void searchPostsWithWrongSearchCriteria() throws Exception {
        mockMvc.perform(get("/public/posts/search?category=All&keywords=k1,k2&sortCriteria=recent&searchCriteria=wrong"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("SearchCriteria is wrong"));
    }
    
    @Test
    @DisplayName("올바른 페이지를 입력해야 한다")
    void searchPostsWithWrongPage() throws Exception {
        mockMvc.perform(get("/public/posts/search?category=All&keywords=k1,k2&sortCriteria=recent&searchCriteria=Title&page=-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("page has to be greater than zero"));
    }

    @Test
    @DisplayName("특정 카테고리로 검색")
    void searchPostsWithSpecificCategory() throws Exception {
        // given
        
        // when
        
        // then
    }
}
