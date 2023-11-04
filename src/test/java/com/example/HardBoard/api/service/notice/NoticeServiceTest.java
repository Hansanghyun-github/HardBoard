package com.example.HardBoard.api.service.notice;

import com.example.HardBoard.api.service.notice.request.NoticeCreateServiceRequest;
import com.example.HardBoard.api.service.notice.request.NoticeEditServiceRequest;
import com.example.HardBoard.api.service.notice.response.NoticeResponse;
import com.example.HardBoard.domain.notice.NoticeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class NoticeServiceTest {
    @Autowired
    NoticeService noticeService;

    @Autowired
    NoticeRepository noticeRepository;

    @Test
    @DisplayName("알맞은 Notice 객체를 생성한다")
    void createNotice() throws Exception {
        // given
        NoticeCreateServiceRequest request = NoticeCreateServiceRequest.builder()
                .title("title1")
                .contents("contents1")
                .build();
        
        // when
        NoticeResponse notice = noticeService.createNotice(request);

        // then
        assertThat(notice.getId()).isNotNull();
        assertThat(notice.getTitle()).isEqualTo("title1");
        assertThat(notice.getContents()).isEqualTo("contents1");
    }

    @Test
    @DisplayName("Notice 객체 수정")
    void editNotice() throws Exception {
        // given
        NoticeCreateServiceRequest request = NoticeCreateServiceRequest.builder()
                .title("title1")
                .contents("contents1")
                .build();
        NoticeResponse notice = noticeService.createNotice(request);

        NoticeEditServiceRequest request1 = NoticeEditServiceRequest.builder()
                .title("title2")
                .contents("contents2")
                .build();


        // when
        noticeService.editNotice(notice.getId(), request1);

        // then
        NoticeResponse byId = noticeService.findById(notice.getId());
        assertThat(byId.getTitle()).isEqualTo("title2");
        assertThat(byId.getContents()).isEqualTo("contents2");
    }

    @Test
    @DisplayName("Notice 객체를 삭제한다")
    void deleteNotice() throws Exception {
        // given
        NoticeCreateServiceRequest request = NoticeCreateServiceRequest.builder()
                .title("title1")
                .contents("contents1")
                .build();
        NoticeResponse notice = noticeService.createNotice(request);

        // when
        noticeService.deleteNotice(notice.getId());

        // then
        assertThatThrownBy(() -> noticeService.findById(notice.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Id");
    }

    // TODO title, contents 길이 제한 테스트
}