package com.example.HardBoard.api.controller.post.request;

import com.example.HardBoard.api.service.post.request.PostEditServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class PostEditRequest {
    @NotBlank(message = "제목은 필수 입니다")
    private String title;
    @NotBlank(message = "내용은 필수 입니다")
    private String contents;

    @Builder
    public PostEditRequest(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public PostEditServiceRequest toServiceRequest(Long postId) {
        return PostEditServiceRequest.builder()
                .postId(postId)
                .title(title)
                .contents(contents)
                .build();
    }
}