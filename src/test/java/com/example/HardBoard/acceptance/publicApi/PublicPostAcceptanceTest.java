package com.example.HardBoard.acceptance.publicApi;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.service.post.response.PostResponse;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.block.Block;
import com.example.HardBoard.domain.post.Category;
import com.example.HardBoard.domain.post.PostRecommendRepository;
import com.example.HardBoard.domain.post.PostRepository;
import com.example.HardBoard.domain.post.PostUnrecommendRepository;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class PublicPostAcceptanceTest {

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

    @BeforeEach // TODO BeforeAll로 바꿔서 최적화
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
    @DisplayName("postId에 해당하는 post와 commentList를 조회할 때, 댓글이 20개 미만이라면, 20개 미만의 댓글을 조회한다")
    void getPostAndCommentListOfPostIdWhenCommentIsLessThan20() throws Exception {
        // given

        // TODO 조회 수 1 증가 했는지

        // when

        // then
    }
    @Test
    @DisplayName("postId에 해당하는 post와 commentList를 받는다")
    void getPostAndCommentListOfPostId() throws Exception {
        // given

        // TODO 조회 수 1 증가 했는지

        // when

        // then
    }

    @Test
    @DisplayName("올바른 postId를 입력해야 post와 commentList를 받을 수 있따")
    void getPostAndCommentListOfWorngPostId() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("로그인을 했다면 차단한 유저의 comment를 제외한 post와 commentList가 반환되야 한다")
    void getPostAndCommentListOfPostIdAndAuthenticatedUser() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("자기 자신의 글을 조회한다면 조회 수가 증가하지 않는다")
    void getMyPostAndCommentList() throws Exception {
        // given

        // when

        // then
    }
    
    @Test
    @DisplayName("30분 내에 한번 더 글을 조회한다면 조회 수가 증가하지 않는다")
    void getPostAndCommentTwice() throws Exception {
        // given
        // TODO 조회 수 방지 시간을 몇 분(초)로 설정할까?
        
        // when
        
        // then
    }
    
    @Test
    @DisplayName("postList를 조회한다")
    void getPostList() throws Exception {
        // given
        
        // when
        String content = mockMvc.perform(get("/public/posts"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<PostResponse> collect = (List<PostResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), PostResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        
        // then
        assertThat(collect.size()).isEqualTo(20);

        for(int i=0;i<collect.size()-1;i++){
            assertThat(collect.get(i).getCreatedDateTime().compareTo(collect.get(i+1).getCreatedDateTime())).isNotNegative();
        }
    }
    
    @Test
    @DisplayName("특정 카테고리의 postList를 조회한다")
    void getPostListWithCategory() throws Exception {
        // given

        // when
        String content = mockMvc.perform(get("/public/posts?category=Chat"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<PostResponse> collect = (List<PostResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), PostResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // then
        collect.stream().forEach(pR -> {
            assertThat(pR.getCategory()).isEqualTo(Category.Chat);
        });

        for(int i=0;i<collect.size()-1;i++){
            assertThat(collect.get(i).getCreatedDateTime().compareTo(collect.get(i+1).getCreatedDateTime())).isNotNegative();
        }
    }
    
    @Test
    @DisplayName("postList를 조회할 때, category가 Hot이라면, 다른 쿼리가 나가야 한다")
    void getPostListWithHotCategory() throws Exception {
        // given

        // when
        String content = mockMvc.perform(get("/public/posts?category=Hot"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<PostResponse> collect = (List<PostResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), PostResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // then
        collect.stream().forEach(pR -> {
            assertThat(pR.getCategory()).isEqualTo(Category.Hot); // TODO Hot ?
        });

        for(int i=0;i<collect.size()-1;i++){
            assertThat(collect.get(i).getRecommends().compareTo(collect.get(i+1).getRecommends())).isNotNegative();
        }
    }

    @Test
    @DisplayName("postList를 조회할 때 RequestParameter로 올바른 category를 입력해야 한다")
    void getPostListWithWrongCategory() throws Exception {
        // when // then
        mockMvc.perform(get("/public/posts?category=wrong"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Category is wrong"));
    }
    
    @Test
    @DisplayName("postList를 조회할 때, post가 PAGE_SIZE(20)개 미만이라면, 20개보다 적게 반환된다")
    void getPostListLessThan20() throws Exception {
        // given

        // when
        String content = mockMvc.perform(get("/public/posts"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<PostResponse> collect = (List<PostResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), PostResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // then
        assertThat(collect.size()).isLessThan(20).isEqualTo(15);

        collect.stream().forEach(pR -> {
            assertThat(pR.getCategory()).isEqualTo(Category.All);
        });
    }

    @Test
    @DisplayName("postList를 조회할 때 올바른 페이지를 입력해야 한다")
    void getPostListWithWrongPage() throws Exception {
        // when // then
        mockMvc.perform(get("/public/posts?page=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("page has to be greater than zero"));
    }

    @Test
    @DisplayName("postList를 조회할 때, 로그인한 상태라면, 차단한 유저의 글은 보이지 않아야 한다")
    void getPostListWithoutPostOfBlockedUserIfLogin() throws Exception {
        // given

        // when
        String content = mockMvc.perform(get("/public/posts")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<PostResponse> collect = (List<PostResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), PostResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        List<Block> blockList = user.getBlockList();
        // TODO collection's LAZY loading ?
        // TODO when LAZY loading is executed ?

        // then
        collect.stream().forEach(pR -> {
            assertThat(pR.getCategory()).isEqualTo(Category.All);
            assertThat(pR.getUserId()).isNotIn(blockList.toArray());
        });

        for(int i=0;i<collect.size()-1;i++){
            assertThat(collect.get(i).getCreatedDateTime().compareTo(collect.get(i+1).getCreatedDateTime())).isNotNegative();
        }
    }

    @Test
    @DisplayName("userId를 이용해 해당 유저의 PostList를 조회한다")
    void getPostListWithUserId() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("userId를 이용해 해당 유저의 PostList를 조회할 때, 올바른 userId를 입력해야 한다")
    void getPostListWithWrongUserId() throws Exception {
        // given

        // when

        // then
    }
    
    @Test
    @DisplayName("userId를 이용해 해당 유저의 PostList를 조회할 때, 올바른 page를 입력해야 한다")
    void getPostListWithUserIdAndWrongPage() throws Exception {
        // given
        
        // when
        
        // then
    }

    @Test
    @DisplayName("userId를 이용해 해당 유저의 PostList를 조회할 때, 글이 20개 미만이라면, 20개 미만의 글이 조회된다")
    void getPostListWithUserIdWhenPostIsLessThan20() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("오늘의 베스트 글을 조회한다")
    void getDayBestRecommendedPostList() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("오늘의 베스트 글을 조회할 때, 로그인을 했다면, 차단한 유저의 글은 제외한다")
    void getDayBestRecommendedPostListWithoutPostOfBlockUserIfLogin() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("주간 베스트 글을 조회한다")
    void getWeekBestRecommendedPostList() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("주간 베스트 글을 조회할 때, 로그인을 했다면, 차단한 유저의 글은 제외한다")
    void getWeekBestRecommendedPostListWithoutPostOfBlockUserIfLogin() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("월간 베스트 글을 조회한다")
    void getMonthBestRecommendedPostList() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("월간 베스트 글을 조회할 때, 로그인을 했다면, 차단한 유저의 글은 제외한다")
    void getMonthBestRecommendedPostListWithoutPostOfBlockUserIfLogin() throws Exception {
        // given

        // when

        // then
    }
}