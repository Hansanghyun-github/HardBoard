package com.example.HardBoard.api.service.comment.response;

import com.example.HardBoard.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String contents;
    private Long parentCommentId;
    private Long postId;
    private Long userId;
    private Long recommends;
    private Long unrecommends;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // TODO add user nickname

    @Builder
    public CommentResponse(Long id, String contents, Long parentCommentId, Long postId, Long userId, Long recommends, Long unrecommends, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.contents = contents;
        this.parentCommentId = parentCommentId;
        this.postId = postId;
        this.userId = userId;
        this.recommends = recommends;
        this.unrecommends = unrecommends;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public static CommentResponse of(Comment comment, long recommends, long unrecommends) {
        return CommentResponse.builder()
                .id(comment.getId())
                .contents(comment.getContents())
                .parentCommentId(comment.getParent().getId())
                .createdAt(comment.getCreatedDateTime())
                .updatedAt(comment.getModifiedDateTime())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .recommends(recommends)
                .unrecommends(unrecommends)
                .build();
    }
}
