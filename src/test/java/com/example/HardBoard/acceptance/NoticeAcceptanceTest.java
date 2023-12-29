package com.example.HardBoard.acceptance;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.notice.request.NoticeCreateRequest;
import com.example.HardBoard.api.controller.notice.request.NoticeEditRequest;
import com.example.HardBoard.api.service.notice.response.NoticeResponse;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.domain.notice.Notice;
import com.example.HardBoard.domain.notice.NoticeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
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
        com.example.HardBoard.api.service.notice.response.NoticeResponse noticeResponse = objectMapper.readValue(objectMapper
                        .writeValueAsString(apiResponse.getData()), com.example.HardBoard.api.service.notice.response.NoticeResponse.class);

        // then
        assertThat(noticeRepository.findById(noticeResponse.getNoticeId())
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
        mockMvc.perform(patch("/notices/" + noticeId)
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

    @Test
    @DisplayName("공지를 조회한다")
    void getNoticeList() throws Exception {
        // given
        for(int i=0;i<35;i++){
            noticeRepository.save(
                    Notice.builder()
                            .title("title" + i)
                            .contents("contents" + i)
                            .build()
            );
        }

        // when
        String content = mockMvc.perform(get("/public/notices?page=1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ArrayList list = (ArrayList) objectMapper.readValue(content, ApiResponse.class).getData();
        List<NoticeResponse> collect = (List<NoticeResponse>) list.stream().map(d ->
                {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(d), NoticeResponse.class);
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
}
