package com.example.HardBoard.api.service.inquiry.request;

import com.example.HardBoard.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryEditServiceRequest {
    private String title;
    private String contents;
    private Long inquiryId;

    @Builder
    public InquiryEditServiceRequest(String title, String contents, Long inquiryId) {
        this.title = title;
        this.contents = contents;
        this.inquiryId = inquiryId;
    }
}
