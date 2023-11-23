package com.example.HardBoard.api.controller.inquiry.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryRegisterRequest {
    private String title;
    private String contents;

    @Builder
    public InquiryRegisterRequest(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}
