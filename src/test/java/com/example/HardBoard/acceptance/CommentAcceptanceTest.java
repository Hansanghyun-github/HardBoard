package com.example.HardBoard.acceptance;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.comment.request.CommentCreateRequest;
import com.example.HardBoard.api.controller.comment.request.CommentEditRequest;
import com.example.HardBoard.api.service.comment.response.CommentResponse;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.comment.*;
import com.example.HardBoard.domain.post.*;
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

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class CommentAcceptanceTest {
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
    CommentRepository commentRepository;
    @Autowired
    CommentRecommendRepository commentRecommendRepository;
    @Autowired
    CommentUnrecommendRepository commentUnrecommendRepository;

    User user;
    Long userId;
    String accessToken;
    Post post;

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

        post = postRepository.save(
                Post.builder()
                        .title("title1")
                        .contents("contents1")
                        .user(user)
                        .build());

    }
    @Test
    @DisplayName("댓글을 생성한다")
    void createComment() throws Exception {
        // given
        String contents = "comment1";
        CommentCreateRequest request = CommentCreateRequest.builder()
                .contents(contents)
                .parentCommentId(-1L)
                .build();
        System.out.println(request.getParentCommentId());

        // when
        String content = mockMvc.perform(post("/comments/" + post.getId())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        CommentResponse commentResponse = objectMapper.readValue(objectMapper
                .writeValueAsString(apiResponse.getData()), CommentResponse.class);

        // then
        assertThat(commentResponse.getUserId()).isEqualTo(userId);
        assertThat(commentResponse.getPostId()).isEqualTo(post.getId());
        assertThat(commentRepository.findById(commentResponse.getCommentId())
                .orElseThrow()).isNotNull()
                .satisfies(comment -> {
                    assertThat(comment.getContents()).isEqualTo(contents);
                    assertThat(comment.getId()).isEqualTo(commentResponse.getParentCommentId());
                });
    }
    
    @Test
    @DisplayName("댓글을 생성할 때는 내용이 비어 있으면 안된다")
    void createCommentWithEmptyContentsInFail() throws Exception {
        // given
        String contents = " ";
        CommentCreateRequest request = CommentCreateRequest.builder()
                .contents(contents)
                .parentCommentId(-1L)
                .build();
        System.out.println(request.getParentCommentId());

        // when
        mockMvc.perform(post("/comments/" + post.getId())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("내용은 필수입니다"));
    }
    
    @Test
    @DisplayName("댓글을 생성할 때 올바른 postId이어야 한다")
    void createCommentWithWrongPostId() throws Exception {
        // given
        String contents = "comment1";
        CommentCreateRequest request = CommentCreateRequest.builder()
                .contents(contents)
                .parentCommentId(-1L)
                .build();
        Long wrongPostId = post.getId()+1L;

        // when
        mockMvc.perform(post("/comments/" + wrongPostId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid postId"));
    }

    @Test
    @DisplayName("대댓글을 생성한다")
    void createChildComment() throws Exception {
        // given
        Comment parent = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(user)
                        .build());
        parent.setParent(parent); // set root

        String contents = "comment1";
        CommentCreateRequest request = CommentCreateRequest.builder()
                .contents(contents)
                .parentCommentId(parent.getId())
                .build();
        System.out.println(request.getParentCommentId());

        // when
        String content = mockMvc.perform(post("/comments/" + post.getId())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        CommentResponse commentResponse = objectMapper.readValue(objectMapper
                .writeValueAsString(apiResponse.getData()), CommentResponse.class);

        // then
        assertThat(commentResponse.getUserId()).isEqualTo(userId);
        assertThat(commentResponse.getPostId()).isEqualTo(post.getId());
        assertThat(commentResponse.getParentCommentId()).isEqualTo(parent.getId());
        assertThat(commentRepository.findById(commentResponse.getCommentId())
                .orElseThrow()).isNotNull()
                .satisfies(comment -> {
                    assertThat(comment.getContents()).isEqualTo(contents);
                });
    }

    @Test
    @DisplayName("대댓글을 생성할 때는 올바른 parent comment id이어야 한다")
    void createChildCommentWithWrongParentCommentId() throws Exception {
        // given
        Comment parent = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(user)
                        .build());
        parent.setParent(parent); // set root

        Long wrongParentCommentId = -2L;

        String contents = "comment1";
        CommentCreateRequest request = CommentCreateRequest.builder()
                .contents(contents)
                .parentCommentId(wrongParentCommentId)
                .build();

        // when
        mockMvc.perform(post("/comments/" + post.getId())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid parent comment id"));
    }
    
    @Test
    @DisplayName("댓글을 수정한다")
    void editComment() throws Exception {
        // given
        Comment prevComment = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(user)
                        .build());
        prevComment.setParent(prevComment);
        
        // when
        String editContents = "cc";
        CommentEditRequest request = CommentEditRequest.builder()
                .contents(editContents)
                .build();
        String content = mockMvc.perform(patch("/comments/" + prevComment.getId())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        CommentResponse commentResponse = objectMapper.readValue(objectMapper
                .writeValueAsString(apiResponse.getData()), CommentResponse.class);
        
        // then
        assertThat(commentRepository.findById(commentResponse.getCommentId())
                .orElseThrow()).isNotNull()
                .satisfies(comment -> {
                    assertThat(comment.getContents()).isEqualTo(editContents);
                });
    }
    
    @Test
    @DisplayName("댓글을 수정할 때는 올바른 id를 입력해야 한다")
    void editCommentWithWrongCommentId() throws Exception {
        // given
        Comment comment = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(user)
                        .build());
        comment.setParent(comment);

        Long wrongCommentId = comment.getId() + 1L;

        // when // then
        String editContents = "cc";
        CommentEditRequest request = CommentEditRequest.builder()
                .contents(editContents)
                .build();
        mockMvc.perform(patch("/comments/" + wrongCommentId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid commentId"));
    }

    @Test
    @DisplayName("댓글을 수정할 때는 내용이 비어있으면 안된다")
    void editCommentWithoutContents() throws Exception {
        // given
        Comment comment = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(user)
                        .build());
        comment.setParent(comment);

        // when // then
        String editContents = " ";
        CommentEditRequest request = CommentEditRequest.builder()
                .contents(editContents)
                .build();
        mockMvc.perform(patch("/comments/" + comment.getId())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("내용은 필수입니다"));

    }

    @Test
    @DisplayName("자신의 댓글만 수정할 수 있다")
    void editCommentOtherUser() throws Exception {
        // given
        User anotherUser = userRepository.save(
                User.builder()
                        .email("eemail@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("hhusi")
                        .build());
        Comment comment = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(anotherUser)
                        .build());
        comment.setParent(comment);

        // when // then
        String editContents = "cc";
        CommentEditRequest request = CommentEditRequest.builder()
                .contents(editContents)
                .build();
        mockMvc.perform(patch("/comments/" + comment.getId())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can't control other user's comment"));
    }

    @Test
    @DisplayName("댓글을 삭제한다")
    void deleteComment() throws Exception {
        // given
        Comment comment = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(user)
                        .build());
        comment.setParent(comment);

        assertThat(comment.getIsDeleted()).isFalse();

        // when // then
        mockMvc.perform(delete("/comments/" + comment.getId())
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        assertThat(comment.getIsDeleted()).isTrue();
    }
    
    @Test
    @DisplayName("댓글을 삭제할 때는 올바른 id를 입력해야 한다")
    void deleteCommentWithWrongCommentId() throws Exception {
        // given
        Comment comment = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(user)
                        .build());
        comment.setParent(comment);

        Long wrongCommentId = comment.getId() + 1L;

        assertThat(comment.getIsDeleted()).isFalse();

        // when // then
        mockMvc.perform(delete("/comments/" + wrongCommentId)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest());

        assertThat(comment.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("자신의 댓글만 삭제할 수 있다")
    void deleteCommentOtherUser() throws Exception {
        // given
        User anotherUser = userRepository.save(
                User.builder()
                        .email("eemail@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("hhusi")
                        .build());
        Comment comment = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(anotherUser)
                        .build());
        comment.setParent(comment);

        // when // then
        mockMvc.perform(delete("/comments/" + comment.getId())
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can't control other user's comment"));

        assertThat(comment.getIsDeleted()).isFalse();
    }

    // TODO add test that recommend/unrecommend with wrong comment id

    // TODO add test that duplicate recommed/unrecommend and duplicate cancel

    @Test
    @DisplayName("댓글을 추천한다")
    void recommendComment() throws Exception {
        // given
        Comment comment = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(user)
                        .build());
        comment.setParent(comment);

        // when // then
        mockMvc.perform(post("/comments/" + comment.getId() + "/recommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        assertThat(commentRecommendRepository.findByCommentIdAndUserId(comment.getId(), userId)
                .orElseThrow()).isNotNull();

        assertThat(commentRecommendRepository.countByCommentId(comment.getId())).isEqualTo(1L);
    }

    @Test
    @DisplayName("중복 추천은 불가능하다")
    void recommendDuplicatePost() throws Exception {
        // given
        Post post = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build());
        Comment comment = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(user)
                        .build());
        comment.setParent(comment);

        commentRecommendRepository.save(
                CommentRecommend.builder()
                        .user(user)
                        .comment(comment)
                        .build()
        );

        // when // then
        mockMvc.perform(post("/comments/" + comment.getId() + "/recommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can't duplicate recommend same comment"));
    }

    @Test
    @DisplayName("댓글 추천을 취소한다")
    void cancelRecommendComment() throws Exception {
        // given
        Comment comment = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(user)
                        .build());
        comment.setParent(comment);

        mockMvc.perform(post("/comments/" + comment.getId() + "/recommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        assertThat(commentRecommendRepository.findByCommentIdAndUserId(comment.getId(), userId)
                .orElseThrow()).isNotNull();

        // when // then
        mockMvc.perform(delete("/comments/" + comment.getId() + "/recommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        assertThat(commentRecommendRepository.findByCommentIdAndUserId(comment.getId(), userId)
                        .isEmpty()).isTrue();

        assertThat(commentRecommendRepository.countByCommentId(comment.getId())).isEqualTo(0L);
    }

    @Test
    @DisplayName("추천하지 않아서 삭제할 수 없다")
    void deleteEmptyRecommend() throws Exception {
        // when // then
        mockMvc.perform(delete("/comments/" + 1L + "/recommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Didn't recommend it"));
    }

    @Test
    @DisplayName("댓글을 비추천한다")
    void unrecommendComment() throws Exception {
        // given
        Comment comment = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(user)
                        .build());
        comment.setParent(comment);

        // when // then
        mockMvc.perform(post("/comments/" + comment.getId() + "/unrecommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        assertThat(commentUnrecommendRepository.findByCommentIdAndUserId(comment.getId(), userId)
                .orElseThrow()).isNotNull();

        assertThat(commentUnrecommendRepository.countByCommentId(comment.getId())).isEqualTo(1L);
    }

    @Test
    @DisplayName("중복 비추천은 불가능하다")
    void unrecommendDuplicatePost() throws Exception {
        // given
        Post post = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build());
        Comment comment = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(user)
                        .build());
        comment.setParent(comment);

        commentUnrecommendRepository.save(
                CommentUnrecommend.builder()
                        .user(user)
                        .comment(comment)
                        .build()
        );

        // when // then
        mockMvc.perform(post("/comments/" + comment.getId() + "/unrecommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can't duplicate unrecommend same comment"));
    }

    @Test
    @DisplayName("댓글 비추천을 취소한다")
    void cancelUnrecommendComment() throws Exception {
        // given
        Comment comment = commentRepository.save(
                Comment.builder()
                        .contents("contents1")
                        .post(post)
                        .user(user)
                        .build());
        comment.setParent(comment);

        mockMvc.perform(post("/comments/" + comment.getId() + "/unrecommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        assertThat(commentUnrecommendRepository.findByCommentIdAndUserId(comment.getId(), userId)
                .orElseThrow()).isNotNull();

        // when
        mockMvc.perform(delete("/comments/" + comment.getId() + "/unrecommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        // then
        assertThat(commentUnrecommendRepository.findByCommentIdAndUserId(comment.getId(), userId)
                .isEmpty()).isTrue();

        assertThat(commentUnrecommendRepository.countByCommentId(comment.getId())).isEqualTo(0L);
    }

    @Test
    @DisplayName("비추천하지 않아서 삭제할 수 없다")
    void deleteEmptyUnrecommend() throws Exception {
        // when // then
        mockMvc.perform(delete("/comments/" + 1L + "/unrecommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Didn't unrecommend it"));
    }
}
