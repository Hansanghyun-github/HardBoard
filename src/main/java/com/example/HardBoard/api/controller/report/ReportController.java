package com.example.HardBoard.api.controller.report;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.report.request.ReportRequest;
import com.example.HardBoard.api.service.report.response.ReportResponse;
import com.example.HardBoard.config.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    @PostMapping("/posts/reports/{postId}")
    public ApiResponse<String> reportPost(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long postId,
            @Valid @RequestBody ReportRequest request
    ) {
        return null;
    } // 신고하고 신고글 안봐도 되서 그냥 String으로 반환 함

    @PostMapping("/comments/reports/{commentId}")
    public ApiResponse<String> reportComment(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long commentId,
            @Valid @RequestBody ReportRequest request
    ) {
        return null;
    }

    @DeleteMapping("/reports/{reportId}")
    public ApiResponse<String> cancelReport(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long reportId
    ) {
        return null;
    }

    @GetMapping("/reports")
    public ApiResponse<List<ReportResponse>> getReportList(
            @AuthenticationPrincipal PrincipalDetails principal
    ){
        return null;
    } // TODO 페이징처리 하고 테스트
}
