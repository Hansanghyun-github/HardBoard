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
import org.assertj.core.api.Assertions;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @BeforeEach
        // TODO BeforeAll로 바꿔서 최적화
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
    @DisplayName("postId의 commentList를 조회한다")
    void getCommentListWithPostId() throws Exception {
        // given
        Post post = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
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
            Comment comment = commentRepository.save(
                    Comment.builder()
                            .contents("contents" + i)
                            .post(post)
                            .user(saved)
                            .build()
            );
            comment.setParent(comment);
        }

        // when
        String content = mockMvc.perform(get("/public/comments/posts/" + post.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<CommentResponse> collect = (List<CommentResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), CommentResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        
        // then
        assertThat(collect.size()).isEqualTo(20);
        for(int i=0;i<collect.size()-1;i++){
            assertThat(collect.get(i).getCreatedDateTime().compareTo(collect.get(i+1).getCreatedDateTime())).isNotPositive();
        }
        collect.stream().map(c -> c.getPostId()).collect(Collectors.toList())
                .forEach(id -> {
                    assertThat(id).isEqualTo(post.getId());
                });
    }

    @Test
    @DisplayName("postId의 commentList를 조회할 때, 로그인 했다면, 차단한 유저의 comment 제외해야 한다")
    void getCommentListWithPostIdWithoutCommentOfBlockUserIfLogin() throws Exception {
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
            Comment comment = commentRepository.save(
                    Comment.builder()
                            .contents("contents" + i)
                            .post(post)
                            .user(saved)
                            .build()
            );
            comment.setParent(comment);

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
        String content = mockMvc.perform(get("/public/comments/posts/" + post.getId())
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<CommentResponse> commentList = (List<CommentResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), CommentResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // then
        assertThat(commentList.size()).isEqualTo(5);
        List<Block> blockList = blockRepository.findByUserId(user.getId());
        assertThat(commentList).isNotIn(blockList.toArray());
    }

    @Test
    @DisplayName("postId의 commentList를 조회할 때 올바른 postId를 입력해야 한다")
    void getCommentListWithWrongPostId() throws Exception {
        // when // then
        mockMvc.perform(get("/public/comments/posts/" + 0L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid postId"));
    }

    @Test
    @DisplayName("postId의 commentList를 조회할 때 올바른 page를 입력해야 한다")
    void getCommentListWithPostIdAndWrongPage() throws Exception {
        // when // then
        mockMvc.perform(get("/public/comments/posts/" + 0L + "?page=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("page has to be greater than zero"));
    }

    @Test
    @DisplayName("userId의 commentList를 조회한다")
    void getCommentListOfUserId() throws Exception {
        // given
        User anotherUser = userRepository.save(
                User.builder()
                        .email("email@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("nickname")
                        .role(Role.ROLE_USER)
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()
        );

        for(int i=0;i<30;i++){
            Comment comment = commentRepository.save(
                    Comment.builder()
                            .contents("contents" + i)
                            .post(post)
                            .user(anotherUser)
                            .build()
            );
            comment.setParent(comment);
        }

        // when
        String content = mockMvc.perform(get("/public/comments/" + anotherUser.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<CommentResponse> commentList = (List<CommentResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), CommentResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // then
        assertThat(commentList.size()).isEqualTo(20);
        for(int i=0;i<commentList.size()-1;i++){
            assertThat(commentList.get(i).getCreatedDateTime().compareTo(commentList.get(i+1).getCreatedDateTime())).isNotNegative();
        }
        commentList.stream().map(c -> c.getUserId()).collect(Collectors.toList())
                .forEach(id -> {
                    assertThat(id).isEqualTo(anotherUser.getId());
                });
    }

    @Test
    @DisplayName("userId의 commentList를 조회할 때 올바른 userId를 입력해야 한다")
    void getCommentListOfWrongUserId() throws Exception {
        // when // then
        mockMvc.perform(get("/public/comments/" + 0L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid userId"));
    }

    @Test
    @DisplayName("userId의 commentList를 조회할 때 올바른 page를 입력해야 한다")
    void getCommentListOfUserIdAndWrongPage() throws Exception {
        // when // then
        mockMvc.perform(get("/public/comments/" + 0L + "?page=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("page has to be greater than zero"));
    }
}
