package com.example.HardBoard.api.controller.notice.request;

import com.example.HardBoard.api.service.notice.request.NoticeEditServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class NoticeEditRequest {
    @NotBlank(message = "제목은 필수 입니다")
    private String title;
    @NotBlank(message = "내용은 필수 입니다")
    private String contents;

    @Builder
    private NoticeEditRequest(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public NoticeEditServiceRequest toServiceEdit(){
        return NoticeEditServiceRequest.builder()
                .title(title)
                .contents(contents)
                .build();
    }
}

