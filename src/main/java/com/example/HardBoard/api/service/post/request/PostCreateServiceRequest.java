package com.example.HardBoard.api.service.post.request;

import com.example.HardBoard.domain.post.Category;
import com.example.HardBoard.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCreateServiceRequest {
    private String title;
    private String contents;
    private Category category;
    private User user;

    @Builder
    public PostCreateServiceRequest(String title, String contents, Category category, User user) {
        this.title = title;
        this.contents = contents;
        this.category = category;
        this.user = user;
    }
}
