package com.example.HardBoard.api.controller.notice.request;

import com.example.HardBoard.api.service.notice.request.NoticeCreateServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class NoticeCreateRequest {
    @NotBlank(message = "제목은 필수 입니다")
    private String title;
    @NotBlank(message = "내용은 필수 입니다")
    private String contents;

    @Builder
    private NoticeCreateRequest(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public NoticeCreateServiceRequest toServiceCreate(){
        return NoticeCreateServiceRequest.builder()
                .title(title)
                .contents(contents)
                .build();
    }
}
