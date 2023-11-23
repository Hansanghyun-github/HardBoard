package com.example.HardBoard.api.controller.comment.request;

import com.example.HardBoard.api.service.comment.request.CommentEditServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class CommentEditRequest {
    @NotBlank(message = "내용은 필수입니다")
    private String contents;

    @Builder
    public CommentEditRequest(String contents) {
        this.contents = contents;
    }

    public CommentEditServiceRequest toServiceRequest(Long commentId) {
        return CommentEditServiceRequest.builder()
                .id(commentId)
                .contents(contents)
                .build();
    }
}
