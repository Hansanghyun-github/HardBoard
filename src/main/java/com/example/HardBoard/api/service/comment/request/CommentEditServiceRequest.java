package com.example.HardBoard.api.service.comment.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentEditServiceRequest {
    private Long id;
    private String contents;

    @Builder
    public CommentEditServiceRequest(Long id, String contents) {
        this.id = id;
        this.contents = contents;
    }
}
