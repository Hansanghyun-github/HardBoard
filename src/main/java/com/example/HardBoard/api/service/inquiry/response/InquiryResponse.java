package com.example.HardBoard.api.service.inquiry.response;

import com.example.HardBoard.domain.inquiry.Inquiry;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
public class InquiryResponse {
    private Long inquiryId;
    private String title;
    private String contents;
    private Long userId;
    private LocalDateTime createdDateTime;
    private Boolean isResponded;
    private String response;
    private LocalDateTime respondDateTime;

    @Builder
    public InquiryResponse(Long inquiryId, String title, String contents, Long userId, LocalDateTime createdDateTime, boolean isResponded, String response, LocalDateTime respondDateTime) {
        this.inquiryId = inquiryId;
        this.title = title;
        this.contents = contents;
        this.userId = userId;
        this.createdDateTime = createdDateTime;
        this.isResponded = isResponded;
        this.response = response;
        this.respondDateTime = respondDateTime;
    }

    public static InquiryResponse of(Inquiry inquiry) {
        return InquiryResponse.builder()
                .inquiryId(inquiry.getId())
                .title(inquiry.getTitle())
                .contents(inquiry.getContents())
                .userId(inquiry.getUser().getId())
                .createdDateTime(inquiry.getCreatedDateTime())
                .isResponded(inquiry.getIsResponded())
                .response(inquiry.getResponse())
                .respondDateTime(inquiry.getRespondDateTime())
                .build();
    }
}
