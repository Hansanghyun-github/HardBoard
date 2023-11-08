package com.example.HardBoard.api.service.notice;

import com.example.HardBoard.api.service.notice.request.NoticeCreateServiceRequest;
import com.example.HardBoard.api.service.notice.request.NoticeEditServiceRequest;
import com.example.HardBoard.api.service.notice.response.NoticeResponse;
import com.example.HardBoard.domain.notice.Notice;
import com.example.HardBoard.domain.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    final private NoticeRepository noticeRepository;

    public NoticeResponse createNotice(NoticeCreateServiceRequest request) {
        Notice notice = Notice.create(request.getTitle(), request.getContents());
        Notice savedNotice = noticeRepository.save(notice);

        return NoticeResponse.of(savedNotice);
    }

    public void editNotice(Long noticeId, NoticeEditServiceRequest request){
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() ->
                new IllegalArgumentException("Invalid id"));
        notice.setTitle(request.getTitle());
        notice.setContents(request.getContents());
    }

    public void deleteNotice(Long noticeId){
        noticeRepository.deleteById(noticeId);
    }

    public NoticeResponse findById(Long noticeId) {
        return NoticeResponse.of(noticeRepository.findById(noticeId)
                .orElseThrow(() ->
                new IllegalArgumentException("Invalid id")));
    }
}
