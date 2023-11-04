package com.example.HardBoard.api.controller.notice;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.notice.request.NoticeCreateRequest;
import com.example.HardBoard.api.controller.notice.request.NoticeEditRequest;
import com.example.HardBoard.api.service.notice.NoticeService;
import com.example.HardBoard.api.service.notice.response.NoticeResponse;
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
    public ApiResponse<NoticeResponse> createNotice(
            @RequestBody NoticeCreateRequest request
            ){
        return ApiResponse.ok(noticeService.createNotice(request.toServiceCreate()));
    }

    @PutMapping("/{noticeId}")
    public ApiResponse<String> editNotice(
            @RequestParam Long noticeId,
            @RequestBody NoticeEditRequest request
    ){
        noticeService.editNotice(noticeId, request.toServiceEdit());
        return ApiResponse.ok("ok");
    }

    @DeleteMapping("/{noticeId}")
    public ApiResponse<String> deleteNotice(
            @RequestParam Long noticeId
    ){
        noticeService.deleteNotice(noticeId);
        return ApiResponse.ok("ok");
    }
}
