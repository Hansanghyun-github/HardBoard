package com.example.HardBoard.domain.comment;

import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_recommends")
@ToString
public class CommentRecommend {
    @Id @GeneratedValue
    @Column(name = "comment_recommend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Comment comment;

    @Builder
    public CommentRecommend(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }

    public static CommentRecommend create(User user, Comment comment){
        comment.recommend();
        return CommentRecommend.builder()
                .user(user)
                .comment(comment)
                .build();
    }
}
