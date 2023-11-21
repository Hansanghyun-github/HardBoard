package com.example.HardBoard.domain.post;

import com.example.HardBoard.domain.BaseEntity;
import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "posts")
@ToString(exclude = {"user"})
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

    private Long views; // TODO if get, plus 1

    // TODO 같은 get 요청을 계속 하면, 조회수가 끊임없이 올라간다. 어떻게 해결하나?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Post(String title, String contents, Category category, User user) {
        this.title = title;
        this.contents = contents;
        this.category = category;
        this.user = user;
        this.views = 0L;
    }

    @Builder
    public Post(String title, String contents, User user) {
        this.title = title;
        this.contents = contents;
        this.user = user;
        this.category = Category.Chat;
    }

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
}
