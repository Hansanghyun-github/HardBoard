package com.example.HardBoard.acceptance;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.post.request.PostCreateRequest;
import com.example.HardBoard.api.controller.post.request.PostEditRequest;
import com.example.HardBoard.api.service.post.response.PostResponse;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.post.*;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
public class PostAcceptanceTest {
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
    @DisplayName("post를 생성한다")
    void createPost() throws Exception {
        // given
        String title = "title1";
        String contents = "contents1";
        PostCreateRequest request = PostCreateRequest.builder()
                .title(title)
                .contents(contents)
                .category(Category.Chat)
                .build();

        // when
        String content = mockMvc.perform(post("/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        PostResponse postResponse = objectMapper.readValue(objectMapper
                .writeValueAsString(apiResponse.getData()), PostResponse.class);

        // then
        assertThat(postRepository.findById(postResponse.getPostId())
                .orElseThrow()).isNotNull()
                .satisfies(post -> {
                    assertThat(post.getTitle()).isEqualTo(title);
                    assertThat(post.getContents()).isEqualTo(contents);
                });
    }
    
    @ParameterizedTest
    @CsvSource(value = {"title1,null", "null,contents1"}, nullValues = {"null"})
    @DisplayName("post를 생성할 때 제목과 내용을 필수값이다")
    void createPostWithoutTitleOrContents(String title, String contents) throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title(title)
                .contents(contents)
                .build();

        // when
        mockMvc.perform(post("/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("post를 수정한다")
    void editPost() throws Exception {
        // given
        String title = "title1";
        String contents = "contents1";

        Long postId = postRepository.save(
                Post.builder()
                        .title(title)
                        .contents(contents)
                        .user(user)
                        .build()).getId();

        String newTitle = "newTitle";
        String newContents = "newContents";
        PostEditRequest editRequest = PostEditRequest.builder()
                .title(newTitle)
                .contents(newContents)
                .build();

        // when
        String content = mockMvc.perform(patch("/posts/" + postId)
                        .content(objectMapper.writeValueAsString(editRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        PostResponse postResponse = objectMapper.readValue(objectMapper
                .writeValueAsString(apiResponse.getData()), PostResponse.class);

        // then
        assertThat(postRepository.findById(postResponse.getPostId())
                .orElseThrow()).isNotNull()
                .satisfies(post -> {
                    assertThat(post.getTitle()).isEqualTo(newTitle);
                    assertThat(post.getContents()).isEqualTo(newContents);
                });
    }

    @ParameterizedTest
    @CsvSource(value = {"null,newContents", "newTitle, null"}, nullValues = {"null"})
    @DisplayName("post를 수정할 때 제목과 내용은 필수값이다")
    void editPostWithoutTitleOrContents(String newTitle, String newContents) throws Exception {
        // given
        String title = "title1";
        String contents = "contents1";

        Long postId = postRepository.save(
                Post.builder()
                        .title(title)
                        .contents(contents)
                        .user(user)
                        .build()).getId();

        PostEditRequest editRequest = PostEditRequest.builder()
                .title(newTitle)
                .contents(newContents)
                .build();

        // when // then
        mockMvc.perform(patch("/posts/" + postId)
                        .content(objectMapper.writeValueAsString(editRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("post를 수정할 때 올바른 postId를 전달해야 한다")
    void editPostWithWrongPostId() throws Exception {
        // given
        String title = "title1";
        String contents = "contents1";

        Long postId = postRepository.save(
                Post.builder()
                        .title(title)
                        .contents(contents)
                        .user(user)
                        .build()).getId();
        Long wrongPostId = postId + 1L;

        String newTitle = "newTitle";
        String newContents = "newContents";
        PostEditRequest editRequest = PostEditRequest.builder()
                .title(newTitle)
                .contents(newContents)
                .build();

        // when // then
        mockMvc.perform(patch("/posts/" + wrongPostId)
                        .content(objectMapper.writeValueAsString(editRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid postId"));
    }

    @Test
    @DisplayName("Post의 user와 UserId가 일치하지 않으면 Post를 수정하지 못한다")
    void editPostWithWrongUserID() throws Exception {
        // given
        String title = "title1";
        String contents = "contents1";

        User anotherUser = userRepository.save(
                User.builder()
                        .email("eemail@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("hhusi")
                        .build());

        Long postId = postRepository.save(
                Post.builder()
                        .title(title)
                        .contents(contents)
                        .user(anotherUser)
                        .build()).getId();

        String newTitle = "newTitle";
        String newContents = "newContents";
        PostEditRequest editRequest = PostEditRequest.builder()
                .title(newTitle)
                .contents(newContents)
                .build();

        // when // then
        mockMvc.perform(patch("/posts/" + postId)
                        .content(objectMapper.writeValueAsString(editRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Post's user is different with authenticated user"));
    }
    
    @Test
    @DisplayName("post를 삭제한다")
    void deletePost() throws Exception {
        // given
        Long postId = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()).getId();

        // when // then
        mockMvc.perform(delete("/posts/" + postId)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        assertThat(postRepository.findById(postId).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("post를 삭제할 때 올바른 postId를 전달해야 한다")
    void deletePostWithWrongPostId() throws Exception {
        // given
        Long postId = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()).getId();

        Long wrongPostID = postId+1L;

        // when // then
        mockMvc.perform(delete("/posts/" + wrongPostID)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid postId"));

        assertThat(postRepository.findById(postId).isEmpty()).isFalse();
    }

    @Test
    @DisplayName("Post의 user와 UserId가 일치하지 않으면 Post를 삭제하지 못한다")
    void deletePostWithWrongUserId() throws Exception {
        // given

        User anotherUser = userRepository.save(
                User.builder()
                        .email("eemail@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("hhusi")
                        .build());

        Long postId = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(anotherUser)
                        .build()).getId();

        // when // then
        mockMvc.perform(delete("/posts/" + postId)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Post's user is different with authenticated user"));

        assertThat(postRepository.findById(postId).isEmpty()).isFalse();
    }

    @Test
    @DisplayName("post를 추천한다")
    void recommendPost() throws Exception {
        // given
        Post post = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build());
        Long postId = post.getId();
        Long prevRecommend = postRecommendRepository.countByPostId(postId);

        // when // then
        mockMvc.perform(post("/posts/" + postId+"/recommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        assertThat(postRecommendRepository.findByUserIdAndPostId(userId, postId).isPresent())
                .isTrue();

        assertThat(postRecommendRepository.countByPostId(postId) - prevRecommend)
                .isEqualTo(1L);
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
        Long postId = post.getId();
        Long prevRecommend = postRecommendRepository.countByPostId(postId);

        postRecommendRepository.save(
                PostRecommend.builder()
                        .user(user)
                        .post(post)
                        .build()
        );

        // when // then
        mockMvc.perform(post("/posts/" + postId+"/recommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can't duplicate recommend same post"));
    }

    @Test
    @DisplayName("post 추천을 취소한다")
    void cancelRecommendPost() throws Exception {
        // given
        Long postId = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()).getId();

        // TODO use Domain, not use mockMvc
        mockMvc.perform(post("/posts/" + postId+"/recommend")
                .header(JwtProperties.HEADER_STRING,
                        JwtProperties.TOKEN_PREFIX + accessToken));

        Long prevRecommend = postRecommendRepository.countByPostId(postId);

        // when
        mockMvc.perform(delete("/posts/" + postId+"/recommend")
                .header(JwtProperties.HEADER_STRING,
                        JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        assertThat(postRecommendRepository.findByUserIdAndPostId(userId, postId).isPresent())
                .isFalse();

        assertThat(prevRecommend - postRecommendRepository.countByPostId(postId))
                .isEqualTo(1L);
    }

    @Test
    @DisplayName("추천하지 않아서 삭제할 수 없다")
    void deleteEmptyRecommend() throws Exception {
        // when // then
        mockMvc.perform(delete("/posts/" + 1L+"/recommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Didn't recommend it"));
    }

    @Test
    @DisplayName("post를 비추천한다")
    void unrecommendPost() throws Exception {
        // given
        Long postId = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()).getId();

        Long prevUnrecommend = postUnrecommendRepository.countByPostId(postId);

        // when // then
        mockMvc.perform(post("/posts/" + postId+"/unrecommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        assertThat(postUnrecommendRepository.findByUserIdAndPostId(userId, postId).isPresent())
                .isTrue();

        assertThat(postUnrecommendRepository.countByPostId(postId - prevUnrecommend))
                .isEqualTo(1L);
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
        Long postId = post.getId();
        Long prevRecommend = postUnrecommendRepository.countByPostId(postId);

        postUnrecommendRepository.save(
                PostUnrecommend.builder()
                        .user(user)
                        .post(post)
                        .build()
        );

        // when // then
        mockMvc.perform(post("/posts/" + postId+"/unrecommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can't duplicate unrecommend same post"));
    }

    @Test
    @DisplayName("post 비추천을 취소한다")
    void cancelUnrocommendPost() throws Exception {
        // given
        Long postId = postRepository.save(
                Post.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()).getId();

        mockMvc.perform(post("/posts/" + postId+"/unrecommend")
                .header(JwtProperties.HEADER_STRING,
                        JwtProperties.TOKEN_PREFIX + accessToken));

        Long prevUnrecommend = postUnrecommendRepository.countByPostId(postId);

        // when
        mockMvc.perform(delete("/posts/" + postId+"/unrecommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        assertThat(postUnrecommendRepository.findByUserIdAndPostId(userId, postId).isPresent())
                .isFalse();

        assertThat(prevUnrecommend - postUnrecommendRepository.countByPostId(postId))
                .isEqualTo(1L);
    }

    @Test
    @DisplayName("비추천하지 않아서 삭제할 수 없다")
    void deleteEmptyUnrecommend() throws Exception {
        // when // then
        mockMvc.perform(delete("/posts/" + 1L+"/unrecommend")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Didn't unrecommend it"));
    }
}
