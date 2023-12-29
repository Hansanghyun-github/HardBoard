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
import java.time.LocalDateTime;
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

    @PatchMapping("/inquiries/{inquiryId}")
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

    @PostMapping("/admin/inquiries/{inquiryId}")
    public ApiResponse<InquiryResponse> respondInquiry(
            @PathVariable Long inquiryId,
            @Valid @RequestBody InquiryRespondRequest request
    ){
        return ApiResponse.ok(inquiryService.respondInquiry(request.toServiceRequest(inquiryId), LocalDateTime.now()));
    }

    @GetMapping("/inquiries")
    public ApiResponse<List<InquiryResponse>> getInquiryList(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        if(page <= 0) throw new IllegalArgumentException("page has to be greater than zero");
        return ApiResponse.ok(inquiryService.getInquiryList(principal.getUser().getId(), page - 1));
    }

    @GetMapping("/inquiries/{inquiryId}")
    public ApiResponse<InquiryResponse> getInquiry(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long inquiryId
    ) {
        inquiryService.validateInquiry(inquiryId, principal.getUser());
        return ApiResponse.ok(inquiryService.getInquiry(inquiryId));
    }
}
