package com.example.HardBoard.api.service.notice;

import com.example.HardBoard.domain.notice.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class NoticeServiceTest {
    @Autowired
    NoticeService noticeService;

    @Autowired
    NoticeRepository noticeRepository;

    
}