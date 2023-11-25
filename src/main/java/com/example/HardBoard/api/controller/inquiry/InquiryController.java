package com.example.HardBoard.api.controller.inquiry;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.inquiry.request.InquiryEditRequest;
import com.example.HardBoard.api.controller.inquiry.request.InquiryRegisterRequest;
import com.example.HardBoard.api.controller.inquiry.request.InquiryRespondRequest;
import com.example.HardBoard.api.service.inquiry.InquiryService;
import com.example.HardBoard.api.service.inquiry.response.InquiryResponse;
import com.example.HardBoard.config.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InquiryController {
    private final InquiryService inquiryService;

    @PostMapping("/inquiries")
    public ApiResponse<InquiryResponse> registerInquiry(
            @AuthenticationPrincipal PrincipalDetails principal,
            @Valid @RequestBody InquiryRegisterRequest request
    ){
        return ApiResponse.ok(inquiryService.registerInquiry(request
                .toServiceRequest(principal.getUser())));
    }

    @PostMapping("/inquiries/{inquiryId}")
    public ApiResponse<InquiryResponse> editInquiry(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long inquiryId,
            @Valid @RequestBody InquiryEditRequest request
    ){
        inquiryService.validateInquiry(inquiryId, principal.getUser());
        return ApiResponse.ok(inquiryService.editInquiry(request.toServiceRequest(inquiryId)));
    }

    @DeleteMapping("/inquiries/{inquiryId}")
    public ApiResponse<String> deleteInquiry(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long inquiryId
    ){
        inquiryService.validateInquiry(inquiryId, principal.getUser());
        inquiryService.deleteInquiry(inquiryId);
        return ApiResponse.ok("ok");
    }

    @GetMapping("/inquiries") // TODO 조회 테스트는 나중에 - 페이징 처리
    public ApiResponse<List<InquiryResponse>> getInquiryList(
            @AuthenticationPrincipal PrincipalDetails principal
    ){
        return null;
    }

    @PostMapping("/admin/inquiries/{inquiryId}")
    public ApiResponse<InquiryResponse> respondInquiry(
            @PathVariable Long inquiryId,
            @Valid @RequestBody InquiryRespondRequest request
    ){
        return ApiResponse.ok(inquiryService.respondInquiry(request.toServiceRequest(inquiryId)));
    }
}
