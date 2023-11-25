package com.example.HardBoard.api.controller.inquiry.request;

import com.example.HardBoard.api.service.inquiry.request.InquiryRegisterServiceRequest;
import com.example.HardBoard.api.service.inquiry.request.InquiryRespondServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class InquiryRespondRequest {
    @NotBlank
    private String response;

    @Builder
    public InquiryRespondRequest(String response) {
        this.response = response;
    }

    public InquiryRespondServiceRequest toServiceRequest(Long inquiryId) {
        return InquiryRespondServiceRequest.builder()
                .response(response)
                .inquiryId(inquiryId)
                .build();
    }
}
