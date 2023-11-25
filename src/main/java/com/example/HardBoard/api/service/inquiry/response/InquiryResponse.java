package com.example.HardBoard.api.service.inquiry.response;

import com.example.HardBoard.domain.inquiry.Inquiry;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryResponse {
    private Long inquiryId;
    private String title;
    private String contents;
    private Long userId;
    private String response;

    @Builder
    public InquiryResponse(Long inquiryId, String title, String contents, Long userId, String response) {
        this.inquiryId = inquiryId;
        this.title = title;
        this.contents = contents;
        this.userId = userId;
        this.response = response;
    }

    public static InquiryResponse of(Inquiry inquiry) {
        return InquiryResponse.builder()
                .inquiryId(inquiry.getId())
                .title(inquiry.getTitle())
                .contents(inquiry.getContents())
                .userId(inquiry.getUser().getId())
                .response(inquiry.getResponse())
                .build();
    }
}
