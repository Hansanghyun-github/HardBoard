package com.example.HardBoard.api.service.comment.request;

import com.example.HardBoard.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateServiceRequest {
    private User user;
    private Long postId;
    private Long parentCommentId;
    private String contents;

    @Builder
    public CommentCreateServiceRequest(User user, Long postId, Long parentCommentId, String contents) {
        this.user = user;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.contents = contents;
    }
}
