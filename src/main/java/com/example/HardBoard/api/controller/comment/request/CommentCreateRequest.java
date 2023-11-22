package com.example.HardBoard.api.controller.comment.request;

import com.example.HardBoard.api.service.comment.request.CommentCreateServiceRequest;
import com.example.HardBoard.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {
    private Long parentCommentId;
    @NotBlank(message = "내용은 필수입니다")
    private String contents;

    @Builder
    public CommentCreateRequest(Long parentCommentId, String contents) {
        this.parentCommentId = parentCommentId;
        this.contents = contents;
    }

    public CommentCreateServiceRequest toServiceRequest(User user, Long postId) {
        return CommentCreateServiceRequest.builder()
                .user(user)
                .postId(postId)
                .parentCommentId(parentCommentId)
                .contents(contents)
                .build();
    }
}
