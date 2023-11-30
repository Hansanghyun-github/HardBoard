package com.example.HardBoard.api.service.notice.response;

import com.example.HardBoard.domain.notice.Notice;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoticeResponse {
    private Long id;
    private String title;
    private String contents;
    private LocalDateTime createdDateTime;

    @Builder
    public NoticeResponse(Long id, String title, String contents, LocalDateTime createdDateTime) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.createdDateTime = createdDateTime;
    }

    public static NoticeResponse of(Notice notice){
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .contents(notice.getContents())
                .createdDateTime(notice.getCreatedDateTime())
                .build();
    }
}
