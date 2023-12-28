package com.example.HardBoard.acceptance;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.report.request.ReportRequest;
import com.example.HardBoard.api.service.block.response.BlockResponse;
import com.example.HardBoard.api.service.report.response.ReportResponse;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.comment.Comment;
import com.example.HardBoard.domain.comment.CommentRepository;
import com.example.HardBoard.domain.post.Category;
import com.example.HardBoard.domain.post.Post;
import com.example.HardBoard.domain.post.PostRepository;
import com.example.HardBoard.domain.report.Report;
import com.example.HardBoard.domain.report.ReportRepository;
import com.example.HardBoard.domain.report.TargetStatus;
import com.example.HardBoard.domain.user.Role;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
public class ReportAcceptanceTest {
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
    ReportRepository reportRepository;

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
                        .role(Role.ROLE_USER)
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
        Long postId = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()
        ).getId();

        ReportRequest request = ReportRequest.builder()
                .comments("comments")
                .build();

        // when
        mockMvc.perform(post("/reports/posts/" + postId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        // then
        assertThat(reportRepository.findByStatusAndTargetId(TargetStatus.POST, postId).isPresent()).isTrue();

    }

    @Test
    @DisplayName("게시글을 신고할 때는 올바른 postId를 입력해야 한다")
    void reportPostWithWrongPostId() throws Exception {
        // given
        ReportRequest request = ReportRequest.builder()
                .comments("comments")
                .build();

        // when // then
        mockMvc.perform(post("/reports/posts/" + 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid postId"));
    }
    
    @Test
    @DisplayName("게시글을 신고할 때는 사유를 입력해야 한다")
    void reportPostWithWrongComments() throws Exception {
        // given
        ReportRequest request = ReportRequest.builder()
                .comments(" ")
                .build();

        // when // then
        mockMvc.perform(post("/reports/posts/" + 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest());
    }

    @Autowired
    EntityManager em;
    @Test
    @DisplayName("댓글을 신고한다")
    void reportComment() throws Exception {
        // given
        Post post = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .category(Category.Chat)
                        .user(user)
                        .build()
        );
        Long postId = post.getId();

        Comment comment = Comment.builder()
                .contents("conts")
                .user(user)
                .post(post)
                .build();
        comment.setParent();
        Long commentId = commentRepository.save(comment).getId();

        ReportRequest request = ReportRequest.builder()
                .comments("comments")
                .build();

        em.flush();

        System.out.println("----------------- after flush --------------------");

        // when
        mockMvc.perform(post("/reports/comments/" + commentId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        // then
        assertThat(reportRepository.findByStatusAndTargetId(TargetStatus.COMMENT, commentId).isPresent()).isTrue();
    }

    @Test
    @DisplayName("댓글을 신고할 때는 올바른 commentId를 입력해야 한다")
    void reportCommentWithWrongCommentId() throws Exception {
        // given
        ReportRequest request = ReportRequest.builder()
                .comments("comments")
                .build();

        // when // then
        mockMvc.perform(post("/reports/comments/" + 0L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid commentId"));
    }

    @Test
    @DisplayName("댓글을 신고할 때는 사유를 입력해야 한다")
    void reportCommentWithEmptyComments() throws Exception {
        // given
        ReportRequest request = ReportRequest.builder()
                .comments(" ")
                .build();

        // when // then
        mockMvc.perform(post("/reports/comments/" + 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("신고를 취소한다")
    void cancelReport() throws Exception {
        // given
        Long postId = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()
        ).getId();

        Long reportId = reportRepository.save(
                Report.builder()
                        .status(TargetStatus.POST)
                        .targetId(postId)
                        .comments("comments")
                        .user(user)
                        .build()
        ).getId();

        // when // then
        mockMvc.perform(delete("/reports/" + reportId)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        assertThat(reportRepository.findById(reportId).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("신고를 취소할 때는 올바른 reportId를 입력해야 한다")
    void cancelReportWithWrongReportId() throws Exception {
        // given
        Long postId = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()
        ).getId();

        Long reportId = reportRepository.save(
                Report.builder()
                        .status(TargetStatus.POST)
                        .targetId(postId)
                        .comments("comments")
                        .user(user)
                        .build()
        ).getId();

        Long wrongReportId = reportId + 1L;

        // when // then
        mockMvc.perform(delete("/reports/" + wrongReportId)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest());

        assertThat(reportRepository.findById(reportId).isEmpty()).isFalse();
    }
    
    @Test
    @DisplayName("자신의 신고만 취소할 수 있다")
    void cancelOtherReportInFail() throws Exception {
        // given
        User anotherUser = userRepository.save(
                User.builder()
                        .email("Anotheremail@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("husiAnother")
                        .build());

        Long postId = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()
        ).getId();

        Long reportId = reportRepository.save(
                Report.builder()
                        .status(TargetStatus.POST)
                        .targetId(postId)
                        .comments("comments")
                        .user(anotherUser)
                        .build()
        ).getId();

        // when // then
        mockMvc.perform(delete("/reports/" + reportId)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can't delete other user's report"));

        assertThat(reportRepository.findById(reportId).isEmpty()).isFalse();
    }

    @Test
    @DisplayName("신고 리스트 조회")
    void getReportList() throws Exception {
        // given
        User anotherUser = userRepository.save(
                User.builder()
                        .email("Anotheremail@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("husiAnother")
                        .build());
        for(int i=0;i<35;i++){
            Long postId = postRepository.save(
                    Post.builder()
                            .title("title" + i)
                            .contents("contents" + i)
                            .user(anotherUser)
                            .build()
            ).getId();

            reportRepository.save(
                    Report.builder()
                            .status(TargetStatus.POST)
                            .targetId(postId)
                            .comments("comments")
                            .user(user)
                            .build());
        }

        // when
        String content = mockMvc.perform(get("/reports?page=1")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<ReportResponse> collect = (List<ReportResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), ReportResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }) // TODO stream 내에서는 try-catch를 따로 해줘야 하나?
                .collect(Collectors.toList());


        // then
        assertThat(collect.size()).isEqualTo(20);
        for(int i=0;i<collect.size()-1;i++){
            assertThat(collect.get(i).getCreatedDateTime().compareTo(collect.get(i+1).getCreatedDateTime())).isNotNegative();
        }
    }
}
