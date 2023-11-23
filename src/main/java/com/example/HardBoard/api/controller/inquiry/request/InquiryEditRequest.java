package com.example.HardBoard.api.controller.inquiry.request;

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
}
