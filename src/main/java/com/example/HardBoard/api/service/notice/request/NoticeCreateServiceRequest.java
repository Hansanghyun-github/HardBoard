package com.example.HardBoard.api.service.notice.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeCreateServiceRequest {
    private String title;
    private String contents;

    @Builder
    private NoticeCreateServiceRequest(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}
