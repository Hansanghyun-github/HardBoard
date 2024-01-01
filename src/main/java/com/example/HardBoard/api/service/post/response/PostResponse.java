package com.example.HardBoard.api.service.post.response;

import com.example.HardBoard.domain.post.Category;
import com.example.HardBoard.domain.post.Post;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class PostResponse { // PostList에서 필요한 정보들
    private Long postId;
    private String title;
    private String contents;
    private Long recommends;
    private Long unrecommends;
    private Long comments;
    private Category category;
    private Long userId;
    private String nickname;

    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;

    @Builder
    public PostResponse(
            Long postId, String title, String contents,
            Long recommends, Long unrecommends, Long comments,
            Category category, Long userId, String nickname,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.title = title;
        this.contents = contents;
        this.recommends = recommends;
        this.unrecommends = unrecommends;
        this.comments = comments;
        this.category = category;
        this.userId = userId;
        this.nickname = nickname;
        this.createdDateTime = createdAt;
        this.modifiedDateTime = updatedAt;
    }

    public static PostResponse of(Post post){
        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .category(post.getCategory())
                .recommends(post.getCntRecommends())
                .unrecommends(post.getCntUnrecommends())
                .comments(post.getCntComments())
                .createdAt(post.getCreatedDateTime())
                .updatedAt(post.getModifiedDateTime())
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .build();
    }
}
