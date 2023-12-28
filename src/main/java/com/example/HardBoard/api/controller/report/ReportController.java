package com.example.HardBoard.api.controller.report;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.report.request.ReportRequest;
import com.example.HardBoard.api.service.report.ReportService;
import com.example.HardBoard.api.service.report.response.ReportResponse;
import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.domain.report.TargetStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/reports/posts/{postId}")
    public ApiResponse<String> reportPost(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long postId,
            @Valid @RequestBody ReportRequest request
    ) {
        reportService.validateTarget(TargetStatus.POST, postId);
        reportService.createReport(request.toServiceRequest(principal.getUser(), postId, TargetStatus.POST));
        return ApiResponse.ok("ok");
    } // 신고하고 신고글 안봐도 되서 그냥 String으로 반환 함

    @PostMapping("/reports/comments/{commentId}")
    public ApiResponse<String> reportComment(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long commentId,
            @Valid @RequestBody ReportRequest request
    ) {
        reportService.validateTarget(TargetStatus.COMMENT, commentId);
        reportService.createReport(request.toServiceRequest(principal.getUser(), commentId, TargetStatus.COMMENT));
        return ApiResponse.ok("ok");
    }

    @DeleteMapping("/reports/{reportId}")
    public ApiResponse<String> cancelReport(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long reportId
    ) {
        reportService.validateTarget(reportId, principal.getUser());
        reportService.cancelReport(reportId);
        return ApiResponse.ok("ok");
    }

    @GetMapping("/reports")
    public ApiResponse<List<ReportResponse>> getReportList(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        if(page <= 0) throw new IllegalArgumentException("page has to be greater than zero");
        return ApiResponse.ok(reportService.getBlockList(principal.getUser().getId(), page - 1));
    }
    // TODO Block, Report, Inquiry getXXXList 페이징 테스트
    // TODO Response에 entity 없는지 체크
}
