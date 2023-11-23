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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

        // when

        // then
    }

    @ParameterizedTest
    @CsvSource(value = {"title,null","null,contents"}, nullValues = {"null"})
    @DisplayName("문의를 등록할 때 제목이나 내용이 비어있으면 안된다")
    void registerInquiryWithEmptyTitleOrEmptyContents(String title, String contents) throws Exception {
        // given
        
        // when
        
        // then
    }

    @Test
    @DisplayName("문의를 수정한다")
    void editInquiry() throws Exception {
        // given

        // when

        // then
    }

    @ParameterizedTest
    @CsvSource(value = {"title,null","null,contents"}, nullValues = {"null"})
    @DisplayName("문의를 수정할 때 제목이나 내용이 비어있으면 안된다")
    void editInquiryWithEmptyTitleOrEmptyContents(String title, String contents) throws Exception {
        // given

        // when

        // then
    }
    
    @Test
    @DisplayName("문의를 수정할 때는 올바른 inquiryId를 입력해야 한다")
    void editInquiryWithWrongInquiryId() throws Exception {
        // given
        
        // when
        
        // then
    }

    @Test
    @DisplayName("자신의 문의만 수정할 수 있다")
    void editInquiryWithWrongUserId() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("문의를 삭제한다")
    void deleteInquiry() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("문의를 삭제할 때는 올바른 inquiryId를 입력해야 한다")
    void deleteInquiryWithWrongInquiryId() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("문의에 답변한다")
    @WithMockUser(roles = {"ADMIN"})
    void respondInquiry() throws Exception {
        // given

        // when

        // then
    }
    
    @Test
    @DisplayName("문의에 답변할 때는 내용이 비어있으면 안된다")
    @WithMockUser(roles = {"ADMIN"})
    void respondInquiryWithEmptyContents() throws Exception {
        // given
        
        // when
        
        // then
    }
    
    @Test
    @DisplayName("문의에 답변할 때는 올바른 InquiryId를 입력해야 한다")
    @WithMockUser(roles = {"ADMIN"})
    void respondInquiryWithWrongInquiryId() throws Exception {
        // given
        
        // when
        
        // then
    }
}
