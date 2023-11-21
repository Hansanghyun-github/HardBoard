package com.example.HardBoard.api.service.post.request;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostEditServiceRequest {
    private Long postId;
    private String title;
    private String contents;

    @Builder
    public PostEditServiceRequest(Long postId, String title, String contents) {
        this.postId = postId;
        this.title = title;
        this.contents = contents;
    }

}
