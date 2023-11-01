package com.example.HardBoard.notice;

import com.example.HardBoard.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    final private NoticeService noticeService;
    @PostMapping
    public String createNotice(
            @RequestBody NoticeDto noticeDto
    ){
        //TODO
        return "ok";
    }

    @PutMapping("/{noticeId}")
    public String editNotice(
            @RequestParam Long noticeId,
            @RequestBody NoticeDto noticeDto
    ){
        //TODO
        return "ok";
    }

    @DeleteMapping("/{noticeId}")
    public String deleteNotice(
            @RequestParam Long noticeId
    ){
        //TODO
        return "ok";
    }
}
