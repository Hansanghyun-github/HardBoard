package com.example.HardBoard.api.controller.notice;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.notice.request.NoticeCreateRequest;
import com.example.HardBoard.api.controller.notice.request.NoticeEditRequest;
import com.example.HardBoard.api.service.notice.NoticeService;
import com.example.HardBoard.api.service.notice.response.NoticeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class NoticeController {

    final private NoticeService noticeService;
    @PostMapping("/notices")
    public ApiResponse<NoticeResponse> createNotice(
            @Valid @RequestBody NoticeCreateRequest request
            ){
        return ApiResponse.ok(noticeService.createNotice(request.toServiceCreate()));
    }

    @PutMapping("/notices/{noticeId}")
    public ApiResponse<String> editNotice(
            @PathVariable Long noticeId,
            @Valid @RequestBody NoticeEditRequest request
    ){
        noticeService.editNotice(noticeId, request.toServiceEdit());
        return ApiResponse.ok("ok");
    }

    @DeleteMapping("/notices/{noticeId}")
    public ApiResponse<String> deleteNotice(
            @PathVariable Long noticeId
    ){
        noticeService.deleteNotice(noticeId);
        return ApiResponse.ok("ok");
    }

    @GetMapping("/public/notices")
    public ApiResponse<List<NoticeResponse>> getNoticeList(
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        if(page <= 0) throw new IllegalArgumentException("page has to be greater than zero");
        return ApiResponse.ok(noticeService.findAll(page));
    }
}
