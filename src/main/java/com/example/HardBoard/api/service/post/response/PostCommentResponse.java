package com.example.HardBoard.api.service.post.response;

import com.example.HardBoard.api.service.comment.response.CommentResponse;
import com.example.HardBoard.domain.post.Category;
import com.example.HardBoard.domain.post.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCommentResponse {
    private Long postId;
    private String title;
    private String contents;
    private Long recommends;
    private Long unrecommends;
    private Long views;
    private Category category;
    private Long userId;
    private String nickname;

    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;

    private List<CommentResponse> commentList;

    @Builder
    public PostCommentResponse(Long postId, String title, String contents,
                               Long recommends, Long unrecommends, Long views,
                               Category category, Long userId, String nickname,
                               LocalDateTime createdDateTime, LocalDateTime modifiedDateTime, List<CommentResponse> commentList) {
        this.postId = postId;
        this.title = title;
        this.contents = contents;
        this.recommends = recommends;
        this.unrecommends = unrecommends;
        this.views = views;
        this.category = category;
        this.userId = userId;
        this.nickname = nickname;
        this.createdDateTime = createdDateTime;
        this.modifiedDateTime = modifiedDateTime;
        this.commentList = commentList;
    }

    public static PostCommentResponse of(PostResponse postResponse, List<CommentResponse> commentList){
        return PostCommentResponse.builder()
                .postId(postResponse.getPostId())
                .title(postResponse.getTitle())
                .contents(postResponse.getContents())
                .category(postResponse.getCategory())
                .recommends(postResponse.getRecommends())
                .unrecommends(postResponse.getUnrecommends())
                .views(postResponse.getViews())
                .createdDateTime(postResponse.getCreatedDateTime())
                .modifiedDateTime(postResponse.getModifiedDateTime())
                .userId(postResponse.getUserId())
                .nickname(postResponse.getNickname())
                .commentList(commentList)
                .build();
    }
}
