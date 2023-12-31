package com.example.HardBoard.api.service.notice;

import com.example.HardBoard.api.service.notice.request.NoticeCreateServiceRequest;
import com.example.HardBoard.api.service.notice.request.NoticeEditServiceRequest;
import com.example.HardBoard.api.service.notice.response.NoticeResponse;
import com.example.HardBoard.domain.notice.NoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

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
        assertThat(notice.getNoticeId()).isNotNull();
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
        noticeService.editNotice(notice.getNoticeId(), request1);

        // then
        NoticeResponse byId = noticeService.findById(notice.getNoticeId());
        assertThat(byId.getTitle()).isEqualTo("title2");
        assertThat(byId.getContents()).isEqualTo("contents2");
    }

    @Test
    @DisplayName("존재하지 않는 Notice를 수정하면 실패한다")
    void editNullNoticeBeError() throws Exception {
        // given
        NoticeEditServiceRequest request = NoticeEditServiceRequest.builder()
                .title("title1")
                .contents("contents1")
                .build();

        // when // then
        assertThatThrownBy(() -> noticeService.editNotice(9L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid id");
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
        noticeService.deleteNotice(notice.getNoticeId());

        // then
        assertThatThrownBy(() -> noticeService.findById(notice.getNoticeId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid id");
    }

    @Test
    @DisplayName("존재하지 않는 Notice를 삭제하는 요청을 보내면 실패한다")
    void deleteNullNoticeBeError() throws Exception {
        // when // then
        assertThatThrownBy(() -> noticeService.deleteNotice(9L))
                .isInstanceOf(EmptyResultDataAccessException.class);

    }

    // TODO title, contents 길이 제한 테스트
}