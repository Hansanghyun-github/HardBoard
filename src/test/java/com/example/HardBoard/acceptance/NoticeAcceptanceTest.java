package com.example.HardBoard.acceptance;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.notice.request.NoticeCreateRequest;
import com.example.HardBoard.api.controller.notice.request.NoticeEditRequest;
import com.example.HardBoard.api.service.notice.response.NoticeResponse;
import com.example.HardBoard.api.service.user.response.UserResponse;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.notice.Notice;
import com.example.HardBoard.domain.notice.NoticeRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
public class NoticeAcceptanceTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    NoticeRepository noticeRepository;

    @Test
    @DisplayName("공지를 생성한다")
    void createNotice() throws Exception {
        // given
        String title = "title1";
        String contents = "contents1";
        NoticeCreateRequest request = NoticeCreateRequest.builder()
                .title(title)
                .contents(contents)
                .build();

        // when
        String content = mockMvc.perform(post("/notices")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        NoticeResponse noticeResponse = objectMapper.readValue(objectMapper
                        .writeValueAsString(apiResponse.getData()), NoticeResponse.class);

        // then
        assertThat(noticeRepository.findById(noticeResponse.getId())
                .orElseThrow()).isNotNull()
                .satisfies(notice -> {
                    assertThat(notice.getTitle()).isEqualTo(title);
                    assertThat(notice.getContents()).isEqualTo(contents);
                });


    }

    @Test
    @DisplayName("공지를 수정한다")
    void editNotice() throws Exception {
        // given
        String title = "title1";
        String contents = "contents1";

        Long noticeId = noticeRepository.save(Notice.builder()
                .title(title)
                .contents(contents)
                .build()).getId();

        NoticeEditRequest request = NoticeEditRequest.builder()
                .title(title + "new")
                .contents(contents + "new")
                .build();

        // when
        mockMvc.perform(put("/notices/" + noticeId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        assertThat(noticeRepository.findById(noticeId).orElseThrow())
                .isNotNull()
                .satisfies(notice -> {
                   assertThat(notice.getTitle()).isNotEqualTo(title)
                           .isEqualTo(title+"new");
                   assertThat(notice.getContents()).isNotEqualTo(contents)
                           .isEqualTo(contents+"new");
                });
    }
    
    @Test
    @DisplayName("공지를 삭제한다")
    void deleteNotice() throws Exception {
        // given
        String title = "title1";
        String contents = "contents1";

        Long noticeId = noticeRepository.save(Notice.builder()
                .title(title)
                .contents(contents)
                .build()).getId();

        NoticeEditRequest request = NoticeEditRequest.builder()
                .title(title + "new")
                .contents(contents + "new")
                .build();

        // when
        mockMvc.perform(delete("/notices/" + noticeId))
                .andExpect(status().isOk());
        
        // then
        assertThatThrownBy(() -> noticeRepository.findById(noticeId).orElseThrow())
                .isInstanceOf(NoSuchElementException.class);
    }
}
