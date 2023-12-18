package com.example.HardBoard.domain.post;

import com.example.HardBoard.domain.BaseEntity;
import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String contents;

    @Enumerated(EnumType.STRING)
    private Category category;

    // TODO 추천과 비추천을 통계 쿼리로 계속 유지시킬 수 있나?

/*    private Long recommends;

    private Long unrecommends;*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Post(String title, String contents, Category category, User user) {
        this.title = title;
        this.contents = contents;
        this.category = category;
        this.user = user;
    }

    @Builder
    public Post(String title, String contents, User user) {
        this.title = title;
        this.contents = contents;
        this.user = user;
        this.category = Category.Chat;
    }

    // TODO 빌더 2개나 만드는게 맞을지


    public static Post create(String title,
                              String contents,
                              Category category,
                              User user){
        return Post.builder()
                .title(title)
                .contents(contents)
                .user(user)
                .category(category)
                .build();
    }

    public void edit(String title, String contents) {
        this.title=title;
        this.contents=contents;
    }

    public void changeCreatedDateTimeforTest(LocalDateTime createdDateTime){
        this.createdDateTime = createdDateTime;
    } // TODO TODO test 때문에 set 메서드 만듬, 나중에는 없애야 함

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", category=" + category +
                ", createdDateTime=" + createdDateTime +
                ", modifiedDateTime=" + modifiedDateTime +
                '}';
    }
}
