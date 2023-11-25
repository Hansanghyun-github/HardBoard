package com.example.HardBoard.api.controller.inquiry.request;

import com.example.HardBoard.api.service.inquiry.request.InquiryEditServiceRequest;
import com.example.HardBoard.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryEditRequest {
    private String title;
    private String contents;

    @Builder
    public InquiryEditRequest(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public InquiryEditServiceRequest toServiceRequest(Long inquiryId) {
        return InquiryEditServiceRequest.builder()
                .title(title)
                .contents(contents)
                .inquiryId(inquiryId)
                .build();
    }
}
