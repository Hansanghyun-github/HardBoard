package com.example.HardBoard.api.controller.inquiry.request;

import com.example.HardBoard.api.service.inquiry.request.InquiryRegisterServiceRequest;
import com.example.HardBoard.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class InquiryRegisterRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String contents;

    @Builder
    public InquiryRegisterRequest(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public InquiryRegisterServiceRequest toServiceRequest(User user) {
        return InquiryRegisterServiceRequest.builder()
                .title(title)
                .contents(contents)
                .user(user)
                .build();
    }
}
