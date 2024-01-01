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
@ToString(exclude = {"user"})
public class Post extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String contents;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "cnt_recommends")
    private Long cntRecommends;

    @Column(name = "cnt_unrecommends")
    private Long cntUnrecommends;

    @Column(name = "cnt_comments")
    private Long cntComments;

    @Builder
    public Post(String title, String contents, Category category, User user) {
        this.title = title;
        this.contents = contents;
        this.category = category;
        this.user = user;
        cntRecommends = 0L;
        cntUnrecommends = 0L;
        cntComments = 0L;
    }

    @Builder
    public Post(String title, String contents, User user) {
        this.title = title;
        this.contents = contents;
        this.user = user;
        this.category = Category.Chat;
        cntRecommends = 0L;
        cntUnrecommends = 0L;
        cntComments = 0L;
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

    public void recommend(){
        cntRecommends++;
    }

    public void cancelRecommend(){
        cntRecommends--;
    }

    public void unrecommend(){
        cntUnrecommends++;
    }

    public void cancelUnrecommend(){
        cntUnrecommends--;
    }

    public void increaseCntComments(){
        cntComments++;
    }

    public void decreaseComment(){
        cntComments--;
    }

    public void changeCreatedDateTimeforTest(LocalDateTime createdDateTime){
        this.createdDateTime = createdDateTime;
    } // TODO TODO test 때문에 set 메서드 만듬, 나중에는 없애야 함
}
