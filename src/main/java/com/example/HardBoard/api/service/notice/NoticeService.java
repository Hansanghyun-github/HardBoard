package com.example.HardBoard.api.service.notice;

import com.example.HardBoard.domain.notice.Notice;
import com.example.HardBoard.domain.notice.NoticeRepository;
import com.example.HardBoard.dto.NoticeDto;
import com.example.HardBoard.exception.InvalidIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    final private NoticeRepository noticeRepository;

    public Notice createNotice(NoticeDto noticeDto) {
        Notice notice = new Notice();
        return null;
    }

    public void editNotice(Long noticeId, NoticeDto noticeDto){
        if(isExists(noticeId)) throw new InvalidIdException();

    }

    public void deleteNotice(Long noticeId){
        if(isExists(noticeId)) throw new InvalidIdException();
        noticeRepository.deleteById(noticeId);
    }

    private Boolean isExists(Long noticeId) {
        return noticeRepository.existsById(noticeId);
    }
}
