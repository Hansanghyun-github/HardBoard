package com.example.HardBoard.acceptance.publicApi;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.service.comment.response.CommentResponse;
import com.example.HardBoard.api.service.post.response.PostCommentResponse;
import com.example.HardBoard.api.service.post.response.PostResponse;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.block.Block;
import com.example.HardBoard.domain.block.BlockRepository;
import com.example.HardBoard.domain.comment.Comment;
import com.example.HardBoard.domain.comment.CommentRepository;
import com.example.HardBoard.domain.post.*;
import com.example.HardBoard.domain.user.Role;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @BeforeEach // TODO BeforeAll로 바꿔서 최적화
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

    // TODO @BeforeAll로 미리 100개 정도의 post와 comment를 만들어 놓고, 그걸로 테스트 하자

    @Test
    @DisplayName("postId에 해당하는 post와 commentList를 받는다")
    void getPostAndCommentListOfPostId() throws Exception {
        // given
        Post post = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .category(Category.Chat)
                        .user(user)
                        .build()
        );

        for(int i=0;i<30;i++){
            User saved = userRepository.save(
                    User.builder()
                            .email("email@email" + i)
                            .password(passwordEncoder.encode("password"))
                            .nickname("nickname" + i)
                            .role(Role.ROLE_USER)
                            .build()
            );
            commentRepository.save(
                    Comment.builder()
                            .contents("contents" + i)
                            .post(post)
                            .user(saved)
                            .build()
            );
        }

        Long prevViews = post.getViews();

        // when
        String content = mockMvc.perform(get("/public/posts/" + post.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        PostCommentResponse postCommentResponse = objectMapper.readValue(objectMapper
                .writeValueAsString(apiResponse.getData()), PostCommentResponse.class);

        List<CommentResponse> commentList = postCommentResponse.getCommentList();

        // then
        assertThat(postRepository.findById(postCommentResponse.getPostId()).orElseThrow().getViews())
                .isEqualTo(postCommentResponse.getViews())
                .isEqualTo(prevViews + 1L);
        assertThat(postCommentResponse.getNickname()).isEqualTo(user.getNickname());
        assertThat(commentList.size()).isEqualTo(10);

        for(int i=0;i < commentList.size()-1;i++)
            assertThat(commentList.get(i).getCreatedDateTime().compareTo(commentList.get(i+1).getCreatedDateTime())).isNotPositive();
    }

    @Test
    @DisplayName("올바른 postId를 입력해야 post와 commentList를 받을 수 있다")
    void getPostAndCommentListOfWorngPostId() throws Exception {
        // when // then
        mockMvc.perform(get("/public/posts/" + 0L)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Invalid postId"));
    }

    @Test
    @DisplayName("로그인을 했다면 차단한 유저의 comment를 제외한 post와 commentList가 반환되야 한다")
    void getPostAndCommentListOfPostIdAndAuthenticatedUser() throws Exception {
        // given

        Post post = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .category(Category.Chat)
                        .user(user)
                        .build()
        );

        for(int i=0;i<10;i++){
            User saved = userRepository.save(
                    User.builder()
                            .email("email@email" + i)
                            .password(passwordEncoder.encode("password"))
                            .nickname("nickname" + i)
                            .role(Role.ROLE_USER)
                            .build()
            );
            commentRepository.save(
                    Comment.builder()
                            .contents("contents" + i)
                            .post(post)
                            .user(saved)
                            .build()
            );

            if(i < 5){
                blockRepository.save(
                        Block.builder()
                                .comments("comments" + i)
                                .user(user)
                                .blockUser(saved)
                                .build()
                );
            }
        }

        Long prevViews = post.getViews();

        // when
        String content = mockMvc.perform(get("/public/posts/" + post.getId())
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        PostCommentResponse postCommentResponse = objectMapper.readValue(objectMapper
                .writeValueAsString(apiResponse.getData()), PostCommentResponse.class);

        List<CommentResponse> commentList = postCommentResponse.getCommentList();

        // then
        assertThat(commentList.size()).isEqualTo(5);
        List<Block> blockList = blockRepository.findByUserId(user.getId());
        assertThat(commentList).isNotIn(blockList.toArray());
    }
    
    @Test
    @Disabled
    @DisplayName("30분 내에 한번 더 글을 조회한다면 조회 수가 증가하지 않는다")
    void getPostAndCommentTwice() throws Exception {
        // given
        // TODO 조회 수 방지 시간을 몇 분(초)로 설정할까?
        
        // when
        
        // then
    }
    
    // TODO 한 명의 유저가 하루에 쓸 수 있는 Post, Comment, Recommend 제한
    
    @Test
    @DisplayName("postList를 조회한다")
    void getPostList() throws Exception {
        // given
        for(int i=0;i<30;i++){
            User saved = userRepository.save(
                    User.builder()
                            .email("email@email" + i)
                            .nickname("nickname" + i)
                            .password(passwordEncoder.encode("password"))
                            .role(Role.ROLE_USER)
                            .build()
            );
            postRepository.save(
                    Post.builder()
                            .title("title" + i)
                            .contents("contents" + i)
                            .category(Category.Chat)
                            .user(saved)
                            .build()
            );
        }
        
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
        for(int i=0;i<30;i++){
            User saved = userRepository.save(
                    User.builder()
                            .email("email@email" + i)
                            .nickname("nickname" + i)
                            .password(passwordEncoder.encode("password"))
                            .role(Role.ROLE_USER)
                            .build()
            );
            postRepository.save(
                    Post.builder()
                            .title("title" + i)
                            .contents("contents" + i)
                            .category(Category.Chat)
                            .user(saved)
                            .build()
            );
        }

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
        assertThat(collect.size()).isEqualTo(20);

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
        for(int i=0;i<30;i++){
            User saved = userRepository.save(
                    User.builder()
                            .email("email@email" + i)
                            .nickname("nickname" + i)
                            .password(passwordEncoder.encode("password"))
                            .role(Role.ROLE_USER)
                            .build()
            );
            postRepository.save(
                    Post.builder()
                            .title("title" + i)
                            .contents("contents" + i)
                            .category(Category.Chat)
                            .user(saved)
                            .build()
            );
        }

        // TODO 랜덤 추천 메서드

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
        for(int i=0;i<15;i++){
            User saved = userRepository.save(
                    User.builder()
                            .email("email@email" + i)
                            .nickname("nickname" + i)
                            .password(passwordEncoder.encode("password"))
                            .role(Role.ROLE_USER)
                            .build()
            );
            postRepository.save(
                    Post.builder()
                            .title("title" + i)
                            .contents("contents" + i)
                            .category(Category.Chat)
                            .user(saved)
                            .build()
            );
        }

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
        for(int i=0;i<10;i++){
            User saved = userRepository.save(
                    User.builder()
                            .email("email@email" + i)
                            .nickname("nickname" + i)
                            .password(passwordEncoder.encode("password"))
                            .role(Role.ROLE_USER)
                            .build()
            );
            postRepository.save(
                    Post.builder()
                            .title("title" + i)
                            .contents("contents" + i)
                            .category(Category.Chat)
                            .user(saved)
                            .build()
            );

            if(i < 5){
                blockRepository.save(
                        Block.builder()
                                .comments("comments" + i)
                                .user(user)
                                .blockUser(saved)
                                .build()
                );
            }
        }

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

        List<Block> blockList = blockRepository.findByUserId(user.getId());
        // TODO collection's LAZY loading ?
        // TODO when LAZY loading is executed ?

        // then
        assertThat(collect.size()).isEqualTo(5);
        collect.stream().forEach(pR -> {
            assertThat(pR.getUserId()).isNotIn(blockList.toArray());
        });
    }

    @Test
    @DisplayName("userId를 이용해 해당 유저의 PostList를 조회한다")
    void getPostListWithUserId() throws Exception {
        // given
        User anotherUser = userRepository.save(
                User.builder()
                        .email("email@email")
                        .nickname("nickname")
                        .password(passwordEncoder.encode("password"))
                        .role(Role.ROLE_USER)
                        .build()
        );
        for(int i=0;i<30;i++){
            postRepository.save(
                    Post.builder()
                            .title("title" + i)
                            .contents("contents" + i)
                            .category(Category.Chat)
                            .user(anotherUser)
                            .build()
            );
        }

        // when
        String content = mockMvc.perform(get("/public/posts/" + anotherUser.getId()))
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
        collect.stream().map(c -> c.getUserId()).collect(Collectors.toList())
                .forEach(id -> {
                    assertThat(id).isEqualTo(anotherUser.getId());
                });
        for(int i=0;i<collect.size()-1;i++)
            assertThat(collect.get(i).getCreatedDateTime().compareTo(collect.get(i+1).getCreatedDateTime())).isNotNegative();
    }

    @Test
    @DisplayName("userId를 이용해 해당 유저의 PostList를 조회할 때, 올바른 userId를 입력해야 한다")
    void getPostListWithWrongUserId() throws Exception {
        // when // then
        mockMvc.perform(get("/public/posts/" + 0L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Invalid userId"));
    }
    
    @Test
    @DisplayName("userId를 이용해 해당 유저의 PostList를 조회할 때, 올바른 page를 입력해야 한다")
    void getPostListWithUserIdAndWrongPage() throws Exception {
        // when // then
        mockMvc.perform(get("/public/posts/" + 0L + "?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("page has to be greater than zero"));
    }

    @Test
    @DisplayName("userId를 이용해 해당 유저의 PostList를 조회할 때, 글이 20개 미만이라면, 20개 미만의 글이 조회된다")
    void getPostListWithUserIdWhenPostIsLessThan20() throws Exception {
        // given
        User anotherUser = userRepository.save(
                User.builder()
                        .email("email@email")
                        .nickname("nickname")
                        .password(passwordEncoder.encode("password"))
                        .role(Role.ROLE_USER)
                        .build()
        );
        for(int i=0;i<15;i++){
            postRepository.save(
                    Post.builder()
                            .title("title" + i)
                            .contents("contents" + i)
                            .category(Category.Chat)
                            .user(anotherUser)
                            .build()
            );
        }

        // when
        String content = mockMvc.perform(get("/public/posts/" + anotherUser.getId()))
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
        assertThat(collect.size()).isEqualTo(15);
    }

    @Test
    @DisplayName("오늘의 베스트 글을 조회한다")
    void getDayBestRecommendedPostList() throws Exception {
        // given
        LocalDateTime midnightToday = LocalDateTime.of(
                LocalDate.now(), LocalTime.MIDNIGHT);
        LocalDateTime midnightYesterday = midnightToday.minusDays(1L);
        LocalDateTime beforeMidnightYesterday = midnightYesterday.minusNanos(1L);
        LocalDateTime beforeMidnightToday = midnightToday.minusNanos(1L);

        List<User> recommendUsers = new ArrayList<>();
        for(int i=0;i<20;i++){
            recommendUsers.add(userRepository.save(
                    User.builder()
                            .email("email@email" + i)
                            .password(passwordEncoder.encode("password"))
                            .nickname("nickname" + i)
                            .role(Role.ROLE_USER)
                            .build()
            ));
        }

        Post postNotIncluded = postRepository.save(
                Post.builder()
                        .title("prevTitle")
                        .contents("prevContents")
                        .category(Category.Chat)
                        .user(user)
                        .build()
        );
        postNotIncluded.changeCreatedDateTimeforTest(beforeMidnightYesterday);

        for(int j=0;j<20;j++){
            postRecommendRepository.save(
                    PostRecommend.builder()
                            .post(postNotIncluded)
                            .user(recommendUsers.get(j))
                            .build()
            );
        }

        assertThat(postRecommendRepository.countByPostId(postNotIncluded.getId())).isEqualTo(20L);

        postNotIncluded = postRepository.save(
                Post.builder()
                        .title("todayTitle")
                        .contents("todayContents")
                        .category(Category.Chat)
                        .user(user)
                        .build()
        );
        postNotIncluded.changeCreatedDateTimeforTest(midnightToday);

        for(int j=0;j<20;j++){
            postRecommendRepository.save(
                    PostRecommend.builder()
                            .post(postNotIncluded)
                            .user(recommendUsers.get(j))
                            .build()
            );
        }

        assertThat(postRecommendRepository.countByPostId(postNotIncluded.getId())).isEqualTo(20L);

        for(int i=0;i<21;i++){
            Post post = postRepository.save(
                    Post.builder()
                            .title("yesterdayTitle" + i)
                            .contents("yesterdayContents" + i)
                            .category(Category.Chat)
                            .user(user)
                            .build()
            );

            if(i < 11) post.changeCreatedDateTimeforTest(midnightYesterday);
            else post.changeCreatedDateTimeforTest(beforeMidnightToday);

            for(int j=0;j<i-1;j++){
                postRecommendRepository.save(
                        PostRecommend.builder()
                                .user(recommendUsers.get(j))
                                .post(post)
                                .build()
                );
            }
        }

        assertThat(postRepository.findAll().size()).isEqualTo(23);

        // when
        String content = mockMvc.perform(get("/public/posts/day"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<PostResponse> bestPostList = (List<PostResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), PostResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // then
        assertThat(bestPostList.size()).isEqualTo(20);
        for(int i=0;i<bestPostList.size()-1;i++){
            assertThat(bestPostList.get(i).getRecommends()
                    .compareTo(bestPostList.get(i+1).getRecommends()))
                    .isNotNegative();
        }
        bestPostList.stream().map(PostResponse::getCreatedDateTime)
                .forEach(t -> assertThat(t).isBetween(midnightYesterday, beforeMidnightToday));
    }

    @Test
    @DisplayName("오늘의 베스트 글을 조회할 때, 로그인을 했다면, 차단한 유저의 글은 제외한다")
    void getDayBestRecommendedPostListWithoutPostOfBlockUserIfLogin() throws Exception {
        // given
        LocalDateTime midnightToday = LocalDateTime.of(
                LocalDate.now(), LocalTime.MIDNIGHT);
        LocalDateTime midnightYesterday = midnightToday.minusDays(1L);
        LocalDateTime beforeMidnightYesterday = midnightYesterday.minusNanos(1L);
        LocalDateTime beforeMidnightToday = midnightToday.minusNanos(1L);

        List<User> recommendUsers = new ArrayList<>();
        for(int i=0;i<10;i++){
            recommendUsers.add(userRepository.save(
                    User.builder()
                            .email("email@email" + i)
                            .password(passwordEncoder.encode("password"))
                            .nickname("nickname" + i)
                            .role(Role.ROLE_USER)
                            .build()
            ));
        }

        for(int i=0;i<10;i++){
            Post post = postRepository.save(
                    Post.builder()
                            .title("yesterdayTitle" + i)
                            .contents("yesterdayContents" + i)
                            .category(Category.Chat)
                            .user(recommendUsers.get(i))
                            .build()
            );

            post.changeCreatedDateTimeforTest(beforeMidnightToday);

            for(int j=0;j<i-1;j++){
                postRecommendRepository.save(
                        PostRecommend.builder()
                                .user(recommendUsers.get(j))
                                .post(post)
                                .build()
                );
            }

            if(i < 5){
                blockRepository.save(
                        Block.builder()
                                .comments("comments")
                                .user(user)
                                .blockUser(recommendUsers.get(i))
                                .build()
                );
            }
        }

        assertThat(postRepository.findAll().size()).isEqualTo(10);

        // when
        String content = mockMvc.perform(get("/public/posts/day"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<PostResponse> bestPostList = (List<PostResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), PostResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // then
        assertThat(bestPostList.size()).isEqualTo(5);
        for(int i=0;i<bestPostList.size()-1;i++){
            assertThat(bestPostList.get(i).getRecommends()
                    .compareTo(bestPostList.get(i+1).getRecommends()))
                    .isNotNegative();
        }
        bestPostList.stream().map(PostResponse::getCreatedDateTime)
                .forEach(t -> assertThat(t).isBetween(midnightYesterday, beforeMidnightToday));

        assertThat(bestPostList.stream().map(PostResponse::getUserId).collect(Collectors.toList()))
                .isNotIn(blockRepository.findByUserId(userId).toArray());
    }

    @Test
    @DisplayName("주간 베스트 글을 조회한다")
    void getWeekBestRecommendedPostList() throws Exception {
        // given
        LocalDateTime midnightToday = LocalDateTime.of(
                LocalDate.now(), LocalTime.MIDNIGHT);
        LocalDateTime midnightWeekAgo = midnightToday.minusWeeks(1L);
        LocalDateTime beforeMidnightWeekAgo = midnightWeekAgo.minusNanos(1L);
        LocalDateTime beforeMidnightToday = midnightToday.minusNanos(1L);

        List<User> recommendUsers = new ArrayList<>();
        for(int i=0;i<20;i++){
            recommendUsers.add(userRepository.save(
                    User.builder()
                            .email("email@email" + i)
                            .password(passwordEncoder.encode("password"))
                            .nickname("nickname" + i)
                            .role(Role.ROLE_USER)
                            .build()
            ));
        }

        Post postNotIncluded = postRepository.save(
                Post.builder()
                        .title("prevTitle")
                        .contents("prevContents")
                        .category(Category.Chat)
                        .user(user)
                        .build()
        );
        postNotIncluded.changeCreatedDateTimeforTest(beforeMidnightWeekAgo);

        for(int j=0;j<20;j++){
            postRecommendRepository.save(
                    PostRecommend.builder()
                            .post(postNotIncluded)
                            .user(recommendUsers.get(j))
                            .build()
            );
        }

        assertThat(postRecommendRepository.countByPostId(postNotIncluded.getId())).isEqualTo(20L);

        postNotIncluded = postRepository.save(
                Post.builder()
                        .title("todayTitle")
                        .contents("todayContents")
                        .category(Category.Chat)
                        .user(user)
                        .build()
        );
        postNotIncluded.changeCreatedDateTimeforTest(midnightToday);

        for(int j=0;j<20;j++){
            postRecommendRepository.save(
                    PostRecommend.builder()
                            .post(postNotIncluded)
                            .user(recommendUsers.get(j))
                            .build()
            );
        }

        assertThat(postRecommendRepository.countByPostId(postNotIncluded.getId())).isEqualTo(20L);

        for(int i=0;i<21;i++){
            Post post = postRepository.save(
                    Post.builder()
                            .title("yesterdayTitle" + i)
                            .contents("yesterdayContents" + i)
                            .category(Category.Chat)
                            .user(user)
                            .build()
            );

            if(i < 11) post.changeCreatedDateTimeforTest(midnightWeekAgo);
            else post.changeCreatedDateTimeforTest(beforeMidnightToday);

            for(int j=0;j<i-1;j++){
                postRecommendRepository.save(
                        PostRecommend.builder()
                                .user(recommendUsers.get(j))
                                .post(post)
                                .build()
                );
            }
        }

        assertThat(postRepository.findAll().size()).isEqualTo(23);

        // when
        String content = mockMvc.perform(get("/public/posts/day"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<PostResponse> bestPostList = (List<PostResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), PostResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // then
        assertThat(bestPostList.size()).isEqualTo(20);
        for(int i=0;i<bestPostList.size()-1;i++){
            assertThat(bestPostList.get(i).getRecommends()
                    .compareTo(bestPostList.get(i+1).getRecommends()))
                    .isNotNegative();
        }
        bestPostList.stream().map(PostResponse::getCreatedDateTime)
                .forEach(t -> assertThat(t).isBetween(midnightWeekAgo, beforeMidnightToday));
    }

    @Test
    @DisplayName("주간 베스트 글을 조회할 때, 로그인을 했다면, 차단한 유저의 글은 제외한다")
    void getWeekBestRecommendedPostListWithoutPostOfBlockUserIfLogin() throws Exception {
        // given
        LocalDateTime midnightToday = LocalDateTime.of(
                LocalDate.now(), LocalTime.MIDNIGHT);
        LocalDateTime midnightWeekAgo = midnightToday.minusWeeks(1L);
        LocalDateTime beforeMidnightWeekAgo = midnightWeekAgo.minusNanos(1L);
        LocalDateTime beforeMidnightToday = midnightToday.minusNanos(1L);

        List<User> recommendUsers = new ArrayList<>();
        for(int i=0;i<10;i++){
            recommendUsers.add(userRepository.save(
                    User.builder()
                            .email("email@email" + i)
                            .password(passwordEncoder.encode("password"))
                            .nickname("nickname" + i)
                            .role(Role.ROLE_USER)
                            .build()
            ));
        }

        for(int i=0;i<10;i++){
            Post post = postRepository.save(
                    Post.builder()
                            .title("yesterdayTitle" + i)
                            .contents("yesterdayContents" + i)
                            .category(Category.Chat)
                            .user(recommendUsers.get(i))
                            .build()
            );

            post.changeCreatedDateTimeforTest(beforeMidnightToday);

            for(int j=0;j<i-1;j++){
                postRecommendRepository.save(
                        PostRecommend.builder()
                                .user(recommendUsers.get(j))
                                .post(post)
                                .build()
                );
            }

            if(i < 5){
                blockRepository.save(
                        Block.builder()
                                .comments("comments")
                                .user(user)
                                .blockUser(recommendUsers.get(i))
                                .build()
                );
            }
        }

        assertThat(postRepository.findAll().size()).isEqualTo(10);

        // when
        String content = mockMvc.perform(get("/public/posts/day"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<PostResponse> bestPostList = (List<PostResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), PostResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // then
        assertThat(bestPostList.size()).isEqualTo(5);
        for(int i=0;i<bestPostList.size()-1;i++){
            assertThat(bestPostList.get(i).getRecommends()
                    .compareTo(bestPostList.get(i+1).getRecommends()))
                    .isNotNegative();
        }
        bestPostList.stream().map(PostResponse::getCreatedDateTime)
                .forEach(t -> assertThat(t).isBetween(midnightWeekAgo, beforeMidnightToday));

        assertThat(bestPostList.stream().map(PostResponse::getUserId).collect(Collectors.toList()))
                .isNotIn(blockRepository.findByUserId(userId).toArray());
    }

    @Test
    @DisplayName("월간 베스트 글을 조회한다")
    void getMonthBestRecommendedPostList() throws Exception {
        // given
        LocalDateTime midnightToday = LocalDateTime.of(
                LocalDate.now(), LocalTime.MIDNIGHT);
        LocalDateTime midnightMonthAgo = midnightToday.minusMonths(1L);
        LocalDateTime beforeMidnightMonthAgo = midnightMonthAgo.minusNanos(1L);
        LocalDateTime beforeMidnightToday = midnightToday.minusNanos(1L);

        List<User> recommendUsers = new ArrayList<>();
        for(int i=0;i<20;i++){
            recommendUsers.add(userRepository.save(
                    User.builder()
                            .email("email@email" + i)
                            .password(passwordEncoder.encode("password"))
                            .nickname("nickname" + i)
                            .role(Role.ROLE_USER)
                            .build()
            ));
        }

        Post postNotIncluded = postRepository.save(
                Post.builder()
                        .title("prevTitle")
                        .contents("prevContents")
                        .category(Category.Chat)
                        .user(user)
                        .build()
        );
        postNotIncluded.changeCreatedDateTimeforTest(beforeMidnightMonthAgo);

        for(int j=0;j<20;j++){
            postRecommendRepository.save(
                    PostRecommend.builder()
                            .post(postNotIncluded)
                            .user(recommendUsers.get(j))
                            .build()
            );
        }

        assertThat(postRecommendRepository.countByPostId(postNotIncluded.getId())).isEqualTo(20L);

        postNotIncluded = postRepository.save(
                Post.builder()
                        .title("todayTitle")
                        .contents("todayContents")
                        .category(Category.Chat)
                        .user(user)
                        .build()
        );
        postNotIncluded.changeCreatedDateTimeforTest(midnightToday);

        for(int j=0;j<20;j++){
            postRecommendRepository.save(
                    PostRecommend.builder()
                            .post(postNotIncluded)
                            .user(recommendUsers.get(j))
                            .build()
            );
        }

        assertThat(postRecommendRepository.countByPostId(postNotIncluded.getId())).isEqualTo(20L);

        for(int i=0;i<21;i++){
            Post post = postRepository.save(
                    Post.builder()
                            .title("yesterdayTitle" + i)
                            .contents("yesterdayContents" + i)
                            .category(Category.Chat)
                            .user(user)
                            .build()
            );

            if(i < 11) post.changeCreatedDateTimeforTest(midnightMonthAgo);
            else post.changeCreatedDateTimeforTest(beforeMidnightToday);

            for(int j=0;j<i-1;j++){
                postRecommendRepository.save(
                        PostRecommend.builder()
                                .user(recommendUsers.get(j))
                                .post(post)
                                .build()
                );
            }
        }

        assertThat(postRepository.findAll().size()).isEqualTo(23);

        // when
        String content = mockMvc.perform(get("/public/posts/day"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<PostResponse> bestPostList = (List<PostResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), PostResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // then
        assertThat(bestPostList.size()).isEqualTo(20);
        for(int i=0;i<bestPostList.size()-1;i++){
            assertThat(bestPostList.get(i).getRecommends()
                    .compareTo(bestPostList.get(i+1).getRecommends()))
                    .isNotNegative();
        }
        bestPostList.stream().map(PostResponse::getCreatedDateTime)
                .forEach(t -> assertThat(t).isBetween(midnightMonthAgo, beforeMidnightToday));
    }

    @Test
    @DisplayName("월간 베스트 글을 조회할 때, 로그인을 했다면, 차단한 유저의 글은 제외한다")
    void getMonthBestRecommendedPostListWithoutPostOfBlockUserIfLogin() throws Exception {
        // given
        LocalDateTime midnightToday = LocalDateTime.of(
                LocalDate.now(), LocalTime.MIDNIGHT);
        LocalDateTime midnightMonthAgo = midnightToday.minusMonths(1L);
        LocalDateTime beforeMidnightMonthAgo = midnightMonthAgo.minusNanos(1L);
        LocalDateTime beforeMidnightToday = midnightToday.minusNanos(1L);

        List<User> recommendUsers = new ArrayList<>();
        for(int i=0;i<10;i++){
            recommendUsers.add(userRepository.save(
                    User.builder()
                            .email("email@email" + i)
                            .password(passwordEncoder.encode("password"))
                            .nickname("nickname" + i)
                            .role(Role.ROLE_USER)
                            .build()
            ));
        }

        for(int i=0;i<10;i++){
            Post post = postRepository.save(
                    Post.builder()
                            .title("yesterdayTitle" + i)
                            .contents("yesterdayContents" + i)
                            .category(Category.Chat)
                            .user(recommendUsers.get(i))
                            .build()
            );

            post.changeCreatedDateTimeforTest(beforeMidnightToday);

            for(int j=0;j<i-1;j++){
                postRecommendRepository.save(
                        PostRecommend.builder()
                                .user(recommendUsers.get(j))
                                .post(post)
                                .build()
                );
            }

            if(i < 5){
                blockRepository.save(
                        Block.builder()
                                .comments("comments")
                                .user(user)
                                .blockUser(recommendUsers.get(i))
                                .build()
                );
            }
        }

        assertThat(postRepository.findAll().size()).isEqualTo(10);

        // when
        String content = mockMvc.perform(get("/public/posts/day"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<PostResponse> bestPostList = (List<PostResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), PostResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // then
        assertThat(bestPostList.size()).isEqualTo(5);
        for(int i=0;i<bestPostList.size()-1;i++){
            assertThat(bestPostList.get(i).getRecommends()
                    .compareTo(bestPostList.get(i+1).getRecommends()))
                    .isNotNegative();
        }
        bestPostList.stream().map(PostResponse::getCreatedDateTime)
                .forEach(t -> assertThat(t).isBetween(midnightMonthAgo, beforeMidnightToday));

        assertThat(bestPostList.stream().map(PostResponse::getUserId).collect(Collectors.toList()))
                .isNotIn(blockRepository.findByUserId(userId).toArray());
    }
}