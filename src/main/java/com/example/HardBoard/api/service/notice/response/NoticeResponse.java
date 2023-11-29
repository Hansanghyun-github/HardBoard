package com.example.HardBoard.api.service.notice.response;

import com.example.HardBoard.domain.notice.Notice;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeResponse {
    private Long id;
    private String title;
    private String contents;

    @Builder
    private NoticeResponse(Long id, String title, String contents) {
        this.id = id;
        this.title = title;
        this.contents = contents;
    }

    public static NoticeResponse of(Notice notice){
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .contents(notice.getContents())
                .build();
    }
}
