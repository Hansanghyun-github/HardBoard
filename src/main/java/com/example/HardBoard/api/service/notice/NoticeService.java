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
        Optional<Notice> optionalNotice = noticeRepository.findById(noticeId);
        if(optionalNotice.isEmpty()) throw new IllegalArgumentException("Invalid Id");

        Notice notice = optionalNotice.get();
        notice.setTitle(request.getTitle());
        notice.setContents(request.getContents());
    }

    public void deleteNotice(Long noticeId){
        noticeRepository.deleteById(noticeId);
    }

    public NoticeResponse findById(Long noticeId) {
        // TODO change Optional default method
        Optional<Notice> notice = noticeRepository.findById(noticeId);
        if(notice.isEmpty()) throw new IllegalArgumentException("Invalid Id");
        return NoticeResponse.of(notice.get());
    }
}
