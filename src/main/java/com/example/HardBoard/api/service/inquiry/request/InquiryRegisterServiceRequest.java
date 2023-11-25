package com.example.HardBoard.api.service.inquiry.request;

import com.example.HardBoard.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryRegisterServiceRequest {
    private String title;
    private String contents;
    private User user;

    @Builder
    public InquiryRegisterServiceRequest(String title, String contents, User user) {
        this.title = title;
        this.contents = contents;
        this.user = user;
    }
}
