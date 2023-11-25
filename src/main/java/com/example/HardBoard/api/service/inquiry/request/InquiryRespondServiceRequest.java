package com.example.HardBoard.api.service.inquiry.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryRespondServiceRequest {
    private String response;
    private Long inquiryId;

    @Builder
    public InquiryRespondServiceRequest(String response, Long inquiryId) {
        this.response = response;
        this.inquiryId = inquiryId;
    }
}
