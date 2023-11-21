package com.example.HardBoard.api.controller.post.request;

import com.example.HardBoard.api.service.post.request.PostCreateServiceRequest;
import com.example.HardBoard.domain.post.Category;
import com.example.HardBoard.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class PostCreateRequest {
    @NotBlank(message = "제목은 필수 입니다")
    private String title;
    @NotBlank(message = "내용은 필수 입니다")
    private String contents;

    private Category category;

    @Builder
    public PostCreateRequest(String title, String contents, Category category) {
        this.title = title;
        this.contents = contents;
        this.category = category;
    }

    @Builder
    public PostCreateRequest(String title, String contents) {
        this.title = title;
        this.contents = contents;
        this.category = Category.Chat;
    }

    public PostCreateServiceRequest toServiceRequest(User user) {
        return PostCreateServiceRequest.builder()
                .title(title)
                .contents(contents)
                .category(category)
                .user(user)
                .build();
    }
}
