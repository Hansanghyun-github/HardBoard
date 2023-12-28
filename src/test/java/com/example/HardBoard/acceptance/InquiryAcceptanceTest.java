package com.example.HardBoard.acceptance;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.inquiry.request.InquiryEditRequest;
import com.example.HardBoard.api.controller.inquiry.request.InquiryRegisterRequest;
import com.example.HardBoard.api.controller.inquiry.request.InquiryRespondRequest;
import com.example.HardBoard.api.service.inquiry.response.InquiryResponse;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.inquiry.Inquiry;
import com.example.HardBoard.domain.inquiry.InquiryRepository;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
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
public class InquiryAcceptanceTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    InquiryRepository inquiryRepository;

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
    @DisplayName("문의를 등록한다")
    void registerInquiry() throws Exception {
        // given
        String title = "title1";
        String contents = "contents1";
        InquiryRegisterRequest request = InquiryRegisterRequest.builder()
                .title(title)
                .contents(contents)
                .build();

        // when
        String content = mockMvc.perform(post("/inquiries")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        InquiryResponse inquiryResponse = objectMapper.readValue(objectMapper
                .writeValueAsString(apiResponse.getData()), InquiryResponse.class);

        // then
        assertThat(inquiryResponse).isNotNull()
                .satisfies(iR -> {
                    assertThat(iR.getUserId()).isEqualTo(userId);
                    assertThat(iR.getTitle()).isEqualTo(title);
                    assertThat(iR.getContents()).isEqualTo(contents);
                    // TODO assertThat(iR.getResponse()).isEqualTo("");
                });
    }

    @ParameterizedTest
    @CsvSource(value = {"title,null","null,contents"}, nullValues = {"null"})
    @DisplayName("문의를 등록할 때 제목이나 내용이 비어있으면 안된다")
    void registerInquiryWithEmptyTitleOrEmptyContents(String title, String contents) throws Exception {
        // given
        InquiryRegisterRequest request = InquiryRegisterRequest.builder()
                .title(title)
                .contents(contents)
                .build();

        // when // then
        mockMvc.perform(post("/inquiries")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("문의를 수정한다")
    void editInquiry() throws Exception {
        // given
        Long inquiryId = inquiryRepository.save(
                Inquiry.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()
        ).getId();

        String title = "title1";
        String contents = "contents1";

        InquiryEditRequest request = InquiryEditRequest.builder()
                .title(title)
                .contents(contents)
                .build();

        // when
        String content = mockMvc.perform(put("/inquiries/" + inquiryId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        InquiryResponse inquiryResponse = objectMapper.readValue(objectMapper
                .writeValueAsString(apiResponse.getData()), InquiryResponse.class);

        // then
        assertThat(inquiryResponse).isNotNull()
                .satisfies(iR -> {
                    assertThat(iR.getTitle()).isEqualTo(title);
                    assertThat(iR.getContents()).isEqualTo(contents);
                });
    }

    @ParameterizedTest
    @CsvSource(value = {"title,null","null,contents"}, nullValues = {"null"})
    @DisplayName("문의를 수정할 때 제목이나 내용이 비어있으면 안된다")
    void editInquiryWithEmptyTitleOrEmptyContents(String title, String contents) throws Exception {
        InquiryEditRequest request = InquiryEditRequest.builder()
                .title(title)
                .contents(contents)
                .build();

        // when // then
        mockMvc.perform(put("/inquiries/" + 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("문의를 수정할 때는 올바른 inquiryId를 입력해야 한다")
    void editInquiryWithWrongInquiryId() throws Exception {
        String title="title";
        String contents = "contents";
        InquiryEditRequest request = InquiryEditRequest.builder()
                .title(title)
                .contents(contents)
                .build();

        // when // then
        mockMvc.perform(put("/inquiries/" + 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("자신의 문의만 수정할 수 있다")
    void editInquiryWithWrongUserId() throws Exception {
        // given
        User anotherUser = userRepository.save(
                User.builder()
                        .email("Anotheremail@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("husiAnother")
                        .build());

        Long inquiryId = inquiryRepository.save(
                Inquiry.builder()
                        .title("title")
                        .contents("contents")
                        .user(anotherUser)
                        .build()
        ).getId();

        String title = "title1";
        String contents = "contents1";

        InquiryEditRequest request = InquiryEditRequest.builder()
                .title(title)
                .contents(contents)
                .build();

        // when // then
        mockMvc.perform(put("/inquiries/" + inquiryId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can't control other user's inquiry"));
    }

    @Test
    @DisplayName("문의를 삭제한다")
    void deleteInquiry() throws Exception {
        // given
        Long inquiryId = inquiryRepository.save(
                Inquiry.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()
        ).getId();

        // when
        mockMvc.perform(delete("/inquiries/" + inquiryId)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk());

        // then
        assertThat(inquiryRepository.findById(inquiryId).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("문의를 삭제할 때는 올바른 inquiryId를 입력해야 한다")
    void deleteInquiryWithWrongInquiryId() throws Exception {
        // when // then
        mockMvc.perform(delete("/inquiries/" + 1L)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid inquiryId"));
    }

    @Test
    @DisplayName("자신의 문의만 삭제할 수 있다")
    void deleteOtherUserInquiry() throws Exception {
        // given
        User anotherUser = userRepository.save(
                User.builder()
                        .email("Anotheremail@email")
                        .password(passwordEncoder.encode("password"))
                        .nickname("husiAnother")
                        .build());

        Long inquiryId = inquiryRepository.save(
                Inquiry.builder()
                        .title("title")
                        .contents("contents")
                        .user(anotherUser)
                        .build()
        ).getId();

        // when // then
        mockMvc.perform(delete("/inquiries/" + inquiryId)
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can't control other user's inquiry"));
    }

    @Test
    @DisplayName("문의에 답변한다")
    @WithMockUser(roles = {"ADMIN"})
    void respondInquiry() throws Exception {
        // given
        Long inquiryId = inquiryRepository.save(
                Inquiry.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()
        ).getId();

        InquiryRespondRequest request = InquiryRespondRequest.builder()
                .response("respond")
                .build();

        // when
        String content = mockMvc.perform(post("/admin/inquiries/" + inquiryId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        InquiryResponse inquiryResponse = objectMapper.readValue(objectMapper
                .writeValueAsString(apiResponse.getData()), InquiryResponse.class);

        // then
        assertThat(inquiryResponse).isNotNull()
                .satisfies(iR -> {
                    assertThat(iR.getResponse()).isEqualTo("respond");
                });
    }
    
    @Test
    @DisplayName("문의에 답변할 때는 내용이 비어있으면 안된다")
    @WithMockUser(roles = {"ADMIN"})
    void respondInquiryWithEmptyContents() throws Exception {
        // given
        Long inquiryId = inquiryRepository.save(
                Inquiry.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()
        ).getId();

        InquiryRespondRequest request = InquiryRespondRequest.builder()
                .response(" ")
                .build();

        // when
        mockMvc.perform(post("/admin/inquiries/" + inquiryId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("문의에 답변할 때는 올바른 InquiryId를 입력해야 한다")
    @WithMockUser(roles = {"ADMIN"})
    void respondInquiryWithWrongInquiryId() throws Exception {
        // given
        InquiryRespondRequest request = InquiryRespondRequest.builder()
                .response("respond")
                .build();
        
        // when // then
        mockMvc.perform(post("/admin/inquiries/" + 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("문의 리스트를 조회한다")
    void getInquiryList() throws Exception {
        // given
        for(int i=0;i<35;i++){
            inquiryRepository.save(
                    Inquiry.builder()
                            .title("title" + i)
                            .contents("contents" + i)
                            .user(user)
                            .build()
            );
        }

        // when
        String content = mockMvc.perform(get("/inquiries?page=1")
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<InquiryResponse> collect = (List<InquiryResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), InquiryResponse.class);
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
    @DisplayName("문의 조회")
    void getInquiry() throws Exception {
        // given
        Inquiry inquiry = inquiryRepository.save(
                Inquiry.builder()
                        .title("title")
                        .contents("contents")
                        .user(user)
                        .build()
        );

        // when
        String content = mockMvc.perform(get("/inquiries/" + inquiry.getId())
                        .header(JwtProperties.HEADER_STRING,
                                JwtProperties.TOKEN_PREFIX + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        InquiryResponse inquiryResponse = objectMapper.readValue(objectMapper
                .writeValueAsString(apiResponse.getData()), InquiryResponse.class);

        // then
        assertThat(inquiryResponse.getTitle()).isEqualTo("title");
        assertThat(inquiryResponse.getContents()).isEqualTo("contents");
        assertThat(inquiryResponse.getIsResponded()).isEqualTo(false);
    }
}
