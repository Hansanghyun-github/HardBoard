package com.example.HardBoard.api.controller.notice;

import com.example.HardBoard.api.controller.notice.request.NoticeCreateRequest;
import com.example.HardBoard.api.controller.notice.request.NoticeEditRequest;
import com.example.HardBoard.api.service.notice.NoticeService;
import com.example.HardBoard.config.SecurityConfig;
import com.example.HardBoard.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {NoticeController.class})
@Import(TestSecurityConfig.class)
@WithMockUser(roles = "ADMIN")
@MockBean(JpaMetamodelMappingContext.class)
class NoticeControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected NoticeService noticeService;

    @Test
    @DisplayName("신규 공지를 등록한다")
    void createNotice() throws Exception {
        // given
        NoticeCreateRequest request = NoticeCreateRequest.builder()
                .title("title1")
                .contents("contents1")
                .build();
        when(noticeService.createNotice(any()))
                .thenReturn(null);
        
        // when // then
        mockMvc.perform(
                post("/notices")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }
    
    @ParameterizedTest
    @CsvSource(value = {"title1,,내용은 필수 입니다", ",contents1,제목은 필수 입니다"})
    @DisplayName("타이틀이나 콘텐츠가 비어있으면 공지 생성 실패한다")
    void titleOrContentsIsNullFailToCreateNotice(String title, String contents, String errorMessage) throws Exception {
        // given
        NoticeCreateRequest request = NoticeCreateRequest.builder()
                .title(title)
                .contents(contents)
                .build();

        // when // then
        mockMvc.perform(
                        post("/notices")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("공지를 수정한다")
    void editNotice() throws Exception {
        // given
        NoticeEditRequest request = NoticeEditRequest.builder()
                .title("title1")
                .contents("contents1")
                .build();

        // when // then
        mockMvc.perform(patch("/notices/1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvSource(value = {"title1,,내용은 필수 입니다", ",contents1,제목은 필수 입니다"})
    @DisplayName("타이틀이나 콘텐츠가 비어있으면 공지 수정 실패한다")
    void titleOrContentsIsNullFailToEditNotice(String title, String contents, String errorMessage) throws Exception {
        // given
        NoticeEditRequest request = NoticeEditRequest.builder()
                .title(title)
                .contents(contents)
                .build();

        // when // then
        mockMvc.perform(
                        patch("/notices/1")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.data").isEmpty());
    }
    
    @Test
    @DisplayName("공지를 삭제한다")
    void deleteNotice() throws Exception {
        // given
        
        // when // then
        mockMvc.perform(delete("/notices/1"))
                .andExpect(status().isOk());
    }
}