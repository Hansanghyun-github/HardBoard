package com.example.HardBoard.api.service.post.response;

import com.example.HardBoard.domain.post.Category;
import com.example.HardBoard.domain.post.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponse {
    private Long postId;
    private String title;
    private String contents;
    private Long recommends;
    private Long unrecommends;
    private Long views;
    private Category category;
    private Long userId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public PostResponse(
            Long postId, String title, String contents,
            Long recommends, Long unrecommends, Long views,
            Category category, Long userId,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.title = title;
        this.contents = contents;
        this.recommends = recommends;
        this.unrecommends = unrecommends;
        this.views = views;
        this.category = category;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PostResponse of(Post post, Long recommends, Long unrecommends){
        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .category(post.getCategory())
                .recommends(recommends)
                .unrecommends(unrecommends)
                .views(post.getViews())
                .createdAt(post.getCreatedDateTime())
                .updatedAt(post.getModifiedDateTime())
                .build();
    }
}
