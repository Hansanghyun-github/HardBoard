package com.example.HardBoard.domain.comment;

import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_rnrecommends")
@ToString
public class CommentUnrecommend {
    @Id
    @GeneratedValue
    @Column(name = "comment_unrecommend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Comment comment;

    @Builder
    public CommentUnrecommend(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }

    public static CommentUnrecommend create(User user, Comment comment){
        comment.recommend();
        return CommentUnrecommend.builder()
                .user(user)
                .comment(comment)
                .build();
    }
}

