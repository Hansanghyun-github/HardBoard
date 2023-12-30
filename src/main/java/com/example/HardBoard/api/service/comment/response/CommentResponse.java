package com.example.HardBoard.api.service.comment.response;

import com.example.HardBoard.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponse {
    private Long commentId;
    private String contents;
    private Long parentCommentId;
    private Long postId;
    private Long userId;
    private String nickname;
    private Long recommends;
    private Long unrecommends;

    private LocalDateTime createdDateTime;

    @Builder
    public CommentResponse(Long commentId, String contents, Long parentCommentId, Long postId, Long userId, String nickname, Long recommends, Long unrecommends, LocalDateTime createdDateTime) {
        this.commentId = commentId;
        this.contents = contents;
        this.parentCommentId = parentCommentId;
        this.postId = postId;
        this.userId = userId;
        this.nickname = nickname;
        this.recommends = recommends;
        this.unrecommends = unrecommends;
        this.createdDateTime = createdDateTime;
    }

    public static CommentResponse of(Comment comment, long recommends, long unrecommends) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .contents(comment.getContents())
                .parentCommentId(comment.getParent().getId())
                .createdDateTime(comment.getCreatedDateTime())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .recommends(recommends)
                .unrecommends(unrecommends)
                .build();
    }
}
